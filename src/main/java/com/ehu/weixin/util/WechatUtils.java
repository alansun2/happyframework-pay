package com.ehu.weixin.util;

import com.ehu.bean.PayResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Map;
import java.util.Random;


@Slf4j
public class WechatUtils {
    /**
     * 处理微信返回
     *
     * @param wxResponse 微信返回MAP
     * @return boolean
     */
    public static boolean wechatResponseHandler(Map<String, String> wxResponse) {
        boolean flag = false;
        if (null == wxResponse || wxResponse.isEmpty()) {
            log.error("微信返回有误");
            return false;
        }

        wxResponse.forEach((k, v) -> log.info(k + ":::" + v));

        if (wxResponse.containsKey("return_code") && "SUCCESS".equals(wxResponse.get("return_code"))) {
            if (wxResponse.containsKey("result_code") && "SUCCESS".equals(wxResponse.get("result_code"))) {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 处理微信返回
     *
     * @param wxResponseMap 微信返回MAP
     */
    public static void wechatResponseHandler(Map<String, String> wxResponseMap, PayResponse response) {
        if (null == wxResponseMap || wxResponseMap.isEmpty()) {
            response.setResultMessage("微信返回有误");
            response.setResult(false);
            return;
        }

        wxResponseMap.forEach((k, v) -> log.info(k + ":::" + v));

        if (wxResponseMap.containsKey("return_code") && "SUCCESS".equals(wxResponseMap.get("return_code"))) {
            if ("SUCCESS".equals(wxResponseMap.get("result_code"))) {
                response.setResult(true);
            } else if ("FAIL".equals(wxResponseMap.get("result_code"))) {
                response.setResult(false);
                response.setResultMessage(wxResponseMap.get("err_code_des"));
                response.setResultCode(wxResponseMap.get("err_code"));
            } else {
                response.setResultMessage("微信返回有误");
                response.setResult(false);
            }
        } else {
            response.setResult(false);
            response.setResultMessage(wxResponseMap.get("return_msg"));
            response.setResultCode(wxResponseMap.get("return_code"));
        }
    }

    /**
     * 获取随机数
     *
     * @return 随机数
     */
    public static String getNonceStr() {
        Random random = new Random();
        return DigestUtils.md5Hex(String.valueOf(random.nextInt(10000)));
    }

    /**
     * 获取时间戳
     *
     * @return 时间戳 单位： 秒
     */
    public static String getTimeStamp() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }

    /**
     * 把金额转为分单位
     *
     * @param price 单位： 元
     */
    public static String getFinalMoney(double price) {
        //转为两位小数
        String finalMoney = String.format("%.2f", price);
        int i = Integer.parseInt(finalMoney.replace(".", ""));
        //转为分
        return Integer.toString(i);
    }
}
