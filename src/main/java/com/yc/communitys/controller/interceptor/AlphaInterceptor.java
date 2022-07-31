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
     * @description: 在Controller处理请求之前执行
     * @author: yangchao
     * @date: 2022/7/30 16:04
     * @param: [request, response, handler]
     * @return: boolean
     **/
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.debug("preHandle: " + handler.toString());
        return true;
    }

    /**
     * @description: 在Controller之后, 模板引擎之前 执行
     * @author: yangchao
     * @date: 2022/7/30 16:04
     * @param: [request, response, handler, modelAndView]
     * @return: void
     **/
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        logger.debug("postHandle: " + handler.toString());
    }

    /**
     * @description: 在模板引擎之后执行
     * @author: yangchao
     * @date: 2022/7/30 16:04
     * @param: [request, response, handler, ex]
     * @return: void
     **/
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        logger.debug("afterCompletion: " + handler.toString());
    }
}
