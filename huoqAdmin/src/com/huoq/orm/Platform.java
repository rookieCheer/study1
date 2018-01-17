package com.huoq.orm;


import java.util.Date;

/**
 * Platform entity. @author MyEclipse Persistence Tools
 */

@SuppressWarnings("serial")
public class Platform implements java.io.Serializable {

	// Fields
	private Long id;//逻辑id; 1
	private Double totalMoney;//发布产品总额
	private Double totalProfit;//已发放总收益
	private Double nototalProfit;//未发放总收益
	private Double collectMoney;//平台投资总额(购买产品总额产品加零钱罐)
	private Double rechargeMoney;//平台充值总额
	private Double todayrechargeMoney;//今日充值总额
	private Double piggybankMney;//零钱罐总额
	private Double remainingMoney;//账户余额总额
	private Double totalCoupon;//赠送的投资券总金额
	private Double useCoupon;//有效使用投资券的总金额
	private Integer registerCount;//注册人数
	private Integer todayregisterCount;//今日注册人数()
	private Integer certificationCount;//累计认证用户()
	private Integer todaycertificationCount;//今日认证用户()
	private Date insertTime;//插入时间
	private Date updateTime;//更新时间
	private Double freshmanCoupon;//新手仅有一次投资机会的投资券
	private Double virtualMoney;//虚拟投资总额;
	private Long totalCoin;//平台发放喵币;
	private Long payCoin;//平台支出总喵币;
	private Long leftCoin;//平台剩余总喵币;
	private Double allCapitalStock;//平台资金存量()
	private Double todayCapitalStock;//今日存量增量()
	private Double allOutCashMoney;//平台累计提现金额()
	private Double todayOutCashMoney;//今日提现金额()
	private Double todayBuyMoney;//今日购买金额()
	private Integer todayBuyNumber;//今日购买人数(包含零钱罐)
	private Integer todayNewBuyNumber;//今日首投用户
	private Double todayUAuditingOutCashMoney;//今日提现未审核
	
	/**
	 * 今日满标企业数量
	 */
	private Integer todayFullScaleCompanyNumber;
	/**
	 * 未审核提现总额
	 */
	private Double uncheckedOutCashMoney;
	/**
	 * 平台交易总额(元)
	 */
	private Double allBuyMoney;
	
	
	
	
   
    
 
    
    public Double getUncheckedOutCashMoney() {
        return uncheckedOutCashMoney;
    }
    
    public void setUncheckedOutCashMoney(Double uncheckedOutCashMoney) {
        this.uncheckedOutCashMoney = uncheckedOutCashMoney;
    }
    public Double getAllBuyMoney() {
        return allBuyMoney;
    }
    public void setAllBuyMoney(Double allBuyMoney) {
        this.allBuyMoney = allBuyMoney;
    }
    public Integer getTodayFullScaleCompanyNumber() {
        return todayFullScaleCompanyNumber;
    }
    public void setTodayFullScaleCompanyNumber(Integer todayFullScaleCompanyNumber) {
        this.todayFullScaleCompanyNumber = todayFullScaleCompanyNumber;
    }
   
