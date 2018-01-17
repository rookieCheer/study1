package com.huoq.thread.action;
/**
 * 后台发放收益加层 枫叶日
 */
import java.util.List;
import javax.annotation.Resource;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import com.huoq.account.bean.InvestorsRecordBean;
import com.huoq.account.bean.UserRechargeBean;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.CzRecord;
import com.huoq.thread.bean.SendGdpThreadBean;



@Service(value="SendGdpThread")
public class SendGdpThread implements Runnable {
	
	Logger log = Logger.getLogger(SendGdpThread.class);
	@Resource
	private SendGdpThreadBean sendGdpThreadBean;
	@Resource
	private InvestorsRecordBean invstorsRecordBean;
	@Resource
	private UserRechargeBean userRechargeBean;

	@Override
	public void run() {
		
		long st = System.currentTimeMillis();
		log.info("进入后台线程-------枫叶日 收益加成奖励----");
		
		String stTime = "2017-04-28 00:00:00";
		String etTime = "2017-04-28 23:59:59";
		
		try {
			PageUtil pageUtil = new PageUtil();
			pageUtil.setPageSize(200);
			int currentPage = 0;
			//平台投资总额
			Object sum = invstorsRecordBean.getSumInvCopies(null, stTime, etTime, false, "1",null);
			Double sumInv = 4229900D;//QwyUtil.isNullAndEmpty(sum)?0D:Double.parseDouble(sum.toString());
			
			for(;;){
				currentPage++;
				pageUtil.setCurrentPage(currentPage);
				
				//分页获取  活动当天投资 各投资用户的投资总收益
				pageUtil =  sendGdpThreadBean.getsumSY(pageUtil, stTime, etTime);
				List<Object[]> list = pageUtil.getList();
				
				if (!QwyUtil.isNullAndEmpty(list)) {
					for(Object[] objects : list){
						Long usersId = Long.parseLong(objects[0].toString()); //用户ID
						Double shouyi = Double.parseDouble(objects[1].toString()); //总收益
						Double newInv = 0D;
						if (sumInv >= 8000000) {   //800w  13%
							newInv = QwyUtil.calcNumber(shouyi, 0.13, "*").doubleValue();
						}else if (sumInv >= 6000000) {   //600w  8%
							newInv = QwyUtil.calcNumber(shouyi, 0.08, "*").doubleValue();
						}else if (sumInv >= 5000000) {   //500w  5%
							newInv = QwyUtil.calcNumber(shouyi, 0.05, "*").doubleValue();
						}else if (sumInv >= 4000000){    //400w  3%
							newInv = QwyUtil.calcNumber(shouyi, 0.03, "*").doubleValue();
						}
						log.info("用户ID: "+usersId+" 发放收益: "+(newInv*0.01)+"元");
						//  发放奖励
						if (newInv != 0D) {
							if (userRechargeBean.getCzRecord(usersId, null, null, "枫叶日收益")) {
								boolean isOk = userRechargeBean.usreRecharge(usersId, newInv, "cz", "系统充值", "枫叶日收益奖励");
								CzRecord czRecord = userRechargeBean.addCzRecordJL(usersId, newInv, null, null, null, null, "枫叶日收益奖励", "1", null);
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
					log.info("没有投资的用户~");
					break;
				}
				
			}
		} catch (Exception e) {
			log.info("操作异常：",e);
		}
		long et = System.currentTimeMillis();
		log.info("发放【枫叶日收益奖励】耗时: "+(et-st));
		
	}

}
