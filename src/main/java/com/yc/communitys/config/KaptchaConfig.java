package com.yc.communitys.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @author: yangchao
 * @createTime: 2022-07-26  16:08
 * @description: 生成验证码，自动装配到容器中
 */
@Configuration
public class KaptchaConfig {
    // 生成验证码
    @Bean
    public Producer kaptchaProducer() {
        // 设置验证码的属性
        Properties properties = new Properties();
        properties.setProperty("kaptcha.image.width", "100");
        properties.setProperty("kaptcha.image.height", "40");
        properties.setProperty("kaptcha.textproducer.font.size", "32");
        properties.setProperty("kaptcha.textproducer.font.color", "0,0,0");
        properties.setProperty("kaptcha.textproducer.char.string", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        properties.setProperty("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");

        // 创建验证码
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        // 传入配置
        Config config = new Config(properties);
        kaptcha.setConfig(config);

        // class DefaultKaptcha implements Producer
        return kaptcha;
    }
}
