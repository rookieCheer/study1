package com.huoq.payUtils.payUtilImpl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpResponse;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.common.util.QwyUtil;
import com.huoq.payUtils.JXMConvertUtil;
import com.huoq.payUtils.PayUtil;
import com.huoq.payUtils.SecurityUtil;
import com.huoq.payUtils.httpUtil.HttpRequest;
import com.huoq.payUtils.httpUtil.HttpRespons;
import com.huoq.payUtils.httpUtil.Md5Algorithm;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
/**
 * Description:环迅交互接口
 * @author  changhaipeng
 * @date 2017年6月3日09:08:09
 */
@Service
public class PayUtilImpl implements PayUtil{
	private static Logger log = Logger.getLogger(PayUtilImpl.class);
	private HttpRequest req = new HttpRequest();
	
	//签名MD5(operationType +merchantID +request+系统证书(MD5))	取 32 位小写 “+”此处仅表示字符串拼接
	private String sign;
	//请求信息
	private String request;
	//读取文件
	private static ResourceBundle resb = ResourceBundle.getBundle("app");
	//商户存管交易账号
	private String merchantID = resb.getString("merchantID");
	//请求的路径https://UFunds.ips.com.cn/p2p-deposit/gateway.htm
	private String urlString = resb.getString("urlString");
	//http请求头属性的信息
	private static Map<String, String> headProMap = new HashMap<String,String>();
	//请求参数map
	private Map<String, String> paramsMap = new HashMap<String,String>();
	//系统证书,配置文件中就是Md5还是原始的，待确认  TODO
	private String sysCertificate = resb.getString("sysCertificate");
	
	
	static{
		//TODO 是否有要求待确认  application/x-www-form-urlencoded
		headProMap.put("Content-Type", "multipart/form-data");
	}
	
	/**
	 * Description:环迅充值用
	 * @author  changhaipeng
	 * @date 下午4:19:05
	 *  data包含：充值银行号（bankCode）、充值金额（trdAmt）、充值日期（merDate  yyyy-MM-DD）、商户订单号（merBillNo）
	 */
	@Override
	public Map<String, Object> recharge(Map<String, Object> data) {
		//TODO 确认参数的
//		data.put("webUrl", "");				//页面返回地址
//		data.put("s2SUrl","");				//后台通知地址
//		data.put("taker", "");				//发起方：1、商户发起，2、用户发起（商户发起时充值类型只能为还款充值）
//		data.put("merFeeType", "");			//平台手续费收取方式：1、内扣，2、外扣
//		data.put("merFee", "");				//平台收取用户的手续费
//		data.put("ipsFeeType", "");			//ips 手续费承担方:1、平台商户 2、平台用户
//		data.put("ipsAcctNo", "");			//IPs 虚拟账户号
//		data.put("channelType", "");		//充值渠道 1、个人网银 2、企业网银（用户发起且为普通充值时必填）
//		data.put("depositType", "");		//充值类型：1、普通充值 2、还款充值
//		data.put("userType", "");			//用户类型 1、个人 2、企业
		return postReq("trade.deposit",data);
	}
	
	/**
	 * Description:环迅提现
	 * @author  changhaipeng
	 * @date 下午4:19:05
	 * 请求格式：operationType=user.addUser&merchantID=1001251&sign=18989798668abcdeefeewe&
	 *  request={“pMerBillNo”:”10025#abc.com”,“ pAcctType”:”45fefefewewfwefwfwfe”,“pIdentNo”:”1.0.0”,“ pRealName”:”88:f3:cb:g8:0r:5d}
	 *	data:商户订单号(merBillNo)、日期（merDate  yyyy-MM-DD）、提现金额（trdAmt）	
	 */
	@Override
	public Map<String, Object> withdrawals(Map<String, Object> data) {
		//TODO 确认参数的
//		data.put("webUrl", "");				//页面返回地址
//		data.put("s2SUrl","");				//后台通知地址
//		data.put("merFeeType", "");			//平台手续费收取方式：1、内扣，2、外扣
//		data.put("merFee", "");				//平台收取用户的手续费
//		data.put("ipsFeeType", "");			//ips 手续费承担方:1、平台商户 2、平台用户    平台手续内外扣类型 内扣 1、外扣 2
//		data.put("ipsAcctNo", "");			//IPs 虚拟账户号
//		data.put("userType", "");			//用户类型 1、个人 2、企业
		return postReq("trade.withdraw",data);
	}
	
