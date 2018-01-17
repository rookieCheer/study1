/**
 * hongshiwl.com Inc.
 * Copyright (c) 2015-2017 All Rights Reserved.
 */
package com.huoq.couponRelease.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huoq.admin.product.bean.ReleaseProductBean;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.*;
import com.huoq.util.CouponReleaseRuleEnum;
import com.huoq.util.SelectItem;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;
import org.springframework.beans.factory.annotation.Autowired;

import com.huoq.common.action.BaseAction;
import com.huoq.couponRelease.bean.CouponReleaseRuleBean;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * @author guoyin.yi
 * @version $Id: CouponReleaseRuleAction.java, v 0.1  2017/9/20 Exp $
 */
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/CouponReleaseRule")
@Results({
		@Result(name = "error", value = "/Product/error.jsp"),
		@Result(name = "couponReleaseRuleList", value = "/Product/couponRule/couponReleaseRuleList.jsp"),
        @Result(name = "addCouponReleaseRule", value = "/Product/couponRule/addCouponReleaseRule.jsp"),
        @Result(name = "addItemMoreRate", value = "/Product/couponRule/addItemMoreRate.jsp"),
        @Result(name = "addProductList", value = "/Product/couponRule/addProductList.jsp"),
        @Result(name = "queryCouponRuleBy", value = "/Product/couponRule/modifyCouponReleaseRule.jsp"),
        @Result(name = "modifyBaseRule", value = "/Product/couponRule/modifyBaseRule.jsp"),
		@Result(name = "login", value = "/Product/loginBackground.jsp" ,type=org.apache.struts2.dispatcher.ServletRedirectResult.class)
})
public class CouponReleaseRuleAction extends BaseAction{

	@Autowired
	private CouponReleaseRuleBean couponReleaseRuleBean;

	@Resource
	private ReleaseProductBean bean;


	private Map<String, String> paramMap;
	private Integer currentPage = 1;
	private Integer pageSize = 50;

	private String finishTime;
	private Product product;
	private String productStatus;

	private String ruleId;
	private String prodId;


