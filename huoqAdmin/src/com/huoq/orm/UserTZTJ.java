package com.huoq.orm;


/**
 * 用户投资统计(无映射，页面展示用）
 * Created by yks on 2016/11/23.
 */
public class UserTZTJ {

    private String userId; //用户Id
    private String userName;//用户姓名
    private String zcpt;//注册平台 '0:web端注册; 1:Android移动端; 2:IOS移动端; 3:微信注册;'
    private String isBuy;//是否投资
    private String investCount;//投资次数
    private String accountBalance;//账户余额
    private String insertTime;//时间

    public UserTZTJ() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getZcpt() {
        return zcpt;
    }

    public void setZcpt(String zcpt) {
       /* //'0:web端注册; 1:Android移动端; 2:IOS移动端; 3:微信注册;'
        if ("0".equals(zcpt)){
            this.zcpt = "web端注册";
        }else if ("1".equals(zcpt)){
            this.zcpt = "Android移动端";
        }else  if ("2".equals(zcpt)){
            this.zcpt = "IOS移动端";
        }else if ("3".equals(zcpt)){
            this.zcpt = "微信注册";
        }else if (QwyUtil.isNullAndEmpty(zcpt)){
            this.zcpt = "";
        }else {
            this.zcpt = "WAP";
        }*/
        this.zcpt = zcpt;
    }

    public String getIsBuy() {
        return isBuy;
    }

    public void setIsBuy(String isBuy) {
        if ("y".equals(isBuy)){
            this.isBuy = "是";
        }else if ("n".equals(isBuy)){
            this.isBuy = "否";
        }
    }

    public String getInvestCount() {
        return investCount;
    }

    public void setInvestCount(String investCount) {
        this.investCount = investCount;
    }

    public String getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(String accountBalance) {
        this.accountBalance = accountBalance;
    }

    public String getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(String insertTime) {
        this.insertTime = insertTime;
    }
}
