package com.ehu.core;

import com.ehu.alipay.AlipayUtils;
import com.ehu.bean.PayInfoResponse;
import com.ehu.bean.PayOrder;
import com.ehu.bean.PayType;

/**
 * @author AlanSun
 * @date 2019/7/4 16:06
 **/
public class PayUtils {

    public static PayInfoResponse getPrePayInfo(PayOrder payOrder) {
        if (PayType.PAY_TYPE_1 == payOrder.getPayType()) {
            AlipayUtils.
        } else {

        }
        return null;
    }
}
