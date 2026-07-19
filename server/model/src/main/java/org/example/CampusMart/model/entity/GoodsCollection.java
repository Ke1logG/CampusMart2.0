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

@Schema(description = "商品收藏")
@Data
@TableName(value = "goods_collection")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoodsCollection {

    @Schema(description = "collectionID")
    @TableId(value = "collectionID", type = IdType.AUTO)
    private Long collectionID;

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
