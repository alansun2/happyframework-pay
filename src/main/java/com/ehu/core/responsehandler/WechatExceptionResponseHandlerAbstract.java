package com.ehu.core.responsehandler;

import com.ehu.bean.PayResponse;
import com.ehu.constants.PayBaseConstants;
import com.ehu.constants.PayResultMessageConstants;
import com.ehu.exception.PayException;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author AlanSun
 * @date 2019/7/16 19:54
 **/
@Slf4j
public abstract class WechatExceptionResponseHandlerAbstract<P, R> implements ResponseHandler<Map<String, String>, P, R> {

    @Override
    public PayResponse<R> handler(Map<String, String> responseMap, P params) {
        PayResponse<R> payResponse = new PayResponse<>();
        if (null == responseMap || responseMap.isEmpty()) {
            throw new PayException(PayResultMessageConstants.STRING_WECHATPAY_10003);
        }

        if (responseMap.containsKey("return_code") && PayBaseConstants.RETURN_SUCCESS.equals(responseMap.get("return_code"))) {
            if (responseMap.containsKey("result_code") && PayBaseConstants.RETURN_SUCCESS.equals(responseMap.get("result_code"))) {
                //业务成功
                this.customService(payResponse, responseMap, params);
            } else {
                log.error("业务错误, 返回信息：{}", responseMap);
                throw new PayException(PayResultMessageConstants.STRING_WECHATPAY_10003);
            }
        } else if (responseMap.containsKey("return_code")) {
            log.error("业务错误, 返回信息：{}", responseMap);
            throw new PayException(PayResultMessageConstants.STRING_WECHATPAY_10003);
        } else {
            throw new PayException(PayResultMessageConstants.STRING_WECHATPAY_10003);
        }
        return payResponse;
    }

    abstract void customService(PayResponse<R> payResponse, Map<String, String> response, P param);
}
