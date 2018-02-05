package com.huoq.admin.guest.action;

import java.io.IOException;

import javax.annotation.Resource;

import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;

import com.huoq.admin.guest.bean.GuestLoginBackgroundBean;
import com.huoq.common.action.BaseAction;
import com.huoq.common.bean.PlatformBean;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.Platform;
import com.huoq.orm.UsersGuest;

/**
 * 访客登录Action层<br>
 * @author yks
 * on 2016-11-08
 */


@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/Guest")
public class GuestLoginBackgroundAction extends BaseAction {

    @Resource
    private GuestLoginBackgroundBean bean;
    @Resource
    private PlatformBean platformBean;
    private UsersGuest guest;


    public String adminLogin() {
        String json = "";
        try {
            if (QwyUtil.isNullAndEmpty(guest)) {
                json = QwyUtil.getJSONString("err", "用户名或密码错误");
            } else {
                if (QwyUtil.isNullAndEmpty(guest.getUsername()) || QwyUtil.isNullAndEmpty(guest.getPassword())) {
                    json = QwyUtil.getJSONString("err", "用户名或密码不能为空");
                } else {
                    guest = bean.getUsersGuest(guest);
                    if (!QwyUtil.isNullAndEmpty(guest)) {
                        if ("0".equals(guest.getUserStatus())) {
                            json = QwyUtil.getJSONString("ok", "访客登录成功");
                            //登录成功
                            log.info("【访客登录成功】---- name: "+guest.getUsername() + "--------------");
                            getRequest().getSession().setAttribute("usersGuest", guest);
//                            Platform platform = platformBean.getPlatform(null);
//                            getRequest().getSession().setAttribute("platform", platform);
                        } else {
                            json = QwyUtil.getJSONString("err", "该用户已被禁用，请联系超级管理员——文勇欧巴");
                        }
                    } else {
                        json = QwyUtil.getJSONString("err", "用户名或密码错误");
                    }
                }
            }
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        try {
            QwyUtil.printJSON(getResponse(), json);
        } catch (IOException e) {
        	log.error("操作异常: ", e);
        }
        return null;
    }


    public UsersGuest getGuest() {
        return guest;
    }

    public void setGuest(UsersGuest guest) {
        this.guest = guest;
    }
}
