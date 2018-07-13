package com.ehu.constants;


public class BaseConstants {
	public static final String RETURN_SUCCESS_LOWER = "success";
	/**
	 * 返回：SUCCESS
	 */
	public static final String RETURN_SUCCESS = "SUCCESS";
	/**
	 * 返回：FAIL
	 */
	public static final String RETURN_FAIL = "FAIL";
	/**
	 * 返回：请重试
	 */
	public static final String TRY_AGAIN = "请稍后重试";
	/**
	 * 验证guid
	 */
	public static final String GUID_FORMAT = "^[0-9]{1,10}$";
	/**
	 * 支付类型：微信支付
	 */
	public static final String ORDER_PAY_TYPE_WECHATPAY = "WECHATPAY";
	/**
	 * 支付类型：支付宝支付
	 */
	public static final String ORDER_PAY_TYPE_ALIPAY = "ALIPAY";
	/**
	 * 易乎支付宝账号
	 */
	public static final String EH_EMAIL = "ehoo100@163.com";
	public static final String PAY_STATUS = "发起支付";
	/**
	 * 6-11微信退款失败
	 */
	public static final String REFUND_FAIL = "6-11微信退款失败";
	
	public static final String TRANS_FAIL = "6-11微信退款失败";
	/**
	 * 支付宝业务返回码
	 */
	public static final String ALIPAY_RETURN_CODE_10000 = "10000";
	/**
	 * trade_type:APP
	 */
	public static final String TRADE_TYPE_APP = "APP";
	/**
	 * trade_type:NATIVE
	 */
	public static final String TRADE_TYPE_NATIVE = "NATIVE";
	/**
	 * trade_state:SUCCESS
	 */
	public static final String TRADE_STATE_SUCCESS = "SUCCESS";
	/**
	 * trade_state:REFUND
	 */
	public static final String TRADE_STATE_REFUND = "REFUND";
	/**
	 * trade_state:NOTPAY
	 */
	public static final String TRADE_STATE_NOTPAY = "NOTPAY";
	/**
	 * trade_state:CLOSED
	 */
	public static final String TRADE_STATE_CLOSED = "CLOSED";
	/**
	 * trade_state:REVOKED(刷卡支付)
	 */
	public static final String TRADE_STATE_REVOKED = "REVOKED";
	/**
	 * trade_state:USERPAYING
	 */
	public static final String TRADE_STATE_USERPAYING = "USERPAYING";
	/**
	 * trade_state:PAYERROR(支付失败(其他原因，如银行返回失败))
	 */
	public static final String TRADE_STATE_PAYERROR = "PAYERROR";
}
