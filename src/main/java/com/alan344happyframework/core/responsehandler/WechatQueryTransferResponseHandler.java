package com.alan344happyframework.core.responsehandler;

import com.alan344happyframework.bean.PayResponse;
import com.alan344happyframework.constants.PayBaseConstants;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author AlanSun
 * @date 2019/7/5 16:04
 * <p>
 * 处理查询转账到零钱的结果
 **/
@Slf4j
public class WechatQueryTransferResponseHandler extends WechatResponseHandlerAbstract<Object> {
    private WechatQueryTransferResponseHandler() {
    }

    private static WechatQueryTransferResponseHandler wechatResponseHandlerBase = new WechatQueryTransferResponseHandler();

    public static WechatQueryTransferResponseHandler getInstance() {
        return wechatResponseHandlerBase;
    }

    @Override
    void customService(PayResponse<Map<String, String>> payResponse, Map<String, String> response, Object param) {
        String status = response.get("status");
        payResponse.setResultCode(status);
    }

    @Override
    void errorCustomService(PayResponse<Map<String, String>> payResponse, Map<String, String> response, Object param) {
        String err_code = response.get("err_code");
        if ("ORDERNOTEXIST".equals(err_code)) {
            payResponse.setResultCode(PayBaseConstants.ORDER_NOT_EXIST);
        } else if ("NOT_FOUND".equals(err_code)) {
            payResponse.setResultCode(PayBaseConstants.MANUAL);
        } else if ("SYSTEMERROR".equals(err_code) || "INVALID_REQUEST".equals(err_code)) {
            payResponse.setResultCode(PayBaseConstants.RETRY);
        }
    }
}
