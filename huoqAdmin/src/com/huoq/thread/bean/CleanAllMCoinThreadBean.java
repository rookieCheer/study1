package com.huoq.thread.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.common.dao.ObjectDAO;
import com.huoq.common.util.PageUtil;
import com.huoq.orm.UsersInfo;
@Service
public class CleanAllMCoinThreadBean {
	@Resource(name="objectDAO")
	private ObjectDAO dao;
	private static Logger log = Logger.getLogger(CleanAllMCoinThreadBean.class); 
	
	@SuppressWarnings("unchecked")
	public PageUtil<UsersInfo> loadUserInfo(PageUtil<UsersInfo> pageUtil){
		try {
		//ArrayList<Object> ob = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();
		hql.append(" FROM UsersInfo h WHERE 1=1 and  h.totalPoint != "+ Long.parseLong("0"));
		hql.append(" ORDER BY h.insertTime DESC ");
		return dao.getPage(pageUtil, hql.toString(),null);
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return null;
	}
	
}
