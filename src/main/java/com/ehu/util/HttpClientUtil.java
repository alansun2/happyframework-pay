package com.ehu.util;

import com.ehu.bean.HttpParams;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class HttpClientUtil {
    // 池化管理
    private static Map<String, PoolingHttpClientConnectionManager> poolConnManagerMap = new HashMap<>();

    //请求器的配置
    private static RequestConfig requestConfig;

    private static SSLConnectionSocketFactory sslsf;

    private static SSLConnectionSocketFactory getSslsf(String keyStorePath, String keyStorepass) throws Exception {
        if (null == sslsf) {
            sslsf = SSlUtil.getSSL(keyStorePath, keyStorepass);
        }
        return sslsf;
    }

    private static void init(String keyStorePath, String keyStorepass, boolean useCert) {
        log.info("初始化HttpClientTest~~~开始");
        try {
//            SSLContextBuilder builder = new SSLContextBuilder();
//            builder.loadTrustMaterial(SSlUtil.getKeyStore("/opt/appdata/ssl/server.keystore", "tissot"), new TrustSelfSignedStrategy());
            Registry<ConnectionSocketFactory> socketFactoryRegistry;
            if (useCert) {
                // 配置同时支持 HTTP 和 HTPPS
                socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().register(
                        "http", PlainConnectionSocketFactory.getSocketFactory()).register(
                        "https", getSslsf(keyStorePath, keyStorepass)).build();
            } else {
                socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                        .register("http", PlainConnectionSocketFactory.getSocketFactory())
                        .register("https", SSLConnectionSocketFactory.getSocketFactory())
                        .build();
            }
            // 初始化连接管理器
            PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

            // 将最大连接数增加到200，实际项目最好从配置文件中读取这个值
            connectionManager.setMaxTotal(1000);

            // 设置最大路由
            connectionManager.setDefaultMaxPerRoute(2);

            poolConnManagerMap.put(keyStorepass, connectionManager);

            // 根据默认超时限制初始化requestConfig
            int socketTimeout = 10000;
            int connectTimeout = 10000;
            int connectionRequestTimeout = 10000;
            requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(connectionRequestTimeout)
                    .setSocketTimeout(socketTimeout)
                    .setConnectTimeout(connectTimeout)
                    .build();

            log.info("初始化HttpClientTest~~~结束");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static CloseableHttpClient getConnection(String keyStorePath, String keyStorepass, boolean useCert) {
        if (!useCert) keyStorepass = "-1";
        if (!poolConnManagerMap.containsKey(keyStorepass) || null == requestConfig) {
            init(keyStorePath, keyStorepass, useCert);
        }
        PoolingHttpClientConnectionManager connectionManager = poolConnManagerMap.get(keyStorepass);
        CloseableHttpClient httpClient = HttpClients.custom()
                // 设置连接池管理
                .setConnectionManager(connectionManager)
                // 设置请求配置
                .setDefaultRequestConfig(requestConfig)
                // 设置重试次数
                .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false))
                .build();

        if (connectionManager != null && connectionManager.getTotalStats() != null) {
            log.info("now client pool {}", connectionManager.getTotalStats().toString());
        }

        return httpClient;
    }

    /**
     * post
     *
     * @param httpParams http参数
     */
    public static String doPost(String keyStorePath, String keyStorepass, boolean useCert, HttpParams httpParams) throws IOException {
        HttpPost post = new HttpPost(httpParams.getUrl());
        if (httpParams.getHeaders() == null) {
            post.setHeaders(httpParams.getHeaders());
        }
        if (httpParams.getStrEntity() != null) {
            StringEntity se = new StringEntity(httpParams.getStrEntity(), "UTF-8");
            post.setEntity(se);
        }
        // Send the post request and get the response
        CloseableHttpResponse response = getConnection(keyStorePath, keyStorepass, useCert).execute(post);
        String responseStr = null;
        try {

            HttpEntity entity = response.getEntity();
            StatusLine statusLine = response.getStatusLine();
            log.info(statusLine.toString());
            if (HttpStatus.SC_OK == statusLine.getStatusCode()) {
                responseStr = EntityUtils.toString(entity, "UTF-8");
            }
            EntityUtils.consume(entity);
        } finally {
            if (null != response)
                response.close();
        }
        return responseStr;
    }

    /**
     * 执行GET请求
     *
     * @param httpParams
     * @return
     * @throws IOException
     */
    public static String doGet(String keyStorePath, String keyStorepass, boolean useCert, HttpParams httpParams) throws IOException {
        // 创建http GET请求
        HttpGet httpGet = new HttpGet(httpParams.getUrl());
        if (httpParams.getHeaders() == null) {
            httpGet.setHeaders(httpParams.getHeaders());
        }
        CloseableHttpResponse response = getConnection(keyStorePath, keyStorepass, useCert).execute(httpGet);
        String responseStr = null;
        try {
            // 执行请求
            // 判断返回状态是否为200
            HttpEntity entity = response.getEntity();
            StatusLine statusLine = response.getStatusLine();
            log.info(statusLine.toString());
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                responseStr = EntityUtils.toString(entity, "UTF-8");
            }
            EntityUtils.consume(entity);
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return responseStr;
    }

    public static void main(String[] str) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        HttpParams params = new HttpParams();
        params.setUrl("https://www.baidu.com");
        BasicHeader[] headers = new BasicHeader[1];
        BasicHeader header = new BasicHeader("User-Agent", "Mozilla/5.0");
        headers[0] = header;
        params.setHeaders(headers);
        String closeableHttpResponse = doPost(null, null, false, params);
//        params.setHeaders(headers);
//        CloseableHttpResponse closeableHttpResponse = doPost(params);
//        int i = 0;
//        try {
//            String s = doGet(null, null, false, "https://www.baidu.com");
//            System.out.println(s);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        String s = EntityUtils.toString(closeableHttpResponse.getEntity());
    }
}