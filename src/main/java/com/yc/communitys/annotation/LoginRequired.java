package com.yc.communitys.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: yangchao
 * @createTime: 2022-07-28  15:10
 * @description: 标识是否需要在登录状态下才能访问该方法，打上这个注解，只有登录以后才能访问方法
 */
@Target(ElementType.METHOD)  // 元注解，标识自定义的注解用在方法上
@Retention(RetentionPolicy.RUNTIME)  // 标识注解有效的时机，程序运行时有效
public @interface LoginRequired {
}
