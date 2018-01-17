package com.huoq.admin.guest.action;

import java.io.FileOutputStream;
import java.util.*;

import javax.annotation.Resource;

import com.huoq.admin.guest.bean.InvestorsGuestBean;
import com.huoq.orm.*;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;

import com.huoq.common.action.BaseAction;
import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import org.jfree.util.Log;

/**
 * 后台管理——访客渠道投资统计
 */
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/Guest")
@Results({@Result(name = "invertorsRecord", value = "/Product/Admin/productManager/inverstorRecord.jsp"),
        @Result(name = "platInverstorsMoney", value = "/Product/Admin/productManager/platInverstorsMoney.jsp"),
        @Result(name = "platResUserInverstors", value = "/Product/Admin/operationManager/platUsersIns.jsp"), @Result(name = "err", value = "/Product/Admin/err.jsp"),
        @Result(name = "investGuestChannelCount", value = "/Product/Guest/investGuestChannelCount.jsp"),
        @Result(name = "guestLogin", value = "/Product/guestLogin.jsp"),//访客未登录
        @Result(name = "userInvertors", value = "/Product/Admin/operationManager/userInvertors.jsp")})
public class InvestorsGuestAction extends BaseAction {

    @Resource
    private InvestorsGuestBean investorsGuestBean;

    private Investors investors;
    private Integer currentPage = 1;
    private Integer pageSize = 20;
    private String status = "all";
    private String name = "";
    private String insertTime;
    private String type;
    private String productId;// 产品ID
    private String productStatus;
    private String targetDate;
    private String statisticsJsonData;
    private String productTitle;
    private String realname;

    protected static Logger log = Logger.getLogger(BaseAction.class);

    public String userInvertors() {
        String json = "";
        try {

            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                // 管理员没有登录;
                return null;
            }
            PageUtil<PlatInvestors> pageUtil = new PageUtil<PlatInvestors>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(pageSize);
            StringBuffer url = new StringBuffer();
            url.append(getRequest().getServletContext().getContextPath());
            url.append("/Product/Admin/investors!userInvertors.action?");
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                url.append("&insertTime=");
                url.append(insertTime);
            }

            if (!QwyUtil.isNullAndEmpty(name)) {
                url.append("&name=");
                url.append(name);
            }

            if (!QwyUtil.isNullAndEmpty(realname)) {
                url.append("&realname=");
                url.append(realname);
            }

            pageUtil.setPageUrl(url.toString());

