package com.huoq.admin.guest.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * 未审核提现总额详情bean
*    
* 项目名称：xinhuajindian-admin-dashboard   
* 类名称：UAuditiongOutCashTotalMoneyDetailBean   
* 类描述：   
* 创建人：zhuhaojie   
* 创建时间：2018年1月19日 下午1:45:48   
   
* @version    
*
 */
public class UAuditiongOutCashTotalMoneyDetailBean implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * 序号
     */
    private String id;
    
    /**
     * 用户名 手机号
     */
    private String userName;
    
    /**
     * 用户名
     */
    private String realName;
    /**
     * 提现金额（元）
     */
    private Double cashMoney;
    
    /**
     * 所属省份
     */
    private String province;
    /**
     * 所属城市
     */
    private String city;
    
    /**
     * 持卡人好友
     */
    private String friendName;
    /**
     * 提现状态
     */
    private String status;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 流水号
     */
    private String serialNumber;
    /**
     * 申请提现时间
     */
    private Date applyCashTime;
    
    /**
     * 审核提现时间
     */
    private Date checkCashTime;
    /**
     * 提现类型
     */
    private String type;
    
    /**
     * 平台订单号
     */
    private String platformOrderNumber;
    
    /**
     * 交易流水号
     */
    private String dealSerialNumber;
    
    /**
     * 提现方式
     */
    private String way;

    
    public String getId() {
        return id;
    }

    
    public void setId(String id) {
        this.id = id;
    }

    
    public String getUserName() {
        return userName;
    }

    
    public void setUserName(String userName) {
        this.userName = userName;
    }

    
    public String getRealName() {
        return realName;
    }

    
    public void setRealName(String realName) {
        this.realName = realName;
    }

    
    public Double getCashMoney() {
        return cashMoney;
    }

    
    public void setCashMoney(Double cashMoney) {
        this.cashMoney = cashMoney;
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

    
    public String getFriendName() {
        return friendName;
    }

    
    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    
    public String getStatus() {
        return status;
    }

    
    public void setStatus(String status) {
        this.status = status;
    }

    
    public String getRemark() {
        return remark;
    }

    
    public void setRemark(String remark) {
        this.remark = remark;
    }

    
    public String getSerialNumber() {
        return serialNumber;
    }

    
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    
    public Date getApplyCashTime() {
        return applyCashTime;
    }

    
    public void setApplyCashTime(Date applyCashTime) {
        this.applyCashTime = applyCashTime;
    }

    
    public Date getCheckCashTime() {
        return checkCashTime;
    }

    
    public void setCheckCashTime(Date checkCashTime) {
        this.checkCashTime = checkCashTime;
    }

    
    public String getType() {
        return type;
    }

    
    public void setType(String type) {
        this.type = type;
    }

    
    public String getPlatformOrderNumber() {
        return platformOrderNumber;
    }

    
    public void setPlatformOrderNumber(String platformOrderNumber) {
        this.platformOrderNumber = platformOrderNumber;
    }

    
    public String getDealSerialNumber() {
        return dealSerialNumber;
    }

    
    public void setDealSerialNumber(String dealSerialNumber) {
        this.dealSerialNumber = dealSerialNumber;
    }

    
    public String getWay() {
        return way;
    }

    
    public void setWay(String way) {
        this.way = way;
    }


    @Override
    public String toString() {
        return "UAuditiongOutCashTotalMoneyDetailBean [id=" + id + ", userName=" + userName + ", realName=" + realName + ", cashMoney=" + cashMoney + ", province=" + province
               + ", city=" + city + ", friendName=" + friendName + ", status=" + status + ", remark=" + remark + ", serialNumber=" + serialNumber + ", applyCashTime="
               + applyCashTime + ", checkCashTime=" + checkCashTime + ", type=" + type + ", platformOrderNumber=" + platformOrderNumber + ", dealSerialNumber=" + dealSerialNumber
               + ", way=" + way + "]";
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((applyCashTime == null) ? 0 : applyCashTime.hashCode());
        result = prime * result + ((cashMoney == null) ? 0 : cashMoney.hashCode());
        result = prime * result + ((checkCashTime == null) ? 0 : checkCashTime.hashCode());
        result = prime * result + ((city == null) ? 0 : city.hashCode());
        result = prime * result + ((dealSerialNumber == null) ? 0 : dealSerialNumber.hashCode());
        result = prime * result + ((friendName == null) ? 0 : friendName.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((platformOrderNumber == null) ? 0 : platformOrderNumber.hashCode());
        result = prime * result + ((province == null) ? 0 : province.hashCode());
        result = prime * result + ((realName == null) ? 0 : realName.hashCode());
        result = prime * result + ((remark == null) ? 0 : remark.hashCode());
        result = prime * result + ((serialNumber == null) ? 0 : serialNumber.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((userName == null) ? 0 : userName.hashCode());
        result = prime * result + ((way == null) ? 0 : way.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        UAuditiongOutCashTotalMoneyDetailBean other = (UAuditiongOutCashTotalMoneyDetailBean) obj;
        if (applyCashTime == null) {
            if (other.applyCashTime != null) return false;
        } else if (!applyCashTime.equals(other.applyCashTime)) return false;
        if (cashMoney == null) {
            if (other.cashMoney != null) return false;
        } else if (!cashMoney.equals(other.cashMoney)) return false;
        if (checkCashTime == null) {
            if (other.checkCashTime != null) return false;
        } else if (!checkCashTime.equals(other.checkCashTime)) return false;
        if (city == null) {
            if (other.city != null) return false;
        } else if (!city.equals(other.city)) return false;
        if (dealSerialNumber == null) {
            if (other.dealSerialNumber != null) return false;
        } else if (!dealSerialNumber.equals(other.dealSerialNumber)) return false;
        if (friendName == null) {
            if (other.friendName != null) return false;
        } else if (!friendName.equals(other.friendName)) return false;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        if (platformOrderNumber == null) {
            if (other.platformOrderNumber != null) return false;
        } else if (!platformOrderNumber.equals(other.platformOrderNumber)) return false;
        if (province == null) {
            if (other.province != null) return false;
        } else if (!province.equals(other.province)) return false;
        if (realName == null) {
            if (other.realName != null) return false;
        } else if (!realName.equals(other.realName)) return false;
        if (remark == null) {
            if (other.remark != null) return false;
        } else if (!remark.equals(other.remark)) return false;
        if (serialNumber == null) {
            if (other.serialNumber != null) return false;
        } else if (!serialNumber.equals(other.serialNumber)) return false;
        if (status == null) {
            if (other.status != null) return false;
        } else if (!status.equals(other.status)) return false;
        if (type == null) {
            if (other.type != null) return false;
        } else if (!type.equals(other.type)) return false;
        if (userName == null) {
            if (other.userName != null) return false;
        } else if (!userName.equals(other.userName)) return false;
        if (way == null) {
            if (other.way != null) return false;
        } else if (!way.equals(other.way)) return false;
        return true;
    } 
}
