package com.huoq.admin.product.action;

import com.huoq.admin.product.bean.InviteEarnBean;
import com.huoq.common.action.BaseAction;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.*;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.poi.hssf.usermodel.*;
import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;

import javax.annotation.Resource;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

/**
 * 邀请投资奖励记录
 * Created by yks on 2016/10/10.
 */
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/Admin")
@Results({
        @Result(name = "inviteEarnRecords", value = "/Product/Admin/inviteManager/invite_earn_record.jsp"),
        @Result(name = "err", value = "/Product/Admin/err.jsp")
})
public class InviteEarnAction extends BaseAction {

    private PageUtil<InviteEarn> pageUtil;
    private Integer currentPage = 1;
    private Integer pageSize = 50;
    private String status = "all";
    private String inviter = ""; //邀请人姓名
    private String insertTime;
    private String inviteId; //邀请人id
    @Resource
    private InviteEarnBean bean;

    /**
     * 邀请投资奖励记录列表显示
     * @return
     */
    public String inviteEarnRecords() {
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
                if (isExistsQX("邀请投资奖励记录", users.getId())) {
                    getRequest().setAttribute("err", "您没有操作该功能的权限!");
                    return "err";
                }
            }
            PageUtil<InviteEarn> pageUtil = new PageUtil<InviteEarn>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(pageSize);
            StringBuilder url = new StringBuilder();
            url.append(getRequest().getServletContext().getContextPath());
            url.append("/Product/Admin/inviteEarn!inviteEarnRecords.action?");
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                url.append("&insertTime=");
                url.append(insertTime);
            }
            if (!QwyUtil.isNullAndEmpty(inviteId)) {
                url.append("&inviteId=");
                url.append(inviteId);
            }
            if (!QwyUtil.isNullAndEmpty(status)) {
                url.append("&status=");
                url.append(status);
            }
            if (!QwyUtil.isNullAndEmpty(inviter)) {
                inviter = inviter.trim();
                url.append("&inviter=");
                url.append(inviter);
            }
            pageUtil.setPageUrl(url.toString());
            pageUtil = bean.loadInviteEarnRecords(pageUtil,inviter,inviteId,status,insertTime);
            if (!QwyUtil.isNullAndEmpty(pageUtil)) {
                //getRequest().setAttribute("currentPage",currentPage);
                getRequest().setAttribute("insertTime", insertTime);
                getRequest().setAttribute("inviter", inviter);
                getRequest().setAttribute("inviteId", inviteId);
                getRequest().setAttribute("pageUtil", pageUtil);
                getRequest().setAttribute("list", pageUtil.getList());
                return "inviteEarnRecords";
            }
        } catch (Exception e) {
            log.error("操作异常: ", e);
            json = QwyUtil.getJSONString("err", "查询记录异常");
        }
        return "inviteEarnRecords";
    }


    /**
     * 导出邀请投资奖励记录报表
     */
    public String iportInviteEarnTable() {
        List<JasperPrint> list = null;
        String pathname = QwyUtil.fmyyyyMMddHHmmss3.format(new Date()) + "_ieRecord.jasper";
        try {
            PageUtil<InviteEarn> pageUtil = new PageUtil<InviteEarn>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(pageSize);
            String filePath = request.getServletContext().getRealPath("/WEB-INF/classes/ie_record.jasper");
            log.info("iportInviteEarnTable报表路径: " + filePath);
            list = bean.getInviteEarnJasperPrintList(inviter,insertTime,inviteId,status,pageUtil,filePath);
            log.info("list 大小 ："+list.size());
            doIreport(list, pathname);
        } catch (Exception e) {
            log.error("导出失败，原因：",e.getCause());
            return null;
        }
        return null;
    }

    /**
     * 导出邀请投资奖励记录报表
     */
    public String iportTable() {
        try {
            PageUtil<InviteEarn> pageUtil = new PageUtil<InviteEarn>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(999999);
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("邀请投资奖励记录");
            HSSFRow row = sheet.createRow((int) 0);
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
            HSSFCell cell = row.createCell(0);
            cell.setCellValue("序号");
            cell.setCellStyle(style);
            cell = row.createCell(1);
            cell.setCellValue("邀请人id");
            cell.setCellStyle(style);
            cell = row.createCell(2);
            cell.setCellValue("邀请人");
            cell.setCellStyle(style);
            cell = row.createCell(3);
            cell.setCellValue("被邀请人id");
            cell.setCellStyle(style);
            cell = row.createCell(4);
            cell.setCellValue("被邀请人手机号");
            cell.setCellStyle(style);
            cell = row.createCell(5);
            cell.setCellValue("被邀请人投资份数");
            cell.setCellStyle(style);

            cell = row.createCell(6);
            cell.setCellValue("奖励金额");
            cell.setCellStyle(style);
            cell = row.createCell(7);
            cell.setCellValue("创建时间");
            cell.setCellStyle(style);
            cell = row.createCell(8);
            cell.setCellValue("发放奖励时间");
            cell.setCellStyle(style);
            cell = row.createCell(9);
            cell.setCellValue("状态");
            cell.setCellStyle(style);

            cell = row.createCell(10);
            cell.setCellValue("备注");
            cell.setCellStyle(style);
            InviteEarnExport inviteEarn = null;
            List list = bean.exportInviteEarnExport(pageUtil,inviter,inviteId,status,insertTime);
            for (int i = 0; i < list.size(); i++) {
                row = sheet.createRow((int) i + 1);
                inviteEarn = (InviteEarnExport) list.get(i);
                row = sheet.createRow((int) i + 1);
                row.createCell(0).setCellValue((int) i + 1);
                row.createCell(1).setCellValue(inviteEarn.getInviteId());
                row.createCell(2).setCellValue(inviteEarn.getInviter());
                row.createCell(3).setCellValue(inviteEarn.getBeInvitedId());
                row.createCell(4).setCellValue(inviteEarn.getBeInvitePhone());
                row.createCell(5).setCellValue(inviteEarn.getCopies());
                row.createCell(6).setCellValue(inviteEarn.getEarnMoney());
                row.createCell(7).setCellValue(inviteEarn.getInsertTime());
                row.createCell(8).setCellValue(inviteEarn.getReturnTime());
                row.createCell(9).setCellValue(inviteEarn.getStatus());
                row.createCell(10).setCellValue(inviteEarn.getNote());
            }
            String pathname = QwyUtil.fmyyyyMMddHHmmss3.format(new Date()) + "_ieRecord.xls";
            String realPath = request.getServletContext().getRealPath("/report/"+pathname);
            log.info("邀请投资奖励记录报表地址："+realPath);
            FileOutputStream fout = new FileOutputStream(realPath);
            wb.write(fout);
            fout.close();
            response.getWriter().write("/report/"+pathname);
        } catch (Exception e) {
            log.error("操作异常: ",e);
        }
        return null;
    }

    public PageUtil<InviteEarn> getPageUtil() {
        return pageUtil;
    }

    public void setPageUtil(PageUtil<InviteEarn> pageUtil) {
        this.pageUtil = pageUtil;
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

    public String getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(String insertTime) {
        this.insertTime = insertTime;
    }

    public String getInviter() {
        return inviter;
    }

    public void setInviter(String inviter) {
        this.inviter = inviter;
    }

    public String getInviteId() {
        return inviteId;
    }

    public void setInviteId(String inviteId) {
        this.inviteId = inviteId;
    }

}
