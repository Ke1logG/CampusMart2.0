package org.example.CampusMart.web.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "alipay")
public class AlipayProperties {
    private String appId;
    private String privateKey;
    private String publicKey;
    private String serverUrl;
    private String notifyUrl;
    private String returnUrl;
}
