package com.yc.communitys.dao;

import com.yc.communitys.CommunityApplication;
import com.yc.communitys.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * @author: yangchao
 * @createTime: 2022-07-24  17:03
 * @description: 测试userMapper
 */
@SpringBootTest(classes = CommunityApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class UserMapperTest {
    @Autowired
    private UserMapper userMapper;

    @Test // start 测试增删改
    public void testSelectUser() {
        User user = userMapper.selectById(101);
        System.out.println(user);

        user = userMapper.selectByName("liubei");
        System.out.println(user);

        user = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);
    }

    @Test
    public void testInsertUser() {
        User user = new User();
        user.setUsername("yangchao");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("157555784@qq.com");
        user.setHeaderUrl("http://www.abc.com/1.png");
        user.setCreateTime(new Date());
        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }
}