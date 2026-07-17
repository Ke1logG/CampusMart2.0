package org.example.CampusMart.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.CampusMart.model.entity.GoodsLike;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GoodsLikeMapper extends BaseMapper<GoodsLike> {

    List<Long> selectLikedGoodIdsByUserId(@Param("userId") Long userId);
}
