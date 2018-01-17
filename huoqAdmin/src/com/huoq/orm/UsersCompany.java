package com.huoq.orm;

import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.QwyUtil;

import java.util.Date;

/**
 * 企业用户实体表
 *
 * @author yks
 * @createTime 2016-10-24
 */
@SuppressWarnings("serial")
public class UsersCompany implements java.io.Serializable {

    // Fields
    private Long id;//逻辑id,自增+1,起始值为1000
    private String username;//用户名
    private String password;//密码
    private String payPassword;//支付密码
    private String phone;//手机号
    private Long userType;//用户类型(大于等于0的,属于用户,否则为管理员) -1: 超级管理员(拥有所有权限) 0:普通用户
    private String userStatus;//用户状态
    private Date insertTime;//注册时间
    private Date updateTime;//更新时间
    private Date lastTime;//上一次登录时间
    private String isOnline;//是否在线；
    private String note;//备注

    public UsersCompany() {
    }
    public UsersCompany(Long id, String username, String password,
                        String payPassword, String phone, Long userType,
                        String userStatus, Date insertTime, Date updateTime,
                        Date lastTime, String isOnline, String note) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.payPassword = payPassword;
        this.phone = phone;
        this.userType = userType;
        this.userStatus = userStatus;
        this.insertTime = insertTime;
        this.updateTime = updateTime;
        this.lastTime = lastTime;
        this.isOnline = isOnline;
        this.note = note;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPayPassword() {
        return payPassword;
    }

    public void setPayPassword(String payPassword) {
        this.payPassword = payPassword;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getUserType() {
        return userType;
    }

    public void setUserType(Long userType) {
        this.userType = userType;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public Date getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Date getLastTime() {
        return lastTime;
    }

    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
    }

    public String getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(String isOnline) {
        this.isOnline = isOnline;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}