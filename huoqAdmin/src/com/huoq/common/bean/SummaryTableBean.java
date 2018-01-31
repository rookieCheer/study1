package com.huoq.common.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.huoq.common.dao.SummaryTableDao;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.SummaryTable;

@Service
public class SummaryTableBean {
	@Resource
	private SummaryTableDao steDao;

	@SuppressWarnings({ "rawtypes", "unused" })
	public SummaryTable findSummaryTable(String insertTime) throws ParseException {
		SummaryTable summarizeTable = new SummaryTable();
		List<Object> list = new ArrayList<Object>();
		if (!QwyUtil.isNullAndEmpty(insertTime)) {
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sd1 = new SimpleDateFormat("yyyyMMdd");
			String[] time = QwyUtil.splitTime(insertTime);
			Date parse = QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59");
			summarizeTable.setTadayDate(sd1.format(parse));
			list.add(sd.format(parse));
		} else {
			Date date = new Date();
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sd1 = new SimpleDateFormat("yyyyMMdd");
			summarizeTable.setTadayDate(sd1.format(date));
			String zdate1 = sd.format(date);
			list.add(zdate1);
		}
		// 累计注册用户
		StringBuffer sql8 = new StringBuffer();
		sql8.append("SELECT COUNT(1) FROM users u WHERE u.insert_time < DATE_FORMAT(?, '%Y-%m-%d 23:59:59')");
		List loadAllSql8 = steDao.LoadAllSql(sql8.toString(), list.toArray());
		Object object8 = loadAllSql8.get(0);
		if (!QwyUtil.isNullAndEmpty(object8)) {
			String allrigist = object8.toString().replaceAll(",", "");
			summarizeTable.setAllEnUser(allrigist);
		}
		// 累计绑定用户
		StringBuffer sql9 = new StringBuffer();
		sql9.append(
				"SELECT COUNT(1) FROM account ac WHERE ac.STATUS = 1 AND ac.insert_time < DATE_FORMAT(?, '%Y-%m-%d 23:59:59')");
		List loadAllSql9 = steDao.LoadAllSql(sql9.toString(), list.toArray());
		Object object9 = loadAllSql9.get(0);
		if (!QwyUtil.isNullAndEmpty(object9)) {
			String allCertification = object9.toString().replaceAll(",", "");
			summarizeTable.setAllAutUser(allCertification);
		} else {
			summarizeTable.setAllEnUser("0".toString());
		}
		// 累计资金流入
		StringBuffer sql18 = new StringBuffer();
		sql18.append(
				"SELECT FORMAT(SUM(cz.money)/100,0) money FROM cz_record cz WHERE cz.STATUS = '1' AND TYPE = '1' ");
		if (!QwyUtil.isNullAndEmpty(insertTime)) {
			sql18.append("AND cz.insert_time < DATE_FORMAT(?, '%Y-%m-%d 23:59:59')");
		}
		List loadAllSql20 = steDao.LoadAllSql(sql18.toString(),
				QwyUtil.isNullAndEmpty(insertTime) ? null : list.toArray());
		Object object20 = loadAllSql20.get(0);
		if (!QwyUtil.isNullAndEmpty(object20)) {
			String string = object20.toString().replaceAll(",", "");
			summarizeTable.setAllinMoney(string);
		} else {
			summarizeTable.setAllinMoney("");
		}
		list.clear();
		if (!QwyUtil.isNullAndEmpty(insertTime)) {
			SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
			String[] time = QwyUtil.splitTime(insertTime);
			if (time.length > 1) {
				list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
				list.add(QwyUtil.fmMMddyyyy.parse(time[1]));
			} else {
				list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
				list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
			}
		} else {
			Date date = new Date();
			SimpleDateFormat sd1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String zdate1 = sd1.format(date);
			list.add(zdate1);
			list.add(zdate1);
			// 当前时间
			SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
			String zdate = sd.format(date);
		}
		// 新增认证用户（绑卡）
		StringBuffer sql = new StringBuffer();
		sql.append(
				"SELECT COUNT(1) FROM account ac WHERE ac.insert_time BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59')");
		List loadAllSql = steDao.LoadAllSql(sql.toString(), list.toArray());
		Object object = loadAllSql.get(0);
		if (!QwyUtil.isNullAndEmpty(object)) {
			String tieCard = object.toString();
			summarizeTable.setNAutUser(tieCard);
		}
		StringBuffer sql1 = new StringBuffer();
		// 新增注册用户
		sql1.append(
				"SELECT COUNT(1) FROM users u WHERE u.insert_time BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59')");
		List loadAllSql1 = steDao.LoadAllSql(sql1.toString(), list.toArray());
		Object object1 = loadAllSql1.get(0);
		if (!QwyUtil.isNullAndEmpty(object1)) {
			String addregist = object1.toString().replaceAll(",", "");
			summarizeTable.setNEnrollUser(addregist);
		}
		StringBuffer sql2 = new StringBuffer();
		// 新增安卓用户
		sql2.append(
				"SELECT COUNT(u.id) d FROM users u WHERE u.regist_platform = 1 AND u.insert_time BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59')");
		List loadAllSql2 = steDao.LoadAllSql(sql2.toString(), list.toArray());
		Object object2 = loadAllSql2.get(0);
		if (!QwyUtil.isNullAndEmpty(object2)) {
			String addandroid = object2.toString().replaceAll(",", "");
			summarizeTable.setNEnrollAndroidUser(addandroid);
		}
		StringBuffer sql3 = new StringBuffer();
		// 新增ios用户
		sql3.append(
				"SELECT COUNT(u.id) i FROM users u WHERE u.regist_platform = 2 AND u.insert_time BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59')");
		List loadAllSql3 = steDao.LoadAllSql(sql3.toString(), list.toArray());
		Object object3 = loadAllSql3.get(0);
		if (!QwyUtil.isNullAndEmpty(object3)) {
			String addios = object3.toString().replaceAll(",", "");
			summarizeTable.setNEnrollIosUser(addios);
		}
		StringBuffer sql4 = new StringBuffer();
		// 新增微信用户
		sql4.append(
				"SELECT COUNT(u.id) i FROM users u WHERE u.regist_platform = 3 AND u.insert_time BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59')");
		List loadAllSql4 = steDao.LoadAllSql(sql4.toString(), list.toArray());
		Object object4 = loadAllSql4.get(0);
		if (!QwyUtil.isNullAndEmpty(object4)) {
			String addwechat = object4.toString().replaceAll(",", "");
			summarizeTable.setNEnrollWeChatUser(addwechat);
		}
		StringBuffer sql5 = new StringBuffer();
		// 新增安卓认证用户（绑卡）
		sql5.append(
				"SELECT COUNT(u.id) FROM account ac LEFT JOIN users u ON ac.users_id = u.id WHERE u.insert_time AND ac.insert_time BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59') AND u.regist_platform = 1");
		List loadAllSql5 = steDao.LoadAllSql(sql5.toString(), list.toArray());
		Object object5 = loadAllSql5.get(0);
		if (!QwyUtil.isNullAndEmpty(object5)) {
			String android = object5.toString().replaceAll(",", "");
			summarizeTable.setNAutAndroidUser(android);
		}
		StringBuffer sql6 = new StringBuffer();
		// 新增ios认证用户（绑卡）
		sql6.append(
				"SELECT COUNT(u.id) FROM account ac LEFT JOIN users u ON ac.users_id = u.id WHERE u.insert_time AND ac.insert_time BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59') AND u.regist_platform = 2");
		List loadAllSql6 = steDao.LoadAllSql(sql6.toString(), list.toArray());
		Object object6 = loadAllSql6.get(0);
		if (!QwyUtil.isNullAndEmpty(object6)) {
			String ios = object6.toString().replaceAll(",", "");
			summarizeTable.setNAutIosUser(ios);
		}
		// 新增微信认证用户（绑卡）
		StringBuffer sql7 = new StringBuffer();
		sql7.append(
				"SELECT COUNT(u.id) FROM account ac LEFT JOIN users u ON ac.users_id = u.id WHERE u.insert_time AND ac.insert_time BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59') AND u.regist_platform = 3");
		List loadAllSql7 = steDao.LoadAllSql(sql7.toString(), list.toArray());
		Object object7 = loadAllSql7.get(0);
		if (!QwyUtil.isNullAndEmpty(object7)) {
			String wechat = object7.toString().replaceAll(",", "");
			summarizeTable.setNAutWeChatUser(wechat);
		}

		// 当日购买新手标笔数(新用户)
		StringBuffer sql11 = new StringBuffer();
		sql11.append(
				"SELECT COUNT(1) FROM investors i LEFT JOIN product p ON p.id = i.product_id WHERE i.investor_status = '1' AND i.insert_time BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59') AND p.title LIKE '新华新手%' ");
		List loadAllSql11 = steDao.LoadAllSql(sql11.toString(), list.toArray());
		Object object11 = loadAllSql11.get(0);
		if (!QwyUtil.isNullAndEmpty(object11)) {
			String newUserSpart = object11.toString().replaceAll(",", "");
			summarizeTable.setNUnserDeal(newUserSpart);
		}

		// 当日资金流入
		StringBuffer sql13 = new StringBuffer();
		sql13.append(
				"SELECT SUM(cz.money)/100 money FROM cz_record cz WHERE cz.STATUS = '1' AND TYPE = '1' AND cz.insert_time "
						+ "BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59')  ");
		List loadAllSql13 = steDao.LoadAllSql(sql13.toString(), list.toArray());
		Object object13 = loadAllSql13.get(0);
		if (!QwyUtil.isNullAndEmpty(object13)) {
			Double todaymoneyInflow = Double.valueOf(object13.toString().replaceAll(",", ""));
			summarizeTable.setTodayincapital(todaymoneyInflow);
		} else {
			summarizeTable.setTodayincapital(0.0);
		}
		StringBuffer sql14 = new StringBuffer();
		// 当日首投总额
		sql14.append(
				"SELECT SUM(i.in_money/100) FROM investors i LEFT JOIN product p ON p.id = i.product_id WHERE i.investor_status = '1' AND i.insert_time BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59') AND p.title LIKE '新华新手%'");
		List loadAllSql14 = steDao.LoadAllSql(sql14.toString(), list.toArray());
		Object object14 = loadAllSql14.get(0);
		if (!QwyUtil.isNullAndEmpty(object14)) {
			Double firsttoutotalmoney = Double.valueOf(object14.toString().replaceAll(",", ""));
			summarizeTable.setNDealMoney(firsttoutotalmoney);
		} else {
			summarizeTable.setNDealMoney(0.0);
		}

		StringBuffer sql16 = new StringBuffer();
		// 零钱罐购买额度(活期产品部分)
		sql16.append(
				"SELECT FORMAT(SUM(cpf.money/100),2) FROM coin_purse_funds_record cpf WHERE cpf.type = 'to'  AND cpf.insert_time BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59') ");
		List loadAllSql16 = steDao.LoadAllSql(sql16.toString(), list.toArray());
		Object object16 = loadAllSql16.get(0);
		if (!QwyUtil.isNullAndEmpty(object16)) {
			Double huoqiproductpart = Double.valueOf(object16.toString().replaceAll(",", ""));
			summarizeTable.setCurrentProduct(huoqiproductpart);
		} else {
			summarizeTable.setCurrentProduct(0.0);
		}
		// 每日提现金额
		StringBuffer sql19 = new StringBuffer();
		/*
		"SELECT FORMAT(SUM(tr.money/100),2) FROM tx_record tr "
		+ "WHERE tr.is_check = '1' AND tr.check_time "
		+ "BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59')
*/		sql19.append(
				"SELECT FORMAT(SUM(tr.money/100),2) FROM tx_record tr WHERE STATUS IN ('1','3') AND tr.insert_time BETWEEN "
						+ "DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59')");
		List loadAllSql18 = steDao.LoadAllSql(sql19.toString(), list.toArray());
		Object object18 = loadAllSql18.get(0);
		if (!QwyUtil.isNullAndEmpty(object18)) {
			Double todaytxmoney = Double.valueOf(object18.toString().replaceAll(",", ""));
			summarizeTable.setTodayoutMoney(todaytxmoney);
		} else {
			summarizeTable.setTodayoutMoney(0.0);
		}

		// 当日可提现金额
		StringBuffer sql21 = new StringBuffer();
		sql21.append("SELECT FORMAT(SUM(t.money),2) money FROM ( "
				+ "(SELECT SUM(cp.money/100) money FROM coin_purse_funds_record cp WHERE TYPE = 'shouyi'  AND cp.insert_time < DATE_FORMAT(?, '%Y-%m-%d 00:00:00')) " 
				+ "UNION "
				+ "(SELECT SUM(cp.money/100) money FROM coin_purse_funds_record cp WHERE TYPE = 'to'  AND cp.insert_time < DATE_FORMAT(?, '%Y-%m-%d 00:00:00')) " 
				+ "UNION  "
				+ "(SELECT SUM(-cp.money/100) money FROM coin_purse_funds_record cp WHERE TYPE = 'out'  AND cp.insert_time < DATE_FORMAT(?, '%Y-%m-%d 00:00:00')) " 
				+ "UNION "
				+ "(SELECT SUM(u.left_money)/100 money FROM users_info u WHERE u.insert_time < DATE_FORMAT(?, '%Y-%m-%d 00:00:00')) "
				+ "UNION "
				+ "(SELECT SUM(i.clear_money/100) money  FROM investors i  WHERE i.investor_status = '3' AND i.insert_time BETWEEN "
				+ "DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59'))) t ");
		List<Object> list1 = new ArrayList<Object>();
		if (!QwyUtil.isNullAndEmpty(insertTime)) {
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
			String[] time = QwyUtil.splitTime(insertTime);
			Date parse = QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59");
			list1.add(sd.format(parse));
			list1.add(sd.format(parse));
			list1.add(sd.format(parse));
			list1.add(sd.format(parse));
			String yesterday = QwyUtil.fmyyyyMMdd.format(QwyUtil.addDaysFromOldDate(parse, -1).getTime());
			list1.add(yesterday);
			list1.add(yesterday);
		}else {
			list1.add(new Date());
			list1.add(new Date());
			list1.add(new Date());
			list1.add(new Date());
			String yesterday = QwyUtil.fmyyyyMMdd.format(QwyUtil.addDaysFromOldDate(new Date(), -1).getTime());
			list1.add(yesterday);
			list1.add(yesterday);
		}
		List loadAllSql21 = steDao.LoadAllSql(sql21.toString(), list1.toArray());
		Object object21 = loadAllSql21.get(0);
		if (!QwyUtil.isNullAndEmpty(object21)) {
			String todayCash = object21.toString().replaceAll(",", "");
			summarizeTable.setTodayCash(Double.valueOf(todayCash));
		} else {
			summarizeTable.setTodayCash(Double.valueOf(0.0));
		}
		// 定期产品部分
				StringBuffer sql17 = new StringBuffer();
				sql17.append(
						"SELECT SUM(i.in_money/100) money  FROM investors i WHERE i.investor_status IN ('1','2','3')  AND i.insert_time BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59')");
				List loadAllSql17 = steDao.LoadAllSql(sql17.toString(), list.toArray());
				Object object17 = loadAllSql17.get(0);
				if (!QwyUtil.isNullAndEmpty(object17)) {
					Double dingqiproductpart = Double.valueOf(object17.toString().replaceAll(",", ""));
					summarizeTable.setRegularProduct(dingqiproductpart);
				} else {
					summarizeTable.setRegularProduct(0.0);
				}
		// list添加条件
		SimpleDateFormat sd1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (!QwyUtil.isNullAndEmpty(insertTime)) {
			String[] time = QwyUtil.splitTime(insertTime);
			if (time.length > 1) {
				list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
				list.add(QwyUtil.fmMMddyyyy.parse(time[1]));
			} else {
				list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
				list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
			}
		} else {
			Date date = new Date();
			String zdate1 = sd1.format(date);
			list.add(zdate1);
			list.add(zdate1);
		}

		// 当日购买笔数
		StringBuffer sql10 = new StringBuffer();
		sql10.append(
				"SELECT SUM(t.allmoney) FROM ((SELECT COUNT(1) allmoney  FROM investors i WHERE i.investor_status IN ('1','2','3') AND i.insert_time "
						+ "BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59')) "
						+ "UNION ALL "
						+ "(SELECT COUNT(1) allmoney FROM coin_purse_funds_record cpf WHERE cpf.type = 'to'  AND cpf.insert_time BETWEEN "
						+ "DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59')))t ");
		List loadAllSql10 = steDao.LoadAllSql(sql10.toString(), list.toArray());
		Object object10 = loadAllSql10.get(0);
		if (!QwyUtil.isNullAndEmpty(object10)) {
			String todayBuyRecharge = object10.toString().replaceAll(",", "");
			summarizeTable.setTodayDeal(todayBuyRecharge);
		}

		// 当日购买其他标笔数(老用户)
		StringBuffer sql12 = new StringBuffer();
		sql12.append(
				"SELECT SUM(t.allmoney) FROM ((SELECT COUNT(1) allmoney FROM investors i LEFT JOIN product p ON p.id = i.product_id WHERE i.investor_status IN('1','2','3') AND i.insert_time "
						+ "BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59') AND p.title NOT LIKE '新华新手%') "
						+ "UNION ALL "
						+ "(SELECT COUNT(1) allmoney FROM coin_purse_funds_record cpf WHERE cpf.type = 'to'  AND cpf.insert_time BETWEEN "
						+ "DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59')))t ");
		List loadAllSql12 = steDao.LoadAllSql(sql12.toString(), list.toArray());
		Object object12 = loadAllSql12.get(0);
		if (!QwyUtil.isNullAndEmpty(object12)) {
			String oldUserSpart = object12.toString().replaceAll(",", "");
			summarizeTable.setOUserDeal(oldUserSpart);
		}
		
		// 当日复投总额
		StringBuffer sql15 = new StringBuffer();
		sql15.append(
				"SELECT SUM(t.money) FROM (SELECT SUM(i.in_money/100) money FROM investors i LEFT JOIN product p ON p.id = i.product_id WHERE i.investor_status IN ('1','2','3') AND i.insert_time "
						+ "BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59') AND p.title NOT LIKE '新华新手%' "
						+ "UNION ALL "
						+ "SELECT SUM(cpf.money/100) money FROM coin_purse_funds_record cpf WHERE cpf.type = 'to'  AND cpf.insert_time BETWEEN "
						+ "DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59'))t");
		List loadAllSql15 = steDao.LoadAllSql(sql15.toString(), list.toArray());
		Object object15 = loadAllSql15.get(0);
		if (!QwyUtil.isNullAndEmpty(object15)) {
			Double secondtoutotalmoney = Double.valueOf(object15.toString().replaceAll(",", ""));
			summarizeTable.setODealMoney(secondtoutotalmoney);
		} else {
			summarizeTable.setODealMoney(0.0);
		}
		return summarizeTable;
	}
}
