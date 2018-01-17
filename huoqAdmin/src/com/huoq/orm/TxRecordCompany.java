package com.huoq.orm;

import java.util.Date;



/**
 * TxRecord entity. @author MyEclipse Persistence Tools
 */

public class TxRecordCompany implements java.io.Serializable {

	// Fields

	private String id;
	private Long recordNumber;//流水号
	private Long usersCompanyId;//用户ID
	private Double money;//提现金额
	private String status;//提现状态;0:待审核;1:提现成功;2提现失败;3:正在审核
	private Date insertTime;//插入时间
	private Date checkTime;//审核时间
	/*private String txAccount;//提现帐号:银行卡号,或者支付宝帐号等;
	private String txBankName;//提现银行名称;或者机构名称;
*/	private String type;//提现类型;0:易宝提现;1:支付宝提现;2:连连提现
	private String accountId;///账户ID
	private String requestId;///请求ID
	private String bfOrderId;//易宝交易流水号
	private String errorCode;//错误码
	private Account account;
	private String note;//备注
	private String drawType;//提现类型;  0:T+0到账; 1:T+1到账;
	private String userIp;//用户提现时的IP;
	private String txStatus;//是否已经操作:0:未操作,1:已操作
	//以下字段
	private String txzt;
	
	private UsersCompany usersCompany;//用户;

	// Constructors

	public UsersCompany getUsersCompany() {
		return usersCompany;
	}

	public void setUsersCompany(UsersCompany usersCompany) {
		this.usersCompany = usersCompany;
	}

	public String getTxzt() {
		String temp = "待审核";
		if("0".equals(status)){
			temp = "处理中";
		}else if("1".equals(status))
			temp = "提现成功";
		else if("2".equals(status))
			temp = "提现失败";
		else if("3".equals(status))
			temp = "正在审核";
		return temp;
	}

	/** default constructor */
	public TxRecordCompany() {
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

	public Long getusersCompanyId() {
		return this.usersCompanyId;
	}

	public void setusersCompanyId(Long usersCompanyId) {
		this.usersCompanyId = usersCompanyId;
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

	/*public String getTxAccount() {
		return txAccount;
	}

	public void setTxAccount(String txAccount) {
		this.txAccount = txAccount;
	}

	public String getTxBankName() {
		return txBankName;
	}

	public void setTxBankName(String txBankName) {
		this.txBankName = txBankName;
	}*/

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getBfOrderId() {
		return bfOrderId;
	}

	public void setBfOrderId(String bfOrderId) {
		this.bfOrderId = bfOrderId;
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

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getDrawType() {
		return drawType;
	}

	public void setDrawType(String drawType) {
		this.drawType = drawType;
	}

	public String getUserIp() {
		return userIp;
	}

	public void setUserIp(String userIp) {
		this.userIp = userIp;
	}

	

	public String getTxStatus() {
		return txStatus;
	}

	public void setTxStatus(String txStatus) {
		this.txStatus = txStatus;
	}
	

}