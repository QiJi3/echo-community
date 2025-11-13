package com.echo.dto.post;

import com.echo.entity.Post;
import java.util.List;

public class PostListResponse {
  private List<Post> posts;
  private int total;
  private int page;
  private int size;
  private String orderBy;
  private Integer userId;

  public PostListResponse() {}

  public PostListResponse(
      List<Post> posts, int total, int page, int size, String orderBy, Integer userId) {
    this.posts = posts;
    this.total = total;
    this.page = page;
    this.size = size;
    this.orderBy = orderBy;
    this.userId = userId;
  }

  public List<Post> getPosts() {
    return posts;
  }

  public void setPosts(List<Post> posts) {
    this.posts = posts;
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

  public String getOrderBy() {
    return orderBy;
  }

  public void setOrderBy(String orderBy) {
    this.orderBy = orderBy;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }
}
