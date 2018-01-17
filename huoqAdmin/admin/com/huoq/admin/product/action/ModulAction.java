package com.huoq.admin.product.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;

import com.huoq.admin.product.bean.ModulBean;
import com.huoq.common.action.BaseAction;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.Modul;
import com.huoq.orm.RolesRight;
import com.huoq.orm.UsersAdmin;
import com.huoq.thread.dao.ThreadDAO;

/**后台管理--模块功能;
 * @author 覃文勇
 *
 * @createTime 2015年8月8日 16:02:01
 */
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/Admin")
	@Results({ 
		@Result(name = "noLogin", value = "/Product/loginBackground.jsp" ,type=org.apache.struts2.dispatcher.ServletRedirectResult.class),
		@Result(name = "saveModul", value = "/Product/Admin/functionManager/replaseFunction.jsp"),
		@Result(name = "findModul", value = "/Product/Admin/functionManager/modullist.jsp")
		
			  
})
public class ModulAction extends BaseAction {
	
	@Resource
	private ModulBean bean;
	private Modul modul;
	private String type;
	
	
	@Resource
	private ThreadDAO threadDAO;
	/**
	 * 保存
	 * @return
	 */
	public String saveModul(){
		try {
			UsersAdmin admin = (UsersAdmin) getRequest().getSession()
					.getAttribute("usersAdmin");
			if (QwyUtil.isNullAndEmpty(admin)) {
				return "noLogin";
			}
			if (!QwyUtil.isNullAndEmpty(modul)) {
				if (!QwyUtil.isNullAndEmpty(bean.saveModul(modul, admin.getId()))) {
					request.setAttribute("message", "添加成功");
					//获取所有权限;
					List<Modul> listModul = threadDAO.getModul();
					getRequest().getServletContext().setAttribute("listModul", listModul);
				} else {
					request.setAttribute("message", "添加失败");
				}
			} else {
				request.setAttribute("message", "添加失败");
			}
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return "saveModul";
	}
	/**
	 * 根据Type查询
	 * @return
	 */
	public String findModulByType(){
		String json = "";
		try {
				if(!QwyUtil.isNullAndEmpty(type)){
					List<Modul> list=bean.findModulsByType(type);
					if(!QwyUtil.isNullAndEmpty(list)){
						json = QwyUtil.getJSONString("ok", bean.getArray(list));
						QwyUtil.printJSON(response, json);
						return null;
					}
				}
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return null;
	}
	/**
	 * 查询所有的模块
	 * @return
	 */
	public String findModul(){
		String json="";
		try {
			 UsersAdmin admin=(UsersAdmin)getRequest().getSession().getAttribute("usersAdmin");
				if(QwyUtil.isNullAndEmpty(admin)){
					json = QwyUtil.getJSONString("err", "管理员未登录");
					QwyUtil.printJSON(getResponse(), json);
					//管理员没有登录;
					return null;
				}
				String superName="0EA5D6BC23E8EEC78F62546B9F68BABFA96976B775889BA625DB6D764FD0DBD42A1C0F45F85B0DE8";
				if(!superName.equals(admin.getUsername())){
				if(isExistsQX("模块列表展示", admin.getId())){
					getRequest().setAttribute("err", "您没有操作该功能的权限!");
					return "err";
				}
				}
			//List<Modul> list=bean.findModulsByType(null);
			List<Modul> listModul = (List<Modul>)getRequest().getServletContext().getAttribute("listModul");
			List<Modul> newListModul = new ArrayList<Modul>();
			newListModul.addAll(listModul);
			if(!QwyUtil.isNullAndEmpty(listModul)){
				Collections.sort(newListModul, new ModulCompare());
				request.setAttribute("list", newListModul);
			}
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return "findModul";
	}
	
	/**修改模块表数据;
	 * 
	 * @author qwy
	 * @return null
	 */
	public String modifyModul() {
		String json = "";
		try {
			UsersAdmin admin = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
			if (QwyUtil.isNullAndEmpty(admin)) {
				json = QwyUtil.getJSONString("noLogin", "登录已失效,请重新登录");
				QwyUtil.printJSON(response, json);
				return null;
			}
			if(-1!=admin.getUserType().longValue()){
				json = QwyUtil.getJSONString("error", "没有操作权限，请联系超级管理员！");
				QwyUtil.printJSON(response, json);
				return null;
			}
			if (!QwyUtil.isNullAndEmpty(modul)) {
				boolean isOk = bean.modifyModul(modul);
				if(isOk){
					json = QwyUtil.getJSONString("ok", "修改成功");
					//获取所有权限;
					List<Modul> listModul = threadDAO.getModul();
					getRequest().getServletContext().setAttribute("listModul", listModul);
					
					//获取所有用户的一级标题权限;
					List<RolesRight> firstRolesRight = threadDAO.getFirstRolesRight();
					getRequest().getServletContext().setAttribute("firstRolesRight", firstRolesRight);
					
					//获取用户的权限;
					List<RolesRight> listRolesRight = threadDAO.getRolesRight();
					getRequest().getServletContext().setAttribute("listRolesRight", listRolesRight);
				}
				else{
					json = QwyUtil.getJSONString("error", "修改失败");
				}
				QwyUtil.printJSON(response, json);
				return null;
			}else{
				json = QwyUtil.getJSONString("error", "参数错误!");
				QwyUtil.printJSON(response, json);
				return null;
			}
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return null;
	}
	
	public Modul getModul() {
		return modul;
	}
	public void setModul(Modul modul) {
		this.modul = modul;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	class ModulCompare implements Comparator<Modul>{

		@Override
		public int compare(Modul o1, Modul o2) {
			Date d1 = o1.getUpdateTime();
			Date d2 = o2.getUpdateTime();
			if(QwyUtil.isNullAndEmpty(d1))
				return -1;
			if(QwyUtil.isNullAndEmpty(d2))
				return -1;
			if(d1.after(d2))
				return -1;
			else if(d1.equals(d2))
				return 0;
			else
				return 1;
		}
		
	}
}
