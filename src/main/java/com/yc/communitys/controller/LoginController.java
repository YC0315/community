package com.yc.communitys.controller;

import com.google.code.kaptcha.Producer;
import com.yc.communitys.util.CommunityConstant;
import com.yc.communitys.util.CommunityUtil;
import com.yc.communitys.entity.User;
import com.yc.communitys.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @description: 注册和登录
 * @author: yangchao
 * @date: 2022/7/26 15:13
 **/
@Controller
public class LoginController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private Producer kaptchaProducer;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private RedisTemplate redisTemplate;


    // 获取请求注册页面
    @GetMapping("/register")
    public String getRegisterPage() {
        return "/site/register";
    }

    // 注册请求处理
    @PostMapping("/register")
    public String register(Model model, User user) {
        // 根据返回值map做出响应
        Map<String, Object> map = userService.register(user);
        // map是存放错误信息的；若为空，则注册成功！
        if (map == null || map.isEmpty()) {
            model.addAttribute("msg", "注册成功，我们已经向您的邮箱发送了一封激活邮件，请尽快激活！");
            // 跳转到首页
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        } else {
            // 三种可能会存在的错误信息，无则为空
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            // 出错时发送回页面
            return "/site/register";
        }
    }

    /** 激活码请求处理
     * http://localhost:8080/community/activation/101/code
     */
    @GetMapping("/activation/{userId}/{code}")
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code) {
        int result = userService.activation(userId, code);
        // 对激活的结果进行判断，进行响应页面的跳转
        switch (result) {
            case ACTIVATION_SUCCESS:
            {
                model.addAttribute("msg", "激活成功！");
                model.addAttribute("target", "/login");
                break;
            }
            case ACTIVATION_REPEAT:
            {
                model.addAttribute("msg", "该账号已经激活！");
                model.addAttribute("target", "/index");
                break;
            }
            case ACTIVATION_FAILED:
            {
                model.addAttribute("msg", "激活码不正确！");
                model.addAttribute("target", "/index");
                break;
            }
            default:
        }
        return "/site/operate-result";
    }

    // 访问登录页面
    @GetMapping("/login")
    public String getLoginPage() {
        return "/site/login";
    }

    /**
    * 验证码生成
    * 敏感信息直接存session
    * */
    @GetMapping("/kaptcha")
    public void getKaptcha(HttpServletResponse response, HttpSession session) {
        // 生成验证码
        // 生成字符串
        String text = kaptchaProducer.createText();
        // 生成一张图像
        BufferedImage image = kaptchaProducer.createImage(text);

        /**
         * 验证码的归属
         * 此处进行优化
         * 将验证码存入session改为Redis
         * session.setAttribute("kaptcha", text);
        */
        session.setAttribute("kaptcha", text);
        /*String kaptchaOwner = CommunityUtil.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner", kaptchaOwner);
        cookie.setMaxAge(60);
        cookie.setPath(contextPath);
        response.addCookie(cookie);
        // 将验证码存入Redis
        String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(redisKey, text, 60, TimeUnit.SECONDS);*/

        // 将图片输出给浏览器，而不是通过return返回
        response.setContentType("/image/png");
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            logger.error("响应验证码失败：" + e.getMessage());
        }
    }

    /**
     * @description: 登录
     * @author: yangchao
     * @date: 2022/7/26 16:41
     * @param: [username, password, code, rememberme, model, response, kaptchaOwner]
     * @return: java.lang.String
     **/
    @PostMapping("/login")
    public String login(String username, String password, String code, boolean rememberme,
            Model model, HttpSession session, HttpServletResponse response) {
        /**
         * 1 检查验证码 (优化了)
         * String kaptcha = (String) session.getAttribute("kaptcha");
        */
        String kaptcha = (String) session.getAttribute("kaptcha");
        /*String kaptcha = null;
        if (StringUtils.isNotBlank(kaptchaOwner)) {
            String redisKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(redisKey);
        }*/
        // 取出session中的验证码和前端传入的验证码进行比较
        if (StringUtils.isBlank(kaptcha)
                || StringUtils.isBlank(code)
                || !kaptcha.equalsIgnoreCase(code)) {
            model.addAttribute("codeMsg", "验证码不正确！");
            return "/site/login";
        }
        // 2 验证码正确则检查账号，密码
        // 检查用户是否勾选了rememberme
        int expiredSeconds = rememberme ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        // 登录, 凭证录入数据库中, 返回 <"ticket", ticket凭证字符串对象>
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        String key = "ticket";
        // 若map里包含ticket，则登录成功，跳转到首页页面
        if (map.containsKey(key)) {
            // 从map中取出凭证放入cookie
            Cookie cookie = new Cookie(key, map.get(key).toString());
            // 设置有限范围: 整个项目cookie都有效
            cookie.setPath(contextPath);
            // 设置cookie有效时间
            cookie.setMaxAge(expiredSeconds);
            // 将凭证存到response中，下次浏览器再访问服务器会带着这个凭证
            response.addCookie(cookie);
            // 重定向到首页
            return "redirect:/index";
        } else {
            // 登录失败: 可能会出错且包含的信息，重新登录！
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "/site/login";
        }
    }

    /**
     * @description: 退出
     * @author: yangchao
     * @date: 2022/7/26 16:55
     * @param: [ticket]
     * @return: java.lang.String
     **/
    @GetMapping("/logout")
    // 使用@CookieValue注解要求springmvc注入值
    public String logout(@CookieValue("ticket") String ticket) {
        // 登录凭证设置为失效状态：1
        userService.logout(ticket);
        // 跳转到登录页面
        return "redirect:/login";
    }

}
