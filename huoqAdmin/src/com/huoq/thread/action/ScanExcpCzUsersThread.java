package com.huoq.thread.action;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.admin.product.bean.BlackListBean;
import com.huoq.common.bean.BaoFuBean;
import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.CzRecord;
import com.huoq.orm.ExcpCzUsers;
import com.huoq.thread.bean.ScanExcpCzUsersThreadBean;



/**扫描充值记录;对异常的充值记录进行处理;<br>
 * 把充值记录跟宝付支付进行对比校验;<br>
 * 有异常数据则进行修复;
 * @author 覃文勇
 *
 * 2017年2月25日下午6:46:49
 */
@Service
public class ScanExcpCzUsersThread implements Runnable{
	private static Logger log = Logger.getLogger(ScanExcpCzUsersThread.class);
	@Resource
	private ScanExcpCzUsersThreadBean scanExcpCzUsersThreadBean;
	@Resource	
    private BlackListBean blackListBean;
	private static final int PAGE_SIZE = 100;
	@Override
	public synchronized void run() {
		long st = System.currentTimeMillis();
		int currentPage = 0;
		int totalPage = 0;
		int totalSize = 0;
		int leftSize = 0;
		try {
			log.info("进入后台线程----对充值表CzRecord进行扫描;当前时间:"+QwyUtil.fmyyyyMMddHHmmss.format(new Date()));
			PageUtil<CzRecord> pageUtil = new PageUtil<CzRecord>();
			pageUtil.setPageSize(PAGE_SIZE);
			for (;;) {
				currentPage++;
				pageUtil.setCurrentPage(1);
				pageUtil = scanExcpCzUsersThreadBean.getCzRecord(pageUtil,"1",null);
				List<CzRecord> listCzRecord = pageUtil.getList();
				if(QwyUtil.isNullAndEmpty(listCzRecord) || (totalPage!=0 && currentPage>totalPage)){
					leftSize = pageUtil.getCount();//结束时 剩余的条数;
					log.info("-------------------没有需要校对的充值记录;,当前页数: "+currentPage);
					break;
				}
				totalPage = totalPage==0?pageUtil.getPageCount():totalPage;
				totalSize = totalSize==0?pageUtil.getCount():totalSize;
			for (CzRecord czRecord : listCzRecord) {
				if(QwyUtil.isNullAndEmpty(czRecord) || QwyUtil.isNullAndEmpty(czRecord.getOrderId())){
					continue;
				}
				String result = queryCzOrder(czRecord);
				log.info(result);
			}
			}
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		
		if(leftSize>=PAGE_SIZE){
			try {
				Thread a = new Thread(this);
				a.start();
				a.join();
			} catch (InterruptedException e) {
				log.error("操作异常: ",e);
			}
		}
		long et = System.currentTimeMillis();
		log.info("--------------对充值表CzRecord进行扫描;耗时: "+(et-st));
	}
	
	
	/**查询充值订单状态;<br>
	 * 如果查询到订单有异常 则直接做了处理;
	 * @param czRecord 关联Users实体的CzRecord; 请用Hibernate查询CzRecord
	 * @return 返回字符串;
	 * @throws Exception
	 */
	public String queryCzOrder(CzRecord czRecord) throws Exception{
		if(QwyUtil.isNullAndEmpty(czRecord)){
			return "充值记录不存在";
		}
		if(QwyUtil.isNullAndEmpty(czRecord.getUsers())){
			return "充值记录没有关联到Users实体";
		}
		String uuid = UUID.randomUUID().toString();
		Map<String, Object> map = BaoFuBean.queryCzRecord(czRecord.getOrderId(),czRecord.getInsertTime(), uuid);
		String respCode = map.get("resp_code").toString();
		if("0000".equals(respCode)){
			//交易成功;
			String orderId = map.get("orig_trans_id").toString();
			String queryId = map.get("trans_serial_no").toString();
			String bfMoneyStr = map.get("succ_amt").toString();//返回金额单位(元)
			//查询返回的订单号和流水号必须和发起的一致;否则数据被篡改过;视为异常数据;
			System.out.println("orderID:"+orderId+" ===================== queryID:"+queryId);
			if(czRecord.getOrderId().equals(orderId) && uuid.equals(queryId)){
				//继续对比充值金额;
				double czMoney = czRecord.getMoney();
//				double bfMoney = QwyUtil.calcNumber(Double.parseDouble(bfMoneyStr), 100, "*").doubleValue();
				double bfMoney = QwyUtil.calcNumber(Double.parseDouble(bfMoneyStr), 1, "*").doubleValue();
				System.out.println("czMoney:"+czRecord.getMoney()+" ==========bfMoneyStr:"+bfMoneyStr+"=========== 返回： bfMoney:"+bfMoney);
				
				if(czMoney != bfMoney){
					//前后充值金额不一致;被抓包修改过数据;
					//处理机制: 添加黑名单,修复充值数据;永久封号;
					String username = DESEncrypt.jieMiUsername(czRecord.getUsers().getUsername());
					String note = "充值金额不一致;修复前金额:"+(czMoney*0.01)+"元";
					ExcpCzUsers excp = new ExcpCzUsers(czRecord.getUsersId(),username,czMoney,bfMoney,"充值金额不一致");
					//添加为异常充值用户;
					scanExcpCzUsersThreadBean.saveOrUpdate(excp);
					//修复充值数据;
					scanExcpCzUsersThreadBean.updateCzRecord(czRecord.getId(),null, bfMoney, uuid, "9999", note);
					//添加进黑名单 永久封号
					blackListBean.saveBlackList(username, null, "篡改充值金额(永久封号)-【线程扫描】", null);
					return username+"充值成功---但-篡改充值金额(永久封号)";
				}else{
					//充值金额相等;
					scanExcpCzUsersThreadBean.updateCzRecord(czRecord.getId(),null, null, uuid, null, null);
					return "充值成功";
				}
			}else{
				//订单号和流水号被篡改;此记录列为异常充值;
				log.info("查询充值记录时,被篡改数据;");
				scanExcpCzUsersThreadBean.updateCzRecord(czRecord.getId(),"2", null, uuid, "8888", "查询充值记录时,被篡改数据;");
				return "查询充值记录时,被篡改数据;";
			}
		}else{
			if("FI00002".equalsIgnoreCase(respCode)){
				//交易结果未知，请稍后查询
				scanExcpCzUsersThreadBean.updateCzRecord(czRecord.getId(),null, null, null, "7777", "交易结果未知,请稍后查询");
				return "交易结果未知，请稍后查询";
			}else if("FI00007".equalsIgnoreCase(respCode)){
				//交易失败
				scanExcpCzUsersThreadBean.updateCzRecord(czRecord.getId(),"2", null, uuid, "6666", "交易失败");
				return "交易失败";
			}else if("FI00014".equalsIgnoreCase(respCode)){
				//订单不存在
				scanExcpCzUsersThreadBean.updateCzRecord(czRecord.getId(),"2", null, uuid, "7777", "订单不存在");
				return "订单不存在";
			}else if("FI00054".equalsIgnoreCase(respCode)){
				//订单未支付
				scanExcpCzUsersThreadBean.updateCzRecord(czRecord.getId(),"2", null, uuid, "7777", "订单未支付");
				return "订单未支付";
			}else{
				//未知异常
				scanExcpCzUsersThreadBean.updateCzRecord(czRecord.getId(),"2", null, uuid, "7777", "充值未知异常");
				return "充值未知异常";
			}
		}
	}
}
