package com.huoq.thread.action;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.TxRecord;
import com.huoq.thread.bean.SendRichThreadBean;

/**
 * 枫叶星球 奖励共享
 * @author wxl
 * 		2017年3月16日15:03:50
 *
 */

@Service(value="SendRichThread")
public class SendRichThread implements Runnable {
	
	Logger log = Logger.getLogger(SendRichThread.class);
	@Resource
	private SendRichThreadBean bean;

	@Override
	public void run() {
		
		long st = System.currentTimeMillis();
		log.info("进入后台线程-------枫叶星球 奖励共享----");
		
		String stTime = "2017-03-22 00:00:00";
		String etTime = "2017-04-15 23:59:59";
		
		try {
			PageUtil pageUtil = new PageUtil();
			pageUtil.setPageSize(200);
			int currentPage = 0;
			for(;;){
				currentPage++;
				pageUtil.setCurrentPage(currentPage);
				
				//分页获取   星球排行榜 投资总额 >=20w 的星主ID  投资总额  desc排序
				pageUtil =  bean.getRankingList(pageUtil, stTime, etTime);
				List list = pageUtil.getList();
				if (!QwyUtil.isNullAndEmpty(list)) {
					
					for (int i = 0; i < list.size(); i++) {
						Object[] objects = (Object[]) list.get(i);
						String usersId = objects[0].toString();               //星主ID
						Double money = Double.valueOf(objects[4].toString()); // 投资总额
						
						//获取该用户活动期间邀请好友的投资总收益
						Object friSY = bean.getInviteSum(Long.parseLong(usersId), stTime, etTime);
						//获取该用户的活动期间的投资总收益
						Object selfSY = bean.getSumInvestorById(Long.parseLong(usersId), stTime, etTime);
						
						Double fri = QwyUtil.calcNumber(QwyUtil.isNullAndEmpty(friSY)?0:friSY, 1,"*").doubleValue();
						Double self = QwyUtil.calcNumber(QwyUtil.isNullAndEmpty(selfSY)?0:selfSY, 1, "*").doubleValue();
						
						Double sumSY = QwyUtil.calcNumber(fri, self, "+").doubleValue();   //总收益
						Double sendMoney ;     //赠送总金额
						
						//根据星主ID 获取该用户活动期间邀请的用户
						List listId = bean.getListId(Long.parseLong(usersId),stTime,etTime);
						//加上该用户
						listId.add(usersId);
						
						//根据投资总额 和 投资总收益  判断赠送金额
						//根据投资总额的大小 来判断 奖励的大小  >= 20w 5% >=50w 10% >=100w 20%
						if (money>=1000000) {
							sendMoney = QwyUtil.calcNumber(sumSY, 0.2, "*").doubleValue();
							sendMoney = QwyUtil.calcNumber(sendMoney, listId.size(), "/").doubleValue();
//							sendMoney = (sumSY * 0.2)/listId.size();
							bean.rechargeByUsersId(listId, sendMoney);
						}else if (money >=500000) {
							sendMoney = QwyUtil.calcNumber(sumSY, 0.1, "*").doubleValue();
							sendMoney = QwyUtil.calcNumber(sendMoney, listId.size(), "/").doubleValue();
//							sendMoney = (sumSY * 0.1)/listId.size();
							bean.rechargeByUsersId(listId, sendMoney);
						}else if(money >= 200000){
							sendMoney = QwyUtil.calcNumber(sumSY, 0.05, "*").doubleValue();
							sendMoney = QwyUtil.calcNumber(sendMoney, listId.size(), "/").doubleValue();
//							sendMoney = (sumSY * 0.05)/listId.size();
							bean.rechargeByUsersId(listId, sendMoney);
						}
						log.info("该星主发放完毕----星主ID："+usersId);
					}
					log.info("第"+currentPage+" 页发放完毕---");
				}else{
					log.info("---没有要发送的星主---");
					break;
				}
				
			}
			
		} catch (Exception e) {
			log.info("操作异常：",e);
		}
		long et = System.currentTimeMillis();
		log.info("发放【土豪星球奖励共享】耗时: "+(et-st));
		
	}

}
