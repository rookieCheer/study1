package com.huoq.account.action;

import java.io.IOException;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;

import com.huoq.account.bean.MyAccountBean;
import com.huoq.account.bean.UserRechargeBean;
import com.huoq.account.bean.ValidateBean;
import com.huoq.account.dao.UserRechargeDAO;
import com.huoq.common.action.BaseAction;
import com.huoq.common.bean.SystemConfigBean;
import com.huoq.common.bean.YiBaoPayBean;
import com.huoq.common.util.QwyUtil;
import com.huoq.login.bean.RegisterUserBean;
import com.huoq.thread.action.CheckProductStatusThread;

/**
 * 用户充值提现Action层;<br>
 * 接收充值的金额;接收提现的金额;
 * 
 * @author qwy
 *
 * @createTime 2015-4-27上午9:58:44
 */
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product")
// 理财产品
@Results({ @Result(name = "myAccount", value = "/Product/User/myAccount.jsp"), @Result(name = "recharge", value = "/Product/User/recharge.jsp"),
		@Result(name = "getMoney", value = "/Product/User/getMoney.jsp"), @Result(name = "login", value = "/Product/login.jsp"), @Result(name = "error", value = "/Product/error.jsp"),
		@Result(name = "yeepay", value = "/Product/chinabankpay/reqpay.jsp"),
		@Result(name = "SUCCESS", value = "/Product/User/myAccount!showMyAccount.action", type = org.apache.struts2.dispatcher.ServletRedirectResult.class)

})
public class CallBackAction extends BaseAction {
	private static Logger log = Logger.getLogger(CallBackAction.class);

	@Resource
	private UserRechargeBean bean;
	@Resource
	private UserRechargeDAO dao;
	@Resource
	private ValidateBean validateBean;
	@Resource
	private MyAccountBean myAccountBean;
	@Resource
	private YiBaoPayBean yiBaoPayBean;
	@Resource
	private SystemConfigBean systemConfigBean;

	private String rechargeMoney;
	private String payType = "0";
	private Integer currentPage = 1;
	private Integer pageSize = 25;
	private String payPassword;
	private String pd_FrpId;
	private String recordType;
	@Resource
	private RegisterUserBean registerUserBean;
	@Resource
	private UserRechargeBean userRechargeBean;
	/**
	 * 扫描是否需要自动续约的理财产品 add by qwy 2016-09-25
	 */
	@Resource
	private CheckProductStatusThread checkProductStatusThread;



	


	
	
	/**
	 * 启动自动发布理财产品线程
	 * @author 覃文勇
	 * @date 2016-09-25
	 * @return
	 */
	public String autoReleaseProductOperate() {
		String json = "";
		try {
			new Thread(checkProductStatusThread).start();
			String flag = request.getParameter("flag");
			log.info("--------------------启动续标线程------接口调用-----【"+flag+"】");
		} catch (Exception e) {
			log.error("autoReleaseProductOperate",e);
		}
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			log.error("autoReleaseProductOperate",e);
		}
		return null;
	}

	public String getRechargeMoney() {
		return rechargeMoney;
	}

	public void setRechargeMoney(String rechargeMoney) {
		this.rechargeMoney = rechargeMoney;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
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

	public String getPayPassword() {
		return payPassword;
	}

	public void setPayPassword(String payPassword) {
		this.payPassword = payPassword;
	}

	public String getPd_FrpId() {
		return pd_FrpId;
	}

	public void setPd_FrpId(String pd_FrpId) {
		this.pd_FrpId = pd_FrpId;
	}

	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

}
