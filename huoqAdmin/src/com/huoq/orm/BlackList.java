package com.huoq.orm;

import java.io.Serializable;
import java.util.Date;

import com.huoq.common.util.QwyUtil;

/**
 * 黑名单
 * 
 * @author 覃文勇 2016-10-13 14:44:06
 */
public class BlackList implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Long id;//id
	private String username;// '用户名'
	private String ip;// ip地址
	private String status;// 状态0使用中  1:已解除黑名单
	private String note;// 备注
	private Date insertTime;// 插入时间
	private Date updateTime;//修改时间;
	private String imei;//手机imei值;
	private String description;//描述
	
	

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Date getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(Date insertTime) {
		this.insertTime = insertTime;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

}
