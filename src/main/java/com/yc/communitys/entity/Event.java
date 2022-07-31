package com.yc.communitys.entity;

import java.util.HashMap;
import java.util.Map;
/**
 * @description: 封装事件对象
 * @author: yangchao
 * @date: 2022/7/31 15:19
 **/
public class Event {
    // 事件的类型-主题
    private String topic;

    // 事件触发的人-I，谁给帖子做了评论...
    private int userId;

    /**
     * 事件发生在哪个实体上
     * entityType 实体类型
     * entityId 实体Id
     * entityUserId 实体作者Id
     * */
    private int entityType;
    private int entityId;
    private int entityUserId;

    // 其他的额外数据存入Map中
    private Map<String, Object> data = new HashMap<>();

    public String getTopic() {
        return topic;
    }

    // 这样设置可以链式 setTopic往topic中存值
    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Event setData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }
}
