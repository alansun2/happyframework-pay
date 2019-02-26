package com.ehu.weixin.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author AlanSun
 * @Date 2016年8月3日
 * 微信企业付款实体类
 */
@Getter
@Setter
@ToString
public class WechatBusinessPay {
    /**
     * 商户端订单号（唯一）,必填
     */
    private String orderId;
    /**
     * 微信实名认证姓名
     */
    private String reUserName;
    /**
     * 订单总价格,必填
     */
    private double amount;
    /**
     * 付款描述 ,必填
     */
    private String desc;
    /**
     * 用户唯一标识,必填
     */
    private String openId;
    /**
     * NO_CHECK：不校验真实姓名
     * FORCE_CHECK：强校验真实姓名（未实名认证的用户会校验失败，无法转账）
     * ,必填
     */
    private String checkName = "NO_CHECK";

    //-----------------------------------------------------------------------------------
    //以下属性为自定义时添加

    /**
     * 申请商户号的appid或商户号绑定的appid
     */
    private String mchAppid;

    /**
     * 商户号
     */
    private String mchid;

    /**
     * ssl证书地址
     */
    private String caPath;

    /**
     * 密码
     */
    private String code;

    /**
     * 签名密匙
     */
    private String privateKey;
}
