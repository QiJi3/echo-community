package com.echo.controller;

import com.echo.api.Result;
import com.echo.dto.comment.CommentListResponse;
import com.echo.dto.comment.CreateCommentRequest;
import com.echo.entity.Comment;
import com.echo.security.LoginUser;
import com.echo.service.CommentService;
import javax.validation.Valid;
import javax.validation.constraints.Max;
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
@RequestMapping("/api/comment")
public class CommentController {
  private final CommentService commentService;

  public CommentController(CommentService commentService) {
    this.commentService = commentService;
  }

  @PostMapping
  @PreAuthorize("isAuthenticated()")
  public Result<Comment> createComment(
      @Valid @RequestBody CreateCommentRequest request,
      @AuthenticationPrincipal LoginUser loginUser) {
    Comment comment = commentService.addComment(loginUser.getUserId(), request);
    return Result.ok(comment, "Comment created successfully");
  }

  @GetMapping("/list")
  public Result<CommentListResponse> listComments(
      @RequestParam @Min(value = 1, message = "entityType must be 1 or 2")
          @Max(value = 2, message = "entityType must be 1 or 2") int entityType,
      @RequestParam @Positive(message = "entityId must be positive") long entityId,
      @RequestParam(defaultValue = "1") @Min(value = 1, message = "page must be greater than 0")
          int page,
      @RequestParam(defaultValue = "10") @Min(value = 1, message = "size must be greater than 0")
          int size) {
    return Result.ok(commentService.listComments(entityType, entityId, page, size));
  }
}
