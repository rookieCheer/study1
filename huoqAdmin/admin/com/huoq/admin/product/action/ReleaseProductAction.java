package com.huoq.admin.product.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import com.huoq.assetSide.bean.BorrowerInfoBean;
import com.huoq.orm.*;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;

import com.huoq.admin.product.bean.ReleaseProductBean;
import com.huoq.admin.product.bean.VirtualInsRecordBean;
import com.huoq.common.action.BaseAction;
import com.huoq.common.bean.PlatformBean;
import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.ossUtils.OssUtil;
import com.huoq.product.bean.IndexBean;
import com.huoq.thread.bean.SendBookingProductBean;

import net.sf.jasperreports.engine.JasperPrint;
/**
 * 后台发布产品Action层<br>
 * 管理员进行产品的发布,对页面的值进行判断;
 *
 * @author qwy
 *         <p>
 *         2015-4-16下午11:52:17
 */
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/Admin")
// 发布产品页面
@Results({
        @Result(name = "release", value = "/Product/Admin/productManager/releaseProduct.jsp"),
        @Result(name = "productSend", value = "/Product/Admin/productManager/product.jsp"),
        @Result(name = "releaseRedirect", value = "/Product/Admin/releaseProduct!sendProduct.action?isOk=${isOk}", type = org.apache.struts2.dispatcher.ServletRedirectResult.class),
        @Result(name = "productrecord", value = "/Product/Admin/productManager/product_history.jsp"),
        @Result(name = "productAudit", value = "/Product/Admin/productManager/productAudit.jsp"),
        @Result(name = "details", value = "/Product/Admin/productManager/productDetails.jsp"),
        @Result(name = "detailsFreshman", value = "/Product/Admin/productManager/productDetailsFreshman.jsp"),
        @Result(name = "productzs", value = "/Product/Admin/productManager/product_zs.jsp"),
        @Result(name = "productList", value = "/Product/Admin/productManager/productlist.jsp"),
        @Result(name = "productFx", value = "/Product/Admin/productManager/fxzb.jsp"),
        @Result(name = "findZjsdmx", value = "/Product/Admin/productManager/zjsdmxlist.jsp"),
        @Result(name = "modifyProduct", value = "/Product/Admin/productManager/modifyProduct.jsp"),
        @Result(name = "err", value = "/Product/Admin/err.jsp"),
        @Result(name = "releaseCGYY", value = "/Product/Admin/productManager/releaseCGYYProduct.jsp"),//发布常规预约产品页面 add by yks 2016-09-23
        @Result(name = "modifyYYProduct", value = "/Product/Admin/productManager/modifyYYProduct.jsp"),//修改常规预约产品页面 add by yks 2016-09-26
        @Result(name = "booking", value = "/Product/Admin/productManager/addProduct.jsp")

})
public class ReleaseProductAction extends BaseAction {

    /**
     * 产品
     */
    private Product product;
    @Resource
    private ReleaseProductBean bean;
    @Resource
    IndexBean indexBean;
    @Resource
    private PlatformBean platformBean;
    @Resource
    private VirtualInsRecordBean virtualInsRecordBean;
    @Resource
    private BorrowerInfoBean borrowerInfoBean;
    private String endTime;
    private String productStatus;

    private String finishTime;
    private String insertTime;
    private String username;
    private String productId;


    private String title;
    private String annualEarnings;
    private String financingAmount;

    private File file;
    private String fileContentType;
    // private List<String> fileFileName;
    private String fileFileName;
    private String removeId;
    private Integer currentPage = 1;
    private Integer pageSize = 50;
    private static String superName = "0EA5D6BC23E8EEC78F62546B9F68BABFA96976B775889BA625DB6D764FD0DBD42A1C0F45F85B0DE8";
    private String inputTitle;//页面用户输入的产品名称，待校验

    private String keyword;
    private BookingKeyword bookingKeyword;
    private String realName;
    
    public String flag;

    @Resource
    private SendBookingProductBean sendBookingProductBean;
    
