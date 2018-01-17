package com.huoq.thread.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.common.bean.PlatformBean;
import com.huoq.common.dao.MyWalletDAO;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.Platform;
import com.huoq.thread.dao.ThreadDAO;

/**
 * 更新平台资金存量bean
 * 
 * @author Administrator
 *
 */
@Service
public class SaveIndexTableThreadBean {
	private static Logger log = Logger.getLogger(UpdateQdtjPlatformThreadBean.class);
	@Resource
	private ThreadDAO dao;
	@Resource
	private MyWalletDAO mwdao;

	/**
	 * 保存昨天的数据
	 */
	public void saveIndexTbale(String insertTime) {
		Platform plat = new Platform();
		plat.setInsertTime(new Date());
		// 获取昨日购买人数()
		Integer BuyNumber = findTodayBuyNumber(insertTime);
		plat.setTodayBuyNumber(BuyNumber);
		//昨日购买金额()
		Double findTodayBuyMoney = findTodayBuyMoney(insertTime);
		plat.setTodayBuyMoney(findTodayBuyMoney);
		// 获取昨日绑卡人数()
		Integer certificationCount = findTodaycertificationCount(insertTime);
		plat.setTodaycertificationCount(certificationCount);
		//获取昨日注册人数()
		Integer findregisterCount = findRegisterCount(insertTime);
		plat.setTodayregisterCount(findregisterCount);
		//更新平台总认证人数()
		Integer findCertificationCount = findCertificationCount(insertTime);
		plat.setCertificationCount(findCertificationCount);
		//更新平台总注册人数()
		Integer findAllRegisterCount = findAllRegisterCount(insertTime);
		plat.setRegisterCount(findAllRegisterCount);
		//累计充值总金额()
		Double findRechargeMoney = findRechargeMoney(insertTime);
		plat.setRechargeMoney(findRechargeMoney);
		//获取昨日充值金额()
		Double findTodayrechargeMoney = findTodayrechargeMoney(insertTime);
		plat.setTodayrechargeMoney(findTodayrechargeMoney);
		//累计提现金额()
		Double findAllOutCashMoney = findAllOutCashMoney(insertTime);
		plat.setAllOutCashMoney(findAllOutCashMoney);
		//昨日提现金额()
		Double findTodayOutCashMoney = findTodayOutCashMoney(insertTime);
		plat.setTodayOutCashMoney(findTodayOutCashMoney);
		//获取平台昨日存量增量()
		Double findTodayCapitalStock = findTodayCapitalStock(insertTime);
		plat.setTodayCapitalStock(findTodayCapitalStock);
		//获取平台资金存量()
		Double findAllCapitalStock = findAllCapitalStock();
		plat.setAllCapitalStock(findAllCapitalStock);
		String save = dao.save(plat);
		System.out.println(save);
	}

