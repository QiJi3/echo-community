package com.echo.dto.like;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class LikeRequest {
  @NotNull(message = "entityType is required")
  @Min(value = 1, message = "entityType must be 1")
  @Max(value = 1, message = "entityType must be 1")
  private Integer entityType;

  @NotNull(message = "entityId is required")
  @Positive(message = "entityId must be positive")
  private Long entityId;

  @Positive(message = "entityUserId must be positive")
  private Long entityUserId;

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

  public Long getEntityUserId() {
    return entityUserId;
  }

  public void setEntityUserId(Long entityUserId) {
    this.entityUserId = entityUserId;
  }
}
