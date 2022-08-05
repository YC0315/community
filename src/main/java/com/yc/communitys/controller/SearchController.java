package com.yc.communitys.controller;

import com.yc.communitys.entity.DiscussPost;
import com.yc.communitys.entity.Page;
import com.yc.communitys.service.ElasticsearchService;
import com.yc.communitys.service.LikeService;
import com.yc.communitys.service.UserService;
import com.yc.communitys.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 处理搜索请求，展现结果
@Controller
public class SearchController implements CommunityConstant {
    // 查询帖子
    @Autowired
    private ElasticsearchService elasticsearchService;
    // 搜到帖子以后还要展现作者
    @Autowired
    private UserService userService;
    /// 展现帖子的点赞数
    @Autowired
    private LikeService likeService;

    /**
     * @description: 搜索帖子
     * @author: yangchao
     * @date: 2022/8/2 15:36
     * @param: [keyword, page, model]  搜索关键字，分页条件， 查到的数据返回给Model  search?keyword=xxx
     * @return: java.lang.String
     **/
    @GetMapping( "/search")
    public String search(String keyword, Page page, Model model) {
        // 搜索帖子
        org.springframework.data.domain.Page<DiscussPost> searchResult =
                elasticsearchService.searchDiscussPost(keyword, page.getCurrent() - 1, page.getLimit());
        // 聚合数据，填充用户数据和点赞数据
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (searchResult != null) {
            for (DiscussPost post : searchResult) {
                Map<String, Object> map = new HashMap<>();
                // 帖子
                map.put("post", post);
                // 填充作者
                map.put("user", userService.findUserById(post.getUserId()));
                // 填充点赞数量，参数，实体类型和实体id
                map.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId()));

                discussPosts.add(map);
            }
        }
        // 将聚合数据传给model
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("keyword", keyword);

        // 设置分页路径和设置总的页数
        page.setPath("/search?keyword=" + keyword);
        page.setRows(searchResult == null ? 0 : (int) searchResult.getTotalElements());

        return "/site/search";
    }

}
