package com.ehu.alipay;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayResponse;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import com.ehu.alipay.entity.*;
import com.ehu.alipay.util.AlipayFunction;
import com.ehu.alipay.util.AlipayNotify;
import com.ehu.bean.LowerUnderscoreFilter;
import com.ehu.bean.PayResponse;
import com.ehu.config.EhPayConfig;
import com.ehu.constants.PayBaseConstants;
import com.ehu.constants.PayResultCodeConstants;
import com.ehu.constants.PayResultMessageConstants;
import com.ehu.exception.PayException;
import com.ehu.util.StringUtils;
import com.github.rholder.retry.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


/**
 * 类名：AlipayUtils
 * 功能：支付宝接口公用工具类
 * 详细：该类是请求、通知返回两个文件所调用的公用函数核心处理文件
 * 日期：2015-01-10
 */
@Slf4j
public class AlipayUtils {

    public static EhPayConfig config = EhPayConfig.getInstance();
    private static AlipayClient alipayClient = new DefaultAlipayClient(config.getAlipay_open_api(), config.getAlipay_app_id(), config.getAlipay_private_key(), "json", config.getAlipay_input_charset(), config.getAlipay_open_public_key());

    /**
     * 创建支付宝订单支付信息（无线）
     *
     * @param order 订单信息
     * @return 支付宝订单支付信息（无线）
     */
    public static String createPayInfo(AlipayOrder order) throws PayException {
        String sign;
        String orderInfo;
        try {
            orderInfo = AlipayFunction.getOrderInfo(order);
            sign = AlipayFunction.createSign(orderInfo);
        } catch (Exception e) {
            throw new PayException("生成支付失败");
        }
        return orderInfo + "&sign=\"" + sign + "\"&sign_type=\"" + config.getAlipay_sign_type() + "\"";
    }

    /**
     * 创建支付宝订单支付信息（无线）
     *
     * @param order 订单信息
     * @return 支付宝订单支付信息（无线）
     * @throws Exception e
     */
    public static String createPayInfo1(AlipayOrder order) throws Exception {
/*        //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setBody(order.getBody());
        model.setSubject(order.getSubject());
        model.setOutTradeNo(order.getOrderId());
        model.setTimeoutExpress(order.getTimeoutExpress());
        model.setTotalAmount(order.getPrice());
        model.setProductCode("QUICK_MSECURITY_PAY");//固定值
        request.setBizModel(model);
        request.setNotifyUrl(config.getAlipay_second_hand_notify_url());
        try {
            //这里和普通的接口调用不同，使用的是sdkExecute
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
            log.info(response.getBody());//就是orderString 可以直接给客户端请求，无需再做处理。
            return response.getBody();
        } catch (AlipayApiException e) {
            log.error("构建支付信息失败：", e);
            throw new PayException("构建支付信息失败,请重试");
        }*/
        return null;
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
        return isSign && "true".equals(responseTxt);
    }

    /**
     * 获取批量退款URL(网页)
     *
     * @param alipayRefundOrder alipayRefundOrder
     * @return 批量退款URL
     * @throws Exception e
     */
    public static String alipayRefund(AlipayRefundOrder alipayRefundOrder) throws Exception {
        EhPayConfig config = EhPayConfig.getInstance();
        Map<String, String> orderInfo = AlipayFunction.getRefundInfoMap(alipayRefundOrder);
        String prestr = AlipayFunction.createLinkString(orderInfo);
        String sign = AlipayFunction.createSign(prestr);
        orderInfo.put("notify_url", URLEncoder.encode(orderInfo.get("notify_url"), config.getAlipay_input_charset()));
        orderInfo.put("detail_data", URLEncoder.encode(orderInfo.get("detail_data"), config.getAlipay_input_charset()));
        String linkstr = AlipayFunction.createLinkString(orderInfo);
        return config.getAlipay_gateway_url() + linkstr + "&sign=" + sign + "&sign_type=" + config.getAlipay_sign_type();
    }

