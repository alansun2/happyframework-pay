package com.alan344happyframework.bean;

import com.alipay.api.domain.AlipayDataDataserviceBillDownloadurlQueryModel;
import com.alan344happyframework.weixin.entity.WechatFinancialReport;
import lombok.Getter;
import lombok.Setter;

/**
 * @author AlanSun
 * @date 2019/7/16 9:52
 * <p>
 * 获取财务报告
 **/
@Getter
@Setter
public class FinancialReport extends PayBase {
    /**
     * 下载哪一天的账单
     * <p>
     * 格式：2016-04-05
     * <p>
     * 支付宝支持月账单：格式为yyyy-MM
     */
    private String data;
    /**
     * 下载文件存放绝对路径 （包含文件名）
     */
    private String desPath;

    private WechatFinancialReport wechatFinancialReport = new WechatFinancialReport();

    private AlipayDataDataserviceBillDownloadurlQueryModel alipayDataDataserviceBillDownloadurlQueryModel;
}
