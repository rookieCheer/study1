/**
 * 
 */
package com.huoq.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.huoq.common.yeepay.HttpUtils;
import com.huoq.common.yeepay.HttpUtilsAlibaba;
import com.huoq.login.bean.RegisterUserBean;

import net.sf.json.JSONObject;

/**
 * 获取手机号码归属地
 * @author 薛瑞
 * 2016-10-28 09:28:53
 */
public class MobileLocationUtil {
	private static Logger log = Logger.getLogger(RegisterUserBean.class);
	public static Map<String,Object> getMobileLocation(String mobile){
		Map<String,Object> map=new HashMap<String, Object>();
		try {
			if(!QwyUtil.isNullAndEmpty(mobile)){
				//String url="http://www.ip138.com:8080/search.asp?action=mobile&mobile="+mobile;
				String url="http://sj.apidata.cn/?mobile="+mobile;
				Document doc = Jsoup.connect(url).ignoreContentType(true).userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.15)").timeout(5000).get();
		        Elements el=doc.getElementsByTag("body");
		        
		        JSONObject js = JSONObject.fromObject(el.get(0).text());
		        if(js.get("message").equals("success")){
		        JSONObject js2 = JSONObject.fromObject(js.get("data"));
		       
		        String type=(String) js2.get("isp");
				String cardType="";
				if(type.contains("移动"))  
					cardType="移动";
				else if(type.contains("联通"))
					cardType="联通";
				else if(type.contains("电信"))
					cardType="电信";
				else
					cardType="未知";
				map.put("cardType",cardType);
				map.put("city",js2.get("city"));
				map.put("province",js2.get("province"));
		        
		        }else{
		        	map.put("province","未知");
					map.put("city","未知");
					map.put("cardType","未知");
		        }
				/**
				Document document=Jsoup.connect(url).get();
				Elements elements=document.getElementsByClass("T");
				String location=elements.get(1).html() ;
				location = location.replaceAll("&nbsp;", "@");
				if(!location.contains("手机号有误")){
					String [] str=location.split("@");
					Document doc = Jsoup.parseBodyFragment(str[0]);
					Elements el=doc.getElementsByTag("body");
					if(str.length>1){
						map.put("province",el.get(0).text());
						map.put("city",str[1]);
					}else{
						map.put("province",el.get(0).text());
						map.put("city",el.get(0).text());
					}
					String type=elements.get(2).text();
					String cardType="";
					if(type.contains("移动"))  
						cardType="移动";
					else if(type.contains("联通"))
						cardType="联通";
					else if(type.contains("电信"))
						cardType="电信";
					else
						cardType="未知";
					map.put("cardType",cardType);
				}else{
					map.put("province","未知");
					map.put("city","未知");
					map.put("cardType","未知");
				}**/
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		log.info(map.toString());
		return map;
	}
	
	/**
	 * 获取手机号码归属地(阿里)
	 * @param mobile
	 * @return
	 */
	public static Map<String,Object> getMobileLocationAlibaba(String mobile){
		Map<String,Object> map=new HashMap<String, Object>();
		String host = "http://jisushouji.market.alicloudapi.com";
	    String path = "/shouji/query";
	    String method = "GET";
	    String appcode = "eaa7866573114e0b8ef3c0ba78a7f49b";
	    Map<String, String> headers = new HashMap<String, String>();
	    //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
	    headers.put("Authorization", "APPCODE " + appcode);
	    Map<String, String> querys = new HashMap<String, String>();
	    querys.put("shouji", mobile);
	    try {
	    	/**
	    	* 重要提示如下:
	    	* HttpUtils请从
	    	* https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
	    	* 下载
	    	*
	    	* 相应的依赖请参照
	    	* https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
	    	*/
	    	HttpResponse response = HttpUtilsAlibaba.doGet(host, path, method, headers, querys);
	    	String string = EntityUtils.toString(response.getEntity());
	    	if (!QwyUtil.isNullAndEmpty(string)) {
	    		JSONObject json = JSONObject.fromObject(string);
	    		JSONObject result = JSONObject.fromObject(json.get("result").toString());
		    	map.put("shouji", result.get("shouji"));
		    	map.put("province", result.get("province"));
		    	map.put("city", result.get("city"));
		    	map.put("company", result.get("company"));
		    	String cardType="";
		    	if(result.get("company").toString().contains("移动"))  
					cardType="移动";
				else if(result.get("company").toString().contains("联通"))
					cardType="联通";
				else if(result.get("company").toString().contains("电信"))
					cardType="电信";
				else
					cardType="未知";
		    	map.put("cardType", cardType);
		    	map.put("areacode", result.get("areacode"));
			}else {
				map.put("province","未知");
				map.put("city","未知");
				map.put("cardtype","未知");
			}
	    	
	    	//获取response的body
	    	//System.out.println(EntityUtils.toString(response.getEntity()));
	    	return map;
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
		return null;
	}

}