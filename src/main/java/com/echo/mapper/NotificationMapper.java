package com.echo.mapper;

import com.echo.entity.Notification;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface NotificationMapper {
  int insert(Notification notification);

  List<Notification> selectByUser(
      @Param("userId") long userId, @Param("offset") int offset, @Param("limit") int limit);

  int selectCountByUser(@Param("userId") long userId);

  int selectUnreadCount(@Param("userId") long userId);

  int markReadByIds(@Param("userId") long userId, @Param("ids") List<Long> ids);

  int markReadAll(@Param("userId") long userId);
}
