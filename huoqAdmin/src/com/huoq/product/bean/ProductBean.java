package com.huoq.product.bean;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.huoq.common.util.ArrayUtils;
import com.huoq.common.util.PageUtil;
import com.huoq.bean.FullScaleCompanyMessage;
import com.huoq.orm.Product;
import com.huoq.product.dao.ProductCategoryDAO;
import com.huoq.util.MyLogger;

@Service
public class ProductBean {

    private static MyLogger    log = MyLogger.getLogger(ProductBean.class);
    @Resource
    private ProductCategoryDAO productCategoryDAO;

    /**
     * 根据主键获取标的信息
     *
     * @param borrowerInfoId 主键
     * @return
     */
    @SuppressWarnings("unchecked")
    public Product getProductById(String id) {
        String hql = "from Product p where p.id=?";
        List<Product> productList = productCategoryDAO.LoadAll(hql, new Object[] { id });
        if (null != productList && !productList.isEmpty()) {
            return productList.get(0);
        }
        return null;
    }

    /**
     * 根据sql查询产品信息
     * 
     * @author：zhuhaojie
     * @time：2018年1月11日 下午4:48:42
     * @version
     * @param sql 要执行的sql
     * @param params 请求参数
     * @return 获取到的对象集合
     */
    public List<Product> getProductBySql(String sql, Object[] params) {
        @SuppressWarnings("unchecked")
        List<Product> result = productCategoryDAO.LoadAllSql(sql, params, Product.class);
        return result;
    }

    /**
     * Product表分页查询
     * 
     * @author：zhuhaojie
     * @time：2018年1月15日 上午11:33:44
     * @version
     * @return
     */
    public PageUtil<String> findProductsPageUtil(PageUtil<FullScaleCompanyMessage> pageUtil, String sql, Object[] params) {

        StringBuffer sqlCount = new StringBuffer();
        sqlCount.append("SELECT COUNT(*) FROM ( ");
        sqlCount.append(sql);
        sqlCount.append(" )t");
        PageUtil page = productCategoryDAO.getBySqlAndSqlCount(pageUtil, sql.toString(), sqlCount.toString(), params);

        PageUtil<String> result = new PageUtil<String>();
        if (page != null) {
            result.setPageSize(page.getPageSize());
            result.setCount(page.getCount());
            result.setCurrentPage(page.getCurrentPage());
            result.setLastPage(page.isLastPage());
            // result.setListCount(listCount);
            List list = page.getList();

            if (list != null && list.size() > 0) {
                // 存储公司id
                Set<String> productId = new HashSet<String>();
                for (Object obj : list) {
                    if (obj instanceof String) {
                        String id = (String) obj;// id
                        productId.add(id);
                    }
                    String[] productIds = productId.toArray(new String[list.size()]);
                    List<String> listOne = ArrayUtils.converArrayToList(productIds);
                    result.setList(listOne);
                    return result;
                }
            }
        }
        return null;
    }

