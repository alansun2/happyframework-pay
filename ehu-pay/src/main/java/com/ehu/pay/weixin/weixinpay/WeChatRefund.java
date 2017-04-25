package com.ehu.pay.weixin.weixinpay;

import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import com.ehu.pay.config.EhPayConfig;
import com.ehu.pay.exception.PayException;
import com.ehu.pay.weixin.entity.WeChatRefundInfo;
import com.ehu.pay.weixin.util.MD5Util;
import com.ehu.pay.weixin.util.WeChatUtils;


/**
 * 20160429
 * @author AlanSun
 *
 */
public class WeChatRefund {
	
	static final String requestUrl = "https://api.mch.weixin.qq.com/secapi/pay/refund";
	@SuppressWarnings("unchecked")
	public static boolean weChatRefundOper(WeChatRefundInfo weChatRefundInfo) throws PayException{
		EhPayConfig config = EhPayConfig.getInstance();
		String finalmoney = String.format("%.2f", weChatRefundInfo.getTotalFee());
		finalmoney = finalmoney.replace(".", "");
		while(finalmoney.startsWith("0")){
			finalmoney = finalmoney.substring(1, finalmoney.length());
		}
		Random random = new Random();
		String nonce_str = MD5Util.MD5Encode(String.valueOf(random.nextInt(10000)), "gbk");
		SortedMap<String, String> packageParams = new TreeMap<String,String>();
		packageParams.put("appid",config.getWxPay_appid());
		packageParams.put("mch_id", config.getWxPay_mch_id());
		packageParams.put("nonce_str", nonce_str);
		packageParams.put("transaction_id", weChatRefundInfo.getTransactionId());
		packageParams.put("out_refund_no", weChatRefundInfo.getOutRefundNo());
		packageParams.put("total_fee", finalmoney);
		packageParams.put("refund_fee", finalmoney);
		packageParams.put("transaction_id", weChatRefundInfo.getTransactionId());
		packageParams.put("op_user_id", config.getWxPay_mch_id());
		packageParams = WeChatUtils.createSign(packageParams, config);
		Map<String,String> map = WeChatUtils.wechatPostWithSSL(packageParams, requestUrl,config.getWxPay_code());
		return WeChatUtils.checkWechatResponse(map);
	}
	
}
