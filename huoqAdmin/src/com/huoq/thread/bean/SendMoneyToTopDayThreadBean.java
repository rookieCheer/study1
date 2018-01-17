package com.huoq.thread.bean;
/**
 *新手奖励 每日最高 发放30元到余额中
 * @author wxl
 * 		2017年4月5日15:49:19
 *
 */

import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.Investors;
import com.huoq.thread.dao.ThreadDAO;

@Service
public class SendMoneyToTopDayThreadBean {
	
	@Resource
	private ThreadDAO dao;

	/**
	 * 获取时间段内的首投用户
	 * @param stTime
	 * @param etTime
	 * @return
	 */
	public List<Object[]> getYestodayTopInv(String stTime,String etTime){
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT (SELECT username FROM users WHERE id = inv.users_id),copies,users_id,pay_time FROM investors inv");
		sql.append(" WHERE  inv.investor_status IN ('1','2','3') ");
		sql.append(" AND inv.pay_time BETWEEN '"+stTime+"' AND '"+etTime+"' ");
		sql.append(" GROUP BY inv.users_id");
		sql.append(" ORDER BY inv.copies DESC,inv.pay_time ASC");
		
		List<Object[]> list = dao.LoadAllSql(sql.toString(), null);
		if (!QwyUtil.isNullAndEmpty(list)) {
			return list;
		}
		
		return null;
		
	}
	
	/**
	 * 根据用户ID 获取改用户的投资记录
	 * @param usersId
	 * @return
	 */
	public List<Object[]> getInvestList(Long usersId){
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT users_id,pay_time,copies FROM (");
		sql.append(" SELECT * FROM investors");
		sql.append(" WHERE users_id ="+usersId);
		sql.append(" AND investor_status IN ('1','2','3') ");
		sql.append(" ORDER BY insert_time ASC )t");
		sql.append(" GROUP BY users_id");
		List list = dao.LoadAllSql(sql.toString(), null);
		if (!QwyUtil.isNullAndEmpty(list)) {
			return list;
		}
		return null;
	}
	
	
	/**
	 * 活动期内  当天首投的用户  
	 * @param stTime
	 * @param etTime
	 * @return
	 */
	public List<Object[]> getOneFirInvList(String stTime,String etTime){
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT * FROM (");
		sql.append(" SELECT (SELECT username FROM users WHERE id = users_id)uname,copies,users_id,pay_time FROM (");
		sql.append(" SELECT copies,pay_time,users_id FROM investors inves");
		sql.append(" WHERE inves.pay_time BETWEEN '"+stTime+"' AND '"+etTime+"'");
		sql.append(" AND inves.investor_status IN ('1','2','3')");
		sql.append(" ORDER BY pay_time ASC )a GROUP BY users_id)b");
		sql.append(" WHERE NOT EXISTS (");
		sql.append(" SELECT users_id FROM investors inv WHERE inv.users_id = b.users_id");
		sql.append(" AND DATE_FORMAT(b.pay_time,'%Y-%m-%d') > DATE_FORMAT(inv.pay_time,'%Y-%m-%d'))");
		sql.append(" ORDER BY copies DESC");
		
		List<Object[]> list = dao.LoadAllSql(sql.toString(), null);
		if (!QwyUtil.isNullAndEmpty(list)) {
			return list;
		}
		return null;
	}
	
	/**
	 * 根据用户ID 获取该用户今天是否已经得到奖励的记录
	 * 				为空则返回TRUE  不为空则FALSE
	 * @param usersId
	 * @param stTime
	 * @param etTime
	 * @return
	 */
	public boolean getCzRecord(Long usersId,String stTime,String etTime){
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT * FROM cz_record WHERE users_id = "+usersId);
		sql.append(" AND STATUS = '1' ");
		sql.append(" AND insert_time BETWEEN '"+stTime+"' AND '"+etTime+"'");
		sql.append(" AND note LIKE '%新手每日最高奖励%'");
		List list = dao.LoadAllSql(sql.toString(), null);
		if (QwyUtil.isNullAndEmpty(list)) {
			return true;
		}
		return false;
	}
	
}
