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

import com.huoq.admin.product.bean.StartPageBean;
import com.huoq.common.action.BaseAction;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.StartPage;
import com.huoq.orm.SystemConfig;
import com.huoq.orm.UsersAdmin;
import com.huoq.ossUtils.OssUtil;

/**
 * StartPage管理;
 * @author qwy
 *
 * @createTime 2015-4-27下午3:51:34
 */
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/Admin")
	//发布产品页面
	@Results({ 
		@Result(name = "loadStartPage", value = "/Product/Admin/startPageManager/startPageList.jsp"),
		@Result(name = "StartPage", value = "/Product/Admin/startPageManager/startPage.jsp"),
		@Result(name = "err", value = "/Product/Admin/err.jsp")
})
public class StartPageAction extends BaseAction {
	@Resource
	StartPageBean bean;
	private StartPage startPage;
	private File file;
	private String fileContentType;
	private String removeId;
	
	/**
	 * 加载版本
	 * @return
	 */
	public String loadStartPage(){
		String json = "";
		try {
			UsersAdmin users = (UsersAdmin) getRequest().getSession()
					.getAttribute("usersAdmin");
			if (QwyUtil.isNullAndEmpty(users)) {
				json = QwyUtil.getJSONString("err", "管理员未登录");
				QwyUtil.printJSON(getResponse(), json);
				//管理员没有登录;
				return null;
			}
			String superName="0EA5D6BC23E8EEC78F62546B9F68BABFA96976B775889BA625DB6D764FD0DBD42A1C0F45F85B0DE8";
			if(!superName.equals(users.getUsername())){
			if(isExistsQX("启动页展示", users.getId())){
				getRequest().setAttribute("err", "您没有操作该功能的权限!");
				return "err";
			}
			}
			if(QwyUtil.isNullAndEmpty(startPage)){
				startPage=new StartPage();
			}
			List<StartPage> list=bean.findStartPages(null);
			getRequest().setAttribute("list", list);
			return "loadStartPage";
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("err", "查询记录异常");
		}
		return "loadStartPage";
	}
	
	/**
	 * 保存版本信息
	 * @return
	 */
	public String saveStartPage() {
		
		try {
			String json = "";
			UsersAdmin users = (UsersAdmin) getRequest().getSession()
					.getAttribute("usersAdmin");
			if (QwyUtil.isNullAndEmpty(users)) {
				json = QwyUtil.getJSONString("err", "管理员未登录");
				QwyUtil.printJSON(getResponse(), json);
				//管理员没有登录;
				return null;
			}
			if(!QwyUtil.isNullAndEmpty(startPage)){
				if(!QwyUtil.isNullAndEmpty(bean.saveStartPage(startPage))){
					request.setAttribute("isOk", "ok");
					return "StartPage";
				}
			}
		} catch (Exception e) {
			log.error("操作异常: ",e);
			log.error("操作异常: ",e);
		}
		request.setAttribute("isOk", "no");
		return "StartPage";
	}
	
	/**
	 * 根据id修改状态
	 * @return
	 */
	public String updateStatuById() {
		String json = "";
		try {
			UsersAdmin users = (UsersAdmin) getRequest().getSession()
					.getAttribute("usersAdmin");
			if (QwyUtil.isNullAndEmpty(users)) {
				json = QwyUtil.getJSONString("err", "管理员未登录");
				QwyUtil.printJSON(getResponse(), json);
				//管理员没有登录;
				return null;
			}
			if(bean.updateStatusById(startPage.getId())){
				request.setAttribute("update", "ok");
				json = QwyUtil.getJSONString("ok", "成功");
				QwyUtil.printJSON(getResponse(), json);
				return null;
			}
		} catch (Exception e) {
			log.error("操作异常: ",e);
			log.error("操作异常: ",e);
		}
		json = QwyUtil.getJSONString("error", "失败");
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			log.error("操作异常: ",e);
		}
		return null;	
	}
	
	
	/**
	 * 上传手机图片
	 * @return
	 */
	public String uploadMobileStartPageImage(){
		return uploadStartPageImage(getRequest().getParameter("type"));
	}
	
	/**
	 * 上传图片
	 * @param status 
	 * 
	 * @return
	 */
	public String uploadStartPageImage(String status) {
		String tempType = "common/";
		if("0".equals(status)){
			tempType = "ios/";
		}else if("1".equals(status)){
			tempType = "android/";
		}else {
			tempType = "common/";
		}
		
		
		OssUtil ossUtil = new OssUtil();
		log.info("上传图片"+file);
		request = getRequest();
		SystemConfig systemConfig = (SystemConfig) request.getServletContext().getAttribute("systemConfig");
		String json="";
		String imageName = "";
		if (!QwyUtil.isNullAndEmpty(file)) {
			try {
				String sux = fileContentType.split("/")[1];
				String fileName = UUID.randomUUID().toString() + "."+sux;
				imageName += fileName;
				String folder = systemConfig.getFileUrl() + "/mobile_img/StartPage/"+tempType;
				
				String newFile = folder + fileName;
                ossUtil.uploadFile(file, newFile, null);
            	json = QwyUtil.getJSONString("ok", imageName);
			}catch (Exception e) {
				log.error("上传图片失败"+e);
				json = QwyUtil.getJSONString("error", "上传图片失败");
			}finally{
				if(!QwyUtil.isNullAndEmpty(ossUtil)){
					ossUtil.destroy();
				}
			}
		}
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			log.error("上传图片失败"+e);
		}
		return null;
	}
	
	
	/**
	 * 上传手机图片
	 * @return
	 */
	public String removeMobileStartPageImage(){
		return removeImage(getRequest().getParameter("type"));
	}
	
	/**
	 * 移除上传的图片
	 * 
	 * @return
	 */
	public String removeImage(String status) {
		try {
			QwyUtil.printJSON(getResponse(), QwyUtil.getJSONString("ok",""));
		} catch (IOException e) {
			log.error("删除图片失败！"+e);
		}
		return null;
	}
	
	
	

	public StartPage getStartPage() {
		return startPage;
	}

	public void setStartPage(StartPage startPage) {
		this.startPage = startPage;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getFileContentType() {
		return fileContentType;
	}

	public void setFileContentType(String fileContentType) {
		this.fileContentType = fileContentType;
	}

	public String getRemoveId() {
		return removeId;
	}

	public void setRemoveId(String removeId) {
		this.removeId = removeId;
	}
	
	
}
