package org.example.CampusMart.web.controller.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.CampusMart.common.result.Result;
import org.example.CampusMart.model.entity.Payment;
import org.example.CampusMart.web.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Tag(name = "支付单")
@RestController
@RequestMapping("/app/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Operation(summary = "根据订单ID查询支付单")
    @GetMapping("/order/{orderId}")
    public Result<Payment> getByOrderId(@PathVariable Long orderId) {
        return Result.ok(paymentService.getByOrderId(orderId));
    }

    @Operation(summary = "创建支付单")
    @PostMapping("/create")
    public Result<Payment> createPayment(@RequestAttribute("userId") Long userId,
                                         @RequestParam Long orderId) {
        return Result.ok(paymentService.createPayment(orderId, userId));
    }

    @Operation(summary = "获取支付宝支付串")
    @GetMapping("/{paymentId}/alipay/orderString")
    public Result<String> getAlipayOrderString(@PathVariable Long paymentId,
                                               @RequestAttribute("userId") Long userId) throws Exception {
        return Result.ok(paymentService.getAlipayOrderString(paymentId, userId));
    }

    @Operation(summary = "支付宝异步通知")
    @PostMapping("/alipay/notify")
    public String alipayNotify(HttpServletRequest request) throws Exception {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : requestParams.entrySet()) {
            String name = entry.getKey();
            String[] values = entry.getValue();
            StringBuilder valueStr = new StringBuilder();
            for (int i = 0; i < values.length; i++) {
                valueStr.append(i == values.length - 1 ? values[i] : values[i] + ",");
            }
            params.put(name, valueStr.toString());
        }
        log.info("支付宝异步通知参数: {}", params);
        boolean success = paymentService.handleAlipayNotify(params);
        return success ? "success" : "fail";
    }
}
