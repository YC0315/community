package com.yc.communitys.service;

import com.yc.communitys.dao.DiscussPostMapper;
import com.yc.communitys.entity.DiscussPost;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: yangchao
 * @createTime: 2022-07-25  15:16
 * @description: 帖子业务逻辑层
 */
@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    /*
     * @description: 查询帖子
     * @author: yangchao
     * @date: 2022/7/25 15:18
     * @param: [userId, offset, limit]
     * @return: java.util.List<com.yc.communitys.entity.DiscussPost>
     **/
    public List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit){
        return discussPostMapper.selectDiscussPosts(userId, offset, limit);
    }

    /**
     * @description: 查询帖子行数
     * @author: yangchao
     * @date: 2022/7/25 15:19
     * @param: [userId]
     * @return: int
     **/
    public int selectDiscussPostRows(@Param("userId") int userId){
        return discussPostMapper.selectDiscussPostRows(userId);
    }
}
