package com.alan344happyframework.bean;

import lombok.Getter;

@Getter
public enum TradeTypeEnum {
    /**
     * APPLET--（小程序支付）、NATIVE--Native支付、APP--app支付，MWEB--H5支付
     */
    APP("APP"), NATIVE("NATIVE"), APPLET("JSAPI");

    private String tradeType;

    TradeTypeEnum(String tradeType) {
        this.tradeType = tradeType;
    }
}
