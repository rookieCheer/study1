package com.huoq.common.bean;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;


@Service
public class SMSNoticeBean {
    public static ExecutorService executorService =   Executors.newFixedThreadPool(100);
	@Resource
	SMSNotifyBean smsNotifyBean;
    /**
     * 在线程池总进行短信发送
     * 
     * @param content
     * @param phone
     */
    public void sendSMSInThreadPool(final String shortUrl,final String phone, final String content, final Object[] obj){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
             	smsNotifyBean.sendSMSNotify(shortUrl,phone, content,obj);
            }
        });

        executorService.execute(thread);
    }
}
