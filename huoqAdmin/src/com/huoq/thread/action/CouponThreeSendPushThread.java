/**
 * hongshiwl.com Inc.
 * Copyright (c) 2015-2017 All Rights Reserved.
 */
package com.huoq.thread.action;

import com.huoq.admin.product.bean.NoticeBean;
import com.huoq.thread.bean.CouponSendPushThreadBean;
import com.huoq.thread.bean.JiguangPushBean;
import com.huoq.thread.model.CouponPush;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author guoyin.yi
 * @version $Id: CouponThreeSendPushThread.java, v 0.1  2017/12/18 Exp $
 */
@Service
public class CouponThreeSendPushThread implements Runnable{

    @Resource
    CouponSendPushThreadBean couponSendPushThreadBean;

    private static ResourceBundle resbsms = ResourceBundle.getBundle("sms-notice");

    @Override
    public void run() {
        try {

            String message = resbsms.getString("PUSH_THREE_MESSAGE");
//                List<CouponPush> list = couponSendPushThreadBean.queryOverTimeSeven("3");
            List<CouponPush> list = new ArrayList<>();
            CouponPush c = new CouponPush();
            c.setCouponCount(2L);
            c.setUsersId(1886L);
            c = new CouponPush();
            c.setCouponCount(2L);
            c.setUsersId(1886L);
            list.add(c);
            couponSendPushThreadBean.queryCupon( list,message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
