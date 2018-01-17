/**
 * 
 */
package com.huoq.admin.product.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.huoq.admin.product.dao.TransferCostRateDAO;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.TransferCostRate;

/**
 * @author 覃文勇
 * 2015年11月9日下午1:58:10
 */
@Service
public class TransferCostRateBean {
	@Resource
	TransferCostRateDAO dao;
	
	/**
	 * 查询转让规则
	 * @param pageUtil
	 * @param status 状态0:可用，1:不可用
	 * @param productTitle 产品标题(模糊查询)
	 * @return
	 */
	public PageUtil<TransferCostRate> findTransferCostRates(PageUtil<TransferCostRate> pageUtil,String status,String productTitle){
		List<Object> list=new ArrayList<Object>();
		StringBuffer buffer=new StringBuffer();
		buffer.append(" FROM TransferCostRate tc WHERE 1=1 ");
		if(!QwyUtil.isNullAndEmpty(status)){
			buffer.append(" AND tc.status = ? ");
			list.add(status);
		}
		if(!QwyUtil.isNullAndEmpty(productTitle)){
			buffer.append(" AND tc.product.title like '%"+productTitle+"%' ");
		}
		return dao.getPage(pageUtil, buffer.toString(), list.toArray());
	}
	
	public TransferCostRate saveTransferCostRate(TransferCostRate transferCostRate){
		transferCostRate.setStatus("0");
		dao.save(transferCostRate);
		return transferCostRate;
		
	}
}
