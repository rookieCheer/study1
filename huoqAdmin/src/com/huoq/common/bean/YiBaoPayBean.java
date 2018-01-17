package com.huoq.common.bean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.UUID;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import yjpay.URIList.PayAPIURIList;
import yjpay.api.service.impl.PayAPIServiceImpl;

import com.alibaba.fastjson.JSON;
import com.util.common.RandomUtil;
import com.util.common.YJPayUtil;
import com.util.encrypt.AES;
import com.util.encrypt.EncryUtil;
import com.util.encrypt.RSA;
import com.huoq.common.dao.MyWalletDAO;
import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.Account;
import com.huoq.orm.TxRecord;

/**
 * 易宝支付第三方接口Bean层;
 * 
 * @author qwy
 * 
 * @createTime 2015-05-18 09:51:25
 */
@Service
public class YiBaoPayBean {
	private static Logger log = Logger.getLogger(YiBaoPayBean.class); 
	private  PayAPIServiceImpl payAPIService = new PayAPIServiceImpl();
	@Resource
	private  MyWalletDAO dao;
	private ResourceBundle resb1 = ResourceBundle.getBundle("payapi");
	
	/**
	 * 检查银行卡归属地;
	 * 
	 * @param cardno
	 *            银行卡号
	 * @return JOSN格式;
	 * @throws Exception
	 */
	public  String bankCardCheck(String cardno) throws Exception {
		log.info("检查银行卡归属地,卡号: "+cardno);
		String payresult = payAPIService.bankCardCheck(cardno);
		log.info("检查银行卡归属地,接口返回结果：" + payresult);
		return payresult;
	}

	/**
	 * 绑定卡请求;
	 * 
	 * @param usersId
	 *            用户ID;
	 * @param cardno
	 *            银行卡号
	 * @param requestId
	 *            绑卡请求id;随机生成的唯一;
	 * @param idcardno
	 *            身份证号;
	 * @param username
	 *            真实姓名;
	 * @param phone
	 *            银行预留手机号
	 * @param userip
	 *            用户绑卡时的ip
	 * @return JOSN格式;
	 * @throws Exception
	 */
	public String bindBankcard(long usersId, String cardno,
			String requestId, String idcardno, String username, String phone,
			String userip,String type) throws Exception {
		log.info("绑定卡请求,用户Id: "+usersId+";卡号: "+cardno+";请求ID: "+requestId+";userip: "+userip);
		String uid = usersId + "";
		Object obj =getAccountByIdcard(idcardno,type);
		if(!QwyUtil.isNullAndEmpty(obj)){
			return "{\"message\":\"该身份证号已被绑定\",\"end\":\"error\"}";
		}
		Object obj2 =getAccountByUsersId(usersId,type);
		if(!QwyUtil.isNullAndEmpty(obj2)){
			return "{\"message\":\"该用户已绑定银行卡\",\"end\":\"error\"}";
		}
		String payresult="";
		try {
			payresult = payAPIService.bindBankcard(uid, 2, requestId,
					cardno, "01", idcardno, username, phone, "", "", userip, "01",
					"", "", "", "", userip, "");
		} catch (Exception e) {
			log.error("操作异常: ",e);
			if(e.getMessage().indexOf("Read timed out")!=-1){
				payresult = "{\"message\":\"请求超时,请重新发送请求\",\"end\":\"error\"}";
			}
		}
		log.info("绑定卡请求,接口返回结果：" + payresult);
		return payresult;

	}

