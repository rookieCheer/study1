package com.huoq.admin.product.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.admin.product.dao.InvestorsDAO;
import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.BackStatsOperateDay;
import com.huoq.orm.Investors;
import com.huoq.orm.PlatInvestors;
import com.huoq.orm.PlatUser;
import com.huoq.orm.InvestChannelExcelData;

@Service
public class InvestorsBean {

    @Resource
    InvestorsDAO          dao;
    
    private static Logger log = Logger.getLogger(InvestorsBean.class);

    /**
     * 分页获取结算记录
     * 
     * @param pageUtil 分页工具类
     * @param investorStatus 投资状态
     * @param name 用户名
     * @param insertTime 投资时间
     * @param type
     * @param productId 产品ID
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public PageUtil<Investors> findInvertorses(PageUtil pageUtil, String productTitle, String investorStatus, String name, String insertTime, String type,
                                               String productId) throws Exception {
        List<Object> list = new ArrayList<Object>();
        StringBuffer buffer = new StringBuffer();
        buffer.append(" SELECT u.username,us.real_name,u.regist_channel,MIN( t.pay_time),i.pay_time, p.title,p.lcqx,p.lcqx, ");
        buffer.append(" i.finish_time,i.investor_status,i.copies,i.in_money*0.01,i.expect_earnings/100,i.coupon*0.01,c.note, ");
        buffer.append(" i.coupon_shouyi/100,i.hongbao,c.type,i.annual_earnings,(i.expect_earnings/100+i.coupon_shouyi/100), ");
        buffer.append(" i.pay_time,i.start_time,i.clear_time,i.finish_time  ");
        buffer.append(" FROM  investors i  ");
        buffer.append(" LEFT JOIN product p ON i.product_id=p.id ");
        buffer.append(" LEFT JOIN users_info us  ON i.users_id=us.users_id  ");
        buffer.append(" LEFT JOIN users u  ON i.users_id=u.id  ");
        buffer.append(" LEFT JOIN  coupon c ON p.id=c.product_id  ");
        buffer.append(" LEFT JOIN (SELECT v.pay_time ,v.users_id FROM investors v GROUP BY v.users_id ORDER BY v.pay_time ASC) t  ");
        buffer.append(" ON t.users_id = i.users_id  ");

        if (!"all".equals(investorStatus)) {
            buffer.append(" AND i.investor_status = ?");
            list.add(investorStatus);
        } else {
            buffer.append(" AND i.investor_status != 5 ");

        }
        if (!QwyUtil.isNullAndEmpty(name)) {
            buffer.append(" AND u.username = ? ");

            list.add(DESEncrypt.jiaMiUsername(name));
        }
        // 充值时间
        if (!QwyUtil.isNullAndEmpty(insertTime)) {
            String[] time = QwyUtil.splitTime(insertTime);
            if (time.length > 1) {
                buffer.append(" AND i.pay_time >= ? ");

                list.add(QwyUtil.fmMMddyyyy.parse(time[0] + " 00:00:00"));
                buffer.append(" AND i.pay_time <= ? ");

                list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
            } else {
                buffer.append(" AND i.pay_time >= ? ");

                list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                buffer.append(" AND i.pay_time <= ? ");

                list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
            }
        }
        if (!QwyUtil.isNullAndEmpty(type) && type.equals("1")) {
            String[] time = QwyUtil.splitTime(insertTime);
            buffer.append(" AND u.insert_time >= ? ");

            list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
            buffer.append(" AND u.insert_time <= ? ");

            list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
        }

        if (!QwyUtil.isNullAndEmpty(productId)) {
            buffer.append(" AND p.id = ? ");

            list.add(productId);
        }
        if (!QwyUtil.isNullAndEmpty(productTitle)) {
            buffer.append(" AND p.title like '%" + productTitle + "%' ");
        }

        buffer.append("GROUP BY i.users_id,i.pay_time  ORDER BY i.pay_time DESC  ");
        StringBuffer bufferCount = new StringBuffer();
        bufferCount.append(" SELECT COUNT(*)  ");
        bufferCount.append(" FROM (");
        bufferCount.append(buffer);
        bufferCount.append(") t");

        pageUtil = dao.getBySqlAndSqlCount(pageUtil, buffer.toString(), bufferCount.toString(), list.toArray());
        List<Investors> investors = toInvestors(pageUtil.getList());
        pageUtil.setList(investors);
        return pageUtil;

    }

    private List<Investors> toInvestors(List<Object[]> list) throws ParseException {
        List<Investors> investorsList = new ArrayList<Investors>();
        if (!QwyUtil.isNullAndEmpty(list)) {
            for (Object[] object : list) {
                Investors investors = new Investors();
                if(!QwyUtil.isNullAndEmpty(object)){
                    investors.setUsername(DESEncrypt.jieMiUsername(object[0]+""));
                    investors.setRealname(object[1]+"");
                    investors.setRegistChannel(object[2]+"");
                     if(object[3].toString().equals(object[4].toString())){
                         investors.setIsFirstInvt("是");
                     }else {
                         investors.setIsFirstInvt("否");
                     }
                     investors.setTitle(object[5]+"");
                     investors.setTits(!QwyUtil.isNullAndEmpty(object[6])? Integer.valueOf(object[6] + "") : 0);
                     investors.setLcqx(!QwyUtil.isNullAndEmpty(object[7])? Integer.valueOf(object[7] + "") : 0);
                     if(!QwyUtil.isNullAndEmpty(object[8])){
                         Date date=new Date();
                         String time = object[8]+"";
                         SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
                         Date parse = sd.parse(time);
                         long day=0;
                         if(parse.getTime() < date.getTime()){
                            day =((date.getTime()-parse.getTime())/(1000*3600*24));
                            investors.setTzqx(day);
                         }else{
                             day=((parse.getTime()-date.getTime())/ (1000*3600*24));
                             investors.setTzqx(day);
                         }
                     }
                     if(!QwyUtil.isNullAndEmpty(object[9])){
                         investors.setInvestorStatus(object[9]+"");
                     }
                    investors.setCopies(!QwyUtil.isNullAndEmpty(object[10])? Long.valueOf(object[10] + "") : 0);
                    investors.setInMoney(!QwyUtil.isNullAndEmpty(object[11]) ? Double.valueOf(object[11] + "") : 0.0);
                    investors.setExpectEarnings(!QwyUtil.isNullAndEmpty(object[12]) ? Double.valueOf(object[12] + "") : 0.0);
                    investors.setCoupon(!QwyUtil.isNullAndEmpty(object[13]) ?  Double.valueOf(object[13] + "") : 0.0);
                    if("1".equals(object[17]) || "0".equals(object[17]) ){
                        investors.setInvestSource(!QwyUtil.isNullAndEmpty(object[14])? object[14].toString() : null);
                    }else if ("3".equals(object[17])) {
                        investors.setRedPackageSource(!QwyUtil.isNullAndEmpty(object[14])? object[14].toString() : null);
                    }
                    investors.setCouponShouyi(!QwyUtil.isNullAndEmpty(object[15]) ? Double.valueOf(object[15] + "") : 0.0);
                    investors.setHongbao(!QwyUtil.isNullAndEmpty(object[16]) ? Double.valueOf(object[16] + "") : 0.0);
                    investors.setAnnualEarnings(!QwyUtil.isNullAndEmpty(object[18]) ? Double.valueOf(object[18] + "") : 0.0);
                    investors.setFinalEarnings(!QwyUtil.isNullAndEmpty(object[19]) ? Double.valueOf(object[19] + "") : 0.0);
                    investors.setPayTime(!QwyUtil.isNullAndEmpty(object[20]) ? QwyUtil.fmyyyyMMddHHmmss.parse(object[20].toString()) : null);
                    investors.setStartTime(!QwyUtil.isNullAndEmpty(object[21]) ? QwyUtil.fmyyyyMMddHHmmss.parse(object[21].toString()) : null);
                    investors.setClearTime(!QwyUtil.isNullAndEmpty(object[22]) ? QwyUtil.fmyyyyMMddHHmmss.parse(object[22].toString()) : null);
                    investors.setFinishTime(!QwyUtil.isNullAndEmpty(object[23]) ? QwyUtil.fmyyyyMMddHHmmss.parse(object[23].toString()) : null);
                    investorsList.add(investors);
                }
            }
        }
        return investorsList;
    }



    /**
     * 以日期分组查询充值记录
     * 
     * @param pageUtil 分页
     * @param insertTime 时间段
     * @return
     * @throws ParseException
     * @throws Exception
     */
    public PageUtil<BackStatsOperateDay> findInverstorsRecordGroupByDate(PageUtil pageUtil, String insertTime) throws ParseException {

        List<Object> list = new ArrayList<Object>();
        StringBuffer buffer = new StringBuffer();
        buffer.append(" select us.date as date,sum(copiesSum) copiesSum ,sum(investCentSum ) investCentSum");
        buffer.append(", sum(couponCentSum ) couponCentSum  ");
        buffer.append(" FROM back_stats_operate_day us WHERE 1=1 ");

        // 发布时间
        if (!QwyUtil.isNullAndEmpty(insertTime)) {
            String[] time = QwyUtil.splitTime(insertTime);
            if (time.length > 1) {
                buffer.append(" AND us.date >= ? ");
                list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
                buffer.append(" AND us.date <= ? ");
                list.add(QwyUtil.fmMMddyyyy.parse(time[1]));
            } else {
                buffer.append(" AND us.date >= ? ");
                list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                buffer.append(" AND us.date <= ? ");
                list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
            }
        }
        buffer.append(" GROUP BY DATE_FORMAT(us.date, '%Y-%m-%d')  ");
        buffer.append(" ORDER BY us.date DESC ");
        StringBuffer bufferCount = new StringBuffer();
        bufferCount.append(" SELECT COUNT(*)  ");
        bufferCount.append(" FROM (");
        bufferCount.append(buffer);
        bufferCount.append(") t");
        pageUtil = dao.getBySqlAndSqlCount(pageUtil, buffer.toString(), bufferCount.toString(), list.toArray());
        List<BackStatsOperateDay> platUsers = toDateMoney(pageUtil.getList());
        pageUtil.setList(platUsers);

        return pageUtil;
    }

