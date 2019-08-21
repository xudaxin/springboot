package com.neuedu.utils;


import java.math.BigDecimal;

/**
 * 价格计算工具类
 * */
public class BigDecimalUtils {
    /**
     * 加法计算
     * */
    public static BigDecimal add(double a,double b){
        BigDecimal bigDecimal=new BigDecimal(String.valueOf(a));
        BigDecimal bigDecimal1=new BigDecimal(String.valueOf(b));
        return bigDecimal.add(bigDecimal1);
    }

    public static BigDecimal add(BigDecimal a,BigDecimal b){
        BigDecimal bigDecimal=new BigDecimal(String.valueOf(a));
        BigDecimal bigDecimal1=new BigDecimal(String.valueOf(b));
        return bigDecimal1.add(bigDecimal);
    }

    /**
     * 减法运算
     * */

    public static BigDecimal sub(double a,double b){
        BigDecimal bigDecimal=new BigDecimal(String.valueOf(a));
        BigDecimal bigDecimal1=new BigDecimal(String.valueOf(b));
        return bigDecimal1.subtract(bigDecimal);
    }


    //乘法
    public static BigDecimal mil(double a,double b){
        BigDecimal bigDecimal=new BigDecimal(String.valueOf(a));
        BigDecimal bigDecimal1=new BigDecimal(String.valueOf(b));
        return bigDecimal1.multiply(bigDecimal);
    }
    public static BigDecimal mil(double a,BigDecimal b){
        BigDecimal bigDecimal=new BigDecimal(String.valueOf(a));
        BigDecimal bigDecimal1=new BigDecimal(String.valueOf(b));
        return bigDecimal1.multiply(bigDecimal);
    }

    //除法  保留两位小数 四舍五入
    public static BigDecimal div(double a,double b){
        BigDecimal bigDecimal=new BigDecimal(String.valueOf(a));
        BigDecimal bigDecimal1=new BigDecimal(String.valueOf(b));
        return bigDecimal1.divide(bigDecimal,2,BigDecimal.ROUND_HALF_UP);
    }

}
