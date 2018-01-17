package com.huoq.payUtils;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public interface PayUtil {
	String successCode = "000000";//响应成功的code
	String czErrorState = "0";	  //充值提现失败的状态
	String czSuccessState = "1";  //充值提现成功的状态
	String czDealState = "2";	  //充值提现处理中的状态
	String txBackState = "3";	  //提现退票
	
	/**
	 * Description:充值接口
	 * @author  changhaipeng
	 * @date 下午4:25:20
	 */
	public Map<String,Object> recharge(Map<String, Object> data);
	
	
	/**
	 * Description:提现接口
	 * @author  changhaipeng
	 * @date 下午5:30:36
	 */
	public Map<String, Object> withdrawals(Map<String, Object> data);
	
	
	/**
	 * Description:验证签名的正确性,按加密的顺序进行传入
	 * @author  changhaipeng
	 * @ 2017年6月3日09:09:30
	 */
	public boolean checkSign(String obj1,String obj2,String obj3,String checkSign);
	
	/**
	 * Description:验证签名的正确性,按加密的顺序进行传入
	 * @author  changhaipeng
	 * @ 2017年6月3日09:09:30
	 */
	public boolean checkSign(String obj1,String obj2,String obj3,String obj4,String checkSign);
	/**
	 * Description:获得商户订单号merBillNo商户订单号不能重复，前 6 位以 商 户 号 起 始
	 * @author  changhaipeng
	 * @上午10:28:19
	 */
	public String getMerBillNo();
	
	/**
	 * Description:查询可以支持的银行,map中有success和response
	 * @author  changhaipeng
	 * 2017年6月5日13:36:16
	 */
	public Map<String, Object> getBankList();
	
	/**
	 * Description:检验响应的结果是否正确
	 * @author  changhaipeng
	 * @下午1:52:28
	 */
	public Map<String, Object> checkResponse(HttpServletRequest request);
	
	/**
	 * Description:用户开户，异步执行，在回调中解析数据
	 * @author  changhaipeng
	 * @date 2017年6月5日 下午4:40:05
	 */
	public Map<String, Object> register(Map<String, Object> data);
	
	
	/**
	 * Description:验证充值记录
	 * @author  changhaipeng
	 * @date 2017年6月6日 下午3:22:12
	 */
	public Map<String, Object> checkTraRec(Map<String,Object> data);
	
	
}
