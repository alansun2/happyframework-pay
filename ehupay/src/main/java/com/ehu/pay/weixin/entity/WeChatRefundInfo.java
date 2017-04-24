package com.ehu.pay.weixin.entity;

/**
 * @author AlanSun
 * @Date 2016年8月10日
 * 微信退款类
 */
public class WeChatRefundInfo {
	/**
	 * 微信订单号
	 */
	private String transactionId;
	/**
	 * 商家退款号
	 */
	private String outRefundNo;
	/**
	 * 总价格
	 */
	private double totalFee;
	/**
	 * 密码
	 */
	private String password;
	/**
	 * @return the transactionId
	 */
	public String getTransactionId() {
		return transactionId;
	}
	/**
	 * @param transactionId the transactionId to set
	 */
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	/**
	 * @return the outRefundNo
	 */
	public String getOutRefundNo() {
		return outRefundNo;
	}
	/**
	 * @param outRefundNo the outRefundNo to set
	 */
	public void setOutRefundNo(String outRefundNo) {
		this.outRefundNo = outRefundNo;
	}
	/**
	 * @return the totalFee
	 */
	public double getTotalFee() {
		return totalFee;
	}
	/**
	 * @param totalFee the totalFee to set
	 */
	public void setTotalFee(double totalFee) {
		this.totalFee = totalFee;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
}
