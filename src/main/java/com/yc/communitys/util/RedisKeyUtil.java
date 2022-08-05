package com.yc.communitys.util;

/**
 * 生成Key的工具，简化key的使用
 * */
public class RedisKeyUtil {
    private static final String SPLIT = ":";

    // 实体的赞的key
    private static final String PREFIX_ENTITY_LIKE = "like:entity";

    // 用户的赞的key
    private static final String PREFIX_USER_LIKE = "like:user";

    // 关注的目标
    private static final String PREFIX_FOLLOWEE = "followee";

    // 关注目标的粉丝
    private static final String PREFIX_FOLLOWER = "follower";

    // 验证码
    private static final String PREFIX_KAPTCHA = "kaptcha";

    // 凭证
    private static final String PREFIX_TICKET = "ticket";

    // 从缓存中取值
    private static final String PREFIX_USER = "user";

    // 独立用户
    private static final String PREFIX_UV = "uv";

    // 日活跃用户
    private static final String PREFIX_DAU = "dau";

    private static final String PREFIX_POST = "post";

    /**
     * 某个实体的赞
     * 实体: 包括帖子和评论
     * 前缀以 like:entity 开头
     * 举例如下:
     *key like:entity:entityType:entityId -> value set(userId)  知道谁给实体点的赞
     *
     * */
    public static String getEntityLikeKey(int entityType, int entityId) {
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    // 某个用户的赞 like:user:userId -> int
    public static String getUserLikeKey(int userId) {
        return PREFIX_USER_LIKE + SPLIT + userId;
    }

    // 某个用户关注的实体 followee:userId:entityType -> zset(entityId, now)
    public static String getFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    // 某个实体拥有的粉丝 follower:entityType:entityId -> zset(userId, now)
    public static String getFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    // 获取登录验证码
    public static String getKaptchaKey(String owner) {
        return PREFIX_KAPTCHA + SPLIT + owner;
    }

    // 登录的凭证
    public static String getTicketKey(String ticket) {
        return PREFIX_TICKET + SPLIT + ticket;
    }

    // 用户
    public static String getUserKey(int userId) {
        return PREFIX_USER + SPLIT + userId;
    }

    // 单日UV
    public static String getUVKey(String date) {
        return PREFIX_UV + SPLIT + date;
    }

    // 区间UV
    public static String getUVKey(String startDate, String endDate) {
        return PREFIX_UV + SPLIT + startDate + SPLIT + endDate;
    }

    // 单日DAU
    public static String getDAUKey(String date) {
        return PREFIX_DAU + SPLIT + date;
    }

    // 区间DAU
    public static String getDAUKey(String startDate, String endDate) {
        return PREFIX_DAU + SPLIT + startDate + SPLIT + endDate;
    }

    // 统计帖子分数的key
    public static String getPostScoreKey() {
        return PREFIX_POST + SPLIT + "score";
    }

}
