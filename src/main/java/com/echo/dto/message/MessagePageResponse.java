package com.echo.dto.message;

import com.echo.entity.Message;
import java.util.List;

public class MessagePageResponse {
  private String conversationId;
  private List<Message> messages;
  private int total;
  private int page;
  private int size;

  public MessagePageResponse() {}

  public MessagePageResponse(
      String conversationId, List<Message> messages, int total, int page, int size) {
    this.conversationId = conversationId;
    this.messages = messages;
    this.total = total;
    this.page = page;
    this.size = size;
  }

  public String getConversationId() {
    return conversationId;
  }

  public void setConversationId(String conversationId) {
    this.conversationId = conversationId;
  }

  public List<Message> getMessages() {
    return messages;
  }

  public void setMessages(List<Message> messages) {
    this.messages = messages;
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
