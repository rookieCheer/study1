package com.huoq.account.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.account.dao.MyAccountDAO;
import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.Account;
import com.huoq.orm.Users;
import com.huoq.account.bean.CoinpPurseBean;
import com.huoq.admin.webView.bean.StatisticsBean;
import com.huoq.login.bean.RegisterUserBean;
import com.huoq.orm.CoinPurse;
import com.huoq.orm.BlackList;

/**我的账户Bean层,钱包的业务逻辑处理
 * @author qwy
 *
 * @createTime 2015-04-24 14:46:59
 */
@Service
public class MyAccountBean {

	
	private static Logger log = Logger.getLogger(MyAccountBean.class); 
	@Resource
	private MyAccountDAO dao;
	@Resource
	private UserRechargeBean userRechargeBean;
	@Resource
	private StatisticsBean statisticsBean;
	@Resource
	private CoinpPurseBean coinPurseBean;
	@Resource
	private RegisterUserBean registerUserBean;
	
	/**获取可用红包的余额;
	 * @param usersId 用户ID
	 * @return 可用红包的余额;
	 */
	public double getCouponCost(long usersId)  throws Exception{
		return userRechargeBean.getCouponCost(usersId);
	}
	/**获取可用红包的余额;
	 * @param usersId 用户ID
	  * @param type 类别 如:0:常规投资券; 1:新手投资券
	 * @return 可用红包的余额;
	 */
	public double getCouponCost(long usersId,String type)  throws Exception{
		return userRechargeBean.getCouponCost(usersId,type);
	}
	
	/**根据用户id来获取用户
	 * @param usersId
	 * @return
	 */
	public Users getUsersById(long usersId)  throws Exception{
		return (Users)dao.findById(new Users(), usersId);
	}
	
	/**获取投资中的产品的金额
	 * @param usersId 用户ID
	 * @return 获取投资中的产品的金额
	 */
	public double getProductCost(long usersId)  throws Exception{
		StringBuffer hql = new StringBuffer();
		hql.append("SELECT SUM(inv.inMoney) FROM Investors inv ");
		hql.append("WHERE inv.investorStatus IN ('1','2') ");
		hql.append("AND inv.usersId = ? ");
		Double cost = (Double)dao.findJoinActive(hql.toString(), new Object[]{usersId});
		cost = cost==null?0:cost;
		return cost>=0?cost:0;
	}
	
	
	/**根据用户名去查找用户绑定的银行卡;
	 * @param type 支付类型 第三方支付; 0:易宝支付; 1:连连支付
	 * @return null OR Account
	 * @return
	 */
	public Account getAccountByUsersName(String username,String type)  throws Exception{
		String hql = "FROM Account acc WHERE acc.users.username = ? AND acc.status = '1' ";
		ArrayList<Object> ob = new ArrayList<Object>();
		ob.add(DESEncrypt.jiaMiUsername(username));
		Object obj = dao.findJoinActive(hql, ob.toArray());
//		if(!QwyUtil.isNullAndEmpty(type)){
//			ob.add(type);
//			hql+=" AND acc.type = ? ";
//		}
		if(QwyUtil.isNullAndEmpty(obj)){
			return null;
		}else{
			Account ac = (Account)obj;
			return ac;
		}
	}
	/**根据用户名去查找用户绑定的银行卡;
	 * @param usersId 用户id
	 * @param type 支付类型 第三方支付; 0:易宝支付; 1:连连支付
	 * @return list
	 */
	public List<Account> findAccountByUsersName(String username) throws Exception{
		String hql = "FROM Account acc WHERE acc.users.username = ? ";
		ArrayList<Object> ob = new ArrayList<Object>();
		ob.add(DESEncrypt.jiaMiUsername(username));
		List<Account> list=dao.LoadAll(hql.toString(), ob.toArray());
		return list;
	}
	
	/**根据用户id去查找用户绑定的银行卡;
	 * @param usersId 用户id
	 * @param type 支付类型 第三方支付; 0:易宝支付; 1:连连支付
	 * @return null OR Account
	 * @return
	 */
	public Account getAccountByUsersId(long usersId,String type){
		String hql = "FROM Account acc WHERE acc.usersId = ? AND acc.status = '1' ";
		ArrayList<Object> ob = new ArrayList<Object>();
		ob.add(usersId);
		if(!QwyUtil.isNullAndEmpty(type)){
			ob.add(type);
			hql+=" AND acc.type = ? ";
		}
		Object obj = dao.findJoinActive(hql, ob.toArray());
		if(QwyUtil.isNullAndEmpty(obj)){
			return null;
		}else{
			Account ac = (Account)obj;
			return ac;
		}
	}
	
	
	/**根据身份证去查找用户绑定的银行卡;
	 * @param usersId 用户id
	 * @param type 
	 * @return
	 */
	public Account getAccountByIdCard(String idcard,String type) throws Exception{
		ArrayList<Object> list=new ArrayList<Object>();
		StringBuffer hql=new StringBuffer();
		hql .append( "FROM Account acc WHERE acc.idcard = ? AND acc.status = '1'  ");
		list.add(DESEncrypt.jiaMiIdCard(idcard));
		if(!QwyUtil.isNullAndEmpty(type)){
			hql .append( "AND type = ? ");
			list.add(type);
		}
		Object obj = dao.findJoinActive(hql.toString(), list.toArray());
		if(QwyUtil.isNullAndEmpty(obj)){
			return null;
		}else{
			return (Account)obj;
		}
	}
	
