/**
 * xinhuajindian.com Inc. Copyright (c) 2015-2017 All Rights Reserved.
 */
package com.huoq.orm;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户还款记录信息
 *
 * @author guoyin.yi
 * @version $Id: BorrowerRepayment.java, v 0.1 2017/12/26 Exp $
 */
public class BorrowerRepayment implements Serializable {

    private static final long serialVersionUID = 9162026036849204814L;

    private Integer           id;                                     // 主键
    private String            thirdTransaction;                       // 第三方流水号
    private String            payMentTransaction;                     // 打款流水号
    private String            repaymentTransaction;                   // 还款流水号
    private BigDecimal        totalAmount;                            // 当期还款总金额，单位：元
    private BigDecimal        repayAmount;                            // 本金，单位：元
    private BigDecimal        interest;                               // 利息，单位：元
    private BigDecimal        fine;                                   // 罚金，单位：元
    private String            stages;                                 // 分期还款的期数
    private Integer           isLastStages;                           // 是否为最后一期还款(1：是；0：否)
    private Integer           isOverdue;                              // 是否含有逾期（1：是；0：否）
    private Integer           status;                                 // 状态（0：还款成功；1：还款失败；2：还款中）
    private String            returnCode;                             // 宝付支付结果
    private String            returnMsg;                              // 宝付支付描述
    private String            returnRes;                              // 宝付接口返回内容
    private Date              dtCreate;                               // 还款时间
    private Date              dtModify;                               // 修改时间

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getThirdTransaction() {
        return thirdTransaction;
    }

    public void setThirdTransaction(String thirdTransaction) {
        this.thirdTransaction = thirdTransaction;
    }

    public String getPayMentTransaction() {
        return payMentTransaction;
    }

    public void setPayMentTransaction(String payMentTransaction) {
        this.payMentTransaction = payMentTransaction;
    }

    public String getRepaymentTransaction() {
        return repaymentTransaction;
    }

    public void setRepaymentTransaction(String repaymentTransaction) {
        this.repaymentTransaction = repaymentTransaction;
    }

    public BigDecimal getRepayAmount() {
        return repayAmount;
    }

    public void setRepayAmount(BigDecimal repayAmount) {
        this.repayAmount = repayAmount;
    }

    public Date getDtCreate() {
        return dtCreate;
    }

    public void setDtCreate(Date dtCreate) {
        this.dtCreate = dtCreate;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public String getReturnMsg() {
        return returnMsg;
    }

    public void setReturnMsg(String returnMsg) {
        this.returnMsg = returnMsg;
    }

    public String getReturnRes() {
        return returnRes;
    }

    public void setReturnRes(String returnRes) {
        this.returnRes = returnRes;
    }

    public String getStages() {
        return stages;
    }

    public void setStages(String stages) {
        this.stages = stages;
    }

    public Date getDtModify() {
        return dtModify;
    }

    public void setDtModify(Date dtModify) {
        this.dtModify = dtModify;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIsLastStages() {
        return isLastStages;
    }

    public void setIsLastStages(Integer isLastStages) {
        this.isLastStages = isLastStages;
    }

    public BigDecimal getInterest() {
        return interest;
    }

    public void setInterest(BigDecimal interest) {
        this.interest = interest;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public BigDecimal getFine() {
        return fine;
    }
    
    public void setFine(BigDecimal fine) {
        this.fine = fine;
    }
    
    public Integer getIsOverdue() {
        return isOverdue;
    }
    
    public void setIsOverdue(Integer isOverdue) {
        this.isOverdue = isOverdue;
    }

    @Override
    public String toString() {
        return "BorrowerRepayment{" + "id=" + id + ", thirdTransaction='" + thirdTransaction + '\'' + ", payMentTransaction='" + payMentTransaction + '\''
               + ", repaymentTransaction='" + repaymentTransaction + '\'' + ", repayAmount=" + repayAmount + ", dtCreate=" + dtCreate + ", returnCode='" + returnCode + '\''
               + ", returnMsg='" + returnMsg + '\'' + ", returnRes='" + returnRes + '\'' + '}';
    }
}
