package com.ehu.pay.alipay;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayDataDataserviceBillDownloadurlQueryRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayDataDataserviceBillDownloadurlQueryResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.ehu.pay.alipay.entity.*;
import com.ehu.pay.alipay.util.AlipayFunction;
import com.ehu.pay.alipay.util.AlipayNotify;
import com.ehu.pay.config.EhPayConfig;
import com.ehu.pay.constants.BaseConstants;
import com.ehu.pay.constants.PayResultCodeConstants;
import com.ehu.pay.constants.PayResultMessageConstants;
import com.ehu.pay.exception.PayException;
import com.ehu.pay.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;


/**
 * 类名：AlipayUtils
 * 功能：支付宝接口公用工具类
 * 详细：该类是请求、通知返回两个文件所调用的公用函数核心处理文件
 * 日期：2015-01-10
 */
@Slf4j
public class AlipayUtils {

    public static EhPayConfig config = EhPayConfig.getInstance();
    public static AlipayClient alipayClient = new DefaultAlipayClient(config.getAlipay_open_api(), config.getAlipay_app_id(), config.getAlipay_private_key(), "json", config.getAlipay_input_charset(), config.getAlipay_open_public_key());

    /**
     * 创建支付宝订单支付信息（无线）
     *
     * @param order 订单信息
     * @return 支付宝订单支付信息（无线）
     * @throws Exception
     */
    public static String createPayInfo(AlipayOrder order) throws Exception {
        String orderInfo = AlipayFunction.getOrderInfo(order);
        String sign = AlipayFunction.createSign(orderInfo);
        String payInfo = orderInfo + "&sign=\"" + sign + "\"&sign_type=\"" + EhPayConfig.getInstance().getAlipay_sign_type() + "\"";
        return payInfo;
    }

    /**
     * 验证消息是否是支付宝发出的合法消息
     *
     * @param params 通知返回来的参数数组
     * @return 验证结果
     */
    public static boolean verify(Map<String, String> params, Integer ex) {
        try {
            boolean rsaCheckV2 = AlipaySignature.rsaCheckV2(params, config.getAlipay_public_key(), config.getAlipay_input_charset());
            return rsaCheckV2;
        } catch (AlipayApiException e) {
            log.error("alipay verify fail", e);
            return false;
        }
    }

