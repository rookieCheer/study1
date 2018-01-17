package com.huoq.admin.product.action;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;

import com.huoq.admin.product.bean.VersionsBean;
import com.huoq.common.action.BaseAction;
import com.huoq.common.util.MyRedis;
import com.huoq.common.util.QwyUtil;
import com.huoq.newbranch.constants.Constants;
import com.huoq.newbranch.orm.SysConfigNew;
import com.huoq.orm.SystemConfig;
import com.huoq.orm.UsersAdmin;
import com.huoq.orm.Versions;
import com.huoq.ossUtils.OssUtil;

/**
 * 客户端版本管理;
 * @author qwy
 *
 * @createTime 2015-4-27下午3:51:34
 */
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/Admin")
	//发布产品页面
	@Results({ 
		@Result(name = "clientVersion", value = "/Product/Admin/clientVersionManager/clienVersions.jsp"),
		@Result(name = "version", value = "/Product/Admin/clientVersionManager/repleaseClienVersion.jsp"),
		@Result(name = "editVersions", value = "/Product/Admin/clientVersionManager/editClienVersion.jsp"),
		@Result(name = "err", value = "/Product/Admin/err.jsp")
})
public class ClientVersionAction extends BaseAction {
	@Resource
	VersionsBean bean;
	private Versions versions;
	
	 private File updateFile;//上传文件
	 private String updateFileContentType;//上传文件类型
	 private String updateFileFileName;//上传文件名字
	
