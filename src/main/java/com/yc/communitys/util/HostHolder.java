package com.yc.communitys.util;

import com.yc.communitys.entity.User;
import org.springframework.stereotype.Component;
/**
 * @description:  采用 ThreadLocal 实现了线程隔离: 以线程对象为key存放用户信息
 * @author: yangchao
 * @date: 2022/7/27 14:21
 **/
@Component
public class HostHolder {
    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    /**
     *  每个Thread都有一个ThreadLocalMap, 它维护着一个Entry数组,
     *  每个 Entry 代表一个完整的对象
     *  Key 是 ThreadLocal, Value 是 <T> 即 User
     *  ThreadLocal 是 弱引用, 一旦发现即回收
     *  即 ThreadLocalMap 的 key没了, value还在
     * 所以, 使用完ThreadLocal后, 及时调用remove方法释放内存空间
     * */
    public void clear() {
        users.remove();
    }
}
