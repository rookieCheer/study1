package com.huoq.callBack.action;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.huoq.account.bean.UserRechargeBean;
import com.huoq.admin.product.bean.CheckTxsqBean;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.TxRecord;
import com.huoq.payUtils.JXMConvertUtil;
import com.huoq.payUtils.PayUtil;
import com.huoq.payUtils.SecurityUtil;
import com.huoq.payUtils.payUtilImpl.PayUtilImpl;
import com.huoq.product.action.IndexAction;
import com.huoq.thread.dao.ThreadDAO;

/**
 * 回调Action
 *
 */
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product")
public class CallBackAction extends IndexAction {
	private static Logger log = Logger.getLogger(CallBackAction.class);
		
	@Resource
	private CheckTxsqBean checkTxsqBean;
	@Resource
	private UserRechargeBean userRechargeBean;
	@Resource
	private ThreadDAO dao;
	
	/**
	 * 提現回調
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/txhxCallbackUrl")
	public @ResponseBody Map<String, Object> txhxCallbackUrl(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		PayUtil payUtil = new PayUtilImpl();
		log.info("=====进入环迅提現回调接口");
		String resultCode = request.getParameter("resultCode");	
		String resultMsg =  request.getParameter("resultMsg");
		String merchantID = request.getParameter("merchantID");	
		String sign = request.getParameter("sign");	
		String responseContent = request.getParameter("response");	
		String returnMsg = null;
		if (responseContent.isEmpty()) {// 判断参数是否为空
			log.info("=====返回数据为空");
			return null;
		}
		log.info("=====返回数据:" + responseContent);
		
		//验证签名
		if(!payUtil.checkSign(merchantID, resultCode, resultMsg, responseContent, sign)){
			log.info("提现回调签名验证未通过！");
			return null;
		}
		
		responseContent = SecurityUtil.Base64Decode(responseContent);
		log.info("=====返回数据解密结果:" + responseContent);
		
		Map<String, Object> ArrayData = JXMConvertUtil.JsonConvertHashMap(responseContent);// 将JSON转化为Map对象。
		if (!ArrayData.containsKey("resp_code")) {
			log.info("=====map对象没有返回码");
		} else {
			log.info("=====解密后map对象" + ArrayData);
			try {
				String resp_code = resultCode;		// 支付结果编码
				String merBillNo = ArrayData.get("merBillNo").toString();		// 商户订单号
				String ipsBillNo =  ArrayData.get("ipsBillNo").toString();		//该笔交易充值的 IPS 订单号 ipsBillNo
				String ipsDoTime = ArrayData.get("ipsDoTime").toString();		//IPS 处理时间
				String ipsFee = ArrayData.get("ipsTrdAmt").toString();			//IPS 手续费金额
				String merFee = ArrayData.get("merFee").toString();				//平台手续费
				String ipsAcctNo = ArrayData.get("ipsAcctNo").toString();	    //IPS 存管账户号
				String ipsTrdAmt = ArrayData.get("ipsTrdAmt").toString();	    //IPS 提现金额
				String trdStatus = ArrayData.get("trdStatus").toString();		//提现状态0 失败1 成功2 处理中3 退票		
				
				double money = QwyUtil.calcNumber(ipsTrdAmt, 100, "*").doubleValue();
				synchronized (merBillNo) {
					TxRecord txRecord = checkTxsqBean.findTxRecordByJyls(merBillNo);
					//提现是否成功(发送宝付交易成功,未知实际提现到账结果)
					txRecord.setTxStatus("1");//该条记录已操作;
					txRecord.setCheckTime(new Date());
					
					//change
					if (PayUtil.successCode.equals(resp_code)&&PayUtil.czSuccessState.equals(trdStatus)) {
						log.info("提现成功");
						//提现成功;修改TxRecord的记录状态;改为1;
						txRecord.setStatus("1");
						txRecord.setYbOrderId(ipsBillNo);
					}else if(PayUtil.czDealState.equals(trdStatus)){//提现状态：处理中
						//TODO 添加状态4？  处理中  的提现记录，下次再调用该接口时，根据其订单号查询后，再进行处理
						txRecord.setStatus("4");//添加提现状态为“处理中”,
						txRecord.setErrorCode(resultCode);
						txRecord.setYbOrderId(merBillNo);//将环迅的交易号更新到易宝的交易流水号中
						txRecord.setNote(txRecord.getNote() + returnMsg);//失败原因;
					}else{
						//退票和失败都返还金额
						//提现失败;修改TxRecord的记录状态;改为2;
						boolean isOk = userRechargeBean.baoFuTixianFail(txRecord.getUsersId(), txRecord.getMoney());
						if (isOk){
							returnMsg += ";提现金额已返还;";
						}else{
							returnMsg += ";提现金额返还失败,请联系客服;";
						}
						txRecord.setStatus("2");
						txRecord.setErrorCode(resultCode);
						txRecord.setYbOrderId(ipsBillNo);//将环迅的交易号更新到易宝的交易流水号中
						txRecord.setNote(txRecord.getNote() + returnMsg);//失败原因;
						log.info("------------------------------提现失败原因:" + returnMsg + "	用户ID: " + txRecord.getUsersId());
					}
					txRecord.setMoney(money);//实际提现金额
					dao.saveOrUpdate(txRecord);
					response.getWriter().print("ipsCheckOk");
				}
			}catch(Exception e){
				log.error("提现回调失败！"+e);
			}
		}
		return null;
	}
}
