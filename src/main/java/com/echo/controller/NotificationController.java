package com.echo.controller;

import com.echo.api.ApiException;
import com.echo.api.Result;
import com.echo.dto.notification.NotificationReadResponse;
import com.echo.dto.notification.NotificationUnreadResponse;
import com.echo.security.LoginUser;
import com.echo.service.NotificationService;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.Min;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/notification")
public class NotificationController {
  private final NotificationService notificationService;

  public NotificationController(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public Result<Object> notification(
      @AuthenticationPrincipal LoginUser loginUser,
      @RequestParam(defaultValue = "list") String action,
      @RequestParam(required = false) String ids,
      @RequestParam(defaultValue = "1") @Min(value = 1, message = "page must be greater than 0")
          int page,
      @RequestParam(defaultValue = "10") @Min(value = 1, message = "size must be greater than 0")
          int size) {
    String normalizedAction = action == null ? "list" : action.trim().toLowerCase();
    long userId = loginUser.getUserId();

    if ("count".equals(normalizedAction)) {
      return Result.ok(new NotificationUnreadResponse(notificationService.getUnreadCount(userId)));
    }
    if ("read".equals(normalizedAction)) {
      int updated = notificationService.markRead(userId, parseIds(ids));
      return Result.ok(new NotificationReadResponse(updated), "Notifications marked as read");
    }
    if (!"list".equals(normalizedAction)) {
      throw new ApiException(400, "action must be list, count or read");
    }

    return Result.ok(notificationService.listNotifications(userId, page, size));
  }

  private List<Long> parseIds(String ids) {
    List<Long> parsedIds = new ArrayList<>();
    if (ids == null || ids.isBlank()) {
      return parsedIds;
    }

    String[] values = ids.split(",");
    for (String value : values) {
      String normalized = value == null ? "" : value.trim();
      if (normalized.isEmpty()) {
        continue;
      }

      long id;
      try {
        id = Long.parseLong(normalized);
      } catch (NumberFormatException ex) {
        throw new ApiException(400, "ids contains invalid value");
      }
      if (id <= 0) {
        throw new ApiException(400, "ids must be positive");
      }
      parsedIds.add(id);
    }
    return parsedIds;
  }
}
