package com.huoq.orm;

import com.huoq.common.util.QwyUtil;

import java.util.Date;

/**
 * 渠道统计
 * @author 覃文勇
 *2015年7月7日 18:06:47
 */
public class Qdtj {
	private Long id;
	private String index;//序号 没有orm映射
	private String channel;//渠道号
	private String channelName;//渠道名称
	private String channelCode;//渠道编码  2017-01-09 添加
	private String activityCount;//激活人数(激活量)
	private String regCount;//注册人数(当日注册人数)
	private String bindCount;//绑定人数(认证人数)
	private String qdzhl;//渠道转换率(激活注册转换率)
	private String tzrs;//投资人数
	private String strs;//首投人数
	private String stje;//首投金额(总首投金额)
	private String ftrs;//复投人数
	private String ftje;//复投金额
	private String tzje;//投资金额
	private String cftzl;//重复投资率()
	private String czcount;//充值次数
	private String czje;//充值金额
	private Date date;//渠道日期 精确到天 
	private Date insertTime;//插入时间
	private String xzftyh;//新增复投用户
	private String xhftyhtzze;//新增复投用户投资总额
	private String rzstzhl;//认证首投转换率
	private String rjstje;//人均首投金额
	private String xzftl;//新增复投率
	private String rjftje;//人均复投金额
	private String rjtzje;//人均投资金额
	private String zcjhzhl;//注册认证转换率
	private String dateStr;//渠道日期 字符串 页面展示用  精确到天
	private String lqgje;//零钱罐金额
	private String lqgrs;//零钱罐人数
	private String channelType;//渠道状态
	private String channelCost;//渠道费用
	private String activityCost;//激活成本
	private String registerCost;//注册成本
	private String fristBuyCost;//首投成本
	private String fristBuyROI;//首投ROI
	private String secondCost;//复投成本
	private String secondROI;//复投ROI
	private String buyROI;//投资ROI
	public String getChannelType() {
		return channelType;
	}

	public void setChannelType(String channelType) {
		this.channelType = channelType;
	}

