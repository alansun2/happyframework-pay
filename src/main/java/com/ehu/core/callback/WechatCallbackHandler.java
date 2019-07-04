package com.ehu.core.callback;

import com.alan344happyframework.util.StringUtils;
import com.ehu.config.Wechat;
import com.ehu.constants.PayBaseConstants;
import com.ehu.constants.PayResultCodeConstants;
import com.ehu.exception.PayException;
import com.alan344happyframework.util.XmlUtils;
import com.ehu.weixin.util.Signature;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.jdom2.JDOMException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * @author AlanSun
 * @date 2019/7/1 18:28
 **/
@Slf4j
public class WechatCallbackHandler implements CallbackHandler {
    @Override
    public CallBackParam getCallBackParam(Map<String, String> params) {
        log.debug("微信支付回调开始");
        if (params.containsKey("return_code")) {
            String return_code = params.get("return_code");
            String out_trade_no;
            String transaction_id;
            String openId;
            String totalFee;
            //String result_code = null;
            //String appid = null;
            //String mch_id = null;
            if (PayBaseConstants.RETURN_SUCCESS.equals(return_code)) {
                if (params.containsKey("result_code")) {
                    String result_code = params.get("result_code");
                    if (!PayBaseConstants.RETURN_SUCCESS.equals(result_code)) {
                        throw new PayException(PayResultCodeConstants.ERROR_CODE_WECHATPAY_10004, "result_code fail");
                    }
                    out_trade_no = params.get("out_trade_no");
                    //result_code = params.get("result_code");
                    transaction_id = params.get("transaction_id");
                    totalFee = params.get("total_fee");
                    openId = params.get("openid");
                    //appid = params.get("appid");
                    //mch_id = params.get("mch_id");
                } else {
                    throw new PayException(PayResultCodeConstants.ERROR_CODE_WECHATPAY_10004, "result_code为空");
                }

                CallBackParam callBackParam = new CallBackParam();
                callBackParam.setOrderId(out_trade_no);
                callBackParam.setCallBackPrice(totalFee);
                callBackParam.setPayAccount(openId);
                callBackParam.setPayType(PayBaseConstants.ORDER_PAY_TYPE_WECHATPAY);
                callBackParam.setThirdOrderId(transaction_id);

                return callBackParam;
            } else {
                throw new PayException(PayResultCodeConstants.ERROR_CODE_WECHATPAY_10004, "return_code为fail");
            }
        }
        return null;
    }

    @Override
    public void handler(HttpServletRequest request, HttpServletResponse response, boolean isVerify, ConcretePayService concretePayService) {
        PrintWriter out = null;
        try {
            out = response.getWriter();
            Map<String, String> map = getWechatPayCallBackmap(request);

            if (!isVerify || checkIsSignValidFromResponseString(map)) {
                CallBackParam callBackParam = getCallBackParam(map);
                if (null != callBackParam) {
                    concretePayService.handler(callBackParam);
                    out.write(getWXReturn(1));
                } else {
                    out.println(getWXReturn(2));
                }
            } else {
                out.println(getWXReturn(2));
            }
        } catch (Exception e) {
            assert out != null;
            out.println(getWXReturn(2));
            log.error("微信回调fail", e);
        } finally {
            if (out != null)
                out.close();
        }
    }

    /**
     * 检验API返回的数据里面的签名是否合法，避免数据在传输的过程中被第三方篡改
     *
     * @param map API返回的XML数据字符串
     * @return API签名是否合法
     */
    public static boolean checkIsSignValidFromResponseString(Map<String, String> map) {
        String signResponse = map.get("sign");
        if (StringUtils.isEmpty(signResponse)) {
            log.error("API返回的数据签名数据不存在，有可能被第三方篡改!!!");
            return false;
        }
        //清掉返回数据对象里面的Sign数据（不能把这个数据也加进去进行签名），然后用签名算法进行签名
        map.remove("sign");

        //将API返回的数据根据用签名算法进行计算新的签名，用来跟API返回的签名进行比较
        Wechat config = Wechat.getInstance();

        //获取签名
        String signLocal = Signature.getSign(map, config.getMchMap().get(Wechat.DEFAULT_MCH).getSignKey());

        if (!signLocal.equals(signResponse)) {
            //签名验不过，表示这个API返回的数据有可能已经被篡改了
            log.error("API返回的数据签名验证不通过，有可能被第三方篡改!!!，微信的sign:{}, 本地sign:{}", signResponse, signLocal);
            return false;
        }
        return true;
    }

    /**
     * 处理微信回调信息
     *
     * @param httpServletRequest httpServletRequest
     * @return paramsMap
     * @throws IOException   e
     * @throws JDOMException e
     */
    private static Map<String, String> getWechatPayCallBackmap(HttpServletRequest httpServletRequest) throws Exception {
        BufferedReader reader;
        String line;
        StringBuilder inputString = new StringBuilder();
        reader = httpServletRequest.getReader();
        try {
            // 获取收到的报文
            while ((line = reader.readLine()) != null) {
                inputString.append(line);
            }

            return com.alan344happyframework.util.XmlUtils.xmlToMap(inputString.toString());
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    /**
     * 生成微信回调xml
     *
     * @param type 1：SUCCESS;2:FAIL
     * @return str
     */
    private static String getWXReturn(int type) {
        if (1 == type)
            return XmlUtils.mapToXml(ImmutableMap.of("return_code", "SUCCESS", "return_msg", "OK"));
        return XmlUtils.mapToXml(ImmutableMap.of("return_code", "FAIL", "return_msg", "alerdy ok"));
    }
}
