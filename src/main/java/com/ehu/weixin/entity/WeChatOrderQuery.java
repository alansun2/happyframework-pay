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
public class WeChatOrderQuery extends Mch {
    /**
     * 商户订单号
     */
    private String outTradeNo;
    /**
     * 如果该值不填写则返回订单状态，如果填写了该值，则会比较订单状态和该值是否相等，相等返回true，否则返回异常
     */
    private String queryFlag;
}
