package com.echo.mapper;

import com.echo.entity.ColumnArticle;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ColumnArticleMapper {
  ColumnArticle selectById(@Param("id") long id);

  List<ColumnArticle> selectList(
      @Param("columnName") String columnName,
      @Param("offset") int offset,
      @Param("limit") int limit);

  int selectCount(@Param("columnName") String columnName);
}
