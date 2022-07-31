package com.yc.communitys.controller.advice;

import com.yc.communitys.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

// 统一处理异常(自动扫描所有带Controller注解的类,捕获异常)
// 用@ControllerAdvice 可以将所有controller抛出的异常都拦截到
@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    // 记录日志
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler({Exception.class}) // 处理所有异常的方法
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOGGER.error("服务器发生异常: ", e.getMessage());
        for (StackTraceElement element : e.getStackTrace()) {
            // 记录每条异常到日志
            LOGGER.error(element.toString());
        }
        // 给浏览器一个响应
        // 通过request判断请求方式
        String xRequestedWith = request.getHeader("x-requested-with");
        // 如果 xRequestedWith 为 XMLHttpRequest 则为 Ajax 异步HTTP请求
        String type = "XMLHttpRequest";
        if (type.equals(xRequestedWith)) {
            // 异步请求要响应一个json字符串，不需要重定向
            // text/plain的意思是将文件设置为纯文本的形式，浏览器在获取到这种文件时并不会对其进行处理
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJSONString(1, "服务器异常！"));
        } else {
            // 普通请求则重定向到错误页面
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }

}
