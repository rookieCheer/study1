package com.huoq.orm;

import java.util.Date;

public class PlatInvestors {
    private Long id;//用户id
    private String username;//用户名
    private String real_name;//客户真实姓名
    private Date insertTime;//注册日期
    private Date bandCardTime;//绑卡日期
    private Date fristBuyTime;//首投日期
    private String copies;//投资总额
    private String allMoney;//现存资金(用户资金总额)
    private String buyInMoney;//在贷金额
    private String coinPurseMoney;//零钱罐金额
    private String leftMoney;//账户余额
    private String coupon;//投资券的金额
    private String friendNumber;//邀请好友人数
    private String friendMoney;//好友总金额
    private String insMoney;//好友投资的金额
    private String date;//日期

	public String getReal_name() {
		return real_name;
	}
	public void setReal_name(String real_name) {
		this.real_name = real_name;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getInsMoney() {
		return insMoney;
	}
	public void setInsMoney(String insMoney) {
		this.insMoney = insMoney;
	}
	public Date getInsertTime() {
		return insertTime;
	}
	public void setInsertTime(Date insertTime) {
		this.insertTime = insertTime;
	}
	public String getCopies() {
		return copies;
	}
	public void setCopies(String copies) {
		this.copies = copies;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getCoupon() {
		return coupon;
	}
	public void setCoupon(String coupon) {
		this.coupon = coupon;
	}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getBandCardTime() {
        return bandCardTime;
    }

    public void setBandCardTime(Date bandCardTime) {
        this.bandCardTime = bandCardTime;
    }

    public Date getFristBuyTime() {
        return fristBuyTime;
    }

    public void setFristBuyTime(Date fristBuyTime) {
        this.fristBuyTime = fristBuyTime;
    }

    public String getAllMoney() {
        return allMoney;
    }

    public void setAllMoney(String allMoney) {
        this.allMoney = allMoney;
    }

    public String getBuyInMoney() {
        return buyInMoney;
    }

    public void setBuyInMoney(String buyInMoney) {
        this.buyInMoney = buyInMoney;
    }

    public String getCoinPurseMoney() {
        return coinPurseMoney;
    }

    public void setCoinPurseMoney(String coinPurseMoney) {
        this.coinPurseMoney = coinPurseMoney;
    }

    public String getLeftMoney() {
        return leftMoney;
    }

    public void setLeftMoney(String leftMoney) {
        this.leftMoney = leftMoney;
    }

    public String getFriendNumber() {
        return friendNumber;
    }

    public void setFriendNumber(String friendNumber) {
        this.friendNumber = friendNumber;
    }

    public String getFriendMoney() {
        return friendMoney;
    }

    public void setFriendMoney(String friendMoney) {
        this.friendMoney = friendMoney;
    }
}
