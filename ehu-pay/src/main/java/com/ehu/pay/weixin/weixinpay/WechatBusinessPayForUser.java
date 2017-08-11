package com.ehu.pay.weixin.weixinpay;

import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import com.ehu.pay.config.EhPayConfig;
import com.ehu.pay.exception.PayException;
import com.ehu.pay.weixin.entity.WechatBusinessPay;
import com.ehu.pay.weixin.util.MD5Util;
import com.ehu.pay.weixin.util.WeChatUtils;


/**
 * @author AlanSun
 * @Date 2016年8月3日
 * 微信企业付款操作类
 */
public class WechatBusinessPayForUser {
	
	private static String REQUESTURL = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";
	
	@SuppressWarnings("unchecked")
	public static boolean weChatPayBusinessPayforUser(WechatBusinessPay wechatBusinessPay) throws PayException{
		EhPayConfig config = EhPayConfig.getInstance();
		String finalmoney = String.format("%.2f", wechatBusinessPay.getTotalPrice());//保留两位小数
		finalmoney = finalmoney.replace(".", "");
		int intMoney = Integer.parseInt(finalmoney);
		
		Random random = new Random();
		String nonce_str = MD5Util.MD5Encode(String.valueOf(random.nextInt(10000)), "gbk");
		
		SortedMap<String, String> packageParams = new TreeMap<String,String>();
		packageParams.put("mch_appid", config.getWxPay_appid());
		packageParams.put("mch_id", config.getWxPay_mch_id());
		packageParams.put("nonce_str", nonce_str);
		packageParams.put("partner_trade_no", wechatBusinessPay.getOrderId());
		packageParams.put("openid", wechatBusinessPay.getOrderId());
		/*NO_CHECK：不校验真实姓名
		FORCE_CHECK：强校验真实姓名（未实名认证的用户会校验失败，无法转账）
		OPTION_CHECK：针对已实名认证的用户才校验真实姓名（未实名认证用户不校验，可以转账成功）*/
		packageParams.put("check_name", "OPTION_CHECK");
		packageParams.put("re_user_name", wechatBusinessPay.getReUserName());
		packageParams.put("amount", intMoney + "");
		packageParams.put("spbill_create_ip", config.getWxPay_spbill_create_ip());
		packageParams = WeChatUtils.createSign(packageParams, config);//获取签名
		Map<String,String> map = WeChatUtils.wechatPostWithSSL(packageParams, REQUESTURL, config.getWxPay_code());//发送得到微信服务器
		return WeChatUtils.checkWechatResponse(map);
	}
}
