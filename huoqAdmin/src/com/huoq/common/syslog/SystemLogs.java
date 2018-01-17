package com.huoq.common.syslog;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.huoq.common.syslog.service.SystemLogsService;
import com.huoq.common.util.QwyUtil;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
/**
 * Description:structs2拦截器方式记录入参日志 2017年6月30日11:00:57
 * @author  changhaipeng
 * @params 
 * @date 2017年6月30日11:01:01
 */
public class SystemLogs extends AbstractInterceptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7924552793736335508L;

	private static Logger log = Logger.getLogger(SystemLogs.class);

	@Resource
	private SystemLogsService systemLogsService;

	ThreadLocal<Long> startTime = new ThreadLocal<Long>();

	@Override
	public void init() {
	}

	/**
	 * Description: 为了避免方法异常导致记录插入失败，使用finally来添加记录
	 * @author  changhaipeng
	 * @date 2017年7月3日 下午4:03:48
	 */
	@Override
	public String intercept(ActionInvocation ai) throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		try {
			startTime.set(System.currentTimeMillis());
			ai.invoke();// 执行被拦截action
		} catch (Exception e) {
			log.info("structs2拦截器日志：" + e);
		} finally{
			
			log.info("拦截器：执行方法后");
			if(!QwyUtil.isNullAndEmpty(request)&&containUrl(request)){
				systemLogsService.saveSysLogs(request,(System.currentTimeMillis()-startTime.get())+"ms");
			}
		}
		
		return Action.SUCCESS;
	}

	@Override
	public void destroy() {
	}
	
	/**
	 * Description:将jsp返回的请求链接过滤掉
	 * @author  changhaipeng
	 * @date 2017年7月3日 下午4:18:38
	 */
	private boolean containUrl(HttpServletRequest request){
		StringBuffer uri = request.getRequestURL();
		if(!QwyUtil.isNullAndEmpty(uri)&&!uri.toString().contains("jsp")&&
				(uri.toString().contains(".action")||
				 uri.toString().contains("!")||uri.toString().contains(".do"))){
			return true;
		}else{
			return false;
		}
	}
}
