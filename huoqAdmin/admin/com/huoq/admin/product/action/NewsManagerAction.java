package com.huoq.admin.product.action;

import com.huoq.admin.product.bean.NewsManagerBean;
import com.huoq.common.action.BaseAction;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.News;
import com.huoq.orm.SystemConfig;
import com.huoq.orm.UsersAdmin;
import com.huoq.ossUtils.OssUtil;

import net.sf.json.JSONObject;
import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;

import javax.annotation.Resource;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * 后台发布新闻Action层<br>
 *
 * @author yks
 *         <p>
 *         2016-12-22
 */
@ParentPackage("struts-default")
@Namespace("/Product/Admin")
//发布产品页面
@Results({
        @Result(name = "releaseNews", value = "/Product/Admin/newsManager/newsManager.jsp"),
        @Result(name = "newsList", value = "/Product/Admin/newsManager/newsList.jsp"),
        @Result(name = "editNews", value = "/Product/Admin/newsManager/editNews.jsp"),
        @Result(name = "previewNews", value = "/Product/Admin/newsManager/news_detail.jsp"),
        @Result(name = "err", value = "/Product/Admin/err.jsp")
})
public class NewsManagerAction extends BaseAction {


    private static final long serialVersionUID = 1L;

    private News news;
    private File file;
    private File imgFile;//图片上传
    private String imgFileFileName;
    private String fileContentType;
    private String removeId;
    /**
     * 成功返回0，失败返回1
     */
    public int error;

    /**
     * 成功时返回上传的文件地址
     */
    public String url;

    /**
     * 失败时返回的提示信息
     */
    public String message;
    /**
     * 上传的文件类型
     */
    public String dir;

    @Resource
    private NewsManagerBean bean;

