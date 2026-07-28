package org.example.CampusMart.web.job;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.example.CampusMart.model.entity.Payment;
import org.example.CampusMart.web.mapper.PaymentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 支付单过期关闭兜底任务。
 *
 * 支付单创建时设置了过期时间，但可能存在订单未被正常取消、支付单未被同步关闭的边界情况。
 * 本任务每 5 分钟扫描一次，关闭已经过期的 PENDING 支付单。
 */
@Slf4j
@Component
public class PaymentTimeoutJob {

    @Autowired
    private PaymentMapper paymentMapper;

    // 扫描间隔：5 分钟
    private static final long SCAN_INTERVAL_MILLIS = 5 * 60 * 1000;

    @Scheduled(fixedDelay = SCAN_INTERVAL_MILLIS)
    public void closeExpiredPayments() {
        LambdaUpdateWrapper<Payment> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Payment::getStatus, "PENDING")
                .lt(Payment::getExpireTime, new Date())
                .set(Payment::getStatus, "CLOSED")
                .set(Payment::getUpdateTime, new Date());
        int rows = paymentMapper.update(null, wrapper);
        if (rows > 0) {
            log.info("兜底关闭过期支付单 {} 条", rows);
        }
    }
}
