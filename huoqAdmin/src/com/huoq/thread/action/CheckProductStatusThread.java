package com.huoq.thread.action;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import javax.annotation.Resource;

import com.huoq.login.bean.RegisterUserBean;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.admin.product.bean.VirtualInsRecordBean;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.Product;
import com.huoq.orm.VirtualIns;
import com.huoq.thread.bean.SendBookingProductBean;

/**后台线程 扫描是否有营销中的理财产品被虚拟下架
 * 当剩余份数小于等于总份数的10%时，如果有预约排队的理财产品，则自动售罄，否则不做任何操作
 * @author yks
 * @createTime 2016-9-25
 */
@Service(value = "checkProductStatusThread")
public class CheckProductStatusThread extends TimerTask {

	private Logger log = Logger.getLogger(CheckProductStatusThread.class);
	private Integer pageSize = 50;
	@Resource
	private SendBookingProductBean bean;
	@Resource
	private VirtualInsRecordBean virtualInsRecordBean;
	@Resource
	private RegisterUserBean registerUserBean;
	
	
	@Override
	public void run() {
		try {
			log.info("----------执行虚标线程");
				//获取昨天的理财产品只将昨天的理财产品虚掉
				List<Product>	listProduct = bean.getYesterdayProduct();
				log.info("--------进入--获取所有未售罄的标: ");
				if(!QwyUtil.isNullAndEmpty(listProduct)){
					log.info("----------清除昨天未卖完的标: "+listProduct.size()+"个");
					for (Product product : listProduct) {
						//添加售罄状态
						product.setProductStatus("1");//已售罄;
						log.info("----------清除所有未卖完的标: "+product.getTitle());
						String productId = product.getId();
						long leftCopies = product.getLeftCopies();
						int userCount = 1;
						//灵活计算虚拟投资人数;
						if(leftCopies<=30000){
							userCount = 1;
						}else if(leftCopies<=50000){
							userCount = 2;
						}else if(leftCopies<=100000){
							userCount = 3;
						}else if(leftCopies<=200000){
							userCount = 6;
						}else{
							userCount = 10;
						}
						//虚拟投资;
						VirtualIns ins=virtualInsRecordBean.saveVirtualIns(userCount,QwyUtil.calcNumber(leftCopies, 100, "*").doubleValue(), productId, -1l);
						if(virtualInsRecordBean.virtualInsIns(userCount,QwyUtil.calcNumber(leftCopies, 100, "*").doubleValue(), productId)){
							ins.setStatus("1");
							bean.saveOrUpdate(ins);
							log.info("---------清除当前之前所有未卖完的标--虚拟投资成功");
						}else{
							ins.setStatus("2");
							bean.saveOrUpdate(ins);
							log.info("---------清除当前之前所有未卖完的标--虚拟投资失败");
						}
						
					}
					bean.sendBookingProduct();
				}else{
					log.info("-------未卖完的标: 无");
				}
		} catch (Exception e) {
			log.error("----------执行虚标线程---异常---结束");
			log.error(e);
		}
	}
}
