package com.alan344happyframework.bean;

import com.alipay.api.domain.AlipayFundTransToaccountTransferModel;
import com.alan344happyframework.weixin.entity.WechatBusinessPay;
import lombok.Getter;
import lombok.Setter;

/**
 * @author AlanSun
 * @date 2019/7/5 9:48
 * <p>
 * 支付宝默认使用的是：ALIPAY_LOGONID（即支付宝登录号转账）。如果该改成“ALIPAY_USERID”（支付宝账号），
 * 请在#fundTransToaccountTransferModel中修改
 * <p>
 * 微信转账需要在绑定商户号
 **/
@Getter
@Setter
public class TransferMoneyInternal extends PayBase {
    /**
     * 商户端转账唯一标识,必填
     * <p>
     * 支付宝说明： 商户转账唯一订单号。发起转账来源方定义的转账单据ID，用于将转账回执通知给来源方。不同来源方给出的ID可以重复，同一个来源方必须保证其ID的唯一性。只支持半角英文、数字，及“-”、“_”，最大长度64
     */
    private String transferId;
    /**
     * 收款方账户/openid
     * <p>
     * 必填
     * <p>
     * 支付宝说明：
     * 收款方账户。与payee_type配合使用。付款方和收款方不能是同一个账户。
     * 最大长度100
     */
    private String payeeAccount;
    /**
     * 实名
     * <p>
     * 可选，不填不校验，填则校验
     */
    private String reUserName;
    /**
     * 转账金额，单位：元。
     * 只支持2位小数，小数点前最大支持13位，金额必须大于等于0.1元。
     * 最大转账金额以实际签约的限额为准。
     * 最长16
     * <p>
     * 必填
     */
    private String amount;
    /**
     * 付款描述 ,必填
     * <p>
     * 最长100
     */
    private String desc;

    private WechatBusinessPay wechatBusinessPay = new WechatBusinessPay();
    private AlipayFundTransToaccountTransferModel fundTransToaccountTransferModel;
}
