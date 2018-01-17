package com.huoq.assetSide.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class BorrowerPlayMoneyVo implements Serializable {

    private static final long serialVersionUID = 6547631571857970230L;

    private String            thirdTransaction;                       // 第三方流水号
    private String            payMentTransaction;                     // 打款流水号
    private BigDecimal        payAmount;                              // 打款金额
    private Integer           status;                                 // 状态
    private String            remark;                                 // 打款结果描述

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

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