	/**
	 * 确认绑卡请求;
	 * 
	 * @param requestId
	 *            绑卡请求ID;
	 * @param validatecode
	 *            短信验证码
	 * @return JOSN格式;
	 * @throws Exception
	 */
	public String confirmBindBankCard(String requestId, String validatecode)
			throws Exception {
		log.info("确认绑卡请求,请求ID: " + requestId + ";短信验证码: " + validatecode);
		String payresult = payAPIService.confirmBindBankcard(requestId,
				validatecode);
		log.info("确认绑卡请求,接口返回结果：" + payresult);
		return payresult;
	}
	/**
	 * 调用支付API 支付接口
	 * 
	 * @param orderid
	 *            商户订单号 商户生成的唯一订单号，最长50位
	 * @param amount
	 *            交易金额 以"分"为单位的整型
	 * @param productname
	 *            商品名称 最长50位
	 * @param productdesc
	 *            商品描述 最长200位
	 * @param usersId
	 *            用户标识 最长50位，商户生成的用户唯一标识
	 * @param callbackurl
	 *            回调地址 用来通知商户支付结果
	 * @param userip
	 *            用户请求ip 用户支付时使用的网络终端IP
	 * @return JOSN格式;
	 * @throws Exception
	 */
	public String directBindPay(String orderid, int amount, String productname,
			String productdesc, long usersId, String callbackurl, String userip,String type)
			throws Exception {
		log.info("支付接口,订单号: " + orderid + ";用户id: " + usersId);
		Account account = getAccountByUsersId(usersId,type);
		if(QwyUtil.isNullAndEmpty(account)){
			log.info("支付接口,参数错误: {\"message\":\"该用户未绑定银行卡\",\"end\":\"error\"}");
			return "{\"message\":\"该用户未绑定银行卡\",\"end\":\"error\"}";
		}
		String payresult = payAPIService.directBindPay(orderid,
				(int) (System.currentTimeMillis() / 1000), 156, amount,
				productname, productdesc, usersId + "", 2,
				account.getCardTop(), account.getCardLast(), 30, callbackurl,
				"", userip, "");
		log.info("支付接口,接口返回结果：" + payresult);
		return payresult;
	}
	
	/**异步支付结果查询接口
	 * @param orderid 订单号;
	 * @return JOSN格式;
	 * @throws Exception
	 */
	public String queryPay(String orderid) throws Exception {
		log.info("异步支付结果查询接口,订单号: " + orderid);
		payAPIService = new PayAPIServiceImpl();
		if(QwyUtil.isNullAndEmpty(orderid)){
			return paramException();
		}
		String payresult = payAPIService.queryPay(orderid);
		log.info("异步支付结果查询接口,接口返回结果：" + payresult);
		return payresult;
		
	}
	
