package com.echo.dto.notification;

public class NotificationReadResponse {
  private int updated;

  public NotificationReadResponse() {}

  public NotificationReadResponse(int updated) {
    this.updated = updated;
  }

  public int getUpdated() {
    return updated;
  }

  public void setUpdated(int updated) {
    this.updated = updated;
  }
}
