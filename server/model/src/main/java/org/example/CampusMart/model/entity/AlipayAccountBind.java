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

@Schema(description = "支付宝账号绑定")
@Data
@TableName(value = "alipay_account_binds")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlipayAccountBind {

    @Schema(description = "绑定ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "用户ID")
    @TableField("userID")
    private Long userID;

    @Schema(description = "支付宝账号")
    @TableField("account")
    private String account;

    @Schema(description = "真实姓名")
    @TableField("realName")
    private String realName;

    @Schema(description = "创建时间")
    @TableField("createTime")
    private Date createTime;

    @Schema(description = "更新时间")
    @TableField("updateTime")
    private Date updateTime;
}
