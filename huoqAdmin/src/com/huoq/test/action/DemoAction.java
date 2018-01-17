package com.huoq.test.action;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;
import org.codehaus.groovy.transform.SynchronizedASTTransformation;

import yjpay.api.service.impl.PayAPIServiceImpl;

import com.alibaba.fastjson.JSON;
import com.opensymphony.xwork2.ActionSupport;
import com.util.common.RandomUtil;
import com.util.common.YJPayUtil;
import com.util.encrypt.AES;
import com.util.encrypt.EncryUtil;
import com.util.encrypt.RSA;
import com.huoq.account.bean.UserRechargeBean;
import com.huoq.common.dao.ObjectDAO;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.Product;
import com.huoq.orm.Users;
import com.huoq.product.bean.ProductCategoryBean;
import com.huoq.test.bean.DemoBean;

/**
 * @author qwy
 *
 * 2015-4-14下午3:12:15
 */
@ParentPackage("struts-default")
@Namespace("/Product")
@Results({ @Result(name = "newFile", value = "/NewFile1.jsp")
})
public class DemoAction extends ActionSupport{
	private ResourceBundle resb1 = ResourceBundle.getBundle("payapi");
	private static Logger log = Logger.getLogger(DemoAction.class);
	@Resource
	DemoBean bean;
	Card card;
	
	
	
	
	
	public String getTest(){
		return "newFile";
	}
	
	
	public String sendShouhu(){
		bean.sendShouhu();
		return null;
	}
	
	
	
	
	
	
	
	public String bindCard(){
		try {
			//商户私钥
			//String merchantPrivateKey ="";
			//易宝公钥
			//String yibaoPublicKey="";
			// 完整的请求地址
			//String requestURL = urlPrefix + PayAPIURIList.PAYAPI_BINDCARDPAY.getValue();
			//String requestURL = urlPrefix + "/api/tzt/invokebindbankcard";
			//String requestURL = "https://ok.yeepay.com/payapi/api/tzt/invokebindbankcard";
			// 生成RSA签名
			Map<String, String> map = new HashMap<String, String>();
			map.put("merchantaccount", card.getMerchantaccount());
			map.put("identityid", card.getIdentityid());
			map.put("identitytype", card.getIdentitytype());
			map.put("requestid", card.getRequestid());
			map.put("cardno", card.getCardno());
			map.put("idcardtype", card.getIdcardtype());
			map.put("idcardno", card.getIdcardno());
			map.put("username", card.getUsername());
			map.put("phone", card.getPhone());
			map.put("userip", card.getUserip());
			map.put("registerip", "192.168.0.100");
			map = TZTService.bindBankcard(map);
			YJPayUtil yjpayutil = new YJPayUtil();
			//yjpayutil.checkYbResult(map);
			/*String sign = EncryUtil.handleRSA(map, merchantPrivateKey);
			map.put("sign", sign);

			// 生成data
			String info = JSON.toJSONString(map);
			String data = AES.encryptToBase64(info, merchantAesKey);

			// 使用RSA算法将商户自己随机生成的AESkey加密
			String encryptkey = RSA.encrypt(merchantAesKey, yibaoPublicKey);

			YJPayUtil yjpayutil = new YJPayUtil();

			// 请求一键支付接口
			String ybresult = yjpayutil.payAPIRequest(requestURL, data, encryptkey,
					merchantaccount, true);

			// 将一键支付返回的结果进行验签，解密并返回
			log.info(yjpayutil.checkYbResult(ybresult));*/
			//log.info(map.toString());
		}catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return "newFile";
	}
	
	public String checkBankCard(){
		log.info("卡号: "+card.getCardno());
		try {
			PayAPIServiceImpl testPayAPIImpl = new PayAPIServiceImpl();

			String payresult = testPayAPIImpl.bankCardCheck(card.getCardno());

			log.info("接口返回结果：" + payresult);
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return null;
	}
	
	public String confirmBindBankcard(){
		Map<String, String> map = new HashMap<String, String>();
		map.put("merchantaccount", card.getMerchantaccount());
		map.put("requestid", card.getRequestid());
		map.put("validatecode", "132245");
		TZTService.confirmBindBankcard(map);
		return "newFile";
	}
	

	
	public static void main(String[] args) {
		/*DemoAction d = new DemoAction();
		d.queryCardList();*/
		//Map<String, String>  params = new HashMap<String,String>();TZTService.directBindPay(params);
		
		//TZTService.queryByOrder("599068779g");
		String imei = System.getProperty("phone.imei");
		log.info(imei);
	}
	public String getImei(){
		log.info("获取IMEI值");
		System.getProperty("phone.imei");
		return null;
	}

	public Card getCard() {
		return card;
	}

	public void setCard(Card card) {
		this.card = card;
	}

	
	public static TreeMap<String, Object> payParam(){
		TreeMap<String, Object> dataMap	= new TreeMap<String, Object>();
		dataMap.put("merchantaccount", 	"YB01000000144");
		dataMap.put("orderid", 			1002);
		dataMap.put("transtime", 		System.currentTimeMillis()/1000);
		dataMap.put("currency", 		156);
		dataMap.put("amount", 			10);
		dataMap.put("productname", 		"测试支付001");
		dataMap.put("identityid", 		1002);
		dataMap.put("identitytype", 	2);
		dataMap.put("card_top", 		621483);
		dataMap.put("card_last", 		5537);
		dataMap.put("orderexpdate", 	30);
		dataMap.put("callbackurl", 		"http://192.168.0.100:8080/wgtz/NewFile1.jsp");
		dataMap.put("userip", 			"192.168.0.100");
		dataMap.put("ua", 				"dd");
		return dataMap;
	}
	
	private String phone = ""; 
	
	/**导入手机号码;
	 * @return
	 */
	public String inputPhone(){
		String[] strArray=null;
		strArray=phone.split("\r\n");
		if(strArray.length>0){
	    for(int i=0; i<strArray.length; i++) {
	    	if(QwyUtil.verifyPhone(strArray[i])){
	    		Users users=bean.getUsersByUsername(strArray[i]);
	    		if(users==null){//如果为空
	    			log.info(strArray[i]+"用户不存在");
	    		}else{
	    			boolean isExists = bean.isExistsHDUsers(strArray[i]);
	    			if(!isExists){
	    				bean.createUsers(users);
	    			}else{
	    				log.info(strArray[i]+"已经发放过投资券");
	    			}
	    		}
	    	}
	    }
		}
		return "";
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
}
