package com.huoq.thread.bean;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.thread.dao.ThreadDAO;

@Service
public class SendsmsThreadBean {
	private Logger log = Logger.getLogger(SendsmsThreadBean.class);
	@Resource
	private ThreadDAO dao;
	
	/**
	 * 分页查询  得到未投资的用户手机号码   加密的
	 * @return
	 */
	public PageUtil<Object> getList(PageUtil<Object> pageUtil){
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT phone FROM users_info WHERE users_id NOT IN (SELECT users_id FROM investors WHERE investor_status IN ('1','2','3') GROUP BY users_id)");
		
		StringBuffer sqlCount = new StringBuffer();
		sqlCount.append("SELECT COUNT(*) FROM ( ");
		sqlCount.append(sql);
		sqlCount.append(" ) t");
		pageUtil = dao.getBySqlAndSqlCount(pageUtil, sql.toString(), sqlCount.toString(), null);
		if (!QwyUtil.isNullAndEmpty(pageUtil)) {
			return pageUtil;
		}
		return null;
		
	}
	
	/**
	 * 分页查询所有用户的用户名 ：即手机号码
	 * @param pageUtil
	 * @return
	 */
	public PageUtil<Object> getUsername(PageUtil<Object> pageUtil){
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT username FROM users");
		
		StringBuffer sqlCount = new StringBuffer();
		sqlCount.append("SELECT COUNT(*) FROM ( ");
		sqlCount.append(sql);
		sqlCount.append(" ) t");
		pageUtil = dao.getBySqlAndSqlCount(pageUtil, sql.toString(), sqlCount.toString(), null);
		if (!QwyUtil.isNullAndEmpty(pageUtil)) {
			return pageUtil;
		}
		return null;
		
	}
	
	/**
	 * 获取已投资用户的用户名   即手机号码
	 * @param pageUtil
	 * @return
	 */
	public PageUtil<Object> getInvUsername(PageUtil<Object> pageUtil){
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT username FROM users");
		sql.append(" WHERE id IN ");
		sql.append(" (SELECT users_id FROM investors WHERE investor_status IN ('1','2','3') GROUP BY users_id )");
		
		StringBuffer sqlCount = new StringBuffer();
		sqlCount.append("SELECT COUNT(*) FROM ( ");
		sqlCount.append(sql);
		sqlCount.append(" ) t");
		pageUtil = dao.getBySqlAndSqlCount(pageUtil, sql.toString(), sqlCount.toString(), null);
		if (!QwyUtil.isNullAndEmpty(pageUtil)) {
			return pageUtil;
		}
		
		return null;
	}
	
	/**
	 * 分页获取平台绑卡用户
	 * @param pageUtil
	 * @return
	 */
	public PageUtil<Object> getBindUsername(PageUtil<Object> pageUtil){
		
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT phone FROM users_info WHERE is_bind_bank = 1");
		
		StringBuffer sqlCount = new StringBuffer();
		sqlCount.append("SELECT COUNT(*) FROM ( ");
		sqlCount.append(sql);
		sqlCount.append(" ) t");
		pageUtil = dao.getBySqlAndSqlCount(pageUtil, sql.toString(), sqlCount.toString(), null);
		if (!QwyUtil.isNullAndEmpty(pageUtil)) {
			return pageUtil;
		}
		
		return null;
	}

	/**
	 * 获取该时间段的还款用户用户名
	 * @param pageUtil
	 * @param stTime
	 * @param etTime
	 * @return
	 */
	public PageUtil<Object> getReturnUsers(PageUtil<Object> pageUtil,String stTime,String etTime) {
		
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT (SELECT username FROM users WHERE id = i.users_id) FROM interest_details i ");
		sql.append(" WHERE i.STATUS = '0'");
		sql.append(" AND i.return_time BETWEEN '"+stTime+"' AND '"+etTime+"'");
		sql.append(" GROUP BY i.users_id");
		
		StringBuffer sqlCount = new StringBuffer();
		sqlCount.append("SELECT COUNT(*) FROM (");
		sqlCount.append(sql);
		sqlCount.append(" ) t");
		
		pageUtil = dao.getBySqlAndSqlCount(pageUtil, sql.toString(), sqlCount.toString(), null);
		
		return pageUtil;
	}
	
