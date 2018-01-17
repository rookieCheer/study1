package com.huoq.thread.action;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.login.bean.RegisterUserBean;
import com.huoq.orm.Coupon;
import com.huoq.orm.Users;
import com.huoq.thread.bean.FinishCouponThreadBean;

/**
 * 后台线程扫描过期投资券;Thread;入口<br>
 * 状态 0未使用,1未用完,2已用完,3已过期<br>
 * 把到期的投资券更改状态为 3;
 * 
 * @author qwy
 *
 * @createTime 2015-05-23 12:12:16
 */
@Service
public class NumberAssignmentThread implements Runnable {
	private Logger log = Logger.getLogger(NumberAssignmentThread.class);
	@Resource
	private RegisterUserBean bean;

	@Override
	public void run() {
		try {
			log.info("进入后台线程....补全号码归属地...");
			PageUtil<Users> pageUtil = new PageUtil<Users>();
			pageUtil.setPageSize(100);
				//获取没有归属地的用户
				List<Users> findUsers = bean.findUsers(true, 1, 100);
				if(QwyUtil.isNullAndEmpty(findUsers)) {
					log.info("用户归属地信息已补全,退出本次任务");
					return;
				}
				log.info("本次共有"+findUsers.size()+"个用户需要补全归属地信息");
				bean.setMobileLocation(findUsers);
		} catch (Exception e) {
			log.error("操作异常: ", e);
			log.error("进入后台线程....异常", e);
		} catch (Error e) {
			log.error("操作异常: ", e);
			log.error("进入后台线程....error异常", e);
		}
	}

}
