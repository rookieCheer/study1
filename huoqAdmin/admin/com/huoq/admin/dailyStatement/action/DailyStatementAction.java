package com.huoq.admin.dailyStatement.action;

import com.huoq.admin.dailyStatement.bean.UpdateDailyStatementThreadBean;
import com.huoq.common.action.BaseAction;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.DailyStatement;
import com.huoq.orm.TiedCard;
import com.huoq.orm.UsersAdmin;
import org.apache.poi.hssf.usermodel.*;
import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 运营日报表
 *
 * @author 李瑞丽
 * @Date: Created in 17:47 2018/1/16
 */

@ParentPackage("struts-default")
@Namespace("/Product/Admin")
@Results({
        @Result(name = "dailyStatement", value = "/Product/Admin/dailyStatement/dailyStatement.jsp")
})
public class DailyStatementAction extends BaseAction {

    @Autowired
    private UpdateDailyStatementThreadBean bean;
    private String insertTime;
    private Integer currentPage = 1;
    private Integer pageSize = 20;

    /**
     * 运营日报表概览数据
     *
     * @return
     */
    public String findDailyStatement() {
        String json = "";
        HttpServletRequest request = getRequest();
        try {
            //判断用户是否登陆
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                request.setAttribute("json", json);
                return "noLogin";
            }
            DailyStatement dataOverview = new DailyStatement();

            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
            //新建分页实体类
            PageUtil<DailyStatement> pageUtil = new PageUtil<DailyStatement>();
            if (!QwyUtil.isNullAndEmpty(currentPage)) {
                pageUtil.setCurrentPage(currentPage);
            } else {
                pageUtil.setCurrentPage(1);
            }
            pageUtil.setPageSize(pageSize);
            StringBuffer url = new StringBuffer();
            url.append(getRequest().getServletContext().getContextPath());
            url.append("/Product/Admin/dailyStatement!findDailyStatement.action?");
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                url.append("&insertTime=");
                url.append(insertTime);
            }
            //设置分页地址
            pageUtil.setPageUrl(url.toString());
            //分页查询
            pageUtil = bean.findDailyStatement(pageUtil, insertTime);
            if (!QwyUtil.isNullAndEmpty(pageUtil)) {
                request.setAttribute("pageUtil", pageUtil);
                request.setAttribute("list", pageUtil.getList());
                getRequest().setAttribute("tj", bean.tjDailyStatement(pageUtil.getList()));
                return "dailyStatement";
            }
            return "dailyStatement";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 导出运营日报表
     */
    public String exportDailyStatement() {
        if (!QwyUtil.isNullAndEmpty(insertTime)) {

        } else {
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            String time = sd.format(date);

        }
        try {
            PageUtil<TiedCard> pageUtil = new PageUtil<TiedCard>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(999999);
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("绑卡信息统计表");
            HSSFRow row = sheet.createRow((int) 0);
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
            HSSFCell cell = row.createCell(0);
            cell = row.createCell(0);
            cell.setCellValue("序号");
            cell.setCellStyle(style);
            cell = row.createCell(1);
            cell.setCellValue("查询日期");
            cell.setCellStyle(style);
            cell = row.createCell(2);
            cell.setCellValue("交易额");
            cell.setCellStyle(style);
            cell = row.createCell(3);
            cell.setCellValue("在贷金额（含零钱罐）");
            cell.setCellStyle(style);
            cell = row.createCell(4);
            cell.setCellValue("在贷金额（不含零钱罐）");
            cell.setCellStyle(style);
            cell = row.createCell(5);
            cell.setCellValue("回款金额（不含零钱罐）");
            cell.setCellStyle(style);
            cell = row.createCell(6);
            cell.setCellValue("回款金额（含零钱罐及余额）");
            cell.setCellStyle(style);
            cell = row.createCell(7);
            cell.setCellValue("支付利息");
            cell.setCellStyle(style);
            cell = row.createCell(8);
            cell.setCellValue("今日提现金额");
            cell.setCellStyle(style);
            cell = row.createCell(9);
            cell.setCellValue("回款用户投资率");
            cell.setCellStyle(style);
            cell = row.createCell(10);
            cell.setCellValue("资金流入额");
            cell.setCellStyle(style);
            cell = row.createCell(11);
            cell.setCellValue("净流入金额");
            cell.setCellStyle(style);
            cell = row.createCell(12);
            cell.setCellValue("资金存量");
            cell.setCellStyle(style);
            cell = row.createCell(13);
            cell.setCellValue("激活用户数");
            cell.setCellStyle(style);
            cell = row.createCell(14);
            cell.setCellValue("投资用户数");
            cell.setCellStyle(style);
            cell = row.createCell(15);
            cell.setCellValue("今日注册人数");
            cell.setCellStyle(style);
            cell = row.createCell(16);
            cell.setCellValue("今日认证用户");
            cell.setCellStyle(style);
            cell = row.createCell(17);
            cell.setCellValue("今日首投用户");
            cell.setCellStyle(style);
            cell = row.createCell(18);
            cell.setCellValue("首投用户转化率");
            cell.setCellStyle(style);
            cell = row.createCell(19);
            cell.setCellValue("首投总金额");
            cell.setCellStyle(style);
            cell = row.createCell(20);
            cell.setCellValue("首投客单金额");
            cell.setCellStyle(style);
            cell = row.createCell(21);
            cell.setCellValue("复投金额");
            cell.setCellStyle(style);
            cell = row.createCell(22);
            cell.setCellValue("零钱罐新增金额");
            cell.setCellStyle(style);
            cell = row.createCell(23);
            cell.setCellValue("复投用户数");
            cell.setCellStyle(style);
            cell = row.createCell(24);
            cell.setCellValue("新增复投用户数");
            cell.setCellStyle(style);
            cell = row.createCell(25);
            cell.setCellValue("新增复投用户投资总额");
            cell.setCellStyle(style);
            cell = row.createCell(26);
            cell.setCellValue("复投次数");
            cell.setCellStyle(style);
            cell = row.createCell(27);
            cell.setCellValue("新增复投率");
            cell.setCellStyle(style);
            cell = row.createCell(28);
            cell.setCellValue("复投用户占比");
            cell.setCellStyle(style);
            cell = row.createCell(29);
            cell.setCellValue("复投金额占比");
            cell.setCellStyle(style);
            cell = row.createCell(30);
            cell.setCellValue("复投客单金额");
            cell.setCellStyle(style);
            cell = row.createCell(31);
            cell.setCellValue("人均投资金额");

            
            List<DailyStatement> list = bean.findDailyStatement(pageUtil, insertTime).getList();
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
            DailyStatement dailyStatement = null;
            for (int i = 0; i < list.size(); i++) {
                row = sheet.createRow((int) i + 1);
                dailyStatement = (DailyStatement) list.get(i);
                String format = sd.format(dailyStatement.getInsertTime());
                row.createCell(0).setCellValue((int) i + 1);//序号
                //查询日期
                row.createCell(1).setCellValue(format);
                //交易额
                row.createCell(2).setCellValue(!QwyUtil.isNullAndEmpty(dailyStatement.getTradingVolume()) ? dailyStatement.getTradingVolume() : 0.0);
                //在贷金额（含零钱罐）
                row.createCell(3).setCellValue(!QwyUtil.isNullAndEmpty(dailyStatement.getLoanAmountAll()) ? dailyStatement.getLoanAmountAll() : 0.0);
                //在贷金额（不含零钱罐）
                row.createCell(4).setCellValue(!QwyUtil.isNullAndEmpty(dailyStatement.getLoanAmount()) ? dailyStatement.getLoanAmount() : 0.0);
                //回款金额（不含零钱罐）
                row.createCell(5).setCellValue(!QwyUtil.isNullAndEmpty(dailyStatement.getReimbursementAmount()) ? dailyStatement.getReimbursementAmount() : 0.0);
                //回款金额（含零钱罐及余额）
                row.createCell(6).setCellValue(!QwyUtil.isNullAndEmpty(dailyStatement.getReimbursementAmountAll()) ? dailyStatement.getReimbursementAmountAll() : 0.0);
                //支付利息
                row.createCell(7).setCellValue(!QwyUtil.isNullAndEmpty(dailyStatement.getInterestpayment()) ? dailyStatement.getInterestpayment() : 0.0);
                //今日提现金额
                row.createCell(8).setCellValue(!QwyUtil.isNullAndEmpty(dailyStatement.getTodayOutCashMoney()) ? dailyStatement.getTodayOutCashMoney() : 0.0);
                // 回款用户投资率
                row.createCell(9).setCellValue(!QwyUtil.isNullAndEmpty(dailyStatement.getReturnInvestmentRate()) ? (QwyUtil.jieQuFa( (dailyStatement.getReturnInvestmentRate()*100),2))  +"%": "0.0");
                // 资金流入额
                row.createCell(10).setCellValue(!QwyUtil.isNullAndEmpty(dailyStatement.getCapitalInflow()) ? dailyStatement.getCapitalInflow():0.0);
                // 净流入金额
                row.createCell(11).setCellValue(!QwyUtil.isNullAndEmpty(dailyStatement.getNetInflow()) ? dailyStatement.getNetInflow(): 0.0);
                //平台资金存量
                row.createCell(12).setCellValue(!QwyUtil.isNullAndEmpty(dailyStatement.getCapitalStock()) ? dailyStatement.getCapitalStock() : 0.0);
                //激活用户数
                row.createCell(13).setCellValue(!QwyUtil.isNullAndEmpty(dailyStatement.getActivationCount()) ?dailyStatement.getActivationCount() : 0);
                //投资用户数
                row.createCell(14).setCellValue(!QwyUtil.isNullAndEmpty(dailyStatement.getInvestCount()) ?dailyStatement.getInvestCount() : 0);
                //今日注册用户
                row.createCell(15).setCellValue(!QwyUtil.isNullAndEmpty(dailyStatement.getTodayregisterCount()) ?dailyStatement.getTodayregisterCount() : 0);
                //今日认证用户
                row.createCell(16).setCellValue(!QwyUtil.isNullAndEmpty(dailyStatement.getTodaycertificationCount()) ?dailyStatement.getTodaycertificationCount() : 0);
                //今日首投人数
                row.createCell(17).setCellValue(!QwyUtil.isNullAndEmpty(dailyStatement.getTodayNewBuyNumber()) ? dailyStatement.getTodayNewBuyNumber() : 0);
                // 首投用户转化率
                row.createCell(18).setCellValue(!QwyUtil.isNullAndEmpty(dailyStatement.getFirstPercentConversion()) ?  (QwyUtil.jieQuFa((dailyStatement.getFirstPercentConversion()*100),2))  +"%": "0.0");
                //首投总金额
                row.createCell(19).setCellValue(!QwyUtil.isNullAndEmpty(dailyStatement.getFirstInvestmentTotalMoney()) ? dailyStatement.getFirstInvestmentTotalMoney() : 0.0);
                // 首投客单金额（元）
                row.createCell(20).setCellValue(!QwyUtil.isNullAndEmpty(dailyStatement.getFirstInvestmentMoney()) ? dailyStatement.getFirstInvestmentMoney() : 0.0);
                // 复投金额（元）
                row.createCell(21).setCellValue(!QwyUtil.isNullAndEmpty(dailyStatement.getReInvestmentMoney()) ? dailyStatement.getReInvestmentMoney() : 0.0);
                // 零钱罐新增金额（元）
                row.createCell(22).setCellValue(!QwyUtil.isNullAndEmpty(dailyStatement.getAmountNewMoney()) ? dailyStatement.getAmountNewMoney() : 0.0);
                //复投用户数
                row.createCell(23).setCellValue(!QwyUtil.isNullAndEmpty(dailyStatement.getReInvestmentCount()) ? dailyStatement.getReInvestmentCount() : 0);
                //新增复投用户数
                row.createCell(24).setCellValue(!QwyUtil.isNullAndEmpty(dailyStatement.getAddReInvestmentCount()) ? dailyStatement.getAddReInvestmentCount() : 0);
                //新增复投用户投资总额（元）
                row.createCell(25).setCellValue(!QwyUtil.isNullAndEmpty(dailyStatement.getAddReInvestmentMoney()) ? dailyStatement.getAddReInvestmentMoney() : 0.0);
                //复投次数
                row.createCell(26).setCellValue(!QwyUtil.isNullAndEmpty(dailyStatement.getReInvestmentAmount()) ? dailyStatement.getReInvestmentAmount() : 0);
                // 新增复投率（%）
                row.createCell(27).setCellValue(!QwyUtil.isNullAndEmpty(dailyStatement.getMultipleRate()) ?  (QwyUtil.jieQuFa((dailyStatement.getMultipleRate()*100),2))  +"%": "0.0");
                // 复投用户占比（%）
                row.createCell(28).setCellValue(!QwyUtil.isNullAndEmpty(dailyStatement.getOccupationRatio()) ? (QwyUtil.jieQuFa((dailyStatement.getOccupationRatio()*100),2))  +"%": "0.0");
                // 复投金额占比（%）
                row.createCell(29).setCellValue(!QwyUtil.isNullAndEmpty(dailyStatement.getReInvestmentRate()) ? (QwyUtil.jieQuFa((dailyStatement.getReInvestmentRate()*100),2))  +"%": "0.0");
                // 复投客单金额（元）
                row.createCell(30).setCellValue(!QwyUtil.isNullAndEmpty(dailyStatement.getSumMoney()) ? dailyStatement.getSumMoney(): 0.0);
                // 人均投资金额（元）
                row.createCell(31).setCellValue(!QwyUtil.isNullAndEmpty(dailyStatement.getCapitaInvestmentMoney() )? dailyStatement.getCapitaInvestmentMoney() : 0.0);
                }
            String pathname = QwyUtil.fmyyyyMMddHHmmss3.format(new Date()) + "_find_daily_statement.xls";
            String realPath = request.getServletContext().getRealPath("/report/" + pathname);
            log.info("运营日报表表地址：" + realPath);
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
}


