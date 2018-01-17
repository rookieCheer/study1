package com.huoq.admin.product.action;

import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;

import com.huoq.admin.product.bean.WinnerBean;
import com.huoq.common.action.BaseAction;
import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.CzRecord;
import com.huoq.orm.Investors;
import com.huoq.orm.UsersAdmin;
import com.huoq.orm.Winner;

@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/Admin")
@Results({
	@Result(name = "winner", value = "/Product/Admin/operationManager/winner.jsp"),
	@Result(name = "lr", value = "/Product/Admin/operationManager/lotteryRecord.jsp"),
	@Result(name = "prizeWinner", value = "/Product/Admin/operationManager/prizeWinner.jsp"),
	@Result(name = "hot", value = "/Product/Admin/operationManager/hotData.jsp")
})
public class WinnerAction extends BaseAction  {
	private String username;
	private String insertTime;
	
	private Long prizeId;
	private String usersId;
	private String type;
	
	public String getInsertTime() {
		return insertTime;
	}
	public void setInsertTime(String insertTime) {
		this.insertTime = insertTime;
	}

	@Resource
	private WinnerBean bean;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	private PageUtil< Winner> pageUtil;
	private Integer currentPage = 1;
	public Integer getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	private Integer pageSize = 50;
	
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public String showWinner(){
		request = getRequest();
		UsersAdmin users = (UsersAdmin)getRequest().getSession().getAttribute("usersAdmin");
		if(QwyUtil.isNullAndEmpty(users)){
			return "login";
		}
		
		PageUtil<Winner> pageUtil = new PageUtil<Winner>();
		pageUtil.setCurrentPage(currentPage);
		pageUtil.setPageSize(pageSize);
		
		StringBuffer url = new StringBuffer();
		url.append(getRequest().getServletContext().getContextPath());
		url.append("/Product/Admin/winner!showWinner.action?");
		if(!QwyUtil.isNullAndEmpty(insertTime)){
			url.append("&insertTime=");
			url.append(insertTime);
		}
		if(!QwyUtil.isNullAndEmpty(username)){
			url.append("&username=");
			url.append(username);
		}
		pageUtil.setPageUrl(url.toString());
		pageUtil = bean.loadWinner(username, insertTime, pageUtil);
		getRequest().setAttribute("username", username);
		getRequest().setAttribute("insertTime", insertTime);
		if (!QwyUtil.isNullAndEmpty(pageUtil)) {
			getRequest().setAttribute("pageUtil", pageUtil);
			getRequest().setAttribute("list", pageUtil.getList());
			return "winner";
		}
		
		return null;
		
	}
	
	
	/**
	 * 获取中奖人信息
	 * @return
	 */
	public String getWinnerList(){
		
		UsersAdmin users = (UsersAdmin)getRequest().getSession().getAttribute("usersAdmin");
		if(QwyUtil.isNullAndEmpty(users)){
			return "login";
		}
		try {
			//分页对象
			PageUtil<Object[]> pageUtil = new PageUtil<Object[]>();
			pageUtil.setCurrentPage(currentPage);
			pageUtil.setPageSize(pageSize);
			
			StringBuffer url = new StringBuffer();
			url.append(getRequest().getServletContext().getContextPath());
			url.append("/Product/Admin/winner!getWinnerList.action?");
			
			if (!QwyUtil.isNullAndEmpty(username)) {
				url.append("&username=");
				url.append(username);
			}
			if (!QwyUtil.isNullAndEmpty(prizeId)) {
				url.append("&prizeId=");
				url.append(prizeId);
			}
			
			if (!QwyUtil.isNullAndEmpty(type)) {
				url.append("&type=");
				url.append(type);
			}
			
			if (!QwyUtil.isNullAndEmpty(insertTime)) {
				url.append("&insertTime=");
				url.append(insertTime);
			}
			
			pageUtil.setPageUrl(url.toString());
	//		List kindOfPrizeList = bean.getWinnerCount();
			
			// 奖品的ID和名字 六月热活动 奖品type为2
			List prizeIdAndName = bean.getPrizeId("2");
			
	//		getRequest().setAttribute("kindOfPrizeList", kindOfPrizeList);
			getRequest().setAttribute("prizeIdAndName", prizeIdAndName);
			getRequest().setAttribute("prizeId", prizeId);
			getRequest().setAttribute("username", username);
			getRequest().setAttribute("type", type);
			getRequest().setAttribute("insertTime", insertTime);
			//getRequest().setAttribute("currentPage", currentPage);
			
			//对时间参数判断
			String stTime = "2017-06-05 00:00:00";
			String etTime = "2017-06-26 23:59:59";

			pageUtil = bean.getWinnerList(pageUtil,username,prizeId,stTime,etTime,"2","3",insertTime);
			if (!QwyUtil.isNullAndEmpty(pageUtil)) {
				getRequest().setAttribute("pageUtil", pageUtil);
				getRequest().setAttribute("list", pageUtil.getList());
				getRequest().setAttribute("table", "1");
				
				return "prizeWinner";
			}
		} catch (Exception e) {
			log.error("操作异常",e);
		}
		return null;
	}
	
	
	/**
	 * 抽奖记录数据
	 * @return
	 */
	public String getLotterRecord(){
		UsersAdmin users = (UsersAdmin)getRequest().getSession().getAttribute("usersAdmin");
		if(QwyUtil.isNullAndEmpty(users)){
			return "login";
		}
		
		PageUtil<Object[]> pageUtil = new PageUtil<Object[]>();
		pageUtil.setCurrentPage(currentPage);
		pageUtil.setPageSize(pageSize);
		
		StringBuffer url = new StringBuffer();
		url.append(getRequest().getServletContext().getContextPath());
		url.append("/Product/Admin/winner!getLotterRecord.action?");
		
		if (!QwyUtil.isNullAndEmpty(username)) {
			url.append("&username=");
			url.append(username);
		}
		if (!QwyUtil.isNullAndEmpty(type)) {
			url.append("&type=");
			url.append(type);
		}
		
		getRequest().setAttribute("type", type);
		
		//分页查询 抽奖记录数据
		pageUtil = bean.getLotteryRecord(pageUtil, username, type, "2017-06-05 00:00:00", "2017-06-26 23:59:59");
		if (!QwyUtil.isNullAndEmpty(pageUtil)) {
			getRequest().setAttribute("pageUtil", pageUtil);
			getRequest().setAttribute("list", pageUtil.getList());
			getRequest().setAttribute("table", "1");
		}
		
		return "lr";
	}
	
