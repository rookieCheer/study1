package com.huoq.admin.product.action;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;
import com.huoq.admin.product.bean.SmsRecordBean;
import com.huoq.common.action.BaseAction;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.SmsRecord;
import com.huoq.orm.UsersAdmin;

/**
 * 后台管理——短信列表
 */
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/Admin")
@Results({ @Result(name = "sms_record", value = "/Product/Admin/operationManager/sms_record.jsp") })
public class SmsRecordAction extends BaseAction {

	private Integer currentPage = 1;
	private Integer pageSize = 20;
	private String status;
	@Resource
	private SmsRecordBean smsRecordBean;
	private String mobile;
	private String insertTime;
	protected static Logger log = Logger.getLogger(BaseAction.class);

	/**
	 * 加载短信列表
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getSmsRecord() {

		String json = "";
		try {

			UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
			if (QwyUtil.isNullAndEmpty(users)) {
				json = QwyUtil.getJSONString("err", "管理员未登录");
				QwyUtil.printJSON(getResponse(), json);
				// 管理员没有登录;
				return null;
			}
			PageUtil<SmsRecord> pageUtil = new PageUtil<SmsRecord>();
			pageUtil.setCurrentPage(currentPage);
			pageUtil.setPageSize(pageSize);
			StringBuffer url = new StringBuffer();
			url.append(getRequest().getServletContext().getContextPath());
			url.append("/Product/Admin/smsRecord!getSmsRecord.action?");

			if (!QwyUtil.isNullAndEmpty(mobile)) {
				url.append("&mobile=");
				url.append(mobile);
			}

			if (!QwyUtil.isNullAndEmpty(insertTime)) {
				url.append("&insertTime=");
				url.append(insertTime);
			}

			pageUtil.setPageUrl(url.toString());
			pageUtil = smsRecordBean.loadSmsRecord(mobile, insertTime, pageUtil);

			if (!QwyUtil.isNullAndEmpty(pageUtil)) {
				getRequest().setAttribute("pageUtil", pageUtil);
				getRequest().setAttribute("list", pageUtil.getList());
				
				return "sms_record";
			}

		} catch (Exception e) {
			log.error("操作异常: ", e);
			json = QwyUtil.getJSONString("err", "查询记录异常");
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

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(String insertTime) {
		this.insertTime = insertTime;
	}

}
