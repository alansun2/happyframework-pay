package com.ehu.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * @author AlanSun
 * @date 2019/7/5 13:23
 **/
@Getter
@Setter
public abstract class PayBase {
    private TradeTypeEnum tradeType;

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
