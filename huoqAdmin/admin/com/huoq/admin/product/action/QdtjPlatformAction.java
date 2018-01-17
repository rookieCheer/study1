package com.huoq.admin.product.action;

import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;
import com.huoq.admin.product.bean.QdtjPlatformBean;
import com.huoq.common.action.BaseAction;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.QdtjPlatform;
import com.huoq.orm.UsersAdmin;
import com.huoq.thread.bean.UpdateQdtjPlatformThreadBean;
import com.huoq.thread.bean.UpdateQdtjThreadBean;

/**
 * 各平台渠道统计
 *
 * @author wxl
 * @createTime 2017年3月15日18:37:33
 */
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/Admin")
//发布产品页面
@Results({
        @Result(name = "qdtjP", value = "/Product/Admin/operationManager/qdtjPlatform.jsp"),
        @Result(name = "err", value = "/Product/Admin/err.jsp")
})
public class QdtjPlatformAction extends BaseAction {
    @Resource
    private QdtjPlatformBean qdtjPlatformBean;
    @Resource
    private UpdateQdtjPlatformThreadBean updateQdtjPlatformThreadBean;
    private String insertTime;
    private Integer currentPage=1;
    private Integer pageSize = 150;
    private String platform;

 
    /**
     * 加载 各平台渠道统计
     * @return
     */
    public String loadQdtjPlatform(){
    	String json = "";
    	try {
			
    		UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                //管理员没有登录;
                return null;
            }
    		
            PageUtil<QdtjPlatform> pageUtil = new PageUtil<QdtjPlatform>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(pageSize);
            StringBuffer url = new StringBuffer();
            url.append(getRequest().getServletContext().getContextPath());
            url.append("/Product/Admin/qdtjPlatform!loadQdtjPlatform.action?");
    		
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                url.append("&insertTime=");
                url.append(insertTime);
            }
            if (!QwyUtil.isNullAndEmpty(platform)) {
            	url.append("&platform=");
            	url.append(platform);
            }
            
            pageUtil.setPageUrl(url.toString());
            getRequest().setAttribute("insertTime", insertTime);
            getRequest().setAttribute("platform", platform);
            
            pageUtil = qdtjPlatformBean.getQdtjPlatformList(pageUtil,insertTime,platform);
            List<Object[]> list = qdtjPlatformBean.getHj(platform,insertTime);
            
            List platformList = qdtjPlatformBean.getPlatformList();
            if (!QwyUtil.isNullAndEmpty(platformList)) {
            	getRequest().setAttribute("platformList", platformList);
			}
            
            if (!QwyUtil.isNullAndEmpty(pageUtil)) {
            	getRequest().setAttribute("pageUtil", pageUtil);
            	getRequest().setAttribute("list", pageUtil.getList());
            	getRequest().setAttribute("hj", list);
            	getRequest().setAttribute("table", "1");
			}
           
    		
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
    	
