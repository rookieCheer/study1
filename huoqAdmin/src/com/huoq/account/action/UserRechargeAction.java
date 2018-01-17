package com.huoq.account.action;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;



import com.alibaba.fastjson.JSON;
import com.huoq.account.bean.MyAccountBean;
import com.huoq.account.bean.UserRechargeBean;
import com.huoq.account.bean.ValidateBean;
import com.huoq.account.dao.UserRechargeDAO;
import com.huoq.common.action.BaseAction;
import com.huoq.common.lianlian.pay.utils.PayOrder;
import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.common.yeepay.Configuration;
import com.huoq.common.yeepay.PaymentForOnlineService;
import com.huoq.login.bean.RegisterUserBean;
import com.huoq.orm.Account;
import com.huoq.orm.BankCard;
import com.huoq.orm.CzRecord;
import com.huoq.orm.TxRecord;
import com.huoq.orm.Users;
import com.huoq.orm.UsersInfo;
import com.huoq.orm.UsersLogin;
import com.huoq.orm.Yeepay;

/**用户充值提现Action层;<br>
 * 接收充值的金额;接收提现的金额;
 * @author qwy
 *
 * @createTime 2015-4-27上午9:58:44
 */
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/User")
//理财产品
@Results({ 
	@Result(name = "myAccount", value = "/Product/User/myAccount.jsp"),
//	@Result(name = "recharge", value = "/Product/User/recharge.jsp"),
	@Result(name = "recharge", value = "/Product/User/recharge__LianPay.jsp"),
//	@Result(name = "getMoney", value = "/Product/User/getMoney.jsp"),
	@Result(name = "getMoney", value = "/Product/User/getMoney__LLpay.jsp"),
	@Result(name = "login", value = "/Product/login.jsp"),
	@Result(name = "error", value = "/Product/page_404.jsp"),
	@Result(name = "yeepay", value = "/Product/chinabankpay/reqpay.jsp"),
	@Result(name = "llreqpay", value = "/Product/chinabankpay/llreqpay.jsp"),
	@Result(name = "SUCCESS", value = "/Product/User/myAccount!showMyAccount.action", type=org.apache.struts2.dispatcher.ServletRedirectResult.class),
	@Result(name = "showUsersMoney", value = "/Product/User/userRecharge!showUsersMoney.action", type=org.apache.struts2.dispatcher.ServletRedirectResult.class)
	
})
public class UserRechargeAction extends BaseAction{
	@Resource
	private UserRechargeBean bean;
	@Resource
	private UserRechargeDAO dao;
	@Resource
	private ValidateBean validateBean;
	@Resource
	private MyAccountBean myAccountBean;
	@Resource
	private RegisterUserBean rubean;
	
	private String rechargeMoney;
	private String payType="0"; 
	private Integer currentPage = 1;
	private Integer pageSize = 25;
	private String payPassword;
	private String pd_FrpId;
	private String recordType;
	
	private String idcard;
	
	private String cardNo;
	
	private String name;
	
