package com.neuedu.service;

import com.neuedu.common.ServerResponse;

public interface IBackOrderService {
    //订单发货
    ServerResponse send_goods(Long orderNo);

    //后台--按订单号查询订单
    ServerResponse backfindorderByorderNo(Long orderNo);

    //后台查询所有订单信息
    ServerResponse findallorderMes(Integer pageSize,Integer pageNum);
}
