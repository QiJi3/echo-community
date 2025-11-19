package com.echo.service;

import com.echo.api.ApiException;
import com.echo.dto.follow.FollowActionResponse;
import com.echo.dto.follow.FollowRecordResponse;
import com.echo.dto.follow.FolloweesResponse;
import com.echo.dto.follow.FollowersResponse;
import com.echo.util.RedisKeyUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

@Service
public class FollowService {
  private final StringRedisTemplate stringRedisTemplate;
  private final NotificationService notificationService;

  public FollowService(
      StringRedisTemplate stringRedisTemplate, NotificationService notificationService) {
    this.stringRedisTemplate = stringRedisTemplate;
    this.notificationService = notificationService;
  }

  public FollowActionResponse follow(Long userId, int entityType, long entityId, boolean follow) {
    if (userId == null) {
      throw new ApiException(401, "Login required");
    }
    int normalizedEntityType = normalizeEntityType(entityType);
    long normalizedEntityId = requirePositiveId(entityId, "entityId");

    String followeeKey = RedisKeyUtil.getFolloweeKey(userId, normalizedEntityType);
    String followerKey = RedisKeyUtil.getFollowerKey(normalizedEntityType, normalizedEntityId);
    String followerMember = String.valueOf(userId);
    String followeeMember = String.valueOf(normalizedEntityId);

    if (follow) {
      double now = (double) System.currentTimeMillis();
      stringRedisTemplate.opsForZSet().add(followeeKey, followeeMember, now);
      stringRedisTemplate.opsForZSet().add(followerKey, followerMember, now);
      notificationService.createNotification(
          "follow", normalizedEntityId, normalizedEntityType, normalizedEntityId, userId);
    } else {
      stringRedisTemplate.opsForZSet().remove(followeeKey, followeeMember);
      stringRedisTemplate.opsForZSet().remove(followerKey, followerMember);
    }

    long followerCount = getFollowerCount(normalizedEntityType, normalizedEntityId);
    return new FollowActionResponse(normalizedEntityType, normalizedEntityId, follow, followerCount);
  }

  public long getFolloweeCount(long userId, int entityType) {
    String key = RedisKeyUtil.getFolloweeKey(userId, entityType);
    Long count = stringRedisTemplate.opsForZSet().zCard(key);
    return count == null ? 0L : count;
  }

  public long getFollowerCount(int entityType, long entityId) {
    String key = RedisKeyUtil.getFollowerKey(entityType, entityId);
    Long count = stringRedisTemplate.opsForZSet().zCard(key);
    return count == null ? 0L : count;
  }

  public FolloweesResponse listFollowees(long userId, int entityType, int page, int size) {
    int normalizedEntityType = normalizeEntityType(entityType);
    long normalizedUserId = requirePositiveId(userId, "userId");
    long total = getFolloweeCount(normalizedUserId, normalizedEntityType);

    String key = RedisKeyUtil.getFolloweeKey(normalizedUserId, normalizedEntityType);
    long start = (long) (page - 1) * size;
    long end = start + size - 1;
    Set<ZSetOperations.TypedTuple<String>> tuples =
        stringRedisTemplate.opsForZSet().reverseRangeWithScores(key, start, end);

    List<FollowRecordResponse> records = toFollowRecords(tuples);
    return new FolloweesResponse(normalizedUserId, normalizedEntityType, records, total, page, size);
  }

  public FollowersResponse listFollowers(int entityType, long entityId, int page, int size) {
    int normalizedEntityType = normalizeEntityType(entityType);
    long normalizedEntityId = requirePositiveId(entityId, "entityId");
    long total = getFollowerCount(normalizedEntityType, normalizedEntityId);

    String key = RedisKeyUtil.getFollowerKey(normalizedEntityType, normalizedEntityId);
    long start = (long) (page - 1) * size;
    long end = start + size - 1;
    Set<ZSetOperations.TypedTuple<String>> tuples =
        stringRedisTemplate.opsForZSet().reverseRangeWithScores(key, start, end);

    List<FollowRecordResponse> records = toFollowRecords(tuples);
    return new FollowersResponse(normalizedEntityType, normalizedEntityId, records, total, page, size);
  }

  public boolean hasFollowed(long userId, int entityType, long entityId) {
    String key = RedisKeyUtil.getFollowerKey(entityType, entityId);
    Double score = stringRedisTemplate.opsForZSet().score(key, String.valueOf(userId));
    return score != null;
  }

  private List<FollowRecordResponse> toFollowRecords(Set<ZSetOperations.TypedTuple<String>> tuples) {
    List<FollowRecordResponse> records = new ArrayList<>();
    if (tuples == null || tuples.isEmpty()) {
      return records;
    }

    for (ZSetOperations.TypedTuple<String> tuple : tuples) {
      if (tuple == null || tuple.getValue() == null) {
        continue;
      }
      long id;
      try {
        id = Long.parseLong(tuple.getValue());
      } catch (NumberFormatException ex) {
        continue;
      }
      long followedAt = tuple.getScore() == null ? 0L : tuple.getScore().longValue();
      records.add(new FollowRecordResponse(id, followedAt));
    }
    return records;
  }

  private int normalizeEntityType(int entityType) {
    if (entityType <= 0) {
      throw new ApiException(400, "entityType must be positive");
    }
    return entityType;
  }

  private long requirePositiveId(long id, String fieldName) {
    if (id <= 0) {
      throw new ApiException(400, fieldName + " must be positive");
    }
    return id;
  }
}