	private String msg;
	
	
	private PayOrder payOrder;
	private String idcardno;
	private String cardno;
	private String realName;
	private String username;
	private String money;
	private String province;
	private String city;
	private String braBankName;
	private Account account;
	private Users user;
	//回调地址
	private String call_url = "https://yintong.com.cn/traderapi/cardandpay.htm";
	
	
//	/**进入充值页面,显示用户金额(易宝支付);
//	 * @return
//	 */
//	public String showUsersMoney(){
//		try {
//			request = getRequest();
//			UsersLogin usersLogin  = (UsersLogin)request.getSession().getAttribute("usersLogin");
//			if(QwyUtil.isNullAndEmpty(usersLogin)){
//				return "login";
//			}
//			Users users = (Users)bean.findObjectById(new Users(), usersLogin.getUsersId());
//			request.setAttribute("users", users);
//			double coupons = bean.getCouponCost(users.getId());
//			request.setAttribute("coupons", coupons);
//			
//			UsersInfo ui = users.getUsersInfo();
//			if("1".equals(ui.getIsBindBank())){
//				//绑定了银行卡;并且进行了实名认证;
//				List<BankCard> listBankCard = validateBean.getBindBankCard(users.getId());
//				request.setAttribute("listBankCard", listBankCard);
//				request.setAttribute("isBindBank", QwyUtil.isNullAndEmpty(listBankCard)?0:1);
//			}
//			PageUtil<CzRecord> pageUtil = new PageUtil<CzRecord>();
//			pageUtil.setCurrentPage(currentPage);
//			pageUtil.setPageSize(pageSize);
//			String url= request.getServletContext().getContextPath()+"/Product/User/userRecharge!showUsersMoney.action?recordType="+recordType;
//			pageUtil.setPageUrl(url);
//			PageUtil<CzRecord> listCz = bean.getCzRecordByUserId(pageUtil,users.getId(),recordType);
//			//if(!QwyUtil.isNullAndEmpty(recordType)){
//				request.setAttribute("anchor", "#cz_record");
//			//}
//			request.setAttribute("recordType", recordType);
//			request.setAttribute("listCzRecord", listCz.getList());
//			request.setAttribute("pageUtil", pageUtil);
//		} catch (Exception e) {
//			log.error("操作异常: ",e);
//			return "error";
//		}
//		return "recharge";
//	}
	/**进入充值页面,显示用户金额（连连支付）;
	 * @return
	 */
	public String showUsersMoney(){
		try {
			request = getRequest();
			UsersLogin usersLogin  = (UsersLogin)request.getSession().getAttribute("usersLogin");
			if(QwyUtil.isNullAndEmpty(usersLogin)){
				return "login";
			}
			Users users = rubean.getUsersById(usersLogin.getUsersId());
			request.setAttribute("users", users);
			double coupons = bean.getCouponCost(users.getId());
			request.setAttribute("coupons", coupons);
			
			UsersInfo ui = users.getUsersInfo();
			if("1".equals(ui.getIsBindBank())){
				//绑定了银行卡;并且进行了实名认证;
				List<BankCard> listBankCard = validateBean.getBindBankCard(users.getId());
				request.setAttribute("isBindBank", QwyUtil.isNullAndEmpty(listBankCard)?0:1);
			}
			PageUtil<CzRecord> pageUtil = new PageUtil<CzRecord>();
			pageUtil.setCurrentPage(currentPage);
			pageUtil.setPageSize(pageSize);
			String url= request.getServletContext().getContextPath()+"/Product/User/userRecharge!showUsersMoney.action?recordType="+recordType;
			pageUtil.setPageUrl(url);
			PageUtil<CzRecord> listCz = bean.getCzRecordByUserId(pageUtil,users.getId(),recordType);
			request.setAttribute("anchor", "#cz_record");
			request.setAttribute("recordType", recordType);
			request.setAttribute("listCzRecord", listCz.getList());
			request.setAttribute("pageUtil", pageUtil);
			if(!QwyUtil.isNullAndEmpty(msg)){
				request.setAttribute("msg", "");
			}
		} catch (Exception e) {
			log.error("操作异常: ",e);
			return "error";
		}
		return "recharge";
	}
	
	/**进入提现页面,显示用户金额;
	 * @return
	 */
	public String getMoney(){
		try {
			request = getRequest();
			UsersLogin usersLogin  = (UsersLogin)request.getSession().getAttribute("usersLogin");
			if(QwyUtil.isNullAndEmpty(usersLogin)){
				return "login";
			}			
			//Users users = (Users)bean.findObjectById(new Users(), usersLogin.getUsersId());
			Users users = rubean.getUsersById(usersLogin.getUsersId());
			request.setAttribute("users", users);
			double coupons = bean.getCouponCost(users.getId());
			request.setAttribute("coupons", coupons);
			double productCost = myAccountBean.getProductCost(users.getId());
			productCost = QwyUtil.jieQuFa(productCost, 0);
			request.setAttribute("productCost", QwyUtil.calcNumber(productCost, 0.01, "*").doubleValue());
			
			account = myAccountBean.getAccountByUsersName(DESEncrypt.jieMiUsername(users.getUsername()), "1");
			if(QwyUtil.isNullAndEmpty(account)){
				return "recharge";
			}
			String isFirstCZ="1";
			if(!QwyUtil.isNullAndEmpty(account)){
				if(!QwyUtil.isNullAndEmpty(account.getProvinceCode())&& !QwyUtil.isNullAndEmpty(account.getCityCode()) && !QwyUtil.isNullAndEmpty(account.getBraBankName())){
				isFirstCZ="0";
			}
			}
			request.setAttribute("isFirstCZ", isFirstCZ);
			//查询是否设置了交易密码
			String isPwd="no";
			if(!QwyUtil.isNullAndEmpty(users.getPayPassword())){
				isPwd="ok";
			}
			getRequest().setAttribute("isPwd", isPwd);
			UsersInfo ui = users.getUsersInfo();
			if("1".equals(ui.getIsBindBank())){
				//绑定了银行卡;并且进行了实名认证;
				List<BankCard> listBankCard = validateBean.getBindBankCard(users.getId());
				PageUtil<TxRecord> pageUtil = new PageUtil<TxRecord>();
				pageUtil.setCurrentPage(currentPage);
				pageUtil.setPageSize(pageSize);
				String url= request.getServletContext().getContextPath()+"/Product/User/userRecharge!getMoney.action";
				pageUtil.setPageUrl(url);
				PageUtil<TxRecord> listCz = bean.getTxRecordByUserId(pageUtil,users.getId());
				request.setAttribute("listTxRecord", listCz.getList());
				request.setAttribute("pageUtil", pageUtil);
				request.setAttribute("listBankCard", listBankCard);

			}
			return "getMoney";
//			else{
//				request.setAttribute("msg", "您未绑卡，充值即可完成绑卡");
//				return "recharge";
//			}
		} catch (Exception e) {
			log.error("操作异常: ",e);
			return "error";
		}

	}
	
	
	
