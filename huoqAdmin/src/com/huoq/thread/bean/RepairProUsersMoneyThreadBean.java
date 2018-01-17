package com.huoq.thread.bean;
/**
 *修复异常资金账户
 * @author wxl
 * 		2017年3月21日15:57:22
 *
 */

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.huoq.account.bean.UserRechargeBean;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.ExcpCzUsers;
import com.huoq.thread.dao.ThreadDAO;

@Service
public class RepairProUsersMoneyThreadBean {
	
	@Resource
	private ThreadDAO dao;
	@Resource
	private UserRechargeBean userRechargeBean;
	
	
	/**
	 * 分页加载异常资金的账户
	 * @param pageUtil
	 * @return
	 */
	public PageUtil<ExcpCzUsers> getUsersMoneyList(PageUtil<ExcpCzUsers> pageUtil){
		
		StringBuffer hql = new StringBuffer();
		hql.append(" FROM ExcpCzUsers eu WHERE 1=1 AND eu.status = '0'");
		
		pageUtil = (PageUtil<ExcpCzUsers>) dao.getPage(pageUtil, hql.toString(), null);
		
		if (!QwyUtil.isNullAndEmpty(pageUtil)) {
			return pageUtil;
		}
		
		return null;
	}
	
	/**
	 * 根据用户ID 修改账户余额 总资产
	 * @param usersId
	 * @param money
	 * @return
	 */
	public boolean modifyMoney(Long usersId,Double money){
		
		StringBuffer sql = new StringBuffer();
		
		sql.append("UPDATE users_info SET total_money = (total_money -"+money+"),left_money = (left_money - "+money+") ");
		sql.append(" WHERE users_id ="+usersId);
		
		Object object = dao.excuteSql(sql.toString(), null);
		if (Long.parseLong(object.toString()) >= 1) {
			return true;
		}
		
		return false;
	}
	
	
}
