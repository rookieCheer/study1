package com.huoq.thread.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.ExcpCzUsers;
import com.huoq.thread.bean.RepairProUsersMoneyThreadBean;
import com.huoq.thread.dao.ThreadDAO;

/**
 * 修复异常资金账户
 * @author wxl
 *    2017年3月21日15:53:45
 *
 */
@Service
public class RepairProUsersMoneyThread implements Runnable{
	private Logger log = Logger.getLogger(RepairProUsersMoneyThread.class);
	
	@Resource
	private RepairProUsersMoneyThreadBean repairProUsersMoneyBean;

	@Resource
	private ThreadDAO dao;
	
	@Override
	public void run() {
		
		long st = System.currentTimeMillis();
		try {
			PageUtil<ExcpCzUsers> pageUtil = new PageUtil<ExcpCzUsers>();
			ArrayList<String> listId = new ArrayList<String>();
			Double diff = 0D;
			Double sumDiff = 0D;
			pageUtil.setPageSize(200);
			int currentPage = 0;
			for(;;){
				currentPage++;
				pageUtil.setCurrentPage(currentPage);
				
				//分页获取   异常资金账户的用户
				pageUtil =  repairProUsersMoneyBean.getUsersMoneyList(pageUtil);
				List<ExcpCzUsers> list = pageUtil.getList();
				if (!QwyUtil.isNullAndEmpty(list)) {
					for (int i = 0; i < list.size(); i++) {
						//获取 用户对象
						ExcpCzUsers eCzUsers = list.get(i);
						
						
						// 算出差值
						if (!QwyUtil.isNullAndEmpty(eCzUsers)) {
							diff = QwyUtil.calcNumber(eCzUsers.getCzMoney(),eCzUsers.getBfMoney(), "-").doubleValue();
						}
						
						//修改该用户的账户总资产和账户余额 
						boolean isRepair = repairProUsersMoneyBean.modifyMoney(eCzUsers.getUsersId(), Math.abs(diff));
						//修复成功后 修改状态 更改时间 会导致下次分页查询无结果
						if (isRepair) {
							listId.add(eCzUsers.getId().toString());
							sumDiff+= Math.abs(diff);
//							eCzUsers.setStatus("1");
//							eCzUsers.setUpdateTime(new Date());
//							dao.saveOrUpdate(eCzUsers);
						}
					}
					
					log.info("第 "+currentPage+" 页修复完毕");
				}else{
					log.info("---没有异常资金的账户---");
					break;
				}
				
			}
			
			// 循环修改异常充值用户的状态
			if (!QwyUtil.isNullAndEmpty(listId)) {
				for(String id : listId){
					ExcpCzUsers users = (ExcpCzUsers) dao.findById(new ExcpCzUsers(), id);
					users.setStatus("1");
					users.setUpdateTime(new Date());
					dao.saveOrUpdate(users);
				}
			}
			
			String diffMoney = QwyUtil.calcNumber(sumDiff, 0.01,"*").toPlainString();
			log.info("---状态修改完毕----");
			log.info("修改人数为："+listId.size()+"位，金额为："+diffMoney+" 元");
			
		} catch (Exception e) {
			log.info("操作异常：",e);
		}
		
		long et = System.currentTimeMillis();
		log.info("处理【异常资金的账户】耗时: "+(et-st));
		
		
	}

}
