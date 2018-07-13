package com.ehu.constants;
/**
 * 
 * @author AlanSun
 * 2016-08-10
 * 支付错误信息常量类
 */
public class PayResultMessageConstants {
	/**
	 * 微信支付：微信返回有误
	 */
	public static final String STRING_WECHATPAY_10004 = "微信返回有误";
	/**
	 * 微信支付：微信服务器异常
	 */
	public static final String STRING_WECHATPAY_10003 = "查询微信服务器异常";
	/**
	 * 微信支付：微信支付获取prepayid失败
	 */
	public static final String STRING_WECHATPAY_10008 = "微信支付获取";
	/**
	 * 支付宝支付：支付宝查询有误
	 */
	public static final String STRING_ALIPAY_10005 = "支付宝查询有误";
	/**
	 * 支付宝支付：支付宝返回信息可能被篡改
	 */
	public static final String STRING_ALIPAY_10006 = "签名错误：支付宝返回信息可能被篡改";
	/**
	 * 回调验证错误
	 */
	public static final String STRING_CALLBACK_VER_10007 = "回调验证错误";
	/**
	 * 支付宝扫码错误
	 */
	public static final String ALIPAY_SCAN_ERROR_30001 = "支付宝扫码错误";
	/**
	 * 支付宝退款失败
	 */
	public static final String ALIPAY_SCAN_ERROR_30002 = "支付宝退款失败";
	/**
	 * 支付宝扫码查询失败
	 */
	public static final String ALIPAY_SCAN_ERROR_30003 = "支付宝扫码查询失败";
	/**
	 * 获取微信二维码失败
	 */
	public static final String WECHAT_SCAN_ERROR_30004 = "获取微信二维码失败";
	/**
	 * 支付宝交易不存在
	 */
	public static final String TRADE_NOT_EXIST_30005 = "支付宝交易不存在，您可能还未付款";
	/**
	 * 微信支付trade_state:NOTPAY(未支付)
	 */
	public static final String TRADE_STATE_NOTPAY_30006 = "您还未支付或已取消支付";
	/**
	 * 微信支付trade_state:SUCCESS
	 */
	public static final String TRADE_STATE_SUCCESS_30007 = "交易成功";
	/**
	 * 微信支付trade_state:REFUND
	 */
	public static final String TRADE_STATE_REFUND_30008 = "交易退款中";
	/**
	 * 微信支付trade_state:CLOSED
	 */
	public static final String TRADE_STATE_CLOSED_30009 = "交易已关闭";
	/**
	 * 微信支付trade_state:USERPAYING
	 */
	public static final String TRADE_STATE_USERPAYING_30010 = "支付中";
	/**
	 * 微信支付trade_state:PAYERROR
	 */
	public static final String TRADE_STATE_PAYERROR_30011 = "支付失败";
	/**
	 * 微信支付trade_state:REVOKED
	 */
	public static final String TRADE_STATE_REVOKED_30012 = "支付已撤销";
	/**
	 * 获取财务url失败
	 */
	public static final String GET_FINANCIAL_30013 = "财务账单执行失败";
}
