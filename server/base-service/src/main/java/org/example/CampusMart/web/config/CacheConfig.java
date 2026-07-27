package org.example.CampusMart.web.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Spring Cache 配置类
 *
 * 使用 Spring Data Redis 提供的 RedisCacheManager 作为缓存管理器。
 * - cacheNullValues()：开启缓存空值，解决缓存穿透问题。
 * - 不同缓存名称配置不同 TTL：使各类缓存错开过期时间，缓解缓存雪崩。
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        // 默认缓存配置：30 分钟过期
        // Spring Data Redis 3.x 默认允许缓存 null 值，用于解决缓存穿透
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30));

        // 为不同缓存名称配置独立 TTL，使过期时间错开
        Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
        configMap.put("goods:detail", defaultConfig.entryTtl(Duration.ofMinutes(30)));
        configMap.put("user:info", defaultConfig.entryTtl(Duration.ofMinutes(60)));
        configMap.put("message:recent", defaultConfig.entryTtl(Duration.ofMinutes(10)));
        configMap.put("wallet:balance", defaultConfig.entryTtl(Duration.ofMinutes(5)));

        return RedisCacheManager.builder(factory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(configMap)
                .build();
    }
}
