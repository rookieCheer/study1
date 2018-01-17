package com.huoq.admin.product.action;

import java.io.IOException;

import javax.annotation.Resource;

import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;

import com.huoq.common.action.BaseAction;
import com.huoq.common.util.QwyUtil;
import com.huoq.thread.action.AutoSendCouponThread;
import com.huoq.thread.action.AutoSendRatesThread;
import com.huoq.thread.action.AutoShiftTohread;
import com.huoq.thread.action.AutoStatsOperateThread;
import com.huoq.thread.action.CheckProductStatusThread;
import com.huoq.thread.action.FinishCouponThread;
import com.huoq.thread.action.FinishInterestCouponThread;
import com.huoq.thread.action.FixedOrderThread;
import com.huoq.thread.action.IphoneOperateThread;
import com.huoq.thread.action.MeowCurrencyReportThread;
import com.huoq.thread.action.PutOnSaleProductThread;
import com.huoq.thread.action.RepairProUsersMoneyThread;
import com.huoq.thread.action.ScanExcpCzUsersThread;
import com.huoq.thread.action.SendCoinPurseRatesThread;
import com.huoq.thread.action.SendCzCouponThread;
import com.huoq.thread.action.SendFreshmanMessegeThread;
import com.huoq.thread.action.SendGdpThread;
import com.huoq.thread.action.SendInviteEarnThread;
import com.huoq.thread.action.SendJLThread;
import com.huoq.thread.action.SendMcoinThread;
import com.huoq.thread.action.SendMoneyToTopDayThread;
import com.huoq.thread.action.SendProfitThread;
import com.huoq.thread.action.SendRichThread;
import com.huoq.thread.action.SendsmsThread;
import com.huoq.thread.action.TxRequestThread;
import com.huoq.thread.action.UpdateProductThread;
import com.huoq.thread.action.UpdateQdtjPlatformThread;
import com.huoq.thread.action.UpdateQdtjThread;
import com.huoq.thread.action.UpdateUsersInfoThread;
import com.huoq.thread.bean.UpdateQdtjPlatformThreadBean;
import com.huoq.thread.bean.UpdateQdtjThreadBean;

/**
 * 后台管理--启动线程
 * 
 * @author qwy
 *
 * @createTime 2015-4-27下午3:51:34
 */
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/Admin")
// 发布产品页面
@Results({ @Result(name = "thread", value = "/Product/Admin/functionManager/function.jsp") })
public class StartThreadAction extends BaseAction {
	/**
	 * 结算理财产品;
	 */
	@Resource
	private UpdateProductThread updateProductThread;
	/**
	 * 扫描过期投资券;
	 */
	@Resource
	private FinishCouponThread finishCouponThread;
	/**
	 * 扫描过期投资券;
	 */
	@Resource
	private FinishInterestCouponThread finishInterestCouponThread;
	/**
	 * 发放收益;
	 */
	@Resource
	private SendProfitThread sendProfitThread;

	/**
	 * 发放邀请投资奖励
	 */
	@Resource
	private SendInviteEarnThread sendInviteEarnThread;

	@Resource
	private SendMcoinThread sendMcoinThread;

	@Resource
	private MeowCurrencyReportThread sendMeowCurrency;

	/**
	 * 提现查询接口;
	 */
	@Resource
	private TxRequestThread txRequestThread;
	/**
	 * 更新用户信息
	 */
	@Resource
	private UpdateUsersInfoThread updateUsersInfoThread;

	/**
	 * 自动计算收益线程
	 */
	@Resource
	private AutoSendRatesThread autoSendRatesThread;

	/**
	 * 自动发放收益
	 */
	@Resource
	private SendCoinPurseRatesThread coinPurseRatesThread;

	/**
	 * 自动转入
	 */
	@Resource
	private AutoShiftTohread autoShiftTohread;
	/**
	 * 发送新手投资到期短信
	 */
	@Resource
	private SendFreshmanMessegeThread sendFreshmanMessegeThread;

