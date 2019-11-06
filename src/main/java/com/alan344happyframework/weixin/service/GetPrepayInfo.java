package com.alan344happyframework.weixin.service;

import com.alan344happyframework.bean.OrderPay;
import com.alan344happyframework.bean.OrderScanPay;
import com.alan344happyframework.bean.PayResponse;
import com.alan344happyframework.config.Wechat;
import com.alan344happyframework.constants.ErrorCode;
import com.alan344happyframework.constants.PayBaseConstants;
import com.alan344happyframework.core.httpresponsehandler.MapStringStringResponseHandler;
import com.alan344happyframework.core.responsehandler.WechatResponseHandlerBase;
import com.alan344happyframework.exception.PayException;
import com.alan344happyframework.util.HttpClientUtils;
import com.alan344happyframework.util.StringUtils;
import com.alan344happyframework.util.ValidationUtil;
import com.alan344happyframework.util.XmlUtils;
import com.alan344happyframework.util.bean.HttpParams;
import com.alan344happyframework.weixin.entity.WeChatResponseVO;
import com.alan344happyframework.weixin.entity.WechatPayOrder;
import com.alan344happyframework.weixin.util.Signature;
import com.alan344happyframework.weixin.util.WechatUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpException;

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
public class GetPrepayInfo {
    private static final String REQUESTURL = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    /**
     * @param order 订单信息
     * @return 支付信息
     * @throws PayException e
     */
    @SuppressWarnings("unchecked")
    public static WeChatResponseVO generatorPrepay(OrderPay order) throws PayException {
        WechatPayOrder wechatPayOrder = order.getWechatPayOrder();
        int mchNo = wechatPayOrder.getMchNo();
        Wechat config = Wechat.getInstance();
        config.getMchAppIdMap().get(Wechat.DEFAULT_MCH);

        //默认使用第一个appid进行支付
        String appId = config.getMchAppIdMap().get(wechatPayOrder.getMchAppIdNo());
        Wechat.WechatMch wechatMch = config.getMchMap().get(mchNo);
        String mchId = wechatMch.getMchId();
        String signKey = wechatMch.getSignKey();
        //封装获取prepayid
        String nonceStr = WechatUtils.getNonceStr();
        SortedMap<String, String> packageParams = new TreeMap<>();
        packageParams.put("appid", appId);
        packageParams.put("mch_id", mchId);
        packageParams.put("nonce_str", nonceStr);
        packageParams.put("body", order.getBody());
        packageParams.put("out_trade_no", order.getOrderId());
        packageParams.put("total_fee", WechatUtils.getFinalMoney(order.getPrice()));
        packageParams.put("spbill_create_ip", config.getSpbillCreateIp());
        packageParams.put("notify_url", StringUtils.getDefaultIfNull(order.getNotifyUrl(), config.getNotifyUrl()));
        packageParams.put("trade_type", order.getTradeType().getTradeType());
        packageParams.put("sign", Signature.getSign(packageParams, signKey));

        //得到prepayid
        String prepayId = getSpecificKey(sendRequest(packageParams), "prepay_id");

        //签名
        String timestamp = WechatUtils.getTimeStamp();
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
    public static WeChatResponseVO generatorPrepayXcx(OrderPay order) throws PayException {
        WechatPayOrder wechatPayOrder = order.getWechatPayOrder();
        ValidationUtil.ValidResult validResult = ValidationUtil.validateBean(wechatPayOrder);
        if (validResult.hasErrors()) {
            throw new IllegalArgumentException(validResult.getErrors());
        }

        int mchNo = wechatPayOrder.getMchNo();
        Wechat config = Wechat.getInstance();

        Wechat.WechatMch wechatMch = config.getMchMap().get(mchNo);
        String mchId = wechatMch.getMchId();
        String signKey = wechatMch.getSignKey();

        String appid = config.getMchAppIdMap().get(wechatPayOrder.getMchAppIdNo());

        //封装获取prepayid
        String nonceStr = WechatUtils.getNonceStr();
        SortedMap<String, String> packageParams = new TreeMap<>();
        packageParams.put("appid", appid);
        packageParams.put("mch_id", mchId);
        packageParams.put("nonce_str", nonceStr);
        packageParams.put("body", order.getBody());
        packageParams.put("out_trade_no", order.getOrderId());
        packageParams.put("total_fee", WechatUtils.getFinalMoney(order.getPrice()));
        packageParams.put("spbill_create_ip", config.getSpbillCreateIp());
        packageParams.put("notify_url", StringUtils.getDefaultIfNull(order.getNotifyUrl(), config.getNotifyUrl()));
        packageParams.put("trade_type", order.getTradeType().getTradeType());
        packageParams.put("openid", wechatPayOrder.getOpenid());
        packageParams.put("sign", Signature.getSign(packageParams, signKey));
        //得到prepayid
        String prepayId = getSpecificKey(sendRequest(packageParams), "prepay_id");

        String timeStamp = WechatUtils.getTimeStamp();
        SortedMap<String, String> finalPackage = new TreeMap<>();
        finalPackage.put("appId", appid);
        finalPackage.put("nonceStr", nonceStr);
        finalPackage.put("package", "prepay_id=" + prepayId);
        finalPackage.put("signType", "MD5");
        finalPackage.put("timeStamp", timeStamp);

        WeChatResponseVO weChatResponseVO = new WeChatResponseVO();
        weChatResponseVO.setAppId(appid);
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
    public static String generatorPrepayScan(OrderScanPay order) throws PayException {
        WechatPayOrder wechatPayOrder = order.getWechatPayOrder();
        Wechat config = Wechat.getInstance();
        int mchNo = wechatPayOrder.getMchNo();

        Wechat.WechatMch wechatMch = config.getMchMap().get(mchNo);
        //封装获取prepayid
        String nonceStr = WechatUtils.getNonceStr();
        SortedMap<String, String> packageParams = new TreeMap<>();
        packageParams.put("appid", config.getMchAppIdMap().get(wechatPayOrder.getMchAppIdNo()));
        packageParams.put("mch_id", wechatMch.getMchId());
        packageParams.put("nonce_str", nonceStr);
        packageParams.put("body", order.getBody());
        packageParams.put("out_trade_no", order.getOrderId());
        packageParams.put("total_fee", WechatUtils.getFinalMoney(order.getPrice()));
        packageParams.put("spbill_create_ip", config.getSpbillCreateIp());
        packageParams.put("notify_url", StringUtils.getDefaultIfNull(order.getNotifyUrl(), config.getNotifyUrl()));
        packageParams.put("trade_type", order.getTradeType().getTradeType());

        if (StringUtils.isNotEmpty(order.getStoreId())) {
            packageParams.put("scene_info", "{\"store_info\" : {\n" +
                    "\"id\": \"" + order.getStoreId() + "\" }}");
        }

        packageParams.put("sign", Signature.getSign(packageParams, wechatMch.getSignKey()));
        //得到prepayid，qr_code
        return getSpecificKey(sendRequest(packageParams), "code_url");
    }

    /**
     * 获取prepayId
     *
     * @param map map
     * @throws PayException e
     */
    private static Map<String, String> sendRequest(SortedMap<String, String> map) throws PayException {
        String params = XmlUtils.mapToXml(map);
        HttpParams httpParams = HttpParams.builder().url(REQUESTURL).strEntity(params).build();
        Map<String, String> responseMap;

        try {
            responseMap = HttpClientUtils.doPostWithResponseHandler(httpParams, new MapStringStringResponseHandler());
        } catch (IOException | HttpException e) {
            log.error("预支付失败，params:{}", params);
            log.error("预支付失败", e);
            throw new PayException(ErrorCode.PRE_ORDER_FAIL);
        }

        PayResponse<Map<String, String>> payResponse = WechatResponseHandlerBase.getInstance().handler(responseMap, null);

        if (!PayBaseConstants.RETURN_SUCCESS.equals(payResponse.getResultCode())) {
            throw new PayException(ErrorCode.PRE_ORDER_FAIL);
        }

        return responseMap;
    }

    /**
     * @param responseMap 微信返回信息
     * @param key         需要获取的key
     * @return 如果存在，返回key对应的value
     * @throws PayException e
     */
    private static String getSpecificKey(Map<String, String> responseMap, String key) throws PayException {
        if (responseMap.containsKey(key)) {
            return responseMap.get(key);
        } else {
            throw new PayException(ErrorCode.PRE_ORDER_FAIL);
        }
    }
}
