package com.huoq.thread.bean;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.common.util.QwyUtil;
import com.huoq.orm.QdtjPlatform;
import com.huoq.thread.dao.ThreadDAO;

/**更新 各平台渠道统计汇总表
 * @author 覃文勇
 *
 * 2017年1月21日上午3:54:05
 */
@Service
public class UpdateQdtjPlatformThreadBean {
	private static Logger log = Logger.getLogger(UpdateQdtjPlatformThreadBean.class); 
	@Resource
	private ThreadDAO dao;
	
	/**
	 * 全部重新更新一遍;<br>一般用于初始化qdtj表
	 */
	public void updateQdtjAll(){
		List<Object> listDateday = dao.loadDateday();
		if(!QwyUtil.isNullAndEmpty(listDateday)){
			for (Object object : listDateday) {
				try {
					long a1 = System.currentTimeMillis();
					updateQdtjPlatformByDate(QwyUtil.fmyyyyMMddHHmmss.parse(object.toString()));
					long a2 = System.currentTimeMillis();
					log.info(object.toString()+"-------------updateQdtjAll【更新各平台渠道sql】耗时:"+(a2-a1));
				} catch (Exception e) {
					log.error("操作异常: ",e);
				}
			}
		}
	}
	
	/**
	 * 更新昨天的渠道统计数据到qdtj表;一般在零点后更新昨天的数据;<br>
	 * 一般用于线程;
	 */
	public void updateQdtjPlatformYestoday(){
		try {
			Date yestoday = QwyUtil.addDaysFromOldDate(new Date(), -1).getTime();
			long a1 = System.currentTimeMillis();
			updateQdtjPlatformByDate(yestoday);
			long a2 = System.currentTimeMillis();
			log.info("-------------updateQdtjYestoday【更新各平台渠道sql】耗时:"+(a2-a1));
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
	}
	
	/**
	 * 更新当天的渠道统计数据到qdtj表;一般在零点后更新昨天的数据;<br>
	 * 一般用于线程;
	 */
	public void updateQdtjPlatformToday(){
		try {
			Date today = new Date();
			long a1 = System.currentTimeMillis();
			updateQdtjPlatformByDate(today);
			long a2 = System.currentTimeMillis();
			log.info("-------------updateQdtjToday【更新各平台渠道sql】耗时:"+(a2-a1));
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
	}
	
	/**
	 * 删除qdtj数据;更新该天数据时,先删除;
	 */
	public void deleteQdtjByDate(Date date){
		StringBuffer sb = new StringBuffer();
		sb.append("DELETE FROM qdtj_platform  WHERE query_date BETWEEN ? AND ?");
		Object obj = dao.excuteSql(sb.toString(),new Object[]{QwyUtil.fmyyyyMMdd.format(date)+" 00:00:00",QwyUtil.fmyyyyMMdd.format(date)+" 23:59:59"});
		log.info("---------【更新各平台渠道sql】----删除数据成功: "+obj);
	}
	
	
	
	/**参数空时，返回"0"
	 * @param str
	 * @return
	 */
	public String isNullReturnZero(Object str){
		return QwyUtil.isNullAndEmpty(str)?"0":str.toString();
	}
	
	
	/**获取激活次数;
	 * @param date
	 * @return
	 */
	private List<Object[]> activityNum(String date){
		ArrayList<Object> obList = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT ifnull(type,1)'myType',COUNT(*) FROM `activity` act ");
		if(!QwyUtil.isNullAndEmpty(date)){
			sb.append("  WHERE act.`insert_time` BETWEEN ? AND ? ");
			obList.add(date +" 00:00:00");
			obList.add(date +" 23:59:59");
		}
		sb.append("GROUP BY myType ");
		
		return (List<Object[]>)dao.LoadAllSql(sb.toString(), obList.toArray());
	}
	
	
	
	/**获取注册人数
	 * @param date
	 * @return
	 */
	private List<Object[]> registNum(String date){
		ArrayList<Object> obList = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT ui.`regist_platform` ,COUNT(*) ");
		sb.append(" FROM `users` ui ");
		if(!QwyUtil.isNullAndEmpty(date)){
			sb.append("  WHERE ui.`insert_time` BETWEEN ? AND ? ");
			obList.add(date +" 00:00:00");
			obList.add(date +" 23:59:59");
		}
		sb.append(" GROUP BY ui.`regist_platform` ORDER BY ui.`regist_platform`+0 asc ");
		
		return (List<Object[]>)dao.LoadAllSql(sb.toString(), obList.toArray());
	}
	
	
	
	/**获取绑定人数
	 * @param date
	 * @return
	 */
	private List<Object[]> bindNum(String date){
		ArrayList<Object> obList = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT ui.`regist_platform`  ,COUNT(ac.`id`)  FROM `users` ui ");
		sb.append(" LEFT JOIN `account` ac on ui.`id` =ac.`users_id` ");
		sb.append("  WHERE ac.status= '0' ");
		if(!QwyUtil.isNullAndEmpty(date)){
			sb.append("  AND ac.`insert_time` BETWEEN ? AND ? ");
			obList.add(date +" 00:00:00");
			obList.add(date +" 23:59:59");
		}
		sb.append("GROUP BY ui.`regist_platform` ORDER BY ui.`regist_platform`+0 asc;");
		
		return (List<Object[]>)dao.LoadAllSql(sb.toString(), obList.toArray());
	}
	
	
	/**获取投资人数,投资金额<br>
	 * 投资人数,投资金额 找出所有用户的第一笔投资,然后根据时间筛选
	 * @param date
	 * @return
	 */
	private List<Object[]> tzRs(String date){
		ArrayList<Object> obList = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT ui.`regist_platform`,count(distinct inv.users_id),sum(inv.`in_money`) FROM `users` ui ");
		sb.append(" LEFT JOIN `investors` inv    on ui.`id` =inv.`users_id`  ");
		sb.append(" WHERE inv.`investor_status` IN ('1','2','3') ");
		if(!QwyUtil.isNullAndEmpty(date)){
			sb.append("  AND inv.`pay_time` BETWEEN ? AND ? ");
			obList.add(date +" 00:00:00");
			obList.add(date +" 23:59:59");
		}
		sb.append(" GROUP BY ui.`regist_platform` ORDER BY ui.`regist_platform`+0 asc; ");
		return (List<Object[]>)dao.LoadAllSql(sb.toString(), obList.toArray());
	}
	
	/**首投人数,首投金额<br>
	 * @param date
	 * @return
	 */
	private List<Object[]> stRs(String date){
		ArrayList<Object> obList = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT ui.`regist_platform`,count(distinct invUsersId),sum(`in_money`) FROM `users` ui   LEFT JOIN    ");
		sb.append(" ( ");
		sb.append(" SELECT inv.`users_id`'invUsersId' ,inv.`in_money` ,inv.`pay_time`  FROM `investors` inv WHERE inv.`investor_status` in(1,2,3) ");
		sb.append(" GROUP BY inv.`users_id` ORDER BY `pay_time` asc )t on invUsersId=ui.`id` ");
		if(!QwyUtil.isNullAndEmpty(date)){
			sb.append("  WHERE `pay_time` BETWEEN ? AND ? ");
			obList.add(date +" 00:00:00");
			obList.add(date +" 23:59:59");
		}
		sb.append(" GROUP BY `regist_platform` ORDER BY ui.`regist_platform`+0 asc; ");
		
		return (List<Object[]>)dao.LoadAllSql(sb.toString(), obList.toArray());
	}
	
	/**充值金额<br>
	 * @param date
	 * @return
	 */
	private List<Object[]> czMoney(String date){
		ArrayList<Object> obList = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT us.regist_platform ,ifnull(sum(cz.`money`),0) 'czMoney'  FROM `users` us");
		sb.append(" LEFT JOIN `cz_record` cz on us.`id` =cz.`users_id` and cz.`STATUS` ='1' ");
		if(!QwyUtil.isNullAndEmpty(date)){
			sb.append("  and cz.`insert_time`  BETWEEN ? AND ? ");
			obList.add(date +" 00:00:00");
			obList.add(date +" 23:59:59");
		}
		sb.append(" GROUP BY us.regist_platform ORDER BY us.regist_platform+0 asc;");
		return (List<Object[]>)dao.LoadAllSql(sb.toString(), obList.toArray());
	}
	
	/**提现金额<br>
	 * @param date
	 * @return
	 */
	private List<Object[]> txMoney(String date){
		ArrayList<Object> obList = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT ui.regist_platform , ifnull(sum(tx.`money`),0) 'txMoney'  FROM `users` ui   ");
		sb.append(" LEFT JOIN `tx_record` tx on ui.id = tx.`users_id` and tx.`STATUS` ='1' ");
		if(!QwyUtil.isNullAndEmpty(date)){
			sb.append("  and  tx.`check_time`  BETWEEN ? AND ? ");
			obList.add(date +" 00:00:00");
			obList.add(date +" 23:59:59");
		}
		sb.append(" GROUP BY ui.regist_platform ORDER BY ui.regist_platform+0 asc;");
		return (List<Object[]>)dao.LoadAllSql(sb.toString(), obList.toArray());
	}
	
	/**根据日期来更新对应的运营数据【各平台渠道统计汇总】
	 * @param date
	 * @throws ParseException 
	 */
	public void updateQdtjPlatformByDate(Date queryDate) throws Exception{
		String date = QwyUtil.fmyyyyMMdd.format(queryDate);
		//获取运营数据
		List<Object[]> registList = registNum(date);
		List<Object[]> jhrsList = activityNum(date);
		List<Object[]> bindList = bindNum(date);
		List<Object[]> tzList = tzRs(date);
		List<Object[]> stList = stRs(date);
		List<Object[]> czMoneyList = czMoney(date);
		List<Object[]> txMoneyList = txMoney(date);
		
		Map<String,QdtjPlatform> qdtjMap = new HashMap<String,QdtjPlatform>();
		
		//注册平台,注册人数
		if(!QwyUtil.isNullAndEmpty(registList)){
			for (Object[] obj : registList) {
				if(QwyUtil.isNullAndEmpty(obj))
					continue;
				String platform = isNullReturnZero(obj[0]);//注册平台
				QdtjPlatform qdtj = new QdtjPlatform();
				qdtj.setPlatform(platform);
				qdtj.setZcrs(isNullReturnZero(obj[1]));
				qdtj.setQueryDate(QwyUtil.fmyyyyMMdd.parse(date));
				qdtj.setInsertTime(new Date());
				qdtjMap.put(platform, qdtj);
			}
		}
		//注册平台,激活人数
		if(!QwyUtil.isNullAndEmpty(jhrsList)){
			for (Object[] obj : jhrsList) {
				if(QwyUtil.isNullAndEmpty(obj))
					continue;
				String platform = isNullReturnZero(obj[0]);//注册平台
				QdtjPlatform qdtj = qdtjMap.get(platform);
				if(QwyUtil.isNullAndEmpty(qdtj)){
					qdtj = new QdtjPlatform();
					qdtj.setPlatform(platform);
					qdtj.setQueryDate(QwyUtil.fmyyyyMMdd.parse(date));
					qdtj.setInsertTime(new Date());
				}
				qdtj.setJhcs(isNullReturnZero(obj[1]));//激活人数
					
				String jhrs = qdtj.getJhcs();
				String qdzhlStrNew = "0".equals(jhrs)?"0":QwyUtil.calcNumber(qdtj.getZcrs(), jhrs, "/").toString();//渠道转化率
				qdtj.setZczhl(QwyUtil.calcNumber(qdzhlStrNew, 100, "*").toString());//渠道转化率%
				qdtjMap.put(platform, qdtj);
			}
		}
		
		//注册平台,绑定人数
		if(!QwyUtil.isNullAndEmpty(bindList)){
			for (Object[] obj : bindList) {
				if(QwyUtil.isNullAndEmpty(obj))
					continue;
				String platform = isNullReturnZero(obj[0]);//注册平台
				QdtjPlatform qdtj = qdtjMap.get(platform);
				if(QwyUtil.isNullAndEmpty(qdtj)){
					qdtj = new QdtjPlatform();
					qdtj.setPlatform(platform);
					qdtj.setQueryDate(QwyUtil.fmyyyyMMdd.parse(date));
					qdtj.setInsertTime(new Date());
				}
				qdtj.setBkrs(isNullReturnZero(obj[1]));
				
				String zcrs = qdtj.getZcrs();
				String bkzhl = "0".equals(zcrs)?"0":QwyUtil.calcNumber(qdtj.getBkrs(), zcrs, "/").toString();//渠道转化率
				qdtj.setBkzhl(QwyUtil.calcNumber(bkzhl, 100, "*").toString());//绑卡转化率%
				
				qdtjMap.put(platform, qdtj);
			}
		}
		
		
		//注册平台,投资人数,投资本金
		if(!QwyUtil.isNullAndEmpty(tzList)){
			for (Object[] obj : tzList) {
				if(QwyUtil.isNullAndEmpty(obj))
					continue;
				String platform = isNullReturnZero(obj[0]);//注册平台
				QdtjPlatform qdtj = qdtjMap.get(platform);
				if(QwyUtil.isNullAndEmpty(qdtj)){
					qdtj = new QdtjPlatform();
					qdtj.setPlatform(platform);
					qdtj.setQueryDate(QwyUtil.fmyyyyMMdd.parse(date));
					qdtj.setInsertTime(new Date());
				}
				qdtj.setTzrs(isNullReturnZero(obj[1]));//投资人数
				qdtj.setTzje(isNullReturnZero(obj[2]));//投资金额
				
				String tzrs = qdtj.getTzrs();
				String rjtzje = "0".equals(tzrs)?"0":QwyUtil.calcNumber(qdtj.getTzje(), tzrs, "/").toString();//人均投资本金
				qdtj.setRjtzje(rjtzje);//人均投资本金
				
				qdtjMap.put(platform, qdtj);
			}
		}
		
		
		//注册平台,首投人数,首投本金
		if(!QwyUtil.isNullAndEmpty(stList)){
			for (Object[] obj : stList) {
				if(QwyUtil.isNullAndEmpty(obj))
					continue;
				String platform = isNullReturnZero(obj[0]);//注册平台
				QdtjPlatform qdtj = qdtjMap.get(platform);
				if(QwyUtil.isNullAndEmpty(qdtj)){
					qdtj = new QdtjPlatform();
					qdtj.setPlatform(platform);
					qdtj.setQueryDate(QwyUtil.fmyyyyMMdd.parse(date));
					qdtj.setInsertTime(new Date());
				}
				qdtj.setStrs(isNullReturnZero(obj[1]));//首投人数
				qdtj.setStje(isNullReturnZero(obj[2]));//首投金额
				
				String bkrs = qdtj.getBkrs();
				String stzhl = "0".equals(bkrs)?"0":QwyUtil.calcNumber(qdtj.getStrs(), bkrs, "/").toString();//首投转化率
				qdtj.setStzhl(QwyUtil.calcNumber(stzhl, 100, "*").toString());//首投转化率%
				
				String strs = qdtj.getStrs();
				String rjstje = "0".equals(strs)?"0":QwyUtil.calcNumber(qdtj.getStje(), strs, "/").toString();//人均首投本金
				qdtj.setRjstje(rjstje);//人均首投本金
				
				qdtjMap.put(platform, qdtj);
			}
		}
		
		//注册平台,充值金额
		if(!QwyUtil.isNullAndEmpty(czMoneyList)){
			for (Object[] obj : czMoneyList) {
				if(QwyUtil.isNullAndEmpty(obj))
					continue;
				String platform = isNullReturnZero(obj[0]);//注册平台
				QdtjPlatform qdtj = qdtjMap.get(platform);
				if(QwyUtil.isNullAndEmpty(qdtj)){
					qdtj = new QdtjPlatform();
					qdtj.setPlatform(platform);
					qdtj.setQueryDate(QwyUtil.fmyyyyMMdd.parse(date));
					qdtj.setInsertTime(new Date());
				}
				qdtj.setCzje(isNullReturnZero(obj[1]));//充值金额
				qdtjMap.put(platform, qdtj);
			}
		}

		//注册平台,提现金额
		if(!QwyUtil.isNullAndEmpty(txMoneyList)){
			for (Object[] obj : txMoneyList) {
				if(QwyUtil.isNullAndEmpty(obj))
					continue;
				String platform = isNullReturnZero(obj[0]);//注册平台
				QdtjPlatform qdtj = qdtjMap.get(platform);
				if(QwyUtil.isNullAndEmpty(qdtj)){
					qdtj = new QdtjPlatform();
					qdtj.setPlatform(platform);
					qdtj.setQueryDate(QwyUtil.fmyyyyMMdd.parse(date));
					qdtj.setInsertTime(new Date());
				}
				qdtj.setTxje(isNullReturnZero(obj[1]));//充值金额
				qdtjMap.put(platform, qdtj);
			}
		}

		// 对运营数据进行操作;
		if(!QwyUtil.isNullAndEmpty(qdtjMap)){
			//先删除旧数据;
			deleteQdtjByDate(queryDate);
			Set<String> keys = qdtjMap.keySet();
			List<QdtjPlatform> listQdtj = new ArrayList<QdtjPlatform>();
			for (String key : keys) {
				listQdtj.add(qdtjMap.get(key));
			}
			//添加改日期的运营数据
			if(!QwyUtil.isNullAndEmpty(listQdtj)){
				dao.saveList(listQdtj);
			}
			
		}
		
		
	}
	

}
