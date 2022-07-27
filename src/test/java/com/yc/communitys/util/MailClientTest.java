package com.yc.communitys.util;

import com.yc.communitys.CommunityApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import static org.junit.Assert.*;

/**
 * @author: yangchao
 * @createTime: 2022-07-26  14:44
 * @description: 测试邮件功能
 */
@SpringBootTest(classes = CommunityApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class MailClientTest {

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testSendMail() {
        mailClient.sendMail("yc_lil@163.com", "test", "yangchao");
    }

    @Test
    public void testHtmlMail(){ // 尝试发送HTML邮件
        Context context = new Context(); // thymeleaf 的 Context
        context.setVariable("username", "yangchao");
        String content = templateEngine.process("/mail/demo", context); // 生成动态网页
        System.out.println(content);
        mailClient.sendMail("yc_lil@163.com", "测试SpringBoot发HTML邮件的功能", content);
    }
}