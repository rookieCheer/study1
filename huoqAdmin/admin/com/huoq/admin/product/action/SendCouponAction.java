package com.huoq.admin.product.action;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import com.huoq.common.util.DESEncrypt;
import org.apache.poi.hssf.usermodel.*;
import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;

import com.huoq.admin.product.bean.SendCouponBean;
import com.huoq.common.action.BaseAction;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.login.bean.RegisterUserBean;
import com.huoq.orm.Coupon;
import com.huoq.orm.CouponRecord;
import com.huoq.orm.Users;
import com.huoq.orm.UsersAdmin;
import com.huoq.orm.UsersInfo;

/**
 * 后天管理--发送红包;
 *
 * @author qwy
 * @createTime 2015-4-27上午11:44:54
 */
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/Admin")
// 发布产品页面
@Results({ @Result(name = "login", value = "/Product/loginBackground.jsp", type = org.apache.struts2.dispatcher.ServletRedirectResult.class),
		@Result(name = "coupon", value = "/Product/Admin/fundsManager/sendCoupon.jsp"),
		@Result(name = "couponRecord", value = "/Product/Admin/fundsManager/couponRecord.jsp"),
		@Result(name = "couponRecordNote", value = "/Product/Admin/fundsManager/couponRecordNote.jsp"),
		@Result(name = "err", value = "/Product/Admin/err.jsp") })

public class SendCouponAction extends BaseAction {
    private Coupon con;
    private String overTime;
    private String overTimeNum;
    private String useTime = QwyUtil.fmMMddyyyy.format(new Date());
    private String username;
    @Resource
    private SendCouponBean bean;
    private String isBindBank;
    private Integer currentPage = 1;
    private Integer pageSize = 50;
    private String insertTime;
    private String status;
    private String note;
    @Resource
    private RegisterUserBean registerUserBean;

