package com.huoq.orm;


import java.util.Date;


/**邀请好友投资奖励(用于报表显示，无映射)
 * @author yks
 * @createTime 2016-10-11
 */
public class InviteEarnExport {

	// Fields
	private String id;//主键 UUID
	private String beInvitedId;//被邀请人id
	private String inviteId;//邀请人id
	private String beInvitePhone;//被邀请人号码
	private String copies;//购买份数;1元1份;
	private String investorsId;//投资记录id
	private String earnMoney;//邀请投资奖励金额
	private String insertTime;//插入时间
	private String updateTime;//修改时间
	private String returnTime;//发放奖励时间
	private String status;//状态   0：未发放 1：已发放
	private String type;//类型  1 邀请好友投资
	private String note;//备注
	private String inviter; //邀请人姓名

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBeInvitedId() {
		return beInvitedId;
	}

	public void setBeInvitedId(String beInvitedId) {
		this.beInvitedId = beInvitedId;
	}

	public String getInviteId() {
		return inviteId;
	}

	public void setInviteId(String inviteId) {
		this.inviteId = inviteId;
	}

	public String getBeInvitePhone() {
		return beInvitePhone;
	}

	public void setBeInvitePhone(String beInvitePhone) {
		this.beInvitePhone = beInvitePhone;
	}

	public String getCopies() {
		return copies;
	}

	public void setCopies(String copies) {
		this.copies = copies;
	}

	public String getInvestorsId() {
		return investorsId;
	}

	public void setInvestorsId(String investorsId) {
		this.investorsId = investorsId;
	}

	public String getEarnMoney() {
		return earnMoney;
	}

	public void setEarnMoney(String earnMoney) {
		this.earnMoney = earnMoney;
	}

	public String getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(String insertTime) {
		this.insertTime = insertTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getReturnTime() {
		return returnTime;
	}

	public void setReturnTime(String returnTime) {
		this.returnTime = returnTime;
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

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getInviter() {
		return inviter;
	}

	public void setInviter(String inviter) {
		this.inviter = inviter;
	}
}
