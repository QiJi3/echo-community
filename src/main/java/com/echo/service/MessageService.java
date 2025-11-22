package com.echo.service;

import com.echo.api.ApiException;
import com.echo.dto.message.ConversationListResponse;
import com.echo.dto.message.ConversationResponse;
import com.echo.dto.message.MessagePageResponse;
import com.echo.dto.message.SendMessageRequest;
import com.echo.entity.Message;
import com.echo.mapper.MessageMapper;
import com.echo.util.SensitiveFilter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MessageService {
  private static final int MESSAGE_STATUS_UNREAD = 0;

  private final MessageMapper messageMapper;
  private final SensitiveFilter sensitiveFilter;

  public MessageService(MessageMapper messageMapper, SensitiveFilter sensitiveFilter) {
    this.messageMapper = messageMapper;
    this.sensitiveFilter = sensitiveFilter;
  }

  @Transactional
  public Message sendMessage(Long fromId, SendMessageRequest request) {
    if (fromId == null) {
      throw new ApiException(401, "Login required");
    }

    long toId = requirePositiveId(request.getToId(), "toId");
    if (fromId == toId) {
      throw new ApiException(400, "Cannot send message to yourself");
    }

    Message message = new Message();
    message.setFromId(fromId);
    message.setToId(toId);
    message.setConversationId(buildConversationId(fromId, toId));
    message.setContent(filterRequiredText(request.getContent()));
    message.setStatus(MESSAGE_STATUS_UNREAD);

    int inserted = messageMapper.insert(message);
    if (inserted != 1 || message.getId() == null) {
      throw new ApiException(500, "Failed to send message");
    }

    Message saved = messageMapper.selectById(message.getId());
    if (saved == null) {
      throw new ApiException(500, "Failed to load message after send");
    }
    return saved;
  }

  public ConversationListResponse listConversations(long userId, int page, int size) {
    int offset = (page - 1) * size;
    List<Message> latestMessages = messageMapper.selectConversations(userId, offset, size);
    List<ConversationResponse> conversations = new ArrayList<>();
    for (Message latestMessage : latestMessages) {
      int unreadCount = messageMapper.selectUnreadCount(userId, latestMessage.getConversationId());
      conversations.add(
          new ConversationResponse(latestMessage.getConversationId(), latestMessage, unreadCount));
    }
    int total = messageMapper.selectConversationCount(userId);
    return new ConversationListResponse(conversations, total, page, size);
  }

  @Transactional
  public MessagePageResponse listMessages(long userId, String conversationId, int page, int size) {
    String normalizedConversationId = normalizeConversationId(conversationId);
    ensureParticipant(userId, normalizedConversationId);

    int offset = (page - 1) * size;
    List<Message> messages = messageMapper.selectMessages(normalizedConversationId, offset, size);
    int total = messageMapper.selectMessageCount(normalizedConversationId);
    messageMapper.markRead(userId, normalizedConversationId);
    return new MessagePageResponse(normalizedConversationId, messages, total, page, size);
  }

  private long requirePositiveId(Long id, String fieldName) {
    if (id == null || id <= 0) {
      throw new ApiException(400, fieldName + " must be positive");
    }
    return id;
  }

  private String filterRequiredText(String text) {
    String normalized = text == null ? null : text.trim();
    if (normalized == null || normalized.isBlank()) {
      throw new ApiException(400, "content is required");
    }
    return sensitiveFilter.filter(normalized);
  }

  private String buildConversationId(long fromId, long toId) {
    long small = Math.min(fromId, toId);
    long large = Math.max(fromId, toId);
    return small + "_" + large;
  }

  private String normalizeConversationId(String conversationId) {
    if (conversationId == null || conversationId.isBlank()) {
      throw new ApiException(400, "conversationId is required");
    }
    return conversationId.trim();
  }

  private void ensureParticipant(long userId, String conversationId) {
    String[] segments = conversationId.split("_");
    if (segments.length != 2) {
      throw new ApiException(400, "conversationId is invalid");
    }

    long first;
    long second;
    try {
      first = Long.parseLong(segments[0]);
      second = Long.parseLong(segments[1]);
    } catch (NumberFormatException ex) {
      throw new ApiException(400, "conversationId is invalid");
    }

    if (userId != first && userId != second) {
      throw new ApiException(403, "No permission to access this conversation");
    }
  }
}
