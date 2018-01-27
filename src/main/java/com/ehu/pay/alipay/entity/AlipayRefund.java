package com.ehu.pay.alipay.entity;


import lombok.Getter;
import lombok.Setter;

/**
 * @author AlanSun
 * @Date 2017年3月30日 上午11:25:38
 */
@Setter
@Getter
public class AlipayRefund {
	/**
	 * 商户订单号
	 */
	private String out_trade_no;
	/**
	 * 本次退款请求流水号，部分退款时必传
	 */
	private String out_request_no;
	/**
	 * 本次退款金额
	 */
	private double refund_amount;
	/**
	 * 退款原因
	 */
	private String refund_reason;
	/**
	 * 店铺id
	 */
	private String store_id;
}
