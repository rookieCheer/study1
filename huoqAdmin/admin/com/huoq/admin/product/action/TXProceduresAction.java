package com.huoq.admin.product.action;

import javax.annotation.Resource;

import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;

import com.huoq.admin.product.bean.CZProceduresBean;
import com.huoq.admin.product.bean.TXProceduresBean;
import com.huoq.admin.product.dao.CZProcedures;
import com.huoq.admin.product.dao.TXProcedures;
import com.huoq.common.action.BaseAction;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.UsersAdmin;
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/Admin")
@Results({
	@Result(name = "txProcedures", value = "/Product/Admin/operationManager/txProcedures.jsp"),
	@Result(name = "extractionrate", value = "/Product/Admin/operationManager/extractionrate.jsp")
	
})

public class TXProceduresAction extends BaseAction {
	private String insertTime;
	private Integer pageSize = 31;
	public Integer getPageSize() {
		return pageSize;
	}
	private Integer currentPage = 1;
	public Integer getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	@Resource
	private TXProceduresBean bean;
	private PageUtil<TXProcedures> pageUtil;
	public String showTXProcedures(){
		request = getRequest();
	
		UsersAdmin users = (UsersAdmin)getRequest().getSession().getAttribute("usersAdmin");
		if(QwyUtil.isNullAndEmpty(users)){
			return "login";
		}
		PageUtil<TXProcedures> pageUtil = new PageUtil<TXProcedures>();
		pageUtil.setCurrentPage(currentPage);
		pageUtil.setPageSize(pageSize);
		StringBuffer url = new StringBuffer();
		url.append(getRequest().getServletContext().getContextPath());
		url.append("/Product/Admin/tXProcedures!showTXProcedures.action?");
		if(!QwyUtil.isNullAndEmpty(insertTime)){
			url.append("&insertTime=");
			url.append(insertTime);
		}
		pageUtil.setPageUrl(url.toString());
		
		pageUtil = bean.loadTXProcedures(insertTime,pageUtil);
		getRequest().setAttribute("insertTime", insertTime);
		if (!QwyUtil.isNullAndEmpty(pageUtil)) {
			getRequest().setAttribute("pageUtil", pageUtil);
			getRequest().setAttribute("list", pageUtil.getList());
			return "txProcedures";
			}
		return null;
	}
	
	
	public String loadExtractionrate(){
		request = getRequest();
	
		UsersAdmin users = (UsersAdmin)getRequest().getSession().getAttribute("usersAdmin");
		if(QwyUtil.isNullAndEmpty(users)){
			return "login";
		}
		PageUtil<TXProcedures> pageUtil = new PageUtil<TXProcedures>();
		pageUtil.setCurrentPage(currentPage);
		pageUtil.setPageSize(pageSize);
		StringBuffer url = new StringBuffer();
		url.append(getRequest().getServletContext().getContextPath());
		url.append("/Product/Admin/tXProcedures!loadExtractionrate.action?");
		if(!QwyUtil.isNullAndEmpty(insertTime)){
			url.append("&insertTime=");
			url.append(insertTime);
		}
		pageUtil.setPageUrl(url.toString());
		pageUtil = bean.loadExtractionrate(insertTime,pageUtil);
		getRequest().setAttribute("insertTime", insertTime);
		if (!QwyUtil.isNullAndEmpty(pageUtil)) {
			getRequest().setAttribute("pageUtil", pageUtil);
			getRequest().setAttribute("list", pageUtil.getList());
			return "extractionrate";
			}
		return null;
	}
	
	

	public String getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(String insertTime) {
		this.insertTime = insertTime;
	}
}
