package com.huoq.admin.dailyStatement;

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

import static org.apache.struts2.ServletActionContext.getRequest;

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
            cell.setCellValue("查询日期");
            cell.setCellStyle(style);
            cell = row.createCell(1);
            cell.setCellValue("查询日期");
            cell.setCellStyle(style);
            cell = row.createCell(1);
            cell.setCellValue("查询日期");
            cell.setCellStyle(style);
            cell = row.createCell(1);
            cell.setCellValue("查询日期");
            cell.setCellStyle(style);
            cell = row.createCell(1);
            cell.setCellValue("查询日期");
            cell.setCellStyle(style);
            cell = row.createCell(1);
            cell.setCellValue("查询日期");
            cell.setCellStyle(style);
            cell = row.createCell(1);
            cell.setCellValue("查询日期");
            cell.setCellStyle(style);
            cell = row.createCell(1);
            cell.setCellValue("查询日期");
            cell.setCellStyle(style);
            cell = row.createCell(1);
            cell.setCellValue("查询日期");
            cell.setCellStyle(style);
            cell = row.createCell(1);
            cell.setCellValue("查询日期");
            cell.setCellStyle(style);
            cell = row.createCell(1);
            cell.setCellValue("查询日期");
            cell.setCellStyle(style);
            cell = row.createCell(1);
            cell.setCellValue("查询日期");
            cell.setCellStyle(style);
            cell = row.createCell(1);
            cell.setCellValue("查询日期");









            List<DailyStatement> list = bean.findDailyStatement(pageUtil, insertTime).getList();
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");

            DailyStatement dailyStatement = null;
            for (int i = 0; i < list.size(); i++) {
                row = sheet.createRow((int) i + 1);
                dailyStatement = (DailyStatement) list.get(i);
                String format = sd.format(dailyStatement.getInsertTime());
                row.createCell(0).setCellValue((int) i + 1);//序号
                row.createCell(1).setCellValue(format);//查询日期
            }
            String pathname = QwyUtil.fmyyyyMMddHHmmss3.format(new Date()) + "_find_data_overview.xls";
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


