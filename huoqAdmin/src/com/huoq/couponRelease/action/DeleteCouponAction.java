/**
 * hongshiwl.com Inc.
 * Copyright (c) 2015-2017 All Rights Reserved.
 */
package com.huoq.couponRelease.action;

import javax.annotation.Resource;

import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.huoq.common.action.BaseAction;
import com.huoq.common.util.QwyUtil;
import com.huoq.couponRelease.bean.CouponReleaseRuleBean;
import com.huoq.couponRelease.bean.DeleteCouponBean;
import com.huoq.orm.CouponReleaseRule;
import com.huoq.orm.CouponReleaseRuleItemRed;
import com.opensymphony.xwork2.ModelDriven;

/**
 * @author guoyin.yi
 * @version $Id: CouponReleaseRuleAction.java, v 0.1  2017/9/20 Exp $
 */
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product")
@Results({ @Result(name = "couponReleaseRule", value = "/Product/couponRule/addCouponReleaseRule.jsp"),
        @Result(name = "deleteCpupon", value = "/Product/User/recharge.jsp"),
        @Result(name = "getMoney", value = "/Product/User/getMoney.jsp"),
        @Result(name = "login", value = "/Product/login.jsp"),
})
public class DeleteCouponAction extends BaseAction {
	String json = "";
	private CouponReleaseRule crr = new CouponReleaseRule();
	@Resource
	private DeleteCouponBean bean;
	/**
	 * 删除优惠券
	 * @return
	 */
	public String deleteCoupon(){
		try {
			String id = getRequest().getParameter("id");
			int nid = Integer.parseInt(id);
			CouponReleaseRule crrs = bean.getById(nid);
			if (!QwyUtil.isNullAndEmpty(crrs)) {
				bean.delete(crrs);
				request.setAttribute("delete", "ok");
				json = QwyUtil.getJSONString("ok", "成功");
				QwyUtil.printJSON(getResponse(), json);
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		json = QwyUtil.getJSONString("error", "失败");
		return null;
	}
	public CouponReleaseRule getCrr() {
		return crr;
	}
	public void setCrr(CouponReleaseRule crr) {
		this.crr = crr;
	}
	public DeleteCouponBean getBean() {
		return bean;
	}
	public void setBean(DeleteCouponBean bean) {
		this.bean = bean;
	}
}
