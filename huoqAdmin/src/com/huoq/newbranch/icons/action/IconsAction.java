package com.huoq.newbranch.icons.action;

import com.huoq.common.action.BaseAction;
import com.huoq.common.bean.SystemConfigBean;
import com.huoq.common.util.MyRedis;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.newbranch.constants.Constants;
import com.huoq.newbranch.enums.ModuleEnum;
import com.huoq.newbranch.icons.service.IconsService;
import com.huoq.newbranch.orm.Icons;
import com.huoq.orm.SystemConfig;
import com.huoq.ossUtils.OssUtil;
import org.apache.log4j.Logger;
import org.apache.struts2.config.*;
import org.apache.struts2.dispatcher.ServletRedirectResult;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Description：新华金典理财后台-图标模块
 * Created by zf on 2017/6/7.
 */
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Icons")
@Results({
        @Result(name = "iconsList", value = "/icons/iconsList.jsp"),
        @Result(name = "addIcons", value = "/icons/addIcons.jsp"),
        @Result(name = "editIcons", value = "/icons/editIcons.jsp"),
        @Result(name = "list", value = "/Icons/icons!loadIcons.action", type = ServletRedirectResult.class)
})
public class IconsAction extends BaseAction {

    private static Logger log = Logger.getLogger(IconsAction.class);

    @Resource
    private IconsService iconsService;

    @Resource
    private SystemConfigBean systemConfigBean;

    private Icons icons;
    private File file;
    private String fileContentType;
    private String removeId;

    private Integer currentPage = 1;
    private Integer pageSize = 7;

    private String delFlag; // 逻辑删除标识

    /**
     * 加载图标列表
     *
     * @return
     */
    public String loadIcons() {
        log.info("加载图标列表");
        try {
            SystemConfig config = systemConfigBean.findSystemConfig();
            String httpUrl = config.getHttpUrl();

            PageUtil<Icons> pageUtil = getIconsByPageUtil(currentPage, pageSize, icons);
            StringBuffer url = new StringBuffer();
            url.append(getRequest().getServletContext().getContextPath());
            url.append("/Icons/icons!loadIcons.action?");
            if (!QwyUtil.isNullAndEmpty(icons)) {
                url.append("icons.module=" + icons.getModule());
            }
            pageUtil.setPageUrl(url.toString());
            if (!QwyUtil.isNullAndEmpty(pageUtil)) {
                List<Icons> iconsList = pageUtil.getList();
                for (Icons icon : iconsList) {
                    icon.setIconURL(httpUrl + Constants.UPLOAD_ICONS_PATH + icon.getIconURL());
                }
                request.setAttribute("iconsList", iconsList);
                request.setAttribute("pageUtil", pageUtil);
            }
        } catch (Exception e) {
            log.error("操作异常: ", e);
            log.error("操作异常： ", e);
        }
        return "iconsList";
    }

    /**
     * 根据分布对象查询图标列表
     *
     * @param currentPage 当前页
     * @param pageSize    页大小
     * @param icons       图标
     * @return
     */
    public PageUtil<Icons> getIconsByPageUtil(int currentPage, int pageSize, Icons icons) {
        PageUtil<Icons> pageUtil = new PageUtil<>();
        pageUtil.setCurrentPage(currentPage);
        pageUtil.setPageSize(pageSize);
        pageUtil = iconsService.getIconsByCondition(pageUtil, icons);
        return pageUtil;
    }

    /**
     * 跳转到添加页面
     *
     * @return
     */
    public String saveIconsUI() {
        return "addIcons";
    }

    /**
     * 保存图标
     *
     * @return
     */
    public String saveIcons() throws ParseException {
        log.info("添加图标");
        try {
            if (!QwyUtil.isNullAndEmpty(icons)) {
                // 获取图标URL
                String iconName = (String) request.getSession().getAttribute("iconName");
                icons.setIconURL(iconName);
                // 获取最大序列号
                Long maxSeq = iconsService.getMaxSeq();
                if (maxSeq == null) {
                    maxSeq = 0L;
                }
                icons.setSeq(maxSeq + 100);
                // 插入创建时间
                if (icons.getCreateTime() == null) {
                    icons.setCreateTime(new Date());
                }
                icons.setIsDelete('0');
                iconsService.saveIcons(icons);
                // 清除redis缓存
                deleteRedis();
                request.setAttribute("isOk", "ok");
                return "addIcons";
            }
        } catch (Exception e) {
            log.error("操作异常: ", e);
            log.error("添加图标异常：" + e);
            request.setAttribute("isOk", "no");
        } finally {
            deleteIconNameOfSession();
        }
        return "addIcons";
    }

