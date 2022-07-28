package com.yc.communitys.controller.interceptor;


import com.yc.communitys.annotation.LoginRequired;
import com.yc.communitys.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author Jungle
 */
@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    // 尝试去获取用户，获取到了表示已经登录
    @Autowired
    private HostHolder hostHolder;

    /**
     * 在进入Controller之前执行，因此重写preHandle方法
     * 在请求之前将user暂存到hostHolder
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        // 判断拦截的目标是不是方法
        if (handler instanceof HandlerMethod) {
            // 获取拦截到的method对象
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            // 从method对象上取出需要类型的注解，其他的不管
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);
            /*
            * 第一个不为null表示 需要登录的方法中设置了该注解, 即需要登录才行
            * 第二个null表示 没获取到user, 即没登录
            * */
            // 当前方法需要登录但是你又没登录
            if (loginRequired != null && hostHolder.getUser() == null) {
                // /community+/login 重定向到登录页面
                response.sendRedirect(request.getContextPath() + "/login");
                System.out.println("----------------------拦截---------------------");
                // 没登录就拒绝后面的执行
                return false;
            }
        }
        return true;
    }

}
