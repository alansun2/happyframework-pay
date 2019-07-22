package com.ehu.core;

import com.ehu.bean.PayResponse;
import com.ehu.bean.TransferMoneyInternal;
import com.ehu.exception.PayException;
import com.ehu.weixin.entity.TransferToBankCardParams;

/**
 * @author 53479
 * @date 2019/7/22 16:27
 **/
public interface TransferAccounts {
    /**
     * 第三方内部转账
     *
     * @param params {@link TransferMoneyInternal}
     * @return {@link PayResponse}
     */
    PayResponse transferMoneyInternal(TransferMoneyInternal params) throws PayException;

    /**
     * 转账到银行卡
     *
     * @param params {@link TransferToBankCardParams}
     * @return {@link PayResponse}
     */
    PayResponse transferToBankCard(TransferToBankCardParams params) throws PayException;

    /**
     * 查询转账到银行卡的结果
     *
     * @param orderId 订单id
     * @return {@link PayResponse}
     */
    PayResponse getResultOfTransferToBank(String orderId) throws PayException;
}
