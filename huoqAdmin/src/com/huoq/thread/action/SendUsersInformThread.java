/**
 * hongshiwl.com Inc.
 * Copyright (c) 2015-2017 All Rights Reserved.
 */
package com.huoq.thread.action;

import com.huoq.admin.product.bean.InvestorsBean;
import com.huoq.orm.Users;
import com.huoq.orm.UsersInfo;
import com.huoq.thread.bean.SendUsersInformBean;
import com.huoq.util.MyLogger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户认证成功2天后未投资进行通知
 * @author guoyin.yi
 * @version $Id: SendUsersInform.java, v 0.1  2017/12/6 Exp $
 */
@Service
public class SendUsersInformThread implements Runnable {
    private MyLogger logger = MyLogger.getLogger(SendUsersInformThread.class);

    @Resource
    private SendUsersInformBean sendUsersInformBean;

    @Resource
    private InvestorsBean investorsBean;

    @Override
    public void run() {
        logger.info("=============进入 用户认证2天后为投资发短信提醒 ================"+Thread.currentThread().getName());
        List<Users> list = sendUsersInformBean.queryInformUsers();
        for (int i = 0; i < list.size(); i++) {
            Users users = list.get(i);
            //判断用户是否投资
            int count = investorsBean.queryInvestorsByUsersId(users.getId()+"");
            if (count <=0 ) {
                sendUsersInformBean.sendUsersNotify(users);
            }
        }
        logger.info("=============结束 用户认证2天后为投资发短信提醒 ================");
    }
}
