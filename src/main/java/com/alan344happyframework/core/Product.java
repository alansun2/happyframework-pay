package com.alan344happyframework.core;

import com.alan344happyframework.constants.SeparatorConstants;
import com.alan344happyframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alan
 * @date 2018/11/12 14:53
 **/
public interface Product {
    /**
     * 支付时给第三方的body长度
     */
    int PRODUCT_NAME_LENGTH = 127;

    /**
     * 获取下单的产品名称，微信不能超过49个字
     *
     * @param products 产品
     * @return 产品名，逗号隔开
     */
    static String getNames(List<? extends Product> products) {
        String body = "";
        if (products != null && products.size() > 0) {
            StringBuilder sb = new StringBuilder();
            StringBuilder sbTem = new StringBuilder();
            int size = products.size(), countSum = 0;
            for (int i = 0; i < size; i++) {
                Product product = products.get(i);
                String productName = product.getName();
                sbTem.append(productName);
                int length = sbTem.toString().getBytes().length;
                if (length > PRODUCT_NAME_LENGTH) {
                    if (i == 0) {
                        String str = sbTem.toString();
                        sb.append(StringUtils.subStringSpecifyBytes(str.length() > 49 ? str.substring(0, 49) : str, PRODUCT_NAME_LENGTH));
                        countSum++;//计算有多少个商品被加入
                        break;
                    } else {
                        break;
                    }
                } else {
                    sb.append(productName);
                    countSum++;//计算有多少个商品被加入
                    if (size != 1) {
                        sb.append(",");
                        sbTem.append(",");
                    }
                }
            }
            if (size > 1) {
                sb.deleteCharAt(sb.lastIndexOf(","));
            }
            body = sb.toString();
            body = body.replaceAll(" ", "");

            if (size > countSum) {
                body = addSoOn(body);
            }
        }
        return body;
    }

    /**
     * 如果有多个商品
     *
     * @param body body
     * @return 处理后的body
     */
    static String addSoOn(String body) {
        String bodyTem = body + "等";
        if (bodyTem.getBytes().length > PRODUCT_NAME_LENGTH) {
            return body.substring(0, body.lastIndexOf(SeparatorConstants.COMMA)) + "等";
        } else {
            return bodyTem;
        }
    }

    /**
     * 返回商品的名称
     *
     * @return 商品的名称
     */
    String getName();

    static void main(String[] args) {
        List<Product> products = new ArrayList<>();
        Product product = () -> "【盒装新鲜毛豆米】300g";
        products.add(product);
        product = () -> "【鲜山羊排】500g±50g";
        products.add(product);
        product = () -> "【豆饼青菜】100g豆饼+250g青菜";
        products.add(product);
        product = () -> "【净甜玉米棒】1000g";
        products.add(product);
        product = () -> "【生菜】500g";
        products.add(product);


        String names = getNames(products);
        System.out.println(names);
        System.out.println(names.length());
        System.out.println(names.getBytes().length);
    }
}