	/**根据用户id去查找用户绑定的银行卡;
	 * @param id id
	 * @return
	 */
	public Account getAccountById(String id) throws Exception{
		Object obj=dao.findById(new Account(), id);
		if(QwyUtil.isNullAndEmpty(obj)){
			return null;
		}else{
			return (Account)obj;
		}
	}
	
	/**
	 * 根据用户ID得到用户的充值次数（成功）
	 * @param userId
	 * @return
	 */
	public Object getCZCount(Long userId){
		StringBuffer buffer = new StringBuffer();
		ArrayList<Object> ob = new ArrayList<Object>();
		buffer.append("SELECT COUNT(*) from `cz_record` WHERE `STATUS` = '1' ");
		if (!QwyUtil.isNullAndEmpty(userId)) {
			buffer.append("AND users_id = ?");
			ob.add(userId);
		}
		Object o = dao.getSqlCount(buffer.toString(), ob.toArray());
		if (QwyUtil.isNullAndEmpty(o)) {
			o = 0;
		}
		return o;
	}
	
	/**
	 * 根据用户ID获得该用户充值成功的总金额
	 * @param userId
	 * @return
	 */
	public Object getCZSum(long userId){
		StringBuffer buffer = new StringBuffer();
		ArrayList<Object> ob = new ArrayList<Object>();
		buffer.append("SELECT SUM(money) FROM cz_record WHERE `STATUS` = '1' ");
		if (!QwyUtil.isNullAndEmpty(userId)) {
			buffer.append("AND users_id = ?");
			ob.add(userId);
		}
		Object o = dao.getSqlCount(buffer.toString(), ob.toArray());
		if (QwyUtil.isNullAndEmpty(o)) {
			o = 0;
		}
		return o;
	}
	
	/**
	 * 根据用户ID得到用户提现的总次数
	 * @param userId
	 * @return
	 */
	public Object getTXCount(Long userId){
		StringBuffer buffer = new StringBuffer();
		ArrayList<Object> ob = new ArrayList<Object>();
		buffer.append("SELECT COUNT(*) FROM tx_record WHERE `STATUS` = '1' ");
		if (!QwyUtil.isNullAndEmpty(userId)) {
			buffer.append("AND users_id = ?");
			ob.add(userId);
		}
		Object o = dao.getSqlCount(buffer.toString(), ob.toArray());
		if (QwyUtil.isNullAndEmpty(o)) {
			o = 0;
		}
		return o;
	}
	
	
	/**
	 * 根据用户ID 获得该用户提现成功的总金额
	 * @param userId
	 * @return
	 */
	public Object getTXSum(long userId) {
		StringBuffer buffer = new StringBuffer();
		ArrayList<Object> ob = new ArrayList<Object>();
		buffer.append("SELECT SUM(money) FROM tx_record WHERE STATUS = '1'");
		if (!QwyUtil.isNullAndEmpty(userId)) {
			buffer.append("AND users_id = ?");
			ob.add(userId);
		}
		Object o = dao.getSqlCount(buffer.toString(), ob.toArray());
		if (QwyUtil.isNullAndEmpty(o)) {
			o = 0;
		}
		return o;
	}
	
	/**
	 * add by Thz in 2017-10-14
	 * 根据用户ID 获得该用户提现待审核的总金额
	 * @param userId
	 * @return
	 */
	public Object getTXCheckPendingSum(long userId) {
		StringBuffer buffer = new StringBuffer();
		ArrayList<Object> ob = new ArrayList<Object>();
		buffer.append("SELECT SUM(money) FROM tx_record WHERE STATUS = '0'");
		if (!QwyUtil.isNullAndEmpty(userId)) {
			buffer.append("AND users_id = ?");
			ob.add(userId);
		}
		Object o = dao.getSqlCount(buffer.toString(), ob.toArray());
		if (QwyUtil.isNullAndEmpty(o)) {
			o = 0;
		}
		return o;
	}
	
	
 
