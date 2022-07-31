package com.yc.communitys.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Component;

// AOP示例
/*@Component
@Aspect  // 声明这是一个切面组件*/
public class AlphaAspect {
    /*
    * 示例: 定义切入点，描述哪些Bean，哪些方法是我们要处理的目标
    * 针对所有的方法
    * execution(* com.nowcoder.community.service.*.*(..))
    *      *       com.nowcoder.community.service .*    .*      (..)
    * 所有的返回值   对应包                          .所有类.所有方法(所有参数)
    * */
    @Pointcut("execution(* com.yc.communitys.service.*.*(..))")
    public void pointcut() {

    }

    // 下面是5种 advice-通知:针对这个切点有效
    @Before("pointcut()") // 连接点 的 一开始织入逻辑
    public void before() {
        System.out.println("before");
    }

    @After("pointcut()") // 后面织入逻辑
    public void after() {
        System.out.println("after");
    }

    @AfterReturning("pointcut()") // 在有返回值以后织入逻辑
    public void afterRetuning() {
        System.out.println("afterRetuning");
    }

    @AfterThrowing("pointcut()") // 在抛异常时织入逻辑
    public void afterThrowing() {
        System.out.println("afterThrowing");
    }

    @Around("pointcut()") // 前后都织入逻辑, 需要参数: ProceedingJoinPoint 连接点
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("around before");
        Object object = joinPoint.proceed(); // 调用目标组件的方法, 可能会有返回值
        System.out.println("around after");
        return object;
    }

}
