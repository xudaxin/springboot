package com.neuedu;

import com.neuedu.interceptors.AdminAuthroityInterceptor;
import com.neuedu.interceptors.ProtalAuthroityInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@SpringBootConfiguration
public class MySpringBootConfig implements WebMvcConfigurer{

    //拦截后台请求，验证用户是否登录
    @Autowired
    AdminAuthroityInterceptor adminAuthroityInterceptor;

    @Autowired
    ProtalAuthroityInterceptor protalAuthroityInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(adminAuthroityInterceptor)
                .addPathPatterns("/manage/order/**");///manage/**
       // .excludePathPatterns("/manage/login,manage/register");


        List<String> addneedstop=new ArrayList<>();
        addneedstop.add("/shipping/**");//拦截地址
        addneedstop.add("/cart/**");//拦截购物车
        addneedstop.add("/order/**");//拦截订单

        List<String>exneedstop=new ArrayList<>();
        exneedstop.add("/shipping/add");
        exneedstop.add("/order/callback");


        registry.addInterceptor(protalAuthroityInterceptor)
                .addPathPatterns(addneedstop)
                .excludePathPatterns(exneedstop);
    }
}
