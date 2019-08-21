package com.neuedu.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 *
 * 服务端响应对象
 *
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class ServerResponse<T> {
    private  int status;
    private String msg;
    private T data;//接口返回前端的数据


    private ServerResponse(){}
    private ServerResponse(int status){
        this.status=status;
    }
    private ServerResponse(int status,String msg){
        this.status=status;
        this.msg=msg;
    }

    private ServerResponse(int status,T data){
        this.status=status;
        this.data=data;
    }

    private ServerResponse(int status,String msg,T data){
        this.status=status;
        this.msg=msg;
        this.data=data;
    }

    @JsonIgnore
    public boolean isSucess(){   //判断接口调用是否成功
        return this.status==0;
    }

    public static<T> ServerResponse<T> creatResverResponseBysucess(){  //成功，单纯地返回状态码0
        return new ServerResponse<>(0);
    }

    public static<T> ServerResponse<T> creatResverResponseBysucess(T data){  //成功，返回状态码0，和对象信息
        return new ServerResponse<>(0,data);
    }

    public static<T> ServerResponse<T> creatResverResponseBysucess(String msg){   //成功，返回状态码0，和额外的信息
        return new ServerResponse<>(0,msg);
    }

    public static<T> ServerResponse<T> creatResverResponseBysucess(String msg,T data){   //成功，返回字符串信息和对象信息
        return new ServerResponse<>(0,msg,data);
    }

    public static<T> ServerResponse<T> creatResverResponseByfaile(int status,String msg){   //失败，返回状态码和错误信息 状态码为非0数
        return new ServerResponse<>(status,msg);
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
