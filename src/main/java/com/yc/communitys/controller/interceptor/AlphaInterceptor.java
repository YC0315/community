package com.yc.communitys.controller.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @description: 拦截器示例
 * @author: yangchao
 * @date: 2022/7/26 17:14
 * @param:
 * @return:
 **/
@Component
public class AlphaInterceptor implements HandlerInterceptor {
    // 打印日志
    private static final Logger logger = LoggerFactory.getLogger(AlphaInterceptor.class);

    /**
     * 在Controller处理请求之前执行
     *
     * @param request 通过这个对象, 你可以在在拦截过程中, 加一些自己的操作逻辑
     * @param response 同上
     * @param handler 同上
     * @return true 代表 继续执行; false 代表取消请求, 不应该往下执行
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.debug("preHandle: " + handler.toString());
        return true;
    }

    /**
     * 在Controller之后, 模板引擎之前 执行
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        logger.debug("postHandle: " + handler.toString());
    }

    /**
     * 在模板引擎之后执行
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        logger.debug("afterCompletion: " + handler.toString());
    }
}
