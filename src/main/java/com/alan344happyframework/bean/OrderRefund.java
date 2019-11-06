package com.alan344happyframework.bean;

import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alan344happyframework.weixin.entity.WeChatRefundInfo;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class OrderRefund extends PayBase {
    /**
     * 商户订单号，支付时的订单号
     */
    @NotBlank
    private String orderId;
    /**
     * 本次退款请求流水号，部分退款时必传
     */
    @NotBlank
    private String refundId;
    /**
     * 本次退款金额
     */
    @NotBlank
    @Digits(integer = 30, fraction = 2)
    private String refundAmount;
    /**
     * 订单总价格
     */
    @NotBlank
    @Digits(integer = 30, fraction = 2)
    private String totalAmount;
    /**
     * 退款原因
     */
    private String refundReason;
    /**
     * 订单退款币种信息
     * 默认人民币
     */
    private String refundCurrency;

    private WeChatRefundInfo weChatRefundInfo = new WeChatRefundInfo();
    private AlipayTradeRefundModel alipayTradeRefundModel;
}