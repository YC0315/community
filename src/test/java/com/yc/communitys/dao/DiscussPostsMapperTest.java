package com.yc.communitys.dao;

import com.yc.communitys.CommunityApplication;
import com.yc.communitys.entity.DiscussPost;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * @author: yangchao
 * @createTime: 2022-07-25  15:07
 * @description: 测试DiscussPostsMapper
 */
@SpringBootTest(classes = CommunityApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class DiscussPostsMapperTest {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    public void testSelectDiscussPost(){
        // 查询帖子分页结果
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(0, 0, 10, 0);
        for (DiscussPost discussPost : discussPosts) {
            System.out.println(discussPost);
        }

        // userId=0表示查询总的条数，不为0表示查询某个用户的总帖子条数
        int i = discussPostMapper.selectDiscussPostRows(103);
        System.out.println(i);


    }

}
