package com.huoq.thread.bean;

import com.huoq.common.util.QwyUtil;
import com.huoq.orm.Qdtj;
import com.huoq.thread.dao.ThreadDAO;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.*;

/**
 * 更新 Android渠道统计汇总表
 * 
 * @author 覃文勇
 *
 *         2017年1月21日上午3:54:05
 */
@Service
public class UpdateQdtjThreadBean {
	private static Logger log = Logger.getLogger(UpdateQdtjThreadBean.class);
	@Resource
	private ThreadDAO dao;

	/**
	 * 全部重新更新一遍;<br>
	 * 一般用于初始化qdtj表
	 */
	public void updateQdtjAll() {
		List<Object> listDateday = dao.loadDateday();
		if (!QwyUtil.isNullAndEmpty(listDateday)) {
			for (Object object : listDateday) {
				try {
					long a1 = System.currentTimeMillis();
					updateQdtjByDate(QwyUtil.fmyyyyMMddHHmmss.parse(object.toString()));
					long a2 = System.currentTimeMillis();
					log.info("-------------updateQdtjAll【更新Android渠道sql】耗时:" + (a2 - a1));
				} catch (Exception e) {
					log.error("操作异常: ", e);
				}
			}
		}
	}

	/**
	 * 更新昨天的渠道统计数据到qdtj表;一般在零点后更新昨天的数据;<br>
	 * 一般用于线程;
	 */
	public void updateQdtjYestoday() {
		try {
			Date yestoday = QwyUtil.addDaysFromOldDate(new Date(), -1).getTime();
			long a1 = System.currentTimeMillis();
			updateQdtjByDate(yestoday);
			long a2 = System.currentTimeMillis();
			log.info("-------------updateQdtjYestoday【更新Android渠道sql】耗时:" + (a2 - a1));
		} catch (Exception e) {
			log.error("操作异常: ", e);
		}
	}

	/**
	 * 更新当天的渠道统计数据到qdtj表;一般在零点后更新昨天的数据;<br>
	 * 一般用于线程;
	 */
	public void updateQdtjToday() {
		try {
			Date today = new Date();
			long a1 = System.currentTimeMillis();
			updateQdtjByDate(today);
			long a2 = System.currentTimeMillis();
			log.info("-------------updateQdtjToday【更新Android渠道sql】耗时:" + (a2 - a1));
		} catch (Exception e) {
			log.error("操作异常: ", e);
		}
	}

	/**
	 * 删除qdtj数据;更新该天数据时,先删除;
	 */
	public void deleteQdtjByDate(Date date) {
		StringBuffer sb = new StringBuffer();
		sb.append("DELETE FROM qdtj  WHERE date BETWEEN ? AND ?");
		Object obj = dao.excuteSql(sb.toString(), new Object[] { QwyUtil.fmyyyyMMdd.format(date) + " 00:00:00",
				QwyUtil.fmyyyyMMdd.format(date) + " 23:59:59" });
		log.info("---------【更新Android渠道sql】----删除数据成功: " + obj);
	}

