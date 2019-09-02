package com.alan344happyframework.bean;

import com.alan344happyframework.weixin.entity.WechatBusinessPay;
import com.alipay.api.domain.AlipayFundTransToaccountTransferModel;
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
public class TransferMoneyInternal extends QueryTransferMoneyInternal {
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

    private AlipayFundTransToaccountTransferModel fundTransToaccountTransferModel;
}
