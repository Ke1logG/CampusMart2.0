package org.example.CampusMart.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.CampusMart.model.entity.Goods;
import org.example.CampusMart.web.service.GoodsService;
import org.example.CampusMart.web.mapper.GoodsMapper;
import org.example.CampusMart.web.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
* @author a32271
* @description 针对表【goods】的数据库操作Service实现
* @createDate 2025-12-27 23:08:03
*/
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods>
    implements GoodsService{

    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    @Cacheable(value = "goods:detail", key = "#id", sync = true)
    public Goods getById(Serializable id) {
        return super.getById(id);
    }

    /*
     * 集群环境下防止缓存击穿的 Redisson 分布式锁方案（已保留，供后续扩展）。
     * 单体环境下 Spring Cache 的 sync=true 已能满足同一 JVM 内的并发控制需求。
     */
//    @Autowired
//    private RedissonClient redissonClient;
//
//    private static final String GOODS_DETAIL_KEY_PREFIX = "goods:detail:";
//    private static final String GOODS_DETAIL_LOCK_PREFIX = "lock:goods:detail:";
//    private static final long GOODS_DETAIL_TIMEOUT = 30;
//
//    @Override
//    public Goods getById(Serializable id) {
//        String key = GOODS_DETAIL_KEY_PREFIX + id;
//        RBucket<Goods> bucket = redissonClient.getBucket(key);
//
//        // 1. 先查缓存（包括缓存的 null 值）
//        Goods goods = bucket.get();
//        if (goods != null || bucket.isExists()) {
//            return goods;
//        }
//
//        // 2. 缓存未命中，尝试获取分布式锁
//        String lockKey = GOODS_DETAIL_LOCK_PREFIX + id;
//        RLock lock = redissonClient.getLock(lockKey);
//        try {
//            // 最多等待 3 秒；不指定 leaseTime，启用 Redisson 看门狗自动续期
//            if (lock.tryLock(3, TimeUnit.SECONDS)) {
//                try {
//                    // 3. 双重检查：其他线程可能已查询并写入缓存
//                    goods = bucket.get();
//                    if (goods != null || bucket.isExists()) {
//                        return goods;
//                    }
//
//                    // 4. 查询数据库
//                    goods = super.getById(id);
//
//                    // 5. 写入缓存，null 值也会缓存（缓存穿透防护）
//                    bucket.set(goods, GOODS_DETAIL_TIMEOUT, TimeUnit.MINUTES);
//                    return goods;
//                } finally {
//                    if (lock.isHeldByCurrentThread()) {
//                        lock.unlock();
//                    }
//                }
//            } else {
//                // 6. 获取锁失败，降级直接查数据库
//                return super.getById(id);
//            }
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//            return super.getById(id);
//        }
//    }

    @Override
    @CacheEvict(value = "goods:detail", key = "#entity.goodID")
    public boolean save(Goods entity) {
        return super.save(entity);
    }

    @Override
    @CacheEvict(value = "goods:detail", key = "#entity.goodID")
    public boolean updateById(Goods entity) {
        return super.updateById(entity);
    }

    @Override
    @CacheEvict(value = "goods:detail", key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    public IPage<GoodsVo> pageGoods(IPage<GoodsVo> page) {
        return goodsMapper.selectGoodsPage(page);
    }

    @Override
    public IPage<GoodsVo> searchGoodsByTitle(IPage<GoodsVo> page, LambdaQueryWrapper<Goods> queryWrapper) {
        return goodsMapper.selectGoodsByTitle(page, queryWrapper);
    }

    @Override
    public IPage<GoodsVo> searchGoodsByPublisherId(IPage<GoodsVo> page, LambdaQueryWrapper<Goods> queryWrapper) {
        return goodsMapper.selectGoodsByPublisherId(page,queryWrapper);
    }

}
