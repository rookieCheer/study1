package com.huoq.thread.action;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.thread.bean.SendBookingProductBean;

/**后台线程 扫描是否有营销中的理财产品需要续约
 * 当剩余份数小于等于总份数的10%时，如果有预约排队的理财产品，则自动售罄，否则不做任何操作
 * @author yks
 * @createTime 2016-9-25
 */
@Service
public class PutOnSaleProductThread implements Runnable{
	private Logger log = Logger.getLogger(CheckProductStatusThread.class);
	@Resource
	private SendBookingProductBean bean;
	@Override
	public void run() {
		log.info("-----进入自动续标线程---------");
		try {
		Date date = new Date();
		int hours = date.getHours();
		int minutes = date.getMinutes();
		int time = hours+minutes;
		if (time>=10) {
			//执行续标线程
			bean.sendBookingProduct();
			log.info("----------执行续标线程------结束");
		}
		
			
		} catch (Exception e) {
			log.error("----------执行续标线程---异常---结束");
			log.error(e);
		}
	}
	public static void main(String[] args) {
		Date date = new Date();
		int hours = date.getHours();
		System.out.println("小时:"+hours);
		int minutes = date.getMinutes();
		System.out.println("分钟"+minutes);
	}
}