	/**用户提现;
	 * @param requestid 请求id
	 * @param usersId 用户id
	 * @param amount 提现金额(分)
	 * @param drawtype 提现类型;  0:T+0到账; 1:T+1到账;
	 * @param userip 用户支付时使用的网络终端IP
	 * @return JOSN格式;
	 * @throws Exception
	 */
	public String withdraw(String requestid, long usersId,
			int amount, String drawtype, String userip)throws Exception{
		log.info("用户提现,用户Id: "+usersId+";提现类型: "+drawtype+";请求ID: "+requestid);
		Account account = getAccountByUsersId(usersId,"0");
		if(QwyUtil.isNullAndEmpty(account)){
			log.info("用户提现,参数错误: {\"message\":\"该用户未绑定银行卡\",\"end\":\"error\"}");
			return "{\"message\":\"该用户未绑定银行卡\",\"end\":\"error\"}";
		}
		// URGENT（自然日t+0）;NORMAL(自然日t+1);
		if("0".equals(drawtype)){
			drawtype = "URGENT";
		}else if("1".equals(drawtype)){
			drawtype = "NORMAL";
		}else{
			drawtype = "NORMAL";
		}
		String payresult = payAPIService.withdraw(requestid, usersId+"", 2, account.getCardTop(), account.getCardLast(), 156, amount, drawtype, "", userip,"");
		
		log.info("用户提现,接口返回结果：" + payresult);
		return payresult;
	}
	/**查询提现记录接口;
	 * @param usersId 用户id
	 * @param requestId 请求ID
	 * @param yborderId 易宝交易流水号;
	 * @return
	 * @throws Exception
	 */
	public String withdrawQuery(String requestId) throws Exception{
		String myBankJson = payAPIService.drawrecord(requestId, null);
		JSONObject jb = JSONObject.fromObject(myBankJson);
		Object errorObj = jb.get("error_msg");
		if(!QwyUtil.isNullAndEmpty(errorObj)){
			return QwyUtil.getJSONString("error", errorObj.toString());
		}
		Object errorEnd = jb.get("end");
		if(!QwyUtil.isNullAndEmpty(errorEnd)){
			return QwyUtil.getJSONString("end", jb.getString("message"));
		}
		Object objStatus = jb.get("status");
		if(!QwyUtil.isNullAndEmpty(objStatus)){
			String status = objStatus.toString();
			if("DOING".equalsIgnoreCase(status)){
				//正在审核
				return QwyUtil.getJSONString("error", "正在处理中");
			}else if("FAILURE".equalsIgnoreCase(status)){
				//提现失败
				return QwyUtil.getJSONString("error", "提现失败");
			}else if("REFUND".equalsIgnoreCase(status)){
				//提现退回
				return QwyUtil.getJSONString("error", "提现退回");
			}else if("SUCCESS".equalsIgnoreCase(status)){
				//提现已到账
				return QwyUtil.getJSONString("ok", "提现成功");
			}else if("UNKNOW".equalsIgnoreCase(status)){
				//未知
				return QwyUtil.getJSONString("error", "提现异常");
			}
		}
		return QwyUtil.getJSONString("error", "提现异常");
		
	}
	
