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

import org.apache.log4j.Logger;

import com.huoq.common.Commons;
import com.huoq.newbranch.constants.Constants;

import net.sf.json.JSONObject;

/**
 * 短信工具类
 * @author 覃文勇
 *2015年5月12日 10:22:45
 */
public class SMSUtil {
	private static Logger log = Logger.getLogger(SMSUtil.class); 
	
	private static ResourceBundle resb = ResourceBundle.getBundle("app");
   /**
    * 发送验证码
    * @param mobile 手机号码
    * @param code 验证码
    * @param smsContent 短信内容
    * @return
    */
	public static Map<String, Object> sendYzm(String mobile, String code, String smsContent){
		try {
			Map<String, Object> values=new HashMap<String, Object>();
			values.put("apikey",resb.getString("APIKEY"));// SMSProperties.APIKEY
			values.put("mobile", mobile);
			values.put("text", smsContent);
			return QwyUtil.accessIntentByPost(resb.getString("YP_URL"), values);//SMSProperties.YP_URL
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return Commons.mapException();
	}
	/**
	    * 发送验证码
	    * @param mobile 手机号码
	    * @param code 验证码
	    * @param smsContent 短信内容
	    * @return
	    */
		public static Map<String, Object> sendYzm2(String mobile, String code, String smsContent){
			try {
				Map<String, Object> values=new HashMap<String, Object>();
				values.put("cdkey",resb.getString("YM_CDKEY") );//SMSProperties.YM_CDKEY
				values.put("password",resb.getString("YM_PASSWORD") );//SMSProperties.YM_PASSWORD
				values.put("phone", mobile);
				values.put("message", smsContent);
				values.put("seqid", 10000L);
				values.put("smspriority", 1);
				return QwyUtil.returnXMLAccessIntentByPost(resb.getString("YM_URL"), values);// SMSProperties.YM_URL
			} catch (Exception e) {
				log.error("操作异常: ",e);
			}
			return Commons.mapException();
		}
			/**
			 * 发放利息当天发送定时短信
			 * @param mobile
			 * @param sendtime
			 * @return
			 */
			public static Map<String, Object> sendProfitMessage(String mobile, String sendtime, String message){
				try {
					Map<String, Object> values=new HashMap<String, Object>();
					values.put("cdkey",resb.getString("YM_CDKEY") );//SMSProperties.YM_CDKEY
					values.put("password", resb.getString("YM_PASSWORD"));// SMSProperties.YM_PASSWORD
					values.put("phone", mobile);
					values.put("message", message);
					values.put("sendtime", sendtime);
					values.put("seqid", 10000L);
					values.put("smspriority", 1);
					return QwyUtil.returnXMLAccessIntentByPost(resb.getString("YM_TIME_URL"), values);//SMSProperties.YM_TIME_URL
				} catch (Exception e) {
					log.error("操作异常: ",e);
				}
				return Commons.mapException();
			}
		
		
	
	/**
	 * 解绑银行卡成功短信内容
	 * @param tel 客户电话
	 */
	public static String unbindSuc(String smsQm,String tel){
		return String.format(Constants.SMS_UNBINDSUC, tel,smsQm);//smsQm+"解绑银行卡成功，感谢您使用国内领先的金融理财服务！"+tel;
	}
	
	/**
	 * 短信内容	
	 * @param code 验证码
	 * @param company 公司名
	 * @param type 发送类型;
	 * @param wh 尾号
	 * @return
	 */
	public static  String smsContent(String smsQm,String code,String type, String wh){
		
		if(type.equals("1")){
			return  String.format(Constants.SMS_REGSUC,code,smsQm);			//smsQm+"您注册帐号的验证码是"+code+"(验证码30分钟内有效)";
		}else if(type.equals("2")){
			return String.format(Constants.SMS_FINDLOGINPW,code,smsQm);		// smsQm+"您找回登录密码的验证码是"+code+"(验证码30分钟内有效)";
		} else if(type.equals("3")){
			return  String.format(Constants.SMS_CHANGETRADEPW,code,smsQm);	 //smsQm+"您设置修改交易密码的验证码是"+code+"(验证码30分钟内有效)";
		}else if(type.equals("4")){
			return String.format(Constants.SMS_UNBINDBANKCARD,wh,code,smsQm);// smsQm+"您正在进行解绑银行卡尾号为"+wh+"的操作，如非本人操作，请尽快修改账户密码。验证码:"+code+"(验证码30分钟有效)";
		}else if(type.equals("5")){
			return String.format(Constants.SMS_CHANGEPHONE,code,smsQm);		 // smsQm+"您正在更换绑定的手机号码,验证码:"+code+"(验证码30分钟有效)";
		}else{
			return "";
		}
	}
	
	
	
	
//	   /**
//	    * 查询亿美余额
//	    * @return
//	    */
//		public static Map<String, Object> queryBalance(){
//			try {
//				Map<String, Object> values=new HashMap<String, Object>();
//				values.put("cdkey",resb.getString("YM_CDKEY") );//SMSProperties.YM_CDKEY
//				values.put("password",resb.getString("YM_PASSWORD") );//SMSProperties.YM_PASSWORD
//				return QwyUtil.returnXMLAccessIntentByPost(resb.getString("YM_QUERY_URL"), values);//SMSProperties.YM_QUERY_URL
//			} catch (Exception e) {
//				log.error("操作异常: ",e);
//			}
//			return Commons.mapException();
//		}
		/**
		 * 查询畅卓余额
		 * @return
		 */
		public static Map<String, Object> queryBalance(){
			try {
				Map<String, Object> values=new HashMap<String, Object>();
				values.put("account",resb.getString("CZ_CDKEY") );//SMSProperties.YM_CDKEY
				values.put("password",resb.getString("CZ_MD5_PWD") );//SMSProperties.YM_PASSWORD
				return QwyUtil.returnCzXMLAccessIntentByPost(resb.getString("CZ_QUERY_URL"), values);//SMSProperties.YM_QUERY_URL
			} catch (Exception e) {
				log.error("操作异常: ",e);
			}
			return Commons.mapException();
		}
		
		public static void main(String[] args) {
			//Map<String, Object> rs = sendYzmCZ("17730070607","9999",smsContent("【新华金典理财】","9999","1",null));
			//Map<String, Object> rs = sendYzmCZ_SALE("17730070607","营销短信自定义，不用模板【新华金典理财】");
//			Map<String, Object> rs = sendTongZhi("17730070607","缺少（%s）理财产品，请尽快补全。%s【新华金典理财】");
			Map<String, Object> rs = queryBalance();
			
			log.info(rs.get("message").toString());
			log.info(rs.get("error").toString());
			log.info(rs.get("result").toString());
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
		public static Map<String, Object> sendYzmCZ(String mobile, String code, String smsContent) {
			try {
				StringBuffer buff = new StringBuffer();
				buff.append("&account=" + resb.getString("CZ_ACCOUNT"));// SMSProperties.CZ_ACCOUNT
				buff.append("&password=" + resb.getString("CZ_YZPWD"));//已将原密码用md5 32位加密; + MD5Util.computeMD5(SMSProperties.CZ_PASSWORD) a980ffabc17a5ef9100866f40b3cace6
				buff.append("&mobile=" + mobile);
				buff.append("&content=" + java.net.URLEncoder.encode(smsContent, "utf-8"));
				buff.append("&sendTime=");
				String result = SMS(buff.toString(),resb.getString("CZ_URL"));//SMSProperties.CZ_URL
				return jieXiCZSMS(result);
			} catch (Exception e) {
				log.error("操作异常: ",e);
			}
			return Commons.mapException();
		}
		
		/**
		 * 畅卓科技发送验证码————营销短信
		 * 
		 * @param mobile
		 *            手机号码
		 * @param code
		 *            验证码
		 * @param smsContent
		 *            短信内容
		 * @return
		 */
		public static Map<String, Object> sendYzmCZ_SALE(String mobile, String smsContent) {
			try {
				StringBuffer buff = new StringBuffer();
				buff.append("&account=" + resb.getString("CZ_ACCOUNT_SALE"));//SMSProperties.CZ_ACCOUNT_SALE
				buff.append("&password="+ resb.getString("CZ_YXPWD_SALE"));//已将原密码用md5 32位加密; + MD5Util.computeMD5(SMSProperties.CZ_PASSWORD)34d0a94beadd78ddc12243de3c225ba2
				buff.append("&mobile=" + mobile);
				buff.append("&sendTime=");
				buff.append("&content=" + java.net.URLEncoder.encode(smsContent, "utf-8"));
				String result = SMS(buff.toString(),resb.getString("CZ_URL"));//SMSProperties.CZ_URL
				return jieXiCZSMS(result);
			} catch (Exception e) {
				log.error("操作异常: ",e);
			}
			return Commons.mapException();
		}

		/**
		 * 畅卓科技发送【通知】类短信
		 * 
		 * @param mobile
		 *            手机号码
		 * @param smsContent
		 *            短信内容
		 * @return
		 */
		public static Map<String, Object> sendTongZhi(String mobile, String smsContent) {
			try {
				StringBuffer buff = new StringBuffer();
				buff.append("&account=" +resb.getString("CZ_ACCOUNT_TZ"));//SMSProperties.CZ_ACCOUNT_TZ
				buff.append("&password="+resb.getString("CZ_PWD_TZ"));//已将原密码用md5 32位加密; + MD5Util.computeMD5(SMSProperties.CZ_PASSWORD)d9ebc9b997c511877fb507042d52b32c
				buff.append("&mobile=" + mobile);
				buff.append("&sendTime=");
				buff.append("&content=" + java.net.URLEncoder.encode(smsContent, "utf-8"));
				String result = SMS(buff.toString(),resb.getString("CZ_URL"));//SMSProperties.CZ_URL
				return jieXiCZSMS(result);
			} catch (Exception e) {
				log.error("操作异常: ",e);
			}
			return Commons.mapException();
		}
		
		
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
				log.error("操作异常: ",e);
			}
			return "";
		}
		
		

		/**解析畅卓短信返回的结果;
		 * @param jsonResult json格式;
		 * @return
		 */
		public static Map<String, Object> jieXiCZSMS(String jsonResult) {
			try {
				Map<String, Object> map = new HashMap<String, Object>();
				JSONObject json = JSONObject.fromObject(jsonResult);
				String returnstatus = json.getString("status");//0表示成功，其它为失败
				if ("0".equals(returnstatus)) {
					map.put("error", "0");
					map.put("message", "发送成功");
					map.put("result", json.getString("taskId"));
					return map;
				} else {
					String desc = json.getString("desc");//0表示成功，其它为失败
					map.put("error", "1");
					map.put("error", desc);
					map.put("result", json.getString("taskId"));
					return map;
				}
			}catch (Exception e) {
				log.error("操作异常: ",e);
			}
			return Commons.mapException();
		}
	
		
		
}
