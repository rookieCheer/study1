package com.huoq.test.bean;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.account.bean.UserRechargeBean;
import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.HdUsers;
import com.huoq.orm.Product;
import com.huoq.orm.Users;
import com.huoq.test.dao.TestDAO;

@Service
public class DemoBean{
	
	private static Logger log = Logger.getLogger(DemoBean.class);
	
	@Resource
	TestDAO dao;
	@Resource
	private UserRechargeBean userRechargeBean;
	public List<Users> getTestList(){
		log.info("jinlai");
		List<Users> list = (List<Users>)dao.LoadAll("FROM Users", null);
		return list;
	}
	
	/**根据用户名查找用户;
	 * @param phone
	 * @return
	 */
	public Users getUsersByUsername(String phone){
		StringBuffer hql = new StringBuffer();
		hql.append("FROM Users us WHERE us.username = ? ");
		return (Users)dao.findJoinActive(hql.toString(), new Object[]{DESEncrypt.jiaMiUsername(phone)});
	}
	
	public boolean isExistsHDUsers(String username){
		StringBuffer hql = new StringBuffer();
		hql.append("FROM HdUsers us WHERE us.username = ? ");
		Object ob = dao.findJoinActive(hql.toString(), new Object[]{DESEncrypt.jiaMiUsername(username)});
		if(QwyUtil.isNullAndEmpty(ob)){
			return false;
		}
		return true;
	}
	
	public void createUsers(Users user){
		HdUsers hd = new HdUsers();
		hd.setHdFlagId("1");
		hd.setInsertTime(new Date());
		hd.setNote("加群领取50元投资券");
		hd.setUserId(user.getId());
		hd.setUsername(user.getUsername());
		dao.save(hd);
		
	}
	
	
	public String sendShouhu(){
		StringBuffer sb = new StringBuffer();
		sb.append("select  `users_id` ,`product_id` ,`in_money` from `investors` WHERE `investor_status` ='1' and `pay_time` BETWEEN '2017-04-28 00:00:00' and '2017-04-28 14:59:19' and `in_money` >=300000 and `product_id` IN (select id from `product` where `product_type` ='0' and `product_status` in ('0','1') and `lcqx` >=30) ORDER BY `users_id` desc, `in_money` DESC ;");
		
		List<Object[]> d = (List<Object[]>)dao.LoadAllSql(sb.toString(), null);
		
		if(!QwyUtil.isNullAndEmpty(d)){
			log.info("------------开始补发红包");
			int i = 0;
			for (Object[] obj : d) {
				try {
					long usersId = Long.parseLong(obj[0].toString());
					String proId = obj[1].toString();
					double inMoney = Double.parseDouble(obj[2].toString());
					Product pro = (Product)dao.load(new Product(), proId);
					shouhu2(usersId, inMoney, pro);
					log.info((++i)+"人补发成功");
				} catch (NumberFormatException e) {
					log.error("操作异常: ",e);
				}
			}
		}
		return null;
	}
	
	public void shouhu2(long usersId,double money,Product pro){
		try {
			/** * 枫叶日活动  GDP 活动时间 2017/4/28 00:00:00—— 4/28 23:59:59   start  */
			String time = "2017-04-28";
			String stTime = "2017-04-28 00:00:00";
			String etTime = "2017-04-28 23:59:59";
//		String invTime = QwyUtil.fmyyyyMMdd.format(inv.getInsertTime());
			if (time.equals(QwyUtil.fmyyyyMMdd.format(new Date()))) {
				if ("0".equals(pro.getProductType())) {
					if (!"0".equals(pro.getQxType())) {
						if(money >= 1500000){
							List list88 = getCoupon(usersId, "枫叶日", stTime, etTime, null, "2","8800");
							if (QwyUtil.isNullAndEmpty(list88)) {
								userRechargeBean.sendHongBao(usersId, 8800D, QwyUtil.addDaysFromOldDate(new Date(),30).getTime(), "2", -1, "枫叶日活动","30天及以上常规产品");
							}else{
								List list30 = getCoupon(usersId, "枫叶日", stTime, etTime, null, "2","3000");
								if (QwyUtil.isNullAndEmpty(list30)) {
									userRechargeBean.sendHongBao(usersId, 3000D, QwyUtil.addDaysFromOldDate(new Date(),30).getTime(), "2", -1, "枫叶日活动","30天及以上常规产品");
								}else{
									List list10 = getCoupon(usersId, "枫叶日", stTime, etTime, null, "2","1000");
									if (QwyUtil.isNullAndEmpty(list10)) {
										userRechargeBean.sendHongBao(usersId, 1000D, QwyUtil.addDaysFromOldDate(new Date(),30).getTime(), "2", -1, "枫叶日活动","30天及以上常规产品");
									}
								}
							}
						}else if(money >= 800000) {
							List list30 = getCoupon(usersId, "枫叶日", stTime, etTime, null, "2","3000");
							if (QwyUtil.isNullAndEmpty(list30)) {
								userRechargeBean.sendHongBao(usersId, 3000D, QwyUtil.addDaysFromOldDate(new Date(),30).getTime(), "2", -1, "枫叶日活动","30天及以上常规产品");
							}else{
								List list10 = getCoupon(usersId, "枫叶日", stTime, etTime, null, "2","1000");
								if (QwyUtil.isNullAndEmpty(list10)) {
									userRechargeBean.sendHongBao(usersId, 1000D, QwyUtil.addDaysFromOldDate(new Date(),30).getTime(), "2", -1, "枫叶日活动","30天及以上常规产品");
								}
							}
							
						}else if(money >= 300000) {
							List list10 = getCoupon(usersId, "枫叶日", stTime, etTime, null, "2","1000");
							if (QwyUtil.isNullAndEmpty(list10)) {
								userRechargeBean.sendHongBao(usersId, 1000D, QwyUtil.addDaysFromOldDate(new Date(),30).getTime(), "2", -1, "枫叶日活动","30天及以上常规产品");
							}
						}
					}
					
				}
			}
			/** * 枫叶日活动  GDP 活动时间 2017/4/28 00:00:00—— 4/28 23:59:59    end   */
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
	}
	
	
	/**
	 *  时间段内是否领取某个类型的券
	 * @param usersId	用户ID
	 * @param note      备注
	 * @param stTime    时间段
	 * @param etTime
	 * @param status	状态
	 * @param type		类型		
	 * @return
	 */
	public List getCoupon(Long usersId,String note,String stTime,String etTime,String status,String type,String initMoney){
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * FROM coupon WHERE 1=1");
		if (!QwyUtil.isNullAndEmpty(usersId)) {
			sql.append(" AND users_id ="+usersId);
		}
		
		if (!QwyUtil.isNullAndEmpty(type)) {
			sql.append(" AND type = '"+type+"'");
		}
		
		if (!QwyUtil.isNullAndEmpty(stTime)) {
			sql.append(" AND insert_time BETWEEN '"+stTime+"' AND '"+etTime+"'");
		}
		
		if (!QwyUtil.isNullAndEmpty(note)) {
			sql.append(" AND note LIKE '%"+note+"%'");
		}
		
		if (!QwyUtil.isNullAndEmpty(status)) {
			sql.append(" AND STATUS IN ("+status+")");
		}
		
		if (!QwyUtil.isNullAndEmpty(initMoney)) {
			sql.append(" AND init_money = "+initMoney);
		}
		
		List list = dao.LoadAllSql(sql.toString(), null);
		if(!QwyUtil.isNullAndEmpty(list)){
			return list;
		}
		
		return null;
	}
}
