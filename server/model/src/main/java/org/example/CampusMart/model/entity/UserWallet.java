package org.example.CampusMart.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Schema(description = "用户钱包")
@Data
@TableName(value = "user_wallets")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserWallet {

    @Schema(description = "钱包ID")
    @TableId(value = "walletID", type = IdType.AUTO)
    private Long walletID;

    @Schema(description = "用户ID")
    @TableField("userID")
    private Long userID;

    @Schema(description = "余额")
    @TableField("balance")
    private BigDecimal balance;

    @Schema(description = "乐观锁版本号")
    @Version
    @TableField("version")
    private Long version;

    @Schema(description = "创建时间")
    @TableField("createTime")
    private Date createTime;

    @Schema(description = "更新时间")
    @TableField("updateTime")
    private Date updateTime;
}
