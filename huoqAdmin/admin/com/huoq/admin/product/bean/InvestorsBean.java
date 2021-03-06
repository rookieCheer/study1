package com.huoq.admin.product.bean;

import java.text.ParseException;
import java.util.ArrayList;
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
import com.huoq.orm.Product;
import com.huoq.orm.InvestChannelExcelData;

@Service
public class InvestorsBean {
    @Resource
    InvestorsDAO dao;
    private static Logger log = Logger.getLogger(InvestorsBean.class);

    /**
     * 分页获取结算记录
     * 
     * @param pageUtil
     *            分页工具类
     * @param investorStatus
     *            投资状态
     * @param name
     *            用户名
     * @param insertTime
     *            投资时间
     * @param type
     * @param productId
     *            产品ID
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public PageUtil<Investors> findInvertorses(PageUtil<Investors> pageUtil, String productTitle, String investorStatus,
            String name, String insertTime, String type, String productId) throws Exception {
        List<Object> list = new ArrayList<Object>();
        StringBuffer hql = new StringBuffer();
        hql.append(" FROM Investors ins WHERE 1 = 1 ");
        // hql.append(" AND in.users.usersInfo.realName = ?");
        // list.add(realName);
        if (!"all".equals(investorStatus)) {
            hql.append(" AND ins.investorStatus = ?");
            list.add(investorStatus);
        } else {
            hql.append(" AND ins.investorStatus != 5 ");
        }
        if (!QwyUtil.isNullAndEmpty(name)) {
            hql.append(" AND ins.users.username = ? ");
            list.add(DESEncrypt.jiaMiUsername(name));
        }
        // 充值时间
        if (!QwyUtil.isNullAndEmpty(insertTime)) {
            String[] time = QwyUtil.splitTime(insertTime);
            if (time.length > 1) {
                hql.append(" AND ins.payTime >= ? ");
                list.add(QwyUtil.fmMMddyyyy.parse(time[0] + " 00:00:00"));
                hql.append(" AND ins.payTime <= ? ");
                list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
            } else {
                hql.append(" AND ins.payTime >= ? ");
                list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                hql.append(" AND ins.payTime <= ? ");
                list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
            }
        }
        if (!QwyUtil.isNullAndEmpty(type) && type.equals("1")) {
            String[] time = QwyUtil.splitTime(insertTime);
            hql.append(" AND ins.users.insertTime >= ? ");
            list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
            hql.append(" AND ins.users.insertTime <= ? ");
            list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
        }

        if (!QwyUtil.isNullAndEmpty(productId)) {
            hql.append(" AND ins.productId = ? ");
            list.add(productId);
        }
        if (!QwyUtil.isNullAndEmpty(productTitle)) {
            hql.append(" AND ins.product.title like '%" + productTitle + "%' ");
        }
        hql.append(" ORDER BY ins.payTime DESC ");
        return dao.getByHqlAndHqlCount(pageUtil, hql.toString(), hql.toString(), list.toArray());
    }

    /**
     * 以日期分组查询充值记录
     * 
     * @param pageUtil
     *            分页
     * @param insertTime
     *            时间段
     * @return
     * @throws ParseException
     * @throws Exception
     */
    public PageUtil<BackStatsOperateDay> findInverstorsRecordGroupByDate(PageUtil pageUtil, String insertTime)
            throws ParseException {

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
     * @param pageUtil
     *            分页
     * @param insertTime
     *            时间段
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
     * @param insertTime
     *            注册时间
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

    private List<PlatInvestors> toMoney(List<Object[]> list) {
        List<PlatInvestors> platInverstors = new ArrayList<PlatInvestors>();
        if (!QwyUtil.isNullAndEmpty(list)) {
            for (Object[] object : list) {
                PlatInvestors plat = new PlatInvestors();
                plat.setUsername(object[0] == null ? "" : object[0] + "");

                plat.setCopies(!QwyUtil.isNullAndEmpty(object[1]) ? object[1] + "" : "0");
                plat.setInsMoney(!QwyUtil.isNullAndEmpty(object[2]) ? object[2] + "" : "0");
                plat.setCoupon(!QwyUtil.isNullAndEmpty(object[3]) ? object[3] + "" : "0");
                plat.setDate(object[4] + "");
                plat.setReal_name(!QwyUtil.isNullAndEmpty(object[5]) ? object[5] + "" : "");
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

            buff.append(
                    "SELECT	u.username  as username ,	SUM(ins.copies)  as copies ,	SUM(ins.in_money)  as in_money ,");
            buff.append(
                    " SUM(ins.coupon)  as coupon ,ins.pay_time as pay_time , i.real_name AS real_name FROM	investors ins LEFT JOIN users u on ins.users_id = u.id ");
            buff.append("  LEFT JOIN users_info i ON i.users_id = u.id ");

            buff.append(" WHERE	investor_status IN ('1', '2', '3')");

            if (!QwyUtil.isNullAndEmpty(name)) {
                buff.append("AND u.username = ? ");
                // hql.append(" AND ins.product.title like '%"+productTitle+"%'
                // ");
                ob.add(DESEncrypt.jiaMiUsername(name));
            }
            if (!QwyUtil.isNullAndEmpty(realname)) {
                buff.append("AND i.real_name like '%" + realname + "%'");
            }
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    buff.append(" AND ins.pay_time >= ? ");
                    ob.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buff.append(" AND ins.pay_time <= ? ");
                    ob.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
                } else {

                    buff.append(" AND ins.pay_time >= ? ");
                    ob.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buff.append(" AND ins.pay_time <= ? ");
                    ob.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }

            }
            buff.append(" GROUP BY	ins.users_id");
            buff.append(" ORDER BY in_money DESC ");

            StringBuffer bufferCount = new StringBuffer();
            bufferCount.append(" SELECT COUNT(t.username)  ");
            bufferCount.append(" FROM (");
            bufferCount.append(buff);
            bufferCount.append(") t");
            // buff.append("ORDER BY fr.insert_time DESC ");
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
            sql.append(" UNION ALL SELECT COUNT(1) FROM (SELECT users_id FROM investors WHERE DATE_FORMAT('"
                    + targetDate + " " + i
                    + "','%Y-%m-%d %H') = DATE_FORMAT(pay_time,'%Y-%m-%d %H') GROUP BY users_id) tab" + i);
        }
        jsonData.append("{");
        jsonData.append("\"personCount\":" + ListToStringArray(dao.LoadAllSql(sql.toString(), null)));

        // 人次统计
        sql.setLength(0);
        sql.append("SELECT COUNT(1) FROM investors WHERE DATE_FORMAT('" + targetDate
                + " 0','%Y-%m-%d %H') = DATE_FORMAT(pay_time,'%Y-%m-%d %H')");
        for (int i = 1; i < 24; i++) {
            sql.append(" UNION ALL SELECT COUNT(1) FROM investors WHERE DATE_FORMAT('" + targetDate + " " + i
                    + "','%Y-%m-%d %H') = DATE_FORMAT(pay_time,'%Y-%m-%d %H')");
        }
        jsonData.append(",\"personTime\":" + ListToStringArray(dao.LoadAllSql(sql.toString(), null)));

        // 自己投入的金额统计
        sql.setLength(0);
        sql.append(
                "SELECT CASE WHEN SUM(in_money*0.01) IS NULL THEN 0 ELSE SUM(in_money*0.01) END AS sum_ FROM investors WHERE investor_status in ('1','2','3') and DATE_FORMAT('"
                        + targetDate + " 0','%Y-%m-%d %H') = DATE_FORMAT(pay_time,'%Y-%m-%d %H')");
        for (int i = 1; i < 24; i++) {
            sql.append(
                    " UNION ALL SELECT CASE WHEN SUM(in_money*0.01) IS NULL THEN 0 ELSE SUM(in_money*0.01) END AS sum_ FROM investors WHERE DATE_FORMAT('"
                            + targetDate + " " + i + "','%Y-%m-%d %H') = DATE_FORMAT(pay_time,'%Y-%m-%d %H')");
        }
        jsonData.append(",\"inMoney\":" + ListToStringArray(dao.LoadAllSql(sql.toString(), null)));

        // 优惠券金额统计
        sql.setLength(0);
        sql.append(
                "SELECT CASE WHEN SUM(coupon*0.01) IS NULL THEN 0 ELSE SUM(coupon*0.01) END AS sum_ FROM investors WHERE investor_status in ('1','2','3') and DATE_FORMAT('"
                        + targetDate + " 0','%Y-%m-%d %H') = DATE_FORMAT(pay_time,'%Y-%m-%d %H')");
        for (int i = 1; i < 24; i++) {
            sql.append(
                    " UNION ALL SELECT CASE WHEN SUM(coupon*0.01) IS NULL THEN 0 ELSE SUM(coupon*0.01) END AS sum_ FROM investors WHERE DATE_FORMAT('"
                            + targetDate + " " + i + "','%Y-%m-%d %H') = DATE_FORMAT(pay_time,'%Y-%m-%d %H')");
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
    public List<Object[]> investChannelScreen(String channel, String st, String et, int currentPage, int pageSize,
            String order, String registPlatform, String firstInvest, int excel) {
        StringBuffer sb = new StringBuffer();
        sb.append(
                " SELECT * FROM( SELECT * FROM ( SELECT (us.`username`), (SELECT CONCAT(usi.`real_name`,'_',usi.`invest_count`) FROM `users_info` usi WHERE usi.`users_id` = us.`id` )'username,investCount',");
        sb.append(
                "(SELECT pro.`title` FROM `product` pro WHERE pro.`id` IN (iv.`product_id`))'title',(iv.pay_time) 'pay_time',");
        sb.append(
                "iv.copies 'copies', iv.`in_money`*0.01 'in_money',iv.`coupon`*0.01 'coupon',us.province 'province',us.regist_channel 'regist_channel',us.regist_platform,(us.insert_time) 'insert_time', us.id 'id' ");
        sb.append(
                "FROM `users` us LEFT JOIN `investors` iv ON us.`id` =iv.`users_id`  WHERE   iv.`investor_status` IN ('1','2','3')");
        if (!QwyUtil.isNullAndEmpty(channel))
            sb.append("AND us.`regist_channel` ='" + channel + "' ");
        if (!QwyUtil.isNullAndEmpty(registPlatform))
            sb.append("AND us.`regist_platform` ='" + registPlatform + "' ");
        if (!QwyUtil.isNullAndEmpty(order)) {
            sb.append("ORDER BY ");
            sb.append(order);
            sb.append(", (us.regist_channel+0) DESC, us.username DESC");
        } else
            sb.append(" ORDER BY  (us.regist_channel+0) DESC, us.username DESC,iv.pay_time )a ");

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
        if (QwyUtil.isNullAndEmpty(list))
            return null;
        return list;
    }

    /**
     * 查询对应渠道号的投资情况;
     * 
     * @param channel
     *            渠道号
     * @param st
     *            开始时间;
     * @param et
     *            结束时间;
     * @return
     */
    public List<Object[]> investChannel(String channel, String st, String et, int currentPage, int pageSize,
            String order, String registPlatform) {
        StringBuffer sb = new StringBuffer();
        sb.append(
                "SELECT (us.`username`), (SELECT CONCAT(usi.`real_name`,'_',usi.`invest_count`) FROM `users_info` usi WHERE usi.`users_id` = us.`id` )'username,investCount',");
        sb.append(
                "(SELECT pro.`title` FROM `product` pro WHERE pro.`id` IN (iv.`product_id`))'title',(us.insert_time) 'insert_time',(iv.pay_time) 'pay_time',");
        sb.append(
                "iv.copies 'copies', iv.`in_money`*0.01 'in_money',iv.`coupon`*0.01 'coupon',us.province,us.regist_channel ");
        sb.append(
                "FROM `users` us LEFT JOIN `investors` iv ON us.`id` =iv.`users_id`  WHERE   iv.`investor_status` IN ('1','2','3')");
        if (!QwyUtil.isNullAndEmpty(channel))
            sb.append("AND us.`regist_channel` ='" + channel + "' ");
        if (!QwyUtil.isNullAndEmpty(registPlatform))
            sb.append("AND us.`regist_platform` ='" + registPlatform + "' ");

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
        } else
            sb.append("ORDER BY  (us.regist_channel+0) DESC, us.username DESC, iv.pay_time DESC ");
        List<Object[]> list = (List<Object[]>) dao.findAdvListMapSql(sb.toString(), null, currentPage, pageSize);
        ;// dao.LoadAllSql(sb.toString(), null);
        // dao.findAdvListMapSql(sb.toString(), null,1, 11);
        if (QwyUtil.isNullAndEmpty(list))
            return null;
        return list;

    }

    /**
     * 平台渠道统计查询
     * 
     * @param channel
     *            渠道号
     * @param st
     *            开始时间;
     * @param et
     *            结束时间;
     * @return
     */
    public List registPlatform(String registPlatform, String registChannel, String st, String et, int currentPage,
            int pageSize, int investorStatus) {
        StringBuffer sb = new StringBuffer();
        sb.append(
                " SELECT i.pay_time, SUM(i.copies) as copies, SUM(i.in_money) as in_money, SUM(i.coupon ) as coupon ,u.regist_platform , u.regist_channel FROM investors i ");
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
     * 
     */
    public List<Object[]> selectRegistPlatform() {
        StringBuffer sb = new StringBuffer();
        sb.append("SELECT regist_platform FROM `users` GROUP BY regist_platform ORDER BY regist_platform+0 ASC ");
        List<Object[]> list = (List<Object[]>) dao.LoadAllSql(sb.toString(), null);
        if (QwyUtil.isNullAndEmpty(list))
            return null;
        return list;
    }

    /**
     * 查询对应渠道号的投资情况;
     * 
     * @param channel
     *            渠道号
     * @param st
     *            开始时间;
     * @param et
     *            结束时间;
     * @return
     */
    public List<Object[]> findInvestChannel(String channel, String st, String et, int currentPage, int pageSize,
            String order, String registPlatform) {
        StringBuffer sb = new StringBuffer();
        sb.append(
                "SELECT (us.`username`), (SELECT CONCAT(usi.`real_name`,'_',usi.`invest_count`) FROM `users_info` usi WHERE usi.`users_id` = us.`id` )'username,investCount',");
        sb.append(
                "(SELECT pro.`title` FROM `product` pro WHERE pro.`id` IN (iv.`product_id`))'title',(iv.pay_time) 'pay_time',");
        sb.append(
                "iv.copies 'copies', iv.`in_money`*0.01 'in_money',iv.`coupon`*0.01 'coupon',us.province,us.regist_channel, us.regist_platform,(us.insert_time) 'insert_time' ");
        sb.append(
                "FROM `users` us LEFT JOIN `investors` iv ON us.`id` =iv.`users_id`  WHERE   iv.`investor_status` IN ('1','2','3')");
        if (!QwyUtil.isNullAndEmpty(channel))
            sb.append("AND us.`regist_channel` ='" + channel + "' ");
        if (!QwyUtil.isNullAndEmpty(registPlatform))
            sb.append("AND us.`regist_platform` ='" + registPlatform + "' ");

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
        } else
            sb.append("ORDER BY  (us.regist_channel+0) DESC, us.username DESC, iv.pay_time DESC ");
        // List<Object[]> list =
        // (List<Object[]>)dao.findAdvListSql(sb.toString(),null,currentPage,pageSize);
        List<Object[]> list = dao.LoadAllSql(sb.toString(), null);
        if (QwyUtil.isNullAndEmpty(list))
            return null;
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
            iced.setUsername(
                    !QwyUtil.isNullAndEmpty(list.get(i)[0]) ? DESEncrypt.jieMiUsername(list.get(i)[0].toString()) : "");
            iced.setNameInvestCount(!QwyUtil.isNullAndEmpty(list.get(i)[1]) ? list.get(i)[1].toString() : "");
            iced.setProduct(!QwyUtil.isNullAndEmpty(list.get(i)[2]) ? list.get(i)[2].toString() : "");
            iced.setPayTime(
                    !QwyUtil.isNullAndEmpty(list.get(i)[3]) ? QwyUtil.fmMMddyyyyHHmmss.format(list.get(i)[3]) : "");
            iced.setTotalInvestment(!QwyUtil.isNullAndEmpty(list.get(i)[4]) ? list.get(i)[4].toString() : "0");
            iced.setCapital(!QwyUtil.isNullAndEmpty(list.get(i)[5]) ? list.get(i)[5].toString() : "0");
            iced.setCouponMoney(!QwyUtil.isNullAndEmpty(list.get(i)[6]) ? list.get(i)[6].toString() : "0");
            iced.setProvince(!QwyUtil.isNullAndEmpty(list.get(i)[7]) ? list.get(i)[7].toString() : "");
            iced.setChannelNo(!QwyUtil.isNullAndEmpty(list.get(i)[8]) ? list.get(i)[8].toString() : "");
            iced.setRegistPlatform(!QwyUtil.isNullAndEmpty(list.get(i)[9]) ? list.get(i)[9].toString() : "");
            iced.setInsertTime(
                    !QwyUtil.isNullAndEmpty(list.get(i)[10]) ? QwyUtil.fmMMddyyyyHHmmss.format(list.get(i)[10]) : "");
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
        @SuppressWarnings("unchecked")
        List result = dao.LoadAllSql(sql, params);
        return result;

    }
}