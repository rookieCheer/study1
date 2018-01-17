/**
 * hongshiwl.com Inc.
 * Copyright (c) 2015-2017 All Rights Reserved.
 */
package com.huoq.couponRelease.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.couponRelease.dao.FindAllProductAndCouponDao;
import com.huoq.orm.CouponReleaseRule;
import com.huoq.orm.CouponReleaseRuleSales;

/**
 * @author Alex
 * @version 查询出所有产品和理财券
 */
@SuppressWarnings("unchecked")
@Service
public class FindAllProductAndCouponBean {
	private static Logger log = Logger.getLogger(FindAllProductAndCouponBean.class);
	@Resource
	private FindAllProductAndCouponDao dao;
	private Integer id;

	public ArrayList<CouponReleaseRule> findAllProductAndCoupon() {
		try {
			// 查询出所有的销售规则表
			//
			// 查询出优惠券规则表
			List<CouponReleaseRule> ccrslist = dao.LoadAll("from CouponReleaseRule ", null);
			return (ArrayList<CouponReleaseRule>) ccrslist;
		} catch (Exception e) {
			log.error("操作异常: ", e);
			e.printStackTrace();
		}
		return null;
	}

	public ArrayList<CouponReleaseRuleSales> findallRuleSales() {
		try {
			List<CouponReleaseRuleSales> ccrlist = (ArrayList<CouponReleaseRuleSales>) dao
					.LoadAll("from CouponReleaseRuleSales", null);
			return (ArrayList<CouponReleaseRuleSales>) ccrlist;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public ArrayList<CouponReleaseRule> findallRule() {
		try {
			List<CouponReleaseRule> ccrlist = (ArrayList<CouponReleaseRule>) dao
					.LoadAll("from CouponReleaseRule", null);
			return (ArrayList<CouponReleaseRule>) ccrlist;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
}
