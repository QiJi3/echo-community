package com.echo.dto.follow;

public class FollowRecordResponse {
  private long id;
  private long followedAt;

  public FollowRecordResponse() {}

  public FollowRecordResponse(long id, long followedAt) {
    this.id = id;
    this.followedAt = followedAt;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getFollowedAt() {
    return followedAt;
  }

  public void setFollowedAt(long followedAt) {
    this.followedAt = followedAt;
  }
}
