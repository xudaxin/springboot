package com.neuedu.controller.backend;

import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.Category;
import com.neuedu.pojo.Product;
import com.neuedu.pojo.UserInfo;
import com.neuedu.pojo.productInfoPage;
import com.neuedu.service.impl.ProductServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/manage/product")
public class ProductController {
    @Autowired
    private ProductServiceImpl productService;
    /*
     * 新增或更新商品
     * */

    @RequestMapping("saveOrupdate")
    public ServerResponse addOrUpdateProduct(HttpSession httpSession,Product product){
        //判断是否登录
        UserInfo userInfo=(UserInfo) httpSession.getAttribute("user");
        if(userInfo==null){
            return ServerResponse.creatResverResponseByfaile(1,"未登录");
        }
        //判断是否有权限
        if(userInfo.getRole()==0){
            return ServerResponse.creatResverResponseByfaile(2,"没有权限");
        }

        return productService.addorupdateproduct(product);

    }

    @RequestMapping("set_sale_status")
    public ServerResponse upOrdeleteProduct(HttpSession httpSession,int productId,int status){
//        int id=Integer.parseInt(productId);
//        int stat=Integer.parseInt(status);
        UserInfo userInfo=(UserInfo) httpSession.getAttribute("user");
        if(userInfo==null){
            return ServerResponse.creatResverResponseByfaile(1,"未登录");
        }
        //判断是否有权限
        if(userInfo.getRole()==0){
            return ServerResponse.creatResverResponseByfaile(2,"没有权限");
        }
        return productService.onOrdeleteProduct(productId,status);
    }


    //获取商品详情
    @RequestMapping("/detail")
    public ServerResponse findProDetail(HttpSession httpSession,int productId){
        UserInfo userInfo=(UserInfo) httpSession.getAttribute("user");
        if(userInfo==null){
            return ServerResponse.creatResverResponseByfaile(1,"未登录");
        }
        //判断是否有权限
        if(userInfo.getRole()==0){
            return ServerResponse.creatResverResponseByfaile(1,"没有权限");
        }
        return productService.findproductdetailbyid(productId);
    }

    //查看商品列表
    @RequestMapping("/list")
    public ServerResponse findProDetail(HttpSession httpSession,
                                        @RequestParam(value = "pageNum",required = false,defaultValue = "1")int pageNum,
                                        @RequestParam(value = "pageSize",required = false,defaultValue = "10")int pageSize){
        UserInfo userInfo=(UserInfo) httpSession.getAttribute("user");
        if(userInfo==null){
            return ServerResponse.creatResverResponseByfaile(10,"用户未登录，请先登录");
        }
        //判断是否有权限
        if(userInfo.getRole()==0){
            return ServerResponse.creatResverResponseByfaile(1,"没有权限");
        }
        return productService.findProLimit(pageNum,pageSize);
    }

    //产品搜索
    @RequestMapping("/search")
    public ServerResponse searchProduct(HttpSession httpSession,
                                        @RequestParam(value = "productName",required = false)String productName,
                                        @RequestParam(value = "productId",required = false)Integer productId,
                                        @RequestParam(value = "pageNum",required = false,defaultValue = "1")Integer pageNum,
                                        @RequestParam(value = "pageSize",required = false,defaultValue = "10")Integer pageSize){
        UserInfo userInfo=(UserInfo) httpSession.getAttribute("user");
        if(userInfo==null){
            return ServerResponse.creatResverResponseByfaile(10,"用户未登录，请先登录");
        }
        //判断是否有权限
        if(userInfo.getRole()==0){
            return ServerResponse.creatResverResponseByfaile(1,"没有权限");
        }

        return productService.searchProduct(productName,productId,pageNum,pageSize);
    }




}
