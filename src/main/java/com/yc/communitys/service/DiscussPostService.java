package com.yc.communitys.service;

import com.yc.communitys.dao.DiscussPostMapper;
import com.yc.communitys.entity.DiscussPost;
import com.yc.communitys.util.SensitiveFilter;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

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

    @Autowired
    /** 工具-过滤敏感词 */
    private SensitiveFilter sensitiveFilter;

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

    /**
     * @description: 增加帖子
     * @author: yangchao
     * @date: 2022/7/28 16:22
     * @param: [post]
     * @return: int
     **/
    public int addDiscussPost(DiscussPost post) {

        if (post == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        // HtmlUtils.htmlEscape(): 转义HTML标记
        // 将内容传进来，如果带着大于小于这种标签就进行转义，防止注入
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));

        // 对表中的标题和内容进行过滤敏感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));

        return discussPostMapper.insertDiscussPost(post);
    }

    /**
     * @description: 根据id查询帖子详情
     * @author: yangchao
     * @date: 2022/7/28 16:49
     * @param: [id]
     * @return: com.yc.communitys.entity.DiscussPost
     **/
    public DiscussPost findDiscussPostById(int id) {
        return discussPostMapper.selectDiscussPostById(id);
    }

}
