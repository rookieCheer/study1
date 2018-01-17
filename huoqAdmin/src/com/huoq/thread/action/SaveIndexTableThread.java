package com.huoq.thread.action;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.common.util.QwyUtil;
import com.huoq.thread.bean.SaveIndexTableThreadBean;

/**
 * 保存首页表格
 * @author Administrator
 *
 */
@Service
public class SaveIndexTableThread implements Runnable{
	private static Logger log = Logger.getLogger(UpdateQdtjPlatformThread.class); 
	@Resource
	private SaveIndexTableThreadBean bean;
	@Override
	public synchronized void run() {
		long st = System.currentTimeMillis();
		try {
			log.info("----------执行更新-----【将首页信息存入数据库】-------当前时间:"+QwyUtil.fmyyyyMMddHHmmss.format(new Date()));
			//获取昨天的时间
			String yesterday = QwyUtil.fmyyyyMMdd.format(QwyUtil.addDaysFromOldDate(new Date(), -1).getTime());
			//保存昨天的数据
			bean.saveIndexTbale(yesterday);
		} catch (Exception e) {
			log.error(e);
		}
		long et = System.currentTimeMillis();
		log.info("--------执行更新-----【将首页信息存入数据库】------耗时:"+(et-st));
	}
}
