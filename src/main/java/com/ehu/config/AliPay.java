package com.ehu.config;

import com.ehu.util.FileUtils;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "pay.alipay")
public class AliPay {
    private static final AliPay ALIPAY_INSTANCE = new AliPay();

    private AliPay() {
    }

    public static AliPay getInstance() {
        return ALIPAY_INSTANCE;
    }

    /**
     * 合作身份者ID，以2088开头由16位纯数字组成的字符串
     */
    private String partner;
    /**
     * 收款时的支付宝账号，需要与partner对应的支付宝账号为同一个，也就是说收款支付宝账号必须是签约时的支付宝账号
     */
    private String seller;
    /**
     * 商户的私钥
     */
    private String privateKey;
    /**
     * 支付宝的公钥，无需修改该值
     */
    private String publicKey;
    /**
     * 字符编码格式 目前支持 GBK 或 UTF-8
     */
    private String inputCharset;
    /**
     * 签名方式 不需修改
     */
    private String signType;
    /**
     * 设置未付款交易的超时时间 默认30分钟，一旦超时，该笔交易就会自动被关闭。 <br>
     * 取值范围：1m～15d。<br>
     * m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。 <br>
     * 该参数数值不接受小数点，如1.5h，可转换为90m。<br>
     */
    private String timeOut;
    /**
     * 支付宝提供给商户的服务接入网关URL(新)
     */
    private String gatewayUrl;
    /**
     * 支付宝消息验证地址
     */
    private String verifyUrl;
    /**
     * 批量转账接口名称
     */
    private String service;
    /**
     * 付款账号名
     */
    private String accountName;
    /**
     * 应用appid
     */
    private String appId;
    /**
     * md5key
     */
    private String md5Key;
    /**
     * openapi地址
     */
    private String openApi;
    /**
     * 开放平台支付宝公钥
     */
    private String openPublicKey;

    public void setPartner(String partner) {
        ALIPAY_INSTANCE.partner = partner;
    }

    public void setSeller(String seller) {
        ALIPAY_INSTANCE.seller = seller;
    }

    public void setPrivateKey(String privateKey) {
        ALIPAY_INSTANCE.privateKey = FileUtils.readFile(privateKey);
    }

    public void setPublicKey(String publicKey) {
        ALIPAY_INSTANCE.publicKey = FileUtils.readFile(publicKey);
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

    public void setGatewayUrl(String gatewayUrl) {
        ALIPAY_INSTANCE.gatewayUrl = gatewayUrl;
    }

    public void setVerifyUrl(String verifyUrl) {
        ALIPAY_INSTANCE.verifyUrl = verifyUrl;
    }

    public void setService(String service) {
        ALIPAY_INSTANCE.service = service;
    }

    public void setAccountName(String accountName) {
        ALIPAY_INSTANCE.accountName = accountName;
    }

    public void setAppId(String appId) {
        ALIPAY_INSTANCE.appId = appId;
    }

    public void setMd5Key(String md5Key) {
        ALIPAY_INSTANCE.md5Key = FileUtils.readFile(md5Key);
    }

    public void setOpenApi(String openApi) {
        ALIPAY_INSTANCE.openApi = openApi;
    }
}