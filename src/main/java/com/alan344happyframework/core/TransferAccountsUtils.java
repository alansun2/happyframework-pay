package com.alan344happyframework.core;

import com.alan344happyframework.bean.PayBase;
import com.alan344happyframework.bean.PayResponse;
import com.alan344happyframework.bean.QueryTransferMoneyInternal;
import com.alan344happyframework.bean.TransferMoneyInternal;
import com.alan344happyframework.exception.PayException;
import com.alan344happyframework.weixin.entity.TransferToBankCardParams;

/**
 * @author AlanSun
 * @date 2019/7/22 16:50
 **/
public class TransferAccountsUtils {

    /**
     * 第三方内部转账，如果是支付宝就转账至余额，如果是微信就转账至零钱
     *
     * @return 如果是微信：SUCCESS：转账成功，PROCESSING：处理中，需要后续处理；其他：失败，需要后续处理
     */
    public static PayResponse transferMoneyInternal(TransferMoneyInternal params) throws PayException {
        return PayIntegrate.getPay(params.getPayType()).transferMoneyInternal(params);
    }

    /**
     * 查询内部转账的结果
     *
     * @return 如果是微信：SUCCESS：转账成功，PROCESSING：处理中，需要后续处理；其他：失败，需要后续处理
     */
    public static PayResponse getResultOfTransferMoneyInternal(QueryTransferMoneyInternal params) throws PayException {
        return PayIntegrate.getPay(params.getPayType()).getResultOfTransferMoneyInternal(params);
    }

    /**
     * 转账到银行卡
     * <p>
     * 支付宝未开放该接口
     *
     * @return 如果是微信：
     * SUCCESS：微信受理成功，转账是否成功还要看银行方面的具体情况；
     * PROCESSING：处理中，需要后续处理；
     * BANK_FAIL：银行退票；
     * FAILED：付款失败,需要替换付款单号重新发起付款；
     * MANUAL：需要人工处理
     * FAIL：微信受理失败，需要重试；
     */
    public static PayResponse transferToBankCard(TransferToBankCardParams params) throws PayException {
        return PayIntegrate.getPay(PayBase.PAY_TYPE_2).transferToBankCard(params);
    }

    /**
     * 查询转账到银行卡的结果
     * <p>
     * 支付宝未开放该接口
     *
     * @return 如果是微信：
     * SUCCESS：转账成功；
     * PROCESSING：处理中，需要后续处理；
     * BANK_FAIL：银行退票；
     * FAILED：付款失败,需要替换付款单号重新发起付款；
     * MANUAL：需要人工处理
     * FAIL：查询失败，需要重试；
     */
    public static PayResponse getResultOfTransferToBank(String orderId) throws PayException {
        return PayIntegrate.getPay(PayBase.PAY_TYPE_2).getResultOfTransferToBank(orderId);
    }
}
