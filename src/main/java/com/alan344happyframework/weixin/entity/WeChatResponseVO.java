package com.alan344happyframework.weixin.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


/**
 * @author AlanSun
 * @Date 2016年8月9日
 */
@Setter
@Getter
public class WeChatResponseVO implements Serializable {

    private static final long serialVersionUID = 1L;
    private String appId;
    private String timeStamp;
    private String nonceStr;
    private String packageValue;
    private String partnerId;
    private String prepayId;
    private String sign;
    /**
     * 小程序签名类型 MD5
     */
    private String signType;

    @Override
    public String toString() {
        return "微信返回返回串：appId:" + appId + ",timeStamp:" + timeStamp + ",nonceStr:" + nonceStr + ",packageValue:" + packageValue + ",partnerId:" + partnerId + ",prepayId:" + prepayId + ",sign:" + sign;
    }
}
