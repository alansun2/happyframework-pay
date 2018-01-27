package com.ehu.pay.bean;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author alan
 * @createtime 18-1-26 * 退款
 */
@Builder
@Getter
@Setter
public class RefundInfo {
    /**
     * 订单号或第三方支付号
     */
    private String orderId;
    /**
     * 订单总金额
     */
    private double totalMoney;
    /**
     * 退款金额
     */
    private double refundMoney;
    /**
     * 退款批次 ，
     */
    private String refundNo;
    /**
     * 退款原因
     */
    private String reason;

}
