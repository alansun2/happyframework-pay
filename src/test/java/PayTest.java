import com.alan344happyframework.bean.OrderPay;
import com.alan344happyframework.bean.OrderQuery;
import com.alan344happyframework.core.PayIntegrate;
import com.alan344happyframework.core.proxy.ValidationInvocationHandler;
import com.alan344happyframework.exception.PayException;
import com.alan344happyframework.weixin.WechatPayUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * @author AlanSun
 * @date 2019/11/6 14:56
 */
@Tag("fast")
class PayTest {
    @Test
    @DisplayName("My 1st JUnit 5 test! 😎")
    void testValidation() throws PayException {
        InvocationHandler handler = new ValidationInvocationHandler(new WechatPayUtils());
        PayIntegrate sub = (PayIntegrate) Proxy.newProxyInstance(handler.getClass().getClassLoader(), WechatPayUtils.class.getInterfaces(), handler);
        OrderPay order = new OrderPay();
        order.setBody("sdf");
        order.setPrice("2.23");
        order.setSubject("sdfsd");
        order.setOrderId("aaaaaaaaaaaaaaaaaaaaaaabbbbbbbbb");
        sub.createPayInfo(order);
    }
}
