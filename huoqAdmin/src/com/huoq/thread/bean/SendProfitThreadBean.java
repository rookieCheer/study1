package com.huoq.thread.bean;

import com.huoq.common.bean.MyWalletBean;
import com.huoq.common.bean.SMSNoticeBean;
import com.huoq.common.bean.SMSNotifyBean;
import com.huoq.common.bean.SystemConfigBean;
import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.LockHolder;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.InterestDetails;
import com.huoq.orm.Investors;
import com.huoq.orm.Product;
import com.huoq.orm.Users;
import com.huoq.thread.dao.ThreadDAO;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * 后台线程Bean层--结算理财产品(非新手专享产品,即product.productType=0的理财产品)
 * 
 * @author qwy
 *
 * @createTime 2015-4-28上午9:54:11
 */
@Service
public class SendProfitThreadBean {
	private Logger log = Logger.getLogger(SendProfitThreadBean.class);
	@Resource
	private ThreadDAO dao;
	@Resource
	private MyWalletBean myWalletBean;
	@Resource
	private SystemConfigBean configBean;
	@Resource
	SMSNotifyBean smsNotifyBean;
	@Resource
	SMSNoticeBean smsNoticeBean;
	
	/**
	 * 更新理财产品Bean层;
	 */
	@Resource
	private UpdateProductThreadBean updateProductThreadBean;
	
    private static ResourceBundle resbSms = ResourceBundle.getBundle("sms-notice");


	/**
	 * 查找理财产品;分页;<br>
	 * 按照 ORDER BY insertTime ASC, orders ASC ,productId ASC 来排序
	 * 
	 * @param pageUtil
	 *            分页对象;
	 * @param status
	 *            状态 0未支付,1已冻结,2已支付,3已删除
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public PageUtil<InterestDetails> getInterestDetailsByPageUtil(PageUtil<InterestDetails> pageUtil, String[] status) {
		StringBuffer hql = new StringBuffer();
		hql.append(" FROM InterestDetails inde ");
		hql.append(" WHERE inde.returnTime<=now() ");
		if (!QwyUtil.isNullAndEmpty(status)) {
			String myStatus = QwyUtil.packString(status);
			hql.append(" AND inde.status IN (" + myStatus + ") ");
		}
		hql.append(" ORDER BY inde.updateTime ASC,inde.insertTime ASC ");
		return (PageUtil<InterestDetails>) dao.getPage(pageUtil, hql.toString(), null);

	}

	/**
	 * 获取结算的投资记录集合
	 * @param pageUtil
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public PageUtil<Investors> getInvestorsByPageUtil(PageUtil<Investors> pageUtil) {
		StringBuffer hql = new StringBuffer();
		hql.append(" FROM Investors ");
		hql.append(" WHERE investorStatus = 3 ");
		hql.append(" AND DATE_FORMAT(clearTime,'%Y-%m-%d') =  '"+QwyUtil.fmyyyyMMdd.format(new Date())+"' ");
		return dao.getPage(pageUtil, hql.toString(), null);

	}
	/**
	 * 获取到期产品发息集合
	 * @param pageUtil
	 * @param status
	 * @return
	 */
	public PageUtil<InterestDetails> getFinishInterestDetailsByPageUtil(PageUtil<InterestDetails> pageUtil, String[] status) {
		StringBuffer hql = new StringBuffer();
		hql.append(" FROM InterestDetails inde ");
		hql.append(" WHERE inde.finishTime<=now() ");
		if (!QwyUtil.isNullAndEmpty(status)) {
			String myStatus = QwyUtil.packString(status);
			hql.append(" AND inde.status IN (" + myStatus + ") ");
		}
		System.out.println("获取到期集合："+hql.toString());
		return (PageUtil<InterestDetails>) dao.getPage(pageUtil, hql.toString(), null);
	}