    /**
     * 每日投资统计
     * 
     * @param pageUtil 分页
     * @param insertTime 时间段
     * @return
     * @throws ParseException
     * @throws Exception
     */
    public PageUtil<Investors> findInverstorsRecordGroupBy(PageUtil pageUtil, String insertTime) throws ParseException {

        List<Object> list = new ArrayList<Object>();
        StringBuffer buffer = new StringBuffer();
        buffer.append(" select pay_time ,sum(copies) copies ,sum(in_money ) inMoney");
        buffer.append(", sum(coupon ) coupon  ");
        buffer.append(" FROM investors WHERE 1=1 AND investor_status in ('1','2','3') ");

        // 发布时间
        if (!QwyUtil.isNullAndEmpty(insertTime)) {
            String[] time = QwyUtil.splitTime(insertTime);
            if (time.length > 1) {
                buffer.append(" AND pay_time >= ? ");
                list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
                buffer.append(" AND pay_time <= ? ");
                list.add(QwyUtil.fmMMddyyyy.parse(time[1]));
            } else {
                buffer.append(" AND pay_time >= ? ");
                list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                buffer.append(" AND pay_time <= ? ");
                list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
            }
        }
        buffer.append(" GROUP BY DATE_FORMAT(pay_time, '%Y-%m-%d')  ");
        buffer.append(" ORDER BY pay_time DESC ");
        StringBuffer bufferCount = new StringBuffer();
        bufferCount.append(" SELECT COUNT(*)  ");
        bufferCount.append(" FROM (");
        bufferCount.append(buffer);
        bufferCount.append(") t");
        pageUtil = dao.getBySqlAndSqlCount(pageUtil, buffer.toString(), bufferCount.toString(), list.toArray());
        List<Investors> platUsers = toDateMoney2(pageUtil.getList());
        pageUtil.setList(platUsers);

        return pageUtil;
    }

