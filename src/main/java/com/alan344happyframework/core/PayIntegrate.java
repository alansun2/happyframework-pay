package com.alan344happyframework.core;

import com.alan344happyframework.alipay.AlipayUtils;
import com.alan344happyframework.bean.PayBase;
import com.alan344happyframework.weixin.WechatPayUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 53479
 * @date 2019/7/22 17:03
 **/
public interface PayIntegrate extends Pay, TransferAccounts {
    Map<Integer, PayIntegrate> payTypePayMap = new HashMap<>();

    static PayIntegrate getPay(int payType) {
        PayIntegrate pay;
        if (payTypePayMap.containsKey(payType)) {
            pay = payTypePayMap.get(payType);
        } else {
            switch (payType) {
                case PayBase.PAY_TYPE_1:
                    pay = new AlipayUtils();
                    break;
                case PayBase.PAY_TYPE_2:
                    pay = new WechatPayUtils();
                    break;
                default:
                    throw new IllegalArgumentException("payType error");
            }
            payTypePayMap.put(payType, pay);
        }

        return pay;
    }
}
