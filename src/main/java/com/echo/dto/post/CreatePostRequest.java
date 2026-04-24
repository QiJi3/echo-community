package com.echo.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreatePostRequest {
  @NotBlank(message = "title is required")
  @Size(max = 255, message = "title length must be less than or equal to 255")
  private String title;

  @NotBlank(message = "content is required")
  private String content;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }
}
