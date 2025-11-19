package com.echo.service;

import com.echo.api.ApiException;
import com.echo.dto.like.LikeResponse;
import com.echo.entity.Comment;
import com.echo.entity.Post;
import com.echo.mapper.CommentMapper;
import com.echo.mapper.PostMapper;
import com.echo.util.RedisKeyUtil;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikeService {
  private static final Logger log = LoggerFactory.getLogger(LikeService.class);
  private static final int COMMENT_STATUS_NORMAL = 0;
  private static final String POST_LIKE_SYNC_KEY = RedisKeyUtil.getPostLikeSyncKey();

  private final StringRedisTemplate stringRedisTemplate;
  private final PostMapper postMapper;
  private final PostService postService;
  private final CommentMapper commentMapper;
  private final NotificationService notificationService;

  public LikeService(
      StringRedisTemplate stringRedisTemplate,
      PostMapper postMapper,
      PostService postService,
      CommentMapper commentMapper,
      NotificationService notificationService) {
    this.stringRedisTemplate = stringRedisTemplate;
    this.postMapper = postMapper;
    this.postService = postService;
    this.commentMapper = commentMapper;
    this.notificationService = notificationService;
  }

  @Transactional
  public LikeResponse like(Long userId, int entityType, long entityId) {
    if (userId == null) {
      throw new ApiException(401, "Login required");
    }

    int normalizedEntityType = normalizeEntityType(entityType);
    long normalizedEntityId = requirePositiveId(entityId, "entityId");
    Long targetUserId = ensureEntityExists(normalizedEntityType, normalizedEntityId);

    String likeKey = RedisKeyUtil.getEntityLikeKey(normalizedEntityType, normalizedEntityId);
    String member = String.valueOf(userId);
    SetOperations<String, String> operations = stringRedisTemplate.opsForSet();

    boolean liked;
    if (Boolean.TRUE.equals(operations.isMember(likeKey, member))) {
      operations.remove(likeKey, member);
      liked = false;
    } else {
      operations.add(likeKey, member);
      liked = true;
    }

    long likeCount = getLikeCount(normalizedEntityType, normalizedEntityId);
    if (normalizedEntityType == CommentService.ENTITY_TYPE_POST) {
      markPostLikeCountDirty(normalizedEntityId);
    }
    if (liked) {
      notificationService.createNotification(
          "like", targetUserId, normalizedEntityType, normalizedEntityId, userId);
    }

    return new LikeResponse(normalizedEntityType, normalizedEntityId, likeCount, liked);
  }

  public long getLikeCount(int entityType, long entityId) {
    String likeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
    Long count = stringRedisTemplate.opsForSet().size(likeKey);
    return count == null ? 0L : count;
  }

  public boolean hasLiked(long userId, int entityType, long entityId) {
    String likeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
    return Boolean.TRUE.equals(stringRedisTemplate.opsForSet().isMember(likeKey, String.valueOf(userId)));
  }

  @Scheduled(cron = "0 0/5 * * * ?")
  public void syncPostLikeCountToMysql() {
    Set<String> dirtyPostIds = stringRedisTemplate.opsForSet().members(POST_LIKE_SYNC_KEY);
    if (dirtyPostIds == null || dirtyPostIds.isEmpty()) {
      return;
    }

    for (String postIdValue : dirtyPostIds) {
      long postId;
      try {
        postId = Long.parseLong(postIdValue);
      } catch (NumberFormatException ex) {
        log.warn("Invalid post id in redis sync set: {}", postIdValue);
        stringRedisTemplate.opsForSet().remove(POST_LIKE_SYNC_KEY, postIdValue);
        continue;
      }

      long likeCount = getLikeCount(CommentService.ENTITY_TYPE_POST, postId);
      try {
        int updated = postMapper.updateLikeCount(toPostId(postId), toLikeCountInt(likeCount));
        if (updated == 1) {
          stringRedisTemplate.opsForSet().remove(POST_LIKE_SYNC_KEY, postIdValue);
        } else {
          log.warn("Failed to sync post like count, postId={}, likeCount={}", postId, likeCount);
        }
      } catch (Exception ex) {
        log.error("Error syncing post like count, postId={}, likeCount={}", postId, likeCount, ex);
      }
    }
  }

  private Long ensureEntityExists(int entityType, long entityId) {
    if (entityType == CommentService.ENTITY_TYPE_POST) {
      Post post = postService.findPostById(toPostId(entityId));
      return post.getUserId();
    }

    Comment comment = commentMapper.selectById(entityId);
    if (comment == null || comment.getStatus() == null || comment.getStatus() != COMMENT_STATUS_NORMAL) {
      throw new ApiException(404, "Comment not found");
    }
    return comment.getUserId();
  }

  private void markPostLikeCountDirty(long postId) {
    stringRedisTemplate.opsForSet().add(POST_LIKE_SYNC_KEY, String.valueOf(postId));
  }

  private int toLikeCountInt(long likeCount) {
    if (likeCount > Integer.MAX_VALUE) {
      throw new ApiException(500, "likeCount overflow");
    }
    return (int) likeCount;
  }

  private int normalizeEntityType(int entityType) {
    if (entityType != CommentService.ENTITY_TYPE_POST && entityType != CommentService.ENTITY_TYPE_COMMENT) {
      throw new ApiException(400, "entityType must be 1 or 2");
    }
    return entityType;
  }

  private long requirePositiveId(long id, String fieldName) {
    if (id <= 0) {
      throw new ApiException(400, fieldName + " must be positive");
    }
    return id;
  }

  private int toPostId(long postId) {
    if (postId <= 0 || postId > Integer.MAX_VALUE) {
      throw new ApiException(400, "entityId must be positive and within int range");
    }
    return (int) postId;
  }
}
