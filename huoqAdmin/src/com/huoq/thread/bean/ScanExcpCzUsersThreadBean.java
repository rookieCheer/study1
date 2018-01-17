package com.huoq.thread.bean;

import java.util.ArrayList;
import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.huoq.common.dao.ObjectDAO;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.CzRecord;

@Service
public class ScanExcpCzUsersThreadBean {
	@Resource
	private  ObjectDAO objectDAO;
	
	/**获取没有校验过的充值记录;<br>
	 * @param pageUtil
	 * @param status
	 * @param usersId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public PageUtil<CzRecord> getCzRecord(PageUtil<CzRecord> pageUtil,String status,Long usersId){
		ArrayList<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		sb.append("FROM CzRecord  cz ");
		sb.append(" WHERE cz.status = ? ");
		list.add(status);
		sb.append(" AND cz.queryId IS NULL ");
		sb.append(" AND cz.orderId IS NOT NULL ");
		sb.append(" AND cz.orderId !='' ");
		if(!QwyUtil.isNullAndEmpty(usersId)){
			sb.append(" AND cz.usersId = ? ");	
			list.add(usersId);
		}
		sb.append(" ORDER BY cz.insertTime ASC ");
		return (PageUtil<CzRecord>)objectDAO.getPage(pageUtil, sb.toString(), list.toArray());
	}
	
	/**对ORM实体进行更新或者保存;
	 * @param obj
	 */
	public void saveOrUpdate(Object obj){
		objectDAO.saveOrUpdate(obj);
	}
	
	
	/**更新充值记录;
	 * @param id id
	 * @param status //充值状态;0:待充值;1:充值成功;2充值失败;3:易宝充值成功,数据库插入失败;
	 * @param bfMoney 充值金额(分)
	 * @param queryId 查询id
	 * @param err_code 错误码
	 * @param err_cause 错误原因
	 */
	public void updateCzRecord(String id,String status,Double bfMoney,String queryId,String err_code,String err_cause){
		String queryTime = QwyUtil.fmyyyyMMddHHmmss.format(new Date());
		StringBuffer sql = new StringBuffer();
		ArrayList<Object> list = new ArrayList<Object>();
		sql.append(" UPDATE cz_record SET ");
		if(!QwyUtil.isNullAndEmpty(bfMoney)){
			sql.append(" money = ?, ");
			list.add(bfMoney);
		}
		if(!QwyUtil.isNullAndEmpty(status)){
			sql.append(" status = ?, ");
			list.add(status);
		}
		if(!QwyUtil.isNullAndEmpty(err_code)){
			sql.append(" error_code = ?, ");
			list.add(err_code);
		}
		if(!QwyUtil.isNullAndEmpty(err_cause)){
			sql.append(" err_cause = ?, ");
			list.add(err_cause);
		}
		if(!QwyUtil.isNullAndEmpty(queryId)){
			sql.append(" query_id = ?, ");
			list.add(queryId);
		}
		sql.append(" query_time = ? ");
		list.add(queryTime);
		sql.append(" WHERE id = ? ");
		list.add(id);
		objectDAO.excuteSql(sql.toString(), list.toArray());
	}
	
	
}
