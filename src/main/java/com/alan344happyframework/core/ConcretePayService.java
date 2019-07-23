package com.alan344happyframework.core;

import com.alan344happyframework.core.callback.CallBackParam;

/**
 * @author 53479
 * @date 2019/7/1 16:47
 * <p>
 * 具体回调业务处理类
 **/
public interface ConcretePayService {
    /**
     * 处理具体的支付业务
     *
     * @param callBackParam 参数
     */
    void handler(CallBackParam callBackParam) throws Exception;
}
