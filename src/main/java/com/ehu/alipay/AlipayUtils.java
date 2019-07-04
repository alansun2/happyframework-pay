package com.ehu.alipay;

import com.alan344happyframework.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayResponse;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.*;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import com.ehu.alipay.entity.AlipayRefund;
import com.ehu.alipay.entity.TransferSingleParams;
import com.ehu.bean.PayInfoResponse;
import com.ehu.bean.PayOrder;
import com.ehu.bean.PayResponse;
import com.ehu.bean.ScanPayOrder;
import com.ehu.config.AliPay;
import com.ehu.constants.PayBaseConstants;
import com.ehu.constants.PayResultCodeConstants;
import com.ehu.constants.PayResultMessageConstants;
import com.ehu.core.LowerUnderscoreFilter;
import com.ehu.core.Pay;
import com.ehu.exception.PayException;
import com.github.rholder.retry.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
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
public class AlipayUtils implements Pay {

    public static final AliPay config = AliPay.getInstance();
    private static AlipayClient alipayClient = new DefaultAlipayClient(config.getGatewayUrl(), config.getAppId(), config.getPrivateKey(), "json", config.getInputCharset(), config.getOpenPublicKey());

    /**
     * 创建支付宝订单支付信息（无线）
     * <p>
     * 2.0
     *
     * @param order 订单信息
     * @return 支付宝订单支付信息（无线）
     */
    @Override
    public PayInfoResponse createPayInfo(PayOrder order) {
        //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();

        //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
        AlipayTradeAppPayModel model = order.getAlipayTradeAppPayModel();
        if (model == null) {
            model = new AlipayTradeAppPayModel();
            model.setTimeoutExpress("90m");
        }

        model.setOutTradeNo(order.getOrderId());
        model.setBody(order.getBody());
        model.setSubject(order.getSubject());
        model.setTotalAmount(order.getPrice());
        //固定值
        model.setProductCode("QUICK_MSECURITY_PAY");
        request.setBizModel(model);
        request.setNotifyUrl(StringUtils.getDefaultIfNull(order.getNotifyUrl(), config.getNotifyUrl()));
        try {
            //这里和普通的接口调用不同，使用的是sdkExecute
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
            //就是orderString 可以直接给客户端请求，无需再做处理。
            log.info(response.getBody());
            return PayInfoResponse.builder().alipayStr(response.getBody()).build();
        } catch (AlipayApiException e) {
            log.error("构建支付信息失败：", e);
            throw new PayException("构建支付信息失败,请重试");
        }
    }

    /**
     * 线下扫码支付：扫码支付获取二维码
     *
     * @return 二维码地址
     * @throws PayException e
     */
    @Override
    public String getQrCode(ScanPayOrder payOrder) throws PayException {
        //创建API对应的request类
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        request.setNotifyUrl(StringUtils.getDefaultIfNull(payOrder.getNotifyUrl(), config.getNotifyUrl()));

        AlipayTradePrecreateModel alipayTradePrecreateModel = payOrder.getAlipayTradePrecreateModel();
        if (alipayTradePrecreateModel == null) {
            alipayTradePrecreateModel = new AlipayTradePrecreateModel();
            alipayTradePrecreateModel.setTimeoutExpress("90m");
        }
        alipayTradePrecreateModel.setOutTradeNo(payOrder.getOrderId());
        alipayTradePrecreateModel.setBody(payOrder.getBody());
        alipayTradePrecreateModel.setSubject(payOrder.getSubject());
        alipayTradePrecreateModel.setTotalAmount(payOrder.getPrice());
        alipayTradePrecreateModel.setStoreId(payOrder.getStoreId());

        request.setBizModel(alipayTradePrecreateModel);

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
     * 支付宝退款
     *
     * @param alipayRefund alipayRefund
     * @return 退款是否成功
     * @throws PayException e
     */
    public static boolean aliPayRefund(AlipayRefund alipayRefund) throws PayException {
        //创建API对应的request类
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        AlipayTradeRefundModel tradeRefundModel = new AlipayTradeRefundModel();
        tradeRefundModel.setOutTradeNo(alipayRefund.getOutTradeNo());
        tradeRefundModel.setRefundAmount(alipayRefund.getRefundAmount());
        if (!StringUtils.isEmpty(alipayRefund.getOutRequestNo())) {
            tradeRefundModel.setOutRequestNo(alipayRefund.getOutRequestNo());
        }

        //设置业务参数
        request.setBizModel(tradeRefundModel);

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
        AlipayTradeQueryModel tradeQueryModel = new AlipayTradeQueryModel();
        tradeQueryModel.setOutTradeNo(outTradeNo);
        request.setBizModel(tradeQueryModel);

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
        AlipayDataDataserviceBillDownloadurlQueryModel downloadurlQueryModel = new AlipayDataDataserviceBillDownloadurlQueryModel();
        downloadurlQueryModel.setBillDate(time);
        downloadurlQueryModel.setBillType("trade");

        request.setBizModel(downloadurlQueryModel);

        AlipayDataDataserviceBillDownloadurlQueryResponse response;
        try {
            response = alipayClient.execute(request);
            if (response.isSuccess()) {
                try {
                    if (!StringUtils.isEmpty(aliSrcPath)) {
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
}
