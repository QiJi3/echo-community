package com.echo.dto.comment;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class CreateCommentRequest {
  @NotNull(message = "entityType is required")
  @Min(value = 1, message = "entityType must be 1 or 2")
  @Max(value = 2, message = "entityType must be 1 or 2")
  private Integer entityType;

  @NotNull(message = "entityId is required")
  @Positive(message = "entityId must be positive")
  private Long entityId;

  @Positive(message = "targetId must be positive")
  private Long targetId;

  @NotBlank(message = "content is required")
  private String content;

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

  public Long getTargetId() {
    return targetId;
  }

  public void setTargetId(Long targetId) {
    this.targetId = targetId;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }
}
