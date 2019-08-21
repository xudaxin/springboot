package com.neuedu.service;

import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.UserInfo;

import java.util.Map;

public interface IOrderService {

    //创建订单
    ServerResponse createorder(Integer userId,Integer shippingId);
    //取消订单
    ServerResponse cancelorder(Integer userId,Long orderNo);
    //获取购物车中订单明细
    ServerResponse get_order_cart_product(Integer userId);

    //订单支付
    ServerResponse pay(Integer userId,Long orderNo);

    //支付回调接口
    String callback(Map<String, String> requestParams);

    //查询订单状态
    ServerResponse findorderstatus(Integer userId,Long orderNo);
}
