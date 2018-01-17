package com.huoq.account.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;

import com.huoq.account.bean.BuyCarBean;
import com.huoq.account.bean.CoinpPurseBean;
import com.huoq.account.bean.ConfirmInvestBean;
import com.huoq.account.bean.RollOutBean;
import com.huoq.account.bean.SendRatesBean;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.InterestDetails;
import com.huoq.orm.Investors;
import com.huoq.orm.Product;
import com.huoq.orm.SendRates;
import com.huoq.orm.Users;
import com.huoq.orm.UsersInfo;
import com.huoq.orm.UsersLogin;
import com.huoq.product.action.IndexAction;
import com.huoq.product.bean.ProductCategoryBean;


/**
 * 发息Action层
 * @author qwy
 *
 * 2015-04-20 19:45:50
 */
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/User")
//理财产品
@Results({
	@Result(name = "login", value = "/Product/login.jsp"),
	@Result(name = "rollOut", value = "/Product/coin_purse/rollOut.jsp"),
})
public class SendRatesAction extends IndexAction {
	private static Logger log = Logger.getLogger(SendRatesAction.class); 
	@Resource
	SendRatesBean sendRatesBean;
	private Integer currentPage = 1;
	private Integer pageSize = 20;
	/**
	 * 发息记录
	 */
	public String findSendRates(){
		String json="";
		try {
			UsersLogin usersLogin  = (UsersLogin)getRequest().getSession().getAttribute("usersLogin");
			if(QwyUtil.isNullAndEmpty(usersLogin)){
				return "login";
			}
			PageUtil<SendRates> pageUtil = new PageUtil<SendRates>();
			pageUtil.setCurrentPage(currentPage);
			pageUtil.setPageSize(pageSize);
			pageUtil=sendRatesBean.findPageUtil(pageUtil, usersLogin.getUsersId(),null);
			StringBuffer url = new StringBuffer();
			url.append(getRequest().getServletContext().getContextPath());
			url.append("/Product/User/sendRates!findSendRates.action?");
			pageUtil.setPageUrl(url.toString());
			request.setAttribute("pageUtil", pageUtil);
			request.setAttribute("usersId",usersLogin.getUsersId());
			
		} catch (Exception e) {
			json=QwyUtil.getJSONString("error", "系统错误");
			log.error(e.getMessage(), e);
		}
		try {
			QwyUtil.printJSON(response, json);
		} catch (IOException e) {
			log.error("发息记录findSendRates", e);
		}
		return null;
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
