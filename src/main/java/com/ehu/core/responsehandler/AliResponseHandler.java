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
public class AliResponseHandler<T extends AlipayResponse> implements ResponseHandler<T> {
    private AliResponseHandler() {
    }

    private static AliResponseHandler aliResponseHandler = new AliResponseHandler();

    public static AliResponseHandler getInstance() {
        return aliResponseHandler;
    }

    @Override
    public PayResponse<T> handler(T alipayResponse) {
        PayResponse<T> response = new PayResponse<>();
        log.info("支付宝返回信息：{}", JSON.toJSONString(alipayResponse));
        if (null == alipayResponse) {
            response.setResultCode(PayBaseConstants.RETURN_FAIL);
            response.setResultMessage("response null error");
        } else if (alipayResponse.isSuccess()) {
            if (!PayBaseConstants.ALIPAY_RETURN_CODE_10000.equals(alipayResponse.getCode())) {
                response.setResultCode(alipayResponse.getSubCode());
                response.setResultMessage(alipayResponse.getSubMsg());
                response.setData(alipayResponse);
            }
        } else {
            response.setResultCode(alipayResponse.getSubCode());
            response.setResultMessage(alipayResponse.getSubMsg());
            response.setData(alipayResponse);
        }

        return response;
    }
}
