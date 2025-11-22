package com.echo.dto.message;

import java.util.List;

public class ConversationListResponse {
  private List<ConversationResponse> conversations;
  private int total;
  private int page;
  private int size;

  public ConversationListResponse() {}

  public ConversationListResponse(
      List<ConversationResponse> conversations, int total, int page, int size) {
    this.conversations = conversations;
    this.total = total;
    this.page = page;
    this.size = size;
  }

  public List<ConversationResponse> getConversations() {
    return conversations;
  }

  public void setConversations(List<ConversationResponse> conversations) {
    this.conversations = conversations;
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
