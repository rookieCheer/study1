/**
 * www.xinhuajindian.com Inc.
 * Copyright (c) 2015-2017 All Rights Reserved.
 */
package com.huoq.baofoo.utils;

import java.io.Serializable;

/**
 * 代付参数bean
 *
 * @author guoyin.yi
 * @version $Id: BaofooPayParams.java, v 0.1  2017/12/28 Exp $
 * @email guoyin.yi@xinhuajindian.com
 */
public class BaofooPayParams implements Serializable {

    private static final long serialVersionUID = 2665305398472590345L;
    private String transNo;// 商户订单
    private String transMoney;    // 转账金额
    private String toAccName; // 收款人姓名
    private String toAccNo;    // 收款人银行帐号
    private String toBankName;    // 收款人银行名称
    private String toProName;        // 收款人开户行省名
    private String toCityName;            // 收款人开户行市名
    private String toAccDept;            // 收款人开户行机构
    private String transCardId;// 银行卡身份证件号
    private String transMobile;// 银行卡预留手机号
    private String transSummary;        // 摘要

    public BaofooPayParams() {
    }

    public String getTransNo() {
        return transNo;
    }

    public void setTransNo(String transNo) {
        this.transNo = transNo;
    }

    public String getTransMoney() {
        return transMoney;
    }

    public void setTransMoney(String transMoney) {
        this.transMoney = transMoney;
    }

    public String getToAccName() {
        return toAccName;
    }

    public void setToAccName(String toAccName) {
        this.toAccName = toAccName;
    }

    public String getToAccNo() {
        return toAccNo;
    }

    public void setToAccNo(String toAccNo) {
        this.toAccNo = toAccNo;
    }

    public String getToBankName() {
        return toBankName;
    }

    public void setToBankName(String toBankName) {
        this.toBankName = toBankName;
    }

    public String getToProName() {
        return toProName;
    }

    public void setToProName(String toProName) {
        this.toProName = toProName;
    }

    public String getToCityName() {
        return toCityName;
    }

    public void setToCityName(String toCityName) {
        this.toCityName = toCityName;
    }

    public String getToAccDept() {
        return toAccDept;
    }

    public void setToAccDept(String toAccDept) {
        this.toAccDept = toAccDept;
    }

    public String getTransCardId() {
        return transCardId;
    }

    public void setTransCardId(String transCardId) {
        this.transCardId = transCardId;
    }

    public String getTransMobile() {
        return transMobile;
    }

    public void setTransMobile(String transMobile) {
        this.transMobile = transMobile;
    }

    public String getTransSummary() {
        return transSummary;
    }

    public void setTransSummary(String transSummary) {
        this.transSummary = transSummary;
    }

    @Override
    public String toString() {
        return "BaofooPayParams{" +
                "transNo='" + transNo + '\'' +
                ", transMoney='" + transMoney + '\'' +
                ", toAccName='" + toAccName + '\'' +
                ", toAccNo='" + toAccNo + '\'' +
                ", toBankName='" + toBankName + '\'' +
                ", toProName='" + toProName + '\'' +
                ", toCityName='" + toCityName + '\'' +
                ", toAccDept='" + toAccDept + '\'' +
                ", transCardId='" + transCardId + '\'' +
                ", transMobile='" + transMobile + '\'' +
                ", transSummary='" + transSummary + '\'' +
                '}';
    }
}
