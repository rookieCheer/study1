package com.huoq.admin.login.action;

import com.huoq.common.action.BaseAction;
import com.huoq.common.bean.PlatformBean;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.BuyProductInfo;
import com.huoq.orm.DataOverview;
import com.huoq.orm.TiedCard;
import com.huoq.orm.UsersAdmin;
import com.huoq.thread.bean.UpdateDataOverviewThreadBean;
import org.apache.poi.hssf.usermodel.*;
import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@ParentPackage("struts-default")
@Namespace("/Product/findDataOverview")
@Results({
        //后台登录页面
        @Result(name = "login", value = "/Product/Admin/dataOverview/dataOverview.jsp")
})
/**
 * 首页数据概览
 * @author Administrator
 *
 */
public class FindDataOverviewAction extends BaseAction {
    @Autowired
    private UpdateDataOverviewThreadBean bean;
    private String insertTime;
    private Integer currentPage = 1;
    private Integer pageSize = 20;

    /**
     * 统计首页概览数据
     *
     * @return
     */
    public String findDataOverview() {
        String json = "";
        HttpServletRequest request = getRequest();
        try {
            //补全之前数据
            //bean.updateDataOverview();
            //判断用户是否登陆
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                request.setAttribute("json", json);
                return "noLogin";
            }

            DataOverview dataOverview = new DataOverview();

            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
            //新建分页实体类
            PageUtil<DataOverview> pageUtil = new PageUtil<DataOverview>();
            if(!QwyUtil.isNullAndEmpty(currentPage)){
                pageUtil.setCurrentPage(currentPage);
            }else{
                pageUtil.setCurrentPage(1);
            }
            pageUtil.setPageSize(pageSize);
            StringBuffer url = new StringBuffer();
            url.append(getRequest().getServletContext().getContextPath());
            // url.append("/Product/buyInfo/userBuy!tiedCardInfo.action?");
            url.append("/Product/findDataOverview/findDataOverview!findDataOverview.action?");
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                url.append("&insertTime=");
                url.append(insertTime);
            }
            //设置分页地址
            pageUtil.setPageUrl(url.toString());
            //分页查询
            pageUtil = bean.findDataOverview(pageUtil, insertTime);
            if (!QwyUtil.isNullAndEmpty(pageUtil)) {
                // request.setAttribute("insertTime", insertTime);
                request.setAttribute("pageUtil", pageUtil);
                request.setAttribute("list", pageUtil.getList());
                return "login";
            }
            return "login";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 导出数据概览表
     */
    public String exportDataOverviewInfo() {
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
            cell.setCellValue("平台资金存量(元)");
            cell.setCellStyle(style);
            cell = row.createCell(3);
            cell.setCellValue("今日存量增量(元)");
            cell.setCellStyle(style);
            cell = row.createCell(4);
            cell.setCellValue("累计提现金额(元)");
            cell.setCellStyle(style);
            cell = row.createCell(5);
            cell.setCellValue("今日提现金额(元)");
            cell.setCellStyle(style);
            cell = row.createCell(6);
            cell.setCellValue("累计充值金额(元)");
            cell.setCellStyle(style);
            cell = row.createCell(7);
            cell.setCellValue("今日充值金额(元)");
            cell.setCellStyle(style);
            cell = row.createCell(8);
            cell.setCellValue("今日购买金额(元)");
            cell.setCellStyle(style);
            cell = row.createCell(9);
            cell.setCellValue("累计注册用户");
            cell.setCellStyle(style);
            cell = row.createCell(10);
            cell.setCellValue("今日注册用户");
            cell.setCellStyle(style);
            cell = row.createCell(11);
            cell.setCellValue("累计认证用户");
            cell.setCellStyle(style);
            cell = row.createCell(12);
            cell.setCellValue("今日认证用户");
            cell.setCellStyle(style);
            cell = row.createCell(13);
            cell.setCellValue("今日购买人数");
            cell.setCellStyle(style);
            cell = row.createCell(14);
            cell.setCellValue("今日首投人数");
            cell.setCellStyle(style);
            cell = row.createCell(15);
            cell.setCellValue("今日未审核提现总额(元)");
            cell.setCellStyle(style);
            cell = row.createCell(16);
            List<DataOverview> list = bean.findDataOverview(pageUtil, insertTime).getList();
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");

            DataOverview dataOverview = null;
            for (int i = 0; i < list.size(); i++) {
                row = sheet.createRow((int) i + 1);
                dataOverview = (DataOverview) list.get(i);
                String format = sd.format(dataOverview.getInsertTime());
                row.createCell(0).setCellValue((int) i + 1);//序号
                row.createCell(1).setCellValue(format);//查询日期
                row.createCell(2).setCellValue(!QwyUtil.isNullAndEmpty(dataOverview.getAllCapitalStock()) ? dataOverview.getAllCapitalStock() : 0.0);//平台资金存量
                row.createCell(3).setCellValue(!QwyUtil.isNullAndEmpty(dataOverview.getTodayCapitalStock()) ? dataOverview.getTodayCapitalStock() : 0.0);//今日存量增量
                row.createCell(4).setCellValue(!QwyUtil.isNullAndEmpty(dataOverview.getAllOutCashMoney()) ? dataOverview.getAllOutCashMoney() : 0.0);//累计提现金额
                row.createCell(5).setCellValue(!QwyUtil.isNullAndEmpty(dataOverview.getTodayOutCashMoney()) ? dataOverview.getTodayOutCashMoney() : 0.0);//今日提现金额
                row.createCell(6).setCellValue(!QwyUtil.isNullAndEmpty(dataOverview.getRechargeMoney()) ? dataOverview.getRechargeMoney() : 0.0);//累计充值金额
                row.createCell(7).setCellValue(!QwyUtil.isNullAndEmpty(dataOverview.getTodayrechargeMoney()) ? dataOverview.getTodayrechargeMoney() : 0.0);//今日充值金额
                row.createCell(8).setCellValue(!QwyUtil.isNullAndEmpty(dataOverview.getTodayBuyMoney()) ? dataOverview.getTodayBuyMoney() : 0.0);//今日购买金额
                row.createCell(9).setCellValue(!QwyUtil.isNullAndEmpty(dataOverview.getRegisterCount()) ? dataOverview.getRegisterCount() : 0);//累计注册用户
                row.createCell(10).setCellValue(!QwyUtil.isNullAndEmpty(dataOverview.getTodayregisterCount()) ? dataOverview.getTodayregisterCount() : 0);//今日注册用户
                row.createCell(11).setCellValue(!QwyUtil.isNullAndEmpty(dataOverview.getCertificationCount()) ? dataOverview.getCertificationCount() : 0);//累计认证用户
                row.createCell(12).setCellValue(!QwyUtil.isNullAndEmpty(dataOverview.getTodaycertificationCount()) ? dataOverview.getTodaycertificationCount() : 0);//今日认证用户
                row.createCell(13).setCellValue(!QwyUtil.isNullAndEmpty(dataOverview.getTodayBuyNumber()) ? dataOverview.getTodayBuyNumber() : 0);//今日购买人数
                row.createCell(14).setCellValue(!QwyUtil.isNullAndEmpty(dataOverview.getTodayNewBuyNumber()) ? dataOverview.getTodayNewBuyNumber() : 0);//今日首投人数
                row.createCell(15).setCellValue(!QwyUtil.isNullAndEmpty(dataOverview.getTodayUAuditingOutCashMoney()) ? dataOverview.getTodayUAuditingOutCashMoney() : 0.0);//今日未审核提现总额
            }
            String pathname = QwyUtil.fmyyyyMMddHHmmss3.format(new Date()) + "_find_data_overview.xls";
            String realPath = request.getServletContext().getRealPath("/report/" + pathname);
            log.info("资金数据概览表表地址：" + realPath);
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
