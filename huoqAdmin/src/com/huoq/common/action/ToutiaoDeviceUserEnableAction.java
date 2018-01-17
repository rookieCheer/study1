/**
 * 
 */
package com.huoq.common.action;

import java.io.IOException;
import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;
import org.springframework.util.DigestUtils;

import com.huoq.common.bean.DeviceEnableBean;
import com.huoq.common.guava.ActivityCache;
import com.huoq.common.util.PropertiesUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.Activity;
import com.huoq.thread.action.AutoExcuteDeviceActiveCallbakThread;

/**
 * 对今日头条暴露的接口，统计设备用户激活数量
 * @author gaoshuang
 * @email  gaoshvng@163.com
 * @createTime 2017年5月24日 下午4:01:45
 */
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/UserEnable")
@Results({ 
	@Result(name = "error", value = "/Product/error.jsp") 
})
public class ToutiaoDeviceUserEnableAction extends BaseAction{
	
	private static Logger LOGGER = Logger.getLogger(ToutiaoDeviceUserEnableAction.class); 
	
	/*
	 * 今日头条IOS签名秘钥
	 */
	public static final String IOS_TOUTIAO_CONVERT_SECRET_KEY = (String) PropertiesUtil.getProperties("IOS", "app.properties");
	/*
	 * 今日头条ANDROID签名秘钥
	 */
	public static final String ANDROID_TOUTIAO_CONVERT_SECRET_KEY = (String) PropertiesUtil.getProperties("Android", "app.properties");
	/*
	 * 广告计划 id 
	 */
	private String adid;
	/*
	 * 广告创意 id 
	 */
	private String cid;
	/*
	 * iOS IDFA 适用iOS6 及以上 
	 */
	private String idfa;
	/*
	 * 设备IMEI号
	 */
	private String imei;
	/*
	 * 用户终端的MAC地址
	 */
	private String mac;
	/*
	 * 客户端操作系统的类型 0–Android 1–iOS 2– WP3
	 */
	private String os;
	/*
	 * 用户终端AndroidID
	 */
	private String androidid;
	/*
	 * 头条客户端触发监测 的时间 
	 */
	private String timestamp;
	/*
	 * 激活回调地址(方案一) 
	 */
	private String callback_url;
	/*
	 * 激活回调参数(方案二)
	 */
	private String callback;
	/*
	 * 签名
	 */
	private String sign;
	
	
	@Resource
	DeviceEnableBean deviceEnableBean;
	
	@Resource
	AutoExcuteDeviceActiveCallbakThread autoExcuteDeviceActiveCallbakThread;
	/**
	 * 对头条暴露的接口 完成头条请求数据的接收及返回系统查询到的设备信息
	 * @return
	 */
	public String excute(){
		LOGGER.info("接收到今日头条用户点击下载枫叶应用请求,请求参数列表:" + this.toString());

		String reponseMsg = "[ret:'0', message:'success']";

		//今日头条请求签名验证
		if (sign.startsWith("__")) {
			LOGGER.info("今日头条调用接口签名为空！！！");
			return null;
		}
		
		if(os.startsWith("__")){
			LOGGER.info("今日头条调用系统接口参数错误！！！");
			return null;
		}
		
		StringBuffer requestURL = request.getRequestURL();

		if (StringUtils.isNotBlank(request.getQueryString())) {
			requestURL.append('?');
			requestURL.append(request.getQueryString());
		}
		String actionUrl = requestURL.substring(0, requestURL.lastIndexOf("&")).toString();
		
		if(Long.parseLong(os) == 0){
			actionUrl = actionUrl + ANDROID_TOUTIAO_CONVERT_SECRET_KEY;
			if(!sign.equals(DigestUtils.md5DigestAsHex((actionUrl).getBytes()))){
				LOGGER.info("今日头条调用系统接口ANDROID签名验证失败！！！");
				return null;        
			}
		}else if(Long.parseLong(os) == 1){
			actionUrl = actionUrl + IOS_TOUTIAO_CONVERT_SECRET_KEY;
			if(!sign.equals(DigestUtils.md5DigestAsHex((actionUrl).getBytes()))){
				LOGGER.info("今日头条调用系统接口IOS签名验证失败！！！");
				return null;
			}
		}else{
			LOGGER.info("系统编号识别失败 os:" + os);
			return null;
		}
		
		
		int queryTime = 0;
		
		if(StringUtils.isBlank(imei)||imei.startsWith("__")){
			imei = null;
		}else{
			ActivityCache ac = ActivityCache.getInstance();
			queryTime = (int) ac.getValue(ac.getkey(imei));
			ac.put(ac.getkey(imei), ++queryTime);
			if(queryTime > 1){
				LOGGER.info("今日头条发送该设备激活请求过多,本次请求放弃处理!!! imei"+imei + " Time:"+queryTime);
				return null;
			}
		}
		
		if(StringUtils.isBlank(idfa)||idfa.startsWith("__")){
			idfa = null;
		}else{
			ActivityCache ac = ActivityCache.getInstance();
			queryTime = (int) ac.getValue(ac.getkey(idfa));
			ac.put(ac.getkey(idfa), ++queryTime);
			if(queryTime > 1){
				LOGGER.info("今日头条发送该设备激活请求过多,本次请求放弃处理!!! idfa:"+idfa + " Time:"+queryTime);
				return null;
			}
		}
		
		//TODO 写入头条激活请求到数据表 异步回调今日头条接口完成设备激活统计
		Activity ac = new Activity();
		ac.setInsertTime(new Date());
		ac.setCallbakUrl(callback_url);
		if(StringUtils.isNotBlank(imei)){
			ac.setImei(imei);
		}else if(StringUtils.isNotBlank(idfa)){
			ac.setImei(idfa);
		}else{
			LOGGER.info("设备编号 今日头条传值错误....");
			return null;
		}
		ac.setType(String.valueOf(Long.parseLong(os)+1));
		
		//配置落如数据表 异步线程根据激活信息选择回调今日头条接口
		deviceEnableBean.saveChannelDeviceActivityRepuest(ac);
		
		try {
			getResponse().getWriter().write(reponseMsg);
		} catch (IOException e) {
			LOGGER.error("响应信息写入失败:", e); 
		}
		return null;
	}
	
	public String autoSendCallbak2TouTiao(){
		
		String json = "";
		try {
			// 更新用户信息；包括用户性别，年龄，生日
			new Thread(autoExcuteDeviceActiveCallbakThread).start();
			json = QwyUtil.getJSONString("ok", "启动成功");
		} catch (Exception e) {
			json = QwyUtil.getJSONString("error", "启动失败");
		}
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
		return null;
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


	public String getIdfa() {
		return idfa;
	}

	public void setIdfa(String idfa) {
		this.idfa = idfa;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getAndroidid() {
		return androidid;
	}

	public void setAndroidid(String androidid) {
		this.androidid = androidid;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getCallback_url() {
		return callback_url;
	}

	public void setCallback_url(String callback_url) {
		this.callback_url = callback_url;
	}

	public String getCallback() {
		return callback;
	}

	public void setCallback(String callback) {
		this.callback = callback;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	@Override
	public String toString() {
		return "ToutiaoDeviceUserEnableAction [adid=" + adid + ", cid=" + cid + ", idfa=" + idfa + ", imei=" + imei
				+ ", mac=" + mac + ", os=" + os + ", androidid=" + androidid + ", timestamp=" + timestamp
				+ ", callback_url=" + callback_url + ", callback=" + callback + ", sign=" + sign + "]";
	}
}
