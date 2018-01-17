package com.huoq.admin.product.action;

import java.io.IOException;

import javax.annotation.Resource;

import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;

import com.huoq.admin.product.bean.UsersVIPBeatBean;
import com.huoq.common.action.BaseAction;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.UsersAdmin;
import com.huoq.orm.UsersVIPBeat;

/**
 * 后台管理--用户投资状况 数据表
 *
 * @author wxl
 * @createTime 2017年3月28日10:48:42
 */
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/Admin")
@Results({@Result(name = "vip", value = "/Product/Admin/operationManager/addVipUsers.jsp")

})
public class UsersVIPBeatAction extends BaseAction {
	
	@Resource
	private UsersVIPBeatBean usersVIPBeatBean;
	
	private Integer currentPage = 1;
    private Integer pageSize = 100;
	
    private String username;
    private String insertTime;
    private String status;
    private Double rate;
    
	private UsersVIPBeat usersVIPBeat;
	
	/**
	 * 分页加载VIP内测用户
	 * @return
	 */
	public String loadUsersVipList() {
		String json = "";
		 try {
			 UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
			 if (QwyUtil.isNullAndEmpty(users)) {
	                json = QwyUtil.getJSONString("err", "管理员未登录");
	                QwyUtil.printJSON(getResponse(), json);
	                //管理员没有登录;
	                return null;
	            }
			 	//分页对象设置
	            PageUtil<UsersVIPBeat> pageUtil = new PageUtil<UsersVIPBeat>();
	            pageUtil.setCurrentPage(currentPage);
	            pageUtil.setPageSize(pageSize);
	            StringBuffer url = new StringBuffer();
	            url.append(getRequest().getServletContext().getContextPath());
	            url.append("/Product/Admin/usersVIPBeat!loadUsersVipList.action?");
				
				if (!QwyUtil.isNullAndEmpty(insertTime)) {
	                url.append("&insertTime=");
	                url.append(insertTime);
	            }
				if (!QwyUtil.isNullAndEmpty(username)) {
					url.append("&username=");
		            url.append(username);
				}
				if (!QwyUtil.isNullAndEmpty(status)) {
					url.append("&status=");
		            url.append(status);
				}
				/*if (!QwyUtil.isNullAndEmpty(rate)) {
					url.append("&rate=");
		            url.append(rate);
				}*/
				request.setAttribute("username", username);
				request.setAttribute("insertTime", insertTime);
				request.setAttribute("status", status);
				request.setAttribute("rate", rate);
				
	            pageUtil.setPageUrl(url.toString());
	            
	            pageUtil = usersVIPBeatBean.getUsersVIPList(pageUtil, username, status, rate,insertTime);
	            if (!QwyUtil.isNullAndEmpty(pageUtil)) {
	            	 getRequest().setAttribute("list", pageUtil.getList());
	                 getRequest().setAttribute("pageUtil", pageUtil);
	                 getRequest().setAttribute("table", "1");
				}   

	        } catch (Exception e) {
	            log.error("操作异常: ",e);
	        }
	        return "vip";
	}
	
	
	/**
	 * 添加VIP内测用户
	 * @return
	 */
	public String addVIPUsers(){
		String json="";
		try {
			UsersAdmin admin=(UsersAdmin)getRequest().getSession().getAttribute("usersAdmin");
			if(QwyUtil.isNullAndEmpty(admin)){
				json = QwyUtil.getJSONString("err", "管理员未登录");
				QwyUtil.printJSON(getResponse(), json);
				//管理员没有登录;
				return null;
			}
			if(!QwyUtil.isNullAndEmpty(usersVIPBeat)){
				if(!QwyUtil.isNullAndEmpty(usersVIPBeat.getUsername())){
					//该用户是否已经存在
					UsersVIPBeat vip = usersVIPBeatBean.findByUsername(usersVIPBeat.getUsername());
					if (QwyUtil.isNullAndEmpty(vip)) {
						//不存在则添加  返回ID不为空则添加成功
						String id = usersVIPBeatBean.addUsersVip(usersVIPBeat.getUsername());
						if(!QwyUtil.isNullAndEmpty(id)){
							request.setAttribute("message", "添加成功");	
						}else{
							request.setAttribute("message", "添加失败");
						}
					}else{
						request.setAttribute("message", "该用户已存在");
					}
					
				}else{
					request.setAttribute("message", "用户名不能为空");
				}
			}else{
				request.setAttribute("message", "VIP对象不能为空");
			}
				
		} catch (IOException e) {
			log.error("操作异常: ",e);
		}
		return "vip";
	}
	
	/**
	 * 修改用户信息
	 * @return
	 */
	public String modifyVip(){
		String json = "";
		try {
			if(!QwyUtil.isNullAndEmpty(usersVIPBeat)){
				if(usersVIPBeatBean.modifyUsersVip(usersVIPBeat.getId(), usersVIPBeat.getNote(),usersVIPBeat.getVip(),usersVIPBeat.getAddInterestRates())){
					json = QwyUtil.getJSONString("ok","修改成功");
				}else{
					json = QwyUtil.getJSONString("error","修改失败");
				}
			}else{
				json = QwyUtil.getJSONString("error","VIP用户为空");
			}
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("error", "VIP用户修改异常");
		}
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			log.error("操作异常: ",e);
		}
		return null;
	}

	/**
	 * 更改VIP用户状态
	 * @return
	 */
	public String updateStatuById() {
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
			if(usersVIPBeatBean.updateStatusById(usersVIPBeat.getId())){
				request.setAttribute("update", "ok");
				json = QwyUtil.getJSONString("ok", "成功");
				QwyUtil.printJSON(getResponse(), json);
				return null;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(String insertTime) {
		this.insertTime = insertTime;
	}

	public UsersVIPBeat getUsersVIPBeat() {
		return usersVIPBeat;
	}

	public void setUsersVIPBeat(UsersVIPBeat usersVIPBeat) {
		this.usersVIPBeat = usersVIPBeat;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Double getRate() {
		return rate;
	}

	public void setRate(Double rate) {
		this.rate = rate;
	}
	
}
