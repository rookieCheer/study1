package com.huoq.orm;

import java.util.Date;

/**
 * 渠道统计
 * @author 覃文勇
 *2015年7月7日 18:06:47
 */
public class QdtjPlatform {
	private String id;
	
	private Date queryDate;  //查询时间
	private Date insertTime; //数据插入时间
	private String platform; // 平台
	private String jhcs;     //激活次数
	private String zcrs;     //注册人数
	private String zczhl;	 // 注册/激活（人数） 注册转化率
	private String bkrs;	 //绑卡人数
	private String bkzhl;    //绑卡/注册（人数） 绑卡转化率
	private String strs;     //首投人数
	private String stzhl;    //首投转化率
	private String stje;     //首投金额（投资本金不含券）
	private String rjstje;   //人均首投金额 
	private String tzrs;     //投资人数
	private String tzje;	 //投资金额
	private String rjtzje;   //人均投资金额
	private String czje;  //充值金额
	private String txje;  //提现金额
	
	public QdtjPlatform() {
		this.jhcs="0";     //激活次数
		this.zcrs="0";     //注册人数
		this.zczhl="0";	 // 注册/激活（人数） 注册转化率
		this.bkrs="0";	 //绑卡人数
		this.bkzhl="0";    //绑卡/注册（人数） 绑卡转化率
		this.strs="0";     //首投人数
		this.stzhl="0";    //首投转化率
		this.stje="0";     //首投金额（投资本金不含券）
		this.rjstje="0";   //人均首投金额 
		this.tzrs="0";     //投资人数
		this.tzje="0";	 //投资金额
		this.rjtzje="0";   //人均投资金额
		this.czje="0";  //充值金额
		this.txje="0";  //提现金额
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Date getQueryDate() {
		return queryDate;
	}
	public void setQueryDate(Date queryDate) {
		this.queryDate = queryDate;
	}
	public Date getInsertTime() {
		return insertTime;
	}
	public void setInsertTime(Date insertTime) {
		this.insertTime = insertTime;
	}
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	public String getJhcs() {
		return jhcs;
	}
	public void setJhcs(String jhcs) {
		this.jhcs = jhcs;
	}
	public String getZcrs() {
		return zcrs;
	}
	public void setZcrs(String zcrs) {
		this.zcrs = zcrs;
	}
	public String getZczhl() {
		return zczhl;
	}
	public void setZczhl(String zczhl) {
		this.zczhl = zczhl;
	}
	public String getBkrs() {
		return bkrs;
	}
	public void setBkrs(String bkrs) {
		this.bkrs = bkrs;
	}
	public String getBkzhl() {
		return bkzhl;
	}
	public void setBkzhl(String bkzhl) {
		this.bkzhl = bkzhl;
	}
	public String getStrs() {
		return strs;
	}
	public void setStrs(String strs) {
		this.strs = strs;
	}
	public String getStzhl() {
		return stzhl;
	}
	public void setStzhl(String stzhl) {
		this.stzhl = stzhl;
	}
	public String getStje() {
		return stje;
	}
	public void setStje(String stje) {
		this.stje = stje;
	}
	public String getRjstje() {
		return rjstje;
	}
	public void setRjstje(String rjstje) {
		this.rjstje = rjstje;
	}
	public String getTzrs() {
		return tzrs;
	}
	public void setTzrs(String tzrs) {
		this.tzrs = tzrs;
	}
	public String getTzje() {
		return tzje;
	}
	public void setTzje(String tzje) {
		this.tzje = tzje;
	}
	public String getRjtzje() {
		return rjtzje;
	}
	public void setRjtzje(String rjtzje) {
		this.rjtzje = rjtzje;
	}
	public String getCzje() {
		return czje;
	}
	public void setCzje(String czje) {
		this.czje = czje;
	}
	public String getTxje() {
		return txje;
	}
	public void setTxje(String txje) {
		this.txje = txje;
	}
	
}
