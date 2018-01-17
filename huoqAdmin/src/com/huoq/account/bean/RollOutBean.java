/**
 * 
 */
package com.huoq.account.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.huoq.account.dao.RollOutDAO;
import com.huoq.common.ApplicationContexts;
import com.huoq.common.bean.MyWalletBean;
import com.huoq.common.bean.SystemConfigBean;
import com.huoq.common.util.QwyUtil;
import com.huoq.login.bean.RegisterUserBean;
import com.huoq.orm.CoinPurse;
import com.huoq.orm.RollOut;
import com.huoq.orm.ShiftTo;

/**
 * @author 覃文勇 2015年8月17日下午3:07:07 转出记录操作
 */
@Service
public class RollOutBean {
	private static Logger log = Logger.getLogger(RollOutBean.class);
	@Resource
	RollOutDAO dao;
	@Resource
	ShiftToBean shiftToBean;
	@Resource
	CoinpPurseBean coinpPurseBean;
	@Resource
	private SystemConfigBean systemConfigBean;
	@Resource
	RegisterUserBean registerUserBean;
	@Resource
	MyWalletBean walletBean;
	@Resource
	CoinPurseFundRecordBean cpfrBean;

	/**
	 * 转出时，插入一条记录 保存转入记录
	 * 
	 * @param usersId
	 *            用户ID
	 * @param status
	 *            状态 0:成功1：失败
	 * @param outMoney
	 *            转出金额
	 * @param leftMoney
	 *            剩余金额
	 * @return
	 */
	public String saveRollOut(Long usersId, String status, Double outMoney, Double leftMoney) {
		RollOut shiftTo = new RollOut();
		shiftTo.setInsertTime(new Date());
		shiftTo.setInMoney(outMoney);
		shiftTo.setLeftMoney(leftMoney);
		shiftTo.setStatus(status);
		shiftTo.setType("0");
		shiftTo.setUsersId(usersId);
		String id = dao.saveAndReturnId(shiftTo);
		return id;
	}

	/**
	 * 转出的所有操作
	 * 
	 * @param usersId
	 * @param outMoney
	 * @return
	 */
	public String rollOut(Long usersId, Double outMoney) {
		try {
			List<RollOut> list = findRecordsByUid(usersId, 1, 20, true);
			if (!QwyUtil.isNullAndEmpty(list)) {
				return "一天只能转出一次";
			}
		} catch (Exception e1) {
			log.error("操作异常",e1);
		}
		ApplicationContext context = ApplicationContexts.getContexts();
		// SessionFactory sf = (SessionFactory)
		// context.getBean("sessionFactory");
		PlatformTransactionManager tm = (PlatformTransactionManager) context.getBean("transactionManager");
		TransactionStatus ts = tm.getTransaction(new DefaultTransactionDefinition());
		boolean flag = false;
		try {
			CoinPurse coinPurse = coinpPurseBean.findCoinPurseByUsersId(usersId);
			// SystemConfig systemConfig = systemConfigBean.findSystemConfig();
			// Users users = registerUserBean.getUsersById(usersId);
			// UsersInfo usersInfo = users.getUsersInfo();
			Double money = outMoney;
			if (coinPurse.getInMoney() < outMoney) {
				return "您的账户余额不足";
			}
			Double dou = coinPurse.getInMoney();
			List<ShiftTo> shiftTos = new ArrayList<ShiftTo>();
			List<ShiftTo> list = shiftToBean.findShiftTosByUsersId(usersId, "'0','1'", null, null, "insertTime", " ASC ");
			String rollOutId = saveRollOut(usersId, "0", outMoney, QwyUtil.calcNumber(coinPurse.getInMoney(), outMoney, "-").doubleValue());
			if (!QwyUtil.isNullAndEmpty(rollOutId)) {
				for (ShiftTo shiftTo : list) {
					Double lmoney = shiftTo.getLeftMoney() - money;
					// 转入记录剩余金额大于转出金额，改变剩余金额的值
					if (lmoney > 0) {
						shiftTo.setLeftMoney(lmoney);
						shiftTos.add(shiftTo);
						break;
					} else if (lmoney == 0) {
						// 转入记录剩余金额等于于转出金额，改变剩余金额的值
						shiftTo.setLeftMoney(0D);
						shiftTo.setStatus("2");
						shiftTos.add(shiftTo);
						break;
					} else {
						shiftTo.setLeftMoney(0D);
						shiftTo.setStatus("2");
						money -= shiftTo.getLeftMoney();
						shiftTos.add(shiftTo);
						continue;
					}
				}
				if (!QwyUtil.isNullAndEmpty(shiftTos)) {
					for (ShiftTo shiftTo : shiftTos) {
						dao.update(shiftTo);
					}
					coinPurse.setInMoney(dou - outMoney);
					coinPurse.setUpdateTime(new Date());
					dao.update(coinPurse);
					if (walletBean.addLeftMoney(usersId, outMoney, "out", "零钱包转出", "零钱包转出")) {
						flag = true;
					}
				}
			}
			if (flag) {
				cpfrBean.saveCoinPurseFundsRecord(usersId, outMoney, rollOutId, "out", dou - outMoney, "");
				tm.commit(ts);
				return "ok";
			}
		} catch (Exception e) {
			log.error("操作异常: ",e);
			log.error(e.getMessage(), e);
		}
		tm.rollback(ts);
		return "error";
	}

	/**
	 * 根据ID查询
	 * 
	 * @param id
	 * @return
	 */
	public RollOut findRollOutById(String id) {
		Object object = dao.findById(new RollOut(), id);
		if (!QwyUtil.isNullAndEmpty(object)) {
			return (RollOut) object;
		}
		return null;
	}

	/**
	 * 根据用户ID获取转出记录
	 * 
	 * @param uid
	 *            用户ID
	 * @param currentPage
	 *            当前页
	 * @param pageSize
	 *            一页条数
	 * @param isInsertTime
	 *            是否需要当前日期
	 * @return
	 */
	public List<RollOut> findRecordsByUid(Long uid, Integer currentPage, Integer pageSize, boolean isInsertTime) throws Exception {
		StringBuffer hql = new StringBuffer();
		hql.append(" FROM RollOut ro ");
		hql.append(" WHERE ro.usersId = ? ");
		if (isInsertTime) {
			hql.append(" AND ro.insertTime >= '" + QwyUtil.fmyyyyMMdd.format(new Date()) + "' ");
		}
		hql.append(" ORDER BY ro.insertTime DESC ");
		return dao.findAdvList(hql.toString(), new Object[] { uid }, currentPage, pageSize);
	}

}
