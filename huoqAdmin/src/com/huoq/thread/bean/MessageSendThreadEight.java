package com.huoq.thread.bean;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.admin.product.bean.SmsRecordBean;


/**
 * 枫叶网短信自动发送
 * @author xr
 *
 */
@Service(value ="messageSendThreadEight")


public class MessageSendThreadEight implements Runnable {
	@Resource
	private SmsRecordBean bean;
	
	private Logger log = Logger.getLogger(MessageSendThreadEight.class);
	
	@Override
	public void run() {
			try {
				log.info("进入后台线程....枫叶网短信自动发送！！！");
				bean.sendSMStoHuoQRegisitUsers_1_28_8();
			} catch (Exception e) {
				log.error("操作异常: ", e);
				log.error("进入后台线程....异常", e);
			} catch (Error e) {
				log.error("操作异常: ", e);
				log.error("进入后台线程....error异常", e);
			}
	}

}
