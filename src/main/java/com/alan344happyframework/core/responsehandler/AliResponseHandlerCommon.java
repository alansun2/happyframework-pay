package com.alan344happyframework.core.responsehandler;

import com.alipay.api.AlipayResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author AlanSun
 * @date 2019/7/15 18:36
 **/
@Slf4j
public class AliResponseHandlerCommon extends AliResponseHandlerAbstract<AlipayResponse, Object, AlipayResponse> {
    private AliResponseHandlerCommon() {
    }

    private static AliResponseHandlerCommon aliResponseHandlerCommon = new AliResponseHandlerCommon();

    public static AliResponseHandlerCommon getInstance() {
        return aliResponseHandlerCommon;
    }


}
