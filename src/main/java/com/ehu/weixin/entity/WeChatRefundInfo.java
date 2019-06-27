package com.ehu.weixin.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @author AlanSun
 * @Date 2016年8月10日
 * 微信退款类
 */
@Setter
@Getter
public class WeChatRefundInfo extends Mch {
    /**
     * 微信订单号
     */
    private String outTradeNo;
    /**
     * 商家退款号
     */
    private String outRefundNo;
    /**
     * 总价格
     */
    private double totalFee;
    /**
     * 退款金额
     */
    private double refundFee;
    /**
     * 密码
     */
    private String password;
}
