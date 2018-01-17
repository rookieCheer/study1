package com.huoq.orm;

import java.io.Serializable;
import java.util.Date;

public class DataOverview implements Serializable {
	private Long id;
	private Date insertTime;// 查询日期()
	private Double allCapitalStock;// 平台资金存量()
	private Double todayCapitalStock;// 今日存量增量()
	private Double allOutCashMoney;// 平台累计提现金额()
	private Double todayOutCashMoney;// 今日提现金额()
	private Double rechargeMoney;// 平台充值总额()
	private Double todayrechargeMoney;// 今日充值总额()
	private Double todayBuyMoney;// 今日购买金额()
	private Integer registerCount;// 累计注册人数()
	private Integer todayregisterCount;// 今日注册人数()
	private Integer certificationCount;// 累计认证用户()
	private Integer todaycertificationCount;// 今日认证用户()
	private Integer todayBuyNumber;// 今日购买人数(包含零钱罐)()
	private Integer todayNewBuyNumber;// 今日首投用户()
	private Double todayUAuditingOutCashMoney;//提现未审核()

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(Date insertTime) {
		this.insertTime = insertTime;
	}

	public Double getAllCapitalStock() {
		return allCapitalStock;
	}

	public void setAllCapitalStock(Double allCapitalStock) {
		this.allCapitalStock = allCapitalStock;
	}

	public Double getTodayCapitalStock() {
		return todayCapitalStock;
	}

	public void setTodayCapitalStock(Double todayCapitalStock) {
		this.todayCapitalStock = todayCapitalStock;
	}

	public Double getAllOutCashMoney() {
		return allOutCashMoney;
	}

	public void setAllOutCashMoney(Double allOutCashMoney) {
		this.allOutCashMoney = allOutCashMoney;
	}

	public Double getTodayOutCashMoney() {
		return todayOutCashMoney;
	}

	public void setTodayOutCashMoney(Double todayOutCashMoney) {
		this.todayOutCashMoney = todayOutCashMoney;
	}

	public Double getRechargeMoney() {
		return rechargeMoney;
	}

	public void setRechargeMoney(Double rechargeMoney) {
		this.rechargeMoney = rechargeMoney;
	}

	public Double getTodayrechargeMoney() {
		return todayrechargeMoney;
	}

	public void setTodayrechargeMoney(Double todayrechargeMoney) {
		this.todayrechargeMoney = todayrechargeMoney;
	}

	public Double getTodayBuyMoney() {
		return todayBuyMoney;
	}

	public void setTodayBuyMoney(Double todayBuyMoney) {
		this.todayBuyMoney = todayBuyMoney;
	}

	public Integer getRegisterCount() {
		return registerCount;
	}

	public void setRegisterCount(Integer registerCount) {
		this.registerCount = registerCount;
	}

	public Integer getTodayregisterCount() {
		return todayregisterCount;
	}

	public void setTodayregisterCount(Integer todayregisterCount) {
		this.todayregisterCount = todayregisterCount;
	}

	public Integer getCertificationCount() {
		return certificationCount;
	}

	public void setCertificationCount(Integer certificationCount) {
		this.certificationCount = certificationCount;
	}

	public Integer getTodaycertificationCount() {
		return todaycertificationCount;
	}

	public void setTodaycertificationCount(Integer todaycertificationCount) {
		this.todaycertificationCount = todaycertificationCount;
	}

	public Integer getTodayBuyNumber() {
		return todayBuyNumber;
	}

	public void setTodayBuyNumber(Integer todayBuyNumber) {
		this.todayBuyNumber = todayBuyNumber;
	}

	public Integer getTodayNewBuyNumber() {
		return todayNewBuyNumber;
	}

	public void setTodayNewBuyNumber(Integer todayNewBuyNumber) {
		this.todayNewBuyNumber = todayNewBuyNumber;
	}

	public Double getTodayUAuditingOutCashMoney() {
		return todayUAuditingOutCashMoney;
	}

	public void setTodayUAuditingOutCashMoney(Double todayUAuditingOutCashMoney) {
		this.todayUAuditingOutCashMoney = todayUAuditingOutCashMoney;
	}
}