	/**
	 * 根据用户ID取得已使用的积分总额
	 * @param userId
	 * @return
	 */
	public Object getUsedPoint(Long userId){
		StringBuffer buffer = new StringBuffer();
		ArrayList<Object> ob = new ArrayList<Object>();
		buffer.append("SELECT SUM(coin) FROM m_coin_pay WHERE status = '0'");
		if (!QwyUtil.isNullAndEmpty(userId)) {
			buffer.append("AND users_id = ?");
			ob.add(userId);
		}
		Object o = dao.getSqlCount(buffer.toString(), ob.toArray());
		if (QwyUtil.isNullAndEmpty(o)) {
			o = 0;
		}
		return o;
	}
	
	/**
	 * 根据用户ID获取不同状态下的投资券的总金额
	 * @param userId
	 * @param status
	 * @param type  投资券类型  红包 2 3 理财券 0 1 
	 * 			传参数类型：string type = "'1','0'";
	 * @return
	 */
	public Object getCoupon(long userId,int status,String type){
		StringBuffer buffer = new StringBuffer();
		ArrayList<Object> ob = new ArrayList<Object>();
		buffer.append("SELECT SUM(init_money) FROM coupon  WHERE 1=1 ");
		if (!QwyUtil.isNullAndEmpty(userId)) {
			buffer.append(" AND users_id = ? ");
			ob.add(userId);
		}
		if (!QwyUtil.isNullAndEmpty(status)) {
			buffer.append(" AND STATUS = ?");
			ob.add(status);
		}
		if (!QwyUtil.isNullAndEmpty(type)) {
			buffer.append(" AND type IN ("+type+")");
		}
		
		Object object = dao.getSqlCount(buffer.toString(), ob.toArray());
		if (QwyUtil.isNullAndEmpty(object)) {
			object = 0;
		}
		return object;
	}
	
	/**
	 * 根据用户名获取不同状态下的投资个数
	 * @param userId
	 * @param status
	 * @return String status = "'1','2','3'";传参格式
	 */
	public Object getInvstorsCount(long userId,String status){
		StringBuffer buffer = new StringBuffer();
		ArrayList<Object> ob = new ArrayList<Object>();
		buffer.append("SELECT COUNT(*) FROM investors WHERE 1=1 ");
		if (!QwyUtil.isNullAndEmpty(userId)) {
			buffer.append("AND users_id = ? ");
			ob.add(userId);
		}
		if (!QwyUtil.isNullAndEmpty(status)) {
			buffer.append("AND investor_status IN ("+status+")");
		}
		Object object = dao.getSqlCount(buffer.toString(), ob.toArray());
		if (QwyUtil.isNullAndEmpty(object)) {
			object = 0;
		}
		return object;
	}
	
	/**
	 * 根据用户id 查询该用户冻结资金总额 即状态为1
	 * @param userId
	 * @param status
	 * @return
	 */
	public Object getSumInvestor(long userId){
		StringBuffer buffer = new StringBuffer();
		ArrayList<Object> ob = new ArrayList<Object>();
		buffer.append("SELECT SUM(in_money) FROM investors WHERE investor_status='1' ");
		if (!QwyUtil.isNullAndEmpty(userId)) {
			buffer.append(" AND users_id = ? ");
			ob.add(userId);
		}
		Object object = dao.getSqlCount(buffer.toString(), ob.toArray());
		if (QwyUtil.isNullAndEmpty(object)) {
			object = 0;
		}
		return object;
	}
	
