package com.echo.controller;

import com.echo.api.Result;
import com.echo.dto.like.LikeRequest;
import com.echo.dto.like.LikeResponse;
import com.echo.security.LoginUser;
import com.echo.service.LikeService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/like")
public class LikeController {
  private final LikeService likeService;

  public LikeController(LikeService likeService) {
    this.likeService = likeService;
  }

  @PostMapping
  @PreAuthorize("isAuthenticated()")
  public Result<LikeResponse> like(
      @Valid @RequestBody LikeRequest request, @AuthenticationPrincipal LoginUser loginUser) {
    LikeResponse response =
        likeService.like(loginUser.getUserId(), request.getEntityType(), request.getEntityId());
    return Result.ok(response, "Like status updated");
  }
}
