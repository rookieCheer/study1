/**
 * hongshiwl.com Inc.
 * Copyright (c) 2015-2017 All Rights Reserved.
 */
package com.huoq.thread.action;

import com.huoq.util.MyLogger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ResourceBundle;

/**
 * 理财券到期提醒用户推送消息
 * @author guoyin.yi
 * @version $Id: CuponSendPushThread.java, v 0.1  2017/12/12 Exp $
 */
@Service
public class CuponSendPushThread implements Runnable{
    private MyLogger log = MyLogger.getLogger(CuponSendPushThread.class);

    private static ResourceBundle resbsms = ResourceBundle.getBundle("sms-notice");

    @Resource
    private CouponOneSendPushThread couponOneSendPushThread;

    @Resource
    private CouponThreeSendPushThread couponThreeSendPushThread;

    @Resource
    private CouponSevenSendPushThread couponSevenSendPushThread;

    @Override
    public void run() {
        long st =  System.currentTimeMillis();
        try {
            log.info("###############  提现用户有理财券或红包即将过期 #########"+Thread.currentThread().getName());

            try {
                Thread upt = new Thread(couponOneSendPushThread);
                upt.start();
                upt.join();
            } catch (Exception e) {
                log.error(e);
            }
            try {
                Thread upt = new Thread(couponThreeSendPushThread);
                upt.start();
                upt.join();
            } catch (Exception e) {
                log.error(e);
            }
            try {
                Thread upt = new Thread(couponSevenSendPushThread);
                upt.start();
                upt.join();
            } catch (Exception e) {
                log.error(e);
            }
        } catch (Exception e) {
            log.error("操作异常: ",e);
            log.error(e);
        }
        long et = System.currentTimeMillis();
        log.info("###############  提现用户有理财券或红包即将过期 执行完毕,耗时: "+(et-st));

    }

}
