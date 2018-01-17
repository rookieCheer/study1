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
public class UsersMoneyProblem implements java.io.Serializable {


	private String id;// 逻辑id,
	private Long usersId;// 用户id
	private String userName;//用户名
	private Double freezeMoney;//账户冻结资金
	private Double backMoney;//返款资金
	private String status;// 状态;0:未解决 ,1:已解决
	private Date insertTime;// 插入时间
	private Date updateTime;// 更新时间
	
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
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Double getFreezeMoney() {
		return freezeMoney;
	}
	public void setFreezeMoney(Double freezeMoney) {
		this.freezeMoney = freezeMoney;
	}
	public Double getBackMoney() {
		return backMoney;
	}
	public void setBackMoney(Double backMoney) {
		this.backMoney = backMoney;
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
	
}