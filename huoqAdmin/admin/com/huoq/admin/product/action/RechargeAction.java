package com.huoq.admin.product.action;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.annotation.Resource;
import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.DateUtils;

import net.sf.jasperreports.engine.JasperPrint;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;
import com.huoq.account.bean.MyAccountBean;
import com.huoq.account.bean.UserRechargeBean;
import com.huoq.admin.product.bean.InvestorsBean;
import com.huoq.admin.product.bean.RechargeBean;
import com.huoq.common.action.BaseAction;
import com.huoq.common.bean.YiBaoPayBean;
import com.huoq.common.util.ObjectUtil;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.login.bean.RegisterUserBean;
import com.huoq.orm.BackStatsOperateDay;
import com.huoq.orm.CapitalRecord;
import com.huoq.orm.CzRecord;
import com.huoq.orm.CzRecordCompany;
import com.huoq.orm.DateMoney;
import com.huoq.orm.FullScaleCompanyMessage;
import com.huoq.orm.InnerCompanyMessage;
import com.huoq.orm.Product;
import com.huoq.orm.RootTxRecord;
import com.huoq.orm.UserCzTx;
import com.huoq.orm.UserInfoList;
import com.huoq.orm.Users;
import com.huoq.orm.UsersAdmin;
import com.huoq.orm.WeekLeftMoney;
import com.huoq.product.bean.ProductBean;

/**
 * 后台管理--充值
 * 
 * @author qwy
 * @createTime 2015-4-27下午3:51:34
 */
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/Admin")
// 发布产品页面
@Results({ @Result(name = "czrecord", value = "/Product/Admin/fundsManager/czRecord.jsp"), @Result(name = "platCzMoney", value = "/Product/Admin/fundsManager/platCzMoney.jsp"),
           @Result(name = "platTxMoney", value = "/Product/Admin/fundsManager/platTxMoney.jsp"),
           @Result(name = "platLeftMoney", value = "/Product/Admin/fundsManager/platLeftMoney.jsp"),
           @Result(name = "loadUserCzTx", value = "/Product/Admin/operationManager/userCzTx.jsp"),
           @Result(name = "weekLeftMoney", value = "/Product/Admin/fundsManager/weekLeftMoney.jsp"),
           @Result(name = "capitalRecord", value = "/Product/Admin/operationManager/capitalRecord.jsp"),
           @Result(name = "rootTxRecord", value = "/Product/Admin/fundsManager/rootTxRecord.jsp"), @Result(name = "err", value = "/Product/Admin/err.jsp"),

           // 今日满标企业详情
           @Result(name = "todayFullScaleUserDetail", value = "/Product/Admin/functionManager/todayFullScaleUserDetail.jsp"),
           // 未审核提现总额
           @Result(name = "uncheckedUserDetail", value = "/Product/Admin/functionManager/uncheckedUserDetail.jsp"),
           // 平台总交易额
           @Result(name = "platformDealToalUserDetail", value = "/Product/Admin/functionManager/platformDealToalUserDetail.jsp"),

           @Result(name = "weekRemainMoney", value = "/Product/Admin/fundsManager/weekRemainMoney.jsp"),
           @Result(name = "czrecordCompany", value = "/Product/Admin/fundsManager/czRecordCompany.jsp") })
public class RechargeAction extends BaseAction {

    private String           username;
    private double           rechargeMoney = 0;
    @Resource
    private RechargeBean     bean;
    @Resource
    private UserRechargeBean userRechargeBean;
    @Resource
    RegisterUserBean         registerUserBean;
    @Resource
    MyAccountBean            accountBean;
    @Resource
    YiBaoPayBean             yiBaoPayBean;

    /**
     * 注入产品表service
     */
    @Resource
    private ProductBean      productService;
    /**
     * 注入投资表service
     */
    @Resource
    private InvestorsBean    investorsService;

    private Integer          currentPage   = 1;
    private Integer          pageSize      = 50;
    private String           status        = "all";
    private String           name          = "";
    private String           insertTime;
    private String           requestId;
    private String           registPlatform;       // 注册
    private String           cause;                // 后台充值原因
    private String           money;
    private String           note;
    private String           type;

