package com.yc.communitys.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @description: 工具类，从request中获取cookie
 * @author: yangchao
 * @date: 2022/7/27 14:02
 * @param:
 * @return:
 **/
public class CookieUtil {

    /**
     * 获得指定name的cookie值,name就是key
     */
    public static String getValue(HttpServletRequest request, String name) {
        if (request == null || name == null) {
            throw new IllegalArgumentException("参数为空！");
        }
        // 得到所有的cookie对象
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

}
