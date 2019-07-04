package com.ehu.weixin.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @author AlanSun
 * 20160422
 */
@Getter
@Setter
public class WechatPayOrder extends Mch {
    /**
     * openid 微信小程序必传
     */
    private String openid;
    /**
     * [APP,NATIVE,JSAPI]
     * APP:    app
     * NATIVE: 扫码
     * JSAPI:  小程序，网页
     */
    private WechatTradeTypeEnum tradeType = WechatTradeTypeEnum.APP;
}