    /**
     * 管理员后台充值
     * 
     * @return
     */
    public String rechargeMoney() {
        String json = "";
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                // 管理员没有登录;
                return null;
            }
            Users us = bean.getUsersByUsername(username);
            if (QwyUtil.isNullAndEmpty(us)) {
                // 没有找到这个用户;
                json = QwyUtil.getJSONString("err", "用户名不存在");
                QwyUtil.printJSON(getResponse(), json);
                return null;
            }
            String superName = "0EA5D6BC23E8EEC78F62546B9F68BABFA96976B775889BA625DB6D764FD0DBD42A1C0F45F85B0DE8";
            if (!superName.equals(users.getUsername())) {
                // 没有找到这个用户;
                json = QwyUtil.getJSONString("err", "您没有充值权限!");
                QwyUtil.printJSON(getResponse(), json);
                return null;
            }
            String note = "管理员手动为用户充值,管理员编号: " + users.getId();
            CzRecord cz = userRechargeBean.addCzRecord2(us.getId(), rechargeMoney * 100, "", "", "", "内部人员充值", note, cause);
            boolean isSend = bean.usreRecharge(us.getId(), rechargeMoney * 100, "cz", "管理员后台充值", note);
            if (isSend) {
                cz.setStatus("1");
                log.info("充值成功___");
                // 发送成功;
                json = QwyUtil.getJSONString("ok", "充值成功");
            } else {
                cz.setStatus("2");
                json = QwyUtil.getJSONString("err", "充值失败");
            }
            cz.setCheckTime(new Date());
            userRechargeBean.saveOrUpdate(cz);
        } catch (Exception e) {
            log.error("操作异常: ", e);
            json = QwyUtil.getJSONString("err", "充值异常");
        }
        try {
            QwyUtil.printJSON(getResponse(), json);
        } catch (IOException e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 充值记录
     * 
     * @return
     */
    public String rechargeMoneyRecord() {
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
                if (isExistsQX("充值记录", users.getId())) {
                    getRequest().setAttribute("err", "您没有操作该功能的权限!");
                    return "err";
                }
            }
            // 根据状态来加载提现的记录;
            PageUtil<CzRecord> pageUtil = new PageUtil<CzRecord>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(pageSize);
            StringBuffer url = new StringBuffer();
            url.append(getRequest().getServletContext().getContextPath());
            url.append("/Product/Admin/recharge!rechargeMoneyRecord.action?name=" + name + "&status=" + status);
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                url.append("&insertTime=");
                url.append(insertTime);
            }
            pageUtil.setPageUrl(url.toString());
            pageUtil = bean.loadCzRecord(pageUtil, status, name, insertTime);
            if (!QwyUtil.isNullAndEmpty(pageUtil)) {
                getRequest().setAttribute("pageUtil", pageUtil);
                getRequest().setAttribute("name", name);
                getRequest().setAttribute("insertTime", insertTime);
                getRequest().setAttribute("czRecordList", pageUtil.getList());
                return "czrecord";
            }
        } catch (Exception e) {
            log.error("操作异常: ", e);
            json = QwyUtil.getJSONString("err", "查询记录异常");
        }
        return null;
    }

    /**
     * 充值统计
     * 
     * @return
     */
    public String platCzMoney() {
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
                if (isExistsQX("充值记录统计", users.getId())) {
                    getRequest().setAttribute("err", "您没有操作该功能的权限!");
                    return "err";
                }
            }
            PageUtil<DateMoney> pageUtil = new PageUtil<DateMoney>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(31);
            StringBuffer url = new StringBuffer();
            url.append(getRequest().getServletContext().getContextPath());
            url.append("/Product/Admin/recharge!platCzMoney.action?");
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                url.append("&insertTime=");
                url.append(insertTime);
            }
            pageUtil.setPageUrl(url.toString());
            pageUtil = bean.findCzRecordGroupByDate(pageUtil, insertTime);
            getRequest().setAttribute("insertTime", insertTime);
            if (!QwyUtil.isNullAndEmpty(pageUtil)) {
                getRequest().setAttribute("pageUtil", pageUtil);
                getRequest().setAttribute("list", pageUtil.getList());
                return "platCzMoney";
            }
        } catch (Exception e) {
            log.error("操作异常: ", e);
            json = QwyUtil.getJSONString("err", "查询记录异常");
        }
        return "platCzMoney";
    }

    /**
     * 提现统计
     * 
     * @return
     */
    public String platTxMoney() {
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
                if (isExistsQX("提现记录统计", users.getId())) {
                    getRequest().setAttribute("err", "您没有操作该功能的权限!");
                    return "err";
                }
            }
            PageUtil<DateMoney> pageUtil = new PageUtil<DateMoney>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(31);
            StringBuffer url = new StringBuffer();
            url.append(getRequest().getServletContext().getContextPath());
            url.append("/Product/Admin/recharge!platTxMoney.action?");
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                url.append("&insertTime=");
                url.append(insertTime);
            }
            pageUtil.setPageUrl(url.toString());
            pageUtil = bean.findTxRecordGroupByDate(pageUtil, insertTime);
            getRequest().setAttribute("insertTime", insertTime);
            if (!QwyUtil.isNullAndEmpty(pageUtil)) {
                getRequest().setAttribute("pageUtil", pageUtil);
                getRequest().setAttribute("list", pageUtil.getList());
                return "platTxMoney";
            }
        } catch (Exception e) {
            log.error("操作异常: ", e);
            json = QwyUtil.getJSONString("err", "查询记录异常");
        }
        return "platTxMoney";
    }

    /**
     * 资金流水
     * 
     * @return
     */
    public String CapitalRecord() {
        String json = "";
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                // 管理员没有登录;
                return null;
            }
            // String
            // superName="0EA5D6BC23E8EEC78F62546B9F68BABFA96976B775889BA625DB6D764FD0DBD42A1C0F45F85B0DE8";
            // if(!superName.equals(users.getUsername())){
            // if(isExistsQX("资金流水", users.getId())){
            // getRequest().setAttribute("err", "您没有操作该功能的权限!");
            // return "err";
            // }
            // }
            PageUtil<CapitalRecord> pageUtil = new PageUtil<CapitalRecord>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(31);
            StringBuffer url = new StringBuffer();
            url.append(getRequest().getServletContext().getContextPath());
            url.append("/Product/Admin/recharge!CapitalRecord.action?");
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                url.append("&insertTime=");
                url.append(insertTime);
            }
            pageUtil.setPageUrl(url.toString());
            pageUtil = bean.findCapitalRecordGroupByDate(pageUtil, insertTime);
            getRequest().setAttribute("insertTime", insertTime);
            if (!QwyUtil.isNullAndEmpty(pageUtil)) {
                getRequest().setAttribute("pageUtil", pageUtil);
                getRequest().setAttribute("tj", bean.tjCapital(insertTime));
                return "capitalRecord";
            }
        } catch (Exception e) {
            log.error("操作异常: ", e);
            json = QwyUtil.getJSONString("err", "查询记录异常");
        }
        return "capitalRecord";
    }

    /**
     * 提现查询
     * 
     * @return
     */
    public String TxMoneyArrive() {
        String json = "";
        try {
            UsersAdmin ua = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(ua)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                // 管理员没有登录;
                return null;
            }
            // 得到用户数据
            Users users = registerUserBean.getUsersByUsername(name);

            json = yiBaoPayBean.withdrawQuery(requestId);
            QwyUtil.printJSON(getResponse(), json);
            return null;
        } catch (Exception e) {
            log.error("操作异常: ", e);
            json = QwyUtil.getJSONString("err", "查询记录异常");
        }
        return null;
    }

    /**
     * 当前平台可用余额
     * 
     * @return
     */
    public String platLeftMoney() {
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
                if (isExistsQX("平台可用余额", users.getId())) {
                    getRequest().setAttribute("err", "您没有操作该功能的权限!");
                    return "err";
                }
            }
            String leftMoney = bean.findAllLeftMoney();
            getRequest().setAttribute("leftMoney", leftMoney);
            String inShiftMoney = bean.findInShiftMoney();
            getRequest().setAttribute("inShiftMoney", inShiftMoney);
            String outShiftMoney = bean.findOutShiftMoney();
            getRequest().setAttribute("outShiftMoney", outShiftMoney);
            double shiftMoney = Double.valueOf(inShiftMoney) + Double.valueOf(outShiftMoney);
            getRequest().setAttribute("shiftMoney", shiftMoney);
        } catch (Exception e) {
            log.error("操作异常: ", e);
            json = QwyUtil.getJSONString("err", "查询记录异常");
        }
        return "platLeftMoney";
    }

    /**
     * 一周可用余额
     * 
     * @return
     */
    public String weekLeftMoney() {
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
                if (isExistsQX("一周不可动金额", users.getId())) {
                    getRequest().setAttribute("err", "您没有操作该功能的权限!");
                    return "err";
                }
            }
            List<WeekLeftMoney> list = bean.findWeekLeftMoneys();
            // String allCzMoney=bean.allCzRecord();
            // String allDay=bean.allDay();
            // String leftMoney=bean.findAllLeftMoney();
            // String allLeftMoney=bean.allLeftMoney();
            // String txMaxCount=bean.txMaxCount();
            // getRequest().setAttribute("allDay", allDay);
            // getRequest().setAttribute("allCzMoney", allCzMoney);
            // getRequest().setAttribute("leftMoney", leftMoney);
            // getRequest().setAttribute("allLeftMoney", allLeftMoney);
            // getRequest().setAttribute("txMaxCount", txMaxCount);
            getRequest().setAttribute("list", list);
        } catch (Exception e) {
            log.error("操作异常: ", e);
            json = QwyUtil.getJSONString("err", "查询记录异常");
        }
        return "weekLeftMoney";
    }

    /**
     * 一周预留资金
     * 
     * @return
     */
    public String weekRemainMoney() {
        String json = "";
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                // 管理员没有登录;
                return null;
            }
            /*
             * String superName = "0EA5D6BC23E8EEC78F62546B9F68BABFA96976B775889BA625DB6D764FD0DBD42A1C0F45F85B0DE8"; if
             * (!superName.equals(users.getUsername())) { if (isExistsQX("预留金额", users.getId())) {
             * getRequest().setAttribute("err", "您没有操作该功能的权限!"); return "err"; } }
             */
            List<WeekLeftMoney> list = bean.findWeekRemainMoneys();
            // String allCzMoney=bean.allCzRecord();
            // String allDay=bean.allDay();
            // String leftMoney=bean.findAllLeftMoney();
            // String allLeftMoney=bean.allLeftMoney();
            // String txMaxCount=bean.txMaxCount();
            // getRequest().setAttribute("allDay", allDay);
            // getRequest().setAttribute("allCzMoney", allCzMoney);
            // getRequest().setAttribute("leftMoney", leftMoney);
            // getRequest().setAttribute("allLeftMoney", allLeftMoney);
            // getRequest().setAttribute("txMaxCount", txMaxCount);
            getRequest().setAttribute("list", list);
        } catch (Exception e) {
            log.error("操作异常: ", e);
            json = QwyUtil.getJSONString("err", "查询记录异常");
        }
        return "weekRemainMoney";
    }

    /**
     * 导出喵币报表
     * 
     * @return
     */
    public String exportWeekLeftMoney() {

        try {
            List<WeekLeftMoney> list = bean.findWeekLeftMoneys();
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("一周不可动金额");
            HSSFRow row = sheet.createRow((int) 0);
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式

            HSSFCell cell = row.createCell(0);
            cell.setCellValue("日期");
            cell.setCellStyle(style);
            cell = row.createCell(1);
            cell.setCellValue("预期充值金额手续费");
            cell.setCellStyle(style);
            cell = row.createCell(2);
            cell.setCellValue("预期还本付息金额");
            cell.setCellStyle(style);
            cell = row.createCell(3);
            cell.setCellValue("账户余额总和");
            cell.setCellStyle(style);
            cell = row.createCell(4);
            cell.setCellValue("预计提现手续费");
            cell.setCellStyle(style);
            cell = row.createCell(5);

            cell.setCellValue("合计");
            cell.setCellStyle(style);
            WeekLeftMoney report = null;
            for (int i = 0; i < list.size(); i++) {
                row = sheet.createRow((int) i + 1);
                report = (WeekLeftMoney) list.get(i);
                row.createCell(0).setCellValue(report.getDate());
                // row.createCell(1).setCellValue(report.getInsertTime());
                row.createCell(1).setCellValue(QwyUtil.calcNumber(report.getYjczsxf(), 1, "/", 2) + "");
                row.createCell(2).setCellValue(QwyUtil.calcNumber(report.getDqhbfxje(), 1, "/", 2) + "");
                row.createCell(3).setCellValue(QwyUtil.calcNumber(report.getAllAccountLeftMoney(), 1, "/", 2) + "");
                row.createCell(4).setCellValue(QwyUtil.calcNumber(report.getYjtxsxf(), 1, "/", 2) + "");
                row.createCell(5).setCellValue(QwyUtil.calcNumber(report.getSum(), 1, "/", 2) + "");

            }
            String realPath = request.getServletContext().getRealPath("/report/weekLeftMoney.xls");
            FileOutputStream fout = new FileOutputStream(realPath);
            wb.write(fout);
            fout.close();
            response.getWriter().write("/report/weekLeftMoney.xls");

        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 充值提现记录
     * 
     * @return
     */
    public String loadUserCzTx() {
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
                if (isExistsQX("充值提现数据报表", users.getId())) {
                    getRequest().setAttribute("err", "您没有操作该功能的权限!");
                    return "err";
                }
            }
            PageUtil<BackStatsOperateDay> pageUtil = new PageUtil<BackStatsOperateDay>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(pageSize);
            StringBuffer url = new StringBuffer();
            url.append(getRequest().getServletContext().getContextPath());
            url.append("/Product/Admin/recharge!loadUserCzTx.action?");
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                url.append("&insertTime=");
                url.append(insertTime);
            }

            if (!QwyUtil.isNullAndEmpty(registPlatform)) {
                url.append("&registPlatform=");
                url.append(registPlatform);
            }

            pageUtil.setPageUrl(url.toString());
            pageUtil = bean.findUserCzTx(pageUtil, insertTime, registPlatform);
            getRequest().setAttribute("insertTime", insertTime);
            getRequest().setAttribute("registPlatform", registPlatform);
            if (!QwyUtil.isNullAndEmpty(pageUtil)) {
                getRequest().setAttribute("pageUtil", pageUtil);
                if (pageUtil.getCount() > 0) getRequest().setAttribute("table", "1");
                getRequest().setAttribute("tj", totalAllCzTx());
                return "loadUserCzTx";
            }
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return "loadUsersConvert";
    }

    /**
     * 全部合计
     * 
     * @return
     */
    private BackStatsOperateDay totalAllCzTx() {
        BackStatsOperateDay returnBackStatsOperateDay = new BackStatsOperateDay();
        try {

            for (Map<String, Object> map : bean.findCzTxTotal(registPlatform, insertTime)) {
                returnBackStatsOperateDay = (BackStatsOperateDay) ObjectUtil.mapToObject(map, returnBackStatsOperateDay);
            }
            // 人均投资
            returnBackStatsOperateDay.setAvgInvestCentSum();
            // 新增二次投资率
            returnBackStatsOperateDay.setNewTwoInvestRate();
            // 复投
            returnBackStatsOperateDay.setReInvestRate();
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return returnBackStatsOperateDay;
    }

    /**
     * 导出产品表格
     */
    public String iportTable() {
        try {
            PageUtil<UserCzTx> pageUtil = new PageUtil<UserCzTx>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(999999);
            bean.findUserCzTx(pageUtil, insertTime, registPlatform);
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("充值提现数据报表");
            HSSFRow row = sheet.createRow((int) 0);
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式

            row = sheet.createRow(0);
            row.createCell(0).setCellValue("日期");
            row.createCell(1).setCellValue("充值金额");
            row.createCell(2).setCellValue("充值次数");
            row.createCell(3).setCellValue("提现金额");
            row.createCell(4).setCellValue("提现次数");
            row.createCell(5).setCellValue("成功提现金额");
            row.createCell(6).setCellValue("成功提现次数");

            row.createCell(7).setCellValue("当日成功提现金额");
            row.createCell(8).setCellValue("当日成功提现次数");
            row.createCell(9).setCellValue("当天提现没到账的金额");
            row.createCell(10).setCellValue("当天提现未到账的次数");
            row.createCell(11).setCellValue("昨天提现今天到账金额");
            row.createCell(12).setCellValue("昨天提现今天到账次数");
            row.createCell(13).setCellValue("前天及更早提现但今天到账金额");
            row.createCell(14).setCellValue("前天及更早提现但今天到账次数");

            row.createCell(15).setCellValue("非今天提现但今天到账金额");
            row.createCell(16).setCellValue("非今天提现但今天到账次数");

            BackStatsOperateDay backStatsOperateDay = totalAllCzTx();
            row = sheet.createRow(1);
            row.createCell(0).setCellValue("合计");
            row.createCell(1).setCellValue(backStatsOperateDay.getRechargeCentSum() / 100);
            row.createCell(2).setCellValue(backStatsOperateDay.getRechargeCount());
            row.createCell(3).setCellValue(backStatsOperateDay.getWithdrawCentSum() / 100);
            row.createCell(4).setCellValue(backStatsOperateDay.getWithdrawCount());
            row.createCell(5).setCellValue(backStatsOperateDay.getSuccessWithdrawCentSum() / 100);
            row.createCell(6).setCellValue(backStatsOperateDay.getSuccessWithdrawCount());

            row.createCell(7).setCellValue(backStatsOperateDay.getSameDaySuccessWithdrawCentSum() / 100);
            row.createCell(8).setCellValue(backStatsOperateDay.getSameDaySuccessWithdrawCount());
            row.createCell(9).setCellValue(backStatsOperateDay.getTodayWithdrawNoArrivalCentSum() / 100);
            row.createCell(10).setCellValue(backStatsOperateDay.getTodayWithdrawNoArrivalCount());
            row.createCell(11).setCellValue(backStatsOperateDay.getYesterdayWithdrawTodayArrivalCentSum() / 100);
            row.createCell(12).setCellValue(backStatsOperateDay.getYesterdayWithdrawTodayArrivalCount());
            row.createCell(13).setCellValue((backStatsOperateDay.getNotSameDayWithdrawTodayArrivalCentSum() - backStatsOperateDay.getYesterdayWithdrawTodayArrivalCentSum()) / 100);
            row.createCell(14).setCellValue(backStatsOperateDay.getNotSameDayWithdrawTodayArrivalCount() - backStatsOperateDay.getYesterdayWithdrawTodayArrivalCount());
            row.createCell(15).setCellValue(backStatsOperateDay.getNotSameDayWithdrawTodayArrivalCentSum() / 100);
            row.createCell(16).setCellValue(backStatsOperateDay.getNotSameDayWithdrawTodayArrivalCount());
            List list = pageUtil.getList();
            BackStatsOperateDay report = null;
            for (int i = 0; i < list.size(); i++) {
                row = sheet.createRow((int) i + 2);
                report = (BackStatsOperateDay) list.get(i);
                row.createCell(0).setCellValue(report.getDates());
                row.createCell(1).setCellValue(report.getRechargeCentSum() / 100);
                row.createCell(2).setCellValue(report.getRechargeCount());
                row.createCell(3).setCellValue(report.getWithdrawCentSum() / 100);
                row.createCell(4).setCellValue(report.getWithdrawCount());
                row.createCell(5).setCellValue(report.getSuccessWithdrawCentSum() / 100);
                row.createCell(6).setCellValue(report.getSuccessWithdrawCount());

                row.createCell(7).setCellValue(report.getSameDaySuccessWithdrawCentSum() / 100);
                row.createCell(8).setCellValue(report.getSameDaySuccessWithdrawCount());
                row.createCell(9).setCellValue(report.getTodayWithdrawNoArrivalCentSum() / 100);
                row.createCell(10).setCellValue(report.getTodayWithdrawNoArrivalCount());
                row.createCell(11).setCellValue(report.getYesterdayWithdrawTodayArrivalCentSum() / 100);
                row.createCell(12).setCellValue(report.getYesterdayWithdrawTodayArrivalCount());
                row.createCell(13).setCellValue((report.getNotSameDayWithdrawTodayArrivalCentSum() - report.getYesterdayWithdrawTodayArrivalCentSum()) / 100);
                row.createCell(14).setCellValue(report.getNotSameDayWithdrawTodayArrivalCount() - report.getYesterdayWithdrawTodayArrivalCount());

                row.createCell(15).setCellValue(report.getNotSameDayWithdrawTodayArrivalCentSum() / 100);
                row.createCell(16).setCellValue(report.getNotSameDayWithdrawTodayArrivalCount());
            }

            String realPath = request.getServletContext().getRealPath("/report/cztxRecord.xls");
            FileOutputStream fout = new FileOutputStream(realPath);
            wb.write(fout);
            fout.close();
            response.getWriter().write("/report/cztxRecord.xls");

        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 导出充值记录表
     */
    public String iportCzTable1() {
        List<JasperPrint> list = null;
        String pathname = QwyUtil.fmyyyyMMddHHmmss3.format(new Date()) + "_czRecord";
        try {
            PageUtil<CzRecord> pageUtil = new PageUtil<CzRecord>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(pageSize);
            String filePath = request.getServletContext().getRealPath("/WEB-INF/classes/cz_record.jasper");
            log.info("iportTable报表路径: " + filePath);
            list = bean.getUserCzJasperPrintList(name, insertTime, status, pageUtil, filePath);
            doIreport(list, pathname);
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 导出提现记录表
     */
    public String iportTxTable() {
        List<JasperPrint> list = null;
        String pathname = QwyUtil.fmyyyyMMddHHmmss3.format(new Date()) + "_txRecord";
        try {
            PageUtil<CzRecord> pageUtil = new PageUtil<CzRecord>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(pageSize);
            String filePath = request.getServletContext().getRealPath("/WEB-INF/classes/tx_record.jasper");
            log.info("iportTable报表路径: " + filePath);
            list = bean.getUserTxJasperPrintList(name, insertTime, status, pageUtil, filePath);
            doIreport(list, pathname);
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 申请提现
     * 
     * @return
     */
    public String rootWithdraw() {
        String json = "";
        try {
            request = getRequest();
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                // 管理员没有登录;
                return null;
            }
            Pattern pattern = Pattern.compile("^[0-9]+(\\.[0-9]{1,2})?$");// 带有小数的金钱格式;
            boolean isTrue = pattern.matcher(money).matches();
            if (isTrue) {
                Users u = registerUserBean.getUsersByUsername(username);
                if (!QwyUtil.isNullAndEmpty(u)) {
                    json = bean.withdraw(u.getId(), Integer.parseInt(money), QwyUtil.getIpAddr(request), users.getId(), note);
                } else {
                    json = QwyUtil.getJSONString("error", "用户不存在");
                }
            } else {
                json = QwyUtil.getJSONString("error", "金额格式有误");
            }

        } catch (Exception e) {
            log.error("操作异常: ", e);
            json = QwyUtil.getJSONString("error", "充值异常,请联系客服;code:8889");
        }
        try {
            QwyUtil.printJSON(getResponse(), json);
        } catch (IOException e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 申请提现
     * 
     * @return
     */
    public String rootTxRecord() {
        String json = "";
        try {
            request = getRequest();
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                // 管理员没有登录;
                return null;
            }
            PageUtil<RootTxRecord> pageUtil = new PageUtil<RootTxRecord>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(pageSize);
            StringBuffer url = new StringBuffer();
            url.append(getRequest().getServletContext().getContextPath());
            url.append("/Product/Admin/recharge!rootTxRecord.action?");
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                url.append("&insertTime=");
                url.append(insertTime);
            }
            pageUtil.setPageUrl(url.toString());
            pageUtil = bean.findPageUtil(pageUtil, insertTime);
            getRequest().setAttribute("insertTime", insertTime);
            if (!QwyUtil.isNullAndEmpty(pageUtil)) {
                getRequest().setAttribute("pageUtil", pageUtil);
            }
        } catch (Exception e) {
            log.error("操作异常: ", e);
            json = QwyUtil.getJSONString("error", "充值异常,请联系客服;code:8889");
        }
        return "rootTxRecord";
    }

    /**
     * 企业充值记录
     * 
     * @return
     */
    public String rechargeMoneyRecordCompany() {
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
                if (isExistsQX("充值记录", users.getId())) {
                    getRequest().setAttribute("err", "您没有操作该功能的权限!");
                    return "err";
                }
            }
            // 根据状态来加载提现的记录;
            PageUtil<CzRecordCompany> pageUtil = new PageUtil<CzRecordCompany>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(pageSize);
            StringBuffer url = new StringBuffer();
            url.append(getRequest().getServletContext().getContextPath());
            url.append("/Product/Admin/recharge!rechargeMoneyRecordCompany.action?username=" + username + "&status=" + status);
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                url.append("&insertTime=");
                url.append(insertTime);
            }
            pageUtil.setPageUrl(url.toString());
            pageUtil = bean.loadCzRecordCompany(pageUtil, status, name, insertTime);
            if (!QwyUtil.isNullAndEmpty(pageUtil)) {
                getRequest().setAttribute("pageUtil", pageUtil);
                getRequest().setAttribute("name", name);
                getRequest().setAttribute("insertTime", insertTime);
                getRequest().setAttribute("czRecordList", pageUtil.getList());
                return "czrecordCompany";
            }
        } catch (Exception e) {
            log.error("操作异常: ", e);
            json = QwyUtil.getJSONString("err", "查询记录异常");
        }
        return null;
    }

    /**
     * 今日满标企业详情
     * 
     * @author：zhuhaojie
     * @time：2018年1月12日 下午6:28:01
     * @version
     * @return
     */
    public String todayFullScaleUserDetail() {
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
                if (isExistsQX("满标企业信息", users.getId())) {
                    getRequest().setAttribute("err", "您没有操作该功能的权限!");
                    return "err";
                }
            }
            /**
             * 分页对象
             */
            PageUtil<FullScaleCompanyMessage> pageUtil = new PageUtil<FullScaleCompanyMessage>();

            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(pageSize);

            StringBuffer url = new StringBuffer();
            url.append(getRequest().getServletContext().getContextPath());
            url.append("/Product/Admin/recharge!todayFullScaleUserDetail.action?");

            StringBuffer sql = new StringBuffer();

            sql.append("select t.product_id  from ( ").append(" SELECT inv.product_id , sum(in_money) total FROM investors inv  ").append(" join product pro ").append(" on pro.id=inv.product_id ").append(" where inv.investor_status='1' ").append(" and inv.insert_time>=? and inv.insert_time<=? ").append(" group by inv.product_id ) t where t.total/100/10000>90");
            Object[] param = new Object[2];
            Date begin = new Date(DateUtils.getStartTime());
            Date end = new Date(DateUtils.getEndTime());
            param[0] = begin;
            param[1] = end;
            PageUtil<String> pageUtilone = productService.findProductsPageUtil(pageUtil, sql.toString(), param);
            if (pageUtilone != null) {
                // 按照分页查找到的产品id
                List<String> productIds = pageUtilone.getList();
                if (productIds != null && productIds.size() > 0) {
                    // 不分页查询部分数据
                    pageUtil.setPageSize(pageUtilone.getPageSize());
                    pageUtil.setCount(pageUtilone.getCount());
                    pageUtil.setCurrentPage(pageUtilone.getCurrentPage());
                    pageUtil.setLastPage(pageUtilone.isLastPage());
                    sql.delete(0, sql.length());
                    sql.append(" select pro.id,pro.title,pro.real_name,pro.all_copies,inv.insert_time,inv.in_money from product pro").append(" join investors inv").append(" on inv.product_id=pro.id").append(" and inv.insert_time>=? and inv.insert_time<=?").append(" and pro.id in(:ids)");
                    List listOne = productService.queryBySql(sql.toString(), param, "ids", productIds);
                    if (listOne != null && listOne.size() > 0) {
                        // //将List转换成迭代器
                        Iterator<String> productIdsIt = productIds.iterator();
                        int size = productIds.size();
                        // 将集合转换成迭代器
                        Iterator<Object> listIt = listOne.iterator();
                        // 将公司名称相同的放入一个list
                        List<List<Object>> separateList = new ArrayList<List<Object>>(size);

                        while (productIdsIt.hasNext()) {
                            String productIdOne = productIdsIt.next();
                            List<Object> oneList = new ArrayList<Object>();
                            while (listIt.hasNext()) {
                                Object objOne = listIt.next();
                                if (objOne instanceof Object[]) {
                                    Object[] array = (Object[]) objOne;
                                    String id = (String) array[0];// id
                                    if (id.equals(productIdOne)) {
                                        oneList.add(objOne);
                                        listIt.remove();
                                    }
                                }

                            }
                            separateList.add(oneList);
                        }
                        // separateList 一个元素代表一行记录
                        List<FullScaleCompanyMessage> finalList = new ArrayList<FullScaleCompanyMessage>();
                        for (List<Object> innerList : separateList) {
                            FullScaleCompanyMessage message = new FullScaleCompanyMessage();
                            Object obj = innerList.get(0);
                            if (obj instanceof Object[]) {
                                Object[] array = (Object[]) obj;
                                message.setCompanyName((String) array[2]);
                                message.setBrowLimit(new BigDecimal(array[3].toString()));
                                message.setRealInvest(new BigDecimal(array[3].toString()));
                                String type = (String) array[1];
                                int index = type.indexOf("N");
                                if (index != -1) {
                                    type = type.substring(0, index);
                                }
                                message.setType(type);
                            }
                            int sizeInner = innerList.size();
                            message.setChildBidNumber(sizeInner);
                            List<InnerCompanyMessage> innerMessage = new ArrayList<InnerCompanyMessage>(sizeInner);

                            for (Object objOne : innerList) {
                                InnerCompanyMessage innerMessageElement = new InnerCompanyMessage();
                                if (objOne instanceof Object[]) {
                                    Object[] array = (Object[]) objOne;
                                    // 虚拟投资
                                    String xuni = array[5].toString();
                                    innerMessageElement.setVirtualInvest(new BigDecimal(xuni));
                                    String number = (String) array[1];
                                    int index = number.indexOf(".");
                                    if (index != -1) {
                                        number = number.substring(index + 1, number.length());
                                    }
                                    innerMessageElement.setNumber(number);

                                }
                                innerMessage.add(innerMessageElement);
                            }
                            message.setInnerMessage(innerMessage);
                            finalList.add(message);
                        }
                        pageUtil.setList(finalList);
                    }
                }
            }
            if (!QwyUtil.isNullAndEmpty(pageUtil)) {
                getRequest().setAttribute("pageUtil", pageUtil);
                // getRequest().setAttribute("name", name);
                // getRequest().setAttribute("insertTime", insertTime);
                getRequest().setAttribute("czRecordList", pageUtil.getList());
                return "todayFullScaleUserDetail";
            }
        } catch (Exception e) {
            log.error("操作异常: ", e);
            json = QwyUtil.getJSONString("err", "查询记录异常");
        }
        return null;
    }

    // @Result(name = "uncheckedUserDetail", value = "/Product/Admin/functionManager/uncheckedUserDetail.jsp"),
    /**
     * 未提现总额详情
     * 
     * @author：zhuhaojie
     * @time：2018年1月16日 下午2:39:08
     * @version
     * @return String 详情页面路径
     */
    public String uncheckedUserDetail() {

        return null;
    }

    /**
     * 导出充值记录表
     */
    public String iportCzTable() {
        try {
            PageUtil<CzRecord> pageUtil = new PageUtil<CzRecord>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(999999);
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("充值记录");
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
            cell.setCellValue("持卡人姓名");
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
            cell.setCellValue("充值金额(元)");
            cell.setCellStyle(style);
            cell = row.createCell(7);
            cell.setCellValue("到帐时间");
            cell.setCellStyle(style);
            cell = row.createCell(8);
            cell.setCellValue("充值状态");
            cell.setCellStyle(style);
            cell = row.createCell(9);
            cell.setCellValue("备注");
            cell.setCellStyle(style);
            cell = row.createCell(10);
            cell.setCellValue("申请充值时间");
            cell.setCellStyle(style);
            cell = row.createCell(11);
            cell.setCellValue("流水号");
            cell.setCellStyle(style);
            cell = row.createCell(12);
            cell.setCellValue("平台订单号");
            cell.setCellStyle(style);
            cell = row.createCell(13);
            cell.setCellValue("交易流水号");
            cell.setCellStyle(style);
            cell = row.createCell(14);
            cell.setCellValue("支付方式");
            cell.setCellStyle(style);
            CzRecord czRecord = null;
            List list = bean.loadCzRecord(pageUtil, status, name, insertTime).getList();
            for (int i = 0; i < list.size(); i++) {
                row = sheet.createRow((int) i + 1);
                czRecord = (CzRecord) list.get(i);
                row = sheet.createRow((int) i + 1);
                row.createCell(0).setCellValue((int) i + 1);
                if (!QwyUtil.isNullAndEmpty(czRecord)) {
                    row.createCell(1).setCellValue(!QwyUtil.isNullAndEmpty(czRecord.getUserName()) ? DESEncrypt.jieMiUsername(czRecord.getUserName()) : "");
                    if (!QwyUtil.isNullAndEmpty(czRecord)) {
                        row.createCell(2).setCellValue(!QwyUtil.isNullAndEmpty(czRecord.getRealName()) ? czRecord.getRealName() : "");
                    } else {
                        row.createCell(2).setCellValue("");
                    }
                } else {
                    row.createCell(1).setCellValue("");
                    row.createCell(2).setCellValue("");
                }
                row.createCell(3).setCellValue(!QwyUtil.isNullAndEmpty(czRecord.getProvince()) ? czRecord.getProvince() : "");
                row.createCell(4).setCellValue(!QwyUtil.isNullAndEmpty(czRecord.getCity()) ? czRecord.getCity() : "");
                row.createCell(5).setCellValue(!QwyUtil.isNullAndEmpty(czRecord.getCategory()) ? czRecord.getCategory() : "");
                row.createCell(6).setCellValue(QwyUtil.calcNumber((QwyUtil.isNullAndEmpty(czRecord.getMoney()) ? 0 : czRecord.getMoney()), 0.01, "*", 2).doubleValue());
                row.createCell(7).setCellValue(!QwyUtil.isNullAndEmpty(czRecord.getCheckTime()) ? QwyUtil.fmyyyyMMddHHmmss.format(czRecord.getCheckTime()) : "");
                if (czRecord.getStatus().equals("1")) {
                    row.createCell(8).setCellValue("充值成功");
                } else if (czRecord.getStatus().equals("2")) {
                    row.createCell(8).setCellValue("充值失败");
                } else if (czRecord.getStatus().equals("0")) {
                    row.createCell(8).setCellValue("待充值");
                } else {
                    row.createCell(8).setCellValue("易宝充值成功,数据库插入失败");
                }
                row.createCell(9).setCellValue(czRecord.getNote());
                row.createCell(10).setCellValue(!QwyUtil.isNullAndEmpty(czRecord.getInsertTime()) ? QwyUtil.fmyyyyMMddHHmmss.format(czRecord.getInsertTime()) : "");
                row.createCell(11).setCellValue(!QwyUtil.isNullAndEmpty(czRecord.getRecordNumber()) ? czRecord.getRecordNumber() : 0L);
                row.createCell(12).setCellValue(czRecord.getOrderId());
                row.createCell(13).setCellValue(czRecord.getYbOrderId());

                if ("1".equals(czRecord.getType())) {
                    row.createCell(14).setCellValue("宝付快捷支付");
                } else if ("3".equals(czRecord.getType())) {
                    row.createCell(14).setCellValue("连连认证支付");
                } else if ("4".equals(czRecord.getType())) {
                    row.createCell(14).setCellValue("连连网银支付");
                } else {
                    row.createCell(14).setCellValue("宝付快捷支付");
                }
            }

            String pathname = QwyUtil.fmyyyyMMddHHmmss3.format(new Date()) + "_cz_record.xls";
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getRechargeMoney() {
        return rechargeMoney;
    }

    public void setRechargeMoney(double rechargeMoney) {
        this.rechargeMoney = rechargeMoney;
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

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRegistPlatform() {
        return registPlatform;
    }

    public void setRegistPlatform(String registPlatform) {
        this.registPlatform = registPlatform;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
