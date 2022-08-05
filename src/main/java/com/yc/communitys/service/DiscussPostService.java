package com.yc.communitys.service;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.yc.communitys.dao.DiscussPostMapper;
import com.yc.communitys.entity.DiscussPost;
import com.yc.communitys.util.SensitiveFilter;
import org.apache.ibatis.annotations.Param;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author: yangchao
 * @createTime: 2022-07-25  15:16
 * @description: 帖子业务逻辑层
 */
@Service
public class DiscussPostService {

    private static final Logger logger = LoggerFactory.getLogger(DiscussPostService.class);

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    /** 工具-过滤敏感词 */
    private SensitiveFilter sensitiveFilter;

    // 本地缓存缓存帖子列表的最大容量，本地缓存在本地服务器，不是Redis服务器
    @Value("${caffeine.posts.max-size}")
    private int maxSize;
    // 缓存过期时间
    @Value("${caffeine.posts.expire-seconds}")
    private int expireSeconds;

    // Caffeine核心接口: Cache, LoadingCache, AsyncLoadingCache

    // 帖子列表缓存
    private LoadingCache<String, List<DiscussPost>> postListCache;

    // 帖子总数缓存
    private LoadingCache<Integer, Integer> postRowsCache;

    @PostConstruct
    public void init() {
        // 初始化帖子列表缓存，使用newBuilder()工具构建cache
        postListCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Nullable
                    @Override
                    // 查询数据库将结果放入缓存
                    public List<DiscussPost> load(@NonNull String key) throws Exception {
                        if (key == null || key.length() == 0) {
                            throw new IllegalArgumentException("参数错误!");
                        }
                        // 解析key,key使用offset:limit拼接而来
                        String[] params = key.split(":");
                        if (params == null || params.length != 2) {
                            throw new IllegalArgumentException("参数错误!");
                        }
                        int offset = Integer.valueOf(params[0]);
                        int limit = Integer.valueOf(params[1]);

                        // 二级缓存: Redis -> mysql
                        // 查询数据库
                        logger.debug("load post list from DB.");
                        return discussPostMapper.selectDiscussPosts(0, offset, limit, 1);
                    }
                });
        // 初始化帖子总数缓存
        postRowsCache = Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Nullable
                    @Override
                    // 从数据库查询数据放入本地缓存中
                    public Integer load(@NonNull Integer key) throws Exception {
                        logger.debug("load post rows from DB.");
                        return discussPostMapper.selectDiscussPostRows(key);
                    }
                });
    }

    /*
     * @description: 查询帖子
     * @author: yangchao
     * @date: 2022/7/25 15:18
     * @param: [userId, offset, limit]
     * @return: java.util.List<com.yc.communitys.entity.DiscussPost>
     **/
    public List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit, int orderMode){
        // 用户未登录并且查看的热帖列表，则直接从缓存中取
        if (userId == 0 && orderMode == 1) {
            return postListCache.get(offset + ":" + limit);
        }

        logger.debug("load post list from DB.");
        return discussPostMapper.selectDiscussPosts(userId, offset, limit, orderMode);
    }

    /**
     * @description: 查询帖子行数
     * @author: yangchao
     * @date: 2022/7/25 15:19
     * @param: [userId]
     * @return: int
     **/
    public int selectDiscussPostRows(@Param("userId") int userId){
        // 如果用户查看帖子则不走本地缓存
        if (userId == 0) {
            return postRowsCache.get(userId);
        }

        logger.debug("load post rows from DB.");
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    /**
     * @description: 增加帖子
     * @author: yangchao
     * @date: 2022/7/28 16:22
     * @param: [post]
     * @return: int
     **/
    public int addDiscussPost(DiscussPost post) {

        if (post == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        // HtmlUtils.htmlEscape(): 转义HTML标记
        // 将内容传进来，如果带着大于小于这种标签就进行转义，防止注入
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));

        // 对表中的标题和内容进行过滤敏感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));

        return discussPostMapper.insertDiscussPost(post);
    }

    /**
     * @description: 根据id查询帖子详情
     * @author: yangchao
     * @date: 2022/7/28 16:49
     * @param: [id]
     * @return: com.yc.communitys.entity.DiscussPost
     **/
    public DiscussPost findDiscussPostById(int id) {
        return discussPostMapper.selectDiscussPostById(id);
    }

    /**
     * @description: 更新评论的数量
     * @author: yangchao
     * @date: 2022/7/29 16:48
     * @param: [id, commentCount]
     * @return: int
     **/
    public int updateCommentCount(int id, int commentCount) {
        return discussPostMapper.updateCommentCount(id, commentCount);
    }

    /**
     * @description: 更新帖子类型
     * @author: yangchao
     * @date: 2022/8/3 15:07
     * @param: [id, type]
     * @return: int
     **/
    public int updateType(int id, int type) {
        return discussPostMapper.updateType(id, type);
    }

    /**
     * @description: 更新帖子状态
     * @author: yangchao
     * @date: 2022/8/3 15:07
     * @param: [id, status]
     * @return: int
     **/
    public int updateStatus(int id, int status) {
        return discussPostMapper.updateStatus(id, status);
    }

    /**
     * 更新帖子分数
     *
     * @param id
     * @param score
     */
    public void updateScore(int id, double score) {
        discussPostMapper.updateScore(id, score);
    }


}
