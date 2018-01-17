package com.huoq.orm;

import java.util.Date;


/**
 * orm 映射表 访客信息实体类
 * create by yks on 2016-11-08
 */
public class UsersGuest implements java.io.Serializable {

	// Fields
	private Long id;
	private String username;
	private String password;
	private Long userType;
	private String userStatus;//状态;0:帐号可用; 1:帐号不可用;
	private Date insertTime;
	private Date updateTime;
	private Date lastTime;//上一次登录时间
	private String note;
	private String channelNo; //渠道号


	public UsersGuest() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Long getUserType() {
		return userType;
	}

	public void setUserType(Long userType) {
		this.userType = userType;
	}

	public String getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
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

	public Date getLastTime() {
		return lastTime;
	}

	public void setLastTime(Date lastTime) {
		this.lastTime = lastTime;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getChannelNo() {
		return channelNo;
	}

	public void setChannelNo(String channelNo) {
		this.channelNo = channelNo;
	}

	@Override
	public String toString() {
		return "UsersGuest{" +
				"id=" + id +
				", username='" + username + '\'' +
				", password='" + password + '\'' +
				", userType=" + userType +
				", userStatus='" + userStatus + '\'' +
				", insertTime=" + insertTime +
				", updateTime=" + updateTime +
				", lastTime=" + lastTime +
				", note='" + note + '\'' +
				", channelNo='" + channelNo + '\'' +
				'}';
	}
}