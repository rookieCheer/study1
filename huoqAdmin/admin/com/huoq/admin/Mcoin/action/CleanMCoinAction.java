package com.huoq.admin.Mcoin.action;

import java.io.IOException;
import javax.annotation.Resource;

import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;

import com.huoq.common.action.BaseAction;
import com.huoq.common.util.QwyUtil;
import com.huoq.thread.action.CleanAllMCoinThread;
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/Admin")
//发布产品页面
@Results({ @Result(name = "cleanMCoin", value = "/Product/Admin/operationManager/mCoinDayDetail.jsp"),
})
public class CleanMCoinAction   extends BaseAction  {
	private Integer pageSize = 50;

	@Resource
	private CleanAllMCoinThread cleanAllMCoinThread; 
	
	public String cleanMCoinThread(){
		String json = "";
		try {
			new Thread(cleanAllMCoinThread).start();
			json = QwyUtil.getJSONString("ok", "启动成功");
		} catch (Exception e) {
			log.error("启动失败: ", e);
			json = QwyUtil.getJSONString("error", "启动失败");
		}
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			log.error("启动失败: ", e);
		}
		return null;
		
	}

	public Integer getPageSize() {
		return pageSize;
	}


	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

}
