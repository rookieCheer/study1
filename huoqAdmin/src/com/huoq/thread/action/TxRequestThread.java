package com.huoq.thread.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.annotation.Resource;

import com.huoq.common.bean.SMSNoticeBean;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.baofoo.rsa.RsaCodingUtil;
import com.baofoo.tixian.TransReqBF0040001;
import com.baofoo.util.JXMConvertUtil;
import com.huoq.account.bean.MyAccountBean;
import com.huoq.account.bean.UserRechargeBean;
import com.huoq.admin.product.bean.CheckTxsqBean;
import com.huoq.common.bean.MyWalletBean;
import com.huoq.common.bean.YiBaoPayBean;
import com.huoq.common.util.BaofooClient;
import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.LockHolder;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.common.util.RequestParams;
import com.huoq.common.util.SimpleHttpResponse;
import com.huoq.orm.Account;
import com.huoq.orm.BlackList;
import com.huoq.orm.CzRecord;
import com.huoq.orm.TxRecord;
import com.huoq.orm.Users;
import com.huoq.orm.UsersInfo;
import com.huoq.payUtils.SecurityUtil;
import com.huoq.thread.bean.ScanExcpCzUsersThreadBean;
import com.huoq.thread.bean.TxQueryThreadBean;
import com.huoq.thread.dao.ThreadDAO;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**查询提现接口;后台线程;<br>
 * 后台线程,自动查询提现接口;查询的都是审核后的第二天;<br>
 * @author qwy
 *
 * @createTime 2015-06-02 03:09:15
 */
@Service
public class TxRequestThread implements Runnable{

	private static Logger log = Logger.getLogger(TxRequestThread.class);
	@Resource
	private ThreadDAO dao;
	@Resource
	private TxQueryThreadBean bean;
	@Resource
	private UserRechargeBean userRechargeBean;
	@Resource
	private YiBaoPayBean yiBaoPayBean;
	@Resource
	private CheckTxsqBean checkTxsqBean;
	@Resource
	private MyWalletBean myWalletBean;
	@Resource
	private MyAccountBean myAccountBean;
	/**需要获取提现的证书路径;<br>
	 * request.getServletContext().getRealPath("/CER");
	 */
	public static String pfxpath="";
	
	public static boolean isDongjie=false;
	/**
	 * 
	 */
	private static final String SUCCESS_STRING = "提现资金审核通过";
	
	@Resource
	private ScanExcpCzUsersThread scanExcpCzUsersThread;
	@Resource
	private ScanExcpCzUsersThreadBean scanExcpCzUsersThreadBean;
	private static ResourceBundle resb = ResourceBundle.getBundle("app");
	private static ResourceBundle resbsms = ResourceBundle.getBundle("sms-notice");

	@Resource
	private SMSNoticeBean smsNoticeBean;

