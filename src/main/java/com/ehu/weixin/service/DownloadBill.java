package com.ehu.weixin.service;

import com.alan344happyframework.constants.SeparatorConstants;
import com.alan344happyframework.util.HttpClientUtils;
import com.alan344happyframework.util.StringUtils;
import com.alan344happyframework.util.XmlUtils;
import com.alan344happyframework.util.bean.HttpParams;
import com.ehu.bean.FinancialReport;
import com.ehu.bean.PayResponse;
import com.ehu.config.Wechat;
import com.ehu.core.responsehandler.WechatFinancialReportResponseHandler;
import com.ehu.exception.PayException;
import com.ehu.weixin.entity.WechatFinancialReport;
import com.ehu.weixin.util.Signature;
import com.ehu.weixin.util.WechatUtils;
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
