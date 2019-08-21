package com.neuedu.service.impl;

import com.neuedu.common.ServerResponse;
import com.neuedu.dao.CategoryMapper;
import com.neuedu.exception.Myexception;
import com.neuedu.pojo.Category;
import com.neuedu.pojo.UserInfo;
import com.neuedu.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements ICategoryService {
    @Autowired
    CategoryMapper categoryMapper;



    @Override
    public List<Integer> searchsonidsbypid(List<Integer> lists, int categoryid) {
        List<Category> categories=categoryMapper.findCategorysByPid(categoryid);
        for(Category category:categories){
            if(category!=null){
                lists.add(category.getId());
                searchsonidsbypid(lists,category.getId());
            }
        }
        return lists;
    }



    @Override
    public List<Integer> digui(List<Integer> lists, int categoryid) {  //这个方法的使用前提是知道子节点
        for(Integer newid:lists){

        if(categoryMapper.findpidbysid(newid)==categoryid){
            digui(lists,categoryMapper.findpidbysid(categoryid));
            lists.add(newid);
        }
        }
        return lists;
    }


    @Override
    public int insertcate(Category category)  {
        int result=categoryMapper.insert(category);
        return result;
    }

    @Override
    public int deletecate(int categoryid)  {
        int result=categoryMapper.deleteByPrimaryKey(categoryid);
        return result;
    }

    @Override
    public int updatecate(Category category)  {
        int result=categoryMapper.updateByPrimaryKey(category);
        return result;
    }

    @Override
    public List<Category> searchallcate()  {
        List<Category> categories=categoryMapper.selectAll();
        return categories;
    }

    @Override
    public Category findBycateid(int careid) {
        Category category=categoryMapper.selectByPrimaryKey(careid);
        return category;
    }



    @Override
    public ServerResponse findExistsCategoryId(HttpSession httpSession,int categoryid) {
        UserInfo userInfo=(UserInfo) httpSession.getAttribute("user");
        if(userInfo==null){
            return ServerResponse.creatResverResponseByfaile(10,"用户未登录,请登录");
        }
        int result=categoryMapper.selectexistsCategoryid(categoryid);
        if(result==0){
            return ServerResponse.creatResverResponseByfaile(1,"未找到该品类");
        }

        //递归查询？？？？？？？？？/
        List<Category>categories=categoryMapper.findCategoryByIdAndPid(categoryid);
        System.out.println(categories);
        return ServerResponse.creatResverResponseBysucess(categories);

    }

    @Override
    public ServerResponse findAllSonIdByCategoryid(int category) {

        //查询总的记录数
        int records=categoryMapper.findcategoryrecords();
        List<Integer> list=new ArrayList<>();
        List<Integer>resulelist=searchsonidsbypid(list,category);
        if(resulelist.isEmpty()){
            return ServerResponse.creatResverResponseByfaile(1,"无权限");
        }
        return ServerResponse.creatResverResponseBysucess(resulelist);

    }
}
