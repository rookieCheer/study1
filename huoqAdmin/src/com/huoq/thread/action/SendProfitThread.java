package com.huoq.thread.action;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.InterestDetails;
import com.huoq.orm.SystemConfig;
import com.huoq.thread.bean.SendProfitThreadBean;
import com.huoq.thread.dao.ThreadDAO;

/**后台线程View层--对用户进行收益的发放;<br>
 * 对利息表进行按要求发放收益;
 * @author qwy
 *
 * @createTime 2015-05-18 04:44:39
 */
@Service
public class SendProfitThread implements Runnable {
	private Logger log = Logger.getLogger(SendProfitThread.class);
	private Integer pageSize = 50;
	@Resource
	private SendProfitThreadBean bean;
	@Resource
	private ThreadDAO threadDAO;
	@Override
	public void run() {
		long st = System.currentTimeMillis();
		final SystemConfig systemConfig = threadDAO.getSystemConfig();
		int currentPage = 0;
		int totalPage = 0;
		int totalSize = 0;
		int leftSize = 0;
		try {
			
			log.info("进入后台线程----对收益表InterestDetails进行按要求发放;当前时间:"+QwyUtil.fmyyyyMMddHHmmss.format(new Date()));
			PageUtil<InterestDetails> pageUtil = new PageUtil<InterestDetails>();
			pageUtil.setPageSize(pageSize);
			String[] status = {"0","1"};//状态 0未支付,1已冻结,2已支付,3已删除
			for (;;) {
				if(currentPage>100){
					log.info("-----------安全机制------达到100页自动退出,防止死循环,下次需手动启动线程-------");
					break;
				}
				currentPage++;
				pageUtil.setCurrentPage(1);
				pageUtil = bean.getInterestDetailsByPageUtil(pageUtil, status);
				List<InterestDetails> listInterestDetails = pageUtil.getList();
				if(QwyUtil.isNullAndEmpty(listInterestDetails) || (totalPage!=0 && currentPage>totalPage)){
					leftSize = pageUtil.getCount();//结束时 剩余的条数;
					log.info("发放收益InterestDetails结束,当前页数: "+currentPage);
					break;
				}
				totalPage = totalPage==0?pageUtil.getPageCount():totalPage;
				totalSize = totalSize==0?pageUtil.getCount():totalSize;
				String logInfo = FixedOrderThread.pageUtilLog("InterestDetails表【产品利息表】", totalPage, totalSize, pageUtil.getCount(), currentPage);
				log.info(logInfo);
				for (InterestDetails interestDetails : listInterestDetails) {
					//发放收益
					String temp = bean.sendProfit(interestDetails,"线程定时自动发放收益",systemConfig.getSmsTip());
					if(!QwyUtil.isNullAndEmpty(temp))
					log.info(interestDetails.getId()+" 发放收益的结果: "+temp);
				}


			}
		} catch (Exception e) {
			log.error("操作异常: ",e);
			log.error("发放收益的后台线程异常: ",e);
		}
		if(leftSize>=pageSize){
			try {
				Thread a = new Thread(this);
				a.start();
				a.join();
			} catch (InterruptedException e) {
				log.error("操作异常: ",e);
			}
		}
		long et = System.currentTimeMillis();
		log.info("发放【理财产品】收益InterestDetails耗时: "+(et-st));
	}


}
