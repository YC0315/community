package com.yc.communitys.service;

import com.yc.communitys.dao.CommentMapper;
import com.yc.communitys.entity.Comment;
import com.yc.communitys.util.CommunityConstant;
import com.yc.communitys.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @author: yangchao
 * @createTime: 2022-07-28  17:24
 * @description: 帖子评论
 */
@Service
public class CommentService implements CommunityConstant {
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussPostService discussPostService;

    public List<Comment> findCommentsByEntity(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentsByEntity(entityType, entityId, offset, limit);
    }

    // 查询评论的数量
    public int findCommentCount(int entityType, int entityId) {
        return commentMapper.selectCountByEntity(entityType, entityId);
    }

    /**
     * @description: 增加评论
     * @author: yangchao
     * @date: 2022/7/29 16:50
     * @param: [comment]
     * @return: int
     **/
    // 使用事务确保两次DML操作安全
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int addComment(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        // 添加评论
        // 1 过滤标签
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        // 2 过滤敏感词
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        int rows = commentMapper.insertComment(comment);

        // 更新帖子评论数量
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            // 查询帖子的数量
            int count = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            // 更新到帖子表中
            discussPostService.updateCommentCount(comment.getEntityId(), count);
        }
        return rows;
    }

    /**
     * 根据Id查询评论
     * */
    public Comment findCommentById(int id) {
        return commentMapper.selectCommentById(id);
    }
}
