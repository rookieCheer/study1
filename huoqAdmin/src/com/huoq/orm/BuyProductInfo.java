package com.huoq.orm;

import java.io.Serializable;

import com.huoq.common.util.QwyUtil;

public class BuyProductInfo implements Serializable{
	/**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Integer id; //id用户 id
	private String username; //用户名
	private String insterTime; //购买时间
	private Double inMoney; //购买金额
	private String productName; //商品名称
	private String finishTime; //兑付时间
	private String endTime; //兑付倒计时
	private String realName; //真实姓名
	private String gender; //性别
	private String phone; //手机
	private String category;//类别
	private String province; //省份
	private String city; //城市
	private String friend;//朋友
	
	
	
    
    public Integer getId() {
        return id;
    }

    
    public void setId(Integer id) {
        this.id = id;
    }

    public String getFriend() {
        return friend;
    }
    
    public void setFriend(String friend) {
        this.friend = friend;
    }
    /*public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}*/
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getInsterTime() {
		return insterTime;
	}
	public void setInsterTime(String insterTime) {
		this.insterTime = insterTime;
	}
	public Double getInMoney() {
		return inMoney;
	}
	public void setInMoney(Double inMoney) {
		this.inMoney = inMoney;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getFinishTime() {
		return finishTime;
	}
	public void setFinishTime(String finishTime) {
		this.finishTime = finishTime;
	}
	public String getEndTime() {
		if (!QwyUtil.isNullAndEmpty(endTime)) {
		    try{
		        if (Integer.parseInt(endTime) < 0 ) {
	                return "已兑付";
	            }
		    }catch(NumberFormatException e){
		        return endTime;
		    }
			
		}
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
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
	public String getCategory() {
		if (QwyUtil.isNullAndEmpty(category)) {
			return "客户";
		}
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public BuyProductInfo(String username, String insterTime, double inMoney, String productName, String finishTime,
			String endTime, String realName, String gender, String phone, String category, String province,
			String city) {
		super();
		this.username = username;
		this.insterTime = insterTime;
		this.inMoney = inMoney;
		this.productName = productName;
		this.finishTime = finishTime;
		this.endTime = endTime;
		this.realName = realName;
		this.gender = gender;
		this.phone = phone;
		this.category = category;
		this.province = province;
		this.city = city;
	}
	public BuyProductInfo() {
		super();
		
	}

    @Override
    public String toString() {
        return "BuyProductInfo [username=" + username + ", insterTime=" + insterTime + ", inMoney=" + inMoney + ", productName=" + productName + ", finishTime=" + finishTime
               + ", endTime=" + endTime + ", realName=" + realName + ", gender=" + gender + ", phone=" + phone + ", category=" + category + ", province=" + province + ", city="
               + city + ", friend=" + friend + "]";
    }
	
}
