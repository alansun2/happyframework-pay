package com.alan344happyframework.alipay.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * AlipayRefundOrder
 * 
 * Alan 2016年8月1日  
 * 
 */
@Getter
@Setter
public class AlipayRefundOrder {

	// 退款批次号
	private String batchNo;
	// 退款总笔数
	private int batchNum;
	// 退款时间
	private String refundDate;
	// 单笔数据集
	private String detailData;

	/**
	 * 回调地址
	 */
	private String notifyUrl;
}