    /**
     * 将数据转换为DateMoney
     * 
     * @throws ParseException
     */
    private List<Investors> toDateMoney2(List<Object[]> list) throws ParseException {
        List<Investors> backStatsOperateDayList = new ArrayList<Investors>();
        if (!QwyUtil.isNullAndEmpty(list)) {
            for (Object[] obj : list) {
                Investors investors = new Investors();
                investors.setPayTime(QwyUtil.fmyyyyMMddHHmmss.parse(QwyUtil.fmyyyyMMddHHmmss.format(obj[0])));
                investors.setCopies(Long.parseLong(obj[1] + ""));
                investors.setInMoney(Double.parseDouble(obj[2] + ""));
                investors.setCoupon(Double.parseDouble(obj[3] + ""));
                backStatsOperateDayList.add(investors);
            }
        }
        return backStatsOperateDayList;
    }

    /**
     * 将数据转换为DateMoney
     * 
     * @throws ParseException
     */
    private List<BackStatsOperateDay> toDateMoney(List<Object[]> list) throws ParseException {
        List<BackStatsOperateDay> backStatsOperateDayList = new ArrayList<BackStatsOperateDay>();
        if (!QwyUtil.isNullAndEmpty(list)) {
            for (Object[] obj : list) {
                BackStatsOperateDay dateMoney = new BackStatsOperateDay();
                dateMoney.setDate(QwyUtil.fmyyyyMMddHHmmss.parse(QwyUtil.fmyyyyMMddHHmmss.format(obj[0])));
                dateMoney.setCopiesSum(Integer.parseInt(obj[1] + ""));
                dateMoney.setInvestCentSum(Double.parseDouble(obj[2] + ""));
                dateMoney.setCouponCentSum(Double.parseDouble(obj[3] + ""));
                backStatsOperateDayList.add(dateMoney);
            }
        }
        return backStatsOperateDayList;
    }

