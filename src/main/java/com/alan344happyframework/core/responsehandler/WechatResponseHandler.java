package com.alan344happyframework.core.responsehandler;

import com.alan344happyframework.bean.PayResponse;
import com.alan344happyframework.constants.PayBaseConstants;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author AlanSun
 * @date 2019/7/5 16:04
 **/
@Slf4j
public class WechatResponseHandler implements ResponseHandler<Map<String, String>, Object, Map<String, String>> {
    private WechatResponseHandler() {
    }

    private static WechatResponseHandler wechatResponseHandler = new WechatResponseHandler();

    public static WechatResponseHandler getInstance() {
        return wechatResponseHandler;
    }

    @Override
    public PayResponse<Map<String, String>> handler(Map<String, String> wxResponseMap, Object params) {
        PayResponse<Map<String, String>> response = new PayResponse<>();
        if (null == wxResponseMap || wxResponseMap.isEmpty()) {
            response.setResultMessage("微信返回有误");
            response.setResultCode(PayBaseConstants.RETURN_FAIL);
            return response;
        }

        log.info("微信返回信息：{}", wxResponseMap.toString());

        response.setData(wxResponseMap);
        if (wxResponseMap.containsKey("return_code") && PayBaseConstants.RETURN_SUCCESS.equals(wxResponseMap.get("return_code"))) {
            if (!PayBaseConstants.RETURN_SUCCESS.equals(wxResponseMap.get("result_code"))) {
                if (PayBaseConstants.RETURN_FAIL.equals(wxResponseMap.get("result_code"))) {
                    response.setResultMessage(wxResponseMap.get("err_code_des"));
                    response.setResultCode(wxResponseMap.get("err_code"));
                } else {
                    response.setResultMessage("微信返回有误");
                    response.setResultCode(PayBaseConstants.RETURN_FAIL);
                }
            }
        } else if (wxResponseMap.containsKey("return_code")) {
            response.setResultMessage(wxResponseMap.get("return_msg"));
            response.setResultCode(wxResponseMap.get("return_code"));
        } else {
            response.setResultMessage("微信返回有误");
            response.setResultCode(PayBaseConstants.RETURN_FAIL);
        }

        return response;
    }
}