    /**
     * 发布产品
     *
     * @return
     */
    public String releaseProduct() {
        try {
            request = getRequest();
            if (!QwyUtil.isNullAndEmpty(product)) {
                if (QwyUtil.isNullAndEmpty(product.getId())) {
                    Long newFinancialAmount = QwyUtil.calcNumber(
                            product.getFinancingAmount(), 100, "*").longValue();
                    Long atleastMoney = QwyUtil.calcNumber(
                            product.getAtleastMoney(), 100, "*").longValue();
                    product.setFinancingAmount(newFinancialAmount);
                    product.setAtleastMoney(atleastMoney);
                    product.setEndTime(QwyUtil.fmyyyyMMdd.parse(endTime));
                    product.setFinishTime((QwyUtil.fmyyyyMMdd.parse(finishTime)));
                    //查看是否存在排队中预约产品,有则删除
                    Product yyProduct = bean.alterYYProducts(product.getTitle());
                    if (!QwyUtil.isNullAndEmpty(yyProduct)) {
                        log.info("是否删除了预约常规产品[" + yyProduct == null ? "否；" : "是，" + yyProduct.getTitle() + "]");
                    }
                    if (QwyUtil.isNullAndEmpty(product.getRealName())) {
                        log.info("姓名不正确!");
                        request.setAttribute("isOk", "no");
                        return "release";
                    }
                    if (QwyUtil.isNullAndEmpty(product.getPhone())) {
                        log.info("联系号码不正确!");
                        request.setAttribute("isOk", "no");
                        return "release";
                    }
                    product.setPhone(DESEncrypt.jiaMiUsername(product.getPhone()));
                    if (QwyUtil.isNullAndEmpty(product.getIdcard())) {
                        log.info("身份证号不正确!");
                        request.setAttribute("isOk", "no");
                        return "release";
                    }
                    product.setIdcard(DESEncrypt.jiaMiIdCard(product.getIdcard()));
                    if (QwyUtil.isNullAndEmpty(product.getAddress())) {
                        log.info("联系地址不正确!");
                        request.setAttribute("isOk", "no");
                        return "release";
                    }
                    if (QwyUtil.isNullAndEmpty(product.getJkyt())) {
                        log.info("借款用途不能为空!");
                        request.setAttribute("isOk", "no");
                        return "release";
                    }
                    
                    if (product.getTitle().contains("货押宝")) {
                    	if (!"5".equals(product.getInvestType())) {
                    		log.info("产品名称与投资类别不符");
                            request.setAttribute("isOk", "no");
                            return "release";
						}
					}
                    if (product.getTitle().contains("车贷宝")) {
                    	if (!"6".equals(product.getInvestType())) {
                    		log.info("产品名称与投资类别不符");
                            request.setAttribute("isOk", "no");
                            return "release";
						}
					}
                    
                    if ("6".equals(product.getInvestType())) {
						if (QwyUtil.isNullAndEmpty(product.getAge())) {
							log.info("车贷借款人年龄不能为空");
                            request.setAttribute("isOk", "no");
                            return "release";
						}
						
						if (QwyUtil.isNullAndEmpty(product.getEducation())) {
							log.info("借款人学历不能为空");
                            request.setAttribute("isOk", "no");
                            return "release";
						}
						
						if (QwyUtil.isNullAndEmpty(product.getMarriage())) {
							log.info("借款人婚姻状况不能为空");
                            request.setAttribute("isOk", "no");
                            return "release";
						}
						
						if (QwyUtil.isNullAndEmpty(product.getHjAddress())) {
							log.info("借款人户籍地址不能为空");
                            request.setAttribute("isOk", "no");
                            return "release";
						}
						
						if (QwyUtil.isNullAndEmpty(product.getCarDingf())) {
							log.info("丁方车贷审核服务方不能为空");
                            request.setAttribute("isOk", "no");
                            return "release";
						}
						
						if (QwyUtil.isNullAndEmpty(product.getCarDingfAddress())) {
							log.info("丁方车贷审核服务地址不能为空");
                            request.setAttribute("isOk", "no");
                            return "release";
						}
					}
                    
                    String id = bean.saveProduct(product);
                    int productCount = bean.getProductCount(new String[]{"0", "1"});
                    request.setAttribute("productCount", productCount);
                    if (!QwyUtil.isNullAndEmpty(id)) {
                        //platformBean.updateTotalMoney(product.getFinancingAmount());
                        request.setAttribute("isOk", "ok");
                        return "release";
                    }
                } else {
                    if (bean.updateProduct(product)) {
                        if ("0".equals(product.getProductType())) {
                            bean.modifyContractByProductId(product.getId(), product.getTitle());
                        }
                        request.setAttribute("isOk", "ok");
                        return "modifyProduct";
                    } else {
                        request.setAttribute("isOk", "no");
                        return "modifyProduct";
                    }
                }

            }
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        request.setAttribute("isOk", "no");
        request.setAttribute("product", product);
        return "release";
    }


    /**
     * 发布产品记录
     *
     * @return
     */
    public String productRecord() {
        String json = "";
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                //管理员没有登录;
                return null;
            }
            if (!superName.equals(users.getUsername())) {
                if (isExistsQX("产品历史记录", users.getId())) {
                    getRequest().setAttribute("err", "您没有操作该功能的权限!");
                    return "err";
                }
            }
            String filePath = request.getServletContext().getRealPath("/WEB-INF/classes/releaseProduct.jasper");
            log.info("报表路径: " + filePath);
            //根据状态来加载提现的记录;
            PageUtil<Object[]> pageUtil = new PageUtil<Object[]>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(pageSize);
            //产品名称
//			if(!QwyUtil.isNullAndEmpty(title)){
//				title=new String (title.getBytes("ISO-8859-1"),"UTF-8");
//			}
            Product product1 = new Product();
            product1.setTitle(title);
            if (!QwyUtil.isNullAndEmpty(annualEarnings))
                product1.setAnnualEarnings(Double.parseDouble(annualEarnings));
            if (!QwyUtil.isNullAndEmpty(financingAmount))
                product1.setFinancingAmount(Long.parseLong(financingAmount));
            pageUtil = bean.findProductsPageUtil(pageUtil, product1, finishTime, insertTime, username, productStatus,"finish_time",realName);
            StringBuffer url = new StringBuffer();
            url.append(getRequest().getServletContext().getContextPath());
            url.append("/Product/Admin/releaseProduct!productRecord.action?");
            if (!QwyUtil.isNullAndEmpty(title)) {
                url.append("&title=" + title);
            }
            if (!QwyUtil.isNullAndEmpty(financingAmount)) {
                url.append("&financingAmount=" + financingAmount);
            }
            if (!QwyUtil.isNullAndEmpty(annualEarnings)) {
                url.append("&annualEarnings=" + annualEarnings);
            }
            if (!QwyUtil.isNullAndEmpty(finishTime)) {
                url.append("&finishTime=" + finishTime);
            }
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                url.append("&insertTime=" + insertTime);
            }
            if (!QwyUtil.isNullAndEmpty(productStatus)) {
                url.append("&productStatus=" + productStatus);
            }
            if (!QwyUtil.isNullAndEmpty(realName)) {
                url.append("&realName=" + realName);
            }
        /*	getRequest().setAttribute("tj1", tj1);
            getRequest().setAttribute("tj2", tj2);
			getRequest().setAttribute("tj3", tj3);
			getRequest().setAttribute("tj4", tj4);*/
            pageUtil.setPageUrl(url.toString());
            getRequest().setAttribute("title", title);
            getRequest().setAttribute("financingAmount", financingAmount);
            getRequest().setAttribute("annualEarnings", annualEarnings);
            getRequest().setAttribute("productStatus", productStatus);
            getRequest().setAttribute("finishTime", finishTime);
            getRequest().setAttribute("insertTime", insertTime);
            getRequest().setAttribute("realName", realName);
            pageUtil.setPageUrl(url.toString());
            if (!QwyUtil.isNullAndEmpty(pageUtil)) {
                getRequest().setAttribute("pageUtil", pageUtil);
                List<Object[]> list = pageUtil.getList();
                dealList(list);
                getRequest().setAttribute("list", list);
                getRequest().setAttribute("table", "1");
                return "productrecord";
            }
        } catch (Exception e) {
            log.error("操作异常: ", e);
            json = QwyUtil.getJSONString("err", "查询记录异常");
        }
        return null;
    }

    /**
     * 修改虚拟记录
     * @param list
     */
    private void dealList(List<Object[]> list){
      if(list!=null){
           int size = list.size();
           if(size>0){
               Map<String, Double> mapProductToVirtual = virtualInsRecordBean.MapProductToVirtual();
              if(mapProductToVirtual!=null){
                for(int i=0;i<size;i++){
                  Object[] object = list.get(i);
                  String id =(String) object[11];
                   Double virtualInv = mapProductToVirtual.get(id);
                   if(virtualInv!=null){
                       virtualInv = virtualInv*0.01;
                       object[8]=virtualInv;
                       list.set(i,object);
                   }
                }
              }
           }
      }
    }

    /**
     * 发息总表
     *
     * @return
     */
    public String productFx() {
        String json = "";
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                //管理员没有登录;
                return null;
            }
            if (!superName.equals(users.getUsername())) {
                if (isExistsQX("付息总表", users.getId())) {
                    getRequest().setAttribute("err", "您没有操作该功能的权限!");
                    return "err";
                }
            }
            String filePath = request.getServletContext().getRealPath("/WEB-INF/classes/releaseProduct.jasper");
            log.info("报表路径: " + filePath);
            //根据状态来加载提现的记录;
            PageUtil<Product> pageUtil = new PageUtil<Product>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(pageSize);
            //产品名称
//			if(!QwyUtil.isNullAndEmpty(title)){
//				title=new String (title.getBytes("ISO-8859-1"),"UTF-8");
//			}
            if (QwyUtil.isNullAndEmpty(product)) {
                product = new Product();
            }
            product.setTitle(title);
            pageUtil = bean.findProductPageUtil(pageUtil, product, finishTime, null, null, null, "finishTime");
            StringBuffer url = new StringBuffer();
            url.append(getRequest().getServletContext().getContextPath());
            url.append("/Product/Admin/releaseProduct!productFx.action?");
            if (!QwyUtil.isNullAndEmpty(title)) {
                url.append("&title=" + title);
            }
            if (!QwyUtil.isNullAndEmpty(finishTime)) {
                url.append("&finishTime=" + finishTime);
            }
            pageUtil.setPageUrl(url.toString());
            getRequest().setAttribute("title", title);
            getRequest().setAttribute("product", product);
            getRequest().setAttribute("finishTime", finishTime);
            pageUtil.setPageUrl(url.toString());
            if (!QwyUtil.isNullAndEmpty(pageUtil)) {
                Map<String, String> map = bean.findInvestorsByProductId();
                getRequest().setAttribute("pageUtil", pageUtil);
                getRequest().setAttribute("map", map);
                getRequest().setAttribute("list", pageUtil.getList());
                return "productFx";
            }
        } catch (Exception e) {
            log.error("操作异常: ", e);
            json = QwyUtil.getJSONString("err", "查询记录异常");
        }
        return "productFx";
    }


    /**
     * 导出产品表格
     */
//    public String iportFXTable() {
//        List<JasperPrint> list = null;
//        String name = QwyUtil.fmyyyyMMddHHmmss3.format(new Date()) + "_product";
//        try {
//            String filePath = request.getServletContext().getRealPath("/WEB-INF/classes/fxzb.jasper");
//            log.info("iportTable报表路径: " + filePath);
//            list = bean.getFXJasperPrintList(product, finishTime, filePath);
//            doIreport(list, name);
//        } catch (Exception e) {
//            log.error("操作异常: ",e);
//        }
//        return null;
//    }

