package com.ehu.core.callback;

/**
 * @author 53479
 * @date 2019/7/1 16:47
 **/
public interface ConcretePayService {
    /**
     * 处理具体的支付业务
     *
     * @param callBackParam 参数
     */
    void handler(CallBackParam callBackParam);
}
