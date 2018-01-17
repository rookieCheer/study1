package com.huoq.orm;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 李瑞丽
 * @Date: 2017/12/26 19:55
 */
public class ProjectBorrowerInfo implements java.io.Serializable {

    private static final long serialVersionUID = 13454354435L;
    private Integer           id;                             // 逻辑id,自增+1,起始值为1000
    private String            productId;                      // 标的主键
    private String            productTitle;                   // 标的名称
    private Date              productCreate;                  // 上标时间
    private Integer           borrowerInfoId;                 // 借款人基本信息主键
    private String            boUserName;                     // 借款人姓名
    private String            boMobile;                       // 借款人手机号
    private BigDecimal        boAmount;                       // 借款金额（单位元）
    private Integer           termLoan;                       // 借款期限(单位天)
    private Date              boCreate;                       // 借款时间
    private BigDecimal        raiseFunds;                     // 待募集资金(单位元)
    private String            boContractNo;                   // 借款合同编号
    private String            boContractUrl;                  // 借款合同地址

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Integer getBorrowerInfoId() {
        return borrowerInfoId;
    }

    public void setBorrowerInfoId(Integer borrowerInfoId) {
        this.borrowerInfoId = borrowerInfoId;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public Date getProductCreate() {
        return productCreate;
    }

    public void setProductCreate(Date productCreate) {
        this.productCreate = productCreate;
    }

    public String getBoUserName() {
        return boUserName;
    }

    public void setBoUserName(String boUserName) {
        this.boUserName = boUserName;
    }

    public String getBoMobile() {
        return boMobile;
    }

    public void setBoMobile(String boMobile) {
        this.boMobile = boMobile;
    }

    public BigDecimal getBoAmount() {
        return boAmount;
    }

    public void setBoAmount(BigDecimal boAmount) {
        this.boAmount = boAmount;
    }

    public Integer getTermLoan() {
        return termLoan;
    }

    public void setTermLoan(Integer termLoan) {
        this.termLoan = termLoan;
    }

    public Date getBoCreate() {
        return boCreate;
    }

    public void setBoCreate(Date boCreate) {
        this.boCreate = boCreate;
    }

    public BigDecimal getRaiseFunds() {
        return raiseFunds;
    }

    public void setRaiseFunds(BigDecimal raiseFunds) {
        this.raiseFunds = raiseFunds;
    }

    public String getBoContractNo() {
        return boContractNo;
    }

    public void setBoContractNo(String boContractNo) {
        this.boContractNo = boContractNo;
    }

    public String getBoContractUrl() {
        return boContractUrl;
    }

    public void setBoContractUrl(String boContractUrl) {
        this.boContractUrl = boContractUrl;
    }
}
