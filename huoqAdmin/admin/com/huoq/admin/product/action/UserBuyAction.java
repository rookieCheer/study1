package com.huoq.admin.product.action;

import com.huoq.common.action.BaseAction;
import com.huoq.common.bean.*;
import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.ListUtils;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.*;
import com.huoq.util.ExcelUtil;

import org.apache.poi.hssf.usermodel.*;
import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;
import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@SuppressWarnings("serial")
@ParentPackage("struts-default")
@Namespace("/Product/buyInfo")
@Results({ @Result(name = "productInfo", value = "/Product/Admin/BuyInfo/buyInfo.jsp"), @Result(name = "tiedCardInfo", value = "/Product/Admin/BuyInfo/tiedCardInfo.jsp"),
           @Result(name = "summaryTable", value = "/Product/Admin/BuyInfo/summaryTable.jsp"), @Result(name = "outCash", value = "/Product/Admin/BuyInfo/OutCash.jsp"),
           @Result(name = "SumOperation", value = "/Product/Admin/BuyInfo/SumOperation.jsp"),
           @Result(name = "findToutiaoStatisticsTable", value = "/Product/Admin/BuyInfo/toutiaoStatisticsTable.jsp"),
           @Result(name = "findtoutiaoStatisticsInfoTable", value = "/Product/Admin/BuyInfo/toutiaoStatisticsTable.jsp"),
           @Result(name = "noLogin", value = "/Product/loginBackground.jsp") })
public class UserBuyAction extends BaseAction {

    @Resource
    private BuyProductInfoBean         BuyProductInfoBean;
    @Resource
    private TiedCardBean               tiedCardBean;
    @Resource
    private SummaryTableBean           stBean;
    @Resource
    private OutCashBean                outCashBean;
    @Resource
    private SumOperationBean           sOtBean;
    @Resource
    private ToutiaoStatisticsTableBean toutiaoBean;
    private Integer                    currentPage = 1;
    private Integer                    pageSize    = 50;
    private String                     insertTime;
    private String                     phone;
    private String                     isnew;
    private Integer                    os;

