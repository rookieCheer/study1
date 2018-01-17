package com.huoq.admin.product.action;

import com.huoq.thread.bean.MessageSendThreadEight;
import com.huoq.thread.bean.MessageSendThreadTen;

import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;

import com.huoq.common.action.BaseAction;
import com.huoq.common.util.QwyUtil;

import java.io.IOException;

import javax.annotation.Resource;

/**
 * 企业用户管理
 * Created by yks on 2016/10/24.
 */
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/Admin")
@Results({
        @Result(name = "usersCompanyList", value = "/Product/Admin/usersCompanyManager/usersCompanyList.jsp"),
        @Result(name = "err", value = "/Product/Admin/err.jsp")
})
public class MessageSendAction extends BaseAction {

	@Resource
    private MessageSendThreadEight messageSendThreadEight;
	
	@Resource
    private MessageSendThreadTen messageSendThreadTen;
    

	/**
	 * 短信枫叶网-1月28日早上8点
	 * 
	 * @return
	 */
	public String MessageSendThreadEight() {
		String json = "";
		try {
			new Thread(messageSendThreadEight).start();
			json = QwyUtil.getJSONString("ok", "启动成功");
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("error", "启动失败");
		}
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			log.error("操作异常: ",e);
		}
		
		return null;
	}
	
	/**
	 * 短信枫叶网-2月3日晚上21点
	 * 
	 * @return
	 */
	public String MessageSendThreadTen() {
		String json = "";
		try {
			new Thread(messageSendThreadTen).start();
			json = QwyUtil.getJSONString("ok", "启动成功");
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("error", "启动失败");
		}
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			log.error("操作异常: ",e);
		}
		return null;
	}

}
