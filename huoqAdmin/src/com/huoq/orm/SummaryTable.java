package com.huoq.orm;

public class SummaryTable {
	private String tadayDate; //日期
	private String nEnrollUser; //新增注册用户
	private String nEnrollIosUser; //新增ＩＯＳ用户
	private String nEnrollAndroidUser;//新增Android用户
	private String nEnrollWeChatUser;//新增微信用户
	private String nAutUser;//新增认证用户
	private String nAutIosUser;//新增ＩＯＳ认证用户
	private String nAutAndroidUser;//新增Android认证用户
	private String nAutWeChatUser;//新增微信认证用户
	private String allEnUser;//累计注册用户
	private String allAutUser;//累计认证用户
	private String todayDeal;//当日购买交易笔数
	private String nUnserDeal;//新用户购买笔数
	private String oUserDeal;//老用户个购买笔数
	private Double todayincapital;//当日资金流入
	private Double nDealMoney;//首投总额
	private Double oDealMoney;//复投总额
	private Double currentProduct;//活期产品部分
	private Double regularProduct;//定期产品部分
	private String allinMoney;//累计资金流入
	private Double todayoutMoney;//当日提现金额
	private Double allHonourMoney;//累计兑付金额
	private Double todayCash;//当日可提取现金
	private Double capitalStock;//资金存量
	private Integer todayFirstInvestPeople;//今日首投人数
	
	
	

    
    public Integer getTodayFirstInvestPeople() {
        return todayFirstInvestPeople;
    }
    