    /**
     * 根据日期统计用户注册并且投资人数及投资金额
     * 
     * @param insertTime 注册时间
     * @return
     */
    public PageUtil<PlatUser> findUsersCountByDate(PageUtil pageUtil, String insertTime) throws Exception {
        ArrayList<Object> list = new ArrayList<Object>();
        StringBuffer buffer = new StringBuffer();
        buffer.append(" SELECT DATE_FORMAT( dd.insert_time, '%Y-%m-%d' ) as date,   ");
        buffer.append(" t.userscount,t.buycopies,t.inscount ");
        buffer.append(" FROM dateday dd ");
        buffer.append(" LEFT JOIN ( ");
        buffer.append(" SELECT DATE_FORMAT( ins.insert_time, '%Y-%m-%d' ) as date ,    ");
        buffer.append(" COUNT(DISTINCT ins.users_id) as userscount, ");
        buffer.append(" SUM(ins.copies) as buycopies, ");
        buffer.append(" COUNT(ins.id) as inscount ");
        buffer.append(" FROM  investors ins ");
        buffer.append(" WHERE ins.investor_status in ('1','2','3')  ");
        buffer.append(" AND ins.id is not NULL  ");
        buffer.append(" AND ins.users_id in ( ");
        buffer.append(" SELECT id FROM  users as us ");
        buffer.append(" WHERE  DATE_FORMAT( us.insert_time, '%Y-%m-%d' ) = DATE_FORMAT( ins.pay_time, '%Y-%m-%d' ) ");
        buffer.append(" AND us.id=ins.users_id ");
        buffer.append(" ) ");
        buffer.append(" GROUP BY DATE_FORMAT( ins.pay_time, '%Y-%m-%d' )  ");
        buffer.append(" ORDER BY DATE_FORMAT( ins.pay_time, '%Y-%m-%d' ) DESC ");
        buffer.append(" ) t");
        buffer.append(" ON DATE_FORMAT( dd.insert_time, '%Y-%m-%d' ) = t.date ");
        buffer.append(" WHERE 1=1 ");
        // 充值时间
        if (!QwyUtil.isNullAndEmpty(insertTime)) {
            String[] time = QwyUtil.splitTime(insertTime);
            if (time.length > 1) {
                buffer.append(" AND dd.insert_time >= ? ");
                list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
                buffer.append(" AND dd.insert_time <= ? ");
                list.add(QwyUtil.fmMMddyyyy.parse(time[1]));
            } else {
                buffer.append(" AND dd.insert_time >= ? ");
                list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                buffer.append(" AND dd.insert_time <= ? ");
                list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
            }
        }
        buffer.append(" GROUP BY DATE_FORMAT( dd.insert_time, '%Y-%m-%d' ) ");
        buffer.append(" ORDER BY DATE_FORMAT( dd.insert_time, '%Y-%m-%d' ) DESC ");
        StringBuffer bufferCount = new StringBuffer();
        bufferCount.append(" SELECT COUNT(t.date)  ");
        bufferCount.append(" FROM (");
        bufferCount.append(buffer);
        bufferCount.append(") t");
        pageUtil = dao.getBySqlAndSqlCount(pageUtil, buffer.toString(), bufferCount.toString(), list.toArray());
        List<PlatUser> platUsers = toPlatUser(pageUtil.getList());
        pageUtil.setList(platUsers);
        return pageUtil;
    }

    private List<PlatInvestors> toMoney(List<Object[]> list) throws Exception {
        List<PlatInvestors> platInverstors = new ArrayList<PlatInvestors>();
        if (!QwyUtil.isNullAndEmpty(list)) {
            for (Object[] object : list) {
                PlatInvestors plat = new PlatInvestors();
                plat.setId(object[0] == null ? null : Long.valueOf(object[0] + ""));// id
                plat.setUsername(object[1] == null ? "" : object[1] + "");// 用户名
                plat.setReal_name(!QwyUtil.isNullAndEmpty(object[2]) ? object[2] + "" : "");// 用户真实姓名
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
                Date insertTime = null;
                Date bandCardTime = null;
                Date fristBuyTime = null;
                if (!QwyUtil.isNullAndEmpty(object[3])) {
                    insertTime = sd.parse(object[3] + "");
                }
                if (!QwyUtil.isNullAndEmpty(object[4])) {
                    bandCardTime = sd.parse(object[3] + "");
                }
                if (!QwyUtil.isNullAndEmpty(object[5])) {
                    fristBuyTime = sd.parse(object[5] + "");
                }
                plat.setInsertTime(!QwyUtil.isNullAndEmpty(insertTime) ? insertTime : null);// 注册时间
                plat.setBandCardTime(!QwyUtil.isNullAndEmpty(bandCardTime) ? bandCardTime : null);// 绑卡时间
                plat.setFristBuyTime(!QwyUtil.isNullAndEmpty(fristBuyTime) ? fristBuyTime : null);// 首投时间
                plat.setCopies(!QwyUtil.isNullAndEmpty(object[6]) ? QwyUtil.jieQuFa(Double.valueOf(object[6]+""),2) + "" : "0");// 投资总额
                plat.setAllMoney(!QwyUtil.isNullAndEmpty(object[7]) ?  QwyUtil.jieQuFa(Double.valueOf(object[7]+"")/100,2)  + "" : "0");// 现存资金
                plat.setBuyInMoney(!QwyUtil.isNullAndEmpty(object[8]) ? QwyUtil.jieQuFa(Double.valueOf(object[8]+"")/100,2) + "" : "0");// 在贷金额
                plat.setCoinPurseMoney(!QwyUtil.isNullAndEmpty(object[9]) ? QwyUtil.jieQuFa(Double.valueOf(object[9]+"")/100,2) + "" : "0");// 零钱罐金额
                plat.setLeftMoney(!QwyUtil.isNullAndEmpty(object[10]) ? QwyUtil.jieQuFa( Double.valueOf(object[10]+"")/100,2) + "" : "0");// 账户余额
                plat.setCoupon(!QwyUtil.isNullAndEmpty(object[11]) ?  QwyUtil.jieQuFa(Double.valueOf(object[11] + "")/100,2)+"" : "0");// 投资券金额
                plat.setFriendNumber(!QwyUtil.isNullAndEmpty(object[12]) ? object[12] + "" : "0");// 邀请好友人数
                plat.setFriendMoney(!QwyUtil.isNullAndEmpty(object[13]) ? QwyUtil.jieQuFa(Double.valueOf(object[13] + "")/100,2)+"" : "0");// 邀请好友人数
                plat.setHongbao(!QwyUtil.isNullAndEmpty(object[14]) ? QwyUtil.jieQuFa(Double.valueOf(object[14] + "")/100,2)+"" : "0");// 红包金额
                platInverstors.add(plat);
            }
        }
        return platInverstors;
    }

