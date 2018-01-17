package com.huoq.admin.product.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.account.bean.UserRechargeBean;
import com.huoq.common.dao.ObjectDAO;
import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.LockHolder;
import com.huoq.common.util.Page;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.login.bean.RegisterUserBean;
import com.huoq.orm.HdFlag;
import com.huoq.orm.HdMessage;
import com.huoq.orm.HdUsers;
import com.huoq.orm.LotteryRecord;
import com.huoq.orm.LotteryTimes;
import com.huoq.orm.MUsersAddress;
import com.huoq.orm.Users;
import com.huoq.product.dao.ProductCategoryDAO;

import groovy.transform.Synchronized;

/**
 * 
 * @author oi
 * 活动业务层
 */
@Service
public class BannerActivityBean {
	private static Logger log = Logger.getLogger(BannerActivityBean.class);
	
	@Resource
	private ProductCategoryDAO dao;
	@Resource
	private UserRechargeBean userRechargeBean;
	@Resource
	private RegisterUserBean registerUserBean;
	
	/**
	 * 判断是否可参与活动
	 * @param mobileNum
	 * @param xlhNum
	 * @return
	 */
	public String checkMobileAndXlh(String mobileNum,String xlhNum){
		try {
			//防止刷投资券,根据用户名进行同步锁
			synchronized(LockHolder.getLock(mobileNum)){
				String sql = "select id from users WHERE username=?";
				String mobile = DESEncrypt.jiaMiUsername(mobileNum);
				List<Object> usersList = dao.LoadAllSql(sql, new String[]{mobile});
				String userId;
				if(usersList==null || usersList.size()==0){
					return "您还没有注册，请先注册！";
				}else {
					userId = usersList.get(0).toString();
				}
				
				sql = "select * from hd_flag WHERE status=0 AND NOW() BETWEEN insert_time AND end_time AND uniq='1' AND flag=?";
				List<Object> flagList = dao.LoadAllSql(sql, new String[]{xlhNum.toUpperCase()});
				String flagId = "";
				if(flagList==null || flagList.size()==0){
					return "请输入正确的序列号";
				}else {
					Object[] data = (Object[])flagList.get(0);
					flagId = data[0].toString();
				}
				
				sql = "select * from hd_users WHERE hd_flag_id=? and username=?";
				List<Object> hdUsersList = dao.LoadAllSql(sql, new String[]{flagId,mobile});
				if(hdUsersList!=null && hdUsersList.size()>0){
					return "您已经参与过该活动！";
				}
				boolean boo = userRechargeBean.sendHongBao(Long.valueOf(userId),5000,QwyUtil.addDaysFromOldDate(new Date(), 10).getTime(),"0",10001,"加群领取50元投资券",null);
				if(boo){
					HdUsers hdUsers = new HdUsers();
					hdUsers.setHdFlagId(flagId);
					hdUsers.setInsertTime(new Date());
					hdUsers.setNote("加群领取50元投资券");
					hdUsers.setUserId(Long.parseLong(userId));
					hdUsers.setUsername(mobile);
					insertHdUsers(hdUsers);
				}
			}
		} catch (Exception e) {
			log.error("操作异常: ",e);
			return "系统异常";
		}
		return "";
	}
	
	
	/**
	 * 获取双十一当天投资超过2万的用户的信息 投资金额  手机号码
	 * @param stDateTime
	 * @param edDateTime
	 */
	public List getCount(String stDateTime,String edDateTime){
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT inv.in_money,us.phone FROM investors inv INNER JOIN users_info us WHERE inv.in_money >= 20000 AND inv.investor_status='1' AND inv.pay_time BETWEEN '");
		sb.append(stDateTime);
		sb.append("'AND'");
		sb.append(edDateTime);
		sb.append("' ORDER BY in_money DESC");
		
		
		List list = dao.LoadAllSql(sb.toString(), null);
		
		if (!QwyUtil.isNullAndEmpty(list)) {
			return list;
		}
		
		return null;
		
	}
	
	
	
