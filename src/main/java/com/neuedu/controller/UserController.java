package com.neuedu.controller;

import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.UserInfo;
import com.neuedu.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    IUserService iUserService;

    //登录
    @RequestMapping("/login")
    public ServerResponse login(UserInfo userInfo, HttpSession httpSession){
        ServerResponse serverResponse=iUserService.login(userInfo);
        if(serverResponse.isSucess()){
            UserInfo userInfo1=(UserInfo) serverResponse.getData();
            httpSession.setAttribute("user",userInfo1);
        }
        return serverResponse;
    }
    //注册
    @RequestMapping("/register")
    public ServerResponse register(UserInfo userInfo){
        return iUserService.registertUser(userInfo);
    }

    //检查用户名或邮箱是否有效
    @RequestMapping("/check_valid")
    public ServerResponse checkusernamevalid(@RequestParam("str") String str,@RequestParam("type") String type){
        return iUserService.CheckUsernameValid(type,str);
    }


    //获取登录用户信息
    @RequestMapping("/get_user_info")
    public ServerResponse getUserMessage(HttpSession httpSession){
//        UserInfo userInfo=(UserInfo) httpSession.getAttribute("user");
//        if(userInfo==null){
//            return ServerResponse.creatResverResponseByfaile(1,"用户未登录，无法获取当前用户信息");
//        }
//        return ServerResponse.creatResverResponseBysucess(userInfo);

        return iUserService.findcurrentusermessageByuser(httpSession);
    }

    //忘记密码,通过用户名获得密保问题
    @RequestMapping("/forget_get_question")
    public ServerResponse forgetpassword(String username){
        return iUserService.findquestionByusername(username);
    }

    //提交问题答案，通过密保问题的答案得到修改密码的机会与验证码
    @RequestMapping("/forget_check_answer")
    public ServerResponse submitqueAndans(String username,String question,String answer){
        return iUserService.findanswerTrueOrFalse(username,question,answer);
    }

    //通过输入用户名和验证码从而修改密码                  还存在需要改进的地方map
    @RequestMapping("/forget_reset_password")
    public ServerResponse outupdatepassword(String username,String passwordNew,String forgetToken){
        return iUserService.outUpdatepasswordByusername(username,passwordNew,forgetToken);
    }


    //登录状态中修改密码，通过校验旧密码是否正确来使用新密码修改

    @RequestMapping("/reset_password")
    public ServerResponse inupdatePasswordByoldPassword(HttpSession httpSession,String passwordOld,String passwordNew){
        return iUserService.inUpdatepasswordByoldpassword(httpSession,passwordOld,passwordNew);
    }


    //登录状态更新个人信息
    @RequestMapping("/update_information")
    public ServerResponse inUpdatemessagesByusername(HttpSession httpSession,
                                                     String email,
                                                     String phone,
                                                     String question,
                                                     String answer){
        return iUserService.inupdateMessagebyUsername(httpSession,email,phone,question,answer);

        //按道理应该把session中的用户信息也修改了 后期需要优化

    }

    //获取当前登录用户的详细信息
    @RequestMapping("/get_inforamtion")
    public ServerResponse getCurrentUserMessageByUser(HttpSession httpSession){
        return iUserService.findcurrentusermessageByuser(httpSession);
    }


    //退出登录
    @RequestMapping("/logout")
    public ServerResponse exit(HttpSession session){
        session.invalidate();
        return ServerResponse.creatResverResponseBysucess("退出成功");
    }



}