	/**
	 * 每日统计线程
	 */
	@Resource
	private AutoStatsOperateThread autoStatsOperateThread;

	/**
	 * 每日单个渠道数据统计线程（安卓）
	 */
	@Resource
	private UpdateQdtjThread updateQdtjThread;
	/**
	 * 更新各个渠道昨天的数据;
	 */
	@Resource
	private UpdateQdtjPlatformThread updateQdtjPlatformThread;
	/*@Resource
	private ChannelOperateThread channelOperateThread;*/
	/**
	 * 每日单个渠道数据统计线程（安卓）
	 */
	@Resource
	private IphoneOperateThread iphoneOperateThread;

	/**
	 * 自动虚标线程
	 */
	@Resource
	private CheckProductStatusThread checkProductStatusThread;
	/**
	 * 扫描是否需要自动续约的理财产品 add by yks 2016-09-25
	 */
	@Resource
	private PutOnSaleProductThread putOnSaleProductThread;

	/**
	 * 固定线程入口
	 */
	@Resource
	private FixedOrderThread fixedOrderThread;
	
	/**
	 * 扫描异常充值用户
	 */
	@Resource
	private ScanExcpCzUsersThread scanExcpCzUsersThread;

	/**
	 * 未投资用户发送短信
	 */
	@Resource
	private SendsmsThread sendsmsThread;
	
	/**
	 * 土豪星球奖励共享
	 */
	@Resource
	private SendRichThread sendRichThread;
	
	/**
	 * 修复异常资金
	 */
	@Resource
	private RepairProUsersMoneyThread repairProUsersMoneyThread;
	
	/**
	 * 新手每日最高奖励
	 */
	@Resource
	private SendMoneyToTopDayThread sendMoneyToTopDayThread;
	
	/**
	 * 返款用户奖励
	 */
	@Resource
	private AutoSendCouponThread autoSendCouponThread;
	
	/**
	 * 发放枫叶日收益加成奖励
	 */
	@Resource
	private SendGdpThread sendGdpThread;
	
	/**
	 * 发送奖励线程
	 */
	@Resource
	private SendJLThread sendJLThread;
	
	/**
	 * 充值达标送理财券
	 */
	@Resource
	private SendCzCouponThread sendCzCouponThread;
	