	/**
	 * 分页查询  获得双十一当天投资金额大于等于2万的用户的信息  手机号码 投资金额
	 * @param pageUtil
	 * @param stDateTime
	 * @param edDateTime
	 * @return
	 */
	public PageUtil getSingleCount(PageUtil pageUtil,String stDateTime,String edDateTime){
		
		try {
			
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT (SELECT us.username FROM users us WHERE us.id = users_id)'username',SUM(copies)'copies' FROM investors inv WHERE inv.product_id IN (SELECT pro.id FROM product pro WHERE pro.product_type != 1 AND title NOT LIKE '%周利宝%') "
					+ "AND inv.investor_status IN (1) AND inv.copies >=20000 AND inv.pay_time BETWEEN '");
			sb.append(stDateTime);
			sb.append("'AND'");
			sb.append(edDateTime);
			sb.append("' GROUP BY username ORDER BY copies DESC");
			
			StringBuffer sbCount = new StringBuffer();
			sbCount.append(" SELECT COUNT(*)  ");
			sbCount.append(" FROM (");
			sbCount.append(sb);
			sbCount.append(") t ");
			
			pageUtil=dao.getBySqlAndSqlCount(pageUtil, sb.toString(), sbCount.toString(),null);
			
			
			return pageUtil;
			
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		
		return null;
		
	}
	

	/**
	 * 双十一当天  “%悦享11%”产品 投资总额排行榜
	 * @param pageUtil
	 * @param stDateTime
	 * @param edDateTime
	 * @return
	 */
	public PageUtil getRankingList(PageUtil pageUtil,String stDateTime,String edDateTime){
		
		try {
			
			StringBuffer buffer = new StringBuffer();
			buffer.append("SELECT (SELECT us.username FROM users us WHERE us.id = users_id)'username',SUM(copies)'copies' FROM investors inv WHERE inv.product_id IN  "
					+ "(SELECT pro.id FROM product pro WHERE pro.title LIKE '%悦享双十一%') AND inv.investor_status IN (1) AND inv.pay_time BETWEEN '");
			buffer.append(stDateTime);
			buffer.append("'AND'");
			buffer.append(edDateTime);
			buffer.append("' GROUP BY username ORDER BY copies DESC");
			
			StringBuffer sbCount = new StringBuffer();
			sbCount.append(" SELECT COUNT(*)  ");
			sbCount.append(" FROM (");
			sbCount.append(buffer);
			sbCount.append(") t ");
			pageUtil=dao.getBySqlAndSqlCount(pageUtil, buffer.toString(), sbCount.toString(),null);
			
			return pageUtil;
			
		} catch (Exception e) {
			log.error("getRankingList",e);
		}
		return null;
	}

	/**
	 * 瓜分奖金排行榜
	 * @param pageUtil
	 * @param stDateTime
	 * @param edDateTime
	 * @return
	 */
	public PageUtil getbonusRanking(PageUtil pageUtil,String stDateTime,String edDateTime){
		
		try {
			
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT (SELECT us.id FROM users us WHERE us.id = users_id)'id', (SELECT us.username FROM users us WHERE us.id = users_id)'username',SUM(copies)'copies',(SUM(copies)/76223)'proportion',((SUM(copies)/7622300)*8000)'bonus' FROM investors inv WHERE inv.product_id IN (SELECT pro.id FROM product pro WHERE pro.product_type != 1 AND title NOT LIKE '%周利宝%') "
					+ "AND inv.investor_status IN (1) AND inv.copies >=20000 AND inv.pay_time BETWEEN '");
			sb.append(stDateTime);
			sb.append("'AND'");
			sb.append(edDateTime);
			sb.append("' GROUP BY username ORDER BY copies DESC");
			
			StringBuffer sbCount = new StringBuffer();
			sbCount.append(" SELECT COUNT(*)  ");
			sbCount.append(" FROM (");
			sbCount.append(sb);
			sbCount.append(") t ");
			
			pageUtil=dao.getBySqlAndSqlCount(pageUtil, sb.toString(), sbCount.toString(),null);
			
			
			return pageUtil;
			
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		
		return null;
		
	}

