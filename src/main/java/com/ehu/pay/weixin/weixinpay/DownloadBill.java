package com.ehu.pay.weixin.weixinpay;

import com.ehu.pay.config.EhPayConfig;
import com.ehu.pay.constants.PayResultCodeConstants;
import com.ehu.pay.constants.PayResultMessageConstants;
import com.ehu.pay.exception.PayException;
import com.ehu.pay.util.FileUtils;
import com.ehu.pay.weixin.util.WeChatUtils;
import com.ehu.pay.util.XMLUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author AlanSun
 * @Date 2017年8月10日 下午6:21:31
 */
@Slf4j
public class DownloadBill {
    private static final String downloadbillurl = "https://api.mch.weixin.qq.com/pay/downloadbill";

    @SuppressWarnings("unchecked")
    public static void downloadBill(String time, String desPath) throws PayException {
        EhPayConfig config = EhPayConfig.getInstance();

        //封装获取prepayid
        String nonce_str = WeChatUtils.getNonceStr();
        SortedMap<String, String> packageParams = new TreeMap<String, String>();
        packageParams.put("appid", config.getWxPay_appid());
        packageParams.put("mch_id", config.getWxPay_mch_id());
        packageParams.put("nonce_str", nonce_str);
        packageParams.put("bill_date", time);
        packageParams.put("bill_type", "ALL");
        packageParams.put("tar_type", "GZIP");
        packageParams = WeChatUtils.createSign(packageParams, config);//获取签名
        sendRequest(XMLUtil.getXMLString(packageParams), desPath);
    }

    private static void sendRequest(String param, String path) throws PayException {
        // 创建默认的httpClient实例.
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 创建httppost
        HttpPost httppost = new HttpPost(downloadbillurl);
        StringEntity uefEntity;
        try {
            uefEntity = new StringEntity(param, "UTF-8");
            httppost.setEntity(uefEntity);
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
//					String content = HttpClientUtil.InputStreamTOString(entity.getContent(), "UTf-8");
//					if(content.startsWith("<xml>")){
//						@SuppressWarnings("unchecked")
//						Map<String, String> map = XMLUtil.doXMLParse(content);
//						log.error("微信获取账单失败" + map.get("return_msg"));
//						throw new PayException(PayResultCodeConstants.GET_FINANCIAL_30013, PayResultMessageConstants.GET_FINANCIAL_30013);
//					}
                    FileUtils.streamHandler(entity.getContent(), path);
                }
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            log.error("获取微信账单失败", e);
            throw new PayException(PayResultCodeConstants.GET_FINANCIAL_30013, PayResultMessageConstants.GET_FINANCIAL_30013);
        } catch (UnsupportedEncodingException e) {
            log.error("获取微信账单失败", e);
            throw new PayException(PayResultCodeConstants.GET_FINANCIAL_30013, PayResultMessageConstants.GET_FINANCIAL_30013);
        } catch (IOException e) {
            log.error("获取微信账单失败", e);
            throw new PayException(PayResultCodeConstants.GET_FINANCIAL_30013, PayResultMessageConstants.GET_FINANCIAL_30013);
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                log.error("获取微信账单失败", e);
                throw new PayException(PayResultCodeConstants.GET_FINANCIAL_30013, PayResultMessageConstants.GET_FINANCIAL_30013);
            }
        }
    }
}