	/**
	 * 根据利息表发放收益;
	 * 
	 * @param inde
	 *            InterestDetails 利息表
	 * @param interestDetailsNote
	 *            备注
	 * @param smsTip
	 *            短信广告语
	 * @return
	 */
	public String sendProfit(InterestDetails inde, String interestDetailsNote, String smsTip) {
		synchronized (LockHolder.getLock(inde.getUsersId())) {
			String num = QwyUtil.getRandomFourNumber();
			log.info(num+": 发放UsersId: "+inde.getUsersId()+" 的收益");
			if (QwyUtil.isNullAndEmpty(inde)) {
				return "sendProfitFreshmanThreadBean.sendProfit InterestDetails";
			}
			Investors inv = null;
			inde = (InterestDetails) dao.findById(new InterestDetails(), inde.getId());
//			if (!"0".equals(inde.getStatus())) {
//				return "利息表不在支付状态,InterestDetails.id: " + inde.getId();
//			}
			if (new Date().getTime() < inde.getReturnTime().getTime()) {
				return "还没到发放收益的时间" + QwyUtil.fmyyyyMMddHHmmss.format(inde.getReturnTime());
			}
			inv = (Investors) dao.findById(new Investors(), inde.getInvestorsId());
			if(QwyUtil.isNullAndEmpty(inv)){
				log.info(num+": 发放失败,没有投资列表,利息id: "+ inde.getInvestorsId());
				return "发放失败,没有投资列表";
			}
			if (inde.getPayMoney().doubleValue() >= 0) {
				if (inv != null && "1".equals(inv.getInvestorStatus())) {
					inv.setInvestorStatus("2");
					inv.setClearTime(new Date());
					dao.saveOrUpdate(inv);
				}
			}
			Product product = inde.getProduct();
			try {
				//产品在结算中并且利息表未支付
				if("2".equals(inv.getInvestorStatus())&&"0".equals(inde.getStatus())){
					// 更新利息表
					inde.setAlreadyPay(QwyUtil.calcNumber(inde.getPayMoney(), inde.getPayInterest(), "+").doubleValue());
					inde.setAlreadyPayDay(inde.getPayDay());
					inde.setStatus("2");// 已支付;
					inde.setNote(interestDetailsNote);
					inde.setUpdateTime(new Date());
					dao.saveOrUpdate(inde);
					//更新投资记录
					inv.setInvestorStatus("3");
					double shouyi = QwyUtil.isNullAndEmpty(inv.getAllShouyi()) ? 0d : inv.getAllShouyi();
					inv.setAllShouyi(QwyUtil.calcNumber(shouyi, inde.getPayInterest(), "+").doubleValue());
					inv.setClearMoney(inv.getAllShouyi());
					dao.saveOrUpdate(inv);

					boolean isOkInv=false;
					//发放投资收益
					StringBuffer note = new StringBuffer();
					note.append("购买 " + product.getTitle());
					note.append(" 理财产品获得的收益");
					//isOkInv=myWalletBean.sendInterest(inde.getUsersId(), inde.getPayInterest(), note.toString());线上再用
					//2.4版本使用
					isOkInv=myWalletBean.sendInterest_new2(inde.getUsersId(), inde.getPayInterest(),0d, note.toString());
					log.info("uid:"+inv.getUsersId()+"利息是否成功发放："+isOkInv);
					if (inde.getPayMoney().doubleValue() > 0 && isOkInv) {
						//归还投资本金;
						StringBuffer noteInv = new StringBuffer();
						noteInv.append("返款购买 " + product.getTitle());
						noteInv.append(" 理财产品时的本金");
						boolean isOkpay=myWalletBean.backPayMoney_new(inv.getUsersId(), inv.getId(), noteInv.toString());
						log.info("uid:"+inv.getUsersId()+"本金是否成功发放："+isOkpay);
					}

              		return "";
				}else{
					log.info("investorsId:【"+inv.getId()+"】 状态不对;当前状态: "+inv.getInvestorStatus());
				}
			} catch (Exception e) {
				log.error("操作异常: ", e);
				log.info(num+" sendProfitFreshmanThreadBean.sendProfit 数据回滚: 结算理财产品异常;");
				return "sendProfitFreshmanThreadBean.sendProfit 数据回滚: 结算理财产品异常;";
			}
		}
		return "";
	}


	public void saveOrUpdate(Object obj) {
		dao.saveOrUpdate(obj);
	}

	/**
	 * 投资产品到期提醒
	 */
	public void notifyUserProductFinish(InterestDetails inde){
		log.info("======================发送投资产品到期短信提醒=======================");
		synchronized (LockHolder.getLock(inde.getUsersId())) {
			Investors inv = (Investors) dao.findById(new Investors(), inde.getInvestorsId());
			if (!QwyUtil.isNullAndEmpty(inv)) {
				if (inde.getPayMoney().doubleValue() >= 0) {
					// TODO: 发送短信提醒，提醒次日钱款到账，建议用一个Thread进行发送，不造成结算现成阻塞
					Date payTime = inv.getPayTime();
					Calendar cal = Calendar.getInstance();
					cal.setTime(payTime);
					String year = String.valueOf(cal.get(Calendar.YEAR));
					String month = String.valueOf(cal.get(Calendar.MONTH) + 1);
					String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
					String productId = inv.getProductId();
					String shortUrl = resbSms.getString("SMS_PRODUCT_FINISHDAY_URL");
					shortUrl = shortUrl.format(shortUrl, productId);
					String sendMessage = resbSms.getString("SMS_PRODUCT_FINISHDAY");

					String productTitle = "";
					Product product = (Product) dao.findById(new Product(), productId);
					if (!QwyUtil.isNullAndEmpty(product)) {
						productTitle = product.getTitle();
					}
					//还款金额
					Double backMoney = inv.getInMoney();
					backMoney = backMoney / 100;
					Users users = (Users) dao.findById(new Users(), inv.getUsersId());

					Object[] obj = new Object[]{year, month, day, productTitle, backMoney};
					smsNoticeBean.sendSMSInThreadPool(shortUrl, DESEncrypt.jieMiUsername(users.getUsername()), sendMessage, obj);
				}
			}
		}
	}


	public static void main(String[] args) {
		String str = "数据回滚: 结算理财产品异常%s%s%s";
		String str1 = String.format(str, new String[]{"11","22"});
		System.out.println(str1);
	}

	/**
	 * 进行短信通知, 告知钱款已到账,
	 * @param investors
	 */
	public void sendNotifySendProfit(Investors investors) {
		log.info("======================发送投资产品到期短信提醒=======================");
		synchronized (LockHolder.getLock(investors.getUsersId())) {
	//		// TODO: 进行短信通知, 告知钱款已到账, 建议用一个Thread进行发送，不造成结算现成阻塞
			String productTitle = "";
			Product product = (Product) dao.findById(new Product(), investors.getProductId());
			if (!QwyUtil.isNullAndEmpty(product)) {
				productTitle = product.getTitle();
			}
			String shortUrl = resbSms.getString("SMS_PRODUCT_INTO_ACCOUNT_URL");
			String sendMessage = resbSms.getString("SMS_PRODUCT_INTO_ACCOUNT");
			//还款金额
			Double backMoney = investors.getInMoney();
			backMoney = backMoney/100;
			Users users = (Users) dao.findById(new Users(), investors.getUsersId());
			smsNoticeBean.sendSMSInThreadPool(shortUrl,DESEncrypt.jieMiUsername(users.getUsername()), sendMessage, new Object[]{productTitle,backMoney});
		}
	}
}
