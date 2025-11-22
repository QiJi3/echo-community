package com.echo.mapper;

import com.echo.entity.Message;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MessageMapper {
  int insert(Message message);

  Message selectById(@Param("id") long id);

  List<Message> selectConversations(
      @Param("userId") long userId, @Param("offset") int offset, @Param("limit") int limit);

  int selectConversationCount(@Param("userId") long userId);

  List<Message> selectMessages(
      @Param("conversationId") String conversationId,
      @Param("offset") int offset,
      @Param("limit") int limit);

  int selectMessageCount(@Param("conversationId") String conversationId);

  int selectUnreadCount(@Param("userId") long userId, @Param("conversationId") String conversationId);

  int markRead(@Param("userId") long userId, @Param("conversationId") String conversationId);
}
