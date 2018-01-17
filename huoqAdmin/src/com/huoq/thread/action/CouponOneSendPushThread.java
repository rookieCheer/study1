/**
 * hongshiwl.com Inc.
 * Copyright (c) 2015-2017 All Rights Reserved.
 */
package com.huoq.thread.action;

import com.huoq.thread.bean.CouponSendPushThreadBean;
import com.huoq.thread.model.CouponPush;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author guoyin.yi
 * @version $Id: CouponOneSendPushThread.java, v 0.1  2017/12/19 Exp $
 */
@Service
public class CouponOneSendPushThread implements Runnable{

    @Resource
    CouponSendPushThreadBean couponSendPushThreadBean;

    private static ResourceBundle resbsms = ResourceBundle.getBundle("sms-notice");

    @Override
    public void run() {
        try {

            String message = resbsms.getString("PUSH_ONE_MESSAGE");
//            List<CouponPush> list = couponSendPushThreadBean.queryOverTimeSeven("1");
            List<CouponPush> list = new ArrayList<>();
            CouponPush couponPush = new CouponPush();
            couponPush.setUsersId(1886L);
            couponPush.setCouponCount(2L);
            list.add(couponPush);
            couponSendPushThreadBean.queryCupon( list,message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
