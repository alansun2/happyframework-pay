package com.ehu.core;

import com.ehu.bean.PayBase;
import com.ehu.bean.PayResponse;
import com.ehu.bean.TransferMoneyInternal;
import com.ehu.exception.PayException;
import com.ehu.weixin.entity.TransferToBankCardParams;

/**
 * @author AlanSun
 * @date 2019/7/22 16:50
 **/
public class TransferAccountsUtils {

    /**
     * 第三方内部转账
     */
    public static PayResponse transferMoneyInternal(TransferMoneyInternal params) throws PayException {
        return PayIntegrate.getPay(params.getPayType()).transferMoneyInternal(params);
    }

    /**
     * 转账到银行卡
     */
    public static PayResponse transferToBankCard(TransferToBankCardParams params) throws PayException {
        return PayIntegrate.getPay(PayBase.PAY_TYPE_2).transferToBankCard(params);
    }

    /**
     * 查询转账到银行卡的结果
     */
    PayResponse getResultOfTransferToBank(String orderId) throws PayException {
        return PayIntegrate.getPay(PayBase.PAY_TYPE_2).getResultOfTransferToBank(orderId);
    }
}
