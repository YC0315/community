package com.yc.communitys.event;

import com.alibaba.fastjson.JSONObject;
import com.yc.communitys.entity.DiscussPost;
import com.yc.communitys.entity.Event;
import com.yc.communitys.entity.Message;
import com.yc.communitys.service.DiscussPostService;
import com.yc.communitys.service.ElasticsearchService;
import com.yc.communitys.service.MessageService;
import com.yc.communitys.util.CommunityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/*
 * @description: 事件消费者
 * @author: yangchao
 * @date: 2022/7/31 15:25
 **/
@Component
public class EventConsumer implements CommunityConstant {
    // 记录日志
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    // 发消息最终是往消息表中插入数据
    @Autowired
    private MessageService messageService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    // 消费点赞，评论和关注事件，三个主题
    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_LIKE, TOPIC_FOLLOW})
    // 消息的格式为ConsumerRecord
    public void handleCommentMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息的内容为空!");
            return;
        }
        // 如果不为空就往下执行，将消息从字符串恢复成对象
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式错误!");
            return;
        }

        // 发送站内通知，构造Massage对象
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);  // 消息发送者
        message.setToId(event.getEntityUserId());  // 消息接收者
        message.setConversationId(event.getTopic());  // 消息的主题
        message.setCreateTime(new Date());  // 创建时间

        // 拼接显示详情
        Map<String, Object> content = new HashMap<>(16);
        content.put("userId", event.getUserId());  // 事件谁触发的
        content.put("entityType", event.getEntityType());  // 实体的类型，评论了一个帖子还是点赞了一个回复
        content.put("entityId", event.getEntityId()); // 事件对象的id

        // 将剩余数据都存到content中
        if (!event.getData().isEmpty()) {
            for (Map.Entry<String, Object> entry : event.getData().entrySet()) {
                content.put(entry.getKey(), entry.getValue());
            }
        }
        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }

    // 消费发帖事件
    @KafkaListener(topics = {TOPIC_PUBLISH})
    public void handlePublishMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息的内容为空!");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式错误!");
            return;
        }
        // 从事件的消息中得到帖子id, 从数据库中查到对应的帖子，然后将帖子存入ES服务器中即可
        DiscussPost post = discussPostService.findDiscussPostById(event.getEntityId());
        elasticsearchService.saveDiscussPost(post);
    }

    // 消费删帖事件
    @KafkaListener(topics = {TOPIC_DELETE})
    public void handleDeleteMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息的内容为空!");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式错误!");
            return;
        }
        // es服务器中删除帖子
        elasticsearchService.deleteDiscussPost(event.getEntityId());
    }

}
