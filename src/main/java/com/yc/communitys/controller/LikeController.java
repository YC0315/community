package com.yc.communitys.controller;

import com.yc.communitys.entity.Event;
import com.yc.communitys.entity.User;
import com.yc.communitys.event.EventProducer;
import com.yc.communitys.service.LikeService;
import com.yc.communitys.util.CommunityConstant;
import com.yc.communitys.util.CommunityUtil;
import com.yc.communitys.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: 点赞
 * @author: yangchao
 * @date: 2022/7/30 16:48
 * @param:
 * @return:
 **/
@Controller
public class LikeController implements CommunityConstant {

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @PostMapping("/like")
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId, int postId) {
        // 首先获取获取当前用户
        User user = hostHolder.getUser();
        new ArrayList<>();
        // 点赞
        likeService.like(user.getId(), entityType, entityId, entityUserId);
        // 统计点赞数量，返回到页面
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        // 获取点赞状态
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);
        // 传给页面，封装一下返回的结果
        Map<String, Object> map = new HashMap<>(16);
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);

        // 触发点赞事件（kafka）
        // 点赞时发送消息给用户，取消点赞则不发送消息
        // 帖子id，因为页面需要查看被点赞的帖子
        if (likeStatus == 1) {
            Event event = new Event()
                    .setTopic(TOPIC_LIKE)
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setData("postId", postId);
            // 发送消息到主题
            eventProducer.fireEvent(event);
        }
        return CommunityUtil.getJSONString(0, null, map);
    }
}
