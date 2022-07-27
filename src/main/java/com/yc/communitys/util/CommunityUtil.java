package com.yc.communitys.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.UUID;

/**
 * @description: 工具类
 * @author: yangchao
 * @date: 2022/7/26 15:13
 * @param:
 * @return:
 **/
public class CommunityUtil {

    /**
     * 生成随机字符串
     */
    public static String generateUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    /**
     * MD5加密
     * @param key 需要加密的内容（原始密码+随机字符串）
     */
    public static String md5(String key){
        // 如果为空, 则不加密, 直接返回null (空白符包含：空格、tab键、换行符)
        if(StringUtils.isBlank(key)){
            return null;
        }
        // 将插入的字符串加密成一个16进制的字符串, 传入的参数是byte数组
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    /**
     * 获得JSON格式字符串
     * @param code 编号
     * @param msg 提示信息
     * @param map 业务数据
     */
    public static String getJSONString(int code, String msg, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        if (map != null) {
            for (String key : map.keySet()) {
                json.put(key, map.get(key));
            }
        }
        return json.toJSONString();
    }

    /**
     * 重载: 没有业务数据
     * 获得JSON格式字符串
     * @param code 编号
     * @param msg 提示信息
     * @param map 无业务数据
     */
    public static String getJSONString(int code, String msg) {
        return getJSONString(code, msg, null);
    }

    /**
     * 重载: 没有业务数据, 只有code
     * 获得JSON格式字符串
     * @param code 编号
     * @param msg 无提示信息
     * @param map 无业务数据
     */
    public static String getJSONString(int code) {
        return getJSONString(code, null, null);
    }

}
