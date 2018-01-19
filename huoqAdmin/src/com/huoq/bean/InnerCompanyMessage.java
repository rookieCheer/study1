package com.huoq.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 满标企业详细信息中部分内容
*    
* 项目名称：xinhuajindian-admin-dashboard   
* 类名称：InnerCompanyMessage   
* 类描述：   
* 创建人：zhuhaojie   
* 创建时间：2018年1月15日 下午3:06:04   
* 修改人：Administrator   
* 修改时间：2018年1月15日 下午3:06:04   
* 修改备注：   
* @version    
*
 */
public class InnerCompanyMessage implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * 标的编号
     */
    private String number;
    /**
     * 到期日期
     */
    private Date expiringDate;
    /**
     * 满标日期
     */
    private Date fullTagDate;
    /**
     * 虚拟投资资金
     */
    private BigDecimal virtualInvest;
    public String getNumber() {
        return number;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    public Date getExpiringDate() {
        return expiringDate;
    }
    public void setExpiringDate(Date expiringDate) {
        this.expiringDate = expiringDate;
    }
    public Date getFullTagDate() {
        return fullTagDate;
    }
    public void setFullTagDate(Date fullTagDate) {
        this.fullTagDate = fullTagDate;
    }
    public BigDecimal getVirtualInvest() {
        return virtualInvest;
    }
    public void setVirtualInvest(BigDecimal virtualInvest) {
        this.virtualInvest = virtualInvest;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((expiringDate == null) ? 0 : expiringDate.hashCode());
        result = prime * result + ((fullTagDate == null) ? 0 : fullTagDate.hashCode());
        result = prime * result + ((number == null) ? 0 : number.hashCode());
        result = prime * result + ((virtualInvest == null) ? 0 : virtualInvest.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        InnerCompanyMessage other = (InnerCompanyMessage) obj;
        if (expiringDate == null) {
            if (other.expiringDate != null)
                return false;
        } else if (!expiringDate.equals(other.expiringDate))
            return false;
        if (fullTagDate == null) {
            if (other.fullTagDate != null)
                return false;
        } else if (!fullTagDate.equals(other.fullTagDate))
            return false;
        if (number == null) {
            if (other.number != null)
                return false;
        } else if (!number.equals(other.number))
            return false;
        if (virtualInvest == null) {
            if (other.virtualInvest != null)
                return false;
        } else if (!virtualInvest.equals(other.virtualInvest))
            return false;
        return true;
    }
    @Override
    public String toString() {
        return "InnerCompanyMessage [number=" + number + ", expiringDate=" + expiringDate + ", fullTagDate="
                + fullTagDate + ", virtualInvest=" + virtualInvest + "]";
    }
    
    
    

}
