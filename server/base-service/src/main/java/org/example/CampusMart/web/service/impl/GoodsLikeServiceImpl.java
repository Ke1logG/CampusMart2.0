package org.example.CampusMart.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.CampusMart.model.entity.GoodsLike;
import org.example.CampusMart.web.mapper.GoodsLikeMapper;
import org.example.CampusMart.web.service.GoodsLikeService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class GoodsLikeServiceImpl extends ServiceImpl<GoodsLikeMapper, GoodsLike>
        implements GoodsLikeService {

    @Override
    public boolean like(Long userId, Long goodId) {
        if (isLiked(userId, goodId)) {
            return true;
        }
        GoodsLike goodsLike = GoodsLike.builder()
                .userID(userId)
                .goodID(goodId)
                .createTime(new Date())
                .build();
        return save(goodsLike);
    }

    @Override
    public boolean unlike(Long userId, Long goodId) {
        LambdaQueryWrapper<GoodsLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GoodsLike::getUserID, userId).eq(GoodsLike::getGoodID, goodId);
        return remove(wrapper);
    }

    @Override
    public boolean isLiked(Long userId, Long goodId) {
        LambdaQueryWrapper<GoodsLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GoodsLike::getUserID, userId).eq(GoodsLike::getGoodID, goodId);
        return count(wrapper) > 0;
    }

    @Override
    public List<Long> getLikedGoodIds(Long userId) {
        return baseMapper.selectLikedGoodIdsByUserId(userId);
    }
}
