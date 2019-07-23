package com.alan344happyframework.core.responsehandler;

import com.alan344happyframework.util.XmlUtils;
import com.alan344happyframework.bean.FinancialReport;
import com.alan344happyframework.bean.PayResponse;
import com.alan344happyframework.constants.PayBaseConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author AlanSun
 * @date 2019/7/5 16:04
 **/
@Slf4j
public class WechatFinancialReportResponseHandler implements ResponseHandler<HttpEntity, FinancialReport, Map<String, String>> {
    private WechatFinancialReportResponseHandler() {
    }

    private static WechatFinancialReportResponseHandler wechatResponseHandler = new WechatFinancialReportResponseHandler();

    public static WechatFinancialReportResponseHandler getInstance() {
        return wechatResponseHandler;
    }

    @Override
    public PayResponse<Map<String, String>> handler(HttpEntity response, FinancialReport params) {
        PayResponse<Map<String, String>> payResponse = new PayResponse<>();
        try {
            String s = EntityUtils.toString(response);
            Map<String, String> keyValueMap = XmlUtils.xmlToMap(s);
            payResponse.setResultCode(keyValueMap.get("return_code"));
            payResponse.setResultMessage(keyValueMap.get("return_msg"));
            payResponse.setData(keyValueMap);
        } catch (IOException e) {
            try {
                FileUtils.copyInputStreamToFile(response.getContent(), new File(params.getDesPath()));
            } catch (IOException e1) {
                log.error("io errot", e1);
                payResponse.setResultCode(PayBaseConstants.RETURN_FAIL);
                payResponse.setResultMessage("IO 错误");
            }
        }
        return payResponse;
    }
}
