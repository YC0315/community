package com.yc.communitys.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * @description: Redis的配置类，配置RedisTemplate
 * @author: yangchao
 * @date: 2022/7/30 16:15
 * @param:
 * @return:
 **/
@Configuration
public class RedisConfig {

    @Bean  // 将返回值对象RedisTemplate放入Spring ioc容器中
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        // 实例化Bean
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        /* 设置key的序列化方式
        * String
        * RedisSerializer.string()返回能够将key序列化为String的序列化器
        * */
        template.setKeySerializer(RedisSerializer.string());

        /* 设置value的序列化
        * RedisSerializer.json()返回能够将value序列化为json格式的序列化器
        * */
        template.setValueSerializer(RedisSerializer.json());

        /* 设置hash的key的序列化方式
        * String
        * */
        template.setHashKeySerializer(RedisSerializer.string());

        /* 设置hash的value的序列化方式 */
        template.setHashValueSerializer(RedisSerializer.json());

        // 设置生效
        template.afterPropertiesSet();

        return template;
    }

}
