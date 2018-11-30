package com.ehu.weixin.util;

import com.ehu.bean.PayResponse;
import com.ehu.exception.PayException;
import com.ehu.util.SSlUtil;
import com.ehu.util.XmlUtils;
import com.ehu.weixin.client.TenpayHttpClient;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jdom2.JDOMException;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;


@Slf4j
public class WeChatUtils {

    public static Map<String, String> getResponseInfo(SortedMap<String, String> map, String requestUrl) throws PayException {
        Map<String, String> resultMap = null;
        String params = XmlUtils.mapToXml(map);//map转String
        TenpayHttpClient httpClient = new TenpayHttpClient();
        httpClient.setReqContent(requestUrl);
        String resContent;
        if (httpClient.callHttpPost(requestUrl, params)) {
            resContent = httpClient.getResContent();
            resultMap = XmlUtils.xmlToMap(resContent);
        }
        return resultMap;
    }

    private static Map<String, SSLConnectionSocketFactory> sslsfMap = new HashMap<>();

    private static SSLConnectionSocketFactory getSslsf(String keyStorePath, String keyStorepass) throws Exception {
        if (!sslsfMap.containsKey(keyStorepass)) {
            SSLConnectionSocketFactory sslsf = SSlUtil.getSSL(keyStorePath, keyStorepass);
            sslsfMap.put(keyStorepass, sslsf);
            return sslsf;
        } else return sslsfMap.get(keyStorepass);
    }

    /**
     * 使用证书发送请求到微信服务器
     *
     * @param map          参数
     * @param requestUrl
     * @param keyStorepass
     * @return
     */
    public static Map<String, String> wechatPostWithSSL(SortedMap<String, String> map, String requestUrl, String keyStorePath, String keyStorepass) throws PayException {
        CloseableHttpClient httpclient = null;
        Map<String, String> resultMap = null;
        String xmlString = XmlUtils.mapToXml(map);
        try {
            SSLConnectionSocketFactory sslsf = getSslsf(keyStorePath, keyStorepass);
            httpclient = HttpClients.custom()
                    .setSSLSocketFactory(sslsf)
                    .build();
            HttpPost httppost = new HttpPost(requestUrl);
            StringEntity myEntity = new StringEntity(xmlString, "UTF-8");
            httppost.addHeader("Content-Type", "text/xml");
            httppost.setEntity(myEntity);
            try (CloseableHttpResponse response = httpclient.execute(httppost)) {
                HttpEntity entity = response.getEntity();
                log.info(response.getStatusLine().toString());
                if (entity != null) {
                    log.info("Response content length: " + entity.getContentLength());
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent()));
                    String text;
                    StringBuilder sb = new StringBuilder();
                    while ((text = bufferedReader.readLine()) != null) {
                        sb.append(text);
                    }
                    resultMap = XmlUtils.xmlToMap(sb.toString());
                }
                EntityUtils.consume(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (httpclient != null)
                    httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (resultMap == null)
            return null;
        else
            return resultMap;
    }

    /**
     * 处理微信返回
     *
     * @param wxResponse 微信返回MAP
     * @return boolean
     */
    public static boolean wechatResponseHandler(Map<String, String> wxResponse) {
        boolean flag = false;
        if (null == wxResponse || wxResponse.isEmpty()) {
            log.error("微信返回有误");
            return false;
        }

        wxResponse.forEach((k, v) -> log.info(k + ":::" + v));

        if (wxResponse.containsKey("return_code") && "SUCCESS".equals(wxResponse.get("return_code"))) {
            if (wxResponse.containsKey("result_code") && "SUCCESS".equals(wxResponse.get("result_code"))) {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 处理微信返回
     *
     * @param wxResponseMap 微信返回MAP
     */
    public static void wechatResponseHandler(Map<String, String> wxResponseMap, PayResponse<Boolean> response) {
        if (null == wxResponseMap || wxResponseMap.isEmpty()) {
            response.setResultMessage("微信返回有误");
            response.setResult(false);
            return;
        }

        wxResponseMap.forEach((k, v) -> log.info(k + ":::" + v));

        if (wxResponseMap.containsKey("return_code") && "SUCCESS".equals(wxResponseMap.get("return_code"))) {
            if ("SUCCESS".equals(wxResponseMap.get("result_code"))) {
                response.setResult(true);
            } else if ("FAIL".equals(wxResponseMap.get("result_code"))) {
                response.setResult(false);
                response.setResultMessage(wxResponseMap.get("err_code_des"));
                response.setResultCode(wxResponseMap.get("err_code"));
            } else {
                response.setResultMessage("微信返回有误");
                response.setResult(false);
            }
        } else {
            response.setResult(false);
            response.setResultMessage(wxResponseMap.get("return_msg"));
            response.setResultCode(wxResponseMap.get("return_code"));
        }
    }

    /**
     * 获取随机数
     *
     * @return 随机数
     */
    public static String getNonceStr() {
        Random random = new Random();
        return DigestUtils.md5Hex(String.valueOf(random.nextInt(10000)));
    }

    /**
     * 获取时间戳
     *
     * @return 时间戳 单位： 秒
     */
    public static String getTimeStamp() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }

    /**
     * 把金额转为分单位
     *
     * @param price 单位： 元
     */
    public static String getFinalMoney(double price) {
        String finalmoney = String.format("%.2f", price);//转为两位小数
        int i = Integer.parseInt(finalmoney.replace(".", ""));
        return Integer.toString(i);//转为分
    }

    /**
     * 处理微信回调信息
     *
     * @param httpServletRequest httpServletRequest
     * @return paramsMap
     * @throws IOException   e
     * @throws JDOMException e
     */
    public static Map<String, String> getWechatPayCallBackmap(HttpServletRequest httpServletRequest) throws Exception {
        BufferedReader reader;
        String line;
        StringBuilder inputString = new StringBuilder();
        reader = httpServletRequest.getReader();
        try {
            // 获取收到的报文
            while ((line = reader.readLine()) != null) {
                inputString.append(line);
            }

            return XmlUtils.xmlToMap(inputString.toString());
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    /**
     * 生成微信回调xml
     *
     * @param type 1：SUCCESS;2:FAIL
     * @return str
     * @throws Exception e
     */
    public static String getWXReturn(int type) throws Exception {
        if (1 == type)
            return XmlUtils.mapToXml(ImmutableMap.of("return_code", "SUCCESS", "return_msg", "OK"));
        return XmlUtils.mapToXml(ImmutableMap.of("return_code", "FAIL", "return_msg", "alerdy ok"));
    }
}
