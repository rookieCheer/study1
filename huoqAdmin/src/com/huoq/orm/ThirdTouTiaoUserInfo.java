package com.huoq.orm;

import java.io.Serializable;
import java.util.Date;

/**
 * 今日头条接口一数据
 * @author Administrator
 *
 */
public class ThirdTouTiaoUserInfo implements Serializable{
	private Long id;//id;
	private String adid; // 广告计划id
	private String cid;//广告创意id
	private String csite;//广告投放位置
	private String ctype;//创意样式
	private String mac; //用户终端的mac地址
	private String ua;//客户终端上报数据时http的header中的user_agent
	private String androidid;//用户终端的andeoidID,md5加密
	private Integer os; // 客户端类型,0-Android,1-IOS,2-WP,3-Others
	private String imei; // 安卓唯一标识
	private String idfa; // ios唯一标识
	private String uuid;//用户终端的uuid;(iosmd5加密)
	private String openudid;// open udid
	private String ip; //媒体投放系统获取的用户终端的公共ip地址
	private String convert_id;//转化跟踪id
	private String callbackParam;// 回调参数
	private Date insertTime; // 时间戳
	private String callback_url;//回调地址
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getAdid() {
		return adid;
	}
	public void setAdid(String adid) {
		this.adid = adid;
	}
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	public String getCsite() {
		return csite;
	}
	public void setCsite(String csite) {
		this.csite = csite;
	}
	public String getCtype() {
		return ctype;
	}
	public void setCtype(String ctype) {
		this.ctype = ctype;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getUa() {
		return ua;
	}
	public void setUa(String ua) {
		this.ua = ua;
	}
	public String getAndroidid() {
		return androidid;
	}
	public void setAndroidid(String androidid) {
		this.androidid = androidid;
	}
	public Integer getOs() {
		return os;
	}
	public void setOs(Integer os) {
		this.os = os;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getIdfa() {
		return idfa;
	}
	public void setIdfa(String idfa) {
		this.idfa = idfa;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getOpenudid() {
		return openudid;
	}
	public void setOpenudid(String openudid) {
		this.openudid = openudid;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getConvert_id() {
		return convert_id;
	}
	public void setConvert_id(String convert_id) {
		this.convert_id = convert_id;
	}
	public String getCallbackParam() {
		return callbackParam;
	}
	public void setCallbackParam(String callbackParam) {
		this.callbackParam = callbackParam;
	}
	public Date getInsertTime() {
		return insertTime;
	}
	public void setInsertTime(Date insertTime) {
		this.insertTime = insertTime;
	}
	public String getCallback_url() {
		return callback_url;
	}
	public void setCallback_url(String callback_url) {
		this.callback_url = callback_url;
	}
	public ThirdTouTiaoUserInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ThirdTouTiaoUserInfo(Long id, String adid, String cid, String csite, String ctype, String mac, String ua,
			String androidid, Integer os, String imei, String idfa, String uuid, String openudid,
			String ip, String convert_id, String callbackParam, Date insertTime, String callback_url) {
		super();
		this.id = id;
		this.adid = adid;
		this.cid = cid;
		this.csite = csite;
		this.ctype = ctype;
		this.mac = mac;
		this.ua = ua;
		this.androidid = androidid;
		this.os = os;
		this.imei = imei;
		this.idfa = idfa;
		this.uuid = uuid;
		this.openudid = openudid;
		this.ip = ip;
		this.convert_id = convert_id;
		this.callbackParam = callbackParam;
		this.insertTime = insertTime;
		this.callback_url = callback_url;
	}
}
