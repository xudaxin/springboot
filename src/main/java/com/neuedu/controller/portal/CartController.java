package com.neuedu.controller.portal;

import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.UserInfo;
import com.neuedu.service.impl.CartServicelmpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    CartServicelmpl cartServicelmpl;

    /**
     * 购物车列表
     * */
    @RequestMapping("/list")
    @CrossOrigin
    public ServerResponse CartList(HttpSession httpSession){
        UserInfo userInfo=(UserInfo) httpSession.getAttribute("user");
//        if(userInfo==null){
//            return ServerResponse.creatResverResponseByfaile(1,"用户未登录,请登录");
//        }

        return cartServicelmpl.findCartListAll(userInfo.getUsername());
    }



    /**
     * 移除购物车某个商品                                                         这个后期需要完善，应该是可以移除多花个产品
     * 需要通过productid和userid来进行删除，如果只凭借productid来删除的话会把别的用户的购物车中相同的商品也删除了    *
     * */
    @RequestMapping("/delete_product")
    public ServerResponse deteleproByProId(HttpSession httpSession, @RequestParam("productId")Integer productId){
        UserInfo userInfo=(UserInfo) httpSession.getAttribute("user");
//        if(userInfo==null){
//            return ServerResponse.creatResverResponseByfaile(1,"用户未登录,请登录");
//        }
        return cartServicelmpl.deteleproByProId(productId,userInfo.getUsername());
    }


    /**
     * 查询在购物车里的产品数量
     * */

    @RequestMapping("/get_cart_product_count")
    public ServerResponse findcartcounts(HttpSession httpSession){
        UserInfo userInfo=(UserInfo) httpSession.getAttribute("user");
//        if(userInfo==null){
//            return ServerResponse.creatResverResponseByfaile(0,"用户未登录,请登录");
//        }

        return cartServicelmpl.searchproductcounts(userInfo.getUsername());
    }

    /**
     * 购物车全选  与查询用户购物好像没啥区别，也就是总价格发生了变化
     * */
    @RequestMapping("/select_all")
    public ServerResponse selectAllProductInCart(HttpSession httpSession){
        UserInfo userInfo=(UserInfo) httpSession.getAttribute("user");
//        if(userInfo==null){
//            return ServerResponse.creatResverResponseByfaile(10,"用户未登录,请登录");
//        }

        return cartServicelmpl.selectcarttAll(userInfo.getUsername());
    }


    /**
     * 取消全选
     * */
    @RequestMapping("/un_select_all")
    public ServerResponse dropselectAllProductInCart(HttpSession httpSession){
        UserInfo userInfo=(UserInfo) httpSession.getAttribute("user");
//        if(userInfo==null){
//            return ServerResponse.creatResverResponseByfaile(10,"用户未登录,请登录");
//        }
        return cartServicelmpl.dropselectcarttAll(userInfo.getUsername());
    }


    /**
     *购物车选中某个商品
     */
    @RequestMapping("/selectone")
    public ServerResponse selectOneProCart(HttpSession httpSession,@RequestParam("productId")Integer productId){
        UserInfo userInfo=(UserInfo) httpSession.getAttribute("user");
//        if(userInfo==null){
//            return ServerResponse.creatResverResponseByfaile(10,"用户未登录,请登录");
//        }

        return cartServicelmpl.selectOneProCart(productId,userInfo.getUsername());
    }

    /**
     *购物车取消选中的某个商品
     */
    @RequestMapping("/un_select")
    public ServerResponse unselectOneProCart(HttpSession httpSession,@RequestParam("productId")Integer productId){
        UserInfo userInfo=(UserInfo) httpSession.getAttribute("user");
//        if(userInfo==null){
//            return ServerResponse.creatResverResponseByfaile(10,"用户未登录,请登录");
//        }

        return cartServicelmpl.unselectOneProCart(productId,userInfo.getUsername());
    }

    /***
     * 购物车中添加商品
     * */
    @RequestMapping("/add")
    public ServerResponse addcart(HttpSession httpSession,@RequestParam("productId") Integer productId, @RequestParam("count") Integer count){
        UserInfo userInfo=(UserInfo) httpSession.getAttribute("user");
//        if(userInfo==null){
//            return ServerResponse.creatResverResponseByfaile(10,"用户未登录,请登录");
//        }

        return cartServicelmpl.addProductToCart(userInfo.getId(),productId,count);
    }

    /**
     * 更新购物车某个产品数量
     * */

    @RequestMapping("/update")
    public ServerResponse updatecartProductCount(HttpSession httpSession,@RequestParam("productId") Integer productId, @RequestParam("count") Integer count){

        UserInfo userInfo=(UserInfo) httpSession.getAttribute("user");
//        if(userInfo==null){
//            return ServerResponse.creatResverResponseByfaile(10,"用户未登录,请登录");
//        }
        return cartServicelmpl.updatecartProductCount(userInfo.getId(),productId,count);
    }

}
