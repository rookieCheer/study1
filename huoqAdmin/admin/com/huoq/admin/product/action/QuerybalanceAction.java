package com.huoq.admin.product.action;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;

import com.huoq.admin.product.bean.SmsRecordBean;
import com.huoq.common.action.BaseAction;
import com.huoq.common.bean.SystemConfigBean;
import com.huoq.common.util.QwyUtil;
import com.huoq.common.util.SMSUtil;
import com.huoq.orm.SmsRecord;
import com.huoq.orm.UsersAdmin;
import com.huoq.account.bean.UserRechargeBean;
import com.huoq.admin.product.bean.InterestDetailsBean;
import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.PageUtil;
import com.huoq.orm.InterestDetails;
import com.huoq.orm.Users;

/**
 * 亿美短信余额
 * 
 * @author qwy
 *
 *         2015-04-20 12:58:29
 */
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/Admin")
// 理财产品
@Results({  @Result(name = "queryBalance", value = "/Product/Admin/fundsManager/ymSMSCount.jsp"),
			@Result(name = "mass", value = "/Product/Admin/fundsManager/sendSms.jsp"),
		    @Result(name = "err", value = "/Product/Admin/err.jsp") })
public class QuerybalanceAction extends BaseAction {
	@Resource
	SmsRecordBean bean;
	@Resource
	SystemConfigBean systemConfigBean;
	
	@Resource
	private InterestDetailsBean interestDetailsBean;
	@Resource
	private UserRechargeBean userRechargeBean;
	@Resource
	private SmsRecordBean smsRecordBean;
	
	private SmsRecord smsRecord;
	
	private String returnTime;//还款日期;
	private String exReturnTime;//除去还款日期;
	private String coupon;//理财券;
	private String overTime;
	private String note;

	/**
	 * 查询亿美余额;
	 * 
	 * @return
	 */
	public String queryBalance() {
		String json = "";
		try {
			UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
			if (QwyUtil.isNullAndEmpty(users)) {
				json = QwyUtil.getJSONString("err", "管理员未登录");
				QwyUtil.printJSON(getResponse(), json);
				// 管理员没有登录;
				return null;
			}
			String superName = "0EA5D6BC23E8EEC78F62546B9F68BABFA96976B775889BA625DB6D764FD0DBD42A1C0F45F85B0DE8";
			if (!superName.equals(users.getUsername())) {
				if (isExistsQX("发送短信", users.getId())) {
					getRequest().setAttribute("err", "您没有操作该功能的权限!");
					return "err";
				}
			}
			Map<String, Object> map = SMSUtil.queryBalance();
			request.setAttribute("map", map);
			request.setAttribute("message", map.get("overage"));
		} catch (Exception e) {
			log.error("操作异常: ", e);
			// request.setAttribute("errMsg", "发生了错误");
			return "error";
		}
		return "queryBalance";
	}
	
	
	/**
	 * 得到2016-12-16日收益到期 的用户
	 * @return
	 */
	public String sendHongbaoSMS(){
		String json = "";
		
		try {
			
			UsersAdmin users = (UsersAdmin)getRequest().getSession().getAttribute("usersAdmin");
			if(QwyUtil.isNullAndEmpty(users)){
				json = QwyUtil.getJSONString("err", "管理员未登录");
				QwyUtil.printJSON(getResponse(), json);
				//管理员没有登录;
				return null;
			}
			String exReturnTimeST = "";
			String exReturnTimeET = "";
			if(!QwyUtil.isNullAndEmpty(exReturnTime)){
				String[] time = exReturnTime.split("-");
				if(time.length>1){
					exReturnTimeST=QwyUtil.fmyyyyMMdd.format(new Date(time[0]));
					exReturnTimeET=QwyUtil.fmyyyyMMdd.format(new Date(time[1]));
				}else{
					exReturnTimeST=QwyUtil.fmyyyyMMdd.format(new Date(time[0]));
					exReturnTimeET=QwyUtil.fmyyyyMMdd.format(new Date(time[0]));;
				}
			}
			
			PageUtil<Users> pageUtil = new PageUtil<Users>();
			pageUtil.setCurrentPage(1);
			pageUtil.setPageSize(999999);
			pageUtil = interestDetailsBean.getList(pageUtil, returnTime, exReturnTimeST, exReturnTimeET);
			List userList = pageUtil.getList();
			log.info("返款用户人数:"+userList.size());
			int fail = 0;
			for (int i = 0; i < userList.size(); i++) {
				log.info("发送第"+(i+1)+("个人"));
				try {
					InterestDetails interestDetails = (InterestDetails) userList.get(i);
					Users user = interestDetails.getUsers();
					
					String topContent = "【新华金典理财】";
					topContent+=smsRecord.getSmsContent();
					String content = topContent + "回复TD退订";
					String mobile = DESEncrypt.jieMiUsername(user.getUsername());
					Map<String, Object> map = SMSUtil.sendYzm2(mobile, null, content);
					if (!QwyUtil.isNullAndEmpty(map)) {
						bean.addSmsRecord(mobile, content, map.get("error").toString(), users.getId());
						log.info("发送完毕;");
						for (String str : coupon.split(";")) {
							userRechargeBean.sendHongBao(user.getId(), Double.parseDouble(str)*100, QwyUtil.isNullAndEmpty(overTime)?null:QwyUtil.fmyyyyMMdd.parse(overTime),"0", -1, note,null);
						}
						log.info("发送红包完毕;");
					} else {
						fail++;
						log.info("发送短信失败;");
					}
				} catch (Exception e) {
					log.error("发送短信异常",e);
					fail++;
				}
			}
			json = QwyUtil.getJSONString("ok", "发送短信成功;共"+userList.size()+"人;失败:"+fail+"人");
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("err", "发送短信失败;异常");
		}
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return null;
	}
	
	

