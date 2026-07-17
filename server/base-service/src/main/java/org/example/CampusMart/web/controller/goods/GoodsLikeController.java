package org.example.CampusMart.web.controller.goods;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.CampusMart.common.result.Result;
import org.example.CampusMart.web.service.GoodsLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "商品点赞")
@RestController
@RequestMapping("/app/like")
public class GoodsLikeController {

    @Autowired
    private GoodsLikeService goodsLikeService;

    @Operation(summary = "点赞商品")
    @PostMapping("/{goodId}")
    public Result<Boolean> like(@PathVariable Long goodId,
                                @RequestAttribute("userId") Long userId) {
        return Result.ok(goodsLikeService.like(userId, goodId));
    }

    @Operation(summary = "取消点赞")
    @DeleteMapping("/{goodId}")
    public Result<Boolean> unlike(@PathVariable Long goodId,
                                  @RequestAttribute("userId") Long userId) {
        return Result.ok(goodsLikeService.unlike(userId, goodId));
    }

    @Operation(summary = "是否已点赞")
    @GetMapping("/{goodId}")
    public Result<Boolean> isLiked(@PathVariable Long goodId,
                                   @RequestAttribute("userId") Long userId) {
        return Result.ok(goodsLikeService.isLiked(userId, goodId));
    }

    @Operation(summary = "获取用户点赞的商品ID列表")
    @GetMapping("/list")
    public Result<List<Long>> listLiked(@RequestAttribute("userId") Long userId) {
        return Result.ok(goodsLikeService.getLikedGoodIds(userId));
    }
}
