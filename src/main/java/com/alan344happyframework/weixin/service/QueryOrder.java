package com.alan344happyframework.weixin.service;

import com.alan344happyframework.util.HttpClientUtils;
import com.alan344happyframework.util.XmlUtils;
import com.alan344happyframework.util.bean.HttpParams;
import com.alan344happyframework.bean.OrderQuery;
import com.alan344happyframework.bean.PayResponse;
import com.alan344happyframework.config.Wechat;
import com.alan344happyframework.core.httpresponsehandler.MapStringStringResponseHandler;
import com.alan344happyframework.core.responsehandler.WechatQueryOrderResponseHandler;
import com.alan344happyframework.exception.PayException;
import com.alan344happyframework.weixin.entity.WeChatOrderQuery;
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
 * 2016-08-10
 * 微信查询订单类
 */
@Slf4j
public class QueryOrder {
    private static final String REQUEST_URL = "https://api.mch.weixin.qq.com/pay/orderquery";

    /**
     * 查询微信支付是否成功
     *
     * @return String or Boolean
     * @throws PayException queryFlag != null and 订单状态 != queryFlag
     */
    @SuppressWarnings("unchecked")
    public static PayResponse getQueryResult(OrderQuery params) throws PayException {
        Wechat config = Wechat.getInstance();

        WeChatOrderQuery weChatOrderQuery = params.getWeChatOrderQuery();
        Wechat.WechatMch wechatMch = config.getMchMap().get(weChatOrderQuery.getMchNo());

        String nonce_str = WechatUtils.getNonceStr();
        SortedMap<String, String> packageParams = new TreeMap<>();
        packageParams.put("appid", config.getMchAppIdMap().get(weChatOrderQuery.getMchAppIdNo()));
        packageParams.put("mch_id", wechatMch.getMchId());
        packageParams.put("nonce_str", nonce_str);
        packageParams.put("out_trade_no", params.getOrderId());
        packageParams.put("sign", Signature.getSign(packageParams, wechatMch.getSignKey()));

        Map<String, String> resultMap = sendRequest(packageParams);
        return WechatQueryOrderResponseHandler.getInstance().handler(resultMap, params);
    }

    /**
     * 发送请求
     */
    private static Map<String, String> sendRequest(SortedMap<String, String> packageParams) throws PayException {
        HttpParams httpParams = HttpParams.builder().url(REQUEST_URL).strEntity(XmlUtils.mapToXml(packageParams)).build();
        try {
            return HttpClientUtils.doPostWithResponseHandler(httpParams, new MapStringStringResponseHandler());
        } catch (IOException | HttpException e) {
            log.error("查询订单信息失败", e);
            throw new PayException("查询订单信息失败");
        }
    }
}
