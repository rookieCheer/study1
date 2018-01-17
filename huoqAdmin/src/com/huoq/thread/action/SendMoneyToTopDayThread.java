package com.huoq.thread.action;

import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import com.huoq.account.bean.UserRechargeBean;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.CzRecord;
import com.huoq.thread.bean.SendMoneyToTopDayThreadBean;

/**
 * 发放新手奖励    每日最高 奖励30元现金到余额中
 * @author wxl
 *    2017年4月5日15:45:36
 *	//unused_url_deprecated废弃使用
 */
@Service
public class SendMoneyToTopDayThread implements Runnable{
	private Logger log = Logger.getLogger(SendMoneyToTopDayThread.class);
	
	@Resource 
	SendMoneyToTopDayThreadBean threadBean;
	@Resource
	UserRechargeBean userRechargeBean;
	
	@Override
	public void run() {
		
		long st = System.currentTimeMillis();
		try {
			String yestoday = QwyUtil.fmyyyyMMdd.format(QwyUtil.addDaysFromOldDate(new Date(), -1).getTime());
			String stTime = yestoday+" 00:00:00";
			String etTime = yestoday+" 23:59:59";
			
			Date stopDay = QwyUtil.fmyyyyMMddHHmmss.parse("2017-05-17 00:00:00");
			if (new Date().before(stopDay)) {
				//获取昨天首投用户
				List<Object[]> list = threadBean.getOneFirInvList(stTime,etTime);
					
				String nowSt = QwyUtil.fmyyyyMMdd.format(new Date())+" 00:00:00";
				String nowEt = QwyUtil.fmyyyyMMdd.format(new Date())+" 23:59:59";
				
				//取出首投最高的用户的ID  赠送30元现金红包
				if (!QwyUtil.isNullAndEmpty(list)) {
					Long usersId = Long.parseLong(list.get(0)[2].toString());
					//判断今天是否已经发放过改奖励
					if (threadBean.getCzRecord(usersId, nowSt, nowEt)) {
						boolean isOk = userRechargeBean.usreRecharge(usersId, 3000D, "cz","系统充值", "新手每日最高奖励");
						CzRecord czRecord = userRechargeBean.addCzRecordJL(usersId, 3000D, null, null, null, null, "新手每日最高奖励", "1", null);
						if (isOk && !QwyUtil.isNullAndEmpty(czRecord)) {
							log.info("充值成功~");
						}
					}else{
						log.info("今日已发放~");
					}
					
				}else{
					log.info("没有首投的用户");
				}	
			}else{
				log.info("活动已结束~");
			}
			
		} catch (Exception e) {
			log.info("操作异常：",e);
		}
		
		long et = System.currentTimeMillis();
		log.info("处理【新手每日最高奖励的账户】耗时: "+(et-st));
		
	}

}
