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

@Schema(description = "商品点赞")
@Data
@TableName(value = "goods_like")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsLike {

    @Schema(description = "likeID")
    @TableId(value = "likeID", type = IdType.AUTO)
    private Long likeID;

    @Schema(description = "用户ID")
    @TableField("userID")
    private Long userID;

    @Schema(description = "商品ID")
    @TableField("goodID")
    private Long goodID;

    @Schema(description = "创建时间")
    @TableField("createTime")
    private Date createTime;
}
