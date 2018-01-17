package com.huoq.orm;

/**
 * Bank entity. @author MyEclipse Persistence Tools
 */

public class Bank implements java.io.Serializable {

	// Fields

	private Long id;
	private String bankName;//银行名字
	private String bankCode;//银行编码
	private String bankUri;//银行图标后缀
	private String status;//是否支持此银行;0:支持; 1:不支持
	private String bankNote;//备注
	private Integer index;//排序
	private Integer txQuota;//提现单笔限额(元)
	private Integer czQuota;//充值单笔限额(元)
	private String type;//第三方机构类型 ： 1：宝付  2：富友
	private Integer czQuotaDay;//充值当日限额（元）
	private Integer czQuotaMonth;//充值当月限额

	// Constructors

	/** default constructor */
	public Bank() {
	}

	/** minimal constructor */
	public Bank(String bankName, String bankCode) {
		this.bankName = bankName;
		this.bankCode = bankCode;
	}

	/** full constructor */
	public Bank(String bankName, String bankCode, String bankUri) {
		this.bankName = bankName;
		this.bankCode = bankCode;
		this.bankUri = bankUri;
	}

	// Property accessors

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBankName() {
		return this.bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankCode() {
		return this.bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBankUri() {
		return this.bankUri;
	}

	public void setBankUri(String bankUri) {
		this.bankUri = bankUri;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getBankNote() {
		return bankNote;
	}

	public void setBankNote(String bankNote) {
		this.bankNote = bankNote;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public Integer getTxQuota() {
		return txQuota;
	}

	public void setTxQuota(Integer txQuota) {
		this.txQuota = txQuota;
	}

	public Integer getCzQuota() {
		return czQuota;
	}

	public void setCzQuota(Integer czQuota) {
		this.czQuota = czQuota;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getCzQuotaDay() {
		return czQuotaDay;
	}

	public void setCzQuotaDay(Integer czQuotaDay) {
		this.czQuotaDay = czQuotaDay;
	}

	public Integer getCzQuotaMonth() {
		return czQuotaMonth;
	}

	public void setCzQuotaMonth(Integer czQuotaMonth) {
		this.czQuotaMonth = czQuotaMonth;
	}

}