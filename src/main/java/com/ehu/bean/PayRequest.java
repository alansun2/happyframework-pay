package com.ehu.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author alan
 * @createtime 18-7-13 下午2:26 * 支付请求类
 */
@Getter
@Setter
@ToString
public class PayRequest {
    private Integer payType;
    private String openId;
}
