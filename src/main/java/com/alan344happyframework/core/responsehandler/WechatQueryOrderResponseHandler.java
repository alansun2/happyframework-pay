package com.alan344happyframework.core.responsehandler;

import com.alan344happyframework.util.StringUtils;
import com.alan344happyframework.bean.OrderQuery;
import com.alan344happyframework.bean.PayResponse;
import com.alan344happyframework.constants.ErrorCode;
import com.alan344happyframework.constants.PayBaseConstants;
import com.alan344happyframework.exception.PayException;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author AlanSun
 * @date 2019/7/5 16:04
 **/
@Slf4j
public class WechatQueryOrderResponseHandler extends WechatExceptionResponseHandlerAbstract<OrderQuery, Object> {
    private WechatQueryOrderResponseHandler() {
    }

    private static WechatQueryOrderResponseHandler wechatResponseHandler = new WechatQueryOrderResponseHandler();

    public static WechatQueryOrderResponseHandler getInstance() {
        return wechatResponseHandler;
    }

    @Override
    void customService(PayResponse<Object> payResponse, Map<String, String> responseMap, OrderQuery param) throws PayException {
        String resultCode = responseMap.get("result_code");
        switch (resultCode) {
            case PayBaseConstants.RETURN_SUCCESS:
                String queryFlag = param.getQueryFlag();
                if (StringUtils.isEmpty(queryFlag)) {
                    payResponse.setData(responseMap.get("trade_state"));
                } else {
                    String tradeState = responseMap.get("trade_state");
                    if (!TRADE_STATE_SUCCESS.equals(tradeState)) {
                        checkTradeState(tradeState);
                    }
                }
                break;
            default:
                throw new PayException(responseMap.get("err_code_des"));
        }
    }

    /**
     * trade_state:SUCCESS
     */
    private static final String TRADE_STATE_SUCCESS = "SUCCESS";
    /**
     * trade_state:REFUND
     */
    private static final String TRADE_STATE_REFUND = "REFUND";
    /**
     * trade_state:NOTPAY
     */
    private static final String TRADE_STATE_NOTPAY = "NOTPAY";
    /**
     * trade_state:CLOSED
     */
    private static final String TRADE_STATE_CLOSED = "CLOSED";
    /**
     * trade_state:REVOKED(刷卡支付)
     */
    private static final String TRADE_STATE_REVOKED = "REVOKED";
    /**
     * trade_state:USERPAYING
     */
    private static final String TRADE_STATE_USERPAYING = "USERPAYING";
    /**
     * trade_state:PAYERROR(支付失败(其他原因，如银行返回失败))
     */
    private static final String TRADE_STATE_PAYERROR = "PAYERROR";

    /**
     * 微信支付trade_state:NOTPAY(未支付)
     */
    private static final String TRADE_STATE_NOTPAY_30006 = "您还未支付";
    /**
     * 微信支付trade_state:REFUND
     */
    private static final String TRADE_STATE_REFUND_30008 = "交易退款中";
    /**
     * 微信支付trade_state:CLOSED
     */
    private static final String TRADE_STATE_CLOSED_30009 = "交易已关闭";
    /**
     * 微信支付trade_state:USERPAYING
     */
    private static final String TRADE_STATE_USERPAYING_30010 = "支付中";
    /**
     * 微信支付trade_state:PAYERROR
     */
    private static final String TRADE_STATE_PAYERROR_30011 = "支付失败";
    /**
     * 微信支付trade_state:REVOKED
     */
    private static final String TRADE_STATE_REVOKED_30012 = "支付已撤销";

    private static void checkTradeState(String tradeState) throws PayException {
        switch (tradeState) {
            case TRADE_STATE_NOTPAY:
                throw new PayException(TRADE_STATE_NOTPAY_30006, TRADE_STATE_NOTPAY_30006);
            case TRADE_STATE_REFUND:
                throw new PayException(TRADE_STATE_REFUND_30008, TRADE_STATE_REFUND_30008);
            case TRADE_STATE_CLOSED:
                throw new PayException(TRADE_STATE_CLOSED_30009, TRADE_STATE_CLOSED_30009);
            case TRADE_STATE_USERPAYING:
                throw new PayException(TRADE_STATE_USERPAYING_30010, TRADE_STATE_USERPAYING_30010);
            case TRADE_STATE_PAYERROR:
                throw new PayException(TRADE_STATE_PAYERROR_30011, TRADE_STATE_PAYERROR_30011);
            case TRADE_STATE_REVOKED:
                throw new PayException(TRADE_STATE_REVOKED_30012, TRADE_STATE_REVOKED_30012);
            default:
                throw new PayException(ErrorCode.WECHAT_SERVER_ERROR);
        }
    }
}
