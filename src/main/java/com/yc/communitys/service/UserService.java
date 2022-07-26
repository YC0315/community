package com.yc.communitys.service;

import com.yc.communitys.dao.UserMapper;
import com.yc.communitys.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: yangchao
 * @createTime: 2022-07-25  15:21
 * @description: user业务逻辑层
 */
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * @description: 根据userId查询用户信息
     * @author: yangchao
     * @date: 2022/7/25 15:21
     * @param:
     * @return:
     **/
    public User findUserById(int userId){
        return userMapper.selectById(userId);
    }
}
