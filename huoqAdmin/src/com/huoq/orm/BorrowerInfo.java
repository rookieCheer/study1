package com.huoq.orm;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 李瑞丽
 * @Date: 2017/12/25 17:45
 */
public class BorrowerInfo implements java.io.Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 13454354435L;
    private Integer           id;                             // 逻辑id,自增+1,起始值为1000
    private String            thirdTransaction;               // 第三方借款流水号
    private String            boUserName;                     // 借款人姓名
    private String            boAccNo;                        // 借款人银行帐号
    private String            boBankName;                     // 借款人银行名称
    private String            boBankNo;                       // 借款人银行编码
    private String            mobile;                         // 手机号码
    private String            boProvinceName;                 // 借款人开户行省名
    private String            boCityName;                     // 借款人开户行市名
    private String            boBrancheBank;                  // 借款人开户支行名称
    private String            idCard;                         // 借款人身份证号
    private String            address;                        // 借款人地址
    private String            idCardUrl;                      // 借款人身份证URL地址
    private String            transMobile;                    // 银行卡预留手机号
    private BigDecimal        boAmount;                       // 借款金额(单位元)
    private Integer           termLoan;                       // 借款期限(单位天)
    private String            bosource;                       // 平台来源
    private Integer           sex;                            // 性别（1表示男;2:女）
    private Integer           age;                            // 年龄
    private Integer           maritalStatus;                  // 婚姻状况（0:已婚;1:未婚;2:离异;3:未知）
    private String            subjectAttribute;               // 主体属性
    private BigDecimal        incomeMin;                      // 年收入最小值（单位元）
    private BigDecimal        incomeMax;                      // 年收入最大值（单位元）
    private String            industry;                       // 所属行业
    private String            usageLoan;                      // 借款用途
    private Integer           repaymentMethod;                // 还款方式(1:等额本金)
    private String            sourceRepayment;                // 还款来源
    private String            caseComplaint;                  // 涉诉情况
    private String            emergencyContact1;              // 紧急联系人一
    private String            contactPhone1;                  // 紧急联系人一电话
    private String            emergencyContact2;              // 紧急联系人二
    private String            contactPhone2;                  // 紧急联系人二电话
    private String            creditReportUrl;                // 征信报告地址
    private String            boContractNo;                   // 借款合同编号
    private String            agreementUrl;                   // 借款合同地址
    private String            notifyUrl;                      // 通知回调地址
    private Integer           status;                         // 状态(1:待放款;2:放款中;3:放款成功;4:放款失败;5:还款中;6:部分还款;7:已还清;8:已逾期;9:还款失败;10:信息同步失败)
    private String            remark;                         // 备注
    private Date              dtCreate;                       // 创建时间
    private Date              dtModify;                       // 更新时间

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
    
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBoContractNo() {
        return boContractNo;
    }
    
    public void setBoContractNo(String boContractNo) {
        this.boContractNo = boContractNo;
    }

    public String getBoUserName() {
        return boUserName;
    }

    public void setBoUserName(String boUserName) {
        this.boUserName = boUserName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getBoAccNo() {
        return boAccNo;
    }

    public void setBoAccNo(String boAccNo) {
        this.boAccNo = boAccNo;
    }

    public String getBoBankName() {
        return boBankName;
    }

    public void setBoBankName(String boBankName) {
        this.boBankName = boBankName;
    }

    public String getBoBankNo() {
        return boBankNo;
    }

    public void setBoBankNo(String boBankNo) {
        this.boBankNo = boBankNo;
    }

    public Integer getTermLoan() {
        return termLoan;
    }

    public void setTermLoan(Integer termLoan) {
        this.termLoan = termLoan;
    }

    public String getBoProvinceName() {
        return boProvinceName;
    }

    public void setBoProvinceName(String boProvinceName) {
        this.boProvinceName = boProvinceName;
    }

    public String getBoCityName() {
        return boCityName;
    }

    public void setBoCityName(String boCityName) {
        this.boCityName = boCityName;
    }

    public String getBoBrancheBank() {
        return boBrancheBank;
    }

    public void setBoBrancheBank(String boBrancheBank) {
        this.boBrancheBank = boBrancheBank;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getIdCardUrl() {
        return idCardUrl;
    }

    public void setIdCardUrl(String idCardUrl) {
        this.idCardUrl = idCardUrl;
    }

    public String getTransMobile() {
        return transMobile;
    }

    public void setTransMobile(String transMobile) {
        this.transMobile = transMobile;
    }

    public BigDecimal getBoAmount() {
        return boAmount;
    }

    public void setBoAmount(BigDecimal boAmount) {
        this.boAmount = boAmount;
    }

    public String getBosource() {
        return bosource;
    }

    public void setBosource(String bosource) {
        this.bosource = bosource;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(Integer maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getSubjectAttribute() {
        return subjectAttribute;
    }

    public void setSubjectAttribute(String subjectAttribute) {
        this.subjectAttribute = subjectAttribute;
    }

    public BigDecimal getIncomeMin() {
        return incomeMin;
    }

    public void setIncomeMin(BigDecimal incomeMin) {
        this.incomeMin = incomeMin;
    }

    public BigDecimal getIncomeMax() {
        return incomeMax;
    }

    public void setIncomeMax(BigDecimal incomeMax) {
        this.incomeMax = incomeMax;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getCaseComplaint() {
        return caseComplaint;
    }

    public void setCaseComplaint(String caseComplaint) {
        this.caseComplaint = caseComplaint;
    }

    public String getUsageLoan() {
        return usageLoan;
    }

    public void setUsageLoan(String usageLoan) {
        this.usageLoan = usageLoan;
    }

    public Integer getRepaymentMethod() {
        return repaymentMethod;
    }

    public void setRepaymentMethod(Integer repaymentMethod) {
        this.repaymentMethod = repaymentMethod;
    }

    public String getSourceRepayment() {
        return sourceRepayment;
    }

    public void setSourceRepayment(String sourceRepayment) {
        this.sourceRepayment = sourceRepayment;
    }

    public String getEmergencyContact1() {
        return emergencyContact1;
    }

    public void setEmergencyContact1(String emergencyContact1) {
        this.emergencyContact1 = emergencyContact1;
    }

    public String getContactPhone1() {
        return contactPhone1;
    }

    public void setContactPhone1(String contactPhone1) {
        this.contactPhone1 = contactPhone1;
    }

    public String getEmergencyContact2() {
        return emergencyContact2;
    }

    public void setEmergencyContact2(String emergencyContact2) {
        this.emergencyContact2 = emergencyContact2;
    }

    public String getContactPhone2() {
        return contactPhone2;
    }

    public void setContactPhone2(String contactPhone2) {
        this.contactPhone2 = contactPhone2;
    }

    public String getCreditReportUrl() {
        return creditReportUrl;
    }

    public void setCreditReportUrl(String creditReportUrl) {
        this.creditReportUrl = creditReportUrl;
    }

    public String getAgreementUrl() {
        return agreementUrl;
    }

    public void setAgreementUrl(String agreementUrl) {
        this.agreementUrl = agreementUrl;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
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

    public Date getDtCreate() {
        return dtCreate;
    }

    public void setDtCreate(Date dtCreate) {
        this.dtCreate = dtCreate;
    }

    public Date getDtModify() {
        return dtModify;
    }

    public void setDtModify(Date dtModify) {
        this.dtModify = dtModify;
    }
}
