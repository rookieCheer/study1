/**
 * hongshiwl.com Inc.
 * Copyright (c) 2015-2017 All Rights Reserved.
 */
package com.huoq.thread.bean;

import com.huoq.common.bean.SMSNoticeBean;
import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.LockHolder;
import com.huoq.orm.Users;
import com.huoq.orm.UsersInfo;
import com.huoq.thread.dao.ThreadDAO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

/**
 * 用户注册后一个小时内未实名发短信提醒
 * @author guoyin.yi
 * @version $Id: SendRegistThreadBean.java, v 0.1  2017/12/5 Exp $
 */
@Service
public class SendRegistThreadBean {

    @Resource
    private ThreadDAO dao;

    @Resource
    SMSNoticeBean smsNoticeBean;

    private static ResourceBundle resbSms = ResourceBundle.getBundle("sms-notice");


    public List<UsersInfo> queryUsersInfoList() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH ");
        String dateTime = format.format(new Date()).trim();
        StringBuffer sql = new StringBuffer(" select * from users_info  ");
        sql.append(" where is_verify_idcard= 0 ");
        sql.append(" and TIMESTAMPDIFF(hour,date_format(insert_time,'%Y-%m-%d %H'),'"+dateTime+"') = 1 ");
        return dao.queryAllSql(sql.toString(),null,UsersInfo.class);
    }

    /**
     * 发送短信通知
     * @param usersInfo
     */
    public void sendNotify(UsersInfo usersInfo){
        synchronized (LockHolder.getLock(usersInfo.getUsersId())) {
            String shortUrl = resbSms.getString("SMS_REGIST_SUCC");
            String sendMessage = resbSms.getString("SMS_REGIST_URL");
            Users users = (Users) dao.findById(new Users(), usersInfo.getUsersId());
            smsNoticeBean.sendSMSInThreadPool(shortUrl, DESEncrypt.jieMiUsername(users.getUsername()), sendMessage, null);
        }
    }
}
