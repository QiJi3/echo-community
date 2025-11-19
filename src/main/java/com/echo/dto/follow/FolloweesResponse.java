package com.echo.dto.follow;

import java.util.List;

public class FolloweesResponse {
  private long userId;
  private int entityType;
  private List<FollowRecordResponse> followees;
  private long total;
  private int page;
  private int size;

  public FolloweesResponse() {}

  public FolloweesResponse(
      long userId,
      int entityType,
      List<FollowRecordResponse> followees,
      long total,
      int page,
      int size) {
    this.userId = userId;
    this.entityType = entityType;
    this.followees = followees;
    this.total = total;
    this.page = page;
    this.size = size;
  }

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

  public int getEntityType() {
    return entityType;
  }

  public void setEntityType(int entityType) {
    this.entityType = entityType;
  }

  public List<FollowRecordResponse> getFollowees() {
    return followees;
  }

  public void setFollowees(List<FollowRecordResponse> followees) {
    this.followees = followees;
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
