/**
 * hongshiwl.com Inc.
 * Copyright (c) 2015-2017 All Rights Reserved.
 */
package com.huoq.couponRelease.bean;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.couponRelease.dao.UpdateCouponDao;
import com.huoq.orm.CouponReleaseRule;

/**
 * @author guoyin.yi
 * @version $Id: CouponReleaseRuleBean.java, v 0.1  2017/9/20 Exp $
 */
@Service
public class UpdateCouponBean {
	private static Logger log = Logger.getLogger(UpdateCouponBean.class);
	@Resource
	private UpdateCouponDao dao;
	//修改投资券
	public void update(CouponReleaseRule couponReleaseRule) {
		try {
			dao.update(couponReleaseRule);
		} catch (Exception e) {
			log.error("操作异常: ",e);
			e.printStackTrace();
		}
	}
}
