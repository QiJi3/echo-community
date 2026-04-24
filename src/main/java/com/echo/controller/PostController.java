package com.echo.controller;

import com.echo.api.Result;
import com.echo.dto.post.CreatePostRequest;
import com.echo.dto.post.PostListResponse;
import com.echo.entity.Post;
import com.echo.security.LoginUser;
import com.echo.service.PostService;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/post")
public class PostController {
  private final PostService postService;

  public PostController(PostService postService) {
    this.postService = postService;
  }

  @PostMapping
  @PreAuthorize("isAuthenticated()")
  public Result<Post> createPost(
      @Valid @RequestBody CreatePostRequest request,
      @AuthenticationPrincipal LoginUser loginUser) {
    Post post = new Post();
    post.setUserId(loginUser.getUserId());
    post.setTitle(request.getTitle());
    post.setContent(request.getContent());
    return Result.ok(postService.createPost(post), "Post created successfully");
  }

  @GetMapping("/{id}")
  public Result<Post> getPost(@PathVariable @Positive(message = "id must be positive") int id) {
    return Result.ok(postService.findPostById(id));
  }

  @GetMapping("/list")
  public Result<PostListResponse> listPosts(
      @RequestParam(required = false) @Positive(message = "userId must be positive") Integer userId,
      @RequestParam(value = "sort", required = false) String sort,
      @RequestParam(value = "orderBy", required = false) String orderBy,
      @RequestParam(defaultValue = "1") @Min(value = 1, message = "page must be greater than 0") int page,
      @RequestParam(defaultValue = "10") @Min(value = 1, message = "size must be greater than 0") int size) {
    String normalizedSort = normalizeSort(sort, orderBy);
    List<Post> posts = postService.listPosts(userId, normalizedSort, page, size);
    int total = postService.getPostCount(userId);
    PostListResponse response =
        new PostListResponse(posts, total, page, size, normalizedSort, userId);
    return Result.ok(response);
  }

  @PutMapping("/{id}/top")
  @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
  public Result<Boolean> topPost(@PathVariable @Positive(message = "id must be positive") int id) {
    postService.topPost(id);
    return Result.ok(Boolean.TRUE, "Post topped successfully");
  }

  @PutMapping("/{id}/highlight")
  @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
  public Result<Boolean> highlightPost(
      @PathVariable @Positive(message = "id must be positive") int id) {
    postService.highlightPost(id);
    return Result.ok(Boolean.TRUE, "Post highlighted successfully");
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public Result<Boolean> deletePost(@PathVariable @Positive(message = "id must be positive") int id) {
    postService.deletePost(id);
    return Result.ok(Boolean.TRUE, "Post deleted successfully");
  }

  private String normalizeSort(String sort, String orderBy) {
    String candidate = sort == null || sort.isBlank() ? orderBy : sort;
    if ("hot".equalsIgnoreCase(candidate)) {
      return "hot";
    }
    return "time";
  }
}
