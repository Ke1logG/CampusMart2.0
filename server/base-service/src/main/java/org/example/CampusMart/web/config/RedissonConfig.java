package org.example.CampusMart.web.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * Redisson 配置类
 *
 * 当前项目为单体架构，缓存并发控制使用 Spring Cache 的 sync=true 即可满足需求。
 * 显式配置 RedissonClient，避免空密码环境变量导致 Redisson 发送 AUTH 命令失败。
 * 保留 RedissonSpringCacheManager 方案，未来若项目集群化可启用。
 */
@Configuration
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient(RedisProperties redisProperties) {
        Config config = new Config();
        String address = "redis://" + redisProperties.getHost() + ":" + redisProperties.getPort();
        SingleServerConfig serverConfig = config.useSingleServer().setAddress(address);

        // 只在密码非空时才设置，避免无密码 Redis 因收到 AUTH 命令而拒绝连接
        if (StringUtils.hasText(redisProperties.getPassword())) {
            serverConfig.setPassword(redisProperties.getPassword());
        }

        serverConfig.setDatabase(redisProperties.getDatabase());
        return Redisson.create(config);
    }

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
