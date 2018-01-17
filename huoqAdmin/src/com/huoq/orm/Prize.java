package com.huoq.orm;

import java.io.Serializable;
import java.util.Date;

/**
 * 抽奖奖品
 * @author oi
 *
 */
public class Prize implements Serializable {
	private static final long serialVersionUID = 6650794255614435737L;
	private Long id;
	private String prizeName;
	private Date insertTime;
	private String status;   //状态（0：无效   1:有效）
	private String type;    //奖品类型：1 挖宝活动
	private String prizeType;
	private Double prizeValue;
	private Double winningRate;  //中奖概率
	private Double payRate;  //付费抽奖概率
	private Integer serialNum;//奖品排序;(有时候需要跟前端配合返回)
	
	public Integer getSerialNum() {
		return serialNum;
	}
	public void setSerialNum(Integer serialNum) {
		this.serialNum = serialNum;
	}
	public Double getPayRate() {
		return payRate;
	}
	public void setPayRate(Double payRate) {
		this.payRate = payRate;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getPrizeName() {
		return prizeName;
	}
	public void setPrizeName(String prizeName) {
		this.prizeName = prizeName;
	}
	public Date getInsertTime() {
		return insertTime;
	}
	public void setInsertTime(Date insertTime) {
		this.insertTime = insertTime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPrizeType() {
		return prizeType;
	}
	public void setPrizeType(String prizeType) {
		this.prizeType = prizeType;
	}
	public Double getPrizeValue() {
		return prizeValue;
	}
	public void setPrizeValue(Double prizeValue) {
		this.prizeValue = prizeValue;
	}
	public Double getWinningRate() {
		return winningRate;
	}
	public void setWinningRate(Double winningRate) {
		this.winningRate = winningRate;
	}
	
}