	/**
	 * 昨日购买人数
	 */
	public Integer findTodayBuyNumber(String insertTime) {
		try {
			List<Object> list = new ArrayList<Object>();
			list.add(insertTime);
			list.add(insertTime);
			list.add(insertTime);
			list.add(insertTime);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT SUM(1) FROM ( " + "SELECT SUM(t.number) t FROM ( "
					+ "SELECT COUNT(i.id) number,i.users_id userid FROM investors i WHERE i.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') AND i.investor_status IN('1','2','3') GROUP BY users_id "
					+ "UNION ALL "
					+ "SELECT COUNT(cpfr.id) number,cpfr.users_id userid   FROM coin_purse_funds_record  cpfr WHERE cpfr.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') AND cpfr.type='to' GROUP BY users_id)t GROUP BY t.userid)a ");
			List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
			Integer todayBuyNumber = 0;
			if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
				todayBuyNumber = Integer.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
			}
			return todayBuyNumber;
		} catch (Exception e) {
			log.error("操作异常: ", e);
		}
		return null;
	}
	/**
	 * 昨日购买金额
	 */
	public Double findTodayBuyMoney(String insertTime) {
		try {
			List<Object> list = new ArrayList<Object>();
			list.add(insertTime);
			list.add(insertTime);
			list.add(insertTime);
			list.add(insertTime);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT SUM(t.money) FROM ( "
					+ "SELECT SUM(i.in_money)/100 money FROM investors i WHERE i.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') AND i.investor_status IN('1','2','3') "
					+ "UNION ALL "
					+ "SELECT SUM(cpfr.money)/100 money  FROM coin_purse_funds_record  cpfr WHERE cpfr.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') AND cpfr.type='to')t ");
			List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
			Double todayBuyMoney = 0.0;
			if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
				todayBuyMoney = Double.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
			}
			return todayBuyMoney;
		} catch (Exception e) {
			log.error("操作异常: ", e);
		}
		return null;
	}

	/**
	 * 更新当日认证人数
	 */
	public Integer findTodaycertificationCount(String insertTime) {
		try {    
			List<Object> list = new ArrayList<Object>();
			list.add(insertTime);
			list.add(insertTime);
			StringBuffer sql = new StringBuffer();
			sql.append(
					"SELECT COUNT(1) FROM account ac WHERE ac.insert_time  BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59') ");
			List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
			Integer registerCount = 0;
			if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0)) && loadAllSql != null) {
				registerCount = Integer.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
			}
			return registerCount;
		} catch (Exception e) {
			log.error("操作异常: ", e);
		}
		return null;
	}

	/**
	 * 更新今日注册人数
	 */
	public Integer findRegisterCount(String insertTime) {
		try {
			List<Object> list = new ArrayList<Object>();
			list.add(insertTime);
			list.add(insertTime);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT COUNT(1) FROM users u WHERE u.insert_time BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59') ");
			List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
			Integer registerCount = 0;
			if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
				registerCount = Integer.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
			}
			return registerCount;
		} catch (Exception e) {
			log.error("操作异常: ", e);
		}
		return null;
	}

	/**
	 * 更新平台总认证人数
	 */
	public Integer findCertificationCount(String insertTime) {
		List<Object> list = new ArrayList<Object>();
		list.add(insertTime);
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT COUNT(1) FROM account ac WHERE ac.STATUS = 1 ");
			sql.append("AND ac.insert_time < DATE_FORMAT(?, '%Y-%m-%d 23:59:59')");
			List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
			Integer registerCount = 0;
			if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
				registerCount = Integer.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
			}
			return registerCount;
		} catch (Exception e) {
			log.error("操作异常: ", e);
		}
		return null;
	}

	/**
	 * 更新平台的注册人数;<br>
	 */
	public Integer findAllRegisterCount(String insertTime) {
		List<Object> list = new ArrayList<Object>();
		list.add(insertTime);
		try {
			StringBuffer sql = new StringBuffer("SELECT SUM(1) FROM users u ");
			sql.append("WHERE u.insert_time < DATE_FORMAT(?, '%Y-%m-%d 23:59:59') ");
			String loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray()).get(0).toString();
			Integer parseInt = 0;
			if (!QwyUtil.isNullAndEmpty(loadAllSql)) {
				parseInt = Integer.valueOf(loadAllSql.replaceAll(",", ""));
			}
			return parseInt;
		} catch (Exception e) {
			log.error("操作异常: ", e);
		}
		return null;
	}

	/**
	 * 平台充值总金额
	 */
	public Double findRechargeMoney(String insertTime) {
		try {
			List<Object> list = new ArrayList<Object>();
			list.add(insertTime);
			StringBuffer sql = new StringBuffer();
			sql.append(
					"SELECT FORMAT(SUM(cz.money)/100,0) money FROM cz_record cz WHERE cz.STATUS = '1' AND TYPE = '1' ");
			sql.append("AND cz.insert_time <  DATE_FORMAT(?,'%Y-%m-%d 23:59:59')");
			List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
			Double todayrechargeMoney = 0.0;
			if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
				todayrechargeMoney = Double.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
			}
			return todayrechargeMoney;
		} catch (Exception e) {
			log.error("操作异常: ", e);
		}
		return null;
	}

	/**
	 * 更新今日提现金额
	 */
	public Double findTodayOutCashMoney(String insertTime) {
		try {
			List<Object> list = new ArrayList<Object>();
			list.add(insertTime);
			list.add(insertTime);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT FORMAT(SUM(tr.money/100),2) FROM tx_record tr "
					+ "WHERE tr.is_check = '1' AND tr.check_time "
					+ "BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59') ");
			List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
			Double allMoney = 0.0;
			if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
				allMoney = Double.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
			}
			return allMoney;
		} catch (Exception e) {
			log.error("操作异常: ", e);
		}
		return null;
	}

	/**
	 * 更新累计提现金额
	 */
	public Double findAllOutCashMoney(String insertTime) {
		List<Object> list = new ArrayList<Object>();
		list.add(insertTime);
		try {
			StringBuffer sql = new StringBuffer();
			sql.append(
					"SELECT FORMAT(SUM(tr.money/100),2) FROM tx_record tr WHERE tr.is_check = '1' AND tr.check_time ");
			sql.append("< DATE_FORMAT(?, '%Y-%m-%d 23:59:59') ");
			List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
			Double allMoney = 0.0;
			if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
				allMoney = Double.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
			}
			return allMoney;
		} catch (Exception e) {
			log.error("操作异常: ", e);
		}
		return null;
	}

	/**
	 * 今日平台增量
	 */
	public Double findTodayCapitalStock(String insertTime) {
		try {
			List<Object> list = new ArrayList<Object>();
			if (!QwyUtil.isNullAndEmpty(insertTime)) {
				list.add(insertTime);
				list.add(insertTime);
				list.add(insertTime);
				list.add(insertTime);
			} else {
				Date date = new Date();
				SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
				String time = sd.format(date);
				list.add(time);
				list.add(time);
				list.add(time);
				list.add(time);
			}
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT FORMAT(SUM(money),2) FROM(  ");
			sql.append(
					"SELECT SUM(cz.money)/100 money FROM cz_record cz WHERE cz.STATUS = '1' AND TYPE = '1' AND cz.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') ");
			sql.append("UNION ALL ");
			sql.append(
					"SELECT SUM(-tr.money/100) money FROM tx_record tr WHERE tr.is_check = '1' AND tr.check_time BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59'))t ");
			List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
			Double todayCapitalStock = 0.0;
			if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
				todayCapitalStock = Double.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
			}
			return todayCapitalStock;
		} catch (Exception e) {
			log.error("操作异常: ", e);
		}
		return null;
	}

	/**
	 * 今日充值金额
	 */
	public Double findTodayrechargeMoney(String insertTime) {
		try {
			List<Object> list = new ArrayList<Object>();
			if (!QwyUtil.isNullAndEmpty(insertTime)) {
				list.add(insertTime);
				list.add(insertTime);
			} else {
				Date date = new Date();
				SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
				String time = sd.format(date);
				list.add(time);
				list.add(time);
			}
			StringBuffer sql = new StringBuffer();
			sql.append(
					"SELECT FORMAT(SUM(cz.money)/100,0) money FROM cz_record cz WHERE cz.STATUS = '1' AND TYPE = '1' ");
			sql.append(
					"AND cz.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59')");
			List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
			Double todayrechargeMoney = 0.0;
			if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
				todayrechargeMoney = Double.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
			}
			return todayrechargeMoney;
		} catch (Exception e) {
			log.error("操作异常: ", e);
		}
		return null;
	}
	
	/**
	 * 获取平台资金存量
	 */
	public Double findAllCapitalStock() {
		try {
			Platform plat = (Platform) dao.findById(new Platform(), 1L);
			Double allCapitalStock = plat.getAllCapitalStock();
			return allCapitalStock;
		} catch (Exception e) {
			log.error(e);
		}
		return null;
	}

}
