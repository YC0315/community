package com.yc.communitys.controller.interceptor;

import com.yc.communitys.entity.User;
import com.yc.communitys.service.MessageService;
import com.yc.communitys.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * @description: 查询未读消息数量
 * @author: yangchao
 * @date: 2022/7/31 16:58
 **/

@Component
public class MessageInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private MessageService messageService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        // 查用户的未读消息数量
        if (user != null && modelAndView != null) {
            // 未读私信数量
            int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
            // 未读通知数量
            int noticeUnreadCount = messageService.findNoticeUnreadCount(user.getId(), null);
            modelAndView.addObject("allUnreadCount", letterUnreadCount + noticeUnreadCount);
        }
    }

}
