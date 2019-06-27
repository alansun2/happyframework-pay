package com.ehu.weixin.weixinpay;

import com.ehu.config.Wechat;
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
public class GetPrepayInfo {
    private static final String REQUESTURL = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    /**
     * @param order 订单信息
     * @return 支付信息
     * @throws PayException e
     */
    @SuppressWarnings("unchecked")
    public static WeChatResponseVO gerneratorPrepay(WeChatpayOrder order) throws PayException {
        int mchNo = order.getMchNo();
        Wechat config = Wechat.getInstance();

        String appId = config.getAppId();
        Wechat.WechatMch wechatMch = config.getMchMap().get(mchNo);
        String mchId = wechatMch.getMchId();
        String signKey = wechatMch.getSignKey();
        //封装获取prepayid
        String nonceStr = WeChatUtils.getNonceStr();
        SortedMap<String, String> packageParams = new TreeMap<>();
        packageParams.put("appid", appId);
        packageParams.put("mch_id", mchId);
        packageParams.put("nonce_str", nonceStr);
        packageParams.put("body", order.getBody());
        packageParams.put("out_trade_no", order.getOrderId());
        packageParams.put("total_fee", WeChatUtils.getFinalMoney(order.getPrice()));
        packageParams.put("spbill_create_ip", config.getSpbillCreateIp());
        packageParams.put("notify_url", order.getNotifyUrl());
        packageParams.put("trade_type", order.getTradeType());
        packageParams.put("sign", Signature.getSign(packageParams, signKey));
        //得到prepayid
        String prepayId = getSpecificKey(sendPrepay(packageParams), "prepay_id");
        packageParams.put("nonce_str", nonceStr);

        //签名
        String timestamp = WeChatUtils.getTimeStamp();
        SortedMap<String, String> finalPackage = new TreeMap<>();
        finalPackage.put("appid", appId);
        finalPackage.put("timestamp", timestamp);
        finalPackage.put("noncestr", nonceStr);
        finalPackage.put("package", "Sign=WXPay");
        finalPackage.put("partnerid", mchId);
        finalPackage.put("prepayid", prepayId);

        WeChatResponseVO weChatResponseVO = new WeChatResponseVO();
        weChatResponseVO.setAppId(appId);
        weChatResponseVO.setTimeStamp(timestamp);
        weChatResponseVO.setNonceStr(nonceStr);
        weChatResponseVO.setPackageValue("Sign=WXPay");
        weChatResponseVO.setPartnerId(mchId);
        weChatResponseVO.setPrepayId(prepayId);
        weChatResponseVO.setSign(Signature.getSign(finalPackage, signKey));
        return weChatResponseVO;
    }


    /**
     * 获取小程序prepayId
     *
     * @param order 订单信息
     * @return 支付信息
     * @throws PayException e
     */
    public static WeChatResponseVO gerneratorPrepayXcx(WeChatpayOrder order) throws PayException {
        int mchNo = order.getMchNo();
        Wechat config = Wechat.getInstance();

        String appletsAppId = config.getAppletsAppId();
        Wechat.WechatMch wechatMch = config.getMchMap().get(mchNo);
        String mchId = wechatMch.getMchId();
        String signKey = wechatMch.getSignKey();

        //封装获取prepayid
        String nonceStr = WeChatUtils.getNonceStr();
        SortedMap<String, String> packageParams = new TreeMap<>();
        packageParams.put("appid", appletsAppId);
        packageParams.put("mch_id", mchId);
        packageParams.put("nonce_str", nonceStr);
        packageParams.put("body", order.getBody());
        packageParams.put("out_trade_no", order.getOrderId());
        packageParams.put("total_fee", WeChatUtils.getFinalMoney(order.getPrice()));
        packageParams.put("spbill_create_ip", config.getSpbillCreateIp());
        packageParams.put("notify_url", order.getNotifyUrl());
        packageParams.put("trade_type", order.getTradeType());
        packageParams.put("openid", order.getOpenid());
        packageParams.put("sign", Signature.getSign(packageParams, signKey));
        //得到prepayid
        String prepayId = getSpecificKey(sendPrepay(packageParams), "prepay_id");

        String timeStamp = WeChatUtils.getTimeStamp();
        SortedMap<String, String> finalPackage = new TreeMap<>();
        finalPackage.put("appId", appletsAppId);
        finalPackage.put("nonceStr", nonceStr);
        finalPackage.put("package", "prepay_id=" + prepayId);
        finalPackage.put("signType", "MD5");
        finalPackage.put("timeStamp", timeStamp);

        WeChatResponseVO weChatResponseVO = new WeChatResponseVO();
        weChatResponseVO.setAppId(appletsAppId);
        weChatResponseVO.setTimeStamp(timeStamp);
        weChatResponseVO.setPackageValue("prepay_id=" + prepayId);
        weChatResponseVO.setNonceStr(nonceStr);
        weChatResponseVO.setSignType("MD5");
        weChatResponseVO.setSign(Signature.getSign(finalPackage, signKey));
        return weChatResponseVO;
    }

