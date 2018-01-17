package com.huoq.admin.product.action;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;

import com.huoq.account.bean.CoinpPurseBean;
import com.huoq.account.bean.InvestorsRecordBean;
import com.huoq.account.bean.MyAccountBean;
import com.huoq.account.bean.UserInfoBean;
import com.huoq.common.action.BaseAction;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.login.bean.RegisterUserBean;
import com.huoq.orm.CoinPurse;
import com.huoq.orm.Investors;
import com.huoq.orm.Users;
import com.huoq.orm.UsersAdmin;
import com.huoq.orm.UsersInfo;
import com.huoq.admin.webView.bean.StatisticsBean;
import com.huoq.admin.product.bean.BlackListBean;
import com.huoq.common.util.DESEncrypt;
/**我的钱包
 * @author qwy
 *
 * 2015-04-20 12:58:29
 */
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/Admin")
//理财产品
@Results({ 
	@Result(name = "myAccount", value = "/Product/Admin/fundsManager/myAccount.jsp"),
	@Result(name = "login", value = "/Product/loginBackground.jsp" ,type=org.apache.struts2.dispatcher.ServletRedirectResult.class),
	@Result(name = "error", value = "/Product/error.jsp"),
	@Result(name = "bindInfo", value = "/Product/Admin/operationManager/bindInfo.jsp"),
	@Result(name = "myPurse", value = "/Product/Admin/fundsManager/myPurse.jsp") 
})
public class AccountAction extends BaseAction {
	
	@Resource
	private MyAccountBean bean;
	@Resource
	private UserInfoBean uibean;
	@Resource
	private InvestorsRecordBean investorsRecordBean;
	@Resource
	private RegisterUserBean registerUserBean;
	@Resource
	private CoinpPurseBean coinPurseBean;
	@Resource
	private BlackListBean blackListBean;
	@Resource
	private StatisticsBean statisticsBean;
	
