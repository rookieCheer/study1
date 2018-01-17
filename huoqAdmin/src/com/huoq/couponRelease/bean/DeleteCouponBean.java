/**
 * hongshiwl.com Inc.
 * Copyright (c) 2015-2017 All Rights Reserved.
 */
package com.huoq.couponRelease.bean;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.couponRelease.dao.DeleteCouponDao;
import com.huoq.orm.CouponReleaseRule;

/**
 * @author guoyin.yi
 * @version $Id: CouponReleaseRuleBean.java, v 0.1  2017/9/20 Exp $
 */
@Service
public class DeleteCouponBean {
	private static Logger log = Logger.getLogger(DeleteCouponBean.class);
	@Resource
	private DeleteCouponDao dao;
	//删除投资券
	public void delete(CouponReleaseRule couponReleaseRule) {
		try {
			dao.delete(couponReleaseRule);
		} catch (Exception e) {
			log.error("操作异常: ",e);
			e.printStackTrace();
		}
	}
	//根据id查询出要删除的投资券
	public CouponReleaseRule getById(Integer id){
		CouponReleaseRule crr = (CouponReleaseRule) dao.findById(new CouponReleaseRule(), id);
		return crr;
		
	}
}
