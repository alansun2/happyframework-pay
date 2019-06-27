package com.ehu.weixin.weixinpay;

import com.ehu.bean.DownloadParam;
import com.ehu.config.Wechat;
import com.ehu.constants.PayResultCodeConstants;
import com.ehu.constants.PayResultMessageConstants;
import com.ehu.exception.PayException;
import com.ehu.util.FileUtils;
import com.ehu.util.XmlUtils;
import com.ehu.weixin.util.Signature;
import com.ehu.weixin.util.WeChatUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author AlanSun
 * @Date 2017年8月10日 下午6:21:31
 */
@Slf4j
public class DownloadBill {
    private static final String DOWNLOADBILLURL = "https://api.mch.weixin.qq.com/pay/downloadbill";

    @SuppressWarnings("unchecked")
    public static void downloadBill(DownloadParam param) throws PayException {
        Wechat config = Wechat.getInstance();
        Wechat.WechatMch wechatMch = config.getMchMap().get(param.getMchNo());
        String mchAppId = config.getMchAppidMap().get(param.getMchAppIdNo());
        //封装获取prepayid
        String nonce_str = WeChatUtils.getNonceStr();
        SortedMap<String, String> packageParams = new TreeMap<String, String>();
        packageParams.put("appid", mchAppId);
        packageParams.put("mch_id", wechatMch.getMchId());
        packageParams.put("nonce_str", nonce_str);
        packageParams.put("bill_date", param.getTime());
        packageParams.put("bill_type", "ALL");
        packageParams.put("tar_type", "GZIP");
        packageParams.put("sign", Signature.getSign(packageParams, wechatMch.getSignKey()));
        sendRequest(XmlUtils.mapToXml(packageParams), param.getDesPath());
    }

    private static void sendRequest(String param, String path) throws PayException {
        // 创建默认的httpClient实例.
        // 创建httppost
        HttpPost httppost = new HttpPost(DOWNLOADBILLURL);
        StringEntity uefEntity;
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            uefEntity = new StringEntity(param, "UTF-8");
            httppost.setEntity(uefEntity);
            try (CloseableHttpResponse response = httpclient.execute(httppost)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    FileUtils.streamHandler(entity.getContent(), path);
                }
            }
        } catch (IOException e) {
            log.error("获取微信账单失败", e);
            throw new PayException(PayResultCodeConstants.GET_FINANCIAL_30013, PayResultMessageConstants.GET_FINANCIAL_30013);
        }
    }
}
