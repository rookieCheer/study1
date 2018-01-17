package com.huoq.account.action;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;

import com.huoq.account.bean.FundRecordBean;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.FundRecord;
import com.huoq.orm.Users;
import com.huoq.orm.UsersLogin;
import com.huoq.product.action.IndexAction;

/**
 * @author qwy
 *
 * @createTime 2015-5-6下午6:34:29
 */
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/User")
//理财产品
@Results({ 
	@Result(name = "moneyDetails", value = "/Product/User/moneyDetails.jsp"),
	@Result(name = "login", value = "/Product/login.jsp"),
	@Result(name = "error", value = "/Product/error.jsp")
})
public class FundRecordAction extends IndexAction {
	private static Logger log = Logger.getLogger(FundRecordAction.class); 
	private Integer currentPage = 1;
	private Integer pageSize = 20;
	private String type="all";
	private String startDate="";
	private String endDate="";
	@Resource
	private FundRecordBean bean;
	
	/**获取资金流水记录;
	 * @return
	 */
	public String loadFundRecord(){
		try {
			UsersLogin usersLogin  = (UsersLogin)getRequest().getSession().getAttribute("usersLogin");
			if(QwyUtil.isNullAndEmpty(usersLogin)){
				return "login";
			}
			PageUtil<FundRecord> pageUtil = new PageUtil<FundRecord>();
			pageUtil.setPageSize(pageSize);
			pageUtil.setCurrentPage(currentPage);
			pageUtil.setPageUrl(getRequest().getServletContext().getContextPath()+"/Product/User/fundRecord!loadFundRecord.action?type="+type+"&startDate="+startDate+"&endDate="+endDate);
			pageUtil = bean.loadFundRecord(usersLogin.getUsersId(),null,type,startDate,endDate,pageUtil);
			if(!QwyUtil.isNullAndEmpty(pageUtil)){
				getRequest().setAttribute("pageUtil", pageUtil);
				List<FundRecord> list = pageUtil.getList();
				if(!QwyUtil.isNullAndEmpty(list)){
					getRequest().setAttribute("fundRecordList", list);
				}
			}
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return "moneyDetails";
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
 
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

}
