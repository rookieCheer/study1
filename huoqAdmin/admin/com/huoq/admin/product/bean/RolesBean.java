package com.huoq.admin.product.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.admin.product.dao.RolesDAO;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.Roles;
import com.huoq.orm.UsersAdmin;

/**
 * @author 覃文勇
 * @createTime 2015-8-10下午2:00:24
 */
@Service
public class RolesBean {
	
	private static Logger log = Logger.getLogger(RolesBean.class);
	
	@Resource
	RolesDAO dao;
	/**
	 * 保存role
	 * @param role
	 * @return
	 */
   public Roles saveRoles(Roles role,UsersAdmin admin){
	   
		   
		try {
//			if(!QwyUtil.isNullAndEmpty(role.getRoleName())){
//			   String  roleName = new String (role.getRoleName().getBytes("ISO-8859-1"),"UTF-8");			
//			   role.setRoleName(roleName);
//			 }
//			if(!QwyUtil.isNullAndEmpty(role.getNote())){
//			   String  note = new String (role.getNote().getBytes("ISO-8859-1"),"UTF-8");
//		       role.setNote(note);
//			 } 
			   role.setStatus(1L);//0:禁用 1：可用
			   role.setInsertTime(new Date());	   
			   role.setUsersAdminId(admin.getId());
			   //role.setUsersAdmin(admin);
			   String id=dao.saveAndReturnId(role);
			   role=(Roles)dao.findById(new Roles(), Long.parseLong(id));
			   } catch (Exception e) {
					log.error("操作异常: ",e);
		}	
	   return role;
   }
   public List<Roles> findListByStatus(Long status){
	   List<Object> objects=new ArrayList<Object>();
	   StringBuffer hql=new StringBuffer();
	   hql.append(" FROM Roles WHERE 1=1 ");
	   if(!QwyUtil.isNullAndEmpty(status)){
		   hql.append(" AND status=? ");
		   objects.add(status);
	   }
	   
	   return dao.LoadAll(hql.toString(), objects.toArray());
   }

   
}
