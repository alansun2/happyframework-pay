package com.ehu.weixin.weixinpay;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ehu.bean.LowerUnderscoreFilter;
import com.ehu.bean.PayResponse;
import com.ehu.config.EhPayConfig;
import com.ehu.exception.PayException;
import com.ehu.util.RSAUtils;
import com.ehu.util.StringUtils;
import com.ehu.weixin.entity.TransferToBankCardParams;
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
public class TransferMoney {

    private static final String REQUESTURL = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";

    /**
     * 转账到银行卡
     */
    private static final String URL_TOBANK = "https://api.mch.weixin.qq.com/mmpaysptrans/pay_bank";

    /**
     * 微信转账到零钱
     *
     * @param wechatBusinessPay {@link WechatBusinessPay}
     * @return true or false
     * @throws PayException e
     */
    public static boolean weChatPayBusinessPayforUser(WechatBusinessPay wechatBusinessPay) throws PayException {
        EhPayConfig config = EhPayConfig.getInstance();

        SortedMap<String, String> packageParams = new TreeMap<>();
        packageParams.put("mch_appid", StringUtils.getDefaultIfNullPay(wechatBusinessPay.getMchAppid(), config.getWxPay_mch_appid()));
        packageParams.put("mchid", StringUtils.getDefaultIfNullPay(wechatBusinessPay.getMchid(), config.getWxPay_mch_id()));
        packageParams.put("nonce_str", WeChatUtils.getNonceStr());
        packageParams.put("partner_trade_no", wechatBusinessPay.getOrderId());
        packageParams.put("openid", wechatBusinessPay.getOpenId());
        /*NO_CHECK：不校验真实姓名
        FORCE_CHECK：强校验真实姓名（未实名认证的用户会校验失败，无法转账）*/
        packageParams.put("check_name", wechatBusinessPay.getCheckName());
        if (!wechatBusinessPay.getCheckName().equals("NO_CHECK")) {
            packageParams.put("re_user_name", wechatBusinessPay.getReUserName());
        }
        packageParams.put("amount", WeChatUtils.getFinalMoney(wechatBusinessPay.getAmount()));
        packageParams.put("spbill_create_ip", config.getWxPay_spbill_create_ip());
        packageParams.put("desc", wechatBusinessPay.getDesc());
        packageParams.put("sign", Signature.getSign(packageParams, StringUtils.getDefaultIfNullPay(wechatBusinessPay.getPrivateKey(), config.getWxPay_app_key())));
        Map<String, String> map = WeChatUtils.wechatPostWithSSL(packageParams, REQUESTURL, StringUtils.getDefaultIfNullPay(wechatBusinessPay.getCaPath(), config.getWxPay_ca()), StringUtils.getDefaultIfNullPay(wechatBusinessPay.getCode(), config.getWxPay_code()));//发送得到微信服务器
        return WeChatUtils.wechatResponseHandler(map);
    }

    /**
     * 转账到银行卡
     *
     * @param params {@link TransferToBankCardParams}
     * @return {@link PayResponse}
     */
    public static PayResponse<Boolean> transferToBankCard(TransferToBankCardParams params) throws PayException {
        EhPayConfig config = EhPayConfig.getInstance();
        PayResponse<Boolean> response = new PayResponse<>();
        params.setAmount(Integer.parseInt(WeChatUtils.getFinalMoney(params.getAmount())));
        String wxPublicKey = StringUtils.getDefaultIfNullPay(params.getWxPublicKey(), config.getWxPay_public_key());
        try {
            params.setEncBankNo(RSAUtils.encryptByPublicKeyToString(params.getEncBankNo().getBytes(), wxPublicKey));
            params.setEncTrueName(RSAUtils.encryptByPublicKeyToString(params.getEncTrueName().getBytes(), wxPublicKey));
        } catch (Exception e) {
            response.setResult(false);
            response.setResultMessage("RSA error");
        }
        String s = JSON.toJSONString(params, new LowerUnderscoreFilter());
        SortedMap<String, String> packageParams = JSON.parseObject(s, new TypeReference<TreeMap<String, String>>() {

        });
        packageParams.put("mch_id", StringUtils.getDefaultIfNullPay(params.getMchid(), config.getWxPay_mch_id()));
        packageParams.put("nonce_str", WeChatUtils.getNonceStr());
        packageParams.put("sign", Signature.getSign(packageParams, StringUtils.getDefaultIfNullPay(params.getPrivateKey(), config.getWxPay_app_key())));
        Map<String, String> map = WeChatUtils.wechatPostWithSSL(packageParams, URL_TOBANK, StringUtils.getDefaultIfNullPay(params.getCaPath(), config.getWxPay_ca()), StringUtils.getDefaultIfNullPay(params.getCode(), config.getWxPay_code()));//发送得到微信服务器
        WeChatUtils.wechatResponseHandler(map, response);
        return response;
    }
}
