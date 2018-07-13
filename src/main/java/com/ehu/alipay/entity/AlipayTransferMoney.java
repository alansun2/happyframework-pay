package com.ehu.alipay.entity;

/**
 * @author AlanSun
 * @Date 2016年8月1日
 * 转账类
 */
public class AlipayTransferMoney {
	private String detailData;
	/**总金额*/
	private String batchFee;
	/**
	 * 交易总笔数
	 */
	private int batchNum;

	/**
	 * @return the batchFee
	 */
	public String getBatchFee() {
		return batchFee;
	}
	/**
	 * @param batchFee the batchFee to set
	 */
	public void setBatchFee(String batchFee) {
		this.batchFee = batchFee;
	}
	/**
	 * @return the batchNum
	 */
	public int getBatchNum() {
		return batchNum;
	}
	/**
	 * @param batchNum the batchNum to set
	 */
	public void setBatchNum(int batchNum) {
		this.batchNum = batchNum;
	}
	/**
	 * @return the detailData
	 */
	public String getDetailData() {
		return detailData;
	}
	/**
	 * @param detailData the detailData to set
	 */
	public void setDetailData(String detailData) {
		this.detailData = detailData;
	}
	
}
