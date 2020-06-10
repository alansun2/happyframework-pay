# 快速集成支付宝和微信
> 开发项目必须为spring项目

### 1. 使用
#### 1.1. 如何集成到项目中？
1. 把下面的依赖加到你的 pom.xml 即可：
```
    <dependency>
        <groupId>com.alan344</groupId>
        <artifactId>happyframework-pay</artifactId>
        <version>1.5.2-SNAPSHOT</version>
    </dependency>
```
2. 把 `@EnablePay` 注解放到有 @SpringBootApplication/@Configuration 的地方；

2. 把以下内容，放入`application.properties/yml`并修改为你自己的微信或支付宝信息即可(有默认值的可以不填)；
    ```
    pay:
      alipay:
        app-id: ggg #应用appid
        private-key: ccc #商户的私钥。表示一个路径，示例：/root/a/private-key
        gateway-url: ggg #选填。 默认：https://openapi.alipay.com/gateway.do
        open-public-key: eee #开放平台支付宝公钥 表示一个路径，示例：/root/a/private-key
        notify-url: fff #支付回调地址
        input-charset: aaa #选填，默认：UTF-8 
        sign-type: bbb #选填，默认：RSA2
        time-out: 默认：30m 
      wechat:
        spbill-create-ip: bbb #调用接口的ip
        notify-url: ddd #微信支付回调地址
        mch-app-id-map: #与商户号绑定的appid或小程序appid，默认使用第一个支付
          1: ddd
          2: eee
        mch-map: #商户号信息，默认第一个为primary
          1: 
            mch-id: fff #商户号
            public-key: ggg #微信RSA公钥。表示一个路径，示例：/root/a/private-key
            sign-key: hhh #用户md5签名。表示一个路径，示例：/root/a/private-key
            ca: iii #ca证书地址。表示一个路径，示例：/root/a/private-key
            ca-code: jjj #ca证书的密匙。表示一个路径，示例：/root/a/private-key
          2: 
            mch-id: fff #商户号
            public-key: ggg #微信RSA公钥。表示一个路径，示例：/root/a/private-key
            sign-key: hhh #用户md5签名。表示一个路径，示例：/root/a/private-key
            ca: iii #ca证书地址。表示一个路径，示例：/root/a/private-key
            ca-code: jjj #ca证书的密匙。表示一个路径，示例：/root/a/private-key
    ```
    alipay
    * app-id: 应用appid
    * sign-type: 支付宝推荐使用RSA2，这里不填默认RSA2
    * private-key: 商户的私钥。表示一个路径，示例：/root/a/private-key
    * open-public-key:开放平台支付宝公钥 表示一个路径，示例：/root/a/private-key
    * notify-url: fff #支付的默认回调地址，包括app，扫码等
    
    你需要填写的主要有：app-id，private-key，open-public-key，notify-url
    
    wechat
    * spbill-create-ip: 调用接口的ip
    * notify-url: ddd #微信支付回调地址
    * mch-app-id-map: 与商户号绑定的appid或小程序appid，主要用于转账到微信零钱，默认使用第一个appid进行支付，退款，查询订单
      > 该属性为一个map，key为`Integer`，value为`String`
    * mch-map: 商户号信息，默认第一个为primary
      > 该属性为一个map，key为`Integer`，value为`WechatMch`
       *  mch-id: 商户号
       *  public-key: 微信RSA公钥。表示一个路径，示例：/root/a/private-key
       *  sign-key: 用户md5签名。表示一个路径，示例：/root/a/private-key
       *  ca: ca证书地址。表示一个路径，示例：/root/a/private-key
       *  ca-code: ca证书的密匙。表示一个路径，示例：/root/a/private-key

#### 1.2. 如何使用
1. `PayUtils`：支持支付、退款、查询订单操作
2. `TransferAccountsUtils`：支持转账操作（包括转账到零钱和银行卡（**支付宝未开放转账到银行卡接口**））


### 2. 使用示例
#### 2.1. 支付
支付宝
```java
OrderPay order = new OrderPay();
order.setBody("肥仔快乐水");
order.setPrice("3.25");
order.setSubject("盒马生鲜");
order.setOrderId("sadfasdfas234234");
PayUtils.createPayInfo(order);
```

微信
```java
OrderPay order = new OrderPay();
order.setBody("肥仔快乐水");
order.setPrice("3.25");
order.setOrderId("sadfasdfas234234");
order.setPayType(PayBase.PAY_TYPE_2);
// tradeType 默认是 APP，如果要使用小程序支付，需要修改
orderPay.setTradeType(BaseConstants.OS_WX == os ? TradeTypeEnum.APPLET : TradeTypeEnum.APP);
// 小程序支付需要设置 openid
orderPay.getWechatPayOrder().setOpenid(request.getOpenId());
// 选择你配置文件中配置的商户号对应的序号，也就是说，你可以配置多个商户号信息来选择，默认：1
orderPay.getWechatPayOrder().setMchNo(1);
PayUtils.createPayInfo(order);
```

其中 body 可以使用 bodyProducts 来设置，如下：
```java
OrderPay order = new OrderPay();
order.setBodyProducts(bodyProducts);
...
...
PayUtils.createPayInfo(order);
```
使用 bodyProducts 的前提是 bodyProducts 必须实现 Product 接口。notify_url 只要在上面的配置文件中配置了，这里就不需要设置了。

