package com.huoq.orm;

import java.util.Date;


/**抽奖次数表
 * @author wxl
 *
 * @createTime 2017年4月25日13:24:10
 */
@SuppressWarnings("serial")
public class LotteryTimes implements java.io.Serializable {

	private Long id;
	private Long usersId;//用户Id;
	private Long freeNum;//免费抽奖次数  
	private Long payNum;//支付获得的抽奖次数
	private Date insertTime;//插入时间
	private Date updateTime;//更新时间
	private Users users;//用户表;
	private String type; //活动类型 null:五月挖宝 1：微信抽奖 2端午活动 3六月热活动
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getUsersId() {
		return usersId;
	}
	public void setUsersId(Long usersId) {
		this.usersId = usersId;
	}
	public Long getFreeNum() {
		return freeNum;
	}
	public void setFreeNum(Long freeNum) {
		this.freeNum = freeNum;
	}
	public Long getPayNum() {
		return payNum;
	}
	public void setPayNum(Long payNum) {
		this.payNum = payNum;
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
	public Users getUsers() {
		return users;
	}
	public void setUsers(Users users) {
		this.users = users;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

}