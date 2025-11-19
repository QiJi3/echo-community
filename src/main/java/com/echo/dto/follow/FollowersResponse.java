package com.echo.dto.follow;

import java.util.List;

public class FollowersResponse {
  private int entityType;
  private long entityId;
  private List<FollowRecordResponse> followers;
  private long total;
  private int page;
  private int size;

  public FollowersResponse() {}

  public FollowersResponse(
      int entityType,
      long entityId,
      List<FollowRecordResponse> followers,
      long total,
      int page,
      int size) {
    this.entityType = entityType;
    this.entityId = entityId;
    this.followers = followers;
    this.total = total;
    this.page = page;
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

  public List<FollowRecordResponse> getFollowers() {
    return followers;
  }

  public void setFollowers(List<FollowRecordResponse> followers) {
    this.followers = followers;
  }

  public long getTotal() {
    return total;
  }

  public void setTotal(long total) {
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
}
