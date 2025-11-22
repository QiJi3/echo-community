package com.echo.dto.notification;

import com.echo.entity.Notification;
import java.util.List;

public class NotificationListResponse {
  private List<Notification> notifications;
  private int total;
  private int page;
  private int size;

  public NotificationListResponse() {}

  public NotificationListResponse(List<Notification> notifications, int total, int page, int size) {
    this.notifications = notifications;
    this.total = total;
    this.page = page;
    this.size = size;
  }

  public List<Notification> getNotifications() {
    return notifications;
  }

  public void setNotifications(List<Notification> notifications) {
    this.notifications = notifications;
  }

  public int getTotal() {
    return total;
  }

  public void setTotal(int total) {
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
