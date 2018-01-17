/**
 * hongshiwl.com Inc.
 * Copyright (c) 2015-2017 All Rights Reserved.
 */
package com.huoq.thread.action;

import com.huoq.common.util.QwyUtil;
import org.apache.log4j.Logger;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 定时短信通知
 * @author guoyin.yi
 * @version $Id: SmsNotifyThread.java, v 0.1  2017/11/20 Exp $
 */
public class SmsNotifyThread implements Runnable {
    private Logger log = Logger.getLogger(SmsNotifyThread.class);


    @Resource
    private SendNotifyProdOverThread sendNotifyProdOverThread;

    @Resource
    private SendNotifySendProfitThread sendNotifySendProfitThread;

    @Override
    public void run() {

        long st =  System.currentTimeMillis();
        try {
            log.info("--------开始执行【发送短信通知接口】---当前时间: "+ QwyUtil.fmyyyyMMddHHmmss.format(new Date()));

            //产品到期短信通知
            try {
                Thread flst = new Thread(sendNotifyProdOverThread);
                flst.start();
                flst.join();
            } catch (Exception e) {
                log.error(e);
            }

        } catch (Exception e) {
            log.error("操作异常: ",e);
            log.error(e);
        }
        long et = System.currentTimeMillis();
        log.info("---------发送短信通知接口执行完毕,耗时: "+(et-st));
    }
}
