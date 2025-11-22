package com.echo.dto.notification;

public class NotificationUnreadResponse {
  private int unreadCount;

  public NotificationUnreadResponse() {}

  public NotificationUnreadResponse(int unreadCount) {
    this.unreadCount = unreadCount;
  }

  public int getUnreadCount() {
    return unreadCount;
  }

  public void setUnreadCount(int unreadCount) {
    this.unreadCount = unreadCount;
  }
}