	/**
	 * Description:支付提现请求参数拼接
	 * @author  changhaipeng
	 * @date 下午5:48:00
	 * operationType: 操作类型，充值为：trade.deposit ，提现：trade.withdraw
	 * operationType=user.addUser&merchantID=1001251&sign=18989798668abcdeefeewe&
	 * request={“pMerBillNo”:”10025#abc.com”,“ pAcctType”:”45fefefewewfwefwfwfe”,“pIdentNo”:”1.0.0”,“ pRealName”:”88:f3:cb:g8:0r:5d}
	 */
	public Map<String, Object> postReq(String operationType,Map<String, Object> data){
		Map<String, Object> okmap = new HashMap<String, Object>();
		if("test".equals(resb.getString("env"))){
			okmap.put("end", "success");
		    okmap.put("response", null);
		    okmap.put("message", "请求成功！");
			return okmap;
		}
		String opCh = "";
		if("trade.deposit".equals(operationType)){
			opCh = "充值";
		}else if("trade.withdraw".equals(operationType)){
			opCh = "提现";
		}else if("query.bankQuery".equals(operationType)){
			opCh = "商户直连银行查询接口";
		}else if("user.register".equals(operationType)){
			opCh = "用户开户接口";
		}else if("query.commonQuery".equals(operationType)){
			opCh = "查询接口";
		}else{
			opCh = "不明的操作类型！";
			okmap.put("end", "error");
			okmap.put("response", null);
			okmap.put("message",opCh);
			return okmap;
		}
		//操作类型
		//验证信息
		if(!"query.bankQuery".equals(operationType)&&(data==null||data.size()<1)){
			log.error(opCh+"信息为空！");
			okmap.put("end", "error");
			okmap.put("response", null);
			okmap.put("message", opCh+"信息为空!");
			return okmap;
		}
		
		try {
			if("query.bankQuery".equals(operationType)){
				request = "";
			}else{
				//商户存管交易账号
				JSONObject jsonObjectFromMap = JSONObject.fromObject(data);
				//请求信息3des加密，base64
				request = SecurityUtil.Base64Encode(jsonObjectFromMap.toString());
			}	
			//验证系统证书
			if(QwyUtil.isNullAndEmpty(sysCertificate)){
				log.error("系统证书不存在！");
				okmap.put("end", "error");
				okmap.put("response", null);
				okmap.put("message", "系统证书不存在！");
				return okmap;
			}
			//TODO sign中request是加密前还是加密后的字符串 Md5Algorithm.getInstance().md5Digest(sysCertificate.getBytes("utf-8")
			sign = operationType + merchantID + request + sysCertificate;
			//签名,md5 32位小写
			sign = Md5Algorithm.getInstance().md5Digest(sign.getBytes("utf-8"));
			
			paramsMap.put("operationType",operationType);
			paramsMap.put("merchantID",merchantID);
			paramsMap.put("sign",sign);
			paramsMap.put("request",request);
			log.info("环迅请求地址："+urlString+",报文："+paramsMap);
			//发送请求
		    HttpRespons response = req.sendPost(urlString, paramsMap, headProMap);
		   
		    //如果是同步响应，就将response塞入okmap中
		    okmap.put("end", "success");
		    okmap.put("response", response);
		    okmap.put("message", "请求成功！");
			return okmap;
		} catch (UnsupportedEncodingException e) {
			log.error("请求环迅"+opCh+"失败！"+e);
			okmap.put("end", "error");
		    okmap.put("response", null);
		    okmap.put("message", "请求环迅"+opCh+"失败！"+e);
			return okmap;
		} catch (IOException e) {
			log.error("请求环迅"+opCh+"失败！"+e);
			okmap.put("end", "error");
		    okmap.put("response", null);
		    okmap.put("message", "请求环迅"+opCh+"失败！"+e);
			return okmap;
		} finally{
			paramsMap.clear();
		}
	}
	
