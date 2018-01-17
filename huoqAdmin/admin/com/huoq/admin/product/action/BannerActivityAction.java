package com.huoq.admin.product.action;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;

import com.huoq.admin.product.bean.BannerActivityBean;
import com.huoq.admin.product.bean.SendCouponBean;
import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.LotteryTimes;
import com.huoq.orm.MUsersAddress;
import com.huoq.orm.Users;
import com.huoq.orm.UsersAdmin;
import com.huoq.product.action.IndexAction;

import net.sf.json.JSONArray;

/**
 * 加群领取50元投资券Action层
 * 
 * @author 王雪林
 *
 */
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/Admin")
@Results({ @Result(name = "doActivity", value = "/activity/jqltzq/jqltzq.jsp"),
		@Result(name = "nationalDay", value = "/Product/Act_NationalDay.jsp"),
		@Result(name = "activityAutumn", value = "/Product/activity_autumn.jsp"),
		@Result(name = "singleDay", value = "/Product/activity_singlesDay.jsp"),
		@Result(name = "count2", value = "/Product/Admin/operationManager/count.jsp"),
		@Result(name = "rankingList", value = "/Product/Admin/operationManager/rankingList.jsp")
		,@Result(name = "bonusRanking", value = "/Product/Admin/operationManager/bonusRanking.jsp"),
		@Result(name = "valentineDay", value = "/Product/Admin/operationManager/valentineDay.jsp"),
		@Result(name = "muaddress", value = "/Product/Admin/operationManager/muaddress.jsp"),
		@Result(name = "VDfirstInv", value = "/Product/Admin/operationManager/VDfirstInv.jsp"),
		@Result(name = "rich", value = "/Product/Admin/operationManager/rich.jsp"),
		@Result(name = "tyc", value = "/Product/Admin/operationManager/tyc.jsp"),
		@Result(name = "mday", value = "/Product/Admin/operationManager/mDay.jsp"),
		@Result(name = "dw", value = "/Product/Admin/operationManager/duanwu.jsp")
})
public class BannerActivityAction extends IndexAction {
	private static Logger log = Logger.getLogger(BannerActivityAction.class);
	private String mobileNum;
	private String xlhNum;
	private Integer currentPage=1;//当前页
	
	private Integer pageCount;//总页数
	private Integer pageSize = 50;
	private String status = "all";
	private String username;
	private String sumMoney;
	private MUsersAddress muserAddress;
	
	private String insertTime;
	
	private LotteryTimes lotteryTimes;
	private String note;
	
	@Resource
	private BannerActivityBean bannerActivityBean;
	@Resource
	private SendCouponBean sendCouponBean;

	/**
	 * 加群领取50元投资券
	 * 
	 * @return
	 */
	public String doActivity() {
		try {
			String message = "";
			if (!QwyUtil.verifyPhone(mobileNum)) {
				message = "请输入正确手机号";
			} else if (QwyUtil.isNullAndEmpty(xlhNum)) {
				message = "请输入序列号";
			} else if (!QwyUtil.verifyStrLength(xlhNum, 4, 50)) {
				message = "序列号格式错误";
			}
			if (message.equals("")) {
				message = bannerActivityBean.checkMobileAndXlh(mobileNum, xlhNum);
			}
			String json = "";
			if (message.equals("")) {
				json = QwyUtil.getJSONString("ok", "投资券已发放到您账户,请注意查收!");
			} else {
				json = QwyUtil.getJSONString("no", message);
			}
			QwyUtil.printJSON(getResponse(), json);
		} catch (Exception e) {
			log.error("操作异常: ", e);
		}
		return null;
	}
	
	

	/**
	 * 注册领取888元投资券
	 * 
	 * @return
	 */
	public String regSendTicket() {
		try {
			String message = "";
			if (!QwyUtil.verifyPhone(mobileNum)) {
				message = "请输入正确手机号";
			}
			if (message.equals("")) {
				message = bannerActivityBean.checkMobileAndXlhSendTicket(mobileNum);
			}
			String json = "";
			if (message.equals("")) {
				json = QwyUtil.getJSONString("ok", "投资券已发放到您账户,请注意查收!");
			} else {
				json = QwyUtil.getJSONString("no", message);
			}
			QwyUtil.printJSON(getResponse(), json);
		} catch (Exception e) {
			log.error("操作异常: ", e);
		}
		return null;
	}

