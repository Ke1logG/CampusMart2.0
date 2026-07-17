package org.example.CampusMart.web.service.impl;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.Common;
import lombok.extern.slf4j.Slf4j;
import org.example.CampusMart.web.config.SmsProperties;
import org.example.CampusMart.web.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SmsServiceImpl implements SmsService {

    @Autowired
    private SmsProperties smsProperties;

    @Override
    public boolean sendVerifyCode(String phone, String code) {
        if (Common.isUnset(smsProperties.getAccessKeyId()) || Common.isUnset(smsProperties.getAccessKeySecret())) {
            log.warn("阿里云短信 AK 未配置，仅打印验证码：phone={}, code={}", phone, code);
            return true;
        }
        try {
            Config config = new Config()
                    .setAccessKeyId(smsProperties.getAccessKeyId())
                    .setAccessKeySecret(smsProperties.getAccessKeySecret());
            config.endpoint = "dysmsapi.aliyuncs.com";
            Client client = new Client(config);
            SendSmsRequest request = new SendSmsRequest()
                    .setPhoneNumbers(phone)
                    .setSignName(smsProperties.getSignName())
                    .setTemplateCode(smsProperties.getTemplateCode())
                    .setTemplateParam("{\"code\":\"" + code + "\"}");
            SendSmsResponse response = client.sendSms(request);
            return "OK".equals(response.getBody().getCode());
        } catch (Exception e) {
            log.error("发送短信失败", e);
            return false;
        }
    }
}