	/**
	 * 启动所有线程;
	 * 
	 * @return
	 */
	public String startAllThread() {
		String json = "";
		try {
			// 更新理财产品;包括:更新之后,结算常规的理财产品和新手理财产品;
			new Thread(updateProductThread).start();
			Thread.sleep(2000);
			// 扫描过期投资券;
			new Thread(finishCouponThread).start();
			Thread.sleep(1000);
			// 扫描过期加息券;
			new Thread(finishInterestCouponThread).start();
			Thread.sleep(1000);
			// 发放收益;
			new Thread(sendProfitThread).start();
			json = QwyUtil.getJSONString("ok", "启动成功");
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("error", "启动失败");
		}
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			log.error("操作异常: ",e);
		}
		return null;
	}

	/**
	 * 启动更新理财产品和结算理财产品的线程
	 * 
	 * @return
	 */
	public String startUpdateProductThread() {
		String json = "";
		try {
			// 更新理财产品;包括:更新之后,结算常规的理财产品和新手理财产品;
			new Thread(updateProductThread).start();
			json = QwyUtil.getJSONString("ok", "启动成功");
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("error", "启动失败");
		}
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			log.error("操作异常: ",e);
		}
		return null;
	}

	/**
	 * 发送瞄币的线程
	 * 
	 * @return
	 */

	public String sendMcoinThread() {
		String json = "";
		try {
			// 更新理财产品;包括:更新之后,结算常规的理财产品和新手理财产品;
			new Thread(sendMcoinThread).start();
			json = QwyUtil.getJSONString("ok", "启动成功");
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("error", "启动失败");
		}
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			log.error("操作异常: ",e);
		}
		return null;

	}

	/**
	 * 邀请好友投资奖励
	 * 
	 * @return
	 */

	public String sendInviteEarnThread() {
		String json = "";
		try {
			new Thread(sendInviteEarnThread).start();
			json = QwyUtil.getJSONString("ok", "启动成功");
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("error", "启动失败");
		}
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			log.error("操作异常: ",e);
		}
		return null;

	}

	public String meowCurrencyReport() {
		String json = "";
		try {
			new Thread(sendMeowCurrency).start();
			json = QwyUtil.getJSONString("ok", "启动成功");

		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("error", "启动失败");
		}
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			log.error("操作异常: ",e);
		}
		return null;
	}

	/**
	 * 启动发放收益线程
	 * 
	 * @return
	 */
	public String startSendProfitThread() {
		String json = "";
		try {
			// 发放收益;
			new Thread(sendProfitThread).start();
			json = QwyUtil.getJSONString("ok", "启动成功");
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("error", "启动失败");
		}
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			log.error("操作异常: ",e);
		}
		return null;
	}

	/**
	 * 启动扫描过期投资券线程
	 * 
	 * @return
	 */
	public String startFinishCouponThread() {
		String json = "";
		try {
			// 扫描过期投资券;
			new Thread(finishCouponThread).start();
			json = QwyUtil.getJSONString("ok", "启动成功");
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("error", "启动失败");
		}
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			log.error("操作异常: ",e);
		}
		return null;
	}

	/**
	 * 启动扫描过期投资券线程
	 * 
	 * @return
	 */
	public String startFinishInterestCouponThread() {
		String json = "";
		try {
			// 扫描过期投资券;
			new Thread(finishInterestCouponThread).start();
			json = QwyUtil.getJSONString("ok", "启动成功");
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("error", "启动失败");
		}
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			log.error("操作异常: ",e);
		}
		return null;
	}

	/**
	 * 启动查询接口记录的线程;
	 * 
	 * @return
	 */
	public String startTxQueryThread() {
		String json = "";
		try {
			// 提现查询接口;
			//request.getSession().setAttribute("txRequest", getRequest());
			new Thread(txRequestThread).start();
			json = QwyUtil.getJSONString("ok", "启动成功");
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("error", "启动失败");
		}
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			log.error("操作异常: ",e);
		}
		return null;
	}

	/**
	 * 启动更新用户信息的线程
	 * 
	 * @return
	 */
	public String startUpdateUsersInfoThread() {
		String json = "";
		try {
			// 更新用户信息；包括用户性别，年龄，生日
			new Thread(updateUsersInfoThread).start();
			json = QwyUtil.getJSONString("ok", "启动成功");
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("error", "启动失败");
		}
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			log.error("操作异常: ",e);
		}
		return null;
	}

	/**
	 * 自动计算收益线程
	 * 
	 * @return
	 */
	public String startAutoSendRatesThread() {
		String json = "";
		try {
			// 更新用户信息；包括用户性别，年龄，生日
			new Thread(autoSendRatesThread).start();
			json = QwyUtil.getJSONString("ok", "启动成功");
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("error", "启动失败");
		}
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			log.error("操作异常: ",e);
		}
		return null;
	}

	/**
	 * 启动 自动发放收益的线程
	 * 
	 * @return
	 */
	public String startSendCoinPurseRatesThread() {
		String json = "";
		try {
			// 更新用户信息；包括用户性别，年龄，生日
			new Thread(coinPurseRatesThread).start();
			json = QwyUtil.getJSONString("ok", "启动成功");
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("error", "启动失败");
		}
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			log.error("操作异常: ",e);
		}
		return null;
	}

	/**
	 * 自动转入
	 * 
	 * @return
	 */
	public String startAutoShiftTohread() {
		String json = "";
		try {
			// 更新用户信息；包括用户性别，年龄，生日
			new Thread(autoShiftTohread).start();
			json = QwyUtil.getJSONString("ok", "启动成功");
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("error", "启动失败");
		}
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			log.error("操作异常: ",e);
		}
		return null;
	}

	/**
	 * 发送新手投资到期短信
	 * 
	 * @return
	 */
	public String startSendFreshmanMessegeThread() {
		String json = "";
		try {
			// 发动短信
			new Thread(sendFreshmanMessegeThread).start();
			json = QwyUtil.getJSONString("ok", "启动成功");
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("error", "启动失败");
		}
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			log.error("操作异常: ",e);
		}
		return null;
	}


	public String startCurrEveryDayStatsOperate() {
		String json = "";
		try {
			autoStatsOperateThread.setInDayStr("today");
			// 更新用户信息；包括用户性别，年龄，生日
			new Thread(autoStatsOperateThread).start();
			json = QwyUtil.getJSONString("ok", "启动更新成功");
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("error", "启动更新成功");
		}
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			log.error("操作异常: ",e);
		}
		return null;
	}

	public String startEveryDayStatsOperate() {
		String json = "";
		try {
			autoStatsOperateThread.setInDayStr("all");
			new Thread(autoStatsOperateThread).start();
			json = QwyUtil.getJSONString("ok", "启动成功");
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("error", "启动失败");
		}
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			log.error("操作异常: ",e);
		}
		return null;
	}
	
	/**
	 * 更新各个渠道昨天的数据;
	 * 
	 * @return
	 */
	public String updateQdtjPlatform() {
		String json = "";
		try {
			new Thread(updateQdtjPlatformThread).start();
			json = QwyUtil.getJSONString("ok", "启动成功");
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("error", "启动失败");
		}
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			log.error("操作异常: ",e);
		}
		return null;
	}

	/**
	 * Android渠道统计汇总表（安卓）
	 * 
	 * @return
	 */
	public String startEveryDayChannelOperate() {
		String json = "";
		try {
			// 更新单个渠道数据统计线程
			new Thread(updateQdtjThread).start();
			json = QwyUtil.getJSONString("ok", "启动成功");
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("error", "启动失败");
		}
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			log.error("操作异常: ",e);
		}
		return null;
	}
	/**
	 * 启动更新单个渠道数据统计线程（IOS）
	 * 
	 * @return
	 */
	public String startEveryDayIphoneOperate() {
		String json = "";
		try {
			// 更新单个渠道数据统计线程(IOS)
			new Thread(iphoneOperateThread).start();
			json = QwyUtil.getJSONString("ok", "启动成功");
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("error", "启动失败");
		}
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			log.error("操作异常: ",e);
		}
		return null;
	}

	/**
	 * 启动自动发布理财产品线程
	 * @author yks
	 * @date 2016-09-25
	 * @return
	 */
	public String autoReleaseProductOperate() {
		String json = "";
		try {
			new Thread(putOnSaleProductThread).start();
			json = QwyUtil.getJSONString("ok", "启动成功");
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("error", "启动失败");
		}
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			log.error("操作异常: ",e);
		}
		return null;
	}


	
	@Resource
	private UpdateQdtjThreadBean updateQdtjThreadBean;
	
	/**全部重新更新一遍;
		一般用于初始化qdtj表
	 * @return
	 */
	public String updateQdtjAll() {
		String json = "";
		try {
			if("true".equals(request.getParameter("isReal"))){
				updateQdtjThreadBean.updateQdtjAll();
				json = QwyUtil.getJSONString("ok", "执行完毕");
			}else{
				json = QwyUtil.getJSONString("err", "错误!");
			}
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("error", "启动失败");
		}
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			log.error("操作异常: ",e);
		}
		return null;
	}
	@Resource
	private UpdateQdtjPlatformThreadBean updateQdtjPlatformThreadBean;
	
	/**全部重新更新一遍;
		一般用于初始化QdtjPlatform表
	 * @return
	 */
	public String updateQdtjPlatformAll() {
		String json = "";
		try {
			if("true".equals(request.getParameter("isReal"))){
				updateQdtjPlatformThreadBean.updateQdtjAll();
				json = QwyUtil.getJSONString("ok", "执行完毕");
			}else{
				json = QwyUtil.getJSONString("err", "错误!");
			}
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("error", "启动失败");
		}
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			log.error("操作异常: ",e);
		}
		return null;
	}
	
	
	
	
	/**
	 * 固定线程入口
	 *
	 * @return
	 */
	public String FixedOrderThread() {
		String json = "";
		try {
			new Thread(fixedOrderThread).start();
			json = QwyUtil.getJSONString("ok", "启动成功");
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("error", "启动失败");
		}
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			log.error("操作异常: ",e);
		}
		return null;
	}
	/**扫描异常充值用户线程;
	 * @return  
	 */
	public String ScanExcpCzUsersThread(){
		String json = "";
		try {
			new Thread(scanExcpCzUsersThread).start();
			json = QwyUtil.getJSONString("ok", "启动成功");
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("error", "启动失败");
		}
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			
			log.error("操作异常: ",e);
		}
		return null;
	}
	
	/**
	 * 未投资用户发送短信
	 * @return
	 */
	public String SendsmsThread(){
		String json = "";
		try {
			new Thread(sendsmsThread).start();
			json = QwyUtil.getJSONString("ok", "启动成功");
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("error", "启动失败");
		}
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			
			log.error("操作异常: ",e);
		}
		return null;
	}
	
	/**
	 * 土豪星球奖励共享
	 * @return
	 */
	public String SendRichThread(){
		String json = "";
		try {
			new Thread(sendRichThread).start();
			json = QwyUtil.getJSONString("ok", "启动成功");
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("error", "启动失败");
		}
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			
			log.error("操作异常: ",e);
		}
		return null;
	}
	
	/**
	 * 修复异常资金账户 
	 * @return
	 */
	public String RepairUsersMoney(){
		String json = "";
		try {
			new Thread(repairProUsersMoneyThread).start();
			json = QwyUtil.getJSONString("ok", "启动成功");
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("error", "启动失败");
		}
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			
			log.error("操作异常: ",e);
		}
		return null;
	}
	
	/**
	 * 新手每日最高奖励线程 
	 * @return
	 */
	public String TopDayInveste(){
		String json = "";
		try {
			//new Thread(sendMoneyToTopDayThread).start();
			json = QwyUtil.getJSONString("ok", "启动成功");
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("error", "启动失败");
		}
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			
			log.error("操作异常: ",e);
		}
		return null;
	}
	
	/**
	 * 返款用户奖励线程
	 * @return
	 */
	public String AutoSendCoupon(){
		String json = "";
		try {
			new Thread(autoSendCouponThread).start();
			json = QwyUtil.getJSONString("ok", "启动成功");
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("error", "启动失败");
		}
		
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return null;
	}
	
	/**
	 * GDP奖励
	 * @return
	 */
	public String sendGdpThread(){
		String json = "";
		try {
			new Thread(sendGdpThread).start();
			json = QwyUtil.getJSONString("ok", "启动成功");
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("error", "启动失败");
		}
		
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return null;
	}
	
	/**
	 * 发送奖励线程
	 * @return
	 */
	public String sendRewardMoney(){
		String json = "";
		try {
			new Thread(sendJLThread).start();
			json = QwyUtil.getJSONString("ok", "启动成功");
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("error", "启动失败");
		}
		
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return null;
	}
	
	/**
	 * 充值达标后送理财券线程
	 * @return
	 */
	public String sendCzCoupon(){
		String json = "";
		try {
			new Thread(sendCzCouponThread).start();
			json = QwyUtil.getJSONString("ok", "启动成功");
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("error", "启动失败");
		}
		
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return null;
	}
}
