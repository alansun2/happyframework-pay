package com.ehu.core;

import com.ehu.bean.*;
import com.ehu.exception.PayException;

/**
 * @author 53479
 * @date 2019/7/4 18:02
 **/
public interface Pay {
    /**
     * 创建支付信息
     *
     * @param order 订单
     * @return 支付信息
     */
    PayInfoResponse createPayInfo(OrderPay order) throws PayException;

    /**
     * 二维码扫码支付
     *
     * @return 二维码url
     */
    String getQrCode(OrderScanPay orderScanPay) throws PayException;

    /**
     * 订单查询
     */
    PayResponse queryOrder(OrderQuery orderQuery) throws PayException;

    /**
     * 退款
     *
     * @param refundOrder {@link OrderRefund}
     * @return {@link PayResponse}
     */
    PayResponse refund(OrderRefund refundOrder) throws PayException;

    /**
     * 第三方内部转账
     *
     * @param params {@link TransferMoneyInternal}
     * @return {@link PayResponse}
     */
    PayResponse transferMoneyInternal(TransferMoneyInternal params) throws PayException;

    /**
     * 获取财务报告
     *
     * @param financialReport {@link FinancialReport}
     * @return {@link PayResponse}
     */
    PayResponse getFinancial(FinancialReport financialReport) throws PayException;
}
