package com.huoq.admin.product.action;

import com.huoq.account.bean.UserInfoBean;
import com.huoq.admin.product.bean.InvestorsBean;
import com.huoq.admin.product.bean.UsersAdminBean;
import com.huoq.common.action.BaseAction;
import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.login.bean.RegisterUserBean;
import com.huoq.orm.*;
import com.huoq.util.ExcelUtil;
import org.apache.poi.hssf.usermodel.*;
import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 后台管理--注册人数;
 *
 * @author qwy
 * @createTime 2015-4-27下午3:51:34
 */
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/Admin")
@Results({@Result(name = "usersStats", value = "/Product/Admin/operationManager/UsersStat.jsp"),
        @Result(name = "platUser", value = "/Product/Admin/operationManager/platUsers.jsp"),
        @Result(name = "loadUserInfo", value = "/Product/Admin/operationManager/userInfo.jsp"),
        @Result(name = "indexUserInfo", value = "/Product/Admin/operationManager/indexuserInfo.jsp"),
        @Result(name = "loadUserInfo2", value = "/Product/Admin/operationManager/userInfo2.jsp"),
        @Result(name = "loadProvince", value = "/Product/Admin/operationManager/provinceStatistics.jsp"),
        @Result(name = "loadCity", value = "/Product/Admin/operationManager/cityStatistics.jsp"),
        @Result(name = "loadSex", value = "/Product/Admin/operationManager/sexStatistics.jsp"),
        @Result(name = "loadAge", value = "/Product/Admin/operationManager/usersAge.jsp"), @Result(name = "err", value = "/Product/Admin/err.jsp"),
        @Result(name = "searchUserInfo", value = "/Product/Admin/operationManager/searchUserInfo.jsp"),
        @Result(name = "loadBank", value = "/Product/Admin/operationManager/bankStatistics.jsp"),
        @Result(name = "loadUserTZTJ", value = "/Product/Admin/operationManager/userTZTJ.jsp")})
public class UserStatAction extends BaseAction {

    @Resource
    UserInfoBean bean;
    @Resource
    RegisterUserBean registerUserBean;
    private Integer currentPage = 1;
    private Integer pageSize = 20;
    private String insertTime;
    private String acinsertTime;
    private String channel;
    private String username;
    private String isbindbank;
    private String islqg;
    private String province;
    private String registPlatform;
    private String level;
    private String inMoney2;
    private String inMoney1;
    private String zcpt;                                                                                            // 注册平台

    private String isBuy;                                                                                           // 是否投资
    private String isZero;                                                                                          // 账户余额是否为0

    @Resource
    private UsersAdminBean usersAdminBean;
    @Resource
    private InvestorsBean investorsBean;

    private String goPage;

    private static String superName = "0EA5D6BC23E8EEC78F62546B9F68BABFA96976B775889BA625DB6D764FD0DBD42A1C0F45F85B0DE8";