	/**
	 * 根据用户ID 获取该用户所获得的利息 已支付利息 和未支付利息
	 * @param userId 用户ID
	 * @param status 状态值   String status = "'1','2','3'";传参格式
	 * @return
	 */
	public Object getSumInterest(Long userId,String status) {
		try {
			StringBuffer sql = new StringBuffer();
			ArrayList<Object> ob = new ArrayList<Object>();
			sql.append("SELECT SUM(pay_interest) FROM interest_details WHERE 1=1 ");
			if (!QwyUtil.isNullAndEmpty(userId)) {
				sql.append("AND users_id = ? ");
				ob.add(userId);
			}
			if (!QwyUtil.isNullAndEmpty(status)) {
				sql.append("AND status IN ("+status+")");
			}
			
			Object object = dao.getSqlCount(sql.toString(), ob.toArray());
			
			if (QwyUtil.isNullAndEmpty(object)) {
				object = 0;
			}
			
			return object;
			
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return null;
		
	}
	
	/**
	 * 算出用户的偏差值 判断是否为异常用户
	 * @param userId
	 * @return
	 */
	public Object getDiff(Long userId){
		try {
		
		Users user =registerUserBean.getUsersById(userId);
			
		//用户充值总额
		Object cz = getCZSum(userId);
		Double czMoney =QwyUtil.calcNumber(cz, 1,"*").doubleValue();
		
		//已支付利息
		Object payInterest = getSumInterest(userId, "'2'"); 
		Double pay = QwyUtil.calcNumber(payInterest, 1,"*").doubleValue();
		
		//零钱罐总收益
		Object lqgInterest = statisticsBean.getSumInterest(userId);
		Double lqg = QwyUtil.calcNumber(lqgInterest,1,"*").doubleValue();
		
		//邀请总奖励
		Object object = getSumInviteEarn(userId);
		Double sumInviteEarn = QwyUtil.calcNumber(object, 1,"*").doubleValue();
		
		//已使用红包总额：红包使用后立即返还，即已使用的type为2的投资券
		Object usedHongb = getCoupon(userId, 2,"'2','3'");//已用
		Double usedHongbao = QwyUtil.calcNumber(usedHongb, 1,"*").doubleValue();
		
		//提现总金额
		Object tx = getTXSum(userId)  ;
		Double txMoney = QwyUtil.calcNumber(tx,1,"*").doubleValue();
		
		Object txCheckpending = getTXCheckPendingSum(userId);
		double txCheckMoney = QwyUtil.calcNumber(txCheckpending,1,"*").doubleValue();
		//提现成功的金额加上待审核的金额
		txMoney = txMoney + txCheckMoney;
		//零钱包余额
		CoinPurse coinPurse = coinPurseBean.findCoinPurseByUsersId(userId);
		Double money =QwyUtil.calcNumber(QwyUtil.isNullAndEmpty(coinPurse)?0.0:coinPurse.getInMoney(), 1, "*").doubleValue() ;
		
		//账户冻结资金
		Double freezeMoney = QwyUtil.calcNumber(user.getUsersInfo().getFreezeMoney(), 1, "*").doubleValue();
		
		//账户剩余金额
		Double leftMoney = QwyUtil.calcNumber(user.getUsersInfo().getLeftMoney(), 1, "*").doubleValue();
		
		//算出差值
//		Double sumIn = QwyUtil.calcNumber(czMoney, pay,"+").doubleValue();
//		sumIn = QwyUtil.calcNumber(sumIn, lqg,"+").doubleValue();
		
		// 收入总额   充值总金额+利息收入+零钱罐总收益+邀请总收益+收益中已支付红包
		Double sumIn = QwyUtil.calcNumber(czMoney, pay,"+").doubleValue();
		sumIn = QwyUtil.calcNumber(sumIn, lqg,"+").doubleValue();
		sumIn = QwyUtil.calcNumber(sumIn, sumInviteEarn,"+").doubleValue();
		sumIn = QwyUtil.calcNumber(sumIn, usedHongbao, "+").doubleValue();
		
		//支出总额   体现总额+零钱包余额+账户冻结资金+账户剩余金额
		Double sumOut = QwyUtil.calcNumber(txMoney, money, "+").doubleValue();
		sumOut = QwyUtil.calcNumber(sumOut, freezeMoney, "+").doubleValue();
		sumOut = QwyUtil.calcNumber(sumOut, leftMoney, "+").doubleValue();
		
		//算出差值
		Double different = QwyUtil.calcNumber(sumIn, sumOut,"-").doubleValue();
		String differ = QwyUtil.calcNumber(different, 1, "*").toPlainString();
		
//		Double diff = czMoney+pay+lqg-txMoney-money-freezeMoney-leftMoney ;
//		Double di = QwyUtil.calcNumber(diff, 1, "*").doubleValue();
//		String d = QwyUtil.calcNumber(di, 1, "*").toPlainString();
		
		return differ;
		
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return null;
	}
	
	/**
	 * 根据用户ID 添加黑名单
	 * @param userId 用户ID
	 * @param ip  ip地址
	 * @return
	 */
	public boolean addBlack(Long userId, String ip){
		try {
		Users users = getUsersById(userId);
		
		BlackList black = new BlackList();
		black.setUsername(DESEncrypt.jieMiUsername(users.getUsername()));
        black.setNote("账户资金异常（账户详情）");
//        black.setImei(blackList.getImei());
        black.setIp(ip);
        black.setInsertTime(new Date());
        black.setStatus("0");//状态：0  拉黑   1 解绑
        dao.saveAndReturnId(black); //添加手机号至黑名单
		
		return true;
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return false;
		
	}
	
	/**
	 * 通过该用户ID 获取该用户的投资奖励总额
	 * @param userId 用户ID 
	 * @return
	 */
	public Object getSumInviteEarn(Long userId) {
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT SUM(earn_money) FROM invite_earn WHERE `STATUS` = '1' ");
			if (!QwyUtil.isNullAndEmpty(userId)) {
				sql.append(" AND invite_id ="+userId);
			}
			Object object = dao.getSqlCount(sql.toString(), null);
			if (QwyUtil.isNullAndEmpty(object)) {
				return 0;
			}
			return object;
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return null;
	}
	
}
