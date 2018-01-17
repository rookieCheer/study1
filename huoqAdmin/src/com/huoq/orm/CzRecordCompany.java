package com.huoq.orm;

import java.util.Date;


/**
 * CzRecord entity. @author MyEclipse Persistence Tools
 */

public class CzRecordCompany implements java.io.Serializable {

	// Fields

	private String id;
	private Long recordNumber;
	private Long usersCompanyId;
	private Double money;
	private String status;//充值状态;0:待充值;1:充值成功;2充值失败;3:易宝充值成功,数据库插入失败;
	private Date insertTime;
	private Date checkTime;
	private String accountId;///账户ID
	private String orderId;///订单ID
	private String bfOrderId;//宝付交易流水号
	private String errorCode;//错误码
	private Account account;
	private String note;
	private String type; //1：易宝网银2:易宝快捷3:连连认证4：连连网银
	private String productName;//商品名称;
	private String errCause;//失败原因
	//以下字段没有ORM映射
	private String czzt;//充值状态;
	
	private UsersCompany usersCompany;//企业用户实体表;
	
	
	// Constructors

	public String getCzzt() {
		String temp = "待处理";
		if("0".equals(status)){
			temp = "处理中";
		}else if("1".equals(status))
			temp = "充值成功";
		else if("2".equals(status))
			temp = "充值失败";
		return temp;
	}
	// Constructors

	/** default constructor */
	public CzRecordCompany() {
	}

	
	// Property accessors

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getRecordNumber() {
		return this.recordNumber;
	}

	public void setRecordNumber(Long recordNumber) {
		this.recordNumber = recordNumber;
	}

	

	public Double getMoney() {
		return this.money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getInsertTime() {
		return this.insertTime;
	}

	public void setInsertTime(Date insertTime) {
		this.insertTime = insertTime;
	}

	public Date getCheckTime() {
		return this.checkTime;
	}

	public void setCheckTime(Date checkTime) {
		this.checkTime = checkTime;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getErrCause() {
		return errCause;
	}

	public void setErrCause(String errCause) {
		this.errCause = errCause;
	}

	public Long getUsersCompanyId() {
		return usersCompanyId;
	}

	public void setUsersCompanyId(Long usersCompanyId) {
		this.usersCompanyId = usersCompanyId;
	}

	public String getBfOrderId() {
		return bfOrderId;
	}

	public void setBfOrderId(String bfOrderId) {
		this.bfOrderId = bfOrderId;
	}

	public UsersCompany getUsersCompany() {
		return usersCompany;
	}

	public void setUsersCompany(UsersCompany usersCompany) {
		this.usersCompany = usersCompany;
	}

	public void setCzzt(String czzt) {
		this.czzt = czzt;
	}
	
}