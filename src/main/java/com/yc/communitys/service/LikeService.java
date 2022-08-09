package com.yc.communitys.service;

import com.yc.communitys.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

/**
 * @description: 点赞
 * @author: yangchao
 * @date: 2022/7/30 16:42
 * @param:
 * @return:
 **/
@Service
public class LikeService {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 点赞业务方法
     * 实体: 包括帖子和评论
     * @param userId 表示谁点的赞
     * @param entityType 实体类型
     * @param entityId 实体Id
     */
    public void like(int userId, int entityType, int entityId, int entityUserId) {

        // 使用事务
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);
                // 当前用户有没有对这个实体点过赞
                boolean isMember = operations.opsForSet().isMember(entityLikeKey, userId);
                // 开启事务，食物中不可以查询，Redis批量执行指令
                operations.multi();

                if (isMember) {
                    // 已赞就取消赞
                    operations.opsForSet().remove(entityLikeKey, userId);
                    operations.opsForValue().decrement(userLikeKey);
                } else {
                    // 否则点赞.
                    // 点赞用的是Redis.set()结构存放点赞的用户id,
                    // ADD 命令向名为 entityLikeKey 的set集合插入userId。
                    operations.opsForSet().add(entityLikeKey, userId);
                    operations.opsForValue().increment(userLikeKey);
                }

                return operations.exec();
            }
        });
    }

    // 查询某实体点赞的数量
    public long findEntityLikeCount(int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        // 集合的size
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    // 查询某人对某实体的点赞状态
    public int findEntityLikeStatus(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        // 1 表示点赞, 0 表示取消赞 (用整数表示更具有扩展性)
        return redisTemplate.opsForSet().isMember(entityLikeKey, userId) ? 1 : 0;
    }

    // 统计某个用户获得的赞的数量
    public int findUserLikeCount(int userId) {
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count == null ? 0 : count;
    }
}
