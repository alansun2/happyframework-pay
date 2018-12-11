# 集成支付宝和微信
必须为spring项目
使用：
1. 导入
在启动处添加@ImportResource(locations = {"classpath:config/applicationContext-ehpay.xml"})
2. 在application.properties/yml中添加和applicationContext-ehpay对应的属性
3. 使用AlipayUtils或WeChatPayUtil操作

It's all!
