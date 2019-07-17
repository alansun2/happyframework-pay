package com.ehu.core.responsehandler;

import com.alipay.api.response.AlipayTradeQueryResponse;
import com.ehu.bean.PayResponse;
import com.ehu.constants.PayResultCodeConstants;
import com.ehu.constants.PayResultMessageConstants;
import com.ehu.exception.PayException;

/**
 * @author AlanSun
 * @date 2019/7/15 17:19
 **/
public class AliQueryOrderResponseHandler extends AliResponseHandlerAbstract<AlipayTradeQueryResponse, Object, Object> {
    private AliQueryOrderResponseHandler() {
    }

    private static AliQueryOrderResponseHandler aliResponseHandlerAbstract = new AliQueryOrderResponseHandler();

    public static AliQueryOrderResponseHandler getInstance() {
        return aliResponseHandlerAbstract;
    }

    @Override
    protected void customResponse(PayResponse<Object> payResponse, AlipayTradeQueryResponse alipayResponse, Object param) {
        if (!alipayResponse.isSuccess()) {
            if ("ACQ.TRADE_NOT_EXIST".equals(alipayResponse.getSubCode())) {
                throw new PayException(PayResultCodeConstants.TRADE_NOT_EXIST_30005, PayResultMessageConstants.TRADE_NOT_EXIST_30005);
            } else {
                throw new PayException("支付宝支付错误");
            }
        } else {
            payResponse.setData(alipayResponse.getTradeStatus());
        }
    }
}
