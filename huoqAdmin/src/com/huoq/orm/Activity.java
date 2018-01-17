package com.huoq.orm;

import java.util.Date;


/**移动设备激活情况;
 * @author qwy
 *
 * @createTime 2015-06-09 11:20:35
 */
public class Activity implements java.io.Serializable {
	
	private static final long serialVersionUID = -7906846819382814323L;
	
	// Fields

	private String    id;//UUID
	private String    imei;//手机唯一标识码
	private String    idfa;//苹果手机唯一标识码
	private String    channel;//安装渠道
	private Date      insertTime;//激活时间
	private String    type;//类型;null或者1为Android; 2:IOS
	private String    md5Imei;//MD5加密的IMEI号
	private String    callbakUrl;//回调地址URL
	private String channelType;//渠道状态 1.市场渠道 2.广告渠道
	

	public String getIdfa() {
		return idfa;
	}

	public void setIdfa(String idfa) {
		this.idfa = idfa;
	}

	public String getChannelType() {
		return channelType;
	}

	public void setChannelType(String channelType) {
		this.channelType = channelType;
	}

	/** default constructor */
	public Activity() {
	}

	/** minimal constructor */
	public Activity(Date insertTime) {
		this.insertTime = insertTime;
	}

	/** full constructor */
	public Activity(String imei, String channel, Date insertTime) {
		this.imei = imei;
		this.channel = channel;
		this.insertTime = insertTime;
	}

	// Property accessors

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getImei() {
		return this.imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getChannel() {
		return this.channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public Date getInsertTime() {
		return this.insertTime;
	}

	public void setInsertTime(Date insertTime) {
		this.insertTime = insertTime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMd5Imei() {
		return md5Imei;
	}

	public void setMd5Imei(String md5Imei) {
		this.md5Imei = md5Imei;
	}

	public String getCallbakUrl() {
		return callbakUrl;
	}

	public void setCallbakUrl(String callbakUrl) {
		this.callbakUrl = callbakUrl;
	}

	@Override
	public String toString() {
		return id + "|" + imei + "|" + channel + "|" + insertTime
				+ "|" + type + "|" + md5Imei + "|"+ callbakUrl;
	}



}