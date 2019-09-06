package com.neuedu.controller.backend;


import com.neuedu.common.ServerResponse;
import com.neuedu.service.impl.BackOrderServicelmpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/manage/order")
public class BackOrderController {

    @Autowired
    BackOrderServicelmpl backOrderServicelmpl;


    /**
     * 订单发货
     * */
    @RequestMapping("/send_goods")
    public ServerResponse send_goods(@RequestParam("orderNo")Long orderNo){
        //登录状态在拦截器中判断
        return backOrderServicelmpl.send_goods(orderNo);
    }

    /**
     * 按订单号查询
     * */
    @RequestMapping("/search")
    public ServerResponse searchByorderNo(@RequestParam("orderNo")Long orderNo){
        return backOrderServicelmpl.backfindorderByorderNo(orderNo);
    }

    /**
     * 订单list
     * */
    @RequestMapping("/list")
    public ServerResponse backorderlist(@RequestParam(value = "pageSize",required = false,defaultValue = "10")Integer pageSize,
                                         @RequestParam(value = "pageNum",required = false,defaultValue = "1")Integer pageNum){

        return backOrderServicelmpl.findallorderMes(pageSize,pageNum);
    }


}
