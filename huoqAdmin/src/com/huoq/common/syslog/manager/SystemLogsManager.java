package com.huoq.common.syslog.manager;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.huoq.common.syslog.dao.SystemLogsDao;
import com.huoq.newbranch.orm.SysLogs;

@Service
public class SystemLogsManager {
	
	@Resource
	private SystemLogsDao systemLogsDao;
	
	/**
	 * Description:保存系统入参日志
	 * @author  changhaipeng
	 * @date 2017年6月29日 下午1:37:08
	 */
	public String saveSysLogs(SysLogs sysLogs) throws Exception{
		return systemLogsDao.save(sysLogs);
	}
}
