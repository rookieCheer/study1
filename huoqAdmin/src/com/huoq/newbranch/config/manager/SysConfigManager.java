package com.huoq.newbranch.config.manager;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.huoq.common.util.PageUtil;
import com.huoq.newbranch.config.dao.SysConfigDao;
import com.huoq.newbranch.orm.SysConfigNew;
@Service
public class SysConfigManager {
	@Resource
	private SysConfigDao dao;
	
	
	public SysConfigNew getSysConfigByCode(String code) {
		StringBuffer sql = null;
		sql = new StringBuffer("FROM SysConfigNew sc WHERE sc.code = ? ");
		return (SysConfigNew) dao.findJoinActive(sql.toString(), new Object[]{code});
	}


	public PageUtil loadSysConfig(PageUtil<SysConfigNew> pageUtil) {
		ArrayList<Object> ob = new ArrayList<Object>();
		StringBuffer sql = null;//isDelete
		sql = new StringBuffer("FROM SysConfigNew sc  where sc.isDelete = ?");
		ob.add("1");
		sql.append(" order by sc.createTime desc");
		return dao.getPage(pageUtil, sql.toString(), ob.toArray());
	}


	public SysConfigNew getSysConfigById(Long id) {
		return dao.getHibernateTemplate().get(SysConfigNew.class, id);
	}


	public void updateConfig(SysConfigNew config) {
		dao.getHibernateTemplate().update(config);
	}


	public void addConfig(SysConfigNew config) {
		dao.getHibernateTemplate().save(config);
	}


	public List<SysConfigNew> getCodeList(String config) {
		 List<SysConfigNew> list = dao.LoadAll(config);
		 return list;
	}
}
