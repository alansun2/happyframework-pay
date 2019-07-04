package com.ehu.bean;

import com.ehu.weixin.entity.WeChatResponseVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author AlanSun
 * @Date 2016年8月9日
 * 支付返回信息类
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class PayInfoResponse {
    /**
     * 支付宝返回信息
     */
    private String alipayStr;
    /**
     * 微信反回信息
     */
    private WeChatResponseVO weChatResponseVO;
}