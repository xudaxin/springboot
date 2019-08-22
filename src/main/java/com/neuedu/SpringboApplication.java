package com.neuedu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.neuedu.dao")  //为dao接口声明代理类
@EnableScheduling   //定时任务
public class SpringboApplication {

	public static void main(String[] args) {

		SpringApplication.run(SpringboApplication.class, args);

	}

}
