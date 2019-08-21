package com.neuedu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.neuedu.dao")  //为dao接口声明代理类
public class SpringboApplication {

	public static void main(String[] args) {

		SpringApplication.run(SpringboApplication.class, args);

	}

}