	/***
	 * 验证签名的正确性
	 * request,三个参数+系统证书顺序加密
	 */
	@Override
	public boolean checkSign(String obj1, String obj2, String obj3,String checkSign) {
		if(QwyUtil.isNullAndEmpty(obj1)||QwyUtil.isNullAndEmpty(obj2)||QwyUtil.isNullAndEmpty(obj3)||QwyUtil.isNullAndEmpty(checkSign)){
			log.error("参数不完整，无法验证！参数1："+obj1+",参数2："+obj2+",参数3："+obj3+",签名："+checkSign);
			return false;
		}
		String sign;
		try {
			sign = Md5Algorithm.getInstance().md5Digest((obj1+""+obj2+""+obj3+sysCertificate).getBytes("utf-8"));
			if(sign!=null&&sign.equals(checkSign)){
				return true;
			}
		} catch (UnsupportedEncodingException e) {
			log.error("操作异常: ",e);
		}
		return false;
	}
	/***
	 * 验证签名的正确性
	 * request,4个参数+系统证书顺序加密
	 */
	@Override
	public boolean checkSign(String obj1, String obj2, String obj3,String obj4,String checkSign) {
		if(QwyUtil.isNullAndEmpty(obj1)||QwyUtil.isNullAndEmpty(obj2)||QwyUtil.isNullAndEmpty(obj3)
				||QwyUtil.isNullAndEmpty(obj4)||QwyUtil.isNullAndEmpty(checkSign)){
			log.error("参数不完整，无法验证！参数1："+obj1+",参数2："+obj2+",参数3："+obj3+",参数4："+obj4+",签名："+checkSign);
			return false;
		}
		String sign;
		try {
			sign = Md5Algorithm.getInstance().md5Digest((obj1+""+obj2+""+obj3+obj4+sysCertificate).getBytes("utf-8"));
			if(sign!=null&&sign.equals(checkSign)){
				return true;
			}
		} catch (UnsupportedEncodingException e) {
			log.error("操作异常: ",e);
		}
		return false;
	}

	/***
	 * 获得商户订单号 商户订单号不能重复，前 6 位以 商 户 号 起 始
	 * 2017年6月3日10:31:43
	 */
	@Override
	public String getMerBillNo() {
		return merchantID+""+ UUID.randomUUID().toString();
	}



	
	/***
	 * 获得支持的银行列表
	 * 2017年6月5日13:37:00
	 */
	@Override
	public Map<String, Object> getBankList() {
	    Map<String, Object> tempMap =  postReq("query.bankQuery",null);
	    try {
		    if("success".equals(tempMap.get("end"))){
		    	HttpRespons resp = (HttpRespons) tempMap.get("response");
		    	JSONObject jsonObj =  JSONObject.fromObject(resp.getContent());
		    	String resultCode = jsonObj.getString("resultCode");
		    	String resultMsg =  jsonObj.getString("resultMsg");
		    	String merchantId = jsonObj.getString("merchantId");
		    	String sign = jsonObj.getString("sign");
		    	String responseContent = jsonObj.getString("response");
		    	boolean checkSign = checkSign(merchantId, resultCode, resultMsg,responseContent , sign);
		    	
		    	//验证签名和返回的状态码
		    	if(checkSign&&"000000".equals(resultCode)){
					responseContent =  SecurityUtil.Base64Decode(responseContent);
					JSONObject tempJsonObj =  JSONObject.fromObject(responseContent);
					Map<String, Object> arrayData = JXMConvertUtil.JsonConvertHashMap(tempJsonObj.getString("banks"));
					tempMap.clear();
					tempMap.put("end", "success");
					tempMap.put("resultMsg", resultMsg);
					tempMap.put("banks",arrayData);
					return tempMap;
		    	}else{
		    		tempMap.put("end", "error");
		    		tempMap.put("resultMsg", resultMsg);
					tempMap.put("banks",null);
					return tempMap;
		    	}
		    }else {
		    	tempMap.put("end", "error");
	    		tempMap.put("resultMsg", "发送请求失败！"+tempMap.get("message"));
				tempMap.put("banks",null);
				return tempMap;
		    }
	    } catch (UnsupportedEncodingException e) {
	    	log.error("操作异常: ",e);
	    } catch (IOException e) {
	    	log.error("操作异常: ",e);
	    }
		return tempMap;
	}
	
