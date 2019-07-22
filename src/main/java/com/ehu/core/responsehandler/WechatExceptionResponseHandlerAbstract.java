package com.ehu.core.responsehandler;

import com.ehu.bean.PayResponse;
import com.ehu.constants.ErrorCode;
import com.ehu.constants.PayBaseConstants;
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
    public PayResponse<R> handler(Map<String, String> responseMap, P params) throws PayException {
        PayResponse<R> payResponse = new PayResponse<>();
        if (null == responseMap || responseMap.isEmpty()) {
            throw new PayException(ErrorCode.WECHAT_SERVER_ERROR);
        }

        if (responseMap.containsKey("return_code") && PayBaseConstants.RETURN_SUCCESS.equals(responseMap.get("return_code"))) {
            if (!responseMap.containsKey("result_code")) {
                log.error("业务错误, 返回信息：{}", responseMap);
                throw new PayException(ErrorCode.WECHAT_SERVER_ERROR);
            }
            //业务
            this.customService(payResponse, responseMap, params);
        } else if (responseMap.containsKey("return_code")) {
            log.error("业务错误, 返回信息：{}", responseMap);
            throw new PayException(ErrorCode.WECHAT_SERVER_ERROR);
        } else {
            log.error("业务错误, 返回信息：{}", responseMap);
            throw new PayException(ErrorCode.WECHAT_SERVER_ERROR);
        }
        return payResponse;
    }

    abstract void customService(PayResponse<R> payResponse, Map<String, String> response, P param) throws PayException;
}
