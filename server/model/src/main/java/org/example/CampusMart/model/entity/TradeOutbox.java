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

import java.util.Date;

@Schema(description = "交易事件发件箱")
@Data
@TableName(value = "trade_outbox")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeOutbox {

    @Schema(description = "事件ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "订单ID")
    @TableField("orderID")
    private Long orderID;

    @Schema(description = "事件类型")
    @TableField("eventType")
    private String eventType;

    @Schema(description = "事件内容")
    @TableField("payload")
    private String payload;

    @Schema(description = "状态：PENDING/DONE")
    @TableField("status")
    private String status;

    @Schema(description = "创建时间")
    @TableField("createTime")
    private Date createTime;
}
