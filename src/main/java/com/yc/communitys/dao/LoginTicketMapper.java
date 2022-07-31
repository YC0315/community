package com.yc.communitys.dao;

import com.yc.communitys.entity.LoginTicket;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Repository
@Deprecated
public interface LoginTicketMapper {

    /**
     * 插入login_ticket表
     * 注意 隔行时有一个空格
     * @Options(useGeneratedKeys = true, keyProperty = "id") 会自动生成主键
     * @param loginTicket 登录凭证对象
     * @return 插入操作受影响的行数
     */
    @Insert({
            "insert into login_ticket(user_id, ticket, status, expired) ",
            "values(#{userId}, #{ticket}, #{status}, #{expired})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);


    /**
     * 通过Ticket查找记录
     *
     * @param ticket 凭证字段
     * @return 登录凭证对象
     */
    @Select({
            "select id, user_id, ticket, status, expired ",
            "from login_ticket where ticket=#{ticket}"
    })
    LoginTicket selectByTicket(String ticket);


    /**
     * 更新login_ticket的status字段
     *
     * @param ticket 更新条件
     * @param status 被更新状态: 0-有效; 1-无效.
     * @return
     */
    @Update({
            "update login_ticket set status = #{status} where ticket=#{ticket} "
    })
    int updateStatus(@Param("ticket") String ticket, @Param("status") int status);



}
