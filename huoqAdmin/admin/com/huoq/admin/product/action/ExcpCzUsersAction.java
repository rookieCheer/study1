package com.huoq.admin.product.action;

import java.io.IOException;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;

import com.huoq.admin.product.bean.ExcpCzUsersBean;
import com.huoq.common.action.BaseAction;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.ExcpCzUsers;
import com.huoq.orm.UsersAdmin;

/**
 * 异常充值用户记录
 *    wxl   2017年2月25日15:21:11
 */
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/Admin")
@Results({ @Result(name = "excp", value = "/Product/Admin/productManager/excpCzUsers.jsp")
})
public class ExcpCzUsersAction extends BaseAction {

	@Resource
	private ExcpCzUsersBean bean;

	private Integer currentPage = 1;
	private Integer pageSize = 50;
	private String status;
	private String name;
	private String insertTime;
	private String id;
	
	protected static Logger log = Logger.getLogger(BaseAction.class);

	/**
	 * 加载异常充值用户记录
	 * @return
	 */
	public String loadExcpCzUsers() {
		String json = "";
		try {

			UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
			if (QwyUtil.isNullAndEmpty(users)) {
				json = QwyUtil.getJSONString("err", "管理员未登录");
				QwyUtil.printJSON(getResponse(), json);
				// 管理员没有登录;
				return null;
			}
			PageUtil<ExcpCzUsers> pageUtil = new PageUtil<ExcpCzUsers>();
			pageUtil.setCurrentPage(currentPage);
			pageUtil.setPageSize(pageSize);
			StringBuffer url = new StringBuffer();
			url.append(getRequest().getServletContext().getContextPath());
			url.append("/Product/Admin/excpCzUsers!loadExcpCzUsers.action?");
			if (!QwyUtil.isNullAndEmpty(insertTime)) {
				url.append("&insertTime=");
				url.append(insertTime);
			}
			if (!QwyUtil.isNullAndEmpty(name)) {
				url.append("&name=");
				url.append(name);
			}
			if (!QwyUtil.isNullAndEmpty(status)) {
				url.append("&status=");
				url.append(status);
			}

			pageUtil.setPageUrl(url.toString());

			pageUtil = bean.loadExcpCzUsersList(pageUtil,status,insertTime,name);
			getRequest().setAttribute("name", name);
			getRequest().setAttribute("status", status);
			getRequest().setAttribute("insertTime", insertTime);
			if (!QwyUtil.isNullAndEmpty(pageUtil)) {
				getRequest().setAttribute("pageUtil", pageUtil);
				getRequest().setAttribute("list", pageUtil.getList());
				return "excp";
			}

		} catch (Exception e) {
			log.error("操作异常: ", e);
			json = QwyUtil.getJSONString("err", "查询记录异常");
		}
		return null;
	}
	
	/**
	 * 更改状态
	 * @return
	 */
	public String updateStatus() {
		String json = "";
		try {
			UsersAdmin users = (UsersAdmin) getRequest().getSession()
					.getAttribute("usersAdmin");
			if (QwyUtil.isNullAndEmpty(users)) {
				json = QwyUtil.getJSONString("err", "管理员未登录");
				QwyUtil.printJSON(getResponse(), json);
				//管理员没有登录;
				return null;
			}
			if (!QwyUtil.isNullAndEmpty(id)) {
				if(bean.updateStatusById(id)){
					request.setAttribute("update", "ok");
					json = QwyUtil.getJSONString("ok", "成功");
					QwyUtil.printJSON(getResponse(), json);
					return null;
				}
			}
			
		} catch (Exception e) {
			log.error("操作异常: ",e);
			log.error("操作异常: ",e);
		}
		json = QwyUtil.getJSONString("error", "失败");
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			log.error("操作异常: ",e);
		}
		return null;	
		
	}
	
	
	public Integer getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(String insertTime) {
		this.insertTime = insertTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


}
