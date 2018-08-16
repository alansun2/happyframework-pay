package com.ehu.weixin.util;


import com.ehu.config.EhPayConfig;
import com.ehu.util.MD5Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;


/**
 * @author Alan
 * @create 18-8-16
 */
public class Signature {

    /**
     * @param map
     * @return
     */
    public static String getSign(Map<String, String> map) {
        EhPayConfig config = EhPayConfig.getInstance();
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
        String result = sb.toString();
        result += "key=" + config.getWxPay_app_key();
        result = MD5Util.MD5Encode(result, "UTF-8").toUpperCase();
        return result;
    }
}
