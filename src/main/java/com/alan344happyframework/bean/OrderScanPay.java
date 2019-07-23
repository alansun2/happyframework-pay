package com.alan344happyframework.bean;

import com.alipay.api.domain.AlipayTradePrecreateModel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author AlanSun
 * @Date 2016年11月4日
 * <p>
 * 多用于线下自动售货机
 */
@Getter
@Setter
public class OrderScanPay extends OrderPay {
    /**
     * 门店id
     */
    private String storeId;
    private AlipayTradePrecreateModel alipayTradePrecreateModel;
}
