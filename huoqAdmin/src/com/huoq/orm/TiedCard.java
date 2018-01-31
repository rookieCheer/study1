package com.huoq.orm;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.QwyUtil;

/**
 * 绑卡信息统计,无映射
 * @author Administrator
 *
 */
public class TiedCard implements Serializable{
	private String zinsertTime;//注册日期
	private String insertTime;//绑定日期
	private String bankName;//银行名称
	private String bankAccount;//银行卡号
	private String registPlatform;//'0:web端注册; 1:Android移动端; 2:IOS移动端; 3:微信注册;
	private String id; //用户id
	private String realName;//真实姓名
	private String phone; //手机号
	private String cardType;//手机运行商类型
	private String idCard;//身份证号
	private String province;//省份
	private String city;//城市
	private String gender;//性别
	private String age;//年龄
	private String birthday;//生日
	private String cardFriend;//持卡人好友
	private String channel;//渠道
	/**
	 * 编号
	 */
	private int no;
	
	
	
    public int getNo() {
        return no;
    }
    
    public void setNo(int no) {
        this.no = no;
    }
    public String getInsertTime() {
		return insertTime;
	}
	public void setInsertTime(String insertTime) {
		this.insertTime = insertTime;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getBankAccount() {
		if (!QwyUtil.isNullAndEmpty(bankAccount)) {
			return DESEncrypt.jieMiBankCard(bankAccount);
		}
		return bankAccount;
	}
	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}
	public String getRegistPlatform() {//0:web端注册; 1:Android移动端; 2:IOS移动端; 3:微信注册
		if (!QwyUtil.isNullAndEmpty(registPlatform)) {
			if (registPlatform.equals("0")) {
				return "web端"; 
			}
			if (registPlatform.equals("1")) {
				return "Android移动端";
			}
			if (registPlatform.equals("2")) {
				return "IOS移动端";
			}
			if (registPlatform.equals("3")) {
				return "微信注册";
			}
		}
		return registPlatform;
	}
	public void setRegistPlatform(String registPlatform) {
		this.registPlatform = registPlatform;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getPhone() {
		if(!QwyUtil.isNullAndEmpty(phone)){
			return DESEncrypt.jieMiUsername(phone);
		}
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getCardType() {
		return cardType;
	}
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
	public String getIdCard() {
		if (!QwyUtil.isNullAndEmpty(idCard)) {
			return DESEncrypt.jieMiIdCard(idCard);
		}
		return idCard;
	}
	public void setIdCard(String idCard) {
		this.idCard = idCard;
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
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
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
	public String getZinsertTime() {
		return zinsertTime;
	}
	public void setZinsertTime(String zinsertTime) {
		this.zinsertTime = zinsertTime;
	}
	public String getCardFriend() {
		if (QwyUtil.isNullAndEmpty(cardFriend)) {
			return"客户";
		}
		return cardFriend;
	}
	public void setCardFriend(String cardFriend) {
		this.cardFriend = cardFriend;
	}
	public String getChannel() {
if (QwyUtil.isNullAndEmpty(channel)) {
		return "微信注册";
		}
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}

}