//
    public String iportFXTable() {
        try {
            PageUtil<Product> pageUtil = new PageUtil<>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(999999);
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("利息总表记录");
            HSSFRow row = sheet.createRow((int) 0);
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
            HSSFCell cell = row.createCell(0);
            cell.setCellValue("序号");
            cell.setCellStyle(style);
            cell = row.createCell(1);
            cell.setCellValue("产品名称");
            cell.setCellStyle(style);
            cell = row.createCell(2);
            cell.setCellValue("投资类别");
            cell.setCellStyle(style);
            cell = row.createCell(3);
            cell.setCellValue("项目总额");
            cell.setCellStyle(style);
            cell = row.createCell(4);
            cell.setCellValue("起投金额");
            cell.setCellStyle(style);
            cell = row.createCell(5);
            cell.setCellValue("年化收益(%)");
            cell.setCellStyle(style);
            cell = row.createCell(6);
            cell.setCellValue("还款方式");
            cell.setCellStyle(style);
            cell = row.createCell(7);
            cell.setCellValue("起息日");
            cell.setCellStyle(style);
            cell = row.createCell(8);
            cell.setCellValue("上线时间");
            cell.setCellStyle(style);
            cell = row.createCell(9);
            cell.setCellValue("项目结束时间");
            cell.setCellStyle(style);
            cell = row.createCell(10);
            cell.setCellValue("付息总额");
            cell.setCellStyle(style);

            Product product = null;
    //        pageUtil = bean.findUsersByChannel(pageUtil, channel, username, insertTime, isbindbank, level, inMoney1, inMoney2);
            pageUtil = bean.findProductPageUtil(pageUtil, product, finishTime, null, null, null, "finishTime");
            List<Product> list = new ArrayList<>();
            if (!QwyUtil.isNullAndEmpty(pageUtil)) {
                list = pageUtil.getList();
            }
            Map<String, String> map = null;
            if (!QwyUtil.isNullAndEmpty(pageUtil)) {
                map = bean.findInvestorsByProductId();
            }
            for (int i = 0; i < list.size(); i++) {
                row = sheet.createRow((int) i + 1);
                product = (Product) list.get(i);
                row = sheet.createRow((int) i + 1);
                row.createCell(0).setCellValue((int) i + 1);
                row.createCell(1).setCellValue(!QwyUtil.isNullAndEmpty(product.getTitle())?product.getTitle():"");
                row.createCell(2).setCellValue(!QwyUtil.isNullAndEmpty(product.getCplx())?product.getCplx():"");
                row.createCell(3).setCellValue(!QwyUtil.isNullAndEmpty(product.getAllCopies())?product.getAllCopies()+"":"");
                row.createCell(4).setCellValue(!QwyUtil.isNullAndEmpty(product.getQtje())?QwyUtil.calcNumber(product.getQtje(),"100","/",2)+"":"");
                row.createCell(5).setCellValue(!QwyUtil.isNullAndEmpty(product.getAnnualEarnings())?product.getAnnualEarnings()+"":"");
                row.createCell(6).setCellValue(!QwyUtil.isNullAndEmpty(product.getFxfs())?product.getFxfs():"");
                row.createCell(7).setCellValue(!QwyUtil.isNullAndEmpty(product.getJxfs())?product.getJxfs():"");
                row.createCell(8).setCellValue(!QwyUtil.isNullAndEmpty(product.getInsertTime())?QwyUtil.fmyyyyMMddHHmmss.format(product.getInsertTime()):"");
                row.createCell(9).setCellValue(!QwyUtil.isNullAndEmpty(product.getFinishTime())?QwyUtil.fmyyyyMMddHHmmss.format(product.getFinishTime()):"");
                String ear = map.get(product.getId());
                row.createCell(10).setCellValue(!QwyUtil.isNullAndEmpty(ear)?QwyUtil.calcNumber(ear,"100","/",2)+"":"");
            }

            String pathname = QwyUtil.fmyyyyMMddHHmmss3.format(new Date()) + "_product_rate.xls";
            String realPath = request.getServletContext().getRealPath("/report/" + pathname);
            log.info("产品利息总表地址：" + realPath);
            FileOutputStream fout = new FileOutputStream(realPath);
            wb.write(fout);
            fout.close();
            response.getWriter().write("/report/" + pathname);
        } catch (Exception e) {
            log.error("操作异常: ",e);
        }
        return null;
    }

//    <td>${item.title}</td>
//								<td>${item.cplx}</td>
//								<td><fmt:formatNumber value="${item.allCopies}" pattern="#,##0.##"/></td>
//								<td><fmt:formatNumber value="${item.qtje*0.01}" pattern="#,##0.##"/></td>
//								<td><fmt:formatNumber value="${item.annualEarnings}" pattern="#.##" />%</td>
//								<td>${item.fxfs}</td>
//								<td>${item.jxfs}</td>
//								<td><fmt:formatDate value="${item.insertTime}" pattern="yyyy-MM-dd HH:MM:ss"/></td>
//								<td><fmt:formatDate value="${item.finishTime}" pattern="yyyy-MM-dd"/></td>
//								<td><a class="a" href="${pageContext.request.contextPath}/Product/Admin/interestDetails!findInvertorsByProduct.action?productId=${item.id}"><fmt:formatNumber value="${map[item.id] * 0.01}" pattern="#,##0.##"/></a></td>
//
//
    /**
     * 所有产品历史记录
     */
    public String productList() {
        String json = "";
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                //管理员没有登录;
                return null;
            }
            if (!superName.equals(users.getUsername())) {
                if (isExistsQX("查看发布产品", users.getId())) {
                    getRequest().setAttribute("err", "您没有操作该功能的权限!");
                    return "err";
                }
            }
            //根据状态来加载提现的记录;
            PageUtil<Product> pageUtil = new PageUtil<Product>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(pageSize);
            //产品名称
