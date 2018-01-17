package com.huoq.common.bean;

import com.huoq.common.dao.SumOperationDao;
import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.ThirdTouTiaoCallBackUserInfo;
import com.huoq.orm.TouTiaoStatistics;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ToutiaoStatisticsTableBean {
	@Resource
	private SumOperationDao dao;
	/**
	 * 头条统计表
	 * @param insertTime
	 * @return
	 */
	public TouTiaoStatistics findToutiaoStatisticsTable(String insertTime) {
		TouTiaoStatistics touTiao = new TouTiaoStatistics();
		try {
			List<Object> list = new ArrayList<Object>();
			if (!QwyUtil.isNullAndEmpty(insertTime)) {
				SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
				String[] time = QwyUtil.splitTime(insertTime);
				Date parse = QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59");
				String format = sd.format(parse);
				touTiao.setDate(format);
				list.add(sd.format(parse));
				list.add(sd.format(parse));
			} else {
				Date date = new Date();
				SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
				String zdate1 = sd.format(date);
				touTiao.setDate(zdate1);
				list.add(zdate1);
				list.add(zdate1);
			}
			//获取当日广告点击量-------------------------------------------------------------------------------------------------------------------------------
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT COUNT(1) FROM `third_toutiao_userinfo` ttu WHERE ttu.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59')");
			List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
			if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
				touTiao.setTouTiaoClicks(loadAllSql.get(0)+"");
			}
			if (!QwyUtil.isNullAndEmpty(insertTime)) {
				SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
				String[] time = QwyUtil.splitTime(insertTime);
				Date parse = QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59");
				String format = sd.format(parse);
				touTiao.setDate(format);
				list.add(sd.format(parse));
				list.add(sd.format(parse));
				list.add(sd.format(parse));
				list.add(sd.format(parse));
				list.add(sd.format(parse));
				list.add(sd.format(parse));
			} else {
				Date date = new Date();
				SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
				String zdate1 = sd.format(date);
				touTiao.setDate(zdate1);
				list.add(zdate1);
				list.add(zdate1);
				list.add(zdate1);
				list.add(zdate1);
				list.add(zdate1);
				list.add(zdate1);
			}
			//获取当日激活量-------------------------------------------------------------------------------------------------------------------------------
			StringBuffer sql1 = new StringBuffer();
			
			sql1.append("SELECT SUM(t.a) FROM (SELECT COUNT(ttuc.`id`) a FROM `third_toutiao_userinfo_callback`  ttuc " + 
					"LEFT JOIN `third_toutiao_userinfo` ttu ON 1=1 " + 
					"WHERE ttuc.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') "+
					"AND  ttu.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') " + 
					"AND  ttu.idfa = ttuc.idfa  " + 
					"UNION " + 
					"SELECT COUNT(ttuc.`id`)a FROM `third_toutiao_userinfo_callback` ttuc  " + 
					"LEFT JOIN `third_toutiao_userinfo` ttu ON 1=1 " + 
					"WHERE ttuc.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') " + 
					"AND  ttu.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') "+
					"AND  ttu.imei = ttuc.imei ) t");
			List loadAllSql1 = dao.LoadAllSql(sql1.toString(), list.toArray());
			if (!QwyUtil.isNullAndEmpty(loadAllSql1.get(0))) {
				touTiao.setActivator(loadAllSql1.get(0)+"");
			}
			//当日注册人数-------------------------------------------------------------------------------------------------------------------------------
			StringBuffer sql2 = new StringBuffer();
			sql2.append("SELECT COUNT(1) FROM account ac " + 
					"LEFT JOIN users u ON u.id = ac.users_id " + 
					"LEFT JOIN ( " + 
					"SELECT username FROM  `third_toutiao_userinfo_callback`  ttuc  " + 
					"LEFT JOIN third_toutiao_userinfo ttu ON ttu.imei = ttuc.imei  " + 
					"WHERE ttuc.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') " + 
					"AND  ttu.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') "+
					"AND ttu.imei = ttuc.imei AND ttuc.`username` <> '' " + 
					"UNION ALL " + 
					"SELECT username FROM  third_toutiao_userinfo_callback  ttuc  " + 
					"LEFT JOIN third_toutiao_userinfo ttu ON ttu.idfa = ttuc.idfa  " + 
					"WHERE ttuc.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') " + 
					"AND  ttu.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') "+
					"AND ttu.idfa = ttuc.idfa AND ttuc.`username` <> '')t ON t.username = u.username " + 
					"WHERE  t.username = u.username AND u.id = ac.users_id ");
			List loadAllSql2 = dao.LoadAllSql(sql2.toString(), list.toArray());
			if (!QwyUtil.isNullAndEmpty(loadAllSql2.get(0))) {
				touTiao.setRegisterNumber(loadAllSql2.get(0)+"");
			}
			//当日认证人数-------------------------------------------------------------------------------------------------------------------------------
			StringBuffer sql3 = new StringBuffer();
			sql3.append("SELECT COUNT(1) FROM( SELECT u.id  FROM `users` u LEFT JOIN  " + 
					"(SELECT username FROM ( " + 
					"SELECT username FROM  `third_toutiao_userinfo_callback`  ttuc  " + 
					"LEFT JOIN `third_toutiao_userinfo` ttu ON ttu.imei = ttuc.imei  " + 
					"WHERE ttuc.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') " + 
					"AND  ttu.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') "+
					"AND ttu.imei = ttuc.imei AND ttuc.`username` <> '' " + 
					"UNION ALL " + 
					"SELECT username FROM  `third_toutiao_userinfo_callback`  ttuc  " + 
					"LEFT JOIN `third_toutiao_userinfo` ttu ON ttu.idfa = ttuc.idfa  " + 
					"WHERE ttuc.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') " + 
					"AND  ttu.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') "+
					"AND ttu.idfa = ttuc.idfa AND ttuc.`username` <> '')t)t ON u.`username` = t.username " + 
					"LEFT JOIN `investors` i ON i.users_id = u.`id` WHERE i.users_id = u.`id` AND u.`username` = t.username " + 
					"GROUP BY u.`id`)t ");
			List loadAllSql3 = dao.LoadAllSql(sql3.toString(), list.toArray());
			if (!QwyUtil.isNullAndEmpty(loadAllSql3.get(0))) {
				touTiao.setBindingNumber(loadAllSql3.get(0)+"");
			}
			list.clear();
			if (!QwyUtil.isNullAndEmpty(insertTime)) {
				SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
				String[] time = QwyUtil.splitTime(insertTime);
				Date parse = QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59");
				String format = sd.format(parse);
				touTiao.setDate(format);
				list.add(sd.format(parse));
				list.add(sd.format(parse));
			} else {
				Date date = new Date();
				SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
				String zdate1 = sd.format(date);
				touTiao.setDate(zdate1);
				list.add(zdate1);
				list.add(zdate1);
			}
			
			
			//激活注册转化率-------------------------------------------------------------------------------------------------------------------------------
			String activator = touTiao.getActivator();//激活人数
			String bindingNumber = touTiao.getBindingNumber();//绑定人数(认证人数)
			String registerNumber = touTiao.getRegisterNumber();//注册人数
			if (!QwyUtil.isNullAndEmpty(activator)&& !"0".equals(activator)&&!QwyUtil.isNullAndEmpty(registerNumber)&& !"0".equals(registerNumber) ) {
				touTiao.setActivationRegisterConversion(((Double.valueOf(registerNumber)/Double.valueOf(activator))*100)+"");
			}
			//注册认证转化率--------------------------------------------------------------------------------------------------------------------------------
			if (!QwyUtil.isNullAndEmpty(bindingNumber)&& !"0".equals(bindingNumber)&&!QwyUtil.isNullAndEmpty(registerNumber)&& !"0".equals(registerNumber) ) {
				touTiao.setRegisterAuthenticationConversion(((Double.valueOf(bindingNumber)/Double.valueOf(registerNumber))*100)+"");
			}else {
				touTiao.setRegisterAuthenticationConversion("0");
			}
			//首投人数--------------------------------------------------------------------------------------------------------------------------------
			StringBuffer sql4 = new StringBuffer();
			sql4.append("Call proc_toutiao_frist_investment(?,?)");
			List<Object[]> loadAllSql4 = dao.LoadAllSql(sql4.toString(), list.toArray());
			if (!QwyUtil.isNullAndEmpty(loadAllSql4.get(0)[0])) {
				touTiao.setFirstInvestmentNumber(loadAllSql4.get(0)[0]+"");
			}
			//首投总金额--------------------------------------------------------------------------------------------------------------------------------
			if (!QwyUtil.isNullAndEmpty(loadAllSql4.get(0)[1])) {
				touTiao.setFirstInvestmentMoney(isNullReturnZero(Integer.valueOf(((loadAllSql4.get(0)[1])+"").replaceAll(",", "")) /100).toString());
			}
			//认证首投转换率--------------------------------------------------------------------------------------------------------------------------------
			String firstInvestmentNumber = touTiao.getFirstInvestmentNumber();//首投人数
			if (!QwyUtil.isNullAndEmpty(bindingNumber)&& !"0".equals(bindingNumber)&&!QwyUtil.isNullAndEmpty(firstInvestmentNumber)&& !"0".equals(firstInvestmentNumber) ) {
				touTiao.setAuthenticationBuyConversion(((Double.valueOf(firstInvestmentNumber)/Double.valueOf(bindingNumber))*100)+"");
			}
			//人均首投金额--------------------------------------------------------------------------------------------------------------------------------
			String firstInvestmentMoney = touTiao.getFirstInvestmentMoney();//首投金额
			if (!QwyUtil.isNullAndEmpty(firstInvestmentNumber) && !QwyUtil.isNullAndEmpty(firstInvestmentMoney) 
					&& !"0".equals(firstInvestmentNumber) && !"0".equals(firstInvestmentMoney)) {
				touTiao.setPerCapitaFirstInvestmentMoney(((Integer.valueOf(firstInvestmentMoney)/Integer.valueOf(firstInvestmentNumber)))+"");
			}else {
				touTiao.setPerCapitaFirstInvestmentMoney("0");
			}
			//复投人数--------------------------------------------------------------------------------------------------------------------------------
			StringBuffer sql5 = new StringBuffer();
			sql5.append("CALL proc_toutiao_re_cast(?,?)");
			List<Object[]> loadAllSql5 = dao.LoadAllSql(sql5.toString(), list.toArray());
			//复投人数；
			if (!QwyUtil.isNullAndEmpty(loadAllSql5.get(0)[0])) {
				touTiao.setSecondInvestmentNumber(loadAllSql5.get(0)[0]+"");
			}
			//复投金额--------------------------------------------------------------------------------------------------------------------------------
			if (!QwyUtil.isNullAndEmpty(loadAllSql5.get(0)[1])) {
				touTiao.setSecondInvestmentMoney(isNullReturnZero(Integer.valueOf(((loadAllSql5.get(0)[1])+"").replaceAll(",", "")) /100)+"");
			}
			String secondInvestmentNumber = touTiao.getSecondInvestmentNumber();//复投人数
			String secondInvestmentMoney = touTiao.getSecondInvestmentMoney();//复投金额
			//人均复投金额--------------------------------------------------------------------------------------------------------------------------------
			if (!QwyUtil.isNullAndEmpty(secondInvestmentNumber) && !QwyUtil.isNullAndEmpty(secondInvestmentMoney) 
					&& !"0".equals(secondInvestmentNumber) && !"0".equals(secondInvestmentMoney)) {
				touTiao.setPerCapitasecondInvestmentMoney(((Integer.valueOf(secondInvestmentMoney.replaceAll(",", ""))/Integer.valueOf(secondInvestmentNumber)))+"");
			}else {
				touTiao.setPerCapitasecondInvestmentMoney("0");
			}
			//新增复投用户--------------------------------------------------------------------------------------------------------------------------------
			StringBuffer sql6 = new StringBuffer();
			sql6.append("CALL proc_toutiao_second_investment(?,?)");
			List<Object[]> loadAllSql6 = dao.LoadAllSql(sql6.toString(), list.toArray());
			//新增复投用户数
			if (!QwyUtil.isNullAndEmpty(loadAllSql6.get(0)[0])) {
				touTiao.setNewSecondInvestmentNumber(loadAllSql6.get(0)[0]+"");
			}
			//新增复投用户总金额--------------------------------------------------------------------------------------------------------------------------------
			if (!QwyUtil.isNullAndEmpty(loadAllSql6.get(0)[1])) {
				touTiao.setNewSecondInvestmentMoney(isNullReturnZero(Integer.valueOf(((loadAllSql6.get(0)[1])+"").replaceAll(",", "")) /100)+"");
			}
			String newSecondInvestmentNumber = touTiao.getNewSecondInvestmentNumber();//复投人数
			String newSecondInvestmentMoney = touTiao.getNewSecondInvestmentMoney();//首投金额
			//新增复投率--------------------------------------------------------------------------------------------------------------------------------
			if (!QwyUtil.isNullAndEmpty(newSecondInvestmentNumber) && !QwyUtil.isNullAndEmpty(newSecondInvestmentMoney) 
					&& !"0".equals(secondInvestmentNumber) && !"0".equals(newSecondInvestmentMoney)) {
				touTiao.setNewSecondConversion(((Integer.valueOf(newSecondInvestmentNumber.replaceAll(",", ""))/Integer.valueOf(newSecondInvestmentNumber))*100)+"");
			}else {
				touTiao.setNewSecondConversion("0");
			}
			
			//投资金额--------------------------------------------------------------------------------------------------------------------------------
			StringBuffer sql7 = new StringBuffer();
			sql7.append("CALL proc_toutiao_all_buy_number(?,?)");
			List<Object[]> loadAllSql7 = dao.LoadAllSql(sql7.toString(), list.toArray());
			if (!QwyUtil.isNullAndEmpty(loadAllSql7.get(0)[1])) {
				touTiao.setInvestmentMoney(isNullReturnZero(Integer.valueOf(((loadAllSql7.get(0)[1])+"").replaceAll(",", "")) /100)+"");
			}
			//投资人数
			if (!QwyUtil.isNullAndEmpty(loadAllSql7.get(0)[0])) {
				touTiao.setInvestmentNumber(loadAllSql7.get(0)[0]+"");
			}
			//人均投资金额
			String investmentMoney = touTiao.getInvestmentMoney();//投资金额
			String investmentNumber = touTiao.getInvestmentNumber();//投资人数
			if (!QwyUtil.isNullAndEmpty(investmentMoney) && !QwyUtil.isNullAndEmpty(investmentNumber) 
					&& !"0".equals(investmentMoney) && !"0".equals(investmentNumber)) {
				touTiao.setPerCapitaMoney(((Integer.valueOf(investmentMoney.replaceAll(",", ""))/Integer.valueOf(investmentNumber))+"").replaceAll(",", ""));
			}else {
				touTiao.setPerCapitaMoney("0");
			}
			return touTiao;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * 头条统计详情表
	 * @param insertTime
	 * @param os
	 * @return
	 */
	public TouTiaoStatistics findtoutiaoStatisticsInfoTable(String insertTime, Integer os) {
		TouTiaoStatistics touTiao = new TouTiaoStatistics();
		try {
			List<Object> list = new ArrayList<Object>();
			if (!QwyUtil.isNullAndEmpty(insertTime)) {
				SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
				String[] time = QwyUtil.splitTime(insertTime);
				Date parse = QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59");
				String format = sd.format(parse);
				touTiao.setDate(format);
				list.add(sd.format(parse));
				list.add(sd.format(parse));
			} else {
				Date date = new Date();
				SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
				String zdate1 = sd.format(date);
				touTiao.setDate(zdate1);
				list.add(zdate1);
				list.add(zdate1);
			}
			//获取当日广告点击量-------------------------------------------------------------------------------------------------------------------------------
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT COUNT(1) FROM `third_toutiao_userinfo` ttu WHERE ttu.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') ");
			if (os == 0 ) {
				sql.append("AND ttu.os = 0");
			}
			if (os == 1 ) {
				sql.append("AND ttu.os = 1");
			}
			List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
			if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
				touTiao.setTouTiaoClicks(loadAllSql.get(0)+"");
			}
			if (!QwyUtil.isNullAndEmpty(insertTime)) {
				SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
				String[] time = QwyUtil.splitTime(insertTime);
				Date parse = QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59");
				String format = sd.format(parse);
				touTiao.setDate(format);
				list.add(sd.format(parse));
				list.add(sd.format(parse));
			} else {
				Date date = new Date();
				SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
				String zdate1 = sd.format(date);
				touTiao.setDate(zdate1);
				list.add(zdate1);
				list.add(zdate1);
			}
			//获取当日激活量-------------------------------------------------------------------------------------------------------------------------------
			StringBuffer sql1 = new StringBuffer();
			if (os == 1) {
				sql1.append("SELECT COUNT(ttuc.`id`) a FROM `third_toutiao_userinfo_callback`  ttuc " + 
						"LEFT JOIN `third_toutiao_userinfo` ttu ON 1=1 " + 
						"WHERE ttuc.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') " + 
						"AND  ttu.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') " + 
						"AND  ttu.idfa = ttuc.idfa  ");
			}
			if (os == 0) {
				sql1.append("SELECT COUNT(ttuc.`id`)a FROM `third_toutiao_userinfo_callback` ttuc  " + 
						"LEFT JOIN `third_toutiao_userinfo` ttu ON 1=1 " + 
						"WHERE ttuc.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') " + 
						"AND  ttu.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') " + 
						"AND  ttu.imei = ttuc.imei ");
			}
			List loadAllSql1 = dao.LoadAllSql(sql1.toString(), list.toArray());
			if (!QwyUtil.isNullAndEmpty(loadAllSql1.get(0))) {
				touTiao.setActivator(loadAllSql1.get(0)+"");
			}
			//当日注册人数-------------------------------------------------------------------------------------------------------------------------------
			StringBuffer sql2 = new StringBuffer();
			if (os == 0) {
				sql2.append("SELECT COUNT(1) FROM account ac  " + 
						"LEFT JOIN users u ON u.id = ac.users_id " + 
						"LEFT JOIN ( " + 
						"SELECT username 'username' FROM  third_toutiao_userinfo_callback  ttuc LEFT JOIN third_toutiao_userinfo ttu ON ttu.imei = ttuc.imei " + 
						"WHERE ttuc.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') " + 
						"AND  ttu.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') " + 
						"AND ttu.idfa = ttuc.idfa AND ttuc.`username` <> '' AND ttuc.os = 0)t ON t.username = u.username " + 
						"WHERE  t.username = u.username AND u.id = ac.users_id  ");
			}
			if (os == 1) {
				sql2.append("SELECT COUNT(1) FROM account ac  " + 
						"LEFT JOIN users u ON u.id = ac.users_id " + 
						"LEFT JOIN ( " + 
						"SELECT username 'username' FROM  third_toutiao_userinfo_callback  ttuc LEFT JOIN third_toutiao_userinfo ttu ON ttu.idfa = ttuc.idfa " + 
						"WHERE ttuc.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') " + 
						"AND  ttu.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') " + 
						"AND ttu.idfa = ttuc.idfa AND ttuc.`username` <> '' AND ttuc.os = 1)t ON t.username = u.username " + 
						"WHERE  t.username = u.username AND u.id = ac.users_id ");
			}
			List loadAllSql2 = dao.LoadAllSql(sql2.toString(), list.toArray());
			if (!QwyUtil.isNullAndEmpty(loadAllSql2.get(0))) {
				touTiao.setRegisterNumber(loadAllSql2.get(0)+"");
			}
			//当日认证人数-------------------------------------------------------------------------------------------------------------------------------
			StringBuffer sql3 = new StringBuffer();
			if (os == 0) {
				sql3.append("SELECT COUNT(1) FROM( SELECT u.id  FROM `users` u LEFT JOIN " + 
						"(SELECT username FROM ( SELECT username FROM  `third_toutiao_userinfo_callback`  ttuc  " + 
						"LEFT JOIN `third_toutiao_userinfo` ttu ON ttu.imei = ttuc.imei  " + 
						"WHERE ttuc.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') " + 
						"AND  ttu.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') " + 
						"AND ttu.imei = ttuc.imei AND ttuc.`username` <> '')t)t ON u.`username` = t.username " + 
						"LEFT JOIN `investors` i ON i.users_id = u.`id` WHERE i.users_id = u.`id` AND u.`username` = t.username " + 
						"GROUP BY u.`id`)t");
			}
			if (os == 1) {
				sql3.append("SELECT COUNT(1) FROM( SELECT u.id  FROM `users` u LEFT JOIN " + 
						"(SELECT username FROM ( SELECT username FROM  `third_toutiao_userinfo_callback`  ttuc  " + 
						"LEFT JOIN `third_toutiao_userinfo` ttu ON ttu.idfa = ttuc.idfa  " + 
						"WHERE ttuc.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') " + 
						"AND  ttu.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') " + 
						"AND ttu.idfa = ttuc.idfa AND ttuc.`username` <> '')t)t ON u.`username` = t.username " + 
						"LEFT JOIN `investors` i ON i.users_id = u.`id` WHERE i.users_id = u.`id` AND u.`username` = t.username " + 
						"GROUP BY u.`id`)t");
			}
			List loadAllSql3 = dao.LoadAllSql(sql3.toString(), list.toArray());
			if (!QwyUtil.isNullAndEmpty(loadAllSql3.get(0))) {
				touTiao.setBindingNumber(loadAllSql3.get(0)+"");
			}
			list.clear();
			if (!QwyUtil.isNullAndEmpty(insertTime)) {
				SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
				String[] time = QwyUtil.splitTime(insertTime);
				Date parse = QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59");
				String format = sd.format(parse);
				touTiao.setDate(format);
				list.add(sd.format(parse));
				list.add(sd.format(parse));
			} else {
				Date date = new Date();
				SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
				String zdate1 = sd.format(date);
				touTiao.setDate(zdate1);
				list.add(zdate1);
				list.add(zdate1);
			}
			
			
			//激活注册转化率-------------------------------------------------------------------------------------------------------------------------------
			String activator = touTiao.getActivator();//激活人数
			String bindingNumber = touTiao.getBindingNumber();//绑定人数(认证人数)
			String registerNumber = touTiao.getRegisterNumber();//注册人数
			if (!QwyUtil.isNullAndEmpty(activator)&& !"0".equals(activator)&&!QwyUtil.isNullAndEmpty(registerNumber)&& !"0".equals(registerNumber) ) {
				touTiao.setActivationRegisterConversion(((Double.valueOf(registerNumber)/Double.valueOf(activator))*100)+"");
			}
			//注册认证转化率--------------------------------------------------------------------------------------------------------------------------------
			if (!QwyUtil.isNullAndEmpty(bindingNumber)&& !"0".equals(bindingNumber)&&!QwyUtil.isNullAndEmpty(registerNumber)&& !"0".equals(registerNumber) ) {
				touTiao.setRegisterAuthenticationConversion(((Double.valueOf(bindingNumber)/Double.valueOf(registerNumber))*100)+"");
			}else {
				touTiao.setRegisterAuthenticationConversion("0");
			}
			//首投人数
			StringBuffer sql4 = new StringBuffer();
			if (os == 0) {
				sql4.append("Call proc_toutiao_android_frist_investment(?,?)");
			}
			if (os == 1) {
				sql4.append("Call proc_toutiao_ios_frist_investment(?,?)");
			}
			List<Object[]> loadAllSql4 = dao.LoadAllSql(sql4.toString(), list.toArray());
			//首投人数
			if (!QwyUtil.isNullAndEmpty(loadAllSql4.get(0)[0])) {
				touTiao.setFirstInvestmentNumber(loadAllSql4.get(0)[0]+"");
			}
			//首投总金额--------------------------------------------------------------------------------------------------------------------------------
			if (!QwyUtil.isNullAndEmpty(loadAllSql4.get(0)[1])) {
				touTiao.setFirstInvestmentMoney(isNullReturnZero(Integer.valueOf(((loadAllSql4.get(0)[1])+"").replaceAll(",", "")) /100).toString());
			}
			//认证首投转换率--------------------------------------------------------------------------------------------------------------------------------
			String firstInvestmentNumber = touTiao.getFirstInvestmentNumber();//首投人数
			if (!QwyUtil.isNullAndEmpty(bindingNumber)&& !"0".equals(bindingNumber)&&!QwyUtil.isNullAndEmpty(firstInvestmentNumber)&& !"0".equals(firstInvestmentNumber) ) {
				touTiao.setAuthenticationBuyConversion(((Double.valueOf(firstInvestmentNumber)/Double.valueOf(bindingNumber))*100)+"");
			}
			//人均首投金额--------------------------------------------------------------------------------------------------------------------------------
			String firstInvestmentMoney = touTiao.getFirstInvestmentMoney();//首投金额
			if (!QwyUtil.isNullAndEmpty(firstInvestmentNumber) && !QwyUtil.isNullAndEmpty(firstInvestmentMoney) 
					&& !"0".equals(firstInvestmentNumber) && !"0".equals(firstInvestmentMoney)) {
				touTiao.setPerCapitaFirstInvestmentMoney(((Integer.valueOf(firstInvestmentMoney)/Integer.valueOf(firstInvestmentNumber)))+"");
			}else {
				touTiao.setPerCapitaFirstInvestmentMoney("0");
			}
			//复投人数
			StringBuffer sql5 = new StringBuffer();
			if (os == 0) {
				sql5.append("CALL proc_toutiao_android_re_cast(?,?)");
			}
			if (os == 1) {
				sql5.append("CALL proc_toutiao_ios_re_cast(?,?) ");
			}
			List<Object[]> loadAllSql5 = dao.LoadAllSql(sql5.toString(), list.toArray());
			//复投人数；
			if (!QwyUtil.isNullAndEmpty(loadAllSql5.get(0)[0])) {
				touTiao.setSecondInvestmentNumber(loadAllSql5.get(0)[0]+"");
			}
			//复投金额--------------------------------------------------------------------------------------------------------------------------------
			if (!QwyUtil.isNullAndEmpty(loadAllSql5.get(0)[1])) {
				touTiao.setSecondInvestmentMoney(isNullReturnZero(Integer.valueOf(((loadAllSql5.get(0)[1])+"").replaceAll(",", "")) /100)+"");
			}
			String secondInvestmentNumber = touTiao.getSecondInvestmentNumber();//复投人数
			String secondInvestmentMoney = touTiao.getSecondInvestmentMoney();//复投金额
			//人均复投金额--------------------------------------------------------------------------------------------------------------------------------
			if (!QwyUtil.isNullAndEmpty(secondInvestmentNumber) && !QwyUtil.isNullAndEmpty(secondInvestmentMoney) 
					&& !"0".equals(secondInvestmentNumber) && !"0".equals(secondInvestmentMoney)) {
				touTiao.setPerCapitasecondInvestmentMoney(((Integer.valueOf(secondInvestmentMoney.replaceAll(",", ""))/Integer.valueOf(secondInvestmentNumber)))+"");
			}else {
				touTiao.setPerCapitasecondInvestmentMoney("0");
			}
			//新增复投用户
			StringBuffer sql6 = new StringBuffer();
			if (os == 0) {
				sql6.append("CALL proc_toutiao_android_second_investment(?,?) ");
			}
			if (os == 1) {
				sql6.append("CALL proc_toutiao_ios_second_investment(?,?) ");
			}
			List<Object[]> loadAllSql6 = dao.LoadAllSql(sql6.toString(), list.toArray());
			//新增复投用户数
			if (!QwyUtil.isNullAndEmpty(loadAllSql6.get(0)[0])) {
				touTiao.setNewSecondInvestmentNumber(loadAllSql6.get(0)[0]+"");
			}
			//新增复投用户总金额--------------------------------------------------------------------------------------------------------------------------------
			if (!QwyUtil.isNullAndEmpty(loadAllSql6.get(0)[1])) {
				touTiao.setNewSecondInvestmentMoney(isNullReturnZero(Integer.valueOf(((loadAllSql6.get(0)[1])+"").replaceAll(",", "")) /100)+"");
			}
			String newSecondInvestmentNumber = touTiao.getNewSecondInvestmentNumber();//复投人数
			String newSecondInvestmentMoney = touTiao.getNewSecondInvestmentMoney();//首投金额
			//新增复投率--------------------------------------------------------------------------------------------------------------------------------
			if (!QwyUtil.isNullAndEmpty(newSecondInvestmentNumber) && !QwyUtil.isNullAndEmpty(newSecondInvestmentMoney) 
					&& !"0".equals(secondInvestmentNumber) && !"0".equals(newSecondInvestmentMoney)) {
				touTiao.setNewSecondConversion(((Integer.valueOf(newSecondInvestmentNumber.replaceAll(",", ""))/Integer.valueOf(newSecondInvestmentNumber))*100)+"");
			}else {
				touTiao.setNewSecondConversion("0");
			}
			StringBuffer sql7 = new StringBuffer();
			if (os == 0) {
				sql7.append("CALL proc_toutiao_android_all_buy_number(?,?) ");
			}
			if (os == 1) {
				sql7.append("CALL proc_toutiao_ios_all_buy_number(?,?) ");
			}
			List<Object[]> loadAllSql7 = dao.LoadAllSql(sql7.toString(), list.toArray());
			if (!QwyUtil.isNullAndEmpty(loadAllSql7.get(0)[1])) {
				touTiao.setInvestmentMoney(isNullReturnZero(Integer.valueOf(((loadAllSql7.get(0)[1])+"").replaceAll(",", "")) /100)+"");
			}
			//投资人数
			if (!QwyUtil.isNullAndEmpty(loadAllSql7.get(0)[0])) {
				touTiao.setInvestmentNumber(loadAllSql7.get(0)[0]+"");
			}
			//人均投资金额
			String investmentMoney = touTiao.getInvestmentMoney();//投资金额
			String investmentNumber = touTiao.getInvestmentNumber();//投资人数
			if (!QwyUtil.isNullAndEmpty(investmentMoney) && !QwyUtil.isNullAndEmpty(investmentNumber) 
					&& !"0".equals(investmentMoney) && !"0".equals(investmentNumber)) {
				touTiao.setPerCapitaMoney(((Integer.valueOf(investmentMoney.replaceAll(",", ""))/Integer.valueOf(investmentNumber))+"").replaceAll(",", ""));
			}else {
				touTiao.setPerCapitaMoney("0");
			}
			return touTiao;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	  /**
     * 参数空时，返回"0"
     *
     * @param str
     * @return
     */
    public String isNullReturnZero(Object str) {
        return QwyUtil.isNullAndEmpty(str) ? "0" : str.toString();
    }

	/**
	 * 将未加密的头条数据改为加密
	 */
	public void encryptUsername(){
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("FROM ThirdTouTiaoCallBackUserInfo");
			List<ThirdTouTiaoCallBackUserInfo> list = dao.LoadAll(sql.toString(), null);
			for(ThirdTouTiaoCallBackUserInfo toutiao : list){
				if(!QwyUtil.isNullAndEmpty(toutiao.getUsername()) && toutiao.getUsername().length()<12){
					String s = DESEncrypt.jiaMiUsername(toutiao.getUsername());
					toutiao.setUsername(s);
					dao.saveOrUpdate(toutiao);
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
