package com.huoq.admin.product.action;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;

import com.huoq.admin.product.bean.CheckTxsqBean;
import com.huoq.common.action.BaseAction;
import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.TxRecord;
import com.huoq.orm.TxRecordCompany;
import com.huoq.orm.UsersAdmin;
import com.huoq.thread.action.TxRequestThread;

/**后台管理--审核提现申请;
 * @author qwy
 *
 * @createTime 2015-4-27下午3:51:34
 */
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/Admin")
//发布产品页面
@Results({ @Result(name = "txsq", value = "/Product/Admin/fundsManager/txsq.jsp"),
		@Result(name = "indextxsq", value = "/Product/Admin/fundsManager/indextxsq.jsp"),
		@Result(name = "yctxsq", value = "/Product/Admin/fundsManager/yctxsq.jsp"),
		@Result(name = "txsqsh", value = "/Product/Admin/fundsManager/txsqsh.jsp"),
		@Result(name = "txsqCompany", value = "/Product/Admin/fundsManager/txsqCompany.jsp"),
		@Result(name = "err", value = "/Product/Admin/err.jsp")
})
public class CheckTxsqAction extends BaseAction {

	private PageUtil<TxRecord> pageUtil;
	private Integer currentPage = 1;
	private Integer pageSize = 150;
	private String status = "all";
	private String name="";
	private String insertTime;
	private String txId;
	private String shStatus="2";
	@Resource(name="checkTxsqBean")
	private CheckTxsqBean bean;
	@Resource
	private TxRequestThread txRequestThread;

	private Long usersId=null;

	/**
	 * 加载提现申请的企业用户
	 * @return
	 */
	public String loadTxsqCompany(){


		String json="";
		try {
			UsersAdmin users = (UsersAdmin)getRequest().getSession().getAttribute("usersAdmin");
			if(QwyUtil.isNullAndEmpty(users)){
				json = QwyUtil.getJSONString("err", "管理员未登录");
				QwyUtil.printJSON(getResponse(), json);
				//管理员没有登录;
				return null;
			}

			//根据状态来加载提现的记录;
			PageUtil<TxRecordCompany> pageUtil = new PageUtil<TxRecordCompany>();
			pageUtil.setCurrentPage(currentPage);
			pageUtil.setPageSize(pageSize);
			StringBuffer url = new StringBuffer();
			url.append(getRequest().getServletContext().getContextPath());
			url.append("/Product/Admin/checkTxsq!loadTxsqCompany.action?status="
					+ status);
			if (!QwyUtil.isNullAndEmpty(name)) {
				url.append("&name=");
				url.append(name);
			}
			if (!QwyUtil.isNullAndEmpty(insertTime)) {
				url.append("&insertTime=");
				url.append(insertTime);
			}

			pageUtil.setPageUrl(url.toString());
			pageUtil = bean.loadTxRecordCompany(pageUtil, status,  name,insertTime);
			if (!QwyUtil.isNullAndEmpty(pageUtil)) {
				getRequest().setAttribute("pageUtil", pageUtil);
				getRequest().setAttribute("name", name);
				getRequest().setAttribute("insertTime", insertTime);
				getRequest().setAttribute("txRecordList", pageUtil.getList());
			}
		} catch (Exception e) {
			log.error("操作异常: ",e);
			log.error("操作异常: ",e);
		}

		return "txsqCompany";
	}


