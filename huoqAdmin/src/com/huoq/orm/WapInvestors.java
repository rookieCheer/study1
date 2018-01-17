package com.huoq.orm;

import java.io.Serializable;

/**
 * WAP 投资统计（无orm映射）
 * Created by yks on 2016/10/25.
 */
public class WapInvestors implements Serializable {

    private static final long serialVersionUID = 1L;
    private String date;//统计日期
    private String platform;//投资平台
    private String registCount;//注册人数
    private String bindCount;//绑卡人数
    private String investCount;//投资人数
    private String totalInvestMoney;//投资金额
    private String reinvestCount;//复投人数
    private String reinvestRate;//复投率 = 复投/投资


    public WapInvestors() {

    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getRegistCount() {
        return registCount;
    }

    public void setRegistCount(String registCount) {
        this.registCount = registCount;
    }

    public String getBindCount() {
        return bindCount;
    }

    public void setBindCount(String bindCount) {
        this.bindCount = bindCount;
    }

    public String getInvestCount() {
        return investCount;
    }

    public void setInvestCount(String investCount) {
        this.investCount = investCount;
    }

    public String getTotalInvestMoney() {
        return totalInvestMoney;
    }

    public void setTotalInvestMoney(String totalInvestMoney) {
        this.totalInvestMoney = totalInvestMoney;
    }

    public String getReinvestCount() {
        return reinvestCount;
    }

    public void setReinvestCount(String reinvestCount) {
        this.reinvestCount = reinvestCount;
    }

    public String getReinvestRate() {
        return reinvestRate;
    }

    public void setReinvestRate(String reinvestRate) {
        this.reinvestRate = reinvestRate;
    }
}
