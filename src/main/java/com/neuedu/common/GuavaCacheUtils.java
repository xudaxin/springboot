package com.neuedu.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


public class GuavaCacheUtils {

    private static final LoadingCache<String,String> loadingCache= CacheBuilder
            .newBuilder()
            .initialCapacity(1000)//初始缓存项
            .maximumSize(10000)//缓存项最大值
            .expireAfterAccess(10, TimeUnit.MINUTES)//缓存项在给定时间内没有被读/写访问，则回收
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(String s) throws Exception {
                    return "null";
                }
            });//如果不存在该key，给他的value赋值为null


    public static void put(String key,String value){
        loadingCache.put(key,value);
    }

    public static String get(String key){
        try {
            String result= loadingCache.get(key);
            if(!result.equals("null")){
                return result;
            }
            return "null";
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return "null";
    }


}
