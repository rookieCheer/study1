package com.huoq.thread.bean;

import com.huoq.admin.product.bean.VirtualInsRecordBean;
import com.huoq.common.bean.SystemConfigBean;
import com.huoq.common.dao.ObjectDAO;
import com.huoq.common.util.QwyUtil;
import com.huoq.common.util.SMSUtil;
import com.huoq.newbranch.constants.Constants;
import com.huoq.orm.Product;
import com.huoq.orm.VirtualIns;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class SendBookingProductBean extends ObjectDAO {

	private static Logger log = Logger.getLogger(SendBookingProductBean.class);
    @Resource
	private VirtualInsRecordBean virtualInsRecordBean;

    @Resource
	private SystemConfigBean systemConfigBean;

    /**
     * 查找预约产品;
     * @return
     */
    public List<Product> findBookingProduct() {
        StringBuffer sb = new StringBuffer();
        sb.append("FROM Product pro ");
        sb.append("WHERE pro.productStatus = '-3' ");
        sb.append("ORDER BY pro.title ASC,pro.bookingTime ASC");
        return (List<Product>) LoadAll(sb.toString(), null);
    }


    /**
     * 查找运行中的产品;
     *
     * @return
     */
    public List<Product> findProductByZero() {
        StringBuffer sb = new StringBuffer();
        sb.append("FROM Product pro ");
        sb.append("WHERE pro.productStatus = '0' ");
        sb.append("ORDER BY pro.insertTime DESC ");
        return (List<Product>) LoadAll(sb.toString(), null);
    }

    /**获取昨天的理财产品;
     *
     * @return
     */
    public List<Product> getYesterdayProduct(){
    	 StringBuffer sb = new StringBuffer();
    	 String nowDate = QwyUtil.fmyyyyMMdd.format(QwyUtil.addDaysFromOldDate(new Date(), -1).getTime());
    	 nowDate+=" 23:59:59";
         sb.append("FROM Product pro ");
         sb.append("WHERE pro.productStatus = '0' ");
         sb.append("AND pro.insertTime <= '"+nowDate+"' ");
         sb.append("ORDER BY pro.insertTime DESC ");
         return (List<Product>) LoadAll(sb.toString(), null);
    }
    /**
     * 2017-10-14改为获取所有营销中的理财产品
     * @return
     */
    public List<Product> getAllSaleProduct(){
    	StringBuffer sb = new StringBuffer();
    	sb.append("FROM Product pro ");
    	sb.append("WHERE pro.productStatus = '0' ");
    	sb.append("ORDER BY pro.insertTime DESC ");
    	return (List<Product>) LoadAll(sb.toString(), null);
    }




    /**发布预约产品;
     * @param ;
     * @throws Exception
     */
	public synchronized void sendBookingProduct() throws Exception {
		String random = QwyUtil.getRandomFourNumber();
		List<String> daiShou = getBookingKeyword();//获取预约关键字
		if(QwyUtil.isNullAndEmpty(daiShou)){
			log.info(random+" -----------sendBookingProduct: 没有预约【关键字】，不执行预约标功能；");
			return;
		}

		log.info(random+" -----------sendBookingProduct: 预约【关键字】："+QwyUtil.packString(daiShou.toArray()).replaceAll("'", ""));
		HashMap<String,String> kongque = new HashMap<String,String>();
		List<Product> saleProduct =findProductByZero();//获取销售中的产品
		// 需要发布的新产品名称;
		if (QwyUtil.isNullAndEmpty(saleProduct)) {
			log.info(random+" -----------需要【全部】续标");
			// -----------平台这的产品都全部卖完;
			for (String str : daiShou) {
				kongque.put(str,str);
			}
		} else {
			// -----------平台产品还有在售卖中;
			// 把空缺的产品填补上;
			log.info(random+" -----------【只续标】需要上线的产品");
			int size = saleProduct.size();
			for (String ds : daiShou) {
				int num = 0;// 初始化个数;
				int xh4h = 0;//新华4号产品数量
				for (Product pro : saleProduct) {
					if (pro.getTitle().contains(ds) && !ds.equals("新华4号信用贷")) {
						// size
						break;
					}
					if(!ds.equals("新华4号信用贷")){
						num++;// 没有包含的个数
					}
					if (ds.equals("新华4号信用贷") && pro.getInvestType().equals("7")) {
						xh4h++;
					}
				}
				if(xh4h < 3 && ds.contains("新华4号信用贷")){
					kongque.put("新华4号","新华4号");
				}
				if (num == size) {
					kongque.put(ds,ds);
				}
			}
			// 所有产品正在售卖中;查找剩余分数<=总份数的10%的产品;找到则售罄,然后上线新产品替代;
			for (Product pro : saleProduct) {
				double percent10 = QwyUtil.calcNumber(pro.getAllCopies(), 0.05, "*").doubleValue();
				double leftCopies = pro.getLeftCopies();
				// 小于5%;把该产品售罄,然后上线新的产品;
				if (leftCopies <= percent10) {
					for (String string : daiShou) {
						// 小于5%的产品,必须是常规的5种之中,其它的不作处理;
						if (pro.getTitle().contains(string)) {
							// 小于5%的产品,在这5种常规产品之内;
							log.info(pro.getTitle()+" 的剩余分数小于5%;");
							String productId = pro.getId();
							int userCount = 1;
							if (leftCopies <= 30000) {
								userCount = 1;
							} else if (leftCopies <= 50000) {
								userCount = 2;
							} else if (leftCopies <= 100000) {
								userCount = 3;
							} else if (leftCopies <= 200000) {
								userCount = 6;
							} else {
								userCount = 10;
							}
							VirtualIns ins = virtualInsRecordBean.saveVirtualIns(userCount,QwyUtil.calcNumber(leftCopies, 100, "*").doubleValue(), productId, -1l);
							boolean isOK = virtualInsRecordBean.virtualInsIns(userCount,QwyUtil.calcNumber(leftCopies, 100, "*").doubleValue(), productId);
							if (isOK) {
								log.info(random+" -----虚拟投资成功~续标!");
								ins.setStatus("1");
								saveOrUpdate(ins);
								// 虚拟成功;
								kongque.put(string,string);
							} else {
								log.info(random+" -----虚拟投资失败~续标!");
								ins.setStatus("2");
								saveOrUpdate(ins);
							}
							break;
						}
					}
				}
			}
		}
		//已经找出空缺的产品;准备预约上线空缺的产品;
		if(!QwyUtil.isNullAndEmpty(kongque)){
			List<Product> bookingProduct = findBookingProduct();
			StringBuffer isSendMessage = new StringBuffer();
			if(QwyUtil.isNullAndEmpty(bookingProduct)){
				log.info(random+" -----没有在排队预约的产品!");
				Set<String> d = kongque.keySet();
				for (String key : d) {
					isSendMessage.append(key);
					isSendMessage.append("、");
				}
			}else{
				log.info(random+" ------------开始上线预约产品");
				//上线预约产品;
				Set<String> d = kongque.keySet();
				int size = bookingProduct.size();
				for (String key : d) {
					int i=0;
					log.info(random+" ------------需要上线预约产品:"+key);
					for (Product pro : bookingProduct) {
						if (pro.getTitle().contains(key)) {
							log.info(random+" ------------正在上线预约产品:"+pro.getTitle());
							pro.setProductStatus("0");
							if (pro.getTitle().contains("新手")) {
								//更新预约新手产品项目结束时间 新手的endTime和finishTime是一致的
								Date newDate = QwyUtil.addDaysFromOldDate(new Date(), pro.getLcqx().intValue()).getTime();
								pro.setFinishTime(newDate);
								pro.setEndTime(newDate);
								pro.setInsertTime(new Date());
							} else {
								// 更新预约常规产品项目结束时间
								Date newDate = QwyUtil.addDaysFromOldDate(new Date(), pro.getLcqx().intValue()).getTime();
								pro.setEndTime(QwyUtil.addDaysFromOldDate(newDate, -3).getTime());// 是结束时间的前3天;
								pro.setFinishTime(newDate);
								pro.setInsertTime(new Date());
							}
							saveOrUpdate(pro);//先更新发布时间;
							log.info(random+" ------预约产品:"+pro.getTitle()+"上线完毕----------");
							break;//只上线一个
						}
						i++;
					}
					if(i==size){
						log.info(random+" ----------缺少预约产品:"+key+"----------");
						isSendMessage.append(key);
						isSendMessage.append("、");
					}
				}
			}
			if(!QwyUtil.isNullAndEmpty(isSendMessage.toString())&&isSendMessage.toString().length()>=3){
				// "【新华金典理财】缺少（"+isSendMessage.toString().substring(0, isSendMessage.toString().length()-1)+"）理财产品，请尽快补全。";
				String smsContent = String.format(Constants.SMS_ADDPRD_TZ,
						isSendMessage.toString().substring(0, isSendMessage.toString().length()-1),
							systemConfigBean.findSystemConfig().getSmsQm());

				//缺少预约产品;发短信;
				SMSUtil.sendTongZhi(Constants.TONGZHI_PHONE, smsContent);
			}
			log.info(random+" ----------自动续标完毕----------");
		}else{
			log.info(random+" ----------没有空缺的产品,【不需预约】----------");
		}
	}

	public static void main(String[] args) {
		Map<String,String> kongque = new HashMap<String,String>();
		kongque.put("d", "d");
		kongque.put("dd", "d");
		kongque.put("ddddx", "d");
		kongque.put("ddssdx", "d");
		kongque.put("ddsdx", "d");
		if(!QwyUtil.isNullAndEmpty(kongque)){
			Set<String> d = kongque.keySet();
			for (String key : d) {
				log.info(key);
				int random = new Random().nextInt(10);
			}
		}
		log.info(QwyUtil.isNullAndEmpty(kongque));

	}

	public List<String> getBookingKeyword(){
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT bk.keyword FROM BookingKeyword bk ");
		sb.append("WHERE bk.status = '0' AND bk.type = '0' ");
		List<String> list = super.LoadAll(sb.toString(), null);
		return list;
	}
}
