package com.huoq.assetSide.action;


import com.huoq.assetSide.bean.BorrowerInfoBean;
import com.huoq.assetSide.bean.ProjectBorrowerInfoBean;
import com.huoq.common.action.BaseAction;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.*;
import com.huoq.thread.bean.SendBookingProductBean;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;


import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 后台发布信用贷标Action层<br>
 * 管理员进行产品的发布,对页面的值进行判断;
 *
 * @author 李瑞丽
 * @Date: 2017/12/27 15:50
 */
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/Admin")
// 发布产品页面
@Results({
        @Result(name = "err", value = "/Product/Admin/err.jsp"),
        @Result(name = "success", value = "/Product/Admin/success.jsp"),
        @Result(name = "creditYYProduct", value = "/Product/Admin/creditProduct/credit.jsp"),
        @Result(name = "borrower", value = "/Product/Admin/creditProduct/bowwerInfo.jsp"),
        @Result(name = "borrowerUser", value = "/Product/Admin/creditProduct/borrow.jsp")

})
public class CreditProductAction extends BaseAction {

    @Resource
    private BorrowerInfoBean borrowerInfoBean;
    @Resource
    private SendBookingProductBean sendBookingProductBean;
    @Resource
    private ProjectBorrowerInfoBean projectBorrowerInfoBean;

    private Product product;

    public String flag;

    private String endTime;
    private String finishTime;
    private String[] userIds;
    private String termLoan;

    private Integer currentPage = 1;
    private Integer pageSize = 50;

