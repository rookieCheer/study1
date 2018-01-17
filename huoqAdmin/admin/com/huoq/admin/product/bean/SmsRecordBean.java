package com.huoq.admin.product.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.SMSUtil;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.admin.product.dao.SmsRecordDAO;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.SmsRecord;
	
/**
 * @author 王雪林
 * @createTime 2016/11/1 下午 17:01
 */
@Service
public class SmsRecordBean {
	@Resource
    SmsRecordDAO dao;
	private static Logger log = Logger.getLogger(InvestorsBean.class);
	public String addSmsRecord(String mobileString,String smsContent,String status,Long usersAdminId){
		SmsRecord smsRecord=new SmsRecord();
		smsRecord.setInsertTime(new Date());
		smsRecord.setMobile(mobileString);
		smsRecord.setSmsContent(smsContent);
		smsRecord.setUsersAdminId(usersAdminId);
		if("0".equals(status)){//短信接口返回值为0，表示成功，为1表示失败
			smsRecord.setStatus(String.valueOf(1L)) ;//状态：0  未发送   1 已发送   2  发送失败
		}
		if("1".equals(status)){
			smsRecord.setStatus(String.valueOf(2L)) ;
		}
				
		return dao.saveAndReturnId(smsRecord);
		
	}
	

	/**
	 * 加载 短信列表 
	 * @param mobile 手机号码
	 * @param insertTime   时间
	 * @param pageUtil     分页对象
	 * @return
	 */
	public  PageUtil<SmsRecord> loadSmsRecord(String mobile,String insertTime,PageUtil pageUtil){
		
		try {
			
			ArrayList<Object> ob = new ArrayList<Object>();
			StringBuffer sb = new StringBuffer();

			sb.append("FROM SmsRecord sms WHERE 1 = 1 ");
			if (!QwyUtil.isNullAndEmpty(mobile)) {
				sb.append("AND sms.mobile = ? ");
				ob.add(mobile);
			}
			
			if (!QwyUtil.isNullAndEmpty(insertTime)) {
				String[] time = QwyUtil.splitTime(insertTime);
				if (time.length > 1) {
					sb.append(" AND sms.insertTime >= ? ");
					ob.add(QwyUtil.fmMMddyyyy.parse(time[0] + " 00:00:00"));
					sb.append(" AND sms.insertTime <= ? ");
					ob.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
				} else {
					sb.append(" AND sms.insertTime >= ? ");
					ob.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
					sb.append(" AND sms.insertTime <= ? ");
					ob.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
				}
				
			}
			
			sb.append("ORDER BY insertTime DESC");
			
			pageUtil = dao.getPage(pageUtil,sb.toString(),ob.toArray());
			
			List<SmsRecord> list = pageUtil.getList();
			
			//将数据库中的数字更换为 字符 0 未发送  1 已发送  2 发送失败
			for (int i = 0; i < list.size(); i++) {
				SmsRecord sms = list.get(i);
				if (sms.getStatus().equals("0")) {
					sms.setStatus("未发送");
				}else if(sms.getStatus().equals("1")){
					sms.setStatus("已发送");
				}else if(sms.getStatus().equals("2")){
					sms.setStatus("发送失败");
				}
			}
			
			pageUtil.setList(list);
			
			return pageUtil;
			
		} catch (Exception e) {
			log.error("操作异常: ", e);
		}
		
		return null;
		
	}

	public String listToString(List<String> list) {
		StringBuffer sb = new StringBuffer();
		String result = "";
		for (String str : list) {
			sb.append(str + ",");
		}
		result = sb.toString();
		result = result.substring(0, result.length() - 1);
		return result;
	}


	public List<String> findRegistUsers(){
		List<String> mobiles = new ArrayList<>();
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT us.username FROM users us WHERE us.id >12192 ");
		List<Object> list = dao.LoadAllSql(sql.toString(),null);
		if(!QwyUtil.isNullAndEmpty(list)){
			for (Object obj : list){
				if(!QwyUtil.isNullAndEmpty(obj)){
					mobiles.add(DESEncrypt.jieMiUsername(obj.toString()));
				}
			}
		}
		return mobiles;
	}


