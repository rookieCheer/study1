package com.huoq.common.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.huoq.admin.product.bean.RechargeBean;
import com.huoq.common.dao.SumOperationDao;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.SumOperation;

@Service
public class SumOperationBean {
	@Resource
	private SumOperationDao sOtDao;
	@Resource
	private RechargeBean bean;

	@SuppressWarnings({ "rawtypes", "unused" })
	public SumOperation findSumOperation(String insertTime) throws Exception {
		SumOperation sumOperation = new SumOperation();
		// 日期
		List<Object> list = new ArrayList<Object>();
		if (!QwyUtil.isNullAndEmpty(insertTime)) {
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sd1 = new SimpleDateFormat("yyyyMMdd");
			String[] time = QwyUtil.splitTime(insertTime);
			Date parse = QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59");
			sumOperation.setTodayDate(sd1.format(parse));
			list.add(sd.format(parse));
		} else {
			Date date = new Date();
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sd1 = new SimpleDateFormat("yyyyMMdd");
			sumOperation.setTodayDate(sd1.format(date));
			String zdate1 = sd.format(date);
			list.add(zdate1);
		}
		// 累计绑卡人数
		StringBuffer sql5 = new StringBuffer();
		sql5.append("SELECT COUNT(1) FROM account ac WHERE ac.STATUS = 1 AND "
				+ "ac.insert_time < DATE_FORMAT(?, '%Y-%m-%d 23:59:59')");
		List loadAllSql5 = sOtDao.LoadAllSql(sql5.toString(), list.toArray());
		Object object5 = loadAllSql5.get(0);
		if (!QwyUtil.isNullAndEmpty(object5)) {
			String allallRigist = object5.toString().replaceAll(",", "");
			sumOperation.setAllallRigist(allallRigist);
		}
		// 累计注册人数
		StringBuffer sql3 = new StringBuffer();
		sql3.append("SELECT COUNT(1) FROM users u WHERE u.insert_time < DATE_FORMAT(?, '%Y-%m-%d 23:59:59')");
		List loadAllSql3 = sOtDao.LoadAllSql(sql3.toString(), list.toArray());
		Object object3 = loadAllSql3.get(0);
		if (!QwyUtil.isNullAndEmpty(object3)) {
			String allRigist = object3.toString();
			sumOperation.setAllRigist(allRigist);
		}
		// 定期标发息
		StringBuffer sql9 = new StringBuffer();
		sql9.append("SELECT FORMAT(SUM(pay_interest)/100,2)  FROM interest_details "
				+ "WHERE DATE_FORMAT(return_time,'%Y-%m-%d')=?");
		List loadAllSql9 = sOtDao.LoadAllSql(sql9.toString(), list.toArray());
		Object object9 = loadAllSql9.get(0);
		if (!QwyUtil.isNullAndEmpty(object9)) {
			Double regularbiaofaxi = Double.valueOf(object9.toString().replaceAll(",", ""));
			sumOperation.setRegularbiaofaxi(regularbiaofaxi);
		}
		// 零钱罐发息
		StringBuffer sql8 = new StringBuffer();
		sql8.append(
				"SELECT FORMAT(SUM(pay_interest/100),2) FROM send_rates WHERE DATE_FORMAT(insert_time,'%Y-%m-%d') = ? ");
		List loadAllSql8 = sOtDao.LoadAllSql(sql8.toString(), list.toArray());
		Object object8 = loadAllSql8.get(0);
		if (!QwyUtil.isNullAndEmpty(object8)) {
			Double lingqianfaxi = Double.valueOf(object8.toString().replaceAll(",", ""));
			sumOperation.setLingqianfaxi(lingqianfaxi);
		}
		// 累计资金流入
		StringBuffer sql7 = new StringBuffer();
		sql7.append("SELECT FORMAT(SUM(cz.money/100),0) money FROM cz_record cz WHERE cz.STATUS = '1' AND TYPE = '1' "
				+ "AND cz.insert_time <  DATE_FORMAT(?, '%Y-%m-%d 00:00:00')");
		List loadAllSql7 = sOtDao.LoadAllSql(sql7.toString(), list.toArray());
		Object object7 = loadAllSql7.get(0);
		if (!QwyUtil.isNullAndEmpty(object7)) {
			sumOperation.setAllMoneyinflowA(object7.toString().replaceAll(",", ""));
		}
		if (!QwyUtil.isNullAndEmpty(insertTime)) {
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
			String[] time = QwyUtil.splitTime(insertTime);
			Date parse = QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59");
			list.add(sd.format(parse));
			list.add(sd.format(parse));
			list.add(sd.format(parse));
		} else {
			Date date = new Date();
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
			String zdate1 = sd.format(date);
			list.add(zdate1);
			list.add(zdate1);
			list.add(zdate1);
		}
		// 成本合计
		StringBuffer sq25 = new StringBuffer();
		sq25.append("SELECT FORMAT(SUM(t.money),2) FROM ( "
				+ "(SELECT SUM(pay_interest)/100 money FROM send_rates WHERE  DATE_FORMAT(insert_time,'%Y-%m-%d')=?) "// 零钱罐发息
				+ "UNION "
				+ "(SELECT SUM(pay_interest)/100 money  FROM interest_details WHERE DATE_FORMAT(return_time,'%Y-%m-%d')=?) "// 定期标发息
				+ "UNION "
				+ "(SELECT SUM(cz.money)/100  money FROM cz_record cz WHERE cz.STATUS = '1' AND TYPE = '0' AND cz.insert_time BETWEEN  "
				+ "DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59')))t");
		List loadAllSq25 = sOtDao.LoadAllSql(sq25.toString(), list.toArray());
		Object object25 = loadAllSq25.get(0);
		if (!QwyUtil.isNullAndEmpty(object25)) {
			sumOperation.setAllcost(Double.valueOf(object25.toString().replaceAll(",", "")));
		}
		if (!QwyUtil.isNullAndEmpty(insertTime)) {
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
			String[] time = QwyUtil.splitTime(insertTime);
			Date parse = QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59");
			Date newtime = QwyUtil.addDaysFromOldDate(parse, -1).getTime();
			list.add(sd.format(newtime));
			list.add(sd.format(newtime));
		} else {
			Date date = new Date();
			Date time = QwyUtil.addDaysFromOldDate(date, -1).getTime();
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
			String zdate1 = sd.format(time);
			list.add(zdate1);
			list.add(zdate1);
		}

		// 平台预留资金
		StringBuffer sql1 = new StringBuffer();
		sql1.append("SELECT FORMAT(SUM(t.money),2) money FROM ( "
				+ "(SELECT SUM(cp.money/100) money FROM coin_purse_funds_record cp "
				+ "WHERE TYPE = 'shouyi' AND cp.insert_time < DATE_FORMAT(?, '%Y-%m-%d 00:00:00')) UNION " 
				+ "(SELECT SUM(cp.money/100) money FROM coin_purse_funds_record cp "
				+ " WHERE TYPE = 'to' AND cp.insert_time < DATE_FORMAT(?, '%Y-%m-%d 00:00:00')) UNION  "
				+ "(SELECT SUM(-cp.money/100) money FROM coin_purse_funds_record cp "
				+ "WHERE TYPE = 'out' AND cp.insert_time < DATE_FORMAT(?, '%Y-%m-%d 00:00:00')) " //零钱罐的存量
				+ "UNION (SELECT SUM(ui.left_money)/100 money "
				+ "FROM users_info ui LEFT JOIN users u ON u.id = ui.users_id " //当前总余额
				+ "WHERE u.insert_time < DATE_FORMAT(?, '%Y-%m-%d 00:00:00')) "
				+ "UNION (SELECT SUM(i.clear_money/100) money  FROM investors i  " //已投资未结算的预期收益
				+ "WHERE i.investor_status != 0 AND i.clear_time "
				+ "BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59'))) t ");
		List loadAllSql1 = sOtDao.LoadAllSql(sql1.toString(), list.toArray());
		Object object1 = loadAllSql1.get(0);
		if (!QwyUtil.isNullAndEmpty(object1)) {
			Double platformreserveMoney = Double.valueOf(object1.toString().replaceAll(",", ""));
			sumOperation.setPlatformreserveMoney(platformreserveMoney);
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
		}
		// 提现交易(笔数)
		StringBuffer sq23 = new StringBuffer();
		sq23.append("SELECT COUNT(id) FROM tx_record tr WHERE STATUS IN ('1','3') AND tr.check_time BETWEEN "
				+ "DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59')");
		List loadAllSq23 = sOtDao.LoadAllSql(sq23.toString(), list.toArray());
		Object object23 = loadAllSq23.get(0);
		if (!QwyUtil.isNullAndEmpty(object23)) {
			sumOperation.setTxDeal(object23.toString().replaceAll(",", ""));
		} else {
			sumOperation.setTxDeal("0");
		}
		// 好友返利成本
		StringBuffer sq24 = new StringBuffer();
		sq24.append("SELECT SUM(cz.money)/100  money FROM cz_record cz WHERE cz.STATUS = '1' AND TYPE = '0' "
				+ "AND cz.insert_time BETWEEN "
				+ "DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59')");
		List loadAllSq24 = sOtDao.LoadAllSql(sq24.toString(), list.toArray());
		Object object24 = loadAllSq24.get(0);
		if (!QwyUtil.isNullAndEmpty(object24)) {
			sumOperation.setFriendreturnMoney(Double.valueOf(object24.toString().replaceAll(",", "")));
		} else {
			sumOperation.setFriendreturnMoney(0.0);
		}

		// 总资金流入
		StringBuffer sq21 = new StringBuffer();
		sq21.append("SELECT FORMAT(SUM(cz.money)/100,0) money FROM cz_record cz WHERE cz.STATUS = '1' AND TYPE = '1' "
				+ "AND cz.insert_time BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59')");
		List loadAllSq21 = sOtDao.LoadAllSql(sq21.toString(), list.toArray());
		Object object21 = loadAllSq21.get(0);
		if (!QwyUtil.isNullAndEmpty(object21)) {
			sumOperation.setAllMoneyinflow(object21.toString().replaceAll(",", ""));
		} else {
			sumOperation.setAllMoneyinflow("0");
		}
		// 平台绑卡人数
		StringBuffer sql2 = new StringBuffer();
		sql2.append("SELECT COUNT(1) FROM account ac WHERE ac.insert_time "
				+ "BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59') ");
		List loadAllSql2 = sOtDao.LoadAllSql(sql2.toString(), list.toArray());
		Object object2 = loadAllSql2.get(0);
		if (!QwyUtil.isNullAndEmpty(object2)) {
			String rigistpersonCount = object2.toString().replaceAll(",", "");
			sumOperation.setRigistpersonCount(rigistpersonCount);
		} else {
			sumOperation.setRigistpersonCount("0".toString());
		}
		// 注册人数
		StringBuffer sql4 = new StringBuffer();
		sql4.append("SELECT COUNT(1) FROM users u WHERE u.insert_time " + "BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') "
				+ "AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59') ");
		List loadAllSql4 = sOtDao.LoadAllSql(sql4.toString(), list.toArray());
		Object object4 = loadAllSql4.get(0);
		if (!QwyUtil.isNullAndEmpty(object4)) {
			String rigistpersonCount = object4.toString().replaceAll(",", "");
			sumOperation.setRigistpersonCount(rigistpersonCount);
		} else {
			sumOperation.setRigistpersonCount("0");
		}

		// 绑卡人数
		StringBuffer sql6 = new StringBuffer();
		sql6.append("SELECT COUNT(1) FROM account ac WHERE ac.STATUS = 1 " + "AND ac.insert_time BETWEEN "
				+ "DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59')");
		List loadAllSql6 = sOtDao.LoadAllSql(sql6.toString(), list.toArray());
		Object object6 = loadAllSql6.get(0);
		if (!QwyUtil.isNullAndEmpty(object6)) {
			String tieCard = object6.toString().replaceAll(",", "");
			sumOperation.setTieCard(tieCard);
		} else {
			sumOperation.setTieCard("0");
		}

		// 每日提现金额
		StringBuffer sql10 = new StringBuffer();
		sql10.append("SELECT FORMAT(SUM(tr.money/100),2) FROM tx_record tr "
				+ "WHERE tr.is_check = '1' AND tr.check_time "
				+ "BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59') ");
		List loadAllSql10 = sOtDao.LoadAllSql(sql10.toString(), list.toArray());
		Object object10 = loadAllSql10.get(0);
		if (!QwyUtil.isNullAndEmpty(object10)) {
			Double txMoney = Double.valueOf(object10.toString().replaceAll(",", ""));
			sumOperation.setTxMoney(txMoney);
		} else {
			sumOperation.setTxMoney(0.0);
		}

		// 平台资金存量
		StringBuffer sql11 = new StringBuffer();
		sql11.append("SELECT FORMAT(SUM(t.money),2) FROM ((SELECT SUM(cz.money)/100  money "
				+ "FROM cz_record cz WHERE cz.STATUS = '1' AND TYPE = '1' "
				+ "AND cz.insert_time <  DATE_FORMAT(?, '%Y-%m-%d 00:00:00')) "
				+ "UNION(SELECT SUM(-tr.money/100) money FROM tx_record tr "
				+ "WHERE tr.insert_time < DATE_FORMAT(?, '%Y-%m-%d 00:00:00')))t ");
		List loadAllSql11 = sOtDao.LoadAllSql(sql11.toString(), list.toArray());
		Object object11 = loadAllSql11.get(0);
		if (!QwyUtil.isNullAndEmpty(object11)) {
			sumOperation.setPlatformsaveMoney(object11.toString().replaceAll(",", ""));
		} else {
			sumOperation.setPlatformsaveMoney("0");
		}
		
		list.clear();
		if (!QwyUtil.isNullAndEmpty(insertTime)) {
			SimpleDateFormat sd = new SimpleDateFormat("yyyyMMdd");
			String[] time = QwyUtil.splitTime(insertTime);
			if (time.length > 1) {
				list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
				list.add(QwyUtil.fmMMddyyyy.parse(time[1]));
				list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
				list.add(QwyUtil.fmMMddyyyy.parse(time[1]));
			} else {
				list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
				list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
				list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
				list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
			}
		} else {
			Date date = new Date();
			SimpleDateFormat sd1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String zdate1 = sd1.format(date);
			list.add(zdate1);
			list.add(zdate1);
			list.add(zdate1);
			list.add(zdate1);
		}
		// 购买交易(笔数)
		StringBuffer sq22 = new StringBuffer();
		sq22.append("SELECT SUM(t.allmoney) FROM ((SELECT COUNT(1) allmoney  FROM investors i WHERE i.investor_status IN ('1','2','3') AND i.insert_time " + 
				"BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59')) " + 
				"UNION ALL " + 
				"(SELECT COUNT(1) allmoney FROM coin_purse_funds_record cpf WHERE cpf.type = 'to'  AND cpf.insert_time BETWEEN " + 
				"DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59')))t ");
		List loadAllSq22 = sOtDao.LoadAllSql(sq22.toString(), list.toArray());
		Object object22 = loadAllSq22.get(0);
		if (!QwyUtil.isNullAndEmpty(object22)) {
			sumOperation.setBuyDeal(object22.toString());
		} else {
			sumOperation.setBuyDeal("0");
		}
		
		return sumOperation;
	}
}
