package com.neuedu.utils;


//json与java对象转换


import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class JsonUtils {

    private static ObjectMapper objectMapper=new ObjectMapper();


    static {
        //对象中所有的字段序列化
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.ALWAYS);
        //取消默认的timestamp格式
        objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS,false);
        //忽略空bean转json错误
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS,false);
        //设置日期格式
        objectMapper.setDateFormat(new SimpleDateFormat(DateUtils.STANDARD_FORMAT));

        //忽略在json字符串中存在，但是在java中不存在的属性，防止出错
        objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    }

    /**
     * 将对象转成字符串
     * */
    public  static <T> String obj2String(T obj){
        if(obj==null){
            return null;
        }
        try {
            return obj instanceof String ? (String)obj:objectMapper.writeValueAsString(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //格式化的方法
    public  static <T> String obj2StringPretty(T obj){
        if(obj==null){
            return null;
        }
        try {
            return obj instanceof String ? (String)obj:objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 将字符串转对象
     * */

    public static <T> T string2Obj(String str,Class<T> tClass){
        if(StringUtils.isEmpty(str)||tClass==null){
            return null;
        }

        try {
            return tClass.equals(String.class)?(T)str:objectMapper.readValue(str,tClass);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将json数组转集合
     * */
    public static <T> T string2Obj(String str, TypeReference<T> typeReference){
        if(StringUtils.isEmpty(str)||typeReference==null){
            return null;
        }
        try {
            return typeReference.getType().equals(String.class)?(T)str:objectMapper.readValue(str,typeReference);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
/**
 * 当集合中有多种类型时？？？？？？？？？？
 * */
    public static <T> T string2Obj(String str,Class<?> collectionClass,Class<?>... elements){
        JavaType javaType=objectMapper.getTypeFactory().constructParametricType(collectionClass,elements);
        try {
            return objectMapper.readValue(str,javaType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
