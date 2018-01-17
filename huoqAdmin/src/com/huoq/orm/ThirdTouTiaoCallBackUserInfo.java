package com.huoq.orm;

import java.io.Serializable;
import java.util.Date;

public class ThirdTouTiaoCallBackUserInfo implements Serializable{
	private Long id;//id;
	private Integer os; // 客户端类型,0-Android,1-IOS,2-WP,3-Others
	private String imei; // 安卓唯一标识
	private String idfa; // ios唯一标识
	private Date insertTime; // 插入时间
	private String username; //用户名
	private String eventType;//信息来源
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getOs() {
		return os;
	}
	public void setOs(Integer os) {
		this.os = os;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getIdfa() {
		return idfa;
	}
	public void setIdfa(String idfa) {
		this.idfa = idfa;
	}
	public Date getInsertTime() {
		return insertTime;
	}
	public void setInsertTime(Date insertTime) {
		this.insertTime = insertTime;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

	public ThirdTouTiaoCallBackUserInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public ThirdTouTiaoCallBackUserInfo(Long id, Integer os, String imei, String idfa, Date insertTime,
			String username, String eventType) {
		super();
		this.id = id;
		this.os = os;
		this.imei = imei;
		this.idfa = idfa;
		this.insertTime = insertTime;
		this.username = username;
		this.eventType = eventType;
	}
	
}
