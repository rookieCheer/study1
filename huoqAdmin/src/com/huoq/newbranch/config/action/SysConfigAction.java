package com.huoq.newbranch.config.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;

import com.huoq.common.action.BaseAction;
import com.huoq.common.util.MyRedis;
import com.huoq.common.util.ObjectTranscoder;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.newbranch.config.service.SysConfigService;
import com.huoq.newbranch.constants.Constants;
import com.huoq.newbranch.orm.SysConfigNew;
import com.huoq.orm.UsersAdmin;

/**
 * 后台管理--系统配置
 * 
 * @author zx
 * @createTime 2017年6月15日 10:30:47
 */
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/Admin")
// 发布产品页面
@Results({ @Result(name = "sysConfig", value = "/Product/Admin/sysconfig/sysConfigIndex.jsp"),
		  @Result(name = "edit", value = "/Product/Admin/sysconfig/sysConfigEdit.jsp"),
		  @Result(name = "add", value = "/Product/Admin/sysconfig/sysConfigAdd.jsp")
		  })
public class SysConfigAction extends BaseAction {
	@Resource
	private SysConfigService bean;
	private SysConfigNew config;
	//分页参数
	private Integer currentPage = 1;
	private Integer pageSize = 20;
	/**
	 * 跳转到系统配置详细信息
	 */
	public String getSysConfig() {
		String json = "";
		try {
			UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
			if (QwyUtil.isNullAndEmpty(users)) {
				json = QwyUtil.getJSONString("err", "管理员未登录");
				QwyUtil.printJSON(getResponse(), json);
				// 管理员没有登录;
				return null;
			}
			String superName = "0EA5D6BC23E8EEC78F62546B9F68BABFA96976B775889BA625DB6D764FD0DBD42A1C0F45F85B0DE8";
			if (!superName.equals(users.getUsername())) {
				if (isExistsQX("配置管理", users.getId())) {
					getRequest().setAttribute("err", "您没有操作该功能的权限!");
					return "err";
				}
			}
			
			PageUtil<SysConfigNew> pageUtil=new PageUtil<SysConfigNew>();
			pageUtil.setCurrentPage(currentPage);
		    pageUtil.setPageSize(pageSize);
		    
		    StringBuffer url = new StringBuffer();
			url.append(getRequest().getServletContext().getContextPath());
			url.append("/Product/Admin/sysConfig!getSysConfig.action?");
			pageUtil.setPageUrl(url.toString());
		    
			pageUtil =  bean.loadSysConfig(pageUtil);
			List<SysConfigNew> list =  pageUtil.getList();
			request.setAttribute("list", list);
			request.setAttribute("pageUtil", pageUtil);
			return "sysConfig";
		} catch (Exception e) {
			log.error("操作异常: ", e);
			json = QwyUtil.getJSONString("err", "查询记录异常");
		}
		return null;
	}
	/**
	 * 删除功能
	 */
	public void delete(){
		response.setContentType("application/json;charset=GBK");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
			if (QwyUtil.isNullAndEmpty(users)) {
				out.println("0");
				return;
			}
			String id =  request.getParameter("id").toString().trim();
			
			if(id == null || "".equals(id)){
				out.println("0");
				return;
			}
			SysConfigNew config = bean.getSysConfigById(Long.valueOf(id));
			if(config != null){
				config.setIsDelete("0");
				bean.updateConfig(config);
				out.println("1");
				return;
			}else{
				out.println("0");
				return;
			}
			
		} catch (IOException e) {
			log.error("操作异常: ",e);
		}finally{
			if(out !=null){
				out.flush();
				out.close();
			}
		}
	}
	/**
	 * 进入添加页面
	 * @return
	 */
	public String edit(){
		try {
			String id =  request.getParameter("id").toString().trim();
			
			if(id == null || "".equals(id)){
				log.error("操作异常,要修改的id为空，修改失败!");
				return null;
			}
			SysConfigNew config = bean.getSysConfigById(Long.valueOf(id));
			request.setAttribute("id", config.getId());
			request.setAttribute("name", config.getName());
			request.setAttribute("note", config.getNote());
			request.setAttribute("type", config.getType());
			request.setAttribute("state", config.getState());
			request.setAttribute("code", config.getCode());
			
			return "edit";
		} catch (Exception e) {
			log.error("操作异常: ",e);
		} 
		return null;
	}
	/**
	 * 添加操作
	 * @return
	 */
	
	public String editDo(){
		try {
			UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
			String json = "";
			if (QwyUtil.isNullAndEmpty(users)) {
				json = QwyUtil.getJSONString("err", "管理员未登录");
				QwyUtil.printJSON(getResponse(), json);
				// 管理员没有登录;
				return null;
			}
			String id =  request.getParameter("id").toString().trim(); 
			String name =  request.getParameter("name").toString().trim();
			String note =  request.getParameter("note").toString().trim();
			String type =  request.getParameter("type").toString().trim();
			String state =  request.getParameter("state").toString().trim();
			SysConfigNew config = bean.getSysConfigById(Long.valueOf(id));
			if(config != null){
				config.setName(name);
				config.setNote(note);
				config.setState(state);
				config.setType(type);
				config.setModifier(users.getUsername());
				config.setUpdateTime(new Timestamp(new Date().getTime()));
				bean.updateConfig(config);
				request.setAttribute("isOk", "ok");
			}else{
				request.setAttribute("isOk", "error");
			}
			
			return "edit";
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return null;
		
	}
	/**
	 * 进入添加页面
	 * @return
	 */
	public String add(){
		return "add";
	}
	/**
	 * 添加操作
	 * @return
	 */
	public String addDo(){
		try {
			UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
			String json = "";
			if (QwyUtil.isNullAndEmpty(users)) {
				json = QwyUtil.getJSONString("err", "管理员未登录");
				QwyUtil.printJSON(getResponse(), json);
				// 管理员没有登录;
				return null;
			}
			String name =  request.getParameter("name").toString().trim();
			String note =  request.getParameter("note").toString().trim();
			String type =  request.getParameter("type").toString().trim();
			String state =  request.getParameter("state").toString().trim();
			String code =  request.getParameter("code").toString().trim();
			SysConfigNew config = new SysConfigNew();
			
			config.setName(name);
			config.setCode(code);;
			config.setNote(note);
			config.setState(state);
			config.setType(type);
			config.setModifier(users.getUsername());
			config.setCreator(users.getUsername());
			config.setCreateTime(new Timestamp(new Date().getTime()));
			config.setUpdateTime(new Timestamp(new Date().getTime()));
			config.setIsDelete("1");
			bean.addConfig(config);
			request.setAttribute("isOk", "ok");
			
			return "add";
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return null;
	}
	/**
	 * 添加时校验code是否唯一
	 */
	public void checkCodeIsOnly(){
		response.setContentType("application/json;charset=GBK");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			
			String code =  request.getParameter("code").toString().trim();
			
			if(code == null || "".equals(code)){
				out.println("0");
				log.error("code不能为空");
				return;
			}
			List<SysConfigNew> codeList = bean.getCodeList("SysConfig");
			boolean flag = true;
			for(SysConfigNew config:codeList){
				if(code.equals(config.getCode())){
					flag = false;
					break;
				}
			}
			if(flag){
				log.info("code可用");
				out.println("1");
			}else{
				log.info("code已经存在");
				out.println("0");
			}
			
		} catch (IOException e) {
			log.error("操作异常: ",e);
		}finally{
			if(out !=null){
				out.flush();
				out.close();
			}
		}
		
	}
	
	/**
	 * 刷新缓存
	 */
	public String reflushRedis(){
		List<SysConfigNew> sysConfigList = null;
		try {
			sysConfigList = bean.getCodeList("SysConfig");
			MyRedis yibu=new MyRedis();
			for(SysConfigNew config:sysConfigList){
				yibu.delObject(Constants.PROOJECTNNAME+ config.getCode());
			}
			for(SysConfigNew config:sysConfigList){
				yibu.setObject(Constants.PROOJECTNNAME +config.getCode(), config);
			}
			request.setAttribute("isReflush", "ok");
			
		} catch (Exception e) {
			log.error("刷新缓存出现异常",e);
			log.error("操作异常: ",e);
		}
		return "sysConfig";
	}
	
}
