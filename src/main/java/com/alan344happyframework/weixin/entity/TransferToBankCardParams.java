package com.alan344happyframework.weixin.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @author alan
 * @createtime 18-8-28 下午2:05 * 付款到银行卡参数
 */
@Getter
@Setter
public class TransferToBankCardParams extends Mch {
    /**
     * 商户订单号，需保持唯一（只允许数字[0~9]或字母[A~Z]和[a~z]，最短8位，最长32位）
     * 最长32
     * <p>
     * 必填
     */
    private String partnerTradeNo;

    /**
     * 收款方银行卡号
     * 最长64
     * <p>
     * 必填
     */
    private String encBankNo;

    /**
     * 收款方用户名
     * 最长64
     * <p>
     * 必填
     */
    private String encTrueName;

    /**
     * 银行卡所在开户行编号
     * 银行名称       银行ID
     * 工商银行       1002
     * 农业银行       1005
     * 中国银行       1026
     * 建设银行       1003
     * 招商银行       1001
     * 邮储银行       1066
     * 交通银行       1020
     * 浦发银行       1004
     * 民生银行       1006
     * 兴业银行       1009
     * 平安银行       1010
     * 中信银行       1021
     * 华夏银行       1025
     * 广发银行       1027
     * 光大银行       1022
     * 北京银行       1032
     * 宁波银行       1056
     */
    private String bankCode;

    /**
     * 金额 两位小数 单位：元
     * 最高5万
     * <p>
     * 必填
     */
    private String amount;
    /**
     * 企业付款到银行卡付款说明,即订单备注（UTF8编码，允许100个字符以内）
     * 最长100
     * <p>
     * 可选
     */
    private String desc;
}
