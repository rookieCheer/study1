package com.huoq.orm;

import com.huoq.common.util.QwyUtil;

public class TouTiaoStatistics {
	private String date;//时间(系统获取，或者传入)
	private String channelNumber;//渠道编号
	private String channelName;//渠道名称
	private String phoneModelNumber;//手机型号
	private String place;//位置
	private String touTiaoClicks;//头条广告点击量(当日点击人数)
	private String downLoadNumber;//所有的下载量(当日头条app下载人次)
	private String touTiaoDownLoad;//头条下载量(安卓加ios)
	private String activator;//激活量
	private String activationRegisterConversion;//激活注册转换率
	private String registerNumber;//当日注册人数
	private String bindingNumber;//当日绑定人数
	private String registerAuthenticationConversion;//注册认证转换率
	private String authenticationBuyConversion;//认证首投转换率
	private String firstInvestmentNumber;//首投人数
	private String firstInvestmentMoney;//首投总金额
	private String perCapitaFirstInvestmentMoney;//人均首投总金额
	private String secondInvestmentNumber;//复投人数
	private String secondInvestmentMoney;//复投金额
	private String newSecondInvestmentNumber;//新增复投用户人数
	private String newSecondInvestmentMoney;//新增复投资金额总额
	private String newSecondConversion;//新增复投率
	private String perCapitasecondInvestmentMoney;//人均复投金额
	private String investmentNumber;//投资人数
	private String investmentMoney;//投资金额
	private String perCapitaMoney;//人均投资金额
	public TouTiaoStatistics() {
		this.perCapitaMoney="0";
		this.investmentMoney="0";
		this.investmentNumber="0";
		this.perCapitasecondInvestmentMoney="0";
		this.newSecondConversion="0";
		this.newSecondInvestmentMoney="0";
		this.newSecondInvestmentNumber="0";
		this.secondInvestmentMoney="0";
		this.secondInvestmentNumber="0";
		this.perCapitaFirstInvestmentMoney="0";
		this.firstInvestmentMoney="0";
		this.firstInvestmentNumber="0";
		this.authenticationBuyConversion="0";
		this.registerAuthenticationConversion="0";
		this.bindingNumber="0";
		this.registerNumber="0";
		this.activationRegisterConversion="0";
		this.activator="0";
		this.touTiaoDownLoad="0";
		this.downLoadNumber="0";
		this.touTiaoClicks="0";
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getChannelNumber() {
		return channelNumber;
	}
	public void setChannelNumber(String channelNumber) {
		this.channelNumber = channelNumber;
	}
	public String getChannelName() {
		return channelName;
	}
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
	public String getPhoneModelNumber() {
		return phoneModelNumber;
	}
	public void setPhoneModelNumber(String phoneModelNumber) {
		this.phoneModelNumber = phoneModelNumber;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public String getTouTiaoClicks() {
		if (QwyUtil.isNullAndEmpty(touTiaoClicks)) {
			return "0";
		}
		return touTiaoClicks;
	}
	public void setTouTiaoClicks(String touTiaoClicks) {
		this.touTiaoClicks = touTiaoClicks;
	}
	public String getDownLoadNumber() {
		if (QwyUtil.isNullAndEmpty(downLoadNumber)) {
			return "0";
		}
		return downLoadNumber;
	}
	public void setDownLoadNumber(String downLoadNumber) {
		this.downLoadNumber = downLoadNumber;
	}
	public String getTouTiaoDownLoad() {
		if (QwyUtil.isNullAndEmpty(touTiaoDownLoad)) {
			return "0";
		}
		return touTiaoDownLoad;
	}
	public void setTouTiaoDownLoad(String touTiaoDownLoad) {
		this.touTiaoDownLoad = touTiaoDownLoad;
	}
	public String getActivator() {
		if (QwyUtil.isNullAndEmpty(activator)) {
			return "0";
		}
		return activator;
	}
	public void setActivator(String activator) {
		this.activator = activator;
	}
	public String getActivationRegisterConversion() {
		if (QwyUtil.isNullAndEmpty(activationRegisterConversion)) {
			return "0";
		}
		return activationRegisterConversion;
	}
	public void setActivationRegisterConversion(String activationRegisterConversion) {
		this.activationRegisterConversion = activationRegisterConversion;
	}
	public String getRegisterNumber() {
		if (QwyUtil.isNullAndEmpty(registerNumber)) {
			return "0";
		}
		return registerNumber;
	}
	public void setRegisterNumber(String registerNumber) {
		this.registerNumber = registerNumber;
	}
	public String getBindingNumber() {
		if (QwyUtil.isNullAndEmpty(bindingNumber)) {
			return "0";
		}
		return bindingNumber;
	}
	public void setBindingNumber(String bindingNumber) {
		this.bindingNumber = bindingNumber;
	}
	public String getRegisterAuthenticationConversion() {
		if (QwyUtil.isNullAndEmpty(registerAuthenticationConversion)) {
			return "0";
		}
		return registerAuthenticationConversion;
	}
	public void setRegisterAuthenticationConversion(String registerAuthenticationConversion) {
		this.registerAuthenticationConversion = registerAuthenticationConversion;
	}
	public String getAuthenticationBuyConversion() {
		if (QwyUtil.isNullAndEmpty(authenticationBuyConversion)) {
			return "0";
		}
		return authenticationBuyConversion;
	}
	public void setAuthenticationBuyConversion(String authenticationBuyConversion) {
		this.authenticationBuyConversion = authenticationBuyConversion;
	}
	public String getFirstInvestmentNumber() {
		if (QwyUtil.isNullAndEmpty(firstInvestmentNumber)) {
			return "0";
		}
		return firstInvestmentNumber;
	}
	public void setFirstInvestmentNumber(String firstInvestmentNumber) {
		this.firstInvestmentNumber = firstInvestmentNumber;
	}
	public String getFirstInvestmentMoney() {
		if (QwyUtil.isNullAndEmpty(firstInvestmentMoney)) {
			return "0";
		}
		return firstInvestmentMoney;
	}
	public void setFirstInvestmentMoney(String firstInvestmentMoney) {
		this.firstInvestmentMoney = firstInvestmentMoney;
	}
	public String getPerCapitaFirstInvestmentMoney() {
		if (QwyUtil.isNullAndEmpty(perCapitaFirstInvestmentMoney)) {
			return "0";
		}
		return perCapitaFirstInvestmentMoney;
	}
	public void setPerCapitaFirstInvestmentMoney(String perCapitaFirstInvestmentMoney) {
		this.perCapitaFirstInvestmentMoney = perCapitaFirstInvestmentMoney;
	}
	public String getSecondInvestmentNumber() {
		if (QwyUtil.isNullAndEmpty(secondInvestmentNumber)) {
			return "0";
		}
		return secondInvestmentNumber;
	}
	public void setSecondInvestmentNumber(String secondInvestmentNumber) {
		this.secondInvestmentNumber = secondInvestmentNumber;
	}
	public String getSecondInvestmentMoney() {
		if (QwyUtil.isNullAndEmpty(secondInvestmentMoney)) {
			return "0";
		}
		return secondInvestmentMoney;
	}
	public void setSecondInvestmentMoney(String secondInvestmentMoney) {
		this.secondInvestmentMoney = secondInvestmentMoney;
	}
	public String getNewSecondInvestmentNumber() {
		if (QwyUtil.isNullAndEmpty(newSecondInvestmentNumber)) {
			return "0";
		}
		return newSecondInvestmentNumber;
	}
	public void setNewSecondInvestmentNumber(String newSecondInvestmentNumber) {
		this.newSecondInvestmentNumber = newSecondInvestmentNumber;
	}
	public String getNewSecondInvestmentMoney() {
		if (QwyUtil.isNullAndEmpty(newSecondInvestmentMoney)) {
			return "0";
		}
		return newSecondInvestmentMoney;
	}
	public void setNewSecondInvestmentMoney(String newSecondInvestmentMoney) {
		this.newSecondInvestmentMoney = newSecondInvestmentMoney;
	}
	public String getNewSecondConversion() {
		if (QwyUtil.isNullAndEmpty(newSecondConversion)) {
			return "0";
		}
		return newSecondConversion;
	}
	public void setNewSecondConversion(String newSecondConversion) {
		this.newSecondConversion = newSecondConversion;
	}
	public String getPerCapitasecondInvestmentMoney() {
		if (QwyUtil.isNullAndEmpty(perCapitasecondInvestmentMoney)) {
			return "0";
		}
		return perCapitasecondInvestmentMoney;
	}
	public void setPerCapitasecondInvestmentMoney(String perCapitasecondInvestmentMoney) {
		this.perCapitasecondInvestmentMoney = perCapitasecondInvestmentMoney;
	}
	public String getInvestmentNumber() {
		if (QwyUtil.isNullAndEmpty(investmentNumber)) {
			return "0";
		}
		return investmentNumber;
	}
	public void setInvestmentNumber(String investmentNumber) {
		this.investmentNumber = investmentNumber;
	}
	public String getInvestmentMoney() {
		if (QwyUtil.isNullAndEmpty(investmentMoney)) {
			return "0";
		}
		return investmentMoney;
	}
	public void setInvestmentMoney(String investmentMoney) {
		this.investmentMoney = investmentMoney;
	}
	public String getPerCapitaMoney() {
		if (QwyUtil.isNullAndEmpty(perCapitaMoney)) {
			return "0";
		}
		return perCapitaMoney;
	}
	public void setPerCapitaMoney(String perCapitaMoney) {
		this.perCapitaMoney = perCapitaMoney;
	}
	public TouTiaoStatistics(String date, String channelNumber, String channelName, String phoneModelNumber,
			String place, String touTiaoClicks, String downLoadNumber, String touTiaoDownLoad, String activator,
			String activationRegisterConversion, String registerNumber, String bindingNumber,
			String registerAuthenticationConversion, String authenticationBuyConversion, String firstInvestmentNumber,
			String firstInvestmentMoney, String perCapitaFirstInvestmentMoney, String secondInvestmentNumber,
			String secondInvestmentMoney, String newSecondInvestmentNumber, String newSecondInvestmentMoney,
			String newSecondConversion, String perCapitasecondInvestmentMoney, String investmentNumber,
			String investmentMoney, String perCapitaMoney) {
		super();
		this.date = date;
		this.channelNumber = channelNumber;
		this.channelName = channelName;
		this.phoneModelNumber = phoneModelNumber;
		this.place = place;
		this.touTiaoClicks = touTiaoClicks;
		this.downLoadNumber = downLoadNumber;
		this.touTiaoDownLoad = touTiaoDownLoad;
		this.activator = activator;
		this.activationRegisterConversion = activationRegisterConversion;
		this.registerNumber = registerNumber;
		this.bindingNumber = bindingNumber;
		this.registerAuthenticationConversion = registerAuthenticationConversion;
		this.authenticationBuyConversion = authenticationBuyConversion;
		this.firstInvestmentNumber = firstInvestmentNumber;
		this.firstInvestmentMoney = firstInvestmentMoney;
		this.perCapitaFirstInvestmentMoney = perCapitaFirstInvestmentMoney;
		this.secondInvestmentNumber = secondInvestmentNumber;
		this.secondInvestmentMoney = secondInvestmentMoney;
		this.newSecondInvestmentNumber = newSecondInvestmentNumber;
		this.newSecondInvestmentMoney = newSecondInvestmentMoney;
		this.newSecondConversion = newSecondConversion;
		this.perCapitasecondInvestmentMoney = perCapitasecondInvestmentMoney;
		this.investmentNumber = investmentNumber;
		this.investmentMoney = investmentMoney;
		this.perCapitaMoney = perCapitaMoney;
	}
	
 }