    /**
     * 统计注册人数
     *
     * @return
     */
    public String loadUsersStat() {
        List<UsersStat> usersStats;
        String json = "";
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                // 管理员没有登录;
                return null;
            }
            if (!superName.equals(users.getUsername())) {
                if (isExistsQX("平台注册人数", users.getId())) {
                    getRequest().setAttribute("err", "您没有操作该功能的权限!");
                    return "err";
                }
            }
            usersStats = bean.findUsersCount(insertTime);
            String registAllCount = bean.findAllUsersCount(insertTime);
            getRequest().setAttribute("list", usersStats);
            getRequest().setAttribute("registAllCount", registAllCount);
            getRequest().setAttribute("insertTime", insertTime);
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return "usersStats";
    }

    /**
     * 以日期为分组注册人数统计
     *
     * @return
     */
    public String platUser() {
        String json = "";
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                // 管理员没有登录;
                return null;
            }
            if (!superName.equals(users.getUsername())) {
                if (isExistsQX("注册人数日统计", users.getId())) {
                    getRequest().setAttribute("err", "您没有操作该功能的权限!");
                    return "err";
                }
            }
            PageUtil<PlatUser> pageUtil = new PageUtil<PlatUser>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(31);
            StringBuffer url = new StringBuffer();
            url.append(getRequest().getServletContext().getContextPath());
            url.append("/Product/Admin/userStat!platUser.action?");
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                url.append("&insertTime=");
                url.append(insertTime);
            }
            pageUtil.setPageUrl(url.toString());
            pageUtil = bean.findUsersCountByDate(pageUtil, insertTime);
            getRequest().setAttribute("insertTime", insertTime);
            if (!QwyUtil.isNullAndEmpty(pageUtil)) {
                getRequest().setAttribute("pageUtil", pageUtil);
                getRequest().setAttribute("list", pageUtil.getList());
                return "platUser";
            }
        } catch (Exception e) {
            log.error("操作异常: ", e);
            json = QwyUtil.getJSONString("err", "查询记录异常");
        }
        return "platUser";
    }

    /**
     * 首页加载注册的用户
     *
     * @return
     */
    public String indexUserInfo() {
        String json = "";
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                // 管理员没有登录;
                return null;
            }
            if (!superName.equals(users.getUsername())) {
                if (isExistsQX("注册用户信息", users.getId())) {
                    getRequest().setAttribute("err", "您没有操作该功能的权限!");
                    return "err";
                }
            }
            PageUtil<UserInfoList> pageUtil = new PageUtil<UserInfoList>();

            StringBuffer url = new StringBuffer();
            url.append(getRequest().getServletContext().getContextPath());
            url.append("/Product/Admin/userStat!indexUserInfo.action?");

            if (!QwyUtil.isNullAndEmpty(channel)) {
                url.append("&channel=" + channel);
            }
            if (!QwyUtil.isNullAndEmpty(username)) {
                url.append("&username=" + username);
            }
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                url.append("&insertTime=" + insertTime);
            }
            if (!QwyUtil.isNullAndEmpty(acinsertTime)) {
                url.append("&acinsertTime=" + acinsertTime);
            }
            if (!QwyUtil.isNullAndEmpty(isbindbank)) {
                url.append("&isbindbank=" + isbindbank);
            }
            if (!QwyUtil.isNullAndEmpty(level)) {
                url.append("&level=" + level);
            }
            if (!QwyUtil.isNullAndEmpty(inMoney1)) {
                url.append("&inMoney1=" + inMoney1);
            }
            if (!QwyUtil.isNullAndEmpty(inMoney2)) {
                url.append("&inMoney2=" + inMoney2);
            }
            if (!QwyUtil.isNullAndEmpty(goPage)) {
                currentPage = Integer.parseInt(goPage);
            }

            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(pageSize);
            pageUtil.setPageUrl(url.toString());
            pageUtil = bean.findIndexUsersByChannel(pageUtil, channel, username, insertTime, acinsertTime, isbindbank, level, inMoney1, inMoney2);
            getRequest().setAttribute("pageUtil", pageUtil);
            getRequest().setAttribute("channel", channel);
            getRequest().setAttribute("username", username);
            getRequest().setAttribute("level", level);
            getRequest().setAttribute("inMoney1", inMoney1);
            getRequest().setAttribute("inMoney2", inMoney2);
            getRequest().setAttribute("insertTime", insertTime);
            getRequest().setAttribute("isbindbank", isbindbank);
            getRequest().setAttribute("goPage", goPage);
            getRequest().setAttribute("pageSize", pageSize);

        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return "indexUserInfo";
    }

    /**
     * 渠道注册人
     *
     * @return
     */
    public String loadUserInfo() {
        String json = "";
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                // 管理员没有登录;
                return null;
            }
            if (!superName.equals(users.getUsername())) {
                if (isExistsQX("注册用户信息", users.getId())) {
                    getRequest().setAttribute("err", "您没有操作该功能的权限!");
                    return "err";
                }
            }
            PageUtil<UserInfoList> pageUtil = new PageUtil<UserInfoList>();

            StringBuffer url = new StringBuffer();
            url.append(getRequest().getServletContext().getContextPath());
            url.append("/Product/Admin/userStat!loadUserInfo.action?");
            // if(!QwyUtil.isNullAndEmpty(username)){
            // username=new String (username.getBytes("ISO-8859-1"),"UTF-8");
            // }
            if (!QwyUtil.isNullAndEmpty(channel)) {
                url.append("&channel=" + channel);
            }
            if (!QwyUtil.isNullAndEmpty(username)) {
                url.append("&username=" + username);
            }
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                url.append("&insertTime=" + insertTime);
            }
            if (!QwyUtil.isNullAndEmpty(acinsertTime)) {
                url.append("&acinsertTime=" + acinsertTime);
            }
            if (!QwyUtil.isNullAndEmpty(isbindbank)) {
                url.append("&isbindbank=" + isbindbank);
            }
            if (!QwyUtil.isNullAndEmpty(level)) {
                url.append("&level=" + level);
            }
            if (!QwyUtil.isNullAndEmpty(inMoney1)) {
                url.append("&inMoney1=" + inMoney1);
            }
            if (!QwyUtil.isNullAndEmpty(inMoney2)) {
                url.append("&inMoney2=" + inMoney2);
            }
            if (!QwyUtil.isNullAndEmpty(islqg)) {
                url.append("&islqg=" + islqg);
            }
            if (!QwyUtil.isNullAndEmpty(goPage)) {
                // url.append("&goPage="+goPage);
                // if(currentPage < Integer.parseInt(goPage))
                currentPage = Integer.parseInt(goPage);
            }

            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(pageSize);
            pageUtil.setPageUrl(url.toString());
            pageUtil = bean.findUsersByChannel(pageUtil, channel, username, insertTime, acinsertTime, isbindbank, islqg, level, inMoney1, inMoney2);
            getRequest().setAttribute("pageUtil", pageUtil);
            getRequest().setAttribute("channel", channel);
            getRequest().setAttribute("username", username);
            getRequest().setAttribute("level", level);
            getRequest().setAttribute("inMoney1", inMoney1);
            getRequest().setAttribute("inMoney2", inMoney2);
            getRequest().setAttribute("insertTime", insertTime);
            getRequest().setAttribute("isbindbank", isbindbank);
            getRequest().setAttribute("goPage", goPage);
            getRequest().setAttribute("pageSize", pageSize);
            getRequest().setAttribute("islqg", islqg);
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return "loadUserInfo";
    }

    /**
     * 根据省份统计人数
     *
     * @return
     */
    public String loadProvince() {
        String json = "";
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                // 管理员没有登录;
                return null;
            }
            if (!superName.equals(users.getUsername())) {
                if (isExistsQX("地域统计", users.getId())) {
                    getRequest().setAttribute("err", "您没有操作该功能的权限!");
                    return "err";
                }
            }
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
            PageUtil<Region> pageUtil = new PageUtil<Region>();
            if (!QwyUtil.isNullAndEmpty(currentPage)) {
                pageUtil.setCurrentPage(currentPage);
            } else {
                pageUtil.setCurrentPage(1);
            }
            pageUtil.setPageSize(pageSize);

            StringBuffer url = new StringBuffer();
            url.append(getRequest().getServletContext().getContextPath());
            url.append("/Product/Admin/userStat!loadProvince.action?");
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                url.append("&insertTime=");
                url.append(insertTime);
            }
            // 设置分页地址
            pageUtil.setPageUrl(url.toString());
            // 分页查询
            pageUtil = bean.loadProvince(pageUtil, insertTime);
            if (!QwyUtil.isNullAndEmpty(pageUtil)) {
                getRequest().setAttribute("pageUtil", pageUtil);
                getRequest().setAttribute("totalCount", bean.getOthsers(null, null));
                getRequest().setAttribute("list", pageUtil.getList());
                return "loadProvince";
            }
            return "loadProvince";

        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 导出地域统计信息
     */
    public String exportProvince() {
        if (!QwyUtil.isNullAndEmpty(insertTime)) {

        } else {
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            String time = sd.format(date);
        }
        try {
            PageUtil pageUtil = new PageUtil();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(999999);
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("地域统计表");
            HSSFRow row = sheet.createRow((int) 0);
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
            HSSFCell cell = row.createCell(0);
            cell = row.createCell(0);
            cell.setCellValue("序号");
            cell.setCellStyle(style);
            cell = row.createCell(1);
            cell.setCellValue("省份");
            cell.setCellStyle(style);
            cell = row.createCell(2);
            cell.setCellValue("注册人数");
            cell.setCellStyle(style);
            cell = row.createCell(3);
            List<Region> list = bean.loadProvince(pageUtil, insertTime).getList();

            Region region = null;
            for (int i = 0; i < list.size(); i++) {
                row = sheet.createRow((int) i + 1);
                region = (Region) list.get(i);
                row.createCell(0).setCellValue((int) i + 1);// 序号
                row.createCell(1).setCellValue(!QwyUtil.isNullAndEmpty(region.getProvince()) ? region.getProvince() : "");
                row.createCell(2).setCellValue(!QwyUtil.isNullAndEmpty(region.getUsersCount()) ? region.getUsersCount() : "");

            }
            String pathname = QwyUtil.fmyyyyMMddHHmmss3.format(new Date()) + "_find_province.xls";
            String realPath = request.getServletContext().getRealPath("/report/" + pathname);
            log.info("地域统计表表地址：" + realPath);
            FileOutputStream fout = new FileOutputStream(realPath);
            wb.write(fout);
            fout.close();
            response.getWriter().write("/report/" + pathname);
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 根据省份下属城市统计人数
     *
     * @return
     */
    public String loadCity() {
        try {
            PageUtil<Region> pageUtil = new PageUtil<Region>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(pageSize);
            // province=new String(province.getBytes("ISO-8859-1"),"UTF-8");//乱码问题
            StringBuffer url = new StringBuffer();
            url.append(getRequest().getServletContext().getContextPath());
            url.append("/Product/Admin/userStat!loadCity.action?");
            if (!QwyUtil.isNullAndEmpty(province)) {
                url.append("&province=");
                url.append(province);
            }

            pageUtil.setPageUrl(url.toString());
            pageUtil = bean.loadCity(province, pageUtil, insertTime);
            getRequest().setAttribute("list", pageUtil.getList());
            getRequest().setAttribute("province", province);
            getRequest().setAttribute("pageUtil", pageUtil);
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return "loadCity";
    }


    /**
     * 导出地域统计信息
     */
    public String exportCity() {
        if (!QwyUtil.isNullAndEmpty(insertTime)) {

        } else {
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            String time = sd.format(date);
        }
        try {
            PageUtil pageUtil = new PageUtil();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(999999);
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("地域统计表");
            HSSFRow row = sheet.createRow((int) 0);
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
            HSSFCell cell = row.createCell(0);
            cell = row.createCell(0);
            cell.setCellValue("序号");
            cell.setCellStyle(style);
            cell = row.createCell(1);
            cell.setCellValue("省份");
            cell.setCellStyle(style);
            cell = row.createCell(2);
            cell.setCellValue("注册人数");
            cell.setCellStyle(style);
            cell = row.createCell(3);
            List<Region> list = bean.loadCity(province, pageUtil, insertTime).getList();

            Region region = null;
            for (int i = 0; i < list.size(); i++) {
                row = sheet.createRow((int) i + 1);
                region = (Region) list.get(i);
                row.createCell(0).setCellValue((int) i + 1);// 序号
                row.createCell(1).setCellValue(!QwyUtil.isNullAndEmpty(region.getCity()) ? region.getCity() : "");
                row.createCell(2).setCellValue(!QwyUtil.isNullAndEmpty(region.getUsersCount()) ? region.getUsersCount() : "");

            }
            String pathname = QwyUtil.fmyyyyMMddHHmmss3.format(new Date()) + "_find_city.xls";
            String realPath = request.getServletContext().getRealPath("/report/" + pathname);
            log.info("地域统计表表地址：" + realPath);
            FileOutputStream fout = new FileOutputStream(realPath);
            wb.write(fout);
            fout.close();
            response.getWriter().write("/report/" + pathname);
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }


    /**
     * 将归属地、卡类型为空的设值
     *
     * @return
     */
    public String setMobileLocation() {
        String json = "";
        try {
            if (registerUserBean.updateMobileLocation()) {
                json = QwyUtil.getJSONString("ok", "设置成功");
                QwyUtil.printJSON(getResponse(), json);
                return null;
            } else {
                json = QwyUtil.getJSONString("error", "设置失败");
            }
        } catch (Exception e) {
            json = QwyUtil.getJSONString("error", "系统异常");
            log.error("操作异常: ", e);
        }
        try {
            QwyUtil.printJSON(getResponse(), json);
        } catch (IOException e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 统计注册用户的性别
     *
     * @return
     */
    public String loadSex() {
        String json = "";
        try {
            UsersAdmin admin = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(admin)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                // 管理员没有登录;
                return null;
            }
            if (!superName.equals(admin.getUsername())) {
                if (isExistsQX("性别统计", admin.getId())) {
                    getRequest().setAttribute("err", "您没有操作该功能的权限!");
                    return "err";
                }
            }
            StringBuffer url = new StringBuffer();
            url.append(getRequest().getServletContext().getContextPath());
            url.append("/Product/Admin/userStat!loadSex.action?");
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                url.append("&insertTime=");
                url.append(insertTime);
            }
            List<Age> list = bean.loadSex(insertTime);

            request.setAttribute("list", list);
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return "loadSex";
    }

    /**
     * 导出性别统计表信息
     */
    public String exportSex() {
        if (!QwyUtil.isNullAndEmpty(insertTime)) {

        } else {
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            String time = sd.format(date);
        }
        try {
            PageUtil pageUtil = new PageUtil();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(999999);
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("性别统计表");
            HSSFRow row = sheet.createRow((int) 0);
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
            HSSFCell cell = row.createCell(0);
            cell = row.createCell(0);
            cell.setCellValue("性别");
            cell.setCellStyle(style);
            cell = row.createCell(1);
            cell.setCellValue("人数");
            cell.setCellStyle(style);
            cell = row.createCell(2);
            cell.setCellValue("人数比例");
            cell.setCellStyle(style);
            cell = row.createCell(3);
            cell.setCellValue("投资次数");
            cell.setCellStyle(style);
            cell = row.createCell(4);
            cell.setCellValue("投资总额");
            cell.setCellStyle(style);
            cell = row.createCell(5);
            List<Age> list = bean.loadSex(insertTime);

            Age age = null;
            for (int i = 0; i < list.size(); i++) {
                row = sheet.createRow((int) i + 1);
                age = (Age) list.get(i);
                row.createCell(0).setCellValue(!QwyUtil.isNullAndEmpty(age.getSexChina()) ? age.getSexChina() : "");
                row.createCell(1).setCellValue(!QwyUtil.isNullAndEmpty(age.getRsCount()) ? age.getRsCount() : "");
                row.createCell(2).setCellValue(!QwyUtil.isNullAndEmpty(age.getRate()) ? (Double.valueOf(age.getRate()) * 100) + "%" : "");
                row.createCell(3).setCellValue(!QwyUtil.isNullAndEmpty(age.getCsCount()) ? age.getCsCount() : "");
                String jeCount = !QwyUtil.isNullAndEmpty(age.getJeCount()) ? age.getJeCount() : "0.0";
                if (!jeCount.equals("0.0")) {
                    row.createCell(4).setCellValue(0);
                } else {
                    Double aDouble = Double.valueOf(jeCount) / 100;
                    BigDecimal bd = new BigDecimal(aDouble.toString());
                    String s = bd.toPlainString();
                    row.createCell(4).setCellValue(!QwyUtil.isNullAndEmpty(age.getJeCount()) ? s : "");
                }

            }
            String pathname = QwyUtil.fmyyyyMMddHHmmss3.format(new Date()) + "_find_sex.xls";
            String realPath = request.getServletContext().getRealPath("/report/" + pathname);
            log.info("性别统计表表地址：" + realPath);
            FileOutputStream fout = new FileOutputStream(realPath);
            wb.write(fout);
            fout.close();
            response.getWriter().write("/report/" + pathname);
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 用户年龄段分布表
     *
     * @return
     */
    public String loadAge() {
        String json = "";
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                // 管理员没有登录;
                return null;
            }
            if (!superName.equals(users.getUsername())) {
                if (isExistsQX("用户年龄分布表", users.getId())) {
                    getRequest().setAttribute("err", "您没有操作该功能的权限!");
                    return "err";
                }
            }
            StringBuffer url = new StringBuffer();
            url.append(getRequest().getServletContext().getContextPath());
            url.append("/Product/Admin/userStat!loadAge.action?");
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                url.append("&insertTime=");
                url.append(insertTime);
            }
            if (!QwyUtil.isNullAndEmpty(registPlatform)) {
                url.append("&registPlatform=");
                url.append(registPlatform);
            }


            List<Age> ageList = bean.loadAge(registPlatform, insertTime);
            request.setAttribute("list", ageList);
            request.setAttribute("registPlatform", registPlatform);

            return "loadAge";

        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return "loadAge";
    }

    /**
     * 导出用户年龄分布表信息
     */
    public String exportAge() {
        if (!QwyUtil.isNullAndEmpty(insertTime)) {

        } else {
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            String time = sd.format(date);
        }
        try {
            PageUtil pageUtil = new PageUtil();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(999999);
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("用户年龄分布表");
            HSSFRow row = sheet.createRow((int) 0);
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
            HSSFCell cell = row.createCell(0);
            cell = row.createCell(0);
            cell.setCellValue("年龄段");
            cell.setCellStyle(style);
            cell = row.createCell(1);
            cell.setCellValue("人数");
            cell.setCellStyle(style);
            cell = row.createCell(2);
            cell.setCellValue("投资次数");
            cell.setCellStyle(style);
            cell = row.createCell(3);
            cell.setCellValue("投资总额(元)");
            cell.setCellStyle(style);
            cell = row.createCell(4);
            List<Age> list = bean.loadAge(registPlatform, insertTime);

            Age age = null;
            for (int i = 0; i < list.size(); i++) {
                row = sheet.createRow((int) i + 1);
                age = (Age) list.get(i);
                row.createCell(0).setCellValue(!QwyUtil.isNullAndEmpty(age.getAgeCeng()) ? age.getAgeCeng() : "");
                row.createCell(1).setCellValue(!QwyUtil.isNullAndEmpty(age.getRsCount()) ? age.getRsCount() : "");
                row.createCell(2).setCellValue(!QwyUtil.isNullAndEmpty(age.getCsCount()) ? age.getCsCount() : "");
                String price = age.getJeCount();
                double priceValue = Double.parseDouble(price);
                int cents = (int) (priceValue * 0.01);
                String newPrice = Integer.toString(cents);
                row.createCell(3).setCellValue(!QwyUtil.isNullAndEmpty(age.getJeCount()) ? newPrice : "");

            }
            String pathname = QwyUtil.fmyyyyMMddHHmmss3.format(new Date()) + "_find_age.xls";
            String realPath = request.getServletContext().getRealPath("/report/" + pathname);
            log.info("用户年龄分布表表地址：" + realPath);
            FileOutputStream fout = new FileOutputStream(realPath);
            wb.write(fout);
            fout.close();
            response.getWriter().write("/report/" + pathname);
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 查询用户信息
     *
     * @return
     */
    public String searchUserInfo() {
        String json = "";
        List<Users> list = null;
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                // 管理员没有登录;
                return null;
            }
            if (!superName.equals(users.getUsername())) {
                if (isExistsQX("查询用户信息", users.getId())) {
                    getRequest().setAttribute("err", "您没有操作该功能的权限!");
                    return "err";
                }
            }
            if (!QwyUtil.isNullAndEmpty(username)) {
                list = bean.searchUsersInfo(username);
                if (list != null) {
                    getRequest().setAttribute("list", list);
                }
            }
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return "searchUserInfo";
    }

    /**
     * 银行卡数据统计
     *
     * @return
     */

    public String loadbankStatistics() {
        String json = "";
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                // 管理员没有登录;
                return null;
            }
            if (!superName.equals(users.getUsername())) {
                if (isExistsQX("银行卡数据统计", users.getId())) {
                    getRequest().setAttribute("err", "您没有操作该功能的权限!");
                    return "err";
                }
            }
            HttpServletRequest request = getRequest();
            String begin = request.getParameter("begin");
            String end = request.getParameter("end");
            List<Age> bankList = bean.loadbank(insertTime);
            request.setAttribute("list", bankList);
            request.setAttribute("begin", begin);
            request.setAttribute("end", end);
            return "loadBank";

        } catch (Exception e) {

            log.error("操作异常", e);
        }
        return "loadBank";
    }

    /**
     * 导出银行卡数据统计列表
     *
     * @throws Exception
     * @author：zhuhaojie
     * @time：2018年1月23日 下午2:21:52
     * @version
     */
    public void exportExcelList() throws Exception {

        HttpServletRequest request = getRequest();

        String insertTime = request.getParameter("insertTime");
        List<Age> bankList = bean.loadbank(insertTime);
        if (bankList != null && bankList.size() > 0) {

            String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xls";
            response.setContentType(ExcelUtil.EXCEL_STYLE2007);
            response.setHeader("Content-disposition", "attachment;filename=" + fileName);
            ServletOutputStream outputStream = response.getOutputStream(); // 取得输出流
            LinkedHashMap<String, String> fieldMap = new LinkedHashMap<String, String>();

            fieldMap.put("银行", "bankName");
            fieldMap.put("绑定人数", "rsCount");
            fieldMap.put("投资金额(元)", "jeCount");
            fieldMap.put("投资成功次数", "cgCount");
            fieldMap.put("投资失败次数", "sbCount");
            ExcelUtil.exportExcelNew(outputStream, "银行卡数据统计列表", fieldMap, bankList, null);

        }

    }

    /**
     * 获取注册用户信息;根据日期.
     *
     * @return
     */
    public String getRegister() {
        List<Users> listUsers = usersAdminBean.loadUsersByInsertTime(insertTime);
        getRequest().setAttribute("listUsers", listUsers);
        ;
        return "loadUserInfo2";
    }

    /************************************************************************************************************
     * 用户绑定银行卡投资信息统计 条件: 用户名,日期,是否投资,账户余额是否为0 功能:导出报表 用户Id,用户姓名,注册平台,是否投资,投资次数,账户余额
     */

    /**
     * 加载用户投资信息统计
     *
     * @return
     */
    public String loadUserInvestRecords() {
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
                if (isExistsQX("用户投资信息统计", users.getId())) {
                    getRequest().setAttribute("err", "您没有操作该功能的权限!");
                    return "err";
                }
            }
            PageUtil<UserTZTJ> pageUtil = new PageUtil<>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(100);
            StringBuffer url = new StringBuffer();
            url.append(getRequest().getServletContext().getContextPath());
            url.append("/Product/Admin/userStat!loadUserInvestRecords.action?");
            if (!QwyUtil.isNullAndEmpty(username)) {
                url.append("&username=");
                url.append(username);
            }
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                url.append("&insertTime=");
                url.append(insertTime);
            }
            if (!QwyUtil.isNullAndEmpty(isBuy)) {
                url.append("&isBuy=");
                url.append(isBuy);
            }
            if (!QwyUtil.isNullAndEmpty(isZero)) {
                url.append("&isZero=");
                url.append(isZero);
            }
            pageUtil.setPageUrl(url.toString());
            bean.loadUserTZTJ(pageUtil, username, insertTime, isBuy, isZero, zcpt);
            List<Object[]> zcpts = investorsBean.selectRegistPlatform();// 查询注册平台
            getRequest().setAttribute("zcpts", zcpts);
            getRequest().setAttribute("insertTime", insertTime);
            getRequest().setAttribute("username", username);
            getRequest().setAttribute("isBuy", isBuy);
            getRequest().setAttribute("isZero", isZero);
            getRequest().setAttribute("zcpt", zcpt);
            if (!QwyUtil.isNullAndEmpty(pageUtil)) {
                getRequest().setAttribute("pageUtil", pageUtil);
                getRequest().setAttribute("list", pageUtil.getList());
                if (pageUtil.getCount() > 0) getRequest().setAttribute("table", "true");
                return "loadUserTZTJ";
            }
        } catch (Exception e) {
            log.error("系统错误", e);
        }
        return null;
    }

    /**
     * 导出用户投资记录报表
     */
    public String exportUserTZTJ() {
        try {
            PageUtil<UserTZTJ> pageUtil = new PageUtil<UserTZTJ>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(999999);
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("用户投资记录");
            HSSFRow row = sheet.createRow((int) 0);
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
            HSSFCell cell = row.createCell(0);
            cell.setCellValue("序号");
            cell.setCellStyle(style);
            cell = row.createCell(1);
            cell.setCellValue("用户Id");
            cell.setCellStyle(style);
            cell = row.createCell(2);
            cell.setCellValue("用户名");
            cell.setCellStyle(style);
            cell = row.createCell(3);
            cell.setCellValue("注册平台");
            cell.setCellStyle(style);
            cell = row.createCell(4);
            cell.setCellValue("是否投资");
            cell.setCellStyle(style);
            cell = row.createCell(5);
            cell.setCellValue("投资次数");
            cell.setCellStyle(style);
            cell = row.createCell(6);
            cell.setCellValue("账户余额(元)");
            cell.setCellStyle(style);
            cell = row.createCell(7);
            cell.setCellValue("用户注册日期");
            cell.setCellStyle(style);
            UserTZTJ userTZTJ = null;
            List list = bean.loadUserTZTJ(pageUtil, username, insertTime, isBuy, isZero, zcpt).getList();
            for (int i = 0; i < list.size(); i++) {
                row = sheet.createRow((int) i + 1);
                userTZTJ = (UserTZTJ) list.get(i);
                row = sheet.createRow((int) i + 1);
                row.createCell(0).setCellValue((int) i + 1);
                row.createCell(1).setCellValue(!QwyUtil.isNullAndEmpty(userTZTJ.getUserId()) ? userTZTJ.getUserId() : "");
                row.createCell(2).setCellValue(!QwyUtil.isNullAndEmpty(userTZTJ.getUserName()) ? userTZTJ.getUserName() : "");
                row.createCell(3).setCellValue(!QwyUtil.isNullAndEmpty(userTZTJ.getZcpt()) ? userTZTJ.getZcpt() : "");
                row.createCell(4).setCellValue(!QwyUtil.isNullAndEmpty(userTZTJ.getIsBuy()) ? userTZTJ.getIsBuy() : "");
                row.createCell(5).setCellValue(!QwyUtil.isNullAndEmpty(userTZTJ.getInvestCount()) ? userTZTJ.getInvestCount() : "0");
                row.createCell(6).setCellValue(!QwyUtil.isNullAndEmpty(userTZTJ.getAccountBalance()) ? userTZTJ.getAccountBalance() : "0");
                row.createCell(7).setCellValue(!QwyUtil.isNullAndEmpty(userTZTJ.getInsertTime()) ? userTZTJ.getInsertTime() : "");
            }
            String pathname = QwyUtil.fmyyyyMMddHHmmss3.format(new Date()) + "_usertztj_record.xls";
            String realPath = request.getServletContext().getRealPath("/report/" + pathname);
            log.info("用户投资统计记录报表地址：" + realPath);
            FileOutputStream fout = new FileOutputStream(realPath);
            wb.write(fout);
            fout.close();
            response.getWriter().write("/report/" + pathname);
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 导出首页用户信息记录
     */
    public String iportIndexUserInfo() {
        try {
            PageUtil<UserInfoList> pageUtil = new PageUtil<UserInfoList>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(999999);
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("用户信息记录");
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
            cell.setCellValue("所属省份");
            cell.setCellStyle(style);
            cell = row.createCell(4);
            cell.setCellValue("所属城市");
            cell.setCellStyle(style);
            cell = row.createCell(5);
            cell.setCellValue("持卡人好友");
            cell.setCellStyle(style);
            cell = row.createCell(6);
            cell.setCellValue("注册平台");
            cell.setCellStyle(style);
            cell = row.createCell(7);
            cell.setCellValue("是否绑定银行卡");
            cell.setCellStyle(style);
            cell = row.createCell(8);
            cell.setCellValue("投资总额");
            cell.setCellStyle(style);
            cell = row.createCell(9);
            cell.setCellValue("注册时间");
            cell.setCellStyle(style);
            cell = row.createCell(10);
            cell.setCellValue("VIP等级");
            cell.setCellStyle(style);
            cell = row.createCell(11);
            cell.setCellValue("性别");
            cell.setCellStyle(style);
            cell = row.createCell(12);
            cell.setCellValue("年龄");
            cell.setCellStyle(style);
            cell = row.createCell(13);
            cell.setCellValue("生日");
            cell.setCellStyle(style);

            UserInfoList userInfo = null;
            pageUtil = bean.findIndexUsersByChannel(pageUtil, channel, username, insertTime, acinsertTime, isbindbank, level, inMoney1, inMoney2);
            List list = new ArrayList<>();
            if (!QwyUtil.isNullAndEmpty(pageUtil)) {
                list = pageUtil.getList();
            }
            for (int i = 0; i < list.size(); i++) {
                row = sheet.createRow((int) i + 1);
                userInfo = (UserInfoList) list.get(i);
                row = sheet.createRow((int) i + 1);
                row.createCell(0).setCellValue((int) i + 1);
                row.createCell(1).setCellValue(!QwyUtil.isNullAndEmpty(userInfo.getUsername()) ? DESEncrypt.jieMiUsername(userInfo.getUsername()) : "");
                row.createCell(2).setCellValue(!QwyUtil.isNullAndEmpty(userInfo.getRealName()) ? userInfo.getRealName() : "");
                row.createCell(3).setCellValue(!QwyUtil.isNullAndEmpty(userInfo.getProvince()) ? userInfo.getProvince() : "");
                row.createCell(4).setCellValue(!QwyUtil.isNullAndEmpty(userInfo.getCity()) ? userInfo.getCity() : "");
                row.createCell(5).setCellValue(!QwyUtil.isNullAndEmpty(userInfo.getCategory()) ? userInfo.getCategory() : "");
                row.createCell(6).setCellValue(!QwyUtil.isNullAndEmpty(userInfo.getRegistPlatform()) ? userInfo.getRegistPlatform() : "");
                row.createCell(7).setCellValue(!QwyUtil.isNullAndEmpty(userInfo.getIsBindBank()) ? userInfo.getIsBindBank() : "");
                row.createCell(8).setCellValue(!QwyUtil.isNullAndEmpty(userInfo.getInMoney()) ? userInfo.getInMoney() : "");
                row.createCell(9).setCellValue(!QwyUtil.isNullAndEmpty(userInfo.getInsertTime()) ? QwyUtil.fmyyyyMMddHHmmss.format(userInfo.getInsertTime()) : "");
                row.createCell(10).setCellValue(!QwyUtil.isNullAndEmpty(userInfo.getLevel()) ? userInfo.getLevel() : "");
                row.createCell(11).setCellValue(!QwyUtil.isNullAndEmpty(userInfo.getSex()) ? userInfo.getSex() : "");
                row.createCell(12).setCellValue(!QwyUtil.isNullAndEmpty(userInfo.getAge()) ? userInfo.getAge() : "");
                row.createCell(13).setCellValue(!QwyUtil.isNullAndEmpty(userInfo.getBirthday()) ? userInfo.getBirthday() : "");

            }

            String pathname = QwyUtil.fmyyyyMMddHHmmss3.format(new Date()) + "_userIndexinfo.xls";
            String realPath = request.getServletContext().getRealPath("/report/" + pathname);
            log.info("首页充值记录报表地址：" + realPath);
            FileOutputStream fout = new FileOutputStream(realPath);
            wb.write(fout);
            fout.close();
            response.getWriter().write("/report/" + pathname);
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 导出用户信息记录
     */
    public String iportUserInfo() {
        try {
            PageUtil<UserInfoList> pageUtil = new PageUtil<UserInfoList>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(999999);
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("用户信息记录");
            HSSFRow row = sheet.createRow((int) 0);
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
            HSSFCell cell = row.createCell(0);
            cell.setCellValue("序号");
            cell.setCellStyle(style);
            cell = row.createCell(1);
            cell.setCellValue("用户ID");
            cell.setCellStyle(style);
            cell = row.createCell(2);
            cell.setCellValue("用户名");
            cell.setCellStyle(style);
            cell = row.createCell(3);
            cell.setCellValue("姓名");
            cell.setCellStyle(style);
            cell = row.createCell(4);
            cell.setCellValue("性别");
            cell.setCellStyle(style);
            cell = row.createCell(5);
            cell.setCellValue("年龄");
            cell.setCellStyle(style);
            cell = row.createCell(6);
            cell.setCellValue("生日");
            cell.setCellStyle(style);
            cell = row.createCell(7);
            cell.setCellValue("所属省份");
            cell.setCellStyle(style);
            cell = row.createCell(8);
            cell.setCellValue("所属城市");
            cell.setCellStyle(style);
            cell = row.createCell(9);
            cell.setCellValue("电话类型");
            cell.setCellStyle(style);
            cell = row.createCell(10);
            cell.setCellValue("注册时间");
            cell.setCellStyle(style);
            cell = row.createCell(11);
            cell.setCellValue("绑卡时间");
            cell.setCellStyle(style);
            cell = row.createCell(12);
            cell.setCellValue("注册平台");
            cell.setCellStyle(style);
            cell = row.createCell(13);
            cell.setCellValue("是否绑定银行卡");
            cell.setCellStyle(style);
            cell = row.createCell(14);
            cell.setCellValue("首投日期");
            cell.setCellStyle(style);
            cell = row.createCell(15);
            cell.setCellValue("首投产品");
            cell.setCellStyle(style);
            cell = row.createCell(16);
            cell.setCellValue("首投金额");
            cell.setCellStyle(style);
            cell = row.createCell(17);
            cell.setCellValue("投资总额");
            cell.setCellStyle(style);
            cell = row.createCell(18);
            cell.setCellValue("零钱罐金额");
            cell.setCellStyle(style);
            cell = row.createCell(19);
            cell.setCellValue("VIP等级");
            cell.setCellStyle(style);
            cell = row.createCell(20);
            cell.setCellValue("渠道号");
            cell.setCellStyle(style);
            UserInfoList userInfo = null;
            pageUtil = bean.findUsersByChannel(pageUtil, channel, username, insertTime, acinsertTime, isbindbank, islqg, level, inMoney1, inMoney2);
            List list = new ArrayList<>();
            if (!QwyUtil.isNullAndEmpty(pageUtil)) {
                list = pageUtil.getList();
            }
            for (int i = 0; i < list.size(); i++) {
                row = sheet.createRow((int) i + 1);
                userInfo = (UserInfoList) list.get(i);
                row = sheet.createRow((int) i + 1);
                row.createCell(0).setCellValue((int) i + 1);
                row.createCell(1).setCellValue(!QwyUtil.isNullAndEmpty(userInfo.getId()) ? userInfo.getId() : "");
                row.createCell(2).setCellValue(!QwyUtil.isNullAndEmpty(userInfo.getUsername()) ? DESEncrypt.jieMiUsername(userInfo.getUsername()) : "");
                row.createCell(3).setCellValue(!QwyUtil.isNullAndEmpty(userInfo.getRealName()) ? userInfo.getRealName() : "");
                row.createCell(4).setCellValue(!QwyUtil.isNullAndEmpty(userInfo.getSex()) ? userInfo.getSex() : "");
                row.createCell(5).setCellValue(!QwyUtil.isNullAndEmpty(userInfo.getAge()) ? userInfo.getAge() : "");
                row.createCell(6).setCellValue(!QwyUtil.isNullAndEmpty(userInfo.getBirthday()) ? userInfo.getBirthday() : "");
                row.createCell(7).setCellValue(!QwyUtil.isNullAndEmpty(userInfo.getProvince()) ? userInfo.getProvince() : "");
                row.createCell(8).setCellValue(!QwyUtil.isNullAndEmpty(userInfo.getCity()) ? userInfo.getCity() : "");
                row.createCell(9).setCellValue(!QwyUtil.isNullAndEmpty(userInfo.getCardType()) ? userInfo.getCardType() : "");
                row.createCell(10).setCellValue(!QwyUtil.isNullAndEmpty(userInfo.getInsertTime()) ? QwyUtil.fmyyyyMMddHHmmss.format(userInfo.getInsertTime()) : "");
                row.createCell(11).setCellValue(!QwyUtil.isNullAndEmpty(userInfo.getAcinsertTime()) ? QwyUtil.fmyyyyMMddHHmmss.format(userInfo.getAcinsertTime()) : "");
                row.createCell(12).setCellValue(!QwyUtil.isNullAndEmpty(userInfo.getRegistPlatform()) ? userInfo.getRegistPlatform() : "");
                row.createCell(13).setCellValue(!QwyUtil.isNullAndEmpty(userInfo.getIsBindBank()) ? userInfo.getIsBindBank().equals("1") ? "是" : "否" : "");
                row.createCell(14).setCellValue(!QwyUtil.isNullAndEmpty(userInfo.getPayTime()) ? QwyUtil.fmyyyyMMddHHmmss.format(userInfo.getPayTime()) : "");
                row.createCell(15).setCellValue(!QwyUtil.isNullAndEmpty(userInfo.getTitle()) ? userInfo.getTitle() : "");
                row.createCell(16).setCellValue(!QwyUtil.isNullAndEmpty(userInfo.getInMoney1()) ? userInfo.getInMoney1() : "");
                row.createCell(17).setCellValue(!QwyUtil.isNullAndEmpty(userInfo.getInMoney()) ? userInfo.getInMoney() : "");
                row.createCell(18).setCellValue(!QwyUtil.isNullAndEmpty(userInfo.getCoinPurseFundsRecordMoney()) ? userInfo.getCoinPurseFundsRecordMoney() : "");
                row.createCell(19).setCellValue(!QwyUtil.isNullAndEmpty(userInfo.getLevel()) ? userInfo.getLevel() : "");
                row.createCell(20).setCellValue(!QwyUtil.isNullAndEmpty(userInfo.getRegistChannel()) ? userInfo.getRegistChannel() : "");

            }

            String pathname = QwyUtil.fmyyyyMMddHHmmss3.format(new Date()) + "_userinfo.xls";
            String realPath = request.getServletContext().getRealPath("/report/" + pathname);
            log.info("充值记录报表地址：" + realPath);
            FileOutputStream fout = new FileOutputStream(realPath);
            wb.write(fout);
            fout.close();
            response.getWriter().write("/report/" + pathname);
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    public String getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(String insertTime) {
        this.insertTime = insertTime;
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

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIsbindbank() {
        return isbindbank;
    }

    public void setIsbindbank(String isbindbank) {
        this.isbindbank = isbindbank;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getRegistPlatform() {
        return registPlatform;
    }

    public void setRegistPlatform(String registPlatform) {
        this.registPlatform = registPlatform;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getInMoney2() {
        return inMoney2;
    }

    public void setInMoney2(String inMoney2) {
        this.inMoney2 = inMoney2;
    }

    public String getInMoney1() {
        return inMoney1;
    }

    public void setInMoney1(String inMoney1) {
        this.inMoney1 = inMoney1;
    }

    public String getGoPage() {
        return goPage;
    }

    public void setGoPage(String goPage) {
        this.goPage = goPage;
    }

    public String getIsBuy() {
        return isBuy;
    }

    public void setIsBuy(String isBuy) {
        this.isBuy = isBuy;
    }

    public String getIsZero() {
        return isZero;
    }

    public void setIsZero(String isZero) {
        this.isZero = isZero;
    }

    public String getZcpt() {
        return zcpt;
    }

    public void setZcpt(String zcpt) {
        this.zcpt = zcpt;
    }

    public String getAcinsertTime() {
        return acinsertTime;
    }

    public void setAcinsertTime(String acinsertTime) {
        this.acinsertTime = acinsertTime;
    }

    public String getIslqg() {
        return islqg;
    }

    public void setIslqg(String islqg) {
        this.islqg = islqg;
    }
}
