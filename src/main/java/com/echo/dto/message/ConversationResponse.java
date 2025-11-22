package com.echo.dto.message;

import com.echo.entity.Message;

public class ConversationResponse {
  private String conversationId;
  private Message latestMessage;
  private int unreadCount;

  public ConversationResponse() {}

  public ConversationResponse(String conversationId, Message latestMessage, int unreadCount) {
    this.conversationId = conversationId;
    this.latestMessage = latestMessage;
    this.unreadCount = unreadCount;
  }

  public String getConversationId() {
    return conversationId;
  }

  public void setConversationId(String conversationId) {
    this.conversationId = conversationId;
  }

  public Message getLatestMessage() {
    return latestMessage;
  }

  public void setLatestMessage(Message latestMessage) {
    this.latestMessage = latestMessage;
  }

  public int getUnreadCount() {
    return unreadCount;
  }

  public void setUnreadCount(int unreadCount) {
    this.unreadCount = unreadCount;
  }
}
