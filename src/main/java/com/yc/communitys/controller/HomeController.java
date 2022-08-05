package com.yc.communitys.controller;

import com.yc.communitys.entity.DiscussPost;
import com.yc.communitys.entity.Page;
import com.yc.communitys.entity.User;
import com.yc.communitys.service.DiscussPostService;
import com.yc.communitys.service.LikeService;
import com.yc.communitys.service.UserService;
import com.yc.communitys.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: yangchao
 * @createTime: 2022-07-25  15:29
 * @description: 访问主页
 */
@Controller
public class HomeController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @RequestMapping(path = "/index", method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page,
                               @RequestParam(name = "orderMode", defaultValue = "0") int orderMode){
        /**
         方法调用前, SpringMVC会自动实例化Model和Page,并将Page注入Model.
         所以,在thymeleaf中可以直接访问Page对象中的数据.
         */
        // 设置总行数
        page.setRows(discussPostService.selectDiscussPostRows(0));
        // 设置访问路径
        page.setPath("/index?orderMode=" + orderMode);
        List<DiscussPost> list = discussPostService.selectDiscussPosts(0, page.getOffset(), page.getLimit(), orderMode);
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if(list != null){
            for (DiscussPost post : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                int userId = post.getUserId();
                User user = userService.findUserById(userId);
                map.put("user", user);

                // 添加点赞
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount", likeCount);

                discussPosts.add(map);
            }
        }
        // 将要展示的结果装到model中去
        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("orderMode", orderMode);
        return "/index";
    }

    /**
     * @description: 错误页面的路径
     * @author: yangchao
     * @date: 2022/7/30 15:08
     * @param: []
     * @return: java.lang.String
     **/
    @GetMapping("/error")
    public String getErrorPage() {
        return "/error/500";
    }

    @GetMapping("/denied")
    public String getDeniedPage() {
        return "/error/404";
    }
}
