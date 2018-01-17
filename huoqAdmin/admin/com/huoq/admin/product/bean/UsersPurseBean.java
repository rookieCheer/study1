package com.huoq.admin.product.bean;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.admin.Mcoin.dao.MeowIncomeDAO;
import com.huoq.admin.product.dao.UsersPurseDao;
import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.CoinPurseFundsRecord;
import com.huoq.orm.RollOut;
import com.huoq.orm.RollOutUsers;
import com.huoq.orm.ShiftTo;

import sun.print.resources.serviceui_pt_BR;

/**
 * 用户零钱包 bean
 * 
 * @author 覃文勇
 * @createTime 2015-9-22上午10:04:53
 */
@Service
public class UsersPurseBean {
	private static Logger log = Logger.getLogger(UsersPurseBean.class);
	@Resource
	private UsersPurseDao dao;
	/**
	 * 用户资金流水
	 * @param pageUtil
	 * @param username
	 * @param insertTime
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public PageUtil<CoinPurseFundsRecord> LoadCoinPurseFunds(PageUtil<CoinPurseFundsRecord> pageUtil,String username,String insertTime){
		try {
			ArrayList<Object> objects = new ArrayList<Object>();
			StringBuffer hql = new StringBuffer();
			hql.append(" FROM CoinPurseFundsRecord c WHERE 1=1 ");
			if(!QwyUtil.isNullAndEmpty(username)) {
				hql.append(" AND c.users.username= ? ");
				objects.add(DESEncrypt.jiaMiUsername(username));
			}
			// if(!"all".equals(status)&&!QwyUtil.isNullAndEmpty(status)){
			// hql.append(" AND c.status= ? ");
			// objects.add(status);
			// }
			// 插入时间
			if (!QwyUtil.isNullAndEmpty(insertTime)) {
				String[] time = QwyUtil.splitTime(insertTime);
				if (time.length > 1) {
					hql.append(" AND c.insertTime >= ? ");
					objects.add(QwyUtil.fmMMddyyyy.parse(time[0]));
					hql.append(" AND c.insertTime <= ? ");
					objects.add(QwyUtil.fmMMddyyyy.parse(time[1]));
				} else {
					hql.append(" AND c.insertTime >= ? ");
					objects.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
					hql.append(" AND c.insertTime <= ? ");
					objects.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
				}
			}
			hql.append(" ORDER BY c.insertTime DESC,c.usersId DESC ");
			return (PageUtil<CoinPurseFundsRecord>) dao.getPage(pageUtil, hql.toString(), objects.toArray());
		} catch (Exception e) {
			log.error("操作异常: ", e);
		}
		return null;
	}

	/**
	 * 用户零钱包转入列表
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public PageUtil<ShiftTo> LoadShiftTo(PageUtil<ShiftTo> pageUtil, String username, String insertTime) {
		try {
			ArrayList<Object> objects = new ArrayList<Object>();
			StringBuffer hql = new StringBuffer();
			hql.append(" FROM ShiftTo s WHERE 1=1 ");
			if (!QwyUtil.isNullAndEmpty(username)) {
				hql.append(" AND s.users.username= ? ");
				objects.add(DESEncrypt.jiaMiUsername(username));
			}
			// if(!"all".equals(status)&&!QwyUtil.isNullAndEmpty(status)){
			// hql.append(" AND s.status= ? ");
			// objects.add(status);
			// }
			// 插入时间
			if (!QwyUtil.isNullAndEmpty(insertTime)) {
				String[] time = QwyUtil.splitTime(insertTime);
				if (time.length > 1) {
					hql.append(" AND s.insertTime >= ? ");
					objects.add(QwyUtil.fmMMddyyyy.parse(time[0]));
					hql.append(" AND s.insertTime <= ? ");
					objects.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
				} else {
					hql.append(" AND s.insertTime >= ? ");
					objects.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
					hql.append(" AND s.insertTime <= ? ");
					objects.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
				}
			}
			hql.append(" ORDER BY s.insertTime DESC ");
			return (PageUtil<ShiftTo>) dao.getPage(pageUtil, hql.toString(), objects.toArray());
		} catch (Exception e) {
			log.error("操作异常: ", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public PageUtil<RollOutUsers> loadUsersCoinPurse(PageUtil pageUtil, String username) throws ParseException {
		try {
			ArrayList<Object> objects = new ArrayList<Object>();
			StringBuffer hql = new StringBuffer();
			hql.append(
					"select u.`username` as username , c.in_money , c.insert_time as insert_time from   coin_purse c left join users u  on c.users_id = u.id");
			hql.append(" where 1=1 ");
			if (!QwyUtil.isNullAndEmpty(username)) {
				hql.append(" AND u.username= ? ");
				objects.add(DESEncrypt.jiaMiUsername(username));
			}
			hql.append(" ORDER BY c.insert_time DESC ");

			StringBuffer bufferCount = new StringBuffer();
			bufferCount.append(" SELECT COUNT(*)  ");
			bufferCount.append(" FROM (");
			bufferCount.append(hql);
			bufferCount.append(") t");
			// buff.append("ORDER BY fr.insert_time DESC ");
			pageUtil = dao.getBySqlAndSqlCount(pageUtil, hql.toString(), bufferCount.toString(), objects.toArray());

			List<RollOutUsers> platUsers = toRollOutUsers(pageUtil.getList());
			pageUtil.setList(platUsers);
			return pageUtil;
		} catch (Exception e) {
			log.error("操作异常: ", e);
		}
		return null;
	}

	/**
	 * 将数据转换为toRollOutUsers
	 */
	private List<RollOutUsers> toRollOutUsers(List<Object[]> list) throws ParseException {
		List<RollOutUsers> meowIncome = new ArrayList<RollOutUsers>();
		if (!QwyUtil.isNullAndEmpty(list)) {
			for (Object[] object : list) {
				RollOutUsers plat = new RollOutUsers();
				plat.setUsersname(object[0] == null ? "" : object[0] + "");
				plat.setLeftMoney(object[1] == null ? "" : object[1] + "");
				plat.setInsertTime(QwyUtil.fmyyyyMMddHHmmss.parse(QwyUtil.fmyyyyMMddHHmmss.format(object[2])));
				meowIncome.add(plat);
			}
		}
		return meowIncome;
	}

