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

@Schema(description = "支付单")
@Data
@TableName(value = "payments")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Schema(description = "支付单ID")
    @TableId(value = "paymentID", type = IdType.AUTO)
    private Long paymentID;

    @Schema(description = "订单ID")
    @TableField("orderID")
    private Long orderID;

    @Schema(description = "支付用户ID")
    @TableField("userID")
    private Long userID;

    @Schema(description = "支付金额")
    @TableField("amount")
    private BigDecimal amount;

    @Schema(description = "状态：PENDING/PAID/CLOSED")
    @TableField("status")
    private String status;

    @Schema(description = "支付宝交易号")
    @TableField("alipayTradeNo")
    private String alipayTradeNo;

    @Schema(description = "支付时间")
    @TableField("payTime")
    private Date payTime;

    @Schema(description = "过期时间")
    @TableField("expireTime")
    private Date expireTime;

    @Schema(description = "创建时间")
    @TableField("createTime")
    private Date createTime;

    @Schema(description = "更新时间")
    @TableField("updateTime")
    private Date updateTime;
}