	/**查询提现记录接口;
	 * @param usersId 用户id
	 * @param requestId 请求ID
	 * @param yborderId 易宝交易流水号;
	 * @return
	 * @throws Exception
	 */
	public String withdrawQuery(long usersId,String requestId,String yborderId) throws Exception{
		try {
			TxRecord txRecord = getTxRecordByUsersId(usersId, requestId);
			if(QwyUtil.isNullAndEmpty(txRecord))
				return QwyUtil.getJSONString("error", "找不到提现记录");
			String myBankJson = payAPIService.drawrecord(requestId, yborderId);
			requestId = QwyUtil.isNullAndEmpty(requestId)?yborderId:requestId;
			JSONObject jb = JSONObject.fromObject(myBankJson);
			Object errorObj = jb.get("error_msg");
			if(!QwyUtil.isNullAndEmpty(errorObj)){
				txRecord.setStatus("2");
				txRecord.setNote(errorObj.toString());
				dao.saveOrUpdate(txRecord);
				return QwyUtil.getJSONString("error", errorObj.toString());
			}
			Object errorEnd = jb.get("end");
			if(!QwyUtil.isNullAndEmpty(errorEnd)){
				txRecord.setStatus("2");
				txRecord.setNote(jb.getString("message"));
				dao.saveOrUpdate(txRecord);
				return QwyUtil.getJSONString("end", jb.getString("message"));
			}
			Object objStatus = jb.get("status");
			if(!QwyUtil.isNullAndEmpty(objStatus)){
				String status = objStatus.toString();
				if("DOING".equalsIgnoreCase(status)){
					//正在审核
				}else if("FAILURE".equalsIgnoreCase(status)){
					//提现失败
					txRecord.setStatus("2");
					txRecord.setNote("提现失败");
				}else if("REFUND".equalsIgnoreCase(status)){
					//提现退回
					txRecord.setStatus("2");
					txRecord.setNote("提现退回");
				}else if("SUCCESS".equalsIgnoreCase(status)){
					//提现已到账
					txRecord.setStatus("1");
					txRecord.setNote("提现成功");
				}else if("UNKNOW".equalsIgnoreCase(status)){
					//未知
					txRecord.setStatus("2");
					txRecord.setNote("提现异常");
				}
				dao.saveOrUpdate(txRecord);
				return QwyUtil.getJSONString("ok", "");
			}
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return QwyUtil.getJSONString("ok", "");
	}
	
	/**
	 * 查询绑定银行卡列表
	 * 
	 * @param usersId
	 *            用户ID
	 * @return JOSN格式;
	 * @throws Exception
	 */
	public String bindList(long usersId) throws Exception {
		log.info("查询绑定银行卡列表,用户id：" + usersId);
		if(QwyUtil.isNullAndEmpty(usersId)){
			log.info("查询绑定银行卡列表,参数错误；");
			return paramException();
		}
		//payAPIService = new PayAPIServiceImpl();
		String payresult = payAPIService.bindList(usersId + "", 2);

		log.info("查询绑定银行卡列表,接口返回结果：" + payresult);
		return payresult;
	}
	
	/**
	 * 解绑卡;
	 * 
	 * @param bindid
	 *            绑卡id;获取途径: 需要先通过查询绑卡记录(接口:bindList),获取bindid;
	 * @param usersId 用户id
	 * @return JOSN格式;
	 * @throws Exception
	 */
	public String unbindCard(String bindid, long usersId) throws Exception {
		log.info("解绑卡,用户id：" + usersId + ";绑卡id: " + bindid);
		if(QwyUtil.isNullAndEmpty(bindid) || QwyUtil.isNullAndEmpty(usersId)){
			log.info("解绑卡,参数错误；");
			return paramException();
		}
		//payAPIService = new PayAPIServiceImpl();
		String payresult = payAPIService.unbindCard(bindid, usersId + "", 2);
		log.info("解绑卡,接口返回结果：" + payresult);
		return payresult;
	}
	

	/**封装 帐号表 未保存到数据库;<br>
	 * 状态为 "0:已绑定";
	 * @param usersId 用户ID;
	 * @param bankName 银行名称
	 * @param bankAccount 银行卡帐号
	 * @param bankAccountName 开户姓名
	 * @param idcard 身份证号;
	 * @param phone 开卡预留手机号
	 * @param registIp 绑卡时的ip
	 * @param type //第三方支付; 0:易宝支付; 1:连连支付
	 * @return null OR Account
	 */
	/**
	 * @param usersId 用户ID;
	 * @param bankName 银行名称
	 * @param bankAccount 银行卡帐号
	 * @param bankAccountName 开户姓名
	 * @param idcard 身份证号;
	 * @param phone 开卡预留手机号
	 * @param registIp 绑卡时的ip
	 * @param bankCode 银行编码 例如:ICBC
	 * @param type //第三方支付; 0:易宝支付; 1:连连支付
	 * @return
	 */
	public Account packAccount(long usersId, String bankName,
			String bankAccount, String bankAccountName, String idcard,
			String phone, 
			String registIp,String bankCode,String type) {
		if(QwyUtil.isNullAndEmpty(bankAccount)){
			return null;
		}
		String cardTop = bankAccount.substring(0, 6);
		String cardLast = bankAccount.substring(bankAccount.length()-4, bankAccount.length());
		Account acc = new Account(null, usersId, "0", UUID.randomUUID()
				.toString(), new Date(), null, bankName, bankAccount,
				bankAccountName, idcard, phone, cardTop, cardLast, usersId+"",
				2, null, registIp,bankCode,type, null);
		return acc;
		
	}
	
	/**根据身份证号去查找用户绑定的银行卡;
	 * @param idcard 身份证号
	 * @param type 支付类型 第三方支付; 0:易宝支付; 1:连连支付
	 * @return null OR Account
	 * @return
	 */
	public Account getAccountByIdcard(String idcard,String type){
		String hql = "FROM Account acc WHERE acc.idcard = ? AND acc.status = '1' ";
		ArrayList<Object> ob = new ArrayList<Object>();
		ob.add(DESEncrypt.jiaMiIdCard(idcard));
		if(!QwyUtil.isNullAndEmpty(type)){
			ob.add(type);
			hql+=" AND acc.type = ? ";
		}
		Object obj = dao.findJoinActive(hql, ob.toArray());
		if(QwyUtil.isNullAndEmpty(obj)){
			return null;
		}else{
			return (Account)obj;
		}
	}
	
	/**根据用户id去查找用户绑定的银行卡;
	 * @param usersId 用户id
	 * @param type 支付类型 第三方支付; 0:易宝支付; 1:连连支付
	 * @return null OR Account
	 * @return
	 */
	public Account getAccountByUsersId(long usersId,String type){
		String hql = "FROM Account acc WHERE acc.usersId = ? AND acc.status = '1' ";
		ArrayList<Object> ob = new ArrayList<Object>();
		ob.add(usersId);
		if(!QwyUtil.isNullAndEmpty(type)){
			ob.add(type);
			hql+=" AND acc.type = ? ";
		}
		Object obj = dao.findJoinActive(hql, ob.toArray());
		if(QwyUtil.isNullAndEmpty(obj)){
			return null;
		}else{
			Account ac = (Account)obj;
			return ac;
		}
	}
	
	/**根据用户id去查找用户绑定的银行卡;
	 * @param usersId 用户id
	 * @return null OR Account
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Account> getAccountByUsersId(long usersId){
		String hql = "FROM Account acc WHERE acc.usersId = ? AND acc.status = '1' ";
		ArrayList<Object> ob = new ArrayList<Object>();
		ob.add(usersId);
		List obj = dao.LoadAll(hql, ob.toArray());
		if(QwyUtil.isNullAndEmpty(obj)){
			return null;
		}else{
			return (List<Account>)obj;
		}
	}
	
	/**根据UsersId 和 请求ID;
	 * @param usersId 用户ID
	 * @param requestId 请求ID 或者是易宝流水号;
	 * @return
	 */
	public TxRecord getTxRecordByUsersId(long usersId,String requestId){
		StringBuffer hql = new StringBuffer();
		hql.append("FROM TxRecord tx ");
		hql.append("WHERE tx.usersId = ?  ");
		hql.append(" AND (tx.requestId = ? OR tx.ybOrderId = ?) ");
		
		Object obj = dao.findJoinActive(hql.toString(), new Object[]{usersId,requestId,requestId});
		if(!QwyUtil.isNullAndEmpty(obj) && !"-1".equals(obj.toString()))
			return (TxRecord)obj;
		return null;
	}
	
	@SuppressWarnings("unused")
	private String withdraw2(String requestid, long usersId,
			int amount, String drawtype, String userip)throws Exception{
		//payAPIService = new PayAPIServiceImpl();
		String payresult = payAPIService.withdraw(requestid, usersId+"", 2, "621483", "5537", 156, amount, drawtype, "", userip,"");
		
		log.info("接口返回结果：" + payresult);
		return payresult;
	}
	
	@SuppressWarnings("unused")
	private String directBindPay2(String orderid, int amount, String productname,
			String productdesc, long usersId, String callbackurl, String userip)
			throws Exception {
		/*Account account = getAccountByUsersId(usersId);
		if(QwyUtil.isNullAndEmpty(account)){
			return "{\"message\":\"该用户未绑定银行卡\",\"end\":\"exists\"}";
		}*/
		payAPIService = new PayAPIServiceImpl();
		String payresult = payAPIService.directBindPay(orderid,
				(int) (System.currentTimeMillis() / 1000), 156, amount,
				productname, productdesc, usersId + "", 2,
				"621483", "5537", 30, callbackurl,
				"", userip, "");
		log.info("接口返回结果：" + payresult);
		return payresult;
	}
	
	public String paramException(){
		return "{\"message\":\"参数错误\",\"end\":\"error\"}";
	}
	
}
