/**
 * hongshiwl.com Inc.
 * Copyright (c) 2015-2017 All Rights Reserved.
 */
package com.huoq.thread.bean;

import com.huoq.admin.product.bean.InvestorsBean;
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
 * 认证用户2天后无任何投资，短信通知
 * @author guoyin.yi
 * @version $Id: SendUsersInformBean.java, v 0.1  2017/12/6 Exp $
 */
@Service
public class SendUsersInformBean {

    @Resource
    private ThreadDAO dao;

    @Resource
    SMSNoticeBean smsNoticeBean;

    private static ResourceBundle resbSms = ResourceBundle.getBundle("sms-notice");

    /**
     * 获取绑卡成功2天的数据
     * @return
     */
    public List<Users> queryInformUsers(){

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dateTime = format.format(new Date()).trim();
        StringBuffer sql = new StringBuffer(" select u.* from users u inner join account a on u.id=a.users_id ");
        sql.append(" where a.status=1 ");
        sql.append(" and TIMESTAMPDIFF(day,date_format(a.insert_time,'%Y-%m-%d'),date_format('"+dateTime+"','%Y-%m-%d')) =2 ");

        return dao.queryAllSql(sql.toString(),null,Users.class);
    }

    /**
     * 发送短信通知
     * @param users
     */
    public void sendUsersNotify(Users users) {
        synchronized (LockHolder.getLock(users.getId())) {
            String shortUrl = resbSms.getString("SMS_REMIND_USER");
            String sendMessage = resbSms.getString("SMS_REMIND_USER_URL");
            smsNoticeBean.sendSMSInThreadPool(shortUrl, DESEncrypt.jieMiUsername(users.getUsername()), sendMessage, null);
        }
    }
}
