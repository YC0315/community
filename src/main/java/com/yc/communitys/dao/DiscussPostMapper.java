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
                                         @Param("limit") int limit,
                                         @Param("orderMode") int orderMode);

    /**
     * @description: 查询一共多少条数据，@Param("userId")定义别名
     * @author: yangchao
     * @date: 2022/7/25 14:59
     * @param: [userId]
     * @return: int
     **/
    int selectDiscussPostRows(@Param("userId") int userId);

    /**
     * @description: 插入帖子
     * @author: yangchao
     * @date: 2022/7/28 16:16
     * @param: [discussPost]
     * @return: int
     **/
    int insertDiscussPost(DiscussPost discussPost);

    /**
     * @description: 帖子详情
     * @author: yangchao
     * @date: 2022/7/28 16:47
     * @param: [id]
     * @return: com.yc.communitys.entity.DiscussPost
     **/
    DiscussPost selectDiscussPostById(int id);

    /**
     * 更新帖子评论数量
     */
    int updateCommentCount(@Param("id") int id,
                           @Param("commentCount") int commentCount);

    /**
     * 更新帖子类型 0：普通 1：置顶
     */
    int updateType(@Param("id") int id,
                   @Param("type") int type);

    /**
     * 更新帖子状态 0：正常 1：精华 2：拉黑
     */
    int updateStatus(@Param("id") int id,
                     @Param("status") int status);

    /**
     * 更新帖子分数
     */
    void updateScore(@Param("id") int id,
                     @Param("score") double score);
}
