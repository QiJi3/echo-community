package com.echo.dto.comment;

import com.echo.entity.Comment;
import java.util.List;

public class CommentListResponse {
  private List<Comment> comments;
  private int total;
  private int page;
  private int size;
  private int entityType;
  private long entityId;

  public CommentListResponse() {}

  public CommentListResponse(
      List<Comment> comments, int total, int page, int size, int entityType, long entityId) {
    this.comments = comments;
    this.total = total;
    this.page = page;
    this.size = size;
    this.entityType = entityType;
    this.entityId = entityId;
  }

  public List<Comment> getComments() {
    return comments;
  }

  public void setComments(List<Comment> comments) {
    this.comments = comments;
  }

  public int getTotal() {
    return total;
  }

  public void setTotal(int total) {
    this.total = total;
  }

  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public int getEntityType() {
    return entityType;
  }

  public void setEntityType(int entityType) {
    this.entityType = entityType;
  }

  public long getEntityId() {
    return entityId;
  }

  public void setEntityId(long entityId) {
    this.entityId = entityId;
  }
}
