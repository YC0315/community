package com.yc.communitys.dao;

import com.yc.communitys.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @description: ES 可以看做是一个特殊的数据库，ElasticsearchRepository<实体类名, 主键类型>
 * @author: yangchao
 * @date: 2022/8/1 15:45
 **/
@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, Integer> {
}
