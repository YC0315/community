package com.yc.communitys.controller;

import com.yc.communitys.entity.Comment;
import com.yc.communitys.entity.DiscussPost;
import com.yc.communitys.entity.Event;
import com.yc.communitys.event.EventProducer;
import com.yc.communitys.service.CommentService;
import com.yc.communitys.service.DiscussPostService;
import com.yc.communitys.util.CommunityConstant;
import com.yc.communitys.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

/**
 * @description: 新增评论，更新帖子数量
 * @author: yangchao
 * @date: 2022/7/29 16:55
 **/
@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private EventProducer eventProducer;
    /**
     * @description: 新增评论
     * @author: yangchao
     * @date: 2022/7/29 16:56
     * @param: [discussPostId, comment]
     * @return: java.lang.String
     **/
    @PostMapping("/add/{discussPostId}")
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment) {
        // 设置帖子的内容
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        // 使用kafka给用户发消息
        // 触发评论事件
        // 构建事件对象，给消费者使用
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId", discussPostId);
        // 设置实体类型: POST-帖子, Comment-评论
        // 如果是给帖子做评论，查帖子表
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        } else if (comment.getEntityType() == ENTITY_TYPE_COMMENT) {
            // 当前评论的是一个评论
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        // 异步发布消息到topic 由消费者去消费不需要立即消费
        eventProducer.fireEvent(event);

        // 先判断是否评论给帖子，还是评论给了评论
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            // 触发发帖事件
            event = new Event()
                    .setTopic(TOPIC_PUBLISH)
                    .setUserId(comment.getUserId())
                    .setEntityType(ENTITY_TYPE_POST)
                    .setEntityId(discussPostId);
            eventProducer.fireEvent(event);
        }

        // 重定向的帖子详情
        return "redirect:/discuss/detail/" + discussPostId;
    }

}