    /**
     * 加载发布产品的页面；
     *
     * @return
     */
    public String sendCredit() {
        String json = "";
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                //管理员没有登录;
                return null;
            }
            //获取已发布的产品总数;(包含新手产品,包含预约中的产品)
            int productCount = borrowerInfoBean.getProductCount(new String[]{"0", "1"});
            getRequest().setAttribute("productCount", productCount);
            int count = borrowerInfoBean.getBorrowerInfoAmount();
            getRequest().setAttribute("count", count);
            Double amount = borrowerInfoBean.getBorrowerInfoCount();
            getRequest().setAttribute("amount", amount);
            if (flag.equals("yy")) {
                List<String> list = sendBookingProductBean.getBookingKeyword();
                String yyKeyword = QwyUtil.isNullAndEmpty(list) ? "没有预约关键字" : QwyUtil.packString(list.toArray()).replaceAll("'", "");
                List<Product> listZero = borrowerInfoBean.getProductByStatus("0");
                List<Product> listYuYue = borrowerInfoBean.getProductByStatus("-3");
                getRequest().setAttribute("listZero", listZero);
                getRequest().setAttribute("listYuYue", listYuYue);
                getRequest().setAttribute("yyKeyword", yyKeyword);
                return "creditYYProduct";
            }

        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 查询借款人信息
     *
     * @return
     */
    public String borrowerProduct() {
        String json = "";
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                //管理员没有登录;
                return null;
            }
            //根据借款期限来加载借款人的信息;
            List borrowerInfo = borrowerInfoBean.findProductPageUtil(termLoan);
            StringBuffer url = new StringBuffer();
            url.append(getRequest().getServletContext().getContextPath());
            url.append("/Product/Admin/creditProduct!borrowerProduct.action?");
            if (!QwyUtil.isNullAndEmpty(borrowerInfo)) {
                getRequest().setAttribute("list", borrowerInfo);
                getRequest().setAttribute("table", "1");
                return "borrower";
            }

        } catch (Exception e) {
            log.error("操作异常: ", e);
            json = QwyUtil.getJSONString("err", "查询记录异常");
        }


        return null;
    }


    /**
     * 发布预约产品
     *
     * @return
     * @author lrl
     * @date 2017-12-27
     */

    public String creditYYProduct() {
        try {
            log.info("发布预约产品");
            request = getRequest();
            if (!QwyUtil.isNullAndEmpty(product)) {
                if (QwyUtil.isNullAndEmpty(product.getId())) {
                    //根据userIds查询借款人总金额
                    if(null!=userIds && userIds.length>0){
                        BigDecimal totalAmount = borrowerInfoBean.getProductAllMoney(userIds);
                        Long newFinancialAmount = QwyUtil.calcNumber(totalAmount, 100, "*").longValue();
                        product.setFinancingAmount(newFinancialAmount);
                    }
                    Long atleastMoney = QwyUtil.calcNumber(product.getAtleastMoney(), 100, "*").longValue();
                    product.setAtleastMoney(atleastMoney);
                    if (!QwyUtil.isNullAndEmpty(endTime)) {
                        product.setEndTime(QwyUtil.fmyyyyMMddHHmm.parse(endTime));
                    } else {
                        long eTime = (product.getLcqx() - 2) * 24 * 60 * 60 * 1000 + new Date().getTime();
                        product.setEndTime(new Date(eTime));
                    }
                    if (!QwyUtil.isNullAndEmpty(finishTime)) {
                        product.setFinishTime((QwyUtil.fmyyyyMMddHHmm.parse(finishTime)));
                    } else {
                        long fTime = (product.getLcqx()) * 24 * 60 * 60 * 1000 + new Date().getTime();
                        product.setFinishTime(new Date(fTime));
                    }
                    product.setProductStatus("-3"); //预约排队产品状态
                    if ("1".equals(product.getProductType())) {
                        product.setActivity("新手专享");
                        product.setActivityColor("#f4583f");

                        if (!"4".equals(product.getQxType())) {
                            log.info("新手产品要对应新手产品期限类型");
                            request.setAttribute("isOk", "no");
                            return "creditYYProduct";
                        }

                        if (product.getTitle().indexOf("新手") == -1) {
                            log.info("新手产品类型要对应新手产品名称");
                            request.setAttribute("isOk", "no");
                            return "creditYYProduct";
                        }
                    }

                    //统计发布数
                    int productCount = borrowerInfoBean.getProductCount(new String[]{"0", "1"});
                    request.setAttribute("productCount", productCount);
                    //保存产品
                    String id = borrowerInfoBean.saveYYProduct(product);
                    //修改借款人信息
                    borrowerInfoBean.updateYYBorrowerInfo(userIds);
                    if (null != userIds && userIds.length > 0) {
                        for (String userId : userIds) {
                            String[] split = userId.split(",");
                            for (String ids : split) {
                                BorrowerInfo borrowerInfo = borrowerInfoBean.getBorrowerInfoById(Integer.valueOf(ids));
                                if (borrowerInfo != null) {
                                    ProjectBorrowerInfo brojectBorrowerInfo = new ProjectBorrowerInfo();
                                    brojectBorrowerInfo.setProductId(id);
                                    brojectBorrowerInfo.setProductTitle(product.getTitle());
                                    brojectBorrowerInfo.setProductCreate(product.getInsertTime());
                                    brojectBorrowerInfo.setBorrowerInfoId(Integer.valueOf(ids));
                                    brojectBorrowerInfo.setBoUserName(borrowerInfo.getBoUserName());
                                    brojectBorrowerInfo.setBoAmount(borrowerInfo.getBoAmount());
                                    brojectBorrowerInfo.setBoMobile(borrowerInfo.getMobile());
                                    brojectBorrowerInfo.setTermLoan(borrowerInfo.getTermLoan());
                                    brojectBorrowerInfo.setBoCreate(borrowerInfo.getDtCreate());
                                    brojectBorrowerInfo.setRaiseFunds(borrowerInfo.getBoAmount());
                                    brojectBorrowerInfo.setBoContractNo(borrowerInfo.getBoContractNo());
                                    brojectBorrowerInfo.setBoContractUrl(borrowerInfo.getCreditReportUrl());
                                    //保存
                                    this.projectBorrowerInfoBean.saveProjectBorrowerInfo(brojectBorrowerInfo);
                                }


                            }


                        }
                    }
                    if (!QwyUtil.isNullAndEmpty(id)) {
                        request.setAttribute("isOk", "ok");
                        return "creditYYProduct";
                    }
                }
            }
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        request.setAttribute("isOk", "no");
        request.setAttribute("product", product);
        return  "success";
    }

    /**
     * 发布历史记录选中借款人的信息页面
     *
     * @return
     */
    public String toShowBorrowerInfo() {
        String json = "";
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                //管理员没有登录;
                return null;
            }
            //根据标来加载借款人的信息;
            String productId = (String) getRequest().getParameter("productId");
            List<Object[]> projectBorrowerInfo=borrowerInfoBean.findProjectBorrowerInfo(productId);

            StringBuffer url = new StringBuffer();
            url.append(getRequest().getServletContext().getContextPath());
            url.append("/Product/Admin/creditProduct!toShowBorrowerInfo.action?");
            if (!QwyUtil.isNullAndEmpty(projectBorrowerInfo)) {
                //根据productId在查询打款记录
                for(Object[] pid:projectBorrowerInfo){
                    Object o = pid[1];
                    List borrowerPlayMoney= borrowerInfoBean.getBorrowerPlayMoneyByPid(o.toString());
                    getRequest().setAttribute("blist", borrowerPlayMoney);

                }
                getRequest().setAttribute("list", projectBorrowerInfo);
                getRequest().setAttribute("table", "1");
                return "borrowerUser";
            }
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return  null;
    }

    /**
     * 查看产品记录页面 ，删除信用贷预约产品
     *
     * @return
     */
    public String deleteCreditProduct() throws Exception {
        String productId = (String) getRequest().getParameter("productId");
        log.info("删除预约产品，产品id:" + productId);
        String json = "";
        try {
            //删除标
            borrowerInfoBean.deleteCreditProduct(productId);
            //查询借款人信息
            List<Object> blist=borrowerInfoBean.findBorrowerInfo(productId);
            for (Object id:blist) {
                BorrowerInfo borrowerInfo = borrowerInfoBean.getBorrowerInfoById(Integer.valueOf(id+""));
                //借款人重置
                borrowerInfoBean.upBorrowerInfo(borrowerInfo);
            }
            List<Object> plist=borrowerInfoBean.findProjectBorrowerInfoById(productId);
            for(Object pid:plist){
                ProjectBorrowerInfo projectBorrowerInfo = borrowerInfoBean.getProjectBorrowerInfoById(Integer.valueOf(pid+""));
                borrowerInfoBean.deleteProduct(projectBorrowerInfo);
            }


            json = QwyUtil.getJSONString("success", " ");
            QwyUtil.printJSON(getResponse(), json);

        } catch (Exception e) {
            log.error("操作异常: ", e);
            log.error("系统错误", e);
        }

        return null;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }


    public String getTermLoan() {
        return termLoan;
    }

    public void setTermLoan(String termLoan) {
        this.termLoan = termLoan;
    }

    public String[] getUserIds() {
        return userIds;
    }

    public void setUserIds(String[] userIds) {
        this.userIds = userIds;
    }
}