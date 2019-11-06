package com.alan344happyframework.weixin;

import com.alan344happyframework.bean.*;
import com.alan344happyframework.constants.BaseConstants;
import com.alan344happyframework.constants.PayBaseConstants;
import com.alan344happyframework.core.PayIntegrate;
import com.alan344happyframework.core.responsehandler.WechatResponseHandlerBase;
import com.alan344happyframework.exception.PayException;
import com.alan344happyframework.weixin.entity.TransferToBankCardParams;
import com.alan344happyframework.weixin.entity.WechatBusinessPay;
import com.alan344happyframework.weixin.service.*;
import com.github.rholder.retry.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author AlanSun
 */
@Slf4j
public class WechatPayUtils implements PayIntegrate {

    /**
     * 微信支付(app支付与jsapi共用)
     * APPLET--JSAPI支付（或小程序支付）、NATIVE--Native支付、APP--app支付，MWEB--H5支付
     *
     * @throws PayException e
     */
    @Override
    public PayInfoResponse createPayInfo(OrderPay order) throws PayException {
        if (TradeTypeEnum.APPLET.equals(order.getTradeType())) {
            return PayInfoResponse.builder().weChatResponseVO(GetPrepayInfo.generatorPrepayXcx(order)).build();
        } else {
            return PayInfoResponse.builder().weChatResponseVO(GetPrepayInfo.generatorPrepay(order)).build();
        }
    }

    /**
     * 获取扫码支付二维码
     *
     * @param payOrder {@link OrderScanPay}
     * @throws PayException e
     */
    @Override
    public String getQrCode(OrderScanPay payOrder) throws PayException {
        return GetPrepayInfo.generatorPrepayScan(payOrder);
    }

    /**
     * 微信订单查询
     *
     * @throws PayException e
     */
    @Override
    public PayResponse queryOrder(OrderQuery orderQuery) throws PayException {
        return QueryOrder.getQueryResult(orderQuery);
    }

    /**
     * 微信退款
     * <p>
     * 注意：
     * <p>
     * 1、交易时间超过一年的订单无法提交退款
     * <p>
     * 2、微信支付退款支持单笔交易分多次退款，多次退款需要提交原支付订单的商户订单号和设置不同的退款单号。申请退款总金额不能超过订单金额。 一笔退款失败后重新提交，请不要更换退款单号，请使用原商户退款单号
     * <p>
     * 3、请求频率限制：150qps，即每秒钟正常的申请退款请求次数不超过150次
     * <p>
     * 错误或无效请求频率限制：6qps，即每秒钟异常或错误的退款申请请求不超过6次
     * <p>
     * 4、每个支付订单的部分退款次数不能超过50次
     *
     * @return boolean
     */
    @Override
    public PayResponse refund(OrderRefund refundOrder) throws PayException {
        return WechatResponseHandlerBase.getInstance().handler(Refund.weChatRefund(refundOrder), null);
    }

