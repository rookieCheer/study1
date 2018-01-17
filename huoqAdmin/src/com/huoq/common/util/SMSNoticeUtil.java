package com.huoq.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import com.huoq.common.Commons;
import com.huoq.common.bean.SystemConfigBean;

import net.sf.json.JSONObject;
import org.apache.poi.util.SystemOutLogger;
import sun.net.www.http.HttpClient;

/**
 * 短信工具类
 * 
 * @author tanghz 2017年10月15日 16:08:45
 */
public class SMSNoticeUtil {
	
	private static Logger log = Logger.getLogger(SMSNoticeUtil.class);
	private static ResourceBundle resb = ResourceBundle.getBundle("app");
	private static ResourceBundle resbsms = ResourceBundle.getBundle("sms-notice");
	/**
	 * 短信内容
	 * 
	 * @param smsContent
	 *            内容
	 * @param company
	 *            公司名
	 * @param type
	 *            发送类型; 1:注册; 2:找回登录密码; 3:找回交易密码;4 解绑银行卡;5:更换手机绑定;6:企业登录
	 * @param wh
	 *            尾号
	 * @return
	 */
	public  static String smsContent(String smsContent,String shortUrl, Object[] obj,String smsQm) {
		log.info("短信签名: "+smsQm);
		Object[] ob = new Object[obj.length+2];
		for (int i=0;i<obj.length;i++){
			ob[i] = obj[i];
		}
		ob[ob.length-2] = shortUrl;
		ob[ob.length-1] = smsQm;
		return String.format(smsContent,ob);
	}

	/**
	 * 畅卓科技发送通知短信
	 * 
	 * @param mobile
	 *            手机号码
	 * @param smsContent
	 *            短信内容
	 * @return
	 */
	public static Map<String, Object> sendYzm(String mobile, String smsContent) {
		try {
			StringBuffer buff = new StringBuffer();
			buff.append("&account=" +resbsms.getString("CZ_ACCOUNT") );
			buff.append("&password="+resbsms.getString("CZ_MD532Pwd"));// 已将原密码用md5a980ffabc17a5ef9100866f40b3cace6
			buff.append("&mobile=" + mobile);
			buff.append("&sendTime=");
			buff.append("&content=" + java.net.URLEncoder.encode(smsContent, "utf-8"));
			log.info("发送的短信内容为："+buff.toString());
			String result = SMS(buff.toString(),resbsms.getString("CZ_URL"));
			return jieXiCZSMS(result);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return Commons.mapException();
	}

	/**
	 * 
	 * @param postData
	 * @param postUrl
	 * @return
	 */
	public static String SMS(String postData, String postUrl) {
		try {
			// 发送POST请求
			URL url = new URL(postUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setUseCaches(false);
			conn.setDoOutput(true);

			conn.setRequestProperty("Content-Length", "" + postData.length());
			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
			out.write(postData);
			out.flush();
			out.close();

			// 获取响应状态
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				log.info("connect failed!");
				return "";
			}
			// 获取响应内容体
			String line, result = "";
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
			while ((line = in.readLine()) != null) {
				result += line + "\n";
			}
			in.close();
			return result;
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return "";
	}

	/**
	 * 解析畅卓短信返回的结果;
	 * 
	 * @param jsonResult
	 *            json格式;
	 * @return
	 */
	public static Map<String, Object> jieXiCZSMS(String jsonResult) {
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			JSONObject json = JSONObject.fromObject(jsonResult);
			String returnstatus = json.getString("status");// 0表示成功，其它为失败
			log.info(returnstatus);
			if ("0".equals(returnstatus)) {
				map.put("error", "0");
				map.put("message", "发送成功");
				map.put("result", json.getString("taskId"));
				return map;
			} else {
				String desc = json.getString("desc");// 0表示成功，其它为失败
				map.put("error", "1");
				map.put("error", desc);
				map.put("result", json.getString("taskId"));
				return map;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return Commons.mapException();
	}
	
	/**
	 * 畅卓科技发送验证码
	 * 
	 * @param mobile
	 *            手机号码
	 * @param code
	 *            验证码
	 * @param smsContent
	 *            短信内容
	 * @return
	 */
	public static Map<String, Object> sendNoticeCZ(String mobile, String code, String smsContent) {
		try {
			StringBuffer buff = new StringBuffer();
			buff.append("&account=" +resbsms.getString("CZ_NOTICE_ACCOUNT") );
			buff.append("&password="+resbsms.getString("CZ_NOTICE_MD532Pwd"));// 已将原密码用md5a980ffabc17a5ef9100866f40b3cace6
			buff.append("&mobile=" + mobile);
			buff.append("&sendTime=");
			buff.append("&content=" + java.net.URLEncoder.encode(smsContent, "utf-8"));
			log.info("发送的短信内容为："+buff.toString());
			String result = SMS(buff.toString(),resbsms.getString("CZ_NOTICE_URL"));
			return jieXiCZSMS(result);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return Commons.mapException();
	}

}
