package com.neuedu.service.impl;

import com.neuedu.common.GuavaCacheUtils;
import com.neuedu.common.ServerResponse;
import com.neuedu.dao.UserInfoMapper;
import com.neuedu.exception.Myexception;
import com.neuedu.pojo.UserInfo;
import com.neuedu.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    UserInfoMapper userInfoMapper;

    @Override
    public ServerResponse login(UserInfo userInfo) {
        //参数的非空校验
        if(userInfo==null){
            return ServerResponse.creatResverResponseByfaile(1,"参数不能为空");
        }
        if(userInfo.getUsername()==null||userInfo.getUsername().equals("")){
            return ServerResponse.creatResverResponseByfaile(2,"用户名不能为空！");
        }
        if(userInfo.getPassword()==null||userInfo.getPassword().equals("")){
            return ServerResponse.creatResverResponseByfaile(3,"密码不能为空！");
        }
        //判断用户名是否存在
        int result=userInfoMapper.exsitsUsername(userInfo.getUsername());
        if(result==0){
            return ServerResponse.creatResverResponseByfaile(4,"用户名不存在");
        }
        //根据用户名和密码登录
       UserInfo userInfo1=userInfoMapper.findByUsernameAndPassword(userInfo);
        if(userInfo1==null){
            return ServerResponse.creatResverResponseByfaile(5,"密码错误");
        }
        return ServerResponse.creatResverResponseBysucess(userInfo1);
    }

    @Override
    public ServerResponse registertUser(UserInfo userInfo) {
        //参数非空判断

        //step1.用户已存在
        int result1=userInfoMapper.exsitsUsername(userInfo.getUsername());
        if(result1!=0){
            return ServerResponse.creatResverResponseByfaile(1,"用户名已存在");
        }
        //step2.邮箱已注册
        int result2=userInfoMapper.exsitsEmail(userInfo.getEmail());
        if(result2!=0){
            return ServerResponse.creatResverResponseByfaile(2,"邮箱已注册");
        }
        //step3.注册信息不能为空
        if(userInfo==null){
            return ServerResponse.creatResverResponseByfaile(100,"注册信息不能为空");
        }

        //进行数据的录入
        int result3=userInfoMapper.insert(userInfo);
        return ServerResponse.creatResverResponseBysucess("用户注册成功");
    }

    @Override
    public ServerResponse CheckUsernameValid(String type,String value) {
        if(type.equals("username")){
            int result=userInfoMapper.exsitsUsername(value);
            if(result==1){
                return ServerResponse.creatResverResponseByfaile(1,"用户名已存在");
            }
        }
        if(type.equals("email")){
            int result=userInfoMapper.exsitsEmail(value);
            if(result!=0){
                return ServerResponse.creatResverResponseByfaile(2,"邮箱已注册");
            }
        }
        return ServerResponse.creatResverResponseBysucess("校验成功");
    }

    @Override
    public ServerResponse findquestionByusername(String username) {
        if(username==null||username.equals("")){
            return ServerResponse.creatResverResponseByfaile(100,"用户名不能为空");
        }
        int result1=userInfoMapper.exsitsUsername(username);
        if(result1==0){
            return ServerResponse.creatResverResponseByfaile(101,"用户名不存在");
        }
        int result2=userInfoMapper.findexsitsquestionByusername(username);
        if(result2==0){
            return ServerResponse.creatResverResponseByfaile(1,"该用户未设置找回密码问题");
        }
        String question=userInfoMapper.findquestionbyusername(username);
        return ServerResponse.creatResverResponseBysucess(question);
    }

    @Override
    public ServerResponse findanswerTrueOrFalse(String username, String question, String answer) {
        if(username.equals("")||username==null){
            return ServerResponse.creatResverResponseByfaile(100,"用户名不能为空");
        }
        if(question.equals("")||question==null){
            return ServerResponse.creatResverResponseByfaile(100,"问题不能为空");
        }
        if(answer.equals("")||answer==null){
            return ServerResponse.creatResverResponseByfaile(100,"答案不能为空");
        }
        String result=userInfoMapper.exsitsAnswerByusernameAndquestion(username,question);
        if(result.equals(answer)){

            String uuid=UUID.randomUUID().toString();
            GuavaCacheUtils.put(username,uuid);
            return ServerResponse.creatResverResponseBysucess(null,"生成的验证码为："+uuid);
        }
        return ServerResponse.creatResverResponseByfaile(1,"问题答案错误");
    }

    @Override
    public ServerResponse outUpdatepasswordByusername(String username, String newpassword, String uuid) {
        String result=GuavaCacheUtils.get(username);
        if(result.equals("null")){
            return ServerResponse.creatResverResponseByfaile(104,"非法的token");
        }
        if(!result.equals(uuid)){
            return ServerResponse.creatResverResponseByfaile(105,"非法的token");
        }
        if(username==null||username.equals("")){
            return ServerResponse.creatResverResponseByfaile(100,"用户名不能为空");
        }
        if(newpassword==null||newpassword.equals("")){
            return ServerResponse.creatResverResponseByfaile(100,"密码不能为空");
        }
        int resultint=userInfoMapper.outUpdatePasswordByUsername(username,newpassword);
        return ServerResponse.creatResverResponseBysucess("修改密码成功");
    }

    @Override
    public ServerResponse inUpdatepasswordByoldpassword(HttpSession httpSession,String oldpassword, String newpassword) {
        UserInfo userInfo=(UserInfo) httpSession.getAttribute("user");
        if(userInfo==null){
            return ServerResponse.creatResverResponseByfaile(1,"用户未登录，无法获取当前用户信息");
        }
        if(newpassword==null||newpassword.equals("")){
            return ServerResponse.creatResverResponseByfaile(100,"密码不能为空");
        }
        if(oldpassword==null||oldpassword.equals("")){
            return ServerResponse.creatResverResponseByfaile(100,"密码不能为空");
        }
        if(!oldpassword.equals(userInfo.getPassword())){  //字符串进行比较会不会出现问题？
            return ServerResponse.creatResverResponseByfaile(1,"旧密码输入错误");
        }
        int result=userInfoMapper.inUpdatePasswordByOldpassword(userInfo.getUsername(),newpassword);
        //修改之后让session失效
        httpSession.invalidate();//??????????修改得没毛病

        return ServerResponse.creatResverResponseBysucess("修改密码成功");
    }

    @Override
    public ServerResponse inupdateMessagebyUsername(HttpSession httpSession, String email, String phone, String question, String answer) {
        UserInfo userInfo=(UserInfo) httpSession.getAttribute("user");
        if(userInfo==null){
            return ServerResponse.creatResverResponseByfaile(1,"用户未登录");
        }
        int result=userInfoMapper.InUpdateUserMessageByUsername(email,phone,question,answer,userInfo.getUsername());
        //可以在这对session中的用户进行更新
        return ServerResponse.creatResverResponseBysucess("更新个人信息成功");
    }

    @Override
    public ServerResponse findcurrentusermessageByuser(HttpSession httpSession) {
        UserInfo userInfo=(UserInfo) httpSession.getAttribute("user");
        if(userInfo==null){
            return ServerResponse.creatResverResponseByfaile(10,"用户未登录，无法获取当前用户信息,status=10强制退出");
        }
        UserInfo userInfo1=userInfoMapper.findByUsernameAndPassword(userInfo);
        return ServerResponse.creatResverResponseBysucess(userInfo1);
    }


}