	/**
	 *
	 *  发送短信给枫叶注册用户  1月25日早上9点
	 * 分批发送，每批200名用户
	 * @return
	 */
	public boolean sendSMStoHuoQRegisitUsers_1_25_9() {
		boolean result = false;
		try {
			List<String> mobiles = findRegistUsers();
			String newStr = "";
			int total = mobiles.size();
			int init = 200;
			int times = total / init;
			if (total % init != 0) {
				times += 1;
			}
			List<String> sb = new ArrayList<>();
			log.info("需要发短信用户: "+mobiles.size()+"人;");
			for (int i = 0; i < times; i++) {
				if (mobiles.size() < init) {
					init = mobiles.size();
				}
				for (int j = 0; j < init; j++) {
					if (mobiles.get(j) == null) {
						break;
					}
					sb.add(mobiles.get(j));
				}
				newStr = listToString(sb);
				String smsContent = "家里父母盼了一年，只为那一顿团圆的饺子。新华金典理财温馨提示，春运期间注意安全，一路平安。";
				String content = "【枫叶网】" + smsContent;
				Map<String, Object> map = SMSUtil.sendYzm2(newStr, null, content);
				//Map<String, Object> map = null;
				if (!QwyUtil.isNullAndEmpty(map)) {
					addSmsRecord(newStr, content, map.get("error").toString(), 0l);
					result = true;
					log.info("第 "+(i+1)+"次成功;");
				} else {
					log.info("第 "+(i+1)+"次失败;");
				}
				mobiles.removeAll(sb);
				sb.clear();
			}
			log.info("发送完毕");
		} catch (Exception e) {
			log.error("操作异常: ",e);
			return result;
		}
		return result;
	}


	/**
	 *
	 *  发送短信给枫叶注册用户  1月28日早上8点
	 * 分批发送，每批200名用户
	 * @return
	 */
	public boolean sendSMStoHuoQRegisitUsers_1_28_8() {
		boolean result = false;
		try {
			List<String> mobiles = findRegistUsers();
			String newStr = "";
			int total = mobiles.size();
			int init = 200;
			int times = total / init;
			if (total % init != 0) {
				times += 1;
			}
			List<String> sb = new ArrayList<>();
			log.info("需要发短信用户: "+mobiles.size()+"人;");
			for (int i = 0; i < times; i++) {
				if (mobiles.size() < init) {
					init = mobiles.size();
				}
				for (int j = 0; j < init; j++) {
					if (mobiles.get(j) == null) {
						break;
					}
					sb.add(mobiles.get(j));
				}
				newStr = listToString(sb);
				String smsContent = "新春第一天，没有浮夸的辞藻，只有真诚的祝福~新华金典理财祝您以及家人鸡年一鸣惊人，财源滚滚，好运连连。";
				String content = "【枫叶网】" + smsContent;
				Map<String, Object> map = SMSUtil.sendYzm2(newStr, null, content);
				//Map<String, Object> map = null;
				if (!QwyUtil.isNullAndEmpty(map)) {
					addSmsRecord(newStr, content, map.get("error").toString(), 0l);
					result = true;
					log.info("第 "+(i+1)+"次成功;");
				} else {
					log.info("第 "+(i+1)+"次失败;");
				}
				mobiles.removeAll(sb);
				sb.clear();
			}
			log.info("发送完毕");
		} catch (Exception e) {
			log.error("操作异常: ",e);
			return result;
		}
		return result;
	}


