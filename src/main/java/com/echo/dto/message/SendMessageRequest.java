package com.echo.dto.message;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class SendMessageRequest {
  @NotNull(message = "toId is required")
  @Positive(message = "toId must be positive")
  private Long toId;

  @NotBlank(message = "content is required")
  private String content;

  public Long getToId() {
    return toId;
  }

  public void setToId(Long toId) {
    this.toId = toId;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }
}
