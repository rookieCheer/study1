package com.huoq.thread.action;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.account.bean.InvestorsRecordBean;
import com.huoq.account.bean.UserRechargeBean;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.CzRecord;
import com.huoq.thread.bean.SendJLThreadBean;

/**
 * 发送奖励线程
 * @author admin
 *
 */
@Service(value="senJlThread")
public class SendJLThread implements Runnable {
	
	Logger log = Logger.getLogger(SendJLThread.class);
	@Resource
	private InvestorsRecordBean investorsRecordBean;
	@Resource
	private SendJLThreadBean sendJLThreadBean;
	@Resource
	private UserRechargeBean userRechargeBean;

	@Override
	public void run() {
		
		long st = System.currentTimeMillis();
		log.info("进入后台线程-------发送奖励----");
		String note = "存管活动"; //某个活动奖励备注
		String stTime = "2017-5-17 00:00:00"; //活动时间
		String etTime = "2017-5-19 23:59:59"; 
		try {
			PageUtil pageUtil = new PageUtil();
			pageUtil.setPageSize(200);
			int currentPage = 0;
			for(;;){
				currentPage++;
				pageUtil.setCurrentPage(currentPage);
				
				//投资总额(元)
				Object sum = investorsRecordBean.getSumInvCopies(null, stTime, etTime, true, "1", null);
				Double sumInv = QwyUtil.calcNumber(sum, 1, "*").doubleValue();
				
				// 活动期间内  投资达标的用户
				pageUtil = investorsRecordBean.getInvList(pageUtil,null, stTime, etTime, false, "'0','4'", "2", 10000D);
				List list = pageUtil.getList();
				if (!QwyUtil.isNullAndEmpty(list)) {
					for (int i = 0; i < list.size(); i++) {
						Object[] ob = (Object[]) list.get(i);
						Double sumInvSelf = QwyUtil.calcNumber(ob[0], 1, "*").doubleValue(); //用户投资总额(元)
						Long usersId = Long.parseLong(ob[1].toString());
						//奖励金额
						Double money = sendJLThreadBean.getJLMoney(sumInv, sumInvSelf);
						
						//发送奖励
						if (money > 0D) {
							if (userRechargeBean.getCzRecord(usersId, null, null, note)) {
								boolean isOk = userRechargeBean.usreRecharge(usersId, money, "cz", "系统充值", note);
								CzRecord czRecord = userRechargeBean.addCzRecordJL(usersId, money, null, null, null, null, note, "1", null);
								if (isOk && !QwyUtil.isNullAndEmpty(czRecord)) {
									log.info("发放成功~,用户ID："+usersId);
								}
							}else{
								log.info("该用户已发放改奖励~ 用户ID："+usersId);
							}
						}
					}
					log.info("第"+currentPage+"页发放完毕~");
				}else{
					log.info("没有要发送奖励的用户~");
					break;
				}
				
			}
			
		} catch (Exception e) {
			log.info("操作异常：",e);
		}
		long et = System.currentTimeMillis();
		log.info("发放奖励耗时: "+(et-st));
		
	}

}