    @SuppressWarnings("unchecked")
    public PageUtil<PlatInvestors> loadInvestors(String name, String realname, String insertTime, PageUtil pageUtil) {
        try {
            ArrayList<Object> ob = new ArrayList<Object>();
            StringBuffer buff = new StringBuffer();
            buff.append("SELECT u.id,u.username  AS username, i.real_name AS real_name,IFNULL(u.insert_time,null) ,IFNULL(ac.insert_time,null),IFNULL(zc.insert_time,null),SUM(ins.copies)  AS copies ,");
            buff.append("i.total_money,c.all_money,lqg.in_money,i.left_money,SUM(ins.coupon)  AS coupon ,inv.num,inv.money,SUM(ins.hongbao) ");
            buff.append("FROM investors ins ");
            buff.append("LEFT JOIN users u ON ins.users_id = u.id  ");
            buff.append("LEFT JOIN users_info i ON i.users_id = u.id  ");
            buff.append("LEFT JOIN account ac ON ac.users_id = u.id ");
            buff.append("LEFT JOIN (SELECT MIN(i.insert_time) insert_time,i.users_id ");
            buff.append("FROM investors i  GROUP BY i.`users_id` )zc ON zc.users_id = u.id ");
            buff.append("LEFT JOIN (SELECT i.users_id,SUM(i.in_money) all_money ");
            buff.append("FROM investors i WHERE investor_status ='1' GROUP BY i.`users_id` )c ON c.users_id = u.id ");
            buff.append("LEFT JOIN (SELECT in_money,users_id FROM coin_purse cp )lqg ON lqg.users_id = u.id ");
            buff.append("LEFT JOIN (SELECT COUNT(be_invited_id) num,invite_id,SUM(ui.total_money) money FROM invite inv ");
            buff.append("LEFT JOIN users_info ui ON ui.`users_id` = inv.be_invited_id ");
            buff.append("GROUP BY invite_id)inv ON inv.invite_id = u.id ");
            buff.append("WHERE investor_status IN ('1', '2', '3') ");
            if (!QwyUtil.isNullAndEmpty(name)) {
                buff.append("AND u.username = ? ");
                ob.add(DESEncrypt.jiaMiUsername(name));
            }
            if (!QwyUtil.isNullAndEmpty(realname)) {
                buff.append("AND i.real_name like '%" + realname + "%'");
            }
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    buff.append(" AND u.insert_time >= ? ");
                    ob.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buff.append(" AND u.insert_time <= ? ");
                    ob.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
                } else {
                    buff.append(" AND u.insert_time >= ? ");
                    ob.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buff.append(" AND u.insert_time <= ? ");
                    ob.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }

            }
            buff.append(" GROUP BY ins.users_id ");
            buff.append(" ORDER BY i.total_money DESC");
            StringBuffer bufferCount = new StringBuffer();
            bufferCount.append(" SELECT COUNT(t.username)  ");
            bufferCount.append(" FROM (");
            bufferCount.append(buff);
            bufferCount.append(") t");
            pageUtil = dao.getBySqlAndSqlCount(pageUtil, buff.toString(), bufferCount.toString(), ob.toArray());
            List<PlatInvestors> platUsers = toMoney(pageUtil.getList());
            pageUtil.setList(platUsers);
            return pageUtil;
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 将数据转换为platUser形式
     * 
     * @param list
     * @return
     */
    private List<PlatUser> toPlatUser(List<Object[]> list) {
        List<PlatUser> platUsers = new ArrayList<PlatUser>();
        if (!QwyUtil.isNullAndEmpty(list)) {
            for (Object[] object : list) {
                PlatUser platUser = new PlatUser();
                platUser.setDate(object[0] + "");
                platUser.setUserscount(!QwyUtil.isNullAndEmpty(object[1]) ? object[1] + "" : "0");
                platUser.setInsMoney(!QwyUtil.isNullAndEmpty(object[2]) ? object[2] + "" : "0");
                platUsers.add(platUser);
            }
        }
        return platUsers;
    }

