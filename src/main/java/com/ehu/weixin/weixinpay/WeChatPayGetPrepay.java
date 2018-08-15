package com.ehu.weixin.weixinpay;

import com.ehu.config.EhPayConfig;
import com.ehu.constants.PayBaseConstants;
import com.ehu.constants.PayResultCodeConstants;
import com.ehu.constants.PayResultMessageConstants;
import com.ehu.exception.PayException;
import com.ehu.util.XmlUtils;
import com.ehu.weixin.client.TenpayHttpClient;
import com.ehu.weixin.entity.WeChatResponseVO;
import com.ehu.weixin.entity.WeChatpayOrder;
import com.ehu.weixin.util.Signature;
import com.ehu.weixin.util.WeChatUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;


/**
 * @author AlanSun
 * 20160422
 * 获取prepayid
 */
@Slf4j
public class WeChatPayGetPrepay {
    private static final String requestUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    /**
     * @param order
     * @return
     * @throws PayException
     */
    @SuppressWarnings("unchecked")
    public static WeChatResponseVO gerneratorPrepay(WeChatpayOrder order) throws PayException {
        EhPayConfig config = EhPayConfig.getInstance();

        //封装获取prepayid
        String nonceStr = WeChatUtils.getNonceStr();
        SortedMap<String, String> packageParams = new TreeMap<String, String>();
        packageParams.put("appid", config.getWxPay_appid());
        packageParams.put("mch_id", config.getWxPay_mch_id());
        packageParams.put("nonce_str", nonceStr);
        packageParams.put("body", order.getBody());
        packageParams.put("out_trade_no", order.getOrderId());
        packageParams.put("total_fee", WeChatUtils.getFinalMoney(order.getPrice()));
        packageParams.put("spbill_create_ip", config.getWxPay_spbill_create_ip());
        packageParams.put("out_trade_no", order.getOrderId());
        packageParams.put("notify_url", order.getNotifyUrl());
        packageParams.put("trade_type", order.getTradeType());
        packageParams = WeChatUtils.createSign(packageParams, config);
        String prepayId = sendPrepay(packageParams, "prepay_id");//得到prepayid
        packageParams.put("nonce_str", nonceStr);

        String timestamp = WeChatUtils.getTimeStamp();
        SortedMap<String, String> finalPackage = new TreeMap<String, String>();
        finalPackage.put("appid", config.getWxPay_appid());
        finalPackage.put("timestamp", timestamp);
        finalPackage.put("noncestr", nonceStr);
        finalPackage.put("package", "Sign=WXPay");
        finalPackage.put("partnerid", config.getWxPay_mch_id());
        finalPackage.put("prepayid", prepayId);
        finalPackage = WeChatUtils.createSign(finalPackage, config);

        WeChatResponseVO weChatResponseVO = new WeChatResponseVO();
        weChatResponseVO.setAppId(config.getWxPay_appid());
        weChatResponseVO.setTimeStamp(timestamp);
        weChatResponseVO.setNonceStr(nonceStr);
        weChatResponseVO.setPackageValue("Sign=WXPay");
        weChatResponseVO.setPartnerId(config.getWxPay_mch_id());
        weChatResponseVO.setPrepayId(prepayId);
        weChatResponseVO.setSign(finalPackage.get("sign"));
        return weChatResponseVO;
    }

    /**
     * @param order
     * @return
     * @throws PayException
     */
    @SuppressWarnings("unchecked")
    public static String gerneratorPrepayScan(WeChatpayOrder order) throws PayException {
        EhPayConfig config = EhPayConfig.getInstance();

        //封装获取prepayid
        String nonceStr = WeChatUtils.getNonceStr();
        SortedMap<String, String> packageParams = new TreeMap<String, String>();
        packageParams.put("appid", config.getWxPay_appid());
        packageParams.put("mch_id", config.getWxPay_mch_id());
        packageParams.put("nonce_str", nonceStr);
        packageParams.put("body", order.getBody());
        packageParams.put("out_trade_no", order.getOrderId());
        packageParams.put("total_fee", WeChatUtils.getFinalMoney(order.getPrice()));
        packageParams.put("spbill_create_ip", config.getWxPay_spbill_create_ip());
        packageParams.put("out_trade_no", order.getOrderId());
        packageParams.put("notify_url", order.getNotifyUrl());
        packageParams.put("trade_type", order.getTradeType());
        packageParams = WeChatUtils.createSign(packageParams, config);
        return sendPrepay(packageParams, "code_url");//得到prepayid
    }