	/**
	 * 获取投资券规则列表
	 * @return
	 */
	public String loadCouponReleaseRule(){
		String json="";
		try {
			UsersAdmin users = (UsersAdmin)getRequest().getSession().getAttribute("usersAdmin");
			if(QwyUtil.isNullAndEmpty(users)){
				json = QwyUtil.getJSONString("err", "管理员未登录");
				QwyUtil.printJSON(getResponse(), json);
				//管理员没有登录;
				return "login";
			}

			PageUtil<CouponReleaseRule> pageUtil = new PageUtil<>();
			pageUtil.setCurrentPage(currentPage);
			pageUtil.setPageSize(pageSize);
			StringBuffer url = new StringBuffer();
			url.append(getRequest().getServletContext().getContextPath());
			url.append("/Product/couponReleaseRule!loadCouponReleaseRule.action");
			pageUtil.setPageUrl(url.toString());
			pageUtil = couponReleaseRuleBean.loadCouponReleaseRule(pageUtil);
			if (!QwyUtil.isNullAndEmpty(pageUtil)) {
				getRequest().setAttribute("pageUtil", pageUtil);
			}
			return "couponReleaseRuleList";
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return "error";
	}


	/**
	 * 根据id获取规则信息
	 * @return
	 */
	public String queryCouponRuleById(){
		try {
			String json = "";
			UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
			if (QwyUtil.isNullAndEmpty(users)) {
				json = QwyUtil.getJSONString("err", "管理员未登录");
				QwyUtil.printJSON(getResponse(), json);
				//管理员没有登录;
				return "login";
			}
			CouponReleaseRule couponReleaseRule = couponReleaseRuleBean.queryCouponRuleById(Integer.valueOf(ruleId));

			//设置规则项
			if (null != couponReleaseRule) {
				List<CouponReleaseRuleItem> list = couponReleaseRuleBean.queryCouponRuleItemById(couponReleaseRule.getId());
				if(!CollectionUtils.isEmpty(list)){
					for (CouponReleaseRuleItem items : list){
						if (items instanceof CouponReleaseRuleItemRed) {
							items.setItemType(CouponReleaseRuleEnum.RED.getDesc());
							items.setItemTypeValue(CouponReleaseRuleEnum.RED.getCode());
						}else if (items instanceof CouponReleaseRuleItemMoreMoney ){
							items.setItemType(CouponReleaseRuleEnum.MOREMONEY.getDesc());
							items.setItemTypeValue(CouponReleaseRuleEnum.MOREMONEY.getCode());
						}else if (items instanceof CouponReleaseRuleItemMoreRate){
							items.setItemType(CouponReleaseRuleEnum.MORERATE.getDesc());
							items.setItemTypeValue(CouponReleaseRuleEnum.MORERATE.getCode());
						}else if (items instanceof CouponReleaseRuleItemCash){
							items.setItemType(CouponReleaseRuleEnum.CASH.getDesc());
							items.setItemTypeValue(CouponReleaseRuleEnum.CASH.getCode());
						}else{
						}
					}
					couponReleaseRule.setRuleItems(new HashSet<>(list));
				}else {
					couponReleaseRule.setRuleItems(null);
				}
			}
			//获取产品
//			List<String> prodRuleIdList = couponReleaseRuleBean.queryProductByRuleId(couponReleaseRule.getId());
			List<Product> prodList = couponReleaseRuleBean.queryProductById(couponReleaseRule.getId());

			CouponReleaseRuleSales sales = new CouponReleaseRuleSales();
			sales.setId(couponReleaseRule.getId());
			sales.setProducts(new HashSet<Product>(prodList));
			getRequest().setAttribute("prodList", sales);
			getRequest().setAttribute("couponReleaseRule", couponReleaseRule);
		}catch (Exception e){
			log.error("操作异常: ",e);
		}

		return "queryCouponRuleBy";
	}

    /**
     * 修改规则信息
     * @return
     */
	public String modifyCouponRule(){
		String json = "";
		try {
			UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
			if (QwyUtil.isNullAndEmpty(users)) {
				json = QwyUtil.getJSONString("err", "管理员未登录");
				QwyUtil.printJSON(getResponse(), json);
				//管理员没有登录;
				return "login";
			}
			String id = paramMap.get("id");
			String name = paramMap.get("name");//规则名称
			Boolean toAllProduct = new Boolean(paramMap.get("toAllProduct"));//是否应用所有产品
			Boolean isEnable = new Boolean(paramMap.get("isEnable"));//是否启用
			CouponReleaseRule rule = new CouponReleaseRule();
			rule.setId(QwyUtil.converStrToInt(id,null));
			rule.setName(name);
			rule.setIsEnable(isEnable);
			rule.setToAllProduct(toAllProduct);
			couponReleaseRuleBean.modify(rule);


			json = QwyUtil.getJSONString("ok", "成功");
			QwyUtil.printJSON(getResponse(), json);
			return null;
		}catch (Exception e){
			log.error("操作异常: ",e);
		}
		json = QwyUtil.getJSONString("error", "操作失败");
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			log.error("操作异常: ",e);
		}
		return null;
    }

    /**
     * 删除规则
     * @return
     */
	public String delCouponRule(){
        String json="";
        try {
			UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
			if (QwyUtil.isNullAndEmpty(users)) {
				json = QwyUtil.getJSONString("err", "管理员未登录");
				QwyUtil.printJSON(getResponse(), json);
				//管理员没有登录;
				return "login";
			}

			CouponReleaseRuleSales rule = new CouponReleaseRuleSales();
            rule.setId(Integer.valueOf(ruleId));
            couponReleaseRuleBean.delCouponRule(rule);
			json = QwyUtil.getJSONString("ok", "成功");
			QwyUtil.printJSON(getResponse(), json);
			return null;
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		json = QwyUtil.getJSONString("error", "操作失败");
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			log.error("操作异常: ",e);
		}
	   return null;
    }

	/**
	 * 跳转到新增页面
	 * @return
	 */
	public String addCouponRule(){
		try {
			String json="";
			request = getRequest();
			UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
			if (QwyUtil.isNullAndEmpty(users)) {
				json = QwyUtil.getJSONString("err", "管理员未登录");
				QwyUtil.printJSON(getResponse(), json);
				//管理员没有登录;
				return "login";
			}
		}catch (Exception e){
			log.error("操作异常: ",e);
		}
		return "addCouponReleaseRule";
	}

