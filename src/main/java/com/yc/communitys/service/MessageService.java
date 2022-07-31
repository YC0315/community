package com.yc.communitys.service;

import com.yc.communitys.dao.MessageMapper;
import com.yc.communitys.entity.Message;
import com.yc.communitys.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @author Jungle
 */
@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    /**
     * @description: 查询当前用户的会话列表，针对每个会话只返回一条最新的私信
     * @author: yangchao
     * @date: 2022/7/29 17:25
     * @param: [userId, offset, limit]
     * @return: java.util.List<com.yc.communitys.entity.Message>
     **/
    public List<Message> findConversations(int userId, int offset, int limit) {
        return messageMapper.selectConversations(userId, offset, limit);
    }

    /**
     * @description: 查询当前用户的会话数量
     * @author: yangchao
     * @date: 2022/7/29 17:26
     * @param: [userId]
     * @return: int
     **/
    public int findConversationCount(int userId) {
        return messageMapper.selectConversationCount(userId);
    }

    /**
     * @description: 查询某个会话所包含的私信列表
     * @author: yangchao
     * @date: 2022/7/29 17:27
     * @param: [conversationId, offset, limit]
     * @return: java.util.List<com.yc.communitys.entity.Message>
     **/
    public List<Message> findLetters(String conversationId, int offset, int limit) {
        return messageMapper.selectLetters(conversationId, offset, limit);
    }

    /**
     * @description: 查询某个会话包含的私信数量
     * @author: yangchao
     * @date: 2022/7/29 17:27
     * @param: [conversationId]
     * @return: int
     **/
    public int findLetterCount(String conversationId) {
        return messageMapper.selectLetterCount(conversationId);
    }

    /**
     * @description: 查询未读私信数量
     * @author: yangchao
     * @date: 2022/7/29 17:27
     * @param: [userId, conversationId]
     * @return: int
     **/
    public int findLetterUnreadCount(int userId, String conversationId) {
        return messageMapper.selectLetterUnreadCount(userId, conversationId);
    }


    /**
     * @description: 添加消息
     * @author: yangchao
     * @date: 2022/7/30 14:18
     * @param: [message]
     * @return: int
     **/
    public int addMessage(Message message) {
        // 过滤标签及敏感词
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return messageMapper.insertMessage(message);
    }

    /**
     * @description: 修改状态
     * @author: yangchao
     * @date: 2022/7/30 14:19
     * @param: [ids]
     * @return: int
     **/
    public int readMessage(List<Integer> ids) {
        // 将状态改为已读
        return messageMapper.updateStatus(ids, 1);
    }

    /**
     * @description: 查询最新的通知
     * @author: yangchao
     * @date: 2022/7/31 16:35
     * @param: [userId, topic]
     * @return: com.yc.communitys.entity.Message
     **/
    public Message findLatestNotice(int userId, String topic) {
        return messageMapper.selectLatestNotice(userId, topic);
    }

    /**
     * @description: 查询最新的通知数量
     * @author: yangchao
     * @date: 2022/7/31 16:35
     * @param: [userId, topic]
     * @return: int
     **/
    public int findNoticeCount(int userId, String topic) {
        return messageMapper.selectNoticeCount(userId, topic);
    }

    /**
     * @description: 查询未读的通知
     * @author: yangchao
     * @date: 2022/7/31 16:36
     * @param: [userId, topic]
     * @return: int
     **/
    public int findNoticeUnreadCount(int userId, String topic) {
        return messageMapper.selectNoticeUnreadCount(userId, topic);
    }

    /**
     * @description: 分页查询通知列表
     * @author: yangchao
     * @date: 2022/7/31 16:36
     * @param: [userId, topic, offset, limit]
     * @return: java.util.List<com.yc.communitys.entity.Message>
     **/
    public List<Message> findNotices(int userId, String topic, int offset, int limit) {
        return messageMapper.selectNotices(userId, topic, offset, limit);
    }

}
