package com.ehu.alipay.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ehu.alipay.entity.AlipayOrder;
import com.ehu.alipay.entity.AlipayRefundOrder;
import com.ehu.alipay.entity.AlipayTransferMoney;
import com.ehu.alipay.sign.MD5;
import com.ehu.alipay.sign.RSA;
import com.ehu.config.EhPayConfig;
import com.ehu.constants.PayResultCodeConstants;
import com.ehu.constants.PayResultMessageConstants;
import com.ehu.exception.PayException;
import com.ehu.util.DateUtil;
import lombok.extern.slf4j.Slf4j;


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
        List<String> keys = new ArrayList<String>(params.keySet());
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

    public static void verifMD5(Map<String, String> map, String sign) throws PayException {
        EhPayConfig config = EhPayConfig.getInstance();
        String prestr = AlipayFunction.createLinkString(map);
        String sign2 = MD5.sign(prestr, config.getAlipay_md5_key(), config.getAlipay_input_charset());
        if (!sign.equals(sign2)) {
            log.info("签名错误：支付宝返回信息可能被篡改" + map.toString() + sign + sign2);
            throw new PayException(PayResultCodeConstants.ERROR_CODE_ALIPAY_10006, PayResultMessageConstants.STRING_ALIPAY_10006);
        }
    }

//	/** 
//	 * 生成文件摘要
//	 * @param strFilePath 文件路径
//	 * @param file_digest_type 摘要算法
//	 * @return 文件摘要结果
//	 */
//	public static String getAbstract(String strFilePath, String file_digest_type) throws IOException {
//		PartSource file = new FilePartSource(new File(strFilePath));
//		if(file_digest_type.equals("MD5")){
//			return DigestUtils.md5Hex(file.createInputStream());
//		}
//		else if(file_digest_type.equals("SHA")) {
//			return DigestUtils.sha256Hex(file.createInputStream());
//		}
//		else {
//			return "";
//		}
//	}

    /**
     * 对字符串生成校验码
     *
     * @param str
     * @return
     * @throws Exception
     */
    public static String createSign(String str) throws Exception {
        EhPayConfig config = EhPayConfig.getInstance();
        String sign = RSA.sign(str, config.getAlipay_private_key(), config.getAlipay_input_charset());
        // 仅需对sign 做URL编码
        try {
            sign = URLEncoder.encode(sign, config.getAlipay_input_charset());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new Exception("创建校验码失败", e);
        }

        return sign;
    }
//    /**
//     * MAP类型数组转换成NameValuePair类型
//     * @param properties  MAP类型数组
//     * @return NameValuePair类型数组
//     */
//    public static NameValuePair[] generatNameValuePair(Map<String, String> properties) {
//        NameValuePair[] nameValuePair = new NameValuePair[properties.size()];
//        int i = 0;
//        for (Map.Entry<String, String> entry : properties.entrySet()) {
//            nameValuePair[i++] = new NameValuePair(entry.getKey(), entry.getValue());
//        }
//
//        return nameValuePair;
//    }

    /**
     * 创建支付宝订单信息
     *
     * @param order 订单信息
     * @return 支付宝订单信息字符串
     * @throws Exception
     */
    public static String getOrderInfo(AlipayOrder order) throws Exception {
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
        map.put("notify_url", config.getAlipay_notify_url());
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
        Date date = new Date();
        //把请求参数打包成数组
        Map<String, String> sParaTemp = new HashMap<String, String>();
        sParaTemp.put("service", "batch_trans_notify");
        sParaTemp.put("partner", config.getAlipay_partner());
        sParaTemp.put("_input_charset", config.getAlipay_input_charset());
        sParaTemp.put("notify_url", config.getAlipay_notify_url());
        sParaTemp.put("email", config.getAlipay_seller());
        sParaTemp.put("account_name", config.getAlipay_account_name());
        sParaTemp.put("pay_date", DateUtil.formatDate(date, "yyyyMMdd"));
        sParaTemp.put("batch_no", DateUtil.formatDate(date, "yyyyMMddHHmmss"));
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

    public static Map<String, String> getQueryMap(String out_trade_no) {
        EhPayConfig config = EhPayConfig.getInstance();
        //把请求参数打包成数组
        Map<String, String> sParaTemp = new HashMap<String, String>();
        sParaTemp.put("service", "single_trade_query");
        sParaTemp.put("partner", config.getAlipay_partner());
        sParaTemp.put("_input_charset", config.getAlipay_input_charset());
        //sParaTemp.put("trade_no", "2016080921001004110290638130");
        sParaTemp.put("out_trade_no", out_trade_no);
        return sParaTemp;
    }

}
