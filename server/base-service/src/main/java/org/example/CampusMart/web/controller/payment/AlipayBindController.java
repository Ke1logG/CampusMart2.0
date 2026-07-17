package org.example.CampusMart.web.controller.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.CampusMart.common.result.Result;
import org.example.CampusMart.model.entity.AlipayAccountBind;
import org.example.CampusMart.web.service.AlipayAccountBindService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "支付宝绑定")
@RestController
@RequestMapping("/app/alipay")
public class AlipayBindController {

    @Autowired
    private AlipayAccountBindService alipayAccountBindService;

    @Operation(summary = "获取当前用户支付宝绑定")
    @GetMapping("/bind")
    public Result<AlipayAccountBind> getBind(@RequestAttribute("userId") Long userId) {
        return Result.ok(alipayAccountBindService.getByUserId(userId));
    }

    @Operation(summary = "绑定支付宝账号")
    @PostMapping("/bind")
    public Result<Boolean> bind(@RequestAttribute("userId") Long userId,
                                @RequestParam String account,
                                @RequestParam String realName) {
        return Result.ok(alipayAccountBindService.bind(userId, account, realName));
    }
}
