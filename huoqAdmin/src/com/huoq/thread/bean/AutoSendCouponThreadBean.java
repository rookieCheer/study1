package com.huoq.thread.bean;

import java.util.List;
import javax.annotation.Resource;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.thread.dao.ThreadDAO;

@Service
public class AutoSendCouponThreadBean {
	private Logger log = Logger.getLogger(AutoSendCouponThreadBean.class);
	@Resource
	private ThreadDAO dao;
	
	/**
	 * 获取该时间段的还款用户
	 * @param pageUtil
	 * @param stTime
	 * @param etTime
	 * @return
	 */
	public PageUtil getReturnUsers(PageUtil pageUtil,String stTime,String etTime) {
		
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT users_id FROM interest_details");
		sql.append(" WHERE STATUS = '2'");
		sql.append(" AND return_time BETWEEN '"+stTime+"' AND '"+etTime+"'");
		sql.append(" GROUP BY users_id");
		
		StringBuffer sqlCount = new StringBuffer();
		sqlCount.append("SELECT COUNT(*) FROM (");
		sqlCount.append(sql);
		sqlCount.append(" ) t");
		
		pageUtil = dao.getBySqlAndSqlCount(pageUtil, sql.toString(), sqlCount.toString(), null);
		
		return pageUtil;
	}
	
	
	/**
	 * 根据用户ID 获取该用户活动期间是否已经发放奖励  去重复
	 * @param usersId
	 * @return
	 */
	public boolean getCouponRecord(Long usersId){
		
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT * FROM coupon ");
		sql.append(" WHERE users_id = "+usersId);
		sql.append(" AND insert_time BETWEEN '2017-04-08 00:00:00' AND '2017-04-12 23:59:59'");
		sql.append(" AND note LIKE '%返款奖励%'");
		
		List list = dao.LoadAllSql(sql.toString(), null);
		if (QwyUtil.isNullAndEmpty(list)) {
			return true;
		}
		
		return false;
	}
	

}
