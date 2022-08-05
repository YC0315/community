package com.yc.communitys.config;

import com.yc.communitys.quartz.PostScoreRefreshJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

/**
 * @Description: quartz 分布式定时任务调度相关配置类
 * 作用：
 * 1. -> 仅仅当第一次访问时读取该配置
 * 2. -> 并将该配置封装的信息初始化到数据库数据库
 * 3. -> 以后每次quartz是访问数据去调用,而不再访问该配置类！
 */
// 配置 -> 数据库 -> 调用
@Configuration
public class QuartzConfig {

    // FactoryBean可简化Bean的实例化过程:
    // 1.通过FactoryBean封装Bean的实例化过程.
    // 2.将FactoryBean装配到Spring容器里.
    // 3.将FactoryBean注入给其他的Bean.
    // 4.该Bean得到的是FactoryBean所管理的对象实例.
    // FactoryBean里面管理的是某个Bean，

    // 刷新帖子分数任务
    // 配置JobDetail
     @Bean
    public JobDetailFactoryBean postScoreRefreshJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(PostScoreRefreshJob.class);
        factoryBean.setName("postScoreRefreshJob");
        factoryBean.setGroup("communityJobGroup");
        factoryBean.setDurability(true);
        factoryBean.setRequestsRecovery(true);
        return factoryBean;
    }

    // 配置Trigger(SimpleTriggerFactoryBean, CronTriggerFactoryBean)
     @Bean
    public SimpleTriggerFactoryBean postScoreRefreshTrigger(JobDetail postScoreRefreshJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(postScoreRefreshJobDetail);
        factoryBean.setName("postScoreRefreshTrigger");
        factoryBean.setGroup("communityTriggerGroup");
        factoryBean.setRepeatInterval(1000*60*5); // 5分钟刷新帖子分值
        factoryBean.setJobDataMap(new JobDataMap());
        return factoryBean;
    }

}
