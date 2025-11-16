package com.echo.mapper;

import com.echo.entity.Comment;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CommentMapper {
  List<Comment> selectByEntity(
      @Param("entityType") int entityType,
      @Param("entityId") long entityId,
      @Param("offset") int offset,
      @Param("limit") int limit);

  int selectCountByEntity(@Param("entityType") int entityType, @Param("entityId") long entityId);

  int insert(Comment comment);

  Comment selectById(@Param("id") long id);
}
