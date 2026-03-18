package com.echo.mapper;

import com.echo.entity.Interview;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface InterviewMapper {
  Interview selectById(@Param("id") long id);

  List<Interview> selectList(
      @Param("company") String company,
      @Param("position") String position,
      @Param("offset") int offset,
      @Param("limit") int limit);

  int selectCount(@Param("company") String company, @Param("position") String position);
}