    /**
     * 获取小程序prepayId
     *
     * @param order
     * @return
     * @throws PayException
     */
    public static WeChatResponseVO gerneratorPrepayXcx(WeChatpayOrder order) throws PayException {
        EhPayConfig config = EhPayConfig.getInstance();

        //封装获取prepayid
        String nonceStr = WeChatUtils.getNonceStr();
        SortedMap<String, String> packageParams = new TreeMap<>();
        packageParams.put("appid", config.getWxxcx_appid());
        packageParams.put("mch_id", config.getWxPay_mch_id());
        packageParams.put("nonce_str", nonceStr);
        packageParams.put("body", order.getBody());
        packageParams.put("out_trade_no", order.getOrderId());
        packageParams.put("total_fee", WeChatUtils.getFinalMoney(order.getPrice()));
        packageParams.put("spbill_create_ip", config.getWxPay_spbill_create_ip());
        packageParams.put("out_trade_no", order.getOrderId());
        packageParams.put("notify_url", order.getNotifyUrl());
        packageParams.put("trade_type", order.getTradeType());
        packageParams.put("openid", order.getOpenid());
        packageParams = WeChatUtils.createSign(packageParams, config);
        String prepayId = sendPrepay(packageParams, "prepay_id");//得到prepayid

        String timeStamp = WeChatUtils.getTimeStamp();
        SortedMap<String, String> finalPackage = new TreeMap<>();
        finalPackage.put("appId", config.getWxxcx_appid());
        finalPackage.put("nonceStr", nonceStr);
        finalPackage.put("package", "prepay_id=" + prepayId);
        finalPackage.put("signType", "MD5");
        finalPackage.put("timeStamp", timeStamp);

        WeChatResponseVO weChatResponseVO = new WeChatResponseVO();
        weChatResponseVO.setAppId(config.getWxxcx_appid());
        weChatResponseVO.setTimeStamp(timeStamp);
        weChatResponseVO.setPackageValue("prepay_id=" + prepayId);
        weChatResponseVO.setNonceStr(nonceStr);
        weChatResponseVO.setSignType("MD5");
        weChatResponseVO.setSign(Signature.getSign(finalPackage));
        return weChatResponseVO;
    }

    @SuppressWarnings("unchecked")
    public static String sendPrepay(SortedMap<String, String> map, String key) throws PayException {
        String params = XmlUtils.mapToXml(map);
        TenpayHttpClient httpClient = new TenpayHttpClient();
        httpClient.setReqContent(requestUrl);
        if (httpClient.callHttpPost(requestUrl, params)) {
            String resContent = httpClient.getResContent();
            log.info(resContent);
            Map<String, String> responseMap;
            try {
                responseMap = XmlUtils.xmlToMap(resContent);
            } catch (Exception e) {
                log.error(PayResultMessageConstants.STRING_WECHATPAY_10008 + key + PayBaseConstants.RETURN_FAIL, e);
                throw new PayException(PayResultCodeConstants.ERROR_CODE_WECHATPAY_10008, PayResultMessageConstants.STRING_WECHATPAY_10008 + key + PayBaseConstants.RETURN_FAIL + PayBaseConstants.TRY_AGAIN);
            }
            if (PayBaseConstants.RETURN_FAIL.equals(responseMap.get("return_code"))) {
                log.error(params + responseMap.toString());
                throw new PayException(PayResultCodeConstants.ERROR_CODE_WECHATPAY_10008, PayResultMessageConstants.STRING_WECHATPAY_10008 + key + PayBaseConstants.RETURN_FAIL + PayBaseConstants.TRY_AGAIN);
            }
            if (PayBaseConstants.REFUND_FAIL.equalsIgnoreCase(responseMap.get("result_code"))) {
                log.error(params + responseMap.toString());
                throw new PayException(PayResultCodeConstants.ERROR_CODE_WECHATPAY_10008, PayResultMessageConstants.STRING_WECHATPAY_10008 + key + PayBaseConstants.RETURN_FAIL + PayBaseConstants.TRY_AGAIN);
            }
            if (responseMap.containsKey(key)) {
                return responseMap.get(key);
            } else {
                log.error(params + responseMap.toString());
                throw new PayException(PayResultCodeConstants.ERROR_CODE_WECHATPAY_10008, PayResultMessageConstants.STRING_WECHATPAY_10008 + key + PayBaseConstants.RETURN_FAIL + PayBaseConstants.TRY_AGAIN);
            }
        } else {
            log.error("httpClient.callHttpPost(requestUrl, params) 返回false" + params);
            throw new PayException(PayResultCodeConstants.ERROR_CODE_WECHATPAY_10008, PayResultMessageConstants.STRING_WECHATPAY_10008 + key + PayBaseConstants.RETURN_FAIL);
        }
    }
}
