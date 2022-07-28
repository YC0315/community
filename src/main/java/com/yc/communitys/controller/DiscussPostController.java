package com.yc.communitys.controller;


import com.yc.communitys.entity.Comment;
import com.yc.communitys.entity.DiscussPost;
import com.yc.communitys.entity.Page;
import com.yc.communitys.entity.User;
import com.yc.communitys.service.CommentService;
import com.yc.communitys.service.DiscussPostService;
import com.yc.communitys.service.UserService;
import com.yc.communitys.util.CommunityConstant;
import com.yc.communitys.util.CommunityUtil;
import com.yc.communitys.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private CommentService commentService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

/*    @Autowired
    private LikeService likeService;

    @Autowired
    private EventProducer eventProducer;*/

    /**
     * @description: 增加帖子
     * @author: yangchao
     * @date: 2022/7/28 16:27
     * @param: [title, content]
     * @return: java.lang.String
     **/
    @PostMapping("/add")
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        // 获取用户，未登录不能发帖子
        User user = hostHolder.getUser();
        if (user == null) {
            // 403 表示 没有权限
            return CommunityUtil.getJSONString(403, "您还未登录");
        }
        // 创建帖子并插入
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

/*        // 触发发帖事件
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(user.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(post.getId());
        eventProducer.fireEvent(event);*/

        // 程序执行到这里, 默认成功
        // 在执行的过程中若报错, 报错的情况将来统一处理
        return CommunityUtil.getJSONString(0, "发布成功！");
    }

    /**
     * @description: 查询帖子详情
     * @author: yangchao
     * @date: 2022/7/28 16:53
     * @param: [discussPostId, model, page]
     * @return: java.lang.String
     **/
    @GetMapping("/detail/{discussPostId}")
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page) {
        // 帖子
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post", post);
        // 查询作者
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);

        /*// 查询点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPostId);
        model.addAttribute("likeCount", likeCount);
        // 查询点赞状态
        int likeStatus = hostHolder.getUser() == null ? 0 :
                likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_POST, discussPostId);
        model.addAttribute("likeStatus", likeStatus);*/


        // 查询评论分页信息
        page.setLimit(5);  // 每页条
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(post.getCommentCount());

        /**
         * @description: 评论列表
         * @author: yangchao
         * @date: 2022/7/28 17:32
         * @param: [discussPostId, model, page]
         * @return: java.lang.String
         * 评论：给帖子的评论 - post.getId(), 且 targetId = 0, 表示 默认 用户发帖时的值
         * 回复：给评论的评论 - comment.getId() 且 targetId != 0, 表示 别的用户对当前帖子用户的回复
         **/
        List<Comment> commentList = commentService.findCommentsByEntity(
                        ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());
        // 1 评论VO列表
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                // 评论VO
                Map<String, Object> commentVo = new HashMap<>(16);
                // 评论
                commentVo.put("comment", comment);
                // 作者
                commentVo.put("user", userService.findUserById(comment.getUserId()));

                /*// 点赞数量
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeCount", likeCount);
                // 点赞状态
                likeStatus =
                        hostHolder.getUser() == null
                                ? 0
                                : likeService.findEntityLikeStatus(
                                hostHolder.getUser().getId(),
                                ENTITY_TYPE_COMMENT,
                                comment.getId());
                commentVo.put("likeStatus", likeStatus);*/

                // 2 回复列表 (评论的评论) - comment.getId()
                List<Comment> replyList = commentService.findCommentsByEntity(
                                // 设置 从 0 到最大, 即 有多少条查多少, 不做分页
                                ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                // 2 回复VO列表
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVo = new HashMap<>(16);
                        // 回复
                        replyVo.put("reply", reply);
                        // 作者
                        replyVo.put("user", userService.findUserById(reply.getUserId()));
                        // 回复目标
                        User target =
                                reply.getTargetId() == 0
                                        ? null
                                        : userService.findUserById(reply.getTargetId());
                        replyVo.put("target", target);

                        /*// 点赞数量
                        likeCount =
                                likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeCount", likeCount);
                        // 点赞状态
                        likeStatus =
                                hostHolder.getUser() == null ? 0
                                        : likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeStatus", likeStatus);*/

                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys", replyVoList);

                // 回复数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);

                // 最后加入到集合中
                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("comments", commentVoList);

        return "site/discuss-detail";
    }

}
