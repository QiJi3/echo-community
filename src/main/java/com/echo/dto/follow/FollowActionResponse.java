package com.echo.dto.follow;

public class FollowActionResponse {
  private int entityType;
  private long entityId;
  private boolean followed;
  private long followerCount;

  public FollowActionResponse() {}

  public FollowActionResponse(int entityType, long entityId, boolean followed, long followerCount) {
    this.entityType = entityType;
    this.entityId = entityId;
    this.followed = followed;
    this.followerCount = followerCount;
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

  public boolean isFollowed() {
    return followed;
  }

  public void setFollowed(boolean followed) {
    this.followed = followed;
  }

  public long getFollowerCount() {
    return followerCount;
  }

  public void setFollowerCount(long followerCount) {
    this.followerCount = followerCount;
  }
}
