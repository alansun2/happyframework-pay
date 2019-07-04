package com.ehu.alipay.entity;


import lombok.Getter;
import lombok.Setter;

/**
 * @author AlanSun
 * @Date 2017年3月30日 上午11:25:38
 */
@Getter
@Setter
public class AlipayRefund {
    /**
     * 商户订单号
     */
    private String outTradeNo;
    /**
     * 本次退款请求流水号，部分退款时必传
     */
    private String outRequestNo;
    /**
     * 本次退款金额
     */
    private String refundAmount;
}
