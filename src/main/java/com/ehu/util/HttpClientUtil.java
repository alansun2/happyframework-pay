package com.ehu.util;

import com.ehu.bean.HttpParams;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

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

    private static HttpClient httpClient;

    private static ResponseHandler<String> responseHandler = new BasicResponseHandler();

    private static Map<String, SSLConnectionSocketFactory> sslsfMap = new HashMap<>();

    private static SSLConnectionSocketFactory getSslsf(String keyStorePath, String keyStorepass) throws Exception {
        if (!sslsfMap.containsKey(keyStorepass)) {
            SSLConnectionSocketFactory sslsf = SSlUtil.getSSL(keyStorePath, keyStorepass);
            sslsfMap.put(keyStorepass, sslsf);
            return sslsf;
        } else return sslsfMap.get(keyStorepass);
    }

    private static void init(String keyStorePath, String keyStorepass) {
        log.info("初始化HttpClientTest~~~开始");
        try {
            // 初始化连接管理器
            PoolingHttpClientConnectionManager connectionManager = null;
            if (!StringUtils.isBlank(keyStorepass)) {
                // 配置同时支持 HTTP 和 HTPPS
                Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().register(
                        "http", PlainConnectionSocketFactory.getSocketFactory()).register(
                        "https", getSslsf(keyStorePath, keyStorepass)).build();
                connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
                // 将最大连接数增加到200，实际项目最好从配置文件中读取这个值
                connectionManager.setMaxTotal(1000);

                // 设置最大路由
                connectionManager.setDefaultMaxPerRoute(2);
            }

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

    private static CloseableHttpClient getConnection(String keyStorePath, String keyStorepass) {
        if (StringUtils.isBlank(keyStorepass)) keyStorepass = "-1";
        if (!poolConnManagerMap.containsKey(keyStorepass) || null == requestConfig) {
            init(keyStorePath, keyStorepass);
        }

        if (null != httpClient) {
            return (CloseableHttpClient) httpClient;
        }
        PoolingHttpClientConnectionManager connectionManager = poolConnManagerMap.get(keyStorepass);
        httpClient = HttpClients.custom()
                // 设置连接池管理
                .setConnectionManager(connectionManager)
                // 设置请求配置
                .setDefaultRequestConfig(requestConfig)
                //关闭重试
                .disableAutomaticRetries()
                // 设置重试次数
//                .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false))
                .build();
        if (connectionManager != null && connectionManager.getTotalStats() != null) {
            log.info("now client pool {}", connectionManager.getTotalStats().toString());
        }
        return (CloseableHttpClient) httpClient;
    }

    /**
     * post
     *
     * @param httpParams http参数
     */
    public static String doPost(String keyStorePath, String keyStorepass, HttpParams httpParams) throws IOException {
        HttpPost post = new HttpPost(httpParams.getUrl());
        if (httpParams.getHeaders() != null) {
            post.setHeaders(httpParams.getHeaders());
        }
        if (httpParams.getStrEntity() != null) {
            StringEntity se = new StringEntity(httpParams.getStrEntity(), "UTF-8");
            post.setEntity(se);
        }
        // Send the post request and get the response

        return getConnection(keyStorePath, keyStorepass).execute(post, responseHandler);
    }

    /**
     * post
     *
     * @param httpParams http参数
     */
    public static String doPost(HttpParams httpParams) throws IOException {
        HttpPost post = new HttpPost(httpParams.getUrl());
        if (httpParams.getHeaders() != null) {
            post.setHeaders(httpParams.getHeaders());
        }
        if (httpParams.getStrEntity() != null) {
            StringEntity se = new StringEntity(httpParams.getStrEntity(), "UTF-8");
            post.setEntity(se);
        }
        // Send the post request and get the response

        return getConnection(null, null).execute(post, responseHandler);
    }

    /**
     * 执行GET请求
     *
     * @param keyStorePath 证书地址
     * @param keyStorepass 证书密码
     * @param httpParams   参数
     * @return response
     * @throws IOException e
     */
    public static String doGet(String keyStorePath, String keyStorepass, HttpParams httpParams) throws IOException {
        // 创建http GET请求
        HttpGet httpGet = new HttpGet(httpParams.getUrl());
        if (httpParams.getHeaders() == null) {
            httpGet.setHeaders(httpParams.getHeaders());
        }
        return getConnection(keyStorePath, keyStorepass).execute(httpGet, responseHandler);
    }

    public static void main(String[] str) throws IOException, NoSuchAlgorithmException, KeyManagementException {
/*        HttpParams params = new HttpParams();
        params.setUrl("https://www.baidu.com");
        BasicHeader[] headers = new BasicHeader[1];
        BasicHeader header = new BasicHeader("User-Agent", "Mozilla/5.0");
        headers[0] = header;
        params.setHeaders(headers);
        String closeableHttpResponse = doPost(null, null, false, params);
        params.setHeaders(headers);
        CloseableHttpResponse closeableHttpResponse = doPost(params);
        int i = 0;*/
        HttpParams httpParams = new HttpParams();
        httpParams.setUrl("https://www.baidu.com");
        try {
            for (int i = 0; i < 10; i++) {
                String s = doGet(null, null, httpParams);
                System.out.println(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}