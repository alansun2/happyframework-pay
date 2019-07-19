package com.ehu.core.callback;

import com.alipay.api.internal.util.AlipaySignature;
import com.ehu.config.AliPay;
import com.ehu.constants.PayBaseConstants;
import com.ehu.constants.PayResultCodeConstants;
import com.ehu.constants.PayResultMessageConstants;
import com.ehu.core.ConcretePayService;
import com.ehu.exception.PayException;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author AlanSun
 * @date 2019/7/1 16:45
 **/
@Slf4j
public class AlipayCallbackHandler implements CallbackHandler {
    private static final AliPay aliPay = AliPay.getInstance();

    @Override
    public void handler(HttpServletRequest request, HttpServletResponse response, boolean isVerify, ConcretePayService concretePayService) {
        PrintWriter out = null;
        try {
            out = response.getWriter();
            Map<String, String> params = getAlipayCallBackMap(request);
            log.info("支付宝回调开始, 参数：{}", params.toString());

            //调用SDK验证签名
            if (isVerify && !AlipaySignature.rsaCheckV1(params, aliPay.getOpenPublicKey(), aliPay.getInputCharset(), params.get("sign_type)"))) {
                log.error(PayResultMessageConstants.STRING_CALLBACK_VER_10007);
                throw new PayException(PayResultCodeConstants.CALLBACK_VAR_ERROR_10007, PayResultMessageConstants.STRING_CALLBACK_VER_10007);
            }
            CallBackParam callBackParam = this.getCallBackParam(params);
            if (null != callBackParam) {
                concretePayService.handler(callBackParam);
                //不要修改或删除
                out.println("success");
            } else {
                out.println("fail");
            }
        } catch (Exception e) {
            assert out != null;
            out.println("fail");
            log.error("阿里扫码回调fail", e);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    private CallBackParam getCallBackParam(Map<String, String> params) {
        // 商户支付单号
        String out_trade_no = params.get("out_trade_no");
        // 支付宝交易号
        String trade_no = params.get("trade_no");
        // 交易状态
        String trade_status = params.get("trade_status");
        // 付款方支付宝账号
        String buyerId = params.get("buyer_id");
        String total_amount = null;
        if (params.containsKey("total_amount")) {
            total_amount = params.get("total_amount");
        }

        //验签成功则继续业务操作，最后在response中返回success
        if ("TRADE_SUCCESS".equals(trade_status)) {

            CallBackParam callBackParam = new CallBackParam();
            callBackParam.setOrderId(out_trade_no);
            callBackParam.setCallBackPrice(total_amount);
            callBackParam.setPayAccount(buyerId);
            callBackParam.setPayType(PayBaseConstants.ORDER_PAY_TYPE_ALIPAY);
            callBackParam.setThirdOrderId(trade_no);

            return callBackParam;
        }
        return null;
    }

    /**
     * 处理支付宝返回参数
     *
     * @param request HttpServletRequest
     * @return paramsMap
     */
    private static Map<String, String> getAlipayCallBackMap(HttpServletRequest request) {
        //获取支付宝POST过来反馈信息
        Map<String, String[]> requestParams = request.getParameterMap();
        Map<String, String> params = new HashMap<>(requestParams.size());
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            params.put(name, valueStr);
        }
        return params;
    }
}
