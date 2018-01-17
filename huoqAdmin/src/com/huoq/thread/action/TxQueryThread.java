package com.huoq.thread.action;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.Coupon;
import com.huoq.orm.TxRecord;
import com.huoq.thread.bean.TxQueryThreadBean;

/**查询提现接口;后台线程;<br>
 * 后台线程,自动查询提现接口;查询的都是审核后的第二天;<br>
 * @author qwy
 *
 * @createTime 2015-06-02 03:09:15
 */
@Service
public class TxQueryThread implements Runnable{

	private static Logger log = Logger.getLogger(TxQueryThread.class);
	@Resource
	private TxQueryThreadBean bean;

	@Override
	public void run() {
		try {
			PageUtil<TxRecord> pageUtil = new PageUtil<TxRecord>();
			pageUtil.setPageSize(50);
			int currentPage = 0;
			for (;;) {
				currentPage++;
				pageUtil.setCurrentPage(currentPage);
				pageUtil = bean.getTxRecordByChecking(pageUtil);
				List<TxRecord> listTxRecord= pageUtil.getList();
				if(QwyUtil.isNullAndEmpty(listTxRecord)){
					log.info("TxQueryThread没有要查询的提现记录: "+currentPage);
					break;
				}
				if(!QwyUtil.isNullAndEmpty(listTxRecord)){
					for (TxRecord txRecord : listTxRecord) {
						bean.queryTxByRequestId(txRecord.getUsersId(), txRecord.getRequestId());
					}
				}
			}
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		
	}

}
