package com.yc.communitys.entity;

import java.util.Date;

public class Comment {

    private int id;
    private int userId; // 谁发布的评论(回复)

    /*
    * entityType: 评论(回复)的目标类别
    * 1 代表: 针对 帖子 进行评论
    * 2 代表: 针对 评论 进行评论
    * entityId: 回复的是哪一个帖子
    * targetId: 回复的是哪个用户
    * */
    private int entityType;
    private int entityId;
    private int targetId;

    private String content;
    private int status; // 0 代表正常, 1 代表被删除了 或者 不可用了
    private Date createTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getEntityType() {
        return entityType;
    }

    public void setEntityType(int entityType) {
        this.entityType = entityType;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", userId=" + userId +
                ", entityType=" + entityType +
                ", entityId=" + entityId +
                ", targetId=" + targetId +
                ", content='" + content + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                '}';
    }

}