	private String username;
	private Users users;
	
	
	/**
	 * 显示我的钱包详细信息
	 * @return
	 */
	public String showMyPurse() {
		try {
			request = getRequest();
			UsersAdmin users = (UsersAdmin)request.getSession().getAttribute("usersAdmin");
			if (QwyUtil.isNullAndEmpty(users)) {
				//管理員沒有登录;
				return "login";
			}
			
			if (!QwyUtil.isNullAndEmpty(username)) {
				Users newUsers =registerUserBean.getUsersByUsername(username);
				request.setAttribute("users", newUsers);
				
				if (!QwyUtil.isNullAndEmpty(newUsers.getUsersInfo().getTotalMoney())) {
					double totalMoney = QwyUtil.calcNumber(newUsers.getUsersInfo().getTotalMoney(), 0.01, "*").doubleValue();
					String totalMoneyString = QwyUtil.calcNumber(totalMoney, 1, "*").toPlainString();
					request.setAttribute("totalMoney",totalMoneyString); //用戶总资产
				}else{
					request.setAttribute("totalMoney",0.0); 
				}
				
				if (!QwyUtil.isNullAndEmpty(newUsers.getUsersInfo().getLeftMoney())) {
					double leftMoney = QwyUtil.calcNumber(newUsers.getUsersInfo().getLeftMoney(), 0.01, "*").doubleValue();
					String leftMoneyString = QwyUtil.calcNumber(leftMoney, 1, "*").toPlainString();
					request.setAttribute("leftMoney",leftMoneyString ); //用户剩余资金
				}else{
					request.setAttribute("leftMoney",0.0 ); //用户剩余资金
				}
				
				Object sumInv = bean.getSumInvestor(newUsers.getId());//冻结金额 投资状态为1 的总额
				Double sumInvestor = QwyUtil.calcNumber(sumInv, 0.01, "*").doubleValue();
				String sumInvM = QwyUtil.calcNumber(sumInvestor, 1,"*").toPlainString();
				request.setAttribute("sumInvestor", sumInvM);
				
				if (!QwyUtil.isNullAndEmpty(newUsers.getUsersInfo().getFreezeMoney())) {
					double freezeMoney = QwyUtil.calcNumber(newUsers.getUsersInfo().getFreezeMoney(), 0.01, "*").doubleValue();
					String freezeMoneyString = QwyUtil.calcNumber(freezeMoney, 1, "*").toPlainString();
					request.setAttribute("freezeMoney",freezeMoneyString ); //用户冻结金额
					
					if (freezeMoneyString.equals(sumInvM)) {
						request.setAttribute("isEqals", 0);
					}else{
						request.setAttribute("isEqals", 1);
					}
					
				}else{
					request.setAttribute("freezeMoney",0.0 ); //用户冻结金额
				}
				
				if (!QwyUtil.isNullAndEmpty(newUsers.getUsersInfo().getTotalPoint())) {
					long totalPoint = newUsers.getUsersInfo().getTotalPoint(); 
					request.setAttribute("totalPoint",totalPoint );//用户可用总积分
				}else{
					request.setAttribute("totalPoint",0.0 );
				}

				Object objectCount = bean.getUsedPoint(newUsers.getId());
				int usedPoints = Integer.parseInt(objectCount.toString());
				request.setAttribute("usedPoints", usedPoints);//用户的已用积分总额
				
				Object czObject = bean.getCZCount(newUsers.getId());
				int czCount = Integer.parseInt(czObject.toString());
				request.setAttribute("czCount", czCount);//用户充值成功的总次数
				
				Object cz = bean.getCZSum(newUsers.getId());
				double czMoney =QwyUtil.calcNumber(cz, 0.01,"*").doubleValue();
				String czSumString = QwyUtil.calcNumber(czMoney,1,"*").toPlainString();
				request.setAttribute("czSum", czSumString); //充值成功的总金额
				
				Object txObject = bean.getTXCount(newUsers.getId());
				int txCount = Integer.parseInt(txObject.toString());
				request.setAttribute("txCount", txCount); //用户提现成功的次数
				
				Object tx = bean.getTXSum(newUsers.getId());
				double txMoney = QwyUtil.calcNumber(tx,0.01,"*").doubleValue();
				String txSumString = QwyUtil.calcNumber(txMoney,1,"*").toPlainString();
				request.setAttribute("txSum", txSumString); //用户提现成功的总金额
				
				Object txCheckpending = bean.getTXCheckPendingSum(newUsers.getId());
				double txCheckMoney = QwyUtil.calcNumber(txCheckpending,0.01,"*").doubleValue();
				String txCheckSumString = QwyUtil.calcNumber(txCheckMoney,1,"*").toPlainString();
				request.setAttribute("txCheckSum", txCheckSumString); //用户提现待审核的总金额
				
				CoinPurse coinPurse = coinPurseBean.findCoinPurseByUsersId(newUsers.getId());
				if (!QwyUtil.isNullAndEmpty(coinPurse)) {
					double money =QwyUtil.calcNumber(coinPurse.getInMoney(), 0.01, "*").doubleValue() ;
					String coinPurseMoney = QwyUtil.calcNumber(money,1,"*").toPlainString();
					request.setAttribute("coinPurseMoney", coinPurseMoney);
				}else{
					request.setAttribute("coinPurseMoney", 0.0);
				}
				
				Object lqgInterest = statisticsBean.getSumInterest(newUsers.getId());//零钱罐总收益
				if (!QwyUtil.isNullAndEmpty(lqgInterest)) {
					Double lqg = QwyUtil.calcNumber(lqgInterest,0.01,"*").doubleValue();
					String lqgSumInterest = QwyUtil.calcNumber(lqg, 1, "*").toPlainString();
					request.setAttribute("lqgSum", lqgSumInterest);
				}else{
					request.setAttribute("lqgSum", 0.0);
				}
				
				//理财券 type为0,1 (1暂时无用)
				Object usedCoupon = bean.getCoupon(newUsers.getId(), 2,"'0','1'");//已用
				Object unusedCoupon = bean.getCoupon(newUsers.getId(), 0,"'0','1'");//未用
				Object pastCoupon = bean.getCoupon(newUsers.getId(), 3,"'0','1'");//已过期
				
				//红包
				Object usedHongb = bean.getCoupon(newUsers.getId(), 2,"'2','3'");//已用
				Object unusedHongb = bean.getCoupon(newUsers.getId(), 0,"'2','3'");//未用
				Object pastHongb = bean.getCoupon(newUsers.getId(), 3,"'2','3'");//已过期
				
				Double usedHong=QwyUtil.calcNumber(usedHongb, 0.01,"*").doubleValue();
				Double unusedHong=QwyUtil.calcNumber(unusedHongb, 0.01,"*").doubleValue();
				Double pastHong=QwyUtil.calcNumber(pastHongb, 0.01,"*").doubleValue();
				
				String usedHongbao =QwyUtil.calcNumber(usedHong, 1,"*").toPlainString();
				String unusedHongbao =QwyUtil.calcNumber(unusedHong, 1,"*").toPlainString();
				String pastHongbao =QwyUtil.calcNumber(pastHong, 1,"*").toPlainString();
				request.setAttribute("usedHongbao", usedHongbao); //已使用红包
				request.setAttribute("unusedHongbao", unusedHongbao); //未使用红包
				request.setAttribute("pastHongbao", pastHongbao);//已过期红包
				
				
				double used = QwyUtil.calcNumber(usedCoupon, 0.01,"*").doubleValue();
				String usedCouponString = QwyUtil.calcNumber(used,1,"*").toPlainString();
				request.setAttribute("usedCouponString", usedCouponString); //已使用的投资券总金额
				
				double unused = QwyUtil.calcNumber(unusedCoupon, 0.01,"*").doubleValue();
				String unusedCouponString = QwyUtil.calcNumber(unused,1,"*").toPlainString();
				request.setAttribute("unusedCouponString", unusedCouponString);//未使用的投资券的总金额
				
				double past = QwyUtil.calcNumber(pastCoupon, 0.01,"*").doubleValue();
				String pastCouponString = QwyUtil.calcNumber(past,1,"*").toPlainString();
				request.setAttribute("pastCouponString", pastCouponString);//已过期的投资券的总金额

				Object invCount = bean.getInvstorsCount(newUsers.getId(),"'1','2','3'" );
				Object unAccountCount = bean.getInvstorsCount(newUsers.getId(), "'1','2'");
				Object accountCount = bean.getInvstorsCount(newUsers.getId(),"'3'");
				
				request.setAttribute("invCountString", invCount.toString()); //投资成功的项目
				request.setAttribute("unAccountString", unAccountCount.toString());//未结算的项目
				request.setAttribute("accountString", accountCount.toString()); //已结算
				
				Object payInterest = bean.getSumInterest(newUsers.getId(), "'2'"); //已支付利息总额
				Object unPayInterest = bean.getSumInterest(newUsers.getId(), "'0','1'"); //未支付利息总额
				double pay = QwyUtil.calcNumber(payInterest, 0.01,"*").doubleValue();
				String payIntere = QwyUtil.calcNumber(pay, 1, "*").toPlainString();
				
				double unpay = QwyUtil.calcNumber(unPayInterest, 0.01,"*").doubleValue();
				String unpayIntere = QwyUtil.calcNumber(unpay, 1,"*").toPlainString();
//				double sum = pay+unpay;
				Double sum = QwyUtil.calcNumber(pay, unpay,"+").doubleValue();
				String sumInterest =QwyUtil.calcNumber(sum, 1,"*").toPlainString();//总利息
				request.setAttribute("payInterest", payIntere);
				request.setAttribute("unPayInterest", unpayIntere);
				request.setAttribute("sumInterest", sumInterest);
				
				
				//是否为黑名单用户
				List list = blackListBean.getBlackListByUsernameAdnStatus(DESEncrypt.jieMiUsername(newUsers.getUsername()));
				if (!QwyUtil.isNullAndEmpty(list)) {
					request.setAttribute("list", list);
					request.setAttribute("isBlack", 1);//不为空即 为黑名单用户
				}else {
					request.setAttribute("isBlack", 0);//为空 则不是黑名单用户
				}
				
				//账户偏差值
				Object diff = bean.getDiff(newUsers.getId());
				Double diffM =QwyUtil.calcNumber(diff, 0.01,"*").doubleValue();//转换为元;
				String diffMoney = QwyUtil.calcNumber(diffM, 1, "*").toPlainString();
				diffM = QwyUtil.jieQuFa(diffM, 2);
				getRequest().setAttribute("diff", diffMoney);
				if (diffM>=1||diffM<0) {
					request.setAttribute("isDiff", 1);  //账户是否有异常 1 有异常  0 无异常
				}else{
					request.setAttribute("isDiff", 0);
				}
				
				Object object = bean.getSumInviteEarn(newUsers.getId());//邀请奖励总额
				Double sumInvited = QwyUtil.calcNumber(object, 0.01,"*").doubleValue();
				String sumInviteEarn = QwyUtil.calcNumber(sumInvited, 1,"*").toPlainString();
				getRequest().setAttribute("sumInviteEarn", sumInviteEarn);
				
			}
			
		} catch (Exception e) {
			log.error("操作异常",e);
			return "error";
		}
		
		return "myPurse";
	}
	
	
	/**显示我的钱包; 已不使用
	 * @return
	 */
	public String showMyAccount(){
		try {
			request = getRequest();
			UsersAdmin users = (UsersAdmin)getRequest().getSession().getAttribute("usersAdmin");
			if(QwyUtil.isNullAndEmpty(users)){
				//管理员没有登录;
				return "login";
			}
			if(!QwyUtil.isNullAndEmpty(username)){
				Users newUsers = registerUserBean.getUsersByUsername(username);
				request.getSession().setAttribute("users",newUsers);
				double coupon = bean.getCouponCost(newUsers.getId());
				coupon = QwyUtil.jieQuFa(coupon, 0);
				request.setAttribute("coupon", QwyUtil.calcNumber(coupon, 0.01, "*").doubleValue());
				double productCost = bean.getProductCost(newUsers.getId());
				productCost = QwyUtil.jieQuFa(productCost, 0);
				productCost = QwyUtil.calcNumber(productCost, 0.01, "*").doubleValue();
				double freezeMoney = QwyUtil.calcNumber(newUsers.getUsersInfo().getFreezeMoney(), 0.01, "*").doubleValue();
				request.setAttribute("freezeMoney", freezeMoney);
				request.setAttribute("free", productCost);
				UsersInfo userInfo=uibean.getUserInfoById(newUsers.getId());
				String isVerifyEmail=userInfo.getIsVerifyEmail();
				String isVerifyIdcard=userInfo.getIsVerifyIdcard();
				String isVerifyPhone=userInfo.getIsVerifyPhone();
				int safeLevel=10;
				//修改于 2015-05-22 20:29:48 邮箱验证先去掉
				/*if("1".equals(isVerifyEmail)){
					safeLevel+=10;
				}*/
				if("1".equals(isVerifyIdcard)){
						safeLevel+=10;
				}
				if("1".equals(isVerifyPhone)){
						safeLevel+=10;
				}
				request.setAttribute("safeLevel", safeLevel);
				String[] status={"1","2","3"};
				PageUtil<Investors> pageUtil = getInvestorsByPageUtil(1, 10, status, newUsers.getId());
				request.setAttribute("investorsList", pageUtil.getList());
			}
		} catch (Exception e) {
			log.error("操作异常: ",e);
			//request.setAttribute("errMsg", "发生了错误");
			return "error";
		}
		return "myAccount";
	}
	
