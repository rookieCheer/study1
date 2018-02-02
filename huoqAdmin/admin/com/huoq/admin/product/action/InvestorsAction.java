package com.huoq.admin.product.action;

import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

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

import com.huoq.admin.product.bean.InvestorsBean;
import com.huoq.common.action.BaseAction;
import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.BackStatsOperateDay;
import com.huoq.orm.Investors;
import com.huoq.orm.PlatInvestors;
import com.huoq.orm.PlatUser;
import com.huoq.orm.UsersAdmin;
import com.huoq.orm.InvestChannelExcelData;

/**
 * 后台管理——投资记录
 */
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/Admin")
@Results({ @Result(name = "invertorsRecord", value = "/Product/Admin/productManager/inverstorRecord.jsp"),
           @Result(name = "platInverstorsMoney", value = "/Product/Admin/productManager/platInverstorsMoney.jsp"),
           @Result(name = "platResUserInverstors", value = "/Product/Admin/operationManager/platUsersIns.jsp"), @Result(name = "err", value = "/Product/Admin/err.jsp"),
           @Result(name = "investChannelCount", value = "/Product/Admin/operationManager/investChannelCount.jsp"),
           @Result(name = "userInvertors", value = "/Product/Admin/operationManager/userInvertors.jsp"),
           @Result(name = "registPlatformList", value = "/Product/Admin/operationManager/registPlatformList.jsp") })
public class InvestorsAction extends BaseAction {

    @Resource
    private InvestorsBean   investorsBean;

    private Investors       investors;
    private Integer         currentPage = 1;
    private Integer         pageSize    = 20;
    private String          status      = "all";
    private String          name        = "";
    private String          insertTime;
    private String          type;
    private String          productId;                                       // 产品ID
    private String          productStatus;
    private String          targetDate;
    private String          statisticsJsonData;
    private String          productTitle;
    private String          realname;

    private String          registChannel;                                   // 注册渠道
    private String          registPlatform;                                  // '0:web端注册; 1:Android移动端; 2:IOS移动端;
                                                                             // 3:微信注册;
    private String          firstInvest;

    protected static Logger log         = Logger.getLogger(BaseAction.class);

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

