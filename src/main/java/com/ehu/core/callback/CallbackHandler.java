package com.ehu.core.callback;

import com.ehu.core.ConcretePayService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author 53479
 * @date 2019/7/1 16:44
 **/
public interface CallbackHandler {
    CallBackParam getCallBackParam(Map<String, String> params);

    void handler(HttpServletRequest request, HttpServletResponse response, boolean isVerify, ConcretePayService concretePayService);
}