	/**根据分页对象获取投资记录
	 * @param currentPage 当前页数
	 * @param pageSize 显示条数
	 * @param status 查询状态
	 * @return
	 */
	public PageUtil<Investors> getInvestorsByPageUtil(int currentPage,int pageSize,String[] status,long uid){
		PageUtil<Investors> pageUtil = new PageUtil<Investors>();
		pageUtil.setCurrentPage(currentPage);
		pageUtil.setPageSize(pageSize);
		pageUtil = investorsRecordBean.getInvestorsByPageUtil(pageUtil,status,uid);
		return pageUtil;
	}
	/**
	 * 查看帮卡人信息
	 * @return
	 */
	public String showBindInfo(){
		if(!QwyUtil.isNullAndEmpty(username)){
			request.setAttribute("username", username);
		}
		return "bindInfo";
				
	}
	
	
	/**
	 * 添加黑名单
	 * @return
	 */
	public String addBlack(){
		
		String json="";
		String ip = QwyUtil.getIpAddr(getRequest());
		
		try {
			if(!QwyUtil.isNullAndEmpty(users)){
				if(bean.addBlack(users.getId(),ip)){
					json = QwyUtil.getJSONString("ok","黑名单添加成功");
				}else{
					json = QwyUtil.getJSONString("error","黑名单添加失败");
				}
			}else{
				json = QwyUtil.getJSONString("error","users用户不能为空");
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			json = QwyUtil.getJSONString("error", "黑名单添加异常");
		}
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	public Users getUsers() {
		return users;
	}


	public void setUsers(Users users) {
		this.users = users;
	}


	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
