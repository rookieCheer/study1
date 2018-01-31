package com.huoq.admin.product.action;

import com.huoq.common.action.BaseAction;
import com.huoq.common.bean.*;
import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.*;
import com.huoq.util.ExcelUtil;
import org.apache.struts2.config.Namespace;
import org.apache.struts2.config.ParentPackage;
import org.apache.struts2.config.Result;
import org.apache.struts2.config.Results;
import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private PlatformBean  platformBean;//updateTodayfirstBuyNumber
    
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
     * 每日明细表汇总
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
                Integer todayFirstInvestPeople = platformBean.updateTodayfirstBuyNumber(null);
                if(todayFirstInvestPeople!=null){
                    findSummaryTable.setTodayFirstInvestPeople(todayFirstInvestPeople);
                }
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
     * 导出每日明细汇总表
     */
    public void exportExcelDayDetailList() throws Exception{
       
        SummaryTable findSummaryTable = stBean.findSummaryTable(insertTime);
        if(findSummaryTable!=null){
            String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xls";
            response.setContentType(ExcelUtil.EXCEL_STYLE2007);
            response.setHeader("Content-disposition", "attachment;filename=" + fileName);
            ServletOutputStream outputStream = response.getOutputStream(); // 取得输出流
            LinkedHashMap<String, String> fieldMap = new LinkedHashMap<String, String>();
           
           Integer todayFirstInvestPeople = platformBean.updateTodayfirstBuyNumber(null);
           if(todayFirstInvestPeople!=null){
               findSummaryTable.setTodayFirstInvestPeople(todayFirstInvestPeople);
           }
            fieldMap.put("日期", "tadayDate");
            fieldMap.put("新增注册客户数A", "nEnrollUser"); //
            fieldMap.put("新增绑卡客户B", "nAutUser");
            fieldMap.put("今日首投人数", "todayFirstInvestPeople");
            fieldMap.put("累计注册客户数", "allEnUser");
            fieldMap.put("累计绑卡客户", "allAutUser");
            fieldMap.put("资金存量E", "capitalStock"); 
            fieldMap.put("当日提现金额E", "todayoutMoney");
            fieldMap.put("当日资金流入", "todayincapital");
            fieldMap.put("首投总额", "nDealMoney"); //
            fieldMap.put("复投总额D", "oDealMoney");
            fieldMap.put("新增注册iOS客户A", "nEnrollIosUser");
            fieldMap.put("新增注册Android客户A", "nEnrollAndroidUser");
            fieldMap.put("新增注册微信客户A", "nEnrollWeChatUser");
            fieldMap.put("新增绑卡iOS客户B", "nAutIosUser"); 
            fieldMap.put("新增绑卡Android客户B", "nAutAndroidUser");
            fieldMap.put("新增绑卡微信客户B", "nAutWeChatUser");
            fieldMap.put("当日购买交易笔数C", "todayDeal"); //
            fieldMap.put("新客户部分C", "nUnserDeal");
            fieldMap.put("老客户部分C", "oUserDeal");
            fieldMap.put("活期产品部分D", "currentProduct");
            fieldMap.put("定期产品部分D", "regularProduct");
            fieldMap.put("累积资金流入", "allinMoney"); 
            fieldMap.put("当日可提现金额E", "todayCash");
           
            
            List<SummaryTable> list = new ArrayList<SummaryTable>(1);
            list.add(findSummaryTable);
           
           
           ExcelUtil.exportExcelNew(outputStream, "每日明细汇总表", fieldMap, list, null);
        }
    }

    /**
     * 导出提现统计表
     */
    public void exportExcelCashTableList() throws Exception{
        PageUtil<OutCash> pageUtil = new PageUtil<OutCash>();
        pageUtil.setCurrentPage(currentPage);
        pageUtil.setPageSize(1000000);
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
        if(outCashTable!=null){
            List<OutCash> list = outCashTable.getList();
            if(list != null && list.size()>0){
                String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xls";
                response.setContentType(ExcelUtil.EXCEL_STYLE2007);
                response.setHeader("Content-disposition", "attachment;filename=" + fileName);
                ServletOutputStream outputStream = response.getOutputStream(); // 取得输出流
                LinkedHashMap<String, String> fieldMap = new LinkedHashMap<String, String>();
               
                fieldMap.put("提现日期", "outCashTime");
                fieldMap.put("提现金额(元)", "outMoney"); //
                fieldMap.put("客户姓名", "realname");
                fieldMap.put("手机", "phone");
                fieldMap.put("好友", "category");
                fieldMap.put("所属省份", "province");
                fieldMap.put("所属城市", "city"); 
                fieldMap.put("性别", "gender");
               
               
               ExcelUtil.exportExcelNew(outputStream, "提现情况统计表", fieldMap, list, null);
            }
        }
    }

    /**
     * 导出运营统计表
     */
    public void exportExcelOperationSumList() throws Exception{
      
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
