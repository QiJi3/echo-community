package com.echo.service;

import com.echo.api.ApiException;
import com.echo.entity.Post;
import com.echo.mapper.PostMapper;
import com.echo.util.SensitiveFilter;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostService {
  private static final int POST_TYPE_NORMAL = 0;
  private static final int POST_TYPE_TOP = 1;
  private static final int POST_STATUS_NORMAL = 0;
  private static final int POST_STATUS_HIGHLIGHT = 1;
  private static final int POST_STATUS_DELETED = 2;
  private static final long SCORE_EPOCH =
      LocalDateTime.of(2020, 1, 1, 0, 0).toEpochSecond(ZoneOffset.UTC);

  private final SensitiveFilter sensitiveFilter;
  private final PostMapper postMapper;

  public PostService(SensitiveFilter sensitiveFilter, PostMapper postMapper) {
    this.sensitiveFilter = sensitiveFilter;
    this.postMapper = postMapper;
  }

  @Transactional
  public Post createPost(Post post) {
    if (post == null || post.getUserId() == null) {
      throw new ApiException(400, "Post author is required");
    }

    post.setTitle(filterRequiredText(post.getTitle(), "title"));
    post.setContent(filterRequiredText(post.getContent(), "content"));
    post.setType(POST_TYPE_NORMAL);
    post.setStatus(POST_STATUS_NORMAL);
    post.setCommentCount(0);
    post.setLikeCount(0);
    post.setScore(0D);

    int inserted = postMapper.insert(post);
    if (inserted != 1 || post.getId() == null) {
      throw new ApiException(500, "Failed to create post");
    }
    return requireVisiblePost(post.getId().intValue());
  }

  public Post findPostById(int id) {
    return requireVisiblePost(id);
  }

  public List<Post> listPosts(Integer userId, String orderBy, int page, int size) {
    int offset = (page - 1) * size;
    return postMapper.selectPosts(userId, normalizeOrderBy(orderBy), offset, size);
  }

  public int getPostCount(Integer userId) {
    return postMapper.selectPostCount(userId);
  }

  @Transactional
  public int refreshPostScores(int batchSize) {
    int normalizedBatchSize = Math.max(batchSize, 1);
    int page = 1;
    int refreshedCount = 0;
    while (true) {
      int offset = (page - 1) * normalizedBatchSize;
      List<Post> posts = postMapper.selectPosts(null, "time", offset, normalizedBatchSize);
      if (posts == null || posts.isEmpty()) {
        break;
      }

      for (Post post : posts) {
        int updated = postMapper.updateScore(toPostId(post.getId()), calculateScore(post));
        if (updated != 1) {
          throw new ApiException(500, "Failed to refresh post score");
        }
        refreshedCount++;
      }

      if (posts.size() < normalizedBatchSize) {
        break;
      }
      page++;
    }
    return refreshedCount;
  }

  @Transactional
  public void topPost(int id) {
    Post post = requireExistingPost(id);
    if (post.getStatus() != null && post.getStatus() == POST_STATUS_DELETED) {
      throw new ApiException(404, "Post not found");
    }
    if (post.getType() != null && post.getType() == POST_TYPE_TOP) {
      return;
    }
    updateType(id, POST_TYPE_TOP);
  }

  @Transactional
  public void highlightPost(int id) {
    Post post = requireExistingPost(id);
    if (post.getStatus() != null && post.getStatus() == POST_STATUS_DELETED) {
      throw new ApiException(404, "Post not found");
    }
    if (post.getStatus() != null && post.getStatus() == POST_STATUS_HIGHLIGHT) {
      return;
    }
    updateStatus(id, POST_STATUS_HIGHLIGHT);
  }

  @Transactional
  public void deletePost(int id) {
    Post post = requireExistingPost(id);
    if (post.getStatus() != null && post.getStatus() == POST_STATUS_DELETED) {
      return;
    }
    updateStatus(id, POST_STATUS_DELETED);
  }

  private Post requireVisiblePost(int id) {
    Post post = requireExistingPost(id);
    if (post.getStatus() != null && post.getStatus() == POST_STATUS_DELETED) {
      throw new ApiException(404, "Post not found");
    }
    return post;
  }

  private Post requireExistingPost(int id) {
    Post post = postMapper.selectById(id);
    if (post == null) {
      throw new ApiException(404, "Post not found");
    }
    return post;
  }

  private void updateType(int id, int type) {
    int updated = postMapper.updateType(id, type);
    if (updated != 1) {
      throw new ApiException(500, "Failed to update post type");
    }
  }

  private void updateStatus(int id, int status) {
    int updated = postMapper.updateStatus(id, status);
    if (updated != 1) {
      throw new ApiException(500, "Failed to update post status");
    }
  }

  private String filterRequiredText(String text, String fieldName) {
    String normalized = text == null ? null : text.trim();
    if (normalized == null || normalized.isBlank()) {
      throw new ApiException(400, fieldName + " is required");
    }
    return sensitiveFilter.filter(normalized);
  }

  private String normalizeOrderBy(String orderBy) {
    if ("hot".equalsIgnoreCase(orderBy)) {
      return "hot";
    }
    return "time";
  }

  private double calculateScore(Post post) {
    int commentCount = post.getCommentCount() == null ? 0 : post.getCommentCount();
    int likeCount = post.getLikeCount() == null ? 0 : post.getLikeCount();
    int interactionCount = commentCount + likeCount;
    double logPart = Math.log(Math.max(interactionCount, 1));

    if (post.getCreatedAt() == null) {
      return logPart;
    }
    long createSeconds = post.getCreatedAt().toEpochSecond(ZoneOffset.UTC);
    double timePart = (createSeconds - SCORE_EPOCH) / 86400.0;
    return logPart + timePart;
  }

  private int toPostId(Long id) {
    if (id == null || id <= 0 || id > Integer.MAX_VALUE) {
      throw new ApiException(500, "Invalid post id");
    }
    return id.intValue();
  }
}
