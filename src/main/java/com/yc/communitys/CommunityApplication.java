package com.yc.communitys;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 程序的入口的注解
 * @author yangchao
 */
@SpringBootApplication
@MapperScan({"com.yc.communitys"})
public class CommunityApplication {

	public static void main(String[] args) {
		// 启动Tomcat，创建容器
		SpringApplication.run(CommunityApplication.class, args);
	}

}
