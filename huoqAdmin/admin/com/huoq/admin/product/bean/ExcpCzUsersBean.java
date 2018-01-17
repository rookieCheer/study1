package com.huoq.admin.product.bean;

import java.util.ArrayList;
import java.util.Date;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.common.dao.ObjectDAO;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.ExcpCzUsers;

/**异常充值用户
 * @author wxl
 *
 *2017年2月25日15:20:57
 */
@Service
public class ExcpCzUsersBean{
	
	private static Logger log = Logger.getLogger(ExcpCzUsersBean.class);
	
	@Resource(name="objectDAO")
	private ObjectDAO dao;
	
	
	/**
	 * 分页查询 异常充值用户
	 * @param pageUtil
	 * @param status  状态
	 * @param insertTime 时间
	 * @param name  关键字
	 * @return
	 */
	public PageUtil<ExcpCzUsers> loadExcpCzUsersList(PageUtil<ExcpCzUsers> pageUtil,String status,String insertTime,String name){
		try {
			StringBuffer hql = new StringBuffer();
			ArrayList<Object> list = new ArrayList<Object>();
			
			hql.append("FROM ExcpCzUsers e WHERE 1=1");
			
			if (!QwyUtil.isNullAndEmpty(name)) {
				hql.append(" AND (e.usersId = '"+name+"'OR e.username='"+name+"')");
			}
			if (!QwyUtil.isNullAndEmpty(insertTime)) {
				String[] time = QwyUtil.splitTime(insertTime);
				if (time.length > 1) {
					hql.append(" AND e.insertTime >= ? ");
					list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
					hql.append(" AND e.insertTime <= ? ");
					list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
				} else {
					hql.append(" AND e.insertTime >= ? ");
					list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
					hql.append(" AND e.insertTime <= ? ");
					list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
				}
			}
			if (!QwyUtil.isNullAndEmpty(status)) {
				hql.append(" AND e.status= '"+status);
				hql.append(" '");
			}
			
			hql.append(" ORDER BY e.insertTime DESC");
			
			pageUtil = dao.getPage(pageUtil, hql.toString(), list.toArray());
			
			if (!QwyUtil.isNullAndEmpty(pageUtil)) {
				return pageUtil;
			}
			
			return null;
			
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		
		
		return null;
	}
	
	
	/**
	 * 根据ID修改状态
	 */
	public boolean updateStatusById(String id) {
		ExcpCzUsers excp = (ExcpCzUsers) dao.findById(new ExcpCzUsers(), id);
		if (!QwyUtil.isNullAndEmpty(excp)) {
			if (!QwyUtil.isNullAndEmpty(excp.getStatus()) && excp.getStatus().equals("0")) {
				excp.setStatus("1");
			} else {
				excp.setStatus("1");
			}
			excp.setUpdateTime(new Date());
		}
		dao.saveOrUpdate(excp);
		return true;
	}
	
}
