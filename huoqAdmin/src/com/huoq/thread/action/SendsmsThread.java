package com.huoq.thread.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import com.huoq.admin.product.bean.SmsRecordBean;
import com.huoq.common.bean.SystemConfigBean;
import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.common.util.SMSUtil;
import com.huoq.thread.bean.SendsmsThreadBean;

/**
 * 发送短信
 * @author wxl
 *    2017年3月2日19:13:03
 *
 */
@Service
public class SendsmsThread implements Runnable{
	private Logger log = Logger.getLogger(SendsmsThread.class);
	
	@Resource
	private SendsmsThreadBean bean;
	@Resource
	private SmsRecordBean smsRecordBean;
	@Resource
	SystemConfigBean systemConfigBean;
	
	@Override
	public void run() {
		
		long st = System.currentTimeMillis();
		String stTime = "2017-06-05 00:00:00";
		String etTime = "2017-06-26 23:59:59";
		
		try {
			Date stDate = QwyUtil.fmyyyyMMddHHmmss.parse(stTime);
			Date etDate = QwyUtil.fmyyyyMMddHHmmss.parse(etTime);
			if (new Date().before(etDate) && new Date().after(stDate)) {
				log.info("进入明日返款发送短信的线程");
				
				PageUtil<Object> pageUtil = new PageUtil<Object>();
				pageUtil.setPageSize(200);
				int currentPage = 0;
				String now = QwyUtil.fmyyyyMMdd.format(QwyUtil.addDaysFromOldDate(new Date(), 1).getTime());
				String tommSt = now +" 00:00:00";
				String tommEt = now +" 23:59:59";
				for(;;){
					currentPage++;
					pageUtil.setCurrentPage(currentPage);
	//				pageUtil = bean.getList(pageUtil);
	//				pageUtil = bean.getUsername(pageUtil);  //平台所有注册用户的用户名  即手机号码
	//				pageUtil = bean.getInvUsername(pageUtil); //平台所有投资用户的用户名
//					pageUtil = bean.getBindUsername(pageUtil); //平台绑卡用户
					pageUtil = bean.getReturnUsers(pageUtil, tommSt, tommEt); // 明天还款用户
//					pageUtil = bean.getBindUnInveUsers(pageUtil, "2017-04-15 00:00:00", "2017-04-27 23:59:59");  //活动期内未投资用户
//					pageUtil = bean.getNoAddrUsers(pageUtil, "2017-04-15 00:00:00", "2017-04-27 23:59:59");
//					pageUtil = bean.getOldVersionUsers(pageUtil);
					//用来存放手机号码的集合
					List<String> stringMobile = new ArrayList<>();
					
					if (QwyUtil.isNullAndEmpty(pageUtil)) {
						log.info("没有要发送短信的用户！");
						break;
					}
					
					List list = pageUtil.getList();
					for(int i = 0; i < list.size(); i++){
						//解密后存入集合中
						if (QwyUtil.isNullAndEmpty(list.get(i))) {
							continue;
						}
						Object objects = (Object) list.get(i);
						String phoneNum = DESEncrypt.jieMiUsername(objects.toString());
						if (QwyUtil.verifyPhone(phoneNum)) {
							if (bean.isSendSms(phoneNum,stTime,etTime)) {
								stringMobile.add(phoneNum);
							}
						}else{
							log.info("该手机格式错误："+phoneNum);
							continue;
						}
						
					}
					
					//发送短信
					if (!QwyUtil.isNullAndEmpty(stringMobile)) {
					
						String newStr =smsRecordBean.listToString(stringMobile);// 用户名集合转换为拼接字符串
	//					String smsContent = "主人，即日注册可获得最高120元红包，首投还有3900元福袋任性送，快来拿！";
	//					String smsContent = "女神节怎么过？新华金典理财准备女神专属加息标+2.88％，男神也有礼物哦~还不快来  http://m.huoq.com/god.html "; //枫叶女神活动 短信内容
	//					String smsContent = "邀好友，开启投资作战大PK，还有各种现金红包任性撒，还不快来~ http://t.cn/R65z1Zx ";  //土豪星球活动短信
	//					String smsContent = "每一次跨越，都开启新的征程，庆祝新华金典理财投资额破亿，明天10点，全场限时最高1%加息等你来！";
	//					String smsContent = "破亿愚人共享-全场限时火热加息进行时！限时加息狂潮，最高1%！手快有手慢无！ http://t.cn/R6p3Si9";
	//					String smsContent = "破亿愚人共享-全场限时火热加息，最后五小时，最高加息1%，错过今天没有明天~ http://t.cn/R6p3Si9";
//						String smsContent = "花花世界遇见你，献上一份春日理财大礼包，来呀，快活呀~反正有大把时光~";
//						String smsContent = "投资满额即送星巴克券、千元京东卡，抢投资排名再送豪礼，好春光不如投一场。";
//						String smsContent = "检测到您当前使用的版本较低，为了提供更棒的服务，请点击下载最新版APP http://t.cn/RajMuiy";
						String smsContent = "您有笔投资即将到期，6月复投赢金条、京东卡、百分百中奖，再加最高0.6%返现 huoq://com.huoq.www";
						String topContent = systemConfigBean.findSystemConfig().getSmsQm();//短信签名 动态获取
						String content = topContent + smsContent + " 回T退订";    
						Map<String, Object> map = SMSUtil.sendYzmCZ_SALE(newStr, content);
						log.info(map);
						if (!QwyUtil.isNullAndEmpty(map)) {
							smsRecordBean.addSmsRecord(newStr, content, map.get("error").toString(), -1L);
							log.info("第 "+currentPage+"页发送短信成功;");
						} else {
							log.info("第 "+currentPage+"页发送短信失败;");
						}	
					}else{
						log.info("没有要发送短信的用户");
						break;
					}
					
				}
			}
			
		} catch (Exception e) {
			log.info(e);
		}
		
		long et = System.currentTimeMillis();
		log.info("处理【发送短信】耗时: "+(et-st));
		
	}
	
}
