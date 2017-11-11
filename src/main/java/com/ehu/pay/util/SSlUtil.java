package com.ehu.pay.util;

import com.ehu.pay.config.EhPayConfig;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 20150503
 *
 * @author AlanSun
 * 验证微信证书
 */
public class SSlUtil {
    /**
     * 获取SSLConnectionSocketFactory
     * 采用设置信任自签名证书实现https
     * @param keyStorepass
     * @return
     * @throws Exception
     */
    public static SSLConnectionSocketFactory getSSL(String keyStorePath, String keyStorepass) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        FileInputStream instream = new FileInputStream(new File(keyStorePath));
        try {
            keyStore.load(instream, keyStorepass.toCharArray());
        } finally {
            instream.close();
        }

        // Trust own CA and all self-signed certs
        SSLContext sslContext = SSLContexts.custom()
                .loadKeyMaterial(keyStore, keyStorepass.toCharArray())
                .build();
        // Allow TLSv1 protocol only
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslContext,
                new String[]{"TLSv1"},
                null,
                new DefaultHostnameVerifier());
        return sslsf;
    }

    public static SSLContext custom(String keyStorePath, String keyStorepass){
        SSLContext sc = null;
    FileInputStream instream = null;
    KeyStore trustStore = null;
    try {
        trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        instream = new FileInputStream(new File(keyStorePath));
        trustStore.load(instream, keyStorepass.toCharArray());
        // 相信自己的CA和所有自签名的证书
        sc = SSLContexts.custom().loadTrustMaterial(trustStore, new TrustSelfSignedStrategy()).build();
    } catch (KeyStoreException | NoSuchAlgorithmException| CertificateException | IOException | KeyManagementException e) {
        e.printStackTrace();
    } finally {
        try {
            instream.close();
        } catch (IOException e) {
        }
    }
    return sc;
}

/**
 * 模拟请求
 *
 * @param url       资源地址
 * @param map   参数列表
 * @param encoding  编码
 * @return
 * @throws ParseException
 * @throws IOException
 * @throws KeyManagementException
 * @throws NoSuchAlgorithmException
 * @throws ClientProtocolException
 */
public static String send(String url, Map<String,String> map, String encoding) throws ClientProtocolException, IOException {
        String body = "";

        //tomcat是我自己的密钥库的密码，你可以替换成自己的
        //如果密码为空，则用"nopassword"代替
        SSLContext sslcontext = custom("D:\\keys\\wsriakey", "tomcat");

        // 设置协议http和https对应的处理socket链接工厂的对象
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
        .register("http", PlainConnectionSocketFactory.INSTANCE)
        .register("https", new SSLConnectionSocketFactory(sslcontext))
        .build();
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        HttpClients.custom().setConnectionManager(connManager);

        //创建自定义的httpclient对象
        CloseableHttpClient client = HttpClients.custom().setConnectionManager(connManager).build();
        //CloseableHttpClient client = HttpClients.createDefault();

        //创建post方式请求对象
        HttpPost httpPost = new HttpPost(url);

        //装填参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        if(map!=null){
        for (Map.Entry<String, String> entry : map.entrySet()) {
        nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        }
        //设置参数到请求对象中
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, encoding));

        System.out.println("请求地址："+url);
        System.out.println("请求参数："+nvps.toString());

        //设置header信息
        //指定报文头【Content-type】、【User-Agent】
        httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
        httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

        //执行请求操作，并拿到结果（同步阻塞）
        CloseableHttpResponse response = client.execute(httpPost);
        //获取结果实体
        HttpEntity entity = response.getEntity();
        if (entity != null) {
        //按指定编码转换结果实体为String类型
        body = EntityUtils.toString(entity, encoding);
        }
        EntityUtils.consume(entity);
        //释放链接
        response.close();
        return body;
        }
        }
