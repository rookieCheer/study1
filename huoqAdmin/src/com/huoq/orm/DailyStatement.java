package com.huoq.orm;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 李瑞丽
 * @Date: Created in 17:57 2018/1/16
 */
public class DailyStatement implements Serializable {
    private Long    id;
    private Date    insertTime;// 查询日期()
    private Double  tradingVolume;//交易额
    private Double  loanAmountAll;//在贷金额（含零钱罐）
    private Double  loanAmount;//在贷金额（不含零钱罐）
    private Double  reimbursementAmount;//回款金额（不含零钱罐）
    private Double  reimbursementAmountAll;//回款金额（含零钱罐及余额）
    private Double  interestpayment;//支付利息
    private Double  todayOutCashMoney;// 今日提现金额
    private Double  returnInvestmentRate;// 回款用户投资率!!!
    private Double  capitalInflow;// 资金流入额
    private Double  netInflow;// 净流入金额
    private Double  capitalStock;// 资金存量
    private Integer activationCount;//激活用户数
    private Integer investCount;//投资用户数
    private Integer todayregisterCount;// 今日注册人数
    private Integer todaycertificationCount;// 今日认证用户
    private Integer todayNewBuyNumber;// 今日首投用户
    private Double  firstPercentConversion;// 首投用户转化率!!!
    private Double  firstInvestmentTotalMoney;//首投总金额
    private Double  firstInvestmentMoney ;// 首投客单金额（元）
    private Double  reInvestmentMoney;// 复投金额（元）
    private Double  amountNewMoney;// 零钱罐新增金额（元）
    private Integer reInvestmentCount;//复投用户数
    private Integer addReInvestmentCount;//新增复投用户数
    private Double  addReInvestmentMoney;//新增复投用户投资总额（元）
    private Integer reInvestmentAmount;//复投次数
    private Double  multipleRate;// 新增复投率（%）!!!
    private Double  occupationRatio;// 复投用户占比（%）!!!
    private Double  reInvestmentRate;// 复投金额占比（%）!!!
    private Double  sumMoney;// 复投客单金额（元）
    private Double  capitaInvestmentMoney;// 人均投资金额（元）


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }

    public Double getTradingVolume() {
        return tradingVolume;
    }

    public void setTradingVolume(Double tradingVolume) {
        this.tradingVolume = tradingVolume;
    }

    public Double getLoanAmountAll() {
        return loanAmountAll;
    }

    public void setLoanAmountAll(Double loanAmountAll) {
        this.loanAmountAll = loanAmountAll;
    }

    public Double getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(Double loanAmount) {
        this.loanAmount = loanAmount;
    }

    public Double getReimbursementAmount() {
        return reimbursementAmount;
    }

    public void setReimbursementAmount(Double reimbursementAmount) {
        this.reimbursementAmount = reimbursementAmount;
    }

    public Double getReimbursementAmountAll() {
        return reimbursementAmountAll;
    }

    public void setReimbursementAmountAll(Double reimbursementAmountAll) {
        this.reimbursementAmountAll = reimbursementAmountAll;
    }

    public Double getInterestpayment() {
        return interestpayment;
    }

    public void setInterestpayment(Double interestpayment) {
        this.interestpayment = interestpayment;
    }

    public Double getTodayOutCashMoney() {
        return todayOutCashMoney;
    }

    public void setTodayOutCashMoney(Double todayOutCashMoney) {
        this.todayOutCashMoney = todayOutCashMoney;
    }

    public Double getReturnInvestmentRate() {
        return returnInvestmentRate;
    }

    public void setReturnInvestmentRate(Double returnInvestmentRate) {
        this.returnInvestmentRate = returnInvestmentRate;
    }

    public Double getCapitalInflow() {
        return capitalInflow;
    }

    public void setCapitalInflow(Double capitalInflow) {
        this.capitalInflow = capitalInflow;
    }

    public Double getNetInflow() {
        return netInflow;
    }

    public void setNetInflow(Double netInflow) {
        this.netInflow = netInflow;
    }

    public Double getCapitalStock() {
        return capitalStock;
    }

    public void setCapitalStock(Double capitalStock) {
        this.capitalStock = capitalStock;
    }

    public Integer getActivationCount() {
        return activationCount;
    }

    public void setActivationCount(Integer activationCount) {
        this.activationCount = activationCount;
    }

    public Integer getInvestCount() {
        return investCount;
    }

    public void setInvestCount(Integer investCount) {
        this.investCount = investCount;
    }

    public Integer getTodayregisterCount() {
        return todayregisterCount;
    }

    public void setTodayregisterCount(Integer todayregisterCount) {
        this.todayregisterCount = todayregisterCount;
    }

    public Integer getTodaycertificationCount() {
        return todaycertificationCount;
    }

    public void setTodaycertificationCount(Integer todaycertificationCount) {
        this.todaycertificationCount = todaycertificationCount;
    }

    public Integer getTodayNewBuyNumber() {
        return todayNewBuyNumber;
    }

    public void setTodayNewBuyNumber(Integer todayNewBuyNumber) {
        this.todayNewBuyNumber = todayNewBuyNumber;
    }

    public Double getFirstPercentConversion() {
        return firstPercentConversion;
    }

    public void setFirstPercentConversion(Double firstPercentConversion) {
        this.firstPercentConversion = firstPercentConversion;
    }

    public Double getFirstInvestmentTotalMoney() {
        return firstInvestmentTotalMoney;
    }

    public void setFirstInvestmentTotalMoney(Double firstInvestmentTotalMoney) {
        this.firstInvestmentTotalMoney = firstInvestmentTotalMoney;
    }

    public Double getFirstInvestmentMoney() {
        return firstInvestmentMoney;
    }

    public void setFirstInvestmentMoney(Double firstInvestmentMoney) {
        this.firstInvestmentMoney = firstInvestmentMoney;
    }

    public Double getReInvestmentMoney() {
        return reInvestmentMoney;
    }

    public void setReInvestmentMoney(Double reInvestmentMoney) {
        this.reInvestmentMoney = reInvestmentMoney;
    }

    public Double getAmountNewMoney() {
        return amountNewMoney;
    }

    public void setAmountNewMoney(Double amountNewMoney) {
        this.amountNewMoney = amountNewMoney;
    }

    public Integer getReInvestmentCount() {
        return reInvestmentCount;
    }

    public void setReInvestmentCount(Integer reInvestmentCount) {
        this.reInvestmentCount = reInvestmentCount;
    }

    public Integer getAddReInvestmentCount() {
        return addReInvestmentCount;
    }

    public void setAddReInvestmentCount(Integer addReInvestmentCount) {
        this.addReInvestmentCount = addReInvestmentCount;
    }

    public Double getAddReInvestmentMoney() {
        return addReInvestmentMoney;
    }

    public void setAddReInvestmentMoney(Double addReInvestmentMoney) {
        this.addReInvestmentMoney = addReInvestmentMoney;
    }

    public Integer getReInvestmentAmount() {
        return reInvestmentAmount;
    }

    public void setReInvestmentAmount(Integer reInvestmentAmount) {
        this.reInvestmentAmount = reInvestmentAmount;
    }

    public Double getMultipleRate() {
        return multipleRate;
    }

    public void setMultipleRate(Double multipleRate) {
        this.multipleRate = multipleRate;
    }

    public Double getOccupationRatio() {
        return occupationRatio;
    }

    public void setOccupationRatio(Double occupationRatio) {
        this.occupationRatio = occupationRatio;
    }

    public Double getReInvestmentRate() {
        return reInvestmentRate;
    }

    public void setReInvestmentRate(Double reInvestmentRate) {
        this.reInvestmentRate = reInvestmentRate;
    }

    public Double getSumMoney() {
        return sumMoney;
    }

    public void setSumMoney(Double sumMoney) {
        this.sumMoney = sumMoney;
    }

    public Double getCapitaInvestmentMoney() {
        return capitaInvestmentMoney;
    }

    public void setCapitaInvestmentMoney(Double capitaInvestmentMoney) {
        this.capitaInvestmentMoney = capitaInvestmentMoney;
    }
}
