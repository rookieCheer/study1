package com.huoq.admin.product.action;

import com.huoq.admin.product.bean.UserFundsDiffBean;
import com.huoq.common.action.BaseAction;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.FreezeMoneyUsersInfo;
import com.huoq.orm.Investors;
import com.huoq.orm.UsersAdmin;
import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;

import javax.annotation.Resource;
import java.util.List;

/**
 *  用户账户偏差
 * Created by yks on 2016/12/29.
 */
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/Admin")
@Results({@Result(name = "tzdiff",value = "/Product/Admin/fundsManager/fundsDiff.jsp")})
public class UserFundsDiffAction extends BaseAction{


    private Integer currentPage = 1;
    private Integer pageSize = 100;

    @Resource
    private UserFundsDiffBean userFundsDiffBean;


    /**
     * 用户资金异常偏差列表显示
     * @return
     */
    public String listFundDiff111111(){
        request = getRequest();

        UsersAdmin users = (UsersAdmin)getRequest().getSession().getAttribute("usersAdmin");
        if(QwyUtil.isNullAndEmpty(users)){
            return "login";
        }
        List<Investors> list = null;
        try {
            list = userFundsDiffBean.getErroDiffInvestors(currentPage,pageSize);
            if (!QwyUtil.isNullAndEmpty(list)) {
                int totalCount = list.size();//总记录数
                int pageCount = 0;//总页数
                if (totalCount % pageSize ==0){
                    pageCount = totalCount / pageSize;
                }else {
                    pageCount = (totalCount / pageSize) + 1;
                }
                getRequest().setAttribute("list",list);
                getRequest().setAttribute("totalCount",totalCount);
                getRequest().setAttribute("pageCount",pageCount);
                return "tzdiff";
            }
        } catch (Exception e) {
            log.error("操作异常: ",e);
        }
        return "tzdiff";
    }

    /**
     * 用户资金异常偏差列表显示
     * @return
     */
    public String listFundDiff(){
        request = getRequest();

        UsersAdmin users = (UsersAdmin)getRequest().getSession().getAttribute("usersAdmin");
        if(QwyUtil.isNullAndEmpty(users)){
            return "login";
        }
        List<FreezeMoneyUsersInfo> list = null;
        try {
            list = userFundsDiffBean.getFreezeMoneyUsersInfo(currentPage,pageSize);
            if (!QwyUtil.isNullAndEmpty(list)) {
                int totalCount = list.size();//总记录数
                int pageCount = 0;//总页数
                if (totalCount % pageSize ==0){
                    pageCount = totalCount / pageSize;
                }else {
                    pageCount = (totalCount / pageSize) + 1;
                }
                getRequest().setAttribute("list",list);
                getRequest().setAttribute("totalCount",totalCount);
                getRequest().setAttribute("pageCount",pageCount);
                return "tzdiff";
            }
        } catch (Exception e) {
            log.error("操作异常: ",e);
        }
        return "tzdiff";
    }

    public Integer getCurrentPage() {
        return this.currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

}
