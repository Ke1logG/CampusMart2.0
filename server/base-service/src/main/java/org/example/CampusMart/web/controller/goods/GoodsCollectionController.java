package org.example.CampusMart.web.controller.goods;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.CampusMart.common.result.Result;
import org.example.CampusMart.web.service.GoodsCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "商品收藏")
@RestController
@RequestMapping("/app/collection")
public class GoodsCollectionController {

    @Autowired
    private GoodsCollectionService goodsCollectionService;

    @Operation(summary = "收藏商品")
    @PostMapping("/{goodId}")
    public Result<Boolean> collect(@PathVariable Long goodId,
                                   @RequestAttribute("userId") Long userId) {
        return Result.ok(goodsCollectionService.collect(userId, goodId));
    }

    @Operation(summary = "取消收藏")
    @DeleteMapping("/{goodId}")
    public Result<Boolean> uncollect(@PathVariable Long goodId,
                                     @RequestAttribute("userId") Long userId) {
        return Result.ok(goodsCollectionService.uncollect(userId, goodId));
    }

    @Operation(summary = "是否已收藏")
    @GetMapping("/{goodId}")
    public Result<Boolean> isCollected(@PathVariable Long goodId,
                                       @RequestAttribute("userId") Long userId) {
        return Result.ok(goodsCollectionService.isCollected(userId, goodId));
    }

    @Operation(summary = "获取用户收藏的商品ID列表")
    @GetMapping("/list")
    public Result<List<Long>> listCollected(@RequestAttribute("userId") Long userId) {
        return Result.ok(goodsCollectionService.getCollectedGoodIds(userId));
    }
}