	public Qdtj() {
		this.activityCost="0";
		this.fristBuyCost="0";
		this.fristBuyROI="0";
		this.secondCost="0";
		this.secondROI="0";
		this.buyROI="0";
		this.activityCount = "0";
		this.regCount = "0";
		this.bindCount = "0";
		this.qdzhl = "0";
		this.tzrs = "0";
		this.strs = "0";
		this.stje = "0";
		this.ftrs = "0";
		this.ftje = "0";
		this.tzje = "0";
		this.cftzl = "0";
		this.czcount = "0";
		this.czje = "0";
		this.xzftyh = "0";
		this.xhftyhtzze = "0";
		this.rzstzhl = "0";
		this.rjstje = "0";
		this.xzftl = "0";
		this.rjftje = "0";
		this.rjtzje = "0";
		this.dateStr="0";
		this.lqgje="0";
		this.lqgrs="0";
		this.registerCost="0";
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getActivityCount() {
		if (QwyUtil.isNullAndEmpty(activityCount)) {
			return"0";
		}
		return activityCount;
	}
	public void setActivityCount(String activityCount) {
		this.activityCount = activityCount;
	}
	public String getRegCount() {
		if (QwyUtil.isNullAndEmpty(regCount)) {
			return"0";
		}
		return regCount;
	}
	public void setRegCount(String regCount) {
		this.regCount = regCount;
	}
	public String getBindCount() {
		if (QwyUtil.isNullAndEmpty(bindCount)) {
			return"0";
		}
		return bindCount;
	}
	public void setBindCount(String bindCount) {
		this.bindCount = bindCount;
	}
	public String getQdzhl() {
		if (QwyUtil.isNullAndEmpty(qdzhl)) {
			return"0";
		}
		return qdzhl;
	}
	public void setQdzhl(String qdzhl) {
		this.qdzhl = qdzhl;
	}
	public String getTzrs() {
		if (QwyUtil.isNullAndEmpty(tzrs)) {
			return"0";
		}
		return tzrs;
	}
	public void setTzrs(String tzrs) {
		this.tzrs = tzrs;
	}
	public String getStrs() {
		if (QwyUtil.isNullAndEmpty(strs)) {
			return"0";
		}
		return strs;
	}
	public void setStrs(String strs) {
		this.strs = strs;
	}
	public String getStje() {
		if (QwyUtil.isNullAndEmpty(stje)) {
			return"0";
		}
		return stje;
	}
	public void setStje(String stje) {
		this.stje = stje;
	}
	public String getFtrs() {
		if (QwyUtil.isNullAndEmpty(ftrs)) {
			return"0";
		}
		return ftrs;
	}
	public void setFtrs(String ftrs) {
		this.ftrs = ftrs;
	}
	public String getTzje() {
		if (QwyUtil.isNullAndEmpty(tzje)) {
			return"0";
		}
		return tzje;
	}
	public void setTzje(String tzje) {
		this.tzje = tzje;
	}
	public String getCftzl() {
		if (QwyUtil.isNullAndEmpty(cftzl)) {
			return"0";
		}
		return cftzl;
	}
	public void setCftzl(String cftzl) {
		this.cftzl = cftzl;
	}
	public String getCzcount() {
		if (QwyUtil.isNullAndEmpty(czcount)) {
			return"0";
		}
		return czcount;
	}
	public void setCzcount(String czcount) {
		this.czcount = czcount;
	}
	public String getCzje() {
		if (QwyUtil.isNullAndEmpty(czje)) {
			return"0";
		}
		return czje;
	}
	public void setCzje(String czje) {
		this.czje = czje;
	}
	public String getChannelName() {
		if (QwyUtil.isNullAndEmpty(channelName)) {
			return"";
		}
		return channelName;
	}
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
	public String getChannelCode() {
		if (QwyUtil.isNullAndEmpty(channelCode)) {
			return"";
		}
		return channelCode;
	}
	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}
	public String getFtje() {
		if (QwyUtil.isNullAndEmpty(ftje)) {
			return"";
		}
		return ftje;
	}
	public void setFtje(String ftje) {
		this.ftje = ftje;
	}
	public Date getInsertTime() {
		return insertTime;
	}
	public void setInsertTime(Date insertTime) {
		this.insertTime = insertTime;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Date getDate() {
		return date;
	}

	public String getDateStr() {
		return dateStr;
	}

	public void setDateStr(String dateStr) {
		this.dateStr = dateStr;
	}
	public String getXzftyh() {
		if (QwyUtil.isNullAndEmpty(xzftyh)) {
			return"0";
		}
		return xzftyh;
	}
	public void setXzftyh(String xzftyh) {
		this.xzftyh = xzftyh;
	}
	public String getXhftyhtzze() {
		if (QwyUtil.isNullAndEmpty(xhftyhtzze)) {
			return"0";
		}
		return xhftyhtzze;
	}
	public void setXhftyhtzze(String xhftyhtzze) {
		this.xhftyhtzze = xhftyhtzze;
	}
	public String getRzstzhl() {
		if (QwyUtil.isNullAndEmpty(rzstzhl)) {
			return"0";
		}
		return rzstzhl;
	}
	public void setRzstzhl(String rzstzhl) {
		this.rzstzhl = rzstzhl;
	}
	public String getRjstje() {
		if (QwyUtil.isNullAndEmpty(rjstje)) {
			return"0";
		}
		return rjstje;
	}
	public void setRjstje(String rjstje) {
		this.rjstje = rjstje;
	}
	public String getXzftl() {
		if (QwyUtil.isNullAndEmpty(xzftl)) {
			return"0";
		}
		return xzftl;
	}
	public void setXzftl(String xzftl) {
		this.xzftl = xzftl;
	}
	public String getRjftje() {
		if (QwyUtil.isNullAndEmpty(rjftje)) {
			return"0";
		}
		return rjftje;
	}
	public void setRjftje(String rjftje) {
		this.rjftje = rjftje;
	}
	public String getRjtzje() {
		if (QwyUtil.isNullAndEmpty(rjtzje)) {
			return"0";
		}
		return rjtzje;
	}
	public void setRjtzje(String rjtzje) {
		this.rjtzje = rjtzje;
	}
	public String getZcjhzhl() {
		if (QwyUtil.isNullAndEmpty(zcjhzhl)) {
			return"0";
		}
		return zcjhzhl;
	}
	public void setZcjhzhl(String zcjhzhl) {
		this.zcjhzhl = zcjhzhl;
	}
	public String getLqgje() {
		return lqgje;
	}
	public void setLqgje(String lqgje) {
		this.lqgje = lqgje;
	}
	public String getLqgrs() {
		return lqgrs;
	}
	public void setLqgrs(String lqgrs) {
		this.lqgrs = lqgrs;
	}

	public String getChannelCost() {
		if (QwyUtil.isNullAndEmpty(channelCost)) {
			return"0";
		}
		return channelCost;
	}
	public void setChannelCost(String channelCost) {
		this.channelCost = channelCost;
	}

	public String getActivityCost() {
		return activityCost;
	}

	public void setActivityCost(String activityCost) {
		this.activityCost = activityCost;
	}

	public String getFristBuyCost() {
		return fristBuyCost;
	}

	public void setFristBuyCost(String fristBuyCost) {
		this.fristBuyCost = fristBuyCost;
	}

	public String getFristBuyROI() {
		return fristBuyROI;
	}

	public void setFristBuyROI(String fristBuyROI) {
		this.fristBuyROI = fristBuyROI;
	}

	public String getSecondCost() {
		return secondCost;
	}

	public void setSecondCost(String secondCost) {
		this.secondCost = secondCost;
	}

	public String getSecondROI() {
		return secondROI;
	}

	public void setSecondROI(String secondROI) {
		this.secondROI = secondROI;
	}

	public String getBuyROI() {
		return buyROI;
	}

	public void setBuyROI(String buyROI) {
		this.buyROI = buyROI;
	}

    public String getRegisterCost() {
        return registerCost;
    }

    public void setRegisterCost(String registerCost) {
        this.registerCost = registerCost;
    }
}
