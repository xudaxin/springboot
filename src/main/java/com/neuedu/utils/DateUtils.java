package com.neuedu.utils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


import java.util.Date;

//时间转换工具类
public class DateUtils {

    public static final String STANDARD_FORMAT="yyy-MM-dd HH:mm:ss";

    //时间转字符串
    public static String dateToStr(Date date,String formate){   //需要引入一个依赖 joda-time,自定义时间的格式formate
        DateTime dateTime=new DateTime(date);
        return dateTime.toString(formate);
    }

    public static String dateToStr(Date date){   //需要引入一个依赖 joda-time
        DateTime dateTime=new DateTime(date);
        return dateTime.toString(STANDARD_FORMAT);
    }


    //将字符串转换成Date
    public static Date strToDate(String str){
        DateTimeFormatter dateTimeFormatter=DateTimeFormat.forPattern(STANDARD_FORMAT);
        DateTime dateTime=dateTimeFormatter.parseDateTime(str);
        return dateTime.toDate();
    }

    public static Date strToDate(String str,String formate){
        DateTimeFormatter dateTimeFormatter=DateTimeFormat.forPattern(formate);
        DateTime dateTime=dateTimeFormatter.parseDateTime(str);
        return dateTime.toDate();
    }

}
