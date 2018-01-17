package com.huoq.thread.bean;
/**
 * 枫叶星球 奖励共享
 * @author wxl
 * 		2017年3月16日15:03:50
 *
 */

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.account.bean.UserRechargeBean;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.thread.dao.ThreadDAO;

@Service
public class SendRichThreadBean {
	
	private Logger log = Logger.getLogger(SendProfitThreadBean.class);
	@Resource
	private ThreadDAO dao;
	@Resource
	private UserRechargeBean userRechargeBean;
	
	/**
	 * 分页查询 投资排行榜
	 * @param pageUtil
	 * @param stTime
	 * @param etTime
	 * @return
	 */
	public PageUtil getRankingList(PageUtil pageUtil,String stTime,String etTime){
		
		StringBuffer sql = new StringBuffer();
		List<Object> ob=new ArrayList<Object>();
		
		sql.append("SELECT *,(select `username`  from `users` WHERE id =myInviId),(own+ friend) 'total' FROM(");
		sql.append(" select invite_id 'myInviId', IFNULL(sum(copies), 0) 'own', IFNULL((");
		sql.append(" SELECT SUM(invi.copies) FROM `investors` invi ");
		sql.append(" WHERE invi.`investor_status` IN('1', '2', '3')");
		sql.append(" AND invi.`pay_time` >=?");
		sql.append(" AND invi.`pay_time` <=?");
		ob.add(stTime);
		ob.add(etTime);
		sql.append("  AND invi.product_id IN (");
		sql.append(" SELECT id FROM product p WHERE p.title NOT LIKE '%新手%' AND p.title NOT LIKE '%周利宝%')");
		sql.append(" AND invi.`users_id` IN(");
		sql.append(" SELECT inte.`be_invited_id` FROM `invite` inte WHERE inte.`invite_id`= myInviId AND inte.insert_time >= ? and insert_time <= ? )), 0) 'friend'");
		ob.add(stTime);
		ob.add(etTime);
//		sql.append(" SELECT inte.`be_invited_id` FROM `invite` inte WHERE inte.`invite_id`= myInviId )), 0) 'friend'");
		sql.append(" from(");
		sql.append(" select ite.invite_id from invite ite ");
		sql.append(" WHERE ite.insert_time >=?");
		sql.append(" AND ite.insert_time <=?");
		ob.add(stTime);
		ob.add(etTime);
		sql.append(" GROUP BY ite.invite_id) t_inv");
		sql.append(" LEFT JOIN investors inv ON invite_id= inv.users_id");
		sql.append(" AND inv.investor_status IN(1, 2, 3)");
		sql.append(" AND inv.pay_time >=?");
		sql.append(" AND inv.pay_time <=?");
		ob.add(stTime);
		ob.add(etTime);
		sql.append("  AND inv.product_id IN (");
		sql.append(" SELECT id FROM product p WHERE p.title NOT LIKE '%新手%' AND p.title NOT LIKE '%周利宝%')");
		sql.append(" GROUP BY invite_id) temp");
		sql.append(" HAVING total >= 200000");
		sql.append(" ORDER BY total DESC ");
		
		StringBuffer sqlCount = new StringBuffer();
		sqlCount.append("SELECT COUNT(*) FROM (");
		sqlCount.append(sql);
		sqlCount.append(" ) t");
		
		pageUtil = dao.getBySqlAndSqlCount(pageUtil, sql.toString(), sqlCount.toString(), ob.toArray());
		
		if (!QwyUtil.isNullAndEmpty(pageUtil)) {
			return pageUtil;
		}
		
		return null;
		
	}
	
	
	/**
	 * 某段时间内  邀请好友的投资总收益
	 * @param usersId
	 * @param st
	 * @param et
	 * @return
	 */
	public Object getInviteSum(Long usersId,String st,String et){
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT SUM(expect_earnings) FROM investors WHERE users_id IN (SELECT be_invited_id FROM invite WHERE invite_id = "+usersId);
		sql.append(" AND insert_time BETWEEN '"+st+"' AND '"+et+" '");
		sql.append(" ) AND investor_status IN ('1','2','3')");
		
		sql.append(" AND product_id IN (SELECT id FROM product WHERE title NOT LIKE '%新手%' AND title NOT LIKE '%周利宝%')");
	
		sql.append(" AND insert_time BETWEEN '"+st+"' AND '"+et+"'");
		Object object = dao.getSqlCount(sql.toString(), null);
		
		return object;
	}
	
	
	/**
	 * 根据ID 获取活动期内 该用户的投资总收益
	 * @param usersId
	 * @param st
	 * @param et
	 * @return
	 */
	public Object getSumInvestorById(Long usersId,String st,String et){
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT SUM(expect_earnings) FROM investors WHERE users_id = "+usersId+" AND investor_status IN ('1','2','3')");

		sql.append("  AND product_id IN (SELECT id FROM product WHERE title NOT LIKE '%新手%' AND title NOT LIKE '%周利宝%') ");
	
		sql.append("  AND insert_time BETWEEN '"+st+"' AND '"+et+"'");
		
		Object object = dao.getSqlCount(sql.toString(), null);
		
		return object;
	}
	
	
	/**
	 * 根据usersID  获取该用户 活动期内邀请的好友
	 * @param usersId
	 * @param stTime
	 * @param etTime
	 * @return
	 */
	public List getListId(Long usersId,String stTime,String etTime){
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT be_invited_id FROM invite WHERE invite_id = "+usersId);
		sql.append(" AND insert_time BETWEEN '"+stTime+"' AND '"+etTime+"'");
		
		List list = dao.LoadAllSql(sql.toString(), null);
		if (!QwyUtil.isNullAndEmpty(list)) {
			return list;
		}
		return null;
	}
	
	/**
	 * 给集合内的用户 发放奖励
	 * @param list  用户ID集合
	 * @param money  要发放的奖励金额
	 */
	public void rechargeByUsersId(List list,Double money){
		
		if (!QwyUtil.isNullAndEmpty(list)) {
			for (int i = 0; i < list.size(); i++) {
				Long usersId = Long.parseLong(list.get(i).toString()); //获取ID
				//根据ID 给该用户进行充值奖励
				boolean isOk = userRechargeBean.usreRecharge(usersId, money, "cz", "系统充值", "土豪星球奖励共享");
				userRechargeBean.addCzRecordJL(usersId, money, null, null, null, null, "土豪星球奖励共享", "1", null);
			}
			
		}
		
	}
	
	
}
