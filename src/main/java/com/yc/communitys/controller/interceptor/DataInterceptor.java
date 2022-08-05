package com.yc.communitys.controller.interceptor;

import com.yc.communitys.entity.User;
import com.yc.communitys.service.DataService;
import com.yc.communitys.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author: yangchao
 * @createTime: 2022-08-03  15:57
 * @description: TODO
 */
@Component
public class DataInterceptor implements HandlerInterceptor {

    @Autowired
    private DataService dataService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 统计UV,从request中获取ip
        String ip = request.getRemoteHost();
        dataService.recordUV(ip);  // 记录ip

        // 统计DAU
        User user = hostHolder.getUser();
        if (user != null) {
            dataService.recordDAU(user.getId());
        }

        return true;
    }
}