	/**
	 * 新增规则项
	 * @return
	 */
	public String redirectAddItem(){
		try {
			String json="";
			UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
			if (QwyUtil.isNullAndEmpty(users)) {
				json = QwyUtil.getJSONString("err", "管理员未登录");
				QwyUtil.printJSON(getResponse(), json);
				//管理员没有登录;
				return "login";
			}
			List<SelectItem> ruleList = new ArrayList<>();
			for (CouponReleaseRuleEnum m : CouponReleaseRuleEnum.values()) {
				SelectItem item = new SelectItem();
				item.setValue(m.getCode());
				item.setLabel(m.getDesc());
				ruleList.add(item);
			}
			getRequest().setAttribute("ruleTypeList", ruleList);
			return "addItemMoreRate";
		}catch (Exception e){
			log.error("操作异常: ",e);
		}
		return "error";
	}

	/**
	 * 跳转选择产品页
	 * @return
	 */
	public String redirectAddProduct(){
		String json = "";
		Product product = new Product();
		try {
			UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
			if (QwyUtil.isNullAndEmpty(users)) {
				json = QwyUtil.getJSONString("err", "管理员未登录");
				QwyUtil.printJSON(getResponse(), json);
				//管理员没有登录;
				return "login";
			}

			PageUtil<Product> pageUtil = new PageUtil<Product>();
			pageUtil.setCurrentPage(currentPage);
			pageUtil.setPageSize(pageSize);
			pageUtil = bean.findProductPageList(pageUtil, product);
			StringBuffer url = new StringBuffer();
			url.append(getRequest().getServletContext().getContextPath());
			url.append("/Product/couponReleaseRule!redirectAddProduct.action");
			getRequest().setAttribute("product", product);
			pageUtil.setPageUrl(url.toString());
			if (!QwyUtil.isNullAndEmpty(pageUtil)) {
				Map<String, ProductAccount> map = bean.findInvAccountByProductId();
				getRequest().setAttribute("pageUtil", pageUtil);
				getRequest().setAttribute("map", map);

				getRequest().setAttribute("list", pageUtil.getList());
				return "addProductList";
			}
		} catch (Exception e) {
			log.error("操作异常: ", e);
			json = QwyUtil.getJSONString("err", "查询记录异常");
		}
		return "addProductList";
	}

	/**
	 * 保存投资券发放规则
	 */
	public String saveCouponRule(){
		try {
			String json="";
			UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
			if (QwyUtil.isNullAndEmpty(users)) {
				json = QwyUtil.getJSONString("err", "管理员未登录");
				QwyUtil.printJSON(getResponse(), json);
				//管理员没有登录;
				return "login";
			}

			String name = paramMap.get("name");//规则名称
			Boolean toAllProduct = new Boolean(paramMap.get("toAllProduct"));//是否应用所有产品
			Boolean isEnable = new Boolean(paramMap.get("isEnable"));//是否启用
			String ruleItem = paramMap.get("ruleItem");
			String productList = paramMap.get("productIds");
			System.out.println(ruleItem);

			CouponReleaseRuleSales sales = new CouponReleaseRuleSales();
			sales.setName(name);
			sales.setIsEnable(isEnable);
			sales.setCanDelete(true);
			sales.setToAllProduct(toAllProduct);
		    //保存规则明细
			List<CouponReleaseRuleItemMoreRate> moreRateList = moreRateList(CouponReleaseRuleEnum.MORERATE.getCode(), ruleItem, sales);
			List<CouponReleaseRuleItemRed> redList = redList(CouponReleaseRuleEnum.RED.getCode(), ruleItem, sales);
			List<CouponReleaseRuleItemCash> cashList = cashList(CouponReleaseRuleEnum.CASH.getCode(), ruleItem, sales);
			List<CouponReleaseRuleItemMoreMoney> moneyList = moneyList(CouponReleaseRuleEnum.MOREMONEY.getCode(), ruleItem, sales);

			Set<Product> productSet = new HashSet<>();
			if (StringUtils.isNotBlank(productList)) {

				String[] prod = productList.split(",");
				for (int i = 0; i < prod.length; i++) {
					Product product = new Product();
					product.setId(prod[i]);
					productSet.add(product);
				}
			}
			sales.setProducts(productSet);
			couponReleaseRuleBean.save(sales, moreRateList, redList, cashList, moneyList);

			return "addCouponReleaseRule";
		}catch (Exception e){
			log.error("");
		}
		return null;
	}

