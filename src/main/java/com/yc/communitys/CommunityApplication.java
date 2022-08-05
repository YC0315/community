package com.yc.communitys;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
@MapperScan(basePackages = {"com.yc.communitys.dao"})
public class CommunityApplication {
	// 管理Bean的初始化方法
	@PostConstruct
	public void init() {
		// 解决netty启动冲突问题
		// see Netty4Utils.setAvailableProcessors()
		System.setProperty("es.set.netty.runtime.available.processors", "false");
	}

	public static void main(String[] args) {
		// 启动Tomcat，创建容器
		SpringApplication.run(CommunityApplication.class, args);
	}
}