    /**
     * 扫码支付获取支付的二维码
     *
     * @param order 订单信息
     * @return 二维码连接
     * @throws PayException e
     */
    @SuppressWarnings("unchecked")
    public static String gerneratorPrepayScan(WeChatpayOrder order) throws PayException {
        Wechat config = Wechat.getInstance();
        int mchNo = order.getMchNo();

        Wechat.WechatMch wechatMch = config.getMchMap().get(mchNo);
        //封装获取prepayid
        String nonceStr = WeChatUtils.getNonceStr();
        SortedMap<String, String> packageParams = new TreeMap<>();
        packageParams.put("appid", config.getAppId());
        packageParams.put("mch_id", wechatMch.getMchId());
        packageParams.put("nonce_str", nonceStr);
        packageParams.put("body", order.getBody());
        packageParams.put("out_trade_no", order.getOrderId());
        packageParams.put("total_fee", WeChatUtils.getFinalMoney(order.getPrice()));
        packageParams.put("spbill_create_ip", config.getSpbillCreateIp());
        packageParams.put("notify_url", order.getNotifyUrl());
        packageParams.put("trade_type", order.getTradeType());
        packageParams.put("sign", Signature.getSign(packageParams, wechatMch.getSignKey()));
        //得到prepayid
        return getSpecificKey(sendPrepay(packageParams), "code_url");
    }

    /**
     * 获取prepayId
     *
     * @param map map
     * @return prepayId
     * @throws PayException e
     */
    private static Map<String, String> sendPrepay(SortedMap<String, String> map) throws PayException {
        String params = XmlUtils.mapToXml(map);
        TenpayHttpClient httpClient = new TenpayHttpClient();
        httpClient.setReqContent(REQUESTURL);
        if (httpClient.callHttpPost(REQUESTURL, params)) {
            String resContent = httpClient.getResContent();
            log.info(resContent);
            Map<String, String> responseMap;
            try {
                responseMap = XmlUtils.xmlToMap(resContent);
            } catch (Exception e) {
                log.error(PayResultMessageConstants.STRING_WECHATPAY_10008, e);
                throw new PayException(PayResultCodeConstants.ERROR_CODE_WECHATPAY_10008, PayResultMessageConstants.STRING_WECHATPAY_10008);
            }
            if (PayBaseConstants.RETURN_FAIL.equals(responseMap.get("return_code"))) {
                log.error(params + responseMap.toString());
                throw new PayException(PayResultCodeConstants.ERROR_CODE_WECHATPAY_10008, PayResultMessageConstants.STRING_WECHATPAY_10008);
            }
            if (PayBaseConstants.REFUND_FAIL.equalsIgnoreCase(responseMap.get("result_code"))) {
                log.error(params + responseMap.toString());
                throw new PayException(PayResultCodeConstants.ERROR_CODE_WECHATPAY_10008, PayResultMessageConstants.STRING_WECHATPAY_10008);
            }

            return responseMap;
        } else {
            log.error("httpClient.callHttpPost(requestUrl, params) 返回false" + params);
            throw new PayException(PayResultCodeConstants.ERROR_CODE_WECHATPAY_10008, PayResultMessageConstants.STRING_WECHATPAY_10008);
        }
    }


    private static String getSpecificKey(Map<String, String> responseMap, String key) throws PayException {
        if (responseMap.containsKey(key)) {
            return responseMap.get(key);
        } else {
            throw new PayException(PayResultCodeConstants.ERROR_CODE_WECHATPAY_10008, PayResultMessageConstants.STRING_WECHATPAY_10008 + key + PayBaseConstants.RETURN_FAIL + PayBaseConstants.TRY_AGAIN);
        }
    }
}
