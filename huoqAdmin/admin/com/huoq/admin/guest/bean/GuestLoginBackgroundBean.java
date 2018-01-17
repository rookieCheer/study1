package com.huoq.admin.guest.bean;

import com.huoq.admin.guest.dao.GuestLoginBackgroundDAO;
import com.huoq.common.util.DESEncrypt;
import com.huoq.orm.UsersGuest;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 访客登陆bean
 * Created by yks on 2016/11/8.
 */
@Service
public class GuestLoginBackgroundBean {

    @Resource
    GuestLoginBackgroundDAO dao;
    private static Logger log = Logger.getLogger(InvestorsGuestBean.class);


    public UsersGuest getUsersGuest(UsersGuest guest){
        try {
            StringBuffer hql = new StringBuffer("FROM UsersGuest ug ");
            hql.append("WHERE ug.username = ? ");
            hql.append("AND  ug.password = ? ");
            return (UsersGuest)dao.findJoinActive(hql.toString(), new Object[]{guest.getUsername(),guest.getPassword()});
        } catch (Exception e) {
            log.error("操作异常: ",e);
        }
        return null;
    }

}
