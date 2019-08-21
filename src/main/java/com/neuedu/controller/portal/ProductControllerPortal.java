package com.neuedu.controller.portal;


import com.neuedu.common.ServerResponse;
import com.neuedu.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
public class ProductControllerPortal {

    @Autowired
    IProductService iProductService;


    /*
    * 前台--商品详情
    * */

    @RequestMapping("/detail")
    public ServerResponse searchgoodsdetails(@RequestParam("productId")Integer productId){
        return iProductService.searchgoodsdetails(productId);
    }


    /**
     * 前台--搜索商品并排序
     * */


    @RequestMapping("/list")
    public ServerResponse list(@RequestParam(value = "categoryId",required = false)Integer categoryId,
                               @RequestParam(value = "keyword",required = false)String keyword,
                               @RequestParam(value = "pageNum",required = false,defaultValue = "1")Integer pageNum,
                               @RequestParam(value = "pageSize",required = false,defaultValue = "10")Integer pageSize,
                               @RequestParam(value = "orderBy",required = false,defaultValue = "")String orderBy){
        return iProductService.searchPrdlist(categoryId,keyword,pageNum,pageSize,orderBy);
    }
}
