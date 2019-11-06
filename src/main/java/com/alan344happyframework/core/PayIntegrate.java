package com.alan344happyframework.core;

import com.alan344happyframework.alipay.AlipayUtils;
import com.alan344happyframework.bean.PayBase;
import com.alan344happyframework.core.proxy.ValidationInvocationHandler;
import com.alan344happyframework.weixin.WechatPayUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
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
            InvocationHandler handler;
            switch (payType) {
                case PayBase.PAY_TYPE_1:
                    handler = new ValidationInvocationHandler(new AlipayUtils());
                    break;
                case PayBase.PAY_TYPE_2:
                    handler = new ValidationInvocationHandler(new WechatPayUtils());
                    break;
                default:
                    throw new IllegalArgumentException("payType error");
            }
            pay = (PayIntegrate) Proxy.newProxyInstance(handler.getClass().getClassLoader(), new Class[]{PayIntegrate.class}, handler);
            payTypePayMap.put(payType, pay);
        }

        return pay;
    }
}