    public Double getTodayUAuditingOutCashMoney() {
		return todayUAuditingOutCashMoney;
	}
	public void setTodayUAuditingOutCashMoney(Double todayUAuditingOutCashMoney) {
		this.todayUAuditingOutCashMoney = todayUAuditingOutCashMoney;
	}
	public Integer getTodayNewBuyNumber() {
		return todayNewBuyNumber;
	}
	public void setTodayNewBuyNumber(Integer todayNewBuyNumber) {
		this.todayNewBuyNumber = todayNewBuyNumber;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Double getTotalMoney() {
		return totalMoney;
	}
	public void setTotalMoney(Double totalMoney) {
		this.totalMoney = totalMoney;
	}
	public Double getTotalProfit() {
		return totalProfit;
	}
	public void setTotalProfit(Double totalProfit) {
		this.totalProfit = totalProfit;
	}
	public Double getNototalProfit() {
		return nototalProfit;
	}
	public void setNototalProfit(Double nototalProfit) {
		this.nototalProfit = nototalProfit;
	}
	public Double getCollectMoney() {
		return collectMoney;
	}
	public void setCollectMoney(Double collectMoney) {
		this.collectMoney = collectMoney;
	}
	public Double getRechargeMoney() {
		return rechargeMoney;
	}
	public void setRechargeMoney(Double rechargeMoney) {
		this.rechargeMoney = rechargeMoney;
	}
	public Double getTodayrechargeMoney() {
		return todayrechargeMoney;
	}
	public void setTodayrechargeMoney(Double todayrechargeMoney) {
		this.todayrechargeMoney = todayrechargeMoney;
	}
	public Double getPiggybankMney() {
		return piggybankMney;
	}
	public void setPiggybankMney(Double piggybankMney) {
		this.piggybankMney = piggybankMney;
	}
	public Double getRemainingMoney() {
		return remainingMoney;
	}
	public void setRemainingMoney(Double remainingMoney) {
		this.remainingMoney = remainingMoney;
	}
	public Double getTotalCoupon() {
		return totalCoupon;
	}
	public void setTotalCoupon(Double totalCoupon) {
		this.totalCoupon = totalCoupon;
	}
	public Double getUseCoupon() {
		return useCoupon;
	}
	public void setUseCoupon(Double useCoupon) {
		this.useCoupon = useCoupon;
	}
	public Integer getRegisterCount() {
		return registerCount;
	}
	public void setRegisterCount(Integer registerCount) {
		this.registerCount = registerCount;
	}
	public Integer getTodayregisterCount() {
		return todayregisterCount;
	}
	public void setTodayregisterCount(Integer todayregisterCount) {
		this.todayregisterCount = todayregisterCount;
	}
	public Integer getCertificationCount() {
		return certificationCount;
	}
	public void setCertificationCount(Integer certificationCount) {
		this.certificationCount = certificationCount;
	}
	public Integer getTodaycertificationCount() {
		return todaycertificationCount;
	}
	public void setTodaycertificationCount(Integer todaycertificationCount) {
		this.todaycertificationCount = todaycertificationCount;
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
	public Double getFreshmanCoupon() {
		return freshmanCoupon;
	}
	public void setFreshmanCoupon(Double freshmanCoupon) {
		this.freshmanCoupon = freshmanCoupon;
	}
	public Double getVirtualMoney() {
		return virtualMoney;
	}
	public void setVirtualMoney(Double virtualMoney) {
		this.virtualMoney = virtualMoney;
	}
	public Long getTotalCoin() {
		return totalCoin;
	}
	public void setTotalCoin(Long totalCoin) {
		this.totalCoin = totalCoin;
	}
	public Long getPayCoin() {
		return payCoin;
	}
	public void setPayCoin(Long payCoin) {
		this.payCoin = payCoin;
	}
	public Long getLeftCoin() {
		return leftCoin;
	}
	public void setLeftCoin(Long leftCoin) {
		this.leftCoin = leftCoin;
	}
	public Double getAllCapitalStock() {
		return allCapitalStock;
	}
	public void setAllCapitalStock(Double allCapitalStock) {
		this.allCapitalStock = allCapitalStock;
	}
	public Double getTodayCapitalStock() {
		return todayCapitalStock;
	}
	public void setTodayCapitalStock(Double todayCapitalStock) {
		this.todayCapitalStock = todayCapitalStock;
	}
	public Double getAllOutCashMoney() {
		return allOutCashMoney;
	}
	public void setAllOutCashMoney(Double allOutCashMoney) {
		this.allOutCashMoney = allOutCashMoney;
	}
	public Double getTodayOutCashMoney() {
		return todayOutCashMoney;
	}
	public void setTodayOutCashMoney(Double todayOutCashMoney) {
		this.todayOutCashMoney = todayOutCashMoney;
	}
	public Double getTodayBuyMoney() {
		return todayBuyMoney;
	}
	public void setTodayBuyMoney(Double todayBuyMoney) {
		this.todayBuyMoney = todayBuyMoney;
	}
	public Integer getTodayBuyNumber() {
		return todayBuyNumber;
	}
	public void setTodayBuyNumber(Integer todayBuyNumber) {
		this.todayBuyNumber = todayBuyNumber;
	}
	public Platform(Long id, Double totalMoney, Double totalProfit, Double nototalProfit, Double collectMoney,
			Double rechargeMoney, Double todayrechargeMoney, Double piggybankMney, Double remainingMoney,
			Double totalCoupon, Double useCoupon, Integer registerCount, Integer todayregisterCount,
			Integer certificationCount, Integer todaycertificationCount, Date insertTime, Date updateTime,
			Double freshmanCoupon, Double virtualMoney, Long totalCoin, Long payCoin, Long leftCoin,
			Double allCapitalStock, Double todayCapitalStock, Double allOutCashMoney, Double todayOutCashMoney,
			Double todayBuyMoney, Integer todayBuyNumber,Double uncheckedOutCashMoney) {
		super();
		this.id = id;
		this.totalMoney = totalMoney;
		this.totalProfit = totalProfit;
		this.nototalProfit = nototalProfit;
		this.collectMoney = collectMoney;
		this.rechargeMoney = rechargeMoney;
		this.todayrechargeMoney = todayrechargeMoney;
		this.piggybankMney = piggybankMney;
		this.remainingMoney = remainingMoney;
		this.totalCoupon = totalCoupon;
		this.useCoupon = useCoupon;
		this.registerCount = registerCount;
		this.todayregisterCount = todayregisterCount;
		this.certificationCount = certificationCount;
		this.todaycertificationCount = todaycertificationCount;
		this.insertTime = insertTime;
		this.updateTime = updateTime;
		this.freshmanCoupon = freshmanCoupon;
		this.virtualMoney = virtualMoney;
		this.totalCoin = totalCoin;
		this.payCoin = payCoin;
		this.leftCoin = leftCoin;
		this.allCapitalStock = allCapitalStock;
		this.todayCapitalStock = todayCapitalStock;
		this.allOutCashMoney = allOutCashMoney;
		this.todayOutCashMoney = todayOutCashMoney;
		this.todayBuyMoney = todayBuyMoney;
		this.todayBuyNumber = todayBuyNumber;
		this.uncheckedOutCashMoney = uncheckedOutCashMoney;
	}
	public Platform() {
		super();
		// TODO Auto-generated constructor stub
	}
}