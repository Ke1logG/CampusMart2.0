package org.example.CampusMart.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.CampusMart.model.entity.Goods;
import org.example.CampusMart.web.service.GoodsService;
import org.example.CampusMart.web.mapper.GoodsMapper;
import org.example.CampusMart.web.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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

    private static final String GOODS_DETAIL_KEY_PREFIX = "goods:detail:";
    private static final long GOODS_DETAIL_TIMEOUT = 30;

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Goods getById(Serializable id) {
        String key = GOODS_DETAIL_KEY_PREFIX + id;
        Goods goods = (Goods) redisTemplate.opsForValue().get(key);
        if (goods != null) {
            return goods;
        }
        goods = super.getById(id);
        if (goods != null) {
            redisTemplate.opsForValue().set(key, goods, GOODS_DETAIL_TIMEOUT, TimeUnit.MINUTES);
        }
        return goods;
    }

    @Override
    public boolean save(Goods entity) {
        boolean result = super.save(entity);
        if (result && entity.getGoodID() != null) {
            redisTemplate.delete(GOODS_DETAIL_KEY_PREFIX + entity.getGoodID());
        }
        return result;
    }

    @Override
    public boolean updateById(Goods entity) {
        boolean result = super.updateById(entity);
        if (result && entity.getGoodID() != null) {
            redisTemplate.delete(GOODS_DETAIL_KEY_PREFIX + entity.getGoodID());
        }
        return result;
    }

    @Override
    public boolean removeById(Serializable id) {
        boolean result = super.removeById(id);
        if (result) {
            redisTemplate.delete(GOODS_DETAIL_KEY_PREFIX + id);
        }
        return result;
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




