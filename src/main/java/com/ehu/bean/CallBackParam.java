package com.ehu.bean;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author alan
 * @createtime 18-1-27 * 回调处理参数类
 */
@Getter
@Setter
public class CallBackParam {

    /**
     * 订单id
     */
    private String orderId;

    /**
     * 支付账号
     */
    private String payAccount;

    /**
     * 回调时的价格
     */
    private String callBackPrice;

    /**
     * 第三方订单号
     */
    private String thirdOrderId;

    /**
     * aliPay or wechatPay
     */
    private String payType;
}
