package com.ehu.weixin;

import com.ehu.exception.PayException;
import com.ehu.util.StringUtils;
import com.ehu.weixin.entity.WeChatRefundInfo;
import com.ehu.weixin.entity.WeChatResponseVO;
import com.ehu.weixin.entity.WeChatpayOrder;
import com.ehu.weixin.entity.WechatBusinessPay;
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
     * @param tradeType 1与3 1:商品支付；3：jsapi支付 4:小程序商品支付 5：app闲置支付
     * @throws PayException e
     */
    public static WeChatResponseVO createWeiXinPackage(WeChatpayOrder order, int tradeType) throws PayException {
        if (1 == tradeType) {
            return WeChatPayGetPrepay.gerneratorPrepay(order);
        } else if (2 == tradeType) {
            return WeChatPayGetPrepay.gerneratorPrepayXcx(order);
        }
        return null;
    }

    /**
     * 获取扫码支付二维码
     *
     * @param order
     * @throws PayException e
     */
    public static String getScanPayInfo(WeChatpayOrder order) throws PayException {
        return WeChatPayGetPrepay.gerneratorPrepayScan(order);
    }

    /**
     * 微信订单查询
     *
     * @throws PayException e
     */
    public static Object queryWeChatOrder(String transaction_id, String queryFlag) throws PayException {
        return GetWeChatQuerySign.getQuertResult(transaction_id, queryFlag);
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
        String signLocal = Signature.getSign(map);
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
        return WeChatRefund.weChatRefundOper(weChatRefundInfo);
    }

    /**
     * 小程序微信退款
     *
     * @return boolean
     * @throws PayException e
     */
    public static boolean weChatRefundXcx(WeChatRefundInfo weChatRefundInfo) throws PayException {
        return WeChatRefund.weChatRefundOperXcx(weChatRefundInfo);
    }

    /**
     * 微信企业转账
     *
     * @param wechatBusinessPay
     * @return boolean
     * @throws PayException e
     */
    public static boolean weChatBusinessPayForUser(WechatBusinessPay wechatBusinessPay) throws PayException {
        return WechatBusinessPayForUser.weChatPayBusinessPayforUser(wechatBusinessPay);
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
}
