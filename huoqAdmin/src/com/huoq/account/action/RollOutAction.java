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
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.InterestDetails;
import com.huoq.orm.Investors;
import com.huoq.orm.Product;
import com.huoq.orm.Users;
import com.huoq.orm.UsersInfo;
import com.huoq.orm.UsersLogin;
import com.huoq.product.action.IndexAction;
import com.huoq.product.bean.ProductCategoryBean;


/**
 * 转出Action层
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
public class RollOutAction extends IndexAction {
	private static Logger log = Logger.getLogger(RollOutAction.class); 
	@Resource
	RollOutBean bean;
	@Resource
	CoinpPurseBean coinpPurseBean;
	private String outMoney;
	/**
	 * 保存转出记录
	 */
	public String saveRollOut(){
		String json="";
		try {
			UsersLogin usersLogin  = (UsersLogin)getRequest().getSession().getAttribute("usersLogin");
			if(QwyUtil.isNullAndEmpty(usersLogin)){
				json=QwyUtil.getJSONString("noLogin", "登录已失效，请先登录");
				QwyUtil.printJSON(response, json);
				return null;
			}
			if(QwyUtil.isNullAndEmpty(outMoney)||!QwyUtil.isPrice(outMoney)){
				json=QwyUtil.getJSONString("error", "输入信息不正确");
				QwyUtil.printJSON(response, json);
				return null;
			}else{
				Double money=QwyUtil.calcNumber(100, outMoney, "*").doubleValue();
				String result=bean.rollOut(usersLogin.getUsersId(),money);
				if("ok".equals(result)){
					json=QwyUtil.getJSONString("ok", "转出成功");
					QwyUtil.printJSON(response, json);
					return null;
				}else if("error".equals(result)){
					json=QwyUtil.getJSONString("error", "转出失败");
					QwyUtil.printJSON(response, json);
					return null;
				}else{
					json=QwyUtil.getJSONString("error",result);
					QwyUtil.printJSON(response, json);
					return null;
				}
			}
		} catch (Exception e) {
			json=QwyUtil.getJSONString("error", "系统错误");
			log.error("saveRollOut", e);
		}
		try {
			QwyUtil.printJSON(response, json);
		} catch (IOException e) {
			log.error("saveRollOut",e);   
		}
		return null;
	}
	
	/**
	 * 跳转到转出界面
	 */
	public String toRollOut(){
		try {
			UsersLogin usersLogin  = (UsersLogin)getRequest().getSession().getAttribute("usersLogin");
			if(QwyUtil.isNullAndEmpty(usersLogin)){
				return "login";
			}
			request.setAttribute("usersId",usersLogin.getUsersId());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return "rollOut";
	}

	public String getOutMoney() {
		return outMoney;
	}

	public void setOutMoney(String outMoney) {
		this.outMoney = outMoney;
	}
	
	
}
