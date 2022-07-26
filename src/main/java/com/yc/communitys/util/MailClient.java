package com.yc.communitys.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @author: yangchao
 * @createTime: 2022-07-26  14:36
 * @description: 邮箱客户端，提供发邮件的功能
 */
@Component
public class MailClient {

    private static final Logger logger = LoggerFactory.getLogger(MailClient.class);

    // spring集成的mail功能
    @Autowired
    private JavaMailSender mailSender;

    // 指定从哪个邮箱发送邮件
    @Value("${spring.mail.username}")
    private String from;

    // 发送邮件
    public void sendMail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(from); // 发件人
            helper.setTo(to); // 发送给谁
            helper.setSubject(subject); // 发送的主题
            helper.setText(content, true); // 允许发送html字符串
            mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            e.printStackTrace();
            logger.error("发送邮件失败：" + e.getMessage());
        }
    }

}
