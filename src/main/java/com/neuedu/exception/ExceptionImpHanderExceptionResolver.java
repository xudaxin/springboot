//package com.neuedu.exception;
//
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerExceptionResolver;
//import org.springframework.web.servlet.ModelAndView;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.util.Map;
//
//@Component
//public class ExceptionImpHanderExceptionResolver implements HandlerExceptionResolver {
//    @Override
//    public ModelAndView resolveException(HttpServletRequest httpServletRequest,
//                                         HttpServletResponse httpServletResponse,
//                                         Object o,
//                                         Exception e) {
//
//        Myexception myexception=(Myexception) e;
//        String error=myexception.getDirector();//跳转的界面  注意这个时候不需要再加后缀了jsp
//        ModelAndView modelAndView=new ModelAndView();
//        modelAndView.setViewName(error);//跳转的界面？？？？？？  没错
//
//        //额外传一些数据的话
//        Map<String,Object> model=modelAndView.getModel();
//        model.put("es","es1");//在jsp中获取值：${es}
//
//        return modelAndView;
//    }
//}