	/**
	 * 修改规则项-规则列表修改
	 * @return
	 */
	public String saveCouponRuleItem(){
		try {
			String json="";
			UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
			if (QwyUtil.isNullAndEmpty(users)) {
				json = QwyUtil.getJSONString("err", "管理员未登录");
				QwyUtil.printJSON(getResponse(), json);
				//管理员没有登录;
				return "login";
			}

			String id = paramMap.get("id");//规则名称
			String name = paramMap.get("name");//规则名称
			Boolean toAllProduct = new Boolean(paramMap.get("toAllProduct"));//是否应用所有产品
			Boolean isEnable = new Boolean(paramMap.get("isEnable"));//是否启用
			String ruleItem = paramMap.get("ruleItem");
			String productList = paramMap.get("productIds");
			System.out.println(ruleItem);

			CouponReleaseRuleSales sales = couponReleaseRuleBean.queryCouponRuleSalesById(QwyUtil.converStrToInt(id,null));
			sales.setName(name);
			sales.setIsEnable(isEnable);
			sales.setCanDelete(true);
			sales.setToAllProduct(toAllProduct);
			//保存规则明细
			List<CouponReleaseRuleItemMoreRate> moreRateList = moreRateList(CouponReleaseRuleEnum.MORERATE.getCode(), ruleItem, sales);
			List<CouponReleaseRuleItemRed> redList = redList(CouponReleaseRuleEnum.RED.getCode(), ruleItem, sales);
			List<CouponReleaseRuleItemCash> cashList = cashList(CouponReleaseRuleEnum.CASH.getCode(), ruleItem, sales);
			List<CouponReleaseRuleItemMoreMoney> moneyList = moneyList(CouponReleaseRuleEnum.MOREMONEY.getCode(), ruleItem, sales);

			//获取产品集合
			List<Product> prodList =  couponReleaseRuleBean.queryProductById(sales.getId()) ;
			Set<Product> productSet = new HashSet<>(prodList);

			if (StringUtils.isNotBlank(productList)) {

				String[] prod = productList.split(",");
				for (int i = 0; i < prod.length; i++) {
					Product product = new Product();
					product.setId(prod[i]);
					productSet.add(product);
				}
			}
			sales.setProducts(productSet);
			couponReleaseRuleBean.saveModify(sales, moreRateList, redList, cashList, moneyList);

			return "addCouponReleaseRule";
		}catch (Exception e){
			log.error("");
		}
		return null;
	}

