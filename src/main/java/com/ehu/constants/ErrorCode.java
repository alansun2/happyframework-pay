package com.ehu.constants;

import lombok.Getter;

/**
 * @author AlanSun
 * @date 2019/7/22 15:28
 **/
@Getter
public enum ErrorCode {

    ORDER_NOT_EXIST("ORDER_NOT_EXIST", "预支付订单不存在，请重新下单"),
    PRE_ORDER_FAIL("PRE_ORDER_FAIL", "预支付失败，请稍后重试"),
    WECHAT_SERVER_ERROR("WECHAT_SERVER_ERROR", "微信服务器异常"),
    ALI_SERVER_ERROR("ALI_SERVER_ERROR", "支付宝服务器异常"),
    GET_QR_CODE_ERROR("GET_QR_CODE_ERROR", "获取支付二维码失败，请稍后再试"),
    VERIFY_ERROR("VERIFY_ERROR", "数字验签失败");
    private String code;
    private String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
