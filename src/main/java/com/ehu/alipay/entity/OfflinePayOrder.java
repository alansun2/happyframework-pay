/**
 * @author AlanSun
 * @Date 2016年11月7日
 */
package com.ehu.alipay.entity;

/**
 * @author Alan
 *
 */
public class OfflinePayOrder {
	/**
	 * 商户订单号,64个字符以内、只能包含字母、数字、下划线；需保证在商户端不重复
	 */
	private String outTradeNo;
	/**
	 * 需要退款的金额，该金额不能大于订单金额,单位为元，支持两位小数
	 */
	private double refundAmount;
	/**
	 * 退款理由
	 */
	private String refundReason;
	/**
	 * 标识一次退款请求，同一笔交易多次退款需要保证唯一，如需部分退款，则此参数必传。
	 */
	private int outRequestNo;
	public String getOutTradeNo() {
		return outTradeNo;
	}
	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}
	public double getRefundAmount() {
		return refundAmount;
	}
	public void setRefundAmount(double refundAmount) {
		this.refundAmount = refundAmount;
	}
	public String getRefundReason() {
		return refundReason;
	}
	public void setRefundReason(String refundReason) {
		this.refundReason = refundReason;
	}
	public int getOutRequestNo() {
		return outRequestNo;
	}
	public void setOutRequestNo(int outRequestNo) {
		this.outRequestNo = outRequestNo;
	}
	
}
