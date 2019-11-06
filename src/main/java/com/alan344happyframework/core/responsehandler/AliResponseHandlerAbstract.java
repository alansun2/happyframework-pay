package com.alan344happyframework.core.responsehandler;

import com.alan344happyframework.bean.PayResponse;
import com.alan344happyframework.constants.PayBaseConstants;
import com.alan344happyframework.exception.PayException;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author AlanSun
 * @date 2019/7/5 16:46
 **/
@Slf4j
public abstract class AliResponseHandlerAbstract<T extends AlipayResponse, P, R> implements ResponseHandler<T, P, R> {

    @Override
    public PayResponse<R> handler(T alipayResponse, P params) {
        PayResponse<R> response = new PayResponse<>();
        log.info("支付宝返回信息：{}", JSON.toJSONString(alipayResponse));
        if (null == alipayResponse) {
            response.setResultCode(PayBaseConstants.RETURN_FAIL);
            response.setResultMessage("alibaba return null");
        } else {
            // 支付宝调用错误
            if (!alipayResponse.isSuccess()) {
                response.setResultCode(alipayResponse.getSubCode());
                response.setResultMessage(alipayResponse.getSubMsg());
            } else {
                this.customResponse(response, alipayResponse, params);
            }
        }

        return response;
    }

    protected void customResponse(PayResponse<R> payResponse, T alipayResponse, P param) {
    }
}
