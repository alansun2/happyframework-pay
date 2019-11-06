package com.alan344happyframework.bean;

import com.alan344happyframework.weixin.entity.WechatBusinessPay;
import com.alan344happyframework.weixin.entity.WechatQueryTransferBuiness;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * @author AlanSun
 * @date 2019/9/2 16:03
 */
@Getter
@Setter
public class QueryTransferMoneyInternal extends PayBase {
    /**
     * 商户端转账唯一标识,必填
     * <p>
     * 支付宝说明： 商户转账唯一订单号。发起转账来源方定义的转账单据ID，用于将转账回执通知给来源方。不同来源方给出的ID可以重复，同一个来源方必须保证其ID的唯一性。只支持半角英文、数字，及“-”、“_”，最大长度64
     */
    @NotBlank
    private String transferId;

    private WechatBusinessPay wechatBusinessPay = new WechatBusinessPay();
}
