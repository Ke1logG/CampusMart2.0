package org.example.CampusMart.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.CampusMart.model.entity.GoodsCollection;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GoodsCollectionMapper extends BaseMapper<GoodsCollection> {

    List<Long> selectCollectedGoodIdsByUserId(@Param("userId") Long userId);
}
