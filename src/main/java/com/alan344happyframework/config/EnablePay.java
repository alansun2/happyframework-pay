package com.alan344happyframework.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author AlanSun
 * @date 2019/11/26 17:18
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({AliPay.class, Wechat.class})
public @interface EnablePay {
}
