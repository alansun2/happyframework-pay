package com.ehu.bean;

import com.alipay.api.domain.AlipayTradePrecreateModel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author AlanSun
 * @Date 2016年11月4日
 * <p>
 * 线下自动售货会用的比较多
 */
@Getter
@Setter
public class ScanPayOrder extends PayOrder {
    /**
     * 门店id
     */
    private String storeId;
    private AlipayTradePrecreateModel alipayTradePrecreateModel;
}
