package com.ehu.pay.weixin.weixinpay;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.ehu.pay.config.EhPayConfig;
import com.ehu.pay.weixin.util.MD5Util;
import com.ehu.pay.weixin.util.WXUtil;




/**
 * 
 * @author AlanSun
 * 20160422
 * 获取发送给移动端的最终结果
 */
public class GetFinalWeChatpayResult {
	/**
	 * 二次签名
	 * @param prepayid
	 * @return
	 */
	public static SortedMap<String,String> getFinalPackage(Map<String,String> map){
		EhPayConfig config = EhPayConfig.getInstance();
		String timestamp = WXUtil.getTimeStamp();
		SortedMap<String,String> finalPackage = new TreeMap<String,String>();
		finalPackage.put("appId", config.getWxPay_appid());
		finalPackage.put("timeStamp", timestamp);
		finalPackage.put("nonceStr", map.get("nonce_str"));
		finalPackage.put("packageValue", "Sign=WXPay");
		finalPackage.put("partnerId", config.getWxPay_mch_id());
		finalPackage.put("prepayId", map.get("prepay_id"));
		
		SortedMap<String,String> finalPackage1 = new TreeMap<String,String>();
		finalPackage1.put("appid", config.getWxPay_appid());
		finalPackage1.put("timestamp", timestamp);
		finalPackage1.put("noncestr", map.get("nonce_str"));
		finalPackage1.put("package", "Sign=WXPay");
		finalPackage1.put("partnerid", config.getWxPay_mch_id());
		finalPackage1.put("prepayid", map.get("prepay_id"));
		String appsign = createSign(finalPackage1);
		appsign = appsign+"key="+config.getWxPay_app_key();
		appsign = MD5Util.MD5Encode(appsign, "UTF-8").toUpperCase();
		finalPackage.put("sign", appsign);
		return finalPackage;
	}
	
	public static String createSign(SortedMap<String,String> finalpackage) {
		StringBuffer sb = new StringBuffer();
		Set<Map.Entry<String,String>> es = finalpackage.entrySet();
		Iterator<Map.Entry<String,String>> it = es.iterator();
		while (it.hasNext()) {
			Map.Entry<String,String> entry = (Map.Entry<String,String>) it.next();
			String k = (String) entry.getKey();
			String v = (String) entry.getValue();
			sb.append(k + "=" + v + "&");
		}
		return sb.toString();
	}

}
