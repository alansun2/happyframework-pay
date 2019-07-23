package com.alan344happyframework.weixin.service;

import com.alan344happyframework.constants.SeparatorConstants;
import com.alan344happyframework.util.HttpClientUtils;
import com.alan344happyframework.util.StringUtils;
import com.alan344happyframework.util.XmlUtils;
import com.alan344happyframework.util.bean.HttpParams;
import com.alan344happyframework.bean.FinancialReport;
import com.alan344happyframework.bean.PayResponse;
import com.alan344happyframework.config.Wechat;
import com.alan344happyframework.core.responsehandler.WechatFinancialReportResponseHandler;
import com.alan344happyframework.exception.PayException;
import com.alan344happyframework.weixin.entity.WechatFinancialReport;
import com.alan344happyframework.weixin.util.Signature;
import com.alan344happyframework.weixin.util.WechatUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author AlanSun
 * @Date 2017年8月10日 下午6:21:31
 */
@Slf4j
public class DownloadBill {
    private static final String DOWNLOAD_BILL_URL = "https://api.mch.weixin.qq.com/pay/downloadbill";

    @SuppressWarnings("unchecked")
    public static PayResponse downloadBill(FinancialReport params) throws PayException {
        Wechat config = Wechat.getInstance();
        WechatFinancialReport wechatFinancialReport = params.getWechatFinancialReport();
        Wechat.WechatMch wechatMch = config.getMchMap().get(wechatFinancialReport.getMchNo());
        String mchAppId = config.getMchAppIdMap().get(wechatFinancialReport.getMchAppIdNo());

        SortedMap<String, String> packageParams = new TreeMap<>();
        packageParams.put("appid", mchAppId);
        packageParams.put("mch_id", wechatMch.getMchId());
        packageParams.put("nonce_str", WechatUtils.getNonceStr());
        packageParams.put("bill_date", params.getData().replace("-", SeparatorConstants.EMPTY));
        packageParams.put("bill_type", "ALL");
        String billType = wechatFinancialReport.getBill_type();
        if (StringUtils.isNotEmpty(billType)) {
            packageParams.put("bill_type", billType);
        }
        packageParams.put("tar_type", "GZIP");
        packageParams.put("sign", Signature.getSign(packageParams, wechatMch.getSignKey()));
        HttpEntity entity = null;
        try {
            entity = sendRequest(XmlUtils.mapToXml(packageParams));
        } catch (IOException e) {
            log.error("获取财务账单失败", e);
        }

        return WechatFinancialReportResponseHandler.getInstance().handler(entity, params);
    }

    private static HttpEntity sendRequest(String param) throws IOException {
        HttpParams httpParams = HttpParams.builder().url(DOWNLOAD_BILL_URL).strEntity(param).build();
        try {
            HttpResponse httpResponse = HttpClientUtils.doPost(httpParams);
            return httpResponse.getEntity();
        } catch (IOException e) {
            log.error("获取微信账单失败", e);
            throw e;
        }
    }
}
