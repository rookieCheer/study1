/**
 * xinhuajindian.com Inc. Copyright (c) 2015-2017 All Rights Reserved.
 */
package com.huoq.orm;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author guoyin.yi
 * @version $Id: BorrowerPlayMoney.java, v 0.1 2017/12/26 Exp $
 */
public class BorrowerPlayMoney implements Serializable {

    private static final long serialVersionUID = 6547631571857970230L;

    private Integer           id;                                     // 主键
    private String            productId;                              // 标的主键
    private Integer           borrowerInfoId;                         // 借款人基本信息主键
    private String            thirdTransaction;                       // 第三方流水号
    private String            payMentTransaction;                     // 打款流水号
    private BigDecimal        payAmount;                              // 打款金额
    private Integer           status;                                 // 状态
    private String            returnCode;                             // 宝付支付结果
    private String            returnMsg;                              // 宝付支付描述
    private String            returnRes;                              // 宝付接口返回内容
    private Date              dtCreate;                               // 打款时间
    private Date              dtModify;                               // 修改时间

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    
    public Integer getBorrowerInfoId() {
        return borrowerInfoId;
    }

    
    public void setBorrowerInfoId(Integer borrowerInfoId) {
        this.borrowerInfoId = borrowerInfoId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
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

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getDtModify() {
        return dtModify;
    }

    public void setDtModify(Date dtModify) {
        this.dtModify = dtModify;
    }
}
