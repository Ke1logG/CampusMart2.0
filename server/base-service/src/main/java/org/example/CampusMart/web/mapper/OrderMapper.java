package org.example.CampusMart.web.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.example.CampusMart.model.entity.Order;
import org.example.CampusMart.web.vo.OrderVo;

import java.util.List;

public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 查询买家订单列表，并关联商品标题和最新上传的首图
     */
    List<OrderVo> selectBuyerOrderListWithGoods(@Param("buyerId") Long buyerId);

    /**
     * 查询卖家订单列表，并关联商品标题和最新上传的首图
     */
    List<OrderVo> selectSellerOrderListWithGoods(@Param("sellerId") Long sellerId);
}
