package com.echo.controller;

import com.echo.api.Result;
import com.echo.dto.follow.FollowActionResponse;
import com.echo.dto.follow.FollowRequest;
import com.echo.dto.follow.FolloweesResponse;
import com.echo.dto.follow.FollowersResponse;
import com.echo.security.LoginUser;
import com.echo.service.FollowService;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
public class FollowController {
  private final FollowService followService;

  public FollowController(FollowService followService) {
    this.followService = followService;
  }

  @PostMapping("/api/follow")
  @PreAuthorize("isAuthenticated()")
  public Result<FollowActionResponse> follow(
      @Valid @RequestBody FollowRequest request, @AuthenticationPrincipal LoginUser loginUser) {
    boolean follow = request.getFollow() == null || request.getFollow();
    FollowActionResponse response =
        followService.follow(loginUser.getUserId(), request.getEntityType(), request.getEntityId(), follow);
    return Result.ok(response, "Follow status updated");
  }

  @GetMapping("/api/followees")
  public Result<FolloweesResponse> listFollowees(
      @RequestParam @Positive(message = "userId must be positive") long userId,
      @RequestParam @Positive(message = "entityType must be positive") int entityType,
      @RequestParam(defaultValue = "1") @Min(value = 1, message = "page must be greater than 0")
          int page,
      @RequestParam(defaultValue = "10") @Min(value = 1, message = "size must be greater than 0")
          int size) {
    return Result.ok(followService.listFollowees(userId, entityType, page, size));
  }

  @GetMapping("/api/followers")
  public Result<FollowersResponse> listFollowers(
      @RequestParam @Positive(message = "entityType must be positive") int entityType,
      @RequestParam @Positive(message = "entityId must be positive") long entityId,
      @RequestParam(defaultValue = "1") @Min(value = 1, message = "page must be greater than 0")
          int page,
      @RequestParam(defaultValue = "10") @Min(value = 1, message = "size must be greater than 0")
          int size) {
    return Result.ok(followService.listFollowers(entityType, entityId, page, size));
  }
}
