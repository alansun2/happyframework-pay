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
    public static Map<String, String> paraFilter(Map<String, String> sArray) {

        Map<String, String> result = new HashMap<String, String>();

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

        params = paraFilter(params);
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);

        String prestr = "";

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);

            if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            } else {
                prestr = prestr + key + "=" + value + "&";
            }
        }

        return prestr;
    }

    /**
     * 对字符串生成校验码
     *
     * @param str
     * @return
     * @throws Exception
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
     * @throws Exception
     */
    public static String getOrderInfo(AlipayOrder order) throws UnsupportedEncodingException {
        EhPayConfig config = EhPayConfig.getInstance();
        StringBuilder sb = new StringBuilder();
        // 合作者身份ID
        sb.append("partner=\"");
        sb.append(config.getAlipay_partner());
        // 商户网站唯一订单号
        sb.append("\"&out_trade_no=\"");
        sb.append(order.getOrderId());
        // 商品名称
        sb.append("\"&subject=\"");
        sb.append(order.getSubject());
        // 商品详情
        sb.append("\"&body=\"");
        sb.append(order.getBody());
        // 商品金额
        sb.append("\"&total_fee=\"");
        sb.append(order.getPrice());
        // 服务器异步通知页面路径，网址需要做URL编码
        sb.append("\"&notify_url=\"");
        sb.append(URLEncoder.encode(order.getNotifyUrl(), config.getAlipay_input_charset()));
        // 接口名称， 固定值
        sb.append("\"&service=\"mobile.securitypay.pay");
        // 参数编码， 固定值
        sb.append("\"&_input_charset=\"");
        sb.append(config.getAlipay_input_charset());
        // 		支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        //		sb.append("\"&return_url=\"");
        //		sb.append(URLEncoder.encode("http://m.alipay.com"));
        // 支付类型， 固定值
        sb.append("\"&payment_type=\"1");
        // 卖家支付宝账号
        sb.append("\"&seller_id=\"");
        sb.append(config.getAlipay_seller());
        // 如果show_url值为空，可不传
        // sb.append("\"&show_url=\"");
        sb.append("\"&it_b_pay=\"");
        sb.append(config.getAlipay_time_out());
        sb.append("\"");
        return sb.toString();
    }


    /**
     * 创建支付宝退款订单信息
     *
     * @param refundOrder 订单信息
     * @return 支付宝退款订单信息Map
     * @throws Exception
     */
    public static Map<String, String> getRefundInfoMap(AlipayRefundOrder refundOrder) throws Exception {

        EhPayConfig config = EhPayConfig.getInstance();
        Map<String, String> map = new HashMap<String, String>();
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
     * @param alipayTransferMoney
     * @return
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
}