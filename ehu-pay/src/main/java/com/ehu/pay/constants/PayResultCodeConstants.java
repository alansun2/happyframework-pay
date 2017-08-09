package com.ehu.pay.constants;

/**
 * @author AlanSun
 * @Date 2016年8月10日
 * 支付错误返回码
 */
public class PayResultCodeConstants {
	/**
	 * 微信支付获取prepayid失败
	 */
	public static final int ERROR_CODE_WECHATPAY_10008 = 10008;
	/**
	 * 查询微信服务器异常
	 */
	public static final int ERROR_CODE_WECHATPAY_10003 = 10003;
	/**
	 * 微信返回有误
	 */
	public static final int ERROR_CODE_WECHATPAY_10004 = 10004;
	/**
	 * 支付宝查询有误
	 */
	public static final int ERROR_CODE_ALIPAY_10005 = 10005;
	/**
	 * 支付宝返回信息可能被篡改
	 */
	public static final int ERROR_CODE_ALIPAY_10006 = 10006;
	/**
	 * 回调验证错误
	 */
	public static final int CALLBACK_VAR_ERROR_10007 = 10007;
	/**
	 * 支付宝扫码错误
	 */
	public static final int ALIPAY_SCAN_ERROR_30001 = 30001;
	
	/**
	 * 支付宝退款失败
	 */
	public static final int ALIPAY_SCAN_ERROR_30002 = 30002;
	/**
	 * 支付宝扫码查询失败
	 */
	public static final int ALIPAY_SCAN_ERROR_30003 = 30003;
	/**
	 * 获取微信二维码失败
	 */
	public static final int WECHAT_SCAN_ERROR_30004 = 30004;
	/**
	 * 支付宝交易不存在
	 */
	public static final int TRADE_NOT_EXIST_30005 = 30005;
	/**
	 * 微信支付trade_state:NOTPAY(未支付)
	 */
	public static final int TRADE_STATE_NOTPAY_30006 = 30006;
	/**
	 * 微信支付trade_state:SUCCESS
	 */
	public static final int TRADE_STATE_SUCCESS_30007 = 30007;
	/**
	 * 微信支付trade_state:REFUND
	 */
	public static final int TRADE_STATE_REFUND_30008 = 30008;
	/**
	 * 微信支付trade_state:CLOSED
	 */
	public static final int TRADE_STATE_CLOSED_30009 = 30009;
	/**
	 * 微信支付trade_state:USERPAYING
	 */
	public static final int TRADE_STATE_USERPAYING_30010 = 30010;
	/**
	 * 微信支付trade_state:PAYERROR
	 */
	public static final int TRADE_STATE_PAYERROR_30011 = 30011;
	/**
	 * 微信支付trade_state:REVOKED
	 */
	public static final int TRADE_STATE_REVOKED_30012 = 30012;
	/**
	 * 获取财务url失败
	 */
	public static final int GET_FINANCIAL_30013 = 30013;
}