	/**后台提现;
	 * @return
	 */
	@Override
	public synchronized void run() {
		try {
			
			if (isDongjie) {
				log.info("提现维护");
				return;
			}
			
			PageUtil<TxRecord> pageUtil = new PageUtil<TxRecord>();
			pageUtil.setPageSize(50);
			int currentPage = 0;
			if(QwyUtil.isNullAndEmpty(pfxpath)){
				log.info("提现证书地址不能为空...........");
				return;
			}
			for (;;) {
				currentPage++;
				pageUtil.setCurrentPage(currentPage);
				pageUtil = checkTxsqBean.loadRequestTxRecord(pageUtil, "0",false);
				List<TxRecord> listTxRecord= pageUtil.getList();
				if(QwyUtil.isNullAndEmpty(listTxRecord)){
					log.info("TxRequestThread没有要查询的提现记录: "+currentPage);
					break;
				}
				log.info("TxRequestThread要查询的提现记录: "+listTxRecord.size());
				if(!QwyUtil.isNullAndEmpty(listTxRecord)){
					for (TxRecord txRecord : listTxRecord) {
						long usersId = txRecord.getUsersId();
						String result = checkUsersMoney(usersId, txRecord.getId());
						if(SUCCESS_STRING.equals(result)){
							tx(txRecord.getId());
						}
					}
				}
			}
		} catch (Exception e) {
			log.error("操作异常: ",e);
			log.error("操作异常: ",e);
		}
	}
	/**发起提现;
	 * @param txRecordId 提现id
	 */
	public void tx(String txRecordId){

		synchronized (LockHolder.getLock(txRecordId)) {
			try {
				TxRecord txRecord = (TxRecord) dao.findById(new TxRecord(), txRecordId);
 
//				List<TxRecord> records = checkTxsqBean.findRecordsByUid(txRecord.getUsersId(), 1, 50, true,true);
				if(QwyUtil.isNullAndEmpty(txRecord)){
					log.info("txRecordId: "+txRecordId+" 提现记录不存在;");
					return;
				}
				if(!"0".equals(txRecord.getStatus())){
					log.info("该提现记录不在处理中，提现ID："+txRecord.getId());
					return;
				}
				//当天已有提现记录 所做处理
//				if (!QwyUtil.isNullAndEmpty(records) && records.size() > 3) {
//					txRecord.setStatus("2");
//					txRecord.setTxStatus("1");//该条记录已操作;
//					txRecord.setCheckTime(new Date());
//					txRecord.setNote(txRecord.getNote() + " 提现无效(一天只能提现3次 )");//失败原因;
//					dao.saveOrUpdate(txRecord);
//					String  returnMsg =  "";
//					boolean isOk = userRechargeBean.baoFuTixianFail(txRecord.getUsersId(), txRecord.getMoney());
//					if (isOk)
//						returnMsg += ";提现金额已返还;";
//					else
//						returnMsg += ";提现金额返还失败,请联系客服;";
//
//				} else {
					if(!"1".equals(txRecord.getIsCheck())){
						log.info(txRecord.getUsersId()+" 请先审核资金,再发起提现;");
						return;
					}
					Long usersId = txRecord.getUsersId();
					if ("0".equals(txRecord.getStatus()) && "0".equals(txRecord.getTxStatus())) {
						//查找绑定的宝付银行卡;type=1为宝付支付
						Account ac = myAccountBean.getAccountByUsersId(txRecord.getUsersId(), "1");
						
						//查询黑名单;
						String username = DESEncrypt.jieMiUsername(ac.getUsers().getUsername());
						BlackList bl =getBlackListByUsername(username);
						if(!QwyUtil.isNullAndEmpty(bl)){
							log.info(username+"该账户提现存在风险,请联系客服!【黑名单】");
							txRecord.setTxStatus("1");// 该条记录已操作;
							txRecord.setCheckTime(new Date());
							txRecord.setStatus("2");
							txRecord.setErrorCode("BL");
							txRecord.setNote("提现失败,账户异常【黑名单】");// 失败原因;
							log.info("UsersId: " + txRecord.getUsersId() + txRecord.getNote());
							dao.update(txRecord);
							return;
						}
						
						//提交提现审核记录成功;
						TransReqBF0040001 tx = new TransReqBF0040001();
						
						String orderId = txRecord.getRequestId();
						log.info("-------------------------提现ID: " + txRecord.getId() + "------提现订单: " + orderId
								+ "	用户ID: " + usersId);
						tx.setTrans_no(orderId);// 商户订单号
						tx.setTrans_money(QwyUtil.calcNumber(txRecord.getMoney(), 0.01, "*").toString());// 提现金额(元)
						//tx.setTrans_money(0.01+"");// 提现金额(元)
						tx.setTo_acc_name(ac.getBankAccountName());// 收款人姓名
						tx.setTo_acc_no(ac.getJmbankAccount());// 收款人银行帐号
						tx.setTo_bank_name(ac.getBankName());// 收款人银行名称
						tx.setTo_pro_name(QwyUtil.isNullAndEmpty(ac.getProvinceCode()) ? "" : ac.getProvinceCode());// 收款人开户行省名
						tx.setTo_city_name(QwyUtil.isNullAndEmpty(ac.getCityCode()) ? "" : ac.getCityCode());// 收款人开户行市名
						tx.setTo_acc_dept(QwyUtil.isNullAndEmpty(ac.getBraBankName()) ? "" : ac.getBraBankName());// 收款人开户行机构名
						tx.setTrans_summary("【后台】线程提交提现");// 摘要
						//-------------------调用提现方法,并处理结果--------------------//
						//调用提现方法;
//						TixianMain tixianMain = new TixianMain();
//						String txResult = tixianMain.tiXianHuoq(pfxpath, tx, false);
						boolean txOk = true;//是否提现失败; true:提现成功|false:提现失败
						String bfOrderId = "";//宝付ID
						String resultCode = "";//返回码
						String returnMsg = "";//返回交易信息;失败时,存进数据库
						
						String path = this.getClass().getClassLoader().getResource("/").getPath();				
						// 商户私钥
						String pfxpath = path.substring(0,path.indexOf("WEB-INF")) +"CER/"
								+ resb.getString("daifusy.name");
						// 获取公钥
						String cerpath = path.substring(0,path.indexOf("WEB-INF")) + "CER/"
								+ resb.getString("daifugy.name");	
						// 请求宝付地址
						String requestUrl = resb.getString("withdrawals_url");
						// 商户�?
						String memberId = resb.getString("daifuM.Id");
						// 终端�?
						String terminalId = resb.getString("daifuT.id");
						// 私钥密码
						String pfxpwd = resb.getString("pfxdf.pwd");
						// 加密参数类型
						String dataType = resb.getString("datadf.type");
						
						Map<String, Object> trans_reqData = new HashMap<String, Object>();
						trans_reqData.put("trans_no", orderId);		// 商户订单�?
						trans_reqData.put("trans_money", QwyUtil.calcNumber(txRecord.getMoney(), 0.01, "*").toString());		// 转账金额
						trans_reqData.put("to_acc_name", ac.getBankAccountName());	// 收款人姓名
						trans_reqData.put("to_acc_no", ac.getJmbankAccount());			// 收款人银行帐号
						trans_reqData.put("to_bank_name", ac.getBankName());	// 收款人银行名称
						trans_reqData.put("to_pro_name", QwyUtil.isNullAndEmpty(ac.getProvinceCode()) ? "" : ac.getProvinceCode());			// 收款人开户行省名
						trans_reqData.put("to_city_name",QwyUtil.isNullAndEmpty(ac.getCityCode()) ? "" : ac.getCityCode());			// 收款人开户行市名
						trans_reqData.put("to_acc_dept", QwyUtil.isNullAndEmpty(ac.getBraBankName()) ? "" : ac.getBraBankName());			// 收款人开户行机构�?
						Users users = txRecord.getUsers();
						UsersInfo userInfo = users.getUsersInfo();
						String id_card = DESEncrypt.jieMiIdCard(userInfo.getIdcard());
						String mobile = username;
						trans_reqData.put("trans_card_id", id_card);	// 银行卡身份证件号�?
						trans_reqData.put("trans_mobile", mobile);		// 银行卡预留手机号
						trans_reqData.put("trans_summary", "摘要");		// 摘要

						List<Object> list = new ArrayList<Object>();
						list.add(trans_reqData);
						
						Map<String, Object> trans_reqDatas = new HashMap<String, Object>();
						trans_reqDatas.put("trans_reqData", list);
						
						List<Object> lists = new ArrayList<Object>();
						lists.add(trans_reqDatas);
						
						Map<String, Object> trans_content = new HashMap<String, Object>();
//						trans_content.put("trans_head", trans_head);
						trans_content.put("trans_reqDatas", lists);
						
						Map<Object, Object> ArrayData = new HashMap<Object, Object>();
						ArrayData.put("trans_content", trans_content);
						// 将集合转成json
						JSONObject jsonObjectFromMap = JSONObject.fromObject(ArrayData);
						String Json = jsonObjectFromMap.toString();
						log.info("====宝付请求明文:" + Json);
						
						// 加密
						String base64str = SecurityUtil.Base64Encode(Json);
						String data_content = RsaCodingUtil.encryptByPriPfxFile(base64str, pfxpath, pfxpwd);
						
						// 接口的公共参�?
						RequestParams params = new RequestParams();
						params.setMemberId(Integer.parseInt(memberId));
						params.setTerminalId(Integer.parseInt(terminalId));
						params.setDataType(dataType);
						params.setDataContent(data_content);// 加密后数�?
						params.setVersion("4.0.0");
						params.setRequestUrl(requestUrl);
						// 调用代付接口
						SimpleHttpResponse res = BaofooClient.doRequest(params);
						log.info("====宝付公共参数表单:" + params);
						
						String reslut = res.getEntityString();
						// 解密
						reslut = RsaCodingUtil.decryptByPubCerFile(reslut, cerpath);
						reslut = SecurityUtil.Base64Decode(reslut);
						log.info("====宝付响应的表:" + reslut);
						synchronized (orderId) {
							if (reslut.isEmpty()) {
								log.info("====宝付同步返回报文为空");
								
							}
							Map<String, Object> ArrayDataString = JXMConvertUtil.JsonConvertHashMap((Object) reslut);// 将JSON转化为Map对象�?
							if (!ArrayDataString.containsKey("return_code")) {
								log.info("解析参数[return_code]不存在");
							} else {
								 resultCode = ArrayDataString.get("return_code").toString();
								String retMsg = ArrayDataString.get("return_msg").toString();
								if ("0000".equals(resultCode)) {
									txOk = true;
								} else {
									txOk = false;
									returnMsg =retMsg + "错误代码  " + resultCode;
								}
								
								
							}
						}
						
						//提现是否成功(发送宝付交易成功,未知实际提现到账结果)
						txRecord.setTxStatus("1");//该条记录已操作;
						txRecord.setCheckTime(new Date());
						if (txOk) {
							//提现成功;修改TxRecord的记录状态;改为1;
							log.info("提现成功");
							txRecord.setStatus("1");
							txRecord.setYbOrderId(bfOrderId);

							//新增提现成功后短信提醒 20171025 by yigy
							//新增提现成功后短信提醒 20171025 by yigy
							String shortUrl = "";
							String sendMessage = resbsms.getString("SMS_TX_MONEY_SUCC");
							smsNoticeBean.sendSMSInThreadPool(shortUrl,DESEncrypt.jieMiUsername(users.getUsername()), sendMessage, new Object[]{QwyUtil.calcNumber(txRecord.getMoney(), 100, "/", 2)});

						} else {
							//提现失败;修改TxRecord的记录状态;改为2;
							boolean isOk = userRechargeBean.baoFuTixianFail(usersId, txRecord.getMoney());
							if (isOk)
								returnMsg += ";提现金额已返还;";
							else
								returnMsg += ";提现金额返还失败,请联系客服;";
							txRecord.setStatus("2");
							txRecord.setErrorCode(resultCode);
							txRecord.setYbOrderId(bfOrderId);
							txRecord.setNote(txRecord.getNote() + returnMsg);//失败原因;
							log.info("------------------------------提现失败原因:" + returnMsg + "	用户ID: " + usersId);
						}
						dao.saveOrUpdate(txRecord);
						//-------------------调用提现方法,并处理结果--------END--------------------------------//
					}else{
						log.info("------------------------------该提现记录已被操作过;提现ID:"+txRecord.getId()+"	用户ID: " + usersId);
					}
//				}
			} catch (Exception e) {
				log.error("操作异常: ",e);
			}
		}
		
 	}
	/**查找黑名单;
	 * @param username 用户名
	 * @return 黑名单
	 */
	public BlackList getBlackListByUsername(String username){
		StringBuffer sb = new StringBuffer();
		sb.append("FROM BlackList bl WHERE bl.username = ? ");
		sb.append("AND bl.status = '0' ");
		return (BlackList)dao.findJoinActive(sb.toString(), new Object[]{username});
	}
	
	
	/**根据用户id,查询用户是否已经得到过利息;
	 * @param uid 用户id
	 * @return
	 */
	public boolean isAlreadyGetEarnings (long uid){
		String return_time = QwyUtil.fmyyyyMMdd.format(QwyUtil.addDaysFromOldDate(new Date(), -3).getTime());
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * FROM interest_details ");
		sql.append(" WHERE users_id = ? ");
		sql.append(" AND status = '2' ");
		sql.append(" AND return_time >= ? ");
		List list = dao.LoadAllSql(sql.toString(), new Object[]{uid,return_time});
		return QwyUtil.isNullAndEmpty(list)?false:true;
	}
	
