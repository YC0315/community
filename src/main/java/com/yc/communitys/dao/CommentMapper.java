package com.yc.communitys.dao;

import com.yc.communitys.entity.Comment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentMapper {

    /**
     * 分页查询评论
     * 根据 实体 来查询
     * 实体: 即 当前帖子
     * 不再采用 xml 来配置, 而是使用 注解!
     * @param entityType 实体类型
     * @param entityId 实体id
     * @param offset 查询起始索引
     * @param limit 查询的记录数
     * @return 一个分页
     */
    @Select({
            "select id, user_id, entity_type, entity_id, target_id, content, status, create_time",
            "from comment",
            "where status=0 and entity_type = #{entityType} and entity_id = #{entityId}",
            "order by create_time asc",
            "limit #{offset}, #{limit}"
    })
    List<Comment> selectCommentsByEntity(@Param("entityType") int entityType, @Param("entityId") int entityId,
                                         @Param("offset") int offset, @Param("limit") int limit);


    /**
     * 查询评论总数
     *
     * @param entityType
     * @param entityId
     * @return
     */
    @Select({
            "select count(id)",
            "from comment",
            "where status=0 and entity_type = #{entityType} and entity_id = #{entityId}"
    })
    int selectCountByEntity(@Param("entityType") int entityType, @Param("entityId") int entityId);

    /**
     * 增加评论
     *
     * @param comment 评论对象
     * @return 影响的行数
     */
    @Insert({
            "insert into comment (user_id, entity_type, entity_id, target_id, content, status, create_time)",
            "values (#{userId}, #{entityType}, #{entityId}, #{targetId}, #{content}, #{status}, #{createTime})"
    })
    int insertComment(Comment comment);


    /**
     * 根据评论Id查询该评论
     * @param id 评论Id
     * @return 该评论
     */
    @Select({
            "select id, user_id, entity_type, entity_id, target_id, content, status, create_time",
            "from comment",
            "where id = #{id}"
    })
    Comment selectCommentById(int id);

}
