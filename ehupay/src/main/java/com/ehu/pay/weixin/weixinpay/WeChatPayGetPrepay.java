package com.ehu.pay.weixin.weixinpay;

import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import lombok.extern.slf4j.Slf4j;

import com.ehu.pay.config.EhPayConfig;
import com.ehu.pay.constants.BaseConstants;
import com.ehu.pay.constants.PayResultCodeConstants;
import com.ehu.pay.constants.PayResultMessageConstants;
import com.ehu.pay.exception.PayException;
import com.ehu.pay.weixin.client.TenpayHttpClient;
import com.ehu.pay.weixin.entity.WeChatpayOrder;
import com.ehu.pay.weixin.util.MD5Util;
import com.ehu.pay.weixin.util.WeChatUtils;
import com.ehu.pay.weixin.util.XMLUtil;




/**
 * 
 * @author AlanSun
 * 20160422
 * 获取prepayid
 */
@Slf4j
public class WeChatPayGetPrepay {
	private static final String requestUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	@SuppressWarnings("unchecked")
	public static Map<String, String> gerneratorPrepay(WeChatpayOrder order,int tradeType) throws PayException{
		EhPayConfig config = EhPayConfig.getInstance();
		String finalmoney = String.format("%.2f", order.getPrice());
		finalmoney = finalmoney.replace(".", "");
		int intMoney = Integer.parseInt(finalmoney);
		
		//封装获取prepayid
		Random random = new Random();
		String nonce_str = MD5Util.MD5Encode(String.valueOf(random.nextInt(10000)), "gbk");
		SortedMap<String, String> packageParams = new TreeMap<String,String>();
		packageParams.put("appid", config.getWxPay_appid());
		packageParams.put("mch_id", config.getWxPay_mch_id());
		packageParams.put("nonce_str", nonce_str);
		packageParams.put("body", order.getBody());
		packageParams.put("out_trade_no", order.getOrderId());
		packageParams.put("total_fee", intMoney + "");
		packageParams.put("spbill_create_ip", config.getWxPay_spbill_create_ip());
		packageParams.put("out_trade_no", order.getOrderId());
		if(1 == tradeType){
			packageParams.put("notify_url", config.getWxPay_notify_url());
			packageParams.put("trade_type", config.getWxPay_trade_type_app());
		}else if(2 == tradeType){
			packageParams.put("notify_url", config.getWxPay_scan_notify_url());
			packageParams.put("trade_type", config.getWxPay_trade_type_native());
		}else if(3 == tradeType){
			packageParams.put("notify_url", config.getWxPay_notify_url());
			packageParams.put("trade_type", config.getWxPay_trade_type_jsapi());
		}
		packageParams = WeChatUtils.createSign(packageParams,config);
		packageParams = sendPrepay(packageParams);
		packageParams.put("nonce_str", nonce_str);
		return packageParams;
	}

	@SuppressWarnings("unchecked")
	public static SortedMap<String, String> sendPrepay(SortedMap<String, String> map) throws PayException {
		String params = XMLUtil.getXMLString(map);
		TenpayHttpClient httpClient = new TenpayHttpClient();
		httpClient.setReqContent(requestUrl);
		String resContent = "";
		if (httpClient.callHttpPost(requestUrl, params)) {
			resContent = httpClient.getResContent();
			try {
				log.info(resContent);
				map.clear();
				Map<String,String> m = XMLUtil.doXMLParse(resContent);
				map.put("prepay_id", m.get("prepay_id"));
				if(BaseConstants.RETURN_FAIL.equals(m.get("return_code"))){
					log.error("获取prepayid失败"+map.toString());
					throw new PayException(PayResultCodeConstants.ERROR_CODE_WECHATPAY_10008,PayResultMessageConstants.STRING_WECHATPAY_10008);
				}
				if(m.containsKey("code_url")){
					map.put("code_url", m.get("code_url"));
				}
			} catch (Exception e) {
				log.error("获取prepayid失败"+map.get("error_code_des"),e);
				throw new PayException(PayResultCodeConstants.ERROR_CODE_WECHATPAY_10008,PayResultMessageConstants.STRING_WECHATPAY_10008);
			}
		}
		return map;
	}
}
