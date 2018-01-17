package com.huoq.admin.product.action;

import com.huoq.admin.product.bean.BlackListBean;
import com.huoq.admin.product.bean.SmsRecordBean;
import com.huoq.admin.product.bean.UsersAdminBean;
import com.huoq.common.action.BaseAction;
import com.huoq.common.bean.SystemConfigBean;
import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.common.util.SMSUtil;
import com.huoq.login.bean.UsersLoginBean;
import com.huoq.orm.BlackList;
import com.huoq.orm.Users;
import com.huoq.orm.UsersAdmin;
import org.apache.poi.hssf.usermodel.*;
import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;

import javax.annotation.Resource;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 黑名单管理
 * Created by yks on 2016/10/13.
 */
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/Admin")
@Results({
        @Result(name = "blacklist", value = "/Product/Admin/blacklistManager/black_list.jsp"),
        @Result(name = "addBlacklist", value = "/Product/Admin/blacklistManager/add_black_list.jsp"),
        @Result(name = "err", value = "/Product/Admin/err.jsp")
})
public class BlackListAction extends BaseAction {

    private PageUtil<BlackList> pageUtil;
    private Integer currentPage = 1;
    private Integer pageSize = 50;
    private String status = "all";
    private String username = ""; //黑名单
    private String blackListId = ""; //黑名单id
    private String insertTime;
    private BlackList blackList;
    private Long id;
    private String description;//添加描述

    @Resource
    private BlackListBean bean;
    @Resource
    private SystemConfigBean systemConfigBean;
    @Resource
    private SmsRecordBean smsRecordBean;

