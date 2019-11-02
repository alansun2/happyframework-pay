package com.alan344happyframework.core.responsehandler;

import com.alipay.api.AlipayResponse;
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
}
