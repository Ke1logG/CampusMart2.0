package org.example.CampusMart.web.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.CampusMart.common.result.Result;
import org.example.CampusMart.web.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@Tag(name = "短信服务")
@RestController
@RequestMapping("/app/sms")
public class SmsController {

    @Autowired
    private SmsService smsService;

    @Operation(summary = "发送验证码")
    @GetMapping("/sendCode")
    public Result<String> sendCode(@RequestParam String phone) {
        String code = String.format("%06d", new Random().nextInt(1000000));
        boolean success = smsService.sendVerifyCode(phone, code);
        if (!success) {
            return Result.fail();
        }
        return Result.ok(code);
    }
}
