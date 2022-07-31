package com.yc.communitys.dao;

import com.yc.communitys.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageMapper {

    /**
     * @description: 查询会话列表，针对每个会话只返回最新的一条私信
     * @author: yangchao
     * @date: 2022/7/29 17:20
     * @param: [userId, offset, limit]
     * @return: java.util.List<com.yc.communitys.entity.Message>
     **/
    List<Message> selectConversations(@Param("userId") int userId, @Param("offset") int offset, @Param("limit") int limit);

    /**
     * @description: 查询当前用户的会话数量
     * @author: yangchao
     * @date: 2022/7/29 17:20
     * @param: [userId]
     * @return: int
     **/
    int selectConversationCount(@Param("userId") int userId);

    /**
     * @description: 查询某个会话所包含的私信列表
     * @author: yangchao
     * @date: 2022/7/29 17:21
     * @param: [conversationId, offset, limit]
     * @return: java.util.List<com.yc.communitys.entity.Message>
     **/
    List<Message> selectLetters(@Param("conversationId") String conversationId, @Param("offset") int offset, @Param("limit") int limit);

    /**
     * @description: 查询某个会话包含的私信数量
     * @author: yangchao
     * @date: 2022/7/29 17:21
     * @param: [conversationId]
     * @return: int
     **/
    int selectLetterCount(@Param("conversationId") String conversationId);

    /**
     * @description: 查询当前用户未读私信数量,所有未读还是一个会话未读
     * @author: yangchao
     * @date: 2022/7/29 17:22
     * @param: [userId, conversationId]
     * @return: int
     **/
    int selectLetterUnreadCount(@Param("userId") int userId, @Param("conversationId") String conversationId);

   /**
    * @description: 新增消息
    * @author: yangchao
    * @date: 2022/7/30 14:16
    * @param: [message]
    * @return: int
    **/
    int insertMessage(Message message);

    /**
     * @description: 修改消息状态
     * @author: yangchao
     * @date: 2022/7/30 14:17
     * @param: [ids, status]
     * @return: int
     **/
    int updateStatus(@Param("ids") List<Integer> ids, @Param("status") int status);

    /**
     * @description: 查询某个主题下最新的通知
     * @author: yangchao
     * @date: 2022/7/31 16:32
     * @param: [userId, topic]
     * @return: com.yc.communitys.entity.Message
     **/
    Message selectLatestNotice(int userId, String topic);

    /**
     * @description: 查询某个主题所包含的通知数量
     * @author: yangchao
     * @date: 2022/7/31 16:33
     * @param: [userId, topic]
     * @return: int
     **/
    int selectNoticeCount(int userId, String topic);

    /**
     * @description: 查询未读的通知的数量
     * @author: yangchao
     * @date: 2022/7/31 16:33
     * @param: [userId, topic]
     * @return: int
     **/
    int selectNoticeUnreadCount(int userId, String topic);

    /**
     * @description: 查询某个主题所包含的通知列表
     * @author: yangchao
     * @date: 2022/7/31 16:33
     * @param: [userId, topic, offset, limit]
     * @return: java.util.List<com.yc.communitys.entity.Message>
     **/
    List<Message> selectNotices(int userId, String topic, int offset, int limit);

}
