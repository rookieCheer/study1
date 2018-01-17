package com.huoq.common.bean;

import java.util.Map;


import java.text.SimpleDateFormat;
import java.util.Date;

public class BaoFuBean {
	/**
	 * 宝付支付的配置文件路径;
	 */
	public static String APP_PROPERTIES_URL="/app.properties";
	/**
	 * 宝付支付的配置文件路径;
	 */
	public static String CER_URL="";
	private static BaoFuQueryBean wc= new BaoFuQueryBean();
	//private static BaoFuBean baoFuBean = new BaoFuBean();
	private BaoFuBean(){
		
	}
	/**查询充值订单;<br>
	 * 对充值订单进行再次查询确认;
	 * @param orderId 原充值订单号;
	 * @param queryId 查询流水号;
	 * @return 应答码	resp_code    应答信息	resp_msg     成功金额(元)	succ_amt  <br>
	 * 商户订单号	orig_trans_id  查询流水号	 trans_serial_no <br>
	 * 应答码: <br>
	 * 0000	交易成功<br>
	 * 9999	其它错误<br>
	 * FI00002	交易结果未知，请稍后查询<br>
	 * FI00007	交易失败<br>
	 * FI00014	订单不存在<br>
	 * FI00054	订单未支付<br>
	 * @throws Exception
	 */
	public static Map<String,Object> queryCzRecord(final String orderId, final Date insert_time, final String queryId) throws Exception{
		if(wc==null){
			synchronized (wc) {
				if(wc==null)
					wc = new BaoFuQueryBean();
			}
		}
		
		String path = Thread.currentThread().getContextClassLoader().getResource("/").getPath(); 
		CER_URL = path.substring(0,path.indexOf("WEB-INF")) +"CER";
		APP_PROPERTIES_URL = path+"app.properties";
		SimpleDateFormat formate = new SimpleDateFormat("yyyyMMddHHmmss");
		String time = "";
		if (insert_time != null) {
			time = formate.format(insert_time);
		}else{
			time = formate.format(new Date());
		}
		return wc.CzQuery_test (APP_PROPERTIES_URL, CER_URL,orderId, time, queryId);
	}
	
	
	
}
