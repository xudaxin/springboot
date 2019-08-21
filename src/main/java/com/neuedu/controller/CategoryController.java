package com.neuedu.controller;

import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.Category;
import com.neuedu.service.impl.CategoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/manage/category")
public class CategoryController {
    @Autowired
    CategoryServiceImpl categoryService;


    @RequestMapping("/get_category")
    public ServerResponse findcategory(HttpSession httpSession,String categoryId){
        int categoryid=Integer.parseInt(categoryId);
        return categoryService.findExistsCategoryId(httpSession,categoryid);
    }

    @RequestMapping("/get_deep_category")
    public ServerResponse findallSonidsBypid(String categoryId){
        int id=Integer.parseInt(categoryId);
        return categoryService.findAllSonIdByCategoryid(id);
    }


}
