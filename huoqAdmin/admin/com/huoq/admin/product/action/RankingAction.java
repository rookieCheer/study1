package com.huoq.admin.product.action;

import javax.annotation.Resource;

import com.huoq.common.util.DESEncrypt;
import org.apache.poi.hssf.usermodel.*;
import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;

import com.huoq.admin.product.bean.RankingBean;
import com.huoq.common.action.BaseAction;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.Rank;
import com.huoq.orm.UsersAdmin;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * @author 覃文勇
 * @createTime 2015-7-30下午4:50:50
 */
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/Admin")
//显示金额排行
@Results({ 
	@Result(name = "rankInvetors", value = "/Product/Admin/fundsManager/rankInvetors.jsp"),
	@Result(name = "rankCzRecord", value = "/Product/Admin/fundsManager/rankCzRecord.jsp"),
	@Result(name = "err", value = "/Product/Admin/err.jsp")
})
public class RankingAction extends BaseAction{
	
	private Integer currentPage = 1;
	private Integer pageSize = 50;
	private String status="";
	private String insertTime;

	@Resource
	private RankingBean bean;
	/*
	 * 投入总金额排行榜
	 */
	public String showInvestorsRank(){
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
			if(isExistsQX("金额排行榜", users.getId())){
				getRequest().setAttribute("err", "您没有操作该功能的权限!");
				return "err";
			}
			}
			PageUtil<Rank> pageUtil = new PageUtil<Rank>();
			pageUtil.setCurrentPage(currentPage);
			pageUtil.setPageSize(pageSize);
			StringBuffer url = new StringBuffer();
			url.append(getRequest().getServletContext().getContextPath());
			url.append("/Product/Admin/ranking!showInvestorsRank.action?");
			if (!QwyUtil.isNullAndEmpty(insertTime)) {
				url.append("&insertTime=");
				url.append(insertTime);
			}
			//设置分页地址
			pageUtil.setPageUrl(url.toString());
			//分页查询
			pageUtil = bean.loadInvestorRank(pageUtil,insertTime);
			if(!QwyUtil.isNullAndEmpty(pageUtil)){
				getRequest().setAttribute("pageUtil", pageUtil);
				getRequest().setAttribute("list", pageUtil.getList());
				return "rankInvetors";
			}
			return "rankInvetors";
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("err", "查询记录异常");
		}
		return null;
	}

	/**
	 * 导出金额排行榜
	 * @return
	 */
	public String exportInvestorsRank() {
		if (!QwyUtil.isNullAndEmpty(insertTime)) {

		} else {
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date();
			String time = sd.format(date);
		}
		try {
			PageUtil pageUtil = new PageUtil();
			pageUtil.setCurrentPage(currentPage);
			pageUtil.setPageSize(999999);
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("金额排行榜表");
			HSSFRow row = sheet.createRow((int) 0);
			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
			HSSFCell cell = row.createCell(0);
			cell = row.createCell(0);
			cell.setCellValue("序号");
			cell.setCellStyle(style);
			cell = row.createCell(1);
			cell.setCellValue("用户id");
			cell.setCellStyle(style);
			cell = row.createCell(2);
			cell.setCellValue("用户名");
			cell.setCellStyle(style);
			cell = row.createCell(3);
			cell.setCellValue("姓名");
			cell.setCellStyle(style);
			cell = row.createCell(4);
			cell.setCellValue("投资总金额（元）");
			cell.setCellStyle(style);
			cell = row.createCell(5);
			List<Rank> list = bean.loadInvestorRank(pageUtil,insertTime).getList();

			Rank  rank = null;
			for (int i = 0; i < list.size(); i++) {
				row = sheet.createRow((int) i + 1);
				rank = (Rank) list.get(i);
				row.createCell(0).setCellValue((int) i + 1);//序号
				row.createCell(1).setCellValue(!QwyUtil.isNullAndEmpty(rank.getUsersId()) ?  rank.getUsersId():"");
				row.createCell(2).setCellValue(!QwyUtil.isNullAndEmpty(rank.getUsersname())? DESEncrypt.jieMiUsername(rank.getUsersname()):"");
				row.createCell(3).setCellValue(!QwyUtil.isNullAndEmpty(rank.getRealname()) ? rank.getRealname():"");
				Double inmoney = !QwyUtil.isNullAndEmpty(rank.getInmoney()) ?  (Double.valueOf(rank.getInmoney())*0.01):0.0;
				row.createCell(4).setCellValue(!QwyUtil.isNullAndEmpty(rank.getInmoney()) ?  inmoney.toString().substring(0,inmoney.toString().length()-2)+"":"");

			}
			String pathname = QwyUtil.fmyyyyMMddHHmmss3.format(new Date()) + "_find_investors_rank.xls";
			String realPath = request.getServletContext().getRealPath("/report/" + pathname);
			log.info("金额排行榜表地址：" + realPath);
			FileOutputStream fout = new FileOutputStream(realPath);
			wb.write(fout);
			fout.close();
			response.getWriter().write("/report/" + pathname);
		} catch (Exception e) {
			log.error("操作异常: ", e);
		}
		return null;
	}

	/*
	 * 充值总金额排行榜
	 */
	public String showCZRecordRank(){
		String json ="";
		try {
			UsersAdmin users = (UsersAdmin)getRequest().getSession().getAttribute("usersAdmin");
			if(QwyUtil.isNullAndEmpty(users)){
				json = QwyUtil.getJSONString("err", "管理员未登录");
				QwyUtil.printJSON(getResponse(), json);
				//管理员没有登录;
				return null;
			}
			PageUtil<Rank> pageUtil = new PageUtil<Rank>();
			pageUtil.setCurrentPage(currentPage);
			pageUtil.setPageSize(pageSize);
			StringBuffer url = new StringBuffer();
			url.append(getRequest().getServletContext().getContextPath());
			url.append("/Product/Admin/ranking!showCZRecordRank.action?");
			if (!QwyUtil.isNullAndEmpty(insertTime)) {
				url.append("&insertTime=");
				url.append(insertTime);
			}
			pageUtil.setPageUrl(url.toString());
			pageUtil = bean.loadCZRecordRank(pageUtil,insertTime);

			if(!QwyUtil.isNullAndEmpty(pageUtil)){
				getRequest().setAttribute("pageUtil", pageUtil);
				getRequest().setAttribute("list", pageUtil.getList());
				return "rankCzRecord";
			}
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("err", "查询记录异常");
		}
		return null;
	}


	/**
	 * 导出充值总金额排行榜
	 * @return
	 */
	public String exportCZRecordRank() {
		if (!QwyUtil.isNullAndEmpty(insertTime)) {

		} else {
			SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date();
			String time = sd.format(date);
		}
		try {
			PageUtil pageUtil = new PageUtil();
			pageUtil.setCurrentPage(currentPage);
			pageUtil.setPageSize(999999);
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("充值总金额排行榜");
			HSSFRow row = sheet.createRow((int) 0);
			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
			HSSFCell cell = row.createCell(0);
			cell = row.createCell(0);
			cell.setCellValue("序号");
			cell.setCellStyle(style);
			cell = row.createCell(1);
			cell.setCellValue("用户id");
			cell.setCellStyle(style);
			cell = row.createCell(2);
			cell.setCellValue("用户名");
			cell.setCellStyle(style);
			cell = row.createCell(3);
			cell.setCellValue("姓名");
			cell.setCellStyle(style);
			cell = row.createCell(4);
			cell.setCellValue("充值总金额（元）");
			cell.setCellStyle(style);
			cell = row.createCell(5);
			List<Rank> list = bean.loadCZRecordRank(pageUtil,insertTime).getList();

			Rank  rank = null;
			for (int i = 0; i < list.size(); i++) {
				row = sheet.createRow((int) i + 1);
				rank = (Rank) list.get(i);
				row.createCell(0).setCellValue((int) i + 1);//序号
				row.createCell(1).setCellValue(!QwyUtil.isNullAndEmpty(rank.getUsersId()) ?  rank.getUsersId():"");
				row.createCell(2).setCellValue(!QwyUtil.isNullAndEmpty(rank.getUsersname())? DESEncrypt.jieMiUsername(rank.getUsersname()):"");
				row.createCell(3).setCellValue(!QwyUtil.isNullAndEmpty(rank.getRealname()) ? rank.getRealname():"");
				Double czMoney = !QwyUtil.isNullAndEmpty(rank.getMoney()) ?  (QwyUtil.jieQuFa(Double.valueOf(rank.getMoney())*0.01,2)):0.0;
				row.createCell(4).setCellValue(!QwyUtil.isNullAndEmpty(rank.getMoney()) ?  czMoney.toString().substring(0,czMoney.toString().length()-2):"");

			}
			String pathname = QwyUtil.fmyyyyMMddHHmmss3.format(new Date()) + "_find_CzRecord_rank.xls";
			String realPath = request.getServletContext().getRealPath("/report/" + pathname);
			log.info("充值总金额排行榜地址：" + realPath);
			FileOutputStream fout = new FileOutputStream(realPath);
			wb.write(fout);
			fout.close();
			response.getWriter().write("/report/" + pathname);
		} catch (Exception e) {
			log.error("操作异常: ", e);
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

	public String getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(String insertTime) {
		this.insertTime = insertTime;
	}
}
