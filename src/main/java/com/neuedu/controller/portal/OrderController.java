package com.neuedu.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.UserInfo;
import com.neuedu.service.impl.OrderServicelmpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    OrderServicelmpl orderServicelmpl;

    /**
     * 创建订单
     */
    @RequestMapping("/create")
    public ServerResponse createOrder(HttpSession httpSession, Integer shippingId) {

        UserInfo userInfo = (UserInfo) httpSession.getAttribute("user");
//        if (userInfo == null) {
//            return ServerResponse.creatResverResponseByfaile(1, "未登录");
//        }
        return orderServicelmpl.createorder(userInfo.getId(), shippingId);

    }

    /**
     * 取消订单
     */

    @RequestMapping("/cancel")
    public ServerResponse cancelorder(HttpSession httpSession, @RequestParam("orderNo") Long orderNo) {
        UserInfo userInfo = (UserInfo) httpSession.getAttribute("user");
//        if (userInfo == null) {
//            return ServerResponse.creatResverResponseByfaile(1, "未登录");
//        }

        return orderServicelmpl.cancelorder(userInfo.getId(), orderNo);
    }


    /**
     * 获取购物车中的订单明细
     */

    @RequestMapping("/get_order_cart_product")
    public ServerResponse get_order_cart_product(HttpSession httpSession) {
        UserInfo userInfo = (UserInfo) httpSession.getAttribute("user");
//        if (userInfo == null) {
//            return ServerResponse.creatResverResponseByfaile(1, "未登录");
//        }

        return orderServicelmpl.get_order_cart_product(userInfo.getId());
    }

    /**
     * 支付接口
     */
    @RequestMapping("/pay/{orderNo}")
    public ServerResponse pay(@PathVariable("orderNo") Long orderNo, HttpSession httpSession) {

        UserInfo userInfo = (UserInfo) httpSession.getAttribute("user");
//        if (userInfo == null) {
//            return ServerResponse.creatResverResponseByfaile(1, "未登录");
//        }

//        Long orderno=Long.valueOf(orderNo);
        return orderServicelmpl.pay(userInfo.getId(), orderNo);
    }

    /**
     * 回调
     */
    @RequestMapping("/callback")
    public String alipay_callback(HttpServletRequest request) {
        Map<String, String[]> callbackParams = request.getParameterMap();

        Map<String, String> signParams = Maps.newHashMap();
        Iterator<String> iterator = callbackParams.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String[] values = callbackParams.get(key);
            StringBuffer stringBuffer = new StringBuffer();
            if (values != null && values.length > 0) {
                for (int i = 0; i < values.length; i++) {
                    stringBuffer.append(values[i]);
                    if (i != values.length - 1) {
                        stringBuffer.append(",");
                    }
                }
            }
            signParams.put(key, stringBuffer.toString());
        }

        //验证签名
        try {
            signParams.remove("sign_type");
            boolean result = AlipaySignature.rsaCheckV2(signParams, Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType() );
            if (result){
                //修改订单的状态与相关参数
                return orderServicelmpl.callback(signParams);
            }else {
                return "false";
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return "success";
    }

    //查询订单支付状态
    @RequestMapping("/query_order_pay_status")
    public ServerResponse findorderstatus(HttpSession httpSession,@RequestParam("orderNo")Long orderNo){
        UserInfo userInfo = (UserInfo) httpSession.getAttribute("user");
//        if (userInfo == null) {
//            return ServerResponse.creatResverResponseByfaile(1, "未登录");
//        }
        return orderServicelmpl.findorderstatus(userInfo.getId(),orderNo);
    }

    //查询用户未付款的订单
    @RequestMapping("/waitpayorder")
    public ServerResponse findwaitpayorder(HttpSession httpSession){
        UserInfo userInfo=(UserInfo)httpSession.getAttribute("user");
        return orderServicelmpl.findwaitpayorderlist(userInfo.getId());
    }

}