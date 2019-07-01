package com.ehu.callback;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author AlanSun
 * @date 2019/7/1 16:45
 **/
public class CallbackContext {

    private CallbackHandler callbackHandler;

    public CallbackContext(CallbackHandler callbackHandler) {
        this.callbackHandler = callbackHandler;
    }

    public void handler(HttpServletRequest request, HttpServletResponse response, boolean isVerify, ConcretePayService concretePayService) {
        this.callbackHandler.handler(request, response, isVerify, concretePayService);
    }
}
