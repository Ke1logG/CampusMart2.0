package org.example.CampusMart.web.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI().info(
                new Info()
                        .title("CampusMartAPI")
                        .version("1.0")
                        .description("CampusMartAPI"));
    }


    @Bean
    public GroupedOpenApi loginAPI() {

        return GroupedOpenApi.builder().group("登录管理").
                pathsToMatch(
                        "/app/login",
                        "/app/register",
                        "/app/info"
                ).
                build();
    }

    @Bean
    public GroupedOpenApi goodsAPI() {

        return GroupedOpenApi.builder().group("商品信息管理").
                pathsToMatch(
                        "/app/goods/**"
                ).build();
    }
    @Bean
    public GroupedOpenApi notificationAPI() {
        return GroupedOpenApi.builder().group("通知信息管理").
                pathsToMatch(
                        "/app/notifications/**"
                ).build();
    }
    @Bean
    public GroupedOpenApi userAPI() {
        return GroupedOpenApi.builder().group("平台用户管理").
                pathsToMatch(
                        "/app/user/**"
                ).build();
    }

    @Bean
    public GroupedOpenApi messageAPI() {
        return GroupedOpenApi.builder().group("消息管理").
                pathsToMatch(
                        "/app/messages/**"
                ).build();
    }

    @Bean
    public GroupedOpenApi orderAPI() {
        return GroupedOpenApi.builder().group("订单交易").
                pathsToMatch(
                        "/app/orders/**"
                ).build();
    }

    @Bean
    public GroupedOpenApi paymentAPI() {
        return GroupedOpenApi.builder().group("支付单").
                pathsToMatch(
                        "/app/payments/**"
                ).build();
    }

    @Bean
    public GroupedOpenApi walletAPI() {
        return GroupedOpenApi.builder().group("钱包").
                pathsToMatch(
                        "/app/wallet/**"
                ).build();
    }

    @Bean
    public GroupedOpenApi alipayAPI() {
        return GroupedOpenApi.builder().group("支付宝绑定").
                pathsToMatch(
                        "/app/alipay/**"
                ).build();
    }

    @Bean
    public GroupedOpenApi likeAPI() {
        return GroupedOpenApi.builder().group("商品点赞").
                pathsToMatch(
                        "/app/like/**"
                ).build();
    }

    @Bean
    public GroupedOpenApi collectionAPI() {
        return GroupedOpenApi.builder().group("商品收藏").
                pathsToMatch(
                        "/app/collection/**"
                ).build();
    }
}
