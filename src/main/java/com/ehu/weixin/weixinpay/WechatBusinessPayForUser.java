package com.ehu.weixin.weixinpay;

import com.ehu.config.EhPayConfig;
import com.ehu.exception.PayException;
import com.ehu.util.StringUtils;
import com.ehu.weixin.entity.WechatBusinessPay;
import com.ehu.weixin.util.Signature;
import com.ehu.weixin.util.WeChatUtils;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;


/**
 * @author AlanSun
 * @Date 2016年8月3日
 * 微信企业付款操作类
 */
public class WechatBusinessPayForUser {

    private static final String REQUESTURL = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";

    @SuppressWarnings("unchecked")
    public static boolean weChatPayBusinessPayforUser(WechatBusinessPay wechatBusinessPay) throws PayException {
        EhPayConfig config = EhPayConfig.getInstance();

        SortedMap<String, String> packageParams = new TreeMap<String, String>();
        packageParams.put("mch_appid", config.getWxPay_appid());
        packageParams.put("mchid", config.getWxPay_mch_id());
        packageParams.put("nonce_str", WeChatUtils.getNonceStr());
        packageParams.put("partner_trade_no", wechatBusinessPay.getOrderId());
        packageParams.put("openid", wechatBusinessPay.getOrderId());
        /*NO_CHECK：不校验真实姓名
        FORCE_CHECK：强校验真实姓名（未实名认证的用户会校验失败，无法转账）*/
        packageParams.put("check_name", wechatBusinessPay.getCheckName());
        if (!StringUtils.isBlank(wechatBusinessPay.getCheckName())) {
            packageParams.put("re_user_name", wechatBusinessPay.getReUserName());
        }
        packageParams.put("amount", WeChatUtils.getFinalMoney(wechatBusinessPay.getAmount()));
        packageParams.put("spbill_create_ip", config.getWxPay_spbill_create_ip());
        packageParams.put("desc", wechatBusinessPay.getDesc());
        packageParams.put("sign", Signature.getSign(packageParams));
        Map<String, String> map = WeChatUtils.wechatPostWithSSL(packageParams, REQUESTURL, config.getWxPay_ca(), config.getWxPay_code());//发送得到微信服务器
        return WeChatUtils.checkWechatResponse(map);
    }
}
