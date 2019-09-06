package com.neuedu.interceptors;

import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.UserInfo;
import com.neuedu.utils.JsonUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class AdminAuthroityInterceptor implements HandlerInterceptor{
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        HttpSession session=request.getSession();
        UserInfo userInfo=(UserInfo) session.getAttribute("user");
        if(userInfo==null){
            response.reset();
            try {
                response.setHeader("Content-Type","text/html;charset=UTF-8");//解决中文乱码问题 application/json
                PrintWriter printWriter=response.getWriter();
                ServerResponse serverResponse= ServerResponse.creatResverResponseByfaile(1,"未登录");
                printWriter.write(JsonUtils.obj2StringPretty(serverResponse));
                printWriter.flush();;
                printWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

    }
}
