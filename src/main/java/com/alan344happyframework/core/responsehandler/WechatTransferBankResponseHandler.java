package com.alan344happyframework.core.responsehandler;

import com.alan344happyframework.bean.PayResponse;
import com.alan344happyframework.constants.PayBaseConstants;
import com.alan344happyframework.core.TransferAccountsUtils;
import com.alan344happyframework.exception.PayException;
import com.alan344happyframework.weixin.entity.TransferToBankCardParams;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author AlanSun
 * @date 2019/7/5 16:04
 * <p>
 * 处理查询转账到零钱的结果
 **/
@Slf4j
public class WechatTransferBankResponseHandler extends WechatResponseHandlerAbstract<TransferToBankCardParams> {
    private WechatTransferBankResponseHandler() {
    }

    private static WechatTransferBankResponseHandler wechatResponseHandlerBase = new WechatTransferBankResponseHandler();

    public static WechatTransferBankResponseHandler getInstance() {
        return wechatResponseHandlerBase;
    }

    /**
     * result_code == FAIL时的处理
     */
    @Override
    void errorCustomService(PayResponse<Map<String, String>> payResponse, Map<String, String> response, TransferToBankCardParams param) {
        String err_code = response.get("err_code");
        if ("SYSTEMERROR".equals(err_code) || "ORDERPAID".equals(err_code) || "FATAL_ERROR".equals(err_code)) {
            try {
                PayResponse resultOfTransferToBank = TransferAccountsUtils.getResultOfTransferToBank(param.getPartnerTradeNo());
                String queryResult = resultOfTransferToBank.getResultCode();
                if (PayBaseConstants.RETURN_FAIL.equals(queryResult)) {
                    payResponse.setResultCode(PayBaseConstants.PROCESSING);
                } else if (PayBaseConstants.ORDER_NOT_EXIST.equals(queryResult)) {
                    payResponse.setResultCode(PayBaseConstants.RETRY);
                } else {
                    payResponse.setResultCode(queryResult);
                }
            } catch (PayException e) {
                log.error("error", e);
            }
        } else if ("INVALID_REQUEST".equals(err_code)) {
            payResponse.setResultCode(PayBaseConstants.RETRY);
        }
    }
}
