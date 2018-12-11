package com.ehu.weixin;

import com.ehu.bean.PayResponse;
import com.ehu.config.EhPayConfig;
import com.ehu.exception.PayException;
import com.ehu.util.StringUtils;
import com.ehu.weixin.entity.*;
import com.ehu.weixin.util.Signature;
import com.ehu.weixin.weixinpay.*;
import lombok.extern.slf4j.Slf4j;
import org.jdom2.JDOMException;

import java.io.IOException;
import java.util.Map;

/**
 * @author AlanSun
 */
@Slf4j
public class WeChatPayUtil {

    /**
     * 微信支付(app支付与jsapi共用)
     * 不同的appType代表会有不同的回调地址
     *
     * @throws PayException e
     */
    public static WeChatResponseVO createWeiXinPackage(WeChatpayOrder order) throws PayException {
        if ("JSAPI".equals(order.getTradeType())) {
            return GetPrepayInfo.gerneratorPrepayXcx(order);
        } else {
            return GetPrepayInfo.gerneratorPrepay(order);
        }
    }

    /**
     * 获取扫码支付二维码
     *
     * @param order WeChatpayOrder
     * @throws PayException e
     */
    public static String getQrCode(WeChatpayOrder order) throws PayException {
        return GetPrepayInfo.gerneratorPrepayScan(order);
    }

    /**
     * 微信订单查询
     *
     * @throws PayException e
     */
    public static Object queryWeChatOrder(String transaction_id, String queryFlag) throws PayException {
        return QueryOrder.getQuertResult(transaction_id, queryFlag);
    }

    /**
     * 检验API返回的数据里面的签名是否合法，避免数据在传输的过程中被第三方篡改
     *
     * @param map API返回的XML数据字符串
     * @return API签名是否合法
     * @throws JDOMException          e
     * @throws IOException            e
     * @throws IllegalAccessException e
     */
    public static boolean checkIsSignValidFromResponseString(Map<String, String> map) throws JDOMException, IOException, IllegalAccessException {
        String signResponse = map.get("sign");
        if (StringUtils.isBlank(signResponse)) {
            log.error("API返回的数据签名数据不存在，有可能被第三方篡改!!!");
            return false;
        }
        log.info("服务器回包里面的签名是:" + signResponse);
        //清掉返回数据对象里面的Sign数据（不能把这个数据也加进去进行签名），然后用签名算法进行签名
        map.remove("sign");
        //将API返回的数据根据用签名算法进行计算新的签名，用来跟API返回的签名进行比较
        EhPayConfig config = EhPayConfig.getInstance();
        String signLocal = Signature.getSign(map, config.getWxPay_app_key());
        log.info("生成的签名是:" + signLocal);
        if (!signLocal.equals(signResponse)) {
            //签名验不过，表示这个API返回的数据有可能已经被篡改了
            log.error("API返回的数据签名验证不通过，有可能被第三方篡改!!!");
            return false;
        }
        return true;
    }

    /**
     * app微信退款
     *
     * @return boolean
     * @throws PayException e
     */
    public static boolean weChatRefund(WeChatRefundInfo weChatRefundInfo) throws PayException {
        return Refund.weChatRefundOper(weChatRefundInfo);
    }

    /**
     * 小程序微信退款
     *
     * @return boolean
     * @throws PayException e
     */
    public static boolean weChatRefundXcx(WeChatRefundInfo weChatRefundInfo) throws PayException {
        return Refund.weChatRefundOperXcx(weChatRefundInfo);
    }

    /**
     * 微信企业转账
     * ◆ 不支持给非实名用户打款
     * ◆ 给同一个实名用户付款，单笔单日限额2W/2W
     * ◆ 一个商户同一日付款总额限额100W
     * <p>
     * 注意：以上规则中的限额2w、100w由于计算规则与风控策略的关系，不是完全精确值，金额仅做参考，请不要依赖此金额做系统处理，应以接口实际返回和查询结果为准，请知晓。
     *
     * @param wechatBusinessPay
     * @return boolean
     * @throws PayException e
     */
    public static boolean weChatBusinessPayForUser(WechatBusinessPay wechatBusinessPay) throws PayException {
        return TransferMoney.weChatPayBusinessPayforUser(wechatBusinessPay);
    }

    /**
     * 下载账单
     *
     * @param time    下载那一天的账单
     * @param desPath 下载文件存放绝对路径 （包含文件名）
     * @throws PayException e
     */
    public static void downloadBill(String time, String desPath) throws PayException {
        DownloadBill.downloadBill(time, desPath);
    }

    /**
     * 1.企业付款至银行卡只支持新资金流类型账户
     * 2.目前企业付款到银行卡支持17家银行，更多银行逐步开放中
     * 3.付款到账实效为1-3日，最快次日到账
     * 4.每笔按付款金额收取手续费，按金额0.1%收取，最低1元，最高25元,
     * 如果商户开通了运营账户，手续费和付款的金额都从运营账户出。如果没有开通，则都从基本户出。
     * 5.每个商户号每天可以出款100万，单商户给同一银行卡付款每天限额5万
     * 6.发票：在账户中心-发票信息页面申请开票的商户会按月收到发票（已申请的无需重复申请）。
     *
     * @param params {@link TransferToBankCardParams}
     * @return {@link PayResponse}
     */
    public static PayResponse<Boolean> transferToBankCard(TransferToBankCardParams params) throws PayException {
        return TransferMoney.transferToBankCard(params);
    }
}
