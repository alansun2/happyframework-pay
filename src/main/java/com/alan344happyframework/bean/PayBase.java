package com.alan344happyframework.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * @author AlanSun
 * @date 2019/7/5 13:23
 **/
@Getter
@Setter
public abstract class PayBase {
    /**
     * 交易类型：默认：app 支付
     */
    private TradeTypeEnum tradeType = TradeTypeEnum.APP;

    /**
     * 支付方式 1：支付宝；2：微信；默认：支付宝
     */
    private int payType = PAY_TYPE_1;

    /**
     * 支付宝
     */
    public static final int PAY_TYPE_1 = 1;
    /**
     * 微信
     */
    public static final int PAY_TYPE_2 = 2;
}
