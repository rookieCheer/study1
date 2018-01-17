package com.huoq.thread.action;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.account.bean.InvestorsRecordBean;
import com.huoq.account.bean.UserRechargeBean;
import com.huoq.common.bean.InviteBean;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.CzRecord;
import com.huoq.orm.InterestDetails;
import com.huoq.orm.Investors;
import com.huoq.orm.InviteEarn;
import com.huoq.orm.Product;
import com.huoq.orm.SystemConfig;
import com.huoq.thread.bean.ClearingProductFreshmanThreadBean;
import com.huoq.thread.bean.ClearingProductThreadBean;
import com.huoq.thread.bean.SendCzCouponThreadBean;
import com.huoq.thread.bean.SendInviteEarnThreadBean;
import com.huoq.thread.bean.SendJLThreadBean;
import com.huoq.thread.bean.SendProfitThreadBean;
import com.huoq.thread.dao.ThreadDAO;

/**
 * 对活动期内 充值达标的用户 发送理财券
 * @author admin
 *
 */
@Service
public class SendCzCouponThread implements Runnable {
	private Logger log = Logger.getLogger(SendCzCouponThread.class);
	private Integer pageSize = 50;
	@Resource
	private InvestorsRecordBean investorsRecordBean;
	@Resource
	private SendCzCouponThreadBean sendCzCouponThreadBean;
	@Resource
	private UserRechargeBean userRechargeBean;
	
	@Override
	public void run() {
		
		long st = System.currentTimeMillis();
		log.info("进入后台线程-------发送奖励----");
		String note = "端午节活动"; //某个活动奖励备注 
		String stTime = "2017-05-23 00:00:00"; //活动时间
		String etTime = "2017-05-27 23:59:59"; 
		try {
			PageUtil pageUtil = new PageUtil();
			pageUtil.setPageSize(200);
			int currentPage = 0;
			for(;;){
				currentPage++;
				pageUtil.setCurrentPage(currentPage);
				
				// 活动期间内  充值达标的用户
				pageUtil = sendCzCouponThreadBean.getCzSumByUId(pageUtil, "'1'", stTime, etTime,5000);

				List list = pageUtil.getList();
				if (!QwyUtil.isNullAndEmpty(list)) {
					for (int i = 0; i < list.size(); i++) {
						Object[] ob = (Object[]) list.get(i);
						Double sumCz = QwyUtil.calcNumber(ob[0], 1, "*").doubleValue(); //用户充值总额(元)
						Long usersId = Long.parseLong(ob[1].toString());
						//发送奖励  3w/8000 1w/2000  5000/800
						if (sendCzCouponThreadBean.getCoupon(usersId, stTime, etTime, note)) {
							if (sumCz >= 30000) {
								userRechargeBean.sendHongBao(usersId, 800000D, QwyUtil.addDaysFromOldDate(new Date(), 15).getTime(), "0", -1, note, null);
							}else if (sumCz >= 10000) {
								userRechargeBean.sendHongBao(usersId, 200000D, QwyUtil.addDaysFromOldDate(new Date(), 15).getTime(), "0", -1, note, null);
							}else if (sumCz >= 5000) {
								userRechargeBean.sendHongBao(usersId, 80000D, QwyUtil.addDaysFromOldDate(new Date(), 15).getTime(), "0", -1, note, null);
							}else{
								log.info("该用户充值未达标！用户ID："+usersId);
							}
							
						}else{
							log.info("该用户已发放过此奖励!用户ID："+usersId);
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