	/**网银支付，验证充值
	 * @return
	 */
	public String validateDate(){
		String json = "";
		try {
			request = getRequest();
			UsersLogin usersLogin  = (UsersLogin)request.getSession().getAttribute("usersLogin");
			if(QwyUtil.isNullAndEmpty(usersLogin)){
				json = QwyUtil.getJSONString("noLogin", "请先登录");
			}else{
				Users u = validateBean.getUsersById(usersLogin.getUsersId());
				if(!DESEncrypt.jieMiPassword(u.getPayPassword()).equals(payPassword)){
					//支付密码错误;
					json = QwyUtil.getJSONString("error", "支付密码错误");
					QwyUtil.printJSON(getResponse(), json);
					return null;
				}
				Pattern pattern = Pattern.compile("^[0-9]+(\\.[0-9]{1,2})?$");//带有小数的金钱格式;
				//Pattern pattern = Pattern.compile("^[0-9]+$");//
				boolean isTrue = pattern.matcher(rechargeMoney).matches();	
				if(isTrue){
					double money = Double.parseDouble(rechargeMoney);
					double trueMoney = QwyUtil.calcNumber(money, 100, "*").doubleValue();
					
					//trueMoney<100
					if(trueMoney<100){
						json = QwyUtil.getJSONString("error", "充值金额必须1元起");
					}else{
						json = QwyUtil.getJSONString("ok", "验证通过");
					}
				}else{
					json = QwyUtil.getJSONString("error", "充值金额只能是整元");
				}
			}
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("error", "充值异常");
		}
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			log.error("操作异常: ",e);
		}
		return null;
	}
	
	
	
	
	
	
	
	/**
	 * 查询省份
	 * @return
	 */
	public String queryProvince(){
		try {
			List<Map<String,String>> list = bean.queryProvince();
			JSONArray jsonArray = JSONArray.fromObject(list);
			QwyUtil.printJSON(response, jsonArray.toString());
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return null;
	}
	
	/**
	 * 通过省份查询地市
	 * @return
	 */
	public String queryCity(){
		try {
//			province = "广东省";
			List<Map<String,String>> list = bean.queryCity(province);
			JSONArray jsonArray = JSONArray.fromObject(list);
			QwyUtil.printJSON(response, jsonArray.toString());
		} catch (Exception e) {
			log.error("操作异常: ",e);
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

	public String getIdcard() {
		return idcard;
	}

	public void setIdcard(String idcard) {
		this.idcard = idcard;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public PayOrder getPayOrder() {
		return payOrder;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getBraBankName() {
		return braBankName;
	}

	public void setBraBankName(String braBankName) {
		this.braBankName = braBankName;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	public String getIdcardno() {
		return idcardno;
	}

	public void setIdcardno(String idcardno) {
		this.idcardno = idcardno;
	}

	public String getCardno() {
		return cardno;
	}

	public void setCardno(String cardno) {
		this.cardno = cardno;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public void setPayOrder(PayOrder payOrder) {
		this.payOrder = payOrder;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	
}
