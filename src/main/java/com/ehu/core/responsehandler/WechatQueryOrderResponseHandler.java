package com.ehu.core.responsehandler;

import com.alan344happyframework.util.StringUtils;
import com.ehu.bean.OrderQuery;
import com.ehu.bean.PayResponse;
import com.ehu.constants.PayBaseConstants;
import com.ehu.constants.PayResultCodeConstants;
import com.ehu.constants.PayResultMessageConstants;
import com.ehu.exception.PayException;
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

    private static void checkTradeState(String tradeState) throws PayException {
        switch (tradeState) {
            case TRADE_STATE_NOTPAY:
                throw new PayException(PayResultCodeConstants.TRADE_STATE_NOTPAY_30006, PayResultMessageConstants.TRADE_STATE_NOTPAY_30006);
            case TRADE_STATE_REFUND:
                throw new PayException(PayResultCodeConstants.TRADE_STATE_REFUND_30008, PayResultMessageConstants.TRADE_STATE_REFUND_30008);
            case TRADE_STATE_CLOSED:
                throw new PayException(PayResultCodeConstants.TRADE_STATE_CLOSED_30009, PayResultMessageConstants.TRADE_STATE_CLOSED_30009);
            case TRADE_STATE_USERPAYING:
                throw new PayException(PayResultCodeConstants.TRADE_STATE_USERPAYING_30010, PayResultMessageConstants.TRADE_STATE_USERPAYING_30010);
            case TRADE_STATE_PAYERROR:
                throw new PayException(PayResultCodeConstants.TRADE_STATE_PAYERROR_30011, PayResultMessageConstants.TRADE_STATE_PAYERROR_30011);
            case TRADE_STATE_REVOKED:
                throw new PayException(PayResultCodeConstants.TRADE_STATE_REVOKED_30012, PayResultMessageConstants.TRADE_STATE_REVOKED_30012);
            default:
                throw new PayException(PayResultMessageConstants.STRING_WECHATPAY_10003);
        }
    }
}