	/**
	 * 根据手机号码查询是否已经发送过该短信
	 * @param mobile
	 * @return
	 */
	public boolean isSendSms(String mobile,String stTime,String etTime) {
		StringBuffer sql = new StringBuffer();
		
		sql.append(" SELECT * FROM sms_record");
		sql.append(" WHERE mobile = '"+mobile+"'");
		sql.append(" AND `STATUS` = '1'");
		if (!QwyUtil.isNullAndEmpty(stTime)) {
			sql.append(" AND insert_time BETWEEN '"+stTime+"' AND '"+etTime+"'");
		}
		
		sql.append(" AND sms_content LIKE '%【枫叶网】您有笔投资即将到期%'");
		
		List list = dao.LoadAllSql(sql.toString(), null);
		if (QwyUtil.isNullAndEmpty(list)) {
			return true;
		}
		return false;
	}
	
	/**
	 *   获取绑卡用户 活动期内 未投资的
	 * @param pageUtil
	 * @param stTime
	 * @param etTime
	 * @return
	 */
	public PageUtil<Object> getBindUnInveUsers(PageUtil<Object> pageUtil,String stTime,String etTime) {
		
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT phone FROM users_info WHERE is_bind_bank = 1");
		sql.append(" AND users_id NOT IN (");
		sql.append(" SELECT users_id FROM investors WHERE investor_status IN ('1','2','3')");
		sql.append(" AND pay_time BETWEEN '"+stTime+"' AND '"+etTime+"'");
		sql.append(" GROUP BY users_id )");
		
		StringBuffer sqlCount = new StringBuffer();
		sqlCount.append("SELECT COUNT(*) FROM (");
		sqlCount.append(sql);
		sqlCount.append(" ) t");
		
		pageUtil = dao.getBySqlAndSqlCount(pageUtil, sql.toString(), sqlCount.toString(), null);

		return pageUtil;
	}
	
	
	/**
	 * 活动期内  投资满额 未填地址的用户
	 * @param pageUtil
	 * @param stTime
	 * @param etTime
	 * @return
	 */
	public PageUtil<Object[]> getNoAddrUsers(PageUtil<Object[]> pageUtil,String stTime,String etTime){
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT (SELECT username FROM users WHERE id = users_id),SUM(in_money)sumMoney,users_id FROM investors");
		sql.append(" WHERE investor_status IN ('1','2','3') ");
		sql.append(" AND pay_time BETWEEN '"+stTime+"' AND '"+etTime+"'");
		sql.append(" AND product_id IN (");
		sql.append(" SELECT id FROM product WHERE title NOT LIKE '%新手%' AND lcqx >=30)");
		sql.append(" AND users_id NOT IN (");
		sql.append(" SELECT users_id FROM m_users_address WHERE STATUS = '0'");
		sql.append(" AND insert_time BETWEEN '"+stTime+"' AND '"+etTime+"' )");
		sql.append(" GROUP BY users_id");
		sql.append(" HAVING sumMoney >=800000");
		sql.append(" ORDER BY sumMoney DESC");
		
		StringBuffer sqlCount = new StringBuffer();
		sqlCount.append("SELECT COUNT(*) FROM (");
		sqlCount.append(sql);
		sqlCount.append(" ) t");
		
		pageUtil = dao.getBySqlAndSqlCount(pageUtil, sql.toString(), sqlCount.toString(), null);
		return pageUtil;
	}
	
	/**
	 * 平台 旧版本的用户
	 * @param pageUtil
	 * @return
	 */
	public PageUtil<Object> getOldVersionUsers(PageUtil<Object> pageUtil){
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT (SELECT username from users where id = ii.`users_id` )  FROM `investors` ii ");
		sql.append(" WHERE ii.`users_id` NOT IN (");
		sql.append(" select inv.`users_id`  from `investors` inv WHERE inv.`api_version` IS NOT NULL");
		sql.append(" and inv.`investor_status` in (1,2,3) GROUP BY inv.`users_id` ) ");
		sql.append(" and ii.`investor_status` in (1,2,3) GROUP BY ii.`users_id`");
		
		StringBuffer sqlCount = new StringBuffer();
		sqlCount.append("SELECT COUNT(*) FROM (");
		sqlCount.append(sql);
		sqlCount.append(" ) t");
		
		pageUtil = dao.getBySqlAndSqlCount(pageUtil, sql.toString(), sqlCount.toString(), null);
		return pageUtil;
	}
	
	
}