    /**
     * 人数、人次、自己投入的金额、优惠券金额统计
     * 
     * @param targetDate
     * @return
     */
    public String investorsStatistics(String targetDate) {
        StringBuffer jsonData = new StringBuffer();
        // 人数统计
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT COUNT(1) FROM (SELECT users_id FROM investors WHERE DATE_FORMAT('" + targetDate
                   + " 0','%Y-%m-%d %H') = DATE_FORMAT(pay_time,'%Y-%m-%d %H') GROUP BY users_id) tab1 ");
        for (int i = 1; i < 24; i++) {
            sql.append(" UNION ALL SELECT COUNT(1) FROM (SELECT users_id FROM investors WHERE DATE_FORMAT('" + targetDate + " " + i
                       + "','%Y-%m-%d %H') = DATE_FORMAT(pay_time,'%Y-%m-%d %H') GROUP BY users_id) tab" + i);
        }
        jsonData.append("{");
        jsonData.append("\"personCount\":" + ListToStringArray(dao.LoadAllSql(sql.toString(), null)));

        // 人次统计
        sql.setLength(0);
        sql.append("SELECT COUNT(1) FROM investors WHERE DATE_FORMAT('" + targetDate + " 0','%Y-%m-%d %H') = DATE_FORMAT(pay_time,'%Y-%m-%d %H')");
        for (int i = 1; i < 24; i++) {
            sql.append(" UNION ALL SELECT COUNT(1) FROM investors WHERE DATE_FORMAT('" + targetDate + " " + i + "','%Y-%m-%d %H') = DATE_FORMAT(pay_time,'%Y-%m-%d %H')");
        }
        jsonData.append(",\"personTime\":" + ListToStringArray(dao.LoadAllSql(sql.toString(), null)));

        // 自己投入的金额统计
        sql.setLength(0);
        sql.append("SELECT CASE WHEN SUM(in_money*0.01) IS NULL THEN 0 ELSE SUM(in_money*0.01) END AS sum_ FROM investors WHERE investor_status in ('1','2','3') and DATE_FORMAT('"
                   + targetDate + " 0','%Y-%m-%d %H') = DATE_FORMAT(pay_time,'%Y-%m-%d %H')");
        for (int i = 1; i < 24; i++) {
            sql.append(" UNION ALL SELECT CASE WHEN SUM(in_money*0.01) IS NULL THEN 0 ELSE SUM(in_money*0.01) END AS sum_ FROM investors WHERE DATE_FORMAT('" + targetDate + " " + i
                       + "','%Y-%m-%d %H') = DATE_FORMAT(pay_time,'%Y-%m-%d %H')");
        }
        jsonData.append(",\"inMoney\":" + ListToStringArray(dao.LoadAllSql(sql.toString(), null)));

        // 优惠券金额统计
        sql.setLength(0);
        sql.append("SELECT CASE WHEN SUM(coupon*0.01) IS NULL THEN 0 ELSE SUM(coupon*0.01) END AS sum_ FROM investors WHERE investor_status in ('1','2','3') and DATE_FORMAT('"
                   + targetDate + " 0','%Y-%m-%d %H') = DATE_FORMAT(pay_time,'%Y-%m-%d %H')");
        for (int i = 1; i < 24; i++) {
            sql.append(" UNION ALL SELECT CASE WHEN SUM(coupon*0.01) IS NULL THEN 0 ELSE SUM(coupon*0.01) END AS sum_ FROM investors WHERE DATE_FORMAT('" + targetDate + " " + i
                       + "','%Y-%m-%d %H') = DATE_FORMAT(pay_time,'%Y-%m-%d %H')");
        }
        jsonData.append(",\"coupon\":" + ListToStringArray(dao.LoadAllSql(sql.toString(), null)));
        jsonData.append("}");
        return jsonData.toString();
    }

    /**
     * 把list转成js数组
     * 
     * @param list
     * @return
     */
    public String ListToStringArray(List list) {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                sb.append(list.get(i));
            } else {
                sb.append("," + list.get(i) + "");
            }

        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * 渠道投资统计-筛选首投用户 investChannelScreen
     */
    public List<Object[]> investChannelScreen(String channel, String st, String et, int currentPage, int pageSize, String order, String registPlatform, String firstInvest,
                                              int excel) {
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT * FROM( SELECT * FROM ( SELECT (us.`username`), (SELECT CONCAT(usi.`real_name`,'_',usi.`invest_count`) FROM `users_info` usi WHERE usi.`users_id` = us.`id` )'username,investCount',");
        sb.append("(SELECT pro.`title` FROM `product` pro WHERE pro.`id` IN (iv.`product_id`))'title',(iv.pay_time) 'pay_time',");
        sb.append("iv.copies 'copies', iv.`in_money`*0.01 'in_money',iv.`coupon`*0.01 'coupon',us.province 'province',us.regist_channel 'regist_channel',us.regist_platform,(us.insert_time) 'insert_time', us.id 'id' ");
        sb.append("FROM `users` us LEFT JOIN `investors` iv ON us.`id` =iv.`users_id`  WHERE   iv.`investor_status` IN ('1','2','3')");
        if (!QwyUtil.isNullAndEmpty(channel)) sb.append("AND us.`regist_channel` ='" + channel + "' ");
        if (!QwyUtil.isNullAndEmpty(registPlatform)) sb.append("AND us.`regist_platform` ='" + registPlatform + "' ");
        if (!QwyUtil.isNullAndEmpty(order)) {
            sb.append("ORDER BY ");
            sb.append(order);
            sb.append(", (us.regist_channel+0) DESC, us.username DESC");
        } else sb.append(" ORDER BY  (us.regist_channel+0) DESC, us.username DESC,iv.pay_time )a ");

        sb.append(" GROUP BY a.id ORDER BY   (a.regist_channel+0)  DESC, a.username DESC,a.pay_time DESC )b ");
        if (!QwyUtil.isNullAndEmpty(st)) {
            sb.append(" WHERE b.pay_time BETWEEN '");
            sb.append(st);
            sb.append("' AND '");
            sb.append(et + "' ");
        }
        //
        sb.append(" ORDER BY b.pay_time DESC ");
        List<Object[]> list = null;
        if (excel == 1) {
            // 导出表格
            list = dao.LoadAllSql(sb.toString(), null);
        } else {
            // 查询展示
            list = (List<Object[]>) dao.findAdvListMapSql(sb.toString(), null, currentPage, pageSize);
            ;// dao.LoadAllSql(sb.toString(), null);
        }
        // dao.findAdvListMapSql(sb.toString(), null,1, 11);
        if (QwyUtil.isNullAndEmpty(list)) return null;
        return list;
    }

    /**
     * 查询对应渠道号的投资情况;
     * 
     * @param channel 渠道号
     * @param st 开始时间;
     * @param et 结束时间;
     * @return
     */
    public List<Object[]> investChannel(String channel, String st, String et, int currentPage, int pageSize, String order, String registPlatform) {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT (us.`username`), (SELECT CONCAT(usi.`real_name`,'_',usi.`invest_count`) FROM `users_info` usi WHERE usi.`users_id` = us.`id` )'username,investCount',");
        sb.append("(SELECT pro.`title` FROM `product` pro WHERE pro.`id` IN (iv.`product_id`))'title',(us.insert_time) 'insert_time',(iv.pay_time) 'pay_time',");
        sb.append("iv.copies 'copies', iv.`in_money`*0.01 'in_money',iv.`coupon`*0.01 'coupon',us.province,us.regist_channel ");
        sb.append("FROM `users` us LEFT JOIN `investors` iv ON us.`id` =iv.`users_id`  WHERE   iv.`investor_status` IN ('1','2','3')");
        if (!QwyUtil.isNullAndEmpty(channel)) sb.append("AND us.`regist_channel` ='" + channel + "' ");
        if (!QwyUtil.isNullAndEmpty(registPlatform)) sb.append("AND us.`regist_platform` ='" + registPlatform + "' ");

        if (!QwyUtil.isNullAndEmpty(st)) {
            sb.append(" AND iv.pay_time BETWEEN '");
            sb.append(st);
            sb.append("' AND '");
            sb.append(et + "' ");
        }
        if (!QwyUtil.isNullAndEmpty(order)) {
            sb.append("ORDER BY ");
            sb.append(order);
            sb.append(", (us.regist_channel+0) DESC, us.username DESC");
        } else sb.append("ORDER BY  (us.regist_channel+0) DESC, us.username DESC, iv.pay_time DESC ");
        List<Object[]> list = (List<Object[]>) dao.findAdvListMapSql(sb.toString(), null, currentPage, pageSize);
        ;// dao.LoadAllSql(sb.toString(), null);
         // dao.findAdvListMapSql(sb.toString(), null,1, 11);
        if (QwyUtil.isNullAndEmpty(list)) return null;
        return list;

    }

    /**
     * 平台渠道统计查询
     * 
     * @param st 开始时间;
     * @param et 结束时间;
     * @return
     */
    public List registPlatform(String registPlatform, String registChannel, String st, String et, int currentPage, int pageSize, int investorStatus) {
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT i.pay_time, SUM(i.copies) as copies, SUM(i.in_money) as in_money, SUM(i.coupon ) as coupon ,u.regist_platform , u.regist_channel FROM investors i ");
        sb.append(" LEFT JOIN users u ON u.id = i.users_id where 1=1 ");
        sb.append("AND i.investor_status IN ('1', '2', '3') ");
        if (!QwyUtil.isNullAndEmpty(registPlatform)) {
            sb.append("AND u.`regist_platform` ='" + registPlatform + "' ");
        }
        if (!QwyUtil.isNullAndEmpty(registChannel)) {
            sb.append("AND u.`regist_channel` ='" + registChannel + "' ");
        }
        if (!QwyUtil.isNullAndEmpty(st)) {
            sb.append(" AND i.pay_time BETWEEN '");
            sb.append(st);
            sb.append("' AND '");
            sb.append(et + "' ");
        }
        sb.append(" GROUP BY  u.regist_platform , DATE_FORMAT(i.pay_time,'%Y-%m-%d') ORDER BY i.pay_time");
        List list = dao.findAdvListMapSql(sb.toString(), null, currentPage, pageSize);
        ;
        return list;
    }

    /**
     * 注册平台查询
     */
    public List<Object[]> selectRegistPlatform() {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT regist_platform FROM `users` GROUP BY regist_platform ORDER BY regist_platform+0 ASC ");
        List<Object[]> list = (List<Object[]>) dao.LoadAllSql(sb.toString(), null);
        if (QwyUtil.isNullAndEmpty(list)) return null;
        return list;
    }

    /**
     * 查询对应渠道号的投资情况;
     * 
     * @param channel 渠道号
     * @param st 开始时间;
     * @param et 结束时间;
     * @return
     */
    public List<Object[]> findInvestChannel(String channel, String st, String et, int currentPage, int pageSize, String order, String registPlatform) {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT (us.`username`), (SELECT CONCAT(usi.`real_name`,'_',usi.`invest_count`) FROM `users_info` usi WHERE usi.`users_id` = us.`id` )'username,investCount',");
        sb.append("(SELECT pro.`title` FROM `product` pro WHERE pro.`id` IN (iv.`product_id`))'title',(iv.pay_time) 'pay_time',");
        sb.append("iv.copies 'copies', iv.`in_money`*0.01 'in_money',iv.`coupon`*0.01 'coupon',us.province,us.regist_channel, us.regist_platform,(us.insert_time) 'insert_time' ");
        sb.append("FROM `users` us LEFT JOIN `investors` iv ON us.`id` =iv.`users_id`  WHERE   iv.`investor_status` IN ('1','2','3')");
        if (!QwyUtil.isNullAndEmpty(channel)) sb.append("AND us.`regist_channel` ='" + channel + "' ");
        if (!QwyUtil.isNullAndEmpty(registPlatform)) sb.append("AND us.`regist_platform` ='" + registPlatform + "' ");

        if (!QwyUtil.isNullAndEmpty(st)) {
            sb.append(" AND iv.pay_time BETWEEN '");
            sb.append(st);
            sb.append("' AND '");
            sb.append(et + "' ");
        }
        if (!QwyUtil.isNullAndEmpty(order)) {
            sb.append("ORDER BY ");
            sb.append(order);
            sb.append(", (us.regist_channel+0) DESC, us.username DESC");
        } else sb.append("ORDER BY  (us.regist_channel+0) DESC, us.username DESC, iv.pay_time DESC ");
        // List<Object[]> list =
        // (List<Object[]>)dao.findAdvListSql(sb.toString(),null,currentPage,pageSize);
        List<Object[]> list = dao.LoadAllSql(sb.toString(), null);
        if (QwyUtil.isNullAndEmpty(list)) return null;
        return list;

    }

    /**
     * 封装渠道投资统计实体类，用于报表导出
     * 
     * @param list
     * @return
     * @throws Exception
     */
    public List<InvestChannelExcelData> toInvestChannelExcelData(List<Object[]> list) throws Exception {
        List<InvestChannelExcelData> results = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            InvestChannelExcelData iced = new InvestChannelExcelData();
            iced.setIndex((i + 1) + "");
            iced.setUsername(!QwyUtil.isNullAndEmpty(list.get(i)[0]) ? DESEncrypt.jieMiUsername(list.get(i)[0].toString()) : "");
            iced.setNameInvestCount(!QwyUtil.isNullAndEmpty(list.get(i)[1]) ? list.get(i)[1].toString() : "");
            iced.setProduct(!QwyUtil.isNullAndEmpty(list.get(i)[2]) ? list.get(i)[2].toString() : "");
            iced.setPayTime(!QwyUtil.isNullAndEmpty(list.get(i)[3]) ? QwyUtil.fmMMddyyyyHHmmss.format(list.get(i)[3]) : "");
            iced.setTotalInvestment(!QwyUtil.isNullAndEmpty(list.get(i)[4]) ? list.get(i)[4].toString() : "0");
            iced.setCapital(!QwyUtil.isNullAndEmpty(list.get(i)[5]) ? list.get(i)[5].toString() : "0");
            iced.setCouponMoney(!QwyUtil.isNullAndEmpty(list.get(i)[6]) ? list.get(i)[6].toString() : "0");
            iced.setProvince(!QwyUtil.isNullAndEmpty(list.get(i)[7]) ? list.get(i)[7].toString() : "");
            iced.setChannelNo(!QwyUtil.isNullAndEmpty(list.get(i)[8]) ? list.get(i)[8].toString() : "");
            iced.setRegistPlatform(!QwyUtil.isNullAndEmpty(list.get(i)[9]) ? list.get(i)[9].toString() : "");
            iced.setInsertTime(!QwyUtil.isNullAndEmpty(list.get(i)[10]) ? QwyUtil.fmMMddyyyyHHmmss.format(list.get(i)[10]) : "");
            results.add(iced);
        }
        return results;
    }

    /**
     * 根据用户ID获取用户投资记录
     * 
     * @param usersId
     * @return
     */
    public int queryInvestorsByUsersId(String usersId) {
        StringBuffer sql = new StringBuffer(" select count(1) from investors where 1=1 ");
        sql.append(" and usersId ='" + usersId + "' and investor_status not in (0,5) ");
        return (int) dao.getSqlCount(sql.toString(), null);
    }

    public List<Investors> getInvestorsBySql(String sql, Object[] params, String inName, List inList) {
        @SuppressWarnings("unchecked")
        List<Investors> result = dao.LoadAllSql(sql, params, inList, inName, Investors.class);
        return result;

    }

    public List getInvestorsBySqlSecond(String sql, Object[] params) {
        List result = dao.LoadAllSql(sql, params);
        return result;

    }
    public List getListBySql(String sql, Object[] params,List list,String inName) {
       
       return  dao.LoadAllSql(sql, params, list, inName);
    }
}
