package com.ehu.bean;

import com.google.common.base.Joiner;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Alan
 * @date 2018/11/12 14:53
 **/
public interface Product {
    /**
     * 获取下单的产品名称，微信不能超过49个字
     *
     * @param products 产品
     * @return 产品名，逗号隔开
     */
    static String getNames(List<? extends Product> products) {
        String body = "";
        if (products != null && products.size() > 0) {
            body = Joiner.on(",").join(products.stream().map(o -> (o.getName())).collect(Collectors.toList()));

            body = body.replaceAll("\\ ", "");
            if (body.length() > 49) {
                body = body.substring(0, 49) + "等";
            }
        }
        return body;
    }

    String getName();
}
