package com.huoq.admin.product.bean;

import java.util.ArrayList;
import java.util.Date;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.common.dao.ObjectDAO;
import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.UsersInfo;
import com.huoq.orm.UsersMoneyProblem;

/**未返款,资金问题用户;
 * @author 覃文勇
 *
 * 2017年1月14日下午7:14:26
 */
@Service
public class MoneyProblemBean{
	
	private static Logger log = Logger.getLogger(MoneyProblemBean.class);
	
	@Resource(name="objectDAO")
	private ObjectDAO dao;
	
	/**添加未返款用户记录;
	 * @param usersId 用户id
	 * @param username 用户名
	 * @param backMoney 返款金额
	 * @param freezeMoney 账户冻结金额
	 */
	public void addUsersMoneyProblem(long usersId,String username,double backMoney,double freezeMoney){
		StringBuffer sb = new StringBuffer();
		sb.append("FROM UsersMoneyProblem ump WHERE ump.usersId = ? ");
		UsersMoneyProblem ump = (UsersMoneyProblem)dao.findJoinActive(sb.toString(), new Object[]{usersId});
		if(QwyUtil.isNullAndEmpty(ump)){
			UsersMoneyProblem mp = new UsersMoneyProblem();
			mp.setBackMoney(backMoney);
			mp.setFreezeMoney(freezeMoney);
			mp.setStatus("0");
			mp.setInsertTime(new Date());
			mp.setUpdateTime(mp.getInsertTime());
			mp.setUsersId(usersId);
			mp.setUserName(username);
			dao.save(mp);
		}else{
			ump.setBackMoney(backMoney);
			ump.setFreezeMoney(freezeMoney);
			ump.setUpdateTime(new Date());
			dao.saveOrUpdate(ump);
		}
	}
	
	/**
	 * 分页查询 异常资金用户
	 * @param pageUtil
	 * @param status  状态
	 * @param insertTime 时间
	 * @param name  关键字
	 * @return
	 */
	public PageUtil<UsersMoneyProblem> loadMoneyProblemUsers(PageUtil<UsersMoneyProblem> pageUtil,String status,String insertTime,String name){
		try {
			StringBuffer hql = new StringBuffer();
			ArrayList<Object> list = new ArrayList<Object>();
			
			hql.append("FROM UsersMoneyProblem mp WHERE 1=1");
			
			if (!QwyUtil.isNullAndEmpty(name)) {
				hql.append(" AND (mp.usersId = '"+name+"'OR mp.userName='"+DESEncrypt.jiaMiUsername(name)+"')");
			}
			if (!QwyUtil.isNullAndEmpty(insertTime)) {
				
				String[] time = QwyUtil.splitTime(insertTime);
				if (time.length > 1) {
					hql.append(" AND mp.insertTime >= ? ");
					list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
					hql.append(" AND mp.insertTime <= ? ");
					list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
				} else {
					hql.append(" AND mp.insertTime >= ? ");
					list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
					hql.append(" AND mp.insertTime <= ? ");
					list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
				}
				
			}
			if (!QwyUtil.isNullAndEmpty(status)) {
				hql.append(" AND mp.status= "+status);
			}
			
			pageUtil = dao.getPage(pageUtil, hql.toString(), list.toArray());
			
			if (!QwyUtil.isNullAndEmpty(pageUtil)) {
				return pageUtil;
			}
			
			return null;
			
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		
		
		return null;
	}
	
	
	/**
	 * 根据ID修改状态
	 */
	public boolean updateStatusById(String id) {
		UsersMoneyProblem usersMoneyProblem = (UsersMoneyProblem) dao.findById(new UsersMoneyProblem(), id);
		if (!QwyUtil.isNullAndEmpty(usersMoneyProblem)) {
			if (!QwyUtil.isNullAndEmpty(usersMoneyProblem.getStatus()) && usersMoneyProblem.getStatus().equals("0")) {
				usersMoneyProblem.setStatus("1");
			} else {
				usersMoneyProblem.setStatus("1");
			}
			usersMoneyProblem.setUpdateTime(new Date());
		}
		dao.saveOrUpdate(usersMoneyProblem);
		return true;
	}
	
	
	//
	/**
	 * 分页查询 异常资金用户
	 * @param pageUtil
	 * @param status  状态
	 * @param insertTime 时间
	 * @param name  关键字
	 * @return
	 */
	public PageUtil<UsersInfo> loadBalanceAbnorma(PageUtil<UsersInfo> pageUtil,String name){
		try {
			StringBuffer hql = new StringBuffer();
			hql.append(" FROM UsersInfo WHERE ( totalMoney<0 OR leftMoney<0 OR freezeMoney<0 )");
			
			if (!QwyUtil.isNullAndEmpty(name)) {
				hql.append(" AND (usersId = '"+name+"'OR phone='"+DESEncrypt.jiaMiUsername(name)+"')");
			}
			
			pageUtil = dao.getPage(pageUtil, hql.toString(), null);
			
			if (!QwyUtil.isNullAndEmpty(pageUtil)) {
				return pageUtil;
			}
			
			return null;
			
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		
		
		return null;
	}
	
}
