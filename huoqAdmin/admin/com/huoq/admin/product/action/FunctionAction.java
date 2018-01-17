package com.huoq.admin.product.action;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;

import com.huoq.common.action.BaseAction;
import com.huoq.common.bean.BaoFuBean;
import com.huoq.common.bean.YiBaoPayBean;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.CzRecord;

/**后台管理--启动线程
 * @author qwy
 *
 * @createTime 2015-4-27下午3:51:34
 */
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/Admin")
//发布产品页面
@Results({ @Result(name = "function", value = "/Product/Admin/functionManager/function.jsp")
})
public class FunctionAction extends BaseAction {
	private String orderid ="";
	private String usersId = "";
	/**
	 * 易宝支付第三方接口Bean层;
	 */
	@Resource
	private YiBaoPayBean yiBaoPayBean; 
	
	/**查询支付结果;充值结果;
	 * @return
	 */
	public String queryPay(){
		String json = "";
		try {
			CzRecord czRecord = new CzRecord();
			Map<String, Object> result = BaoFuBean.queryCzRecord(orderid, czRecord.getInsertTime(), UUID.randomUUID().toString());
			json = QwyUtil.getJSONString("ok", result.toString());
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("error", "查询失败");
		}
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return null;
	}
	
	/**提现记录查询;
	 * @return
	 */
	public String withdrawQuery(){
		String json = "";
		try {
			//更新理财产品;包括:更新之后,结算常规的理财产品和新手理财产品;
			String result = yiBaoPayBean.withdrawQuery(orderid);
			json = QwyUtil.getJSONString("ok", result.replaceAll("\"", "'"));
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("error", "查询失败");
		}
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			log.error("操作异常: ",e);
		}
		return null;
	}
	
	/**根据用户ID,查询绑卡记录;
	 * @return
	 */
	public String bindList(){
		String json = "";
		try {
			//更新理财产品;包括:更新之后,结算常规的理财产品和新手理财产品;
			boolean isOk = QwyUtil.isOnlyNumber(usersId);
			if(isOk){
				String result = yiBaoPayBean.bindList(Long.parseLong(usersId));
				json = QwyUtil.getJSONString("ok", result.replaceAll("\"", "'"));
			}else{
				json = QwyUtil.getJSONString("error", "无绑卡记录");
			}
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("error", "查询失败");
		}
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			log.error("操作异常: ",e);
		}
		return null;
	}

	public String getOrderid() {
		return orderid;
	}

	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}

	public String getUsersId() {
		return usersId;
	}

	public void setUsersId(String usersId) {
		this.usersId = usersId;
	}



}
