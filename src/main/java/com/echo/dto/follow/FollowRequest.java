package com.echo.dto.follow;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class FollowRequest {
  @NotNull(message = "entityType is required")
  @Positive(message = "entityType must be positive")
  private Integer entityType;

  @NotNull(message = "entityId is required")
  @Positive(message = "entityId must be positive")
  private Long entityId;

  private Boolean follow;

  public Integer getEntityType() {
    return entityType;
  }

  public void setEntityType(Integer entityType) {
    this.entityType = entityType;
  }

  public Long getEntityId() {
    return entityId;
  }

  public void setEntityId(Long entityId) {
    this.entityId = entityId;
  }

  public Boolean getFollow() {
    return follow;
  }

  public void setFollow(Boolean follow) {
    this.follow = follow;
  }
}
