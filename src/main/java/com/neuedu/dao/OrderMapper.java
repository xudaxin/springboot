package com.neuedu.dao;

import com.neuedu.pojo.Cart;
import com.neuedu.pojo.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table neuedu_order
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table neuedu_order
     *
     * @mbggenerated
     */
    int insert(Order record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table neuedu_order
     *
     * @mbggenerated
     */
    Order selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table neuedu_order
     *
     * @mbggenerated
     */
    List<Order> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table neuedu_order
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(Order record);

    //根据userid和orderno查询订单信息
    Order findByuserIdAndOrderNo(@Param("userId") Integer userId, @Param("orderNo")Long orderNo);

    //根据orderno查询订单信息
    Order findByAndOrderNo(Long orderNo);


    //查询需要关闭的订单
    List<Order> findcloseOrder(String orderclosetime);

    //查询未支付订单
    List<Order> findwaitparorder(Integer userId);

}