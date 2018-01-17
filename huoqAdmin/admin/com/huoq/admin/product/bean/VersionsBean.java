package com.huoq.admin.product.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.admin.product.dao.VersionsDAO;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.Versions;

@Service
public class VersionsBean {
	@Resource
	VersionsDAO dao;
	private static Logger log = Logger.getLogger(VersionsBean.class); 
	/**
	 * 查找IOS版本信息
	 * @param type  0 iOS 1 Android 
	 * @return
	 */
	public List<Versions> findVersions(String type,String status) throws Exception{
		List<Object> list=new ArrayList<Object>();
		StringBuffer hql=new StringBuffer();
		hql.append(" FROM Versions v ");
		hql.append(" WHERE 1 = 1 ");
		if(!QwyUtil.isNullAndEmpty(type)){
			hql.append(" AND v.type = ? ");
			list.add(type);
		}
		if(!QwyUtil.isNullAndEmpty(status)){
			hql.append(" AND v.status = ? ");
			list.add(status);
		}
		hql.append(" ORDER BY v.insertTime DESC");
		
		return dao.LoadAll(hql.toString(), list.toArray());
	}
	
	/**
	 * 修改版本为过期状态
	 * @param type 0 IOS 1 Android
	 */
	public boolean updateStatus(String type){
		List<Object> list=new ArrayList<Object>();
		StringBuffer sql=new StringBuffer();
		sql.append(" update versions set status = '1' ");
		if(!QwyUtil.isNullAndEmpty(type)){
			sql.append("where type = ? ");
			list.add(type);
		}
		dao.updateBySql(sql.toString(), list.toArray());
		return true;
	}
	
	/**
	 * 根据ID查询版本信息
	 * @param id
	 * @return
	 */
	public Versions findVersionsById(String id){
		return (Versions) dao.findById(new Versions(), id);
	}
	
	/**
	 * 根据ID修改版本信息
	 */
	public boolean updateStatusById(String id){
		Versions versions=findVersionsById(id);
		if(!QwyUtil.isNullAndEmpty(versions)){
			if(!QwyUtil.isNullAndEmpty(versions.getStatus())&&versions.getStatus().equals("1")){
				versions.setStatus("0");
			}else{
				versions.setStatus("1");
			}
		}
		dao.saveOrUpdate(versions);	
		return true;
	}
	/**
	 * 保存版本信息
	 * @param versions
	 * @return
	 */
	public Versions saveVersions(Versions versions){
		try{
			if(updateStatus(versions.getType())){
				versions.setInsertTime(new Date());
				versions.setStatus("0");
				dao.saveOrUpdate(versions);	
				return versions;
			}
		} catch (Exception e) {
			log.info("回滚事务");
			log.error("操作异常: ",e);
			log.error(e.getMessage(),e);
		}
		return null;
	}
	
	/**
	 * 添加查询满足isupdate = 1 type = 1的所有记录
	 * @param type  0 iOS 1 Android 
	 * @return
	 */
	public List<Versions> findVersions(String type){
		List<Object> list=new ArrayList<Object>();
		StringBuffer hql=new StringBuffer();
		hql.append(" FROM Versions v ");
		hql.append(" WHERE v.status = '0' ");
		hql.append(" AND v.type = ? ");
		hql.append(" AND v.isUpdate = '1' ");
		list.add(type);
		hql.append(" ORDER BY v.insertTime DESC");
		List<Versions> versions=dao.LoadAll(hql.toString(), list.toArray());
		return versions;
		
	}

	public void updateVersionsById(Versions versions) {
		dao.getHibernateTemplate().update(versions);
	}
	
}
