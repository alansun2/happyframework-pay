package com.ehu.pay.weixin.weixinpay;

import com.ehu.pay.config.EhPayConfig;
import com.ehu.pay.constants.BaseConstants;
import com.ehu.pay.constants.PayResultCodeConstants;
import com.ehu.pay.constants.PayResultMessageConstants;
import com.ehu.pay.exception.PayException;
import com.ehu.pay.util.XMLUtil;
import com.ehu.pay.weixin.client.TenpayHttpClient;
import com.ehu.pay.weixin.entity.WeChatResponseVO;
import com.ehu.pay.weixin.entity.WeChatpayOrder;
import com.ehu.pay.weixin.util.WeChatUtils;
import lombok.extern.slf4j.Slf4j;
import org.jdom2.JDOMException;

import java.io.IOException;
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
     * @param tradeType 1：app支付；2：扫码支付；3：网页支付
     * @return
     * @throws PayException
     */
    @SuppressWarnings("unchecked")
    public static WeChatResponseVO gerneratorPrepay(WeChatpayOrder order, int tradeType) throws PayException {
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
        if (1 == tradeType) {
            packageParams.put("notify_url", config.getWxPay_notify_url());
            packageParams.put("trade_type", config.getWxPay_trade_type_app());
        } else if (3 == tradeType) {
            packageParams.put("notify_url", config.getWxPay_notify_url());
            packageParams.put("trade_type", config.getWxPay_trade_type_jsapi());
        }
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
        packageParams.put("notify_url", config.getWxPay_scan_notify_url());
        packageParams.put("trade_type", config.getWxPay_trade_type_native());
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
        packageParams.put("notify_url", config.getWxPay_notify_url());
        packageParams.put("trade_type", config.getWxPay_trade_type_jsapi());
        packageParams.put("openid", order.getOpenid());
        packageParams = WeChatUtils.createSign(packageParams, config);
        String prepayId = sendPrepay(packageParams, "prepay_id");//得到prepayid

        String timeStamp = WeChatUtils.getTimeStamp();
        SortedMap<String, String> finalPackage = new TreeMap<>();
        finalPackage.put("appId", config.getWxPay_appid());
        finalPackage.put("timeStamp", timeStamp);
        finalPackage.put("nonceStr", nonceStr);
        finalPackage.put("package", "prepay_id=" + prepayId);
        finalPackage.put("signType", "MD5");
        finalPackage = WeChatUtils.createSign(finalPackage, config);//再次签名获取

        WeChatResponseVO weChatResponseVO = new WeChatResponseVO();
        weChatResponseVO.setTimeStamp(finalPackage.get("timestamp"));
        weChatResponseVO.setNonceStr(nonceStr);
        weChatResponseVO.setPackageValue("prepay_id=" + prepayId);
        weChatResponseVO.setSignType("MD5");
        weChatResponseVO.setSign(finalPackage.get("sign"));
        return weChatResponseVO;
    }

    @SuppressWarnings("unchecked")
    public static String sendPrepay(SortedMap<String, String> map, String key) throws PayException {
        String params = XMLUtil.getXMLString(map);
        TenpayHttpClient httpClient = new TenpayHttpClient();
        httpClient.setReqContent(requestUrl);
        if (httpClient.callHttpPost(requestUrl, params)) {
            String resContent = httpClient.getResContent();
            log.info(resContent);
            Map<String, String> responseMap = null;
            try {
                responseMap = XMLUtil.doXMLParse(resContent);
            } catch (JDOMException e) {
                log.error(PayResultMessageConstants.STRING_WECHATPAY_10008 + key + BaseConstants.RETURN_FAIL, e);
                throw new PayException(PayResultCodeConstants.ERROR_CODE_WECHATPAY_10008, PayResultMessageConstants.STRING_WECHATPAY_10008 + key + BaseConstants.RETURN_FAIL + BaseConstants.TRY_AGAIN);
            } catch (IOException e) {
                log.error(PayResultMessageConstants.STRING_WECHATPAY_10008 + key + BaseConstants.RETURN_FAIL, e);
                throw new PayException(PayResultCodeConstants.ERROR_CODE_WECHATPAY_10008, PayResultMessageConstants.STRING_WECHATPAY_10008 + key + BaseConstants.RETURN_FAIL + BaseConstants.TRY_AGAIN);
            }
            if (BaseConstants.RETURN_FAIL.equals(responseMap.get("return_code"))) {
                log.error(params + responseMap.toString());
                throw new PayException(PayResultCodeConstants.ERROR_CODE_WECHATPAY_10008, PayResultMessageConstants.STRING_WECHATPAY_10008 + key + BaseConstants.RETURN_FAIL + BaseConstants.TRY_AGAIN);
            }
            if (BaseConstants.REFUND_FAIL.equalsIgnoreCase(responseMap.get("result_code"))) {
                log.error(params + responseMap.toString());
                throw new PayException(PayResultCodeConstants.ERROR_CODE_WECHATPAY_10008, PayResultMessageConstants.STRING_WECHATPAY_10008 + key + BaseConstants.RETURN_FAIL + BaseConstants.TRY_AGAIN);
            }
            if (responseMap.containsKey(key)) {
                return map.put(key, responseMap.get(key));
            } else {
                log.error(params + responseMap.toString());
                throw new PayException(PayResultCodeConstants.ERROR_CODE_WECHATPAY_10008, PayResultMessageConstants.STRING_WECHATPAY_10008 + key + BaseConstants.RETURN_FAIL + BaseConstants.TRY_AGAIN);
            }
        } else {
            log.error("httpClient.callHttpPost(requestUrl, params) 返回false" + params);
            throw new PayException(PayResultCodeConstants.ERROR_CODE_WECHATPAY_10008, PayResultMessageConstants.STRING_WECHATPAY_10008 + key + BaseConstants.RETURN_FAIL);
        }
    }
}
