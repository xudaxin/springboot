package com.neuedu.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayMonitorService;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayMonitorServiceImpl;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.service.impl.AlipayTradeWithHBServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.google.common.collect.Lists;
import com.neuedu.alipay.Main;
import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.dao.*;
import com.neuedu.pojo.*;
import com.neuedu.service.IOrderService;
import com.neuedu.utils.BigDecimalUtils;
import com.neuedu.utils.DateUtils;
import com.neuedu.utils.PropertiesUtils;
import com.neuedu.vo.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class OrderServicelmpl implements IOrderService {
    @Autowired
    OrderMapper orderMapper;

    @Autowired
    CartMapper cartMapper;

    @Autowired
    OrderItemMapper orderItemMapper;

    @Autowired
    ProductMapper productMapper;

    @Autowired
    ShippingMapper shippingMapper;

    @Autowired
    PayInfoMapper payInfoMapper;

//    @Value("${business.ImageHost}")
//    private String ImageHost;

    //创建订单
    @Override
    public ServerResponse createorder(Integer userId, Integer shippingId) {
        //参数非空校验
        if(shippingId==null){
            return ServerResponse.creatResverResponseByfaile(1,"地址参数不能为空");
        }

        //根据userId查询购物车中已选中的商品  List<Cart>
        List<Cart> cartList=cartMapper.fingproductselected(userId);

        //List<Cart>-->List<OrderItem>
        ServerResponse serverResponse=getCartOrderItem(userId,cartList);
        if(!serverResponse.isSucess()){
            return serverResponse;
        }

        //创建订单order并将其保存到数据库

         //计算订单的总价格
        List<OrderItem>orderItems=(List<OrderItem>) serverResponse.getData();  //返回订单详情
//        if(orderItems==null||orderItems.size()==0){
//
//        }

        Order order=createOrder(userId,shippingId,getOrderPrice(orderItems));
        if(order==null){
            return ServerResponse.creatResverResponseByfaile(1,"订单创建失败");
        }
        //将ist<OrderItem>保存到数据库
        for(OrderItem orderItem:orderItems){
            orderItem.setOrderNo(order.getOrderNo());
        }
        //批量插入
        orderItemMapper.insertOrderItemList(orderItems);
        //扣除商品的库存
        reduceProductStock(orderItems);
        //购物车里面已下单的商品要移除
        cleanCart(cartList);

        //返回结果OrderVo
        OrderVo orderVo=createordervo(order,orderItems,shippingId);
        return ServerResponse.creatResverResponseBysucess(orderVo);
    }

    //取消订单
    @Override
    public ServerResponse cancelorder(Integer userId, Long orderNo) {
        //参数非空校验
        if(orderNo==null){
            return ServerResponse.creatResverResponseByfaile(9,"参数不能为空");
        }
        //根据userid和orderno去查询订单
        Order order=orderMapper.findByuserIdAndOrderNo(userId,orderNo);
        if(order==null){
            return ServerResponse.creatResverResponseByfaile(1,"该用户没有此订单");
        }
        //判断订单状态
        if (order.getStatus()!=Const.OrderStatusENUM.ORDER_UN_PAY.getCode()){
            return ServerResponse.creatResverResponseByfaile(6,"订单不可取消");
        }
        //取消订单
        order.setStatus(Const.OrderStatusENUM.ORDER_CANCELED.getCode());
        int result=orderMapper.updateByPrimaryKey(order);

        //返回结果
        if(result==1) {
            return ServerResponse.creatResverResponseBysucess("订单取消成功");
        }
        return ServerResponse.creatResverResponseByfaile(1,"订单取消失败");
    }

    //获取购物车中订单明细
    @Override
    public ServerResponse get_order_cart_product(Integer userId) {

        //查询购物车中该用户选择的商品
        List<Cart>cartList=cartMapper.fingproductselected(userId);
        //List<Cart>-->List<OrderItem>
        ServerResponse serverResponse=getCartOrderItem(userId,cartList);
        if(!serverResponse.isSucess()){
            return serverResponse;
        }

        //组装
        CartOrderItemVo cartOrderItemVo=new CartOrderItemVo();
        cartOrderItemVo.setImageHost(PropertiesUtils.readByKey("ImageHost"));
        List<OrderItem>orderItems=(List<OrderItem>)serverResponse.getData();
        List<OrderItemVo>orderItemVoList=Lists.newArrayList();
        if(orderItems==null||orderItems.size()==0){
            return ServerResponse.creatResverResponseByfaile(1,"购物车为空");
        }
        for(OrderItem orderItem:orderItems){
            orderItemVoList.add(createordervo(orderItem));
        }
        cartOrderItemVo.setOrderItemVoList(orderItemVoList);
        cartOrderItemVo.setTotalPrice(getOrderPrice(orderItems));
        return ServerResponse.creatResverResponseBysucess(cartOrderItemVo);
    }

    //支付宝支付
    @Override
    public ServerResponse pay(Integer userId, Long orderNo) {
        //参数校验
        if(orderNo==null){
            return ServerResponse.creatResverResponseByfaile(1,"订单号不能为空");
        }
        //根据orderid和userid查询订单信息
        Order order=orderMapper.findByuserIdAndOrderNo(userId,orderNo);
        if(order==null){
            return ServerResponse.creatResverResponseByfaile(1,"订单不存在");
        }

        return pay(userId,order);
    }

    @Override
    public String callback(Map<String, String> requestParams) {
        //订单号
        String orderNo=requestParams.get("out_trade_no");
        //流水号
        String trade_no=requestParams.get("trade_no");
        //支付时间
        String gmt_payment=requestParams.get("gmt_payment");
        //支付状态
        String trade_status=requestParams.get("trade_status");

        Order order=orderMapper.findByAndOrderNo(Long.valueOf(orderNo));
        if(order==null){
            return "fail";
        }
        if(trade_status.equals("TRADE_SUCCESS")){
            //支付成功
            //修改订单状态  支付时间   订单状态
            order.setStatus(Const.OrderStatusENUM.ORDER_PAYED.getCode());
            order.setPaymentTime(DateUtils.strToDate(gmt_payment));
            int result=orderMapper.updateByPrimaryKey(order);
            if(result<=0){
                return "fail";
            }

        //添加支付记录
        PayInfo payInfo=new PayInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(Long.valueOf(orderNo));
        payInfo.setPayPlatform(Const.PaymentENUM.ONLINE.getCode());
        payInfo.setPlatformNumber(trade_no);
        payInfo.setPlatformStatus(trade_status);
        int result2=payInfoMapper.insert(payInfo);
        if(result2<=0){
            return "fail";
        }
        return "success";
        }

      return "fail";
    }

    @Override
    public ServerResponse findorderstatus(Integer userId, Long orderNo) {
        //参数非空校验
        if(orderNo==null){
            return ServerResponse.creatResverResponseByfaile(1,"订单号不能为空");
        }
        //查询订单
        Order order=orderMapper.findByuserIdAndOrderNo(userId,orderNo);
        if(order==null){
            return ServerResponse.creatResverResponseByfaile(1,"该用户并没有该订单,查询无效");
        }
        int result=order.getStatus();//订单状态：0-已取消 10-未付款 20-已付款 40-已发货 50-交易成功 60-交易关闭'
        if(result==0)
            return ServerResponse.creatResverResponseBysucess("已取消");
        else if(result==10)
            return ServerResponse.creatResverResponseBysucess("未付款");
        else if(result==20)
            return ServerResponse.creatResverResponseBysucess("已付款");
        else if(result==40)
            return ServerResponse.creatResverResponseBysucess("已发货");
        else if(result==50)
            return ServerResponse.creatResverResponseBysucess("交易成功");
        else if(result==60)
            return ServerResponse.creatResverResponseBysucess("交易关闭");
        else
            return ServerResponse.creatResverResponseByfaile(10,"订单处于未知状态");
    }

    //关闭订单
    @Override
    public List<Order> closeOrder(String closeOrderDate) {
        List<Order>orderList=orderMapper.findcloseOrder(closeOrderDate);
        if(orderList==null||orderList.size()==0){
            return null;
        }
        //通过订单找到多个订单明细表
        for(Order order:orderList){
            List<OrderItem>orderItemList=orderItemMapper.findorderItenbyOrderNo(order.getOrderNo());
            for(OrderItem orderItem:orderItemList){
                //查找到商品的信息,通过userid和productid
                Product product=productMapper.selectByPrimaryKey(orderItem.getProductId());
                if(product==null){
                    continue;
                }
                product.setStock(product.getStock()+orderItem.getQuantity());
                //更新
                productMapper.updateByPrimaryKey(product);  //库存更新完
            }
            //修改订单状态
            order.setStatus(Const.OrderStatusENUM.ORDER_ClOSED.getCode());
            orderMapper.updateByPrimaryKey(order);
        }
        return null;
    }

    private static Log log = LogFactory.getLog(Main.class);

    // 支付宝当面付2.0服务
    private static AlipayTradeService tradeService;

    // 支付宝当面付2.0服务（集成了交易保障接口逻辑）
    private static AlipayTradeService   tradeWithHBService;

    // 支付宝交易保障接口服务，供测试接口api使用，请先阅读readme.txt
    private static AlipayMonitorService monitorService;

    static {
        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

        // 支付宝当面付2.0服务（集成了交易保障接口逻辑）
        tradeWithHBService = new AlipayTradeWithHBServiceImpl.ClientBuilder().build();

        /** 如果需要在程序中覆盖Configs提供的默认参数, 可以使用ClientBuilder类的setXXX方法修改默认参数 否则使用代码中的默认设置 */
        monitorService = new AlipayMonitorServiceImpl.ClientBuilder()
                .setGatewayUrl("http://mcloudmonitor.com/gateway.do").setCharset("GBK")
                .setFormat("json").build();
    }

    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            log.info("body:" + response.getBody());
        }
    }

    // 测试当面付2.0生成支付二维码
    public ServerResponse pay(Integer userId,Order order) {
        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = String.valueOf(order.getOrderNo());

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = "xxx品牌xxx门店当面付扫码消费";

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = "购买商品共"+order.getPayment()+"元";

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        //根据orderno查询订单明细

        List<OrderItem>orderItemList=orderItemMapper.findOrderItemByOrderNoAndUserId(userId,order.getOrderNo());
        if(orderItemList==null||orderItemList.size()==0){
            return ServerResponse.creatResverResponseByfaile(1,"没有可购买的商品");
        }

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
        for(OrderItem orderItem:orderItemList){
            // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
            GoodsDetail goods1 = GoodsDetail.newInstance(orderItem.getProductId().toString(), orderItem.getProductName(), orderItem.getCurrentUnitPrice().intValue(), orderItem.getQuantity());
            // 创建好一个商品后添加至商品明细列表
            goodsDetailList.add(goods1);
        }

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl("http://siunp4.natappfree.cc/order/callback")//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                // 需要修改为运行机器上的路径
                String filePath = String.format("D:/zfb/qr-%s.png",
                        response.getOutTradeNo());
                log.info("filePath:" + filePath);
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, filePath);
                PayVo payVo=new PayVo(order.getOrderNo(),PropertiesUtils.readByKey("ImageHost")+"qr-"+response.getOutTradeNo()+".png");
                return ServerResponse.creatResverResponseBysucess(payVo);

            case FAILED:
                log.error("支付宝预下单失败!!!");
                break;

            case UNKNOWN:
                log.error("系统异常，预下单状态未知!!!");
                break;

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                break;
        }
        return ServerResponse.creatResverResponseByfaile(1,"失败");
    }


    /**
 *构建OrderVo
 **/
