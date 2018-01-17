package com.huoq.thread.bean;

import javax.annotation.Resource;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.thread.dao.ThreadDAO;

@Service
public class SendGdpThreadBean {
	
	private Logger log = Logger.getLogger(SendProfitThreadBean.class);
	@Resource
	private ThreadDAO dao;
	
	/**
	 * 分页查询各用户的投资总额 总收入
	 * @param pageUtil
	 * @param stTime
	 * @param etTime
	 * @return
	 */
	public PageUtil getsumSY(PageUtil pageUtil,String stTime,String etTime){
		StringBuffer sql = new StringBuffer();
		
		sql.append(" SELECT users_id,SUM(expect_earnings)sumShouyi,SUM(copies) FROM investors");
		sql.append(" WHERE investor_status IN ('1','2','3')");
		sql.append(" AND pay_time BETWEEN '"+stTime+"' AND '"+etTime+"'");
		sql.append(" GROUP BY users_id");
		sql.append(" ORDER BY sumShouyi DESC");
		
		StringBuffer sqlCount = new StringBuffer();
		sqlCount.append(" SELECT COUNT(*) FROM (");
		sqlCount.append(sql);
		sqlCount.append(" )t");
		
		pageUtil = dao.getBySqlAndSqlCount(pageUtil, sql.toString(), sqlCount.toString(), null);
		
		if (!QwyUtil.isNullAndEmpty(pageUtil)) {
			return pageUtil;
		}
		
		return null;
	}
	
}
