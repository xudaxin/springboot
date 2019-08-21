package com.neuedu.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.neuedu.common.ServerResponse;
import com.neuedu.dao.ShippingMapper;
import com.neuedu.pojo.Shipping;
import com.neuedu.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

@Service
public class ShippingServicelmpl implements IShippingService {
    @Autowired
    ShippingMapper shippingMapper;

/**
 * 添加地址
 * */
    @Override
    public ServerResponse addaddress(Shipping shipping) {
        int result=shippingMapper.insert(shipping);
        if(result==1){
            return ServerResponse.creatResverResponseBysucess("添加成功");
        }else {
            return ServerResponse.creatResverResponseByfaile(1,"新建地址失败");
        }
    }

    /**
     * 删除地址
     * */
    @Override
    public ServerResponse deleteaddress(Integer shippingId) {
        int result=shippingMapper.deleteByPrimaryKey(shippingId);
        if(result==1){
            return ServerResponse.creatResverResponseBysucess("删除地址成功");
        }else {
            return ServerResponse.creatResverResponseByfaile(1,"删除地址失败");
        }
    }

    /**
     * 更新地址
     * */
    @Override
    public ServerResponse updateaddress(Shipping shipping) {
        int result=shippingMapper.updateByPrimaryKey(shipping);
        if(result==1){
            return ServerResponse.creatResverResponseBysucess("更新地址成功");
        }else {
            return ServerResponse.creatResverResponseByfaile(1,"更新地址失败");
        }
    }

    @Override
    public ServerResponse findaddressByshippingId(Integer shippingId) {
        //参数非空校验
        if(shippingId==null){
            return ServerResponse.creatResverResponseByfaile(1,"参数为空");
        }
        Shipping shipping=shippingMapper.selectByPrimaryKey(shippingId);
        if(shipping==null){
            return ServerResponse.creatResverResponseByfaile(2,"地址不存在");
        }
        //从这应该进行时间的转换DateUtils   后续进行完善

        return ServerResponse.creatResverResponseBysucess(shipping);
    }

    @Override
    public ServerResponse selectAlladdresslimit(Integer pageNum, Integer pageSize) {

        //分页必须在进行查询之前！！！
        Page page= PageHelper.startPage(pageNum,pageSize);//进行分页 原理是springaop

        List<Shipping>shippingList= shippingMapper.selectAll();

        //然后你接着分页
        PageInfo pageInfo=new PageInfo(page);

        return ServerResponse.creatResverResponseBysucess(pageInfo);

    }

}
