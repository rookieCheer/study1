package com.huoq.orm;

import java.util.Date;


/**抽奖次数记录表;记录抽奖次数的变动情况;
 * @author wxl
 *
 * @createTime 2017年4月27日10:27:10
 */
@SuppressWarnings("serial")
public class LotteryRecord implements java.io.Serializable {

	private Long id;
	private Long usersId;//用户Id;
	private String type;//类型 0:增加机会次数;1:使用机会次数
	private Date insertTime;//插入时间
	private Date updateTime;//修改时间
	private String note;//备注;
	private String address;//兑换地址
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
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

}