	/**  查询用户是否有在固定的某段时间内充值;
	 * @param uid 用id
	 * @param startDate 开始时间
	 * @param endDate 结束时间
	 * @return true:有充值记录; false:没有充值记录;
	 */
	public boolean isRechargeByDate(long uid,String startDate,String endDate){
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * FROM cz_record ");
		sql.append(" WHERE users_id = ? ");
		sql.append(" AND insert_time BETWEEN ? AND ? ");
		sql.append(" AND status = '1' ");
		List list = dao.LoadAllSql(sql.toString(), new Object[]{uid,startDate,endDate});
		return QwyUtil.isNullAndEmpty(list)?false:true;
	}
	
	
	/**提现时--检查用户资金情况;
	 * @param usersId 用户id
	 * @return 返回相关信息; 
	 * @throws Exception 
	 */
	public String checkUsersMoney(long usersId,String txRecordId) throws Exception{
		PageUtil<CzRecord> czPageUtil = new PageUtil<CzRecord>();
		czPageUtil.setPageSize(99999);
		czPageUtil.setCurrentPage(1);
		czPageUtil = scanExcpCzUsersThreadBean.getCzRecord(czPageUtil, "1", usersId);
		List<CzRecord> listCzRecord = czPageUtil.getList();
		if(!QwyUtil.isNullAndEmpty(listCzRecord)){
			for (CzRecord cz : listCzRecord) {
				String result = scanExcpCzUsersThread.queryCzOrder(cz);
				log.info("---------处理提现时,审核的充值订单结果: "+result);
			}
		}
		TxRecord txRecord = (TxRecord) dao.findById(new TxRecord(), txRecordId);
		txRecord.setIsCheck("1");//该条记录已审核;
		double freezeMoney = txRecord.getUsers().getUsersInfo().getFreezeMoney().doubleValue();
		//校验合法收入,支出;计算可提现余额;
		double investingSum = getInvestingSum(usersId);//冻结中的投资产品金额(分)
		if(investingSum!=freezeMoney){
			String note = "【账户冻结资金】与【产品冻结资金】不一致,需人工审核";
			log.info(usersId+note);
			txRecord.setCheckTime(new Date());
			txRecord.setTxStatus("1");// 该条记录已操作;
			if(QwyUtil.isNullAndEmpty(txRecord.getNote())||!txRecord.getNote().contains(note)){
				txRecord.setNote(note+txRecord.getNote());//失败原因;
			}
			dao.saveOrUpdate(txRecord);
			return note;
		}
		
		double txSum = getTxSum(usersId);//已提现总金额(分)
		double czSum = getCzSumByUsersId(usersId);//充值总金额(分)
		double shouyi = getInterestDetailsShouyi(usersId);//已获得的产品利息+返现红包(分)
		double inviteSum = getInviteEarnSum(usersId);//邀请好友获得的奖励(分)
		Object[] cp = getCoinPurse(usersId);//零钱包收益(分)
		double lqgLeftMoney=0d;//零钱罐余额(分)
		double lqgShouyi = 0d;//零钱罐收益(分)
		if(!QwyUtil.isNullAndEmpty(cp)){
			lqgLeftMoney = QwyUtil.isNullAndEmpty(cp[0])?0d:Double.parseDouble(cp[0].toString());
			lqgShouyi = QwyUtil.isNullAndEmpty(cp[1])?0d:Double.parseDouble(cp[1].toString());
		}
		
		//收入金额
		double incomeMoney = QwyUtil.calcNumber(czSum, shouyi, "+").doubleValue();
		double incomeMoney2 = QwyUtil.calcNumber(inviteSum, lqgShouyi, "+").doubleValue();
		double incomeMoneySum = QwyUtil.calcNumber(incomeMoney, incomeMoney2, "+").doubleValue();
		
		//支出金额
		double payMoney = QwyUtil.calcNumber(investingSum, lqgLeftMoney, "+").doubleValue();
		double payMoneySum = QwyUtil.calcNumber(payMoney, txSum, "+").doubleValue();
		
		
		//总收入金额-总支出金额=可提现金额
		//最大可提现金额(分)
		double txMaxMoney = QwyUtil.calcNumber(incomeMoneySum, payMoneySum, "-").doubleValue();
		
		if(txRecord.getMoney().doubleValue()>txMaxMoney){
			String note = "提现金额大于可提现金额,需人工审核;最大可提现:"+QwyUtil.calcNumber(txMaxMoney, 100, "/").toPlainString()+"元";
			log.info(usersId+note);
			txRecord.setCheckTime(new Date());
			txRecord.setTxStatus("1");// 该条记录已操作;
			if(QwyUtil.isNullAndEmpty(txRecord.getNote())||!txRecord.getNote().contains(note)){
				txRecord.setNote(note+txRecord.getNote());//失败原因;
			}
			dao.saveOrUpdate(txRecord);
			return note;
		}
		txRecord.setCheckTime(new Date());
		dao.saveOrUpdate(txRecord);
		/*boolean isGetEarnings = isAlreadyGetEarnings(usersId);
		if(!isGetEarnings){
			//3天内没有收益,请人工审核
			txRecord.setCheckTime(new Date());
			String note = "3天内没有收益,请人工审核";
			if(QwyUtil.isNullAndEmpty(txRecord.getNote())||!txRecord.getNote().contains(note)){
				txRecord.setNote(note+txRecord.getNote());//失败原因;
			}
			dao.saveOrUpdate(txRecord);
			continue;
		}
		boolean isRecharge = isRechargeByDate(txRecord.getUsersId(), "2017-02-22 09:00:00", "2017-02-23 12:00:00");*/
		
		//针对22和23号异常充值用户作过度处理,后期可注释;
		/*if(isRecharge){
			//22号和23号有充值,请人工审核
			txRecord.setCheckTime(new Date());
			String note = "22号和23号有充值,请人工审核";
			if(QwyUtil.isNullAndEmpty(txRecord.getNote())||!txRecord.getNote().contains(note)){
				txRecord.setNote(note+txRecord.getNote());//失败原因;
			}
			dao.saveOrUpdate(txRecord);
			continue;
		}*/
		log.info(usersId+SUCCESS_STRING);
		return SUCCESS_STRING;
	}
	
