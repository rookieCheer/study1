package com.huoq.thread.bean;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.thread.dao.ThreadDAO;

@Service
public class SendCzCouponThreadBean {
	
	private Logger log = Logger.getLogger(SendProfitThreadBean.class);
	@Resource
	private ThreadDAO dao;
	
	
	/**分页查询
	 * 查询 时间段内 用户的充值总额		
	 * @param pageUtil
	 * @param status 充值状态  传参格式："'1','2'"
	 * @param stTime
	 * @param etTime
	 * @return
	 */
	public PageUtil<Object[]> getCzSumByUId(PageUtil<Object[]> pageUtil,String status,String stTime,String etTime,double atLeast){
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT * FROM (");
		sql.append(" SELECT SUM(money)*0.01 sumMoney,users_id FROM cz_record WHERE 1=1");
		
		//充值状态
		if (!QwyUtil.isNullAndEmpty(status)) {
			sql.append(" AND status IN ("+status+")");
		}
		// 时间
		if (!QwyUtil.isNullAndEmpty(stTime)) {
			sql.append(" AND check_time BETWEEN '"+stTime+"' AND '"+etTime+"'");
		}
		
		sql.append(" GROUP BY users_id ORDER BY sumMoney DESC) a");
		sql.append("  WHERE 1=1");
		if (!QwyUtil.isNullAndEmpty(atLeast)) {
			sql.append(" AND sumMoney >=" +atLeast);
		}
		
		StringBuffer sqlCount = new StringBuffer();
		sqlCount.append("SELECT COUNT(*) FROM (");
		sqlCount.append(sql);
		sqlCount.append(" )t");
		
		return dao.getBySqlAndSqlCount(pageUtil, sql.toString(), sqlCount.toString(), null);
		
	}
	
	/**
	 * 活动期内是否活动某种理财券
	 * @param usersId 用户ID
	 * @param stTime  活动时间 string类型
	 * @param etTime
	 * @param note  活动备注
	 * @return
	 */
	public boolean getCoupon(Long usersId,String stTime,String etTime,String note){
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT * FROM coupon WHERE 1 = 1");
		if (!QwyUtil.isNullAndEmpty(usersId)) {
			sql.append(" AND users_id = "+usersId);
		}
		
		if (!QwyUtil.isNullAndEmpty(stTime)) {
			sql.append(" AND insert_time BETWEEN '"+stTime+"' AND '"+etTime+"'");
		}
		
		if (!QwyUtil.isNullAndEmpty(note)) {
			sql.append(" AND note LIKE '%"+note+"%'");
		}
		
		List list = dao.LoadAllSql(sql.toString(), null);
		if (QwyUtil.isNullAndEmpty(list)) {
			return true;
		}
		return false;
	}
	
}
