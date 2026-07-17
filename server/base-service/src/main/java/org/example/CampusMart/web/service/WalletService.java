package org.example.CampusMart.web.service;

import org.example.CampusMart.model.entity.UserWallet;
import org.example.CampusMart.model.entity.WalletFlow;

import java.math.BigDecimal;
import java.util.List;

public interface WalletService {

    UserWallet getWallet(Long userId);

    void handleOrderSettled(Long sellerId, Long orderId, BigDecimal amount);

    List<WalletFlow> listFlows(Long userId);

    boolean withdraw(Long userId, BigDecimal amount, String alipayAccount);
}
