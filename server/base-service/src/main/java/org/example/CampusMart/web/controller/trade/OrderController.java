package org.example.CampusMart.web.controller.trade;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.CampusMart.common.result.Result;
import org.example.CampusMart.web.service.OrderService;
import org.example.CampusMart.web.vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "订单交易")
@RestController
@RequestMapping("/app/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Operation(summary = "创建订单")
    @PostMapping("/create")
    public Result<OrderVo> createOrder(@RequestAttribute("userId") Long userId,
                                       @RequestParam Long goodId) {
        return Result.ok(orderService.createOrder(userId, goodId));
    }

    @Operation(summary = "取消订单")
    @PostMapping("/{orderId}/cancel")
    public Result<Boolean> cancelOrder(@RequestAttribute("userId") Long userId,
                                       @PathVariable Long orderId) {
        return Result.ok(orderService.cancelOrder(userId, orderId));
    }

    @Operation(summary = "确认收货")
    @PostMapping("/{orderId}/confirm")
    public Result<Boolean> confirmReceipt(@RequestAttribute("userId") Long userId,
                                          @PathVariable Long orderId) {
        return Result.ok(orderService.confirmReceipt(userId, orderId));
    }

    @Operation(summary = "订单详情")
    @GetMapping("/{orderId}")
    public Result<OrderVo> getOrderDetail(@RequestAttribute("userId") Long userId,
                                          @PathVariable Long orderId) {
        return Result.ok(orderService.getOrderDetail(userId, orderId));
    }

    @Operation(summary = "买家订单列表")
    @GetMapping("/buyer/list")
    public Result<List<OrderVo>> listBuyerOrders(@RequestAttribute("userId") Long userId) {
        return Result.ok(orderService.listBuyerOrders(userId));
    }

    @Operation(summary = "卖家订单列表")
    @GetMapping("/seller/list")
    public Result<List<OrderVo>> listSellerOrders(@RequestAttribute("userId") Long userId) {
        return Result.ok(orderService.listSellerOrders(userId));
    }
}
