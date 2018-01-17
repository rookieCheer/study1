package com.huoq.admin.product.action;

import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;

import com.huoq.admin.product.bean.UsersInvInfoBean;
import com.huoq.common.action.BaseAction;
import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.UsersAdmin;

/**
 * 后台管理--用户投资状况 数据表
 *
 * @author wxl
 * @createTime 2017年3月28日10:48:42
 */
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/Admin")
@Results({@Result(name = "notBuy", value = "/Product/Admin/operationManager/notBuyFreshPro.jsp"),
		  @Result(name = "notInvest" , value="/Product/Admin/operationManager/tiedCardInvestmentUsers.jsp"),
		  @Result(name = "low" , value="/Product/Admin/operationManager/vitalityLow.jsp")
})
public class UsersInvInfoAction extends BaseAction {
	
	@Resource
	UsersInvInfoBean usersInvInfoBean;
	
	private Integer currentPage = 1;
    private Integer pageSize = 100;
	
    private String username;
    private String insertTime;
    
    /**
     * 加载未投资过新手的用户信息
     * @return
     */
	public String loadNotBuyFreshPro() {
        String json = "";
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                //管理员没有登录;
                return null;
            }
            PageUtil<Object[]> pageUtil = new PageUtil<Object[]>();

            pageUtil.setCurrentPage(currentPage);

            pageUtil.setPageSize(pageSize);

            StringBuffer url = new StringBuffer();

            url.append(getRequest().getServletContext().getContextPath());
            url.append("/Product/Admin/usersInvInfo!loadNotBuyFreshPro.action?");
			if (!QwyUtil.isNullAndEmpty(username)) {
				url.append("&username=");
				url.append(username);
			}
			if (!QwyUtil.isNullAndEmpty(insertTime)) {
                url.append("&insertTime=");
                url.append(insertTime);
            }

            pageUtil.setPageUrl(url.toString());