    public void setTodayFirstInvestPeople(Integer todayFirstInvestPeople) {
        this.todayFirstInvestPeople = todayFirstInvestPeople;
    }
    public String getTadayDate() {
		return tadayDate;
	}
	public void setTadayDate(String tadayDate) {
		this.tadayDate = tadayDate;
	}
	public String getNEnrollUser() {
		return nEnrollUser;
	}
	public void setNEnrollUser(String nEnrollUser) {
		this.nEnrollUser = nEnrollUser;
	}
	public String getNEnrollIosUser() {
		return nEnrollIosUser;
	}
	public void setNEnrollIosUser(String nEnrollIosUser) {
		this.nEnrollIosUser = nEnrollIosUser;
	}
	public String getNEnrollAndroidUser() {
		return nEnrollAndroidUser;
	}
	public void setNEnrollAndroidUser(String nEnrollAndroidUser) {
		this.nEnrollAndroidUser = nEnrollAndroidUser;
	}
	public String getNEnrollWeChatUser() {
		return nEnrollWeChatUser;
	}
	public void setNEnrollWeChatUser(String nEnrollWeChatUser) {
		this.nEnrollWeChatUser = nEnrollWeChatUser;
	}
	public String getNAutUser() {
		return nAutUser;
	}
	public void setNAutUser(String nAutUser) {
		this.nAutUser = nAutUser;
	}
	public String getNAutIosUser() {
		return nAutIosUser;
	}
	public void setNAutIosUser(String nAutIosUser) {
		this.nAutIosUser = nAutIosUser;
	}
	public String getNAutAndroidUser() {
		return nAutAndroidUser;
	}
	public void setNAutAndroidUser(String nAutAndroidUser) {
		this.nAutAndroidUser = nAutAndroidUser;
	}
	public String getNAutWeChatUser() {
		return nAutWeChatUser;
	}
	public void setNAutWeChatUser(String nAutWeChatUser) {
		this.nAutWeChatUser = nAutWeChatUser;
	}
	public String getAllEnUser() {
		return allEnUser;
	}
	public void setAllEnUser(String allEnUser) {
		this.allEnUser = allEnUser;
	}
	public String getAllAutUser() {
		return allAutUser;
	}
	public void setAllAutUser(String allAutUser) {
		this.allAutUser = allAutUser;
	}
	public String getTodayDeal() {
		return todayDeal;
	}
	public void setTodayDeal(String todayDeal) {
		this.todayDeal = todayDeal;
	}
	public String getNUnserDeal() {
		return nUnserDeal;
	}
	public void setNUnserDeal(String nUnserDeal) {
		this.nUnserDeal = nUnserDeal;
	}
	public String getOUserDeal() {
		return oUserDeal;
	}
	public void setOUserDeal(String oUserDeal) {
		this.oUserDeal = oUserDeal;
	}
	public Double getTodayincapital() {
		return todayincapital;
	}
	public void setTodayincapital(Double todayincapital) {
		this.todayincapital = todayincapital;
	}
	public Double getNDealMoney() {
		return nDealMoney;
	}
	public void setNDealMoney(Double nDealMoney) {
		this.nDealMoney = nDealMoney;
	}
	public Double getODealMoney() {
		return oDealMoney;
	}
	public void setODealMoney(Double oDealMoney) {
		this.oDealMoney = oDealMoney;
	}
	public Double getCurrentProduct() {
		return currentProduct;
	}
	public void setCurrentProduct(Double currentProduct) {
		this.currentProduct = currentProduct;
	}
	public Double getRegularProduct() {
		return regularProduct;
	}
	public void setRegularProduct(Double regularProduct) {
		this.regularProduct = regularProduct;
	}
	public String getAllinMoney() {
		return allinMoney;
	}
	public void setAllinMoney(String allinMoney) {
		this.allinMoney = allinMoney;
	}
	public Double getTodayoutMoney() {
		return todayoutMoney;
	}
	public void setTodayoutMoney(Double todayoutMoney) {
		this.todayoutMoney = todayoutMoney;
	}
	public Double getAllHonourMoney() {
		return allHonourMoney;
	}
	public void setAllHonourMoney(Double allHonourMoney) {
		this.allHonourMoney = allHonourMoney;
	}
	public Double getTodayCash() {
		return todayCash;
	}
	public void setTodayCash(Double todayCash) {
		this.todayCash = todayCash;
	}
	public Double getCapitalStock() {
		return capitalStock;
	}
	public void setCapitalStock(Double capitalStock) {
		this.capitalStock = capitalStock;
	}
	public SummaryTable() {
		super();
		// TODO Auto-generated constructor stub
	}
	public SummaryTable(String tadayDate, String nEnrollUser, String nEnrollIosUser, String nEnrollAndroidUser,
			String nEnrollWeChatUser, String nAutUser, String nAutIosUser, String nAutAndroidUser,
			String nAutWeChatUser, String allEnUser, String allAutUser, String todayDeal, String nUnserDeal,
			String oUserDeal, Double todayincapital, Double nDealMoney, Double oDealMoney, Double currentProduct,
			Double regularProduct, String allinMoney, Double todayoutMoney, Double allHonourMoney, Double todayCash,
			Double capitalStock) {
		super();
		this.tadayDate = tadayDate;
		this.nEnrollUser = nEnrollUser;
		this.nEnrollIosUser = nEnrollIosUser;
		this.nEnrollAndroidUser = nEnrollAndroidUser;
		this.nEnrollWeChatUser = nEnrollWeChatUser;
		this.nAutUser = nAutUser;
		this.nAutIosUser = nAutIosUser;
		this.nAutAndroidUser = nAutAndroidUser;
		this.nAutWeChatUser = nAutWeChatUser;
		this.allEnUser = allEnUser;
		this.allAutUser = allAutUser;
		this.todayDeal = todayDeal;
		this.nUnserDeal = nUnserDeal;
		this.oUserDeal = oUserDeal;
		this.todayincapital = todayincapital;
		this.nDealMoney = nDealMoney;
		this.oDealMoney = oDealMoney;
		this.currentProduct = currentProduct;
		this.regularProduct = regularProduct;
		this.allinMoney = allinMoney;
		this.todayoutMoney = todayoutMoney;
		this.allHonourMoney = allHonourMoney;
		this.todayCash = todayCash;
		this.capitalStock = capitalStock;
	}
	@Override
	public String toString() {
		return "SummaryTable [tadayDate=" + tadayDate + ", nEnrollUser=" + nEnrollUser + ", nEnrollIosUser="
				+ nEnrollIosUser + ", nEnrollAndroidUser=" + nEnrollAndroidUser + ", nEnrollWeChatUser="
				+ nEnrollWeChatUser + ", nAutUser=" + nAutUser + ", nAutIosUser=" + nAutIosUser + ", nAutAndroidUser="
				+ nAutAndroidUser + ", nAutWeChatUser=" + nAutWeChatUser + ", allEnUser=" + allEnUser + ", allAutUser="
				+ allAutUser + ", todayDeal=" + todayDeal + ", nUnserDeal=" + nUnserDeal + ", oUserDeal=" + oUserDeal
				+ ", todayincapital=" + todayincapital + ", nDealMoney=" + nDealMoney + ", oDealMoney=" + oDealMoney
				+ ", currentProduct=" + currentProduct + ", regularProduct=" + regularProduct + ", allinMoney="
				+ allinMoney + ", todayoutMoney=" + todayoutMoney + ", allHonourMoney=" + allHonourMoney
				+ ", todayCash=" + todayCash + ", capitalStock=" + capitalStock + "]";
	}
}
