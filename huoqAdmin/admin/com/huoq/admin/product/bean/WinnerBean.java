package com.huoq.admin.product.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.account.bean.UserRechargeBean;
import com.huoq.common.dao.ObjectDAO;
import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.CzRecord;
import com.huoq.orm.Investors;
import com.huoq.orm.ProductAccount;
import com.huoq.orm.Winner;
@Service
public class WinnerBean {
	@Resource(name="objectDAO")
	private ObjectDAO dao;
	@Resource
	private UserRechargeBean userRechargeBean;
	
	private static Logger log = Logger.getLogger(WinnerBean.class); 
	
	/**
	 * 将数据转换为DateMoney
	 * @throws ParseException 
	 */
	
	
	private List<Winner> toDateMoney(List<Object [] > list) throws ParseException{
		List<Winner> platInverstors=new ArrayList<Winner>();
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(!QwyUtil.isNullAndEmpty(list)){
			for (Object [] object : list) {
				Winner winner=new Winner();
				winner.setUserName(object[0]+"");
				winner.setInsertTime(sdf.parse( object[1].toString()));
				winner.setPrizeName(object[2]==null?"":object[2]+"");
				platInverstors.add(winner);
			}
		}
		return platInverstors;
	}

	@SuppressWarnings("unchecked")
	public PageUtil<Winner> loadWinner(String name,String insertTime,PageUtil pageUtil) {
		try {
		ArrayList<Object> ob = new ArrayList<Object>();
		StringBuffer buff = new StringBuffer();
		buff.append("SELECT fr.user_name as user_name, fr.insert_time  as insert_time , p.prize_name as prize_name FROM winner fr left join prize p on p.id =fr.prize_id    WHERE 1=1 ");
		if(!QwyUtil.isNullAndEmpty(name)){
			buff.append("AND fr.user_name = ? ");
			//hql.append(" AND ins.product.title like '%"+productTitle+"%' ");
			ob.add(DESEncrypt.jiaMiUsername(name));
		}
		if(!QwyUtil.isNullAndEmpty(insertTime)){
			String [] time=QwyUtil.splitTime(insertTime);
			if(time.length > 1)
			{
				buff.append(" AND fr.insert_time >= ? ");
				ob.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 00:00:00"));
				buff.append(" AND fr.insert_time <= ? ");
				ob.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1]+" 23:59:59"));
			}
			else{

				buff.append(" AND fr.insert_time >= ? ");
				ob.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 00:00:00"));
				buff.append(" AND fr.insert_time <= ? ");
				ob.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 23:59:59"));
			}
			

		
		}
		buff.append(" ORDER BY DATE_FORMAT( fr.insert_time, '%Y-%m-%d' ) DESC ");
		
		StringBuffer bufferCount=new StringBuffer();
		bufferCount.append(" SELECT COUNT(*)  ");
		bufferCount.append(" FROM (");
		bufferCount.append(buff);
		bufferCount.append(") t");
		//buff.append("ORDER BY fr.insert_time DESC ");
		 pageUtil=dao.getBySqlAndSqlCount(pageUtil, buff.toString(), bufferCount.toString(), ob.toArray());
		
		 List<Winner> platUsers=toDateMoney(pageUtil.getList());
			pageUtil.setList(platUsers);
			return pageUtil;
	} catch (Exception e) {
		log.error("操作异常: ",e);
	}
		return null;
		}
	
	/**
	 * 根据奖品ID 统计每个奖品中奖的次数  
	 * @param prizeId  奖品ID
	 * @return
	 */
	public List getWinnerCount(){
		try {
			
			StringBuffer hql = new StringBuffer();
			
			hql.append(" SELECT (SELECT prize_name FROM prize WHERE id = prize_id),COUNT(*),prize_id FROM winner GROUP BY prize_id ");
			
			List list = dao.LoadAllSql(hql.toString(),null);
			
			if (!QwyUtil.isNullAndEmpty(list)) {
				return list;
			}
			
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		
		return null;
	}
	
	/**
	 * 得到奖品ID及奖品名字
	 * @return
	 */
	public List getPrizeId(String type){
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("SELECT id,prize_name FROM prize where 1=1");
			if (!QwyUtil.isNullAndEmpty(type)) {
				hql.append(" AND type = '"+type+"'");
			}
			List list =dao.LoadAllSql(hql.toString(),null);
			
			if (!QwyUtil.isNullAndEmpty(list)) {
				return list;
			}
			
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return null;
	}
	
	/**
	 * 分页获取  中奖信息
	 * @param pageUtil
	 * @param username  解密的用户名 sql 中加密
	 * @param prizeId   奖品ID
	 * @param stTime    活动开始时间  字符串类型
	 * @param etTime    活动结束时间  字符串类型
	 * @param type      活动类型  1挖宝活动   2 六月热活动  与prize.type 一致
	 * @return
	 */
	public PageUtil<Object[]> getWinnerList(PageUtil pageUtil,String username,Long prizeId,String stTime,String etTime,String type,String ltType,String insertTime){
		try {
			StringBuffer sql = new StringBuffer();
			
			sql.append(" SELECT * FROM (");
			sql.append(" SELECT w.user_name,(SELECT real_name FROM users_info WHERE users_id = w.users_id)uname,");
//			sql.append(" (SELECT SUM(copies) FROM investors WHERE investor_status IN ('1','2','3') AND users_id = w.users_id ");
//			sql.append(" AND pay_time BETWEEN '"+stTime+"' AND '"+etTime+"'),");
//			sql.append(" (SELECT free_num FROM lottery_times WHERE users_id = w.users_id AND type = '"+ltType+"' GROUP BY users_id)lt,");
//			sql.append(" (SELECT COUNT(*) FROM lottery_record WHERE type = '1' AND users_id = w.users_id");
//			sql.append(" AND insert_time BETWEEN '"+stTime+"' AND '"+etTime+"' GROUP BY users_id)lr,");
			sql.append(" note,a.contract_name,a.phone,a.address,a.zip_code,w.users_id,prize_id,insert_time,w.type");
			sql.append(" FROM winner w LEFT JOIN ");
			sql.append(" (SELECT users_id, contract_name,phone,address,address_detail,zip_code FROM m_users_address");
			sql.append(" WHERE STATUS = 0 AND update_time BETWEEN '"+stTime+"' AND '"+etTime+"' GROUP BY users_id)a");
			sql.append(" ON w.users_id = a.users_id )t WHERE 1 = 1");
			if (!QwyUtil.isNullAndEmpty(username)) {
				sql.append(" AND user_name = '"+DESEncrypt.jiaMiUsername(username)+"'");
			}
			if (!QwyUtil.isNullAndEmpty(prizeId)) {
				sql.append(" AND prize_id = '"+prizeId+"'");
			}
			if (!QwyUtil.isNullAndEmpty(type)) {
				sql.append(" AND type = '"+type+"'");
			}
			
			if (!QwyUtil.isNullAndEmpty(insertTime)) {
				String st = "";
				String et = "";
				String[] times = QwyUtil.splitString(insertTime, "-");
				if (times.length > 1) {
					st = QwyUtil.fmyyyyMMddHHmmss.format(QwyUtil.fmMMddyyyyHHmmss.parse(times[0]+" 00:00:00"));
					et = QwyUtil.fmyyyyMMddHHmmss.format(QwyUtil.fmMMddyyyyHHmmss.parse(times[1]+" 23:59:59"));
				}else{
					st = QwyUtil.fmyyyyMMddHHmmss.format(QwyUtil.fmMMddyyyyHHmmss.parse(times[0]+" 00:00:00"));
					et = QwyUtil.fmyyyyMMddHHmmss.format(QwyUtil.fmMMddyyyyHHmmss.parse(times[0]+" 23:59:59"));
				}
				
				sql.append(" AND insert_time BETWEEN '"+st+"' AND '"+et+"'");
			}
			
			sql.append(" ORDER BY insert_time DESC");
			
			StringBuffer sqlCount = new StringBuffer();
			sqlCount.append(" SELECT COUNT(*) FROM (");
			sqlCount.append(sql);
			sqlCount.append(")t");
			
			return dao.getBySqlAndSqlCount(pageUtil, sql.toString(), sqlCount.toString(), null);
			
		} catch (Exception e) {
			log.error("操作异常",e);
		}
		return null;
	}
	
	/**
	 *  分页获取抽奖记录表
	 * @param pageUtil
	 * @param username  解密的用户名  sql中加密
	 * @param type
	 * @return
	 */
	public PageUtil<Object[]> getLotteryRecord(PageUtil<Object[]> pageUtil,String username,String type,String stTime,String etTime){
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT * FROM (");
		sql.append(" SELECT (SELECT phone FROM users_info WHERE users_id = lr.users_id)uname,");
		sql.append(" (SELECT real_name FROM users_info WHERE users_id = lr.users_id),lr.insert_time,lr.note,type ");
		sql.append(" FROM lottery_record lr )t");
		sql.append(" WHERE 1 =1");
		if (!QwyUtil.isNullAndEmpty(type)) {
			sql.append(" AND type = '"+type+"'");
		}
		
		if (!QwyUtil.isNullAndEmpty(username)) {
			sql.append(" AND uname = '"+DESEncrypt.jiaMiUsername(username)+"'");
		}
		
		if (!QwyUtil.isNullAndEmpty(stTime)) {
			sql.append(" AND insert_time BETWEEN '"+stTime+"' AND '"+etTime+"'");
		}
		
		sql.append(" ORDER BY insert_time DESC");
		
		StringBuffer sqlCount = new StringBuffer();
		sqlCount.append("SELECT COUNT(*) FROM (");
		sqlCount.append(sql);
		sqlCount.append(" )a");
		
		return dao.getBySqlAndSqlCount(pageUtil, sql.toString(), sqlCount.toString(), null);
	}
	
	/**
	 * 获取 六月热 活动数据
	 * @param pageUtil  分页对象
	 * @param username  解密后的用户名
	 * @param stTime 活动开始时间 string类型
	 * @param etTime
	 * @param insertTime   页面输入的查询数据
	 * @return
	 */
	public PageUtil<Investors> getHotDate(PageUtil<Object[]> pageUtil,String username,String stTime,String etTime,String insertTime) {
		try {
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT * FROM (");
			sql.append(" SELECT (SELECT phone FROM users_info WHERE users_id = inv.users_id)uname,");
			sql.append(" (SELECT real_name FROM users_info WHERE users_id = inv.users_id)realname,");
			sql.append(" (SELECT title FROM product WHERE id = product_id),");
			sql.append(" in_money*0.01,pay_time,insert_time,");
			sql.append(" (SELECT count(*) FROM lottery_record WHERE type= '0' AND users_id = inv.users_id");
			sql.append(" AND insert_time BETWEEN '"+stTime+"' AND '"+etTime+"' GROUP BY users_id)lt,users_id");
			sql.append(" ,(SELECT SUM(money)*0.01 FROM cz_record WHERE users_id = inv.users_id ");
			sql.append(" AND status ='1' AND insert_time BETWEEN '"+stTime+"' AND '"+etTime+"' AND note LIKE '%六月%')");
			sql.append(" FROM investors inv");
			sql.append(" WHERE investor_status IN ('1','2','3')");
			sql.append(" AND pay_time BETWEEN '"+stTime+"' AND '"+etTime+"'");
			sql.append(" ORDER BY pay_time DESC");
			sql.append(" )a WHERE 1=1");
			
			if (!QwyUtil.isNullAndEmpty(username)) {
				sql.append(" AND uname = '"+DESEncrypt.jiaMiUsername(username)+"'");
			}
			
			if (!QwyUtil.isNullAndEmpty(insertTime)) {
				String st = "";
				String et = "";
				String[] times = QwyUtil.splitString(insertTime, "-");
				if (times.length > 1) {
					st = QwyUtil.fmyyyyMMddHHmmss.format(QwyUtil.fmMMddyyyyHHmmss.parse(times[0]+" 00:00:00"));
					et = QwyUtil.fmyyyyMMddHHmmss.format(QwyUtil.fmMMddyyyyHHmmss.parse(times[1]+" 23:59:59"));
				}else{
					st = QwyUtil.fmyyyyMMddHHmmss.format(QwyUtil.fmMMddyyyyHHmmss.parse(times[0]+" 00:00:00"));
					et = QwyUtil.fmyyyyMMddHHmmss.format(QwyUtil.fmMMddyyyyHHmmss.parse(times[0]+" 23:59:59"));
				}
				
				sql.append(" AND pay_time BETWEEN '"+st+"' AND '"+et+"'");
			}
			
			
			StringBuffer sqlCount = new StringBuffer();
			sqlCount.append("SELECT COUNT(*) FROM (");
			sqlCount.append(sql);
			sqlCount.append(" )t");
			
			pageUtil = dao.getBySqlAndSqlCount(pageUtil, sql.toString(), sqlCount.toString(), null);
			
			return returnInv(pageUtil);
			
		} catch (Exception e) {
			log.info(e);
		}
		
		return null;
	}
	
	
	/**
	 * 获取奖励金额
	 * @param stTime
	 * @param etTime
	 * @return
	 */
	public Map<Long, CzRecord> findJlMoney(String stTime,String etTime){
		
		Map<Long, CzRecord> map=new HashMap<Long, CzRecord>();
		StringBuffer buffer = new StringBuffer();
		buffer.append("SELECT SUM(money)*0.01,users_id FROM cz_record");
		buffer.append(" WHERE status = '1' AND insert_time BETWEEN '"+stTime+"' AND '"+etTime+"'");
		buffer.append(" AND note LIKE '%六月%'");
		buffer.append(" GROUP BY users_id");
		
		List<Object []> list=dao.LoadAllSql(buffer.toString(), null);
		if(!QwyUtil.isNullAndEmpty(list)){
			for (Object [] objects : list) {
				CzRecord czRecord = new CzRecord();
				czRecord.setMoney(Double.parseDouble(objects[0]+""));
				map.put(Long.parseLong(objects[1].toString()),czRecord );
			}
		}
		return map;
	}

	public int getInvCount(long usersId,Date time){
		StringBuffer sql = new StringBuffer();
		List<Object> ob = new ArrayList<Object>();
		sql.append(" SELECT * FROM investors WHERE 1 = 1");
		if (!QwyUtil.isNullAndEmpty(usersId)) {
			sql.append(" AND users_id = ?");
			ob.add(usersId);
		}
		
		if (!QwyUtil.isNullAndEmpty(time)) {
			sql.append(" AND pay_time <= ?");
			ob.add(time);
		}
		
		List list = dao.LoadAllSql(sql.toString(), ob.toArray());
		
//		Object object = dao.getSqlCount(sql.toString(), ob.toArray());
//		int num = Integer.parseInt(object.toString());
		
		return list.size();
	}
	
	/**
	 * 将数据封装成投资实体对象
	 * @param pageUtil
	 * @return
	 */
	public PageUtil<Investors> returnInv(PageUtil<Object[]> pageUtil){
		try {
			
			if (QwyUtil.isNullAndEmpty(pageUtil) || QwyUtil.isNullAndEmpty(pageUtil.getList())) {
				return null;
			}
			List<Investors> invList = new ArrayList<Investors>();
			PageUtil<Investors> invPage = new PageUtil<Investors>();
			for(Object[] objects : pageUtil.getList()){
				Investors investors = new Investors();
				investors.setUsersId(Long.parseLong(objects[7]+""));
				investors.setApiVersion(objects[0]+""); //用户名
				investors.setInvestorType(objects[1]+""); //真实姓名
				investors.setProductId(objects[2]+"");    //产品名称
				investors.setInMoney(Double.parseDouble(objects[3]+"")); //投资金额
				investors.setPayTime(QwyUtil.fmyyyyMMddHHmmss.parse(objects[4]+"")); //支付时间
				investors.setInsertTime(QwyUtil.fmyyyyMMddHHmmss.parse(objects[5]+""));
				investors.setCopies(QwyUtil.isNullAndEmpty(objects[6])?null:Long.parseLong(objects[6]+"")); //次数
				invList.add(investors);
			}
			
			//设置属性  pagesize 设置在前 否则会有一个除0报错
			invPage.setList(invList);
			invPage.setCurrentPage(pageUtil.getCurrentPage());
			invPage.setPageSize(pageUtil.getPageSize());
			invPage.setCount(QwyUtil.isNullAndEmpty(pageUtil.getCount())?0:pageUtil.getCount());
			invPage.setListCount(pageUtil.getListCount());
			invPage.setPageCount(pageUtil.getPageCount());
			
			invPage.setPageUrl(pageUtil.getPageUrl());
			return invPage;
			
		} catch (Exception e) {
			log.info(e);
		}
		
		return null;
	}
	
}
