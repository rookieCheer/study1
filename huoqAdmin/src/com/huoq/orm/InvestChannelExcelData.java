package com.huoq.orm;

/**
 *
 * 渠道投资统计实体类，用于报表导出，无xml映射
 * Created by yks on 2016/12/2.
 */
public class InvestChannelExcelData {


    private String index;//序号
    private String username;//用户名
    private String nameInvestCount;//姓名_投资次数
    private String product;//产品
    private String payTime;//支付时间
    private String totalInvestment;//总投资（元）
    private String capital;//投资本金（元）
    private String couponMoney;//投资券金额（元）
    private String province;//省份
    private String channelNo;//渠道号
    private String registPlatform;//注册平台
    private String insertTime;//注册时间


    public InvestChannelExcelData() {
    }

    public String getIndex() {
        return this.index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNameInvestCount() {
        return this.nameInvestCount;
    }

    public void setNameInvestCount(String nameInvestCount) {
        this.nameInvestCount = nameInvestCount;
    }

    public String getProduct() {
        return this.product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getPayTime() {
        return this.payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public String getTotalInvestment() {
        return this.totalInvestment;
    }

    public void setTotalInvestment(String totalInvestment) {
        this.totalInvestment = totalInvestment;
    }

    public String getCapital() {
        return this.capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public String getCouponMoney() {
        return this.couponMoney;
    }

    public void setCouponMoney(String couponMoney) {
        this.couponMoney = couponMoney;
    }

    public String getProvince() {
        return this.province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getChannelNo() {
        return this.channelNo;
    }

    public void setChannelNo(String channelNo) {
        this.channelNo = channelNo;
    }

    public String getRegistPlatform() {
        return this.registPlatform;
    }

    public void setRegistPlatform(String registPlatform) {
        this.registPlatform = registPlatform;
    }

	public String getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(String insertTime) {
		this.insertTime = insertTime;
	}
    
}
