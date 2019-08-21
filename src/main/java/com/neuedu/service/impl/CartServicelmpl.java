package com.neuedu.service.impl;

import com.neuedu.common.Const;
import com.neuedu.common.ServerResponse;
import com.neuedu.dao.CartMapper;
import com.neuedu.dao.ProductMapper;
import com.neuedu.dao.UserInfoMapper;
import com.neuedu.pojo.Cart;
import com.neuedu.pojo.Product;
import com.neuedu.pojo.UserInfo;
import com.neuedu.service.ICartService;
import com.neuedu.service.ICategoryService;
import com.neuedu.utils.BigDecimalUtils;
import com.neuedu.vo.CartProductVo;
import com.neuedu.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServicelmpl implements ICartService{
    @Autowired
    CartMapper cartMapper;

    @Autowired
    UserInfoMapper userInfoMapper;

    @Autowired
    ProductMapper productMapper;

    @Override
    public ServerResponse findCartListAll(String username) {
        //根据用户名查询userid,然后查询购物车信息
        UserInfo userInfo=userInfoMapper.findUserByusername(username);
        int userid=userInfo.getId();
        //查询该用户的购物车信息
        List<Cart> cartList=cartMapper.findUserAllCartByUserId(userid);
        if(cartList==null||cartList.size()==0){
            return ServerResponse.creatResverResponseByfaile(1,"还没有选中任何商品哦~");
        }
        //转换为cartproductvo
        List<CartProductVo>cartProductVoList=new ArrayList<>();
        for(Cart cart:cartList){
            cartProductVoList.add(cartassemtocartvo(cart));
        }
        //转换成cartvo
        CartVo ca=new CartVo();
        ca=cartproductvoassemtocartvo(cartProductVoList,cartList.size());

        return ServerResponse.creatResverResponseBysucess(ca);
    }

    @Override
    public ServerResponse deteleproByProId(Integer productId,String username) {

        //参数非空判断
        if(productId==null){
            return ServerResponse.creatResverResponseByfaile(9,"参数不能为空");
        }

        //通过productid和session中的username确定购物车中的cart,需要得到userid
        UserInfo userInfo=userInfoMapper.findUserByusername(username);
        int userid=userInfo.getId();

        Cart cart=cartMapper.fingcartbyproidAndusername(productId,userid);
        if(cart==null||cart.getId()==null){
            return ServerResponse.creatResverResponseByfaile(3,"商品不存在");
        }
        //通过cartid删除商品
        cartMapper.deleteByPrimaryKey(cart.getId());

//        //显示在界面上，首先进行cart转换为cartproductvo
//        List<CartProductVo>cartProductVoList=new ArrayList<>();
//        cartProductVoList.add(cartassemtocartvo(cart));
//
//        //cartproductvo转换成cartvo
//        CartVo ca=new CartVo();
//        ca=cartproductvoassemtocartvo(cartProductVoList);
//        //返回ca
        CartVo ca=cartproductvoassemtocartvo(cartassemtocartvo(cart));


        return ServerResponse.creatResverResponseBysucess(ca);
    }

    @Override
    public ServerResponse searchproductcounts(String username) {
        //通过username获得userid
        UserInfo userInfo=userInfoMapper.findUserByusername(username);
        int userid=userInfo.getId();
        //查询该用户的购物车信息
        List<Cart> cartList=cartMapper.findUserAllCartByUserId(userid);
        if(cartList==null||cartList.size()==0){
            return ServerResponse.creatResverResponseByfaile(1,"还没有选中任何商品哦~");
        }
        int cartlength=cartList.size();

        return ServerResponse.creatResverResponseBysucess(cartlength);
    }

    @Override
    public ServerResponse selectcarttAll(String username) {
        //根据用户名查询userid,然后查询购物车信息
        UserInfo userInfo=userInfoMapper.findUserByusername(username);
        int userid=userInfo.getId();
        //查询该用户的购物车信息
        List<Cart> cartList=cartMapper.findUserAllCartByUserId(userid);
        if(cartList==null||cartList.size()==0){
            return ServerResponse.creatResverResponseByfaile(1,"还没有选中任何商品哦~");
        }
        //转换为cartproductvo
        List<CartProductVo>cartProductVoList=new ArrayList<>();
        for(Cart cart:cartList){
            cartProductVoList.add(cartassemtocartvo(cart));
        }
        //转换成cartvo
        CartVo ca=new CartVo();
        ca=cartproductvoassemtocartvo(cartProductVoList,cartList.size());
        ca.setIsallchecked(true);

        return ServerResponse.creatResverResponseBysucess(ca);
    }

    @Override
    public ServerResponse dropselectcarttAll(String username) {
        //根据用户名查询userid,然后查询购物车信息
        UserInfo userInfo=userInfoMapper.findUserByusername(username);
        int userid=userInfo.getId();
        //查询该用户的购物车信息
        List<Cart> cartList=cartMapper.findUserAllCartByUserId(userid);
        if(cartList==null||cartList.size()==0){
            return ServerResponse.creatResverResponseByfaile(1,"还没有选中任何商品哦~");
        }
        //转换为cartproductvo
        List<CartProductVo>cartProductVoList=new ArrayList<>();
        for(Cart cart:cartList){
            cartProductVoList.add(cartassemtocartvo(cart));
        }
        //转换成cartvo
        CartVo ca=new CartVo();
        ca=cartproductvoassemtocartvo(cartProductVoList,cartList.size());
        ca.setIsallchecked(false);
        ca.setCarttotalprice(new BigDecimal("0"));//网上说最好是这样

        return ServerResponse.creatResverResponseBysucess(ca);
    }

    @Override
    public ServerResponse selectOneProCart(Integer productId, String username) {
        //参数非空判断
        if(productId==null){
            return ServerResponse.creatResverResponseByfaile(9,"参数不能为空");
        }
        //根据用户名查询userid,然后查询购物车信息
        UserInfo userInfo=userInfoMapper.findUserByusername(username);
        int userid=userInfo.getId();
        //查询具体的某一商品
        Cart cart=cartMapper.fingcartbyproidAndusername(productId,userid);
        if(cart==null||cart.getId()==null){
            return ServerResponse.creatResverResponseByfaile(3,"商品不存在");
        }

        //封装结果
        List<CartProductVo>cartProductVoList=new ArrayList<>();
        cartProductVoList.add(cartassemtocartvo(cart));

        //cartproductvo转换成cartvo
        CartVo ca=new CartVo();
        ca=cartproductvoassemtocartvo(cartProductVoList,1);
        ca.setIsallchecked(true);

        //返回结果
        return ServerResponse.creatResverResponseBysucess(ca);
    }

    @Override
    public ServerResponse unselectOneProCart(Integer productId, String username) {
        //参数非空判断
        if(productId==null){
            return ServerResponse.creatResverResponseByfaile(9,"参数不能为空");
        }
        //根据用户名查询userid,然后查询购物车信息
        UserInfo userInfo=userInfoMapper.findUserByusername(username);
        int userid=userInfo.getId();
        //查询具体的某一商品
        Cart cart=cartMapper.fingcartbyproidAndusername(productId,userid);
        if(cart==null||cart.getId()==null){
            return ServerResponse.creatResverResponseByfaile(3,"商品不存在");
        }

        //封装结果
        List<CartProductVo>cartProductVoList=new ArrayList<>();
        cartProductVoList.add(cartassemtocartvo(cart));

        //cartproductvo转换成cartvo
        CartVo ca=new CartVo();
        ca=cartproductvoassemtocartvo(cartProductVoList,1);
        ca.setIsallchecked(false);
        ca.setCarttotalprice(new BigDecimal("0"));//暂且不处理，其实应该是总的减去本次的，总的可以放在一个session中或者......

        //返回结果
        return ServerResponse.creatResverResponseBysucess(ca);
    }

    //添加商品到购物车
    @Override
    public ServerResponse addProductToCart(Integer userId,Integer productId, Integer count) {
        //参数的非空校验
        if(productId==null||count==null){
            return ServerResponse.creatResverResponseByfaile(1,"参数不能为空");
        }

        //根据productid和userid查询购物信息（决定是添加还是更新数量）
        Cart cart=cartMapper.fingcartbyproidAndusername(productId,userId);
        if(cart==null){
            //说明之前没添加过，进行添加
            Cart cart1=new Cart();
            cart1.setUserId(userId);
            cart1.setProductId(productId);
            cart1.setQuantity(count);
            cart1.setChecked(Const.CartCheckedEnum.PRODUCT_CHECKED.getCode());//默认是已勾选
            cartMapper.insert(cart1);
        }
        //说明添加过了，只是更新数量
        else {
        Cart cart1=new Cart();
        cart1.setId(cart.getId());
        cart1.setProductId(productId);
        cart1.setUserId(userId);
        cart1.setQuantity(cart.getQuantity()+count);//增加数量   这边应该进行一个判断，后期再完善吧
        cart1.setChecked(cart.getChecked());
        cartMapper.updateByPrimaryKey(cart1);
        }

        //返回结果
        List<Cart> cartList=cartMapper.fingproductselected(userId);//查询checked==1的
        //查询该用户的购物车信息
        List<Cart> cartAllList=cartMapper.findUserAllCartByUserId(userId);
        if(cartList==null||cartList.size()==0){
            return ServerResponse.creatResverResponseByfaile(1,"还没有选中任何商品哦~");
        }
        //转换为cartproductvo
        List<CartProductVo>cartProductVoList=new ArrayList<>();
        for(Cart cartlists:cartList){
            cartProductVoList.add(cartassemtocartvo(cartlists));
        }
        //转换成cartvo
        CartVo ca=new CartVo();
        ca=cartproductvoassemtocartvo(cartProductVoList,cartAllList.size());

        return ServerResponse.creatResverResponseBysucess(ca);

    }

    /**
     * 更新购物车某个商品的数量
     * */
    @Override
    public ServerResponse updatecartProductCount(Integer userId,Integer productId, Integer count) {
        //参数非空判断
        if(productId==null||count==null){
            return ServerResponse.creatResverResponseByfaile(9,"参数不能为空");
        }
        //查询购物车中的商品
        Cart cart=cartMapper.fingcartbyproidAndusername(productId,userId);
        if(cart!=null){
            //更新数量
            Product product=productMapper.selectByPrimaryKey(cart.getProductId());
            if(product.getStock()>=count){
                cart.setQuantity(count);
                cartMapper.updateByPrimaryKey(cart);
            }else{
                return ServerResponse.creatResverResponseByfaile(2,"超库存，更新数据失败");
            }
        }

        //返回结果
        List<Cart> cartList=cartMapper.fingproductselected(userId);//查询checked==1的
        //查询该用户的购物车信息
        List<Cart> cartAllList=cartMapper.findUserAllCartByUserId(userId);
        if(cartList==null||cartList.size()==0){
            return ServerResponse.creatResverResponseByfaile(1,"还没有选中任何商品哦~");
        }
        //转换为cartproductvo
        List<CartProductVo>cartProductVoList=new ArrayList<>();
        for(Cart cartlists:cartList){
            cartProductVoList.add(cartassemtocartvo(cartlists));
        }
        //转换成cartvo
        CartVo ca=new CartVo();
        ca=cartproductvoassemtocartvo(cartProductVoList,cartAllList.size());

        return ServerResponse.creatResverResponseBysucess(ca);
    }

    private CartProductVo cartassemtocartvo(Cart cart){
        CartProductVo cartProductVo=new CartProductVo();
        cartProductVo.setId(cart.getId());
        cartProductVo.setUserId(cart.getUserId());
        cartProductVo.setProductId(cart.getProductId());
        cartProductVo.setQuantity(cart.getQuantity());//
        cartProductVo.setProductChecked(cart.getChecked());

        //关联到product 通过productid查找到product
        Product product=productMapper.selectByPrimaryKey(cart.getProductId());
        cartProductVo.setProductName(product.getName());
        cartProductVo.setProductSubtitle(product.getSubtitle());
        cartProductVo.setProductMainImage(product.getMainImage());
        cartProductVo.setProductPrice(product.getPrice());
        cartProductVo.setProductStatus(product.getStatus());
        cartProductVo.setProductStock(product.getStock());

        int limitProductCount=0;
        if(product.getStock()>=cart.getQuantity()){
            limitProductCount=cart.getQuantity();
            cartProductVo.setLimitQuantity("LIMIT_NUM_SUCCESS");
        }else {//库存不足
            limitProductCount=product.getStock();
            //更新购物车中商品的数量
            Cart cart1=new Cart();
            cart1.setId(cart.getId());
            cart1.setQuantity(limitProductCount);
            cart1.setChecked(cart.getChecked());
            cart1.setUserId(cart.getUserId());
            cart1.setProductId(cart.getProductId());
            cartMapper.updateByPrimaryKey(cart1);
            cartProductVo.setLimitQuantity("LIMIT_NUM_FAIL");
        }
        cartProductVo.setQuantity(limitProductCount);
        cartProductVo.setProductTotalPrice(BigDecimalUtils.mil(cartProductVo.getQuantity(),cartProductVo.getProductPrice()));
        return cartProductVo;
    }

    private CartVo cartproductvoassemtocartvo(List<CartProductVo> cartProductVoList,int records){
        CartVo cartVo=new CartVo();
        cartVo.setCartProductVos(cartProductVoList);
        BigDecimal totalprice=new BigDecimal(0);
        for(CartProductVo cartProductVo:cartProductVoList){
            cartProductVo.getProductTotalPrice();
            totalprice=BigDecimalUtils.add(totalprice,cartProductVo.getProductTotalPrice());
        }
        cartVo.setCarttotalprice(totalprice);

//        for(CartProductVo cartProductVo:cartProductVoList){//表示没被选中
//            while (cartProductVo.getProductChecked()==0){
//                cartVo.setIsallchecked(false);
//            }
//        }
//        cartVo.setIsallchecked(true);

        if(cartProductVoList.size()==records){
            cartVo.setIsallchecked(true);
        }else {
            cartVo.setIsallchecked(false);
        }

        return cartVo;
    }

    private CartVo cartproductvoassemtocartvo(CartProductVo cartProductVo){
        CartVo cartVo=new CartVo();
        List<CartProductVo>list=new ArrayList<>();
        list.add(cartProductVo);
        cartVo.setCartProductVos(list);

        cartVo.setCarttotalprice(cartProductVo.getProductTotalPrice());

        if(cartProductVo.getProductChecked()==0){
            cartVo.setIsallchecked(false);
        }
        else {
            cartVo.setIsallchecked(true);
        }

        return cartVo;
    }
}
