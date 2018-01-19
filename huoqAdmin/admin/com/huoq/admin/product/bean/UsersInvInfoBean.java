package com.huoq.admin.product.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.admin.product.dao.UsersInvInfoDao;
import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;

/**
 * 用户投资状况数据报表
 * @author wxl
 * 2017年3月28日10:52:11
 *
 */
@Service
public class UsersInvInfoBean {
	
	private static Logger log = Logger.getLogger(UsersInvInfoBean.class);

	@Resource
	UsersInvInfoDao dao;
	
	/**
	 * 分页获取 未投资新手产品的用户信息
	 * @param pageUtil
	 * @param insertTime 注册时间
	 * @param username 用户名
	 * @return
	 */
	public PageUtil<Object[]> loadNotBuyFreshProList(PageUtil<Object[]> pageUtil,String insertTime,String username){
		try {
			
			StringBuffer sql = new StringBuffer();
			List<Object> params = new ArrayList<Object>();
			sql.append(" SELECT u.users_id,u.phone,u.real_name,u.age,u.sex,u.insert_time,");
			sql.append(" IFNULL((SELECT SUM(in_money) FROM investors inv ");
			sql.append(" WHERE inv.investor_status IN ('1','2','3') AND inv.users_id = u.users_id )*0.01 ,0)inMoney");
			sql.append(" FROM users_info u");
			sql.append(" WHERE u.is_bind_bank = '1'");
			sql.append(" AND u.users_id NOT IN (");
			sql.append(" SELECT users_id FROM investors WHERE product_id IN (SELECT id FROM product WHERE title LIKE '%新手%') )");
			
			if (!QwyUtil.isNullAndEmpty(insertTime)) {
				String[] times = QwyUtil.splitTime(insertTime);
	            if (times.length > 1) {
					sql.append(" AND u.insert_time >= ? ");
					params.add( QwyUtil.fmMMddyyyy.parse(times[0]));
					sql.append(" AND u.insert_time <= ? ");
					params.add(QwyUtil.fmMMddyyyyHHmmss.parse(times[1]+" 23:59:59"));
				} else {
					sql.append(" AND u.insert_time >= ? ");
					params.add(QwyUtil.fmMMddyyyyHHmmss.parse(times[0] + " 00:00:00"));
					sql.append(" AND u.insert_time <= ? ");
					params.add(QwyUtil.fmMMddyyyyHHmmss.parse(times[0] + " 23:59:59"));
				}
			}
			
			if (!QwyUtil.isNullAndEmpty(username)) {
				sql.append(" HAVING u.phone = '"+DESEncrypt.jiaMiUsername(username)+"'");
			}
			
			sql.append(" ORDER BY inMoney DESC,u.insert_time ASC");
			
			StringBuffer sqlCount = new StringBuffer();
			sqlCount.append("SELECT COUNT(*) FROM (");
			sqlCount.append(sql);
			sqlCount.append(" )t");
			
			pageUtil = dao.getBySqlAndSqlCount(pageUtil, sql.toString(), sqlCount.toString(), params.toArray());
			
			return pageUtil;
					
			
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		
		return null;
	}
  

	/**
	 * 分页获取 绑卡未投资用户信息表
	 * @param pageUtil
	 * @param insertTime 注册时间
	 * @param username 用户名
	 * @return
	 */
	public PageUtil<Object[]> tiedCardInvestmentUsersList(PageUtil<Object[]> pageUtil,String insertTime,String username){
		
		try{
			StringBuffer sql = new StringBuffer();
			
			ArrayList<Object> params = new ArrayList<Object>();
			
			sql.append(" SELECT u.id, u.phone,(SELECT real_name FROM users_info WHERE u.user_info_id = users_info.id), ");
			sql.append(" (SELECT sex FROM users_info WHERE u.user_info_id = users_info.id),  ");
			sql.append(" (SELECT age FROM users_info WHERE u.user_info_id = users_info.id), ");
//			sql.append(" (SELECT birthday FROM users_info WHERE u.user_info_id = users_info.id), u.province,u.city,u.card_type, ");
			sql.append(" (SELECT is_bind_bank FROM users_info WHERE u.user_info_id = users_info.id),u.province,u.city,");
			sql.append(" u.insert_time,u.regist_channel ");
			sql.append(" FROM users u WHERE u.id NOT IN (SELECT users_id FROM investors GROUP BY users_id) ");
			//是否有指定时间段
			if(!QwyUtil.isNullAndEmpty(insertTime)){
				String[] times = QwyUtil.splitTime(insertTime);
				if(times.length > 1){
					sql.append(" AND u.insert_time >= ? ");
					params.add(QwyUtil.fmMMddyyyy.parse(times[0]));
					sql.append(" AND u.insert_time <= ? ");
					params.add(QwyUtil.fmMMddyyyyHHmmss.parse(times[1] + " 23:59:59" ));
				}else{
					sql.append(" AND u.insert_time >= ? ");
					params.add(QwyUtil.fmMMddyyyyHHmmss.parse(times[0]+" 00:00:00"));
					sql.append(" AND u.insert_time <= ? ");
					params.add(QwyUtil.fmMMddyyyyHHmmss.parse(times[0]+ " 23:59:59"));
				}
				
			}
			
			//是否为关键字搜索
			if(!QwyUtil.isNullAndEmpty(username)){
				sql.append(" HAVING phone = '" + DESEncrypt.jiaMiUsername(username) + "' ");
				
			}
			
			//排序
			//sql.append(" ORDER BY u.insert_time ");
			//数据总数查询
			StringBuffer sqlCount = new StringBuffer();
			sqlCount.append(" SELECT COUNT(*) FROM ( " + sql + " )m ");
			
			pageUtil = dao.getBySqlAndSqlCount(pageUtil, sql.toString(), sqlCount.toString(), params.toArray());
			
			return pageUtil;
			
		}catch (Exception e){
			log.error("操作异常: ",e);
		}
		
		return null;
	}
	
	
	/**
	 * 活跃度低用户
	 * @param pageUtil
	 * @param insertTime
	 * @return
	 */
	public PageUtil<Object[]> getVitalityUsers(PageUtil<Object[]> pageUtil,String insertTime,String username){
		try {
			StringBuffer sql = new StringBuffer();
			List<Object> params = new ArrayList<Object>();
			sql.append(" SELECT * FROM (");
			sql.append(" SELECT ui.users_id,ui.phone,ui.real_name,ui.sex,ui.age,");
			sql.append(" (SELECT province FROM users WHERE id = ui.users_id)province,");
			sql.append(" (SELECT city FROM users WHERE id = ui.users_id)city,");
			sql.append(" (SELECT regist_channel FROM users WHERE id = ui.users_id)regist_channel,");
			sql.append(" inv.pay_time,(SELECT title FROM product WHERE id = inv.product_id)title,copies,investor_status");
			sql.append(" FROM users_info ui LEFT JOIN investors inv ON ui.users_id = inv.users_id");
			sql.append(" WHERE 1=1");
			sql.append(" AND ui.is_bind_bank = '1'");
//			sql.append(" AND inv.investor_status IN ('1','2','3')");
			sql.append(" and ui.users_id NOT IN (SELECT users_id FROM investors WHERE 1=1 AND investor_status IN ('1','2','3') ");
			if (!QwyUtil.isNullAndEmpty(insertTime)) {
//				String[] times = QwyUtil.splitTime(insertTime);
//				if(times.length > 1){
//					sql.append(" AND pay_time >= ? ");
//					params.add(QwyUtil.fmMMddyyyy.parse(times[0]));
//					sql.append(" AND pay_time <= ? ");
//					params.add(QwyUtil.fmMMddyyyyHHmmss.parse(times[1] + " 23:59:59" ));
//				}else{
//					sql.append(" AND pay_time >= ? ");
//					params.add(QwyUtil.fmMMddyyyyHHmmss.parse(times[0]+" 00:00:00"));
//					sql.append(" AND pay_time <= ? ");
//					params.add(QwyUtil.fmMMddyyyyHHmmss.parse(times[0]+ " 23:59:59"));
//				}
				sql.append(" AND pay_time >= ?");
				params.add(QwyUtil.fmMMddyyyyHHmmss.parse(insertTime+ " 23:59:59"));
			}
			sql.append(" GROUP BY users_id)");
			sql.append(" ORDER BY pay_time DESC )a");
			sql.append(" WHERE 1=1 AND investor_status IN ('1','2','3') ");
			if (!QwyUtil.isNullAndEmpty(insertTime)) {
				sql.append(" AND pay_time >= ?");
				sql.append(" AND pay_time <= ?");
				params.add(QwyUtil.fmMMddyyyyHHmmss.parse(insertTime+ " 00:00:00"));
				params.add(QwyUtil.fmMMddyyyyHHmmss.parse(insertTime+ " 23:59:59"));
			}
			
			if (!QwyUtil.isNullAndEmpty(username)) {
				sql.append(" AND a.phone = '"+DESEncrypt.jiaMiUsername(username)+"'");
			}
			sql.append(" GROUP BY a.users_id ORDER BY a.pay_time DESC");
			
			StringBuffer sqlCount = new StringBuffer();
			sqlCount.append("SELECT COUNT(*) FROM (");
			sqlCount.append(sql);
			sqlCount.append(" )t");
			
			pageUtil = dao.getBySqlAndSqlCount(pageUtil, sql.toString(), sqlCount.toString(), params.toArray());
			if (!QwyUtil.isNullAndEmpty(pageUtil)) {
				return pageUtil;
			}
			
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return null;
	}
	
}
