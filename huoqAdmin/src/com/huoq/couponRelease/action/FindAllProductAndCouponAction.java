package com.huoq.couponRelease.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;

import com.huoq.common.action.BaseAction;
import com.huoq.couponRelease.bean.FindAllProductAndCouponBean;
import com.huoq.orm.CouponReleaseRule;
import com.huoq.orm.CouponReleaseRuleSales;
import com.huoq.orm.Product;
import com.opensymphony.xwork2.ActionContext;

/**
 * 
 * @author Alex
 * 查询出所有的产品和理财券
 */
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product")
@Results({ @Result(name = "couponReleaseRule", value = "/Product/couponRule/addCouponReleaseRule.jsp"),
        @Result(name = "recharge", value = "/Product/User/recharge.jsp"),
        @Result(name = "getMoney", value = "/Product/User/getMoney.jsp"),
        @Result(name = "login", value = "/Product/login.jsp"),
        @Result(name = "error", value = "/Product/error.jsp"),
        @Result(name = "findProductAndCoupon", value = "/Product/couponRule/CouponReleaseRuleSales.jsp"),
        @Result(name = "SUCCESS", value = "/Product/User/myAccount!showMyAccount.action", type = org.apache.struts2.dispatcher.ServletRedirectResult.class)
})
public class FindAllProductAndCouponAction extends BaseAction{
	
	@Resource
	private FindAllProductAndCouponBean findBean;
	
	private CouponReleaseRule crr = new CouponReleaseRule();

	/**
	 * 查询出所有的产品和理财券
	 * @return
	 */
	public String findProductAndCoupon(){
		//优惠券列表
		List<CouponReleaseRule> crrlist = null;
		//销售理财券列表
		List<CouponReleaseRuleSales> crrslist2= new ArrayList<>();
		ArrayList<Integer> iplist = new ArrayList<>();
		try {
			//查询出产品和理财券对应表的所有数据
			crrlist = findBean.findAllProductAndCoupon();
			List<CouponReleaseRuleSales> crrslist = findBean.findallRuleSales();
			
			for (CouponReleaseRule crr : crrlist) {
				Integer id = crr.getId();
				iplist.add(id);
			}
			for (CouponReleaseRuleSales crrs : crrslist) {
				Integer id = crrs.getId();
				/*Set<Product> products = crrs.getProducts();
				Iterator<Product> iterator = products.iterator();
				while (iterator.hasNext()) {
					Product next = iterator.next();
				}*/
				if (iplist.contains(id)) {
					crrslist2.add(crrs);
				}
			}
			//将理财规则表传递到前台
			ActionContext.getContext().put("findlist", crrlist);
			//将销售优惠传递到前台
			ActionContext.getContext().put("findplist", crrslist2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "findProductAndCoupon";
	}
	public CouponReleaseRule getCrr() {
		return crr;
	}
	public void setCrr(CouponReleaseRule crr) {
		this.crr = crr;
	}
	public FindAllProductAndCouponBean getFindBean() {
		return findBean;
	}
	public void setFindBean(FindAllProductAndCouponBean findBean) {
		this.findBean = findBean;
	}
}
