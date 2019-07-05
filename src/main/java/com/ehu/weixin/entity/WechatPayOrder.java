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
}
