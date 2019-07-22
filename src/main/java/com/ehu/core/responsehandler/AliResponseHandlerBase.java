package com.ehu.core.responsehandler;

import com.alipay.api.AlipayResponse;
import com.ehu.bean.PayResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author AlanSun
 * @date 2019/7/15 18:36
 **/
@Slf4j
public class AliResponseHandlerBase extends AliResponseHandlerAbstract<AlipayResponse, Object, AlipayResponse> {
    private AliResponseHandlerBase() {
    }

    private static AliResponseHandlerBase aliResponseHandlerBase = new AliResponseHandlerBase();

    public static AliResponseHandlerBase getInstance() {
        return aliResponseHandlerBase;
    }

    @Override
    protected void customResponse(PayResponse<AlipayResponse> payResponse, AlipayResponse alipayResponse, Object param) {
        if (!alipayResponse.isSuccess()) {
            payResponse.setResultCode(alipayResponse.getSubCode());
            payResponse.setResultMessage(alipayResponse.getSubMsg());
            payResponse.setData(alipayResponse);
        }
    }
}
