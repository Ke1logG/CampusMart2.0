package org.example.CampusMart.web.config;

import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Redisson 配置类
 * 用于配置 RedissonSpringCacheManager，预定义缓存名称及过期时间
 */
@Configuration
public class RedissonConfig {

    /**
     * 配置基于 Redisson 的 Spring CacheManager
     *
     * @param redissonClient Redisson 客户端，由 redisson-spring-boot-starter 自动配置
     * @return RedissonSpringCacheManager
     */
    @Bean
    public CacheManager cacheManager(RedissonClient redissonClient) {
        Map<String, CacheConfig> config = new HashMap<>();

        // 商品详情缓存：30 分钟过期
        config.put("goods:detail", new CacheConfig(30 * 60 * 1000, 0));
        // 用户信息缓存：60 分钟过期
        config.put("user:info", new CacheConfig(60 * 60 * 1000, 0));
        // 最近聊天记录缓存：10 分钟过期
        config.put("message:recent", new CacheConfig(10 * 60 * 1000, 0));
        // 钱包余额缓存：5 分钟过期
        config.put("wallet:balance", new CacheConfig(5 * 60 * 1000, 0));

        return new RedissonSpringCacheManager(redissonClient, config);
    }
}
