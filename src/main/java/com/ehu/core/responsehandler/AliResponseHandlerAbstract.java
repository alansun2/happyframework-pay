package com.ehu.core.responsehandler;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayResponse;
import com.ehu.bean.PayResponse;
import com.ehu.constants.PayBaseConstants;
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
            if (!PayBaseConstants.ALIPAY_RETURN_CODE_10000.equals(alipayResponse.getCode())) {
                response.setResultCode(alipayResponse.getCode());
                response.setResultMessage(alipayResponse.getMsg());
            } else {
                this.customResponse(response, alipayResponse, params);
            }
        }

        return response;
    }

    protected abstract void customResponse(PayResponse<R> payResponse, T alipayResponse, P param);
}