    // public PageUtil<Product> findProductsPageUtil(PageUtil<Product> pageUtil,Product product, String finishTime,
    // String insertTime, String username, String productStatus, String DESCName,String realName) throws Exception{
    // StringBuffer sql=new StringBuffer();
    // List<Object> params = new ArrayList<Object>();
    // sql.append(" SELECT
    // p.product_status,p.title,p.invest_type,p.annual_earnings,jiang_li_earnings,financing_amount,finish_time,insert_time,");
    // sql.append(" (SELECT in_money*0.01 FROM virtual_ins WHERE product_id = p.id GROUP BY product_id )sumVir,");
    // sql.append(" (SELECT SUM(copies) FROM investors WHERE product_id = p.id AND investor_status IN
    // ('1','2','3'))sumInv,product_type,id,iStatus,p.real_name ");
    // sql.append(" FROM product p ");
    // sql.append(" WHERE 1=1");
    // if (!QwyUtil.isNullAndEmpty(product)) {
    // //项目总额
    // if (!QwyUtil.isNullAndEmpty(product.getFinancingAmount())) {
    // sql.append(" AND financing_amount = "+product.getFinancingAmount()*100);
    // }
    //
    // //产品名称
    // if (!QwyUtil.isNullAndEmpty(product.getTitle())) {
    // sql.append(" AND title LIKE '%"+product.getTitle()+"%'");
    // }
    // //借款企业名称
    // if (!QwyUtil.isNullAndEmpty(realName)) {
    // sql.append(" AND p.real_name LIKE '%"+realName+"%'");
    // }
    // //年化收益
    // if (!QwyUtil.isNullAndEmpty(product.getAnnualEarnings())) {
    // sql.append(" AND annual_earnings ="+product.getAnnualEarnings());
    // }
    // //项目类型
    // if (!QwyUtil.isNullAndEmpty(product.getInvestType())) {
    // sql.append(" AND invest_type = '"+product.getInvestType()+"'");
    // }
    // //发布时间
    // if (!QwyUtil.isNullAndEmpty(insertTime)) {
    // String [] times=QwyUtil.splitTime(insertTime);
    // if (times.length > 1) {
    // sql.append(" AND insert_time >= ? ");
    // params.add( QwyUtil.fmMMddyyyy.parse(times[0]));
    // sql.append(" AND insert_time <= ? ");
    // params.add(QwyUtil.fmMMddyyyyHHmmss.parse(times[1]+" 23:59:59"));
    // } else {
    // sql.append(" AND insert_time >= ? ");
    // params.add(QwyUtil.fmMMddyyyyHHmmss.parse(times[0] + " 00:00:00"));
    // sql.append(" AND insert_time <= ? ");
    // params.add(QwyUtil.fmMMddyyyyHHmmss.parse(times[0] + " 23:59:59"));
    // }
    // }
    // //到期时间
    // if (!QwyUtil.isNullAndEmpty(finishTime)) {
    // String [] times=QwyUtil.splitTime(finishTime);
    // if (times.length > 1) {
    // sql.append(" AND finish_time >= ? ");
    // params.add( QwyUtil.fmMMddyyyy.parse(times[0]));
    // sql.append(" AND finish_time <= ? ");
    // params.add(QwyUtil.fmMMddyyyyHHmmss.parse(times[1]+" 23:59:59"));
    // } else {
    // sql.append(" AND finish_time >= ? ");
    // params.add(QwyUtil.fmMMddyyyyHHmmss.parse(times[0] + " 00:00:00"));
    // sql.append(" AND finish_time <= ? ");
    // params.add(QwyUtil.fmMMddyyyyHHmmss.parse(times[0] + " 23:59:59"));
    // }
    // }
    // //产品状态
    // if (!QwyUtil.isNullAndEmpty(productStatus)) {
    // sql.append(" AND product_status = '"+productStatus+"'");
    // }
    // }
    //
    // if(!QwyUtil.isNullAndEmpty(DESCName)){
    // sql.append(" ORDER By p."+DESCName+" DESC ");
    // }else{
    // sql.append(" ORDER By p.insert_time DESC ");
    // }
    //
    // StringBuffer sqlCount = new StringBuffer();
    // sqlCount.append("SELECT COUNT(*) FROM ( ");
    // sqlCount.append(sql);
    // sqlCount.append(" )t");
    //
    // return productCategoryDAO.getBySqlAndSqlCount(pageUtil, sql.toString(), sqlCount.toString(), params.toArray());
    //
    // }

    /**
     * 更新标的状态
     * 
     * @param id
     */
    public void updateProduct(String id) {
        Product product = this.getProductById(id);
        product.setiStatus(9);
        this.productCategoryDAO.update(product);
    }

    public List queryBySql(String sql, Object[] obj, String inName, List list) {
        return productCategoryDAO.LoadAllSql(sql, obj, list, inName);
    }
}
