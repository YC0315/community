package com.yc.communitys.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @description: 使用AOP在调用Service层方法之前统一记录日志
 * @author: yangchao
 * @date: 2022/7/30 15:37
 * @param:
 * @return:
 **/
@Component
@Aspect
public class ServiceLogAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceLogAspect.class);

    // 在哪织入逻辑，声明切点
    @Pointcut("execution(* com.yc.communitys.service.*.*(..))")
    public void pointcut() {

    }

    // 在什么时候织入逻辑，前置通知
    @Before("pointcut()")
    // joinPoint是连接点，也就是程序织入的目标中的方法
    public void before(JoinPoint joinPoint) {
        // 用户IP[1.2.3.4], 在[xxxx], 访问了[com.yc.communitys.service.xxx()].
        // 使用工具类RequestContextHolder获取ip
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        // 这是一个特殊的调用kafka调用的service
        if (attributes == null) {
            return;
        }
        // 先得到request
        HttpServletRequest request = attributes.getRequest();
        // 然后再得到ip地址
        String ip = request.getRemoteHost();
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        // 得到目标的类名，方法名
        String target =
                // 得到类名
                joinPoint.getSignature().getDeclaringTypeName()
                        + "."
                        + joinPoint.getSignature().getName(); // 得到方法名
        LOGGER.info(String.format("用户[%s],在[%s],访问了[%s].", ip, now, target));  // 替换占位符
    }

}
