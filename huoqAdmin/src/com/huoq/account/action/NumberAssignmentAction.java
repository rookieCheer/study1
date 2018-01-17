package com.huoq.account.action;

import java.util.List;

import javax.annotation.Resource;

import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;

import com.huoq.common.action.BaseAction;
import com.huoq.common.util.QwyUtil;
import com.huoq.login.bean.RegisterUserBean;
import com.huoq.orm.Users;
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/buyInfo")
@Results({ @Result(name = "productInfo", value = "/Product/Admin/BuyInfo/buyInfo.jsp"),
		@Result(name = "ok", value = "/Product/Admin/BuyInfo/test.jsp")
})
/**
 * 补全缺失的号码归属地
 * @author Administrator
 *
 */
public class NumberAssignmentAction extends BaseAction {
	@Resource
	private RegisterUserBean bean;
	
	public String numberAssignment() throws Exception {
		//获取没有归属地的用户
		List<Users> findUsers = bean.findUsers(true, 1, 50);
	
		//重新设定归属地
		List<Users> setMobileLocation = bean.setMobileLocation(findUsers);
		if (!QwyUtil.isNullAndEmpty(setMobileLocation)) {
			getRequest().setAttribute("ok", "补全成功");
			return "ok";
		}
		return "";
	}
	
}
