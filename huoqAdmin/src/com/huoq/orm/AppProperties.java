package com.huoq.orm;


import java.util.Date;

public class AppProperties {
	private int id;
	private String appName;  //app名称
	private Date insertTime; //插入时间
	private Date updateTime; //更新时间
	private String txRule;   //提现规则
	private String czRule;   //充值规则
	private String apiVersion;//api版本
	private String productTitle; //理财列表标题
	private String findTitle;   //发现页面的标题
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public Date getInsertTime() {
		return insertTime;
	}
	public void setInsertTime(Date insertTime) {
		this.insertTime = insertTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getTxRule() {
		return txRule;
	}
	public void setTxRule(String txRule) {
		this.txRule = txRule;
	}
	public String getCzRule() {
		return czRule;
	}
	public void setCzRule(String czRule) {
		this.czRule = czRule;
	}
	public String getApiVersion() {
		return apiVersion;
	}
	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}
	public String getProductTitle() {
		return productTitle;
	}
	public void setProductTitle(String productTitle) {
		this.productTitle = productTitle;
	}
	public String getFindTitle() {
		return findTitle;
	}
	public void setFindTitle(String findTitle) {
		this.findTitle = findTitle;
	}
	
}
