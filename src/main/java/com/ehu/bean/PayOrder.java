package com.ehu.bean;

import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.ehu.weixin.entity.WechatPayOrder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayOrder extends PayType {
    /**
     * 订单id，必填
     */
    private String orderId;
    /**
     * 订单金额，必填
     */
    private String price;
    /**
     * 对一笔交易的具体描述信息。如果是多种商品，请将商品描述字符串累加传给body。
     * <p>
     * 必填
     */
    private String body;
    /**
     * 商品的标题/交易标题/订单标题/订单关键字等。
     * <p>
     * 必填
     */
    private String subject;
    /**
     * 回调地址
     */
    private String notifyUrl;
    /**
     * 阿里其他参数
     */
    private AlipayTradeAppPayModel alipayTradeAppPayModel;
    /**
     * 微信其他参数
     */
    private WechatPayOrder wechatPayOrder;

}
