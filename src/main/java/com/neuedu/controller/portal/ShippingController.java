package com.neuedu.controller.portal;

import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.Shipping;
import com.neuedu.pojo.UserInfo;
import com.neuedu.service.impl.ShippingServicelmpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/shipping")
public class ShippingController {

    @Autowired
    ShippingServicelmpl shippingServicelmpl;

    //添加地址
    @RequestMapping("/add")
    public ServerResponse addaddress(HttpSession httpSession,
                                     @RequestParam("userId") Integer userId,
                                     @RequestParam("receiverName") String receiverName,
                                     @RequestParam("receiverPhone") String receiverPhone,
                                     @RequestParam("receiverProvince") String receiverProvince,
                                     @RequestParam("receiverCity") String receiverCity,
                                     @RequestParam("receiverAddress") String receiverAddress,
                                     @RequestParam("receiverZip") String receiverZip){
        UserInfo userInfo=(UserInfo) httpSession.getAttribute("user");
        if(userInfo==null){
            return ServerResponse.creatResverResponseByfaile(1,"未登录");
        }
        Shipping shipping=new Shipping();
        shipping.setUserId(userId);
        shipping.setReceiverName(receiverName);
        shipping.setReceiverPhone(receiverPhone);
        shipping.setReceiverProvince(receiverProvince);
        shipping.setReceiverCity(receiverCity);
        shipping.setReceiverAddress(receiverAddress);
        shipping.setReceiverZip(receiverZip);

        return shippingServicelmpl.addaddress(shipping);
    }


    /**
     * 删除地址
     * */
    @RequestMapping("/del")
    public ServerResponse deleteaddress(HttpSession httpSession, @RequestParam("shippingId")Integer shippingId){
//        UserInfo userInfo=(UserInfo) httpSession.getAttribute("user");
//        if(userInfo==null){
//            return ServerResponse.creatResverResponseByfaile(1,"未登录");
//        }
        return shippingServicelmpl.deleteaddress(shippingId);
    }


    /**
     * 登陆状态更新地址
     * */
    @RequestMapping("/update")
    public ServerResponse updateaddress(HttpSession httpSession,@RequestParam(value = "id",required = false) Integer id,
                                        @RequestParam(value = "receiverName",required = false) String receiverName,
                                        @RequestParam(value = "receiverPhone",required = false) String receiverPhone,
                                        @RequestParam(value = "receiverMobile",required = false) String receiverMobile,
                                        @RequestParam(value = "receiverProvince",required = false) String receiverProvince,
                                        @RequestParam(value = "receiverCity",required = false) String receiverCity,
                                        @RequestParam(value = "receiverAddress",required = false) String receiverAddress,
                                        @RequestParam(value = "receiverZip",required = false) String receiverZip){

//        UserInfo userInfo=(UserInfo) httpSession.getAttribute("user");
//        if(userInfo==null){
//            return ServerResponse.creatResverResponseByfaile(1,"未登录");
//        }
        Shipping shipping=new Shipping();
        shipping.setId(id);
        shipping.setReceiverName(receiverName);
        shipping.setReceiverPhone(receiverPhone);
        shipping.setReceiverMobile(receiverMobile);
        shipping.setReceiverProvince(receiverProvince);
        shipping.setReceiverCity(receiverCity);
        shipping.setReceiverAddress(receiverAddress);
        shipping.setReceiverZip(receiverZip);

        return shippingServicelmpl.updateaddress(shipping);
    }

    /**
     * 选中查看具体的地址
     * */

    @RequestMapping("/select")
    public ServerResponse findaddressByshippingId(HttpSession httpSession,@RequestParam("shippingId") Integer shippingId){
//        UserInfo userInfo=(UserInfo) httpSession.getAttribute("user");
//        if(userInfo==null){
//            return ServerResponse.creatResverResponseByfaile(1,"请登录之后查询");
//        }

        return shippingServicelmpl.findaddressByshippingId(shippingId);
    }

    /**
     * 地址列表 分页
     * */

    @RequestMapping("/list")
    public ServerResponse addresslist(HttpSession httpSession,
                                      @RequestParam(value = "pageNum",required = false,defaultValue = "1") Integer pageNum,
                                      @RequestParam(value = "pageSize",required = false,defaultValue = "10")Integer pageSize ){
//        UserInfo userInfo=(UserInfo) httpSession.getAttribute("user");
//        if(userInfo==null){
//            return ServerResponse.creatResverResponseByfaile(1,"请登录之后查询");
//        }

        return shippingServicelmpl.selectAlladdresslimit(pageNum,pageSize);

    }

}
