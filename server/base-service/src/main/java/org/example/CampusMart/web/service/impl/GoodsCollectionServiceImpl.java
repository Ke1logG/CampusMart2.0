package org.example.CampusMart.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.CampusMart.model.entity.GoodsCollection;
import org.example.CampusMart.web.mapper.GoodsCollectionMapper;
import org.example.CampusMart.web.service.GoodsCollectionService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class GoodsCollectionServiceImpl extends ServiceImpl<GoodsCollectionMapper, GoodsCollection>
        implements GoodsCollectionService {

    @Override
    public boolean collect(Long userId, Long goodId) {
        if (isCollected(userId, goodId)) {
            return true;
        }
        GoodsCollection collection = GoodsCollection.builder()
                .userID(userId)
                .goodID(goodId)
                .createTime(new Date())
                .build();
        return save(collection);
    }

    @Override
    public boolean uncollect(Long userId, Long goodId) {
        LambdaQueryWrapper<GoodsCollection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GoodsCollection::getUserID, userId).eq(GoodsCollection::getGoodID, goodId);
        return remove(wrapper);
    }

    @Override
    public boolean isCollected(Long userId, Long goodId) {
        LambdaQueryWrapper<GoodsCollection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GoodsCollection::getUserID, userId).eq(GoodsCollection::getGoodID, goodId);
        return count(wrapper) > 0;
    }

    @Override
    public List<Long> getCollectedGoodIds(Long userId) {
        return baseMapper.selectCollectedGoodIdsByUserId(userId);
    }
}