//			if(!QwyUtil.isNullAndEmpty(product)&&!QwyUtil.isNullAndEmpty(product.getTitle())){
//				product.setTitle(new String (product.getTitle().getBytes("ISO-8859-1"),"UTF-8"));
//			}
//			product.setTitle();
            Map<String, Double> mapProductToVirtual = virtualInsRecordBean.MapProductToVirtual();
            pageUtil = bean.findProductPageUtil(pageUtil, product, finishTime, insertTime, username, productStatus);
            StringBuffer url = new StringBuffer();
            url.append(getRequest().getServletContext().getContextPath());
            url.append("/Product/Admin/releaseProduct!productList.action?");
            if (!QwyUtil.isNullAndEmpty(product)) {
                url.append("&product.title=" + product.getTitle());
            }
            if (!QwyUtil.isNullAndEmpty(finishTime)) {
                url.append("&finishTime=" + finishTime);
            }
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                url.append("&insertTime=" + insertTime);
            }
            if (!QwyUtil.isNullAndEmpty(productStatus)) {
                url.append("&productStatus=" + productStatus);
            }
            getRequest().setAttribute("mapProductToVirtual", mapProductToVirtual);
            getRequest().setAttribute("product", product);
            getRequest().setAttribute("finishTime", finishTime);
            getRequest().setAttribute("insertTime", insertTime);
            pageUtil.setPageUrl(url.toString());
            if (!QwyUtil.isNullAndEmpty(pageUtil)) {
                Map<String, ProductAccount> map = bean.findInvAccountByProductId();
                getRequest().setAttribute("pageUtil", pageUtil);
                getRequest().setAttribute("map", map);
                getRequest().setAttribute("list", pageUtil.getList());
                return "productList";
            }
        } catch (Exception e) {
            log.error("操作异常: ", e);
            json = QwyUtil.getJSONString("err", "查询记录异常");
        }
        return "productList";
    }

    /**
     * 导出产品表格
     */
    public String iportTable() {
        try {
            //根据状态来加载提现的记录;
            PageUtil<Product> pageUtil = new PageUtil<Product>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(999999);
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("产品发布记录");
            HSSFRow row = sheet.createRow((int) 0);
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
            HSSFCell cell = row.createCell(0);
            cell.setCellValue("序号");
            cell.setCellStyle(style);
            cell = row.createCell(1);
            cell.setCellValue("产品名称");
            cell.setCellStyle(style);
            cell = row.createCell(2);
            cell.setCellValue("产品类型");
            cell.setCellStyle(style);
            cell = row.createCell(3);
            cell.setCellValue("产品状态");
            cell.setCellStyle(style);
            cell = row.createCell(4);
            cell.setCellValue("完成进度");
            cell.setCellStyle(style);
            cell = row.createCell(5);
            cell.setCellValue("年化收益");
            cell.setCellStyle(style);

            cell = row.createCell(6);
            cell.setCellValue("理财期限");
            cell.setCellStyle(style);
            cell = row.createCell(7);
            cell.setCellValue("剩余天数");
            cell.setCellStyle(style);
            cell = row.createCell(8);
            cell.setCellValue("起投金额");
            cell.setCellStyle(style);
            cell = row.createCell(9);
            cell.setCellValue("项目总额");
            cell.setCellStyle(style);

            cell = row.createCell(10);
            cell.setCellValue("募集金额");
            cell.setCellStyle(style);
            cell = row.createCell(11);
            cell.setCellValue("实际募集金额");
            cell.setCellStyle(style);
            cell = row.createCell(12);

            cell.setCellValue("投资券金额");
            cell.setCellStyle(style);
            cell = row.createCell(13);

            cell.setCellValue("虚拟投资金额");
            cell.setCellStyle(style);
            cell = row.createCell(14);
            cell.setCellValue("剩余金额");
            cell.setCellStyle(style);
            cell = row.createCell(15);
            cell.setCellValue("发布时间");
            cell.setCellStyle(style);
            cell = row.createCell(16);
            cell.setCellValue("到期时间");
            cell.setCellStyle(style);
            cell = row.createCell(17);
            cell.setCellValue("预发利息");
            cell.setCellStyle(style);
            cell = row.createCell(18);
            cell.setCellValue("实发利息");
            cell.setCellStyle(style);
            Product report = null;
            Map<String, Double> mapProductToVirtual = virtualInsRecordBean.MapProductToVirtual();
            pageUtil = bean.findProductPageUtil(pageUtil, product, finishTime, insertTime, username, productStatus);
            Map<String, ProductAccount> map = bean.findInvAccountByProductId();
            List list = pageUtil.getList();
            for (int i = 0; i < list.size(); i++) {
                row = sheet.createRow((int) i + 1);
                report = (Product) list.get(i);
                row = sheet.createRow((int) i + 1);
                row.createCell(0).setCellValue((int) i + 1);
                row.createCell(1).setCellValue(report.getTitle());
                row.createCell(2).setCellValue(report.getCplx());
                row.createCell(3).setCellValue(report.getCpzt());
                row.createCell(4).setCellValue(report.getWcjd() * 100 + "%");
                row.createCell(5).setCellValue(report.getAnnualEarnings() + "%");
                row.createCell(6).setCellValue(report.getLcqx());
                row.createCell(7).setCellValue(report.getTzqx());
                row.createCell(8).setCellValue(report.getQtje() * 0.01);
                row.createCell(9).setCellValue(report.getAllCopies());
                row.createCell(10).setCellValue(report.getHasCopies());
                row.createCell(11).setCellValue(report.getHasCopies() - mapProductToVirtual.get(report.getId()) * 0.01 - map.get(report.getId()).getCoupon() * 0.01);
                //row.createCell(11).setCellValue(map.get(report.getId()).getIn_money()*0.01);

                row.createCell(12).setCellValue(map.get(report.getId()).getCoupon() * 0.01);
                row.createCell(13).setCellValue(mapProductToVirtual.get(report.getId()) * 0.01);
                row.createCell(14).setCellValue(report.getLeftCopies());
                row.createCell(15).setCellValue((QwyUtil.fmyyyyMMddHHmmss.format(report.getInsertTime())));
                row.createCell(16).setCellValue((QwyUtil.fmyyyyMMdd.format(report.getFinishTime())));
                row.createCell(17).setCellValue(report.getYflx());
                row.createCell(18).setCellValue(map.get(report.getId()).getAll_shouyi() * 0.01);
            }
            String realPath = request.getServletContext().getRealPath("/report/releaseProduct.xls");
            FileOutputStream fout = new FileOutputStream(realPath);
            wb.write(fout);
            fout.close();
            response.getWriter().write("/report/releaseProduct.xls");

        } catch (Exception e) {
            log.error("操作异常: ",e);
        }
        return null;
    }

    /**
     * 资金速动明细
     */
    public String findZjsdmx() {
        String json = "";
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                //管理员没有登录;
                return null;
            }
            if (!superName.equals(users.getUsername())) {
                if (isExistsQX("资金速冻明细表", users.getId())) {
                    getRequest().setAttribute("err", "您没有操作该功能的权限!");
                    return "err";
                }
            }
            //根据状态来加载提现的记录;
            PageUtil<Zjsumx> pageUtil = new PageUtil<Zjsumx>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(pageSize);
            pageUtil = bean.findZjsdmxPageUtil(pageUtil, insertTime);
            StringBuffer url = new StringBuffer();
            url.append(getRequest().getServletContext().getContextPath());
            url.append("/Product/Admin/releaseProduct!findZjsdmx.action?");
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                url.append("&insertTime=" + insertTime);
            }
            getRequest().setAttribute("insertTime", insertTime);
            pageUtil.setPageUrl(url.toString());
            if (!QwyUtil.isNullAndEmpty(pageUtil)) {
                getRequest().setAttribute("pageUtil", pageUtil);
                return "findZjsdmx";
            }
        } catch (Exception e) {
            log.error("操作异常: ", e);
            json = QwyUtil.getJSONString("err", "查询记录异常");
        }
        return "findZjsdmx";
    }

    /**
     * 导出产品表格
     */
    public String iportZjsdmxTable() {
        List<JasperPrint> list = null;
        String name = QwyUtil.fmyyyyMMddHHmmss3.format(new Date()) + "_zjsdmx";
        try {
            String filePath = request.getServletContext().getRealPath("/WEB-INF/classes/zjsdmx.jasper");
            log.info("iportTable报表路径: " + filePath);
            list = bean.getZjsumxJasperPrintList(insertTime, filePath);
            doIreport(list, name);
        } catch (Exception e) {
            log.error("操作异常: ",e);
        }
        return null;
    }


    /**
     * 在售中的产品记录
     *
     * @return
     */
    public String productZS() {
        String json = "";
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                //管理员没有登录;
                return null;
            }
            if (!superName.equals(users.getUsername())) {
                if (isExistsQX("在售项目", users.getId())) {
                    getRequest().setAttribute("err", "您没有操作该功能的权限!");
                    return "err";
                }
            }
            PageUtil<Product> pageUtil = new PageUtil<Product>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(pageSize);
            //产品名称
