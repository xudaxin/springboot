package com.neuedu.service;

import com.neuedu.common.ServerResponse;
import com.neuedu.exception.Myexception;
import com.neuedu.pojo.Category;

import javax.servlet.http.HttpSession;
import java.util.List;

public interface ICategoryService {


    public  List<Integer> searchsonidsbypid(List<Integer> lists,int categoryid);

    public List<Integer> digui(List<Integer> lists,int category);

    //添加类别
    public int insertcate(Category category) ;

    //删除
    public int deletecate(int categoryid) ;

    //修改
    public int updatecate(Category category) ;

    //查询
    public List<Category> searchallcate() ; //最后实现分页查询

    //通过id查询类别信息
    public Category findBycateid(int careid);


    public ServerResponse findExistsCategoryId(HttpSession httpSession,int category);

    public ServerResponse findAllSonIdByCategoryid(int category);

}
