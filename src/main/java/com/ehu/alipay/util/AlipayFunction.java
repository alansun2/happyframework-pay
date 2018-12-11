package com.ehu.alipay.util;

import com.ehu.alipay.entity.AlipayOrder;
import com.ehu.alipay.entity.AlipayRefundOrder;
import com.ehu.alipay.entity.AlipayTransferMoney;
import com.ehu.config.EhPayConfig;
import com.ehu.util.RSAUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


/**
 * 类名：AlipayFunction
 * 功能：支付宝接口公用函数类
 * 详细：公用函数处理文件
 * 日期：2015-01-10
 */
@Slf4j
public class AlipayFunction {
    /**
     * 除去数组中的空值和签名参数
     *
     * @param sArray 签名参数组
     * @return 去掉空值与签名参数后的新签名参数组
     */
    static Map<String, String> paraFilter(Map<String, String> sArray) {

        Map<String, String> result = new HashMap<>();

        if (sArray == null || sArray.size() <= 0) {
            return result;
        }

        for (String key : sArray.keySet()) {
            String value = sArray.get(key);
            if (value == null || value.equals("") || key.equalsIgnoreCase("sign")
                    || key.equalsIgnoreCase("sign_type")) {
                continue;
            }
            result.put(key, value);
        }

        return result;
    }

    /**
     * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
     *
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    public static String createLinkString(Map<String, String> params) {

        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);

        StringBuilder preStr = new StringBuilder();

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);

            if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
                preStr.append(key).append("=").append(value);
            } else {
                preStr.append(key).append("=").append(value).append("&");
            }
        }

        return preStr.toString();
    }

    /**
     * 对字符串生成校验码
     *
     * @param str 待签名串
     * @return 签名
     * @throws Exception e
     */
    public static String createSign(String str) throws Exception {
        EhPayConfig config = EhPayConfig.getInstance();
        String sign = RSAUtils.sign(str.getBytes(config.getAlipay_input_charset()), config.getAlipay_private_key(), RSAUtils.SIGNATURE_ALGORITHM_SHA1);
        // 仅需对sign 做URL编码
        sign = URLEncoder.encode(sign, config.getAlipay_input_charset());

        return sign;
    }

    /**
     * 创建支付宝订单信息
     *
     * @param order 订单信息
     * @return 支付宝订单信息字符串
     * @throws UnsupportedEncodingException e
     */
    public static String getOrderInfo(AlipayOrder order) throws UnsupportedEncodingException {
        EhPayConfig config = EhPayConfig.getInstance();
        return "partner=\"" +
                config.getAlipay_partner() +
                "\"&out_trade_no=\"" +
                order.getOrderId() +
                "\"&subject=\"" +
                order.getSubject() +
                "\"&body=\"" +
                order.getBody() +
                "\"&total_fee=\"" +
                order.getPrice() +
                "\"&notify_url=\"" +
                URLEncoder.encode(order.getNotifyUrl(), config.getAlipay_input_charset()) +
                "\"&service=\"mobile.securitypay.pay" +
                "\"&_input_charset=\"" +
                config.getAlipay_input_charset() +
                "\"&payment_type=\"1" +
                "\"&seller_id=\"" +
                config.getAlipay_seller() +
                "\"&it_b_pay=\"" +
                config.getAlipay_time_out() +
                "\"";
    }


    /**
     * 创建支付宝退款订单信息
     *
     * @param refundOrder 订单信息
     * @return 支付宝退款订单信息Map
     * @throws Exception e
     */
    public static Map<String, String> getRefundInfoMap(AlipayRefundOrder refundOrder) throws Exception {

        EhPayConfig config = EhPayConfig.getInstance();
        Map<String, String> map = new HashMap<>();
        // 接口名称， 固定值
        map.put("service", "refund_fastpay_by_platform_pwd");
        // 合作者身份ID
        map.put("partner", config.getAlipay_partner());
        // 参数编码， 固定值
        map.put("_input_charset", config.getAlipay_input_charset());
        // 服务器异步通知页面路径，网址需要做URL编码
        map.put("notify_url", refundOrder.getNotifyUrl());
        // 退款时间
        map.put("refund_date", refundOrder.getRefundDate());
        // 退款批次号
        map.put("batch_no", refundOrder.getBatchNo());
        // 退款总笔数
        map.put("batch_num", refundOrder.getBatchNum() + "");
        // 单笔数据集
        map.put("detail_data", refundOrder.getDetailData());
        // 卖家支付宝账号
        map.put("seller_email", config.getAlipay_seller());

        return map;
    }

    /**
     * 创建支付宝批量转账信息
     *
     * @param alipayTransferMoney {@link AlipayTransferMoney}
     * @return map
     */
    public static Map<String, String> getTransferMoneyMap(AlipayTransferMoney alipayTransferMoney) {
        EhPayConfig config = EhPayConfig.getInstance();
        LocalDateTime now = LocalDateTime.now();
        //把请求参数打包成数组
        Map<String, String> sParaTemp = new HashMap<>();
        sParaTemp.put("service", "batch_trans_notify");
        sParaTemp.put("partner", config.getAlipay_partner());
        sParaTemp.put("_input_charset", config.getAlipay_input_charset());
        sParaTemp.put("notify_url", alipayTransferMoney.getNotifyUrl());
        sParaTemp.put("email", config.getAlipay_seller());
        sParaTemp.put("account_name", config.getAlipay_account_name());
        sParaTemp.put("pay_date", now.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        sParaTemp.put("batch_no", now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
        sParaTemp.put("batch_fee", alipayTransferMoney.getBatchFee());
        //批量付款笔数（最少1笔，最多1000笔）
        sParaTemp.put("batch_num", alipayTransferMoney.getBatchNum() + "");
        //付款的详细数据，最多支持1000笔。
        //格式为：流水号1^收款方账号1^收款账号姓名1^付款金额1^备注说明1|流水号2^收款方账号2^收款账号姓名2^付款金额2^备注说明2。
        //每条记录以“|”间隔。
        //流水号不能超过64字节，收款方账号小于100字节，备注不能超过200字节。当付款方为企业账户，且转账金额达到（大于等于）50000元，备注不能为空
        //举例:0315006^testture0002@126.com^常炜买家^20.00^hello

        sParaTemp.put("detail_data", alipayTransferMoney.getDetailData());
        return sParaTemp;
    }

    /**
     * 获取授权参数
     *
     * @return map
     */
    public static Map<String, String> getAuthMap() {
        EhPayConfig config = EhPayConfig.getInstance();
        Map<String, String> map = new HashMap<>();
        map.put("apiname", "com.alipay.account.auth");
        map.put("method", "alipay.open.auth.sdk.code.get");
        map.put("app_id", config.getAlipay_app_id());
        map.put("app_name", "mc");
        map.put("biz_type", "openservice");
        map.put("pid", config.getAlipay_app_id());
        map.put("product_id", "APP_FAST_LOGIN");
        map.put("scope", "kuaijie");
        map.put("target_id", UUID.randomUUID().toString().replace("-", ""));
        map.put("auth_type", "AUTHACCOUNT");
        map.put("sign_type", "RSA");
        return map;
    }
}