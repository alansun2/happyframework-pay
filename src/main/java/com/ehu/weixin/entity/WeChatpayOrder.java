package com.ehu.weixin.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @author AlanSun
 * 20160422
 */
@Getter
@Setter
public class WeChatpayOrder {
    /**
     * 订单id
     */
    private String orderId;
    /**
     * 订单价格
     */
    private double price;
    /**
     * 订单详情
     */
    private String body;
    /**
     * openid 微信小程序必传
     */
    private String openid;
    /**
     * 回调地址
     */
    private String notifyUrl;
    /**
     * [APP,NATIVE,JSAPI]
     * NATIVE:扫码
     * JSAPI:小程序，网页
     */
    private String tradeType;

    public void setBody(String body) {
        if (body.length() > 49) {
            body = body.substring(0, 49);
        }
        this.body = body;
    }
}
