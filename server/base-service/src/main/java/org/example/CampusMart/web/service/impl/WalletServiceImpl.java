package org.example.CampusMart.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.CampusMart.common.exception.CampusMartException;
import org.example.CampusMart.common.result.ResultCodeEnum;
import org.example.CampusMart.model.entity.UserWallet;
import org.example.CampusMart.model.entity.WalletFlow;
import org.example.CampusMart.web.mapper.UserWalletMapper;
import org.example.CampusMart.web.mapper.WalletFlowMapper;
import org.example.CampusMart.web.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class WalletServiceImpl extends ServiceImpl<UserWalletMapper, UserWallet> implements WalletService {

    @Autowired
    private UserWalletMapper userWalletMapper;

    @Autowired
    private WalletFlowMapper walletFlowMapper;

    private static final String FLOW_INCOME = "INCOME";
    private static final String FLOW_EXPENSE = "EXPENSE";
    private static final String FLOW_WITHDRAW = "WITHDRAW";

    @Override
    public UserWallet getWallet(Long userId) {
        LambdaQueryWrapper<UserWallet> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserWallet::getUserID, userId);
        UserWallet wallet = userWalletMapper.selectOne(wrapper);
        if (wallet == null) {
            wallet = new UserWallet();
            wallet.setUserID(userId);
            wallet.setBalance(BigDecimal.ZERO);
            wallet.setVersion(0L);
            wallet.setCreateTime(new Date());
            wallet.setUpdateTime(new Date());
            userWalletMapper.insert(wallet);
        }
        return wallet;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleOrderSettled(Long sellerId, Long orderId, BigDecimal amount) {
        UserWallet wallet = getWallet(sellerId);
        wallet.setBalance(wallet.getBalance().add(amount));
        int success = userWalletMapper.updateById(wallet);
        if (success == 0) {
            throw new CampusMartException(ResultCodeEnum.WALLET_UPDATE_FAILED);
        }

        UserWallet updated = getWallet(sellerId);
        WalletFlow flow = new WalletFlow();
        flow.setUserID(sellerId);
        flow.setFlowType(FLOW_INCOME);
        flow.setAmount(amount);
        flow.setBalance(updated.getBalance());
        flow.setRelatedID(orderId);
        flow.setRemark("订单结算收入");
        flow.setCreateTime(new Date());
        walletFlowMapper.insert(flow);
    }

    @Override
    public List<WalletFlow> listFlows(Long userId) {
        LambdaQueryWrapper<WalletFlow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(WalletFlow::getUserID, userId).orderByDesc(WalletFlow::getCreateTime);
        return walletFlowMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean withdraw(Long userId, BigDecimal amount, String alipayAccount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CampusMartException(ResultCodeEnum.WITHDRAW_AMOUNT_INVALID);
        }
        UserWallet wallet = getWallet(userId);
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new CampusMartException(ResultCodeEnum.WALLET_BALANCE_INSUFFICIENT);
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        int success = userWalletMapper.updateById(wallet);
        if (success == 0) {
            throw new CampusMartException(ResultCodeEnum.WALLET_UPDATE_FAILED);
        }

        UserWallet updated = getWallet(userId);
        WalletFlow flow = new WalletFlow();
        flow.setUserID(userId);
        flow.setFlowType(FLOW_WITHDRAW);
        flow.setAmount(amount.negate());
        flow.setBalance(updated.getBalance());
        flow.setRemark("提现至支付宝：" + alipayAccount);
        flow.setCreateTime(new Date());
        walletFlowMapper.insert(flow);
        return true;
    }
}