private OrderVo createordervo(Order order,
                              List<OrderItem>orderItemList,
                              Integer shippingId){

    OrderVo orderVo=new OrderVo();
    List<OrderItemVo>orderItemVoList=Lists.newArrayList();
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
/**
 * 构建shippingvo
 * */

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

/**
 * 构建OrderItemVo
 * */

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
    /**
     * 移除购物车里面已下单的商品
     * */
    //批量删除

    private void cleanCart(List<Cart> carts){
        if(carts!=null&&carts.size()>0){
            cartMapper.deleteCartProByed(carts);
        }
    }

    /**
     * 扣库存
     * */
    private void reduceProductStock(List<OrderItem>orderItemList){
        for(OrderItem orderItem:orderItemList){
            Product product=productMapper.selectByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock()-orderItem.getQuantity());
            productMapper.updateByPrimaryKey(product);
        }
    }

    //计算订单价格
    private BigDecimal getOrderPrice(List<OrderItem> orderItemList){
        BigDecimal ordertotal=new BigDecimal("0");
        for(OrderItem orderItem:orderItemList){
            ordertotal= BigDecimalUtils.add(ordertotal,orderItem.getTotalPrice());
        }
        return ordertotal;
    }

    /**
     * 创建订单
     * */
    private Order createOrder(Integer userId,Integer shippingId,BigDecimal orderTotalPrice){
        Order order=new Order();
        order.setOrderNo(generateOrderNO());//订单号的生成
        order.setUserId(userId);
        order.setShippingId(shippingId);
        order.setStatus(Const.OrderStatusENUM.ORDER_UN_PAY.getCode());//未付款
        //订单金额
        order.setPayment(orderTotalPrice);
        order.setPostage(0);
        order.setPaymentType(Const.PaymentENUM.ONLINE.getCode());

        //将订单信息保存到数据库
        int result=orderMapper.insert(order);
        if(result==1){
            return order;
        }
        return null;
    }

    //生成订单编号  用户量少的时候可以使用
    private Long generateOrderNO(){
        return System.currentTimeMillis()+new Random().nextInt(100);
    }

    //商品明细转为订单明细
    private ServerResponse getCartOrderItem(Integer userid,List<Cart>carts){
        if(carts==null||carts.size()==0){
            return ServerResponse.creatResverResponseByfaile(10,"购物中没有选中的商品");
        }
        List<OrderItem> orderItems= Lists.newArrayList();
        for(Cart cart:carts){
            OrderItem orderItem=new OrderItem();

            orderItem.setUserId(userid);
            Product product=productMapper.selectByPrimaryKey(cart.getProductId());
            if(product==null){
                return ServerResponse.creatResverResponseByfaile(1,"id为"+cart.getProductId()+"的商品不存在");
            }
                                                                                                                               //这里出现错误，因为数据库中有个商品的状态没赋值，按道理应该是可以显示为商品已下架的啊？？？？？？？？？？？？？
            if(product.getStatus()!=Const.ProductStatusENUM.PRODUCT_ONSELL.getCode()){
                return ServerResponse.creatResverResponseByfaile(1,"id为"+product.getId()+"的商品已下架");
            }
            if(product.getStock()<cart.getQuantity()){
                return ServerResponse.creatResverResponseByfaile(1,"id为"+product.getId()+"的商品库存不足");
            }
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setCurrentUnitPrice(product.getPrice());//下单时商品的jine
            orderItem.setProductId(product.getId());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setProductName(product.getName());
            orderItem.setTotalPrice(BigDecimalUtils.mil(cart.getQuantity().doubleValue(),product.getPrice().doubleValue()));

            orderItems.add(orderItem);
        }

        return ServerResponse.creatResverResponseBysucess(orderItems);
    }
}
