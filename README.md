# 集成支付宝和微信
> 开发项目必须为spring项目

### 1. 使用：
在`application.properties/yml`中添加和`EhPayConfig`对应的属性。具体的属性说明请查看`EhPayConfig`

###### 1.1 如何集成到开发项目中？
只要把以下内容，放入`application.properties/yml`并修改为你自己的微信或支付宝信息即可。
```
pay:
  alipay:
    partner: aaa #合作身份者ID，以2088开头由16位纯数字组成的字符串
    seller: bbb #收款时的支付宝账号，需要与partner对应的支付宝账号为同一个
    private-key: ccc #商户的私钥。表示一个路径，示例：/root/a/private-key
    public-key: ddd #支付宝的公钥。表示一个路径，示例：/root/a/private-key
    input-charset: UTF-8
    sign-type: RSA
    time-out: 30m
    account-name: fff #付款账号名
    gateway-url: https://mapi.alipay.com/gateway.do?
    verify-url: https://mapi.alipay.com/gateway.do?service=notify_verify&
    open-api: https://openapi.alipay.com/gateway.do
    app-id: ggg #应用appid
    md5-key: hhh #md5key。表示一个路径，示例：/root/a/private-key
    open-public-key: eee #开放平台支付宝公钥 表示一个路径，示例：/root/a/private-key
  wechat:
    app-id: aaa #微信app付款的appid
    applets-appid: ccc #小程序appid
    spbill-create-ip: bbb #调用接口的ip
    mch-appid-map: #与商户号绑定的appid或小程序appid，主要用于转账到微信零钱
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
* partner: 合作身份者ID，以2088开头由16位纯数字组成的字符串
* seller: 收款时的支付宝账号，需要与partner对应的支付宝账号为同一个
* private-key: 商户的私钥。表示一个路径，示例：/root/a/private-key
* public-key: 支付宝的公钥。表示一个路径，示例：/root/a/private-key
* open-public-key:开放平台支付宝公钥 表示一个路径，示例：/root/a/private-key
* account-name: 付款账号名
* app-id: 应用appid
* md5-key: md5key。表示一个路径，示例：/root/a/private-key

wechat
* app-id: 微信app付款的appid
* spbill-create-ip: 调用接口的ip
* applets-appid: 小程序appid
* mch-appid-map: 与商户号绑定的appid或小程序appid，主要用于转账到微信零钱
  > 该属性为一个map，key为`Integer`，value为`String`
* mch-map: 商户号信息，默认第一个为primary
  > 该属性为一个map，key为`Integer`，value为`WechatMch`
   *  mch-id: 商户号
   *  public-key: 微信RSA公钥。表示一个路径，示例：/root/a/private-key
   *  sign-key: 用户md5签名。表示一个路径，示例：/root/a/private-key
   *  ca: ca证书地址。表示一个路径，示例：/root/a/private-key
   *  ca-code: ca证书的密匙。表示一个路径，示例：/root/a/private-key
        
It's all!
