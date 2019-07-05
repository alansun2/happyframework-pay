package com.ehu.bean;

import com.alipay.api.domain.AlipayTradeRefundModel;
import com.ehu.weixin.entity.WeChatRefundInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRefund extends PayBase {
    /**
     * 商户订单号
     */
    private String orderId;
    /**
     * 本次退款请求流水号，部分退款时必传
     */
    private String refundId;
    /**
     * 本次退款金额
     */
    private String refundAmount;
    /**
     * 订单总价格
     */
    private String totalAmount;
    /**
     * 退款原因
     */
    private String refundReason;
    /**
     * 订单退款币种信息
     */
    private String refundCurrency;

    private WeChatRefundInfo weChatRefundInfo = new WeChatRefundInfo();
    private AlipayTradeRefundModel alipayTradeRefundModel;
}