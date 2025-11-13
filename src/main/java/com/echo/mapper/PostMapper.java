package com.echo.mapper;

import com.echo.entity.Post;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PostMapper {
  int insert(Post post);

  Post selectById(@Param("id") int id);

  List<Post> selectPosts(
      @Param("userId") Integer userId,
      @Param("orderBy") String orderBy,
      @Param("offset") int offset,
      @Param("limit") int limit);

  int selectPostCount(@Param("userId") Integer userId);

  int updateType(@Param("id") int id, @Param("type") int type);

  int updateStatus(@Param("id") int id, @Param("status") int status);

  int updateCommentCount(@Param("id") int id, @Param("commentCount") int commentCount);

  int updateLikeCount(@Param("id") int id, @Param("likeCount") int likeCount);

  int updateScore(@Param("id") int id, @Param("score") double score);
}
