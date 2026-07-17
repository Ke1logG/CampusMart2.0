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

@Schema(description = "钱包流水")
@Data
@TableName(value = "wallet_flows")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletFlow {

    @Schema(description = "流水ID")
    @TableId(value = "flowID", type = IdType.AUTO)
    private Long flowID;

    @Schema(description = "用户ID")
    @TableField("userID")
    private Long userID;

    @Schema(description = "流水类型：RECHARGE/INCOME/EXPENSE/WITHDRAW")
    @TableField("flowType")
    private String flowType;

    @Schema(description = "金额")
    @TableField("amount")
    private BigDecimal amount;

    @Schema(description = "变动后余额")
    @TableField("balance")
    private BigDecimal balance;

    @Schema(description = "关联ID")
    @TableField("relatedID")
    private Long relatedID;

    @Schema(description = "备注")
    @TableField("remark")
    private String remark;

    @Schema(description = "创建时间")
    @TableField("createTime")
    private Date createTime;
}
