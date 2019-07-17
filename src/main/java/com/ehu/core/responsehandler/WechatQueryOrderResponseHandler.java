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
    void customService(PayResponse<Object> payResponse, Map<String, String> responseMap, OrderQuery param) {
        String queryFlag = param.getQueryFlag();
        String resultCode = responseMap.get("result_code");
        if (PayBaseConstants.RETURN_SUCCESS.equals(resultCode)) {
            if (StringUtils.isEmpty(queryFlag)) {
                payResponse.setData(responseMap.get("trade_state"));
            } else {
                String tradeState = responseMap.get("trade_state");
                if (PayBaseConstants.TRADE_STATE_SUCCESS.equals(queryFlag)) {
                    if (PayBaseConstants.TRADE_STATE_SUCCESS.equals(tradeState)) {
                        payResponse.setData(true);
                    } else {
                        WechatQueryOrderResponseHandler.checkTradeState(tradeState);
                    }
                }
            }
        }
    }

    private static void checkTradeState(String tradeState) throws PayException {
        if (PayBaseConstants.TRADE_STATE_NOTPAY.equals(tradeState)) {
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
