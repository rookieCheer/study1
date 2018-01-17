package com.huoq.thread.action;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.common.util.PageUtil;
import com.huoq.thread.bean.meowCurrencyReportBean;
import com.huoq.thread.dao.ThreadDAO;
@Service(value="meowCurrencyReportThread")
public class MeowCurrencyReportThread implements Runnable{
	private Logger log = Logger.getLogger(MeowCurrencyReportThread.class);
	private Integer pageSize = 50;

	@Resource
	private meowCurrencyReportBean bean;
	@Resource
	private ThreadDAO threadDAO;
	@Override
	public void run() {
		PageUtil<Object> pageUtil = new PageUtil<Object>();
		pageUtil.setPageSize(pageSize);
		 bean.setCoinDayDetail(pageUtil);
		
	}

}
