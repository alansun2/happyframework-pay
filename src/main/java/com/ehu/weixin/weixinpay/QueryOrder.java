package com.ehu.weixin.weixinpay;

import com.ehu.constants.PayBaseConstants;
import com.ehu.constants.PayResultCodeConstants;
import com.ehu.constants.PayResultMessageConstants;
import com.ehu.exception.PayException;
import com.ehu.config.EhPayConfig;
import com.ehu.util.StringUtils;
import com.ehu.weixin.util.Signature;
import com.ehu.weixin.util.WeChatUtils;
import lombok.extern.slf4j.Slf4j;

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
    static final String requestUrl = "https://api.mch.weixin.qq.com/pay/orderquery";

    @SuppressWarnings("unchecked")
    public static Object getQuertResult(String out_trade_no, String queryFlag) throws PayException {
        EhPayConfig config = EhPayConfig.getInstance();

        String nonce_str = WeChatUtils.getNonceStr();
        SortedMap<String, String> packageParams = new TreeMap<String, String>();
        packageParams.put("appid", config.getWxPay_appid());
        packageParams.put("mch_id", config.getWxPay_mch_id());
        packageParams.put("nonce_str", nonce_str);
        packageParams.put("out_trade_no", out_trade_no);
        packageParams.put("sign", Signature.getSign(packageParams));
        Map<String, String> resultMap = WeChatUtils.getResponseInfo(packageParams, requestUrl);
        return judgeOrderState(resultMap, queryFlag);
    }

    /**
     * 判断订单状态
     *
     * @param resultMap
     * @param queryFlag
     * @return
     * @throws PayException
     */
    private static Object judgeOrderState(Map<String, String> resultMap, final String queryFlag) throws PayException {
        if (resultMap.containsKey("return_code") && PayBaseConstants.RETURN_SUCCESS.equals(resultMap.get("return_code"))) {
            if (resultMap.containsKey("result_code") && PayBaseConstants.RETURN_SUCCESS.equals(resultMap.get("result_code"))) {
                String resultCode = resultMap.get("result_code");
                if (PayBaseConstants.RETURN_SUCCESS.equals(resultCode)) {
                    if (StringUtils.isBlank(queryFlag)) {
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
