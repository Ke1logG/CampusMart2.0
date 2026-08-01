package org.example.CampusMart.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.xiaoymin.knife4j.core.util.StrUtil;

import cn.hutool.json.JSONUtil;

import org.example.CampusMart.model.entity.Goods;
import org.example.CampusMart.web.service.GoodsService;
import org.example.CampusMart.web.mapper.GoodsMapper;
import org.example.CampusMart.web.vo.GoodsVo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

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

/*
     
    单体环境下 Spring Cache 的 sync=true 已能满足同一 JVM 内的并发控制需求。
      
    @Override
    @Cacheable(value = "goods:detail", key = "#id", sync = true)
    public Goods getById(Serializable id) {
        return super.getById(id);
    }
*/  

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;

    private static final String GOODS_DETAIL_KEY_PREFIX = "goods:detail::";
    private static final String GOODS_DETAIL_LOCK_PREFIX = "lock:goods:detail:";

    @Override
    public Goods getById(Serializable id){
        String key = GOODS_DETAIL_KEY_PREFIX + id;

        //1. 查缓存（快速路径)
        String json = redisTemplate.opsForValue().get(key);
        if(StrUtil.isNotBlank(json)){
            return JSONUtil.toBean(json, Goods.class);
        }
        if(json != null){
            return null;
        }

        //2. 获取redisson分布式锁
        String lockkey = GOODS_DETAIL_LOCK_PREFIX + id;
        RLock lock = redissonClient.getLock(lockkey);
        try{
            //获取锁加定时删除
            boolean isLocked = lock.tryLock(100, 5000, TimeUnit.MILLISECONDS);
            if(!isLocked){
                Thread.sleep(50);
                return getById(id); //自旋重试
            }
            try{
                //3. DCL双重检查，看缓存里是否其他线程已经查上来值了
                json = redisTemplate.opsForValue().get(key);
                if(StrUtil.isNotBlank(json)){
                    return JSONUtil.toBean(json, Goods.class);
                }
                if(json != null){
                    return null;
                }

                //4. 实在没值，查数据库
                Goods goods = super.getById(id);

                //5. 把查上来的值写进缓存
                //TTL随机防止缓存雪崩
                long ttl = 20 + (long) (Math.random()*10);
                if(goods != null){
                    redisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(goods), ttl, TimeUnit.MINUTES);
                }else{
                    redisTemplate.opsForValue().set(key, "", 5, TimeUnit.MINUTES);
                }
                return goods;
            }finally{
                //只有当当前线程还持有锁才释放（防止误删）
                if(lock.isHeldByCurrentThread()){
                    lock.unlock();
                }
            }
        }catch(InterruptedException e){
            Thread.currentThread().interrupt();
            //降级查库兜底
            return super.getById(id);
        }
    }

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