    /**
     * 微信企业转账
     * <p>
     * 重试三次
     * <p>
     * ◆ 不支持给非实名用户打款
     * ◆ 给同一个实名用户付款，单笔单日限额2W/2W
     * ◆ 一个商户同一日付款总额限额100W
     * <p>
     * 注意：以上规则中的限额2w、100w由于计算规则与风控策略的关系，不是完全精确值，金额仅做参考，请不要依赖此金额做系统处理，应以接口实际返回和查询结果为准，请知晓。
     *
     * @param params {@link WechatBusinessPay}
     * @return boolean
     * @throws PayException e
     */
    @Override
    public PayResponse<Map<String, String>> transferMoneyInternal(TransferMoneyInternal params) throws PayException {
        Retryer<PayResponse<Map<String, String>>> retryer = RetryerBuilder.<PayResponse<Map<String, String>>>newBuilder()
                .retryIfException()
                .retryIfResult(input -> input == null || (!input.getResultCode().equals(BaseConstants.SUCCESS) && input.getData().containsKey("err_code") && input.getData().get("err_code").equals("SYSTEMERROR")))
                .withWaitStrategy(WaitStrategies.fixedWait(1, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .build();

        PayResponse<Map<String, String>> payResponse;
        try {
            payResponse = retryer.call(() -> {
                PayResponse<Map<String, String>> mapPayResponse = TransferMoney.weChatPayBusinessPayForUser(params);
                if (!BaseConstants.SUCCESS.equals(mapPayResponse.getResultCode()) && mapPayResponse.getData().containsKey("err_code") && mapPayResponse.getData().get("err_code").equals("SYSTEMERROR")) {
                    PayResponse<Map<String, String>> resultOfBusinessPayForUser = TransferMoney.getResultOfBusinessPayForUser(params);
                    if (BaseConstants.SUCCESS.equals(mapPayResponse.getResultCode())) {
                        return resultOfBusinessPayForUser;
                    }
                }
                return mapPayResponse;
            });
        } catch (ExecutionException | RetryException e) {
            log.error("3次重试失败", e);
            payResponse = TransferMoney.getResultOfBusinessPayForUser(params);
        }
        return payResponse;
    }

    /**
     * 查询微信企业转账
     *
     * @param queryTransferMoneyInternal queryTransferMoneyInternal
     * @return PayResponse
     * @throws PayException e
     */
    public PayResponse<Map<String, String>> getResultOfTransferMoneyInternal(QueryTransferMoneyInternal queryTransferMoneyInternal) throws PayException {
        Retryer<PayResponse<Map<String, String>>> retryer = RetryerBuilder.<PayResponse<Map<String, String>>>newBuilder()
                .retryIfException()
                .retryIfResult(input -> input == null || (!input.getResultCode().equals(BaseConstants.SUCCESS) && input.getData().containsKey("err_code") && input.getData().get("err_code").equals("SYSTEMERROR")))
                .withWaitStrategy(WaitStrategies.fixedWait(1, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .build();

        PayResponse<Map<String, String>> payResponse;
        try {
            payResponse = retryer.call(() -> TransferMoney.getResultOfBusinessPayForUser(queryTransferMoneyInternal));
        } catch (ExecutionException | RetryException e) {
            log.error("3次重试失败", e);
            payResponse = TransferMoney.getResultOfBusinessPayForUser(queryTransferMoneyInternal);
        }
        return payResponse;
    }

    /**
     * 下载财务账单
     *
     * @param params {@link FinancialReport}
     */
    @Override
    public PayResponse getFinancial(FinancialReport params) throws PayException {
        return DownloadBill.downloadBill(params);
    }

    /**
     * 付款到银行卡
     * 1.企业付款至银行卡只支持新资金流类型账户
     * 2.目前企业付款到银行卡支持17家银行，更多银行逐步开放中
     * 3.付款到账实效为1-3日，最快次日到账
     * 4.每笔按付款金额收取手续费，按金额0.1%收取，最低1元，最高25元,
     * 如果商户开通了运营账户，手续费和付款的金额都从运营账户出。如果没有开通，则都从基本户出。
     * 5.每个商户号每天可以出款100万，单商户给同一银行卡付款每天限额5万
     * 6.发票：在账户中心-发票信息页面申请开票的商户会按月收到发票（已申请的无需重复申请）。
     * <p>
     * 这里把微信查询也封装了。
     * 当转账接口返回 SUCCESS 时，返回 PROCESSING，因为一般银行卡转账需要一段时间处理。
     * 当转账接口返回 FAIL 时会根据 微信返回的 err_code 来做相应的处理:
     * if err_code IN (SYSTEMERROR, ORDERPAID, FATAL_ERROR then)
     * 使用查询接口查询，if 查询接口返回 SUCCESS (请求成功)，then 直接根据返回的 status 来返回，
     * if 查询接口返回 FAIL，then 根据查询接口的 err_code 做处理：
     * if err_code = NOT_FOUND，then 返回 MANUAL，说明需要手动处理
     * else 表示请求失败，返回 PROCESSING
     * else 表示请求失败，返回 FAIL
     * <p>
     * 需要注意的是 FAIL 可以使用原订单号重新发起请求
     *
     * @param params {@link TransferToBankCardParams}
     * @return resultCode:
     * SUCCESS：付款成功
     * PROCESSING：处理中。对应转账受理成功和查询接口的 status = PROCESSING
     * FAILED：付款失败
     * BANK_FAIL：银行退票
     * MANUAL：需要人工处理
     * FAIL：请求失败，需要重新发起请求
     */
    @Override
    public PayResponse<Map<String, String>> transferToBankCard(TransferToBankCardParams params) throws PayException {
        Retryer<PayResponse<Map<String, String>>> retryer = RetryerBuilder.<PayResponse<Map<String, String>>>newBuilder()
                .retryIfException()
                .retryIfResult(input -> input == null || input.getResultCode().equals(PayBaseConstants.RETRY))
                .withWaitStrategy(WaitStrategies.fixedWait(1, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .build();

        PayResponse<Map<String, String>> payResponse;
        try {
            payResponse = retryer.call(() -> TransferMoney.transferToBankCard(params));
        } catch (ExecutionException | RetryException e) {
            log.error("3次重试失败", e);
            payResponse = TransferMoney.getResultOfTransferToBank(params.getPartnerTradeNo());
        }

        if (PayBaseConstants.RETRY.equals(payResponse.getResultCode())) {
            payResponse.setResultCode(PayBaseConstants.RETURN_FAIL);
        }
        return payResponse;
    }

    /**
     * 查询企业付款到银行卡
     * <p>
     * if 查询接口返回 SUCCESS (请求成功)，then 直接根据返回的 status 来返回。
     * if 查询接口返回 FAIL，then 根据查询接口的 err_code 做处理：
     * if err_code = ORDERNOTEXIST(订单不存在)，then 返回 ORDER_NOT_EXIST
     * if err_code = NOT_FOUND，then 返回 MANUAL，说明需要手动处理
     * else 表示请求失败，返回 FAIL
     * <p>
     * 请注意：
     * <p>
     * 返回参数：
     * 商户号            mch_id	           是	string(32)	商户号
     * 商户企业付款单号	partner_trade_no   是	string(32)	商户单号
     * 微信企业付款单号	payment_no	       是	string(64)	即为微信内部业务单号
     * 银行卡号	        bank_no_md5	       是	string(32)	收款用户银行卡号(MD5加密)
     * 用户真实姓名	    true_name_md5	   是	string(32)	收款人真实姓名（MD5加密）
     * 代付金额	        amount	           是	int	        代付订单金额RMB：分
     * 代付单状态	    status	           是	string(16)	代付订单状态：
     * PROCESSING（处理中，如有明确失败，则返回额外失败原因；否则没有错误原因）
     * SUCCESS（付款成功）
     * FAILED（付款失败,需要替换付款单号重新发起付款）
     * BANK_FAIL（银行退票，订单状态由付款成功流转至退票,退票时付款金额和手续费会自动退还）
     * 手续费金额	   cmms_amt	           是	int	        手续费订单金额 RMB：分
     * 商户下单时间	   create_time	       是	String(32)	微信侧订单创建时间
     * 成功付款时间	   pay_succ_time	   否	String(32)	微信侧付款成功时间（但无法保证银行不会退票）
     * 失败原因	       reason	           否	String(128)	订单失败原因（如：余额不足）
     *
     * @param orderId 商户订单号
     * @return resultCode:
     * SUCCESS：付款成功
     * PROCESSING：处理中
     * FAILED：付款失败
     * BANK_FAIL：银行退票
     * ORDER_NOT_EXIST: 订单不存在
     * MANUAL：需要人工处理
     * FAIL：请求失败，需要重新发起请求
     */
    @Override
    public PayResponse<Map<String, String>> getResultOfTransferToBank(String orderId) throws PayException {
        Retryer<PayResponse<Map<String, String>>> retryer = RetryerBuilder.<PayResponse<Map<String, String>>>newBuilder()
                .retryIfException()
                .retryIfResult(input -> input == null || input.getResultCode().equals(PayBaseConstants.RETRY))
                .withWaitStrategy(WaitStrategies.fixedWait(1, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(2))
                .build();

        PayResponse<Map<String, String>> payResponse;
        try {
            payResponse = retryer.call(() -> TransferMoney.getResultOfTransferToBank(orderId));
        } catch (ExecutionException | RetryException e) {
            log.error("3次重试失败", e);
            payResponse = TransferMoney.getResultOfTransferToBank(orderId);
        }

        if (PayBaseConstants.RETRY.equals(payResponse.getResultCode())) {
            payResponse.setResultCode(PayBaseConstants.RETURN_FAIL);
        }
        return payResponse;
    }
}
