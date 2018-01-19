package com.huoq.admin.product.action;

import com.huoq.admin.product.bean.ActivityBean;
import com.huoq.common.action.BaseAction;
import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.*;
import com.huoq.thread.bean.UpdateQdtjThreadBean;
import jdk.nashorn.internal.ir.RuntimeNode;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;

import javax.annotation.Resource;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * 入口;
 *
 * @author qwy
 * @createTime 2015-4-27下午3:51:34
 */
@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/Admin")
//发布产品页面
@Results({
        @Result(name = "activityStats", value = "/Product/Admin/operationManager/ActivityStat.jsp"),
        @Result(name = "loadQdtj", value = "/Product/Admin/operationManager/qdtj.jsp"),
        @Result(name = "loadQdcb", value = "/Product/Admin/operationManager/qdcb.jsp"),
        @Result(name = "err", value = "/Product/Admin/err.jsp"),
        @Result(name = "loadQdtjDetails", value = "/Product/Admin/operationManager/qdtjDetails.jsp"),
        @Result(name = "addQdtj", value = "/Product/Admin/operationManager/addQdtj.jsp")
})
public class ActivityAction extends BaseAction {

    private Qdtj qdtj;
    @Resource
    ActivityBean bean;
    @Resource
    private UpdateQdtjThreadBean updateQdtjThreadBean;
    private String insertTime;
    private String channelType;
    private String registChannel;
    private Integer currentPage = 1;
    private Integer pageSize = 50;
    private String channelCode;//渠道编码
    private String date;//单个渠道查询日期
    private String channelName;//渠道名称
    private List<Qdtj> qdtjlist;

