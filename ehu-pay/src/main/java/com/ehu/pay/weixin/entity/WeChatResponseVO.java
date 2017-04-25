package com.ehu.pay.weixin.entity;

import java.io.Serializable;


/**
 * @author AlanSun
 * @Date 2016年8月9日
 */
public class WeChatResponseVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String appId;
	private String timeStamp;
	private String nonceStr;
	private String packageValue;
	private String partnerId;
	private String prepayId;
	private String sign;
	/**
	 * @return the appId
	 */
	public String getAppId() {
		return appId;
	}
	/**
	 * @param appId the appId to set
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}
	/**
	 * @return the timeStamp
	 */
	public String getTimeStamp() {
		return timeStamp;
	}
	/**
	 * @param timeStamp the timeStamp to set
	 */
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	/**
	 * @return the nonceStr
	 */
	public String getNonceStr() {
		return nonceStr;
	}
	/**
	 * @param nonceStr the nonceStr to set
	 */
	public void setNonceStr(String nonceStr) {
		this.nonceStr = nonceStr;
	}
	/**
	 * @return the packageValue
	 */
	public String getPackageValue() {
		return packageValue;
	}
	/**
	 * @param packageValue the packageValue to set
	 */
	public void setPackageValue(String packageValue) {
		this.packageValue = packageValue;
	}
	/**
	 * @return the partnerId
	 */
	public String getPartnerId() {
		return partnerId;
	}
	/**
	 * @param partnerId the partnerId to set
	 */
	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}
	/**
	 * @return the prepayId
	 */
	public String getPrepayId() {
		return prepayId;
	}
	/**
	 * @param prepayId the prepayId to set
	 */
	public void setPrepayId(String prepayId) {
		this.prepayId = prepayId;
	}
	/**
	 * @return the sign
	 */
	public String getSign() {
		return sign;
	}
	/**
	 * @param sign the sign to set
	 */
	public void setSign(String sign) {
		this.sign = sign;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "微信返回返回串：appId:"+appId+",timeStamp:"+timeStamp+",nonceStr:"+nonceStr+",packageValue:"+packageValue+",partnerId:"+partnerId+",prepayId:"+prepayId+",sign:"+sign;
	}
}
