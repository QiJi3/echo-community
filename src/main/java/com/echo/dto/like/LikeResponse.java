package com.echo.dto.like;

public class LikeResponse {
  private int entityType;
  private long entityId;
  private long likeCount;
  private boolean liked;

  public LikeResponse() {}

  public LikeResponse(int entityType, long entityId, long likeCount, boolean liked) {
    this.entityType = entityType;
    this.entityId = entityId;
    this.likeCount = likeCount;
    this.liked = liked;
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

  public long getLikeCount() {
    return likeCount;
  }

  public void setLikeCount(long likeCount) {
    this.likeCount = likeCount;
  }

  public boolean isLiked() {
    return liked;
  }

  public void setLiked(boolean liked) {
    this.liked = liked;
  }
}
