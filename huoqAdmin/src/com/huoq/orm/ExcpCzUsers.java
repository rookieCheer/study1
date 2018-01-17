package com.huoq.orm;

import java.util.Date;

/**
 * 账户异常表
 * 
 * @author sellinwang
 *
 * @createTime 2017-1-14 18:50:10
 */
@SuppressWarnings("serial")
public class ExcpCzUsers implements java.io.Serializable {


	private String id;// 逻辑id,
	private Long usersId;// 用户id
	private String username;//明文用户名
	private Double czMoney;//充值前金额(分)
	private Double bfMoney;//宝付充值后金额(分)
	private String status;//状态  0未修复  1已修复
	private Date insertTime;// 插入时间 不为null
	private Date updateTime;// 更新时间 不为null
	private String note;//备注
	
	/**
	 * @param usersId 
	 * @param userName
	 * @param czMoney
	 * @param bfMoney
	 * @param insertTime
	 * @param note
	 */
	public ExcpCzUsers(long usersId, String username, double czMoney, double bfMoney, String note) {
		this.usersId = usersId;
		this.username = username;
		this.czMoney = czMoney;
		this.bfMoney = bfMoney;
		this.status = "0";
		this.insertTime = new Date();
		this.updateTime = this.insertTime;
		this.note = note;
	}
	
	public ExcpCzUsers(){
		
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Long getUsersId() {
		return usersId;
	}
	public void setUsersId(Long usersId) {
		this.usersId = usersId;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
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
	
	public Double getCzMoney() {
		return czMoney;
	}
	public void setCzMoney(Double czMoney) {
		this.czMoney = czMoney;
	}
	public Double getBfMoney() {
		return bfMoney;
	}
	public void setBfMoney(Double bfMoney) {
		this.bfMoney = bfMoney;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	
}