import com.alan344happyframework.bean.OrderPay;
import com.alan344happyframework.bean.OrderRefund;
import com.alan344happyframework.bean.PayBase;
import com.alan344happyframework.bean.TransferMoneyInternal;
import com.alan344happyframework.core.PayUtils;
import com.alan344happyframework.core.TransferAccountsUtils;
import com.alan344happyframework.exception.PayException;
import com.alan344happyframework.weixin.entity.TransferToBankCardParams;
import com.alan344happyframework.weixin.entity.WeChatRefundInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * @author AlanSun
 * @date 2019/11/6 14:56
 */
@Tag("fast")
class PayTest {
    @Test
    @DisplayName("My 1st JUnit 5 test! 😎")
    void testValidation() throws PayException {
        OrderPay order = new OrderPay();
        order.setBody("肥仔快乐水");
        order.setPrice("3.25");
        order.setSubject("盒马生鲜");
        order.setOrderId("sadfasdfas234234");
        order.setNotifyUrl("http://www.baidu.com");
        order.setPayType(PayBase.PAY_TYPE_2);
        PayUtils.createPayInfo(order);
    }


    void testqueryPay() throws PayException {
        OrderRefund refundOrder = new OrderRefund();
        refundOrder.setOrderId("sadfasdfas234234");
        refundOrder.setRefundId("uniquesfsdfsdfsd");
        refundOrder.setRefundAmount("1.23");
        refundOrder.setTotalAmount("3.23");
        refundOrder.setRefundReason("假货");
        refundOrder.setRefundCurrency("CNY");
        refundOrder.setPayType(PayBase.PAY_TYPE_2);
        WeChatRefundInfo weChatRefundInfo = refundOrder.getWeChatRefundInfo();
        weChatRefundInfo.setMchNo(1);
        weChatRefundInfo.setMchAppIdNo(1);
        weChatRefundInfo.setNotifyUrl("http://www.baidu.com");
        PayUtils.refund(refundOrder);
    }

    void testTransferInner() throws PayException {
        TransferMoneyInternal params = new TransferMoneyInternal();
        params.setTransferId("sdfsdf");
        params.setPayeeAccount("18658254585");
        params.setAmount("100");
        params.setPayType(PayBase.PAY_TYPE_2);
        //以下为可选项
        params.setReUserName("张三");
        params.setDesc("转账测试");
        TransferAccountsUtils.transferMoneyInternal(params);
    }

    void testTransfer2Bank() throws PayException {
        TransferToBankCardParams params = new TransferToBankCardParams();
        params.setPartnerTradeNo("sdfsfsdf");
        params.setEncTrueName("张三");
        params.setEncBankNo("62223234235623423423");
        params.setBankCode("1002");
        params.setAmount("0.1");
        params.setDesc("测试银行卡转账");
        params.setMchAppIdNo(1);
        params.setMchNo(1);
        TransferAccountsUtils.transferToBankCard(params);
    }
}
