/**
 * hongshiwl.com Inc.
 * Copyright (c) 2015-2017 All Rights Reserved.
 */
package com.huoq.thread.action;

import com.huoq.orm.UsersInfo;
import com.huoq.thread.bean.SendRegistThreadBean;
import com.huoq.util.MyLogger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户注册后一个小时内未实名发短信提醒
 * @author guoyin.yi
 * @version $Id: SendRegistThread.java, v 0.1  2017/12/5 Exp $
 */
@Service
public class SendRegistThread implements Runnable{
    MyLogger logger = MyLogger.getLogger(SendRegistThread.class);

    @Resource
    private SendRegistThreadBean sendRegistThreadBean;

    @Override
    public void run() {
        logger.info("=============进入 用户注册后一个小时内未实名发短信提醒 ================"+Thread.currentThread().getName());
        List<UsersInfo> list = sendRegistThreadBean.queryUsersInfoList();
        for (int i = 0; i < list.size(); i++) {
            UsersInfo usersInfo = list.get(i);
            sendRegistThreadBean.sendNotify(usersInfo);
        }
        logger.info("=============结束 用户注册后一个小时内未实名发短信提醒 ================");
    }
}