    /**
     * 入口统计
     *
     * @return
     */
    public String loadActivityStat() {
        try {
            List<ActivityStat> activityStats = bean.findTjActivitys(insertTime);
            getRequest().setAttribute("list", activityStats);
            getRequest().setAttribute("insertTime", insertTime);
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return "activityStats";
    }

    /**
     * 渠道统计（安卓）
     *
     * @return
     */
    public String loadQdtj() {
        String json = "";
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                //管理员没有登录;
                return null;
            }
            String superName = "0EA5D6BC23E8EEC78F62546B9F68BABFA96976B775889BA625DB6D764FD0DBD42A1C0F45F85B0DE8";
            if (!superName.equals(users.getUsername())) {
                if (isExistsQX("渠道统计汇总表", users.getId())) {
                    getRequest().setAttribute("err", "您没有操作该功能的权限!");
                    return "err";
                }
            }
            getRequest().setAttribute("insertTime", insertTime);
            String sDate = "";
            String eDate = "";
            String[] time = QwyUtil.splitTime(insertTime);
            if (!QwyUtil.isNullAndEmpty(time))
                if (time.length > 1) {
                    sDate = QwyUtil.fmyyyyMMddHHmmss.format(QwyUtil.fmMMddyyyy.parse(time[0] + " 00:00:00"));
                    eDate = QwyUtil.fmyyyyMMddHHmmss.format(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
                } else {
                    sDate = QwyUtil.fmyyyyMMddHHmmss.format(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    eDate = QwyUtil.fmyyyyMMddHHmmss.format(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }

            List<Qdtj> listNew = bean.loadQdtjMain(sDate, eDate, channelType);

            if (!QwyUtil.isNullAndEmpty(listNew)) {
                getRequest().setAttribute("list", listNew);
                getRequest().setAttribute("table", "1");
                getRequest().setAttribute("tj", bean.tjQdtj(listNew));
                getRequest().setAttribute("channelType", channelType);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return "loadQdtj";
    }

    /**
     * 导出渠道统计（安卓）
     */
    public String iportQdtjTable() {
        List<JasperPrint> list = null;
        String name = QwyUtil.fmyyyyMMddHHmmss3.format(new Date()) + "_qdtj";
        try {
            String sDate = "";
            String eDate = "";
            String[] time = QwyUtil.splitTime(insertTime);
            if (!QwyUtil.isNullAndEmpty(time))
                if (time.length > 1) {
                    sDate = QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyy.parse(time[0]));
                    eDate = QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyy.parse(time[1]));
                } else {
                    sDate = QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyy.parse(time[0]));
                    eDate = QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyy.parse(time[0]));
                }
            String filePath = request.getServletContext().getRealPath("/WEB-INF/classes/qdtj.jasper");
            log.info("iportTable报表路径: " + filePath);
            list = bean.getQdtjJasperPrintList(sDate, eDate, filePath, channelType);
            doIreport(list, name);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 渠道成本（安卓）
     *
     * @return
     */
    public String loadQdcb() {
        String json = "";
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                //管理员没有登录;
                return null;
            }
            String superName = "0EA5D6BC23E8EEC78F62546B9F68BABFA96976B775889BA625DB6D764FD0DBD42A1C0F45F85B0DE8";
            if (!superName.equals(users.getUsername())) {
                if (isExistsQX("渠道成本", users.getId())) {
                    getRequest().setAttribute("err", "您没有操作该功能的权限!");
                    return "err";
                }
            }
            getRequest().setAttribute("insertTime", insertTime);
            PageUtil<BackStatsOperateDay> pageUtil = new PageUtil<BackStatsOperateDay>();
            //	PageUtil<BackStatsOperateDay> pageUtil = new PageUtil<Qdcb>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(31);
            StringBuffer url = new StringBuffer();
            url.append(getRequest().getServletContext().getContextPath());
            url.append("/Product/Admin/activity!loadQdcb.action?");
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                url.append("&insertTime=");
                url.append(insertTime);
            }
            pageUtil.setPageUrl(url.toString());
            pageUtil = bean.findQdcb(pageUtil, insertTime);
            if (!QwyUtil.isNullAndEmpty(pageUtil) && !QwyUtil.isNullAndEmpty(pageUtil.getList())) {
                getRequest().setAttribute("pageUtil", pageUtil);
                getRequest().setAttribute("list", pageUtil.getList());
                getRequest().setAttribute("table", "1");
                getRequest().setAttribute("tj", bean.tjQdcb(insertTime));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return "loadQdcb";
    }

    /**
     * 导出渠道成本（安卓）
     */


    public String iportQdcbTable() {
        try {
            String name = QwyUtil.fmyyyyMMddHHmmss3.format(new Date()) + "_qdcb";
            PageUtil<BackStatsOperateDay> pageUtil = new PageUtil<BackStatsOperateDay>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(999999);
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("Android渠道统计汇总表");
            HSSFRow row = sheet.createRow((int) 1);
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
            row = sheet.createRow(0);
            row.createCell(0).setCellValue("日期");
            row.createCell(1).setCellValue("注册人数");
            row.createCell(2).setCellValue("投资人数");
            row.createCell(3).setCellValue("投资金额");
            row.createCell(4).setCellValue("首投人数");
            row.createCell(5).setCellValue("首投金额");
            Qdcb cn = bean.tjQdcb(insertTime);
            if (!QwyUtil.isNullAndEmpty(cn)) {
                row = sheet.createRow(1);
                row.createCell(0).setCellValue("合计");
                row.createCell(1).setCellValue(cn.getRegUsersCount());
                row.createCell(2).setCellValue(cn.getInsUsersCount());
                row.createCell(3).setCellValue(QwyUtil.calcNumber(cn.getInsMoney(), 100, "/", 2) + "");
                row.createCell(4).setCellValue(cn.getStrs());
                row.createCell(5).setCellValue(QwyUtil.calcNumber(cn.getStje(), 100, "/", 2) + "");
            }
            pageUtil = bean.findQdcb(pageUtil, insertTime);
            List list = pageUtil.getList();
            BackStatsOperateDay report = null;
            if (!QwyUtil.isNullAndEmpty(cn)) {
                for (int i = 0; i < list.size(); i++) {
                    row = sheet.createRow((int) i + 2);
                    report = (BackStatsOperateDay) list.get(i);
                    row.createCell(0).setCellValue(report.getDate() + "");
                    row.createCell(1).setCellValue(report.getRegUserSum());
                    row.createCell(2).setCellValue(report.getInvestUserSum());
                    row.createCell(3).setCellValue(QwyUtil.calcNumber(report.getInvestCentSum(), 100, "/", 2) + "");
                    row.createCell(4).setCellValue(report.getFirstInvestUserSum());
                    row.createCell(5).setCellValue(QwyUtil.calcNumber(report.getFirstInvestCentSum(), 100, "/", 2) + "");
                }
            }
            String realPath = request.getServletContext().getRealPath("/report/qdtj.xls");
            FileOutputStream fout = new FileOutputStream(realPath);
            wb.write(fout);
            fout.close();
            response.getWriter().write("/report/qdtj.xls");

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public String loadQdtjDetails() {
        String json = "";
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                //管理员没有登录;
                return null;
            }
//			String superName="0EA5D6BC23E8EEC78F62546B9F68BABFA96976B775889BA625DB6D764FD0DBD42A1C0F45F85B0DE8";
//			if(!superName.equals(users.getUsername())){
//				if(isExistsQX("单个渠道统计详情", users.getId())){
//					getRequest().setAttribute("err", "您没有操作该功能的权限!");
//					return "err";
//				}
//			}
            PageUtil<Qdtj> pageUtil = new PageUtil<Qdtj>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(pageSize);
            StringBuffer url = new StringBuffer();
            url.append(getRequest().getServletContext().getContextPath());
            url.append("/Product/Admin/activity!loadQdtjDetails.action?");
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                url.append("&insertTime=");
                url.append(insertTime);
            }
            if (!QwyUtil.isNullAndEmpty(registChannel)) {
                url.append("&registChannel=");
                url.append(registChannel);
            }
            if (!QwyUtil.isNullAndEmpty(channelCode)) {
                url.append("&channelCode=");
                url.append(channelCode);
            }
            pageUtil.setPageUrl(url.toString());
            getRequest().setAttribute("channel", registChannel);
            getRequest().setAttribute("channelCode", channelCode);
            getRequest().setAttribute("insertTime", insertTime);
            pageUtil = bean.loadQdtjDetailByChannel(pageUtil, channelCode, registChannel, insertTime);
            if (!QwyUtil.isNullAndEmpty(pageUtil) && !QwyUtil.isNullAndEmpty(pageUtil.getList())) {
                getRequest().setAttribute("tj", bean.tjQdtj(pageUtil.getList()));
                getRequest().setAttribute("pageUtil", pageUtil);
                getRequest().setAttribute("list", pageUtil.getList());
                getRequest().setAttribute("channelName", pageUtil.getList().get(0).getChannelName());
                getRequest().setAttribute("table", "1");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return "loadQdtjDetails";
    }

    /**
     * 导出单个渠道统计详情（安卓）
     */
    public String iportQdtjDetailTable() {
        List<JasperPrint> list = null;
        String name = QwyUtil.fmyyyyMMddHHmmss3.format(new Date()) + "_qdtjDetails";
        try {
            String filePath = request.getServletContext().getRealPath("/WEB-INF/classes/qdtjDetails.jasper");
            log.info("iportTable报表路径: " + filePath);
            list = bean.getQdtjDetailJasperPrintList(registChannel, insertTime, filePath);
            doIreport(list, name);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 根据渠道名称查询渠道费用
     *
     * @return
     */
    public String addAndroidChannelData() {
        String json = "";
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                //管理员没有登录;
                return null;
            }
            String superName = "0EA5D6BC23E8EEC78F62546B9F68BABFA96976B775889BA625DB6D764FD0DBD42A1C0F45F85B0DE8";
            if (!superName.equals(users.getUsername())) {
                if (isExistsQX("渠道费用表", users.getId())) {
                    getRequest().setAttribute("err", "您没有操作该功能的权限!");
                    return "err";
                }
            }
            //List<Qdtj> list = this.list;
            Qdtj qdtj = this.qdtj;
            getRequest().setAttribute("insertTime", insertTime);
            String Date = "";
            String[] time = QwyUtil.splitTime(insertTime);
            if (!QwyUtil.isNullAndEmpty(time)) {
                if (time.length > 1) {
                    Date = QwyUtil.fmyyyyMMddHHmmss.format(QwyUtil.fmMMddyyyy.parse(time[0] + " 00:00:00"));
                } else {
                    Date = QwyUtil.fmyyyyMMddHHmmss.format(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                }
            }
            PageUtil<Qdtj> pageUtil = new PageUtil<Qdtj>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(pageSize);
            StringBuffer url = new StringBuffer();
            url.append(getRequest().getServletContext().getContextPath());
            url.append("/Product/Admin/activity!addAndroidChannelData.action?");
            if(!QwyUtil.isNullAndEmpty(insertTime)){
                url.append("insertTime=");
                url.append(insertTime+"&");
            }
            if(!QwyUtil.isNullAndEmpty(channelName)){
                url.append("channelName=");
                url.append(channelName+"&");
            }

            pageUtil.setPageUrl(url.toString());
            pageUtil = bean.loadQdtj(pageUtil, Date, channelType,channelName);
            if (!QwyUtil.isNullAndEmpty(pageUtil)) {
                getRequest().setAttribute("pageUtil", pageUtil);
                getRequest().setAttribute("channelType", channelType);
                getRequest().setAttribute("list", pageUtil.getList());
                return "addQdtj";
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;

    }

    /**
     * 根据输入数据补全渠道统计报表(qdtjlist)
     *
     * @return
     */
    public String updateQdtjData() {
        String json = "";
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                QwyUtil.printJSON(getResponse(), json);
                //管理员没有登录;
                return null;
            }
            List<Qdtj> qdtjlist = this.qdtjlist;
            Date insertTime = qdtjlist.get(0).getInsertTime();
            for (Qdtj qgdj:qdtjlist) {
                //获取时间,渠道名称查询出已经在数据库的相关数据
                String channelName = qgdj.getChannelName();
                String channelCost = qgdj.getChannelCost();
                //查询出相关数据
                List<Qdtj> newqdtjlist = bean.loadQdtj(insertTime, channelName);
                if(!QwyUtil.isNullAndEmpty(newqdtjlist)){
                    Qdtj newqdtj = newqdtjlist.get(0);
                    newqdtj.setChannelCost(channelCost);
                    bean.updateQdtj(newqdtj);
                }

            }
            return "addQdtj";
        } catch (Exception e) {
          e.printStackTrace();
        }

        return null;
    }


    /**
     * 更新安卓渠道统计
     *
     * @return
     */
    public String updateAndroidChannelData() {
        log.info("--------------------------------执行安卓渠道统计-----------------------------------------------------");
        String json = "";
        try {
            updateQdtjThreadBean.updateQdtjToday();
            json = QwyUtil.getJSONString("ok", "数据更新成功！");
        } catch (Exception e) {
            json = QwyUtil.getJSONString("err", "数据更新失败！");
            log.error(e.getMessage(), e);
        }
        try {
            QwyUtil.printJSON(getResponse(), json);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 导出安卓渠道统计汇总表excel
     *
     * @return
     */
    public String iportQdtjMainTable() {
        try {
            String sDate = "";
            String eDate = "";
            String[] time = QwyUtil.splitTime(insertTime);
            if (!QwyUtil.isNullAndEmpty(time))
                if (time.length > 1) {
                    sDate = QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyy.parse(time[0]));
                    eDate = QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyy.parse(time[1]));
                } else {
                    sDate = QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyy.parse(time[0]));
                    eDate = QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyy.parse(time[0]));
                }
            List list = bean.loadQdtjMain(sDate, eDate, channelType);
            String name = QwyUtil.fmyyyyMMddHHmmss3.format(new Date()) + "_qdtj_main";
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("Android渠道统计汇总表");
            HSSFRow row = sheet.createRow((int) 1);
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
            row = sheet.createRow(0);
            row.createCell(0).setCellValue("序号");
            row.createCell(1).setCellValue("渠道编号");
            row.createCell(2).setCellValue("渠道名称");
            row.createCell(3).setCellValue("点击量");
            row.createCell(4).setCellValue("下载量");
            row.createCell(5).setCellValue("激活量");
            row.createCell(6).setCellValue("渠道费用(元)");
            row.createCell(7).setCellValue("激活成本");
            row.createCell(8).setCellValue("激活注册转化率(%)");
            row.createCell(9).setCellValue("总注册人数");
            row.createCell(10).setCellValue("注册成本");
            row.createCell(11).setCellValue("认证人数");
            row.createCell(12).setCellValue("注册认证转化率(%)");
            row.createCell(13).setCellValue("认证首投转化率(%)");
            row.createCell(14).setCellValue("首投人数");
            row.createCell(15).setCellValue("首投成本(元)");
            row.createCell(16).setCellValue("首投总金额(元)");
            row.createCell(17).setCellValue("人均首投总金额(元)");
            row.createCell(18).setCellValue("首投ROI");
            row.createCell(19).setCellValue("复投人数");
            row.createCell(20).setCellValue("复投成本");
            row.createCell(21).setCellValue("复投金额(元)");
            row.createCell(22).setCellValue("人均复投金额(元)");
            row.createCell(23).setCellValue("复投ROI");
            row.createCell(24).setCellValue("新增复投用户数");
            row.createCell(25).setCellValue("新增复投金额");
            row.createCell(26).setCellValue("新增复投率");
            row.createCell(27).setCellValue("投资人数");
            row.createCell(28).setCellValue("投资金额(元)");
            row.createCell(29).setCellValue("零钱罐投资金额(元)");
            row.createCell(30).setCellValue("人均投资金额(元)");
            row.createCell(31).setCellValue("投资ROI");
            Qdtj tj = bean.tjQdtj(list);
            if (!QwyUtil.isNullAndEmpty(tj)) {
                row = sheet.createRow(1);
                row.createCell(0).setCellValue("合计");//序号
                row.createCell(1).setCellValue("");//渠道编号
                row.createCell(2).setCellValue("");//渠道名称
                row.createCell(3).setCellValue("");//点击量
                row.createCell(4).setCellValue("");//下载量
                row.createCell(5).setCellValue(QwyUtil.jieQuFa(Double.valueOf(tj.getActivityCount()), 0));//激活量
                row.createCell(6).setCellValue(QwyUtil.jieQuFa(Double.valueOf(tj.getChannelCost()), 0));//渠道费用(元)
                row.createCell(7).setCellValue(QwyUtil.jieQuFa(Double.valueOf(tj.getActivityCost()), 0));//激活成本
                row.createCell(8).setCellValue("");//激活注册转化率(%)
                row.createCell(9).setCellValue(QwyUtil.jieQuFa(Double.valueOf(tj.getRegCount()), 0));//总注册人数
                row.createCell(10).setCellValue(QwyUtil.jieQuFa(Double.valueOf(tj.getRegisterCost()), 0));//注册成本
                row.createCell(11).setCellValue(QwyUtil.jieQuFa(Double.valueOf(tj.getBindCount()), 0));//认证人数
                row.createCell(12).setCellValue("");//注册认证转化率(%)
                row.createCell(13).setCellValue("");//认证首投转化率(%)
                row.createCell(14).setCellValue(QwyUtil.jieQuFa(Double.valueOf(tj.getStrs()), 0));//首投人数
                row.createCell(15).setCellValue(QwyUtil.jieQuFa(Double.valueOf(tj.getFristBuyCost()), 0));//首投成本(元)
                row.createCell(16).setCellValue(QwyUtil.jieQuFa(Double.valueOf(tj.getStje()), 2));//首投总金额(元)
                row.createCell(17).setCellValue(QwyUtil.jieQuFa(Double.valueOf(tj.getRjstje()), 2));//人均首投总金额(元)
                row.createCell(18).setCellValue(QwyUtil.jieQuFa(Double.valueOf(tj.getFristBuyROI()), 0));//首投ROI
                row.createCell(19).setCellValue(QwyUtil.jieQuFa(Double.valueOf(tj.getFtrs()), 0));//复投人数
                row.createCell(20).setCellValue("");//复投成本
                row.createCell(21).setCellValue(QwyUtil.jieQuFa(Double.valueOf(tj.getFtje()), 2));//复投金额(元)
                row.createCell(22).setCellValue(QwyUtil.jieQuFa(Double.valueOf(tj.getRjftje()), 2));//人均复投金额(元)
                row.createCell(23).setCellValue("");//复投ROI
                row.createCell(24).setCellValue(QwyUtil.jieQuFa(Double.valueOf(tj.getXzftyh()), 0));//新增复投用户数
                row.createCell(25).setCellValue(QwyUtil.jieQuFa(Double.valueOf(tj.getXhftyhtzze()), 2));//新增复投金额金额(元)
                row.createCell(26).setCellValue(QwyUtil.jieQuFa(Double.valueOf(tj.getXzftl()), 2));//新增复投率(元)
                row.createCell(27).setCellValue(QwyUtil.jieQuFa(Double.valueOf(tj.getTzrs()), 0));//投资人数
                row.createCell(28).setCellValue(QwyUtil.jieQuFa(Double.valueOf(tj.getTzje()), 2));//投资金额(元)
                row.createCell(29).setCellValue(QwyUtil.jieQuFa(Double.valueOf(tj.getLqgje()), 2));//零钱罐投资金额(元)
                row.createCell(30).setCellValue(QwyUtil.jieQuFa(Double.valueOf(tj.getRjtzje()), 2));//人均投资金额(元)
                row.createCell(31).setCellValue(QwyUtil.jieQuFa(Double.valueOf(tj.getBuyROI()), 0));//投资ROI

            }
            if (!QwyUtil.isNullAndEmpty(tj)) {
                for (int i = 0; i < list.size(); i++) {
                    row = sheet.createRow((int) i + 2);
                    Qdtj cellData = (Qdtj) list.get(i);
                    row.createCell(0).setCellValue(i + 1);//序号
                    row.createCell(1).setCellValue(cellData.getChannel());//渠道编号
                    row.createCell(2).setCellValue(cellData.getChannelName());//渠道名称
                    row.createCell(3).setCellValue("");//点击量
                    row.createCell(4).setCellValue("");//下载量
                    row.createCell(5).setCellValue(QwyUtil.jieQuFa(Double.valueOf(cellData.getActivityCount()), 0));//激活量
                    row.createCell(6).setCellValue(QwyUtil.jieQuFa(Double.valueOf(cellData.getChannelCost()), 0));//渠道费用(元)
                    row.createCell(7).setCellValue(QwyUtil.jieQuFa(Double.valueOf(cellData.getActivityCost()), 0));//激活成本
                    row.createCell(8).setCellValue(QwyUtil.jieQuFa(Double.valueOf(cellData.getZcjhzhl()), 2));//激活注册转化率(%)
                    row.createCell(9).setCellValue(QwyUtil.jieQuFa(Double.valueOf(cellData.getRegCount()), 0));//总注册人数
                    row.createCell(10).setCellValue(QwyUtil.jieQuFa(Double.valueOf(cellData.getRegisterCost()), 0));//注册成本
                    row.createCell(11).setCellValue(QwyUtil.jieQuFa(Double.valueOf(cellData.getBindCount()), 0));//认证人数
                    row.createCell(12).setCellValue(QwyUtil.jieQuFa(Double.valueOf(cellData.getQdzhl()), 2));//注册认证转化率(%)
                    row.createCell(13).setCellValue(QwyUtil.jieQuFa(Double.valueOf(cellData.getRzstzhl()), 2));//认证首投转化率(%)
                    row.createCell(14).setCellValue(QwyUtil.jieQuFa(Double.valueOf(cellData.getStrs()), 0));//首投人数
                    row.createCell(15).setCellValue(QwyUtil.jieQuFa(Double.valueOf(cellData.getFristBuyCost()), 0));//首投成本(元)
                    row.createCell(16).setCellValue(QwyUtil.jieQuFa(Double.valueOf(cellData.getStje()), 2));//首投总金额(元)
                    row.createCell(17).setCellValue(QwyUtil.jieQuFa(Double.valueOf(cellData.getRjstje()), 2));//人均首投总金额(元)
                    row.createCell(18).setCellValue(QwyUtil.jieQuFa(Double.valueOf(cellData.getFristBuyROI()), 0));//首投ROI
                    row.createCell(19).setCellValue(QwyUtil.jieQuFa(Double.valueOf(cellData.getFtrs()), 0));//复投人数
                    row.createCell(20).setCellValue("");///复投成本
                    row.createCell(21).setCellValue(QwyUtil.jieQuFa(Double.valueOf(cellData.getFtje()), 2));//复投金额(元)
                    row.createCell(22).setCellValue(QwyUtil.jieQuFa(Double.valueOf(cellData.getRjftje()), 2));//人均复投金额(元)
                    row.createCell(23).setCellValue("");//复投ROI
                    row.createCell(24).setCellValue(QwyUtil.jieQuFa(Double.valueOf(cellData.getXzftyh()), 0));//新增复投用户数
                    row.createCell(25).setCellValue(QwyUtil.jieQuFa(Double.valueOf(cellData.getXhftyhtzze()), 2));//新增复投金额金额(元)
                    row.createCell(26).setCellValue(QwyUtil.jieQuFa(Double.valueOf(cellData.getXzftl()), 2));//新增复投率(元)
                    row.createCell(27).setCellValue(QwyUtil.jieQuFa(Double.valueOf(cellData.getTzrs()), 0));//投资人数
                    row.createCell(28).setCellValue(QwyUtil.jieQuFa(Double.valueOf(cellData.getTzje()), 2));//投资金额(元)
                    row.createCell(29).setCellValue(QwyUtil.jieQuFa(Double.valueOf(cellData.getLqgje()), 2));//零钱罐投资金额(元)
                    row.createCell(30).setCellValue(QwyUtil.jieQuFa(Double.valueOf(cellData.getRjtzje()), 2));//人均投资金额(元)
                    row.createCell(31).setCellValue(QwyUtil.jieQuFa(Double.valueOf(cellData.getBuyROI()), 0));//投资ROI
                }
            }
            String pathname = QwyUtil.fmyyyyMMddHHmmss3.format(new Date()) + "qdtjMainTable.xls";
            String realPath = request.getServletContext().getRealPath("/report/" + pathname);
            FileOutputStream fout = new FileOutputStream(realPath);
            wb.write(fout);
            fout.close();
            response.getWriter().write("/report/" + pathname);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }


    /**
     * 导出单个安卓渠道统计表excel
     *
     * @return
     */
    public String iportQdtjDetailsTable() {
        try {
            PageUtil<BackStatsOperateDay> pageUtil = new PageUtil<BackStatsOperateDay>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(999999);
            List list = bean.loadQdtjDetailByChannel(pageUtil, channelCode, registChannel, insertTime).getList();
            String name = QwyUtil.fmyyyyMMddHHmmss3.format(new Date()) + "_qdtj_main";
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("Android渠道统计详情表");
            HSSFRow row = sheet.createRow((int) 1);
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
            row = sheet.createRow(0);
            row.createCell(0).setCellValue("序号");
            row.createCell(1).setCellValue("日期");
            row.createCell(2).setCellValue("渠道编号");
            row.createCell(3).setCellValue("渠道名称");
            row.createCell(4).setCellValue("点击量");
            row.createCell(5).setCellValue("下载量");
            row.createCell(6).setCellValue("激活量");
            row.createCell(7).setCellValue("渠道费用(元)");
            row.createCell(8).setCellValue("激活成本");
            row.createCell(9).setCellValue("激活注册转化率(%)");
            row.createCell(10).setCellValue("总注册人数");
            row.createCell(11).setCellValue("注册成本");
            row.createCell(12).setCellValue("认证人数");
            row.createCell(13).setCellValue("注册认证转化率(%)");
            row.createCell(14).setCellValue("认证首投转化率(%)");
            row.createCell(15).setCellValue("首投人数");
            row.createCell(16).setCellValue("首投成本(元)");
            row.createCell(17).setCellValue("首投总金额(元)");
            row.createCell(18).setCellValue("人均首投总金额(元)");
            row.createCell(19).setCellValue("首投ROI");
            row.createCell(20).setCellValue("复投人数");
            row.createCell(21).setCellValue("复投成本");
            row.createCell(22).setCellValue("复投金额(元)");
            row.createCell(23).setCellValue("人均复投金额(元)");
            row.createCell(24).setCellValue("复投ROI");
            row.createCell(25).setCellValue("新增复投用户数");
            row.createCell(26).setCellValue("新增复投金额");
            row.createCell(27).setCellValue("新增复投率");
            row.createCell(28).setCellValue("投资人数");
            row.createCell(29).setCellValue("投资金额(元)");
            row.createCell(30).setCellValue("零钱罐投资金额(元)");
            row.createCell(31).setCellValue("人均投资金额(元)");
            row.createCell(32).setCellValue("投资ROI");
            Qdtj tj = bean.tjQdtj(list);
            if (!QwyUtil.isNullAndEmpty(tj)) {
                row = sheet.createRow(1);
                row.createCell(0).setCellValue("合计");//序号
                row.createCell(1).setCellValue("");//日期
                row.createCell(2).setCellValue("");//渠道编号
                row.createCell(3).setCellValue("");//渠道名称
                row.createCell(4).setCellValue("");//点击量
                row.createCell(5).setCellValue("");//下载量
                row.createCell(6).setCellValue(QwyUtil.jieQuFa(Double.valueOf(tj.getActivityCount()), 0));//激活量
                row.createCell(7).setCellValue("");//渠道费用(元)
                row.createCell(8).setCellValue("");//激活成本
                row.createCell(9).setCellValue("");//激活注册转化率(%)
                row.createCell(10).setCellValue(QwyUtil.jieQuFa(Double.valueOf(tj.getRegCount()), 0));//总注册人数
                row.createCell(11).setCellValue("");//注册成本
                row.createCell(12).setCellValue(QwyUtil.jieQuFa(Double.valueOf(tj.getBindCount()), 0));//认证人数
                row.createCell(13).setCellValue("");//注册认证转化率(%)
                row.createCell(14).setCellValue("");//认证首投转化率(%)
                row.createCell(15).setCellValue(QwyUtil.jieQuFa(Double.valueOf(tj.getStrs()), 0));//首投人数
                row.createCell(16).setCellValue("");//首投成本(元)
                row.createCell(17).setCellValue(QwyUtil.jieQuFa(Double.valueOf(tj.getStje()), 2));//首投总金额(元)
                row.createCell(18).setCellValue(QwyUtil.jieQuFa(Double.valueOf(tj.getRjstje()), 2));//人均首投总金额(元)
                row.createCell(19).setCellValue("");//首投ROI
                row.createCell(20).setCellValue(QwyUtil.jieQuFa(Double.valueOf(tj.getFtrs()), 0));//复投人数
                row.createCell(21).setCellValue("");//复投成本
                row.createCell(22).setCellValue(QwyUtil.jieQuFa(Double.valueOf(tj.getFtje()), 2));//复投金额(元)
                row.createCell(23).setCellValue(QwyUtil.jieQuFa(Double.valueOf(tj.getRjftje()), 2));//人均复投金额(元)
                row.createCell(24).setCellValue("");//复投ROI
                row.createCell(25).setCellValue(QwyUtil.jieQuFa(Double.valueOf(tj.getXzftyh()), 0));//新增复投用户数
                row.createCell(26).setCellValue(QwyUtil.jieQuFa(Double.valueOf(tj.getXhftyhtzze()), 2));//新增复投金额金额(元)
                row.createCell(27).setCellValue(QwyUtil.jieQuFa(Double.valueOf(tj.getXzftl()), 2));//新增复投率(元)
                row.createCell(28).setCellValue(QwyUtil.jieQuFa(Double.valueOf(tj.getTzrs()), 0));//投资人数
                row.createCell(29).setCellValue(QwyUtil.jieQuFa(Double.valueOf(tj.getTzje()), 2));//投资金额(元)
                row.createCell(30).setCellValue(QwyUtil.jieQuFa(Double.valueOf(tj.getLqgje()), 2));//零钱罐投资金额(元)
                row.createCell(31).setCellValue(QwyUtil.jieQuFa(Double.valueOf(tj.getRjtzje()), 2));//人均投资金额(元)
                row.createCell(32).setCellValue("");//投资ROI

            }
            for (int i = 0; i < list.size(); i++) {
                Qdtj cellData = (Qdtj) list.get(i);
                row = sheet.createRow((int) i + 2);
                row.createCell(0).setCellValue(i + 1);//序号
                row.createCell(1).setCellValue(cellData.getDateStr());//日期
                row.createCell(2).setCellValue(cellData.getChannel());//渠道编号
                row.createCell(3).setCellValue(cellData.getChannelName());//渠道名称
                row.createCell(4).setCellValue("");//点击量
                row.createCell(5).setCellValue("");//下载量
                row.createCell(6).setCellValue(QwyUtil.jieQuFa(Double.valueOf(cellData.getActivityCount()), 0));//激活量
                row.createCell(7).setCellValue("");//渠道费用(元)
                row.createCell(8).setCellValue("");//激活成本
                row.createCell(9).setCellValue(QwyUtil.jieQuFa(Double.valueOf(cellData.getQdzhl()), 2));//激活注册转化率(%)
                row.createCell(10).setCellValue(QwyUtil.jieQuFa(Double.valueOf(cellData.getRegCount()), 0));//总注册人数
                row.createCell(11).setCellValue("");//注册成本
                row.createCell(12).setCellValue(QwyUtil.jieQuFa(Double.valueOf(cellData.getBindCount()), 0));//认证人数
                row.createCell(13).setCellValue(QwyUtil.jieQuFa(Double.valueOf(cellData.getZcjhzhl()), 2));//注册认证转化率(%)
                row.createCell(14).setCellValue(QwyUtil.jieQuFa(Double.valueOf(cellData.getRzstzhl()), 2));//认证首投转化率(%)
                row.createCell(15).setCellValue(QwyUtil.jieQuFa(Double.valueOf(cellData.getStrs()), 0));//首投人数
                row.createCell(16).setCellValue("");//首投成本(元)
                row.createCell(17).setCellValue(QwyUtil.jieQuFa(Double.valueOf(cellData.getStje()), 0));//首投总金额(元)
                row.createCell(18).setCellValue(QwyUtil.jieQuFa(Double.valueOf(cellData.getRjstje()), 2));//人均首投总金额(元)
                row.createCell(19).setCellValue("");//首投ROI
                row.createCell(20).setCellValue(QwyUtil.jieQuFa(Double.valueOf(cellData.getFtrs()), 0));//复投人数
                row.createCell(21).setCellValue("");///复投成本
                row.createCell(22).setCellValue(QwyUtil.jieQuFa(Double.valueOf(cellData.getFtje()), 2));//复投金额(元)
                row.createCell(23).setCellValue(QwyUtil.jieQuFa(Double.valueOf(cellData.getRjftje()), 2));//人均复投金额(元)
                row.createCell(24).setCellValue("");//复投ROI
                row.createCell(25).setCellValue(QwyUtil.jieQuFa(Double.valueOf(cellData.getXzftyh()), 0));//新增复投用户数
                row.createCell(26).setCellValue(QwyUtil.jieQuFa(Double.valueOf(cellData.getXhftyhtzze()), 2));//新增复投金额金额(元)
                row.createCell(27).setCellValue(QwyUtil.jieQuFa(Double.valueOf(cellData.getXzftl()), 2));//新增复投率(元)
                row.createCell(28).setCellValue(QwyUtil.jieQuFa(Double.valueOf(cellData.getTzrs()), 0));//投资人数
                row.createCell(29).setCellValue(QwyUtil.jieQuFa(Double.valueOf(cellData.getTzje()), 2));//投资金额(元)
                row.createCell(30).setCellValue(QwyUtil.jieQuFa(Double.valueOf(cellData.getLqgje()), 2));//零钱罐投资金额(元)
                row.createCell(31).setCellValue(QwyUtil.jieQuFa(Double.valueOf(cellData.getRjtzje()), 2));//人均投资金额(元)
                row.createCell(32).setCellValue("");//投资ROI
            }
            String realPath = request.getServletContext().getRealPath("/report/" + name + ".xls");
            FileOutputStream fout = new FileOutputStream(realPath);
            wb.write(fout);
            fout.close();
            response.getWriter().write("/report/" + name + ".xls");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }


    public String getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(String insertTime) {
        this.insertTime = insertTime;
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

    public String getRegistChannel() {
        return registChannel;
    }

    public void setRegistChannel(String registChannel) {
        this.registChannel = registChannel;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public List<Qdtj> getQdtjlist() {
        return qdtjlist;
    }

    public void setQdtjlist(List<Qdtj> qdtjlist) {
        this.qdtjlist = qdtjlist;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
}
