package com.huoq.admin.product.action;

import com.huoq.admin.product.bean.UsersCompanyBean;
import com.huoq.common.action.BaseAction;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.UsersAdmin;
import com.huoq.orm.UsersCompany;
import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 企业用户管理
 * Created by yks on 2016/10/24.
 */
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/Admin")
@Results({
        @Result(name = "usersCompanyList", value = "/Product/Admin/usersCompanyManager/usersCompanyList.jsp"),
        @Result(name = "err", value = "/Product/Admin/err.jsp")
})
public class UsersCompanyAction extends BaseAction {

    private PageUtil<UsersCompany> pageUtil;
    private Integer currentPage = 1;
    private Integer pageSize = 50;
    private String username = ""; //企业用户
    private String insertTime;
    private UsersCompany usersCompany;

    @Resource
    private UsersCompanyBean bean;


    /**
     * 企业用户列表显示
     *
     * @return
     */
    public String listUsersCompany() {
        String json = "";
        log.info("执行企业用户列表显示功能");
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
                if (isExistsQX("企业用户记录", users.getId())) {
                    getRequest().setAttribute("err", "您没有操作该功能的权限!");
                    return "err";
                }
            }
            PageUtil<UsersCompany> pageUtil = new PageUtil<UsersCompany>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(pageSize);
            StringBuilder url = new StringBuilder();
            url.append(getRequest().getServletContext().getContextPath());
            url.append("/Product/Admin/usersCompany!listUsersCompany.action?");
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                url.append("&insertTime=");
                url.append(insertTime);
            }
            if (!QwyUtil.isNullAndEmpty(username)) {
                username = username.trim();
                url.append("&username=");
                url.append(username);
            }
            pageUtil.setPageUrl(url.toString());
            pageUtil = bean.loadUsersCompany(pageUtil, username, insertTime);
            if (!QwyUtil.isNullAndEmpty(pageUtil)) {
                getRequest().setAttribute("insertTime", insertTime);
                getRequest().setAttribute("username", username);
                getRequest().setAttribute("pageUtil", pageUtil);
                getRequest().setAttribute("list", pageUtil.getList());
                return "usersCompanyList";
            }
        } catch (Exception e) {
            log.error("操作异常: ", e);
            json = QwyUtil.getJSONString("err", "查询记录异常");
        }
        return "usersCompanyList";
    }


    /**
     * 添加企业用户
     *
     * @return
     */
    public String saveUsersCompany() {
        log.info("执行添加企业用户功能");
        String json = "";
        try {
            UsersAdmin admin = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(admin)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                // 管理员没有登录;
                return null;
            }
            if (!QwyUtil.isNullAndEmpty(usersCompany)) {
                if (!QwyUtil.verifyPhone(usersCompany.getUsername())) {
                    json = QwyUtil.getJSONString("error", "手机号有误：" + usersCompany.getUsername());
                }
                if (QwyUtil.isNullAndEmpty(json)) {// 判断手机格式
                    if (!QwyUtil.isNullAndEmpty(usersCompany.getNote())) {
                        if (usersCompany.getNote().length() > 500) {
                            json = QwyUtil.getJSONString("error", "备注内容过长");
                        } else {
                            Map<String, String> result = bean.addUsersCompany(usersCompany);
                            if (result.containsKey("ok")) {
                                json = QwyUtil.getJSONString("ok", result.get("ok"));
                            } else {
                                json = QwyUtil.getJSONString("error", result.get("error"));
                            }
                        }
                    } else {
                        json = QwyUtil.getJSONString("error", "备注内容不能为空");
                    }
                }
            } else {
                json = QwyUtil.getJSONString("error", "填写的用户名不能为空");
            }
        } catch (Exception e) {
            log.error("操作异常: ",e);
            json = QwyUtil.getJSONString("error", "操作异常");
        }
        try {
            QwyUtil.printJSON(getResponse(), json);
        } catch (Exception e) {
            log.error("操作异常: ",e);
        }
        return null;
    }


    public PageUtil<UsersCompany> getPageUtil() {
        return pageUtil;
    }

    public void setPageUtil(PageUtil<UsersCompany> pageUtil) {
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(String insertTime) {
        this.insertTime = insertTime;
    }

    public UsersCompany getUsersCompany() {
        return usersCompany;
    }

    public void setUsersCompany(UsersCompany usersCompany) {
        this.usersCompany = usersCompany;
    }

    public UsersCompanyBean getBean() {
        return bean;
    }

    public void setBean(UsersCompanyBean bean) {
        this.bean = bean;
    }
}
