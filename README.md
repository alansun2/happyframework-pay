# 快速集成支付宝和微信
> 开发项目必须为spring项目

### 1. 使用：
##### 1.1 如何集成到开发项目中？
只要把以下内容，放入`application.properties/yml`并修改为你自己的微信或支付宝信息即可(默认可以不填)。

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
    applets-app-id: ccc #小程序appid
    spbill-create-ip: bbb #调用接口的ip
    notify-url: ddd #微信支付回调地址
    mch-app-id-map: #与商户号绑定的appid或小程序appid，主要用于转账到微信零钱，默认使用第一个支付
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
说明：

alipay
* app-id: 应用appid
* sign-type: 支付宝推荐使用RSA2，这里不填默认RSA2
* private-key: 商户的私钥。表示一个路径，示例：/root/a/private-key
* open-public-key:开放平台支付宝公钥 表示一个路径，示例：/root/a/private-key
* notify-url: fff #支付的默认回调地址，包括app，扫码等

你需要填写的主要有：app-id，private-key，open-public-key，notify-url

wechat
* spbill-create-ip: 调用接口的ip
* applets-app-id: 小程序appid
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

##### 1.2 如何使用
1. `PayUtils`：执行支付、退款、查询订单操作
2. `TransferAccountsUtils`：转账操作（包括转账到零钱和银行卡（支付宝未开放转账到银行卡接口））
        
It's all!
