package com.neuedu.service;


import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.UserInfo;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface IUserService {
    public ServerResponse login(UserInfo userInfo) ;
    public ServerResponse registertUser(UserInfo userInfo);
    public ServerResponse CheckUsernameValid(String type,String value);
    public ServerResponse findquestionByusername(String username);
    public ServerResponse findanswerTrueOrFalse(String username,String question,String answer);
    public ServerResponse outUpdatepasswordByusername(String username,String newpassword,String uuid);
    public ServerResponse inUpdatepasswordByoldpassword(HttpSession httpSession,String oldpassword, String newpassword);
    public ServerResponse inupdateMessagebyUsername(HttpSession httpSession,String email, String phone, String question, String answer);
    public ServerResponse findcurrentusermessageByuser(HttpSession httpSession);








}