	/**
	 * 判断是否可参与360注册送投资券活动
	 * @param mobileNum
	 * @param xlhNum
	 * @return
	 */
	public String checkMobileAndXlhSendTicket(String mobileNum){
		try {
			
			//防止刷投资券,根据用户名进行同步锁
			synchronized(LockHolder.getLock(mobileNum)){
				
				Users users=registerUserBean.getUsersByUsername(mobileNum);
//				String sql = "select id from users WHERE username=?";
//				String mobile = DESEncrypt.jiaMiUsername(mobileNum);
//				List<Object> usersList = dao.LoadAllSql(sql, new String[]{mobile});
//				String userId;
//				if(usersList==null || usersList.size()==0){
//					return "您还没有注册，请先注册！";
//				}else {
//					userId = usersList.get(0).toString();
//				}
				if(QwyUtil.isNullAndEmpty(users)){
					return "您还没有注册，请先注册！";
				}
				StringBuffer buffer=new StringBuffer();
				buffer.append(" FROM HdFlag hd ");
				buffer.append(" WHERE hd.flag = '360' ");
				List<HdFlag> hdFlags=dao.LoadAll(buffer.toString(), null);
				if(QwyUtil.isNullAndEmpty(hdFlags)){
					return "该活动！已结束";
				}
				HdFlag hdFlag=hdFlags.get(0);
				if(!(users.getInsertTime().after(hdFlag.getInsertTime())&&users.getInsertTime().before(hdFlag.getEndTime()))){
					return "亲，暂无资格哦!";
				}
				//if
//				sql = "select * from hd_flag WHERE status=0 AND NOW() BETWEEN insert_time AND end_time AND uniq='1' AND flag='360'";
//				List<Object> flagList = dao.LoadAllSql(sql, new String[]{xlhNum.toUpperCase()});
//				String flagId = "";
//				if(flagList==null || flagList.size()==0){
//					return "请输入正确的序列号";
//				}else {
//					Object[] data = (Object[])flagList.get(0);
//					flagId = data[0].toString();
//				}
				
//				sql = "select * from hd_users WHERE hd_flag_id=? and username=?";
//				List<Object> hdUsersList = dao.LoadAllSql(sql, new String[]{flagId,mobile});
//				if(hdUsersList!=null && hdUsersList.size()>0){
//					return "您已经参与过该活动！";
//				}
				mobileNum=DESEncrypt.jiaMiUsername(mobileNum);
				StringBuffer hdUsersBuffer=new StringBuffer();
				hdUsersBuffer.append(" FROM HdUsers hd ");
				hdUsersBuffer.append(" WHERE hd.hdFlagId = ? ");
				hdUsersBuffer.append(" AND hd.username = ? ");
				
				List<HdUsers> hdUsersList=dao.LoadAll(hdUsersBuffer.toString(), new String[]{hdFlag.getId()+"",mobileNum});
				if(!QwyUtil.isNullAndEmpty(hdUsersList)){
					return "您已经参与过该活动！";
				}
				boolean boo = userRechargeBean.sendHongBao(users.getId(),88800,QwyUtil.addDaysFromOldDate(new Date(), 10).getTime(),"0",10001,"360应用市场注册送888投资券",null);
				if(boo){
					HdUsers hdUsers = new HdUsers();
					hdUsers.setHdFlagId(hdFlag.getId()+"");
					hdUsers.setInsertTime(new Date());
					hdUsers.setNote("360应用市场注册送888投资券");
					hdUsers.setUserId(users.getId());
					hdUsers.setUsername(mobileNum);
					insertHdUsers(hdUsers);
				}
			}
		} catch (Exception e) {
			log.error("操作异常: ",e);
			return "系统异常";
		}
		return "";
	}
	
	/**
	 * hd_users表插入参与活动用户数据
	 * @param hdUsers
	 * @return
	 */
	public String insertHdUsers(HdUsers hdUsers){
		try{
			dao.save(hdUsers);
		}catch (Exception e) {
			log.error("操作异常: ",e);
			return "系统异常";
		}
		return "";
	}
	
