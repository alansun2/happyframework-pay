package com.ehu.pay.weixin.util;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;

import com.ehu.pay.config.EhPayConfig;
/**
 * 20150503
 * @author AlanSun
 *	验证微信证书
 */
public class WeChatSSlUtil {
	public static SSLConnectionSocketFactory getSSL(String code) throws Exception {
		EhPayConfig config = EhPayConfig.getInstance();
        KeyStore keyStore  = KeyStore.getInstance("PKCS12");
        FileInputStream instream = new FileInputStream(new File(config.getWxPay_ca()));
        try {
            keyStore.load(instream, code.toCharArray());
        } finally {
            instream.close();
        }

        // Trust own CA and all self-signed certs
        SSLContext sslcontext = SSLContexts.custom()
                .loadKeyMaterial(keyStore, code.toCharArray())
                .build();
        // Allow TLSv1 protocol only
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslcontext,
                new String[] { "TLSv1" },
                null,
                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        return sslsf;
    }
}
