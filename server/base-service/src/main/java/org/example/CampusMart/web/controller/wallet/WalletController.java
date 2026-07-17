package org.example.CampusMart.web.controller.wallet;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.CampusMart.common.result.Result;
import org.example.CampusMart.model.entity.UserWallet;
import org.example.CampusMart.model.entity.WalletFlow;
import org.example.CampusMart.web.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "钱包")
@RestController
@RequestMapping("/app/wallet")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @Operation(summary = "获取钱包信息")
    @GetMapping("")
    public Result<UserWallet> getWallet(@RequestAttribute("userId") Long userId) {
        return Result.ok(walletService.getWallet(userId));
    }

    @Operation(summary = "钱包流水")
    @GetMapping("/flows")
    public Result<List<WalletFlow>> listFlows(@RequestAttribute("userId") Long userId) {
        return Result.ok(walletService.listFlows(userId));
    }

    @Operation(summary = "提现")
    @PostMapping("/withdraw")
    public Result<Boolean> withdraw(@RequestAttribute("userId") Long userId,
                                    @RequestParam BigDecimal amount,
                                    @RequestParam String alipayAccount) {
        return Result.ok(walletService.withdraw(userId, amount, alipayAccount));
    }
}