//			if(!QwyUtil.isNullAndEmpty(product)&&!QwyUtil.isNullAndEmpty(product.getTitle())){
//				product.setTitle(new String (product.getTitle().getBytes("ISO-8859-1"),"UTF-8"));
//			}
            pageUtil = bean.findProductPageUtil(pageUtil, product, finishTime, insertTime, null, "0");
            StringBuffer url = new StringBuffer();
            url.append(getRequest().getServletContext().getContextPath());
            url.append("/Product/Admin/releaseProduct!productZS.action?");
            if (!QwyUtil.isNullAndEmpty(product)) {
                url.append("&product.title = " + product.getTitle());
            }
            if (!QwyUtil.isNullAndEmpty(finishTime)) {
                url.append(" &finishTime = " + finishTime);
            }
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                url.append(" &insertTime = " + insertTime);
            }
            getRequest().setAttribute("product", product);
            getRequest().setAttribute("finishTime", finishTime);
            getRequest().setAttribute("insertTime", insertTime);
            pageUtil.setPageUrl(url.toString());
            if (!QwyUtil.isNullAndEmpty(pageUtil)) {
                getRequest().setAttribute("pageUtil", pageUtil);
                getRequest().setAttribute("list", pageUtil.getList());
                return "productzs";
            }
        } catch (Exception e) {
            log.error("操作异常: ", e);
            json = QwyUtil.getJSONString("err", "查询记录异常");
        }
        return "productzs";
    }

    /**
     * 审核产品记录
     *
     * @return
     */
    public String productAudit() {
        String json = "";
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                //管理员没有登录;
                return null;
            }
            if (!superName.equals(users.getUsername())) {
                if (isExistsQX("产品审核", users.getId())) {
                    getRequest().setAttribute("err", "您没有操作该功能的权限!");
                    return "err";
                }
            }
            quertWSHProduct();
            return "productAudit";
        } catch (Exception e) {
            log.error("操作异常: ", e);
            json = QwyUtil.getJSONString("err", "查询记录异常");
        }
        return "productAudit";
    }

    /**
     * 分页查询未审核产品
     */
    public void quertWSHProduct() throws Exception {
        //根据状态来加载提现的记录;
        PageUtil<Product> pageUtil = new PageUtil<Product>();
        pageUtil.setCurrentPage(1);
        pageUtil.setPageSize(100);
        pageUtil = bean.findProductPageUtil(pageUtil, null, null, null, null, "-1");
        StringBuffer url = new StringBuffer();
        url.append(getRequest().getServletContext().getContextPath());
        url.append("/Product/Admin/releaseProduct!productRecord.action?");
        pageUtil.setPageUrl(url.toString());
        if (!QwyUtil.isNullAndEmpty(pageUtil)) {
            getRequest().setAttribute("pageUtil", pageUtil);
            getRequest().setAttribute("list", pageUtil.getList());
        }
    }

    /**
     * 立即售罄
     *
     * @return
     */
    public String nowSq() {
        String json = "";
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession()
                    .getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                //管理员没有登录;
                return null;
            }
            if (bean.updateProductStatus(product.getId(), "1")) {
                request.setAttribute("update", "ok");
                json = QwyUtil.getJSONString("ok", "成功");
                QwyUtil.printJSON(getResponse(), json);
                return null;
            }
        } catch (Exception e) {
            log.error("操作异常: ",e);
        }
        json = QwyUtil.getJSONString("error", "失败");
        try {
            QwyUtil.printJSON(getResponse(), json);
        } catch (IOException e) {
            log.error("操作异常: ",e);
        }
        return null;
    }

    /**
     * 加载发布产品的页面；
     *
     * @return
     */
    /*public String sendProduct() {
        String json = "";
        String flag = getRequest().getParameter("flag"); //加载发布页面判断标识，如果为空，则为发布正常产品，否，为预约产品
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                //管理员没有登录;
                return null;
            }
            if (!superName.equals(users.getUsername())) {
                if (isExistsQX("发布常规产品", users.getId())) {
                    getRequest().setAttribute("err", "您没有操作该功能的权限!");
                    return "err";
                }
            }
            int productCount = bean.getProductCount(new String[]{"0", "1"});
            getRequest().setAttribute("productCount", productCount);
            if (QwyUtil.isNullAndEmpty(flag)) {
                return "release";
            } else if (flag.equals("yy")) {
                return "releaseCGYY";
            }
        } catch (IOException e) {
            log.error("操作异常: ",e);
        }
        return null;
    }*/
    
    
    /**
     * 加载发布产品的页面；
     *
     * @return
     */
    public String sendProduct() {
        String json = "";
        //String flag = getRequest().getParameter("flag"); //加载发布页面判断标识，如果为空，则为发布正常产品，否，为预约产品
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                //管理员没有登录;
                return null;
            }
            if (!superName.equals(users.getUsername())) {
                if (isExistsQX("发布常规产品", users.getId())) {
                    getRequest().setAttribute("err", "您没有操作该功能的权限!");
                    return "err";
                }
            }
            int productCount = bean.getProductCount(new String[]{"0", "1"});
            getRequest().setAttribute("productCount", productCount);
            if (QwyUtil.isNullAndEmpty(flag)) {
                return "release";
            } else if (flag.equals("yy")) {
            	//加载发布预约产品
            	Product pro = bean.getProductLeader();
            	if(!QwyUtil.isNullAndEmpty(pro)){
            		pro.setPhone(DESEncrypt.jieMiUsername(pro.getPhone()));
            		pro.setIdcard(DESEncrypt.jieMiIdCard(pro.getIdcard()));
            		getRequest().setAttribute("pro", pro);
            	}
            	List<String> list = sendBookingProductBean.getBookingKeyword();
            	String yyKeyword = QwyUtil.isNullAndEmpty(list)?"没有预约关键字":QwyUtil.packString(list.toArray()).replaceAll("'", "");
            	
            	List<Product> listZero = bean.getProductByStatus("0");
            	List<Product> listYuYue = bean.getProductByStatus("-3");
            	getRequest().setAttribute("listZero", listZero);
            	getRequest().setAttribute("listYuYue", listYuYue);
            	getRequest().setAttribute("yyKeyword", yyKeyword);
                return "releaseCGYY";
            }
        } catch (IOException e) {
            log.error("操作异常: ",e);
        }
        return null;
    }


    /**
     * 显示产品的详细信息;<br>
     * 根据产品id查找产品;
     *
     * @return
     */
    public String showProductDetails() {

        try {
            if (!QwyUtil.isNullAndEmpty(productId)) {
                product = indexBean.getProductById(productId);
            } else {
                log.info("111");
                product = getPvProduct(product);
            }
            getProductImg(product);
            getRequest().setAttribute("product", product);
        } catch (Exception e) {
            log.error("IndexAction.showProductDetails", e);
        }
        return "details";
    }


    /**
     * 审核产品
     */
    public String auditProduct() {
        String json = "";
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession()
                    .getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                //管理员没有登录;
                return null;
            }
            if (bean.updateProductStatus(product.getId(), productStatus)) {
                request.setAttribute("update", "ok");
                json = QwyUtil.getJSONString("ok", "成功");
                QwyUtil.printJSON(getResponse(), json);
                return null;
            }
        } catch (Exception e) {
            log.error("操作异常: ",e);
        }
        json = QwyUtil.getJSONString("error", "失败");
        try {
            QwyUtil.printJSON(getResponse(), json);
        } catch (IOException e) {
            log.error("操作异常: ",e);
        }
        return null;
    }

    /**
     * 得到预览产品实体
     *
     * @param product
     * @return
     * @throws Exception
     */
    public Product getPvProduct(Product product) throws Exception {
        if (!QwyUtil.isNullAndEmpty(product)) {
            Long newFinancialAmount = QwyUtil.calcNumber(
                    product.getFinancingAmount(), 100, "*").longValue();
            Long atleastMoney = QwyUtil.calcNumber(
                    product.getAtleastMoney(), 100, "*").longValue();
            product.setFinancingAmount(newFinancialAmount);
            product.setAtleastMoney(atleastMoney);
            if (!QwyUtil.isNullAndEmpty(endTime)) {
                product.setEndTime(QwyUtil.fmyyyyMMdd.parse(endTime));
            } else {
                long eTime = (product.getLcqx() - 2) * 24 * 60 * 60 * 1000 + new Date().getTime();
                product.setEndTime(new Date(eTime));
            }
            if (!QwyUtil.isNullAndEmpty(finishTime)) {
                product.setFinishTime((QwyUtil.fmyyyyMMdd.parse(finishTime)));
            } else {
                long fTime = (product.getLcqx()) * 24 * 60 * 60 * 1000 + new Date().getTime();
                product.setFinishTime(new Date(fTime));
            }
            product.setAllCopies(QwyUtil.calcNumber(product.getFinancingAmount(), 100, "/").longValue());
            product.setHasCopies(0L);
            product.setLeftCopies(product.getAllCopies());
            product.setProductType("0");
            product.setProductStatus("-1");
            product.setUserCount(0L);
            product.setInsertTime(new Date());
            product.setProgress(0d);
        }
        return product;
    }

    /**
     * 显示产品的详细信息;<br>
     * 根据产品id查找产品;
     *
     * @return
     */
    public String showProductDetailsFreshman() {
        try {
            if (!QwyUtil.isNullAndEmpty(productId)) {
                product = indexBean.getProductById(productId);
            } else {
                product = getPvProduct(product);

            }
            getProductImg(product);
            getRequest().setAttribute("product", product);
        } catch (Exception e) {
            log.error("IndexAction.showProductDetails", e);
        }
        return "detailsFreshman";
    }

    /**
     * 加载产品发布统计界面
     *
     * @return
     */
    public String productSend() {
        String json = "";
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession()
                    .getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                //管理员没有登录;
                return null;
            }
            if (!superName.equals(users.getUsername())) {
                if (isExistsQX("产品统计", users.getId())) {
                    getRequest().setAttribute("err", "您没有操作该功能的权限!");
                    return "err";
                }
            }
            int productCount = bean.getProductCount(new String[]{"0", "1"});
            int ptproductCount = bean.getProductCount(new String[]{"0"});
            int xsproductCount = bean.getProductCount(new String[]{"1"});
            int ysqproductCount = bean.getProductCount(null,
                    new String[]{"1", "2", "3"});
//            Platform platform = platformBean.getPlatform(null);
            Double ptproductAllMoney = bean
                    .getProductAllMoney(new String[]{"0"});
            Double xsproductAllMoney = bean
                    .getProductAllMoney(new String[]{"1"});
            quertWSHProduct();
            getRequest().setAttribute("productCount", productCount);
            getRequest().setAttribute("ptproductCount", ptproductCount);
            getRequest().setAttribute("xsproductCount", xsproductCount);
            getRequest().setAttribute("ysqproductCount", ysqproductCount);
