package com.huoq.admin.product.action;

import com.huoq.admin.product.bean.WapInvestorRecordBean;
import com.huoq.common.action.BaseAction;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.UsersAdmin;
import com.huoq.orm.WapInvestors;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;

import javax.annotation.Resource;
import java.io.FileOutputStream;
import java.util.List;

/**
 * 每日WAP投资人统计
 * Created by yks on 2016/10/25.
 */
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/Admin")
@Results({@Result(name = "loadWapInvestRecords", value = "/Product/Admin/operationManager/wapInvestRecord.jsp"),
        @Result(name = "err", value = "/Product/Admin/err.jsp")})
public class WapInvestorRecordAction extends BaseAction {

    @Resource
    private WapInvestorRecordBean bean;
    private Integer currentPage = 1;
    private Integer pageSize = 14;
    private String insertTime;
    private String registPlatform; // 注册平台
    private String isInvested; // 是否投资
    private String isBindBank; // 是否绑定银行卡


    /**
     * 加载wap投资统计
     *
     * @return
     */
    public String loadWapInvestorRecords() {
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
                if (isExistsQX("用户转换数据", users.getId())) {
                    getRequest().setAttribute("err", "您没有操作该功能的权限!");
                    return "err";
                }
            }
            if (QwyUtil.isNullAndEmpty(registPlatform)) {
                log.error("参数错误，缺少【registPlatform】" + registPlatform);
                return "err";
            }
            PageUtil<WapInvestors> pageUtil = new PageUtil<>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(pageSize);
            StringBuffer url = new StringBuffer();
            url.append(getRequest().getServletContext().getContextPath());
            url.append("/Product/Admin/wapInvestorRecord!loadWapInvestorRecords.action?");
            log.info("insertTime:" + insertTime);
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                url.append("&insertTime=");
                url.append(insertTime);
            }
            if (!QwyUtil.isNullAndEmpty(registPlatform)) {
                url.append("&registPlatform=");
                url.append(registPlatform);
            }
            pageUtil.setPageUrl(url.toString());
            bean.findWapInvestorsByDate(pageUtil, insertTime, registPlatform);
            getRequest().setAttribute("insertTime", insertTime);
            getRequest().setAttribute("registPlatform", registPlatform);
            if (!QwyUtil.isNullAndEmpty(pageUtil)) {
                getRequest().setAttribute("pageUtil", pageUtil);
                getRequest().setAttribute("list", pageUtil.getList());
                if (pageUtil.getCount() > 0)
                    getRequest().setAttribute("table", "true");
                return "loadWapInvestRecords";
            }
        } catch (Exception e) {
            log.error("系统错误", e);
        }
        return null;
    }

    /**
     * 导出WAP投资统计excel报表
     *
     * @return
     */
    public String exportTable() {
        try {
            PageUtil<WapInvestors> pageUtil = new PageUtil<WapInvestors>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(999999);
            bean.findWapInvestorsByDate(pageUtil, insertTime, registPlatform);
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("用户转换数据报表");
            HSSFRow row = sheet.createRow((int) 0);
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
            row.createCell(0).setCellValue("统计日期");
            row.createCell(1).setCellValue("投资平台");
            row.createCell(2).setCellValue("注册人数");
            row.createCell(3).setCellValue("绑卡人数");
            row.createCell(4).setCellValue("投资人数");
            row.createCell(5).setCellValue("投资金额");
            row.createCell(6).setCellValue("复投人数");
            row.createCell(7).setCellValue("复投率");
            List list = pageUtil.getList();
            WapInvestors wapInvestors = null;
            for (int i = 0; i < list.size(); i++) {
                row = sheet.createRow((int) i + 1);
                wapInvestors = (WapInvestors) list.get(i);
                row.createCell(0).setCellValue(wapInvestors.getDate());
                row.createCell(1).setCellValue(wapInvestors.getPlatform());
                row.createCell(2).setCellValue(wapInvestors.getRegistCount());
                row.createCell(3).setCellValue(wapInvestors.getBindCount());
                row.createCell(4).setCellValue(wapInvestors.getInvestCount());
                row.createCell(5).setCellValue(wapInvestors.getTotalInvestMoney());
                row.createCell(6).setCellValue(wapInvestors.getReinvestCount());
                row.createCell(7).setCellValue(wapInvestors.getReinvestRate());
            }
            String realPath = request.getServletContext().getRealPath("/report/wap_invest_record.xls");
            FileOutputStream fout = new FileOutputStream(realPath);
            wb.write(fout);
            fout.close();
            response.getWriter().write("/report/wap_invest_record.xls.xls");

        } catch (Exception e) {
            log.error("导出WAP投资统计报表出错！", e);
        }
        return null;
    }


    public WapInvestorRecordBean getBean() {
        return bean;
    }

    public void setBean(WapInvestorRecordBean bean) {
        this.bean = bean;
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

    public String getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(String insertTime) {
        this.insertTime = insertTime;
    }

    public String getRegistPlatform() {
        return registPlatform;
    }

    public void setRegistPlatform(String registPlatform) {
        this.registPlatform = registPlatform;
    }

    public String getIsInvested() {
        return isInvested;
    }

    public void setIsInvested(String isInvested) {
        this.isInvested = isInvested;
    }

    public String getIsBindBank() {
        return isBindBank;
    }

    public void setIsBindBank(String isBindBank) {
        this.isBindBank = isBindBank;
    }
}
