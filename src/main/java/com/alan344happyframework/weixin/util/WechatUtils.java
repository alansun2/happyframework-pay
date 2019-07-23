package com.alan344happyframework.weixin.util;

import com.alan344happyframework.constants.SeparatorConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Random;


@Slf4j
public class WechatUtils {
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
    public static String getFinalMoney(String price) {
        //转为两位小数
        String finalMoney = String.format("%.2f", Double.valueOf(price));
        int i = Integer.parseInt(finalMoney.replace(SeparatorConstants.DOT, SeparatorConstants.EMPTY));
        //转为分
        return Integer.toString(i);
    }
}