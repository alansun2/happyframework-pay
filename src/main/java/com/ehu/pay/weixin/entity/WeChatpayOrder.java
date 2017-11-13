package com.ehu.pay.weixin.entity;

/**
 * @author AlanSun
 * 20160422
 */
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

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        if (body.length() > 49) {
            body = body.substring(0, 49);
        }
        this.body = body;
    }
}
