package com.huoq.common.syslog.service;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.huoq.common.syslog.manager.SystemLogsManager;
import com.huoq.common.util.QwyUtil;
import com.huoq.login.bean.UsersLoginBean;
import com.huoq.newbranch.constants.Constants;
import com.huoq.newbranch.orm.SysLogs;
import com.huoq.orm.UsersAdmin;

import net.sf.json.JSONObject;

@Service
public class SystemLogsService {

	@Resource
	private SystemLogsManager systemLogsManager;
	@Resource
	private UsersLoginBean loginBean;

	/**
	 * Description:记录系统入参日志信息
	 * 
	 * @author changhaipeng
	 * @date 2017年6月26日 下午2:22:43
	 */
	public String saveSysLogs(HttpServletRequest request,String resp_time) throws Exception {
		SysLogs sysLogs = new SysLogs();
		String req_URI = request.getRequestURL().toString();
		String req_type = request.getMethod();
		String parameters = request.getParameter("parameters");
		String user_id = "";
		UsersAdmin users= (UsersAdmin) request.getSession().getAttribute("usersAdmin");
		if(QwyUtil.isNullAndEmpty(users)){
			user_id = "";
		}else{
			user_id = users.getId()+"";
		}
		if ("POST".equals(req_type.toUpperCase())) {
			
			parameters = request.getParameter("parameters");
		} else {
			parameters = request.getQueryString();
		}

		if (req_URI.contains(Constants.API_PROJECT_NAME)) {
			sysLogs.setReq_app(Constants.API_PROJECT_NAME);
		} else if (req_URI.contains(Constants.ADMIN_PROJECT_NAME)) {
			sysLogs.setReq_app(Constants.ADMIN_PROJECT_NAME);
		}
		sysLogs.setReq_params(parameters);
		sysLogs.setReq_type(req_type);
		sysLogs.setReq_url(req_URI);
		sysLogs.setUser_id(user_id);
		sysLogs.setCreate_time(new Date());
		sysLogs.setUpdate_time(new Date());
		sysLogs.setReq_ip(getRealIp(request));
		sysLogs.setIs_delete('0');
		sysLogs.setResp_time(resp_time);
		return systemLogsManager.saveSysLogs(sysLogs);
	}

	
	private static String getRealIp(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (!QwyUtil.isNullAndEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
			// 多次反向代理后会有多个ip值，第一个ip才是真实ip
			int index = ip.indexOf(",");
			if (index != -1) {
				return ip.substring(0, index);
			} else {
				return ip;
			}
		}
		ip = request.getHeader("X-Real-IP");
		if (!QwyUtil.isNullAndEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
			return ip;
		}
		return request.getRemoteAddr();
	}
}
