package com.huoq.thread.action;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.common.util.QwyUtil;
import com.huoq.thread.bean.UpdateQdtjThreadBean;

/**更新 Android渠道统计汇总表
 * @author 覃文勇
 *
 * 2017年1月21日上午3:53:18
 */
@Service
public class UpdateQdtjThread implements Runnable {
	private static Logger log = Logger.getLogger(UpdateQdtjThread.class); 
	@Resource
	private UpdateQdtjThreadBean updateQdtjThreadBean;
	@Override
	public synchronized void run() {
		long st = System.currentTimeMillis();
		try {
			log.info("----------执行更新-----【Android渠道统计汇总表】-------当前时间:"+QwyUtil.fmyyyyMMddHHmmss.format(new Date()));
			updateQdtjThreadBean.updateQdtjYestoday();
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		long et = System.currentTimeMillis();
		log.info("--------执行更新-----【Android渠道统计汇总表】------耗时:"+(et-st));
	}

}
