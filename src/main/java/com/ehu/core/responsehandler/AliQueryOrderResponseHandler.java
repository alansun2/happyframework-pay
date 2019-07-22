package com.ehu.core.responsehandler;

import com.alan344happyframework.util.StringUtils;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.ehu.bean.OrderQuery;
import com.ehu.bean.PayResponse;
import com.ehu.constants.PayResultCodeConstants;
import com.ehu.constants.PayResultMessageConstants;
import com.ehu.exception.PayException;

/**
 * @author AlanSun
 * @date 2019/7/15 17:19
 **/
public class AliQueryOrderResponseHandler extends AliExceptionResponseHandlerAbstract<AlipayTradeQueryResponse, OrderQuery, Object> {
    private AliQueryOrderResponseHandler() {
    }

    private static AliQueryOrderResponseHandler aliResponseHandlerAbstract = new AliQueryOrderResponseHandler();

    public static AliQueryOrderResponseHandler getInstance() {
        return aliResponseHandlerAbstract;
    }

    @Override
    protected void customResponse(PayResponse<Object> payResponse, AlipayTradeQueryResponse alipayResponse, OrderQuery param) throws PayException {
        if (!alipayResponse.isSuccess()) {
            if ("ACQ.TRADE_NOT_EXIST".equals(alipayResponse.getSubCode())) {
                throw new PayException(PayResultCodeConstants.TRADE_NOT_EXIST_30005, PayResultMessageConstants.TRADE_NOT_EXIST_30005);
            } else {
                throw new PayException(PayResultMessageConstants.ALI_SERVER_ERROR);
            }
        } else {
            if (StringUtils.isEmpty(param.getQueryFlag())) {
                payResponse.setData(alipayResponse.getTradeStatus());
            } else {
                String tradeStatus = alipayResponse.getTradeStatus();
                if (!TRADE_SUCCESS.equals(tradeStatus)) {
                    checkTradeState(alipayResponse.getTradeStatus());
                }
            }
        }
    }

    /**
     * 支付宝交易成功
     */
    private static final String TRADE_SUCCESS = "TRADE_SUCCESS";
    private static final String WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
    private static final String TRADE_CLOSED = "TRADE_CLOSED";
    private static final String TRADE_FINISHED = "TRADE_FINISHED";

    private static void checkTradeState(String tradeStatus) throws PayException {
        switch (tradeStatus) {
            case WAIT_BUYER_PAY:
                throw new PayException("订单未支付，等待支付");
            case TRADE_CLOSED:
                throw new PayException("付款超时，交易已关闭，请重新下单");
            case TRADE_FINISHED:
                throw new PayException("交易已结束");
            default:
                throw new PayException(PayResultMessageConstants.ALI_SERVER_ERROR);
        }
    }
}