	/**
	 * 零钱包转出异常的记录
	 * 
	 * @param pageUtil
	 * @param username
	 * @param insertTime
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public PageUtil<RollOut> LoadRollOutException(PageUtil<RollOut> pageUtil, String username, String insertTime) {

		try {

			ArrayList<Object> objects = new ArrayList<Object>();

			StringBuffer buffer = new StringBuffer();
			buffer.append(
					" SELECT t.username ,rt.in_money ,rt.left_money ,rt.insert_time ,rt.`STATUS` ,rt.type FROM roll_out rt LEFT JOIN  ("
							+ "SELECT r.users_id AS userId, DATE_FORMAT(r.insert_time,'%Y-%m-%d') AS insertTime,COUNT(DATE_FORMAT(r.insert_time,'%Y-%m-%d'))"
							+ "AS count,us.username FROM roll_out r LEFT JOIN users us ON r.users_id = us.id WHERE 1 = 1 ");
			
			
			buffer.append(
					"GROUP BY DATE_FORMAT(r.insert_time,'%Y-%m-%d'),r.users_id HAVING COUNT(DATE_FORMAT(r.insert_time,'%Y-%m-%d'))>1"
							+ " ORDER BY r.insert_time DESC ) t ON rt.users_id = t.userId WHERE DATE_FORMAT(rt.insert_time,'%Y-%m-%d') ="
							+ "DATE_FORMAT(t.insertTime,'%Y-%m-%d')");
			
			if (!QwyUtil.isNullAndEmpty(username)) {
				buffer.append(" AND t.username= ? ");
				objects.add(DESEncrypt.jiaMiUsername(username));
			}

			if (!QwyUtil.isNullAndEmpty(insertTime)) {

				String[] time = QwyUtil.splitTime(insertTime);
				if (time.length > 1) {
					buffer.append(" AND rt.insert_time >= ? ");
					objects.add(QwyUtil.fmMMddyyyy.parse(time[0]));
					buffer.append(" AND rt.insert_time <= ? ");
					objects.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
				} else {
					buffer.append(" AND rt.insert_time >= ? ");
					objects.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
					buffer.append(" AND rt.insert_time <= ? ");
					objects.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
				}

			}
			
			buffer.append(" ORDER BY rt.insert_time DESC");
			
			StringBuffer bufferCount = new StringBuffer();
			bufferCount.append(" SELECT COUNT(*) FROM ( ");
			bufferCount.append(buffer);
			bufferCount.append(") ne");
			
			PageUtil<RollOut> page = dao.getBySqlAndSqlCount(pageUtil, buffer.toString(), bufferCount.toString(),objects.toArray());
			
			return page;
			
		} catch (Exception e) {
			log.error("操作异常: ", e);
		}

		return null;
	}

	/**
	 * 用户零钱包转出列表
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public PageUtil<RollOut> LoadRollOut(PageUtil<RollOut> pageUtil, String username, String insertTime) {
		try {
			ArrayList<Object> objects = new ArrayList<Object>();
			StringBuffer hql = new StringBuffer();
			hql.append(" FROM RollOut r WHERE 1=1 ");
			if (!QwyUtil.isNullAndEmpty(username)) {
				hql.append(" AND r.users.username= ? ");
				objects.add(DESEncrypt.jiaMiUsername(username));
			}
			// if(!"all".equals(status)&&!QwyUtil.isNullAndEmpty(status)){
			// hql.append(" AND s.status= ? ");
			// objects.add(status);
			// }
			// 插入时间
			if (!QwyUtil.isNullAndEmpty(insertTime)) {
				String[] time = QwyUtil.splitTime(insertTime);
				if (time.length > 1) {
					hql.append(" AND r.insertTime >= ? ");
					objects.add(QwyUtil.fmMMddyyyy.parse(time[0]));
					hql.append(" AND r.insertTime <= ? ");
					objects.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
				} else {
					hql.append(" AND r.insertTime >= ? ");
					objects.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
					hql.append(" AND r.insertTime <= ? ");
					objects.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
				}
			}
			hql.append(" ORDER BY r.insertTime DESC ");
			return (PageUtil<RollOut>) dao.getPage(pageUtil, hql.toString(), objects.toArray());
		} catch (Exception e) {
			log.error("操作异常: ", e);
		}
		return null;
	}

}
