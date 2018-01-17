package com.huoq.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ResourceBundle;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import net.sf.json.JSONObject; 
/**
 * 
 * @author tanghz 
 * @date  2017-10-15 20:51:45
 * 
 */
public class NoticeUrlUtil {
	private static Logger log = Logger.getLogger(NoticeUrlUtil.class);
	
	private static ResourceBundle resb = ResourceBundle.getBundle("sms-notice");
	/**
	 * 根据传入的地址获取短链接地址
	 * @param url
	 * @return
	 */
    public static String getShortUrl(String param){  
     	//接口地址
    		String path = resb.getString("origalAddressUrl");
    		
    		try {
    			//传入参数
    			param = URLEncoder .encode(param,"UTF-8");
    			path = String.format(path, param);
    			// 1.创建客户端访问服务器的httpclient对象 
    			HttpClient httpclient = new DefaultHttpClient();
    			// 2.以请求的连接地址创建post请求对象
    			HttpGet httpGet = new HttpGet(path);
    			// 3.向服务器端发送请求 并且获取响应对象车
    			HttpResponse response = httpclient.execute(httpGet);
    			// 4.获取响应对象中的响应码
    			StatusLine statusLine = response.getStatusLine();// 获取请求对象中的响应行对象
    			int responseCode = statusLine.getStatusCode();// 从状态行中获取状态码
    			if (responseCode == 200) {
    				// 5.获取HttpEntity消息载体对象 可以接收和发送消息
    				HttpEntity entity = response.getEntity();
    				// EntityUtils中的toString()方法转换服务器的响应数据
    				String str = EntityUtils.toString(entity, "utf-8");
    				log.info("服务器的响应是:" + str);

    				// //6.从消息载体对象中获取操作的读取流对象
    				InputStream input = entity.getContent();
    				BufferedReader br = new BufferedReader(new InputStreamReader(input));
    				// String str=br.readLine();
    				String result = new String(str.getBytes("gbk"), "utf-8");
    				log.info("服务器的响应是:" + result);
    				br.close();
    				input.close();
    				JSONObject jsonObj = JSONObject.fromObject(str);
    				String finalResult = "";
    				if (jsonObj != null ||!jsonObj.isNullObject()){
    					if ("true".equals(jsonObj.getString("success")))
    					{
    						finalResult = jsonObj.getJSONObject("result").getString("data");
    					}
    					else
    					{
    						finalResult = "获取短链接地址失败！";
    					}
    				}
    				return finalResult;
    			} else {
    				log.error("响应失败!");
    			}
    		} catch (ClientProtocolException e) {
    			// TODO Auto-generated catch block
    			log.error("操作异常!");
    		} catch (UnsupportedEncodingException encodingExceptione) {
    			// TODO Auto-generated catch block
    			log.error("操作异常!");
    		}catch (IOException e) {
    			// TODO Auto-generated catch block
    			log.error("操作异常!");
    		}
    		return null;
	}
    public static void main(String[] args) {
    	String str = getShortUrl("xhjd://app/open?action=productDetail");
    	System.out.println(str);
	}
    }  
