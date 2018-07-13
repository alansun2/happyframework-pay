package com.ehu.alipay.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlipayOrder {

    private String orderId;
    private String price;
    private String subject;
    /**
     * 商品的标题/交易标题/订单标题/订单关键字等。例如：大乐透
     */
    private String body;
    /**
     * 该笔订单允许的最晚付款时间，逾期将关闭交易
     * 取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）
     */
    private String timeoutExpress;

    /**
     * 回调地址
     */
    private String notifyUrl;
}
