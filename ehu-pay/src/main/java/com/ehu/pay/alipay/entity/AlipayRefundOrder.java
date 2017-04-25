package com.ehu.pay.alipay.entity;

/**
 * 
 * AlipayRefundOrder
 * 
 * Alan 2016年8月1日  
 * 
 */
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
	 * @return 退款批次号
	 */
	public String getBatchNo() {
		return batchNo;
	}

	/**
	 * @param batchNo 退款批次号
	 */
	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	/**
	 * @return 退款总笔数
	 */
	public int getBatchNum() {
		return batchNum;
	}

	/**
	 * @param batchNum 退款总笔数
	 */
	public void setBatchNum(int batchNum) {
		this.batchNum = batchNum;
	}

	/**
	 * @return 退款时间
	 */
	public String getRefundDate() {
		return refundDate;
	}

	/**
	 * @param refundDate 退款时间(yyyy-MM-dd HH:mm:ss)
	 */
	public void setRefundDate(String refundDate) {
		this.refundDate = refundDate;
	}

	/**
	 * @return 单笔数据集
	 */
	public String getDetailData() {
		return detailData;
	}

	/**
	 * @param detailData 单笔数据集(详见接口文档)
	 */
	public void setDetailData(String detailData) {
		this.detailData = detailData;
	}

}