    /**
     * 购买情况统计
     * 
     * @return
     */
    public String productInfo() {
        String json = "";
        HttpServletRequest request = getRequest();
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                request.setAttribute("json", json);
                // 管理员没有登录;
                return "noLogin";
            }
            PageUtil<BuyProductInfo> pageUtil = new PageUtil<BuyProductInfo>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(pageSize);
            StringBuffer url = new StringBuffer();
            url.append(getRequest().getServletContext().getContextPath());
            url.append("/Product/buyInfo/userBuy!productInfo.action?");
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                url.append("&insertTime=");
                url.append(insertTime);
            }
            if (!QwyUtil.isNullAndEmpty(phone)) {
                url.append("&phone=");
                url.append(phone);
            }
            if (!QwyUtil.isNullAndEmpty(isnew)) {
                url.append("&isnew=");
                url.append(isnew);
            }
            pageUtil.setPageUrl(url.toString());
            // 查询统计
            PageUtil<BuyProductInfo> page = BuyProductInfoBean.productInfo(pageUtil, insertTime, phone, isnew);
            if (!QwyUtil.isNullAndEmpty(page)) {
                request.setAttribute("pageUtil", page);
                request.setAttribute("list", page.getList());
                if (!QwyUtil.isNullAndEmpty(isnew) && isnew.equals("1")) {
                    request.setAttribute("isnew", isnew);
                }
                return "productInfo";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 绑卡统计
     * 
     * @return
     */
    public String tiedCardInfo() {
        String json = "";
        HttpServletRequest request = getRequest();
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                request.setAttribute("json", json);
                // 管理员没有登录;
                return "noLogin";
            }
            PageUtil<TiedCard> pageUtil = new PageUtil<TiedCard>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(pageSize);
            StringBuffer url = new StringBuffer();
            url.append(getRequest().getServletContext().getContextPath());
            url.append("/Product/buyInfo/userBuy!tiedCardInfo.action?");
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                url.append("&insertTime=");
                url.append(insertTime);
            }
            if (!QwyUtil.isNullAndEmpty(phone)) {
                url.append("&phone=");
                url.append(phone);
            }
            pageUtil.setPageUrl(url.toString());
            pageUtil = tiedCardBean.findTiedCard(pageUtil, insertTime, phone, isnew);

            if (!QwyUtil.isNullAndEmpty(pageUtil)) {
                List<TiedCard> list = pageUtil.getList();
                list = deleteNull(list);
                pageUtil.setList(list);
                request.setAttribute("pageUtil", pageUtil);
                request.setAttribute("list", pageUtil.getList());
                return "tiedCardInfo";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String replaceNullStringToNull(String value) {
        if ("null".equals(value)) {
            value = null;
        }
        return value;

    }

    private List<TiedCard> deleteNull(List<TiedCard> list) {
        if (list != null) {
            int size = list.size();
            if (size > 0) {
                size = list.size();
                for (int i = 0; i < size; i++) {
                    TiedCard tiedCard = list.get(i);
                    tiedCard.setNo(i+1);
                    String age = tiedCard.getAge();
                    age = replaceNullStringToNull(age);
                    String bankAccount = tiedCard.getBankAccount();
                    bankAccount = replaceNullStringToNull(bankAccount);
                    String bankName = tiedCard.getBankName();
                    bankName = replaceNullStringToNull(bankName);

                    String zinsertTime = tiedCard.getZinsertTime();
                    zinsertTime = replaceNullStringToNull(zinsertTime);
                    String insertTime = tiedCard.getInsertTime();
                    insertTime = replaceNullStringToNull(insertTime);
                    String registPlatform = tiedCard.getRegistPlatform();
                    registPlatform = replaceNullStringToNull(registPlatform);
                    String id = tiedCard.getId();
                    id = replaceNullStringToNull(id);
                    String realName = tiedCard.getRealName();
                    realName = replaceNullStringToNull(realName);
                    String phone = tiedCard.getPhone();
                    phone = replaceNullStringToNull(phone);
                    String cardType = tiedCard.getCardType();
                    cardType = replaceNullStringToNull(cardType);
                    String idCard = tiedCard.getIdCard();
                    idCard = replaceNullStringToNull(idCard);
                    String province = tiedCard.getProvince();
                    province = replaceNullStringToNull(province);
                    String city = tiedCard.getCity();
                    city = replaceNullStringToNull(city);
                    String gender = tiedCard.getGender();
                    gender = replaceNullStringToNull(gender);

                    String birthday = tiedCard.getBirthday();
                    birthday = replaceNullStringToNull(birthday);
                    String cardFriend = tiedCard.getCardFriend();
                    cardFriend = replaceNullStringToNull(cardFriend);
                    String channel = tiedCard.getChannel();
                    channel = replaceNullStringToNull(channel);

                    tiedCard.setId(id);
                    tiedCard.setAge(age);
                    tiedCard.setBankAccount(bankAccount);
                    tiedCard.setBankName(bankName);
                    tiedCard.setBirthday(birthday);
                    tiedCard.setCardFriend(cardFriend);
                    tiedCard.setCardType(cardType);
                    tiedCard.setChannel(channel);
                    tiedCard.setCity(city);
                    tiedCard.setGender(gender);
                    tiedCard.setIdCard(idCard);
                    tiedCard.setInsertTime(insertTime);
                    tiedCard.setPhone(phone);
                    tiedCard.setProvince(province);
                    tiedCard.setRealName(realName);
                    tiedCard.setRegistPlatform(registPlatform);
                    tiedCard.setZinsertTime(zinsertTime);
                    list.set(i, tiedCard);
                }
            }
        }
        return list;
    }

    /**
     * 统计总表
     * 
     * @return
     */
    public String summaryTable() {
        String json = "";
        HttpServletRequest request = getRequest();
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                request.setAttribute("json", json);
                // 管理员没有登录;
                return "noLogin";
            }
            SummaryTable findSummaryTable = stBean.findSummaryTable(insertTime);
            if (!QwyUtil.isNullAndEmpty(findSummaryTable)) {
                getRequest().setAttribute("list", findSummaryTable);
            }
            return "summaryTable";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 提现统计表
     * 
     * @return
     */
    public String outCashTable() {
        String json = "";
        HttpServletRequest request = getRequest();
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                request.setAttribute("json", json);
                // 管理员没有登录;
                return "noLogin";
            }
            PageUtil<OutCash> pageUtil = new PageUtil<OutCash>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(pageSize);
            StringBuffer url = new StringBuffer();
            url.append(getRequest().getServletContext().getContextPath());
            url.append("/Product/buyInfo/userBuy!outCashTable.action?");
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                url.append("&insertTime=");
                url.append(insertTime);
            }
            if (!QwyUtil.isNullAndEmpty(phone)) {
                url.append("&phone=");
                url.append(phone);
            }
            pageUtil.setPageUrl(url.toString());
            PageUtil<OutCash> outCashTable = outCashBean.outCashTable(pageUtil, insertTime, phone);
            if (!QwyUtil.isNullAndEmpty(outCashTable)) {
                request.setAttribute("pageUtil", outCashTable);
                request.setAttribute("list", outCashTable.getList());
                return "outCash";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 运营总表统计
     * 
     * @return
     */
    public String sumOperation() {
        String json = "";
        HttpServletRequest request = getRequest();
        try {
            UsersAdmin users = (UsersAdmin) getRequest().getSession().getAttribute("usersAdmin");
            if (QwyUtil.isNullAndEmpty(users)) {
                json = QwyUtil.getJSONString("err", "管理员未登录");
                request.setAttribute("json", json);
                // 管理员没有登录;
                return "noLogin";
            }
            SumOperation findSumOperation = sOtBean.findSumOperation(insertTime);
            if (!QwyUtil.isNullAndEmpty(findSumOperation)) {
                getRequest().setAttribute("list", findSumOperation);
            }
            return "SumOperation";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 导出购买情况统计表
     */
    public void exportExcelBuyProductInfoList() throws Exception {

        PageUtil<BuyProductInfo> pageUtil = new PageUtil<BuyProductInfo>();
        pageUtil.setCurrentPage(currentPage);
        pageUtil.setPageSize(1000000);
        StringBuffer url = new StringBuffer();
        url.append(getRequest().getServletContext().getContextPath());
        url.append("/Product/buyInfo/userBuy!productInfo.action");
        if (!QwyUtil.isNullAndEmpty(insertTime)) {
            url.append("&insertTime=");
            url.append(insertTime);
        }
        if (!QwyUtil.isNullAndEmpty(phone)) {
            url.append("&phone=");
            url.append(phone);
        }
        if (!QwyUtil.isNullAndEmpty(isnew)) {
            url.append("&isnew=");
            url.append(isnew);
        }
        pageUtil.setPageUrl(url.toString());
        // 查询统计
        pageUtil = BuyProductInfoBean.productInfo(pageUtil, insertTime, phone, isnew);
        if (pageUtil != null) {
            List<BuyProductInfo> list = pageUtil.getList();
            if (list != null && list.size() > 0) {
                deal(list);
                String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xls";
                response.setContentType(ExcelUtil.EXCEL_STYLE2007);
                response.setHeader("Content-disposition", "attachment;filename=" + fileName);
                ServletOutputStream outputStream = response.getOutputStream(); // 取得输出流
                LinkedHashMap<String, String> fieldMap = new LinkedHashMap<String, String>();
                fieldMap.put("购买产品", "productName");
                fieldMap.put("兑付日期倒计时", "endTime");
                fieldMap.put("购买金额(元)", "inMoney"); // inMoney/100
                fieldMap.put("客户姓名", "realName");
                fieldMap.put("好友", "friend");
                fieldMap.put("所属省份", "province");
                fieldMap.put("所属城市", "city");
                fieldMap.put("手机", "phone"); // mylist.phone 需要解密
                fieldMap.put("购买日期", "insterTime");// String
                fieldMap.put("兑付日期", "finishTime");
                fieldMap.put("性别", "gender");

                ExcelUtil.exportExcelNew(outputStream, "购买情况统计表", fieldMap, list, null);

            }
        }
    }

    private void deal(List<BuyProductInfo> list) {
        if (list != null) {
            int size = list.size();
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    BuyProductInfo info = list.get(i);
                    Double inMoney = info.getInMoney();
                    if (inMoney != null) {
                        inMoney = inMoney / 100;
                        info.setInMoney(inMoney);
                    }
                    String phone = info.getPhone();
                    if (phone != null) {
                        phone = DESEncrypt.jieMiUsername(phone);
                        info.setPhone(phone);
                    }
                    list.set(i, info);
                }
            }
        }
    }

    /**
     * 导出绑卡统计表
     */
    public void exportExcelTiedCardInfoList() throws Exception{
       
        PageUtil<TiedCard> pageUtil = new PageUtil<TiedCard>();
        pageUtil.setCurrentPage(currentPage);
        pageUtil.setPageSize(1000000);
        StringBuffer url = new StringBuffer();
        url.append(getRequest().getServletContext().getContextPath());
        url.append("/Product/buyInfo/userBuy!tiedCardInfo.action?");
        if (!QwyUtil.isNullAndEmpty(insertTime)) {
            url.append("&insertTime=");
            url.append(insertTime);
        }
        if (!QwyUtil.isNullAndEmpty(phone)) {
            url.append("&phone=");
            url.append(phone);
        }
        pageUtil.setPageUrl(url.toString());
        pageUtil = tiedCardBean.findTiedCard(pageUtil, insertTime, phone, isnew);
        if (pageUtil != null) {
            List<TiedCard> list = pageUtil.getList();
            if (list != null && list.size() > 0) {
                deleteNull(list);
                String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xls";
                response.setContentType(ExcelUtil.EXCEL_STYLE2007);
                response.setHeader("Content-disposition", "attachment;filename=" + fileName);
                ServletOutputStream outputStream = response.getOutputStream(); // 取得输出流
                LinkedHashMap<String, String> fieldMap = new LinkedHashMap<String, String>();
                fieldMap.put("编号", "no");
                fieldMap.put("注册日期", "zinsertTime");
                fieldMap.put("绑定日期", "insertTime"); //
                fieldMap.put("开户银行", "bankName");
                fieldMap.put("银行卡号", "bankAccount");
                fieldMap.put("注册平台", "registPlatform");
                fieldMap.put("用户id", "id");
                fieldMap.put("姓名", "realName"); 
                fieldMap.put("手机号", "phone");
                fieldMap.put("电话类型", "cardType");
                fieldMap.put("身份证号码", "idCard");
                fieldMap.put("所属省份", "province"); 
                fieldMap.put("所属城市", "city");
                fieldMap.put("性别", "gender");
                fieldMap.put("年龄", "age");
                fieldMap.put("生日", "birthday"); 
               
               ExcelUtil.exportExcelNew(outputStream, "绑卡情况统计表", fieldMap, list, null);

            }
        }
    }

    /**
     * 导出总表
     */
    public String exportSummarizeTableInfo() {
        FileOutputStream fout = null;
        try {
            PageUtil<SummaryTable> pageUtil = new PageUtil<SummaryTable>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(999999);
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("总表");
            HSSFRow row = sheet.createRow((int) 0);
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
            HSSFCell cell = row.createCell(0);
            cell.setCellValue("日期");
            cell.setCellStyle(style);
            cell = row.createCell(1);
            cell.setCellValue("新增注册用户数A");
            cell.setCellStyle(style);
            cell = row.createCell(2);
            cell.setCellValue("ios用户A");
            cell.setCellStyle(style);
            cell = row.createCell(3);
            cell.setCellValue("android用户A");
            cell.setCellStyle(style);
            cell = row.createCell(4);
            cell.setCellValue("微信用户A");
            cell.setCellStyle(style);
            cell = row.createCell(5);
            cell.setCellValue("新增认证用户B");
            cell.setCellStyle(style);
            cell = row.createCell(6);
            cell.setCellValue("ios用户B");
            cell.setCellStyle(style);
            cell = row.createCell(7);
            cell.setCellValue("android用户B");
            cell.setCellStyle(style);
            cell = row.createCell(8);
            cell.setCellValue("微信用户B");
            cell.setCellStyle(style);
            cell = row.createCell(9);
            cell.setCellValue("累计注册用户数");
            cell.setCellStyle(style);
            cell = row.createCell(10);
            cell.setCellValue("累计认证用户");
            cell.setCellStyle(style);
            cell = row.createCell(11);
            cell.setCellValue("当日购买交易笔数C");
            cell.setCellStyle(style);
            cell = row.createCell(12);
            cell.setCellValue("新用户部分C");
            cell.setCellStyle(style);
            cell = row.createCell(13);
            cell.setCellValue("老用户部分C");
            cell.setCellStyle(style);
            cell = row.createCell(14);
            cell.setCellValue("当日资金流入");
            cell.setCellStyle(style);
            cell = row.createCell(15);
            cell.setCellValue("首投总额");
            cell.setCellStyle(style);
            cell = row.createCell(16);
            cell.setCellValue("复投总额D");
            cell.setCellStyle(style);
            cell = row.createCell(17);
            cell.setCellValue("活期产品部分D");
            cell.setCellStyle(style);
            cell = row.createCell(18);
            cell.setCellValue("定期产品部分D");
            cell.setCellStyle(style);
            cell = row.createCell(19);
            cell.setCellValue("累计资金流入");
            cell.setCellStyle(style);
            cell = row.createCell(20);
            cell.setCellValue("当日提现金额E");
            cell.setCellStyle(style);
            cell = row.createCell(21);
            cell.setCellValue("当日可提现金额E");
            cell.setCellStyle(style);
            cell = row.createCell(22);
            cell.setCellValue("资金存量E");
            cell.setCellStyle(style);
            cell = row.createCell(23);
            SummaryTable summaryTable = null;
            SummaryTable findSummaryTable = stBean.findSummaryTable(insertTime);
            row = sheet.createRow((int) 1);
            row.createCell(0).setCellValue(!QwyUtil.isNullAndEmpty(findSummaryTable.getTadayDate()) ? findSummaryTable.getTadayDate() : "");
            row.createCell(1).setCellValue(!QwyUtil.isNullAndEmpty(findSummaryTable.getnEnrollUser()) ? findSummaryTable.getnEnrollUser() : "");
            row.createCell(2).setCellValue(!QwyUtil.isNullAndEmpty(findSummaryTable.getnEnrollIosUser()) ? findSummaryTable.getnEnrollIosUser() : "");
            row.createCell(3).setCellValue(!QwyUtil.isNullAndEmpty(findSummaryTable.getnEnrollAndroidUser()) ? findSummaryTable.getnEnrollAndroidUser() : "");
            row.createCell(4).setCellValue(!QwyUtil.isNullAndEmpty(findSummaryTable.getnEnrollWeChatUser()) ? findSummaryTable.getnEnrollWeChatUser() : "");
            row.createCell(5).setCellValue(!QwyUtil.isNullAndEmpty(findSummaryTable.getnAutUser()) ? findSummaryTable.getnAutUser() : "");
            row.createCell(6).setCellValue(!QwyUtil.isNullAndEmpty(findSummaryTable.getnAutIosUser()) ? findSummaryTable.getnAutIosUser() : "");
            row.createCell(7).setCellValue(!QwyUtil.isNullAndEmpty(findSummaryTable.getnAutAndroidUser()) ? findSummaryTable.getnAutAndroidUser() : "");
            row.createCell(8).setCellValue(!QwyUtil.isNullAndEmpty(findSummaryTable.getnAutWeChatUser()) ? findSummaryTable.getnAutWeChatUser() : "");
            row.createCell(9).setCellValue(!QwyUtil.isNullAndEmpty(findSummaryTable.getAllEnUser()) ? findSummaryTable.getAllEnUser() : "");
            row.createCell(10).setCellValue(!QwyUtil.isNullAndEmpty(findSummaryTable.getAllAutUser()) ? findSummaryTable.getAllAutUser() : "");
            row.createCell(11).setCellValue(!QwyUtil.isNullAndEmpty(findSummaryTable.getTodayDeal()) ? findSummaryTable.getTodayDeal() : "");
            row.createCell(12).setCellValue(!QwyUtil.isNullAndEmpty(findSummaryTable.getnUnserDeal()) ? findSummaryTable.getnUnserDeal() : "");
            row.createCell(13).setCellValue(!QwyUtil.isNullAndEmpty(findSummaryTable.getoUserDeal()) ? findSummaryTable.getoUserDeal() : "");
            row.createCell(14).setCellValue(!QwyUtil.isNullAndEmpty(findSummaryTable.getTodayincapital()) ? findSummaryTable.getTodayincapital() : 0.0);
            row.createCell(15).setCellValue(!QwyUtil.isNullAndEmpty(findSummaryTable.getnDealMoney()) ? findSummaryTable.getnDealMoney() : 0.0);
            row.createCell(16).setCellValue(!QwyUtil.isNullAndEmpty(findSummaryTable.getoDealMoney()) ? findSummaryTable.getoDealMoney() : 0.0);
            row.createCell(17).setCellValue(!QwyUtil.isNullAndEmpty(findSummaryTable.getCurrentProduct()) ? findSummaryTable.getCurrentProduct() : 0.0);
            row.createCell(18).setCellValue(!QwyUtil.isNullAndEmpty(findSummaryTable.getRegularProduct()) ? findSummaryTable.getRegularProduct() : 0.0);
            row.createCell(19).setCellValue(!QwyUtil.isNullAndEmpty(findSummaryTable.getAllinMoney()) ? findSummaryTable.getAllinMoney() : "");
            row.createCell(20).setCellValue(!QwyUtil.isNullAndEmpty(findSummaryTable.getTodayoutMoney()) ? findSummaryTable.getTodayoutMoney() : 0.0);
            row.createCell(21).setCellValue(!QwyUtil.isNullAndEmpty(findSummaryTable.getTodayCash()) ? findSummaryTable.getTodayCash() : 0.0);
            row.createCell(22).setCellValue(!QwyUtil.isNullAndEmpty(findSummaryTable.getCapitalStock()) ? findSummaryTable.getCapitalStock() : 0.0);
            String pathname = QwyUtil.fmyyyyMMddHHmmss3.format(new Date()) + "exportSummarizeTable_nfo.xls";
            String realPath = request.getServletContext().getRealPath("/report/" + pathname);
            log.info("总表地址：" + realPath);
            fout = new FileOutputStream(realPath);
            wb.write(fout);
            response.getWriter().write("/report/" + pathname);
        } catch (Exception e) {
            log.error("操作异常: ", e);
        } finally {
            try {
                fout.close();
            } catch (Exception e) {

            }
        }
        return null;
    }

    /**
     * 导出提现统计表
     */
    public String exportOutCashTable() {
        FileOutputStream fout = null;
        try {
            PageUtil<TiedCard> pageUtil = new PageUtil<TiedCard>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(999999);
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("提现统计表");
            HSSFRow row = sheet.createRow((int) 0);
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
            HSSFCell cell = row.createCell(0);
            cell.setCellValue("序号");
            cell.setCellStyle(style);
            cell = row.createCell(1);
            cell.setCellValue("提现日期");
            cell.setCellStyle(style);
            cell = row.createCell(2);
            cell.setCellValue("提现金额(元)");
            cell.setCellStyle(style);
            cell = row.createCell(3);
            cell.setCellValue("客户姓名");
            cell.setCellStyle(style);
            cell = row.createCell(4);
            cell.setCellValue("性别");
            cell.setCellStyle(style);
            cell = row.createCell(5);
            cell.setCellValue("手机");
            cell.setCellStyle(style);
            cell = row.createCell(6);
            cell.setCellValue("类别");
            cell.setCellStyle(style);
            cell = row.createCell(7);
            cell.setCellValue("所属省份");
            cell.setCellStyle(style);
            cell = row.createCell(8);
            cell.setCellValue("所属城市");
            cell.setCellStyle(style);
            cell = row.createCell(9);

            OutCash outCash = null;
            List list = outCashBean.outCashTable(pageUtil, insertTime, phone).getList();
            for (int i = 0; i < list.size(); i++) {
                row = sheet.createRow((int) i + 1);
                outCash = (OutCash) list.get(i);
                row = sheet.createRow((int) i + 1);
                row.createCell(0).setCellValue((int) i + 1);
                row.createCell(1).setCellValue(!QwyUtil.isNullAndEmpty(outCash.getOutCashTime()) ? outCash.getOutCashTime() : "");
                row.createCell(2).setCellValue(!QwyUtil.isNullAndEmpty(outCash.getOutMoney()) ? outCash.getOutMoney() : "");
                row.createCell(3).setCellValue(!QwyUtil.isNullAndEmpty(outCash.getRealname()) ? outCash.getRealname() : "");
                row.createCell(4).setCellValue(!QwyUtil.isNullAndEmpty(outCash.getGender()) ? outCash.getGender() : "");
                row.createCell(5).setCellValue(!QwyUtil.isNullAndEmpty(outCash.getPhone()) ? outCash.getPhone() : "");
                row.createCell(6).setCellValue(!QwyUtil.isNullAndEmpty(outCash.getCategory()) ? outCash.getCategory() : "");
                row.createCell(7).setCellValue(!QwyUtil.isNullAndEmpty(outCash.getProvince()) ? outCash.getProvince() : "");
                row.createCell(8).setCellValue(!QwyUtil.isNullAndEmpty(outCash.getCity()) ? outCash.getCity() : "");
            }
            String pathname = QwyUtil.fmyyyyMMddHHmmss3.format(new Date()) + "_outCash_info.xls";
            String realPath = request.getServletContext().getRealPath("/report/" + pathname);
            log.info("提现信息统计表地址：" + realPath);
            fout = new FileOutputStream(realPath);
            wb.write(fout);
            response.getWriter().write("/report/" + pathname);
        } catch (Exception e) {
            log.error("操作异常: ", e);
        } finally {
            try {
                fout.close();
            } catch (Exception e) {

            }
        }
        return null;
    }

    /**
     * 导出运营统计表
     */
    public String exportSumOperationInfo() {
        FileOutputStream fout = null;
        try {
            PageUtil<SumOperation> pageUtil = new PageUtil<SumOperation>();
            pageUtil.setCurrentPage(currentPage);
            pageUtil.setPageSize(999999);
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("运营总表");
            HSSFRow row = sheet.createRow((int) 0);
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
            HSSFCell cell = row.createCell(0);
            cell.setCellValue("日期");
            cell.setCellStyle(style);
            cell = row.createCell(1);
            cell.setCellValue("平台预留资金");
            cell.setCellStyle(style);
            cell = row.createCell(2);
            cell.setCellValue("宝付资金存量");
            cell.setCellStyle(style);
            cell = row.createCell(3);
            cell.setCellValue("交易类别");
            cell.setCellStyle(style);
            cell = row.createCell(4);
            cell.setCellValue("交易金额");
            cell.setCellStyle(style);
            cell = row.createCell(5);
            cell.setCellValue("交易后资金余额");
            cell.setCellStyle(style);
            cell = row.createCell(6);
            cell.setCellValue("注册人数");
            cell.setCellStyle(style);
            cell = row.createCell(7);
            cell.setCellValue("绑卡用户");
            cell.setCellStyle(style);
            cell = row.createCell(8);
            cell.setCellValue("累计注册");
            cell.setCellStyle(style);
            cell = row.createCell(9);
            cell.setCellValue("累计绑卡");
            cell.setCellStyle(style);
            cell = row.createCell(10);
            cell.setCellValue("购买交易");
            cell.setCellStyle(style);
            cell = row.createCell(11);
            cell.setCellValue("总资金流入");
            cell.setCellStyle(style);
            cell = row.createCell(12);
            cell.setCellValue("提现交易");
            cell.setCellStyle(style);
            cell = row.createCell(13);
            cell.setCellValue("提现金额");
            cell.setCellStyle(style);
            cell = row.createCell(14);
            cell.setCellValue("累计资金流入");
            cell.setCellStyle(style);
            cell = row.createCell(15);
            cell.setCellValue("平台资金存量");
            cell.setCellStyle(style);
            cell = row.createCell(16);
            cell.setCellValue("交易公司");
            cell.setCellStyle(style);
            cell = row.createCell(17);
            cell.setCellValue("零钱罐发息");
            cell.setCellStyle(style);
            cell = row.createCell(18);
            cell.setCellValue("定期标发息");
            cell.setCellStyle(style);
            cell = row.createCell(19);
            cell.setCellValue("好友返利成本");
            cell.setCellStyle(style);
            cell = row.createCell(20);
            cell.setCellValue("成本合计");
            cell.setCellStyle(style);
            cell = row.createCell(21);
            cell.setCellValue("累计划出");
            cell.setCellStyle(style);
            cell = row.createCell(22);
            cell.setCellValue("平台收益");
            cell.setCellStyle(style);
            cell = row.createCell(23);
            SumOperation sumOperation = null;
            SumOperation findSumOperation = sOtBean.findSumOperation(insertTime);
            row = sheet.createRow((int) 1);
            row.createCell(0).setCellValue(!QwyUtil.isNullAndEmpty(findSumOperation.getTodayDate()) ? findSumOperation.getTodayDate() : "");
            row.createCell(1).setCellValue(!QwyUtil.isNullAndEmpty(findSumOperation.getPlatformreserveMoney()) ? findSumOperation.getPlatformreserveMoney() : 0.0);
            row.createCell(2).setCellValue(!QwyUtil.isNullAndEmpty(findSumOperation.getBaofusaveMoney()) ? findSumOperation.getBaofusaveMoney() : 0.0);
            row.createCell(3).setCellValue(!QwyUtil.isNullAndEmpty(findSumOperation.getDealtype()) ? findSumOperation.getDealtype() : "");
            row.createCell(4).setCellValue(!QwyUtil.isNullAndEmpty(findSumOperation.getDealMoney()) ? findSumOperation.getDealMoney() : 0.0);
            row.createCell(5).setCellValue(!QwyUtil.isNullAndEmpty(findSumOperation.getAfterdealremainMoney()) ? findSumOperation.getAfterdealremainMoney() : 0.0);
            row.createCell(6).setCellValue(!QwyUtil.isNullAndEmpty(findSumOperation.getRigistpersonCount()) ? findSumOperation.getRigistpersonCount() : "");
            row.createCell(7).setCellValue(!QwyUtil.isNullAndEmpty(findSumOperation.getTieCard()) ? findSumOperation.getTieCard() : "");
            row.createCell(8).setCellValue(!QwyUtil.isNullAndEmpty(findSumOperation.getAllRigist()) ? findSumOperation.getAllRigist() : "");
            row.createCell(9).setCellValue(!QwyUtil.isNullAndEmpty(findSumOperation.getAllallRigist()) ? findSumOperation.getAllallRigist() : "");
            row.createCell(10).setCellValue(!QwyUtil.isNullAndEmpty(findSumOperation.getBuyDeal()) ? findSumOperation.getBuyDeal() : "");
            row.createCell(11).setCellValue(!QwyUtil.isNullAndEmpty(findSumOperation.getAllMoneyinflow()) ? findSumOperation.getAllMoneyinflow() : "");
            row.createCell(12).setCellValue(!QwyUtil.isNullAndEmpty(findSumOperation.getTxDeal()) ? findSumOperation.getTxDeal() : "");
            row.createCell(13).setCellValue(!QwyUtil.isNullAndEmpty(findSumOperation.getTxMoney()) ? findSumOperation.getTxMoney() : 0.0);
            row.createCell(14).setCellValue(!QwyUtil.isNullAndEmpty(findSumOperation.getAllMoneyinflowA()) ? findSumOperation.getAllMoneyinflowA() : "");
            row.createCell(15).setCellValue(!QwyUtil.isNullAndEmpty(findSumOperation.getPlatformsaveMoney()) ? findSumOperation.getPlatformsaveMoney() : "");
            row.createCell(16).setCellValue(!QwyUtil.isNullAndEmpty(findSumOperation.getDealCompany()) ? findSumOperation.getDealCompany() : "");
            row.createCell(17).setCellValue(!QwyUtil.isNullAndEmpty(findSumOperation.getLingqianfaxi()) ? findSumOperation.getLingqianfaxi() : 0.0);
            row.createCell(18).setCellValue(!QwyUtil.isNullAndEmpty(findSumOperation.getRegularbiaofaxi()) ? findSumOperation.getRegularbiaofaxi() : 0.0);
            row.createCell(19).setCellValue(!QwyUtil.isNullAndEmpty(findSumOperation.getFriendreturnMoney()) ? findSumOperation.getFriendreturnMoney() : 0.0);
            row.createCell(20).setCellValue(!QwyUtil.isNullAndEmpty(findSumOperation.getAllcost()) ? findSumOperation.getAllcost() : 0.0);
            row.createCell(21).setCellValue(!QwyUtil.isNullAndEmpty(findSumOperation.getAlllayoff()) ? findSumOperation.getAlllayoff() : 0.0);
            row.createCell(22).setCellValue(!QwyUtil.isNullAndEmpty(findSumOperation.getPlatearnings()) ? findSumOperation.getPlatearnings() : 0.0);
            String pathname = QwyUtil.fmyyyyMMddHHmmss3.format(new Date()) + "exportSummarizeTable_nfo.xls";
            String realPath = request.getServletContext().getRealPath("/report/" + pathname);
            log.info("运营统计地址：" + realPath);
            fout = new FileOutputStream(realPath);
            wb.write(fout);
            response.getWriter().write("/report/" + pathname);
        } catch (Exception e) {
            log.error("操作异常: ", e);
        } finally {
            try {
                fout.close();
            } catch (Exception e) {

            }
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

    public BuyProductInfoBean getBuyProductInfoBean() {
        return BuyProductInfoBean;
    }

    public void setBuyProductInfoBean(BuyProductInfoBean buyProductInfoBean) {
        BuyProductInfoBean = buyProductInfoBean;
    }

    public String getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(String insertTime) {
        this.insertTime = insertTime;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIsnew() {
        return isnew;
    }

    public void setIsnew(String isnew) {
        this.isnew = isnew;
    }

    public Integer getOs() {
        return os;
    }

    public void setOs(Integer os) {
        this.os = os;
    }
}
