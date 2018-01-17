package com.huoq.thread.action;

import com.huoq.common.ApplicationContexts;
import com.huoq.common.util.QwyUtil;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**每天定点执行的一些线程;而且线程是有序性;需等上一个执行完才执行下一个;
 * @author 覃文勇
 *
 * 2017年1月16日下午9:25:02
 */
@Service
public class FixedOrderThread implements Runnable{
	private Logger log = Logger.getLogger(FixedOrderThread.class);
	/**
	 * 发放理财产品收益线程;
	 */
	@Resource
	private SendProfitThread sendProfitThread;

//	@Resource
//	private SendNotifyProdOverThread sendNotifyProdOverThread;

	/**
	 * 产品到期返回金额到账通知短信
	 */
//	@Resource
//	private SendNotifySendProfitThread sendNotifySendProfitThread;

	/**
	 * 发放零钱包收益线程
	 */
	@Resource
	private SendCoinPurseRatesThread coinPurseRatesThread;
	
	/**
	 * 更新理财产品线程
	 */
	@Resource
	private UpdateProductThread updateProductThread;
	
	/**
	 * 扫描处理过期理财券
	 */
	@Resource
	private FinishCouponThread finishCouponThread;

	/**
	 * 更新Android渠道统计汇总表
	 */
	@Resource
	private UpdateQdtjThread updateQdtjThread;
	
	/**
	 * 更新各平台渠道统计汇总表
	 */
	@Resource
	private UpdateQdtjPlatformThread updateQdtjPlatformThread;



	@Override
	public void run() {
		long st =  System.currentTimeMillis();
		try {
			log.info("--------开始执行【固定线程总入口】---当前时间: "+QwyUtil.fmyyyyMMddHHmmss.format(new Date()));
			//每一个线程都是一个单独的个体,出异常也不能影响其它线程;所以每个线程加上try-catch
			try {
				Thread upt = new Thread(updateProductThread);
				upt.start();
				upt.join();
			} catch (Exception e) {
				log.error(e);
			}
			try {
				Thread spt = new Thread(sendProfitThread);
				spt.start();
				spt.join();
			} catch (Exception e) {
				log.error(e);
			}
			try {
				Thread crt = new Thread(coinPurseRatesThread);
				crt.start();
				crt.join();
			} catch (Exception e) {
				log.error(e);
			}
			
			try {
				
				Thread fct = new Thread(finishCouponThread);
				fct.start();
				fct.join();
			} catch (Exception e) {
				log.error(e);
			}
			try {
				Thread qdtj = new Thread(updateQdtjThread);
				qdtj.start();
				qdtj.join();
			} catch (Exception e) {
				log.error(e);
			}
			try {
				Thread flst = new Thread(updateQdtjPlatformThread);
				flst.start();
				flst.join();
			} catch (Exception e) {
				log.error(e);
			}

		} catch (Exception e) {
			log.error("操作异常: ",e);
			log.error(e);
		}
		long et = System.currentTimeMillis();
		log.info("---------总固定线程入口执行完毕,耗时: "+(et-st));
	}
	
	/**线程日志;
	 * @param title 主题;例如 "InterestDetails表【已续投还款】"
	 * @param pageCount 一开始的总条数;
	 * @param count 一开始的总页数;
	 * @param leftSize 变动的剩余条数;
	 * @param currentPage 变动的当前页;
	 * @return
	 */
	public static String pageUtilLog(String title,int pageCount,int count,int leftSize,int currentPage){
		StringBuffer sb = new StringBuffer();
		sb.append(title);
		sb.append("共: ");
		sb.append(pageCount);
		sb.append(" 页;共: ");
		sb.append(count);
		sb.append(" 条数据需要处理;剩余: ");
		sb.append(leftSize);
		sb.append(" 条;当前第: ");
		sb.append(currentPage + "页.");
		return sb.toString();
	}
	
	
	
	public static void main(String[] args) throws InterruptedException {
		ApplicationContext context = ApplicationContexts.getContexts();
		FixedOrderThread f = (FixedOrderThread)context.getBean("fixedOrderThread");
		Thread a = new Thread(f);
		a.start();
		a.join();
		System.exit(0);
	}

}
