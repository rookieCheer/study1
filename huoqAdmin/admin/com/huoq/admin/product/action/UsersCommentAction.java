package com.huoq.admin.product.action;

import javax.annotation.Resource;

import com.huoq.common.util.DESEncrypt;
import org.apache.poi.hssf.usermodel.*;
import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;

import com.huoq.account.bean.InvestorsRecordBean;
import com.huoq.account.bean.MyAccountBean;
import com.huoq.account.bean.UserInfoBean;
import com.huoq.admin.product.bean.UsersCommentBean;
import com.huoq.common.action.BaseAction;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.login.bean.RegisterUserBean;
import com.huoq.orm.Comments;
import com.huoq.orm.Coupon;
import com.huoq.orm.Investors;
import com.huoq.orm.Users;
import com.huoq.orm.UsersAdmin;
import com.huoq.orm.UsersInfo;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 用户评论
 *
 * @author qwy
 * <p>
 * 2015-04-20 12:58:29
 */
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/Admin")
//理财产品
@Results({
        @Result(name = "comment", value = "/Product/Admin/operationManager/usersComments.jsp"),
        @Result(name = "login", value = "/Product/loginBackground.jsp", type = org.apache.struts2.dispatcher.ServletRedirectResult.class),
        @Result(name = "error", value = "/Product/error.jsp"),
        @Result(name = "err", value = "/Product/Admin/err.jsp")

})
public class UsersCommentAction extends BaseAction {
    @Resource
    private UsersCommentBean bean;
    private Integer currentPage = 1;//当前页
    private Integer pageCount;//总页数
    private Integer pageSize = 50;
    private String insertTime;

    /**
     * 显示用户评论;
     *
     * @return
     */
    public String showUsersComment() {
        try {
            request = getRequest();
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                //管理员没有登录;
                return "login";
            }
            String superName = "0EA5D6BC23E8EEC78F62546B9F68BABFA96976B775889BA625DB6D764FD0DBD42A1C0F45F85B0DE8";
            if (!superName.equals(users.getUsername())) {
                if (isExistsQX("用户评论", users.getId())) {
                    getRequest().setAttribute("err", "您没有操作该功能的权限!");
                    return "err";
                }
            }
            PageUtil<Comments> pageUtil = new PageUtil<Comments>(pageSize, currentPage);
            StringBuffer url = new StringBuffer();
            url.append(getRequest().getServletContext().getContextPath());
            url.append("/Product/Admin/usersComment!showUsersComment?");
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                url.append("&insertTime=");
                url.append(insertTime);
            }
            bean.loadComments(pageUtil, null, insertTime);
            request.setAttribute("pageUtil", pageUtil);
            if (!QwyUtil.isNullAndEmpty(pageUtil)) {
                request.setAttribute("list", pageUtil.getList());
            }
        } catch (Exception e) {
            log.error("操作异常: ", e);
            //request.setAttribute("errMsg", "发生了错误");
            return "error";
        }
        return "comment";
    }

    /**
     * 导出用户评论表
     *
     * @return
     */
    public String exportUsersComment() {
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
            HSSFSheet sheet = wb.createSheet("用户评论表");
            HSSFRow row = sheet.createRow((int) 0);
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
            HSSFCell cell = row.createCell(0);
            cell = row.createCell(0);
            cell.setCellValue("评论时间");
            cell.setCellStyle(style);
            cell = row.createCell(1);
            cell.setCellValue("用户名");
            cell.setCellStyle(style);
            cell = row.createCell(2);
            cell.setCellValue("真实姓名");
            cell.setCellStyle(style);
            cell = row.createCell(3);
            cell.setCellValue("评论途径");
            cell.setCellStyle(style);
            cell = row.createCell(4);
            cell.setCellValue("评论内容");
            cell.setCellStyle(style);
            cell = row.createCell(5);
            List<Comments> list = bean.loadComments(pageUtil, null, insertTime).getList();

            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
            Comments comments = null;
            for (int i = 0; i < list.size(); i++) {
                row = sheet.createRow((int) i + 1);
                comments = (Comments) list.get(i);
                String format = sd.format(comments.getInsertTime());
                row.createCell(0).setCellValue(format);//查询日期
                if (Integer.valueOf(comments.getType()) == 0) {
                    row.createCell(1).setCellValue(comments.getContact());
                } else {
                    row.createCell(1).setCellValue(DESEncrypt.jieMiUsername(comments.getUsers().getUsername()));
                }


                if (Integer.valueOf(comments.getType()) == 0) {
                    row.createCell(2).setCellValue(comments.getName());
                } else {
                    row.createCell(2).setCellValue(comments.getUsers().getUsersInfo().getRealName());
                }

                row.createCell(3).setCellValue(!QwyUtil.isNullAndEmpty(comments.getTypename()) ? comments.getTypename() : "");
                row.createCell(4).setCellValue(!QwyUtil.isNullAndEmpty(comments.getContent()) ? comments.getContent() : "");

            }

            String pathname = QwyUtil.fmyyyyMMddHHmmss3.format(new Date()) + "_find_users_comment.xls";
            String realPath = request.getServletContext().getRealPath("/report/" + pathname);
            log.info("用户评论表表地址：" + realPath);
            FileOutputStream fout = new FileOutputStream(realPath);
            wb.write(fout);
            fout.close();
            response.getWriter().write("/report/" + pathname);
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
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
}