	/**首页加载提现申请的用户
	 * @return
	 */
	public String loadIndexTxsq(){
		String json="";
		try {
			UsersAdmin users = (UsersAdmin)getRequest().getSession().getAttribute("usersAdmin");
			if(QwyUtil.isNullAndEmpty(users)){
				json = QwyUtil.getJSONString("err", "管理员未登录");
				QwyUtil.printJSON(getResponse(), json);
				//管理员没有登录;
				return null;
			}
			String superName="0EA5D6BC23E8EEC78F62546B9F68BABFA96976B775889BA625DB6D764FD0DBD42A1C0F45F85B0DE8";
			if(!superName.equals(users.getUsername())){
				if(isExistsQX("提现记录", users.getId())){
					getRequest().setAttribute("err", "您没有操作该功能的权限!");
					return "err";
				}
			}
			//根据状态来加载提现的记录;
			PageUtil<TxRecord> pageUtil = new PageUtil<TxRecord>();
			pageUtil.setCurrentPage(currentPage);
			pageUtil.setPageSize(pageSize);
			StringBuffer url = new StringBuffer();
			url.append(getRequest().getServletContext().getContextPath());
			url.append("/Product/Admin/checkTxsq!loadTxsq.action?status="
					+ status);
			if (!QwyUtil.isNullAndEmpty(name)) {
				url.append("&name=");
				url.append(name);
			}
			if (!QwyUtil.isNullAndEmpty(insertTime)) {
				url.append("&insertTime=");
				url.append(insertTime);
			}
			pageUtil.setPageUrl(url.toString());
			pageUtil = bean.indexloadTxRecord(pageUtil, status,  name,insertTime);
			if (!QwyUtil.isNullAndEmpty(pageUtil)) {
				getRequest().setAttribute("pageUtil", pageUtil);
				getRequest().setAttribute("name", name);
				getRequest().setAttribute("insertTime", insertTime);
				getRequest().setAttribute("txRecordList", pageUtil.getList());
			}
		} catch (Exception e) {
			log.error("操作异常: ",e);
			log.error("操作异常: ",e);
		}
		return "indextxsq";
	}

	/**加载提现申请的用户
	 * @return
	 */
	public String loadTxsq(){
		String json="";
		try {
			UsersAdmin users = (UsersAdmin)getRequest().getSession().getAttribute("usersAdmin");
			if(QwyUtil.isNullAndEmpty(users)){
				json = QwyUtil.getJSONString("err", "管理员未登录");
				QwyUtil.printJSON(getResponse(), json);
				//管理员没有登录;
				return null;
			}
			String superName="0EA5D6BC23E8EEC78F62546B9F68BABFA96976B775889BA625DB6D764FD0DBD42A1C0F45F85B0DE8";
			if(!superName.equals(users.getUsername())){
				if(isExistsQX("提现记录", users.getId())){
					getRequest().setAttribute("err", "您没有操作该功能的权限!");
					return "err";
				}
			}
			//根据状态来加载提现的记录;
			PageUtil<TxRecord> pageUtil = new PageUtil<TxRecord>();
			pageUtil.setCurrentPage(currentPage);
			pageUtil.setPageSize(pageSize);
			StringBuffer url = new StringBuffer();
			url.append(getRequest().getServletContext().getContextPath());
			url.append("/Product/Admin/checkTxsq!loadTxsq.action?status="
					+ status);
			if (!QwyUtil.isNullAndEmpty(name)) {
				url.append("&name=");
				url.append(name);
			}
			if (!QwyUtil.isNullAndEmpty(insertTime)) {
				url.append("&insertTime=");
				url.append(insertTime);
			}
			pageUtil.setPageUrl(url.toString());
			pageUtil = bean.loadTxRecord(pageUtil, status,  name,insertTime);
			if (!QwyUtil.isNullAndEmpty(pageUtil)) {
				getRequest().setAttribute("pageUtil", pageUtil);
				getRequest().setAttribute("name", name);
				getRequest().setAttribute("insertTime", insertTime);
				getRequest().setAttribute("txRecordList", pageUtil.getList());
			}
		} catch (Exception e) {
			log.error("操作异常: ",e);
			log.error("操作异常: ",e);
		}
		return "txsq";
	}

