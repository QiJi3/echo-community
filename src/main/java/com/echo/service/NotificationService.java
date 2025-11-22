package com.echo.service;

import com.echo.api.ApiException;
import com.echo.dto.notification.NotificationListResponse;
import com.echo.entity.Notification;
import com.echo.mapper.NotificationMapper;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {
  private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

  private static final int STATUS_UNREAD = 0;

  private final NotificationMapper notificationMapper;

  public NotificationService(NotificationMapper notificationMapper) {
    this.notificationMapper = notificationMapper;
  }

  @Async
  public void createNotification(
      String type, Long userId, int entityType, long entityId, Long fromUserId) {
    if (userId == null || fromUserId == null || userId <= 0 || fromUserId <= 0) {
      return;
    }
    if (userId.equals(fromUserId)) {
      return;
    }
    if (type == null || type.isBlank()) {
      return;
    }

    Notification notification = new Notification();
    notification.setType(type.trim().toLowerCase());
    notification.setUserId(userId);
    notification.setEntityType(entityType);
    notification.setEntityId(entityId);
    notification.setFromUserId(fromUserId);
    notification.setStatus(STATUS_UNREAD);

    int inserted = notificationMapper.insert(notification);
    if (inserted != 1) {
      log.warn(
          "Failed to create notification: type={}, userId={}, entityType={}, entityId={}, fromUserId={}",
          type,
          userId,
          entityType,
          entityId,
          fromUserId);
    }
  }

  public NotificationListResponse listNotifications(long userId, int page, int size) {
    long normalizedUserId = requirePositiveId(userId, "userId");
    int offset = (page - 1) * size;
    List<Notification> notifications = notificationMapper.selectByUser(normalizedUserId, offset, size);
    int total = notificationMapper.selectCountByUser(normalizedUserId);
    return new NotificationListResponse(notifications, total, page, size);
  }

  @Transactional
  public int markRead(long userId, List<Long> ids) {
    long normalizedUserId = requirePositiveId(userId, "userId");
    if (ids == null || ids.isEmpty()) {
      return notificationMapper.markReadAll(normalizedUserId);
    }
    return notificationMapper.markReadByIds(normalizedUserId, ids);
  }

  public int getUnreadCount(long userId) {
    long normalizedUserId = requirePositiveId(userId, "userId");
    return notificationMapper.selectUnreadCount(normalizedUserId);
  }

  private long requirePositiveId(long id, String fieldName) {
    if (id <= 0) {
      throw new ApiException(400, fieldName + " must be positive");
    }
    return id;
  }
}