    /**
     * 黑名单列表显示
     *
     * @return
     */
    public String listBlack() {
        String json = "";
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                // 管理员没有登录;
                return null;
            }
            PageUtil<BlackList> pageUtil = new PageUtil<BlackList>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(pageSize);
            StringBuilder url = new StringBuilder();
            url.append(getRequest().getServletContext().getContextPath());
            url.append("/Product/Admin/blackList!listBlack.action?");
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                url.append("&insertTime=");
                url.append(insertTime);
            }
            if (!QwyUtil.isNullAndEmpty(status)) {
                url.append("&status=");
                url.append(status);
            }
            if (!QwyUtil.isNullAndEmpty(username)) {
                username = username.trim();
                if (QwyUtil.verifyPhone(username)){//格式为手机号
                    url.append("&username=");
                    url.append(username);
                }else{                        //否则为userId
                    Users user = bean.findUsersById(Long.parseLong(username));
                    if(!QwyUtil.isNullAndEmpty(user)){
                        username = DESEncrypt.jieMiUsername(user.getUsername());
                        url.append("&username=");
                        url.append(username);
                    }
                }
            }
            pageUtil.setPageUrl(url.toString());
            pageUtil = bean.loadBlackListRecords(pageUtil, username, status, insertTime);
            if (!QwyUtil.isNullAndEmpty(pageUtil)) {
                getRequest().setAttribute("insertTime", insertTime);
                getRequest().setAttribute("username", username);
                getRequest().setAttribute("pageUtil", pageUtil);
                getRequest().setAttribute("list", pageUtil.getList());
                return "blacklist";
            }
        } catch (Exception e) {
            log.error("操作异常: ", e);
            json = QwyUtil.getJSONString("err", "查询记录异常");
        }
        return "blacklist";
    }
    /**
     * 添加描述
     */
    public String descriptionAdd(){
    	  String json = "";
          try {
              UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
              if (QwyUtil.isNullAndEmpty(users)) {
                  json = QwyUtil.getJSONString("err", "管理员未登录");
                  QwyUtil.printJSON(getResponse(), json);
                  // 管理员没有登录;
                  return null;
              }
              //添加描述
              bean.updateDescription(blackListId, description);
              json = QwyUtil.getJSONString("ok", "添加成功");
              QwyUtil.printJSON(getResponse(), json);
              
          } catch (Exception e) {
              log.error("操作异常: ", e);
              json = QwyUtil.getJSONString("err", "添加异常");
              try {
				QwyUtil.printJSON(getResponse(), json);
			} catch (IOException e1) {
				log.error("操作异常",e1);
			}
          }
    	return null;
    }


    /**
     * 更改黑名单状态
     * add by yks 2016-10-03
     *
     * @return
     */
    public String changeBlackListStatus() {
        String json = "";
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                //管理员没有登录;
                return null;
            }
            String blackListId = getRequest().getParameter("blackListId"); //待审核提现记录id
            String status = getRequest().getParameter("status"); //审核状态
            log.info("【黑名单记录】，黑名单记录id:" + blackListId);
            if (QwyUtil.isNullAndEmpty(blackListId) || QwyUtil.isNullAndEmpty(status)) {
                json = QwyUtil.getJSONString("err", "操作失败");
                QwyUtil.printJSON(getResponse(), json);
                return null;
            }
            //改变黑名单状态
            bean.updateBlackListStatus(blackListId, status);
            json = QwyUtil.getJSONString("ok", "操作成功");
            QwyUtil.printJSON(getResponse(), json);
        } catch (Exception e) {
            log.error("操作异常: ",e);
            log.error("操作异常: ", e);
        }
        return null;
    }


    /**
     * 跳转到黑名单添加页面
     *
     * @return
     */
    public String toAddBlackList() {
        String json = "";
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                // 管理员没有登录;
                return null;
            }
            BlackList blackList = new BlackList();
            Map<String, Object> map = SMSUtil.queryBalance();
            request.setAttribute("map", map);
            request.setAttribute("message", map.get("message"));
            request.setAttribute("blackList", blackList);
        } catch (Exception e) {
            log.error("操作异常: ", e);
            return "error";
        }
        return "addBlacklist";
    }

    /**
     * 添加黑名单
     *
     * @return
     */
    public String addListBlack() {
        String json = "";
        try {
            UsersAdmin admin = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(admin)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                // 管理员没有登录;
                return null;
            }
            if (!QwyUtil.isNullAndEmpty(blackList)) {
                String mobileString = blackList.getUsername();
                String[] strArray = null;
                strArray = blackList.getUsername().split(","); // 拆分字符为","
                // ,然后把结果交给数组strArray
                for (int i = 0; i < strArray.length; i++) {
                    if (!QwyUtil.verifyPhone(strArray[i])) {
                        json = QwyUtil.getJSONString("error", "手机号有误：" + strArray[i]);
                        break;
                    }
                }
                if (QwyUtil.isNullAndEmpty(json)) {// 判断手机格式
                    //String topContent = systemConfigBean.findSystemConfig().getSmsQm();// 一条短信长度是67
                    if (!QwyUtil.isNullAndEmpty(blackList.getNote())) {
                        if (blackList.getNote().length() > 500) {
                            json = QwyUtil.getJSONString("error", "备注内容过长");
                        } else {
                            //String content = topContent + blackList.getNote();
                            //Map<String, Object> map = SMSUtil.sendYzm2(mobileString, null, content);
                            /*if (!QwyUtil.isNullAndEmpty(map)) {
                                smsRecordBean.addSmsRecord(mobileString, content, map.get("error").toString(), admin.getId());
                                json = QwyUtil.getJSONString("ok", "【黑名单】发送短信成功");
                            } else {
                                json = QwyUtil.getJSONString("error", "【黑名单】发送短信异常");
                            }*/
                            bean.addPhones2BlackList(blackList);//添加黑名单
                            json = QwyUtil.getJSONString("ok", "添加【" + blackList.getUsername() + "】至黑名单成功");
                        }
                    } else {
                        json = QwyUtil.getJSONString("error", "短信内容不能为空");
                    }
                }
            } else {
                json = QwyUtil.getJSONString("error", "请填写黑名单不能为空");
            }
        } catch (Exception e) {
            log.error("操作异常: ",e);
            json = QwyUtil.getJSONString("error", "操作异常");
        }
        try {
            QwyUtil.printJSON(getResponse(), json);
        } catch (Exception e) {
            log.error("操作异常: ",e);
        }
        return null;
    }


    /**
     * 导出黑名单记录报表
     */
    public String iportTable() {
        try {
            PageUtil<BlackList> pageUtil = new PageUtil<BlackList>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(999999);
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("黑名单记录");
            HSSFRow row = sheet.createRow((int) 0);
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
            HSSFCell cell = row.createCell(0);
            cell.setCellValue("序号");
            cell.setCellStyle(style);
            cell = row.createCell(1);
            cell.setCellValue("黑名单");
            cell.setCellStyle(style);
            cell = row.createCell(2);
            cell.setCellValue("ip");
            cell.setCellStyle(style);
            cell = row.createCell(3);
            cell.setCellValue("手机IMEI");
            cell.setCellStyle(style);
            cell = row.createCell(4);
            cell.setCellValue("创建时间");
            cell.setCellStyle(style);
            cell = row.createCell(5);
            cell.setCellValue("状态");
            cell.setCellStyle(style);
            cell = row.createCell(6);
            cell.setCellValue("备注");
            cell.setCellStyle(style);
            BlackList blackList = null;
            List list = bean.exportBlackListExport(pageUtil, username, status, insertTime);
            for (int i = 0; i < list.size(); i++) {
                row = sheet.createRow((int) i + 1);
                blackList = (BlackList) list.get(i);
                row = sheet.createRow((int) i + 1);
                row.createCell(0).setCellValue((int) i + 1);
                row.createCell(1).setCellValue(blackList.getUsername());
                row.createCell(2).setCellValue(blackList.getIp());
                row.createCell(3).setCellValue(blackList.getImei());
                row.createCell(4).setCellValue(QwyUtil.fmyyyyMMddHHmmss.format(blackList.getInsertTime()));
                row.createCell(5).setCellValue(blackList.getStatus());
                row.createCell(6).setCellValue(blackList.getNote());
            }
            String pathname = QwyUtil.fmyyyyMMddHHmmss3.format(new Date()) + "_black_list_record.xls";
            String realPath = request.getServletContext().getRealPath("/report/" + pathname);
            log.info("黑名单记录报表地址：" + realPath);
            FileOutputStream fout = new FileOutputStream(realPath);
            wb.write(fout);
            fout.close();
            response.getWriter().write("/report/" + pathname);
        } catch (Exception e) {
            log.error("操作异常: ",e);
        }
        return null;
    }
    
    
    /**
     * 更改黑名单状态
     * @return
     */
    public String updateStatus(){
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
			if (!QwyUtil.isNullAndEmpty(id)) {
				if(bean.updateStatusById(id)){
					request.setAttribute("update", "ok");
					json = QwyUtil.getJSONString("ok", "成功");
					QwyUtil.printJSON(getResponse(), json);
					return null;
				}
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
    	
    	return null;
    }
    

    public PageUtil<BlackList> getPageUtil() {
        return pageUtil;
    }

    public void setPageUtil(PageUtil<BlackList> pageUtil) {
        this.pageUtil = pageUtil;
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

    public String getBlackListId() {
        return blackListId;
    }

    public void setBlackListId(String blackListId) {
        this.blackListId = blackListId;
    }

    public BlackList getBlackList() {
        return blackList;
    }

    public void setBlackList(BlackList blackList) {
        this.blackList = blackList;
    }
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
    
}