	/**根据用户ID获取用户的总充值金额;
	 * @param usersId
	 * @return 充值金额(分)
	 */
	public  double getCzSumByUsersId(long usersId){
		StringBuffer sb = new StringBuffer();
		sb.append("select SUM(cz.money)  from cz_record cz WHERE  cz.users_id = ? and cz.STATUS = '1' ");
		List obj = dao.LoadAllSql(sb.toString(), new Object[]{usersId});
		return QwyUtil.isNullAndEmpty(obj.get(0))?0D:Double.parseDouble(obj.get(0).toString());
	}
	
	/**根据用户id获取用户的产品收益+红包返现;
	 * <br>'状态 0未支付,1已冻结,2已支付,3已删除',
	 * @param usersId
	 * @return 收益金额(分)
	 * @throws Exception 
	 * 红包直接返现到账号余额 不走利息表 涉及到的表：fundRecord coupon
	 * 修改：wxl 2017年3月1日11:21:07
	 */
	public double getInterestDetailsShouyi(long usersId) throws Exception{
		double shouyi = 0d;
		double hongb = 0d;
		double sum = 0d;
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT SUM(ind.pay_interest) ");
		sb.append(" FROM interest_details ind ");
		sb.append(" WHERE ind.users_id = ? AND ind.status = '2' ");
		List obj = dao.LoadAllSql(sb.toString(), new Object[]{usersId});
		
		shouyi = QwyUtil.isNullAndEmpty(obj.get(0))?0D:Double.parseDouble(obj.get(0).toString());
		
		//使用的红包总额
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT SUM(init_money) FROM coupon WHERE type IN ('2','3') AND `STATUS` = '2' AND users_id = ?");
		List ob = dao.LoadAllSql(sql.toString(),new Object[]{usersId});
		hongb = QwyUtil.isNullAndEmpty(ob.get(0))?0D:Double.parseDouble(ob.get(0).toString());
		
		//总收益：利息总额+红包总收益
		sum = QwyUtil.calcNumber(shouyi, hongb,"+").doubleValue();
		
//		if(!QwyUtil.isNullAndEmpty(obj)){  红包直接返现到账号余额 不走利息表 涉及到的表：fundRecord coupon修改：wxl 2017年3月1日11:21:07
//			double lx = QwyUtil.isNullAndEmpty(obj[0])?0d:Double.parseDouble(obj[0].toString());
//			double hongbao = QwyUtil.isNullAndEmpty(obj[1])?0d:Double.parseDouble(obj[1].toString());
//			shouyi = QwyUtil.calcNumber(lx, hongbao, "+").doubleValue();
//		}
		return sum;
	}
	