	/**人工审核提现;
	 * @return
	 */
	public String shenheTx(){
		String json = "";
		try {
			UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
			if (QwyUtil.isNullAndEmpty(users)) {
				json = QwyUtil.getJSONString("err", "管理员未登录");
				QwyUtil.printJSON(getResponse(), json);
				return null;
			}
			if("1".equals(shStatus)){
				//提现审核通过
				txRequestThread.tx(txId);
			}else{
				//提现审核不通过 不退款
				bean.updateTxRecordStatus(txId,"2");
			}
			json = QwyUtil.getJSONString("ok", "操作成功");
			QwyUtil.printJSON(getResponse(), json);
			return null;
		} catch (Exception e) {
			log.error("操作异常: ",e);
			log.error("操作异常: ",e);
		}
		json = QwyUtil.getJSONString("error", "操作失败");
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			log.error("操作异常: ",e);
			log.error("操作异常: ",e);
		}
		return null;
	}

	/**提现时,检查用户资金情况;
	 * @return
	 */
	public String checkUsersMoney(){
		String json = "";
		try {
			UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
			if (QwyUtil.isNullAndEmpty(users)) {
				json = QwyUtil.getJSONString("err", "管理员未登录");
				QwyUtil.printJSON(getResponse(), json);
				return null;
			}
			String result = txRequestThread.checkUsersMoney(usersId, txId);
			json = QwyUtil.getJSONString("ok", result);
			QwyUtil.printJSON(getResponse(), json);
			return null;
		} catch (Exception e) {
			log.error("操作异常: ",e);
			log.error("操作异常: ",e);
		}
		json = QwyUtil.getJSONString("error", "操作失败");
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			log.error("操作异常: ",e);
			log.error("操作异常: ",e);
		}
		return null;
	}

	/**提现--解除警报
	 * @return
	 */
	public String allclearTx (){
		String json = "";
		try {
			UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
			if (QwyUtil.isNullAndEmpty(users)) {
				json = QwyUtil.getJSONString("err", "管理员未登录");
				QwyUtil.printJSON(getResponse(), json);
				return null;
			}
			txRequestThread.allclearTx(txId);
			json = QwyUtil.getJSONString("ok", "操作成功");
			QwyUtil.printJSON(getResponse(), json);
			return null;
		} catch (Exception e) {
			log.error("操作异常: ",e);
			log.error("操作异常: ",e);
		}
		json = QwyUtil.getJSONString("error", "操作失败");
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			log.error("操作异常: ",e);
			log.error("操作异常: ",e);
		}
		return null;
	}

	/**************************************************************************************************************************************************************
	 * *******************************************************用户提现记录审核 add by yks 2016-10-08 *******************************************************************************************************
	 */
	/**加载用户提现记录审核页面
	 * @return
	 */
	public String loadTxsqToCheck(){
		String json="";
		try {
			UsersAdmin users = (UsersAdmin)getRequest().getSession().getAttribute("usersAdmin");
			if(QwyUtil.isNullAndEmpty(users)){
				json = QwyUtil.getJSONString("err", "管理员未登录");
				QwyUtil.printJSON(getResponse(), json);
				//管理员没有登录;
				return null;
			}
			String superName="0EA5D6BC23E8EEC78F62546B9F68BABFA96976B775889BA625DB6D764FD0DBD42A1C0F45F85B0DE8";
			if(!superName.equals(users.getUsername())){
				if(isExistsQX("提现记录", users.getId())){
					getRequest().setAttribute("err", "您没有操作该功能的权限!");
					return "err";
				}
			}
			//根据状态来加载提现的记录;
			PageUtil<TxRecord> pageUtil = new PageUtil<TxRecord>();
			pageUtil.setCurrentPage(currentPage);
			pageUtil.setPageSize(pageSize);
			StringBuffer url = new StringBuffer();
			url.append(getRequest().getServletContext().getContextPath());
			url.append("/Product/Admin/checkTxsq!loadTxsqToCheck.action?status="
					+ status);
			if (!QwyUtil.isNullAndEmpty(name)) {
				url.append("&name=");
				url.append(name);
			}
			if (!QwyUtil.isNullAndEmpty(insertTime)) {
				url.append("&insertTime=");
				url.append(insertTime);
			}
			pageUtil.setPageUrl(url.toString());
			pageUtil = bean.loadTxRecord(pageUtil, status,  name,insertTime);
			if (!QwyUtil.isNullAndEmpty(pageUtil)) {
				getRequest().setAttribute("pageUtil", pageUtil);
				getRequest().setAttribute("name", name);
				getRequest().setAttribute("insertTime", insertTime);
				getRequest().setAttribute("txRecordList", pageUtil.getList());
			}
		} catch (Exception e) {
			log.error("操作异常: ",e);
			log.error("操作异常: ",e);
		}
		return "txsqsh";
	}

	/**
	 * 提现记录审核人工审核
	 * add by yks 2016-10-08
	 * @return
	 */
	public String TxMoneySH(){
		String json="";
		try {
			UsersAdmin users = (UsersAdmin)getRequest().getSession().getAttribute("usersAdmin");
			if(QwyUtil.isNullAndEmpty(users)){
				json = QwyUtil.getJSONString("err", "管理员未登录");
				QwyUtil.printJSON(getResponse(), json);
				//管理员没有登录;
				return null;
			}
			String superName="0EA5D6BC23E8EEC78F62546B9F68BABFA96976B775889BA625DB6D764FD0DBD42A1C0F45F85B0DE8";
			if(!superName.equals(users.getUsername())){
				if(isExistsQX("提现记录", users.getId())){
					getRequest().setAttribute("err", "您没有操作该功能的权限!");
					return "err";
				}
			}
			String txRecordId = getRequest().getParameter("txRecordId"); //待审核提现记录id
			String shStatus = getRequest().getParameter("status"); //审核状态
			log.info("【人工审核提现记录】，提现记录id:"+txRecordId);
			if (QwyUtil.isNullAndEmpty(txRecordId) || QwyUtil.isNullAndEmpty(shStatus)){
				json = QwyUtil.getJSONString("err", "操作失败");
				QwyUtil.printJSON(getResponse(), json);
				return null;
			}
			//审核提现记录

			if("1".equals(shStatus)){
				//提现审核通过
				txRequestThread.tx(txRecordId);
			}else{
				//提现审核不通过 不退款
				bean.updateTxRecordStatus(txRecordId,shStatus);
			}
			json = QwyUtil.getJSONString("ok", "操作成功");
			QwyUtil.printJSON(getResponse(), json);
		} catch (Exception e) {
			log.error("操作异常: ",e);
			log.error("操作异常: ",e);
		}
		return null;
	}



	/**加载异常提现记录
	 * @return
	 */
	public String loadYCTxsq(){
		String json="";
		try {
			UsersAdmin users = (UsersAdmin)getRequest().getSession().getAttribute("usersAdmin");
			if(QwyUtil.isNullAndEmpty(users)){
				json = QwyUtil.getJSONString("err", "管理员未登录");
				QwyUtil.printJSON(getResponse(), json);
				//管理员没有登录;
				return null;
			}
			String superName="0EA5D6BC23E8EEC78F62546B9F68BABFA96976B775889BA625DB6D764FD0DBD42A1C0F45F85B0DE8";
			if(!superName.equals(users.getUsername())){
				if(isExistsQX("提现记录", users.getId())){
					getRequest().setAttribute("err", "您没有操作该功能的权限!");
					return "err";
				}
			}
			PageUtil<TxRecord> pageUtil = new PageUtil<TxRecord>();
			pageUtil.setCurrentPage(currentPage);
			pageUtil.setPageSize(pageSize);
			StringBuffer url = new StringBuffer();
			url.append(getRequest().getServletContext().getContextPath());
			url.append("/Product/Admin/checkTxsq!loadYCTxsq.action?status="
					+ status);
			if (!QwyUtil.isNullAndEmpty(name)) {
				url.append("&name=");
				url.append(name);
			}
			if (!QwyUtil.isNullAndEmpty(insertTime)) {
				url.append("&insertTime=");
				url.append(insertTime);
			}
			pageUtil.setPageUrl(url.toString());
			pageUtil = bean.loadYCTxRecord(pageUtil, status,  name,insertTime);
			if (!QwyUtil.isNullAndEmpty(pageUtil)) {
				getRequest().setAttribute("pageUtil", pageUtil);
				getRequest().setAttribute("name", name);
				getRequest().setAttribute("insertTime", insertTime);
				getRequest().setAttribute("txRecordList", pageUtil.getList());
			}
		} catch (Exception e) {
			log.error("操作异常: ",e);
			log.error("操作异常: ",e);
		}
		return "yctxsq";
	}

	/**
	 * 导出异常提现用户记录报表
	 */
	public String iportYCTable() {
		try {
			PageUtil<TxRecord> pageUtil = new PageUtil<TxRecord>();
			pageUtil.setCurrentPage(currentPage);
			pageUtil.setPageSize(999999);
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("提现异常用户记录");
			HSSFRow row = sheet.createRow((int) 0);
			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
			HSSFCell cell = row.createCell(0);
			cell.setCellValue("序号");
			cell.setCellStyle(style);
			cell = row.createCell(1);
			cell.setCellValue("流水号");
			cell.setCellStyle(style);
			cell = row.createCell(2);
			cell.setCellValue("用户名");
			cell.setCellStyle(style);
			cell = row.createCell(3);
			cell.setCellValue("提现金额(元)");
			cell.setCellStyle(style);
			cell = row.createCell(4);
			cell.setCellValue("姓名");
			cell.setCellStyle(style);
			cell = row.createCell(5);
			cell.setCellValue("申请提现时间");
			cell.setCellStyle(style);
			cell = row.createCell(6);
			cell.setCellValue("审核提现时间");
			cell.setCellStyle(style);
			cell = row.createCell(7);
			cell.setCellValue("提现类型");
			cell.setCellStyle(style);
			cell = row.createCell(8);
			cell.setCellValue("平台订单号");
			cell.setCellStyle(style);
			cell = row.createCell(9);
			cell.setCellValue("交易流水号");
			cell.setCellStyle(style);
			cell = row.createCell(10);
			cell.setCellValue("提现状态");
			cell.setCellStyle(style);
			cell = row.createCell(11);
			cell.setCellValue("备注");
			cell.setCellStyle(style);
			cell = row.createCell(12);
			cell.setCellValue("提现方式");
			cell.setCellStyle(style);
			cell = row.createCell(13);
			cell.setCellValue("当天提现次数");
			cell.setCellStyle(style);
			TxRecord txRecord = null;
			List list = bean.loadYCTxRecord(pageUtil,status,name,insertTime).getList();
			for (int i = 0; i < list.size(); i++) {
				row = sheet.createRow((int) i + 1);
				txRecord = (TxRecord) list.get(i);
				row = sheet.createRow((int) i + 1);
				row.createCell(0).setCellValue((int) i + 1);
				row.createCell(1).setCellValue(!QwyUtil.isNullAndEmpty(txRecord.getRecordNumber())?txRecord.getRecordNumber():0L);
				row.createCell(2).setCellValue(!QwyUtil.isNullAndEmpty(txRecord.getUserName())? DESEncrypt.jieMiUsername(txRecord.getUserName()):"");
				row.createCell(3).setCellValue(QwyUtil.calcNumber((QwyUtil.isNullAndEmpty(txRecord.getMoney()) ? 0 : txRecord.getMoney()),0.01,"*").toString());
				row.createCell(4).setCellValue(!QwyUtil.isNullAndEmpty(txRecord.getRealName())?txRecord.getRealName():"");
				row.createCell(5).setCellValue(!QwyUtil.isNullAndEmpty(txRecord.getInsertTime())?QwyUtil.fmyyyyMMddHHmmss.format(txRecord.getInsertTime()):"");
				row.createCell(6).setCellValue(!QwyUtil.isNullAndEmpty(txRecord.getCheckTime())?QwyUtil.fmyyyyMMddHHmmss.format(txRecord.getCheckTime()):"");
				if ("0".equals(txRecord.getDrawType())){
					row.createCell(7).setCellValue("T+0");
				}else if ("1".equals(txRecord.getDrawType())){
					row.createCell(7).setCellValue("T+1");
				}else {
					row.createCell(7).setCellValue("");
				}
				row.createCell(8).setCellValue(txRecord.getRequestId());
				row.createCell(9).setCellValue(txRecord.getYbOrderId());
				row.createCell(10).setCellValue(txRecord.getTxzt());
				row.createCell(11).setCellValue(txRecord.getNote());
				if("2".equals(txRecord.getType())){
					row.createCell(12).setCellValue("连连提现");
				}else {
					row.createCell(12).setCellValue("易宝提现");
				}
				row.createCell(13).setCellValue(txRecord.getTxCount());
			}
			String pathname = QwyUtil.fmyyyyMMddHHmmss3.format(new Date()) + "_yctx_record.xls";
			String realPath = request.getServletContext().getRealPath("/report/" + pathname);
			log.info("提现异常用户记录报表地址：" + realPath);
			FileOutputStream fout = new FileOutputStream(realPath);
			wb.write(fout);
			fout.close();
			response.getWriter().write("/report/" + pathname);
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return null;
	}

	/**
	 * 导出首页提现用户记录报表
	 */
	public String iportIndexTXTable() {
		try {
			PageUtil<TxRecord> pageUtil = new PageUtil<TxRecord>();
			pageUtil.setCurrentPage(currentPage);
			pageUtil.setPageSize(999999);
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("提现用户记录");
			HSSFRow row = sheet.createRow((int) 0);
			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
			HSSFCell cell = row.createCell(0);

			cell.setCellValue("序号");
			cell.setCellStyle(style);
			cell = row.createCell(1);
			cell.setCellValue("用户名");
			cell.setCellStyle(style);
			cell = row.createCell(2);
			cell.setCellValue("提现金额(元)");
			cell.setCellStyle(style);
			cell = row.createCell(3);
			cell.setCellValue("姓名");
			cell.setCellStyle(style);
			cell = row.createCell(4);
			cell.setCellValue("所属省份");
			cell.setCellStyle(style);
			cell = row.createCell(5);
			cell.setCellValue("所属城市");
			cell.setCellStyle(style);
			cell = row.createCell(6);
			cell.setCellValue("持卡人好友");
			cell.setCellStyle(style);
			cell = row.createCell(7);
			cell.setCellValue("提现状态");
			cell.setCellStyle(style);
			cell = row.createCell(8);
			cell.setCellValue("备注");
			cell.setCellStyle(style);
			cell = row.createCell(9);
			cell.setCellValue("流水号");
			cell.setCellStyle(style);
			cell = row.createCell(10);
			cell.setCellValue("申请提现时间");
			cell.setCellStyle(style);
			cell = row.createCell(11);
			cell.setCellValue("审核提现时间");
			cell.setCellStyle(style);
			cell = row.createCell(12);
			cell.setCellValue("提现类型");
			cell.setCellStyle(style);
			cell = row.createCell(13);
			cell.setCellValue("平台订单号");
			cell.setCellStyle(style);
			cell = row.createCell(14);
			cell.setCellValue("交易流水号");
			cell.setCellStyle(style);
			cell = row.createCell(15);
			cell.setCellValue("提现方式");
			cell.setCellStyle(style);
			TxRecord txRecord = null;
			List list = bean.indexloadTxRecord(pageUtil,status,name,insertTime).getList();
			for (int i = 0; i < list.size(); i++) {
				row = sheet.createRow((int) i + 1);
				txRecord = (TxRecord) list.get(i);
				row = sheet.createRow((int) i + 1);
				row.createCell(0).setCellValue((int) i + 1);
				if(!QwyUtil.isNullAndEmpty(txRecord)){
					row.createCell(1).setCellValue(!QwyUtil.isNullAndEmpty(txRecord.getUserName())? DESEncrypt.jieMiUsername(txRecord.getUserName()):"");
					if(!QwyUtil.isNullAndEmpty(txRecord)){
						row.createCell(3).setCellValue(!QwyUtil.isNullAndEmpty(txRecord.getRealName())? txRecord.getRealName():"");
					}else {
						row.createCell(3).setCellValue("");
					}
				}else {
					row.createCell(1).setCellValue("");
					row.createCell(3).setCellValue("");
				}
				row.createCell(2).setCellValue(QwyUtil.calcNumber((QwyUtil.isNullAndEmpty(txRecord.getMoney()) ? 0 : txRecord.getMoney()),0.01,"*",2).doubleValue());
				row.createCell(4)
						.setCellValue(!QwyUtil.isNullAndEmpty(txRecord.getProvince()) ? txRecord.getProvince() : "");
				row.createCell(5).setCellValue(!QwyUtil.isNullAndEmpty(txRecord.getCity()) ? txRecord.getCity() : "");
				row.createCell(6)
						.setCellValue(!QwyUtil.isNullAndEmpty(txRecord.getCategory()) ? txRecord.getCategory() : "");
				row.createCell(7).setCellValue(txRecord.getTxzt());
				row.createCell(8).setCellValue(txRecord.getNote());
				row.createCell(9).setCellValue(!QwyUtil.isNullAndEmpty(txRecord.getRecordNumber())?txRecord.getRecordNumber():0L);
				row.createCell(10).setCellValue(!QwyUtil.isNullAndEmpty(txRecord.getInsertTime())?QwyUtil.fmyyyyMMddHHmmss.format(txRecord.getInsertTime()):"");
				row.createCell(11).setCellValue(!QwyUtil.isNullAndEmpty(txRecord.getCheckTime())?QwyUtil.fmyyyyMMddHHmmss.format(txRecord.getCheckTime()):"");
				if ("0".equals(txRecord.getDrawType())){
					row.createCell(12).setCellValue("T+0");
				}else if ("1".equals(txRecord.getDrawType())){
					row.createCell(12).setCellValue("T+1");
				}else {
					row.createCell(12).setCellValue("");
				}
				row.createCell(13).setCellValue(txRecord.getRequestId());
				row.createCell(14).setCellValue(txRecord.getYbOrderId());

				if("2".equals(txRecord.getType())){
					row.createCell(15).setCellValue("连连提现");
				}else {
					row.createCell(15).setCellValue("易宝提现");
				}


			}
			String pathname = QwyUtil.fmyyyyMMddHHmmss3.format(new Date()) + "_tx_record.xls";
			String realPath = request.getServletContext().getRealPath("/report/" + pathname);
			log.info("提现用户记录报表地址：" + realPath);
			FileOutputStream fout = new FileOutputStream(realPath);
			wb.write(fout);
			fout.close();
			response.getWriter().write("/report/" + pathname);
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return null;
	}



	/**
	 * 导出提现用户记录报表
	 */
	public String iportTXTable() {
		try {
			PageUtil<TxRecord> pageUtil = new PageUtil<TxRecord>();
			pageUtil.setCurrentPage(currentPage);
			pageUtil.setPageSize(999999);
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("提现用户记录");
			HSSFRow row = sheet.createRow((int) 0);
			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
			HSSFCell cell = row.createCell(0);
			cell.setCellValue("序号");
			cell.setCellStyle(style);
			cell = row.createCell(1);
			cell.setCellValue("流水号");
			cell.setCellStyle(style);
			cell = row.createCell(2);
			cell.setCellValue("用户名");
			cell.setCellStyle(style);
			cell = row.createCell(3);
			cell.setCellValue("提现金额(元)");
			cell.setCellStyle(style);
			cell = row.createCell(4);
			cell.setCellValue("姓名");
			cell.setCellStyle(style);
			cell = row.createCell(5);
			cell.setCellValue("申请提现时间");
			cell.setCellStyle(style);
			cell = row.createCell(6);
			cell.setCellValue("审核提现时间");
			cell.setCellStyle(style);
			cell = row.createCell(7);
			cell.setCellValue("提现类型");
			cell.setCellStyle(style);
			cell = row.createCell(8);
			cell.setCellValue("平台订单号");
			cell.setCellStyle(style);
			cell = row.createCell(9);
			cell.setCellValue("交易流水号");
			cell.setCellStyle(style);
			cell = row.createCell(10);
			cell.setCellValue("提现状态");
			cell.setCellStyle(style);
			cell = row.createCell(11);
			cell.setCellValue("备注");
			cell.setCellStyle(style);
			cell = row.createCell(12);
			cell.setCellValue("提现方式");
			cell.setCellStyle(style);
			TxRecord txRecord = null;
			List list = bean.loadTxRecord(pageUtil,status,name,insertTime).getList();
			for (int i = 0; i < list.size(); i++) {
				row = sheet.createRow((int) i + 1);
				txRecord = (TxRecord) list.get(i);
				row = sheet.createRow((int) i + 1);
				row.createCell(0).setCellValue((int) i + 1);
				row.createCell(1).setCellValue(!QwyUtil.isNullAndEmpty(txRecord.getRecordNumber())?txRecord.getRecordNumber():0L);
				if(!QwyUtil.isNullAndEmpty(txRecord.getUsers())){
					row.createCell(2).setCellValue(!QwyUtil.isNullAndEmpty(txRecord.getUsers().getUsername())? DESEncrypt.jieMiUsername(txRecord.getUsers().getUsername()):"");
					if(!QwyUtil.isNullAndEmpty(txRecord.getUsers().getUsersInfo())){
						row.createCell(4).setCellValue(!QwyUtil.isNullAndEmpty(txRecord.getUsers().getUsersInfo().getRealName())? txRecord.getUsers().getUsersInfo().getRealName():"");
					}else {
						row.createCell(4).setCellValue("");
					}
				}else {
					row.createCell(2).setCellValue("");
					row.createCell(4).setCellValue("");
				}
				row.createCell(3).setCellValue(QwyUtil.calcNumber((QwyUtil.isNullAndEmpty(txRecord.getMoney()) ? 0 : txRecord.getMoney()),0.01,"*",2).doubleValue());
				row.createCell(5).setCellValue(!QwyUtil.isNullAndEmpty(txRecord.getInsertTime())?QwyUtil.fmyyyyMMddHHmmss.format(txRecord.getInsertTime()):"");
				row.createCell(6).setCellValue(!QwyUtil.isNullAndEmpty(txRecord.getCheckTime())?QwyUtil.fmyyyyMMddHHmmss.format(txRecord.getCheckTime()):"");
				if ("0".equals(txRecord.getDrawType())){
					row.createCell(7).setCellValue("T+0");
				}else if ("1".equals(txRecord.getDrawType())){
					row.createCell(7).setCellValue("T+1");
				}else {
					row.createCell(7).setCellValue("");
				}
				row.createCell(8).setCellValue(txRecord.getRequestId());
				row.createCell(9).setCellValue(txRecord.getYbOrderId());
				row.createCell(10).setCellValue(txRecord.getTxzt());
				row.createCell(11).setCellValue(txRecord.getNote());
				if("2".equals(txRecord.getType())){
					row.createCell(12).setCellValue("连连提现");
				}else {
					row.createCell(12).setCellValue("易宝提现");
				}
			}
			String pathname = QwyUtil.fmyyyyMMddHHmmss3.format(new Date()) + "_tx_record.xls";
			String realPath = request.getServletContext().getRealPath("/report/" + pathname);
			log.info("提现用户记录报表地址：" + realPath);
			FileOutputStream fout = new FileOutputStream(realPath);
			wb.write(fout);
			fout.close();
			response.getWriter().write("/report/" + pathname);
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return null;
	}


	public Integer getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(String insertTime) {
		this.insertTime = insertTime;
	}

	public String getTxId() {
		return txId;
	}

	public void setTxId(String txId) {
		this.txId = txId;
	}




	public String getShStatus() {
		return shStatus;
	}




	public void setShStatus(String shStatus) {
		this.shStatus = shStatus;
	}




	public Long getUsersId() {
		return usersId;
	}




	public void setUsersId(Long usersId) {
		this.usersId = usersId;
	}



}
