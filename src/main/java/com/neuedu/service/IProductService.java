package com.neuedu.service;

import com.neuedu.common.ServerResponse;
import com.neuedu.pojo.Category;
import com.neuedu.pojo.Product;

import java.util.List;

public interface IProductService {

    //增加
    public int insertProduct(Product product);

    //删除
    public int deleteProductByID(int productid);

    //修改
    public int updateProduct(Product product);

    //查找全部
    public List<Product> findAllProduct();

    //查找某一个
    public Product findOneById(int productid);

    //查询所有类别信息
    public List<Category> findAllCategory();

    //查询总记录数
    public int findtotalRecords();

    //分页查询
    public List<Product> findProductlimit(int pageno, int pagesize);


    public ServerResponse addorupdateproduct(Product product);
    public ServerResponse onOrdeleteProduct(int productid,int status);

    //分页查询接口
    ServerResponse findProLimit(Integer pageno,Integer pagesize);
    //id或名字模糊查询
    ServerResponse searchProduct(String productName,
                                 Integer productId,
                                 Integer pageNum,
                                 Integer pageSize);
    /**
     * 前台--商品详情
     */

    ServerResponse searchgoodsdetails(Integer productid);
    ServerResponse searchPrdlist(Integer categoryId,
                        String keyword,
                        Integer pageNum,
                        Integer pageSize,
                        String orderBy);



    /**
     * 查询一级类别
     * */
    ServerResponse searchcatepid();

    /**
     * 查询上架的商品
     * */
    ServerResponse selectonpro();

}
