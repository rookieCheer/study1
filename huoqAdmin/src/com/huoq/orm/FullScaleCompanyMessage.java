package com.huoq.orm;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 满标企业信息
*    
* 项目名称：xinhuajindian-admin-dashboard   
* 类名称：FullScaleCompanyMessage   
* 类描述：   
* 创建人：zhuhaojie   
* 创建时间：2018年1月15日 上午10:07:13   
* 修改人：Administrator   
* 修改时间：2018年1月15日 上午10:07:13   
* 修改备注：   
* @version    
*
 */
public class FullScaleCompanyMessage implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键
     */
    private String id;
    
    /**
     * 编号 
     */
    private String no;
    /**
     * 借款公司名称
     */
    private String companyName;
    
    /**
     * 法人
     */
    private String legalPerson;
    
    /**
     * 借款额度
     */
    private BigDecimal browLimit;
    /**
     * 标的类型
     */
    private String type;
    /**
     * 企业到期时间
     */
    private Date companyDueTime;
    /**
     * 企业回款时间
     */
    private Date backMoneyTime;
    /**
     * 子标数目
     */
    private Integer childBidNumber;
    
    
    /**
     * 标的编号，满标时间，到期时间，虚拟投资金额
     */
    private List<InnerCompanyMessage> innerMessage;
    public List<InnerCompanyMessage> getInnerMessage() {
        return innerMessage;
    }
    public void setInnerMessage(List<InnerCompanyMessage> innerMessage) {
        this.innerMessage = innerMessage;
    }
    /**
     * 实际投资金额
     */
    private BigDecimal realInvest;
  
    
    
    
    
    
    public String getNo() {
        return no;
    }
    
    public void setNo(String no) {
        this.no = no;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getCompanyName() {
        return companyName;
    }
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    public String getLegalPerson() {
        return legalPerson;
    }
    public void setLegalPerson(String legalPerson) {
        this.legalPerson = legalPerson;
    }
    public BigDecimal getBrowLimit() {
        return browLimit;
    }
    public void setBrowLimit(BigDecimal browLimit) {
        this.browLimit = browLimit;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public Integer getChildBidNumber() {
        return childBidNumber;
    }
    public void setChildBidNumber(Integer childBidNumber) {
        this.childBidNumber = childBidNumber;
    }
   
    public BigDecimal getRealInvest() {
        return realInvest;
    }
    public void setRealInvest(BigDecimal realInvest) {
        this.realInvest = realInvest;
    }
    public Date getCompanyDueTime() {
        return companyDueTime;
    }
    public void setCompanyDueTime(Date companyDueTime) {
        this.companyDueTime = companyDueTime;
    }
    public Date getBackMoneyTime() {
        return backMoneyTime;
    }
    public void setBackMoneyTime(Date backMoneyTime) {
        this.backMoneyTime = backMoneyTime;
    }
   
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((backMoneyTime == null) ? 0 : backMoneyTime.hashCode());
        result = prime * result + ((browLimit == null) ? 0 : browLimit.hashCode());
        result = prime * result + ((childBidNumber == null) ? 0 : childBidNumber.hashCode());
        result = prime * result + ((companyDueTime == null) ? 0 : companyDueTime.hashCode());
        result = prime * result + ((companyName == null) ? 0 : companyName.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((innerMessage == null) ? 0 : innerMessage.hashCode());
        result = prime * result + ((legalPerson == null) ? 0 : legalPerson.hashCode());
        result = prime * result + ((no == null) ? 0 : no.hashCode());
        result = prime * result + ((realInvest == null) ? 0 : realInvest.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        FullScaleCompanyMessage other = (FullScaleCompanyMessage) obj;
        if (backMoneyTime == null) {
            if (other.backMoneyTime != null) return false;
        } else if (!backMoneyTime.equals(other.backMoneyTime)) return false;
        if (browLimit == null) {
            if (other.browLimit != null) return false;
        } else if (!browLimit.equals(other.browLimit)) return false;
        if (childBidNumber == null) {
            if (other.childBidNumber != null) return false;
        } else if (!childBidNumber.equals(other.childBidNumber)) return false;
        if (companyDueTime == null) {
            if (other.companyDueTime != null) return false;
        } else if (!companyDueTime.equals(other.companyDueTime)) return false;
        if (companyName == null) {
            if (other.companyName != null) return false;
        } else if (!companyName.equals(other.companyName)) return false;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        if (innerMessage == null) {
            if (other.innerMessage != null) return false;
        } else if (!innerMessage.equals(other.innerMessage)) return false;
        if (legalPerson == null) {
            if (other.legalPerson != null) return false;
        } else if (!legalPerson.equals(other.legalPerson)) return false;
        if (no == null) {
            if (other.no != null) return false;
        } else if (!no.equals(other.no)) return false;
        if (realInvest == null) {
            if (other.realInvest != null) return false;
        } else if (!realInvest.equals(other.realInvest)) return false;
        if (type == null) {
            if (other.type != null) return false;
        } else if (!type.equals(other.type)) return false;
        return true;
    }
    @Override
    public String toString() {
        return "FullScaleCompanyMessage [id=" + id + ", no=" + no + ", companyName=" + companyName + ", legalPerson=" + legalPerson + ", browLimit=" + browLimit + ", type=" + type
               + ", companyDueTime=" + companyDueTime + ", backMoneyTime=" + backMoneyTime + ", childBidNumber=" + childBidNumber + ", innerMessage=" + innerMessage
               + ", realInvest=" + realInvest + "]";
    }
    
   
    
    
   
}