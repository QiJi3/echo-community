package com.echo.util;

public final class RedisKeyUtil {
  private static final String POST_LIKE_SYNC_KEY = "like:sync:post";

  private RedisKeyUtil() {}

  public static String getEntityLikeKey(int entityType, long entityId) {
    return "like:entity:" + entityType + ":" + entityId;
  }

  public static String getPostLikeSyncKey() {
    return POST_LIKE_SYNC_KEY;
  }

  public static String getFolloweeKey(long userId, int entityType) {
    return "followee:" + userId + ":" + entityType;
  }

  public static String getFollowerKey(int entityType, long entityId) {
    return "follower:" + entityType + ":" + entityId;
  }
}
