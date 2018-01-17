package com.huoq.admin.product.bean;

import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.huoq.admin.product.dao.PrizeDAO;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.Prize;

@Service
public class PrizeBean {
	@Resource
	PrizeDAO dao;
	
	/**
	 * 获取奖品集合
	 * @param prizeName  奖品名称
 	 * @param type  奖品类型：1挖宝活动 
 	 * 						倒序排列
	 * @return
	 */
	public List<Prize> findPrize(String prizeName,String type){
		
		StringBuffer hql = new StringBuffer();
		hql.append(" From Prize p WHERE 1=1 ");
		if (!QwyUtil.isNullAndEmpty(prizeName)) {
			hql.append(" AND p.prizeName LIKE '%"+prizeName+"%'");
		}
		
		if (!QwyUtil.isNullAndEmpty(type)) {
			hql.append(" AND p.type = '"+type+"'");
		}
		
		hql.append("ORDER BY p.prizeValue ASC,p.insertTime DESC");
		return dao.LoadAll(hql.toString(), null);
	}
	
	public String addHdFlag(Prize prize){
		return dao.saveAndReturnId(prize);
	}
	
	/**
	 * 根据id修改奖品的状态和中奖概率 
	 * 
	 * @param id
	 * @param winningRate  免费抽奖的 中奖概率
	 * @param payRate	   付费抽奖的 中奖概率
	 * @return
	 */
	public boolean modifyPrize(Long id, Double winningRate,Double payRate) {

		Prize prize = findPrizeById(id);
		prize.setWinningRate(winningRate);
		prize.setPayRate(payRate);
		
		dao.update(prize);
		return true;
	}

	/**	
	 * 根据奖品的ID 获取奖品对象
	 * @param id
	 * @return
	 */
	public Prize findPrizeById(long id){
		return (Prize) dao.findById(new Prize(), id);
	}
	
	
	/**
	 * 根据ID修改状态
	 */
	public boolean updateStatusById(Long id) {
		Prize prize = findPrizeById(id);
		if (!QwyUtil.isNullAndEmpty(prize)) {
			if (!QwyUtil.isNullAndEmpty(prize.getStatus()) && prize.getStatus().equals("1")) {
				prize.setStatus("0");
			} else {
				prize.setStatus("1");
			}
		}
		dao.saveOrUpdate(prize);
		return true;
	}

	
}
