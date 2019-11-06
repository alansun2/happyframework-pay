package com.alan344happyframework.core.responsehandler;

import com.alan344happyframework.bean.PayResponse;
import com.alan344happyframework.constants.ErrorCode;
import com.alan344happyframework.exception.PayException;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author AlanSun
 * @date 2019/7/5 16:46
 **/
@Slf4j
public abstract class AliExceptionResponseHandlerAbstract<T extends AlipayResponse, P, R> implements ResponseHandler<T, P, R> {

    @Override
    public PayResponse<R> handler(T alipayResponse, P params) throws PayException {
        PayResponse<R> response = new PayResponse<>();
        log.info("支付宝返回信息：{}", JSON.toJSONString(alipayResponse));
        if (null == alipayResponse) {
            throw new PayException(ErrorCode.ALI_SERVER_ERROR);
        } else {
            if (!alipayResponse.isSuccess()) {
                throw new PayException(alipayResponse.getSubCode(), alipayResponse.getSubMsg());
            } else {
                this.customResponse(response, alipayResponse, params);
            }
        }

        return response;
    }

    protected abstract void customResponse(PayResponse<R> payResponse, T alipayResponse, P param) throws PayException;
}