	/**
	 * 加载版本
	 * @return
	 */
	public String loadClientVersion(){
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
				if(isExistsQX("版本展示", users.getId())){
					getRequest().setAttribute("err", "您没有操作该功能的权限!");
					return "err";
				}
			}
			if(QwyUtil.isNullAndEmpty(versions)){
				versions=new Versions();
			}
			SystemConfig systemConfig = (SystemConfig) request.getServletContext().getAttribute("systemConfig");
			List<Versions> list=bean.findVersions(versions.getType(), versions.getStatus());
			getRequest().setAttribute("url", systemConfig.getHttpUrl());
			getRequest().setAttribute("downLoadPath", Constants.VERSIONS_UPLOAD_PATH);
			getRequest().setAttribute("folder", Constants.VERSIONS_DOWNLOAD_FOLDER);
			getRequest().setAttribute("list", list);
			getRequest().setAttribute("versions", versions);
			return "clientVersion";
		} catch (Exception e) {
			log.error("操作异常: ",e);
			json = QwyUtil.getJSONString("err", "查询记录异常");
		}
		return "clientVersion";
	}
	
	/**
	 * 保存版本信息
	 * @return
	 */
	public String saveClientVersion() {
		
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
			boolean flag = uploadUpdateFile(versions.getVersions());
			if("1".equals(versions.getType())){
				if(!flag){
					request.setAttribute("isOk", "no");
					return "version";
				}
			}
			if(!QwyUtil.isNullAndEmpty(versions)){
				versions.setPath(updateFileFileName);
				Versions version = bean.saveVersions(versions);
				if(!QwyUtil.isNullAndEmpty(version)){
					request.setAttribute("isOk", "ok");
					return "version";
				}
			}
		} catch (Exception e) {
			log.error("操作异常: ",e);
			log.error("操作异常: ",e);
		}
		request.setAttribute("isOk", "no");
		return "version";
	}
	
	  public boolean uploadUpdateFile(String versions) {
		  	boolean falg = false;
	        OssUtil ossUtil = new OssUtil(null);
	        log.info("上传文件" + updateFile);
	        request = getRequest();
	        String json = "";
	        if (!QwyUtil.isNullAndEmpty(updateFile)) {
	            try {
	                String  folder = Constants.VERSIONS_DOWNLOAD_PATH+ versions+Constants.VERSIONS_DOWNLOAD_FOLDER+updateFileFileName;
	                if(ossUtil.uploadFile(updateFile, folder, null)){
	                	falg = true;
	                }
//	                if (!ossUtil.isExistByPath(folder)) {
//	                    log.info("文件上传到oss失败！请检查配置项！");
//	                    falg = false;
//	                }

	            } catch (Exception e) {
	                log.error("上传文件失败!" + e);

	            } finally {
	                if (!QwyUtil.isNullAndEmpty(ossUtil)) {
	                    ossUtil.destroy();
	                }
	            }
	        }
	        try {
	            QwyUtil.printJSON(getResponse(), json);
	        } catch (IOException e) {
	            log.error("上传图片失败!" + e);
	        }
	        return falg;
	    }
	
	/**
	 * 根据id修改状态
	 * @return
	 */
	public void updateStatuById() {
		String json = "";
		try {
			UsersAdmin users = (UsersAdmin) getRequest().getSession()
					.getAttribute("usersAdmin");
			if (QwyUtil.isNullAndEmpty(users)) {
				json = QwyUtil.getJSONString("err", "管理员未登录");
				QwyUtil.printJSON(getResponse(), json);
				//管理员没有登录;
				return ;
			}
			Versions ver =  bean.findVersionsById(versions.getId());
			if("0".equals(versions.getStatus()) && "1".equals(ver.getType())){
				List<Versions> list = bean.findVersions("1");
				if(list.size() <= 1){
					json = QwyUtil.getJSONString("err","至少要保证android版本一个启用");
					QwyUtil.printJSON(getResponse(), json);
					return ;
				}
			}
			
			if(bean.updateStatusById(versions.getId())){
				request.setAttribute("update", "ok");
				json = QwyUtil.getJSONString("ok", "成功");
				QwyUtil.printJSON(getResponse(), json);
				return ;
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
			log.error("操作异常: ",e);
		}
	}
	/**
	 * 进入修改
	 * @return
	 */
	public String updateVersionsById() {
		String id =  request.getParameter("id");
		if(id == null || "".equals(id)){
			log.error("操作异常,要修改的id为空，修改失败!");
			return null;
		}
		Versions versions =  bean.findVersionsById(id);
		request.setAttribute("id", id);
		request.setAttribute("versions", versions.getVersions());
		request.setAttribute("content", versions.getContent());
		request.setAttribute("type", versions.getType());
		request.setAttribute("isUpdate", versions.getIsUpdate());
		request.setAttribute("path", versions.getPath());
		
		return "editVersions";
	}
	/**
	 * 根据id修改version
	 * @return
	 */
	public String updateVersions() {
		String json = "";
		try {
			UsersAdmin users = (UsersAdmin) getRequest().getSession()
					.getAttribute("usersAdmin");
			if (QwyUtil.isNullAndEmpty(users)) {
				json = QwyUtil.getJSONString("err", "管理员未登录");
				QwyUtil.printJSON(getResponse(), json);
				//管理员没有登录;
				return null ;
			}
			String type = versions.getType();
			Versions ver = bean.findVersionsById(versions.getId());
			if(ver == null){
				request.setAttribute("isOk", "err");
				return  "editVersions";
			}
			if(updateFile != null&&"1".equals(type)){
				 OssUtil ossUtil = new OssUtil(null);
				// ossUtil.deleteFileByFileName(ver.getPath());
				if(uploadUpdateFile(ver.getVersions())){
					ver.setPath(updateFileFileName);
				}
			}
			ver.setContent(versions.getContent());
			ver.setVersions(versions.getVersions());
			ver.setIsUpdate(versions.getIsUpdate());
			ver.setType(type);
			MyRedis yibu=new MyRedis();
			String versionKey = "";
			if("1".equals(type)){
				versionKey ="android"+type;
			}
			if("0".equals(type)){
				versionKey = "ios"+type;
			}
			
			yibu.delObject(Constants.VERSIONKEY + versionKey);
			bean.updateVersionsById(ver);
			request.setAttribute("isOk", "ok");
			return "editVersions";
		
		} catch (Exception e) {
			e.printStackTrace();
			log.error("操作异常: ",e);
		}
		json = QwyUtil.getJSONString("error", "失败");
		try {
			QwyUtil.printJSON(getResponse(), json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("操作异常: ",e);
		}
		return null;
	}

	
	public Versions getVersions() {
		return versions;
	}
	public void setVersions(Versions versions) {
		this.versions = versions;
	}
	
	
	/**
	 * 删除缓存
	 */
	public String deleteRedis(){
		try {
			if(QwyUtil.isNullAndEmpty(versions)){
				versions=new Versions();
			}
			List<Versions> list=bean.findVersions(versions.getType(), versions.getStatus());
			MyRedis yibu=new MyRedis();
			String versionKey = "";
			for(Versions versions:list){
				if("1".equals(versions.getType())){
					versionKey ="android"+versions.getType();
				}
				if("0".equals(versions.getType())){
					versionKey = "ios"+versions.getType();
				}
				yibu.delObject(Constants.VERSIONKEY + versionKey);
			}
			request.setAttribute("isDelete", "ok");
			
		} catch (Exception e) {
			log.error("删除缓存出现异常",e);
			log.error("操作异常: ",e);
		}
		return "clientVersion";
	}

	public VersionsBean getBean() {
		return bean;
	}

	public void setBean(VersionsBean bean) {
		this.bean = bean;
	}

	public File getUpdateFile() {
		return updateFile;
	}

	public void setUpdateFile(File updateFile) {
		this.updateFile = updateFile;
	}

	public String getUpdateFileContentType() {
		return updateFileContentType;
	}

	public void setUpdateFileContentType(String updateFileContentType) {
		this.updateFileContentType = updateFileContentType;
	}

	public String getUpdateFileFileName() {
		return updateFileFileName;
	}

	public void setUpdateFileFileName(String updateFileFileName) {
		this.updateFileFileName = updateFileFileName;
	}
	
}
