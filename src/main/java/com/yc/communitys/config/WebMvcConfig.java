package com.yc.communitys.config;

import com.yc.communitys.controller.interceptor.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
/**
 * @description: 拦截器配置
 * @author: yangchao
 * @date: 2022/7/27 14:29
 **/
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // 注入我们自定义的拦截器
    @Autowired
    private AlphaInterceptor alphaInterceptor;

    // 注入获取凭证和用户信息的拦截器
    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;
    // 使用springsecurity替代
/*    @Autowired
    private LoginRequiredInterceptor loginRequiredInterceptor;*/

    @Autowired
    private MessageInterceptor messageInterceptor;

    @Autowired
    private DataInterceptor dataInterceptor;

    // 注册接口
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 示例
        registry //注入拦截器
                .addInterceptor(alphaInterceptor)
                //排除静态资源
                .excludePathPatterns("/*/*.css", "/*/*.js", "/*/*.png", "/*/*.jpg", "/*/*.jpeg")
                //明确要拦截的路径
                .addPathPatterns("/register", "/login");

        // (不明确要拦截的路径)对于所有页面-登录凭证
        registry
                .addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/*/*.css", "/*/*.js", "/*/*.png", "/*/*.jpg", "/*/*.jpeg");

        // (不明确要拦截的路径)对于所有页面-需要登录,排除静态资源
//        registry
//                .addInterceptor(loginRequiredInterceptor)
//                .excludePathPatterns("/*/*.css", "/*/*.js", "/*/*.png", "/*/*.jpg", "/*/*.jpeg");

        // (不明确要拦截的路径)对于所有页面
        registry.addInterceptor(messageInterceptor)
                .excludePathPatterns("/*/*.css", "/*/*.js", "/*/*.png", "/*/*.jpg", "/*/*.jpeg");

        // (不明确要拦截的路径)对于所有页面
        registry.addInterceptor(dataInterceptor)
                .excludePathPatterns("/*/*.css", "/*/*.js", "/*/*.png", "/*/*.jpg", "/*/*.jpeg");

    }
}
