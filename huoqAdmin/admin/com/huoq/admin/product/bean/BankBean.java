package com.huoq.admin.product.bean;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.common.dao.ObjectDAO;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.Bank;

/**
 * 后台管理——银行卡限额记录
 *   wxl  2017年3月21日15:48:14
 */
@Service
public class BankBean{
	
	private static Logger log = Logger.getLogger(BankBean.class);
	
	@Resource(name="objectDAO")
	private ObjectDAO dao;
	
	
	
	/**
	 * 加载银行限额表
	 * @return
	 */
	public List<Bank> getBankList(){
		try {
			StringBuffer hql = new StringBuffer();
			hql.append("FROM Bank b WHERE 1=1");
			hql.append(" ORDER BY b.index asc");
			List<Bank> list = dao.LoadAll(hql.toString(), null);
			if (!QwyUtil.isNullAndEmpty(list)) {
				return list;
			}
			
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		
		return null;
	}
	
	

	
	/**
	 * 根据ID修改状态
	 */
	public boolean updateStatusById(Long id) {
		Bank bank = (Bank) dao.findById(new Bank(), id);
		
		StringBuffer sql = new StringBuffer();
		if ("0".equals(bank.getStatus())) {
			sql.append("update bank set status = '1' where id = "+id);
		}else{
			sql.append("update bank set status = '0' where id = "+id);
		}
		
		Object object = dao.excuteSql(sql.toString(), null);
		if (Long.parseLong(object.toString()) >= 1l) {
			return true;
		}
		return false;
	}
	
	
	/**
	 * 根据ID 修改银行限额信息
	 * @param id
	 * @param tx
	 * @param cz
	 * @return
	 */
	public boolean modifyBank(Long id, Integer tx,Integer cz,String note) {
		
		StringBuffer sql = new StringBuffer();
		sql.append("update bank set tx_quota ="+tx+" ,cz_quota = "+cz +",bank_note = '"+note+"' where id = "+id);
		
		Object object = dao.excuteSql(sql.toString(), null);
		if (Long.parseLong(object.toString()) >= 1l) {
			return true;
		}

		return false;
	}
}
