package com.huoq.thread.action;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.admin.Mcoin.dao.UserVIP;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.InterestDetails;
import com.huoq.thread.bean.SendVIPGradeBena;
import com.huoq.thread.dao.ThreadDAO;

@Service
public class SendVIPGradeThread implements Runnable{
	private Logger log = Logger.getLogger(SendVIPGradeThread.class);
	private Integer pageSize = 50;
	@Resource
	private ThreadDAO threadDAO;
	
	@Resource
	private SendVIPGradeBena bean;
	private String insertTime;
	public String getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(String insertTime) {
		this.insertTime = insertTime;
	}

	@Override
	public void run() {
		PageUtil<UserVIP> pageUtil = new PageUtil<UserVIP>();
		pageUtil.setPageSize(pageSize);
		String level ="";
		int currentPage = 0;
		for (;;) {
			currentPage++;
			pageUtil.setCurrentPage(currentPage);
			 List<UserVIP> listInterestDetails  = bean.getInvestorsByPageUtil(pageUtil);
			 if(QwyUtil.isNullAndEmpty(listInterestDetails)){
					break;
				}
			 
			 if(!QwyUtil.isNullAndEmpty(listInterestDetails)){
					for (UserVIP vip : listInterestDetails) {
						level =	userLevel(Double.valueOf(vip.getJeCount())*0.01/10000);
						String temp= bean.sendProfit(vip,level);
					}
				}
	    }
		
	}
	
	public String userLevel(double money){
		if(money >5 && money <=10)
			return "1";
		else if(money>10 && money <=50)
			return "2";
		else if(money>50 && money <=150)
			return "3";
		else if(money>150 && money <=300)
			return "4";
		else if(money>300 && money <=500)
			return "5";
		else if(money>500)
			return "6";
		else
			return "0";
		
	}

}
