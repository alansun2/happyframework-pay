package com.ehu.core;

import com.ehu.bean.PayInfoResponse;
import com.ehu.bean.PayOrder;
import com.ehu.bean.ScanPayOrder;

/**
 * @author 53479
 * @date 2019/7/4 18:02
 **/
public interface Pay {
    /**
     * 创建支付信息
     *
     * @param order 订单
     * @return 支付信息
     */
    PayInfoResponse createPayInfo(PayOrder order);

    /**
     * 二维码扫码支付
     *
     * @return 二维码url
     */
    String getQrCode(ScanPayOrder payOrder);
}