	/**
	 * Description:验证respon的数据完整性。验证response是否为空，签名是否正确
	 * return 数据Map
	 * @author  changhaipeng
	 * 2017年6月5日13:55:23
	 */
	public Map<String, Object> checkResponse(HttpServletRequest request){
		Map<String, Object> okmap = new HashMap<String, Object>();
		log.info("=====进入环迅购买理财产品在线支付回调接口=====并完成实名认证");
		String resultCode = request.getParameter("resultCode");	
		String resultMsg =  request.getParameter("resultMsg");
		String merchantID = request.getParameter("merchantID");	
		String sign = request.getParameter("sign");	
		String responseContent = request.getParameter("response");	
		
		if (responseContent.isEmpty()) {// 判断参数是否为空
			log.error("=====返回数据为空");
			okmap.put("end", "error");
			okmap.put("message", "没有返回数据！responseContent"+responseContent);
			return okmap;
		}
		log.info("=====返回数据:" + responseContent);
		
		//验证签名
		if(!checkSign(merchantID, resultCode, resultMsg, responseContent, sign)){
			log.error("签名验证未通过！");
			okmap.put("end", "error");
			okmap.put("message", "签名验证未通过！");
			return null;
		}
		
		try {
			responseContent = SecurityUtil.Base64Decode(responseContent);
		} catch (IOException e) {
			log.error("操作异常: ",e);
		}
		log.info("=====返回数据解密结果:" + responseContent);
		return JXMConvertUtil.JsonConvertHashMap(responseContent);// 将JSON转化为Map对象。
	}

	
	/**
	 * Description: 用户开户，异步执行，在回调中解析数据
	 * @author  changhaipeng
	 * @date 2017年6月5日 下午4:44:39
	 */
	public Map<String, Object> register(Map<String, Object> data) {
		return postReq("user.register",data);
	}

	/**
	 * Description:验证交易记录,使用
	 * @author  changhaipeng
	 * @date 2017年6月6日 下午3:22:46
	 */
	@Override
	public Map<String, Object> checkTraRec(Map<String, Object> data) {
		Map<String, Object> tempMap = postReq("query.commonQuery",data);
		  try {
			    if("success".equals(tempMap.get("end"))){
			    	HttpRespons resp = (HttpRespons) tempMap.get("response");
			    	JSONObject jsonObj =  JSONObject.fromObject(resp.getContent());
			    	String resultCode = jsonObj.getString("resultCode");
			    	String resultMsg =  jsonObj.getString("resultMsg");
			    	String merchantId = jsonObj.getString("merchantId");
			    	String sign = jsonObj.getString("sign");
			    	String responseContent = jsonObj.getString("response");
			    	boolean checkSign = checkSign(merchantId, resultCode, resultMsg,responseContent , sign);
			    	
			    	//验证签名和返回的状态码
			    	if(checkSign&&"000000".equals(resultCode)){
						responseContent =  SecurityUtil.Base64Decode(responseContent);
						JSONObject tempJsonObj =  JSONObject.fromObject(responseContent);
						Map<String, Object> arrayData = JXMConvertUtil.JsonConvertHashMap(tempJsonObj.getString("banks"));
						tempMap.clear();
						tempMap.put("end", "success");
						tempMap.put("resultMsg", resultMsg);
						tempMap.put("resultCode", resultCode);
						tempMap.put("data",arrayData);
						return tempMap;
			    	}else{
			    		tempMap.put("end", "error");
			    		tempMap.put("resultMsg", resultMsg);
			    		tempMap.put("resultCode", resultCode);
						tempMap.put("data",null);
						return tempMap;
			    	}
			    }else {
			    	tempMap.put("end", "error");
		    		tempMap.put("resultMsg", "发送请求失败！"+tempMap.get("message"));
					tempMap.put("data",null);
					return tempMap;
			    }
		    } catch (UnsupportedEncodingException e) {
		    	log.error("操作异常: ",e);
		    } catch (IOException e) {
		    	log.error("操作异常: ",e);
		    }
			return tempMap;
	}
	
}
