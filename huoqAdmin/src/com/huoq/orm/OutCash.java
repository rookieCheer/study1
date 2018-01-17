package com.huoq.orm;

import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.QwyUtil;
/**
 * 提现统计表
 * @author Administrator
 *
 */
public class OutCash {
	private String outCashTime;//提现时间
	private String outMoney;//提现金额(元)
	private String realname;//客户姓名
	private String gender;//性别
	private String phone;//手机
	private String category;//类别
	private String province ;//省份
	private String city;
	public OutCash(String outCashTime, String outMoney, String realname, String gender, String phone, String category,
			String province, String city) {
		super();
		this.outCashTime = outCashTime;
		this.outMoney = outMoney;
		this.realname = realname;
		this.gender = gender;
		this.phone = phone;
		this.category = category;
		this.province = province;
		this.city = city;
	}
	@Override
	public String toString() {
		return "OutCash [outCashTime=" + outCashTime + ", outMoney=" + outMoney + ", realname=" + realname + ", gender="
				+ gender + ", phone=" + phone + ", category=" + category + ", province=" + province + ", city=" + city
				+ "]";
	}
	public OutCash() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getOutCashTime() {
		return outCashTime;
	}
	public void setOutCashTime(String outCashTime) {
		this.outCashTime = outCashTime;
	}
	public String getOutMoney() {
		return outMoney;
	}
	public void setOutMoney(String outMoney) {
		this.outMoney = outMoney;
	}
	public String getRealname() {
		return realname;
	}
	public void setRealname(String realname) {
		this.realname = realname;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getPhone() {
		if (!QwyUtil.isNullAndEmpty(phone)) {
			String jiemiphone = DESEncrypt.jieMiUsername(phone);
			return jiemiphone;
		}
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
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
	
	
}