	/**获取国庆的活动期间的总投资,包含新手产品;
	 * @return
	 */
	public String getInvestorsTotal(String stDateTime,String edDateTime){
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT SUM(copies) FROM investors ");
		sb.append("WHERE investor_status IN (1,2,3) ");
		sb.append("AND pay_time BETWEEN '");
		sb.append(stDateTime);
		sb.append("' AND '");
		sb.append(edDateTime);
		sb.append("' ");
		List list = dao.LoadAllSql(sb.toString(), null);
		if(!QwyUtil.isNullAndEmpty(list)){
			Object tp = list.get(0);
			return QwyUtil.isNullAndEmpty(tp)?"0":tp.toString();
		}
		return "0";
		
	}
	
	
	/**获取国庆的活动期间的总投资不重复人数,不包含新手产品;
	 * @return
	 */
	public String getInvestorsUsersTotal(String stDateTime,String edDateTime){
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT COUNT(*) FROM (");
		sb.append("SELECT users_id FROM investors ");
		sb.append("WHERE product_id NOT IN (SELECT id FROM product WHERE product_type =1) ");
		sb.append("AND investor_status IN (1,2,3) ");
		sb.append("AND copies >=20000 ");
		sb.append("AND pay_time BETWEEN '");
		sb.append(stDateTime);
		sb.append("' AND '");
		sb.append(edDateTime);
		sb.append("' GROUP BY users_Id) tp ");
		List list = dao.LoadAllSql(sb.toString(), null);
		if(!QwyUtil.isNullAndEmpty(list)){
			return list.get(0).toString();
		}
		return "0";
		
	}
	
	/**
	 * @param stDateTime
	 * @param edDateTime
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public synchronized List<Object[]> autumnActivity(String stDateTime,String edDateTime){
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT SUM(copies)'sumCopies',username FROM(");
		sb.append("SELECT inv.copies,(SELECT us.username FROM users us WHERE us.id IN (inv.users_id)) 'username',inv.pay_time  FROM investors inv ");
		sb.append("WHERE inv.investor_status IN (1,2,3) AND  inv.product_id IN (SELECT pro.id FROM product pro WHERE pro.product_type != 1 AND title NOT LIKE '%周利宝%' ) ");
		sb.append(" AND inv.pay_time BETWEEN '");
		sb.append(stDateTime);
		sb.append("' AND '");
		sb.append(edDateTime);
		sb.append("' ORDER BY pay_time ASC )new_inv ");
		sb.append("GROUP BY username ORDER BY sumCopies DESC,pay_time ASC LIMIT 15");		
		List<Object[]> list = (List<Object[]>)dao.LoadAllSql(sb.toString(), null);
		if(!QwyUtil.isNullAndEmpty(list)){
			return list;
		}
		return null;
	}
	
	
	/**获取双十一活动期间的总投资,包含新手产品;投资所有产品都算;
	 * @return
	 */
	public String getSingleDayInvestorsTotal(String stDateTime,String edDateTime){
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT SUM(copies) FROM investors inv");
		sb.append("WHERE inv.investor_status IN (1) ");
		sb.append("AND inv.pay_time BETWEEN '");
		sb.append(stDateTime);
		sb.append("' AND '");
		sb.append(edDateTime);
		sb.append("' ");
		List list = dao.LoadAllSql(sb.toString(), null);
		if(!QwyUtil.isNullAndEmpty(list)){
			Object tp = list.get(0);
			return QwyUtil.isNullAndEmpty(tp)?"0":tp.toString();
		}
		return "0";
		
	}
	
	
	/**获取双十一活动期间的总投资不重复人数<br>
	 * 不包含新手产品,不包含周周利产品;
	 * @return
	 */
	public String getSingleDayInvestorsUsersTotal(String stDateTime,String edDateTime){
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT COUNT(*) FROM (");
		sb.append("SELECT users_id FROM investors inv");
		sb.append("WHERE ");
		sb.append("inv.product_id IN (SELECT pro.id FROM product pro WHERE pro.product_type != 1 AND title NOT LIKE '%周利宝%' )");
		sb.append("AND inv.investor_status IN (1) ");
		sb.append("AND inv.copies >=20000 ");
		sb.append("AND inv.pay_time BETWEEN '");
		sb.append(stDateTime);
		sb.append("' AND '");
		sb.append(edDateTime);
		sb.append("' GROUP BY inv.users_Id) tp ");
		List list = dao.LoadAllSql(sb.toString(), null);
		if(!QwyUtil.isNullAndEmpty(list)){
			return list.get(0).toString();
		}
		return "0";
		
	}
	
	
	/**
	 * 分页查询  情人节活动期间 用户首笔投资额
	 * @param pageUtil
	 * @param stDateTime
	 * @param edDateTime
	 * @return   
	 */
	public PageUtil loadValentineFInvestors(PageUtil pageUtil,String stDateTime,String edDateTime,String username,String money){
		
		try {
			
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT inv.users_id,u.username,inv.in_money,inv.pay_time FROM investors inv LEFT JOIN users u ON inv.users_id = u.id WHERE inv.investor_status IN ('1','2','3') "
					+ "and product_id IN (SELECT pro.id FROM product pro WHERE pro.product_type != 1 ) and inv.pay_time BETWEEN '");
			sb.append(stDateTime);
			sb.append("'AND'");
			sb.append(edDateTime);
			sb.append("' ");
			
			if (!QwyUtil.isNullAndEmpty(username)) {
				sb.append("AND u.username ='"+DESEncrypt.jiaMiUsername(username)+" '");
			}
			
			if (!QwyUtil.isNullAndEmpty(money)) {
				if ("0".equals(money)) {  // 3-5千
					sb.append(" AND inv.in_money >=300000 AND inv.in_money <500000 ");
				}
				if ("1".equals(money)) {  // 5-1万
					sb.append(" AND inv.in_money >=500000 AND inv.in_money <1000000 ");
				}
				if ("2".equals(money)) {  // 1-3万
					sb.append(" AND inv.in_money >=1000000 AND inv.in_money <3000000 ");
				}
				if ("3".equals(money)) {  // 3万以上
					sb.append("AND inv.in_money >=3000000");
				}
			}
			
			sb.append(" GROUP BY users_id ORDER BY inv.in_money DESC ");
			
			
			StringBuffer sbCount = new StringBuffer();
			sbCount.append(" SELECT COUNT(*)  ");
			sbCount.append(" FROM (");
			sbCount.append(sb);
			sbCount.append(") t ");
			
			pageUtil=dao.getBySqlAndSqlCount(pageUtil, sb.toString(), sbCount.toString(),null);
			if (!QwyUtil.isNullAndEmpty(pageUtil)) {
				return pageUtil;
			}
			
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		
		return null;
		
	}
	
	
	/**
	 * 分页查询  情人节用户投资总额
	 * @param pageUtil
	 * @param stDateTime
	 * @param edDateTime
	 * @param username  用户名
	 * @param sumMoney  投资总额区间
	 * @return
	 */
	public PageUtil loadValentineDayInvestor(PageUtil pageUtil,String stDateTime,String edDateTime,String username,String sumMoney){
		
		try {
			
			StringBuffer sb = new StringBuffer();
			sb.append("SELECT SUM(inv.in_money)AS 'sumMoney',inv.users_id,ui.phone,ui.real_name FROM investors inv LEFT JOIN users_info ui ON inv.users_id = ui.users_id"
					+ " WHERE inv.investor_status IN ('1','2','3') and inv.product_id IN (SELECT pro.id FROM product pro WHERE pro.product_type != 1 ) and inv.insert_time BETWEEN '");
			sb.append(stDateTime);
			sb.append("'AND'");
			sb.append(edDateTime);
			sb.append(" ' ");
			
			if (!QwyUtil.isNullAndEmpty(username)) {
				sb.append(" AND ui.phone ='"+DESEncrypt.jiaMiUsername(username)+" '");
			}
			
			sb.append(" GROUP BY inv.users_id ");
			
			if (!QwyUtil.isNullAndEmpty(sumMoney)) {
				if ("all".equals(sumMoney)) {  // >=5万
					sb.append(" HAVING sumMoney >=5000000");
				}
				if ("0".equals(sumMoney)) {  // 5-10万
					sb.append(" HAVING sumMoney >=5000000 AND sumMoney <10000000");
				}
				if ("1".equals(sumMoney)) {  // 10 - 30万
					sb.append(" HAVING sumMoney >=10000000 AND sumMoney <30000000");
				}
				if ("2".equals(sumMoney)) {  // 30 - 50万
					sb.append(" HAVING sumMoney >=30000000 AND sumMoney <50000000");
				}
				if ("3".equals(sumMoney)) {  // >=50万
					sb.append(" HAVING sumMoney >=50000000");
				}
			}
			
			sb.append(" ORDER BY sumMoney DESC");
			
			StringBuffer sbCount = new StringBuffer();
			sbCount.append(" SELECT COUNT(*)  ");
			sbCount.append(" FROM (");
			sbCount.append(sb);
			sbCount.append(") t ");
			
			pageUtil=dao.getBySqlAndSqlCount(pageUtil, sb.toString(), sbCount.toString(),null);
			if (!QwyUtil.isNullAndEmpty(pageUtil)) {
				return pageUtil;
			}
			
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		
		return null;
		
	}
	
	
	/**
	 * 分页查询  情人节 用户填写的地址
	 * @param pageUtil
	 * @param stDateTime
	 * @param edDateTime
	 * @return
	 */
	public PageUtil loadAddress(PageUtil pageUtil,String stDateTime,String edDateTime,String username){
		
		try {
			
			StringBuffer sb = new StringBuffer();
			sb.append(" FROM MUsersAddress ma WHERE ma.insertTime BETWEEN '"+stDateTime+"' AND '"+edDateTime+"'");
			
			if (!QwyUtil.isNullAndEmpty(username)) {
				sb.append(" AND ma.phone ='"+username+" '");
			}
			
			pageUtil=dao.getPage(pageUtil, sb.toString(), null);
			if (!QwyUtil.isNullAndEmpty(pageUtil)) {
				return pageUtil;
			}
			
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		
		return null;
		
	}
	
	public boolean modifyAddress(Long id,String address){
		MUsersAddress mAddress = (MUsersAddress) dao.findById(new MUsersAddress(), id);
		
		if (!QwyUtil.isNullAndEmpty(address)) {
			mAddress.setAddress(address);
			mAddress.setUpdateTime(new Date());
		}
		
		dao.update(mAddress);
		return true;
	}
	
	
	/**
	 * 分页获取土豪星球被邀请者的投资情况
	 * @param pageUtil
	 * @return
	 */
	public PageUtil<Object[]> getRichData(PageUtil<Object[]> pageUtil,String insertTime){
		try {
		
			StringBuffer sql = new StringBuffer();
			List<Object> params = new ArrayList<Object>();
			
			sql.append("select * from (");
			sql.append("SELECT (SELECT username FROM users WHERE id = invite.be_invited_id),(SELECT title FROM product WHERE id = inv.product_id), ");
			sql.append(" (SELECT username FROM users WHERE id = invite.invite_id),");
			sql.append(" inv.in_money*0.01,inv.coupon*0.01,inv.hongbao*0.01,inv.insert_time as insert_time,invite.insert_time as i");
			sql.append(" FROM  (select * from invite ii where 1=1 ");
			
			if (!QwyUtil.isNullAndEmpty(insertTime)) {
	            String[] times = QwyUtil.splitTime(insertTime);
	            if (times.length > 1) {
					sql.append(" AND ii.insert_time >= ? ");
					params.add( QwyUtil.fmMMddyyyy.parse(times[0]));
					sql.append(" AND ii.insert_time <= ? ");
					params.add(QwyUtil.fmMMddyyyyHHmmss.parse(times[1]+" 23:59:59"));
				} else {
					sql.append(" AND ii.insert_time >= ? ");
					params.add(QwyUtil.fmMMddyyyyHHmmss.parse(times[0] + " 00:00:00"));
					sql.append(" AND ii.insert_time <= ? ");
					params.add(QwyUtil.fmMMddyyyyHHmmss.parse(times[0] + " 23:59:59"));
				}
	        }else{
	        	sql.append(" and ii.insert_time between '2017-03-22 00:00:00' and '2017-04-15 23:59:59'");
	        }
			
			sql.append(" ) invite ");
			sql.append(" left join (SELECT * FROM investors invi WHERE invi.investor_status IN ('1','2','3') ");
			sql.append(" AND invi.insert_time between '2017-03-22 00:00:00' and '2017-04-15 23:59:59'");
			sql.append(" ) inv on inv.users_id = invite.be_invited_id ");
			sql.append(" ORDER BY inv.copies DESC,invite.insert_time ASC");
			sql.append(") tt ");
			StringBuffer sqlCount = new StringBuffer();
			
			sqlCount.append("SELECT COUNT(*) FROM (");
			sqlCount.append(sql);
			sqlCount.append(" ) t");
			pageUtil = dao.getBySqlAndSqlCount(pageUtil, sql.toString(), sqlCount.toString(),params.toArray());
			return pageUtil;
		} catch (Exception e) {
			log.error("getRichData",e);
		}
		return null;
	}
	
	
	/**
	 * 活动期内  不如投一场后台数据
	 * 				投资总额不含理财券  不含新手标周标
	 * @param pageUtil
	 * @param username
	 * @param stTime
	 * @param etTime
	 * @return
	 */
	public PageUtil<Object[]> getTycData(PageUtil<Object[]> pageUtil,String username,String stTime,String etTime){
		try {
			
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT t.users_id,t.uname,t.realname,t.sumInMoney,t.sumCoupon,t.sumCopies,t.pay_time,");
			sql.append(" a.contract_name,a.phone,a.address,a.address_detail,a.zip_code FROM(");
			sql.append(" SELECT  users_id,(SELECT username FROM users u WHERE id = inv.users_id)uname,");
			sql.append(" (SELECT real_name FROM users_info WHERE users_id = inv.users_id)realname,");
			sql.append(" SUM(in_money*0.01)sumInMoney,SUM(coupon*0.01)sumCoupon,SUM(copies)sumCopies,pay_time");
			sql.append(" FROM investors inv");
			sql.append(" WHERE investor_status IN ('1','2','3')");
			sql.append(" AND pay_time BETWEEN '"+stTime+"' AND '"+etTime+"'");
			sql.append(" AND product_id IN (");
			sql.append(" SELECT id FROM product WHERE title NOT LIKE '%新手%' AND lcqx >=30)");
			sql.append(" GROUP BY users_id ORDER BY sumCopies DESC )t");
			sql.append(" LEFT JOIN");
			sql.append(" (SELECT users_id, contract_name,phone,address,address_detail,zip_code ");
			sql.append(" FROM m_users_address WHERE STATUS = 0");
			sql.append(" AND insert_time BETWEEN '"+stTime+"' AND '"+etTime+"'");
			sql.append(" )a");
			sql.append(" ON t.users_id = a.users_id");
			sql.append(" WHERE 1=1");
			if (!QwyUtil.isNullAndEmpty(username)) {
				sql.append(" AND t.uname = '"+DESEncrypt.jiaMiUsername(username)+"'");
			}
			
			StringBuffer sqlCount = new StringBuffer();
			sqlCount.append("SELECT COUNT(*) FROM (");
			sqlCount.append(sql);
			sqlCount.append(" )b");
			
			pageUtil = dao.getBySqlAndSqlCount(pageUtil, sql.toString(), sqlCount.toString(), null);
			
			if (!QwyUtil.isNullAndEmpty(pageUtil)) {
				return pageUtil;
			}
			
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		
		return null;
	}
	
	
	/**
	 * @param pageUtil
	 * @param insertTime 插入时间
	 * @param username  用户名 未加密的 sql 中加密
	 * @return
	 */
	public PageUtil<Object[]> getMessageList(PageUtil<Object[]> pageUtil,String insertTime,String username){
		StringBuffer sql = new StringBuffer();
		List<Object> ob = new ArrayList<Object>();
		try {
			sql.append(" SELECT username,(SELECT real_name FROM users_info WHERE users_id = h.users_id)real_name,");
			sql.append(" insert_time,message FROM hd_message h WHERE 1 = 1");
			
			if (!QwyUtil.isNullAndEmpty(username)) {
				sql.append(" AND username = ?");
				ob.add(DESEncrypt.jiaMiUsername(username));
			}
			
			if (!QwyUtil.isNullAndEmpty(insertTime)) {
				String[] times = QwyUtil.splitTime(insertTime);
	            if (times.length > 1) {
	            	sql.append(" AND insert_time >= ? ");
					ob.add( QwyUtil.fmMMddyyyy.parse(times[0]));
					sql.append(" AND insert_time <= ? ");
					ob.add(QwyUtil.fmMMddyyyyHHmmss.parse(times[1]+" 23:59:59"));
				} else {
					sql.append(" AND insert_time >= ? ");
					ob.add(QwyUtil.fmMMddyyyyHHmmss.parse(times[0] + " 00:00:00"));
					sql.append(" AND insert_time <= ? ");
					ob.add(QwyUtil.fmMMddyyyyHHmmss.parse(times[0] + " 23:59:59"));
				}
			}
			
			sql.append(" ORDER BY insert_time DESC");
			
			StringBuffer sqlCount = new StringBuffer();
			sqlCount.append(" SELECT COUNT(*) FROM (");
			sqlCount.append(sql);
			sqlCount.append(" )t");
			
			return dao.getBySqlAndSqlCount(pageUtil, sql.toString(), sqlCount.toString(), ob.toArray());
			
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		
		return null;
		
	}
	
	
	/**
	 * 分页查询 端午后台数据
	 * @param pageUtil
	 * @param username
	 * @param stTime
	 * @param etTime
	 * @return
	 */
	public PageUtil<Object[]> dwRecord(PageUtil<Object[]> pageUtil,String username,String stTime,String etTime,String czEt){
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT t.uname,t.realname,t.sumCz,t.sumInv,t.free_num,a.note,a.address,a.insert_time FROM (");
		sql.append(" SELECT  users_id,(SELECT username FROM users u WHERE id = lr.users_id)uname,");
		sql.append(" (SELECT real_name FROM users_info WHERE users_id = lr.users_id)realname,");
		sql.append(" (SELECT SUM(money)*0.01 FROM cz_record WHERE STATUS = '1' AND check_time BETWEEN '"+stTime+"' AND '"+czEt+"' AND users_id = lr.users_id )sumCz,");
		sql.append(" (SELECT SUM(in_money)*0.01 FROM investors WHERE investor_status IN ('1','2','3') ");
		sql.append(" AND pay_time BETWEEN '"+stTime+"' AND '"+etTime+"' AND users_id = lr.users_id)sumInv,");
		sql.append(" free_num ");
		sql.append(" FROM lottery_times lr WHERE type = '2' AND insert_time BETWEEN '"+stTime+"' AND '"+etTime+"')t");
		sql.append(" LEFT JOIN ");
		sql.append(" (SELECT note,address,users_id,insert_time FROM lottery_record WHERE type = '1' ");
		sql.append(" AND insert_time BETWEEN '"+stTime+"' AND '"+etTime+"')a");
		sql.append(" ON t.users_id = a.users_id WHERE 1=1");
		
		if (!QwyUtil.isNullAndEmpty(username)) {
			sql.append(" AND t.uname = '"+DESEncrypt.jiaMiUsername(username)+"'");
		}
		
		sql.append(" ORDER BY insert_time DESC");
		
		StringBuffer sqlCount = new StringBuffer();
		sqlCount.append(" SELECT COUNT(*) FROM (");
		sqlCount.append(sql);
		sqlCount.append(" )b");
		return dao.getBySqlAndSqlCount(pageUtil, sql.toString(), sqlCount.toString(), null);
	}
	
	/**
	 * 分页获取 拥有粽子的用户
	 * @param pageUtil
	 * @param type
	 * @param stTime
	 * @param etTime
	 * @return
	 */
	public PageUtil<Object[]> getLotteryTimeRecord(PageUtil<Object[]> pageUtil,String type,String stTime,String etTime){
		StringBuffer sql = new StringBuffer();
		
		sql.append(" SELECT users_id,free_num FROM lottery_times WHERE 1=1");
		if (!QwyUtil.isNullAndEmpty(type)) {
			sql.append(" AND type = '"+type+"'");
		}
		
		if (!QwyUtil.isNullAndEmpty(stTime)) {
			sql.append(" AND insert_time BETWEEN '"+stTime+"' AND '"+etTime+"'");	
		}
		
		StringBuffer sqlCount = new StringBuffer();
		sqlCount.append(" SELECT COUNT(*) FROM (");
		sqlCount.append(sql);
		sqlCount.append(" )a");
		
		return dao.getBySqlAndSqlCount(pageUtil, sql.toString(), sqlCount.toString(), null);
	}
	
	/**
	 * 查找用户的抽奖机会对象
	 * @param usersId
	 * @param type null:五月挖宝 1：微信抽奖 2端午活动 3六月热活动
	 * @return
	 */
	public LotteryTimes findLotteryTimesByUId(long usersId,String type){
		StringBuffer hql = new StringBuffer();
		List<Object> ob = new ArrayList<Object>();
		hql.append(" FROM LotteryTimes WHERE 1=1 ");
		if (!QwyUtil.isNullAndEmpty(usersId)) {
			hql.append(" AND usersId = ?");
			ob.add(usersId);
		}
		
		if (!QwyUtil.isNullAndEmpty(type)) {
			hql.append(" AND type = ?");
			ob.add(type);
		}
		
		LotteryTimes lr = (LotteryTimes) dao.findJoinActive(hql.toString(), ob.toArray());
		
		return lr;
	}
	
	/**
	 * 保存抽奖机会对象
	 * @param lotteryTimes 页面传值对象
	 * @param users  用户对象
	 * @return
	 */
	public String addLotteryTimes(LotteryTimes lotteryTimes,Users users){
		LotteryTimes lr = new LotteryTimes();
		lr.setFreeNum(lotteryTimes.getFreeNum());
		lr.setPayNum(lotteryTimes.getFreeNum());
		lr.setUsers(users);
		lr.setUsersId(users.getId());
		lr.setType(lotteryTimes.getType());
		lr.setInsertTime(new Date());
		lr.setUpdateTime(lr.getInsertTime());
		return dao.save(lr);
		
	}
	
	/**
	 *  抽奖机会记录
	 * @param type  
	 * @param usersId
	 * @param note  备注
	 * @param address 兑换实物时的地址
	 */
	public String addLotteryRecord(String type,Long usersId,String note,String address){
		LotteryRecord lotteryRecord = new LotteryRecord();
		lotteryRecord.setUsersId(usersId);
		lotteryRecord.setType(type);
		lotteryRecord.setInsertTime(new Date());
		lotteryRecord.setUpdateTime(lotteryRecord.getInsertTime());
		lotteryRecord.setNote(note);
		lotteryRecord.setAddress(address);
		
		return dao.save(lotteryRecord);
	}
	
	/**
	 * 修改抽奖机会
	 * @param lt 要修改的对象
	 * @param time 添加的机会
	 * @return
	 */
	public boolean updateLottery(LotteryTimes lt,Long time){
		if (QwyUtil.isNullAndEmpty(lt)) {
			return false;
		}
		Long num = QwyUtil.isNullAndEmpty(lt.getFreeNum())?0:lt.getFreeNum();
		time = QwyUtil.isNullAndEmpty(time)?0:time;
		lt.setFreeNum(num+time);
		lt.setUpdateTime(new Date());
		dao.update(lt);
		return true;
	}
}
