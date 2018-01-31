package com.huoq.orm;

import java.io.Serializable;

public class SumOperation implements Serializable {
	private String todayDate;// 日期
	private Double platformreserveMoney;// 预留资金
	private Double baofusaveMoney;// 宝付存量资金
	private String dealtype;// 交易类别
	private Double dealMoney;// 交易金额
	private Double afterdealremainMoney;// 交易后资金余额
	private String rigistpersonCount;// 注册人数
	private String tieCard;// 绑卡用户
	private String allRigist;// 累计注册
	private String allallRigist;// 累计绑卡
	private String buyDeal;// 购买交易
	private String allMoneyinflow;// 总资金流入
	private String txDeal;// 提现交易
	private Double txMoney;// 提现金额
	private String allMoneyinflowA;// 累计资金流入
	private String platformsaveMoney;// 平台资金存量
	private String dealCompany;// 交易公司
	private Double lingqianfaxi;// 零钱罐发息
	private Double regularbiaofaxi;// 定期标发息
	private Double friendreturnMoney;// 好友返利成本
	private Double allcost; // 成本合计
	private Double alllayoff;// 累计划出
	private Double platearnings;// 平台收益
	/**
	 * 宝丰手续费
	 */
	private Double baofuServiceCharge;
	
	/**
	 * 
	* 盈亏
	 */
	private Double proLoss; 
	/**
	 * 资金存量
	 */
	private Double foundStock;
	/**
	 * 当日交易金额
	 */
	private Double todayDealMoney;
	/**
	 * 资金流入
	 */
	private Double foundFlowInto;
	/**
	 * 预留资金
	 */
	private Double reservedFound;
	/**
	 * 定期预留资金
	 */
	private Double constantReservedFound;
	
	
	

	
   


    
    
    public Double getBaofuServiceCharge() {
        return baofuServiceCharge;
    }




    
    public void setBaofuServiceCharge(Double baofuServiceCharge) {
        this.baofuServiceCharge = baofuServiceCharge;
    }




    public Double getProLoss() {
        return proLoss;
    }



    
    public void setProLoss(Double proLoss) {
        this.proLoss = proLoss;
    }



    
    public Double getFoundStock() {
        return foundStock;
    }



    
    public void setFoundStock(Double foundStock) {
        this.foundStock = foundStock;
    }



    
    public Double getTodayDealMoney() {
        return todayDealMoney;
    }



    
    public void setTodayDealMoney(Double todayDealMoney) {
        this.todayDealMoney = todayDealMoney;
    }



    
    public Double getFoundFlowInto() {
        return foundFlowInto;
    }



    
    public void setFoundFlowInto(Double foundFlowInto) {
        this.foundFlowInto = foundFlowInto;
    }



    
    public Double getReservedFound() {
        return reservedFound;
    }



    
    public void setReservedFound(Double reservedFound) {
        this.reservedFound = reservedFound;
    }



    
    public Double getConstantReservedFound() {
        return constantReservedFound;
    }



    
    public void setConstantReservedFound(Double constantReservedFound) {
        this.constantReservedFound = constantReservedFound;
    }



    public SumOperation() {
		super();

	}
	
	

	public SumOperation(String todayDate, Double platformreserveMoney, Double baofusaveMoney, String dealtype,
			Double dealMoney, Double afterdealremainMoney, String rigistpersonCount, String tieCard, String allRigist,
			String allallRigist, String buyDeal, String allMoneyinflow, String txDeal, Double txMoney,
			String allMoneyinflowA, String platformsaveMoney, String dealCompany, Double lingqianfaxi,
			Double regularbiaofaxi, Double friendreturnMoney, Double allcost, Double alllayoff, Double platearnings) {
		super();
		this.todayDate = todayDate;
		this.platformreserveMoney = platformreserveMoney;
		this.baofusaveMoney = baofusaveMoney;
		this.dealtype = dealtype;
		this.dealMoney = dealMoney;
		this.afterdealremainMoney = afterdealremainMoney;
		this.rigistpersonCount = rigistpersonCount;
		this.tieCard = tieCard;
		this.allRigist = allRigist;
		this.allallRigist = allallRigist;
		this.buyDeal = buyDeal;
		this.allMoneyinflow = allMoneyinflow;
		this.txDeal = txDeal;
		this.txMoney = txMoney;
		this.allMoneyinflowA = allMoneyinflowA;
		this.platformsaveMoney = platformsaveMoney;
		this.dealCompany = dealCompany;
		this.lingqianfaxi = lingqianfaxi;
		this.regularbiaofaxi = regularbiaofaxi;
		this.friendreturnMoney = friendreturnMoney;
		this.allcost = allcost;
		this.alllayoff = alllayoff;
		this.platearnings = platearnings;
	}

