package com.ehu.core;

import com.alan344happyframework.constants.BaseConstants;
import com.ehu.bean.*;
import com.ehu.exception.PayException;

/**
 * @author AlanSun
 * @date 2019/7/4 16:06
 * <p>
 * 支付调用的入口
 * <p>
 * payType是必填的，默认时支付宝
 * <p>
 * tradeType默认为APP
 **/
public class PayUtils {

    /**
     * 生成支付信息，预支付订单
     */
    public static PayInfoResponse createPayInfo(OrderPay orderPay) throws PayException {
        return PayIntegrate.getPay(orderPay.getPayType()).createPayInfo(orderPay);
    }

    /**
     * 扫码支付，获取二维码，一般用在自动售货机
     */
    public static String getQrCode(OrderScanPay orderScanPay) throws PayException {
        return PayIntegrate.getPay(orderScanPay.getPayType()).getQrCode(orderScanPay);
    }

    /**
     * 查询订单状态
     */
    public static PayResponse queryOrder(OrderQuery orderQuery) throws PayException {
        return PayIntegrate.getPay(orderQuery.getPayType()).queryOrder(orderQuery);
    }

    /**
     * 查询订单是否支付成功
     */
    public static void checkOrderIsPaySuccess(OrderQuery orderQuery) throws PayException {
        orderQuery.setQueryFlag(BaseConstants.SUCCESS);
        PayIntegrate.getPay(orderQuery.getPayType()).queryOrder(orderQuery);
    }

    /**
     * 退款
     * <p>
     * 支持小程序，app退款
     */
    public static PayResponse refund(OrderRefund refundOrder) throws PayException {
        return PayIntegrate.getPay(refundOrder.getPayType()).refund(refundOrder);
    }

    /**
     * 获取财务报告
     */
    public PayResponse getFinancial(FinancialReport financialReport) throws PayException {
        return PayIntegrate.getPay(financialReport.getPayType()).getFinancial(financialReport);
    }
}
