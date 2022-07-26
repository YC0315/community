package com.yc.communitys.dao;

import com.yc.communitys.entity.DiscussPost;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author: yangchao
 * @createTime: 2022-07-25  14:53
 * @description: 帖子-mapper
 */
@Repository
public interface DiscussPostMapper {

    /**
     * @description: 分页查询帖子
     * @author: yangchao
     * @date: 2022/7/25 14:57
     * @param: [userId, offset, limit]
     * @return: java.util.List<com.yc.communitys.entity.DiscussPost>
     **/
    List<DiscussPost> selectDiscussPosts(@Param("userId") int userId,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit);

    /**
     * @description: 查询一共多少条数据，@Param("userId")定义别名
     * @author: yangchao
     * @date: 2022/7/25 14:59
     * @param: [userId]
     * @return: int
     **/
    int selectDiscussPostRows(@Param("userId") int userId);
}
