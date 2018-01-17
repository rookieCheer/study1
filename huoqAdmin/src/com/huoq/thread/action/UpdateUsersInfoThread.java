package com.huoq.thread.action;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.UsersInfo;
import com.huoq.thread.bean.UpdateUsersInfoThreadBean;
@Service
public class UpdateUsersInfoThread implements Runnable {
	private Logger log = Logger.getLogger(UpdateUsersInfoThread.class);
	private Integer pageSize = 50;
	@Resource
	private UpdateUsersInfoThreadBean updateUsersInfoThreadBean;
	
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		try {
			log.info("=================启动更新用户信息线程.......=================");
			PageUtil<UsersInfo> pageUtil = new PageUtil<UsersInfo>();
			pageUtil.setPageSize(pageSize);
				int currentPage = 0;
				for (;;) {
					currentPage++;
					pageUtil.setCurrentPage(currentPage);
					pageUtil = (PageUtil<UsersInfo>) updateUsersInfoThreadBean.queryUsersInfo(pageUtil);
					List<UsersInfo> list = pageUtil.getList();
					if(QwyUtil.isNullAndEmpty(list)){
						log.info("后台线程: 用户信息更新结束!");
						break;
					}
					//修改用户信息的性别，年龄，生日;
					for (UsersInfo usersInfo : list) {
						if(!QwyUtil.isNullAndEmpty(DESEncrypt.jieMiIdCard(usersInfo.getIdcard()))){
							updateUsersInfoThreadBean.updateUsersInfo(usersInfo);
						}

					}
				}
		} catch (Exception e) {
			log.error("进入修改用户信息的后台线程异常: ",e);
		}
		
		
	}
}
