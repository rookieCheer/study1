package com.huoq.orm;

import java.util.Date;

import com.huoq.common.util.DESEncrypt;

/**内测VIP用户;<br>
 * 优先体验平台活动;
 * @author 覃文勇
 *
 * @createTime 2017-05-17 16:55:32
 */
public class UsersVIPBeat implements java.io.Serializable {

	// Fields

	private String id;
	private Long usersId;//用户ID
	private String status;//默认为0; 0:可用; 1:不可用
	private Date insertTime;//插入时间
	private Date updateTime;//修改时间
	private String username;//手机号码
	private Integer vip;// 'vip等级;0开始;0代表不是vip用户',
	private String note;//备注
	private Double addInterestRates;//加息百分比
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
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Integer getVip() {
		return vip;
	}
	public void setVip(Integer vip) {
		this.vip = vip;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public Double getAddInterestRates() {
		return addInterestRates;
	}
	public void setAddInterestRates(Double addInterestRates) {
		this.addInterestRates = addInterestRates;
	}
	
	

}