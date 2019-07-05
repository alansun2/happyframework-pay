package com.ehu.weixin.service;

import com.alan344happyframework.util.HttpClientUtils;
import com.alan344happyframework.util.StringUtils;
import com.alan344happyframework.util.bean.HttpParams;
import com.ehu.config.Wechat;
import com.ehu.constants.PayBaseConstants;
import com.ehu.constants.PayResultCodeConstants;
import com.ehu.constants.PayResultMessageConstants;
import com.ehu.exception.PayException;
import com.ehu.core.httpresponsehandler.MapStringStringResponseHandler;
import com.alan344happyframework.util.XmlUtils;
import com.ehu.weixin.entity.WeChatOrderQuery;
import com.ehu.weixin.util.Signature;
import com.ehu.weixin.util.WechatUtils;
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
    public static Object getQueryResult(WeChatOrderQuery params) throws PayException {
        Wechat config = Wechat.getInstance();

        Wechat.WechatMch wechatMch = config.getMchMap().get(params.getMchNo());

        String nonce_str = WechatUtils.getNonceStr();
        SortedMap<String, String> packageParams = new TreeMap<>();
        packageParams.put("appid", config.getMchAppIdMap().get(params.getMchAppIdNo()));
        packageParams.put("mch_id", wechatMch.getMchId());
        packageParams.put("nonce_str", nonce_str);
        packageParams.put("out_trade_no", params.getOutTradeNo());
        packageParams.put("sign", Signature.getSign(packageParams, wechatMch.getSignKey()));

        Map<String, String> resultMap = sendRequest(packageParams);
        return judgeOrderState(resultMap, params.getQueryFlag());
    }

    private static Map<String, String> sendRequest(SortedMap<String, String> packageParams) {
        HttpParams httpParams = HttpParams.builder().url(REQUEST_URL).strEntity(XmlUtils.mapToXml(packageParams)).build();
        try {
            return HttpClientUtils.doPostWithResponseHandler(httpParams, new MapStringStringResponseHandler());
        } catch (IOException | HttpException e) {
            log.error("查询订单信息失败", e);
            throw new PayException("查询订单信息失败");
        }
    }

    /**
     * 判断订单状态
     *
     * @param resultMap 微信返回结果
     * @param queryFlag 如果该值不填写则返回订单状态，如果填写了该值，则会比较订单状态和该值是否相等，相等返回true，否则返回异常
     * @return String or Boolean
     * @throws PayException queryFlag != null and 订单状态 != queryFlag
     */
    private static Object judgeOrderState(Map<String, String> resultMap, final String queryFlag) throws PayException {
        if (resultMap.containsKey("return_code") && PayBaseConstants.RETURN_SUCCESS.equals(resultMap.get("return_code"))) {
            if (resultMap.containsKey("result_code") && PayBaseConstants.RETURN_SUCCESS.equals(resultMap.get("result_code"))) {
                String resultCode = resultMap.get("result_code");
                if (PayBaseConstants.RETURN_SUCCESS.equals(resultCode)) {
                    if (StringUtils.isEmpty(queryFlag)) {
                        return resultMap.get("trade_state");
                    } else {
                        String tradeState = resultMap.get("trade_state");
                        if (PayBaseConstants.TRADE_STATE_SUCCESS.equals(queryFlag)) {
                            if (PayBaseConstants.TRADE_STATE_SUCCESS.equals(tradeState)) {
                                return true;
                            } else {
                                QueryOrder.checkTradeState(tradeState);
                            }
                        }
                    }
                }
            } else {
                log.error("查询微信服务器错误" + resultMap);
                throw new PayException(PayResultCodeConstants.ERROR_CODE_WECHATPAY_10003, resultMap.get("err_code_des"));
            }
        } else {
            log.error("查询微信服务器错误");
            throw new PayException(PayResultCodeConstants.ERROR_CODE_WECHATPAY_10003, PayResultMessageConstants.STRING_WECHATPAY_10003);
        }
        return false;
    }

    private static void checkTradeState(String tradeState) throws PayException {
        if (PayBaseConstants.TRADE_STATE_SUCCESS.equals(tradeState)) {
            throw new PayException(PayResultCodeConstants.TRADE_STATE_SUCCESS_30007, PayResultMessageConstants.TRADE_STATE_SUCCESS_30007);
        } else if (PayBaseConstants.TRADE_STATE_NOTPAY.equals(tradeState)) {
            throw new PayException(PayResultCodeConstants.TRADE_STATE_NOTPAY_30006, PayResultMessageConstants.TRADE_STATE_NOTPAY_30006);
        } else if (PayBaseConstants.TRADE_STATE_REFUND.equals(tradeState)) {
            throw new PayException(PayResultCodeConstants.TRADE_STATE_REFUND_30008, PayResultMessageConstants.TRADE_STATE_REFUND_30008);
        } else if (PayBaseConstants.TRADE_STATE_CLOSED.equals(tradeState)) {
            throw new PayException(PayResultCodeConstants.TRADE_STATE_CLOSED_30009, PayResultMessageConstants.TRADE_STATE_CLOSED_30009);
        } else if (PayBaseConstants.TRADE_STATE_USERPAYING.equals(tradeState)) {
            throw new PayException(PayResultCodeConstants.TRADE_STATE_USERPAYING_30010, PayResultMessageConstants.TRADE_STATE_USERPAYING_30010);
        } else if (PayBaseConstants.TRADE_STATE_PAYERROR.equals(tradeState)) {
            throw new PayException(PayResultCodeConstants.TRADE_STATE_PAYERROR_30011, PayResultMessageConstants.TRADE_STATE_PAYERROR_30011);
        } else if (PayBaseConstants.TRADE_STATE_REVOKED.equals(tradeState)) {
            throw new PayException(PayResultCodeConstants.TRADE_STATE_REVOKED_30012, PayResultMessageConstants.TRADE_STATE_REVOKED_30012);
        }
    }
}
