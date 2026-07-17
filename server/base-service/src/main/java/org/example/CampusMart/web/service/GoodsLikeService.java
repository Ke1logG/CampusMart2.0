package org.example.CampusMart.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.CampusMart.model.entity.GoodsLike;

import java.util.List;

public interface GoodsLikeService extends IService<GoodsLike> {

    boolean like(Long userId, Long goodId);

    boolean unlike(Long userId, Long goodId);

    boolean isLiked(Long userId, Long goodId);

    List<Long> getLikedGoodIds(Long userId);
}