    /**
     * 发布新闻
     *
     * @return
     */
    public String releaseNews() {

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
            if (!QwyUtil.isNullAndEmpty(news)) {
                String id = bean.saveNews(news);
                if (!QwyUtil.isNullAndEmpty(id)) {
                    request.setAttribute("isOk", "ok");
                    return "releaseNews";
                }
            }
        } catch (Exception e) {
            log.error("操作异常: ",e);
            log.error("操作异常: ", e);
        }
        request.setAttribute("isOk", "no");
        return "releaseNews";
    }

    /**
     * 新闻列表展示
     *
     * @return
     */
    public String listNews() {
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
            String superName = "0EA5D6BC23E8EEC78F62546B9F68BABFA96976B775889BA625DB6D764FD0DBD42A1C0F45F85B0DE8";
            if (!superName.equals(users.getUsername())) {
                if (isExistsQX("公告展示", users.getId())) {
                    getRequest().setAttribute("err", "您没有操作该功能的权限!");
                    return "err";
                }
            }
            List<News> list = bean.findNewsList();
            request.setAttribute("list", list);
        } catch (Exception e) {
            log.error("操作异常: ",e);
            log.error("操作异常: ", e);
        }
        return "newsList";
    }


    /**
     * 跳转到修改新闻界面
     *
     * @return
     */
    public String toEditNews() {
        try {
            log.info("跳转到修改新闻界面" + news.getId());
            String json = "";
            UsersAdmin users = (UsersAdmin) getRequest().getSession()
                    .getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                //管理员没有登录;
                return null;
            }
            News n = bean.findById(news.getId());
            request.setAttribute("news", n);
        } catch (Exception e) {
            log.error("操作异常: ",e);
            log.error("操作异常: ", e);
        }
        return "editNews";
    }

    /**
     * 修改新闻
     *
     * @return
     */
    public String editNews() {
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
            if (!QwyUtil.isNullAndEmpty(news)) {
                if (bean.updateNotice(news)) {
                    request.setAttribute("isOk", "ok");
                    return "editNews";
                }
            }
        } catch (Exception e) {
            log.error("操作异常: ",e);
            log.error("操作异常: ", e);
        }
        request.setAttribute("isOk", "no");
        return "editNews";
    }


    /**
     * 预览新闻
     *
     * @return
     */
    public String previewNews() {
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
            if (!QwyUtil.isNullAndEmpty(news)) {
                news.setInsertTime(new Date());
                request.setAttribute("news", news);
                return "previewNews";
            }
        } catch (Exception e) {
            log.error("操作异常: ",e);
            log.error("操作异常: ", e);
        }
        return "releaseNews";
    }

    /**
     * 上传编辑内容图片;
     *
     * @return
     */
    public String uploadNewsImage() throws Exception {
        return uploadNewsImage("news");
    }


    /**
     * 上传缩略图;
     *
     * @return
     */
    public String uploadSLT() {
        return upload("news");
    }

    /**
     * 删除缩略图;
     *
     * @return
     */
    public String removeSLT() {
        return removeImage("news");
    }
    /**
     * 上传图片
     *
     * @return
     */
    public String upload(String type) {
    	OssUtil ossUtil = new OssUtil();
    	log.info("上传图片"+file);
        request = getRequest();
        SystemConfig systemConfig = (SystemConfig) request.getServletContext().getAttribute("systemConfig");
        String json = "";
        String imageName = "";
        if (!QwyUtil.isNullAndEmpty(file)) {
            try {
                String sux = fileContentType.split("/")[1];
                String fileName = UUID.randomUUID().toString() + "." + sux;
                imageName += fileName + ";";
                String folder = systemConfig.getFileUrl() + "/mobile_img/" + type + "/";
               
                String newFile = folder + fileName;

                ossUtil.uploadFile(file, newFile, null);
            	json = QwyUtil.getJSONString("ok", imageName);
//            	if (!type.equals("notice/slt")) {
//            		String mobile_folder = systemConfig.getFileUrl() + "/mobile_img/" + type + "/";
//            		//String newImgSrc = Images.yasuoImg(newFile,mobile_folder + fileName,200, 200);
//            		CompressPicDemo mypic = new CompressPicDemo();
//            		//新版本更新后,替换成高清图;
//            		String newImgSrc = mypic.compressPic(folder, mobile_folder, fileName, fileName, 1100, 1528, true);
//            		//旧版的上传图片;
//            		//String newImgSrc = mypic.compressPic(folder, mobile_folder, fileName, fileName, 240, 240, false);
//            		log.info(newImgSrc);
//            	}
            } catch (Exception e) {
            	log.error("上传图片失败"+e);
                json = QwyUtil.getJSONString("error", "上传图片失败");

            } finally {
                if (!QwyUtil.isNullAndEmpty(ossUtil)) {
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
     * 移除上传的图片
     *
     * @return
     */
    public String removeImage(String type) {
        try {
            QwyUtil.printJSON(getResponse(), QwyUtil.getJSONString("ok", ""));
        } catch (IOException e) {
        	log.error("删除图片失败"+e);
        }
        return null;
    }


    /**
     * 上传编辑内容图片
     * @return
     * @throws Exception
     */
    private String uploadNewsImage(String name) throws Exception {
    	OssUtil ossUtil = new OssUtil();
        JSONObject json=null;
        try {
            SystemConfig systemConfig = (SystemConfig) request.getServletContext().getAttribute("systemConfig");
            String savePath = systemConfig.getFileUrl() + "/mobile_img/"+name+"/";
            //文件保存目录URL
            String saveUrl =systemConfig.getHttpUrl()+systemConfig.getFileName()+ "/mobile_img/"+name+"/";
            if(QwyUtil.isNullAndEmpty(imgFile)){
                json= returnResult(1,"请选择文件。");
                QwyUtil.printJSON(getResponse(), json.toString());
                return null;
            }
           
            if(imgFile != null && !imgFile.toString().equals("")){
                //检查扩展名
                String fileExt = imgFileFileName.substring(imgFileFileName.lastIndexOf(".") + 1).toLowerCase();

                SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
                String newFileName = df.format(new Date()) + "_" + new Random().nextInt(1000) + "." + fileExt;

                ossUtil.uploadFile(imgFile, savePath+newFileName, null);
            	JSONObject obj = new JSONObject();
            	obj.put("error", 0);
            	obj.put("url", saveUrl + newFileName);
            	QwyUtil.printJSON(getResponse(), obj.toString());
            	return null;
            }else{
                json=returnResult(1,"上传的文件不存在!");
                QwyUtil.printJSON(getResponse(), json.toString());
                return null;
            }
        } catch (Exception e) {
            log.error("kindEditor上传文件出错了！");
            json=returnResult(1,"上传的文件不存在!");
        } finally {
           if(!QwyUtil.isNullAndEmpty(ossUtil)){
        	   ossUtil.destroy();
           }
        }
        QwyUtil.printJSON(getResponse(), json.toString());
        return null;
    }

    public JSONObject returnResult(Integer error, String message) {
        JSONObject obj = new JSONObject();
        obj.put("error", error);
        if(error==1){
            obj.put("message", message);
        }else{
            obj.put("url", message);
        }
        return obj;
    }



    public News getNews() {
        return this.news;
    }

    public void setNews(News news) {
        this.news = news;
    }

    public File getImgFile() {
        return this.imgFile;
    }

    public void setImgFile(File imgFile) {
        this.imgFile = imgFile;
    }

    public String getImgFileFileName() {
        return this.imgFileFileName;
    }

    public void setImgFileFileName(String imgFileFileName) {
        this.imgFileFileName = imgFileFileName;
    }

    public File getFile() {
        return this.file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFileContentType() {
        return this.fileContentType;
    }

    public void setFileContentType(String fileContentType) {
        this.fileContentType = fileContentType;
    }

    public String getRemoveId() {
        return this.removeId;
    }

    public void setRemoveId(String removeId) {
        this.removeId = removeId;
    }

    public int getError() {
        return this.error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDir() {
        return this.dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }
}