	/**
	 * 通过该用户ID 获取该用户邀请好友的奖励总额
	 * @param usersId 用户ID
	 * @return
	 */
	public double getInviteEarnSum(long usersId) {
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT SUM(ie.earn_money) FROM invite_earn ie WHERE ie.status = '1' AND ie.invite_id = ? ");
		List obj = dao.LoadAllSql(sb.toString(), new Object[]{usersId});
		return QwyUtil.isNullAndEmpty(obj.get(0))?0D:Double.parseDouble(obj.get(0).toString());
	}
	
	/**获取零钱包收益;
	 * @param usersId 用户id
	 * @return
	 */
	public Object[] getCoinPurse(long usersId){
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT cp.in_money,cp.pay_interest FROM coin_purse cp WHERE cp.users_id= ? AND cp.status ='0'");
		List list = dao.LoadAllSql(sb.toString(), new Object[]{usersId});
		if(QwyUtil.isNullAndEmpty(list)){
			return new Object[]{0,0};
		}
		return (Object[])list.get(0);
	}
	
	/**获取冻结中产品金额;
	 * @param usersId 用户id
	 * @return
	 */
	public double getInvestingSum(long usersId){
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT  SUM(inv.in_money) FROM investors inv WHERE  inv.users_id = ? AND inv.investor_status = '1' ");
		List obj = dao.LoadAllSql(sb.toString(), new Object[]{usersId});
		return QwyUtil.isNullAndEmpty(obj.get(0))?0D:Double.parseDouble(obj.get(0).toString());
	}
	
