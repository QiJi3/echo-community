package com.echo.controller;

import com.echo.api.Result;
import com.echo.dto.message.ConversationListResponse;
import com.echo.dto.message.MessagePageResponse;
import com.echo.dto.message.SendMessageRequest;
import com.echo.entity.Message;
import com.echo.security.LoginUser;
import com.echo.service.MessageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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
@RequestMapping("/api/message")
public class MessageController {
  private final MessageService messageService;

  public MessageController(MessageService messageService) {
    this.messageService = messageService;
  }

  @PostMapping
  @PreAuthorize("isAuthenticated()")
  public Result<Message> sendMessage(
      @Valid @RequestBody SendMessageRequest request,
      @AuthenticationPrincipal LoginUser loginUser) {
    Message message = messageService.sendMessage(loginUser.getUserId(), request);
    return Result.ok(message, "Message sent successfully");
  }

  @GetMapping(params = "!conversationId")
  @PreAuthorize("isAuthenticated()")
  public Result<ConversationListResponse> listConversations(
      @AuthenticationPrincipal LoginUser loginUser,
      @RequestParam(defaultValue = "1") @Min(value = 1, message = "page must be greater than 0")
          int page,
      @RequestParam(defaultValue = "10") @Min(value = 1, message = "size must be greater than 0")
          int size) {
    return Result.ok(messageService.listConversations(loginUser.getUserId(), page, size));
  }

  @GetMapping(params = "conversationId")
  @PreAuthorize("isAuthenticated()")
  public Result<MessagePageResponse> listMessages(
      @AuthenticationPrincipal LoginUser loginUser,
      @RequestParam String conversationId,
      @RequestParam(defaultValue = "1") @Min(value = 1, message = "page must be greater than 0")
          int page,
      @RequestParam(defaultValue = "10") @Min(value = 1, message = "size must be greater than 0")
          int size) {
    return Result.ok(messageService.listMessages(loginUser.getUserId(), conversationId, page, size));
  }
}