    /**
     * 管理员发送红包给用户;
     * 新加字段：useRange 底层sendHongBao()添加此字段 可以为空 
     *  useRange 为空 则默认为：可用于全部产品（新手标、零钱罐除外）
     *  wxl 2017年2月16日11:44:24
     * @return
     */
    public String sendHongBao() {
        String json = "";
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                // 管理员没有登录;
                return null;
            }
            Users us = bean.getUsersByUsername(username);
            if (QwyUtil.isNullAndEmpty(us)) {
                // 没有找到这个用户;
                json = QwyUtil.getJSONString("err", "用户名不存在");
                QwyUtil.printJSON(getResponse(), json);
                return null;
            }
            con.setMoney(QwyUtil.calcNumber(con.getMoney(), 100, "*").doubleValue());
            if (!QwyUtil.isNullAndEmpty(overTime)){
            	con.setOverTime(QwyUtil.fmyyyyMMddHHmmss.parse(overTime + " 00:00:00"));
            }
            if (!QwyUtil.isNullAndEmpty(overTimeNum)) {
            	con.setOverTime(QwyUtil.addDaysFromOldDate(new Date(), Integer.parseInt(overTimeNum)).getTime());
			}
//              con.setOverTime(QwyUtil.fmyyyyMMddHHmmss.parse(overTime + " 00:00:00"));
            boolean isSend = bean.sendHongBao(us.getId(), con.getMoney(), con.getOverTime(), con.getType(), users.getId(), con.getNote(),con.getUseRange());
            if (isSend) {
                // 发送成功;
                json = QwyUtil.getJSONString("ok", "发放红包成功");
            } else {
                json = QwyUtil.getJSONString("err", "发放红包失败");
            }
        } catch (Exception e) {
            log.error("操作异常: ", e);
            json = QwyUtil.getJSONString("err", "发放红包异常");
        }
        try {
            QwyUtil.printJSON(getResponse(), json);
        } catch (IOException e) {
            log.error("操作异常: ", e);
        }
        return null;
    }
	/**
	 * 投资券备注查询
	 */
	public String couponRecordNote(){
		
		String json = "";
		try {
			UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
			if (QwyUtil.isNullAndEmpty(users)) {
				json = QwyUtil.getJSONString("err", "管理员未登录");
				QwyUtil.printJSON(getResponse(), json);
				// 管理员没有登录;
				return "login";
			}
			String superName = "0EA5D6BC23E8EEC78F62546B9F68BABFA96976B775889BA625DB6D764FD0DBD42A1C0F45F85B0DE8";
			if (!superName.equals(users.getUsername())) {
				if (isExistsQX("投资卷记录", users.getId())) {
					getRequest().setAttribute("err", "您没有操作该功能的权限!");
					return "err";
				}
			}
			PageUtil<CouponRecord> pageUtil = new PageUtil<CouponRecord>();
			pageUtil.setCurrentPage(currentPage);
			pageUtil.setPageSize(pageSize);
			StringBuffer url = new StringBuffer();
			url.append(getRequest().getServletContext().getContextPath());
			url.append("/Product/Admin/sendCoupon!couponRecordNote.action?username=" + username);
			if (!QwyUtil.isNullAndEmpty(insertTime)) {
				url.append("&insertTime=");
				url.append(insertTime);
			}
			url.append("&note=");
			url.append(note);
			url.append("&useTime=");
			url.append(useTime);
			pageUtil.setPageUrl(url.toString());
			pageUtil = bean.findCouponslist(pageUtil, insertTime, useTime, username,note);
			if (!QwyUtil.isNullAndEmpty(insertTime)) {
				getRequest().setAttribute("insertTime", insertTime);
			}
			if (!QwyUtil.isNullAndEmpty(username)) {
				getRequest().setAttribute("username", username);
			}
			if (!QwyUtil.isNullAndEmpty(useTime)) {
				getRequest().setAttribute("useTime", useTime);
			}
			if (!QwyUtil.isNullAndEmpty(pageUtil)) {
				getRequest().setAttribute("pageUtil", pageUtil);
			}
			if (!QwyUtil.isNullAndEmpty(note)) {
				getRequest().setAttribute("note", note);
			}
			getRequest().setAttribute("table", "1");
		} catch (Exception e) {
			log.error("操作异常: ", e);
			json = QwyUtil.getJSONString("err", "投资券记录异常");
		}
		return "couponRecordNote";
	}
	/**
	 * 导出已使用的投资券
	 */
	public String exoprtcouponRecordNote(){
		try {
				//判断时间是否为一个月
				if(QwyUtil.isNullAndEmpty(insertTime) && QwyUtil.isNullAndEmpty(useTime)){
					String json = QwyUtil.getJSONString("err", "时间不能为空");
					QwyUtil.printJSON(response, json);
					return null;
				}
				if(!QwyUtil.isNullAndEmpty(insertTime)){
				String[] time = QwyUtil.splitTime(insertTime);
				if (time.length > 1) {
					Date date1=QwyUtil.fmMMddyyyy.parse(time[0]);
					Date date2=QwyUtil.fmMMddyyyy.parse(time[1]);
					int date3=QwyUtil.getDifferDays(date1, date2);
					if(date3>31){
						String json = QwyUtil.getJSONString("err", "大于一个月不可以导出");
						QwyUtil.printJSON(response, json);
						return null;
					}
				}
				}
				if(!QwyUtil.isNullAndEmpty(useTime)){
				String[] time2 = QwyUtil.splitTime(useTime);
				if (time2.length > 1) {
					Date date1=QwyUtil.fmMMddyyyy.parse(time2[0]);
					Date date2=QwyUtil.fmMMddyyyy.parse(time2[1]);
					int date3=QwyUtil.getDifferDays(date1, date2);
					if(date3>31){
						String json = QwyUtil.getJSONString("err", "大于一个月不可以导出");
						QwyUtil.printJSON(response, json);
						return null;
					}
				}
				}
				PageUtil<CouponRecord> pageUtil = new PageUtil<CouponRecord>();
				pageUtil.setCurrentPage(currentPage);
				pageUtil.setPageSize(9999999);
				StringBuffer url = new StringBuffer();
				url.append(getRequest().getServletContext().getContextPath());
				url.append("/Product/Admin/sendCoupon!couponRecordNote.action?username=" + username);
				if (!QwyUtil.isNullAndEmpty(insertTime)) {
					url.append("&insertTime=");
					url.append(insertTime);
				}
				url.append("&note=");
				url.append(note);
				url.append("&useTime=");
				url.append(useTime);
				pageUtil.setPageUrl(url.toString());
				
				HSSFWorkbook wb = new HSSFWorkbook();  
			    HSSFSheet sheet = wb.createSheet("已使用的投资券");  
			    HSSFRow row = sheet.createRow((int) 1); 
			    HSSFCellStyle style = wb.createCellStyle();  
			    style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式 
			    row = sheet.createRow(0);
			    row.createCell(0).setCellValue("序号");  
			    row.createCell(1).setCellValue("用户名");  
			    row.createCell(2).setCellValue("发送金额");  
			    row.createCell(3).setCellValue("状态");  
			    row.createCell(4).setCellValue("发送时间");  
			    //row.createCell(5).setCellValue("到期时间");
			    row.createCell(5).setCellValue("使用时间");
			    row.createCell(6).setCellValue("注册平台");
			    row.createCell(7).setCellValue("注册渠道");
			    row.createCell(8).setCellValue("备注");
			    row.createCell(9).setCellValue("备注2");
			    row.createCell(10).setCellValue("投入金额");
		        
			    pageUtil = bean.findCouponslist(pageUtil, insertTime, useTime, username,note);
				  List list = pageUtil.getList();
				  CouponRecord pla = null;
				 
				  for (int i = 0; i < list.size(); i++) {  
			        	row = sheet.createRow((int) i + 1);
			        	pla = (CouponRecord)list.get(i);
			        	row.createCell(0).setCellValue((int) i + 1);
			        	row.createCell(1).setCellValue(DESEncrypt.jieMiUsername(pla.getUsers().getUsername()));
			        	row.createCell(2).setCellValue(QwyUtil.calcNumber(pla.getMoney(),100, "/", 2)+"");
			        	row.createCell(3).setCellValue(pla.getCoupon().getStatusChina());
			        	row.createCell(4).setCellValue(QwyUtil.fmyyyyMMddHHmmss.format(pla.getCoupon().getInsertTime()));
			        	//row.createCell(5).setCellValue(QwyUtil.isNullAndEmpty(pla.getCoupon().getOverTime())?"":pla.getCoupon().getOverTime());
			        	row.createCell(5).setCellValue(QwyUtil.fmyyyyMMddHHmmss.format(pla.getCoupon().getUseTime()));
			        	row.createCell(6).setCellValue(pla.getUsers().getRegistPlatform());
			        	row.createCell(7).setCellValue(pla.getUsers().getRegistChannel());
			        	row.createCell(8).setCellValue(pla.getCoupon().getNote());
			        	row.createCell(9).setCellValue(pla.getNote());
			        	row.createCell(10).setCellValue(pla.getInvestors().getInMoney()*0.01);
				  }
				  
				  String realPath = request.getServletContext().getRealPath("/report/exoprtcouponRecordNote.xls");
			        FileOutputStream fout = new FileOutputStream(realPath);  
			        wb.write(fout);
			        fout.close();
			        //response.getWriter().write("/report/userInvertors.xls");
			        String json = QwyUtil.getJSONString("ok", "/report/exoprtcouponRecordNote.xls");
					QwyUtil.printJSON(response, json);
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return null;
	}

    /**
     * 投资券记录;
     *
     * @return
     */
    public String couponRecord() {
        String json = "";
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                // 管理员没有登录;
                return "login";
            }
            String superName = "0EA5D6BC23E8EEC78F62546B9F68BABFA96976B775889BA625DB6D764FD0DBD42A1C0F45F85B0DE8";
            if (!superName.equals(users.getUsername())) {
                if (isExistsQX("投资卷记录", users.getId())) {
                    getRequest().setAttribute("err", "您没有操作该功能的权限!");
                    return "err";
                }
            }
            PageUtil<Coupon> pageUtil = new PageUtil<Coupon>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(pageSize);
            StringBuffer url = new StringBuffer();
            url.append(getRequest().getServletContext().getContextPath());
            url.append("/Product/Admin/sendCoupon!couponRecord.action?username=" + username);
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                url.append("&insertTime=");
                url.append(insertTime);
            }
            url.append("&note=");
            url.append(note);
            url.append("&useTime=");
            url.append(useTime);
            url.append("&status=");
            url.append(status);
            pageUtil.setPageUrl(url.toString());
            pageUtil = bean.findCoupons(pageUtil, insertTime, useTime, username, status, note);
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                getRequest().setAttribute("insertTime", insertTime);
            }
            if (!QwyUtil.isNullAndEmpty(username)) {
                getRequest().setAttribute("username", username);
            }
            if (!QwyUtil.isNullAndEmpty(useTime)) {
                getRequest().setAttribute("useTime", useTime);
            }
            if (!QwyUtil.isNullAndEmpty(pageUtil)) {
                getRequest().setAttribute("pageUtil", pageUtil);
            }
            if (!QwyUtil.isNullAndEmpty(note)) {
                getRequest().setAttribute("note", note);
            }
        } catch (Exception e) {
            log.error("操作异常: ", e);
            json = QwyUtil.getJSONString("err", "投资券记录异常");
        }
        return "couponRecord";
    }

    /**
     * 管理员发送红包给用户;
     *
     * @return
     */
    public String sendUnbindHongBao() {
        String json = "";
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                // 管理员没有登录;
                return null;
            }
            con.setMoney(QwyUtil.calcNumber(con.getMoney(), 100, "*").doubleValue());
            if (!QwyUtil.isNullAndEmpty(overTime))
                con.setOverTime(QwyUtil.fmyyyyMMddHHmmss.parse(overTime + " 00:00:00"));
            boolean isSend = bean.sendHongBao(isBindBank, con.getMoney(), con.getOverTime(), con.getType(), users.getId(), con.getNote(),con.getUseRange());
            if (isSend) {
                // 发送成功;
                json = QwyUtil.getJSONString("ok", "发放红包成功");
            } else {
                json = QwyUtil.getJSONString("err", "发放红包失败");
            }
        } catch (Exception e) {
            log.error("操作异常: ", e);
            json = QwyUtil.getJSONString("err", "发放红包异常");
        }
        try {
            QwyUtil.printJSON(getResponse(), json);
        } catch (IOException e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 获取用户的真实姓名;
     *
     * @return
     */
    public String getRealNameByUsername() {
        String json = "";
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                // 管理员没有登录;
                return null;
            }
            if (QwyUtil.isNullAndEmpty(username)) {
                json = QwyUtil.getJSONString("err", "用户名不能为空");
                QwyUtil.printJSON(getResponse(), json);
                return null;
            }
            Users user = registerUserBean.getUsersByUsername(username);
            if (QwyUtil.isNullAndEmpty(user)) {
                json = QwyUtil.getJSONString("err", "用户名不存在");
                QwyUtil.printJSON(getResponse(), json);
                return null;
            }
            UsersInfo ui = user.getUsersInfo();
            if (QwyUtil.isNullAndEmpty(ui)) {
                json = QwyUtil.getJSONString("err", "用户信息不存在");
                QwyUtil.printJSON(getResponse(), json);
                return null;
            }
            String realName = ui.getRealName();
            json = QwyUtil.getJSONString("ok", realName);
        } catch (Exception e) {
            log.error("操作异常: ",e);
            log.error(e.getMessage(), e);
            json = QwyUtil.getJSONString("err", "服务器异常");
        }
        try {
            QwyUtil.printJSON(getResponse(), json);
        } catch (IOException e) {
            log.error("操作异常: ",e);
        }
        return null;
    }

    /**
     * 群发投资券
     *
     * @return
     */
    public String sendCouponGroup() {
        String json = "";
        try {
            UsersAdmin admin = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(admin)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                // 管理员没有登录;
                return null;
            }
            if (!QwyUtil.isNullAndEmpty(username)) {
                if (!QwyUtil.isNullAndEmpty(con)) {
                    if (!QwyUtil.isNullAndEmpty(con.getMoney())) {
                        con.setMoney(QwyUtil.calcNumber(con.getMoney(), 100, "*").doubleValue());
                        if (!QwyUtil.isNullAndEmpty(overTime)) {
                            con.setOverTime(QwyUtil.fmyyyyMMddHHmmss.parse(overTime + " 00:00:00"));
                        }
                        String[] strArray = null;
                        strArray = username.split(",");
                        for (int i = 0; i < strArray.length; i++) {
                            if (!QwyUtil.verifyPhone(strArray[i].trim())) {
                                json = QwyUtil.getJSONString("error", "用户名输入有误：" + strArray[i].trim());
                                break;
                            } else {
                                Users users = bean.getUsersByUsername(strArray[i].trim());
                                if (users == null) {
                                    json = QwyUtil.getJSONString("error", "该用户名不存在：" + strArray[i].trim() + "。之前的用户名已成功发送！！");
                                    break;
                                } else {
                                    // String note=new String
                                    // (con.getNote().getBytes("ISO-8859-1"),"UTF-8");
                                    bean.sendHongBao(users.getId(), con.getMoney(), con.getOverTime(), con.getType(), admin.getId(), con.getNote(),con.getUseRange());
                                }
                            }
                        }
                        if ("".equals(json)) {
                            json = QwyUtil.getJSONString("ok", "投资券群发成功");
                        }
                    } else {
                        json = QwyUtil.getJSONString("error", "投资券金额不能为空");
                    }

                } else {
                    json = QwyUtil.getJSONString("error", "投资券不能为空");

                }
            } else {
                json = QwyUtil.getJSONString("error", "用户名不能为空");
            }

        } catch (Exception e) {
            log.error("操作异常: ",e);
        }
        try {
            QwyUtil.printJSON(getResponse(), json);
        } catch (IOException e) {
            log.error("操作异常: ",e);
        }
        return null;
    }


    /**
     * 导出投资券记录报表
     */
    public String exportExcelData() {
        try {
            PageUtil<Coupon> pageUtil = new PageUtil<Coupon>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(999999);
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("投资券记录");
            HSSFRow row = sheet.createRow((int) 0);
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
            HSSFCell cell = row.createCell(0);
            cell.setCellValue("序号");
            cell.setCellStyle(style);
            cell = row.createCell(1);
            cell.setCellValue("用户名");
            cell.setCellStyle(style);
            cell = row.createCell(2);
            cell.setCellValue("发送金额");
            cell.setCellStyle(style);
            cell = row.createCell(3);
            cell.setCellValue("状态");
            cell.setCellStyle(style);
            cell = row.createCell(4);
            cell.setCellValue("发送时间");
            cell.setCellStyle(style);
            cell = row.createCell(5);
            cell.setCellValue("到期时间");
            cell.setCellStyle(style);
            cell = row.createCell(6);
            cell.setCellValue("使用时间");
            cell.setCellStyle(style);
            cell = row.createCell(7);
            cell.setCellValue("注册平台");
            cell.setCellStyle(style);
            cell = row.createCell(8);
            cell.setCellValue("注册渠道");
            cell.setCellStyle(style);
            cell = row.createCell(9);
            cell.setCellValue("备注");
            cell.setCellStyle(style);
            Coupon coupon = null;
            List list = bean.findCoupons(pageUtil, insertTime, useTime, username, status, note).getList();
            for (int i = 0; i < list.size(); i++) {
                row = sheet.createRow((int) i + 1);
                coupon = (Coupon) list.get(i);
                row = sheet.createRow((int) i + 1);
                row.createCell(0).setCellValue((int) i + 1);
                if (!QwyUtil.isNullAndEmpty(coupon.getUsers())) {
                    row.createCell(1).setCellValue(!QwyUtil.isNullAndEmpty(coupon.getUsers().
                            getUsername()) ? DESEncrypt.jieMiUsername(coupon.getUsers().getUsername()) : "");
                    if ("0".equals(coupon.getUsers().getRegistPlatform())) {
                        row.createCell(7).setCellValue("web端注册");
                    } else if ("1".equals(coupon.getUsers().getRegistPlatform())) {
                        row.createCell(7).setCellValue("Android移动端");
                    } else if ("2".equals(coupon.getUsers().getRegistPlatform())) {
                        row.createCell(7).setCellValue("IOS移动端");
                    } else if ("3".equals(coupon.getUsers().getRegistPlatform())) {
                        row.createCell(7).setCellValue("微信注册");
                    } else {
                        row.createCell(7).setCellValue(!QwyUtil.isNullAndEmpty(coupon.getUsers().
                                getRegistPlatform())?coupon.getUsers().getRegistPlatform():"");
                    }
                    row.createCell(8).setCellValue(!QwyUtil.isNullAndEmpty(coupon.getUsers().
                            getRegistChannel()) ? coupon.getUsers().getRegistChannel() : "");
                } else {
                    row.createCell(1).setCellValue("");
                    row.createCell(7).setCellValue("");
                    row.createCell(8).setCellValue("");
                }
                row.createCell(2).setCellValue(QwyUtil.calcNumber((QwyUtil.isNullAndEmpty(coupon.getInitMoney()) ?
                        0 : coupon.getInitMoney()), 0.01, "*", 2).doubleValue());
                row.createCell(3).setCellValue(!QwyUtil.isNullAndEmpty(coupon.getStatusChina()) ? coupon.getStatusChina() : "");
                row.createCell(4).setCellValue(!QwyUtil.isNullAndEmpty(coupon.getInsertTime()) ? QwyUtil.fmyyyyMMddHHmmss.format(coupon.getInsertTime()) : "");
                row.createCell(5).setCellValue(!QwyUtil.isNullAndEmpty(coupon.getOverTime()) ? QwyUtil.fmyyyyMMddHHmmss.format(coupon.getOverTime()) : "");
                row.createCell(6).setCellValue(!QwyUtil.isNullAndEmpty(coupon.getUseTime()) ? QwyUtil.fmyyyyMMddHHmmss.format(coupon.getUseTime()) : "");
                row.createCell(9).setCellValue(!QwyUtil.isNullAndEmpty(coupon.getNote()) ? coupon.getNote() : "");
            }
            String pathname = QwyUtil.fmyyyyMMddHHmmss3.format(new Date()) + "_coupon_record.xls";
            String realPath = request.getServletContext().getRealPath("/report/" + pathname);
            log.info("投资券记录报表地址：" + realPath);
            FileOutputStream fout = new FileOutputStream(realPath);
            wb.write(fout);
            fout.close();
            response.getWriter().write("/report/" + pathname);
        } catch (Exception e) {
            log.error("操作异常: ",e);
        }
        return null;
    }


    public Coupon getCon() {
        return con;
    }

    public void setCon(Coupon con) {
        this.con = con;
    }

    public String getOverTime() {
        return overTime;
    }

    public void setOverTime(String overTime) {
        this.overTime = overTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIsBindBank() {
        return isBindBank;
    }

    public void setIsBindBank(String isBindBank) {
        this.isBindBank = isBindBank;
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

    public String getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(String insertTime) {
        this.insertTime = insertTime;
    }

    public String getUseTime() {
        return useTime;
    }

    public void setUseTime(String useTime) {
        this.useTime = useTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
	public String getOverTimeNum() {
		return overTimeNum;
	}
	public void setOverTimeNum(String overTimeNum) {
		this.overTimeNum = overTimeNum;
	}
    
}
