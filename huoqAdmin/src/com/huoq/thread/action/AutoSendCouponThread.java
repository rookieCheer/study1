package com.huoq.thread.action;

import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import com.huoq.account.bean.UserRechargeBean;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.InterestDetails;
import com.huoq.thread.bean.AutoSendCouponThreadBean;

/**
 * 自动发放还款 奖励
 * @author wxl
 *     2017年4月7日10:00:29
 */
@Service
public class AutoSendCouponThread implements Runnable{
	private Logger log = Logger.getLogger(AutoSendCouponThread.class);

	@Resource
	private AutoSendCouponThreadBean autoSendCouponThreadBean;
	@Resource
	private UserRechargeBean userRechargeBean;
	
	@Override
	public void run() {
		
		long st = System.currentTimeMillis();
		String now = QwyUtil.fmyyyyMMdd.format(new Date());
		String stTime = now + " 00:00:00";
		String etTime = now + " 23:59:59";
		
		Date date = new Date();
		String time = QwyUtil.fmyyyyMMdd.format(date);
		try {
			log.info("进入自动发放还款理财券的线程");
			if ("2017-04-08".equals(time) || "2017-04-10".equals(time) || "2017-04-12".equals(time)) {
			
				PageUtil pageUtil = new PageUtil();
				pageUtil.setPageSize(200);
				int currentPage = 0;
				for(;;){
					
					currentPage++;
					pageUtil.setCurrentPage(currentPage);
					pageUtil = autoSendCouponThreadBean.getReturnUsers(pageUtil, stTime, etTime);
					
					List list = pageUtil.getList();
					if (!QwyUtil.isNullAndEmpty(list)) {
						
						for (int i = 0; i < list.size(); i++) {
							Long usersId = Long.parseLong(list.get(i).toString());
							if (autoSendCouponThreadBean.getCouponRecord(usersId)) {
								// 去重复后 发送理财券和红包  800 3500理财券 50元红包
								userRechargeBean.sendHongBao(usersId, 80000, QwyUtil.addDaysFromOldDate(new Date(), 30).getTime(), "0", -1, "返款奖励", null);
								userRechargeBean.sendHongBao(usersId, 350000, QwyUtil.addDaysFromOldDate(new Date(), 30).getTime(), "0", -1, "返款奖励", null);
							  //userRechargeBean.sendHongBao(usersId, 5000, QwyUtil.addDaysFromOldDate(new Date(), 30).getTime(), "2", -1, "返款奖励", null);
							}else{
								log.info("该用户已发放过本次奖励，用户ID："+usersId);
							}
						}
						log.info("第 "+currentPage+" 页发放完毕~");
						
						
					}else{
						log.info("没有还款用户~");
						break;
					}
				}
			}else{
				log.info("不是发放奖励日期~");
			}
			
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		
		long et = System.currentTimeMillis();
		log.info("处理【自动发放还款奖励】耗时: "+(et-st));
		
	}

	
	public static void main(String[] args) {
		int time = new Date().getDate();
	}
}
