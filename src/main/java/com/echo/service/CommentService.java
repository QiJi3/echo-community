package com.echo.service;

import com.echo.api.ApiException;
import com.echo.dto.comment.CommentListResponse;
import com.echo.dto.comment.CreateCommentRequest;
import com.echo.entity.Comment;
import com.echo.entity.Post;
import com.echo.mapper.CommentMapper;
import com.echo.mapper.PostMapper;
import com.echo.util.SensitiveFilter;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {
  public static final int ENTITY_TYPE_POST = 1;
  public static final int ENTITY_TYPE_COMMENT = 2;

  private static final int COMMENT_STATUS_NORMAL = 0;

  private final SensitiveFilter sensitiveFilter;
  private final CommentMapper commentMapper;
  private final PostMapper postMapper;
  private final PostService postService;
  private final NotificationService notificationService;

  public CommentService(
      SensitiveFilter sensitiveFilter,
      CommentMapper commentMapper,
      PostMapper postMapper,
      PostService postService,
      NotificationService notificationService) {
    this.sensitiveFilter = sensitiveFilter;
    this.commentMapper = commentMapper;
    this.postMapper = postMapper;
    this.postService = postService;
    this.notificationService = notificationService;
  }

  @Transactional
  public Comment addComment(Long userId, CreateCommentRequest request) {
    if (userId == null) {
      throw new ApiException(401, "Login required");
    }

    int entityType = normalizeEntityType(request.getEntityType());
    long entityId = requirePositiveId(request.getEntityId(), "entityId");
    Long notificationUserId;

    if (entityType == ENTITY_TYPE_POST) {
      Post post = postService.findPostById(toPostId(entityId));
      notificationUserId = post.getUserId();
    } else {
      Comment targetComment = requireCommentById(entityId);
      notificationUserId = request.getTargetId() == null ? targetComment.getUserId() : request.getTargetId();
    }

    Comment comment = new Comment();
    comment.setUserId(userId);
    comment.setEntityType(entityType);
    comment.setEntityId(entityId);
    comment.setTargetId(normalizeTargetId(request.getTargetId()));
    comment.setContent(filterRequiredText(request.getContent()));
    comment.setStatus(COMMENT_STATUS_NORMAL);

    int inserted = commentMapper.insert(comment);
    if (inserted != 1 || comment.getId() == null) {
      throw new ApiException(500, "Failed to create comment");
    }

    if (entityType == ENTITY_TYPE_POST) {
      int total = commentMapper.selectCountByEntity(ENTITY_TYPE_POST, entityId);
      int updated = postMapper.updateCommentCount(toPostId(entityId), total);
      if (updated != 1) {
        throw new ApiException(500, "Failed to update post comment count");
      }
    }

    notificationService.createNotification("comment", notificationUserId, entityType, entityId, userId);
    return requireComment(comment.getId());
  }

  public CommentListResponse listComments(int entityType, long entityId, int page, int size) {
    int normalizedEntityType = normalizeEntityType(entityType);
    long normalizedEntityId = requirePositiveId(entityId, "entityId");

    int offset = (page - 1) * size;
    List<Comment> comments =
        commentMapper.selectByEntity(normalizedEntityType, normalizedEntityId, offset, size);
    int total = commentMapper.selectCountByEntity(normalizedEntityType, normalizedEntityId);
    return new CommentListResponse(comments, total, page, size, normalizedEntityType, normalizedEntityId);
  }

  private Comment requireComment(Long id) {
    Comment comment = commentMapper.selectById(id);
    if (comment == null || comment.getStatus() == null || comment.getStatus() != COMMENT_STATUS_NORMAL) {
      throw new ApiException(404, "Comment not found");
    }
    return comment;
  }

  private Comment requireCommentById(long id) {
    Comment comment = commentMapper.selectById(id);
    if (comment == null || comment.getStatus() == null || comment.getStatus() != COMMENT_STATUS_NORMAL) {
      throw new ApiException(404, "Comment not found");
    }
    return comment;
  }

  private String filterRequiredText(String text) {
    String normalized = text == null ? null : text.trim();
    if (normalized == null || normalized.isBlank()) {
      throw new ApiException(400, "content is required");
    }
    return sensitiveFilter.filter(normalized);
  }

  private int normalizeEntityType(Integer entityType) {
    if (entityType == null || (entityType != ENTITY_TYPE_POST && entityType != ENTITY_TYPE_COMMENT)) {
      throw new ApiException(400, "entityType must be 1 or 2");
    }
    return entityType;
  }

  private long requirePositiveId(Long id, String fieldName) {
    if (id == null || id <= 0) {
      throw new ApiException(400, fieldName + " must be positive");
    }
    return id;
  }

  private Long normalizeTargetId(Long targetId) {
    if (targetId == null) {
      return null;
    }
    if (targetId <= 0) {
      throw new ApiException(400, "targetId must be positive");
    }
    return targetId;
  }

  private int toPostId(long postId) {
    if (postId > Integer.MAX_VALUE) {
      throw new ApiException(400, "entityId is too large");
    }
    return (int) postId;
  }
}