	/**
	 * 查询渠道统计的sql;
	 * 
	 * @return
	 */
	/*
	 * public String queryQdtjSQL(String date){ StringBuffer sb = new
	 * StringBuffer(); sb.append("SELECT "); sb.
	 * append(" t1.*,COUNT(ui.id)'zcrs',COUNT(ac.id)'bdrs',COUNT(inv.users_id)'tzrs',COUNT(strsStje.users_id)'strs',SUM(strsStje.stje)'stje',"
	 * ); sb.
	 * append(" COUNT(ftrsftje.users_id)'ftrs',SUM(ftrsftje.stje)'ftje',COUNT(czRecord.users_id)'czrs',SUM(czRecord.czje)'czje'  FROM ("
	 * ); sb.
	 * append(" SELECT pc.channel,pc.channel_code,pc.channel_name,COUNT(act.id) 'jhrs' FROM plat_channel pc "
	 * ); sb.append(" LEFT JOIN activity act ON pc.channel_code = act.channel ");
	 * if(!QwyUtil.isNullAndEmpty(date))
	 * sb.append(" AND act.insert_time BETWEEN '"+date+" 00:00:00' AND '"
	 * +date+" 23:59:59' "); sb.append(" GROUP BY pc.channel_code ");
	 * sb.append(" ) t1");
	 * sb.append(" LEFT JOIN  users us ON t1.channel_code=us.regist_channel");
	 * sb.append(" LEFT JOIN  account ac ON us.id=ac.users_id AND ac.status='0' ");
	 * if(!QwyUtil.isNullAndEmpty(date))
	 * sb.append(" AND ac.insert_time BETWEEN '"+date+" 00:00:00' AND '"
	 * +date+" 23:59:59' "); sb.
	 * append(" LEFT JOIN  (SELECT ii.users_id FROM investors ii WHERE ii.investor_status IN ('1','2','3') "
	 * ); if(!QwyUtil.isNullAndEmpty(date))
	 * sb.append(" AND ii.pay_time BETWEEN '"+date+" 00:00:00' AND '"
	 * +date+" 23:59:59'");
	 * sb.append(" GROUP BY ii.users_id ) inv ON us.id=inv.users_id");
	 * sb.append(" LEFT JOIN ("); sb.append(" SELECT tt.users_id,tt.stje FROM (");
	 * sb.
	 * append(" SELECT  COUNT(inv.users_id),inv.users_Id,SUM(inv.in_money)'stje' FROM investors inv WHERE inv.investor_status IN ('1','2','3') "
	 * ); if(!QwyUtil.isNullAndEmpty(date))
	 * sb.append(" AND inv.pay_time BETWEEN '"+date+" 00:00:00' AND '"
	 * +date+" 23:59:59'");
	 * sb.append(" GROUP BY inv.users_id,DATE_FORMAT(inv.pay_time,'%Y-%m-%d')      "
	 * ); sb.append(" ) tt GROUP BY tt.users_id HAVING COUNT(tt.users_id)=1");
	 * sb.append(" ) strsStje ON us.id=strsStje.users_id");
	 * sb.append(" LEFT JOIN ("); sb.append(" SELECT tt.users_id,tt.stje FROM (");
	 * sb.
	 * append(" SELECT  COUNT(inv.users_id),inv.users_Id,SUM(inv.in_money)'stje' FROM investors inv WHERE inv.investor_status IN ('1','2','3') "
	 * ); if(!QwyUtil.isNullAndEmpty(date))
	 * sb.append(" AND inv.pay_time BETWEEN '"+date+" 00:00:00' AND '"
	 * +date+" 23:59:59'");
	 * sb.append(" GROUP BY inv.users_id,DATE_FORMAT(inv.pay_time,'%Y-%m-%d')      "
	 * ); sb.append(" ) tt GROUP BY tt.users_id HAVING COUNT(tt.users_id)>1");
	 * sb.append(" ) ftrsftje ON us.id=ftrsftje.users_id");
	 * sb.append(" LEFT JOIN  users ui ON ui.id=us.id ");
	 * if(!QwyUtil.isNullAndEmpty(date))
	 * sb.append(" AND ui.insert_time BETWEEN '"+date+" 00:00:00' AND '"
	 * +date+" 23:59:59'"); sb.append(" LEFT JOIN (");
	 * sb.append(" SELECT users_id,czje FROM ("); sb.
	 * append(" SELECT  COUNT(cz.users_id),cz.users_id,SUM(cz.money)'czje' FROM cz_record cz WHERE cz.status IN ('1') GROUP BY cz.users_id  "
	 * ); sb.append(" ) tt GROUP BY tt.users_id ");
	 * sb.append(" ) czRecord ON czRecord.users_id = us.id "); //sb.
	 * append(" LEFT JOIN cz_record cz ON us.id = cz.users_id AND cz.status='1' AND cz.insert_time BETWEEN '"
	 * +date+" 00:00:00' AND '"+date+" 23:59:59'");
	 * if(!QwyUtil.isNullAndEmpty(date))
	 * sb.append(" AND ui.insert_time BETWEEN '"+date+" 00:00:00' AND '"
	 * +date+" 23:59:59'");
	 * sb.append(" GROUP BY channel_code,jhrs ORDER BY channel ASC");
	 * log.info(sb.toString()); return sb.toString(); }
	 */

