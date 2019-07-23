package com.alan344happyframework.weixin.util;


import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;


/**
 * @author Alan
 * @createtime 18-8-16
 */
public class Signature {

    /**
     * @param map 待签名的参数
     * @return 签名
     */
    public static String getSign(Map<String, String> map, String signKey) {

        ArrayList<String> list = new ArrayList<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (!entry.getValue().equals("")) {
                list.add(entry.getKey() + "=" + entry.getValue() + "&");
            }
        }
        int size = list.size();
        String[] arrayToSort = list.toArray(new String[size]);
        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(arrayToSort[i]);
        }
        String sign = sb.toString();
        sign += "key=" + signKey;
        sign = DigestUtils.md5Hex(sign).toUpperCase();
        return sign;
    }
}
