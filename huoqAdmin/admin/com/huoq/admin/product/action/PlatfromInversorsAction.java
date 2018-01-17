package com.huoq.admin.product.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;

import com.huoq.account.bean.InvestorsRecordBean;
import com.huoq.common.action.BaseAction;
import com.huoq.common.bean.PlatformBean;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.Platform;
import com.huoq.orm.PlatformInvestDetail;
import com.huoq.orm.QdtjPlatform;
import com.huoq.orm.UsersAdmin;

/**平台投资情况;
 * @author qwy
 *
 * @createTime 2015-4-27下午3:51:34
 */
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/Admin")
	@Results({ @Result(name = "platfromInversors", value = "/Product/Admin/operationManager/platformInvestDetail.jsp"),
	@Result(name = "err", value = "/Product/Admin/err.jsp")
})
public class PlatfromInversorsAction extends BaseAction {
	@Resource
	InvestorsRecordBean bean;
	@Resource
	private PlatformBean platformBean;
	private String payTime;
	private String status;
	private String insertTime;
	private String platform;
	/**
	 * 投资统计
	 * @return
	 */
	public String loadPlatfromInversors() {
		String json="";
		try {
			 UsersAdmin admin=(UsersAdmin)getRequest().getSession().getAttribute("usersAdmin");
				if(QwyUtil.isNullAndEmpty(admin)){
					json = QwyUtil.getJSONString("err", "管理员未登录");
					QwyUtil.printJSON(getResponse(), json);
					//管理员没有登录;
					return null;
				}
				String superName="0EA5D6BC23E8EEC78F62546B9F68BABFA96976B775889BA625DB6D764FD0DBD42A1C0F45F85B0DE8";
				if(!superName.equals(admin.getUsername())){
				if(isExistsQX("平台投资情况", admin.getId())){
					getRequest().setAttribute("err", "您没有操作该功能的权限!");
					return "err";
				}
				}
		//	String platform= getRequest().getParameter("platform");
			List<Object[]> list= bean.findPlatformInvestDetail(payTime, status,insertTime,platform);
			List<Object[]> hj = bean.getHj(payTime,platform);
//			Platform platform=platformBean.getPlatform();
//			String collectMoney= bean.findAllInvestDetail(payTime, status,insertTime);
			getRequest().setAttribute("list", list);
			getRequest().setAttribute("hj", hj.get(0));
//			getRequest().setAttribute("collectMoney",new BigDecimal(collectMoney));
//			getRequest().setAttribute("collectMoney",QwyUtil.calcNumber(platform.getCollectMoney(), 0.01, "*")+"");
//			getRequest().setAttribute("status", status);
			getRequest().setAttribute("payTime", payTime);
//			getRequest().setAttribute("insertTime", insertTime);
		} catch (Exception e) {
			log.error("操作异常: ",e);
			log.error("操作异常: ",e);
		}
		return "platfromInversors";
	}
	
	/**获取平台信息;
	 * @return
	 */
	public String getPlatformInfo(){
		Platform platform=platformBean.getPlatform(null);
		getRequest().setAttribute("myPlatform", platform);
		return null;
	}
	public String getPayTime() {
		return payTime;
	}
	public void setPayTime(String payTime) {
		this.payTime = payTime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public String getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(String insertTime) {
		this.insertTime = insertTime;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}
}