	/**
	 * 导出 抽奖机会记录 报表  
	 * @return
	 */
	public String iportLotteryRecordData(){
		try {
            String name = QwyUtil.fmyyyyMMddHHmmss3.format(new Date()) + "_lotteryRecordData";
            PageUtil<Object[]> pageUtil = new PageUtil<Object[]>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(999999);
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("抽奖机会记录");
            HSSFRow row = sheet.createRow((int) 1);
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
            row = sheet.createRow(0);
            row.createCell(0).setCellValue("序号");
            row.createCell(1).setCellValue("用户名");
            row.createCell(2).setCellValue("真实姓名");
            row.createCell(3).setCellValue("活动时间");
            row.createCell(4).setCellValue("备注");
            
        	pageUtil = bean.getLotteryRecord(pageUtil, username, type, "2017-06-05 00:00:00", "2017-06-26 23:59:59");
            List objectsList = pageUtil.getList();
            
            if (!QwyUtil.isNullAndEmpty(objectsList)) {
            	for (int i = 0; i < objectsList.size(); i++) {
            		row = sheet.createRow((int) i + 1);
					Object[] objects = (Object[]) objectsList.get(i);
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
	 * 导出 抽奖机会记录 报表  
	 * @return
	 */
	public String iportWinnerData(){
		try {
            String name = QwyUtil.fmyyyyMMddHHmmss3.format(new Date()) + "_winnerData";
            PageUtil<Object[]> pageUtil = new PageUtil<Object[]>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(999999);
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("中奖信息记录");
            HSSFRow row = sheet.createRow((int) 1);
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
            row = sheet.createRow(0);
            row.createCell(0).setCellValue("序号");
            row.createCell(1).setCellValue("用户名");
            row.createCell(2).setCellValue("真实姓名");
//            row.createCell(3).setCellValue("累计投资额");
//            row.createCell(4).setCellValue("剩余抽奖次数");
//            row.createCell(5).setCellValue("已用抽奖次数");
            row.createCell(3).setCellValue("奖品");
            row.createCell(4).setCellValue("抽奖时间");
            row.createCell(5).setCellValue("收货人姓名");
            row.createCell(6).setCellValue("收货人电话");
            row.createCell(7).setCellValue("收货人地址");
            row.createCell(8).setCellValue("收货人邮编");
            
            //对时间参数判断
			String stTime = "2017-06-05 00:00:00";
			String etTime = "2017-06-26 23:59:59";
			pageUtil = bean.getWinnerList(pageUtil,username,prizeId,stTime,etTime,"2","3",insertTime);
            List objectsList = pageUtil.getList();
            
            if (!QwyUtil.isNullAndEmpty(objectsList)) {
            	for (int i = 0; i < objectsList.size(); i++) {
            		row = sheet.createRow((int) i + 1);
					Object[] objects = (Object[]) objectsList.get(i);
					row.createCell(1).setCellValue(DESEncrypt.jieMiUsername(objects[0]+""));
					row.createCell(2).setCellValue(QwyUtil.isNullAndEmpty(objects[1])?"--":objects[1]+"");
//					row.createCell(3).setCellValue(QwyUtil.isNullAndEmpty(objects[2])?"--":objects[2]+"");
//					row.createCell(4).setCellValue(QwyUtil.isNullAndEmpty(objects[3])?"--":objects[3]+"");
//					row.createCell(5).setCellValue(QwyUtil.isNullAndEmpty(objects[4])?"--":objects[4]+"");
					row.createCell(3).setCellValue(QwyUtil.isNullAndEmpty(objects[2])?"--":objects[2]+"");
					row.createCell(4).setCellValue(QwyUtil.isNullAndEmpty(objects[9])?"--":objects[9]+"");
					row.createCell(5).setCellValue(QwyUtil.isNullAndEmpty(objects[3])?"--":objects[3]+"");
					row.createCell(6).setCellValue(QwyUtil.isNullAndEmpty(objects[4])?"--":objects[4]+"");
					row.createCell(7).setCellValue(QwyUtil.isNullAndEmpty(objects[5])?"--":objects[5]+"");
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
	 * 六月热数据
	 * @return
	 */
	public String getHotData(){
		try {
//			long st = System.currentTimeMillis();
			UsersAdmin users = (UsersAdmin)getRequest().getSession().getAttribute("usersAdmin");
			if(QwyUtil.isNullAndEmpty(users)){
				return "login";
			}
			
			PageUtil<Object[]> pageUtil = new PageUtil<Object[]>();
			pageUtil.setCurrentPage(currentPage);
			pageUtil.setPageSize(pageSize);
			
			StringBuffer url = new StringBuffer();
			url.append(getRequest().getServletContext().getContextPath());
			url.append("/Product/Admin/winner!getHotData.action?");
			if(!QwyUtil.isNullAndEmpty(insertTime)){
				url.append("&insertTime=");
				url.append(insertTime);
			}
			if(!QwyUtil.isNullAndEmpty(username)){
				url.append("&username=");
				url.append(username);
			}
			pageUtil.setPageUrl(url.toString());
			// 获取六月热数据  已封装成对象
			PageUtil<Investors> invPage = bean.getHotDate(pageUtil, username, "2017-06-05 00:00:00", "2017-06-26 23:59:59", insertTime);
			getRequest().setAttribute("name", username);
			getRequest().setAttribute("insertTime", insertTime);
			if (!QwyUtil.isNullAndEmpty(invPage)) {
				List<Investors> list = invPage.getList();
				//循环判断是否获得刮奖机会
				for(Investors investors : list){
					long usersId = investors.getUsersId();
					Date time = investors.getPayTime();
					double money = investors.getInMoney();
					int num = bean.getInvCount(usersId, time);
					if (num >= 2 && money >=10000) {
						investors.setIsDraw("1");
					}else{
						investors.setIsDraw("0");
					}
				}
				// 奖励总额 key为usersId  value 为充值对象 直接.money 即可
				Map<Long, CzRecord> map = bean.findJlMoney("2017-06-05 00:00:00", "2017-06-26 23:59:59");
                getRequest().setAttribute("myMap", map);
				getRequest().setAttribute("pageUtil", invPage);
				getRequest().setAttribute("list", invPage.getList());
				getRequest().setAttribute("table", "1");
//				long et = System.currentTimeMillis();
				return "hot";
			}
			
		} catch (Exception e) {
			log.info(e);
		}
		
		return "hot";
	}
	
	/**
	 * 导出 抽奖机会记录 报表  
	 * @return
	 */
	public String iportHotData(){
		try {
            String name = QwyUtil.fmyyyyMMddHHmmss3.format(new Date()) + "_hotData";
            PageUtil<Object[]> pageUtil = new PageUtil<Object[]>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(999999);
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("6月热数据记录");
            HSSFRow row = sheet.createRow((int) 1);
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
            row = sheet.createRow(0);
            row.createCell(0).setCellValue("序号");
            row.createCell(1).setCellValue("用户名");
            row.createCell(2).setCellValue("真实姓名");
            row.createCell(3).setCellValue("投资项目");
            row.createCell(4).setCellValue("投资本金");
            row.createCell(5).setCellValue("投资时间");
            row.createCell(6).setCellValue("获得奖励");
            row.createCell(7).setCellValue("获得抽奖次数");
            row.createCell(8).setCellValue("累计抽奖次数");
            
            //对时间参数判断
			PageUtil<Investors> invPage = bean.getHotDate(pageUtil, username, "2017-06-05 00:00:00", "2017-06-26 23:59:59", insertTime);
			// 奖励总额 key为usersId  value 为充值对象 直接.money 即可
			Map<Long, CzRecord> map = bean.findJlMoney("2017-06-05 00:00:00", "2017-06-26 23:59:59");
			List<Investors> invList = invPage.getList();
            
            if (!QwyUtil.isNullAndEmpty(invList)) {
            	for (int i = 0; i < invList.size(); i++) {
            		row = sheet.createRow((int) i + 1);
					Investors investors = invList.get(i);
					//判断是否有刮奖机会
					long usersId = investors.getUsersId();
					Date time = investors.getPayTime();
					double money = investors.getInMoney();
					int num = bean.getInvCount(usersId, time);
					if (num >= 2 && money >=10000) {
						investors.setIsDraw("1");
					}else{
						investors.setIsDraw("0");
					}
					
					row.createCell(1).setCellValue(DESEncrypt.jieMiUsername(investors.getApiVersion()));
					row.createCell(2).setCellValue(QwyUtil.isNullAndEmpty(investors.getInvestorType())?"--":investors.getInvestorType());
					row.createCell(3).setCellValue(QwyUtil.isNullAndEmpty(investors.getProductId())?"--":investors.getProductId());
					row.createCell(4).setCellValue(QwyUtil.isNullAndEmpty(investors.getInMoney())?"--":investors.getInMoney()+"");
					row.createCell(5).setCellValue(QwyUtil.isNullAndEmpty(investors.getPayTime())?"--":QwyUtil.fmyyyyMMddHHmmss.format(investors.getPayTime()));
					row.createCell(6).setCellValue(QwyUtil.isNullAndEmpty(map.get(investors.getUsersId()))?"--":(QwyUtil.isNullAndEmpty(map.get(investors.getUsersId()).getMoney())?"--":map.get(investors.getUsersId()).getMoney()+""));
					row.createCell(7).setCellValue(QwyUtil.isNullAndEmpty(investors.getIsDraw())?"--":investors.getIsDraw()+"");
					row.createCell(8).setCellValue(QwyUtil.isNullAndEmpty(investors.getCopies())?"--":investors.getCopies()+"");
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
	
	public Long getPrizeId() {
		return prizeId;
	}
	public void setPrizeId(Long prizeId) {
		this.prizeId = prizeId;
	}
	public String getUsersId() {
		return usersId;
	}
	public void setUsersId(String usersId) {
		this.usersId = usersId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

}
