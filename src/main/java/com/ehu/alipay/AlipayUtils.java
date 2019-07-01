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
import com.ehu.bean.LowerUnderscoreFilter;
import com.ehu.bean.PayResponse;
import com.ehu.config.AliPay;
import com.ehu.constants.PayBaseConstants;
import com.ehu.constants.PayResultCodeConstants;
import com.ehu.constants.PayResultMessageConstants;
import com.ehu.exception.PayException;
import com.ehu.util.StringUtils;
import com.github.rholder.retry.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


/**
 * 类名：AlipayUtils
 * 功能：支付宝接口公用工具类
 * 详细：该类是请求、通知返回两个文件所调用的公用函数核心处理文件
 * 日期：2015-01-10
 *
 * @author AlanSun
 */
@Slf4j
public class AlipayUtils {

    public static final AliPay config = AliPay.getInstance();
    private static AlipayClient alipayClient = new DefaultAlipayClient(config.getOpenApi(), config.getAppId(), config.getPrivateKey(), "json", config.getInputCharset(), config.getOpenPublicKey());

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
        return orderInfo + "&sign=\"" + sign + "\"&sign_type=\"" + config.getSignType() + "\"";
    }

    /**
     * 创建支付宝订单支付信息（无线）
     *
     * @param order 订单信息
     * @return 支付宝订单支付信息（无线）
     * @throws Exception e
     */
    public static String createPayInfo1(AlipayOrder order) throws Exception {
        return null;
    }

    /**
     * 获取批量退款URL(网页)
     *
     * @param alipayRefundOrder alipayRefundOrder
     * @return 批量退款URL
     * @throws Exception e
     */
    public static String alipayRefund(AlipayRefundOrder alipayRefundOrder) throws Exception {
        Map<String, String> orderInfo = AlipayFunction.getRefundInfoMap(alipayRefundOrder);
        String prestr = AlipayFunction.createLinkString(orderInfo);
        String sign = AlipayFunction.createSign(prestr);
        orderInfo.put("notify_url", URLEncoder.encode(orderInfo.get("notify_url"), config.getInputCharset()));
        orderInfo.put("detail_data", URLEncoder.encode(orderInfo.get("detail_data"), config.getInputCharset()));
        String linkstr = AlipayFunction.createLinkString(orderInfo);
        return config.getGatewayUrl() + linkstr + "&sign=" + sign + "&sign_type=" + config.getSignType();
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
        Map<String, String> orderInfo = AlipayFunction.getTransferMoneyMap(alipayTransferMoney);
        String preStr = AlipayFunction.createLinkString(orderInfo);
        String sign = AlipayFunction.createSign(preStr);
        orderInfo.put("notify_url", URLEncoder.encode(orderInfo.get("notify_url"), config.getInputCharset()));
        orderInfo.put("detail_data", URLEncoder.encode(orderInfo.get("detail_data"), config.getInputCharset()));
        String linkStr = AlipayFunction.createLinkString(orderInfo);
        return config.getGatewayUrl() + linkStr + "&sign=" + sign + "&sign_type=" + config.getSignType();
    }

    /**
     * 支付宝 单笔转账到支付宝
     *
     * @param params {@link TransferSingleParams}
     */
    public static PayResponse<AlipayResponse> transferSingle(TransferSingleParams params) {
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

        PayResponse<AlipayResponse> response = new PayResponse<>();
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
        //创建API对应的request类
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
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
        //创建API对应的request类
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        StringBuilder sb = new StringBuilder();
        sb.append("{" + "\"out_trade_no\":\"").append(alipayRefund.getOutTradeNo()).append("\",");
        if (!StringUtils.isBlank(alipayRefund.getOutRequestNo())) {
            sb.append("\"out_request_no\":\"").append(alipayRefund.getOutRequestNo()).append("\",");
        }
        sb.append("\"refund_amount\":\"").append(alipayRefund.getRefundAmount()).append("\"}");
        //设置业务参数
        request.setBizContent(sb.toString());

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
                    if (!StringUtils.isBlank(aliSrcPath)) {
                        FileUtils.copyURLToFile(new URL(response.getBillDownloadUrl()), new File(aliSrcPath), 10000, 10000);
                    }
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
     * 处理支付宝返回结果
     *
     * @param response response
     * @param call     支付宝返回结果
     */
    private static <T extends AlipayResponse> void responseHandler(PayResponse<T> response, T call) {
        if (null == call) {
            response.setResult(false);
            response.setResultMessage("response null error");
        } else if (call.isSuccess()) {
            if (!PayBaseConstants.ALIPAY_RETURN_CODE_10000.equals(call.getCode())) {
                response.setResult(false);
                response.setResultCode(call.getSubCode());
                response.setResultMessage(call.getSubMsg());
                response.setData(call);
                log.error(JSON.toJSONString(call));
            } else {
                response.setResult(true);
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