//            getRequest().setAttribute("totalMoney", new BigDecimal(platform.getTotalMoney() / 100));
//            getRequest().setAttribute("wsje", new BigDecimal((platform.getTotalMoney() - platform.getCollectMoney()) / 100));
            getRequest().setAttribute("ptproductAllMoney", new BigDecimal(ptproductAllMoney / 100));
            getRequest().setAttribute("xsproductAllMoney", new BigDecimal(xsproductAllMoney / 100));
            return "productSend";
        } catch (Exception e) {
            log.error("操作异常: ",e);
        }
        return "productSend";
    }

    /**
     * 上传图片
     *
     * @return
     */
    public String uploadImage(String type) {
    	//change：图片存储到oss上
    	OssUtil ossUtil = new OssUtil(null);
    	log.info("上传图片："+file);
        request = getRequest();
        SystemConfig systemConfig = (SystemConfig) request.getServletContext().getAttribute("systemConfig");
        String json = "";
        String imageName = "";
        if (!QwyUtil.isNullAndEmpty(file)) {
            try {
                String sux = fileContentType.split("/")[1];
                String fileName = UUID.randomUUID().toString() + "." + sux;
                imageName += fileName + ";";
                
                String folder = systemConfig.getFileUrl()+"/mobile_img/" + type + "/";
                
                String newFile = folder + fileName;
                ossUtil.uploadFile(file, newFile, null);
            	json = QwyUtil.getJSONString("ok", imageName);
//            	if (!type.equals("notice/slt")) {
//            		String mobile_folder = systemConfig.getFileUrl() + "/mobile_img/" + type + "/";
//            		//String newImgSrc = Images.yasuoImg(newFile,mobile_folder + fileName,200, 200);
//            		CompressPicDemo mypic = new CompressPicDemo();
//            		//新版本更新后,替换成高清图;
//            		String newImgSrc = mypic.compressPic(folder, mobile_folder, fileName, fileName, 900, 1328, true);
//            		//旧版的上传图片;
//            		// String newImgSrc = mypic.compressPic(folder, mobile_folder, fileName, fileName, 580, 580, true);
//            		log.info(newImgSrc);
//                }
            } catch (Exception e) {
            	log.error("上传图片失败！"+e);
                json = QwyUtil.getJSONString("error", "上传图片失败");

            } finally {
                if (!QwyUtil.isNullAndEmpty(ossUtil)) {
                	ossUtil.destroy();
                }
            }
        }
        try {
            QwyUtil.printJSON(getResponse(), json);
        } catch (IOException e) {
           log.error("上传图片失败！"+e);
        }
        return null;
    }

    /**
     * 上传缩略图;
     *
     * @return
     */
    public String uploadSLT() {
        return uploadImage("notice/slt");
    }

    /**
     * 上传信息披露图片;
     *
     * @return
     */
    public String uploadInfoImage() {
        return uploadImage("info");
    }

    /**
     * 上传法律证书图片;
     *
     * @return
     */
    public String uploadLawImage() {
        return uploadImage("law");
    }
    
    /**
     * 上传项目流程图
     * @return
     */
    public String uploadProImage(){
    	return uploadImage("info");
    }


    /**
     * 删除信息披露图片;
     *
     * @return
     */
    public String removeInfoImage() {
        return removeImage("info");
    }

    /**
     * 删除法律证书图片;
     *
     * @return
     */
    public String removeLawImage() {
        return removeImage("law");
    }

    /**
     * 删除缩略图;
     *
     * @return
     */
    public String removeSLT() {
        return removeImage("notice/slt");
    }

    /**
     * 移除上传的图片
     *
     * @return
     */
    public String removeImage(String type) {
        try {
            QwyUtil.printJSON(getResponse(), QwyUtil.getJSONString("ok", ""));
        } catch (IOException e) {
        	 log.info("移除上传图片失败！"+e);
        }
        return null;
    }

    public void getProductImg(Product product) {
        if (!QwyUtil.isNullAndEmpty(product)) {
            //信息披露;
            String infoImg = product.getInfoImg();
            if (!QwyUtil.isNullAndEmpty(infoImg)) {
                List infoList = new ArrayList();
                SystemConfig config = (SystemConfig) getRequest().getServletContext().getAttribute("systemConfig");
                String[] infoImgs = infoImg.split(";");
                for (String str : infoImgs) {
                	String url =config.getHttpUrl() + config.getFileName() + "/web_img/info/" + str;
                    infoList.add(url);
                }
                getRequest().setAttribute("infoList", infoList);
            }
            //法律意见书
            String lawImg = product.getLawImg();
            if (!QwyUtil.isNullAndEmpty(lawImg)) {
                List lawList = new ArrayList();
                SystemConfig config = (SystemConfig) getRequest().getServletContext().getAttribute("systemConfig");
                String[] lawImgs = lawImg.split(";");
                for (String str : lawImgs) {
                    String url = config.getHttpUrl() + config.getFileName() + "/web_img/law/" + str;
                    lawList.add(url);
                }
                getRequest().setAttribute("lawList", lawList);
            }
        }
    }

    /**
     * 进入修改产品页面
     *
     * @return
     */
    public String toModifyProduct() {
        String json = "";
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                //管理员没有登录;
                return null;
            }
            if (!QwyUtil.isNullAndEmpty(productId)) {
                product = bean.findProductById(productId);
                product.setRealName(product.getRealName());
                product.setPhone(DESEncrypt.jieMiUsername(product.getPhone()));
                product.setIdcard(DESEncrypt.jieMiIdCard(product.getIdcard()));
                String flag = getRequest().getParameter("flag");
                if (QwyUtil.isNullAndEmpty(flag)) {
                    request.setAttribute("product", product);
                    return "modifyProduct";
                } else {
                    product.setFinancingAmount(QwyUtil.calcNumber(product.getFinancingAmount(), 100, "/").longValue());
                    product.setAtleastMoney(QwyUtil.calcNumber(product.getAtleastMoney(), 100, "/").longValue());
                    request.setAttribute("product", product);
                    return "modifyYYProduct";
                }
            }
        } catch (Exception e) {
            log.error("操作异常: ",e);
        }
        return "modifyProduct";
    }

    /**
     * 预览修改的产品（包括新手）
     *
     * @return
     */
    public String showModifyProduct() {
        try {
            if (!QwyUtil.isNullAndEmpty(product)) {
                Product oldProduct = bean.findProductById(product.getId());
                oldProduct.setTitle(product.getTitle());
                oldProduct.setHdby(product.getHdby());
                oldProduct.setHdlj(product.getHdlj());
                oldProduct.setHkly(product.getHkly());
                oldProduct.setDescription(product.getDescription());
                oldProduct.setZjbz(product.getZjbz());
                oldProduct.setCplxjs(product.getCplxjs());
                getProductImg(oldProduct);
                getRequest().setAttribute("product", oldProduct);
                //类别 默认为0; 0为普通项目,1为:新手专享;
                if ("0".equals(oldProduct.getProductType())) {
                    return "details";
                }
                if ("1".equals(oldProduct.getProductType())) {
                    return "detailsFreshman";
                }
            }
        } catch (Exception e) {
            log.error("操作异常: ",e);
        }
        return "modifyProduct";
    }


    /**
     * 发布预约产品
     *
     * @return
     * @author yks
     * @date 2016-09-24
     */
    /*public String releaseYYProduct() {
        try {
            log.info("发布预约产品");
            request = getRequest();
            if (!QwyUtil.isNullAndEmpty(product)) {
                if (QwyUtil.isNullAndEmpty(product.getId())) {
                    Long newFinancialAmount = QwyUtil.calcNumber(
                            product.getFinancingAmount(), 100, "*").longValue();
                    Long atleastMoney = QwyUtil.calcNumber(
                            product.getAtleastMoney(), 100, "*").longValue();
                    product.setFinancingAmount(newFinancialAmount);
                    product.setAtleastMoney(atleastMoney);
                    product.setEndTime(QwyUtil.fmyyyyMMdd.parse(endTime));
                    product.setFinishTime((QwyUtil.fmyyyyMMdd.parse(finishTime)));
                    if (!QwyUtil.isNullAndEmpty(endTime)) {
                        product.setEndTime(QwyUtil.fmyyyyMMdd.parse(endTime));
                    } else {
                        long eTime = (product.getLcqx() - 2) * 24 * 60 * 60 * 1000 + new Date().getTime();
                        product.setEndTime(new Date(eTime));
                    }
                    if (!QwyUtil.isNullAndEmpty(finishTime)) {
                        product.setFinishTime((QwyUtil.fmyyyyMMdd.parse(finishTime)));
                    } else {
                        long fTime = (product.getLcqx()) * 24 * 60 * 60 * 1000 + new Date().getTime();
                        product.setFinishTime(new Date(fTime));
                    }
                    product.setProductStatus("-3"); //预约排队产品状态
                    if (QwyUtil.isNullAndEmpty(product.getRealName())) {
                        log.info("姓名不正确!");
                        request.setAttribute("isOk", "no");
                        return "releaseCGYY";
                    }
                    if (QwyUtil.isNullAndEmpty(product.getPhone())) {
                        log.info("联系号码不正确!");
                        request.setAttribute("isOk", "no");
                        return "releaseCGYY";
                    }
                    product.setPhone(DESEncrypt.jiaMiUsername(product.getPhone()));
                    if (QwyUtil.isNullAndEmpty(product.getIdcard())) {
                        log.info("身份证号不正确!");
                        request.setAttribute("isOk", "no");
                        return "releaseCGYY";
                    }
                    product.setIdcard(DESEncrypt.jiaMiIdCard(product.getIdcard()));
                    if (QwyUtil.isNullAndEmpty(product.getAddress())) {
                        log.info("联系地址不正确!");
                        request.setAttribute("isOk", "no");
                        return "releaseCGYY";
                    }
                    String id = bean.saveYYProduct(product);//保存预约新产品
                    int productCount = bean.getProductCount(new String[]{"0", "1"});
                    request.setAttribute("productCount", productCount);
                    if (!QwyUtil.isNullAndEmpty(id)) {
                        platformBean.updateTotalMoney(product.getFinancingAmount());
                        request.setAttribute("isOk", "ok");
                        return "releaseCGYY";
                    }
                } else {
                    if (bean.updateYYProduct(product)) {
                        if ("0".equals(product.getProductType())) {
                            bean.modifyContractByProductId(product.getId(), product.getTitle());
                        }
                        request.setAttribute("isOk", "ok");
                        return "modifyYYProduct";
                    } else {
                        request.setAttribute("isOk", "no");
                        return "modifyYYProduct";
                    }
                }

            }
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        request.setAttribute("isOk", "no");
        request.setAttribute("product", product);
        return "release";
    }*/

    /**
     * 发布预约产品
     *
     * @return
     * @author yks
     * @date 2016-09-24
     */
    public String releaseYYProduct() {
        try {
            log.info("发布预约产品");
            request = getRequest();
            if (!QwyUtil.isNullAndEmpty(product)) {
                if (QwyUtil.isNullAndEmpty(product.getId())) {
                    Long newFinancialAmount = QwyUtil.calcNumber(product.getFinancingAmount(), 100, "*").longValue();
                    Long atleastMoney = QwyUtil.calcNumber(product.getAtleastMoney(), 100, "*").longValue();
                    product.setFinancingAmount(newFinancialAmount);
                    product.setAtleastMoney(atleastMoney);
                    /*product.setEndTime(QwyUtil.fmyyyyMMdd.parse(endTime));
                    product.setFinishTime((QwyUtil.fmyyyyMMdd.parse(finishTime)));*/
                    if (!QwyUtil.isNullAndEmpty(endTime)) {
                        product.setEndTime(QwyUtil.fmyyyyMMdd.parse(endTime));
                    } else {
                        long eTime = (product.getLcqx() - 2) * 24 * 60 * 60 * 1000 + new Date().getTime();
                        product.setEndTime(new Date(eTime));
                    }
                    if (!QwyUtil.isNullAndEmpty(finishTime)) {
                        product.setFinishTime((QwyUtil.fmyyyyMMdd.parse(finishTime)));
                    } else {
                        long fTime = (product.getLcqx()) * 24 * 60 * 60 * 1000 + new Date().getTime();
                        product.setFinishTime(new Date(fTime));
                    }
                    product.setProductStatus("-3"); //预约排队产品状态
                    if (QwyUtil.isNullAndEmpty(product.getRealName())) {
                        log.info("姓名不正确!");
                        request.setAttribute("isOk", "no");
                        return "releaseCGYY";
                    }
                    if (QwyUtil.isNullAndEmpty(product.getPhone())) {
                        log.info("联系号码不正确!");
                        request.setAttribute("isOk", "no");
                        return "releaseCGYY";
                    }
                    product.setPhone(DESEncrypt.jiaMiUsername(product.getPhone()));
                    if (QwyUtil.isNullAndEmpty(product.getIdcard())) {
                        log.info("身份证号不正确!");
                        request.setAttribute("isOk", "no");
                        return "releaseCGYY";
                    }
                    product.setIdcard(DESEncrypt.jiaMiIdCard(product.getIdcard()));
                    if (QwyUtil.isNullAndEmpty(product.getAddress())) {
                        log.info("联系地址不正确!");
                        request.setAttribute("isOk", "no");
                        return "releaseCGYY";
                    }
                    
                    if (QwyUtil.isNullAndEmpty(product.getJkyt())) {
						log.info("借款用途不能为空");
						request.setAttribute("isOk", "no");
						return "releaseCGYY";
					}
                    
                    if ("1".equals(product.getProductType())) {
                    	product.setActivity("新手专享");
                    	product.setActivityColor("#f4583f");
                    	
						if (!"4".equals(product.getQxType())) {
							log.info("新手产品要对应新手产品期限类型");
							request.setAttribute("isOk", "no");
							return "releaseCGYY";
						}
						
						if (product.getTitle().indexOf("新手") == -1) {
							log.info("新手产品类型要对应新手产品名称");
							request.setAttribute("isOk", "no");
							return "releaseCGYY";
						}
						
					}else{
						//常规产品
						if (product.getTitle().contains("新手")) {
							log.info("产品名称和产品类别不一致");
							request.setAttribute("isOk", "no");
							return "releaseCGYY";
						}
						
						if ("4".equals(product.getQxType())) {
							log.info("产品类型和投资期限不一致，非新手产品不应该为新手期限类型");
							request.setAttribute("isOk", "no");
							return "releaseCGYY";
						}
						
						if (product.getTitle().contains("货押宝")) {
							if (!"5".equals(product.getInvestType())) {
								log.info("产品名称要和投资类别一致");
								request.setAttribute("isOk", "no");
								return "releaseCGYY";
							}
						}else if(product.getTitle().contains("车贷宝")){
							if(!"6".equals(product.getInvestType())){
								log.info("产品名称要和投资类别一致");
								request.setAttribute("isOk", "no");
								return "releaseCGYY";
							}
						}
					}
                    
                    //车贷宝投资类别  必填字段
                    if ("6".equals(product.getInvestType())) {
						if (QwyUtil.isNullAndEmpty(product.getAge())) {
							log.info("车贷借款人年龄不能为空");
                            request.setAttribute("isOk", "no");
                            return "release";
						}
						
						if (QwyUtil.isNullAndEmpty(product.getEducation())) {
							log.info("借款人学历不能为空");
                            request.setAttribute("isOk", "no");
                            return "release";
						}
						
						if (QwyUtil.isNullAndEmpty(product.getMarriage())) {
							log.info("借款人婚姻状况不能为空");
                            request.setAttribute("isOk", "no");
                            return "release";
						}
						
						if (QwyUtil.isNullAndEmpty(product.getHjAddress())) {
							log.info("借款人户籍地址不能为空");
                            request.setAttribute("isOk", "no");
                            return "release";
						}
						
						if (QwyUtil.isNullAndEmpty(product.getCarDingf())) {
							log.info("丁方车贷审核服务方不能为空");
                            request.setAttribute("isOk", "no");
                            return "release";
						}
						
						if (QwyUtil.isNullAndEmpty(product.getCarDingfAddress())) {
							log.info("丁方车贷审核服务地址不能为空");
                            request.setAttribute("isOk", "no");
                            return "release";
						}
					}
                    
                    String id = bean.saveYYProduct(product);//保存预约新产品
                    int productCount = bean.getProductCount(new String[]{"0", "1"});
                    request.setAttribute("productCount", productCount);
                    if (!QwyUtil.isNullAndEmpty(id)) {
                        //platformBean.updateTotalMoney(product.getFinancingAmount());
                        request.setAttribute("isOk", "ok");
                        return "releaseCGYY";
                    }
                } else {
                    if (bean.updateYYProduct(product)) {
                        if ("0".equals(product.getProductType())) {
                            bean.modifyContractByProductId(product.getId(), product.getTitle());
                        }
                        request.setAttribute("isOk", "ok");
                        return "modifyYYProduct";
                    } else {
                        request.setAttribute("isOk", "no");
                        return "modifyYYProduct";
                    }
                }

            }
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        request.setAttribute("isOk", "no");
        request.setAttribute("product", product);
        return "release";
    }
    
    
    
    
    /**
     * 产看产品记录页面 ，删除预约产品
     *
     * @return
     * @date 2016-09-26
     */
    public String deleteYYproduct() {
        log.info("删除预约产品，产品id:" + productId);
        String json = "";
        if (!QwyUtil.isNullAndEmpty(productId)) {
            try {
                bean.deleteYYProduct(productId);
                json = QwyUtil.getJSONString("success", " ");
                QwyUtil.printJSON(getResponse(), json);
            } catch (Exception e) {
                log.error("操作异常: ",e);
                log.error("系统错误", e);
            }
        }
        return null;
    }




    /**
     * 处理截取字符串 "xxxxxxxNo.1234567890"
     *
     * @param str1 要操作的字符串
     * @param str2
     * @return
     * @throws Exception
     */
    private String subString(String str1, String str2) throws Exception {
        int offset = 0;
        String result = null;
        if (str1.contains(str2)) {
            offset = str1.indexOf(str2);
            result = str1.substring(0, offset);
        }
        log.info("截取后的字符为：" + result);
        return result;
    }

  
    /**
     * 得到预约产品集合
     * @return
     */
    public String getBookingKeywordList(){
    	
    	UsersAdmin users = (UsersAdmin)getRequest().getSession().getAttribute("usersAdmin");
		if(QwyUtil.isNullAndEmpty(users)){
			return "login";
		}
		
		List<BookingKeyword> list = bean.getBookingKwList();
		if (!QwyUtil.isNullAndEmpty(list)) {
			getRequest().setAttribute("list", list);
		}
		
    	return "booking";
    }
    
    /**
     * 添加预约关键字
     * @return
     */
    public String addProduct(){
		String json="";
		try {
			UsersAdmin admin=(UsersAdmin)getRequest().getSession().getAttribute("usersAdmin");
			if(QwyUtil.isNullAndEmpty(admin)){
				json = QwyUtil.getJSONString("err", "管理员未登录");
				QwyUtil.printJSON(getResponse(), json);
				//管理员没有登录;
				return null;
			}
			if(!QwyUtil.isNullAndEmpty(bookingKeyword)){
				if(!QwyUtil.isNullAndEmpty(bookingKeyword.getKeyword())){
					bookingKeyword.setType("0");
					bookingKeyword.setStatus("0");
					bookingKeyword.setInsertTime(new Date());
					bookingKeyword.setUpdateTime(bookingKeyword.getInsertTime());
					String id=bean.addProduct(bookingKeyword);
					if(!QwyUtil.isNullAndEmpty(id)){
						request.setAttribute("message", "添加成功");	
					}else{
						request.setAttribute("message", "添加失败");
					}
				}else{
					request.setAttribute("message", "关键字不能为空");
				}
			}else{
				request.setAttribute("message", "预约产品不能为空");
			}
				
		} catch (IOException e) {
			log.error("操作异常: ",e);
		}
		return "booking";
	}
    
    /**
     * 根据ID 修改状态
     * @return
     */
    public String updateStatusById(){
    	String json = "";
		try {
			UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
			if (QwyUtil.isNullAndEmpty(users)) {
				json = QwyUtil.getJSONString("err", "管理员未登录");
				QwyUtil.printJSON(getResponse(), json);
				//管理员没有登录;
				return null;
			}
			if(bean.updateStatusById(bookingKeyword.getId())){
				json = QwyUtil.getJSONString("ok", "成功");
				QwyUtil.printJSON(getResponse(), json);
				return null;
			}
		} catch (Exception e) {
			log.error("操作异常: ",e);
			log.error("操作异常: ",e);
		}
		json = QwyUtil.getJSONString("error", "失败");
		try {
			QwyUtil.printJSON(getResponse(), json);
			return null;
		} catch (IOException e) {
			log.error("操作异常: ",e);
		}
		return null;	
    }
    
    
    public String iportProductData(){
    	 try {
             String name = QwyUtil.fmyyyyMMddHHmmss3.format(new Date()) + "_productData";
             PageUtil pageUtil = new PageUtil();
             pageUtil.setCurrentPage(currentPage);
             pageUtil.setPageSize(999999);
             HSSFWorkbook wb = new HSSFWorkbook();
             HSSFSheet sheet = wb.createSheet("产品发布历史");
             HSSFRow row = sheet.createRow((int) 1);
             HSSFCellStyle style = wb.createCellStyle();
             style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
             row = sheet.createRow(0);
             row.createCell(0).setCellValue("序号");
             row.createCell(1).setCellValue("发布状态");
             row.createCell(2).setCellValue("产品名称");
             row.createCell(3).setCellValue("借款企业名称");
             row.createCell(4).setCellValue("产品类型");
             row.createCell(5).setCellValue("年化总收益");
             row.createCell(6).setCellValue("奖励收益");
             row.createCell(7).setCellValue("项目总额");
             row.createCell(8).setCellValue("虚拟投资总额");
             row.createCell(9).setCellValue("真实投资总额");
             row.createCell(10).setCellValue("到期时间");
             row.createCell(11).setCellValue("产品状态");
             row.createCell(12).setCellValue("发布时间");
             Product product1 = new Product();
             product1.setTitle(title);
             if (!QwyUtil.isNullAndEmpty(annualEarnings))
                 product1.setAnnualEarnings(Double.parseDouble(annualEarnings));
             if (!QwyUtil.isNullAndEmpty(financingAmount))
                 product1.setFinancingAmount(Long.parseLong(financingAmount));
             
             pageUtil = bean.findProductsPageUtil(pageUtil, product1, finishTime, insertTime, username, productStatus,"finish_time",realName);
             List objectsList = pageUtil.getList();
             
             if (!QwyUtil.isNullAndEmpty(objectsList)) {
             	for (int i = 0; i < objectsList.size(); i++) {
             		row = sheet.createRow((int) i + 1);
 					Object[] objects = (Object[]) objectsList.get(i);
 					row.createCell(1).setCellValue("提交成功");
 					row.createCell(2).setCellValue(objects[1]+"");
 					row.createCell(3).setCellValue(objects[13]+"");

 					if ("0".equals(objects[2])) { //0:车无忧 1:贸易通;2:牛市通;3房盈宝;4基金产品;5货押宝;6车贷宝
 						row.createCell(4).setCellValue("车无忧");
					}else if("1".equals(objects[2])){
						row.createCell(4).setCellValue("贸易通");
					}else if("5".equals(objects[2])){
						row.createCell(4).setCellValue("货押宝");
					}else if("6".equals(objects[2])){
						row.createCell(4).setCellValue("车贷宝");
					}else{
						row.createCell(4).setCellValue("---");
					}
 					
 					row.createCell(5).setCellValue(QwyUtil.isNullAndEmpty(objects[3])?"0":objects[3]+"");
 					row.createCell(6).setCellValue(QwyUtil.isNullAndEmpty(objects[4])?"0":objects[4]+"");
 					row.createCell(7).setCellValue(QwyUtil.isNullAndEmpty(objects[5])?0:Double.parseDouble(objects[5].toString())*0.01);
 					row.createCell(8).setCellValue(QwyUtil.isNullAndEmpty(objects[8])?0:Double.parseDouble(objects[8].toString()));
 					row.createCell(9).setCellValue(QwyUtil.isNullAndEmpty(objects[9])?0:Double.parseDouble(objects[9].toString()));
 					row.createCell(10).setCellValue(objects[6]+"");
 					if ("-2".equals(objects[0])) {//-2：审核不通过 -1:未审核 0:营销中  1:已售罄  2:结算中   3:已还款 
 						row.createCell(11).setCellValue("审核不通过");
					}else if ("-3".equals(objects[0])) {
						row.createCell(11).setCellValue("预约中");
					}else if ("-1".equals(objects[0])) {
						row.createCell(11).setCellValue("未审核");
					}else if ("0".equals(objects[0])) {
						row.createCell(11).setCellValue("营销中");
					}else if ("1".equals(objects[0])) {
						row.createCell(11).setCellValue("已售罄");
					}else if ("2".equals(objects[0])) {
						row.createCell(11).setCellValue("结算中");
					}else if ("3".equals(objects[0])) {
						row.createCell(11).setCellValue("已还款");
					}else{
						row.createCell(11).setCellValue("---");
					}
 					
 					row.createCell(12).setCellValue(objects[7]+"");
 				
 				}
 				
 			}

             String realPath = request.getServletContext().getRealPath("/report/" + name + ".xls");
             FileOutputStream fout = new FileOutputStream(realPath);
             wb.write(fout);
             fout.close();
             response.getWriter().write("/report/" + name + ".xls");
            
         } catch (Exception e) {
             log.error("操作异常: ",e);
         }
         return null;
    	
    	
    }
    
    public String test() {
        return "";
    }

    public Product getProduct() {
        return product;
    }

    public String getFileFileName() {
        return fileFileName;
    }

    public void setFileFileName(String fileFileName) {
        this.fileFileName = fileFileName;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFileContentType() {
        return fileContentType;
    }

    public void setFileContentType(String fileContentType) {
        this.fileContentType = fileContentType;
    }

    public String getRemoveId() {
        return removeId;
    }

    public void setRemoveId(String removeId) {
        this.removeId = removeId;
    }


    public Integer getCurrentPage() {
        return currentPage;
    }


    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }


    public Integer getPageSize() {
        return pageSize;
    }


    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }


    public static synchronized String getSuperPath() {
        File file = new File("");
        String path = new File(file.getAbsolutePath()).getParent();
        path = path.replace('\\', '/');
        path += "/你的webapps的名字/";
        return path;
    }


    public String getInsertTime() {
        return insertTime;
    }


    public void setInsertTime(String insertTime) {
        this.insertTime = insertTime;
    }


    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    public String getProductStatus() {
        return productStatus;
    }


    public void setProductStatus(String productStatus) {
        this.productStatus = productStatus;
    }


    public String getProductId() {
        return productId;
    }


    public void setProductId(String productId) {
        this.productId = productId;
    }


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public String getAnnualEarnings() {
        return annualEarnings;
    }


    public void setAnnualEarnings(String annualEarnings) {
        this.annualEarnings = annualEarnings;
    }


    public String getFinancingAmount() {
        return financingAmount;
    }


    public void setFinancingAmount(String financingAmount) {
        this.financingAmount = financingAmount;
    }

    public String getInputTitle() {
        return inputTitle;
    }

    public void setInputTitle(String inputTitle) {
        this.inputTitle = inputTitle;
    }

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public BookingKeyword getBookingKeyword() {
		return bookingKeyword;
	}

	public void setBookingKeyword(BookingKeyword bookingKeyword) {
		this.bookingKeyword = bookingKeyword;
	}

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
}
