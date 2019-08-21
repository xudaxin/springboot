package com.neuedu.service;

import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.Shipping;

public interface IShippingService {
    //添加地址
    ServerResponse addaddress(Shipping shipping);
    //删除地址
    ServerResponse deleteaddress(Integer shippingId);
    //更新地址
    ServerResponse updateaddress(Shipping shipping);
    //查询地址
    ServerResponse findaddressByshippingId(Integer shippingId);
    //分页查看地址
    ServerResponse selectAlladdresslimit(Integer pageNum,Integer pageSize);
}
