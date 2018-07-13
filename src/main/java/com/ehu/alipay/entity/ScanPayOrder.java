package com.ehu.alipay.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @author AlanSun
 * @Date 2016年11月4日
 */
@Getter
@Setter
public class ScanPayOrder {
    /**
     * 商户订单号,64个字符以内、只能包含字母、数字、下划线；需保证在商户端不重复
     */
    private String outTradeNo;
    /**
     * 订单总金额，单位为元，精确到小数点后两位，
     * 取值范围[0.01,100000000] 如果同时
     * 传入了【打折金额】，【不可打折金额】，【订
     * 单总金额】三者，则必须满足如下条件：【订单
     * 总金额】=【打折金额】+【不可打折金额】
     */
    private double totalAmount;
    /**
     * 订单标题
     */
    private String subject;
    /**
     * 商品描述
     */
    private String body;
    /**
     * 门店id
     */
    private String storeId;
    /**
     * 回调地址
     */
    private String notifyUrl;
}
