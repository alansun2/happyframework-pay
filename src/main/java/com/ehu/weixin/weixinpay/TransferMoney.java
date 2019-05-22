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
     * 转账到银行卡
     */
    private static final String QUERY_URL_TOBANK = "https://api.mch.weixin.qq.com/mmpaysptrans/query_bank";

    /**
     * RSA 算法
     */
    private static final String ALGORITHM = "RSA/ECB/OAEPWITHSHA-1ANDMGF1PADDING";

    /**
     * 微信转账到零钱
     *
     * @param wechatBusinessPay {@link WechatBusinessPay}
     * @return true or false
     * @throws PayException e
     */
    public static PayResponse<Map<String, String>> weChatPayBusinessPayforUser(WechatBusinessPay wechatBusinessPay) throws PayException {
        EhPayConfig config = EhPayConfig.getInstance();

        SortedMap<String, String> packageParams = new TreeMap<>();
        packageParams.put("mch_appid", StringUtils.getDefaultIfNull(wechatBusinessPay.getMchAppid(), config.getWxPay_mch_appid()));
        packageParams.put("mchid", StringUtils.getDefaultIfNull(wechatBusinessPay.getMchid(), config.getWxPay_mch_id()));
        packageParams.put("nonce_str", WeChatUtils.getNonceStr());
        packageParams.put("partner_trade_no", wechatBusinessPay.getOrderId());
        packageParams.put("openid", wechatBusinessPay.getOpenId());
        /*NO_CHECK：不校验真实姓名
        FORCE_CHECK：强校验真实姓名（未实名认证的用户会校验失败，无法转账）*/
        packageParams.put("check_name", wechatBusinessPay.getCheckName());
        if (!"NO_CHECK".equals(wechatBusinessPay.getCheckName())) {
            packageParams.put("re_user_name", wechatBusinessPay.getReUserName());
        }
        packageParams.put("amount", WeChatUtils.getFinalMoney(wechatBusinessPay.getAmount()));
        packageParams.put("spbill_create_ip", config.getWxPay_spbill_create_ip());
        packageParams.put("desc", wechatBusinessPay.getDesc());
        packageParams.put("sign", Signature.getSign(packageParams, StringUtils.getDefaultIfNull(wechatBusinessPay.getPrivateKey(), config.getWxPay_app_key())));
        //发送得到微信服务器
        Map<String, String> map = WeChatUtils.wechatPostWithSSL(packageParams, REQUESTURL, StringUtils.getDefaultIfNull(wechatBusinessPay.getCaPath(), config.getWxPay_ca()), StringUtils.getDefaultIfNull(wechatBusinessPay.getCode(), config.getWxPay_code()));
        PayResponse<Map<String, String>> response = new PayResponse<>();
        WeChatUtils.wechatResponseHandler(map, response);
        if (response.getResult()) {
            response.setData(map);
        }
        return response;
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
        String wxPublicKey = StringUtils.getDefaultIfNull(params.getWxPublicKey(), config.getWxPay_public_key());
        try {
            params.setEncBankNo(RSAUtils.encryptByPublicKeyToString(params.getEncBankNo().getBytes(), wxPublicKey, ALGORITHM));
            params.setEncTrueName(RSAUtils.encryptByPublicKeyToString(params.getEncTrueName().getBytes(), wxPublicKey, ALGORITHM));
        } catch (Exception e) {
            response.setResult(false);
            response.setResultMessage("RSA error");
            return response;
        }

        String ca = StringUtils.getDefaultIfNull(params.getCaPath(), config.getWxPay_ca());
        String code = StringUtils.getDefaultIfNull(params.getCode(), config.getWxPay_code());
        String mchId = StringUtils.getDefaultIfNull(params.getMchid(), config.getWxPay_mch_id());
        String privateKey = StringUtils.getDefaultIfNull(params.getPrivateKey(), config.getWxPay_app_key());
        params.setPrivateKey(null);
        params.setWxPublicKey(null);
        params.setCaPath(null);
        params.setCode(null);
        params.setMchid(null);

        String s = JSON.toJSONString(params, new LowerUnderscoreFilter());
        SortedMap<String, String> packageParams = JSON.parseObject(s, new TypeReference<TreeMap<String, String>>() {

        });
        packageParams.put("amount", WeChatUtils.getFinalMoney(params.getAmount()));
        packageParams.put("mch_id", mchId);
        packageParams.put("nonce_str", WeChatUtils.getNonceStr());
        packageParams.put("sign", Signature.getSign(packageParams, privateKey));
        //发送得到微信服务器
        Map<String, String> map = WeChatUtils.wechatPostWithSSL(packageParams, URL_TOBANK, ca, code);
        WeChatUtils.wechatResponseHandler(map, response);
        return response;
    }

    /**
     * 查询企业付款到银行卡
     *
     * @param partnerTradeNo 商户订单号
     */
    public static PayResponse<Map<String, String>> getResultOfTransferToBank(String partnerTradeNo) throws PayException {
        EhPayConfig config = EhPayConfig.getInstance();
        SortedMap<String, String> packageParams = new TreeMap<>();
        packageParams.put("mch_id", config.getWxPay_mch_id());
        packageParams.put("partner_trade_no", partnerTradeNo);
        packageParams.put("nonce_str", WeChatUtils.getNonceStr());
        packageParams.put("sign", Signature.getSign(packageParams, config.getWxPay_app_key()));
        //发送得到微信服务器
        Map<String, String> map = WeChatUtils.wechatPostWithSSL(packageParams, QUERY_URL_TOBANK, config.getWxPay_ca(), config.getWxPay_code());
        PayResponse<Map<String, String>> response = new PayResponse<>();
        WeChatUtils.wechatResponseHandler(map, response);
        if (response.getResult()) {
            response.setData(map);
        }
        return response;
    }
}
