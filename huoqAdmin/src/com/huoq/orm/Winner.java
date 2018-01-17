package com.huoq.orm;

import java.io.Serializable;
import java.util.Date;

/**
 * 中奖人
 * @author oi
 *
 */
public class Winner implements Serializable {

	private static final long serialVersionUID = -1448441514177631863L;

	private Long id;
	private String userName;  //用户名 手机号加密;
	private Long usersId; //用户id
	private Long prizeId;  //奖品id
	private Date insertTime;  //中奖时间
	private String status;  //状态  0:无效	1:有效; 
	private String type;   //类型 1 挖宝活动
	private String prizeName;  //奖品名称
	private String date;//没有映射
	private String note; //备注
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Long getPrizeId() {
		return prizeId;
	}
	public Long getUsersId() {
		return usersId;
	}
	public void setUsersId(Long usersId) {
		this.usersId = usersId;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public void setPrizeId(Long prizeId) {
		this.prizeId = prizeId;
	}
	public String getPrizeName() {
		return prizeName;
	}
	public void setPrizeName(String prizeName) {
		this.prizeName = prizeName;
	}
	public Date getInsertTime() {
		return insertTime;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setInsertTime(Date insertTime) {
		this.insertTime = insertTime;
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
	
}
