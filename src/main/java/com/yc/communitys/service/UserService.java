package com.yc.communitys.service;

import com.yc.communitys.dao.LoginTicketMapper;
import com.yc.communitys.dao.UserMapper;
import com.yc.communitys.entity.LoginTicket;
import com.yc.communitys.entity.User;
import com.yc.communitys.util.CommunityConstant;
import com.yc.communitys.util.CommunityUtil;
import com.yc.communitys.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author: yangchao
 * @createTime: 2022-07-25  15:21
 * @description: user业务逻辑层
 */
@Service
public class UserService implements CommunityConstant {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Value("${communitys.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * @description: 根据userId查询用户信息
     * @author: yangchao
     * @date: 2022/7/25 15:21
     * @param:
     * @return:
     **/
    public User findUserById(int userId) {
        return userMapper.selectById(userId);
    }

    /**
     * 注册功能
     */
    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>(16);

        // 空值处理
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空");
            return map;
        }

        // 验证处理: 是否可以查到用户, 是, 则代表已存在
        User u = userMapper.selectByName(user.getUsername());
        if (u != null) {
            map.put("usernameMsg", "该账号已存在！");
            return map;
        }
        u = userMapper.selectByEmail(user.getEmail());
        if (u != null) {
            map.put("emailMsg", "该邮箱已被注册！");
            return map;
        }

        // 注册用户：将信息存入数据库
        // 生成随机字符串
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        // 加密的字符串为原密码加随机字符串，保证安全性
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        // 普通用户
        user.setType(0);
        // 没有激活
        user.setStatus(0);
        // 生成随机激活码
        user.setActivationCode(CommunityUtil.generateUUID());
        // 设置牛客网随机头像
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        // 存入数据库
        userMapper.insertUser(user);

        // 激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // http://localhost:8080/community/activation/101/code //自定义激活url
        String url =
                domain
                        + contextPath
                        + "/activation/"
                        + user.getId()
                        + "/"
                        + user.getActivationCode();
        context.setVariable("url", url);
        //  利用模板引擎生成邮件的内容
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);

        return map;
    }

    // 激活功能
    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        if (user.getStatus() == 1) {
            // 1 代表已经激活
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            // 相等代表激活码相同即激活成功
            // 更改激活状态：0->1
            userMapper.updateStatus(userId, 1);
            //clearCache(userId);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILED;
        }
    }

    /**
     * @description: 登录,验证成功后凭证录入数据库中
     * @author: yangchao
     * @date: 2022/7/26 16:34
     * @param: [username, password, expiredSeconds]
     * @return: java.util.Map<java.lang.String,java.lang.Object>
     **/
    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>(16);

        // 空值处理
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空！");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }

        // 验证账号
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "该账号不存在！");
            return map;
        }

        // 验证状态
        if (user.getStatus() == 0) {
            // `status` int(11) DEFAULT NULL COMMENT '0-未激活; 1-已激活;',
            map.put("usernameMsg", "该账号未激活！");
            return map;
        }

        // 验证密码
        password = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "密码不正确！");
            return map;
        }

        // 若上面的验证都通过, 生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        // 有效状态 0-有效， 1-无效
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000));

        // 存入登录凭证
         loginTicketMapper.insertLoginTicket(loginTicket);
//        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
//        redisTemplate.opsForValue().set(redisKey, loginTicket);

        // 客户端只需要存 ticket(服务器可以通过它可以查询到登录凭证对象), 所以返回 ticket
        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    /**
     * @description: 退出
     * @author: yangchao
     * @date: 2022/7/26 16:52
     * @param: [ticket]
     * @return: void
     **/
    public void logout(String ticket) {
         loginTicketMapper.updateStatus(ticket, 1);
        /*String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        // 删除态
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey, loginTicket);*/
    }

    /**
     * @description: 通过ticket查询数据库得到LoginTicket对象
     * @author: yangchao
     * @date: 2022/7/27 14:07
     * @param: [ticket]
     * @return: com.yc.communitys.entity.LoginTicket
     **/
    public LoginTicket findLoginTicket(String ticket) {
        return loginTicketMapper.selectByTicket(ticket);
        //String redisKey = RedisKeyUtil.getTicketKey(ticket);
        //return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }


}
