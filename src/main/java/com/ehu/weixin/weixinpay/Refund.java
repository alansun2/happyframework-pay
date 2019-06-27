package com.ehu.weixin.weixinpay;

import com.ehu.config.Wechat;
import com.ehu.exception.PayException;
import com.ehu.weixin.entity.WeChatRefundInfo;
import com.ehu.weixin.util.Signature;
import com.ehu.weixin.util.WeChatUtils;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;


/**
 * 20160429
 *
 * @author AlanSun
 */
public class Refund {

    private static final String requestUrl = "https://api.mch.weixin.qq.com/secapi/pay/refund";

    /**
     * app支付退款
     *
     * @param params {@link WeChatRefundInfo}
     * @return true：退款成功
     * @throws PayException e
     */
    @SuppressWarnings("unchecked")
    public static boolean weChatRefundOper(WeChatRefundInfo params) throws PayException {
        Wechat config = Wechat.getInstance();

        String orderMoney = WeChatUtils.getFinalMoney(params.getTotalFee());
        String refundMoney = WeChatUtils.getFinalMoney(params.getRefundFee());

        while (orderMoney.startsWith("0")) {
            orderMoney = orderMoney.substring(1);
        }
        while (refundMoney.startsWith("0")) {
            refundMoney = refundMoney.substring(1);
        }

        Wechat.WechatMch wechatMch = config.getMchMap().get(params.getMchNo());

        SortedMap<String, String> packageParams = new TreeMap<>();
        packageParams.put("appid", config.getAppId());
        packageParams.put("mch_id", wechatMch.getMchId());
        packageParams.put("nonce_str", WeChatUtils.getNonceStr());
        packageParams.put("out_trade_no", params.getOutTradeNo());
        //商户系统内部的退款单号，商户系统内部唯一，同一退款单号多次请求只退一笔
        packageParams.put("out_refund_no", params.getOutRefundNo());
        packageParams.put("total_fee", orderMoney);
        packageParams.put("refund_fee", refundMoney);
        packageParams.put("op_user_id", wechatMch.getMchId());
        packageParams.put("sign", Signature.getSign(packageParams, wechatMch.getSignKey()));
        Map<String, String> map = WeChatUtils.wechatPostWithSSL(packageParams, requestUrl, wechatMch.getCa(), wechatMch.getCaCode());

        return WeChatUtils.wechatResponseHandler(map);
    }

    /**
     * 小程序退款
     *
     * @param params {@link WeChatRefundInfo}
     * @return true：退款成功
     * @throws PayException e
     */
    @SuppressWarnings("unchecked")
    public static boolean weChatRefundOperXcx(WeChatRefundInfo params) throws PayException {
        Wechat config = Wechat.getInstance();
        String orderMoney = WeChatUtils.getFinalMoney(params.getTotalFee());
        String refundMoney = WeChatUtils.getFinalMoney(params.getRefundFee());

        Wechat.WechatMch wechatMch = config.getMchMap().get(params.getMchNo());

        SortedMap<String, String> packageParams = new TreeMap<>();
        packageParams.put("appid", config.getAppletsAppId());
        packageParams.put("mch_id", wechatMch.getMchId());
        packageParams.put("nonce_str", WeChatUtils.getNonceStr());
        packageParams.put("out_trade_no", params.getOutTradeNo());
        packageParams.put("out_refund_no", params.getOutRefundNo());//商户系统内部的退款单号，商户系统内部唯一，同一退款单号多次请求只退一笔
        packageParams.put("total_fee", orderMoney);
        packageParams.put("refund_fee", refundMoney);
        packageParams.put("op_user_id", wechatMch.getMchId());
        packageParams.put("sign", Signature.getSign(packageParams, wechatMch.getSignKey()));
        Map<String, String> map = WeChatUtils.wechatPostWithSSL(packageParams, requestUrl, wechatMch.getCa(), wechatMch.getCaCode());

        return WeChatUtils.wechatResponseHandler(map);
    }
}