    /**
     * 验证消息是否是支付宝发出的合法消息
     *
     * @param params 通知返回来的参数数组
     * @return 验证结果
     */
    public static boolean verify(Map<String, String> params) {

        //判断responsetTxt是否为true，isSign是否为true
        //responsetTxt的结果不是true，与服务器设置问题、合作身份者ID、notify_id一分钟失效有关
        //isSign不是true，与安全校验码、请求时的参数格式（如：带自定义参数等）、编码格式有关
        String responseTxt = "true";
        if (params.get("notify_id") != null) {
            String notify_id = params.get("notify_id");
            responseTxt = AlipayNotify.verifyResponse(notify_id);
        }
        String sign = "";
        if (params.get("sign") != null) {
            sign = params.get("sign");
        }
        boolean isSign = AlipayNotify.getSignVeryfy(params, sign);

        //写日志记录（若要调试，请取消下面两行注释）
        //String sWord = "responseTxt=" + responseTxt + "\n isSign=" + isSign + "\n 返回回来的参数：" + AlipayCore.createLinkString(params);
        //AlipayCore.logResult(sWord);

        if (isSign && "true".equals(responseTxt)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取批量退款URL(网页)
     *
     * @param alipayRefundOrder
     * @return 批量退款URL
     * @throws Exception
     */
    public static String alipayRefund(AlipayRefundOrder alipayRefundOrder) throws Exception {
        EhPayConfig config = EhPayConfig.getInstance();
        Map<String, String> orderInfo = AlipayFunction.getRefundInfoMap(alipayRefundOrder);
        String prestr = AlipayFunction.createLinkString(orderInfo);
        String sign = AlipayFunction.createSign(prestr);
        orderInfo.put("notify_url", URLEncoder.encode(orderInfo.get("notify_url"), config.getAlipay_input_charset()));
        orderInfo.put("detail_data", URLEncoder.encode(orderInfo.get("detail_data"), config.getAlipay_input_charset()));
        String linkstr = AlipayFunction.createLinkString(orderInfo);
        String refundurl = config.getAlipay_gateway_url() + linkstr + "&sign=" + sign + "&sign_type=" + config.getAlipay_sign_type();
        return refundurl;
    }

    /**
     * 获取批量转账url
     *
     * @param alipayTransferMoney
     * @return String
     * @throws Exception
     */
    public static String alipayTransferMoney(AlipayTransferMoney alipayTransferMoney) throws Exception {
        EhPayConfig config = EhPayConfig.getInstance();
        Map<String, String> orderInfo = AlipayFunction.getTransferMoneyMap(alipayTransferMoney);
        String prestr = AlipayFunction.createLinkString(orderInfo);
        String sign = AlipayFunction.createSign(prestr);
        orderInfo.put("notify_url", URLEncoder.encode(orderInfo.get("notify_url"), config.getAlipay_input_charset()));
        orderInfo.put("detail_data", URLEncoder.encode(orderInfo.get("detail_data"), config.getAlipay_input_charset()));
        String linkstr = AlipayFunction.createLinkString(orderInfo);
        String transferUrl = config.getAlipay_gateway_url() + linkstr + "&sign=" + sign + "&sign_type=" + config.getAlipay_sign_type();
        return transferUrl;
    }

    /**
     * 线下支付：扫码支付
     *
     * @return
     * @throws AlipayApiException
     * @throws PayException
     */
    public static String scanPay(ScanPayOrder scanPayOrder) throws PayException {
        EhPayConfig config = EhPayConfig.getInstance();
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();//创建API对应的request类
        request.setNotifyUrl(config.getAlipay_scan_notify_url());
        request.setBizContent("{" +
                "\"out_trade_no\":\"" + scanPayOrder.getOutTradeNo() + "\"," +
                "\"total_amount\":" + scanPayOrder.getTotalAmount() + "," +
                "\"subject\":\"" + scanPayOrder.getSubject() + "\"," +
                "\"body\":\"" + scanPayOrder.getBody() + "\"," +
                "\"store_id\":\"" + scanPayOrder.getStoreId() + "\"," +
                "\"timeout_express\":\"90m\"}");//设置业务参数
        //根据response中的结果继续业务逻辑处理
        AlipayTradePrecreateResponse response = null;
        try {
            response = alipayClient.execute(request);
            if (BaseConstants.ALIPAY_RETURN_CODE_10000.equals(response.getCode())) {
                return response.getQrCode();
            }
        } catch (Exception e) {
            log.error("支付宝扫码错误", e);
            throw new PayException(PayResultCodeConstants.ALIPAY_SCAN_ERROR_30001, PayResultMessageConstants.ALIPAY_SCAN_ERROR_30001);
        }
        return null;
    }

    /**
     * 支付宝退款
     *
     * @param alipayRefund
     * @return
     * @throws PayException
     */
    public static boolean aliPayRefund(AlipayRefund alipayRefund) throws PayException {
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();//创建API对应的request类
        StringBuffer sb = new StringBuffer();
        String jsonString = JSONObject.toJSONString(alipayRefund);
        request.setBizContent(jsonString); //设置业务参数

        try {
            AlipayTradeRefundResponse response = alipayClient.execute(request);
            log.info("支付宝号：" + response.getTradeNo() + "此次退款金额：" + response.getRefundFee() + "退款时间：" + response.getGmtRefundPay() + "用户登录id:" + response.getBuyerLogonId());
            if (response.isSuccess()) {
                if (BaseConstants.ALIPAY_RETURN_CODE_10000.equals(response.getCode())) {
                    return true;
                } else {
                    log.error("支付宝扫码退款失败,code:" + response.getCode() + "subCode" + response.getSubCode() + "subMsg" + response.getSubMsg());
                    return false;
//					throw new PayException(PayResultCodeConstants.ALIPAY_SCAN_ERROR_30002, PayResultMessageConstants.ALIPAY_SCAN_ERROR_30002);
                }
            } else {
                log.error("支付宝扫码退款失败code:" + response.getCode());
                return false;
//				throw new PayException(PayResultCodeConstants.ALIPAY_SCAN_ERROR_30002, PayResultMessageConstants.ALIPAY_SCAN_ERROR_30002);
            }
        } catch (AlipayApiException e) {
            log.error("支付宝扫码退款失败", e);
            return false;
//			throw new PayException(PayResultCodeConstants.ALIPAY_SCAN_ERROR_30002, PayResultMessageConstants.ALIPAY_SCAN_ERROR_30002);
        }//通过alipayClient调用API，获得对应的response类
    }

    /**
     * 查询订单状态
     *
     * @param outTradeNo  商家订单号
     * @param tradeStatus 交易状态
     * @return
     * @throws PayException
     */
    public static boolean queryOrderStatus(String outTradeNo, String tradeStatus) throws PayException {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        request.setBizContent("{" +
                "\"out_trade_no\":\"" + outTradeNo + "\"" +
                "}");
        AlipayTradeQueryResponse response = null;
        try {
            response = alipayClient.execute(request);
            if (response.isSuccess()) {
                if (response.getTradeStatus() != null && tradeStatus.equalsIgnoreCase(response.getTradeStatus())) {
                    return Boolean.TRUE;
                } else {
                    log.error("支付宝查询订单状态失败", response);
                    return Boolean.FALSE;
                }
            } else {
                if ("ACQ.TRADE_NOT_EXIST".equals(response.getSubCode())) {
                    throw new PayException(PayResultCodeConstants.TRADE_NOT_EXIST_30005, PayResultMessageConstants.TRADE_NOT_EXIST_30005);
                }
                log.error("支付宝查询订单状态失败" + response.getCode() + response.getSubMsg());
                throw new PayException(PayResultCodeConstants.ALIPAY_SCAN_ERROR_30003, PayResultMessageConstants.ALIPAY_SCAN_ERROR_30003);
            }
        } catch (AlipayApiException e) {
            log.error("支付宝查询订单状态失败", e);
            throw new PayException(PayResultCodeConstants.ALIPAY_SCAN_ERROR_30003, PayResultMessageConstants.ALIPAY_SCAN_ERROR_30003);
        }
    }

    /**
     * 日期 只支持 yyyy-MM-dd 与yyyy-MM
     * if aliSrcPath is null then just return downloadUrl
     * otherwise return downloadUrl and download the file
     *
     * @param time
     * @param aliSrcPath downloanUrl
     * @throws PayException
     */
    public static String getFinancial(String time, String aliSrcPath) throws PayException {
        AlipayDataDataserviceBillDownloadurlQueryRequest request = new AlipayDataDataserviceBillDownloadurlQueryRequest();
        request.setBizContent("{" +
                "\"bill_type\":\"trade\"," +
                "\"bill_date\":\"" + time + "\"" +
                "}");
        AlipayDataDataserviceBillDownloadurlQueryResponse response;
        try {
            response = alipayClient.execute(request);
            if (response.isSuccess()) {
                try {
                    if (!StringUtils.isEmpty(aliSrcPath))
                        FileUtils.copyURLToFile(new URL(response.getBillDownloadUrl()), new File(aliSrcPath), 10000, 10000);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    log.error("获取财务账单url失败", e);
                    return response.getBillDownloadUrl();
                }
                return response.getBillDownloadUrl();
            } else {
                log.error("获取财务失败", response.getCode() + response.getMsg());
                return null;
            }
        } catch (AlipayApiException e) {
            log.error("获取财务账单url失败", e);
            throw new PayException(PayResultCodeConstants.GET_FINANCIAL_30013, PayResultMessageConstants.GET_FINANCIAL_30013);
        }
    }
}
