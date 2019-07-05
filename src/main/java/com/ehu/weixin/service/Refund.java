package com.ehu.weixin.service;

import com.alan344happyframework.util.HttpClientUtils;
import com.alan344happyframework.util.XmlUtils;
import com.alan344happyframework.util.bean.HttpParams;
import com.ehu.bean.OrderRefund;
import com.ehu.bean.TradeTypeEnum;
import com.ehu.config.Wechat;
import com.ehu.core.httpresponsehandler.MapStringStringResponseHandler;
import com.ehu.exception.PayException;
import com.ehu.weixin.entity.WeChatRefundInfo;
import com.ehu.weixin.util.Signature;
import com.ehu.weixin.util.WechatUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpException;

import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;


/**
 * 20160429
 *
 * @author AlanSun
 */
@Slf4j
public class Refund {

    private static final String REQUEST_URL = "https://api.mch.weixin.qq.com/secapi/pay/refund";

    /**
     * app支付退款
     *
     * @param params {@link WeChatRefundInfo}
     * @return true：退款成功
     * @throws PayException e
     */
    @SuppressWarnings("unchecked")
    public static Map<String, String> weChatRefund(OrderRefund params) throws PayException {
        Wechat config = Wechat.getInstance();
        WeChatRefundInfo weChatRefundInfo = params.getWeChatRefundInfo();

        String orderMoney = WechatUtils.getFinalMoney(params.getTotalAmount());
        String refundMoney = WechatUtils.getFinalMoney(params.getRefundAmount());

        while (orderMoney.startsWith("0")) {
            orderMoney = orderMoney.substring(1);
        }
        while (refundMoney.startsWith("0")) {
            refundMoney = refundMoney.substring(1);
        }

        Wechat.WechatMch wechatMch = config.getMchMap().get(weChatRefundInfo.getMchNo());

        SortedMap<String, String> packageParams = new TreeMap<>();
        if (TradeTypeEnum.APPLET.equals(params.getTradeType())) {
            packageParams.put("appid", config.getAppletsAppId());
        } else if (TradeTypeEnum.APP.equals(params.getTradeType())) {
            packageParams.put("appid", config.getMchAppIdMap().get(weChatRefundInfo.getMchAppIdNo()));
        }
        packageParams.put("mch_id", wechatMch.getMchId());
        packageParams.put("nonce_str", WechatUtils.getNonceStr());
        packageParams.put("out_trade_no", params.getOrderId());
        //商户系统内部的退款单号，商户系统内部唯一，同一退款单号多次请求只退一笔
        packageParams.put("out_refund_no", params.getRefundId());
        packageParams.put("total_fee", orderMoney);
        packageParams.put("refund_fee", refundMoney);
        packageParams.put("op_user_id", wechatMch.getMchId());
        packageParams.put("sign", Signature.getSign(packageParams, wechatMch.getSignKey()));
        return sendRequest(packageParams, wechatMch);
    }

    private static Map<String, String> sendRequest(SortedMap<String, String> packageParams, Wechat.WechatMch wechatMch) {
        HttpParams httpParams = HttpParams.builder().url(REQUEST_URL).strEntity(XmlUtils.mapToXml(packageParams)).build();
        try {
            return HttpClientUtils.doPostWithSslAndResponseHandler(wechatMch.getCa(), wechatMch.getCaCode(), httpParams, new MapStringStringResponseHandler());
        } catch (IOException | HttpException e) {
            if (log.isDebugEnabled()) {
                log.debug("退款失败", e);
            }
            throw new PayException("退款失败");
        }
    }
}