	// 发送短信 
	public String sendMessage() {
		String json = "";
		try {
			UsersAdmin admin = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
			if (QwyUtil.isNullAndEmpty(admin)) {
				json = QwyUtil.getJSONString("err", "管理员未登录");
				QwyUtil.printJSON(getResponse(), json);
				// 管理员没有登录;
				return null;
			}
			if (!QwyUtil.isNullAndEmpty(smsRecord)) {
				String mobileString = smsRecord.getMobile();
				String[] strArray = null;
				strArray = smsRecord.getMobile().split(","); // 拆分字符为","
																// ,然后把结果交给数组strArray
				for (int i = 0; i < strArray.length; i++) {
					if (!QwyUtil.verifyPhone(strArray[i])) {
						json = QwyUtil.getJSONString("error", "手机号有误：" + strArray[i]);
						break;
					}
				}
				if (QwyUtil.isNullAndEmpty(json)) {// 判断手机格式
//					String topContent = systemConfigBean.findSystemConfig().getSmsQm();// 一条短信长度是67
					if (!QwyUtil.isNullAndEmpty(smsRecord.getSmsContent())) {
						if (smsRecord.getSmsContent().length() > 500) {
							json = QwyUtil.getJSONString("error", "短信内容过长");
						} else {
							String topContent = systemConfigBean.findSystemConfig().getSmsQm();//短信签名 动态获取
							String content =  smsRecord.getSmsContent();
							String smsContent = content + " 回复TD退订"+topContent;   
							Map<String, Object> map = SMSUtil.sendYzmCZ_SALE(mobileString, smsContent);  //营销类型
							if (!QwyUtil.isNullAndEmpty(map)) {
								bean.addSmsRecord(mobileString, smsContent, map.get("error").toString(), admin.getId());
								json = QwyUtil.getJSONString("ok", "发送短信成功");
							} else {
								json = QwyUtil.getJSONString("error", "发送短信异常");
							}
						}
					} else {
						json = QwyUtil.getJSONString("error", "短信内容不能为空");
					}
				}
			} else {
				json = QwyUtil.getJSONString("error", "请填写表单不能为空");
			}
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("error", "操作异常");
		}
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return null;
	}
	