	/**
	 * 获取国庆活动的投资数据;
	 * 
	 * @return
	 */
	public String nationalDay() {
		try {
			String stDateTime = "2016-09-26 00:00:00";
			String edDateTime = "2016-10-03 23:59:59";
			String totalInvest = bannerActivityBean.getInvestorsTotal(stDateTime, edDateTime);
			String totalInvestUsers = bannerActivityBean.getInvestorsUsersTotal(stDateTime, edDateTime);
			request.setAttribute("totalInvest", QwyUtil.calcNumber(totalInvest, 8, "*").toString());
			request.setAttribute("totalInvestUsers", QwyUtil.calcNumber(totalInvestUsers, 8, "*").toString());
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return "nationalDay";
	}

	/**
	 * 金秋活动;<br>
	 * 10月19日-11月7日,获取投资额前 15 名（新手标，周周利除外）
	 * 
	 * @return
	 */
	public String autumnActivity() {
		
		String stDateTime = "2016-10-19 00:00:00";
		String edDateTime = "2016-11-07 23:59:59";
		List<Object[]> list = bannerActivityBean.autumnActivity(stDateTime, edDateTime);
		list = formartList(list);
//		Object[] maxObj = newTimeInv(list);
		//list.add(maxObj);
		request.setAttribute("list", list);
		
//		request.setAttribute("maxObj", maxObj);
		return "activityAutumn";
	}
	
	
	/**
	 * 分页查询   获得双十一当天投资额超过2万的用户的信息   手机号码 投资资金
	 * @return
	 */
	public String getSingleNum(){
		
		String stDateTime = "2016-11-11 00:00:00";
		String edDateTime = "2016-11-11 23:59:59";
		
		try {
			request = getRequest();
			UsersAdmin users = (UsersAdmin)getRequest().getSession().getAttribute("usersAdmin");
			if(QwyUtil.isNullAndEmpty(users)){
				return "login";
			}
		 PageUtil pageUtil=new PageUtil();
		 pageUtil.setCurrentPage(currentPage);
	     pageUtil.setPageSize(pageSize);
	     
	     StringBuffer url = new StringBuffer();
		 url.append(getRequest().getServletContext().getContextPath());
		 url.append("/Product/Admin/bannerActivity!getSingleNum.action?");
		 
		 pageUtil.setPageUrl(url.toString());
		 
		 pageUtil = bannerActivityBean.getSingleCount(pageUtil,stDateTime,edDateTime);
		 
		 if(!QwyUtil.isNullAndEmpty(pageUtil)){
			 	List list = pageUtil.getList();
			 
				request.setAttribute("list", list);
			}
		 request.setAttribute("pageUtil", pageUtil);
		 
		 return "count2";
		
		} catch (Exception e) {
			
			log.error("操作异常: ",e);
		}
		
		return null;
	}
	
	/**
	 * 分页查询  “%悦享双十一%”产品 双十一当天投资总额排行榜
	 * @return
	 */
	public String rankingList(){
		
		String stDateTime = "2016-11-11 00:00:00";
		String edDateTime = "2016-11-11 23:59:59";
		
		try {
			request = getRequest();
			UsersAdmin users = (UsersAdmin)getRequest().getSession().getAttribute("usersAdmin");
			if(QwyUtil.isNullAndEmpty(users)){
				return "login";
			}
		 PageUtil pageUtil=new PageUtil();
		 pageUtil.setCurrentPage(currentPage);
	     pageUtil.setPageSize(pageSize);
	     
	     StringBuffer url = new StringBuffer();
		 url.append(getRequest().getServletContext().getContextPath());
		 url.append("/Product/Admin/bannerActivity!rankingList.action?");
		 
		 pageUtil.setPageUrl(url.toString());
		 
		 pageUtil = bannerActivityBean.getRankingList(pageUtil, stDateTime, edDateTime);
		 
		 if(!QwyUtil.isNullAndEmpty(pageUtil)){
			 	List list = pageUtil.getList();
			 	request.setAttribute("pageUtil", pageUtil);
				request.setAttribute("list", list);
			}
		 
		 return "rankingList";
		 
		} catch (Exception e) {
			log.error("操作异常",e);
		}
		
		return null;
	}
	
	
	
	
	/**无刷新更新活动排行榜
	 * @return
	 */
	public String autumnActivityAjax() {
		try {
			String stDateTime = "2016-10-19 00:00:00";
			String edDateTime = "2016-11-07 23:59:59";
			List<Object[]> list = bannerActivityBean.autumnActivity(stDateTime, edDateTime);
			if (QwyUtil.isNullAndEmpty(list)) {
				String json = QwyUtil.getJSONString("noData", "没有数据要更新");
				QwyUtil.printJSON(response, json);
				return null;
			}
			list = formartList(list);
			Object[] maxObj = newTimeInv(list);
			list.add(maxObj);
			JSONArray jary = JSONArray.fromObject(list);
			String json = QwyUtil.getJSONString("ok", jary);
			QwyUtil.printJSON(response, json);
		} catch (IOException e) {
			
			log.error("操作异常: ",e);
		}
		return null;
	}
	
	/**筛选出最新投资者(进入排行榜内);
	 * @param strList
	 * @return
	 */
	public Object[] newTimeInv(List<Object[]> strList){
		if(QwyUtil.isNullAndEmpty(strList))
			return null;
		try {
			long maxTime = 0l;
			Object[] maxObj = null;
			for (Object[] objects : strList) {
				Date date=(Date)objects[2];
				//统一时间格式化;
				Date date2 = QwyUtil.fmyyyyMMddHHmmss.parse(QwyUtil.fmyyyyMMddHHmmss.format(date));
				if(maxTime<date2.getTime()){
					maxTime = date2.getTime();
					maxObj = objects;
				}
			}
			return maxObj;
		} catch (Exception e) {
			
			log.error("操作异常: ",e);
		}
		return null;
	}
	
	/**
	 * 双十一活动的投资数据;
	 * 
	 * @return
	 */
	public String singleDay() {
		try {
			String stDateTime = "2016-11-11 00:00:00";
			String edDateTime = "2016-11-11 23:59:59";
			String totalInvest = bannerActivityBean.getInvestorsTotal(stDateTime, edDateTime);
			String totalInvestUsers = bannerActivityBean.getInvestorsUsersTotal(stDateTime, edDateTime);
			request.setAttribute("totalInvest", QwyUtil.calcNumber(totalInvest, 5, "*").toString());
			request.setAttribute("totalInvestUsers", QwyUtil.calcNumber(totalInvestUsers, 5, "*").toString());
		} catch (Exception e) {
			
			log.error("操作异常: ",e);
		}
		return "singleDay";
	}
	
	/**  对手机号码进行解密   后台直接进行解密即可  如果是显示给到界面可用*号代替中间数字
	 * @param strList
	 * @return
	 */
	public List<Object[]> formartList(List<Object[]> strList){
		if(QwyUtil.isNullAndEmpty(strList))
			return null;
		
		for (Object[] string : strList) {
			String name = DESEncrypt.jieMiUsername(string[1].toString());
			string[1]=name;
		}
		return strList;
	}
	
	/**
	 * 奖金排行榜
	 * @return
	 */
	public String bonusRanking() {
		String stDateTime = "2016-11-11 00:00:00";
		String edDateTime = "2016-11-11 23:59:59";
		
		try {
			request = getRequest();
			UsersAdmin users = (UsersAdmin)getRequest().getSession().getAttribute("usersAdmin");
			if(QwyUtil.isNullAndEmpty(users)){
				return "login";
			}
		 PageUtil pageUtil=new PageUtil();
		 pageUtil.setCurrentPage(currentPage);
	     pageUtil.setPageSize(pageSize);
	     
	     StringBuffer url = new StringBuffer();
		 url.append(getRequest().getServletContext().getContextPath());
		 url.append("/Product/Admin/bannerActivity!bonusRanking.action?");
		 
		 pageUtil.setPageUrl(url.toString());
		 
		 pageUtil = bannerActivityBean.getbonusRanking(pageUtil,stDateTime,edDateTime);
		 if(!QwyUtil.isNullAndEmpty(pageUtil)){
			 	List list = pageUtil.getList();
			 
				request.setAttribute("list", list);
			}
		 request.setAttribute("pageUtil", pageUtil);
		 
		 return "bonusRanking";
		
		} catch (Exception e) {
			
			log.error("操作异常: ",e);
		}
		
		return null;
	}
	
	
	/**
	 * 情人节活动   总投资额 数据
	 * 2017-02-13 -- 2017-02-15
	 * @return
	 */
	public String loadValentineDay() {
		String stDateTime = "2017-02-13 00:00:00";
		String edDateTime = "2017-02-15 23:59:59";
		
		try {
			request = getRequest();
			UsersAdmin users = (UsersAdmin)getRequest().getSession().getAttribute("usersAdmin");
			if(QwyUtil.isNullAndEmpty(users)){
				return "login";
			}
		 PageUtil pageUtil=new PageUtil();
		 pageUtil.setCurrentPage(currentPage);
	     pageUtil.setPageSize(pageSize);
	     
	     StringBuffer url = new StringBuffer();
		 url.append(getRequest().getServletContext().getContextPath());
		 url.append("/Product/Admin/bannerActivity!loadValentineDay.action?");
		 
		 if (!QwyUtil.isNullAndEmpty(username)) {
			url.append("&username=");
			url.append(username);
		 }
		 if (!QwyUtil.isNullAndEmpty(sumMoney)) {
			 url.append("&sumMoney=");
			 url.append(sumMoney);
		 }
		 
		 pageUtil.setPageUrl(url.toString());
		 
		 pageUtil = bannerActivityBean.loadValentineDayInvestor(pageUtil,stDateTime,edDateTime,username,sumMoney);
		 request.setAttribute("username", username);
		 request.setAttribute("sumMoney", sumMoney);
		 if(!QwyUtil.isNullAndEmpty(pageUtil)){
			 	List list = pageUtil.getList();
			 
				request.setAttribute("list", list);
			}
		 request.setAttribute("pageUtil", pageUtil);
		 
		 return "valentineDay";
		
		} catch (Exception e) {
			
			log.error("操作异常: ",e);
		}
		
		return null;
	}
	
	/**
	 * 情人节活动   首次投资额
	 * @return
	 */
	public String loadFirstInvValentine() {
		String stDateTime = "2017-02-13 00:00:00";
		String edDateTime = "2017-02-15 23:59:59";
		
		try {
			request = getRequest();
			UsersAdmin users = (UsersAdmin)getRequest().getSession().getAttribute("usersAdmin");
			if(QwyUtil.isNullAndEmpty(users)){
				return "login";
			}
		 PageUtil pageUtil=new PageUtil();
		 pageUtil.setCurrentPage(currentPage);
	     pageUtil.setPageSize(pageSize);
	     
	     StringBuffer url = new StringBuffer();
		 url.append(getRequest().getServletContext().getContextPath());
		 url.append("/Product/Admin/bannerActivity!loadFirstInvValentine.action?");
		 
		 if (!QwyUtil.isNullAndEmpty(username)) {
			url.append("&username=");
			url.append(username);
		}
		 if (!QwyUtil.isNullAndEmpty(sumMoney)) {
			 url.append("&sumMoney=");
			 url.append(sumMoney);
		 }
		 
		 pageUtil.setPageUrl(url.toString());
		 
		 pageUtil = bannerActivityBean.loadValentineFInvestors(pageUtil,stDateTime,edDateTime,username,sumMoney);
		 request.setAttribute("username", username);
		 request.setAttribute("sumMoney", sumMoney);
		 if(!QwyUtil.isNullAndEmpty(pageUtil)){
			 	List list = pageUtil.getList();
			 
				request.setAttribute("list", list);
			}
		 request.setAttribute("pageUtil", pageUtil);
		 
		 return "VDfirstInv";
		
		} catch (Exception e) {
			
			log.error("操作异常: ",e);
		}
		
		return null;
	}
	
	/**
	 * 情人节活动地址表
	 * @return
	 */
	public String loadAddress() {
		String stDateTime = "2017-02-13 00:00:00";
		String edDateTime = "2017-02-15 23:59:59";
		
		try {
			request = getRequest();
			UsersAdmin users = (UsersAdmin)getRequest().getSession().getAttribute("usersAdmin");
			if(QwyUtil.isNullAndEmpty(users)){
				return "login";
			}
		 PageUtil pageUtil=new PageUtil();
		 pageUtil.setCurrentPage(currentPage);
	     pageUtil.setPageSize(pageSize);
	     
	     StringBuffer url = new StringBuffer();
		 url.append(getRequest().getServletContext().getContextPath());
		 url.append("/Product/Admin/bannerActivity!loadAddress.action?");
		 
		 if (!QwyUtil.isNullAndEmpty(username)) {
			url.append("&username=");
			url.append("username");
		}
		 
		 pageUtil.setPageUrl(url.toString());
		 
		 pageUtil = bannerActivityBean.loadAddress(pageUtil,stDateTime,edDateTime,username);
		 request.setAttribute("username", username);
		 if(!QwyUtil.isNullAndEmpty(pageUtil)){
			 	List list = pageUtil.getList();
			 
				request.setAttribute("list", list);
			}
		 request.setAttribute("pageUtil", pageUtil);
		 
		 return "muaddress";
		
		} catch (Exception e) {
			
			log.error("操作异常: ",e);
		}
		
		return null;
	}
	
	
	/**
	 *   修改情人节活动地址表
	 *  @return
	 */
	public String modifyAddress() {
		String json="";
		try {
			request = getRequest();
			UsersAdmin users = (UsersAdmin)getRequest().getSession().getAttribute("usersAdmin");
			if(QwyUtil.isNullAndEmpty(users)){
				return "login";
			}
			
			if (!QwyUtil.isNullAndEmpty(muserAddress)) {
				if(bannerActivityBean.modifyAddress(muserAddress.getId(), muserAddress.getAddress())){
					json = QwyUtil.getJSONString("ok","修改成功");
				}else {
					json = QwyUtil.getJSONString("error","修改失败");
				}
			}else{
				json = QwyUtil.getJSONString("error","地址不能为空");
			}
		
		} catch (Exception e) {
			
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("error","修改异常");
		}
		
		try {
			QwyUtil.printJSON(getResponse(), json);
			return null;
		} catch (IOException e) {
			
			log.error("操作异常: ",e);
		}
		
		return null;
	}
	
	
	/**
	 * 土豪星球邀请投资详情
	 * @return  2017-03-22  2017-04-15
	 */
	public String getRichData() {
		try {
			request = getRequest();
			UsersAdmin users = (UsersAdmin)getRequest().getSession().getAttribute("usersAdmin");
			if(QwyUtil.isNullAndEmpty(users)){
				return "login";
			}
		 PageUtil<Object[]> pageUtil=new PageUtil<Object[]>();
		 pageUtil.setCurrentPage(currentPage);
	     pageUtil.setPageSize(pageSize);
	     
	     StringBuffer url = new StringBuffer();
		 url.append(getRequest().getServletContext().getContextPath());
		 url.append("/Product/Admin/bannerActivity!getRichData.action?");
		 
		 if (!QwyUtil.isNullAndEmpty(username)) {
			url.append("&username=");
			url.append(username);
		}
		 pageUtil.setPageUrl(url.toString());
		 
		 pageUtil = bannerActivityBean.getRichData(pageUtil,insertTime);
		 request.setAttribute("insertTime", insertTime);
		 if(!QwyUtil.isNullAndEmpty(pageUtil)){
			getRequest().setAttribute("table", "1");
			request.setAttribute("list", pageUtil.getList());
			}
		 request.setAttribute("pageUtil", pageUtil);
		 
		 return "rich";
		
		} catch (Exception e) {
			
			log.error("操作异常: ",e);
		}
		
		return null;
	}
	
	/**
	 * 导出 土豪星球被邀请者投资状况 数据报表 
	 * @return
	 */
	public String iportRichData() {
        try {
            String name = QwyUtil.fmyyyyMMddHHmmss3.format(new Date()) + "_richData";
            PageUtil<Object[]> pageUtil = new PageUtil<Object[]>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(999999);
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("土豪星球被邀请者投资情况");
            HSSFRow row = sheet.createRow((int) 1);
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
            row = sheet.createRow(0);
            row.createCell(0).setCellValue("序号");
            row.createCell(1).setCellValue("被邀请者");
            row.createCell(2).setCellValue("产品名称");
            row.createCell(3).setCellValue("投入金额");
            row.createCell(4).setCellValue("理财券");
            row.createCell(5).setCellValue("红包");
            row.createCell(6).setCellValue("订单生成时间");
            row.createCell(7).setCellValue("注册时间");
            row.createCell(8).setCellValue("邀请者");
            
            pageUtil = bannerActivityBean.getRichData(pageUtil, insertTime);
            List objectsList = pageUtil.getList();
            
            if (!QwyUtil.isNullAndEmpty(objectsList)) {
            	for (int i = 0; i < objectsList.size(); i++) {
            		row = sheet.createRow((int) i + 1);
					Object[] objects = (Object[]) objectsList.get(i);
					row.createCell(1).setCellValue(DESEncrypt.jieMiUsername(objects[0]+""));
					row.createCell(2).setCellValue(QwyUtil.isNullAndEmpty(objects[1])?"--":objects[1]+"");
					row.createCell(3).setCellValue(QwyUtil.isNullAndEmpty(objects[3])?"--":objects[3]+"");
					row.createCell(4).setCellValue(QwyUtil.isNullAndEmpty(objects[4])?"--":objects[4]+"");
					row.createCell(5).setCellValue(QwyUtil.isNullAndEmpty(objects[5])?"--":objects[5]+"");
					row.createCell(6).setCellValue(QwyUtil.isNullAndEmpty(objects[6])?"--":objects[6]+"");
					row.createCell(7).setCellValue(QwyUtil.isNullAndEmpty(objects[7])?"--":objects[7]+"");
					row.createCell(8).setCellValue(DESEncrypt.jieMiUsername(objects[2]+""));
					
//					row.createCell(14).setCellValue(QwyUtil.calcNumber(objects[10], objects[11], "-").doubleValue());
				}
				
			}

            String realPath = request.getServletContext().getRealPath("/report/" + name + ".xls");
            FileOutputStream fout = new FileOutputStream(realPath);
            wb.write(fout);
            fout.close();
            response.getWriter().write("/report/" + name + ".xls");
           
        } catch (Exception e) {
            log.error("操作异常: ",e);
        }
        return null;
    }
	
	/**
	 * 后台 不如投一场投资数据
	 * @return
	 */
	public String getTycData(){
		try {
			
			request = getRequest();
			UsersAdmin users = (UsersAdmin)getRequest().getSession().getAttribute("usersAdmin");
			if(QwyUtil.isNullAndEmpty(users)){
				return "login";
			}
			String stTime = "2017-04-15 00:00:00";
			String etTime = "2017-04-27 23:59:59";
			
			PageUtil<Object[]> pageUtil=new PageUtil<Object[]>();
			pageUtil.setCurrentPage(currentPage);
		    pageUtil.setPageSize(pageSize);
		     
		    StringBuffer url = new StringBuffer();
			url.append(getRequest().getServletContext().getContextPath());
			url.append("/Product/Admin/bannerActivity!getTycData.action?");
			 
			if (!QwyUtil.isNullAndEmpty(username)) {
				url.append("&username=");
				url.append(username);
			}
			pageUtil.setPageUrl(url.toString());
			
			pageUtil = bannerActivityBean.getTycData(pageUtil, username, stTime, etTime);
			
			if (!QwyUtil.isNullAndEmpty(pageUtil)) {
				List<Object[]> list = pageUtil.getList();
				if (!QwyUtil.isNullAndEmpty(list)) {
					for(Object[] object : list){
						double sumMoney = (QwyUtil.isNullAndEmpty(object[3])?0:Double.parseDouble(object[3]+""));
						if (sumMoney >= 180000) {
							object[6] = "1";
						}else if (sumMoney >= 100000) {
							object[6] = "2";
						}else if (sumMoney >= 50000) {
							object[6] = "3";
						}else if (sumMoney >= 15000) {
							object[6] = "4";
						}else if (sumMoney >= 8000) {
							object[6] = "5";
						}else {
							object[6] = "6";
						}
					}
				}
				pageUtil.setList(list);
				request.setAttribute("list", pageUtil.getList());
			}
			request.setAttribute("pageUtil", pageUtil);
			request.setAttribute("table", "1");
			
			return "tyc";
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		
		return null;
	}
	
	
	/**
	 * 导出 投一场后台  数据报表 
	 * @return
	 */
	public String iportTycData() {
        try {
            String name = QwyUtil.fmyyyyMMddHHmmss3.format(new Date()) + "_tycData";
            String stTime = "2017-04-15 00:00:00";
			String etTime = "2017-04-27 23:59:59";
			
            PageUtil<Object[]> pageUtil = new PageUtil<Object[]>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(999999);
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("投一场 后台数据报表");
            HSSFRow row = sheet.createRow((int) 1);
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
            row = sheet.createRow(0);
            row.createCell(0).setCellValue("序号");
            row.createCell(1).setCellValue("用户ID");
            row.createCell(2).setCellValue("用户名");
            row.createCell(3).setCellValue("真实姓名");
            row.createCell(4).setCellValue("投资总额（不含理财券）");
            row.createCell(5).setCellValue("理财券总额");
            row.createCell(6).setCellValue("奖品");
            row.createCell(7).setCellValue("收件人");
            row.createCell(8).setCellValue("联系电话");
            row.createCell(9).setCellValue("地址");
            row.createCell(10).setCellValue("详细地址");
            row.createCell(11).setCellValue("邮政编码");
            
            pageUtil = bannerActivityBean.getTycData(pageUtil, username, stTime, etTime);
            List objectsList = pageUtil.getList();
            
            if (!QwyUtil.isNullAndEmpty(objectsList)) {
            	for (int i = 0; i < objectsList.size(); i++) {
            		row = sheet.createRow((int) i + 1);
					Object[] objects = (Object[]) objectsList.get(i);
					row.createCell(0).setCellValue(i+1);
					row.createCell(1).setCellValue(objects[0]+"");
					row.createCell(2).setCellValue(DESEncrypt.jieMiUsername(objects[1]+""));
					row.createCell(3).setCellValue(QwyUtil.isNullAndEmpty(objects[2])?"--":objects[2]+"");
					row.createCell(4).setCellValue(QwyUtil.isNullAndEmpty(objects[3])?"--":objects[3]+"");
					row.createCell(5).setCellValue(QwyUtil.isNullAndEmpty(objects[4])?"--":objects[4]+"");
//					row.createCell(6).setCellValue(QwyUtil.isNullAndEmpty(objects[6])?"--":objects[6]+"");
					row.createCell(7).setCellValue(QwyUtil.isNullAndEmpty(objects[7])?"--":objects[7]+"");
					row.createCell(8).setCellValue(QwyUtil.isNullAndEmpty(objects[8])?"--":objects[8]+"");
					row.createCell(9).setCellValue(QwyUtil.isNullAndEmpty(objects[9])?"--":objects[9]+"");
					row.createCell(10).setCellValue(QwyUtil.isNullAndEmpty(objects[10])?"--":objects[10]+"");
					row.createCell(11).setCellValue(QwyUtil.isNullAndEmpty(objects[11])?"--":objects[11]+"");
					
					double sumMoney = (QwyUtil.isNullAndEmpty(objects[3])?0:Double.parseDouble(objects[3]+""));
					if (sumMoney >= 180000) {
						row.createCell(6).setCellValue("1000元京东卡");
					}else if (sumMoney >= 100000) {
						row.createCell(6).setCellValue("500元京东卡");
					}else if (sumMoney >= 50000) {
						row.createCell(6).setCellValue("200元京东卡");
					}else if (sumMoney >= 15000) {
						row.createCell(6).setCellValue("50元京东卡");
					}else if (sumMoney >= 8000) {
						row.createCell(6).setCellValue("买一送一券");
					}else {
						row.createCell(6).setCellValue("未达标");
					}
				}
				
			}

            String realPath = request.getServletContext().getRealPath("/report/" + name + ".xls");
            FileOutputStream fout = new FileOutputStream(realPath);
            wb.write(fout);
            fout.close();
            response.getWriter().write("/report/" + name + ".xls");
           
        } catch (Exception e) {
            log.error("操作异常: ",e);
        }
        return null;
    }
	
	/**
	 * 母亲节留言
	 * @return
	 */
	public String getMDay(){
		try {
			
			request = getRequest();
			UsersAdmin users = (UsersAdmin)getRequest().getSession().getAttribute("usersAdmin");
			if(QwyUtil.isNullAndEmpty(users)){
				return "login";
			}
			String stTime = "2017-05-09 00:00:00";
			String etTime = "2017-05-17 23:59:59";
			
			PageUtil<Object[]> pageUtil=new PageUtil<Object[]>();
			pageUtil.setCurrentPage(currentPage);
		    pageUtil.setPageSize(pageSize);
		     
		    StringBuffer url = new StringBuffer();
			url.append(getRequest().getServletContext().getContextPath());
			url.append("/Product/Admin/bannerActivity!getMDay.action?");
			 
			if (!QwyUtil.isNullAndEmpty(username)) {
				url.append("&username=");
				url.append(username);
			}
			
			if (!QwyUtil.isNullAndEmpty(insertTime)) {
				url.append("&insertTime=");
				url.append(insertTime);
			}
			
			pageUtil.setPageUrl(url.toString());
			request.setAttribute("username", username);
			request.setAttribute("insertTime", insertTime);
			
			pageUtil = bannerActivityBean.getMessageList(pageUtil, insertTime, username);
			
			if (!QwyUtil.isNullAndEmpty(pageUtil)) {
				request.setAttribute("list", pageUtil.getList());
			}
			request.setAttribute("pageUtil", pageUtil);
			request.setAttribute("table", "1");
			
			return "mday";
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		
		
		return "mday";
	}
	
	public String iportMdayData(){
		try {
            String name = QwyUtil.fmyyyyMMddHHmmss3.format(new Date()) + "_mDayData";
            PageUtil<Object[]> pageUtil = new PageUtil<Object[]>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(999999);
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("母亲节留言报表");
            HSSFRow row = sheet.createRow((int) 1);
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
            row = sheet.createRow(0);
            row.createCell(0).setCellValue("序号");
            row.createCell(1).setCellValue("用户名");
            row.createCell(2).setCellValue("真实姓名");
            row.createCell(3).setCellValue("留言时间");
            row.createCell(4).setCellValue("留言内容");
            
            pageUtil = bannerActivityBean.getMessageList(pageUtil, insertTime, username);
            List objectsList = pageUtil.getList();
            
            if (!QwyUtil.isNullAndEmpty(objectsList)) {
            	for (int i = 0; i < objectsList.size(); i++) {
            		row = sheet.createRow((int) i + 1);
					Object[] objects = (Object[]) objectsList.get(i);
					row.createCell(0).setCellValue(i+1);
					row.createCell(1).setCellValue(DESEncrypt.jieMiUsername(objects[0]+""));
					row.createCell(2).setCellValue(QwyUtil.isNullAndEmpty(objects[1])?"--":objects[1]+"");
					row.createCell(3).setCellValue(QwyUtil.isNullAndEmpty(objects[2])?"--":objects[2]+"");
					row.createCell(4).setCellValue(QwyUtil.isNullAndEmpty(objects[3])?"--":objects[3]+"");
				}
				
			}

            String realPath = request.getServletContext().getRealPath("/report/" + name + ".xls");
            FileOutputStream fout = new FileOutputStream(realPath);
            wb.write(fout);
            fout.close();
            response.getWriter().write("/report/" + name + ".xls");
           
        } catch (Exception e) {
            log.error("操作异常: ",e);
        }
        return null;
		
		
	}
	
	/**
	 * 端午后台数据
	 * @return
	 */
	public String getDuanWuDate(){
		String json = "";
		try {
			request = getRequest();
			UsersAdmin users = (UsersAdmin)getRequest().getSession().getAttribute("usersAdmin");
			if(QwyUtil.isNullAndEmpty(users)){
				return "login";
			}
			String stTime = "2017-05-23 00:00:00";
			String etTime = "2017-06-02 23:59:59";
			String czEt = "2017-05-27 23:59:59";
			
			PageUtil<Object[]> pageUtil=new PageUtil<Object[]>();
			pageUtil.setCurrentPage(currentPage);
		    pageUtil.setPageSize(pageSize);
		     
		    StringBuffer url = new StringBuffer();
			url.append(getRequest().getServletContext().getContextPath());
			url.append("/Product/Admin/bannerActivity!getDuanWuDate.action?");
			 
			if (!QwyUtil.isNullAndEmpty(username)) {
				url.append("&username=");
				url.append(username);
			}
			
			/*if (!QwyUtil.isNullAndEmpty(insertTime)) {
				url.append("&insertTime=");
				url.append(insertTime);
			}*/
			
			pageUtil.setPageUrl(url.toString());
			request.setAttribute("username", username);
			request.setAttribute("insertTime", insertTime);
			
			//拥有粽子的用户
			pageUtil = bannerActivityBean.dwRecord(pageUtil, username, stTime, etTime, czEt);
			
			if (!QwyUtil.isNullAndEmpty(pageUtil)) {
				request.setAttribute("list", pageUtil.getList());
				request.setAttribute("pageUtil", pageUtil);
				request.setAttribute("table", "1");
			}
			
			return "dw";
			
			
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return "dw";
	}
	
	/**
	 * 
	 * @return
	 */
	public String iportDwData(){
		try {
			String stTime = "2017-05-23 00:00:00";
			String etTime = "2017-06-02 23:59:59";
			String czEt = "2017-05-27 23:59:59";
			
            String name = QwyUtil.fmyyyyMMddHHmmss3.format(new Date()) + "_dwData";
            PageUtil<Object[]> pageUtil = new PageUtil<Object[]>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(999999);
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("端午节活动数据");
            HSSFRow row = sheet.createRow((int) 1);
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
            row = sheet.createRow(0);
            row.createCell(0).setCellValue("序号");
            row.createCell(1).setCellValue("用户名");
            row.createCell(2).setCellValue("真实姓名");
            row.createCell(3).setCellValue("活动充值总额");
            row.createCell(4).setCellValue("活动投资总额");
            row.createCell(5).setCellValue("粽子总额");
            row.createCell(6).setCellValue("兑换实物");
            row.createCell(7).setCellValue("兑换时间");
            row.createCell(8).setCellValue("地址");
            
            pageUtil = bannerActivityBean.dwRecord(pageUtil, username, stTime, etTime, czEt);
            List objectsList = pageUtil.getList();
            
            if (!QwyUtil.isNullAndEmpty(objectsList)) {
            	for (int i = 0; i < objectsList.size(); i++) {
            		row = sheet.createRow((int) i + 1);
					Object[] objects = (Object[]) objectsList.get(i);
					row.createCell(0).setCellValue(i+1);
					row.createCell(1).setCellValue(DESEncrypt.jieMiUsername(objects[0]+""));
					row.createCell(2).setCellValue(QwyUtil.isNullAndEmpty(objects[1])?"--":objects[1]+"");
					row.createCell(3).setCellValue(QwyUtil.isNullAndEmpty(objects[2])?"--":objects[2]+"");
					row.createCell(4).setCellValue(QwyUtil.isNullAndEmpty(objects[3])?"--":objects[3]+"");
					row.createCell(5).setCellValue(QwyUtil.isNullAndEmpty(objects[4])?"--":objects[4]+"");
					row.createCell(6).setCellValue(QwyUtil.isNullAndEmpty(objects[5])?"--":objects[5]+"");
					row.createCell(7).setCellValue(QwyUtil.isNullAndEmpty(objects[7])?"--":objects[7]+"");
					row.createCell(8).setCellValue(QwyUtil.isNullAndEmpty(objects[6])?"--":objects[6]+"");
				}
				
			}

            String realPath = request.getServletContext().getRealPath("/report/" + name + ".xls");
            FileOutputStream fout = new FileOutputStream(realPath);
            wb.write(fout);
            fout.close();
            response.getWriter().write("/report/" + name + ".xls");
           
        } catch (Exception e) {
            log.error("操作异常: ",e);
        }
        return null;
		
	}
	
	/**
	 * 补发抽奖机会
	 * @return
	 */
	public String sendLotteryTimes(){
		String json = "";
		try {
			 UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
			 if (QwyUtil.isNullAndEmpty(users)) {
				 json = QwyUtil.getJSONString("err", "管理员未登录");
	             QwyUtil.printJSON(getResponse(), json);
	              // 管理员没有登录;
	             return null;
	          }
	         Users us = sendCouponBean.getUsersByUsername(username);
	         if (QwyUtil.isNullAndEmpty(us)) {
	            // 没有找到这个用户;
		        json = QwyUtil.getJSONString("err", "用户名不存在");
		        QwyUtil.printJSON(getResponse(), json);
		        return null;
	         }
	         
	         if (QwyUtil.isNullAndEmpty(lotteryTimes)) {
				// 奖励对象为空
	        	 json = QwyUtil.getJSONString("err", "检查发送的机会是否正确~");
	        	 QwyUtil.printJSON(getResponse(), json);
	        	 return null;
			}
	        
	        LotteryTimes lt = bannerActivityBean.findLotteryTimesByUId(us.getId(), lotteryTimes.getType());
	        if (QwyUtil.isNullAndEmpty(lt)) {
				// 对象为空时 则新建对象
	        	String id = bannerActivityBean.addLotteryTimes(lotteryTimes, us);
	        	if (!QwyUtil.isNullAndEmpty(id)) {
					//保存增加抽奖机会的记录
	        		String lrId = bannerActivityBean.addLotteryRecord("0", us.getId(), note, null);
	        		if (!QwyUtil.isNullAndEmpty(lrId)) {
	        			json = QwyUtil.getJSONString("ok", "补发机会成功");;
					}else{
						json = QwyUtil.getJSONString("err", "补发记录失败");
					}
				}else{
					json = QwyUtil.getJSONString("err", "补发机会失败");
				}
			}else{
				//对象不为空时  修改该对象的次数
				boolean isOk = bannerActivityBean.updateLottery(lt, lotteryTimes.getFreeNum());
				if (isOk) {
					String lrId = bannerActivityBean.addLotteryRecord("0", us.getId(), note, null);
					if (!QwyUtil.isNullAndEmpty(lrId)) {
						json = QwyUtil.getJSONString("ok", "补发机会成功");
					}else{
						json = QwyUtil.getJSONString("err", "补发记录失败");
					}
				}else{
					json = QwyUtil.getJSONString("ok", "补发机会失败");
				}
			}
	         
	        QwyUtil.printJSON(getResponse(), json);
			return null;
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("err", "补发异常");
			try {
				QwyUtil.printJSON(getResponse(), json);
			} catch (IOException e1) {
				log.error("操作异常",e1);
			}
			return null;
		}
		
	}

	public String getMobileNum() {
		return mobileNum;
	}

	public void setMobileNum(String mobileNum) {
		this.mobileNum = mobileNum;
	}

	public String getXlhNum() {
		return xlhNum;
	}

	public void setXlhNum(String xlhNum) {
		this.xlhNum = xlhNum;
	}
	public Integer getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getSumMoney() {
		return sumMoney;
	}

	public void setSumMoney(String sumMoney) {
		this.sumMoney = sumMoney;
	}

	public MUsersAddress getMuserAddress() {
		return muserAddress;
	}

	public void setMuserAddress(MUsersAddress muserAddress) {
		this.muserAddress = muserAddress;
	}
	
	public String getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(String insertTime) {
		this.insertTime = insertTime;
	}

	public LotteryTimes getLotteryTimes() {
		return lotteryTimes;
	}

	public void setLotteryTimes(LotteryTimes lotteryTimes) {
		this.lotteryTimes = lotteryTimes;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

}
