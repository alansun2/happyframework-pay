package com.ehu.pay.weixin.entity;

/**
 * @author AlanSun
 * @Date 2016年8月3日
 * 微信企业付款实体类
 */
public class WechatBusinessPay {
	/**
	 * 商户端订单号（唯一）
	 */
	private String orderId;
	/**
	 * 微信实名认证姓名
	 */
	private String reUserName;
	/**
	 * 订单总价格
	 */
	private double totalPrice;
	/**
	 * 付款描述
	 */
	private String desc;
	/**
	 * 用户唯一标识
	 */
	private String openId;
	/**
	 * @return the orderId
	 */
	public String getOrderId() {
		return orderId;
	}
	/**
	 * @param orderId the orderId to set
	 */
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	/**
	 * @return the reUserName
	 */
	public String getReUserName() {
		return reUserName;
	}
	/**
	 * @param reUserName the reUserName to set
	 */
	public void setReUserName(String reUserName) {
		this.reUserName = reUserName;
	}
	/**
	 * @return the totalPrice
	 */
	public double getTotalPrice() {
		return totalPrice;
	}
	/**
	 * @param totalPrice the totalPrice to set
	 */
	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}
	/**
	 * @return the desc
	 */
	public String getDesc() {
		return desc;
	}
	/**
	 * @param desc the desc to set
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}
	/**
	 * @return the openId
	 */
	public String getOpenId() {
		return openId;
	}
	/**
	 * @param openId the openId to set
	 */
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "orderId:"+orderId+"desc:"+desc+"openId:"+openId+"总价格:"+totalPrice;
	}
}
