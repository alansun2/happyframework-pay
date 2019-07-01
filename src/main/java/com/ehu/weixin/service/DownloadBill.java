package com.ehu.weixin.service;

import com.alan344.utils.HttpClientUtils;
import com.alan344.utils.HttpParams;
import com.ehu.bean.DownloadParam;
import com.ehu.config.Wechat;
import com.ehu.constants.PayResultCodeConstants;
import com.ehu.constants.PayResultMessageConstants;
import com.ehu.exception.PayException;
import com.ehu.util.FileUtils;
import com.ehu.util.XmlUtils;
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
    public static void downloadBill(DownloadParam param) throws PayException {
        Wechat config = Wechat.getInstance();
        Wechat.WechatMch wechatMch = config.getMchMap().get(param.getMchNo());
        String mchAppId = config.getMchAppIdMap().get(param.getMchAppIdNo());

        SortedMap<String, String> packageParams = new TreeMap<>();
        packageParams.put("appid", mchAppId);
        packageParams.put("mch_id", wechatMch.getMchId());
        packageParams.put("nonce_str", WechatUtils.getNonceStr());
        packageParams.put("bill_date", param.getTime());
        packageParams.put("bill_type", "ALL");
        packageParams.put("tar_type", "GZIP");
        packageParams.put("sign", Signature.getSign(packageParams, wechatMch.getSignKey()));
        sendRequest(XmlUtils.mapToXml(packageParams), param.getDesPath());
    }

    private static void sendRequest(String param, String path) throws PayException {
        HttpParams httpParams = HttpParams.builder().url(DOWNLOAD_BILL_URL).strEntity(param).build();
        try {
            HttpResponse httpResponse = HttpClientUtils.doPost(httpParams);
            HttpEntity entity = httpResponse.getEntity();
            if (entity != null) {
//                org.apache.commons.io.FileUtils.copyInputStreamToFile();

                FileUtils.streamHandler(entity.getContent(), path);
            }
        } catch (IOException e) {
            log.error("获取微信账单失败", e);
            throw new PayException(PayResultCodeConstants.GET_FINANCIAL_30013, PayResultMessageConstants.GET_FINANCIAL_30013);
        }
    }
}