    /**
     * 获取批量转账url
     * （1）单日转出累计额度为100万元。
     * （2）转账给个人支付宝账户，单笔最高5万元；转账给企业支付宝账户，单笔最高10万元。
     *
     * @param alipayTransferMoney alipayTransferMoney
     * @return String
     * @throws Exception e
     */
    public static String alipayTransferMoney(AlipayTransferMoney alipayTransferMoney) throws Exception {
        EhPayConfig config = EhPayConfig.getInstance();
        Map<String, String> orderInfo = AlipayFunction.getTransferMoneyMap(alipayTransferMoney);
        String prestr = AlipayFunction.createLinkString(orderInfo);
        String sign = AlipayFunction.createSign(prestr);
        orderInfo.put("notify_url", URLEncoder.encode(orderInfo.get("notify_url"), config.getAlipay_input_charset()));
        orderInfo.put("detail_data", URLEncoder.encode(orderInfo.get("detail_data"), config.getAlipay_input_charset()));
        String linkstr = AlipayFunction.createLinkString(orderInfo);
        return config.getAlipay_gateway_url() + linkstr + "&sign=" + sign + "&sign_type=" + config.getAlipay_sign_type();
    }

    /**
     * 支付宝 单笔转账到支付宝
     *
     * @param params {@link TransferSingleParams}
     * @return
     */
    public static PayResponse<Boolean> transferSingle(TransferSingleParams params) {
        AlipayFundTransToaccountTransferRequest request = new AlipayFundTransToaccountTransferRequest();
        String paramStr = JSON.toJSONString(params, new LowerUnderscoreFilter());
        request.setBizContent(paramStr);

        Callable<AlipayFundTransToaccountTransferResponse> callable = () -> alipayClient.execute(request);
        Retryer<AlipayFundTransToaccountTransferResponse> retryer = RetryerBuilder.<AlipayFundTransToaccountTransferResponse>newBuilder()
                .retryIfException()
                .retryIfResult(response ->
                        response == null
//                                || !response.isSuccess()
                                || "SYSTEM_ERROR".equals(response.getSubCode())
//                                || "40004".equals(response.getCode())
                                || "20000".equals(response.getCode()))
                .withWaitStrategy(WaitStrategies.fixedWait(1, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .build();

        PayResponse<Boolean> response = new PayResponse<>(true);
        AlipayFundTransToaccountTransferResponse call = null;
        try {
            call = retryer.call(callable);
        } catch (ExecutionException | RetryException e) {
            response.setResult(false);
            log.error("alipay transfer error", e);
        }

        responseHandler(response, call);
        return response;
    }

    /**
     * 线下支付：扫码支付获取二维码
     *
     * @return 二维码地址
     * @throws PayException e
     */
    public static String scanPay(ScanPayOrder scanPayOrder) throws PayException {
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();//创建API对应的request类
        request.setNotifyUrl(scanPayOrder.getNotifyUrl());
        request.setBizContent("{" +
                "\"out_trade_no\":\"" + scanPayOrder.getOutTradeNo() + "\"," +
                "\"total_amount\":" + scanPayOrder.getTotalAmount() + "," +
                "\"subject\":\"" + scanPayOrder.getSubject() + "\"," +
                "\"body\":\"" + scanPayOrder.getBody() + "\"," +
                "\"store_id\":\"" + scanPayOrder.getStoreId() + "\"," +
                "\"timeout_express\":\"90m\"}");//设置业务参数
        AlipayTradePrecreateResponse response;
        try {
            response = alipayClient.execute(request);
            if (PayBaseConstants.ALIPAY_RETURN_CODE_10000.equals(response.getCode())) {
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
     * @param alipayRefund alipayRefund
     * @return 退款是否成功
     * @throws PayException e
     */
    public static boolean aliPayRefund(AlipayRefund alipayRefund) throws PayException {
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();//创建API对应的request类
        StringBuilder sb = new StringBuilder();
        sb.append("{" + "\"out_trade_no\":\"").append(alipayRefund.getOutTradeNo()).append("\",");
        if (!StringUtils.isBlank(alipayRefund.getOutRequestNo())) {
            sb.append("\"out_request_no\":\"").append(alipayRefund.getOutRequestNo()).append("\",");
        }
        sb.append("\"refund_amount\":\"").append(alipayRefund.getRefundAmount()).append("\"}");
        request.setBizContent(sb.toString()); //设置业务参数

        try {
            AlipayTradeRefundResponse response = alipayClient.execute(request);
            log.info("支付宝号：" + response.getTradeNo() + "此次退款金额：" + response.getRefundFee() + "退款时间：" + response.getGmtRefundPay() + "用户登录id:" + response.getBuyerLogonId());
            if (response.isSuccess()) {
                if (PayBaseConstants.ALIPAY_RETURN_CODE_10000.equals(response.getCode())) {
                    return true;
                } else {
                    log.error("支付宝扫码退款失败,code:" + response.getCode() + "subCode" + response.getSubCode() + "subMsg" + response.getSubMsg());
                    return false;
                }
            } else {
                log.error("支付宝扫码退款失败code:" + response.getCode());
                return false;
            }
        } catch (AlipayApiException e) {
            log.error("支付宝扫码退款失败", e);
            return false;
        }//通过alipayClient调用API，获得对应的response类
    }

    /**
     * 查询支付状态
     *
     * @param outTradeNo outTradeNo
     * @return 订单状态
     * @throws PayException e
     */
    public static String queryOrderStatus(String outTradeNo) throws PayException {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        request.setBizContent("{" +
                "\"out_trade_no\":\"" + outTradeNo + "\"" +
                "}");
        AlipayTradeQueryResponse response;
        try {
            response = alipayClient.execute(request);
            if (response.isSuccess()) {
                return response.getTradeStatus();
            } else {
                if ("ACQ.TRADE_NOT_EXIST".equals(response.getSubCode())) {
                    throw new PayException(PayResultCodeConstants.TRADE_NOT_EXIST_30005, PayResultMessageConstants.TRADE_NOT_EXIST_30005);
                }
                log.error("支付宝扫码查询失败" + response.getCode() + response.getSubMsg());
                throw new PayException(PayResultCodeConstants.ALIPAY_SCAN_ERROR_30003, PayResultMessageConstants.ALIPAY_SCAN_ERROR_30003);
            }
        } catch (AlipayApiException e) {
            log.error("支付宝扫码查询失败", e);
            throw new PayException(PayResultCodeConstants.ALIPAY_SCAN_ERROR_30003, PayResultMessageConstants.ALIPAY_SCAN_ERROR_30003);
        }
    }

    /**
     * 日期 只支持 yyyy-MM-dd 与yyyy-MM
     * if aliSrcPath is null then just return downloadUrl
     * otherwise return downloadUrl and download the file
     *
     * @param time       time
     * @param aliSrcPath downloanUrl
     * @throws PayException e
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
                    if (!StringUtils.isBlank(aliSrcPath))
                        FileUtils.copyURLToFile(new URL(response.getBillDownloadUrl()), new File(aliSrcPath), 10000, 10000);
                } catch (IOException e) {
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

    //------------------------------------------------------------------------------------------------------------------

    /**
     * 处理支付宝返回参数
     *
     * @param request HttpServletRequest
     * @return paramsMap
     */
    public static Map<String, String> getAlipayCallBackMap(HttpServletRequest request) {
        //获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
            params.put(name, valueStr);
        }
        return params;
    }

    /**
     * 处理支付宝返回结果
     *
     * @param response response
     * @param call     支付宝返回结果
     */
    private static void responseHandler(PayResponse<Boolean> response, AlipayResponse call) {
        if (null == call) {
            response.setResult(false);
            response.setResultMessage("response null error");
        } else if (call.isSuccess()) {
            if (!PayBaseConstants.ALIPAY_RETURN_CODE_10000.equals(call.getCode())) {
                response.setResult(false);
                response.setResultCode(call.getSubCode());
                response.setResultMessage(call.getSubMsg());
                log.error(JSON.toJSONString(call));
            }
        } else {
            response.setResult(false);
            response.setResultCode(call.getSubCode());
            response.setResultMessage(call.getSubMsg());
            log.error(JSON.toJSONString(call));
        }
    }

    /**
     * 支付宝登录时获取返回串
     * 用户支付宝登录
     *
     * @return infoStr
     */
    public String getInfoStrToAuth() throws PayException {
        Map<String, String> authMap = AlipayFunction.getAuthMap();
        String linkString = AlipayFunction.createLinkString(authMap);
        try {
            authMap.put("sign", AlipayFunction.createSign(linkString));
            return AlipayFunction.createLinkString(authMap);
        } catch (Exception e) {
            throw new PayException("签名失败");
        }
    }
}