	/**
	 *
	 *  发送短信给枫叶注册用户  2月3日晚上点
	 * 分批发送，每批200名用户
	 * @return
	 */
	public boolean sendSMStoHuoQRegisitUsers_2_3_21() {
		boolean result = false;
		try {
			List<String> mobiles = findRegistUsers();
			String newStr = "";
			int total = mobiles.size();
			int init = 200;
			int times = total / init;
			if (total % init != 0) {
				times += 1;
			}
			List<String> sb = new ArrayList<>();
			log.info("需要发短信用户: "+mobiles.size()+"人;");
			for (int i = 0; i < times; i++) {
				if (mobiles.size() < init) {
					init = mobiles.size();
				}
				for (int j = 0; j < init; j++) {
					if (mobiles.get(j) == null) {
						break;
					}
					sb.add(mobiles.get(j));
				}
				newStr = listToString(sb);
				String smsContent = "和爸爸聊不完的家常，吃不够的妈妈的手艺，都在春节过后成为一年的回忆，枫叶温馨提示收拾好行囊，重新起航当然不要忘记多多投资哦~";
				String content = "【枫叶网】" + smsContent;
				//Map<String, Object> map = null;
				Map<String, Object> map = SMSUtil.sendYzm2(newStr, null, content);
				if (!QwyUtil.isNullAndEmpty(map)) {
					addSmsRecord(newStr, content, map.get("error").toString(), 0l);
					result = true;
					log.info("第 "+(i+1)+"次成功;");
				} else {
					log.info("第 "+(i+1)+"次失败;");
				}
				mobiles.removeAll(sb);
				sb.clear();
			}
			log.info("发送完毕");
		} catch (Exception e) {
			log.error("操作异常: ",e);
			return result;
		}
		return result;
	}
	
	/**
	 * 获取平台所有用户名
	 * @return
	 */
	public List<String> getUsername(){
		StringBuffer sql = new StringBuffer();
		List<String> mobileList = new ArrayList<String>();
		sql.append("SELECT username FROM users");
		
		List<Object> list = dao.LoadAllSql(sql.toString(), null);
		if (!QwyUtil.isNullAndEmpty(list)) {
			for (Object obj : list){
				if(!QwyUtil.isNullAndEmpty(obj)){
					mobileList.add(DESEncrypt.jieMiUsername(obj.toString()));
				}
			}
			return mobileList;
		}
		return null;
	}
	
	
	/**
	 * 获取绑卡用户
	 * @return
	 */
	public List<String> getUsernameBind(){
		StringBuffer sql = new StringBuffer();
		List<String> mobileList = new ArrayList<String>();
		sql.append(" SELECT phone FROM users_info WHERE is_bind_bank = 1");
		
		List<Object> list = dao.LoadAllSql(sql.toString(), null);
		if (!QwyUtil.isNullAndEmpty(list)) {
			for (Object obj : list){
				if(!QwyUtil.isNullAndEmpty(obj)){
					mobileList.add(DESEncrypt.jieMiUsername(obj.toString()));
				}
			}
			return mobileList;
		}
		
		return null;
	}
	
	
	/**
	 * 获取绑卡未投资 
	 * @return
	 */
	public List<String> getUsernameBindedUnInv(){
		StringBuffer sql = new StringBuffer();
		List<String> mobileList = new ArrayList<String>();
		sql.append(" SELECT phone FROM users_info");
		sql.append(" WHERE is_bind_bank = '1'");
		sql.append(" AND users_id NOT IN");
		sql.append(" (SELECT users_id FROM investors WHERE investor_status IN ('1','2','3') GROUP BY users_id)");
		
		List<Object> list = dao.LoadAllSql(sql.toString(), null);
		if (!QwyUtil.isNullAndEmpty(list)) {
			for (Object obj : list){
				if(!QwyUtil.isNullAndEmpty(obj)){
					mobileList.add(DESEncrypt.jieMiUsername(obj.toString()));
				}
			}
			return mobileList;
		}
		
		return null;
	}
	
	/**
	 * 获取投资用户的用户名
	 * @return
	 */
	public List<String> getUsernameInv(){
		StringBuffer sql = new StringBuffer();
		List<String> mobileList = new ArrayList<String>();
		sql.append("SELECT username FROM users");
		sql.append(" WHERE id IN ");
		sql.append(" (SELECT users_id FROM investors WHERE investor_status IN ('1','2','3') GROUP BY users_id )");
		
		List<Object> list = dao.LoadAllSql(sql.toString(), null);
		if (!QwyUtil.isNullAndEmpty(list)) {
			for (Object obj : list){
				if(!QwyUtil.isNullAndEmpty(obj)){
					mobileList.add(DESEncrypt.jieMiUsername(obj.toString()));
				}
			}
			return mobileList;
		}
		
		return null;
	}



}