	/**
	 * 群发短信
	 * @return
	 */
	public String sendMassMessage() {
		String json = "";
		try {
			UsersAdmin admin = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
			if (QwyUtil.isNullAndEmpty(admin)) {
				json = QwyUtil.getJSONString("err", "管理员未登录");
				QwyUtil.printJSON(getResponse(), json);
				// 管理员没有登录;
				return null;
			}
			
			List<String> mobileList = null;
			String smsContent = "";
			if (!QwyUtil.isNullAndEmpty(smsRecord)) { 
				if (!QwyUtil.isNullAndEmpty(smsRecord.getSmsContent())) {
					String topContent = systemConfigBean.findSystemConfig().getSmsQm();//短信签名 动态获取
					smsContent = smsRecord.getSmsContent()+" 回T退订"+topContent;
				}else{
					log.info("短信内容为空~");
					return null;
				}
					
				if (!QwyUtil.isNullAndEmpty(smsRecord.getMobile())) {
					if ("0".equals(smsRecord.getMobile())) { //平台所有用户
						mobileList = smsRecordBean.getUsername();
					}else if("1".equals(smsRecord.getMobile())){ //绑卡用户
						mobileList = smsRecordBean.getUsernameBind();
					}else if("2".equals(smsRecord.getMobile())){ //绑卡为投资用户
						mobileList = smsRecordBean.getUsernameBindedUnInv();
					}else if("3".equals(smsRecord.getMobile())){ //投资用户
						mobileList = smsRecordBean.getUsernameInv();
					}
				}
				
				if (!QwyUtil.isNullAndEmpty(mobileList)) {
					String newStr = "";
					int total = mobileList.size();
					int init = 200;
					int times = total / init;  
					if (total % init != 0) {
						times += 1;
					}
					List<String> sb = new ArrayList<>();
					
					log.info("需要发短信用户: "+mobileList.size()+"人;");
					for (int i = 0; i < times; i++) {
						if (mobileList.size() < init) {
							init = mobileList.size();
						}
						for (int j = 0; j < init; j++) {
							if (mobileList.get(j) == null) {
								break;
							}
							sb.add(mobileList.get(j).toString());
						}
						
						newStr = smsRecordBean.listToString(sb);
						if (!QwyUtil.isNullAndEmpty(smsContent) && !QwyUtil.isNullAndEmpty(newStr)) {
							Map<String, Object> map = SMSUtil.sendYzmCZ_SALE(newStr, smsContent);
							if (!QwyUtil.isNullAndEmpty(map)) {
								smsRecordBean.addSmsRecord(newStr, smsContent, map.get("error").toString(), 0l);
								json = QwyUtil.getJSONString("ok", "发送短信成功");
							} else {
								json = QwyUtil.getJSONString("error", "发送短信异常");
							}
						}
						mobileList.removeAll(sb);
						sb.clear();
						log.info("发放第"+(i+1)+"页；");
					}
					
					log.info("发放完毕~");
					
				}else{
					log.info("没有要发送短信的用户~");
				}
			}else{
				json = QwyUtil.getJSONString("error", "对象为空~");
			}
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("error", "操作异常");
		}
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return null;
	}

	/*public String sendMessage111() {
		String json = "";
		try {
			if (bean.sendSMStoHuoQRegisitUsers_1_25_9()){
				json = QwyUtil.getJSONString("ok", "发送短信成功");
			}
			if (bean.sendSMStoHuoQRegisitUsers_1_28_8()){
				json = QwyUtil.getJSONString("ok", "发送短信成功");
			}
			if (bean.sendSMStoHuoQRegisitUsers_2_3_21()){
				json = QwyUtil.getJSONString("ok", "发送短信成功");
			}
		}catch (Exception e){
			log.error("操作异常: ",e);
		}
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("error", "操作异常");
		}
		return null;
	}*/

	public SmsRecord getSmsRecord() {
		return smsRecord;
	}

	public void setSmsRecord(SmsRecord smsRecord) {
		this.smsRecord = smsRecord;
	}


	public String getReturnTime() {
		return returnTime;
	}

	public void setReturnTime(String returnTime) {
		this.returnTime = returnTime;
	}

	public String getExReturnTime() {
		return exReturnTime;
	}

	public void setExReturnTime(String exReturnTime) {
		this.exReturnTime = exReturnTime;
	}

	public String getCoupon() {
		return coupon;
	}

	public void setCoupon(String coupon) {
		this.coupon = coupon;
	}


	public String getOverTime() {
		return overTime;
	}


	public void setOverTime(String overTime) {
		this.overTime = overTime;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

}
