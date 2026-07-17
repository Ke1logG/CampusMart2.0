package org.example.CampusMart.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.CampusMart.model.entity.GoodsCollection;

import java.util.List;

public interface GoodsCollectionService extends IService<GoodsCollection> {

    boolean collect(Long userId, Long goodId);

    boolean uncollect(Long userId, Long goodId);

    boolean isCollected(Long userId, Long goodId);

    List<Long> getCollectedGoodIds(Long userId);
}
