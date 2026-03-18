package com.echo.mapper;

import com.echo.entity.Moment;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MomentMapper {
  Moment selectById(@Param("id") long id);

  List<Moment> selectList(
      @Param("topic") String topic, @Param("offset") int offset, @Param("limit") int limit);

  int selectCount(@Param("topic") String topic);
}