            pageUtil = investorsBean.loadInvestors(name, realname, insertTime, pageUtil);
            getRequest().setAttribute("name", name);
            getRequest().setAttribute("realname", realname);
            getRequest().setAttribute("insertTime", insertTime);
            if (!QwyUtil.isNullAndEmpty(pageUtil)) {
                getRequest().setAttribute("pageUtil", pageUtil);
                getRequest().setAttribute("table", "1");
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
     * 导出用户投资记录
     */
    public String exoprtUserInvertors() {
        try {
            PageUtil<PlatInvestors> pageUtil = new PageUtil<PlatInvestors>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(9999999);
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
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("用户投资统计(根据投资本金从高到低显示)");
            HSSFRow row = sheet.createRow((int) 1);
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
            row = sheet.createRow(0);
            row.createCell(0).setCellValue("序号");
            row.createCell(1).setCellValue("用户id");
            row.createCell(2).setCellValue("用户名");
            row.createCell(3).setCellValue("真实姓名");
            row.createCell(4).setCellValue("注册日期");
            row.createCell(5).setCellValue("邦卡日期");
            row.createCell(6).setCellValue("首投日期");
            row.createCell(7).setCellValue("投资总额（元）");
            row.createCell(8).setCellValue("现存资金（元）");
            row.createCell(9).setCellValue("在贷金额（不含零钱罐）");
            row.createCell(10).setCellValue("零钱罐金额");
            row.createCell(11).setCellValue("账户余额");
            row.createCell(12).setCellValue("投资券金额（元）");
            row.createCell(13).setCellValue("红包金额（元）");
            row.createCell(14).setCellValue("邀请好友人数");
            row.createCell(15).setCellValue("好友总金额");
            pageUtil = investorsBean.loadInvestors(name, realname, insertTime, pageUtil);
            List list = pageUtil.getList();
            PlatInvestors pla = null;
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
            for (int i = 0; i < list.size(); i++) {
                row = sheet.createRow((int) i + 1);
                pla = (PlatInvestors) list.get(i);
                row.createCell(0).setCellValue((int) i + 1);
                row.createCell(1).setCellValue(pla.getId() + "");
                row.createCell(2).setCellValue(DESEncrypt.jieMiUsername(pla.getUsername()));
                row.createCell(3).setCellValue(pla.getReal_name());
                String insertTime = null;
                String bandCardTime = null;
                String fristBuyTime = null;
                if (!QwyUtil.isNullAndEmpty(pla.getInsertTime())) {
                    insertTime = sd.format(pla.getInsertTime());
                }
                if (!QwyUtil.isNullAndEmpty(pla.getBandCardTime())) {
                    bandCardTime = sd.format(pla.getBandCardTime());
                }
                if (!QwyUtil.isNullAndEmpty(pla.getFristBuyTime())) {
                    fristBuyTime = sd.format(pla.getFristBuyTime());
                }
                row.createCell(4).setCellValue(!QwyUtil.isNullAndEmpty(insertTime) ? insertTime : "");
                row.createCell(5).setCellValue(!QwyUtil.isNullAndEmpty(bandCardTime) ? bandCardTime : "");
                row.createCell(6).setCellValue(!QwyUtil.isNullAndEmpty(fristBuyTime) ? fristBuyTime : "");
                row.createCell(7).setCellValue(pla.getCopies());
                row.createCell(8).setCellValue(pla.getAllMoney());
                row.createCell(9).setCellValue(pla.getBuyInMoney());
                row.createCell(10).setCellValue(pla.getCoinPurseMoney());
                row.createCell(11).setCellValue(pla.getLeftMoney());
                row.createCell(12).setCellValue(pla.getCoupon());
                row.createCell(13).setCellValue(pla.getHongbao());
                row.createCell(14).setCellValue(pla.getFriendNumber());
                row.createCell(15).setCellValue(pla.getFriendMoney());
            }
            String realPath = request.getServletContext().getRealPath("/report/userInvertors.xls");
            FileOutputStream fout = new FileOutputStream(realPath);
            wb.write(fout);
            fout.close();
            // response.getWriter().write("/report/userInvertors.xls");
            String json = QwyUtil.getJSONString("ok", "/report/userInvertors.xls");
            QwyUtil.printJSON(response, json);
        } catch (Exception e) {
            log.error("操作异常: ", e);
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
            pageUtil = investorsBean.findInvertorses(pageUtil, productTitle, status, name, insertTime);
            StringBuffer url = new StringBuffer();
            url.append(getRequest().getServletContext().getContextPath());
            url.append("/Product/Admin/investors!findInvertors.action?status=" + status);
            if (!QwyUtil.isNullAndEmpty(name)) {
                url.append("&name=");
                url.append(name);
            }
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                url.append("&insertTime=");
                url.append(insertTime);
            }
            if (!QwyUtil.isNullAndEmpty(productTitle)) {
                url.append("&productTitle=");
                url.append(productTitle);
            }
            pageUtil.setPageUrl(url.toString());
            getRequest().setAttribute("productTitle", productTitle);
            getRequest().setAttribute("pageUtil", pageUtil.getList());
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
            pageUtil = investorsBean.findInvertorses(pageUtil, productTitle, status, name, insertTime);
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
            cell.setCellValue("姓名");
            cell.setCellStyle(style);

            cell = row.createCell(3);
            cell.setCellValue("渠道");
            cell.setCellStyle(style);

            cell = row.createCell(4);
            cell.setCellValue("是否首投");
            cell.setCellStyle(style);

            cell = row.createCell(5);
            cell.setCellValue("产品名称");
            cell.setCellStyle(style);

            cell = row.createCell(6);
            cell.setCellValue("投资天数");
            cell.setCellStyle(style);

            cell = row.createCell(7);
            cell.setCellValue("理财期限");
            cell.setCellStyle(style);

            cell = row.createCell(8);
            cell.setCellValue("剩余天数");
            cell.setCellStyle(style);

            cell = row.createCell(9);
            cell.setCellValue("购买状态");
            cell.setCellStyle(style);

            cell = row.createCell(10);
            cell.setCellValue("购买份数");
            cell.setCellStyle(style);

            cell = row.createCell(11);
            cell.setCellValue("投入本金");
            cell.setCellStyle(style);

            cell = row.createCell(12);
            cell.setCellValue("本金收益");
            cell.setCellStyle(style);

            cell = row.createCell(13);
            cell.setCellValue("投资券");
            cell.setCellStyle(style);

            cell = row.createCell(14);
            cell.setCellValue("投资券来源");
            cell.setCellStyle(style);

            cell = row.createCell(15);
            cell.setCellValue("投资券收益");
            cell.setCellStyle(style);

            cell = row.createCell(16);
            cell.setCellValue("红包金额");
            cell.setCellStyle(style);

            cell = row.createCell(17);
            cell.setCellValue("红包来源");
            cell.setCellStyle(style);

            cell = row.createCell(18);
            cell.setCellValue("最终年化收益");
            cell.setCellStyle(style);

            cell = row.createCell(19);
            cell.setCellValue("到期总收益");
            cell.setCellStyle(style);

            cell = row.createCell(20);
            cell.setCellValue("投资时间");
            cell.setCellStyle(style);

            cell = row.createCell(21);
            cell.setCellValue("起息时间");
            cell.setCellStyle(style);

            cell = row.createCell(22);
            cell.setCellValue("结算时间");
            cell.setCellStyle(style);

            cell = row.createCell(23);
            cell.setCellValue("项目到期时间");
            cell.setCellStyle(style);

            Investors report = null;
            List list = pageUtil.getList();
            for (int i = 0; i < list.size(); i++) {
                row = sheet.createRow((int) i + 1);
                report = (Investors) list.get(i);
                row = sheet.createRow((int) i + 1);
                row.createCell(0).setCellValue((int) i + 1);
                row.createCell(1).setCellValue(report.getUsername()== null ? "" : DESEncrypt.jieMiUsername(report.getUsername()));
                row.createCell(2).setCellValue(report.getRealname() == null ? "" : report.getRealname());
                row.createCell(3).setCellValue(report.getRegistChannel() == null ? "" : report.getRegistChannel());
                row.createCell(4).setCellValue(report.getIsFirstInvt() == null ? "" : report.getIsFirstInvt());
                row.createCell(5).setCellValue(report.getTitle() == null ? "" : report.getTitle());
                row.createCell(6).setCellValue(report.getTits() != null ? Integer.valueOf(report.getTits()+""):0 );
                row.createCell(7).setCellValue(report.getLcqx()!=null? Integer.valueOf(report.getLcqx()+""):0 );
                row.createCell(8).setCellValue(report.getTzqx()!=null? Integer.valueOf(report.getTzqx()+""):0 );
                row.createCell(9).setCellValue(report.getTzzt()==null?"":report.getTzzt());
                row.createCell(10).setCellValue(report.getCopies()!=null ? Long.valueOf(report.getLcqx()+""):0 );
                row.createCell(11).setCellValue(report.getInMoney()!=null ?Double.valueOf(report.getInMoney()+""):0 );
                row.createCell(12).setCellValue(report.getExpectEarnings() == null ? 0 : (QwyUtil.jieQuFa(report.getExpectEarnings(),2)));
                row.createCell(13).setCellValue(report.getCoupon());
                row.createCell(14).setCellValue(report.getInvestSource()== null ? "" :report.getInvestSource());
                row.createCell(15).setCellValue(report.getCouponShouyi()!= null ? Double.valueOf(report.getCouponShouyi()+""):0.0);
                row.createCell(16).setCellValue(report.getHongbao()!= null ? Double.valueOf(report.getHongbao()+""):0);
                row.createCell(17).setCellValue(report.getRedPackageSource()== null ? "" :report.getRedPackageSource());
                row.createCell(18).setCellValue(report.getAnnualEarnings() == null ? 0.0 :report.getAnnualEarnings());
                row.createCell(19).setCellValue(report.getFinalEarnings() == null ? 0.00: (QwyUtil.jieQuFa (report.getFinalEarnings(),2)));
                row.createCell(20).setCellValue(report.getPayTime() == null ? "" : (QwyUtil.fmyyyyMMddHHmmss.format(report.getPayTime())));
                row.createCell(21).setCellValue(report.getStartTime() == null ? "" : (QwyUtil.fmyyyyMMddHHmmss.format(report.getStartTime())));
                row.createCell(22).setCellValue(report.getClearTime() == null ? "" : (QwyUtil.fmyyyyMMddHHmmss.format(report.getClearTime())));
                row.createCell(23).setCellValue(report.getFinishTime() == null ? "" : (QwyUtil.fmyyyyMMddHHmmss.format(report.getFinishTime())));
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
            pageUtil = investorsBean.findInverstorsRecordGroupByDate(pageUtil, insertTime);
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
            pageUtil = investorsBean.findInverstorsRecordGroupBy(pageUtil, insertTime);
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
            pageUtil = investorsBean.findUsersCountByDate(pageUtil, insertTime);
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
            statisticsJsonData = investorsBean.investorsStatistics(targetDate);
            QwyUtil.printJSON(getResponse(), statisticsJsonData);
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    public String channel = "200";
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
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    st = QwyUtil.fmyyyyMMddHHmmss.format(QwyUtil.fmMMddyyyy.parse(time[0] + " 00:00:00"));
                    et = QwyUtil.fmyyyyMMddHHmmss.format(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
                } else {
                    st = QwyUtil.fmyyyyMMddHHmmss.format(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    et = QwyUtil.fmyyyyMMddHHmmss.format(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            }
            List<Object[]> list = null;
            if (!QwyUtil.isNullAndEmpty(firstInvest) && firstInvest.equals("1")) {
                // 筛选首投
                int excel = 2;// 1.导出表格2.查询展示
                list = investorsBean.investChannelScreen(channel, st, et, QwyUtil.isNullAndEmpty(currentPage) ? 1 : currentPage, 100, null, registPlatform, firstInvest, excel);
            } else {
                list = investorsBean.investChannel(channel, st, et, QwyUtil.isNullAndEmpty(currentPage) ? 1 : currentPage, 100, null, registPlatform);
            }

            if (!QwyUtil.isNullAndEmpty(list)) {
                getRequest().setAttribute("list", list);
                getRequest().setAttribute("table", "1");
            }

            if (!QwyUtil.isNullAndEmpty(myOrder)) {
                Collections.sort(list, new sortChannel());
            }
            // 注册平台查询
            List<Object[]> registPlatformlist = investorsBean.selectRegistPlatform();
            request.setAttribute("registPlatformlist", registPlatformlist);
            request.setAttribute("investChannelList", list);
            request.setAttribute("registPlatform", registPlatform);
            request.setAttribute("channel", channel);
            request.setAttribute("myOrder", myOrder);

        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return "investChannelCount";
    }

    /**
     * 平台渠道统计
     *
     * @return
     */
    public String registPlatformStatistics() {
        try {
            String st = "";
            String et = "";
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    st = QwyUtil.fmyyyyMMddHHmmss.format(QwyUtil.fmMMddyyyy.parse(time[0] + " 00:00:00"));
                    et = QwyUtil.fmyyyyMMddHHmmss.format(QwyUtil.fmMMddyyyy.parse(time[1] + " 23:59:59"));
                } else {
                    st = QwyUtil.fmyyyyMMddHHmmss.format(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    et = QwyUtil.fmyyyyMMddHHmmss.format(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            }
            // 平台渠道统计查询
            List list = investorsBean.registPlatform(registPlatform, registChannel, st, et, QwyUtil.isNullAndEmpty(currentPage) ? 1 : currentPage, 100, 1);

            for (int i = 0; i < list.size(); i++) {
                HashMap<String, Object> users = (HashMap<String, Object>) list.get(i);
                String registPlatform2 = users.get("regist_platform").toString();
                // '0:web端注册; 1:Android移动端; 2:IOS移动端; 3:微信注册;'
                if (registPlatform2.equals("0")) {
                    users.put("regist_platform", "web端注册");
                } else if (registPlatform2.equals("1")) {
                    users.put("regist_platform", "Android移动端");
                } else if (registPlatform2.equals("2")) {
                    users.put("regist_platform", "IOS移动端");
                } else if (registPlatform2.equals("3")) {
                    users.put("regist_platform", "微信注册");
                } else {
                    users.put("regist_platform", registPlatform2);
                }

            }

            // "registPlatform"
            if (!QwyUtil.isNullAndEmpty(myOrder)) {
                Collections.sort(list, new sortChannel());
            }
            request.setAttribute("investChannelList", list);
            request.setAttribute("channel", channel);
            request.setAttribute("listSize", list.size());
            request.setAttribute("myOrder", myOrder);
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return "registPlatformList";
    }

    /**
     * 渠道投资统计excel报表导出
     *
     * @return
     */
    public String exportInvestChannelTable() {
        try {
            PageUtil<InvestChannelExcelData> pageUtil = new PageUtil<InvestChannelExcelData>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(999999);
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("渠道" + registChannel + "投资统计");
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
            cell.setCellValue("姓名_投资次数");
            cell.setCellStyle(style);
            cell = row.createCell(3);
            cell.setCellValue("产品");
            cell.setCellStyle(style);
            cell = row.createCell(4);
            cell.setCellValue("注册时间");
            cell.setCellStyle(style);
            cell = row.createCell(5);
            cell.setCellValue("支付时间");
            cell.setCellStyle(style);
            cell = row.createCell(6);
            cell.setCellValue("总投资(元)");
            cell.setCellStyle(style);
            cell = row.createCell(7);
            cell.setCellValue("投资本金(元)");
            cell.setCellStyle(style);
            cell = row.createCell(8);
            cell.setCellValue("投资券金额(元)");
            cell.setCellStyle(style);
            cell = row.createCell(9);
            cell.setCellValue("省份");
            cell.setCellStyle(style);
            cell = row.createCell(10);
            cell.setCellValue("渠道号");
            cell.setCellStyle(style);
            cell = row.createCell(11);
            cell.setCellValue("注册平台");
            cell.setCellStyle(style);
            String st = "";
            String et = "";
            String json = "";
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    st = QwyUtil.fmyyyyMMddHHmmss.format(QwyUtil.fmMMddyyyy.parse(time[0] + " 00:00:00"));
                    et = QwyUtil.fmyyyyMMddHHmmss.format(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
                } else {
                    st = QwyUtil.fmyyyyMMddHHmmss.format(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    et = QwyUtil.fmyyyyMMddHHmmss.format(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            }
            List<Object[]> data = null;
            if (!QwyUtil.isNullAndEmpty(firstInvest) && firstInvest.equals("1")) {
                // 筛选首投
                int excel = 1;// 1.导出表格2.查询展示
                data = investorsBean.investChannelScreen(channel, st, et, QwyUtil.isNullAndEmpty(currentPage) ? 1 : currentPage, 100, null, registPlatform, firstInvest, excel);
            } else {
                data = investorsBean.findInvestChannel(channel, st, et, QwyUtil.isNullAndEmpty(currentPage) ? 1 : currentPage, 100, null, registPlatform);
            }
            if (QwyUtil.isNullAndEmpty(data)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                return null;
            }
            InvestChannelExcelData excelData = null;
            List<InvestChannelExcelData> list = investorsBean.toInvestChannelExcelData(data);
            for (int i = 0; i < list.size(); i++) {
                row = sheet.createRow((int) i + 1);
                excelData = list.get(i);
                row = sheet.createRow((int) i + 1);
                row.createCell(0).setCellValue((int) i + 1);
                row.createCell(1).setCellValue(excelData.getUsername());
                row.createCell(2).setCellValue(excelData.getNameInvestCount());
                row.createCell(3).setCellValue(excelData.getProduct());
                row.createCell(4).setCellValue(excelData.getInsertTime());
                row.createCell(5).setCellValue(excelData.getPayTime());
                row.createCell(6).setCellValue(excelData.getTotalInvestment());
                row.createCell(7).setCellValue(excelData.getCapital());
                row.createCell(8).setCellValue(excelData.getCouponMoney());
                row.createCell(9).setCellValue(excelData.getProvince());
                row.createCell(10).setCellValue(excelData.getChannelNo());
                row.createCell(11).setCellValue(excelData.getRegistPlatform());
            }
            String pathname = QwyUtil.fmyyyyMMddHHmmss3.format(new Date()) + "_invest_channel.xls";
            String realPath = request.getServletContext().getRealPath("/report/" + pathname);
            log.info("渠道投资统计excel报表地址：" + realPath);
            FileOutputStream fout = new FileOutputStream(realPath);
            wb.write(fout);
            fout.close();
            response.getWriter().write("/report/" + pathname);
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
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

    public String getRegistChannel() {
        return registChannel;
    }

    public void setRegistChannel(String registChannel) {
        this.registChannel = registChannel;
    }

    public String getRegistPlatform() {
        return registPlatform;
    }

    public void setRegistPlatform(String registPlatform) {
        this.registPlatform = registPlatform;
    }

    public String getFirstInvest() {
        return firstInvest;
    }

    public void setFirstInvest(String firstInvest) {
        this.firstInvest = firstInvest;
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
                    // 升序
                    if (myOrder.contains("title")) {
                        return map1.get("title").toString().compareTo(map2.get("title").toString());
                    } else if (myOrder.contains("copies")) {
                        if (copies1 > copies2) return 1;
                        else if (copies1 == copies2) return 0;
                        else return -1;
                    } else if (myOrder.contains("in_money")) {
                        if (in_money1 > in_money2) return 1;
                        else if (in_money1 == in_money2) return 0;
                        else return -1;
                    } else if (myOrder.contains("coupon")) {
                        if (coupon1 > coupon2) return 1;
                        else if (coupon1 == coupon2) return 0;
                        else return -1;
                    } else if (myOrder.contains("province")) {
                        return map1.get("province").toString().compareTo(map2.get("province").toString());
                    }
                } else {
                    // 降序
                    if (myOrder.contains("title")) {
                        return map2.get("title").toString().compareTo(map1.get("title").toString());
                    } else if (myOrder.contains("copies")) {
                        if (copies1 > copies2) return -1;
                        else if (copies1 == copies2) return 0;
                        else return 1;
                    } else if (myOrder.contains("in_money")) {
                        if (in_money1 > in_money2) return -1;
                        else if (in_money1 == in_money2) return 0;
                        else return 1;
                    } else if (myOrder.contains("coupon")) {
                        if (coupon1 > coupon2) return -1;
                        else if (coupon1 == coupon2) return 0;
                        else return 1;
                    } else if (myOrder.contains("province")) {
                        return map2.get("province").toString().compareTo(map1.get("province").toString());
                    }
                }
            }
            return 0;
        }

    }

}
