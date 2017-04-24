package com.ehu.pay.alipay.entity;


/**
 * @author AlanSun
 * @Date 2017年3月30日 上午11:25:38
 */
public class AlipayRefund {
	/**
	 * 商户订单号
	 */
	private long outTradeNo;
	/**
	 * 本次退款请求流水号，部分退款时必传
	 */
	private String outRequestNo;
	/**
	 * 本次退款金额
	 */
	private double refundAmount;
	
	public long getOutTradeNo() {
		return outTradeNo;
	}
	public void setOutTradeNo(long outTradeNo) {
		this.outTradeNo = outTradeNo;
	}
	public String getOutRequestNo() {
		return outRequestNo;
	}
	public void setOutRequestNo(String outRequestNo) {
		this.outRequestNo = outRequestNo;
	}
	public double getRefundAmount() {
		return refundAmount;
	}
	public void setRefundAmount(double refundAmount) {
		this.refundAmount = refundAmount;
	}
}
