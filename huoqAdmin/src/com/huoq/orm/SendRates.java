package com.huoq.orm;

import java.util.Date;

/**
 * SendRates entity. @author MyEclipse Persistence Tools
 */

public class SendRates implements java.io.Serializable {

	// Fields

	private String id;
	private Long usersId;//用户ID
	private String status;//状态 0:未发息,1已发息
	private String type;//类型0:发息
	private Double inMoney;//本金
	private Date insertTime;//发息时间
	private Double payInterest;//支付利息
	private Double earnings;//收益率
	private String note;
	private Date updateTime;//修改时间

	// Constructors

	/** default constructor */
	public SendRates() {
	}

	/** minimal constructor */
	public SendRates(String id, Long usersId, String status, String type,
			Double inMoney, Date insertTime, Double payInterest,
			Double earnings) {
		this.id = id;
		this.usersId = usersId;
		this.status = status;
		this.type = type;
		this.inMoney = inMoney;
		this.insertTime = insertTime;
		this.payInterest = payInterest;
		this.earnings = earnings;
	}

	/** full constructor */
	public SendRates(String id, Long usersId, String status, String type,
			Double inMoney, Date insertTime, Double payInterest,
			Double earnings, String note, Date updateTime) {
		this.id = id;
		this.usersId = usersId;
		this.status = status;
		this.type = type;
		this.inMoney = inMoney;
		this.insertTime = insertTime;
		this.payInterest = payInterest;
		this.earnings = earnings;
		this.note = note;
		this.updateTime = updateTime;
	}

	// Property accessors

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getUsersId() {
		return this.usersId;
	}

	public void setUsersId(Long usersId) {
		this.usersId = usersId;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Double getInMoney() {
		return this.inMoney;
	}

	public void setInMoney(Double inMoney) {
		this.inMoney = inMoney;
	}

	public Date getInsertTime() {
		return this.insertTime;
	}

	public void setInsertTime(Date insertTime) {
		this.insertTime = insertTime;
	}

	public Double getPayInterest() {
		return this.payInterest;
	}

	public void setPayInterest(Double payInterest) {
		this.payInterest = payInterest;
	}

	public Double getEarnings() {
		return this.earnings;
	}

	public void setEarnings(Double earnings) {
		this.earnings = earnings;
	}

	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Date getUpdateTime() {
		return this.updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

}