package com.huoq.orm;

import com.huoq.common.util.QwyUtil;

import java.util.Date;

public class UserInfoList {
	private String id;
	private String inMoney;
	private String username;
	private String province;
	private String city;
	private String cardType;
	private Date insertTime;
	private Date acinsertTime;
	private String registPlatform;
	private String realName ;
	private String sex;
	private String age;
	private String birthday;
	private String level;
	private String isBindBank;
	private String registChannel;
	private String coinPurseFundsRecordMoney;//零钱罐金额
    private String Title;
	private Date payTime;// 支付时间
	private String inMoney1;

	private String category;//持卡人好友

	public String getCategory() {
		if (QwyUtil.isNullAndEmpty(category)) {
			return "客户";
		}

		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}

	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getInMoney() {
		return inMoney;
	}
	public void setInMoney(String inMoney) {
		this.inMoney = inMoney;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCardType() {
		return cardType;
	}
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
	public Date getInsertTime() {
		return insertTime;
	}
	public void setInsertTime(Date insertTime) {
		this.insertTime = insertTime;
	}
	public String getRegistPlatform() {
		if(registPlatform.equals("1")){
			return"Android移动端";
		}
		if(registPlatform.equals("2")){
			return"IOS移动端";
		}
		if(registPlatform.equals("3")){
			return"微信注册";
		}
		return registPlatform;
	}
	public void setRegistPlatform(String registPlatform) {
		this.registPlatform = registPlatform;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getIsBindBank() {
		return isBindBank;
	}
	public void setIsBindBank(String isBindBank) {
		this.isBindBank = isBindBank;
	}
	public String getRegistChannel() {
		return registChannel;
	}
	public void setRegistChannel(String registChannel) {
		this.registChannel = registChannel;
	}
	public Date getAcinsertTime() {
		return acinsertTime;
	}
	public void setAcinsertTime(Date acinsertTime) {
		this.acinsertTime = acinsertTime;
	}
	public String getCoinPurseFundsRecordMoney() {
		return coinPurseFundsRecordMoney;
	}
	public void setCoinPurseFundsRecordMoney(String coinPurseFundsRecordMoney) {
		this.coinPurseFundsRecordMoney = coinPurseFundsRecordMoney;
	}

	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		Title = title;
	}

	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	public String getInMoney1() {
		return inMoney1;
	}

	public void setInMoney1(String inMoney1) {
		this.inMoney1 = inMoney1;
	}
}
