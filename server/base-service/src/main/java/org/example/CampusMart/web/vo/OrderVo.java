package org.example.CampusMart.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Schema(description = "订单信息")
public class OrderVo {

    @Schema(description = "订单ID")
    private Long orderID;

    @Schema(description = "商品ID")
    private Long goodID;

    @Schema(description = "商品标题")
    private String goodTitle;

    @Schema(description = "商品图片")
    private String goodPictureURL;

    @Schema(description = "买家ID")
    private Long buyerID;

    @Schema(description = "卖家ID")
    private Long sellerID;

    @Schema(description = "订单金额")
    private BigDecimal amount;

    @Schema(description = "状态：CREATED/PAID/SETTLED/CANCELLED")
    private String status;

    @Schema(description = "支付超时时间")
    private Date payTimeout;

    @Schema(description = "创建时间")
    private Date createTime;
}
