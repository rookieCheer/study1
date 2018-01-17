/**
 * hongshiwl.com Inc.
 * Copyright (c) 2015-2017 All Rights Reserved.
 */
package com.huoq.couponRelease.bean;

import com.huoq.common.dao.ObjectDAO;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.couponRelease.dao.*;
import com.huoq.orm.*;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author guoyin.yißßß
 * @version $Id: CouponReleaseRuleBean.java, v 0.1  2017/9/20 Exp $
 */
@Service
public class CouponReleaseRuleBean {
	private static Logger log = Logger.getLogger(CouponReleaseRuleBean.class);

	@Resource
	private CouponReleaseRuleDAO couponReleaseRuleDAO;

	@Resource
	private CouponReleaseRuleItemMoreRateDAO couponReleaseRuleItemMoreRateDAO;

	@Resource
	private CouponReleaseRuleItemCashDAO couponReleaseRuleItemCashDAO;

	@Resource
	private CouponReleaseRuleItemRedDAO couponReleaseRuleItemRedDAO;

	@Resource
	private CouponReleaseRuleItemMoreMoneyDAO couponReleaseRuleItemMoreMoneyDAO;

	@Resource
	private CouponReleaseRuleSalesDAO couponReleaseRuleSalesDAO;

	@Resource
	private CouponReleaseRuleItemDAO couponReleaseRuleItemDAO;

	/**
	 * 新增投资券规则
	 * @param sales
	 * @param moreRateList
	 * @param redList
	 * @param cashList
	 * @param moneyList
	 */
	public void save(CouponReleaseRuleSales sales ,List<CouponReleaseRuleItemMoreRate> moreRateList,
					   List<CouponReleaseRuleItemRed> redList ,List<CouponReleaseRuleItemCash> cashList ,
					   List<CouponReleaseRuleItemMoreMoney> moneyList ) {

		couponReleaseRuleSalesDAO.save(sales);

		if (!CollectionUtils.isEmpty(moreRateList)) {
			couponReleaseRuleItemMoreRateDAO.saveList(moreRateList);
		}
		if (!CollectionUtils.isEmpty(cashList)) {
			couponReleaseRuleItemCashDAO.saveList(cashList);
		}
		if (!CollectionUtils.isEmpty(redList)) {
			couponReleaseRuleItemRedDAO.saveList(redList);
		}
		if (!CollectionUtils.isEmpty(moneyList)) {
			couponReleaseRuleItemMoreMoneyDAO.saveList(moneyList);
		}

	}

	/**
	 * 获取规则列表
	 * @param pageUtil
	 * @return
	 */
	public PageUtil<CouponReleaseRule> loadCouponReleaseRule(PageUtil<CouponReleaseRule> pageUtil) {
		ArrayList<Object> ob = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();
		hql.append(" FROM CouponReleaseRule rule ");
		hql.append(" WHERE 1 = 1 and rule.canDelete=true");
		return (PageUtil<CouponReleaseRule>)couponReleaseRuleDAO.getPage(pageUtil, hql.toString(), ob.toArray());
	}

	/**
	 * 删除规则列表
	 * @param rule
	 */
	public void delCouponRule(CouponReleaseRuleSales rule) {
		couponReleaseRuleSalesDAO.delete(rule);
	}

	public CouponReleaseRule queryCouponRuleById(Integer ruleId) {
		return (CouponReleaseRule) couponReleaseRuleSalesDAO.findById(new CouponReleaseRule(),ruleId);
	}

	public void modify(CouponReleaseRule rule) {
		couponReleaseRuleSalesDAO.update(rule);
	}