另外对于支付宝/微信的回调，这里我也做了简单的封装，你需要做的就是实现 ConcretePayService 写你的业务逻辑就可以了。具体的做法：
把一下代码放入你的 Spring Controller 中就可以了。
```java
/**
 * 支付宝回调
 *
 * @param request re
 */
@PostMapping(value = "verifyAlipayCallback")
public void verifyAlipayCallback(HttpServletRequest request, HttpServletResponse response) {
    AlipayCallbackHandler.getInstance().handler(request, response, sysConfig.getProfile().equals("pro"), payService);
}

/**
 * 微信回调
 *
 * @param request re
 */
@PostMapping(value = "verifyWXPayCallback")
public void verifyWXPayCallback(HttpServletRequest request, HttpServletResponse response) {
    WechatCallbackHandler.getInstance().handler(request, response, sysConfig.getProfile().equals("pro"), payService);
}
```
上面的例子中 payService 实现了 ConcretePayService 接口。

#### 2.2. 查询支付状态
当前端调起支付并支付成功后，前端可以调用后台的该接口来获取支付是否真的成功。如果成功后台可以做相应的处理。
```java
OrderQuery orderQuery = new OrderQuery();
orderQuery.setOrderId("sadfasdfas234234");
// PayBase.PAY_TYPE_1：支付宝；PayBase.PAY_TYPE_2：微信。默认：PayBase.PAY_TYPE_1
orderQuery.setPayType(PayBase.PAY_TYPE_2);
PayUtils.queryOrder(orderQuery);
```

#### 2.3. 生成二维码
主要用于生成二维码供用户扫码支付。这个接口的调用和 2.1 差不多，只是多了一个 storeId 来记录是那一台设备生成的。

#### 2.4. 查询订单是否支付成功
这个接口的调用参数和 2.2 一样。是 2.2 的一个特例，主要是为了方便查询订单是否支付成功

#### 2.5 退款
支付宝
```java
OrderRefund refundOrder = new OrderRefund();
refundOrder.setOrderId("sadfasdfas234234");
refundOrder.setRefundId("uniquesfsdfsdfsd");
refundOrder.setRefundAmount("1.23");
refundOrder.setTotalAmount("3.23");
refundOrder.setRefundReason("假货");
// 可选参数，默认是人民币
refundOrder.setRefundCurrency("USD");
PayUtils.refund(refundOrder);
```
另外你还可以通过 OrderRefund.alipayTradeRefundModel 属性进行自定义设置支付宝的参数。

微信
```java
OrderRefund refundOrder = new OrderRefund();
refundOrder.setOrderId("sadfasdfas234234");
refundOrder.setRefundId("uniquesfsdfsdfsd");
refundOrder.setRefundAmount("1.23");
refundOrder.setTotalAmount("3.23");
refundOrder.setRefundReason("假货");
refundOrder.setRefundCurrency("CNY");
refundOrder.setPayType(PayBase.PAY_TYPE_2);
// 以下为可选项
WeChatRefundInfo weChatRefundInfo = refundOrder.getWeChatRefundInfo();
weChatRefundInfo.setMchNo(1);// 配置的商户号信息，默认：1
weChatRefundInfo.setMchAppIdNo(1);// 配置的 appid，默认：1
weChatRefundInfo.setNotifyUrl("http://www.baidu.com");// 异步通知 url，其实结果是实时返回的，不配置也是可以的
PayUtils.refund(refundOrder);
```
resultCode == SUCCESS 表示退款成功，如果返回 FAIL 或出现异常则退款失败，你需要定时的去处理这些退款失败的订单。

#### 2.6. 转账到余额/零钱
支付宝
```java
TransferMoneyInternal params = new TransferMoneyInternal();
params.setTransferId("sdfsdf");
params.setPayeeAccount("18658254585");
params.setAmount("100");
//以下为可选项
params.setReUserName("张三");
params.setDesc("转账测试");
TransferAccountsUtils.transferMoneyInternal(params);
```
还可以通过 TransferMoneyInternal.fundTransToaccountTransferModel 配置支付宝的转账信息。

微信
```java
TransferMoneyInternal params = new TransferMoneyInternal();
params.setTransferId("sdfsdf");
params.setPayeeAccount("18658254585");
params.setAmount("100");
params.setPayType(PayBase.PAY_TYPE_2);
//以下为可选项
params.setReUserName("张三");
params.setDesc("转账测试");
TransferAccountsUtils.transferMoneyInternal(params);
```

#### 2.7. 转账到银行卡
只支持微信，支付宝没开放此接口。
```java
TransferToBankCardParams params = new TransferToBankCardParams();
params.setPartnerTradeNo("sdfsfsdf");
params.setEncTrueName("张三");
params.setEncBankNo("62223234235623423423");
params.setBankCode("1002");
params.setAmount("0.1");
// 以下为可选
params.setDesc("测试银行卡转账");
params.setMchAppIdNo(1);
params.setMchNo(1);
TransferAccountsUtils.transferToBankCard(params);
```

#### 2.8. 查询转账到银行卡
2.7 有可能返回 PROCESSING 对于这些订单你需要定时去查询这些订单的状态，知道这些状态为最终状态。

#### 2.9. 查询转账到余额/零钱

It's all!
