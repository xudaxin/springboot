package com.neuedu.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.dao.OrderItemMapper;
import com.neuedu.dao.OrderMapper;
import com.neuedu.dao.ShippingMapper;
import com.neuedu.pojo.Order;
import com.neuedu.pojo.OrderItem;
import com.neuedu.pojo.Shipping;
import com.neuedu.service.IBackOrderService;
import com.neuedu.utils.DateUtils;
import com.neuedu.utils.PropertiesUtils;
import com.neuedu.vo.OrderItemVo;
import com.neuedu.vo.OrderVo;
import com.neuedu.vo.ShippingVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Service
public class BackOrderServicelmpl implements IBackOrderService {

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    OrderItemMapper orderItemMapper;

    @Autowired
    ShippingMapper shippingMapper;


    @Override
    public ServerResponse send_goods(Long orderNo) {
        //参数非空判断
        if(orderNo==null){
            return ServerResponse.creatResverResponseByfaile(1,"参数不能为空");
        }
        //根据订单号查询订单信息
        Order order=orderMapper.findByAndOrderNo(orderNo);
        if(order==null){
            return ServerResponse.creatResverResponseByfaile(2,"该订单不存在");
        }
        if(order.getStatus()!= Const.OrderStatusENUM.ORDER_PAYED.getCode()){
            return ServerResponse.creatResverResponseByfaile(2,"订单处于的状态不允许发货");
        }
        //修改订单状态
        order.setStatus(Const.OrderStatusENUM.ORDER_SEND.getCode());
        order.setSendTime(new Date());//发货时间

        //返回结果
        int result=orderMapper.updateByPrimaryKey(order);
        if(result==1){
            return ServerResponse.creatResverResponseBysucess("发货成功");
        }
        return ServerResponse.creatResverResponseByfaile(1,"发货失败");
    }

    /**
     * 后台--按订单号查询订单信息
     * */
    @Override
    public ServerResponse backfindorderByorderNo(Long orderNo) {
        //参数非空校验
        if(orderNo==null){
            return ServerResponse.creatResverResponseByfaile(1,"订单号不能为空");
        }

        //查询到order
        Order order=orderMapper.findByAndOrderNo(orderNo);
        if(order==null){
            return ServerResponse.creatResverResponseByfaile(1,"没有找到订单，订单号不存在");
        }

        //查询到List<orderitem>
        List<OrderItem>orderItemList=orderItemMapper.findorderItenbyOrderNo(orderNo);

        //调用封装好的的函数返回ordervo

        OrderVo orderVo=createordervo(order,orderItemList,order.getShippingId());

        //返回ordervo

        return ServerResponse.creatResverResponseBysucess(orderVo);
    }

    //查询所有的订单信息
    @Override
    public ServerResponse findallorderMes(Integer pageSize,Integer pageNum) {

        List<List<OrderItem>>orderItemList=new ArrayList<>();
        List<OrderVo>orderVoArrayList=new ArrayList<>();
        //分页
        Page page= PageHelper.startPage(pageNum,pageSize);//进行分页 原理是springaop
        List<Order>orderList=orderMapper.selectAll();
        for(Order order:orderList){
            orderItemList.add(orderItemMapper.findorderItenbyOrderNo(order.getOrderNo()));
           Iterator<List<OrderItem>> iterator=orderItemList.iterator();
           while (iterator.hasNext()){
               List<OrderItem>orderItemList1=iterator.next();
                   orderVoArrayList.add(createordervo(order,orderItemList1,order.getShippingId()));
           }
        }
        PageInfo pageInfo=new PageInfo(page);
        return ServerResponse.creatResverResponseBysucess(pageInfo);//orderVoArrayList
    }


    //封装的OrderItemVo
    private OrderItemVo createordervo(OrderItem orderItem){
        OrderItemVo orderItemVo=new OrderItemVo();
        if(orderItem!=null){
            orderItemVo.setQuantity(orderItem.getQuantity());
            orderItemVo.setCreateTime(DateUtils.dateToStr(orderItem.getCreateTime()));
            orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
            orderItemVo.setOrderNo(orderItem.getOrderNo());
            orderItemVo.setProductId(orderItem.getProductId());
            orderItemVo.setProductImage(orderItem.getProductImage());
            orderItemVo.setProductName(orderItem.getProductName());
            orderItemVo.setTotalPrice(orderItem.getTotalPrice());
        }
        return orderItemVo;
    }
    //封装的地址
    private ShippingVo createshippingvo(Shipping shipping){
        ShippingVo shippingVo=new ShippingVo();
        if(shipping!=null){
            shippingVo.setReceiverAddress(shipping.getReceiverAddress());
            shippingVo.setReceiverCity(shipping.getReceiverCity());
            shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
            shippingVo.setReceiverMobile(shipping.getReceiverMobile());
            shippingVo.setReceiverName(shipping.getReceiverName());
            shippingVo.setReceiverPhone(shipping.getReceiverPhone());
            shippingVo.setReceiverProvince(shipping.getReceiverProvince());
            shippingVo.setReceiverZip(shipping.getReceiverZip());
        }
        return shippingVo;
    }

    //封装的ordervo
    private OrderVo createordervo(Order order,
                                  List<OrderItem>orderItemList,
                                  Integer shippingId){
        OrderVo orderVo=new OrderVo();
        List<OrderItemVo>orderItemVoList= Lists.newArrayList();
        for(OrderItem orderItem:orderItemList){
            orderItemVoList.add(createordervo(orderItem));
        }
        orderVo.setOrderItemVoList(orderItemVoList);
        orderVo.setImageHost(PropertiesUtils.readByKey("ImageHost"));
        Shipping shipping=shippingMapper.selectByPrimaryKey(shippingId);
        if(shipping!=null){
            orderVo.setShippingId(shippingId);
            ShippingVo shippingVo= createshippingvo(shipping);
            orderVo.setShippingVo(shippingVo);
            orderVo.setReceiverName(shipping.getReceiverName());
        }
        orderVo.setStatus(order.getStatus());
        Const.OrderStatusENUM orderStatusENUM=Const.OrderStatusENUM.codeOf(order.getStatus());
        if(orderStatusENUM!=null){
            orderVo.setStatusDesc(orderStatusENUM.getDesc());
        }
        orderVo.setPostage(0);
        orderVo.setPayment(order.getPayment());
        orderVo.setPaymentType(order.getPaymentType());

        Const.PaymentENUM paymentENUM=Const.PaymentENUM.codeOf(order.getPaymentType());
        if(paymentENUM!=null){
            orderVo.setPaymentTypeDesc(paymentENUM.getDesc());
        }
        orderVo.setOrderNo(order.getOrderNo());
        return orderVo;
    }
}
