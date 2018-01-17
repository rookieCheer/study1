/**
 * xinhuajindian.com Inc. Copyright (c) 2015-2017 All Rights Reserved.
 */
package com.huoq.orm;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author guoyin.yi
 * @version $Id: BorrowerOverduRecord.java, v 0.1 2017/12/26 Exp $
 */
public class BorrowerOverduRecord implements Serializable {

    private static final long serialVersionUID = 2963046620260158199L;

    private Integer           id;                                     // 主键
    private String            thirdTransaction;                       // 第三方流水号
    private String            payMentTransaction;                     // 打款流水号
    private String            repaymentTransaction;                   // 还款流水号
    private Date              expiryDate;                             // 还款日期
    private BigDecimal        overdueAmount;                          // 本金，单位：元
    private BigDecimal        overdueInterest;                        // 利息（单位：元）
    private BigDecimal        overdueFine;                            // 逾期罚金，单位：元
    private String            overdueStages;                          // 分逾期的期数
    private Date              dtCreate;                               // 创建时间

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

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public BigDecimal getOverdueAmount() {
        return overdueAmount;
    }

    public void setOverdueAmount(BigDecimal overdueAmount) {
        this.overdueAmount = overdueAmount;
    }

    public BigDecimal getOverdueInterest() {
        return overdueInterest;
    }

    public void setOverdueInterest(BigDecimal overdueInterest) {
        this.overdueInterest = overdueInterest;
    }

    public BigDecimal getOverdueFine() {
        return overdueFine;
    }

    public void setOverdueFine(BigDecimal overdueFine) {
        this.overdueFine = overdueFine;
    }

    public String getOverdueStages() {
        return overdueStages;
    }

    public void setOverdueStages(String overdueStages) {
        this.overdueStages = overdueStages;
    }

    public Date getDtCreate() {
        return dtCreate;
    }

    public void setDtCreate(Date dtCreate) {
        this.dtCreate = dtCreate;
    }
}
