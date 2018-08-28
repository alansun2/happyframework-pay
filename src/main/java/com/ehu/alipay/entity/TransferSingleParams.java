package com.ehu.alipay.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @author alan
 * @createtime 18-8-28 上午10:09 * 单笔转账到支付宝账户请求类
 */
@Getter
@Setter
public class TransferSingleParams {
    /**
     * 商户转账唯一订单号。发起转账来源方定义的转账单据ID，用于将转账回执通知给来源方。不同来源方给出的ID可以重复，同一个来源方必须保证其ID的唯一性。只支持半角英文、数字，及“-”、“_”。
     * 最大长度64
     * <p>
     * 必填
     */
    private String outBizNo;

    /**
     * 1、ALIPAY_USERID：支付宝账号对应的支付宝唯一用户号。以2088开头的16位纯数字组成。
     * 2、ALIPAY_LOGONID：支付宝登录号，支持邮箱和手机号格式。
     * 最大长度20
     * <p>
     * 必填
     * <p>
     * 默认 ALIPAY_LOGONID
     */
    private String payeeType = "ALIPAY_LOGONID";

    /**
     * 收款方账户。与payee_type配合使用。付款方和收款方不能是同一个账户。
     * 最大长度100
     * <p>
     * 必填
     */
    private String payeeAccount;

    /**
     * 付款方姓名（最长支持100个英文/50个汉字）。显示在收款方的账单详情页。如果该字段不传，则默认显示付款方的支付宝认证姓名或单位名称。
     * 最大长度100
     * <p>
     * 可选
     */
    private String payerShowName;

    /**
     * 收款方真实姓名（最长支持100个英文/50个汉字）。
     * 如果本参数不为空，则会校验该账户在支付宝登记的实名是否与收款方真实姓名一致。
     * 最大长度100
     * <p>
     * 可选
     */
    private String payeeRealName;

    /**
     * 转账备注（支持200个英文/100个汉字）。
     * 当付款方为企业账户，且转账金额达到（大于等于）50000元，remark不能为空。收款方可见，会展示在收款用户的收支详情中。
     * 最大长度200
     * <p>
     * 可选
     * 当付款方为企业账户，且转账金额达到 >= 50000元 必填
     */
    private String remark;
}
