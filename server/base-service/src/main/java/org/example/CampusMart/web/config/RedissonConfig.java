package org.example.CampusMart.web.config;

import org.springframework.context.annotation.Configuration;

/**
 * Redisson 配置类
 *
 * 当前项目为单体架构，缓存并发控制使用 Spring Cache 的 sync=true 即可满足需求。
 * Redisson 客户端由 redisson-spring-boot-starter 自动注入到 Spring 容器（RedissonClient），
 * 保留该依赖，未来若项目集群化，可启用下方分布式锁方案。
 */
@Configuration
public class RedissonConfig {

    /*
     * 集群环境下防止缓存击穿的分布式锁方案（已保留，供后续扩展）。
     * 单体环境下使用 Spring Cache 的 sync=true 更加轻量。
     */
//    @Bean
//    public CacheManager cacheManager(RedissonClient redissonClient) {
//        Map<String, org.redisson.spring.cache.CacheConfig> config = new HashMap<>();
//
//        // 商品详情缓存：30 分钟过期
//        config.put("goods:detail", new org.redisson.spring.cache.CacheConfig(30 * 60 * 1000, 0));
//        // 用户信息缓存：60 分钟过期
//        config.put("user:info", new org.redisson.spring.cache.CacheConfig(60 * 60 * 1000, 0));
//        // 最近聊天记录缓存：10 分钟过期
//        config.put("message:recent", new org.redisson.spring.cache.CacheConfig(10 * 60 * 1000, 0));
//        // 钱包余额缓存：5 分钟过期
//        config.put("wallet:balance", new org.redisson.spring.cache.CacheConfig(5 * 60 * 1000, 0));
//
//        org.redisson.spring.cache.RedissonSpringCacheManager cacheManager =
//                new org.redisson.spring.cache.RedissonSpringCacheManager(redissonClient, config);
//        // 允许缓存 null 值，用于解决缓存穿透
//        cacheManager.setAllowNullValues(true);
//        return cacheManager;
//    }
}