	@Override
	public String toString() {
		return "SumOperation [todayDate=" + todayDate + ", platformreserveMoney=" + platformreserveMoney
				+ ", baofusaveMoney=" + baofusaveMoney + ", dealtype=" + dealtype + ", dealMoney=" + dealMoney
				+ ", afterdealremainMoney=" + afterdealremainMoney + ", rigistpersonCount=" + rigistpersonCount
				+ ", tieCard=" + tieCard + ", allRigist=" + allRigist + ", allallRigist=" + allallRigist + ", buyDeal="
				+ buyDeal + ", allMoneyinflow=" + allMoneyinflow + ", txDeal=" + txDeal + ", txMoney=" + txMoney
				+ ", allMoneyinflowA=" + allMoneyinflowA + ", platformsaveMoney=" + platformsaveMoney + ", dealCompany="
				+ dealCompany + ", lingqianfaxi=" + lingqianfaxi + ", regularbiaofaxi=" + regularbiaofaxi
				+ ", friendreturnMoney=" + friendreturnMoney + ", allcost=" + allcost + ", alllayoff=" + alllayoff
				+ ", platearnings=" + platearnings + "]";
	}

	public String getTodayDate() {
		return todayDate;
	}

	public void setTodayDate(String todayDate) {
		this.todayDate = todayDate;
	}

	public Double getPlatformreserveMoney() {
		return platformreserveMoney;
	}

	public void setPlatformreserveMoney(Double platformreserveMoney) {
		this.platformreserveMoney = platformreserveMoney;
	}

	public Double getBaofusaveMoney() {
		return baofusaveMoney;
	}

	public void setBaofusaveMoney(Double baofusaveMoney) {
		this.baofusaveMoney = baofusaveMoney;
	}

	public String getDealtype() {
		return dealtype;
	}

	public void setDealtype(String dealtype) {
		this.dealtype = dealtype;
	}

	public Double getDealMoney() {
		return dealMoney;
	}

	public void setDealMoney(Double dealMoney) {
		this.dealMoney = dealMoney;
	}

	public Double getAfterdealremainMoney() {
		return afterdealremainMoney;
	}

	public void setAfterdealremainMoney(Double afterdealremainMoney) {
		this.afterdealremainMoney = afterdealremainMoney;
	}

	public String getRigistpersonCount() {
		return rigistpersonCount;
	}

	public void setRigistpersonCount(String rigistpersonCount) {
		this.rigistpersonCount = rigistpersonCount;
	}

	public String getTieCard() {
		return tieCard;
	}

	public void setTieCard(String tieCard) {
		this.tieCard = tieCard;
	}

	public String getAllRigist() {
		return allRigist;
	}

	public void setAllRigist(String allRigist) {
		this.allRigist = allRigist;
	}

	public String getAllallRigist() {
		return allallRigist;
	}

	public void setAllallRigist(String allallRigist) {
		this.allallRigist = allallRigist;
	}

	public String getBuyDeal() {
		return buyDeal;
	}

	public void setBuyDeal(String buyDeal) {
		this.buyDeal = buyDeal;
	}

	public String getAllMoneyinflow() {
		return allMoneyinflow;
	}

	public void setAllMoneyinflow(String allMoneyinflow) {
		this.allMoneyinflow = allMoneyinflow;
	}

	public String getTxDeal() {
		return txDeal;
	}

	public void setTxDeal(String txDeal) {
		this.txDeal = txDeal;
	}

	public Double getTxMoney() {
		return txMoney;
	}

	public void setTxMoney(Double txMoney) {
		this.txMoney = txMoney;
	}

	public String getAllMoneyinflowA() {
		return allMoneyinflowA;
	}

	public void setAllMoneyinflowA(String allMoneyinflowA) {
		this.allMoneyinflowA = allMoneyinflowA;
	}

	public String getPlatformsaveMoney() {
		return platformsaveMoney;
	}

	public void setPlatformsaveMoney(String platformsaveMoney) {
		this.platformsaveMoney = platformsaveMoney;
	}

	public String getDealCompany() {
		return dealCompany;
	}

	public void setDealCompany(String dealCompany) {
		this.dealCompany = dealCompany;
	}

	public Double getLingqianfaxi() {
		return lingqianfaxi;
	}

	public void setLingqianfaxi(Double lingqianfaxi) {
		this.lingqianfaxi = lingqianfaxi;
	}

	public Double getRegularbiaofaxi() {
		return regularbiaofaxi;
	}

	public void setRegularbiaofaxi(Double regularbiaofaxi) {
		this.regularbiaofaxi = regularbiaofaxi;
	}

	public Double getFriendreturnMoney() {
		return friendreturnMoney;
	}

	public void setFriendreturnMoney(Double friendreturnMoney) {
		this.friendreturnMoney = friendreturnMoney;
	}

	public Double getAllcost() {
		return allcost;
	}

	public void setAllcost(Double allcost) {
		this.allcost = allcost;
	}

	public Double getAlllayoff() {
		return alllayoff;
	}

	public void setAlllayoff(Double alllayoff) {
		this.alllayoff = alllayoff;
	}

	public Double getPlatearnings() {
		return platearnings;
	}

	public void setPlatearnings(Double platearnings) {
		this.platearnings = platearnings;
	}

}
