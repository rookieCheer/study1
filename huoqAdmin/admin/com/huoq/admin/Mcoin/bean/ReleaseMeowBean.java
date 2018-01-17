package com.huoq.admin.Mcoin.bean;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.common.dao.ObjectDAO;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.MProduct;
@Service
public class ReleaseMeowBean {
	private static Logger log = Logger.getLogger(ReleaseMeowBean.class); 
	@Resource(name="objectDAO")
	private ObjectDAO dao;
	
	/**
	 * 获取用户的真实姓名;
	 * @param username
	 * @return
	 */
	public MProduct findTitle(String title){
		Object ob=null;
		try {
			if(QwyUtil.isNullAndEmpty(title))
				return null;
			StringBuffer hql = new StringBuffer();
			hql.append("FROM MProduct us ");
			hql.append("WHERE us.title = ? ");
			ob = dao.findJoinActive(hql.toString(), new Object[]{title});
			if(ob!=null){
				return (MProduct)ob;
			}
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return null;
		
	}
}
