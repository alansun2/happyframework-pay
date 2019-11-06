package com.alan344happyframework.weixin.entity;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

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
    @NotBlank
    private String openid;
}