	/**
	 * 获取规则集合
	 * @param id
	 * @return
	 */
	public List<CouponReleaseRuleItem> queryCouponRuleItemById(Integer id) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer buffer = new StringBuffer();
		buffer.append(" FROM CouponReleaseRuleItem item");
		buffer.append(" WHERE 1=1");
		if (!QwyUtil.isNullAndEmpty(id)) {
			buffer.append(" AND ");
			buffer.append("  item.rule.id = ? ");
			list.add(id);
		}
		return couponReleaseRuleItemDAO.LoadAll(buffer.toString(), list.toArray());
	}

	/**
	 * 获取规则集合
	 * @param id
	 * @return
	 */
	public CouponReleaseRuleItem queryCouponRuleItemByItemId(Integer id) {
		return (CouponReleaseRuleItem) couponReleaseRuleItemDAO.findById(new CouponReleaseRuleItem(), id);
	}

	/**
	 * 保存修改
	 * @param rule
	 * @param moreRateList
	 * @param redList
	 * @param cashList
	 * @param moneyList
	 */
	public void saveUpdate(CouponReleaseRule rule, List<CouponReleaseRuleItemMoreRate> moreRateList,
						   List<CouponReleaseRuleItemRed> redList, List<CouponReleaseRuleItemCash> cashList,
						   List<CouponReleaseRuleItemMoreMoney> moneyList) {

		couponReleaseRuleDAO.update(rule);

		if (!CollectionUtils.isEmpty(moreRateList)) {
			couponReleaseRuleItemMoreRateDAO.saveList(moreRateList);
		}
		if (!CollectionUtils.isEmpty(cashList)) {
			couponReleaseRuleItemCashDAO.saveList(cashList);
		}
		if (!CollectionUtils.isEmpty(redList)) {
			couponReleaseRuleItemRedDAO.saveList(redList);
		}
		if (!CollectionUtils.isEmpty(moneyList)) {
			couponReleaseRuleItemMoreMoneyDAO.saveList(moneyList);
		}
	}

	public void saveModify(CouponReleaseRuleSales rule, List<CouponReleaseRuleItemMoreRate> moreRateList,
						   List<CouponReleaseRuleItemRed> redList, List<CouponReleaseRuleItemCash> cashList,
						   List<CouponReleaseRuleItemMoreMoney> moneyList) {

		couponReleaseRuleDAO.saveOrUpdate(rule);

		if (!CollectionUtils.isEmpty(moreRateList)) {
			couponReleaseRuleItemMoreRateDAO.saveList(moreRateList);
		}
		if (!CollectionUtils.isEmpty(cashList)) {
			couponReleaseRuleItemCashDAO.saveList(cashList);
		}
		if (!CollectionUtils.isEmpty(redList)) {
			couponReleaseRuleItemRedDAO.saveList(redList);
		}
		if (!CollectionUtils.isEmpty(moneyList)) {
			couponReleaseRuleItemMoreMoneyDAO.saveList(moneyList);
		}
	}

//	public void delRuleItem(CouponReleaseRuleItem ruleItem) {
//		couponReleaseRuleItemRedDAO.delete(ruleItem);
//	}

	public void delRuleItemRedById(CouponReleaseRuleItemRed red) {
		couponReleaseRuleItemRedDAO.delete(red);
	}

	public void delRuleItemMoreMoneyById(CouponReleaseRuleItemMoreMoney moreMoney) {
		couponReleaseRuleItemRedDAO.delete(moreMoney);
	}

	public void delRuleItemMoreRateById(CouponReleaseRuleItemMoreRate moreRate) {
		couponReleaseRuleItemRedDAO.delete(moreRate);
	}

	public void delRuleItemCashById(CouponReleaseRuleItemCash cash) {
		couponReleaseRuleItemRedDAO.delete(cash);
	}

	public CouponReleaseRuleSales queryCouponRuleSalesById(Integer ruleId) {
		return (CouponReleaseRuleSales) couponReleaseRuleSalesDAO.findById(new CouponReleaseRuleSales(),ruleId);
	}

	/**
	 * 根据规则ruleId 获取产品ID集合
	 * @param ruleId
	 * @return
	 */
	public List<String> queryProductByRuleId(Integer ruleId) {
		List<Object> list = new ArrayList<Object>();
		StringBuffer buffer = new StringBuffer();
		buffer.append(" select product_id from  coupon_release_rule_to_product_collection sale");
		buffer.append(" WHERE 1=1");
		if (!QwyUtil.isNullAndEmpty(ruleId)) {
			buffer.append(" AND ");
			buffer.append("  sale.coupon_release_rule_sales_id = ? ");
			list.add(ruleId);
		}
		return couponReleaseRuleItemDAO.LoadAllSql(buffer.toString(), list.toArray());
	}

	public List<Product> queryProductById(Integer ruleId){
		List<Object> list = new ArrayList<Object>();
		StringBuffer buffer = new StringBuffer();
		buffer.append(" select * FROM product p");
		buffer.append(" WHERE 1=1");
		buffer.append(" AND ");
		buffer.append("  p.id in (select product_id from  coupon_release_rule_to_product_collection sale ");
		buffer.append(" WHERE 1=1  AND sale.coupon_release_rule_sales_id = ?) ");
		list.add(ruleId);
		return couponReleaseRuleItemDAO.queryAllSql(buffer.toString(), list.toArray(),Product.class);
	}

	public void delRuleProdById(String prodId,String ruleId) {
		List<Object> list = new ArrayList<>();
		StringBuffer buffer = new StringBuffer();
		buffer.append(" delete FROM coupon_release_rule_to_product_collection ");
		buffer.append(" WHERE 1=1");
		buffer.append(" AND product_id = ?");
		buffer.append(" AND coupon_release_rule_sales_id = ?");
		list.add(prodId);
		list.add(ruleId);
		couponReleaseRuleSalesDAO.excuteSql(buffer.toString(),list.toArray());
	}
}
