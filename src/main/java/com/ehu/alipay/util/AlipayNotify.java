package com.ehu.alipay.util;

import com.ehu.config.EhPayConfig;
import com.ehu.util.RSAUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;


/**
 * 类名：AlipayNotify
 * 功能：支付宝通知处理类
 * 详细：处理支付宝各接口通知返回
 * 日期：2015-01-10
 */
public class AlipayNotify {

    /**
     * 根据反馈回来的信息，生成签名结果
     *
     * @param Params 通知返回来的参数数组
     * @param sign   比对的签名结果
     * @return 生成的签名结果
     */
    public static boolean getSignVeryfy(Map<String, String> Params, String sign) {

        EhPayConfig config = EhPayConfig.getInstance();
        //过滤空值、sign与sign_type参数
        Map<String, String> sParaNew = AlipayFunction.paraFilter(Params);
        //获取待签名字符串
        String preSignStr = AlipayFunction.createLinkString(sParaNew);
        //获得签名验证结果
        boolean isSign = false;
        if ("RSA".equals(config.getAlipay_sign_type())) {
            try {
                isSign = RSAUtils.verify(preSignStr.getBytes(config.getAlipay_input_charset()), config.getAlipay_public_key(), sign, RSAUtils.SIGNATURE_ALGORITHM_SHA1);
            } catch (Exception e) {
                return false;
            }
        }
        return isSign;
    }

    /**
     * 获取远程服务器ATN结果,验证返回URL
     *
     * @param notify_id 通知校验ID
     * @return 服务器ATN结果
     * 验证结果集：
     * invalid命令参数不对 出现这个错误，请检测返回处理中partner和key是否为空
     * true 返回正确信息
     * false 请检查防火墙或者是服务器阻止端口问题以及验证时间是否超过一分钟
     */
    public static String verifyResponse(String notify_id) {
        //获取远程服务器ATN结果，验证是否是支付宝服务器发来的请求

        EhPayConfig config = EhPayConfig.getInstance();
        String partner = config.getAlipay_partner();
        String veryfy_url = config.getAlipay_verify_url() + "partner=" + partner + "&notify_id=" + notify_id;

        return checkUrl(veryfy_url);
    }

    /**
     * 获取远程服务器ATN结果
     *
     * @param urlvalue 指定URL路径地址
     * @return 服务器ATN结果
     * 验证结果集：
     * invalid命令参数不对 出现这个错误，请检测返回处理中partner和key是否为空
     * true 返回正确信息
     * false 请检查防火墙或者是服务器阻止端口问题以及验证时间是否超过一分钟
     */
    private static String checkUrl(String urlvalue) {
        String inputLine;

        try {
            URL url = new URL(urlvalue);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection
                    .getInputStream()));
            inputLine = in.readLine();
        } catch (Exception e) {
            e.printStackTrace();
            inputLine = "";
        }

        return inputLine;
    }
}
