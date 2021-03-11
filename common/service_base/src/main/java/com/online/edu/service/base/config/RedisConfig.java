package com.online.edu.service.base.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.Serializable;
import java.time.Duration;

/**
 * 我们自定义一个 RedisTemplate，设置序列化器，这样我们可以很方便的操作实例对象。
 * 否则redis自动使用对象的jdk序列化
 * @EnableCaching 开启redis缓存注解  注意：要求 业务方法的返回值就必须是我们要放入缓存redis的内容 就是serviceImpl的方法
 */
@Configuration
@EnableCaching
public class RedisConfig {

    //我们用redisTemplate访问redis中的数据库，需要注入链接工厂，在方法中要添加这个参数，spring就会自动把这个bean注入进来
    // 我们存在redis中的中文时乱码，如果显示为中文，启动的时候加上 --rw
    // LettuceConnectionFactory 是使用连接池的工厂创建对象   RedisConnectionFactory 是原本的工厂创建对象
    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory factory){

        RedisTemplate<String,Object> template=new RedisTemplate<String,Object>();
        //使template可以访问数据库
        template.setConnectionFactory(factory);

        //设置key的序列化方式
        template.setKeySerializer(RedisSerializer.string());
        //普通的value的序列化方式
        template.setValueSerializer(RedisSerializer.json());
        //设置hash的key的序列化方式
        template.setHashKeySerializer(RedisSerializer.string());
        //设置hash的value的序列化方式
        template.setHashValueSerializer(RedisSerializer.json());

        //让配置生效
        template.afterPropertiesSet();
        return template;
    }

//    @Bean
//    public RedisTemplate<String, Serializable> redisTemplate(LettuceConnectionFactory connectionFactory) {
//        RedisTemplate<String, Serializable> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setKeySerializer(new StringRedisSerializer());//key序列化方式
//        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());//value序列化
//        redisTemplate.setConnectionFactory(connectionFactory);
//
//        return redisTemplate;
//    }

    // 设置Redis缓存中显示的字符编码形式
    @Bean
    public CacheManager cacheManager(LettuceConnectionFactory connectionFactory) {
        // 创建RedisCacheCon的配置对象 进行设置
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                //过期时间600秒
                .entryTtl(Duration.ofSeconds(600))
                // 配置序列化 key的序列化 value的序列化
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();  // 设置如果查询到的数据为null，就不存入缓存中
        //  设置完成后 进行配置
        RedisCacheManager cacheManager = RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
        return cacheManager;
    }
}