	/**
	 * 数据入库;临时表的数据填入到qdtj表;做缓存;
	 * 
	 * @param list
	 * @return
	 */
	public List<Qdtj> parseToQdtj(List<Object[]> list, Date date) {
		List<Qdtj> qdtjList = new ArrayList<Qdtj>();
		if (QwyUtil.isNullAndEmpty(list))
			return null;
		for (Object[] obj : list) {
			try {
				Qdtj qdtj = new Qdtj();
				qdtj.setChannel(isNullReturnZero(obj[0]));
				qdtj.setChannelCode(isNullReturnZero(obj[1]));
				qdtj.setChannelName(isNullReturnZero(obj[2]));
				qdtj.setActivityCount(isNullReturnZero(obj[3]));
				qdtj.setRegCount(isNullReturnZero(obj[4]));
				qdtj.setBindCount(isNullReturnZero(obj[5]));
				qdtj.setTzrs(isNullReturnZero(obj[6]));
				qdtj.setStrs(isNullReturnZero(obj[7]));
				qdtj.setStje(isNullReturnZero(Double.parseDouble(isNullReturnZero((obj[8])))));
				qdtj.setFtrs(isNullReturnZero(obj[9]));
				qdtj.setFtje(isNullReturnZero(Double.parseDouble(isNullReturnZero((obj[10])))));
				qdtj.setCzcount(isNullReturnZero(obj[11]));
				qdtj.setCzje(isNullReturnZero(Double.parseDouble(isNullReturnZero((obj[12])))));
				qdtj.setTzje(isNullReturnZero((Double.parseDouble(isNullReturnZero((obj[8])))
						+ Double.parseDouble(isNullReturnZero((obj[10]))))));
				String cftzlStr = isNullReturnZero((obj[6]));
				String cftzlStrNew = "0".equals(cftzlStr) ? "0"
						: QwyUtil.calcNumber(isNullReturnZero((obj[9])), cftzlStr, "/").toString();
				qdtj.setCftzl(QwyUtil.calcNumber(cftzlStrNew, 100, "*").toString());
				String qdzhlStr = isNullReturnZero((obj[3]));
				String qdzhlStrNew = "0".equals(qdzhlStr) ? "0"
						: QwyUtil.calcNumber(isNullReturnZero((obj[4])), qdzhlStr, "/").toString();
				qdtj.setQdzhl(QwyUtil.calcNumber(qdzhlStrNew, 100, "*").toString());
				qdtj.setDate(QwyUtil.fmyyyyMMdd.parse(QwyUtil.fmyyyyMMdd.format(date)));
				qdtj.setInsertTime(new Date());
				qdtjList.add(qdtj);
			} catch (Exception e) {
				log.error("操作异常: ", e);
			}
		}
		return qdtjList;
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
	 * 获取激活人数;
	 * 
	 * @param date
	 * @return
	 */
	private List<Object[]> activityNum(String date) {
		ArrayList<Object> obList = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT pc.channel, pc.channel_code, pc.channel_name, COUNT(act.id) 'jhrs' ");
		sb.append(" FROM plat_channel pc ");
		sb.append(" LEFT JOIN activity act ON pc.channel_code= act.channel");
		if (!QwyUtil.isNullAndEmpty(date)) {
			sb.append("  WHERE act.`insert_time` BETWEEN ? AND ? ");
			obList.add(date + " 00:00:00");
			obList.add(date + " 23:59:59");
		}
		sb.append(" GROUP BY pc.channel_code ORDER BY pc.`channel` asc; ");

		return (List<Object[]>) dao.LoadAllSql(sb.toString(), obList.toArray());
	}

	/**
	 * 获取注册人数
	 * 
	 * @param date
	 * @return
	 */
	private List<Object[]> registNum(String date) {
		ArrayList<Object> obList = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT * FROM (SELECT pc.channel, pc.channel_code, pc.channel_name,count( DISTINCT us.id) ");
		sb.append(" from plat_channel pc ");
		sb.append(" LEFT JOIN users us on pc.channel_code = us.regist_channel ");
		if (!QwyUtil.isNullAndEmpty(date)) {
			sb.append("  WHERE us.insert_time BETWEEN ? AND ? ");
			obList.add(date + " 00:00:00");
			obList.add(date + " 23:59:59");
		}
		sb.append(" GROUP BY pc.channel_code ORDER BY pc.channel asc )t; ");
		return (List<Object[]>) dao.LoadAllSql(sb.toString(), obList.toArray());
	}

	/**
	 * 获取绑定人数
	 * 
	 * @param date
	 * @return
	 */
	private List<Object[]> bindNum(String date) {
		ArrayList<Object> obList = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		sb.append(
				" SELECT pl.channel, pl.channel_code, pl.channel_name,COUNT(users_id)  FROM plat_channel pl LEFT JOIN ");
		sb.append(" ( ");
		sb.append(" SELECT us.id users_id ,us.regist_channel regist_channel  from users us WHERE id IN  ");
		sb.append(" ( SELECT ac.users_id  FROM account ac WHERE ac.status= '1' ");
		if (!QwyUtil.isNullAndEmpty(date)) {
			sb.append("  AND ac.insert_time BETWEEN ? AND ? ");
			obList.add(date + " 00:00:00");
			obList.add(date + " 23:59:59");
		}
		sb.append(" )  ");
		sb.append(" ) bind_user on pl.channel_code =regist_channel ");
		sb.append(" GROUP BY pl.channel_code ORDER BY pl.`channel` asc; ");

		return (List<Object[]>) dao.LoadAllSql(sb.toString(), obList.toArray());
	}

	/**
	 * 获取投资人数,投资金额
	 * 投资人数,投资金额 找出所有用户的第一笔投资,然后根据时间筛选
	 * 
	 * @param date
	 * @return
	 */
	private List<Object[]> tzRs(String date) {
		ArrayList<Object> obList = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		obList.add(date);
		obList.add(date);
		sb.append("CALL proc_all_buy_number(?,?) ");
		return (List<Object[]>) dao.LoadAllSql(sb.toString(), obList.toArray());
	}

	/**
	 * 首投人数,首投金额
	 * 
	 * @param date
	 * @return
	 */
	private List<Object[]> stRs(String date) {
		ArrayList<Object> obList = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		obList.add(date);
		obList.add(date);
		sb.append("CALL proc_first_investment(?,?) ");
		return (List<Object[]>) dao.LoadAllSql(sb.toString(), obList.toArray());
	}

	/**
	 * 复投人数,复投金额
	 * 先找出当天有投资的,然后查找之前是否有投资过,如果有,则是复投;包含当前投资 应该有2条记录(含)以上才是复投
	 * 
	 * @param date
	 * @return
	 */
	private List<Object[]> ftRs(String date) {
		ArrayList<Object> obList = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		obList.add(date);
		obList.add(date);
		sb.append("CALL proc_re_cast(?,?) ");
		return (List<Object[]>) dao.LoadAllSql(sb.toString(), obList.toArray());
	}

	/**
	 * 总投资人数,总金额
	 * 
	 * @param date
	 * @return
	 */
	private List<Object[]> ztzrs(String date) {
		ArrayList<Object> obList = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		obList.add(date);
		obList.add(date);
		sb.append("CALL proc_second_investment(?,?) ");
		return (List<Object[]>) dao.LoadAllSql(sb.toString(), obList.toArray());
	}

	/**
	 * 充值次数,充值金额
	 * 
	 * @param date
	 * @return
	 */
	private List<Object[]> czCount(String date) {
		ArrayList<Object> obList = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		sb.append(
				" SELECT pl.channel, pl.channel_code, pl.channel_name,count(distinct users_id),SUM(money)  FROM plat_channel pl LEFT JOIN    ");
		sb.append(" ( ");
		sb.append(
				" SELECT  us.id 'users_id' ,us.regist_channel 'regist_channel',chongzhi.money 'money'  FROM users us    ");
		sb.append(" right JOIN ( ");
		sb.append(" SELECT cz.users_id , sum(cz.money) 'money'  FROM cz_record cz  ");
		sb.append(" WHERE cz.`STATUS` ='1' ");
		if (!QwyUtil.isNullAndEmpty(date)) {
			sb.append("  and cz.insert_time  BETWEEN ? AND ? ");
			obList.add(date + " 00:00:00");
			obList.add(date + " 23:59:59");
		}
		sb.append(" GROUP BY cz.users_id  )chongzhi on us.id =chongzhi.users_id ");
		sb.append(" ) czje on pl.channel_code = regist_channel ");
		sb.append(" GROUP BY pl.channel_code ORDER BY pl.channel asc; ");

		return (List<Object[]>) dao.LoadAllSql(sb.toString(), obList.toArray());
	}

	/**
	 * 首次复投金额,首次复投人数<br>
	 * 
	 * @param date
	 * @return
	 */
	private List<Object[]> scftCount(String date) {
		ArrayList<Object> obList = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		obList.add(date);
		obList.add(date);
		sb.append("CALL proc_second_investment(?,?) ");
		return (List<Object[]>) dao.LoadAllSql(sb.toString(), obList.toArray());
	}
	
	/**
	 * 购买零钱罐的人数和金额
	 * 
	 * @param date
	 * @return
	 */
	private List<Object[]> lqgCount(String date) {
		ArrayList<Object> obList = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT pl.channel, pl.channel_code, pl.channel_name,count(distinct users_id),SUM(money)  FROM plat_channel pl ");
		sb.append(" LEFT JOIN  ( ");
		sb.append(" SELECT  us.id 'users_id' ,us.regist_channel 'regist_channel',chongzhi.money 'money'  FROM users us ");
		sb.append(" right JOIN ( ");
		sb.append(" SELECT cpfr.users_id 'users_id',SUM(cpfr.money) 'money' FROM coin_purse_funds_record cpfr WHERE cpfr.type = 'to' ");
		sb.append(" AND  cpfr.status = 0  ");
		if (!QwyUtil.isNullAndEmpty(date)) {
			sb.append("  AND cpfr.insert_time  BETWEEN ? AND ? ");
			obList.add(date + " 00:00:00");
			obList.add(date + " 23:59:59");
		}
		sb.append("  GROUP BY cpfr.users_id  )chongzhi on us.id =chongzhi.users_id ");
		sb.append(" ) czje on pl.channel_code = regist_channel ");
		sb.append(" GROUP BY pl.channel_code ORDER BY pl.channel asc; ");

		return (List<Object[]>) dao.LoadAllSql(sb.toString(), obList.toArray());
	}

	/**
	 * 根据日期来更新对应的运营数据【Android渠道统计汇总】
	 *
	 * @throws ParseException
	 */
	public void updateQdtjByDate(Date queryDate) throws Exception {
		String date = QwyUtil.fmyyyyMMdd.format(queryDate);
		//String date ="2017-12-12";
		// 获取运营数据
		List<Object[]> jhrsList = activityNum(date);
		List<Object[]> registList = registNum(date);
		List<Object[]> bindList = bindNum(date);
		List<Object[]> tzList = tzRs(date);
		List<Object[]> stList = stRs(date);
		List<Object[]> ftList = ftRs(date);
		List<Object[]> czList = czCount(date);
		List<Object[]> scftlist = scftCount(date);
		List<Object[]> lqglist = lqgCount(date);
		Map<String, Qdtj> qdtjMap = new HashMap<String, Qdtj>();
		
		
		// 渠道编号,渠道编码,渠道名称,零钱罐投资金额
		if (!QwyUtil.isNullAndEmpty(lqglist)) {
			for (Object[] obj : lqglist) {
				if (QwyUtil.isNullAndEmpty(obj))
					continue;
				String channelCode = isNullReturnZero(obj[1]);// 渠道编码
				Qdtj qdtj = qdtjMap.get(channelCode);
				if (QwyUtil.isNullAndEmpty(qdtj)) {
					qdtj = new Qdtj();
					qdtj.setChannel(isNullReturnZero(obj[0]));// 渠道编号
					qdtj.setChannelCode(channelCode);// 渠道编码
					qdtj.setChannelName(isNullReturnZero(obj[2]));// 渠道名称
					qdtj.setDate(QwyUtil.fmyyyyMMdd.parse(date));
					qdtj.setInsertTime(new Date());
				}
				qdtj.setLqgrs(isNullReturnZero(obj[3]));// 投资人数
				qdtj.setLqgje(isNullReturnZero(obj[4]));// 投资金额
				qdtjMap.put(channelCode, qdtj);
			}
		}
		
		
		// 渠道编号,渠道编码,渠道名称,激活次数
		if (!QwyUtil.isNullAndEmpty(jhrsList)) {
			for (Object[] obj : jhrsList) {
				if (QwyUtil.isNullAndEmpty(obj))
					continue;
				String channelCode = isNullReturnZero(obj[1]);// 渠道编码
				Qdtj qdtj = qdtjMap.get(channelCode);
				if (QwyUtil.isNullAndEmpty(qdtj)) {
					qdtj = new Qdtj();
					qdtj.setChannel(isNullReturnZero(obj[0]));// 渠道编号
					qdtj.setChannelCode(channelCode);// 渠道编码
					qdtj.setChannelName(isNullReturnZero(obj[2]));// 渠道名称
					qdtj.setDate(QwyUtil.fmyyyyMMdd.parse(date));
					qdtj.setInsertTime(new Date());
				}
				qdtj.setActivityCount(isNullReturnZero(obj[3]));// 激活人数
				qdtjMap.put(channelCode, qdtj);
			}
		}
		// 渠道编号,渠道编码,渠道名称,注册人数;
		if (!QwyUtil.isNullAndEmpty(registList)) {
			for (Object[] obj : registList) {
				if (QwyUtil.isNullAndEmpty(obj))
					continue;
				String channelCode = isNullReturnZero(obj[1]);// 渠道编码
				Qdtj qdtj = qdtjMap.get(channelCode);
				if (QwyUtil.isNullAndEmpty(qdtj)) {
					qdtj = new Qdtj();
					qdtj.setChannel(isNullReturnZero(obj[0]));// 渠道编号
					qdtj.setChannelCode(channelCode);// 渠道编码
					qdtj.setChannelName(isNullReturnZero(obj[2]));// 渠道名称
					qdtj.setDate(QwyUtil.fmyyyyMMdd.parse(date));
					qdtj.setInsertTime(new Date());
				}
				qdtj.setRegCount(isNullReturnZero(obj[3]));// 注册人数

				String jhrs = qdtj.getActivityCount();// 激活人数
				String qdzhlStrNew = "0".equals(jhrs) ? "0"
						: QwyUtil.calcNumber(qdtj.getRegCount(), jhrs, "/").toString();// 渠道转化率
				qdtj.setQdzhl(QwyUtil.calcNumber(qdzhlStrNew, 100, "*").toString());// 渠道转化率%(激活注册转化率)
				qdtjMap.put(channelCode, qdtj);
			}
		}

		// 渠道编号,渠道编码,渠道名称,绑定人数;
		if (!QwyUtil.isNullAndEmpty(bindList)) {
			for (Object[] obj : bindList) {
				if (QwyUtil.isNullAndEmpty(obj))
					continue;
				String channelCode = isNullReturnZero(obj[1]);// 渠道编码
				Qdtj qdtj = qdtjMap.get(channelCode);
				if (QwyUtil.isNullAndEmpty(qdtj)) {
					qdtj = new Qdtj();
					qdtj.setChannel(isNullReturnZero(obj[0]));// 渠道编号
					qdtj.setChannelCode(channelCode);// 渠道编码
					qdtj.setChannelName(isNullReturnZero(obj[2]));// 渠道名称
					qdtj.setDate(QwyUtil.fmyyyyMMdd.parse(date));
					qdtj.setInsertTime(new Date());
				}
				qdtj.setBindCount(isNullReturnZero(obj[3]));// 绑定人数
				String regCount = qdtj.getRegCount();// 注册用户
				String zcjhzhl = "0".equals(regCount) ? "0"
						: QwyUtil.calcNumber(isNullReturnZero(obj[3]), regCount, "/").toString();
				qdtj.setZcjhzhl(QwyUtil.calcNumber(zcjhzhl, 100, "*").toString());// 注册认证转换人数
				qdtjMap.put(channelCode, qdtj);
			}
		}

		// 渠道编号,渠道编码,渠道名称,投资人数,投资金额(本金)
		if (!QwyUtil.isNullAndEmpty(tzList)) {
			for (Object[] obj : tzList) {
				if (QwyUtil.isNullAndEmpty(obj))
					continue;
				String channelCode = isNullReturnZero(obj[1]);// 渠道编码
				Qdtj qdtj = qdtjMap.get(channelCode);
				if (QwyUtil.isNullAndEmpty(qdtj)) {
					qdtj = new Qdtj();
					qdtj.setChannel(isNullReturnZero(obj[0]));// 渠道编号
					qdtj.setChannelCode(channelCode);// 渠道编码
					qdtj.setChannelName(isNullReturnZero(obj[2]));// 渠道名称
					qdtj.setDate(QwyUtil.fmyyyyMMdd.parse(date));
					qdtj.setInsertTime(new Date());
				}
				qdtj.setTzrs(isNullReturnZero(obj[3]));// 投资人数
				qdtj.setTzje(isNullReturnZero(obj[4]));// 投资金额

				String rjtzje = "0".equals(qdtj.getTzje()) ? "0"
						: QwyUtil.calcNumber(qdtj.getTzje(), qdtj.getTzrs(), "/").toString();// 人均投资金额
				qdtj.setRjtzje(rjtzje);// 人均投资金额
				qdtj.setChannelType(!QwyUtil.isNullAndEmpty(obj[5]) ? obj[5].toString():null);//渠道标识
				qdtjMap.put(channelCode, qdtj);
			}
		}

		// 渠道编号,渠道编码,渠道名称,首投人数,首投金额(本金)
		if (!QwyUtil.isNullAndEmpty(stList)) {
			for (Object[] obj : stList) {
				if (QwyUtil.isNullAndEmpty(obj))
					continue;
				String channelCode = isNullReturnZero(obj[1]);// 渠道编码
				Qdtj qdtj = qdtjMap.get(channelCode);
				if (QwyUtil.isNullAndEmpty(qdtj)) {
					qdtj = new Qdtj();
					qdtj.setChannel(isNullReturnZero(obj[0]));// 渠道编号
					qdtj.setChannelCode(channelCode);// 渠道编码
					qdtj.setChannelName(isNullReturnZero(obj[2]));// 渠道名称
					qdtj.setDate(QwyUtil.fmyyyyMMdd.parse(date));
					qdtj.setInsertTime(new Date());
				}
				qdtj.setStrs(isNullReturnZero(obj[3]));// 首投人数
				qdtj.setStje(isNullReturnZero(obj[4]));// 首投金额
				String strs = qdtj.getStrs();// 首投人数
				String stje = qdtj.getStje();// 首投金额
				if ("0".equals(qdtj.getBindCount())) {
					qdtj.setRzstzhl("0");// 认证首投转换率
				} else {
					String qdzhlStrNew = "0".equals(strs) ? "0"
							: QwyUtil.calcNumber(strs, qdtj.getBindCount(), "/").toString();// 认证首投转换率(当日首投人数/当日绑定人数)
					qdtj.setRzstzhl(QwyUtil.calcNumber(qdzhlStrNew, 100, "*").toString());// 认证首投转换率
				}
				String rjstje = "0".equals(strs) ? "0" : QwyUtil.calcNumber(stje, strs, "/").toString();// 人均首投金额
				qdtj.setRjstje(rjstje);// 人均首投金额
				qdtj.setChannelType(!QwyUtil.isNullAndEmpty(obj[5]) ? obj[5].toString():null);//渠道标识
				qdtjMap.put(channelCode, qdtj);
			}
		}

		// 渠道编号,渠道编码,渠道名称,复投人数,复投金额(本金)
		if (!QwyUtil.isNullAndEmpty(ftList)) {
			for (Object[] obj : ftList) {
				if (QwyUtil.isNullAndEmpty(obj))
					continue;
				String channelCode = isNullReturnZero(obj[1]);// 渠道编码
				Qdtj qdtj = qdtjMap.get(channelCode);
				if (QwyUtil.isNullAndEmpty(qdtj)) {
					qdtj = new Qdtj();
					qdtj.setChannel(isNullReturnZero(obj[0]));// 渠道编号
					qdtj.setChannelCode(channelCode);// 渠道编码
					qdtj.setChannelName(isNullReturnZero(obj[2]));// 渠道名称
					qdtj.setDate(QwyUtil.fmyyyyMMdd.parse(date));
					qdtj.setInsertTime(new Date());
				}
				qdtj.setFtrs(isNullReturnZero(obj[3]));// 复投人数
				qdtj.setFtje(isNullReturnZero(obj[4]));// 复投金额
				String ftje = qdtj.getFtje();// 复投金额
				String ftrs = qdtj.getFtrs();
				String tzsr = qdtj.getTzrs();// 投资人数
				String cftzlStrNew = "0".equals(ftje) ? "0" : QwyUtil.calcNumber(qdtj.getFtrs(), tzsr, "/").toString();// 重复投资率
				qdtj.setCftzl(QwyUtil.calcNumber(cftzlStrNew, 100, "*").toString());// 重复投资率%
				if (ftrs.equals("0")) {
					String rjftje = "0";
				} else {
					String rjftje = "0".equals(tzsr) ? "0" : QwyUtil.calcNumber(ftje, ftrs, "/").toString();// 人均复投金额
					qdtj.setRjftje(rjftje);// 人均复投金额
				}
				qdtj.setChannelType(!QwyUtil.isNullAndEmpty(obj[5]) ? obj[5].toString():null);//渠道标识
				qdtjMap.put(channelCode, qdtj);
			}
		}
		// 渠道编号,渠道编码,渠道名称,首次复投金额,首次复投人数
		if (!QwyUtil.isNullAndEmpty(scftlist)) {
			for (Object[] obj : scftlist) {
				if (QwyUtil.isNullAndEmpty(obj))
					continue;
				String channelCode = isNullReturnZero(obj[1]);// 渠道编码
				Qdtj qdtj = qdtjMap.get(channelCode);
				if (QwyUtil.isNullAndEmpty(qdtj)) {
					qdtj = new Qdtj();
					qdtj.setChannel(isNullReturnZero(obj[0]));// 渠道编号
					qdtj.setChannelCode(channelCode);// 渠道编码
					qdtj.setChannelName(isNullReturnZero(obj[2]));// 渠道名称
					qdtj.setDate(QwyUtil.fmyyyyMMdd.parse(date));
					qdtj.setInsertTime(new Date());
				}
				qdtj.setXzftyh(obj[3] + "");
				qdtj.setXhftyhtzze(obj[4] + "");
				String xzftyh = qdtj.getXzftyh();// 新增复投用户数
				String ftrs = qdtj.getFtrs();// 新增复投用户投资总额
				String xzftl = "0".equals(ftrs) ? "0" : QwyUtil.calcNumber(xzftyh, ftrs, "/").toString();
				qdtj.setXzftl(QwyUtil.calcNumber(xzftl, 100, "*").toString());
				qdtj.setChannelType(!QwyUtil.isNullAndEmpty(obj[5]) ? obj[5].toString():null);//渠道标识
				qdtjMap.put(channelCode, qdtj);
			}
		}

		// 渠道编号,渠道编码,渠道名称,充值人数,充值金额(本金)
		if (!QwyUtil.isNullAndEmpty(czList)) {
			for (Object[] obj : czList) {
				if (QwyUtil.isNullAndEmpty(obj))
					continue;
				String channelCode = isNullReturnZero(obj[1]);// 渠道编码
				Qdtj qdtj = qdtjMap.get(channelCode);
				if (QwyUtil.isNullAndEmpty(qdtj)) {
					qdtj = new Qdtj();
					qdtj.setChannel(isNullReturnZero(obj[0]));// 渠道编号
					qdtj.setChannelCode(channelCode);// 渠道编码
					qdtj.setChannelName(isNullReturnZero(obj[2]));// 渠道名称
					qdtj.setInsertTime(new Date());
				}
				qdtj.setCzcount(isNullReturnZero(obj[3]));// 充值人数
				qdtj.setCzje(isNullReturnZero(obj[4]));// 充值金额
				qdtjMap.put(channelCode, qdtj);
			}
		}
		// 对运营数据进行操作;
		if (!QwyUtil.isNullAndEmpty(qdtjMap)) {
			// 先删除旧数据;
			deleteQdtjByDate(queryDate);
			Set<String> keys = qdtjMap.keySet();
			List<Qdtj> listQdtj = new ArrayList<Qdtj>();
			for (String key : keys) {
				listQdtj.add(qdtjMap.get(key));
			}
			// 添加改日期的运营数据
			if (!QwyUtil.isNullAndEmpty(listQdtj)) {
				dao.saveList(listQdtj);
			}

		}

	}

}