            pageUtil = usersInvInfoBean.loadNotBuyFreshProList(pageUtil,insertTime,username);
            if (!QwyUtil.isNullAndEmpty(pageUtil)) {
            	 getRequest().setAttribute("list", pageUtil.getList());
                 getRequest().setAttribute("pageUtil", pageUtil);
                 getRequest().setAttribute("table", "1");
			}

        } catch (Exception e) {
            log.error("操作异常: ",e);
        }
        return "notBuy";
    }
	
	
	/**
	 * 导出 未投资新手的用户投资情况 数据报表
	 * @return
	 */
	public String iportRichData() {
        try {
            String name = QwyUtil.fmyyyyMMddHHmmss3.format(new Date()) + "_notBuyFreshData";
            PageUtil<Object[]> pageUtil = new PageUtil<Object[]>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(999999);
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("未投资新手的用户投资情况");
            HSSFRow row = sheet.createRow((int) 1);
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
            row = sheet.createRow(0);
            row.createCell(0).setCellValue("序号");
            row.createCell(1).setCellValue("用户ID");
            row.createCell(2).setCellValue("用户名");
            row.createCell(3).setCellValue("姓名");
            row.createCell(4).setCellValue("年龄");
            row.createCell(5).setCellValue("性别");
            row.createCell(6).setCellValue("投资本金");
            row.createCell(7).setCellValue("注册时间");
            
            pageUtil = usersInvInfoBean.loadNotBuyFreshProList(pageUtil, insertTime, username);
            List objectsList = pageUtil.getList();
            
            if (!QwyUtil.isNullAndEmpty(objectsList)) {
            	for (int i = 0; i < objectsList.size(); i++) {
            		row = sheet.createRow((int) i + 1);
					Object[] objects = (Object[]) objectsList.get(i);
					row.createCell(1).setCellValue(objects[0]+"");
					row.createCell(2).setCellValue(DESEncrypt.jieMiUsername(objects[1]+""));
					row.createCell(3).setCellValue(QwyUtil.isNullAndEmpty(objects[2])?"--":objects[2]+"");
					row.createCell(4).setCellValue(QwyUtil.isNullAndEmpty(objects[3])?"--":objects[3]+"");
					row.createCell(5).setCellValue(QwyUtil.isNullAndEmpty(objects[4])?"--":objects[4]+"");
					row.createCell(6).setCellValue(QwyUtil.isNullAndEmpty(objects[6])?"--":objects[6]+"");
					row.createCell(7).setCellValue(QwyUtil.isNullAndEmpty(objects[5])?"--":objects[5]+"");
					
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(String insertTime) {
		this.insertTime = insertTime;
	}
	
	
	/**
	 * 注册未投资
	 * @return
 	*/
	public String tiedCardInvestmentUsers(){
		
		String json = "";
		try {
		//用户是否登录
		UsersAdmin users =(UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
		if(QwyUtil.isNullAndEmpty(users)){
			json = QwyUtil.getJSONString("err", "管理员未登录");
			//管理员未登录
			QwyUtil.printJSON(getResponse(), json);
				
				return null;
			}
		//分页
		PageUtil<Object[]> pageUtil = new PageUtil<Object[]>();
		pageUtil.setCurrentPage(currentPage);
		pageUtil.setPageSize(pageSize);

		StringBuffer url = new StringBuffer();
		
		url.append(getRequest().getServletContext().getContextPath());
		url.append("/Product/Admin/usersInvInfo!tiedCardInvestmentUsers.action?");
		
		if(!QwyUtil.isNullAndEmpty(username)){
			url.append("&username="+username);
		}
		if(!QwyUtil.isNullAndEmpty(insertTime)){
			url.append("&insertTime="+insertTime);
		}
		
		// /Product/Admin/usersInvInfo!tiedCardInvestmentUsers.action?
		pageUtil.setPageUrl(url.toString());
		//查询并返回分页数据
		pageUtil = usersInvInfoBean.tiedCardInvestmentUsersList(pageUtil,insertTime,username);
		
		if(!QwyUtil.isNullAndEmpty(pageUtil)){
			getRequest().setAttribute("list", pageUtil.getList());
			getRequest().setAttribute("pageUtil",pageUtil);
			getRequest().setAttribute("table", "1"); 
		}
		
			} catch (Exception e) {
				log.error("操作异常: ",e);
			}
		
		return "notInvest";
	}
	
	
	/**
	 * 导出  未投资的用户  数据表
	 * @return
	 */
	public void iportData(){
		try{
			String name = QwyUtil.fmyyyyMMddHHmmss3.format(new Date())+"_notInvestData";
			
			PageUtil<Object[]> pageUtil = new PageUtil<Object[]>();
			pageUtil.setCurrentPage(currentPage);
			pageUtil.setPageSize(999999);
		
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("绑卡未投资用户信息");
			HSSFRow row = sheet.createRow(1);
			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			
			row = sheet.createRow(0);
			row.createCell(0).setCellValue("编号");
			row.createCell(1).setCellValue("用户ID");
			row.createCell(2).setCellValue("用户名");
			sheet.setColumnWidth(2, 3500);
			row.createCell(3).setCellValue("姓名");
			row.createCell(4).setCellValue("性别");
			row.createCell(5).setCellValue("年龄");
			row.createCell(6).setCellValue("绑卡");
			row.createCell(7).setCellValue("所属省份");
			row.createCell(8).setCellValue("所属城市");
			/*row.createCell(9).setCellValue("电话类型");*/
			row.createCell(9).setCellValue("注册时间");
			sheet.setColumnWidth(10, 5500);
			/*row.createCell(10).setCellValue("注册平台");*/
			row.createCell(10).setCellValue("渠道号");
			
			pageUtil = usersInvInfoBean.tiedCardInvestmentUsersList(pageUtil, insertTime, username);
			
			List objectList = pageUtil.getList();
			
			if(!QwyUtil.isNullAndEmpty(objectList)){
				for(int i = 0; i < objectList.size() ; i++){
					row = sheet.createRow(i+1);
					Object[] objects = (Object[])objectList.get(i);
					//设置编号
					row.createCell(0).setCellValue(i + 1 + "");
					row.createCell(1).setCellValue(QwyUtil.isNullAndEmpty(objects[0])? "--" : objects[0]+"");
					
					if(StringUtils.isNotEmpty((String)objects[1])){
						row.createCell(2).setCellValue(DESEncrypt.jieMiUsername(objects[1]+""));
					}else{
						row.createCell(2).setCellValue("--");
					}
					row.createCell(3).setCellValue(QwyUtil.isNullAndEmpty(objects[2])? "--" : objects[2]+"");
					row.createCell(4).setCellValue(QwyUtil.isNullAndEmpty(objects[3])? "--" : objects[3]+"");
					row.createCell(5).setCellValue(QwyUtil.isNullAndEmpty(objects[4])? "--" : objects[4]+"");
					if (QwyUtil.isNullAndEmpty(objects[5])) {
						row.createCell(6).setCellValue("--");
					}else{
						if ("0".equals(objects[5])) {
							row.createCell(6).setCellValue("未绑卡");
						}else if ("1".equals(objects[5])) {
							row.createCell(6).setCellValue("已绑卡");
						}
					}
					/*row.createCell(6).setCellValue(QwyUtil.isNullAndEmpty(objects[5])? "--" : objects[5]+"");*/
					row.createCell(7).setCellValue(QwyUtil.isNullAndEmpty(objects[6])? "--" : objects[6] + "");
					row.createCell(8).setCellValue(QwyUtil.isNullAndEmpty(objects[7])? "--" : objects[7] + "");
					
					/*row.createCell(9).setCellValue(QwyUtil.isNullAndEmpty(objects[8])? "--" : objects[8] + "");*/
					row.createCell(9).setCellValue(QwyUtil.isNullAndEmpty(objects[8])? "--" : objects[8]+"");
					
					/*if(StringUtils.isNotEmpty((String)objects[10])){
					if(Integer.parseInt(objects[10].toString()) == 0){
						row.createCell(11).setCellValue("手机注册");
					}else if(Integer.parseInt(objects[10].toString()) == 1){
						row.createCell(11).setCellValue("邮箱注册");
					}else{
						row.createCell(11).setCellValue("其他");
						}
					}else{
						row.createCell(11).setCellValue("其他");
					}*/
					row.createCell(10).setCellValue(QwyUtil.isNullAndEmpty(objects[9])? "--" : objects[9]+"");
				}
			}
			
			String realPath = request.getServletContext().getRealPath("/report/" + name + ".xls");
			FileOutputStream fout = new FileOutputStream(realPath);
			wb.write(fout);
			fout.close();
			response.getWriter().write("/report/" + name + ".xls");
			
		}  catch(Exception e){
			 log.error("操作异常: ",e);
		}
	}
	
	/**
	 * 加载活跃度低用户
	 * @return
	 */
	public String getVitalityLowUsers(){
		 String json = "";
	        try {
	            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
	            if (QwyUtil.isNullAndEmpty(users)) {
	                json = QwyUtil.getJSONString("err", "管理员未登录");
	                QwyUtil.printJSON(getResponse(), json);
	                //管理员没有登录;
	                return null;
	            }
	            PageUtil<Object[]> pageUtil = new PageUtil<Object[]>();

	            pageUtil.setCurrentPage(currentPage);

	            pageUtil.setPageSize(pageSize);

	            StringBuffer url = new StringBuffer();

	            url.append(getRequest().getServletContext().getContextPath());
	            url.append("/Product/Admin/usersInvInfo!getVitalityLowUsers.action?");
				if (!QwyUtil.isNullAndEmpty(username)) {
					url.append("&username=");
					url.append(username);
				}
				if (!QwyUtil.isNullAndEmpty(insertTime)) {
	                url.append("&insertTime=");
	                url.append(insertTime);
	            }
			
	            pageUtil.setPageUrl(url.toString());
	            String yesterdayTime = QwyUtil.fmMMddyyyy.format(QwyUtil.addDaysFromOldDate(new Date(), -1).getTime());
	            insertTime = QwyUtil.isNullAndEmpty(insertTime)?yesterdayTime:insertTime;

	            pageUtil = usersInvInfoBean.getVitalityUsers(pageUtil, insertTime, username);
	            if (!QwyUtil.isNullAndEmpty(pageUtil)) {
	            	 getRequest().setAttribute("list", pageUtil.getList());
	                 getRequest().setAttribute("pageUtil", pageUtil);
	                 getRequest().setAttribute("table", "1");
				}
	            
	            return "low";

	        } catch (Exception e) {
	            log.error("操作异常: ",e);
	        }
		
		
		return null;
	}
	
	/**
	 * 导出 活跃度低用户 数据报表
	 * @return
	 */
	public String iportVitalityLowData() {
        try {
            String name = QwyUtil.fmyyyyMMddHHmmss3.format(new Date()) + "_vitalityLowData";
            PageUtil<Object[]> pageUtil = new PageUtil<Object[]>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(999999);
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("活跃度低用户");
            HSSFRow row = sheet.createRow((int) 1);
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
            row = sheet.createRow(0);
            row.createCell(0).setCellValue("序号");
            row.createCell(1).setCellValue("用户ID");
            row.createCell(2).setCellValue("用户名");
            row.createCell(3).setCellValue("姓名");
            row.createCell(4).setCellValue("年龄");
            row.createCell(5).setCellValue("性别");
            row.createCell(6).setCellValue("所属省份");
            row.createCell(7).setCellValue("所属城市");
            row.createCell(8).setCellValue("渠道号");
            row.createCell(9).setCellValue("支付时间");
            row.createCell(10).setCellValue("投资项目");
            row.createCell(11).setCellValue("投资资金");            
            
            pageUtil = usersInvInfoBean.getVitalityUsers(pageUtil, insertTime, username);
            List objectsList = pageUtil.getList();
            
            if (!QwyUtil.isNullAndEmpty(objectsList)) {
            	for (int i = 0; i < objectsList.size(); i++) {
            		row = sheet.createRow((int) i + 1);
					Object[] objects = (Object[]) objectsList.get(i);
					row.createCell(1).setCellValue(objects[0]+"");
					row.createCell(2).setCellValue(DESEncrypt.jieMiUsername(objects[1]+""));
					row.createCell(3).setCellValue(QwyUtil.isNullAndEmpty(objects[2])?"--":objects[2]+"");
					row.createCell(4).setCellValue(QwyUtil.isNullAndEmpty(objects[3])?"--":objects[3]+"");
					row.createCell(5).setCellValue(QwyUtil.isNullAndEmpty(objects[4])?"--":objects[4]+"");
					row.createCell(6).setCellValue(QwyUtil.isNullAndEmpty(objects[5])?"--":objects[5]+"");
					row.createCell(7).setCellValue(QwyUtil.isNullAndEmpty(objects[6])?"--":objects[6]+"");
					row.createCell(8).setCellValue(QwyUtil.isNullAndEmpty(objects[7])?"--":objects[7]+"");
					row.createCell(9).setCellValue(QwyUtil.isNullAndEmpty(objects[8])?"--":objects[8]+"");
					row.createCell(10).setCellValue(QwyUtil.isNullAndEmpty(objects[9])?"--":objects[9]+"");
					row.createCell(11).setCellValue(QwyUtil.isNullAndEmpty(objects[10])?"--":objects[10]+"");
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
	
}
