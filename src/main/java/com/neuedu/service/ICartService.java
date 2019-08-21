package com.neuedu.service;

import com.neuedu.common.ServerResponse;

public interface ICartService  {
    //购物车列表
    ServerResponse findCartListAll(String username);
    //删除购物车中的某一物品
    ServerResponse deteleproByProId(Integer productId,String username);
    //查询购物车中的产品数量
    ServerResponse searchproductcounts(String username);
    //全选
    ServerResponse selectcarttAll(String username);

    //取消全选
    ServerResponse dropselectcarttAll(String username);

    //选中某一商品
    ServerResponse selectOneProCart(Integer productId,String username);

    //取消选中的某一商品
    ServerResponse unselectOneProCart(Integer productId,String username);

    //添加商品到购物车
    ServerResponse addProductToCart(Integer userId,Integer productId,Integer count);

    //更新购物车某个商品的数量
    ServerResponse updatecartProductCount(Integer userId,Integer productId, Integer count);
}