    	return "qdtjP";
    }
    
    
    /**
     * 导出各平台渠道统计
     */
    public String iportQdtjPlatform() {
        try {
            String name = QwyUtil.fmyyyyMMddHHmmss3.format(new Date()) + "_qdtjPlatform";
            PageUtil<QdtjPlatform> pageUtil = new PageUtil<QdtjPlatform>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(999999);
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("Android渠道统计汇总表");
            HSSFRow row = sheet.createRow((int) 1);
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
            row = sheet.createRow(0);
            row.createCell(0).setCellValue("序号");
            row.createCell(1).setCellValue("日期");
            row.createCell(2).setCellValue("平台");
            row.createCell(3).setCellValue("激活次数");
            row.createCell(4).setCellValue("注册人数");
            row.createCell(5).setCellValue("注册/激活（人数）");
            row.createCell(6).setCellValue("绑定人数");
            row.createCell(7).setCellValue("绑定/注册（人数）");
            row.createCell(8).setCellValue("首投人数");
            row.createCell(9).setCellValue("首投/绑定（人数）");
            row.createCell(10).setCellValue("首投金额");
            row.createCell(11).setCellValue("人均首投金额");
            row.createCell(12).setCellValue("投资人数");
            row.createCell(13).setCellValue("投资金额");
            row.createCell(14).setCellValue("人均投资金额");
            row.createCell(15).setCellValue("充值金额");
            row.createCell(16).setCellValue("提现金额");
            row.createCell(17).setCellValue("存量");
            
            pageUtil = qdtjPlatformBean.getQdtjPlatformList(pageUtil,insertTime,platform);
            List<Object[]> objectsList = qdtjPlatformBean.getHj(platform,insertTime);
            
            if (!QwyUtil.isNullAndEmpty(objectsList)) {
            	for (int i = 0; i < objectsList.size(); i++) {
            		row = sheet.createRow((int) i + 1);
					Object[] objects = objectsList.get(i);
					row.createCell(0).setCellValue("合计");
					row.createCell(4).setCellValue(QwyUtil.isNullAndEmpty(objects[0])?"0":objects[0]+"");
					row.createCell(6).setCellValue(QwyUtil.isNullAndEmpty(objects[1])?"0":objects[1]+"");
					row.createCell(7).setCellValue(QwyUtil.isNullAndEmpty(objects[2])?"0":objects[2]+"");
					row.createCell(8).setCellValue(QwyUtil.isNullAndEmpty(objects[3])?"0":objects[3]+"");
					row.createCell(9).setCellValue(QwyUtil.isNullAndEmpty(objects[4])?"0":objects[4]+"");
					row.createCell(10).setCellValue(QwyUtil.isNullAndEmpty(objects[5])?"0":objects[5]+"");
					row.createCell(11).setCellValue(QwyUtil.isNullAndEmpty(objects[6])?"0":objects[6]+"");
					row.createCell(12).setCellValue(QwyUtil.isNullAndEmpty(objects[7])?"0":objects[7]+"");
					row.createCell(13).setCellValue(QwyUtil.isNullAndEmpty(objects[8])?"0":objects[8]+"");
					row.createCell(14).setCellValue(QwyUtil.isNullAndEmpty(objects[9])?"0":objects[9]+"");
					row.createCell(14).setCellValue(objects[10]+"");
					row.createCell(14).setCellValue(objects[11]+"");
					row.createCell(14).setCellValue(QwyUtil.calcNumber(objects[10], objects[11], "-").doubleValue());
				}
				
			}
          
            List list = pageUtil.getList();
            if (!QwyUtil.isNullAndEmpty(list)) {
            	for (int i = 0; i < list.size(); i++) {
            		row = sheet.createRow((int) i + 2);
            		QdtjPlatform cellData = (QdtjPlatform) list.get(i);
            		row.createCell(0).setCellValue(i+1);
            		row.createCell(1).setCellValue(QwyUtil.fmyyyyMMdd.format(cellData.getQueryDate()));
            		row.createCell(2).setCellValue(cellData.getPlatform());
            		row.createCell(3).setCellValue(cellData.getJhcs());
            		row.createCell(4).setCellValue(cellData.getZcrs());
            		row.createCell(5).setCellValue(cellData.getZczhl());
            		row.createCell(6).setCellValue(cellData.getBkrs());
            		row.createCell(7).setCellValue(cellData.getBkzhl());
            		row.createCell(8).setCellValue(cellData.getStrs());
            		row.createCell(9).setCellValue(cellData.getStzhl());
            		row.createCell(10).setCellValue(cellData.getStje());
            		row.createCell(11).setCellValue(cellData.getRjstje());
            		row.createCell(12).setCellValue(cellData.getTzrs());
            		row.createCell(13).setCellValue(cellData.getTzje());
            		row.createCell(14).setCellValue(cellData.getRjtzje());
            		row.createCell(15).setCellValue(cellData.getCzje());
            		row.createCell(16).setCellValue(cellData.getTxje());
            		row.createCell(17).setCellValue(QwyUtil.calcNumber(cellData.getCzje(), cellData.getTxje(), "-").doubleValue());
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
     * 更新当天数据--各平台数据
     *
     * @return
     */
    public String updateQdtjPlatformToday() {
        log.info("--------------------------------执行平台统计-----------------------------------------------------");
        String json = "";
        try {
        	updateQdtjPlatformThreadBean.updateQdtjPlatformToday();
            json = QwyUtil.getJSONString("ok", "数据更新成功！");
        } catch (Exception e) {
            json = QwyUtil.getJSONString("err", "数据更新失败！");
            log.error("操作异常: ",e);
        }
        try {
            QwyUtil.printJSON(getResponse(), json);
        } catch (Exception e) {
            log.error("操作异常: ",e);
        }
        return null;
    }
	public String getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(String insertTime) {
		this.insertTime = insertTime;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
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
	
    
}
