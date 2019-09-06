package com.neuedu.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.neuedu.common.ServerResponse;
import com.neuedu.dao.CategoryMapper;
import com.neuedu.dao.ProductMapper;
import com.neuedu.pojo.Category;
import com.neuedu.pojo.Product;
import com.neuedu.service.IProductService;
import com.neuedu.utils.DateUtils;
import com.neuedu.utils.PropertiesUtils;
import com.neuedu.vo.ProductDetailVo;
import com.neuedu.vo.ProductListVo;
import org.omg.PortableInterceptor.INACTIVE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements IProductService {
    @Autowired
    private ProductMapper productMapper;

    @Autowired
    CategoryMapper categoryMapper;

    @Autowired
    CategoryServiceImpl categoryService;

    @Override
    public int insertProduct(Product product) {
        int result=productMapper.insert(product);
        return result;
    }

    @Override
    public int deleteProductByID(int productid) {
        int result=productMapper.deleteByPrimaryKey(productid);
        return result;
    }

    @Override
    public int updateProduct(Product product) {
        int result=productMapper.updateByPrimaryKey(product);
        return result;
    }

    @Override
    public List<Product> findAllProduct() {
        List<Product>products=productMapper.selectAll();
        return products;
    }

    @Override
    public Product findOneById(int productid) {
        Product product=productMapper.selectByPrimaryKey(productid);
        return product;
    }

    @Override
    public List<Category> findAllCategory() {
        List<Category> categorymessage=productMapper.findAllCategoryMessage();
        return categorymessage;
    }

    @Override
    public int findtotalRecords() {
        int result=productMapper.findRecords();
        return result;
    }

    @Override
    public List<Product> findProductlimit(int pageno, int pagesize) {
        int offset=(pageno-1)*pagesize;
        List<Product> products=productMapper.findProlimit(offset,pagesize);
        return products;
    }

    @Override
    public ServerResponse addorupdateproduct(Product product) {
        //参数非空校验
        if(product==null){
            return ServerResponse.creatResverResponseByfaile(1,"参数为空");
        }

        //设置商品的主图
        String subimage=product.getSubImages();
        if(subimage!=null&!subimage.equals("")){
        String [] imagepath=subimage.split(",");
        if(imagepath.length>0){
            product.setMainImage(imagepath[0]);
        }
        }

        //商品上传或更新
        if(product.getId()!=null){
            //更新
            int result=productMapper.updateByPrimaryKey(product);
            if(result==1){
                return ServerResponse.creatResverResponseBysucess("更新成功");
            }
            else {
                return ServerResponse.creatResverResponseByfaile(1,"更新失败");
            }
        }

        else {
            //添加
            int result=productMapper.insert(product);
            if(result==1){
                return ServerResponse.creatResverResponseBysucess("添加成功");
            }
            else {
                return ServerResponse.creatResverResponseByfaile(1,"添加失败");
            }
        }

    }

    @Override
    public ServerResponse onOrdeleteProduct(int productid,int status) {

        int result=productMapper.upordeleteproduct(productid,status);
        if(result==1){
            return ServerResponse.creatResverResponseBysucess("修改产品状态成功");
        }
        else {
            return ServerResponse.creatResverResponseByfaile(1,"修改产品状态失败");
        }
    }

    @Override        //分页查询
    public ServerResponse findProLimit(Integer pageno, Integer pagesize) {//因为有默认值所以不需要参数非空判断
        int offset=(pageno-1)*pagesize;
        List<Product>products=productMapper.findProlimit(offset,pagesize);
        if(products==null){
            return ServerResponse.creatResverResponseByfaile(1,"查询失败");
        }
        return ServerResponse.creatResverResponseBysucess(products);
    }

    @Override  //在第几页查询你要查询的商品  不对
    public ServerResponse searchProduct(String productName, Integer productId, Integer pageNum, Integer pageSize) {
        if(productName!=null&&!productName.equals("")){  //到时候要控制只能输入一个数据！！！
            productName="%"+productName+"%";
        }

        //分页必须在进行查询之前！！！
        Page page=PageHelper.startPage(pageNum,pageSize);//进行分页 原理是springaop
        List<Product>products=productMapper.findproductBynameOrid(productName,productId);




//        List<Integer>list= Lists.newArrayList();
        List<ProductListVo> listVos=new ArrayList<ProductListVo>();//List<ProductListVo> lists=Lists.newArrayList()
        if(products.size()>0&&products!=null){
            for(Product pro:products){
                listVos.add(assemb(pro));
            }
        }

        //然后你接着分页
        PageInfo pageInfo=new PageInfo(page);



        return ServerResponse.creatResverResponseBysucess(pageInfo);
    }

    @Override
    public ServerResponse searchgoodsdetails(Integer productid) {
        //参数校验
        if(productid==null){
            return ServerResponse.creatResverResponseByfaile(1,"参数不能为空");
        }
        //查询product
        Product product=productMapper.selectByPrimaryKey(productid);
        if(product==null){
            return ServerResponse.creatResverResponseByfaile(1,"没有此商品");
        }

        //校验商品状态
        if(product.getStatus()!=1){
            return ServerResponse.creatResverResponseByfaile(4,"商品已下架");
        }

        //获取productDetailsVO
        ProductDetailVo productDetailVo=assembProductDetailVo(product,productMapper.findthispid(product.getCategoryId()));

        //返回结果

        return ServerResponse.creatResverResponseBysucess(productDetailVo);
    }

    @Override
    public ServerResponse searchPrdlist(Integer categoryId, String keyword, Integer pageNum, Integer pageSize, String orderBy) {
        //id和keyword不能同时为空
        if(categoryId==null&&(keyword==null||keyword.equals(""))){
            return ServerResponse.creatResverResponseByfaile(1,"参数错误");
        }
        //categeryid
        List<Integer>soncareids=new ArrayList<>();
        if(categoryId!=null){
            Category category=categoryMapper.selectByPrimaryKey(categoryId);
            System.out.println(category);
            if(category==null&&(keyword==null||keyword.equals(""))){
                //说明没有商品数据
                //分页必须在进行查询之前！！！
                Page page=PageHelper.startPage(pageNum,pageSize);//进行分页 原理是springaop
                List<ProductListVo> listVos=new ArrayList<>();

                //然后你接着分页
                PageInfo pageInfo=new PageInfo(page);

                return ServerResponse.creatResverResponseBysucess(pageInfo);
            }


            List<Integer> list=new ArrayList<>();
            soncareids=categoryService.searchsonidsbypid(list,categoryId);
            soncareids.add(categoryId);//将本次查询的categoryid也放入

        }

        //keyword
        if(keyword!=null&&!keyword.equals("")){
            keyword="%"+keyword+"%";
        }

        Page page=new Page();
        if(orderBy.equals("")){
            page=PageHelper.startPage(pageNum,pageSize);
        }else {
            String []str=orderBy.split("_");
            if(str.length>1){
                page=PageHelper.startPage(pageNum,pageSize,str[0]+" "+str[1]);
            }else {
                page=PageHelper.startPage(pageNum,pageSize);
            }
        }
        List<Product>productList=productMapper.searchproductsbyidandkeyword(soncareids,keyword);
        //listpro--->listvo
        List<ProductListVo> listVos=new ArrayList<ProductListVo>();//List<ProductListVo> lists=Lists.newArrayList()
        if(productList.size()>0&&productList!=null){
            for(Product pro:productList){
                listVos.add(assemb(pro));
            }
        }
        //然后你接着分页
        PageInfo pageInfo=new PageInfo(page);

        //返回
        return ServerResponse.creatResverResponseBysucess(pageInfo);

    }

    //查询pid==0的类别
    @Override
    public ServerResponse searchcatepid() {
        List<Category>categoryList=categoryMapper.searchcatepid();
        if(categoryList==null||categoryList.size()==0){
            return ServerResponse.creatResverResponseByfaile(1,"类别数据表为空！");
        }
        return ServerResponse.creatResverResponseBysucess(categoryList);
    }

    //查询上架商品信息
    @Override
    public ServerResponse selectonpro() {
        List<Product> productList=productMapper.selectonpro();
        if(productList==null||productList.size()==0){
            return ServerResponse.creatResverResponseByfaile(1,"无上架商品!");
        }
        return ServerResponse.creatResverResponseBysucess(productList);
    }


    public ServerResponse findproductdetailbyid(Integer productId){
        if(productId==null){
            return ServerResponse.creatResverResponseByfaile(1,"参数不能为空");
        }

        Product product=productMapper.selectByPrimaryKey(productId);
        if(product==null){
            return ServerResponse.creatResverResponseByfaile(1,"没有此商品");
        }
        int sid=product.getCategoryId();
        //通过sid去类别表查询pid
        int parentid=productMapper.findthispid(sid);
        //转换
        ProductDetailVo productDetailVo=assembProductDetailVo(product,parentid);

        return ServerResponse.creatResverResponseBysucess(productDetailVo);
    }

    private ProductDetailVo assembProductDetailVo(Product product,int parentId){
        ProductDetailVo productDetailVo=new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setParentCategoryId(parentId);
        productDetailVo.setCreateTime(DateUtils.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateUtils.dateToStr(product.getUpdateTime()));
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setName(product.getName());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setSubImages(product.getSubImages());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setStock(product.getStock());
        productDetailVo.setImageHost(PropertiesUtils.readByKey("ImageHost")); //图片的域名？读取配置文件获取
        return productDetailVo;
    }

    private ProductListVo assemb(Product product){
        ProductListVo productListVo=new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setName(product.getName());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        return productListVo;
    }


}
