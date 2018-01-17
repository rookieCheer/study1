package com.huoq.orm;

import java.util.Date;
/**
 * 用户留言表
 * @author admin
 *
 */

public class HdMessage implements java.io.Serializable {

	// Fields

	private String id;
	private Long usersId;     //用户Id
	private String username;  //用户名
	private Date insertTime;  //插入时间
	private String message;   //留言信息
	private String status;    // 0:通过;1:不通过
	// Constructors

	/** default constructor */
	public HdMessage() {
		
	}

	public HdMessage(Long usersId, String username,Date insertTime, String message,String status) {
		this.usersId = usersId;
		this.username = username;
		this.insertTime = insertTime;
		this.message = message;
		this.status = status;
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

	public Date getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(Date insertTime) {
		this.insertTime = insertTime;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	

}