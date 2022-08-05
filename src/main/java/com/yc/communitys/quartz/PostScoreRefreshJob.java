package com.yc.communitys.quartz;

import com.yc.communitys.entity.DiscussPost;
import com.yc.communitys.service.DiscussPostService;
import com.yc.communitys.service.ElasticsearchService;
import com.yc.communitys.service.LikeService;
import com.yc.communitys.util.CommunityConstant;
import com.yc.communitys.util.RedisKeyUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.quartz.Job;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author: yangchao
 * @createTime: 2022-08-04  14:15
 * @description: 帖子分数刷新
 */
public class PostScoreRefreshJob implements Job, CommunityConstant {
    // 记录日志
    private static final Logger logger = LoggerFactory.getLogger(PostScoreRefreshJob.class);

    @Autowired
    private RedisTemplate redisTemplate;

    // 需要查帖子，点赞数量等
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private ElasticsearchService elasticsearchService;

    // 牛客纪元
    private static final Date epoch;

    // 在静态块中初始化epoch，因为这是个常量，只需要初始化一次
    static {
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-08-01 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("初始化牛客纪元失败!", e);
        }
    }

    // 定时任务
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // 取出key
        String redisKey = RedisKeyUtil.getPostScoreKey();
        BoundSetOperations operations = redisTemplate.boundSetOps(redisKey);

        if (operations.size() == 0) {
            logger.info("[任务取消] 没有需要刷新的帖子!");
            return;
        }

        logger.info("[任务开始] 正在刷新帖子分数: " + operations.size());
        while (operations.size() > 0) {
            this.refresh((Integer) operations.pop());
        }
        logger.info("[任务结束] 帖子分数刷新完毕!");
    }

    // 刷新帖子分数
    private void refresh(int postId) {
        // 查出帖子
        DiscussPost post = discussPostService.findDiscussPostById(postId);

        if (post == null) {
            logger.error("该帖子不存在: id = " + postId);
            return;
        }

        // 是否精华
        boolean wonderful = post.getStatus() == 1;
        // 评论数量
        int commentCount = post.getCommentCount();
        // 点赞数量
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, postId);

        // score = log(精华分+评论数*10+点赞数*2+收藏数*2)+(发布时间-牛客纪元)
        // 计算权重
        double w = (wonderful ? 75 : 0) + commentCount * 10 + likeCount * 2;
        // 分数 = 帖子权重 + 距离天数
        double score = Math.log10(Math.max(w, 1))
                + (post.getCreateTime().getTime() - epoch.getTime()) / (1000 * 3600 * 24);// 两个毫秒相减
        // 更新帖子分数
        discussPostService.updateScore(postId, score);
        // 同步到ES数据库中的数据，刷新score
        post.setScore(score);
        elasticsearchService.saveDiscussPost(post);
    }
}
