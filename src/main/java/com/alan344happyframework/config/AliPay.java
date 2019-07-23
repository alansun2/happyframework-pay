package com.alan344happyframework.config;

import com.alan344happyframework.util.FileUtils;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Component
@ConfigurationProperties(prefix = "pay.alipay")
public class AliPay {
    private static final AliPay ALIPAY_INSTANCE = new AliPay();

    private AliPay() {
    }

    public static AliPay getInstance() {
        return ALIPAY_INSTANCE;
    }

    /**
     * 字符编码格式 目前支持 GBK 或 UTF-8
     */
    private String inputCharset = "UTF-8";
    /**
     * 签名方式 不需修改
     */
    private String signType = "RSA2";
    /**
     * 设置未付款交易的超时时间 默认30分钟，一旦超时，该笔交易就会自动被关闭。 <br>
     * 取值范围：1m～15d。<br>
     * m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。 <br>
     * 该参数数值不接受小数点，如1.5h，可转换为90m。<br>
     */
    private String timeOut;
    /**
     * 应用appid
     */
    private String appId;
    /**
     * 商户的私钥
     */
    private String privateKey;
    /**
     * 开放平台支付宝公钥
     */
    private String openPublicKey;
    /**
     * 支付宝网关地址
     */
    private String gatewayUrl = "https://openapi.alipay.com/gateway.do";
    /**
     * 支付宝回调地址
     */
    private String notifyUrl;

    public void setPrivateKey(String privateKey) {
        ALIPAY_INSTANCE.privateKey = FileUtils.readFile(privateKey);
    }

    public void setOpenPublicKey(String openPublicKey) {
        ALIPAY_INSTANCE.openPublicKey = FileUtils.readFile(openPublicKey);
    }

    public void setInputCharset(String inputCharset) {
        ALIPAY_INSTANCE.inputCharset = inputCharset;
    }

    public void setSignType(String signType) {
        ALIPAY_INSTANCE.signType = signType;
    }

    public void setTimeOut(String timeOut) {
        ALIPAY_INSTANCE.timeOut = timeOut;
    }

    public void setAppId(String appId) {
        ALIPAY_INSTANCE.appId = appId;
    }

    public void setGatewayUrl(String gatewayUrl) {
        ALIPAY_INSTANCE.gatewayUrl = gatewayUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        ALIPAY_INSTANCE.notifyUrl = notifyUrl;
    }
}