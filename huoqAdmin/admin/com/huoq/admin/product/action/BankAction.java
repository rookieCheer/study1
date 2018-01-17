package com.huoq.admin.product.action;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;

import com.huoq.admin.product.bean.BankBean;
import com.huoq.admin.product.dao.BankDAO;
import com.huoq.common.action.BaseAction;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.Bank;
import com.huoq.orm.UsersAdmin;

/**
 * 后台管理——银行卡限额记录
 *   wxl  2017年3月21日15:48:14
 */
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/Admin")
@Results({ @Result(name = "bank", value = "/Product/Admin/productManager/bankDetail.jsp")
})
public class BankAction extends BaseAction {

	@Resource
	private BankBean bankBean;
	@Resource
	private BankDAO bankDAO;

	private Bank bank;
	private String status;
	private String name;
	private String insertTime;
	private Long id;
	
	protected static Logger log = Logger.getLogger(BaseAction.class);

	/**
	 * 加载银行限额明细表
	 * @return
	 */
	public String loadBankDetail() {
		String json = "";
		try {

			UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
			if (QwyUtil.isNullAndEmpty(users)) {
				json = QwyUtil.getJSONString("err", "管理员未登录");
				QwyUtil.printJSON(getResponse(), json);
				// 管理员没有登录;
				return null;
			}
			
			List<Bank> list = bankBean.getBankList();
			if (!QwyUtil.isNullAndEmpty(list)) {
				getRequest().setAttribute("list",list);
			}
			return "bank";

		} catch (Exception e) {
			log.error("操作异常: ", e);
			json = QwyUtil.getJSONString("err", "查询记录异常");
		}
		return null;
	}
	
	/**
	 * 更改状态
	 * @return
	 */
	public String updateStatus() {
		String json = "";
		try {
			UsersAdmin users = (UsersAdmin) getRequest().getSession()
					.getAttribute("usersAdmin");
			if (QwyUtil.isNullAndEmpty(users)) {
				json = QwyUtil.getJSONString("err", "管理员未登录");
				QwyUtil.printJSON(getResponse(), json);
				//管理员没有登录;
				return null;
			}
			if (!QwyUtil.isNullAndEmpty(id)) {
				
				if(bankBean.updateStatusById(id)){
					request.setAttribute("update", "ok");
					json = QwyUtil.getJSONString("ok", "成功");
					QwyUtil.printJSON(getResponse(), json);
					return null;
				}
			}
			
		} catch (Exception e) {
			log.error("操作异常: ",e);
			log.error("操作异常: ",e);
		}
		json = QwyUtil.getJSONString("error", "失败");
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			log.error("操作异常: ",e);
			log.error("操作异常: ",e);
		}
		return null;	
		
	}
	
	/**
	 * 更改银行卡限额明细
	 * @return
	 */
	public String modifyBank(){
		String json="";
		try {
			if(!QwyUtil.isNullAndEmpty(bank)){
				if(bankBean.modifyBank(bank.getId(),bank.getTxQuota(),bank.getCzQuota(),bank.getBankNote())){
					json = QwyUtil.getJSONString("ok","修改成功");
				}else{
					json = QwyUtil.getJSONString("error","修改失败");
				}
			}else{
				json = QwyUtil.getJSONString("error","bank不能为空");
			}
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("error", "bank修改异常");
		}
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			log.error("操作异常: ",e);
		}
		return null;
	}
	
	
	
	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


}
