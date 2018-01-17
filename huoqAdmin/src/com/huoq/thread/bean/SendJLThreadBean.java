package com.huoq.thread.bean;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.common.util.QwyUtil;
import com.huoq.thread.dao.ThreadDAO;

@Service
public class SendJLThreadBean {
	
	private Logger log = Logger.getLogger(SendProfitThreadBean.class);
	@Resource
	private ThreadDAO dao;
	
	/**
	 * 根据活动要求算出  各用户的奖励金额
	 * @param sumInv 总投资额  单位：元
	 * @param invSelf 自己的投资总额
	 * @return  返回的单位:分
	 */
	public Double getJLMoney(Double sumInv,Double invSelf) {
		try {
			Double rewardMoney = 0D; //奖励金额
			// 用户投资金额占比
			Double percent = QwyUtil.isNullAndEmpty(QwyUtil.calcNumber(invSelf, sumInv, "/",4).doubleValue())?0D:QwyUtil.calcNumber(invSelf, sumInv, "/",4).doubleValue();
			
			// 大于 3000w 奖金68888  30000000
			if (sumInv >= 30000000D) {
				rewardMoney = QwyUtil.calcNumber(percent, 68888, "*").doubleValue();
			// 大于 2000w 奖金38888	20000000
			}else if (sumInv >= 20000000D) {
				rewardMoney = QwyUtil.calcNumber(percent, 38888, "*").doubleValue();
			// 大于 1000w 奖金18888 10000000
			}else if (sumInv >= 10000000D) {
				rewardMoney = QwyUtil.calcNumber(percent, 18888, "*").doubleValue();
			}
			
			return QwyUtil.calcNumber(rewardMoney, 100, "*",2).doubleValue();
			
		} catch (Exception e) {
			log.info("操作异常：",e);
		}
		
		return null;
	}
	
}
