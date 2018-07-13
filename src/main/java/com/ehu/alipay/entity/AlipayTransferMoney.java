package com.ehu.alipay.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @author AlanSun
 * @Date 2016年8月1日
 * 转账类
 */
@Getter
@Setter
public class AlipayTransferMoney {
    private String detailData;
    /**
     * 总金额
     */
    private String batchFee;
    /**
     * 交易总笔数
     */
    private int batchNum;
    /**
     * 回调地址
     */
    private String notifyUrl;
}
