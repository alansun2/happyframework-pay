package com.ehu.weixin.entity;

import lombok.Getter;

@Getter
public enum WechatTradeTypeEnum {
    APP("APP"), NATIVE("NATIVE"), JSAPI("JSAPI");

    private String tradeType;

    WechatTradeTypeEnum(String tradeType) {
        this.tradeType = tradeType;
    }
}
