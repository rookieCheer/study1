package com.huoq.orm;

import java.util.Date;

/**
 * 用来存放预约产品规则
 * @author wxl
 *
 */
@SuppressWarnings("serial")
public class BookingKeyword implements java.io.Serializable {

	private String id;
	private String keyword;   //预约关键字  如新手 月息宝 周利宝
	private String status;   // 0 已启动  1 未启动
	private String type;  //类型  默认为0
	private Date insertTime;//插入时间
	private Date updateTime;//修改时间
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
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
	
}