/**
 * hongshiwl.com Inc.
 * Copyright (c) 2015-2017 All Rights Reserved.
 */
package com.huoq.thread.action;

import com.huoq.thread.bean.CouponSendPushThreadBean;
import com.huoq.thread.model.CouponPush;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author guoyin.yi
 * @version $Id: CouponSevenSendPushThread.java, v 0.1  2017/12/19 Exp $
 */
@Service
public class CouponSevenSendPushThread implements Runnable{

    @Resource
    CouponSendPushThreadBean couponSendPushThreadBean;

    private static ResourceBundle resbsms = ResourceBundle.getBundle("sms-notice");

    @Override
    public void run() {
        try {
            String message = resbsms.getString("PUSH_SEVEN_MESSAGE");
            List<CouponPush> list = couponSendPushThreadBean.queryOverTimeSeven("7");
            couponSendPushThreadBean.queryCupon( list,message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