            pageUtil = investorsGuestBean.loadInvestors(name, realname, insertTime, pageUtil);
            getRequest().setAttribute("name", name);
            getRequest().setAttribute("realname", realname);
            getRequest().setAttribute("insertTime", insertTime);
            if (!QwyUtil.isNullAndEmpty(pageUtil)) {
                getRequest().setAttribute("pageUtil", pageUtil);

                getRequest().setAttribute("list", pageUtil.getList());
                return "userInvertors";
            }

        } catch (Exception e) {
            log.error("操作异常: ", e);
            json = QwyUtil.getJSONString("err", "查询记录异常");
        }
        return null;
    }

    /**
     * 投资记录
     *
     * @return
     */
    public String findInvertors() {
        String json = "";
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                // 管理员没有登录;
                return null;
            }
            String superName = "0EA5D6BC23E8EEC78F62546B9F68BABFA96976B775889BA625DB6D764FD0DBD42A1C0F45F85B0DE8";
            if (!superName.equals(users.getUsername())) {
                if (isExistsQX("投资记录", users.getId())) {
                    getRequest().setAttribute("err", "您没有操作该功能的权限!");
                    return "err";
                }
            }
            // 根据状态来加载提现的记录;
            PageUtil<Investors> pageUtil = new PageUtil<Investors>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(pageSize);
            if (!QwyUtil.isNullAndEmpty(productStatus)) {
                if (productStatus.equals("0") || productStatus.equals("1")) {
                    status = "1";
                }
                if (productStatus.equals("3")) {
                    status = "3";
                }
            }
            pageUtil = investorsGuestBean.findInvertorses(pageUtil, productTitle, status, name, insertTime, type, productId);
            StringBuffer url = new StringBuffer();
            url.append(getRequest().getServletContext().getContextPath());
            url.append("/Product/Admin/investors!findInvertors.action?status=" + status);
            if (!QwyUtil.isNullAndEmpty(name)) {
                url.append("&name=");
                url.append(name);
            }
            if (!QwyUtil.isNullAndEmpty(type)) {
                url.append("&type=");
                url.append(type);
            }
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                url.append("&insertTime=");
                url.append(insertTime);
            }
            if (!QwyUtil.isNullAndEmpty(productId)) {
                url.append("&productId=");
                url.append(productId);
            }
            if (!QwyUtil.isNullAndEmpty(productTitle)) {
                url.append("&productTitle=");
                url.append(productTitle);
            }
            pageUtil.setPageUrl(url.toString());
            getRequest().setAttribute("productTitle", productTitle);
            getRequest().setAttribute("pageUtil", pageUtil);
            if (!QwyUtil.isNullAndEmpty(pageUtil)) {
                getRequest().setAttribute("pageUtil", pageUtil);
                return "invertorsRecord";
            }
        } catch (Exception e) {
            log.error("操作异常: ", e);
            json = QwyUtil.getJSONString("err", "查询记录异常");
        }
        return null;
    }

    /**
     * 导出投资记录
     */
    public String iportTable() {
        try {
            // 根据状态来加载提现的记录;
            PageUtil<Investors> pageUtil = new PageUtil<Investors>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(999999999);
            if (!QwyUtil.isNullAndEmpty(productStatus)) {
                if (productStatus.equals("0") || productStatus.equals("1")) {
                    status = "1";
                }
                if (productStatus.equals("3")) {
                    status = "3";
                }
            }
            pageUtil = investorsGuestBean.findInvertorses(pageUtil, productTitle, status, name, insertTime, type, productId);
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("投资记录");
            HSSFRow row = sheet.createRow((int) 0);
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
            HSSFCell cell = row.createCell(0);
            cell.setCellValue("序号");
            cell.setCellStyle(style);
            cell = row.createCell(1);

            cell.setCellValue("用户名");
            cell.setCellStyle(style);
            cell = row.createCell(2);

            cell.setCellValue("投资人姓名");
            cell.setCellStyle(style);
            cell = row.createCell(3);

            cell.setCellValue("产品名称");
            cell.setCellStyle(style);
            cell = row.createCell(4);

            cell.setCellValue("投资天数");
            cell.setCellStyle(style);
            cell = row.createCell(5);

            cell.setCellValue("理财期限");
            cell.setCellStyle(style);
            cell = row.createCell(6);

            cell.setCellValue("剩余天数");
            cell.setCellStyle(style);
            cell = row.createCell(7);

            cell.setCellValue("购买状态");
            cell.setCellStyle(style);
            cell = row.createCell(8);

            cell.setCellValue("购买份数");
            cell.setCellStyle(style);
            cell = row.createCell(9);

            cell.setCellValue("投入金额");
            cell.setCellStyle(style);
            cell = row.createCell(10);

            cell.setCellValue("投资券");
            cell.setCellStyle(style);
            cell = row.createCell(11);

            cell.setCellValue("最终年化收益");
            cell.setCellStyle(style);
            cell = row.createCell(12);

            cell.setCellValue("到期收益");
            cell.setCellStyle(style);
            cell = row.createCell(13);

            cell.setCellValue("支付时间");
            cell.setCellStyle(style);
            cell = row.createCell(14);

            cell.setCellValue("起息时间");
            cell.setCellStyle(style);
            cell = row.createCell(15);

            cell.setCellValue("结算时间");
            cell.setCellStyle(style);
            cell = row.createCell(16);

            cell.setCellValue("项目到期时间");
            cell.setCellStyle(style);
            cell.setCellStyle(style);
            Investors report = null;
            List list = pageUtil.getList();
            for (int i = 0; i < list.size(); i++) {
                row = sheet.createRow((int) i + 1);
                report = (Investors) list.get(i);
                row = sheet.createRow((int) i + 1);
                row.createCell(0).setCellValue((int) i + 1);
                row.createCell(1).setCellValue(report.getUsers() == null ? "" : DESEncrypt.jieMiUsername(report.getUsers().getUsername()));
                row.createCell(2).setCellValue(report.getUsers().getUsersInfo().getRealName() == null ? "" : report.getUsers().getUsersInfo().getRealName());
                row.createCell(3).setCellValue(report.getProduct() == null ? "" : report.getProduct().getTitle());
                row.createCell(4).setCellValue(report.getTzts());
                if (QwyUtil.isNullAndEmpty(report.getProduct())) {
                    row.createCell(5).setCellValue("");
                    row.createCell(6).setCellValue("");
                } else {
                    row.createCell(5).setCellValue(report.getProduct().getLcqx());
                    row.createCell(6).setCellValue(report.getProduct().getTzqx());
                }
                row.createCell(7).setCellValue(report.getTzzt());
                row.createCell(8).setCellValue(report.getCopies());
                row.createCell(9).setCellValue(report.getInMoney() * 0.01);
                row.createCell(10).setCellValue(report.getCoupon() * 0.01);
                row.createCell(11).setCellValue(report.getAnnualEarnings());
                row.createCell(12).setCellValue(report.getExpectEarnings() == null ? 0 : (report.getExpectEarnings() * 0.01));
                row.createCell(13).setCellValue(report.getPayTime() == null ? "" : (QwyUtil.fmyyyyMMddHHmmss.format(report.getPayTime())));
                row.createCell(14).setCellValue(report.getStartTime() == null ? "" : (QwyUtil.fmyyyyMMddHHmmss.format(report.getStartTime())));
                row.createCell(15).setCellValue(report.getClearTime() == null ? "" : (QwyUtil.fmyyyyMMddHHmmss.format(report.getClearTime())));
                row.createCell(16).setCellValue(report.getFinishTime() == null ? "" : (QwyUtil.fmyyyyMMddHHmmss.format(report.getFinishTime())));
            }
            String realPath = request.getServletContext().getRealPath("/report/findInvertors.xls");
            FileOutputStream fout = new FileOutputStream(realPath);
            wb.write(fout);
            fout.close();
            response.getWriter().write("/report/findInvertors.xls");
        } catch (Exception e) {
        	log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 投资统计
     *
     * @return
     */
    public String platInverstors() {
        String json = "";
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                // 管理员没有登录;
                return null;
            }
            String superName = "0EA5D6BC23E8EEC78F62546B9F68BABFA96976B775889BA625DB6D764FD0DBD42A1C0F45F85B0DE8";
            if (!superName.equals(users.getUsername())) {
                if (isExistsQX("每日投资统计", users.getId())) {
                    getRequest().setAttribute("err", "您没有操作该功能的权限!");
                    return "err";
                }
            }
            PageUtil<BackStatsOperateDay> pageUtil = new PageUtil<BackStatsOperateDay>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(pageSize);
            StringBuffer url = new StringBuffer();
            url.append(getRequest().getServletContext().getContextPath());
            url.append("/Product/Admin/investors!platInverstors.action?");
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                url.append("&insertTime=");
                url.append(insertTime);
            }
            pageUtil.setPageUrl(url.toString());
            pageUtil = investorsGuestBean.findInverstorsRecordGroupByDate(pageUtil, insertTime);
            getRequest().setAttribute("insertTime", insertTime);
            if (!QwyUtil.isNullAndEmpty(pageUtil)) {
                getRequest().setAttribute("pageUtil", pageUtil);
                getRequest().setAttribute("list", pageUtil.getList());
                return "platInverstorsMoney";
            }
        } catch (Exception e) {
            log.error("操作异常: ", e);
            json = QwyUtil.getJSONString("err", "查询记录异常");
        }
        return "platInverstorsMoney";
    }

    /**
     * 每日投资统计
     *
     * @return
     */
    public String platInver() {
        String json = "";
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                // 管理员没有登录;
                return null;
            }
            String superName = "0EA5D6BC23E8EEC78F62546B9F68BABFA96976B775889BA625DB6D764FD0DBD42A1C0F45F85B0DE8";
            if (!superName.equals(users.getUsername())) {
                if (isExistsQX("每日投资统计", users.getId())) {
                    getRequest().setAttribute("err", "您没有操作该功能的权限!");
                    return "err";
                }
            }
            PageUtil<Investors> pageUtil = new PageUtil<Investors>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(pageSize);
            StringBuffer url = new StringBuffer();
            url.append(getRequest().getServletContext().getContextPath());
            url.append("/Product/Admin/investors!platInver.action?");
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                url.append("&insertTime=");
                url.append(insertTime);
            }
            pageUtil.setPageUrl(url.toString());
            pageUtil = investorsGuestBean.findInverstorsRecordGroupBy(pageUtil, insertTime);
            getRequest().setAttribute("insertTime", insertTime);
            if (!QwyUtil.isNullAndEmpty(pageUtil)) {
                getRequest().setAttribute("pageUtil", pageUtil);
                getRequest().setAttribute("list", pageUtil.getList());
                return "platInverstorsMoney";
            }
        } catch (Exception e) {
            log.error("操作异常: ", e);
            json = QwyUtil.getJSONString("err", "查询记录异常");
        }
        return "platInverstorsMoney";
    }

    /**
     * 注册人投资统计
     *
     * @return
     */
    public String platResUserInverstors() {
        String json = "";
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                // 管理员没有登录;
                return null;
            }
            String superName = "0EA5D6BC23E8EEC78F62546B9F68BABFA96976B775889BA625DB6D764FD0DBD42A1C0F45F85B0DE8";
            if (!superName.equals(users.getUsername())) {
                if (isExistsQX("注册当日投资统计", users.getId())) {
                    getRequest().setAttribute("err", "您没有操作该功能的权限!");
                    return "err";
                }
            }
            PageUtil<PlatUser> pageUtil = new PageUtil<PlatUser>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(31);
            StringBuffer url = new StringBuffer();
            url.append(getRequest().getServletContext().getContextPath());
            url.append("/Product/Admin/investors!platResUserInverstors.action?");
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                url.append("&insertTime=");
                url.append(insertTime);
            }
            pageUtil.setPageUrl(url.toString());
            pageUtil = investorsGuestBean.findUsersCountByDate(pageUtil, insertTime);
            getRequest().setAttribute("insertTime", insertTime);
            if (!QwyUtil.isNullAndEmpty(pageUtil)) {
                getRequest().setAttribute("pageUtil", pageUtil);
                getRequest().setAttribute("list", pageUtil.getList());
                return "platResUserInverstors";
            }
        } catch (Exception e) {
            log.error("操作异常: ", e);
            json = QwyUtil.getJSONString("err", "查询记录异常");
        }
        return "platResUserInverstors";
    }

    /**
     * 投资时间段分布统计
     *
     * @return
     */
    public String queryInvestorsStatistics() {
        try {
            statisticsJsonData = investorsGuestBean.investorsStatistics(targetDate);
            QwyUtil.printJSON(getResponse(), statisticsJsonData);
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    public String channel = "";
    public String myOrder = "";

    /**
     * 投资渠道统计
     *
     * @return
     */
    public String investChannel() {
        try {
            String st = "";
            String et = "";
            String json = "";
            UsersGuest users = (UsersGuest) getRequest().getSession().getAttribute("usersGuest");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "未登录");
                QwyUtil.printJSON(getResponse(), json);
                // 管理员没有登录;
                return "guestLogin";
            }
            log.info("访客查询投资渠道列表 ,【访客：】" + users.toString());
            if (QwyUtil.isNullAndEmpty(users.getChannelNo())) {
                json = QwyUtil.getJSONString("err", "该访客没有查询渠道投资权限！");
                QwyUtil.printJSON(getResponse(), json);
                // 管理员没有登录;
                return "guestLogin";
            }
            if(QwyUtil.isNullAndEmpty(channel)){
                channel = users.getChannelNo();
            }
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    st = QwyUtil.fmyyyyMMddHHmmss.format(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    et = QwyUtil.fmyyyyMMddHHmmss.format(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
                } else {
                    st = QwyUtil.fmyyyyMMddHHmmss.format(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    et = QwyUtil.fmyyyyMMddHHmmss.format(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            }
            if (!users.getChannelNo().contains(channel)) {
                json = QwyUtil.getJSONString("err", "该访客没有查询投资渠道【"+channel+"】权限！");
                QwyUtil.printJSON(getResponse(), json);
                return null;
            }
            List<HashMap<String, String>> list = investorsGuestBean.investChannel(channel, st, et, QwyUtil.isNullAndEmpty(currentPage) ? 1 : currentPage, 100, null);
            if (!QwyUtil.isNullAndEmpty(myOrder)) {
                Collections.sort(list, new sortChannel());
            }
            List<HashMap<String, String>> doList = new ArrayList<>();
            if (!QwyUtil.isNullAndEmpty(list)) {
                for (HashMap<String, String> obj : list) {
                    if (!QwyUtil.isNullAndEmpty(obj.get("username"))) {
                        String starName = DESEncrypt.jieMiUsername(obj.get("username").toString());
                        obj.put("username", QwyUtil.replaceStringToX(starName));
                    }
                    if (!QwyUtil.isNullAndEmpty(obj.get("real_name"))) {
                        obj.put("real_name", addXXToName(obj.get("real_name").toString()));
                    }
                    doList.add(obj);
                }
            }
            request.setAttribute("investChannelList", doList);
            request.setAttribute("channel", channel);
            request.setAttribute("myOrder", myOrder);
            request.setAttribute("totalCount", doList.size());
        } catch (Exception e) {
        	log.error("操作异常: ", e);
        }
        return "investGuestChannelCount";
    }

    public Investors getInvestors() {
        return investors;
    }

    public void setInvestors(Investors investors) {
        this.investors = investors;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(String insertTime) {
        this.insertTime = insertTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(String productStatus) {
        this.productStatus = productStatus;
    }

    public String getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(String targetDate) {
        this.targetDate = targetDate;
    }

    public String getStatisticsJsonData() {
        return statisticsJsonData;
    }

    public void setStatisticsJsonData(String statisticsJsonData) {
        this.statisticsJsonData = statisticsJsonData;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getMyOrder() {
        return myOrder;
    }

    public void setMyOrder(String myOrder) {
        this.myOrder = myOrder;
    }

    class sortChannel implements Comparator<Object> {

        @Override
        public int compare(Object o1, Object o2) {
            System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
            if (!QwyUtil.isNullAndEmpty(myOrder)) {
                Map<String, Object> map1 = (Map<String, Object>) o1;
                Map<String, Object> map2 = (Map<String, Object>) o2;
                double copies1 = Double.parseDouble(map1.get("copies").toString());
                double copies2 = Double.parseDouble(map2.get("copies").toString());
                double in_money1 = Double.parseDouble(map1.get("in_money").toString());
                double in_money2 = Double.parseDouble(map2.get("in_money").toString());
                double coupon1 = Double.parseDouble(map1.get("coupon").toString());
                double coupon2 = Double.parseDouble(map2.get("coupon").toString());

                if (myOrder.contains("1")) {
                    //升序
                    if (myOrder.contains("title")) {
                        return map1.get("title").toString().compareTo(map2.get("title").toString());
                    } else if (myOrder.contains("copies")) {
                        if (copies1 > copies2)
                            return 1;
                        else if (copies1 == copies2)
                            return 0;
                        else
                            return -1;
                    } else if (myOrder.contains("in_money")) {
                        if (in_money1 > in_money2)
                            return 1;
                        else if (in_money1 == in_money2)
                            return 0;
                        else
                            return -1;
                    } else if (myOrder.contains("coupon")) {
                        if (coupon1 > coupon2)
                            return 1;
                        else if (coupon1 == coupon2)
                            return 0;
                        else
                            return -1;
                    } else if (myOrder.contains("province")) {
                        return map1.get("province").toString().compareTo(map2.get("province").toString());
                    }
                } else {
                    //降序
                    if (myOrder.contains("title")) {
                        return map2.get("title").toString().compareTo(map1.get("title").toString());
                    } else if (myOrder.contains("copies")) {
                        if (copies1 > copies2)
                            return -1;
                        else if (copies1 == copies2)
                            return 0;
                        else
                            return 1;
                    } else if (myOrder.contains("in_money")) {
                        if (in_money1 > in_money2)
                            return -1;
                        else if (in_money1 == in_money2)
                            return 0;
                        else
                            return 1;
                    } else if (myOrder.contains("coupon")) {
                        if (coupon1 > coupon2)
                            return -1;
                        else if (coupon1 == coupon2)
                            return 0;
                        else
                            return 1;
                    } else if (myOrder.contains("province")) {
                        return map2.get("province").toString().compareTo(map1.get("province").toString());
                    }
                }
            }
            return 0;
        }


    }


    /**
     * 把字符串中间用*号代替; 字符串长度不足4位的,后面追加4个*号
     *
     * @param str
     * @return
     */
    private String addXXToName(String str) {
        String newStr = "";
        if (QwyUtil.isNullAndEmpty(str)) {
            Log.info("投资人为null");
            return "";
        }

        if (str.length() > 4) {
            String first = str.substring(0, 3);
            String end = str.substring(str.length() - 4);
            newStr = first + "****" + end;
        } else {
            newStr = str.substring(0, 1) + "**";
        }
        return newStr;
    }
}