	/**获取已经提现的总金额;
	 * @param usersId 用户id
	 * @return
	 */
	public double getTxSum(long usersId){
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT SUM(tx.money) FROM tx_record tx WHERE  tx.users_id = ? AND tx.status = '1' ");
		List obj = dao.LoadAllSql(sb.toString(), new Object[]{usersId});
		return QwyUtil.isNullAndEmpty(obj.get(0))?0D:Double.parseDouble(obj.get(0).toString());
	}

	public static void main(String[] args) {
		String strTransReqDats = "{\"trans_reqData\":{\"trans_batchid\":\"47443527\",\"trans_no\":\"496300cc-e136-41df-b90c-6f1cbd4496e1\",\"trans_orderid\":\"54411455\",\"trans_money\":\"8.00\",\"to_acc_name\":\"王杨\",\"to_acc_no\":\"6217002560001727184\",\"to_acc_dept\":\"||中国建设银行\",\"trans_summary\":\"【后台】线程提交提现\"}}";
		JSONObject trans_reqData_json;
		if(strTransReqDats.startsWith("{")){
			//此判断,兼容宝付返回不同的格式进行处理;格式如下:
			//{"trans_reqData":{"to_acc_name":"沈丹","to_acc_no":"6212261202037863561","trans_no":"30279d24-fc65-45e1-bbb8-d44680c6b80f","trans_money":"10.00","to_acc_dept":"||中国工商银行","trans_batchid":"47443524","trans_summary":"【后台】线程提交提现","trans_orderid":"54411452"}}
			JSONObject trans_reqData = JSONObject.fromObject(strTransReqDats);
			String  trans_reqData_json_string= trans_reqData.getString("trans_reqData");
			trans_reqData_json = JSONObject.fromObject(trans_reqData_json_string);
			//如果获取到的trans_reqDatas不是JSONArray格式,则拼接成JSONArray格式;
			log.info("-----------trans_reqDatas--JSONObject格式----"+strTransReqDats);
			log.info("------------拼接成JSONArray格式------------");
			strTransReqDats ="["+strTransReqDats+"]";
			
		}else{
			//[{"trans_orderid":"54411449","trans_batchid":"47443521","trans_no":"1a09fb85-0312-4225-acc0-bb7996686ec8","trans_money":"502.00","to_acc_name":"杨浩浩","to_acc_no":"6212261607007525257","to_acc_dept":"||中国工商银行","trans_summary":"【后台】线程提交提现"}]
			log.info("-----------trans_reqDatas2----JSONArray格式--"+strTransReqDats);
			JSONArray jsonArrayTransReqDats = JSONArray.fromObject(strTransReqDats);
			trans_reqData_json = jsonArrayTransReqDats.getJSONObject(0);
		}
		String bfOrderId,resultCode;
		if (!QwyUtil.isNullAndEmpty(trans_reqData_json)) {
			bfOrderId = (String) trans_reqData_json.get("trans_orderid");//宝付订单号;
			//resultCode = (String) jsonTransHead.get("return_code");
			log.info(bfOrderId);
		}
	}
	
	/**提现解除警报;<br>
	 * 把处理中的提现记录;的tx_status值改成'0';
	 * @param txRecordId
	 */
	public void allclearTx(String txRecordId){
		String check_time = QwyUtil.fmyyyyMMddHHmmss.format(new Date());
		StringBuffer sb = new StringBuffer();
		sb.append("UPDATE tx_record SET tx_status = '0' ,check_time = ?  WHERE id = ? AND status = '0' ");
		dao.excuteSql(sb.toString(), new Object[]{check_time,txRecordId});
	}
	
}
