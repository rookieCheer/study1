package com.huoq.common.bean;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;
import java.util.ResourceBundle;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.common.util.NoticeUrlUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.common.util.SMSNoticeUtil;
import com.huoq.login.dao.RegisterUserDAO;
import com.huoq.orm.SmsRecord;

/**
 * 发送短信
 * 
 * @author
 * 
 */
@Service
public class SMSNotifyBean {

	@Resource
	private RegisterUserDAO dao;
	private static Logger log = Logger.getLogger(SMSNotifyBean.class);
	private static ResourceBundle resbsms = ResourceBundle.getBundle("sms-notice");
	@Resource
	private SystemConfigBean systemConfigBean;
	/**
	 * 发送短信
	 * 
	 * @param mobile
	 * @param smsContent
	 * @param shortUrl
	 * @return
	 */
	public boolean sendSMSNotify(String shortUrl,String mobile,String smsContent, Object[] obj ) {
		try {
			log.info("############线程："+Thread.currentThread().getName()+"发送【"+mobile+"】短信{"+smsContent+"}");
			String smsQm = systemConfigBean.findSystemConfig().getSmsQm();
			if (StringUtils.isNotBlank(shortUrl)) {
				shortUrl = NoticeUrlUtil.getShortUrl(shortUrl);
			}
			log.info("shortUrl: "+shortUrl);
			if (null == shortUrl || "".equals(shortUrl)){
				shortUrl = "";
			}
			smsContent = new String(smsContent.getBytes("ISO-8859-1"), "UTF-8");
			smsContent = SMSNoticeUtil.smsContent(smsContent,shortUrl, obj,smsQm);
			log.info("发送短信内容："+smsContent);
			Map<String,Object> map = SMSNoticeUtil.sendNoticeCZ(mobile, "", smsContent);
			SmsRecord smsRecord = packSmsRecord(smsContent, mobile);
			
			log.info("短信返回结果: "+map.get("error").toString());
			if (map.get("error").toString().equalsIgnoreCase("0")) {
				smsRecord.setStatus("1");
				smsRecord.setSid(map.get("result").toString());
				smsRecord.setUpdateTime(new Date());
				smsRecord.setNote(map.get("message")+"");
				dao.update(smsRecord);
				return true;
			} else {
				smsRecord.setStatus("2");
				smsRecord.setUpdateTime(new Date());
				smsRecord.setNote(map.get("message")+"");
				dao.update(smsRecord);
				return false;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return false;
	}
	
	/**
	 * 获取短信记录
	 * @param smsContent
	 * @param mobile
	 * @return
	 */
	public SmsRecord packSmsRecord(String smsContent,String mobile){
		SmsRecord smsRecord=new SmsRecord();
		smsRecord.setInsertTime(new Date());
		smsRecord.setMobile(mobile);
		smsRecord.setSmsContent(smsContent);
		smsRecord.setStatus("0");
		dao.save(smsRecord);
		if(!QwyUtil.isNullAndEmpty(smsRecord.getId())){
			return (SmsRecord) dao.findById(new SmsRecord(), smsRecord.getId());
		}
		return smsRecord;
	}
	public static void main(String[] args) {
		String smsQm = "【新华金典】";
		String shortUrl = resbsms.getString("SMS_PRODUCT_FINISHDAY_URL");
		shortUrl = NoticeUrlUtil.getShortUrl(shortUrl);
		String smsContent = resbsms.getString("SMS_PRODUCT_FINISHDAY");
		try {
			smsContent = new String(smsContent.getBytes("ISO-8859-1"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		smsContent = SMSNoticeUtil.smsContent(smsContent,shortUrl, new Object[]{"2012","10","17","新华2号",200.12},smsQm);
//		new Object[]{"2012","10","17","新华2号",200.12};
		
		Map<String,Object> map = SMSNoticeUtil.sendNoticeCZ("15029035359", "", smsContent);
	}
	
}
