package com.yc.communitys.controller.interceptor;


import com.yc.communitys.entity.LoginTicket;
import com.yc.communitys.entity.User;
import com.yc.communitys.service.UserService;
import com.yc.communitys.util.CookieUtil;
import com.yc.communitys.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * @description: 拦截器获取Ticket，获取用户暂存到ThreadLocal中
 * @author: yangchao
 * @date: 2022/7/27 14:00
 * @param:
 * @return:
 **/
@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;


    /**
     * 在Controller之前执行
     * 在请求之前将user暂存到hostHolder
     * 使用 ThreadLocal<User> users = new ThreadLocal<>(); 实现了多线程下登录信息的数据隔离
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 从cookie中获取指定key的凭证
        String ticket = CookieUtil.getValue(request, "ticket");
        if (ticket != null) {
            // 查询凭证，得到用户的数据
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            // 检查凭证是否有效
            if (loginTicket != null
                    // 状态有效
                    && loginTicket.getStatus() == 0
                    // 超时时间 > 当前时间, 也就是还未超时
                    && loginTicket.getExpired().after(new Date())) {
                // 根据凭证查询用户
                User user = userService.findUserById(loginTicket.getUserId());
                // 在本次请求开始之前就查询到用户，暂存user到ThreadLocal中,请求不结束，线程一直存货，ThreadLocal中的数组一直存在
                hostHolder.setUser(user);
            }
        }
        return true;
    }

    /**
     * 在Controller之后执行
     * 在模板引擎之前使用user, 存入modelAndView
     */
    @Override
    public void postHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }

    /**
     * 在模板引擎之后执行
     * 整个请求结束时, 清除user.
     */
    @Override
    public void afterCompletion(
            HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        // 请求结束之后，清理掉ThreadLocal中的数据
        hostHolder.clear();
    }

}
