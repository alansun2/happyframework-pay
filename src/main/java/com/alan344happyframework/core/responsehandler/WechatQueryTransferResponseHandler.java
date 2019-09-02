package com.alan344happyframework.core.responsehandler;

import com.alan344happyframework.bean.PayResponse;
import com.alan344happyframework.constants.BaseConstants;
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
        if (response.get("status").equals("FAILED")) {
            payResponse.setResultCode(BaseConstants.FAIL);
        }
    }
}