    //固定规则修改
    public String modifyBaseRule(){
        try {
            String json = "";
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                //管理员没有登录;
                return "login";
            }
            CouponReleaseRule couponReleaseRule = couponReleaseRuleBean.queryCouponRuleById(Integer.valueOf(ruleId));

			if (null != couponReleaseRule) {
				List<CouponReleaseRuleItem> list = couponReleaseRuleBean.queryCouponRuleItemById(couponReleaseRule.getId());
				if(!CollectionUtils.isEmpty(list)){
					for (CouponReleaseRuleItem items : list){
						if (items instanceof CouponReleaseRuleItemRed) {
							items.setItemType(CouponReleaseRuleEnum.RED.getDesc());
							items.setItemTypeValue(CouponReleaseRuleEnum.RED.getCode());
						}else if (items instanceof CouponReleaseRuleItemMoreMoney ){
							items.setItemType(CouponReleaseRuleEnum.MOREMONEY.getDesc());
							items.setItemTypeValue(CouponReleaseRuleEnum.MOREMONEY.getCode());
						}else if (items instanceof CouponReleaseRuleItemMoreRate){
							items.setItemType(CouponReleaseRuleEnum.MORERATE.getDesc());
							items.setItemTypeValue(CouponReleaseRuleEnum.MORERATE.getCode());
						}else if (items instanceof CouponReleaseRuleItemCash){
							items.setItemType(CouponReleaseRuleEnum.CASH.getDesc());
							items.setItemTypeValue(CouponReleaseRuleEnum.CASH.getCode());
						}else{
						}
					}
					couponReleaseRule.setRuleItems(new HashSet<>(list));
				}else {
					couponReleaseRule.setRuleItems(null);
				}
			}

            getRequest().setAttribute("baseRule", couponReleaseRule);
        }catch (Exception e){
            log.error("操作异常: ",e);
        }
        return "modifyBaseRule";
    }

    /**
     * 修改固定规则
     * @return
     */
    public String saveBaseRule(){
        String json="";
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                //管理员没有登录;
                return "login";
            }

            String id = paramMap.get("id");//规则名称
            Boolean isEnable = new Boolean(paramMap.get("isEnable"));//是否启用
            String ruleItem = paramMap.get("ruleItem");
            System.out.println(ruleItem);

			CouponReleaseRule sales = couponReleaseRuleBean.queryCouponRuleById(QwyUtil.converStrToInt(id,null));
			sales.setIsEnable(isEnable);
            //保存规则明细
            List<CouponReleaseRuleItemMoreRate> moreRateList = moreRateList(CouponReleaseRuleEnum.MORERATE.getCode(), ruleItem, sales);
            List<CouponReleaseRuleItemRed> redList = redList(CouponReleaseRuleEnum.RED.getCode(), ruleItem, sales);
            List<CouponReleaseRuleItemCash> cashList = cashList(CouponReleaseRuleEnum.CASH.getCode(), ruleItem, sales);
            List<CouponReleaseRuleItemMoreMoney> moneyList = moneyList(CouponReleaseRuleEnum.MOREMONEY.getCode(), ruleItem, sales);

			couponReleaseRuleBean.saveUpdate(sales, moreRateList, redList, cashList, moneyList);

            return "addCouponReleaseRule";
        }catch (Exception e){
            log.error("");
        }
        return null;
    }

	/**
	 * 删除规则
	 * @return
	 */
	public String delRuleItem(){
		String json="";
		try {
			UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
			if (QwyUtil.isNullAndEmpty(users)) {
				json = QwyUtil.getJSONString("err", "管理员未登录");
				QwyUtil.printJSON(getResponse(), json);
				//管理员没有登录;
				return "login";
			}
			CouponReleaseRuleItem items = couponReleaseRuleBean.queryCouponRuleItemByItemId(QwyUtil.converStrToInt(ruleId,null));
			if (items instanceof CouponReleaseRuleItemRed) {
				CouponReleaseRuleItemRed red  = new CouponReleaseRuleItemRed();
				red.setId(QwyUtil.converStrToInt(ruleId,null));
				couponReleaseRuleBean.delRuleItemRedById(red);
			}else if (items instanceof CouponReleaseRuleItemMoreMoney ){
				CouponReleaseRuleItemMoreMoney moreMoney = new CouponReleaseRuleItemMoreMoney();
				moreMoney.setId(QwyUtil.converStrToInt(ruleId,null));
				couponReleaseRuleBean.delRuleItemMoreMoneyById(moreMoney);
			}else if (items instanceof CouponReleaseRuleItemMoreRate){
				CouponReleaseRuleItemMoreRate moreRate = new CouponReleaseRuleItemMoreRate();
				moreRate.setId(QwyUtil.converStrToInt(ruleId,null));
				couponReleaseRuleBean.delRuleItemMoreRateById(moreRate);
			}else if (items instanceof CouponReleaseRuleItemCash){
				CouponReleaseRuleItemCash cash = new CouponReleaseRuleItemCash();
				cash.setId(QwyUtil.converStrToInt(ruleId,null));
				couponReleaseRuleBean.delRuleItemCashById(cash);
			}else{
			}
//			CouponReleaseRuleItem ruleItem = new CouponReleaseRuleItem();
//			ruleItem.setId(Integer.valueOf(ruleId));
//			couponReleaseRuleBean.delRuleItem(ruleItem);
			json = QwyUtil.getJSONString("ok", "成功");
			QwyUtil.printJSON(getResponse(), json);
			return null;
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		json = QwyUtil.getJSONString("error", "操作失败");
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			log.error("操作异常: ",e);
		}
		return null;
	}

	/**
	 * 修改页面里删除产品信息
	 */
	public void delRuleProd(){
		String json = "";
		try {
			if (StringUtils.isEmpty(ruleId) || StringUtils.isEmpty(prodId)){
				json = QwyUtil.getJSONString("error", "参数错误");
			}else {
				couponReleaseRuleBean.delRuleProdById(prodId,ruleId);
				json = QwyUtil.getJSONString("success", "成功");
			}
			QwyUtil.printJSON(getResponse(), json);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	private List<CouponReleaseRuleItemMoreRate> moreRateList(String ruleEnum,String item, CouponReleaseRule rule){
		List<CouponReleaseRuleItemMoreRate> ruleList = new ArrayList<>();
		JSONArray array = JSON.parseArray(item);
		for (int i = 0; i < array.size(); i++) {
			String ruleType = JSON.parseObject(array.get(i).toString()).get("ruleType").toString();
			if (ruleType.equals(ruleEnum)){
				JSONObject object = (JSONObject) (JSON.parseObject(array.get(i).toString())).get("rule");
				CouponReleaseRuleItemMoreRate itemRed = JSON.parseObject(object.toString(), CouponReleaseRuleItemMoreRate.class);
				itemRed.setRule(rule);
				ruleList.add(itemRed);
			}
		}
		return ruleList;
	}

	private List<CouponReleaseRuleItemRed> redList(String ruleEnum,String item, CouponReleaseRule rule){
		List<CouponReleaseRuleItemRed> ruleList = new ArrayList<>();
		JSONArray array = JSON.parseArray(item);
		for (int i = 0; i < array.size(); i++) {
			String ruleType = JSON.parseObject(array.get(i).toString()).get("ruleType").toString();
			if (ruleType.equals(ruleEnum)){
				JSONObject object = (JSONObject) (JSON.parseObject(array.get(i).toString())).get("rule");
				CouponReleaseRuleItemRed itemRed = JSON.parseObject(object.toString(), CouponReleaseRuleItemRed.class);
				itemRed.setRule(rule);
				ruleList.add(itemRed);
			}
		}
		return ruleList;
	}


	private List<CouponReleaseRuleItemCash> cashList(String ruleEnum,String item, CouponReleaseRule rule){
		List<CouponReleaseRuleItemCash> ruleList = new ArrayList<>();
		JSONArray array = JSON.parseArray(item);
		for (int i = 0; i < array.size(); i++) {
			String ruleType = JSON.parseObject(array.get(i).toString()).get("ruleType").toString();
			if (ruleType.equals(ruleEnum)){
				JSONObject object = (JSONObject) (JSON.parseObject(array.get(i).toString())).get("rule");
				CouponReleaseRuleItemCash itemRed = JSON.parseObject(object.toString(), CouponReleaseRuleItemCash.class);
				itemRed.setRule(rule);
				ruleList.add(itemRed);
			}
		}
		return ruleList;
	}

	private List<CouponReleaseRuleItemMoreMoney> moneyList(String ruleEnum,String item, CouponReleaseRule rule){
		List<CouponReleaseRuleItemMoreMoney> ruleList = new ArrayList<>();
		JSONArray array = JSON.parseArray(item);
		for (int i = 0; i < array.size(); i++) {
			String ruleType = JSON.parseObject(array.get(i).toString()).get("ruleType").toString();
			if (ruleType.equals(ruleEnum)){
				JSONObject object = (JSONObject) (JSON.parseObject(array.get(i).toString())).get("rule");
				CouponReleaseRuleItemMoreMoney itemRed = JSON.parseObject(object.toString(), CouponReleaseRuleItemMoreMoney.class);
				itemRed.setRule(rule);
				ruleList.add(itemRed);
			}
		}
		return ruleList;
	}

	public CouponReleaseRuleBean getCouponReleaseRuleBean() {
		return couponReleaseRuleBean;
	}

	public void setCouponReleaseRuleBean(CouponReleaseRuleBean couponReleaseRuleBean) {
		this.couponReleaseRuleBean = couponReleaseRuleBean;
	}

	public Map<String, String> getParamMap() {
		return paramMap;
	}

	public void setParamMap(Map<String, String> paramMap) {
		this.paramMap = paramMap;
	}

	public Integer getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public String getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(String finishTime) {
		this.finishTime = finishTime;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public String getProductStatus() {
		return productStatus;
	}

	public void setProductStatus(String productStatus) {
		this.productStatus = productStatus;
	}

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public String getProdId() {
		return prodId;
	}

	public void setProdId(String prodId) {
		this.prodId = prodId;
	}
}
