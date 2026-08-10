package org.example.CampusMart.common.result;

import lombok.Getter;

@Getter
public enum ResultCodeEnum {

    SUCCESS(200, "成功"),
    FAIL(201, "失败"),
    PARAM_ERROR(202, "参数不正确"),
    SERVICE_ERROR(203, "服务异常"),
    DATA_ERROR(204, "数据异常"),
    ILLEGAL_REQUEST(205, "非法请求"),
    REPEAT_SUBMIT(206, "重复提交"),
    DELETE_ERROR(207, "请先删除子集"),

    // 管理员模块 3xx
    ADMIN_ACCOUNT_EXIST_ERROR(301, "账号已存在"),
    ADMIN_CAPTCHA_CODE_ERROR(302, "验证码错误"),
    ADMIN_CAPTCHA_CODE_EXPIRED(303, "验证码已过期"),
    ADMIN_CAPTCHA_CODE_NOT_FOUND(304, "未输入验证码"),
    ADMIN_LOGIN_AUTH(305, "未登陆"),
    ADMIN_ACCOUNT_NOT_EXIST_ERROR(306, "账号不存在"),
    ADMIN_ACCOUNT_ERROR(307, "用户名或密码错误"),
    ADMIN_ACCOUNT_DISABLED_ERROR(308, "该用户已被禁用"),
    ADMIN_ACCESS_FORBIDDEN(309, "无访问权限"),

    // 商品模块 4xx
    GOODS_NOT_FOUND(401, "商品不存在"),
    GOODS_SELF_PURCHASE(402, "不能购买自己发布的商品"),
    GOODS_PRICE_ILLEGAL(403,"商品价格小于或等于零"),
    GOODS_TITLE_EMPTY(404,"商品标题为空"),

    // APP 登录模块 5xx
    APP_LOGIN_AUTH(501, "未登陆"),
    APP_LOGIN_PHONE_EMPTY(502, "手机号码为空"),
    APP_LOGIN_CODE_EMPTY(503, "验证码为空"),
    APP_SEND_SMS_TOO_OFTEN(504, "验证法发送过于频繁"),
    APP_LOGIN_CODE_EXPIRED(505, "验证码已过期"),
    APP_LOGIN_CODE_ERROR(506, "验证码错误"),
    APP_ACCOUNT_DISABLED_ERROR(507, "该用户已被禁用"),

    // Token 模块 6xx
    TOKEN_EXPIRED(601, "token过期"),
    TOKEN_INVALID(602, "token非法"),

    // 订单模块 7xx
    ORDER_NOT_FOUND(701, "订单不存在"),
    ORDER_STATUS_ERROR(702, "订单状态不正确"),
    ORDER_CANCEL_NOT_ALLOWED(703, "只能取消未支付的订单"),
    ORDER_CONFIRM_NOT_ALLOWED(704, "只能确认已支付的订单"),
    ORDER_NO_PERMISSION(705, "无权操作该订单"),

    // 支付模块 8xx
    PAYMENT_NOT_FOUND(801, "支付单不存在"),
    PAYMENT_STATUS_ERROR(802, "支付单状态不正确"),
    PAYMENT_NO_PERMISSION(803, "无权操作该支付单"),

    // 钱包模块 9xx
    WALLET_NOT_FOUND(901, "钱包不存在"),
    WALLET_BALANCE_INSUFFICIENT(902, "余额不足"),
    WALLET_UPDATE_FAILED(903, "钱包更新失败"),
    WITHDRAW_AMOUNT_INVALID(904, "提现金额必须大于0");


    private final Integer code;

    private final String message;

    ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
