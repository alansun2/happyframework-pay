package com.ehu.core.responsehandler;

import com.alan344happyframework.util.StringUtils;
import com.alipay.api.response.AlipayDataDataserviceBillDownloadurlQueryResponse;
import com.ehu.bean.FinancialReport;
import com.ehu.bean.PayResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * @author AlanSun
 * @date 2019/7/16 10:39
 **/
@Slf4j
public class AliFinancialReportResponseHandler extends AliResponseHandlerAbstract<AlipayDataDataserviceBillDownloadurlQueryResponse, FinancialReport, Object> {
    private AliFinancialReportResponseHandler() {
    }

    private static AliFinancialReportResponseHandler aliResponseHandlerAbstract = new AliFinancialReportResponseHandler();

    public static AliFinancialReportResponseHandler getInstance() {
        return aliResponseHandlerAbstract;
    }

    @Override
    protected void customResponse(PayResponse<Object> payResponse, AlipayDataDataserviceBillDownloadurlQueryResponse alipayResponse, FinancialReport param) {
        if (alipayResponse.isSuccess()) {
            String desPath = param.getDesPath();
            try {
                if (!StringUtils.isEmpty(desPath)) {
                    FileUtils.copyURLToFile(new URL(alipayResponse.getBillDownloadUrl()), new File(desPath), 10000, 10000);
                } else {
                    payResponse.setData(alipayResponse.getBillDownloadUrl());
                }
            } catch (IOException e) {
                log.error("获取财务账单url失败", e);
                payResponse.setResultCode("IO_ERROR");
                payResponse.setResultMessage("IO 错误");
                payResponse.setData(alipayResponse);
            }
        } else {
            payResponse.setResultCode(alipayResponse.getSubCode());
            payResponse.setResultMessage(alipayResponse.getSubMsg());
            payResponse.setData(alipayResponse);
        }
    }
}
