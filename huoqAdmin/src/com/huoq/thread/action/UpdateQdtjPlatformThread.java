package com.huoq.thread.action;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.common.util.QwyUtil;
import com.huoq.thread.bean.UpdateQdtjPlatformThreadBean;
import com.huoq.thread.bean.UpdateQdtjThreadBean;

/**更新 各平台渠道统计汇总表
 * @author 覃文勇
 *
 * 2017年1月21日上午3:53:18
 */
@Service
public class UpdateQdtjPlatformThread implements Runnable {
	private static Logger log = Logger.getLogger(UpdateQdtjPlatformThread.class); 
	@Resource
	private UpdateQdtjPlatformThreadBean updateQdtjPlatformThreadBean;
	@Override
	public synchronized void run() {
		long st = System.currentTimeMillis();
		try {
			log.info("----------执行更新-----【各平台渠道统计汇总表】-------当前时间:"+QwyUtil.fmyyyyMMddHHmmss.format(new Date()));
			updateQdtjPlatformThreadBean.updateQdtjPlatformYestoday();
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		long et = System.currentTimeMillis();
		log.info("--------执行更新-----【各平台渠道统计汇总表】------耗时:"+(et-st));
	}

}
