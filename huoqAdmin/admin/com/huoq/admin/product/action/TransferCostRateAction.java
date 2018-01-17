package com.huoq.admin.product.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;

import com.huoq.admin.product.bean.BannerBean;
import com.huoq.admin.product.bean.ReleaseProductBean;
import com.huoq.admin.product.bean.TransferCostRateBean;
import com.huoq.common.action.BaseAction;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.Banner;
import com.huoq.orm.Product;
import com.huoq.orm.SystemConfig;
import com.huoq.orm.TransferCostRate;
import com.huoq.orm.UsersAdmin;

/**
 * 基金产品转让规则
 * 
 */
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/Admin")
	//发布产品页面
	@Results({ 
		@Result(name = "addRole", value = "/Product/Admin/productManager/addTransferRule.jsp"),
		@Result(name = "transferRoleList", value = "/Product/Admin/productManager/transferRoleList.jsp"),
})
public class TransferCostRateAction extends BaseAction {
	@Resource
	TransferCostRateBean bean;
	@Resource
	ReleaseProductBean productBean;
	private TransferCostRate transferCostRate;
	private String productTitle;
	private Integer currentPage = 1;
	private Integer pageSize = 20;
	private String status;
	/**
	 * 跳转到添加转让规则界面
	 * @return
	 */
	public String toAddTransferRole(){
		try {
			List<Product> products= productBean.findFundProduct(null, "2");
			getRequest().setAttribute("list", products);
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return "addRole";
	}
	
	
	/**
	 * 添加转让规则
	 * @return
	 */
	public String addTransferRole(){
		try {
			if(!QwyUtil.isNullAndEmpty(transferCostRate)){
				TransferCostRate tr=bean.saveTransferCostRate(transferCostRate);
				if(!QwyUtil.isNullAndEmpty(tr)){
					getRequest().setAttribute("isOk", "ok");
					return "addRole";
				}
			}
			
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return "addRole";
	}
	
	/**
	 * 转让规则列表
	 * @return
	 */
	public String transferRoleList(){
		try {
			PageUtil<TransferCostRate> pageUtil=new PageUtil<TransferCostRate>(20, 1);
			//根据状态来加载提现的记录;
			pageUtil.setCurrentPage(currentPage);
			pageUtil.setPageSize(pageSize);
			pageUtil=bean.findTransferCostRates(pageUtil, status, productTitle);
			StringBuffer url = new StringBuffer();
			url.append(getRequest().getServletContext().getContextPath());
			url.append("/Product/Admin/transferCostRate!transferRoleList.action?");
			if(!QwyUtil.isNullAndEmpty(status)){
				url.append("&status="+status);
			}
			if(!QwyUtil.isNullAndEmpty(productTitle)){
				url.append("&productTitle="+productTitle);
			}
			getRequest().setAttribute("status", status);
			getRequest().setAttribute("productTitle", productTitle);
			getRequest().setAttribute("pageUtil", pageUtil);
			getRequest().setAttribute("list", pageUtil.getList());
			
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return "transferRoleList";
	}
	
	
	
	public TransferCostRate getTransferCostRate() {
		return transferCostRate;
	}
	public void setTransferCostRate(TransferCostRate transferCostRate) {
		this.transferCostRate = transferCostRate;
	}

	public String getProductTitle() {
		return productTitle;
	}


	public void setProductTitle(String productTitle) {
		this.productTitle = productTitle;
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