    /**
     * 跳转到更新页面
     *
     * @return
     */
    public String toEditIcons() {
        if (!QwyUtil.isNullAndEmpty(icons) && !QwyUtil.isNullAndEmpty(icons.getId())) {
            icons = iconsService.findIconById(icons.getId());
        }
        return "editIcons";
    }

    /**
     * 更新图标
     *
     * @return
     */
    public String editIcons() {
        log.info("删除图标");
        try {
            if (!QwyUtil.isNullAndEmpty(icons)) {
                String delFlag = request.getParameter("delFlag");
                // 判断是否是逻辑删除
                if (!QwyUtil.isNullAndEmpty(delFlag)) {
                    icons = iconsService.findIconById(icons.getId());
                    icons.setIsDelete('1');
                }
                // 判断是否更新图标
                String newIconsName = (String) getRequest().getSession().getAttribute("iconName");
                if (!QwyUtil.isNullAndEmpty(newIconsName)) {
                    icons.setIconURL(newIconsName);
                }
                icons.setUpdateTime(new Date());
                iconsService.updateIcons(icons);
                // 清除redis缓存
                deleteRedis();
                request.setAttribute("isOk", "ok");
                return "editIcons";
            }
        } catch (Exception e) {
            log.error("操作异常: ", e);
            log.error("修改图标异常：" + e);
            request.setAttribute("isOk", "no");
        } finally {
            deleteIconNameOfSession();
        }
        return "editIcons";
    }

    /**
     * 上传图片
     *
     * @return
     */
    public String uploadIconsImage() {
        OssUtil ossUtil = new OssUtil(null);
        //change：图片存储到oss上
        log.info("上传图片" + file);
        request = getRequest();
        SystemConfig systemConfig = (SystemConfig) request.getServletContext().getAttribute("systemConfig");
        String json = "";
        String imageName = "";
        if (!QwyUtil.isNullAndEmpty(file)) {
            try {
                String sux = fileContentType.split("/")[1];
                String fileName = UUID.randomUUID().toString() + "." + sux;
                imageName += fileName + ";";
                String path = Constants.UPLOAD_ICONS_PATH.substring(1);
                ossUtil.uploadFile(file, path + fileName, null);

                // 将图标名称添加到session域中
                request.getSession().setAttribute("iconName", fileName);

                json = QwyUtil.getJSONString("ok", imageName);
            } catch (Exception e) {
                log.error("上传图片失败!" + e);

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
        return null;
    }

    /**
     * 移除上传的图片
     *
     * @return
     */
    public String removeImage() {
        try {
            log.info("移除图片" + file);
            QwyUtil.printJSON(getResponse(), QwyUtil.getJSONString("ok", ""));
        } catch (IOException e) {
            log.error(e);
        }
        deleteIconNameOfSession();
        return null;
    }

    /**
     * 删除session域中的图标地址
     */
    private void deleteIconNameOfSession() {
        request.getSession().removeAttribute("iconName");
    }

    /**
     * 刷新redis缓存
     */
    private void deleteRedis() {
        MyRedis redis = new MyRedis();
        for (ModuleEnum v : ModuleEnum.values()) {
            if (redis.exists(Constants.FENGYE_SYSICONS_ICONS + v.getCode())) {
                redis.del(Constants.FENGYE_SYSICONS_ICONS + v.getCode());
            }
        }
    }

    /**
     * 页面上手动刷新redis
     *
     * @throws IOException
     */
    public String flushRedis() throws IOException {
        deleteRedis();
        return "list";
    }


    public Icons getIcons() {
        return icons;
    }

    public void setIcons(Icons icons) {
        this.icons = icons;
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

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
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
