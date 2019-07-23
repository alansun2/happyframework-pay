package com.alan344happyframework.weixin.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @author AlanSun
 * @date 2019/6/27 18:38
 **/
@Getter
@Setter
public class WechatFinancialReport extends Mch {
    /**
     * ALL（默认值），返回当日所有订单信息（不含充值退款订单）
     * <p>
     * SUCCESS，返回当日成功支付的订单（不含充值退款订单）
     * <p>
     * REFUND，返回当日退款订单（不含充值退款订单）
     * <p>
     * RECHARGE_REFUND，返回当日充值退款订单
     */
    private String bill_type;
}
