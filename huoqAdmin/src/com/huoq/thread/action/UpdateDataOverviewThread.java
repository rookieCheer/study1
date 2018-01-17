package com.huoq.thread.action;

import com.huoq.common.util.QwyUtil;
import com.huoq.thread.bean.UpdateDataOverviewThreadBean;
import com.huoq.util.MyLogger;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * 更新平台资金概览
 * @author Administrator
 *
 */
@Service
public class UpdateDataOverviewThread implements Runnable{
	private static MyLogger log = MyLogger.getLogger(UpdateDataOverviewThread.class);
	@Autowired
	private UpdateDataOverviewThreadBean bean;
	@Override
	public synchronized void run() {
		long st = System.currentTimeMillis();
		try {
			log.info("----------执行更新-----【平台资金概览,平台累计充值金额】-------当前时间:"+QwyUtil.fmyyyyMMddHHmmss.format(new Date()));
			//获取昨天时间
			String yesterday = QwyUtil.fmyyyyMMdd.format(QwyUtil.addDaysFromOldDate(new Date(), -1).getTime());
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
			Date newyesterday = sd.parse(yesterday);
			//更新平台平台资金概览数据
			bean.updateDataOverview(newyesterday);
		} catch (Exception e) {
			log.error(e);
		}
		long et = System.currentTimeMillis();
		log.info("--------执行更新-----【平台资金概览,平台累计充值金额】------耗时:"+(et-st));
	}
}
