package com.ehu.pay.alipay.entity;

public class AlipayOrder {

	private String orderId;
	private String price;
	private String subject;
	private String body;

	public String getOrderId() {
		return orderId;
	}

	/**
	 * 订单号
	 * @param orderId 订单号
	 */
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getPrice() {
		return price;
	}

	/**
	 * 订单价格
	 * @param price 订单价格
	 */
	public void setPrice(String price) {
		this.price = price;
	}

	public String getSubject() {
		return subject;
	}

	/**
	 * 商品名称
	 * @param subject 商品名称
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	/**
	 * 商品详情
	 * @param body 商品详情
	 */
	public void setBody(String body) {
		this.body = body;
	}
}
