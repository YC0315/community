package com.yc.communitys.controller;


import com.yc.communitys.annotation.LoginRequired;
import com.yc.communitys.entity.User;
import com.yc.communitys.service.FollowService;
import com.yc.communitys.service.LikeService;
import com.yc.communitys.service.UserService;
import com.yc.communitys.util.CommunityConstant;
import com.yc.communitys.util.CommunityUtil;
import com.yc.communitys.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @description: 用户
 * @author: yangchao
 * @date: 2022/7/27 16:28
 * @param:
 * @return:
 **/
@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    // 资源 上传路径
    @Value("${communitys.path.upload}")
    private String uploadPath;

    // 域名
    @Value("${communitys.path.domain}")
    private String domain;

    // 项目的访问路径
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    /**
     * @description: 页面跳转
     * @author: yangchao
     * @date: 2022/7/27 16:38
     * @param: [model]
     * @return: java.lang.String
     **/
    @LoginRequired
    @GetMapping("/setting")
    public String getSettingPage(Model model) {
        return "/site/setting";
    }

    /**
     * @description: 修改头像请求 Spring MVC 专门处理上传文件的工具-MultipartFile,注解 @LoginRequired 规定需要登录才能跳转页面
     * @author: yangchao
     * @date: 2022/7/27 16:37
     * @param: [headerImage, model]
     * @return: java.lang.String
     **/
    @LoginRequired
    @PostMapping("/upload")
    public String uploadHeader(MultipartFile headerImage, Model model) {
        // 判断有没有图片
        if (headerImage == null) {
            model.addAttribute("error", "您还没有选择图片");
            return "/site/setting";  // 回到上传页面
        }

        // 为了避免图片的名称覆盖，生成一个随机名称，后缀不要变，通过图片的后缀名来判断图片格式
        String fileName = headerImage.getOriginalFilename(); // 获得原始文件名
        String suffix = fileName.substring(fileName.lastIndexOf(".")); // 获得文件后缀名
        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "文件格式不正确");
            return "/site/setting";
        }

        // 生成随机文件名再带上后缀
        fileName = CommunityUtil.generateUUID() + suffix;
        // 确定文件存放的路径，全限定名
        File dest = new File(uploadPath + "/" + fileName);
        try {
            // 把图片写入目标文件 (可以检查uploadPath是否存在, 否则创建该目录)
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败：", e.getMessage());
            // 抛出异常
            throw new RuntimeException("上传文件失败，服务器发生异常", e);
        }

        // 更新当前用户头像的路径(web访问路径)，不是存放路径
        // http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(), headerUrl);
        // 重定向到首页
        return "redirect:/index";
    }

    /**
     * @description: 获取用户头像
     * @author: yangchao
     * @date: 2022/7/27 16:48
     * @param: [fileName, response]
     * @return: void
     **/
    @GetMapping("/header/{fileName}")
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // 服务器上头像的存放路径
        fileName = uploadPath + "/" + fileName;
        // 获取文件的后缀
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        // 响应图片，二进制数据，使用字节流
        response.setContentType("image/" + suffix); // 固定写法

        try (FileInputStream fis = new FileInputStream(fileName);
             OutputStream os = response.getOutputStream();
        ) {
            // 使用一个缓冲区
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) { // 读到b个字节的数据, 最大1024个字节
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败：", e.getMessage());
        }
    }

    /**
     * @description: 修改密码
     * @author: yangchao
     * @date: 2022/7/27 17:05
     **/
    @PostMapping("/updatePassword")
    public String updatePassword(String oldPassword, String newPassword, String confirmPassword, Model model) {
        if (StringUtils.isBlank(oldPassword)) {
            model.addAttribute("oldPasswordMsg", "原密码不能为空！");
            return "/site/setting";
        }
        if (StringUtils.isBlank(newPassword)) {
            model.addAttribute("newPasswordMsg", "新密码不能为空！");
            return "/site/setting";
        }
        if (StringUtils.isBlank(confirmPassword)) {
            model.addAttribute("confirmPasswordMsg", "确认密码不能为空！");
            return "/site/setting";
        }
        if (!confirmPassword.equals(newPassword)) {
            model.addAttribute("confirmPasswordMsg", "两次输入的密码不一致！");
            return "/site/setting";
        }
        User user = hostHolder.getUser();
        String oldPw = CommunityUtil.md5(oldPassword + user.getSalt());
        // 校验原密码
        if (!oldPw.equals(user.getPassword())) {
            model.addAttribute("oldPasswordMsg", "原密码不正确！");
            return "/site/setting";
        }
        // 更新密码,重定向到登陆页面
        userService.updatePassword(user.getId(), CommunityUtil.md5(newPassword + user.getSalt()));
        return "redirect:/index";
    }

    /**
     * @description: 个人主页
     * @author: yangchao
     * @date: 2022/7/30 17:23
     * @param: [userId, model]
     * @return: java.lang.String
     **/
    @LoginRequired
    @GetMapping("/profile/{userId}")
    public String getProfilePage(@PathVariable("userId") int userId, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在!");
        }

        // 用户
        model.addAttribute("user", user);
        // 点赞数量
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);

        // 关注数量
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);
        // 粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);
        // 是否已关注
        boolean hasFollowed = false;
        if (hostHolder.getUser() != null) {
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);

        return "/site/profile";
    }

}
