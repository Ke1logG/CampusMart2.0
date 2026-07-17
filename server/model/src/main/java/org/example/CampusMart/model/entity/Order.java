package org.example.CampusMart.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Schema(description = "订单")
@Data
@TableName(value = "orders")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Schema(description = "订单ID")
    @TableId(value = "orderID", type = IdType.AUTO)
    private Long orderID;

    @Schema(description = "商品ID")
    @TableField("goodID")
    private Long goodID;

    @Schema(description = "买家ID")
    @TableField("buyerID")
    private Long buyerID;

    @Schema(description = "卖家ID")
    @TableField("sellerID")
    private Long sellerID;

    @Schema(description = "订单金额")
    @TableField("amount")
    private BigDecimal amount;

    @Schema(description = "状态：CREATED/PAID/SETTLED/CANCELLED")
    @TableField("status")
    private String status;

    @Schema(description = "支付超时时间")
    @TableField("payTimeout")
    private Date payTimeout;

    @Schema(description = "创建时间")
    @TableField("createTime")
    private Date createTime;

    @Schema(description = "更新时间")
    @TableField("updateTime")
    private Date updateTime;
}
