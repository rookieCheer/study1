package com.huoq.account.bean;

import java.lang.management.BufferPoolMXBean;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.huoq.orm.*;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.account.dao.UserInfoDAO;
import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;

/**
 * 用户信息Bean层
 *
 * @author qwy
 * @createTime 2015-4-20下午1:02:35
 */
@Service
public class UserInfoBean {

    private static Logger log = Logger.getLogger(UserInfoBean.class);
    @Resource
    private UserInfoDAO dao;

    /**
     * 根据用户id查找用户
     *
     * @param id 用户id;
     * @return
     */
    public UsersInfo getUserInfoById(long id) {
        StringBuffer buff = new StringBuffer();
        buff.append("FROM UsersInfo userInfo ");
        buff.append("WHERE userInfo.usersId= ? ");
        UsersInfo userInfo = (UsersInfo) dao.findJoinActive(buff.toString(), new Object[]{id});
        return userInfo;
    }

    /**
     * 根据修改用户资料
     */
    public UsersInfo saveOrUpdateUserInfo(UsersInfo userInfo) {
        // userInfoDao.update(userInfo);
        dao.saveOrUpdate(userInfo);
        return userInfo;
    }

    /**
     * 判断邮箱是否被绑定
     */
    public UsersInfo isEmailband(String email) {
        Object ob = null;
        try {
            if (QwyUtil.isNullAndEmpty(email))
                return null;
            StringBuffer hql = new StringBuffer();
            hql.append("FROM UsersInfo ui ");
            hql.append("WHERE ui.email = ?");
            ob = dao.findJoinActive(hql.toString(), new Object[]{email});
            if (ob != null) {
                return (UsersInfo) ob;
            }
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 根据用户id查找用户
     *
     * @param id 用户id;
     * @return
     */
    public Users getUserById(long id) {
        StringBuffer buff = new StringBuffer();
        buff.append("FROM Users user ");
        buff.append("WHERE user.id= ? ");
        Users users = (Users) dao.findJoinActive(buff.toString(), new Object[]{id});
        return users;
    }

    /**
     * 统计平台注册人数
     *
     * @param insertTime 注册时间
     * @return
     */
    public List<UsersStat> findUsersCount(String insertTime) throws Exception {
        List<Object> arrayList = new ArrayList<Object>();
        StringBuffer sql = new StringBuffer();
        sql.append(" SELECT us.regist_platform,COUNT(DISTINCT us.id) FROM Users as us ");
        sql.append(" WHERE us.regist_platform is not NULL ");
        // 发布时间
        if (!QwyUtil.isNullAndEmpty(insertTime)) {
            String[] time = QwyUtil.splitTime(insertTime);
            if (time.length > 1) {
                sql.append(" AND us.insert_time >= ? ");
                arrayList.add(QwyUtil.fmMMddyyyy.parse(time[0]));
                sql.append(" AND us.insert_time <= ? ");
                arrayList.add(QwyUtil.fmMMddyyyy.parse(time[1]));
            } else {
                sql.append(" AND us.insert_time >= ? ");
                arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                sql.append(" AND us.insert_time <= ? ");
                arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
            }
        }
        sql.append(" GROUP BY us.regist_platform ");
        List<Object[]> list = dao.LoadAllSql(sql.toString(), arrayList.toArray());
        return parseUsersStat(list);
    }

    /**
     * 统计平台注册总人数
     *
     * @param insertTime 注册时间
     * @return 总注册人数
     */
    public String findAllUsersCount(String insertTime) throws Exception {
        List<Object> arrayList = new ArrayList<Object>();
        StringBuffer sql = new StringBuffer();
        sql.append(" SELECT COUNT(DISTINCT us.id) FROM Users as us ");
        sql.append(" WHERE us.regist_platform is not NULL ");
        // 发布时间
        if (!QwyUtil.isNullAndEmpty(insertTime)) {
            String[] time = QwyUtil.splitTime(insertTime);
            if (time.length > 1) {
                sql.append(" AND us.insert_time >= ? ");
                arrayList.add(QwyUtil.fmMMddyyyy.parse(time[0]));
                sql.append(" AND us.insert_time <= ? ");
                arrayList.add(QwyUtil.fmMMddyyyy.parse(time[1]));
            } else {
                sql.append(" AND us.insert_time >= ? ");
                arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                sql.append(" AND us.insert_time <= ? ");
                arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
            }
        }
        Object object = dao.getSqlCount(sql.toString(), arrayList.toArray());
        if (!QwyUtil.isNullAndEmpty(object)) {
            return object + "";
        }
        return "0";
    }

    /**
     * 将数据转换为UsersStat
     *
     * @param list
     * @return
     */
    public List<UsersStat> parseUsersStat(List<Object[]> list) throws Exception {
        List<UsersStat> usersStats = new ArrayList<UsersStat>();
        if (!QwyUtil.isNullAndEmpty(list)) {
            for (Object[] object : list) {
                UsersStat usersStat = new UsersStat();
                usersStat.setRegistPlatorm(object[0] + "");
                usersStat.setRegistPlatormCount(object[1] + "");
                usersStats.add(usersStat);
            }
        }
        return usersStats;
    }

    /**
     * 根据日期统计用户注册人数
     *
     * @param insertTime 注册时间
     * @return
     */
    public PageUtil<PlatUser> findUsersCountByDate(PageUtil pageUtil, String insertTime) throws Exception {
        ArrayList<Object> list = new ArrayList<Object>();
        StringBuffer buffer = new StringBuffer();
        buffer.append(" SELECT DATE_FORMAT( dd.insert_time, '%Y-%m-%d' ) as date ,    ");
        buffer.append(" (  ");
        buffer.append(" SELECT COUNT(*) as userscount FROM  users  us  ");
        buffer.append(" WHERE us.regist_platform is not NULL ");
        buffer.append(" AND DATE_FORMAT( dd.insert_time, '%Y-%m-%d' ) = DATE_FORMAT( us.insert_time, '%Y-%m-%d' ) ");
        buffer.append(" ) as 'userscount'  ");
        buffer.append(" FROM dateday dd ");
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
                platUser.setUserscount(object[1] + "");
                platUsers.add(platUser);
            }
        }
        return platUsers;
    }

    /**
     * 根据渠道去获取用户信息
     *
     * @param channel    入口渠道
     * @param pageUtil
     * @param username   用户名
     * @param insertTime 插入时间
     * @param isbindbank 是否绑定银行卡
     * @throws Exception
     */
    @SuppressWarnings("unchecked")

    public PageUtil<UserInfoList> findUsersByChannel(PageUtil pageUtil, String channel, String username,
                                                     String insertTime, String acinsertTime, String isbindbank, String islqg, String level, String inMoney1, String inMoney2) {
        //是否活期投资
        try {
            List<Object> list = new ArrayList<Object>();
            StringBuffer buff = new StringBuffer();
            buff.append(" SELECT a.id ,	a.username ,a.province ,");
            buff.append(" a.city,a.card_type ,IFNULL(a.insert_time,'') , IFNULL(ac.insert_time,''), ");
            buff.append(" a.regist_platform ,b.real_name ,	b.sex ,");
            buff.append(" b.age ,b.birthday,b.level as level,");
            buff.append(" b.is_bind_bank ,t.in_money,lqg.money/100 ,a.regist_channel,t.pay_time,t.title,t.in_money1 ");
            buff.append(" FROM users a JOIN users_info b ON a.id = b.users_id left join");
            buff.append(" (select SUM(i.in_money/100) as in_money , i.users_id as users_id,p.title AS title,i.pay_time AS pay_time,i.in_money AS in_money1  from investors i  "
                    + "LEFT JOIN product p ON i.product_id =p.id  ");
            buff.append(" where 1=1 and i.investor_status in ('1','2','3')  GROUP BY i.pay_time Asc)t ");
            buff.append("  on t.users_id = b.users_id ");
            buff.append("  LEFT JOIN account ac ON ac.users_id = a.id  AND ac.STATUS = 1  ");
            buff.append(" LEFT JOIN ( ");
            buff.append(" SELECT cpfr.users_id 'users_id',SUM(cpfr.money) 'money', cpfr.insert_time 'insert_time' ");
            buff.append(" FROM coin_purse_funds_record cpfr WHERE cpfr.type = 'to'  AND  cpfr.status = 0  ");
            buff.append(" GROUP BY cpfr.users_id )lqg ");
            buff.append(" ON lqg.users_id = a.id WHERE 1=1 ");
            if (!QwyUtil.isNullAndEmpty(channel)) {
                if (!channel.contains("微信")) {
                    buff.append(" AND a.regist_channel = ? ");
                    list.add(channel);
                }
                if (channel.contains("微信")) {
                    buff.append(" AND a.regist_platform = 3 AND regist_channel = '' ");
                }
            }
            if (!QwyUtil.isNullAndEmpty(username)) {
                buff.append(" AND (");
                buff.append("  a.username = ? ");
                list.add(DESEncrypt.jiaMiUsername(username));
                buff.append(" OR a.id = '" + username + "' ");
                buff.append(" OR b.real_name = ? ");
                list.add(username);
                buff.append(" )");
            }
            if (!QwyUtil.isNullAndEmpty(isbindbank)) {
                buff.append(" AND b.is_bind_bank = ? ");
                list.add(isbindbank);
            }

            // 发布时间
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    buff.append(" AND a.insert_time >= ? ");
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buff.append(" AND a.insert_time <= ? ");
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
                } else {
                    buff.append(" AND a.insert_time >= ? ");
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buff.append(" AND a.insert_time <= ? ");
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            }

            // 发布时间
            if (!QwyUtil.isNullAndEmpty(acinsertTime)) {
                String[] time = QwyUtil.splitTime(acinsertTime);
                if (time.length > 1) {
                    buff.append(" AND ac.insert_time >= ? ");
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buff.append(" AND ac.insert_time <= ? ");
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
                } else {
                    buff.append(" AND ac.insert_time >= ? ");
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buff.append(" AND ac.insert_time <= ? ");
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            }


            if (!QwyUtil.isNullAndEmpty(level)) {
                buff.append(" AND b.level = ? ");
                list.add(level);
            }

            if (!QwyUtil.isNullAndEmpty(inMoney1) && !QwyUtil.isNullAndEmpty(inMoney2)) {
                buff.append(" AND  t.in_money >= ? ");
                list.add(Double.parseDouble(inMoney1) * 10000);
                buff.append(" AND  t.in_money <= ? ");
                list.add(Double.parseDouble(inMoney2) * 10000);
            } else if (!QwyUtil.isNullAndEmpty(inMoney1)) {
                buff.append(" AND  t.in_money >= ? ");
                list.add(Double.parseDouble(inMoney1) * 10000);
            } else if (!QwyUtil.isNullAndEmpty(inMoney2)) {
                buff.append(" AND  t.in_money <= ? ");
                list.add(Double.parseDouble(inMoney2) * 10000);
            }
            if (!QwyUtil.isNullAndEmpty(islqg)) {
                if (islqg.equals("1")) {
                    buff.append("AND a.id NOT IN (SELECT u.id FROM investors i LEFT JOIN users u ON u.id = i.users_id ");
                    buff.append("WHERE i.investor_status IN ('1','2','3') GROUP BY u.id) AND lqg.money IS NOT NULL ");
                }
                if (islqg.equals("0")) {
                    buff.append("AND a.id NOT IN (SELECT u.id FROM coin_purse_funds_record cpf LEFT JOIN users u ON u.id = cpf.users_id ");
                    buff.append("WHERE cpf.TYPE ='to' AND u.id IS NOT NULL GROUP BY u.id) AND t.in_money IS NOT NULL ");
                }
            }
            buff.append(" ORDER BY t.in_money DESC ");

            StringBuffer bufferCount = new StringBuffer();
            bufferCount.append(" SELECT COUNT(*)  ");
            bufferCount.append(" FROM (");
            bufferCount.append(buff);
            bufferCount.append(") t");
            pageUtil = dao.getBySqlAndSqlCount(pageUtil, buff.toString(), bufferCount.toString(), list.toArray());
            List<UserInfoList> platUsers = toDateMoney(pageUtil.getList());
            pageUtil.setList(platUsers);
            return pageUtil;
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    private List<UserInfoList> toDateMoney(List<Object[]> list) throws ParseException {
        List<UserInfoList> meowPay = new ArrayList<UserInfoList>();
        Double money = 0.0;
        if (!QwyUtil.isNullAndEmpty(list)) {
            for (Object[] object : list) {
                UserInfoList plat = new UserInfoList();
                plat.setId(object[0] == null ? "" : object[0] + "");
                plat.setUsername(object[1] == null ? "" : object[1] + "");
                plat.setProvince(object[2] == null ? "" : object[2] + "");
                plat.setCity(object[3] == null ? "" : object[3] + "");
                plat.setCardType(object[4] == null ? "" : object[4] + "");
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                plat.setInsertTime(sd.parse(object[5] + ""));
                plat.setAcinsertTime(!QwyUtil.isNullAndEmpty(object[6]) ? sd.parse(object[6] + "") : null);
                plat.setRegistPlatform(object[7] == null ? "" : object[7] + "");
                plat.setRealName(object[8] == null ? "" : object[8] + "");
                plat.setSex(object[9] == null ? "" : object[9] + "");
                plat.setAge(object[10] == null ? "" : object[10] + "");
                plat.setBirthday(object[11] == null ? ""
                        : object[11].toString().trim().substring(object[11].toString().indexOf("-") + 1, 10) + "");
                if (object[14] == null) {
                    plat.setLevel("0");
                } else {
                    money = Double.parseDouble(object[14] + "");
                    plat.setLevel(userLevel(money / 10000));
                }
                plat.setIsBindBank(object[13] == null ? "" : object[13] + "");
                plat.setInMoney(object[14] == null ? "" : object[14] + "");
                plat.setCoinPurseFundsRecordMoney(object[15] == null ? "" : object[15] + "");
                plat.setRegistChannel(object[16] == null ? "" : object[16] + "");
                plat.setPayTime(!QwyUtil.isNullAndEmpty(object[17]) ? sd.parse(object[17] + "") : null);
                plat.setTitle(object[18] == null ? "" : object[18] + "");
                plat.setInMoney1(object[19] == null ? "" : object[19] + "");
                meowPay.add(plat);

            }
        }
        return meowPay;
    }

    public String userLevel(double money) {
        if (money > 5 && money <= 10)
            return "1";
        else if (money > 10 && money <= 50)
            return "2";
        else if (money > 50 && money <= 150)
            return "3";
        else if (money > 150 && money <= 300)
            return "4";
        else if (money > 300 && money <= 500)
            return "5";
        else if (money > 500)
            return "6";
        else
            return "0";

    }
    // @SuppressWarnings("unchecked")
    // public PageUtil<Users> findUsersByChannel(PageUtil<Users> pageUtil, String
    // channel, String username,
    // String insertTime, String isbindbank) {
    // try {
    // List<Object> list = new ArrayList<Object>();
    // StringBuffer buffer = new StringBuffer();
    // buffer.append(" FROM Users us");
    // buffer.append(" WHERE 1=1");
    // if (!QwyUtil.isNullAndEmpty(channel)) {
    // // if(channel.equals("0")){
    // // /*buffer.append(" AND ( us.registChannel = 0 ");
    // // buffer.append(" OR us.registChannel = '' ");
    // // buffer.append(" OR us.registChannel is null ) ");*/
    // // }else{
    // buffer.append(" AND us.registChannel = ? ");
    // list.add(channel);
    // // }
    // }
    // if (!QwyUtil.isNullAndEmpty(username)) {
    // buffer.append(" AND (");
    // buffer.append(" us.username = ? ");
    // list.add(DESEncrypt.jiaMiUsername(username));
    // buffer.append(" OR us.id = '" + username + "' ");
    // buffer.append(" OR us.usersInfo.realName = ? ");
    // list.add(username);
    // buffer.append(" )");
    // }
    // // 发布时间
    // if (!QwyUtil.isNullAndEmpty(insertTime)) {
    // String[] time = QwyUtil.splitTime(insertTime);
    // if (time.length > 1) {
    // buffer.append(" AND us.insertTime >= ? ");
    // list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
    // buffer.append(" AND us.insertTime <= ? ");
    // list.add(QwyUtil.fmMMddyyyy.parse(time[1]));
    // } else {
    // buffer.append(" AND us.insertTime >= ? ");
    // list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
    // buffer.append(" AND us.insertTime <= ? ");
    // list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
    // }
    // }
    // if (!QwyUtil.isNullAndEmpty(isbindbank)) {
    // buffer.append(" AND us.usersInfo.isBindBank = ? ");
    // list.add(isbindbank);
    // }
    // buffer.append(" ORDER BY us.insertTime DESC ");
    // return dao.getByHqlAndHqlCount(pageUtil, buffer.toString(),
    // buffer.toString(), list.toArray());
    // }

    /**
     * 统计各省份人数
     *
     * @param pageUtil
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public PageUtil<Region> loadProvince(PageUtil pageUtil, String insertTime) throws Exception {
        List<Object> regionList = new ArrayList<Object>();
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT province,COUNT(province) FROM  users ");
            sql.append("where 1=1 ");
            if (!QwyUtil.isNullAndEmpty(insertTime)) { // 按日期查询
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    sql.append(" AND insert_time >= ? ");
                    regionList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    sql.append(" AND insert_time <= ? ");
                    regionList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
                } else {
                    sql.append(" AND insert_time >= ? ");
                    regionList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    sql.append(" AND insert_time <= ? ");
                    regionList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            }

            /*if (!QwyUtil.isNullAndEmpty(insertTime)) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length >1) {
                    sql.append(" AND u.insert_time  BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') ");
                    regionList.add(QwyUtil.fmMMddyyyy.parse(time[0]));
                    regionList.add(QwyUtil.fmMMddyyyy.parse(time[1]));
                }else{
                    sql.append(" AND u.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') ");
                    Date parse = QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00");
                    String format = simpleDateFormat.format(parse);
                    Date parse1 = QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59");
                    String format1 = simpleDateFormat.format(parse1);
                    regionList.add(format);
                    regionList.add(format1);
                }
            }*/
            sql.append("GROUP BY province HAVING province IS NOT NULL ORDER BY COUNT(province) DESC  ");
            StringBuffer sqlCount = new StringBuffer();
            sqlCount.append("SELECT COUNT(t.province) FROM ( ");
            sqlCount.append(sql);
            sqlCount.append(") t ");
            PageUtil bySqlAndSqlCount = dao.getBySqlAndSqlCount(pageUtil, sql.toString(), sqlCount.toString(), regionList.toArray());
            List<Object[]> objlist = bySqlAndSqlCount.getList();
            List<Region> regions = toProvince(objlist);
            bySqlAndSqlCount.setList(regions);
            return bySqlAndSqlCount;

        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;

    }

    public List<Region> toProvince(List<Object[]> objects) {
        List<Region> list = new ArrayList<>();
        try {
            for (Object[] obj : objects) {
                Region region = new Region();
                region.setProvince(obj[0] + "");
                region.setUsersCount(obj[1] + "");
                list.add(region);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 统计省份下属各城市人数
     *
     * @param pageUtil
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public PageUtil<Region> loadCity(String province, PageUtil pageUtil) throws Exception {
        try {
            List<Region> regionList = new ArrayList<Region>();
            String hql = "select city,COUNT(city) from users where province = '" + province
                    + "' GROUP BY city HAVING city IS NOT NULL ORDER BY COUNT(city) DESC ";
            StringBuffer buffer = new StringBuffer();
            buffer.append("  SELECT COUNT(t.city) FROM (");
            buffer.append(hql);
            buffer.append(") t ");
            PageUtil<Object[]> page = dao.getBySqlAndSqlCount(pageUtil, hql, buffer.toString(), null);
            for (int i = 0; i < page.getList().size(); i++) {
                Object[] objects = page.getList().get(i);
                Region region = new Region();
                region.setCity(objects[0] + "");
                region.setUsersCount(objects[1] + "");
                regionList.add(region);
            }
            pageUtil.setList(regionList);
            return pageUtil;
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;

    }

    /**
     * 统计异常省份或城市的人数
     *
     * @return
     * @throws Exception
     */
    public String getOthsers(String province, String city) throws Exception {
        try {
            String count;
            StringBuffer buffer = new StringBuffer();
            buffer.append("  SELECT count(*) FROM users where 1=1");
            if (!QwyUtil.isNullAndEmpty(province)) {
                buffer.append(" and province= '" + province + "'");
            } else if (QwyUtil.isNullAndEmpty(province)) {
                buffer.append(" and province is null");
            }
            if (!QwyUtil.isNullAndEmpty(city)) {
                buffer.append(" and city= '" + city + "'");
            }

            count = String.valueOf(dao.getSqlCount(buffer.toString(), null));
            return count;
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;

    }

    public List<Age> loadSex(String insertTime) throws Exception {
        List<Object> sexList = new ArrayList<Object>();
        try {
            StringBuffer buffer = new StringBuffer();
            buffer.append("SELECT ");
            buffer.append("(SELECT COUNT(*) FROM users_info u WHERE sex IN('男','女')  ");
            if (!QwyUtil.isNullAndEmpty(insertTime)) { // 按日期查询
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    buffer.append(" AND u.insert_time >= ? ");
                    sexList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    sexList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
                } else {
                    buffer.append(" AND u.insert_time >= ? ");
                    sexList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    sexList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            }
            buffer.append(" ),");
            buffer.append(
                    "(SELECT SUM(in_money) FROM investors i LEFT JOIN users_info  ui ON ui.users_id=i.users_id WHERE sex IN('男','女') AND i.investor_status BETWEEN 1 AND 3),  ");
            buffer.append(
                    "(SELECT COUNT(*) FROM investors i LEFT JOIN users_info  ui ON ui.users_id=i.users_id WHERE sex IN('男','女') AND i.investor_status BETWEEN 1 AND 3) ");
            buffer.append(" UNION ALL  ");
            buffer.append("SELECT ");
            buffer.append("(SELECT COUNT(*) FROM users_info u WHERE sex='男'  ");
            if (!QwyUtil.isNullAndEmpty(insertTime)) { // 按日期查询
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    buffer.append(" AND u.insert_time >= ? ");
                    sexList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    sexList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
                } else {
                    buffer.append(" AND u.insert_time >= ? ");
                    sexList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    sexList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            }
            buffer.append(" ),");
            buffer.append(
                    "(SELECT SUM(in_money) FROM investors i LEFT JOIN users_info  ui ON ui.users_id=i.users_id WHERE sex='男' AND i.investor_status BETWEEN 1 AND 3),  ");
            buffer.append(
                    "(SELECT COUNT(*) FROM investors i LEFT JOIN users_info  ui ON ui.users_id=i.users_id WHERE sex='男'AND i.investor_status BETWEEN 1 AND 3) ");
            buffer.append(" UNION ALL  ");
            buffer.append("SELECT ");
            buffer.append("(SELECT COUNT(*) FROM users_info u WHERE sex='女'  ");
            if (!QwyUtil.isNullAndEmpty(insertTime)) { // 按日期查询
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    buffer.append(" AND u.insert_time >= ? ");
                    sexList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    sexList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
                } else {
                    buffer.append(" AND u.insert_time >= ? ");
                    sexList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    sexList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            }
            buffer.append(" ),");
            buffer.append(
                    "(SELECT SUM(in_money) FROM investors i LEFT JOIN users_info  ui ON ui.users_id=i.users_id WHERE sex='女' AND i.investor_status BETWEEN 1 AND 3), ");
            buffer.append(
                    "(SELECT COUNT(*) FROM investors i LEFT JOIN users_info  ui ON ui.users_id=i.users_id WHERE sex='女' AND i.investor_status BETWEEN 1 AND 3)  ");


            List<Object[]> list = dao.LoadAllSql(buffer.toString(), sexList.toArray());
            List<Age> ageList = new ArrayList<Age>();
            String titleCount = "";
            if (list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    Object[] objects = list.get(i);
                    Age age = new Age();
                    age.setRsCount(objects[0] + "");
                    if (i == 0) {
                        age.setSexChina("所有");
                        age.setRate(1f);
                        titleCount = age.getRsCount();
                    } else if (i == 1) {
                        age.setSexChina("男");
                        float maleRate = Float.valueOf(age.getRsCount()).floatValue()
                                / Float.valueOf(titleCount).floatValue();
                        age.setRate(maleRate);
                    } else if (i == 2) {
                        age.setSexChina("女");
                        float femaleRate = Float.valueOf(age.getRsCount()).floatValue()
                                / Float.valueOf(titleCount).floatValue();
                        age.setRate(femaleRate);
                    }
                    if (QwyUtil.isNullAndEmpty(objects[1])) {
                        objects[1] = 0;
                    }
                    age.setJeCount(objects[1] + "");
                    age.setCsCount(objects[2] + "");
                    ageList.add(age);
                }
            }
            return ageList;
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 获取用户年龄分布信息
     *
     * @param registPlatform
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public List<Age> loadAge(String registPlatform,String insertTime) throws Exception {
        List<Object> ageList1 = new ArrayList<Object>();
        try {
            StringBuffer buffer = new StringBuffer();
            buffer.append("select ");
            // 人数
            buffer.append("(SELECT COUNT(*) FROM users_info ui LEFT JOIN users u on ui.users_id=u.id  ");
            buffer.append("  WHERE age>0 ");
            if (!QwyUtil.isNullAndEmpty(registPlatform) && !"all".equals(registPlatform)) {
                buffer.append(" AND u.regist_platform='" + registPlatform + "'");
            }
            if (!QwyUtil.isNullAndEmpty(insertTime)) { // 按日期查询
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    buffer.append(" AND u.insert_time >= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
                } else {
                    buffer.append(" AND u.insert_time >= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            }
            buffer.append("),");
            // 投资总额
            buffer.append(
                    "(SELECT SUM(in_money) FROM investors i , users_info  ui LEFT JOIN users u ON ui.users_id=u.id ");
            buffer.append(" WHERE ui.users_id=i.users_id AND age>0 AND i.investor_status BETWEEN 1 AND 3 ");
            if (!QwyUtil.isNullAndEmpty(registPlatform) && !"all".equals(registPlatform)) {
                buffer.append(" AND u.regist_platform='" + registPlatform + "'");
            }
            if (!QwyUtil.isNullAndEmpty(insertTime)) { // 按日期查询
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    buffer.append(" AND u.insert_time >= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
                } else {
                    buffer.append(" AND u.insert_time >= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            }
            buffer.append("),");
            // 投资总数
            buffer.append("(SELECT COUNT(*) FROM investors i , users_info  ui LEFT JOIN users u ON ui.users_id=u.id ");
            buffer.append(" WHERE ui.users_id=i.users_id AND age>0 AND i.investor_status BETWEEN 1 AND 3 ");
            if (!QwyUtil.isNullAndEmpty(registPlatform) && !"all".equals(registPlatform)) {
                buffer.append(" AND u.regist_platform='" + registPlatform + "'");
            }
            if (!QwyUtil.isNullAndEmpty(insertTime)) { // 按日期查询
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    buffer.append(" AND u.insert_time >= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
                } else {
                    buffer.append(" AND u.insert_time >= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            }
            buffer.append(")");
            buffer.append(" UNION ALL ");
            buffer.append("select ");
            // [1,20)，人数
            buffer.append("(SELECT COUNT(*) FROM users_info ui LEFT JOIN users u on ui.users_id=u.id  ");
            buffer.append("  WHERE age>0 AND age<20");
            if (!QwyUtil.isNullAndEmpty(registPlatform) && !"all".equals(registPlatform)) {
                buffer.append(" AND u.regist_platform='" + registPlatform + "'");
            }
            if (!QwyUtil.isNullAndEmpty(insertTime)) { // 按日期查询
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    buffer.append(" AND u.insert_time >= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
                } else {
                    buffer.append(" AND u.insert_time >= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            }
            buffer.append("),");
            // [1,20)，投资总额
            buffer.append(
                    "(SELECT SUM(in_money) FROM investors i , users_info  ui LEFT JOIN users u ON ui.users_id=u.id ");
            buffer.append(" WHERE ui.users_id=i.users_id AND age>0 AND age<20 AND i.investor_status BETWEEN 1 AND 3");
            if (!QwyUtil.isNullAndEmpty(registPlatform) && !"all".equals(registPlatform)) {
                buffer.append(" AND u.regist_platform='" + registPlatform + "' ");
            }
            if (!QwyUtil.isNullAndEmpty(insertTime)) { // 按日期查询
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    buffer.append(" AND u.insert_time >= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
                } else {
                    buffer.append(" AND u.insert_time >= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            }
            buffer.append("),");
            // [1,20)，投资总数
            buffer.append("(SELECT COUNT(*) FROM investors i , users_info  ui LEFT JOIN users u ON ui.users_id=u.id ");
            buffer.append(" WHERE ui.users_id=i.users_id AND age>0 AND age<20 AND i.investor_status BETWEEN 1 AND 3 ");
            if (!QwyUtil.isNullAndEmpty(registPlatform) && !"all".equals(registPlatform)) {
                buffer.append(" AND u.regist_platform='" + registPlatform + "'");
            }
            if (!QwyUtil.isNullAndEmpty(insertTime)) { // 按日期查询
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    buffer.append(" AND u.insert_time >= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
                } else {
                    buffer.append(" AND u.insert_time >= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            }
            buffer.append(")");
            buffer.append(" UNION ALL ");
            buffer.append("select ");
            // [20,30)，人数
            buffer.append("(SELECT COUNT(*) FROM users_info ui LEFT JOIN users u on ui.users_id=u.id  ");
            buffer.append("  WHERE age>19 AND age<30");
            if (!QwyUtil.isNullAndEmpty(registPlatform) && !"all".equals(registPlatform)) {
                buffer.append(" AND u.regist_platform='" + registPlatform + "'");
            }
            if (!QwyUtil.isNullAndEmpty(insertTime)) { // 按日期查询
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    buffer.append(" AND u.insert_time >= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
                } else {
                    buffer.append(" AND u.insert_time >= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            }
            buffer.append("),");
            // [20,30)，投资总额
            buffer.append(
                    "(SELECT SUM(in_money) FROM investors i , users_info  ui LEFT JOIN users u ON ui.users_id=u.id ");
            buffer.append(" WHERE ui.users_id=i.users_id AND age>19 AND age<30 AND i.investor_status BETWEEN 1 AND 3");
            if (!QwyUtil.isNullAndEmpty(registPlatform) && !"all".equals(registPlatform)) {
                buffer.append(" AND u.regist_platform='" + registPlatform + "' ");
            }
            if (!QwyUtil.isNullAndEmpty(insertTime)) { // 按日期查询
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    buffer.append(" AND u.insert_time >= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
                } else {
                    buffer.append(" AND u.insert_time >= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            }
            buffer.append("),");
            // [20,30)，投资总数
            buffer.append("(SELECT COUNT(*) FROM investors i , users_info  ui LEFT JOIN users u ON ui.users_id=u.id ");
            buffer.append(" WHERE ui.users_id=i.users_id AND age>19 AND age<30 AND i.investor_status BETWEEN 1 AND 3 ");
            if (!QwyUtil.isNullAndEmpty(registPlatform) && !"all".equals(registPlatform)) {
                buffer.append(" AND u.regist_platform='" + registPlatform + "'");
            }
            if (!QwyUtil.isNullAndEmpty(insertTime)) { // 按日期查询
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    buffer.append(" AND u.insert_time >= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
                } else {
                    buffer.append(" AND u.insert_time >= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            }
            buffer.append(")");
            buffer.append(" UNION ALL ");
            buffer.append("select ");
            // [30,40)，人数
            buffer.append("(SELECT COUNT(*) FROM users_info ui LEFT JOIN users u on ui.users_id=u.id  ");
            buffer.append("  WHERE age>29 AND age<40");
            if (!QwyUtil.isNullAndEmpty(registPlatform) && !"all".equals(registPlatform)) {
                buffer.append(" AND u.regist_platform='" + registPlatform + "'");
            }
            if (!QwyUtil.isNullAndEmpty(insertTime)) { // 按日期查询
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    buffer.append(" AND u.insert_time >= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
                } else {
                    buffer.append(" AND u.insert_time >= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            }
            buffer.append("),");
            // [30,40)，投资总额
            buffer.append(
                    "(SELECT SUM(in_money) FROM investors i , users_info  ui LEFT JOIN users u ON ui.users_id=u.id ");
            buffer.append(" WHERE ui.users_id=i.users_id AND age>29 AND age<40 AND i.investor_status BETWEEN 1 AND 3");
            if (!QwyUtil.isNullAndEmpty(registPlatform) && !"all".equals(registPlatform)) {
                buffer.append(" AND u.regist_platform='" + registPlatform + "' ");
            }
            if (!QwyUtil.isNullAndEmpty(insertTime)) { // 按日期查询
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    buffer.append(" AND u.insert_time >= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
                } else {
                    buffer.append(" AND u.insert_time >= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            }
            buffer.append("),");
            // [30,40)，投资总数
            buffer.append("(SELECT COUNT(*) FROM investors i , users_info  ui LEFT JOIN users u ON ui.users_id=u.id ");
            buffer.append(" WHERE ui.users_id=i.users_id AND age>29 AND age<40 AND i.investor_status BETWEEN 1 AND 3 ");
            if (!QwyUtil.isNullAndEmpty(registPlatform) && !"all".equals(registPlatform)) {
                buffer.append(" AND u.regist_platform='" + registPlatform + "'");
            }
            if (!QwyUtil.isNullAndEmpty(insertTime)) { // 按日期查询
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    buffer.append(" AND u.insert_time >= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
                } else {
                    buffer.append(" AND u.insert_time >= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            }
            buffer.append(")");
            buffer.append(" UNION ALL ");
            buffer.append("select ");
            // [40,50)，人数
            buffer.append("(SELECT COUNT(*) FROM users_info ui LEFT JOIN users u on ui.users_id=u.id  ");
            buffer.append("  WHERE age>39 AND age<50");
            if (!QwyUtil.isNullAndEmpty(registPlatform) && !"all".equals(registPlatform)) {
                buffer.append(" AND u.regist_platform='" + registPlatform + "'");
            }
            if (!QwyUtil.isNullAndEmpty(insertTime)) { // 按日期查询
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    buffer.append(" AND u.insert_time >= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
                } else {
                    buffer.append(" AND u.insert_time >= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            }
            buffer.append("),");
            // [40,50)，投资总额
            buffer.append(
                    "(SELECT SUM(in_money) FROM investors i , users_info  ui LEFT JOIN users u ON ui.users_id=u.id ");
            buffer.append(" WHERE ui.users_id=i.users_id AND age>39 AND age<50 AND i.investor_status BETWEEN 1 AND 3 ");
            if (!QwyUtil.isNullAndEmpty(registPlatform) && !"all".equals(registPlatform)) {
                buffer.append(" AND u.regist_platform='" + registPlatform + "'");
            }
            if (!QwyUtil.isNullAndEmpty(insertTime)) { // 按日期查询
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    buffer.append(" AND u.insert_time >= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
                } else {
                    buffer.append(" AND u.insert_time >= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            }
            buffer.append("),");
            // [40,50)，投资总数
            buffer.append("(SELECT COUNT(*) FROM investors i , users_info  ui LEFT JOIN users u ON ui.users_id=u.id ");
            buffer.append(" WHERE ui.users_id=i.users_id AND age>39 AND age<50 AND i.investor_status BETWEEN 1 AND 3 ");
            if (!QwyUtil.isNullAndEmpty(registPlatform) && !"all".equals(registPlatform)) {
                buffer.append(" AND u.regist_platform='" + registPlatform + "'");
            }
            if (!QwyUtil.isNullAndEmpty(insertTime)) { // 按日期查询
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    buffer.append(" AND u.insert_time >= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
                } else {
                    buffer.append(" AND u.insert_time >= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            }
            buffer.append(")");
            buffer.append(" UNION ALL ");
            buffer.append("select ");
            // [50,60)，人数
            buffer.append("(SELECT COUNT(*) FROM users_info ui LEFT JOIN users u on ui.users_id=u.id  ");
            buffer.append("  WHERE age>49 AND age<60");
            if (!QwyUtil.isNullAndEmpty(registPlatform) && !"all".equals(registPlatform)) {
                buffer.append(" AND u.regist_platform='" + registPlatform + "'");
            }
            if (!QwyUtil.isNullAndEmpty(insertTime)) { // 按日期查询
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    buffer.append(" AND u.insert_time >= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
                } else {
                    buffer.append(" AND u.insert_time >= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            }
            buffer.append("),");
            // [50,60)，投资总额
            buffer.append(
                    "(SELECT SUM(in_money) FROM investors i , users_info  ui LEFT JOIN users u ON ui.users_id=u.id ");
            buffer.append(" WHERE ui.users_id=i.users_id AND age>49 AND age<60 AND i.investor_status BETWEEN 1 AND 3 ");
            if (!QwyUtil.isNullAndEmpty(registPlatform) && !"all".equals(registPlatform)) {
                buffer.append(" AND u.regist_platform='" + registPlatform + "'");
            }
            if (!QwyUtil.isNullAndEmpty(insertTime)) { // 按日期查询
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    buffer.append(" AND u.insert_time >= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
                } else {
                    buffer.append(" AND u.insert_time >= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            }
            buffer.append("),");
            // [50,60)，投资总数
            buffer.append("(SELECT COUNT(*) FROM investors i , users_info  ui LEFT JOIN users u ON ui.users_id=u.id ");
            buffer.append(" WHERE ui.users_id=i.users_id AND age>49 AND age<60 AND i.investor_status BETWEEN 1 AND 3 ");
            if (!QwyUtil.isNullAndEmpty(registPlatform) && !"all".equals(registPlatform)) {
                buffer.append(" AND u.regist_platform='" + registPlatform + "'");
            }
            if (!QwyUtil.isNullAndEmpty(insertTime)) { // 按日期查询
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    buffer.append(" AND u.insert_time >= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
                } else {
                    buffer.append(" AND u.insert_time >= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            }
            buffer.append(")");
            buffer.append(" UNION ALL ");
            buffer.append("select ");
            // 大于60岁，人数
            buffer.append("(SELECT COUNT(*) FROM users_info ui LEFT JOIN users u on ui.users_id=u.id  ");
            buffer.append("  WHERE age>59");
            if (!QwyUtil.isNullAndEmpty(registPlatform) && !"all".equals(registPlatform)) {
                buffer.append(" AND u.regist_platform='" + registPlatform + "'");
            }
            if (!QwyUtil.isNullAndEmpty(insertTime)) { // 按日期查询
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    buffer.append(" AND u.insert_time >= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
                } else {
                    buffer.append(" AND u.insert_time >= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            }
            buffer.append("),");
            // 大于60岁，投资总额
            buffer.append(
                    "(SELECT SUM(in_money) FROM investors i , users_info  ui LEFT JOIN users u ON ui.users_id=u.id ");
            buffer.append(" WHERE ui.users_id=i.users_id AND age>59 AND i.investor_status BETWEEN 1 AND 3");
            if (!QwyUtil.isNullAndEmpty(registPlatform) && !"all".equals(registPlatform)) {
                buffer.append(" AND u.regist_platform='" + registPlatform + "'");
            }
            if (!QwyUtil.isNullAndEmpty(insertTime)) { // 按日期查询
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    buffer.append(" AND u.insert_time >= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
                } else {
                    buffer.append(" AND u.insert_time >= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            }
            buffer.append("),");
            // 大于60岁，投资总数
            buffer.append("(SELECT COUNT(*) FROM investors i , users_info  ui LEFT JOIN users u ON ui.users_id=u.id ");
            buffer.append(" WHERE ui.users_id=i.users_id AND age>59 AND i.investor_status BETWEEN 1 AND 3");
            if (!QwyUtil.isNullAndEmpty(registPlatform) && !"all".equals(registPlatform)) {
                buffer.append(" AND u.regist_platform='" + registPlatform + "'");
            }
            if (!QwyUtil.isNullAndEmpty(insertTime)) { // 按日期查询
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    buffer.append(" AND u.insert_time >= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
                } else {
                    buffer.append(" AND u.insert_time >= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND u.insert_time <= ? ");
                    ageList1.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            }
            buffer.append(")");
            List<Object[]> list = dao.LoadAllSql(buffer.toString(), ageList1.toArray());
            List<Age> ageList = new ArrayList<Age>();
            Object[] objects = null;
            if (list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    objects = list.get(i);
                    Age age = new Age();
                    if (i == 0) {
                        age.setAgeCeng("所有年龄");
                    } else if (i == 1) {
                        age.setAgeCeng("0到20岁");
                    } else if (i == 2) {
                        age.setAgeCeng("20到30岁（包括20）");
                    } else if (i == 3) {
                        age.setAgeCeng("30到40岁（包括30）");
                    } else if (i == 4) {
                        age.setAgeCeng("40到50岁（包括40）");
                    } else if (i == 5) {
                        age.setAgeCeng("50到60岁（包括50）");
                    } else if (i == 6) {
                        age.setAgeCeng("60岁以上（包括60）");
                    }
                    age.setRsCount(objects[0] + "");
                    if (QwyUtil.isNullAndEmpty(objects[1])) {
                        objects[1] = 0;
                    }
                    age.setJeCount(objects[1] + "");
                    age.setCsCount(objects[2] + "");
                    ageList.add(age);
                }
            }
            return ageList;
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 根据用户名查询用户信息
     *
     * @param username
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Users> searchUsersInfo(String username) {
        List<Object> list = new ArrayList<Object>();
        StringBuffer buffer = new StringBuffer();
        buffer.append(" FROM Users us");
        buffer.append(" WHERE 1=1");
        if (!QwyUtil.isNullAndEmpty(username)) {
            buffer.append(" AND ");
            buffer.append("  us.username = ? ");
            list.add(DESEncrypt.jiaMiUsername(username));
        }
        return dao.LoadAll(buffer.toString(), list.toArray());
    }

    @SuppressWarnings("unchecked")
    public List<Bank> findBankList() {
        List<Bank> list = (List<Bank>) dao.LoadAll(" FROM Bank ", null);
        return list;
    }

    /**
     * 银行投资统计
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Age> loadbank() {

        StringBuffer buffer = new StringBuffer();

        List<Bank> bankList = findBankList();

        if (bankList.size() > 0) {
            for (int i = 0; i < bankList.size(); i++) {
                buffer.append("SELECT ");
                buffer.append("'" + bankList.get(i).getBankName() + "' ,");
                buffer.append("(SELECT COUNT(*) FROM  account a WHERE a.status=1 AND a.bank_name='"
                        + bankList.get(i).getBankName() + "'), ");
                buffer.append(
                        "(SELECT SUM(money) FROM  cz_record cz LEFT JOIN account a ON cz.account_id=a.id WHERE cz.status=1 AND cz.order_id IS NOT NULL AND a.bank_name='"
                                + bankList.get(i).getBankName() + "'),  ");
                buffer.append(
                        "(SELECT COUNT(*) FROM  cz_record cz LEFT JOIN account a ON cz.account_id=a.id WHERE cz.status=1 AND cz.order_id IS NOT NULL AND a.bank_name='"
                                + bankList.get(i).getBankName() + "'), ");
                buffer.append(
                        "(SELECT COUNT(*) FROM  cz_record cz LEFT JOIN account a ON cz.account_id=a.id WHERE cz.status=2 AND a.bank_name='"
                                + bankList.get(i).getBankName() + "')");
                if (i < bankList.size() - 1) {
                    buffer.append(" UNION ALL  ");
                }

            }
        }

        List<Object[]> list = dao.LoadAllSql(buffer.toString(), null);
        List<Age> ageList = new ArrayList<Age>();
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Object[] objects = list.get(i);
                Age age = new Age();
                age.setBankName(objects[0] + "");
                age.setRsCount(objects[1] + "");
                age.setJeCount(objects[2] + "");
                age.setCgCount(objects[3] + "");
                age.setSbCount(objects[4] + "");
                ageList.add(age);
            }

        }
        return ageList;

    }

    /**
     * 加载用户投资统计
     *
     * @param pageUtil
     * @param username 用户名
     * @param date     日期
     * @param isBuy    是否投资:投资次数大于0 = 'y'
     * @param isZero   账户余额是否为0: 0 = 'y'
     * @param zcpt     注册平台
     * @return
     */
    public PageUtil<UserTZTJ> loadUserTZTJ(PageUtil pageUtil, String username, String date, String isBuy, String isZero,
                                           String zcpt) {
        try {
            List<Object> list = new ArrayList<>();
            StringBuilder sb = new StringBuilder();
            sb.append(
                    " SELECT us.id , us.username , us.regist_platform , IF(usi.invest_count = 0,'n','y'), usi.invest_count, "
                            + " usi.left_money, us.insert_time,IF(usi.left_money = 0,'y','n') FROM users us LEFT JOIN investors ins ON "
                            + " us.id= ins.users_id LEFT JOIN users_info usi ON us.id = usi.users_id WHERE 1=1 ");
            if (!QwyUtil.isNullAndEmpty(username)) { // 按用户名查询
                sb.append("AND us.username = ?");
                list.add(DESEncrypt.jiaMiUsername(username));
            }
            if (!QwyUtil.isNullAndEmpty(isBuy)) { // 按是否投资查询
                if ("n".equals(isBuy)) {
                    sb.append("AND usi.invest_count = 0 ");
                }
                if ("y".equals(isBuy)) {
                    sb.append("AND usi.invest_count > 0 ");
                }
            }
            if (!QwyUtil.isNullAndEmpty(isZero)) { // 按是否投资查询
                if ("n".equals(isZero)) {
                    sb.append("AND usi.left_money > 0 ");
                }
                if ("y".equals(isZero)) {
                    sb.append("AND usi.left_money = 0 ");
                }
            }
            if (!QwyUtil.isNullAndEmpty(zcpt)) { // 按注册平台查询
                sb.append("AND us.regist_platform = ? ");
                list.add(zcpt);
            }
            if (!QwyUtil.isNullAndEmpty(date)) { // 按日期查询
                String[] time = QwyUtil.splitTime(date);
                if (time.length > 1) {
                    sb.append(" AND us.insert_time >= ? ");
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    sb.append(" AND us.insert_time <= ? ");
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
                } else {
                    sb.append(" AND us.insert_time >= ? ");
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    sb.append(" AND us.insert_time<= ? ");
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            }
            sb.append("GROUP BY us.id ORDER BY us.insert_time DESC, us.id ASC");
            StringBuilder sbCount = new StringBuilder();
            sbCount.append(" SELECT COUNT(t.id) FROM (");
            sbCount.append(sb);
            sbCount.append(") t");
            pageUtil = dao.getBySqlAndSqlCount(pageUtil, sb.toString(), sbCount.toString(), list.toArray());
            pageUtil.setList(toUserTZTJList(pageUtil.getList()));
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return pageUtil;
    }

    private List<UserTZTJ> toUserTZTJList(List<Object[]> list) throws Exception {
        List<UserTZTJ> userTZTJs = new ArrayList<>();
        for (Object[] obj : list) {
            UserTZTJ utztj = new UserTZTJ();
            utztj.setUserId(!QwyUtil.isNullAndEmpty(obj[0]) ? obj[0].toString() : "");
            utztj.setUserName(!QwyUtil.isNullAndEmpty(obj[1]) ? DESEncrypt.jieMiUsername(obj[1].toString()) : "");
            utztj.setZcpt(!QwyUtil.isNullAndEmpty(obj[2]) ? obj[2].toString() : "");
            utztj.setIsBuy(!QwyUtil.isNullAndEmpty(obj[3]) ? obj[3].toString() : "");
            utztj.setInvestCount(!QwyUtil.isNullAndEmpty(obj[4]) ? obj[4].toString() : "0");
            utztj.setAccountBalance(
                    !QwyUtil.isNullAndEmpty(obj[5])
                            ? QwyUtil.jieQuFa(QwyUtil.calcNumber(Double.parseDouble(obj[5].toString()), 0.01, "*", 2)
                            .doubleValue(), 2) + ""
                            : "0");
            utztj.setInsertTime(!QwyUtil.isNullAndEmpty(obj[6]) ? QwyUtil.fmyyyyMMddHHmmss.format(obj[6]) : "");
            userTZTJs.add(utztj);
        }
        return userTZTJs;
    }

    public PageUtil<UserInfoList> findIndexUsersByChannel(PageUtil pageUtil, String channel, String username, String insertTime, String acinsertTime, String isbindbank, String level, String inMoney1, String inMoney2) {
        try {
            List<Object> list = new ArrayList<Object>();
            StringBuffer buff = new StringBuffer();
            buff.append(" SELECT a.id ,a.username ,b.real_name,a.province,  a.city,v.realname,a.regist_platform, b.is_bind_bank, ");
            buff.append(" t.in_money,IFNULL(a.insert_time,'') ,b.level AS LEVEL,b.sex ,b.age ,b.birthday ");
            buff.append(" FROM users a ");
            buff.append(" LEFT JOIN users_info b ON a.id = b.users_id ");
            buff.append(" LEFT JOIN (SELECT u.id id ,ui.real_name  realname FROM users u ");
            buff.append(" LEFT JOIN invite i ON i.be_invited_id = u.id ");
            buff.append(" LEFT JOIN users_info ui ON ui.users_id = i.invite_id) v ON a.id = v.id ");
            buff.append(" LEFT JOIN (select SUM(i.in_money/100) as in_money , i.users_id as users_id  from investors i where 1=1 and i.investor_status in ('1','2','3')  GROUP BY i.users_id )t");
            buff.append("  on t.users_id = b.users_id ");
            buff.append("  LEFT JOIN account ac ON ac.users_id = a.id  AND ac.STATUS = 1  ");
            buff.append("  LEFT JOIN ( ");
            buff.append("  SELECT cpfr.users_id 'users_id',SUM(cpfr.money) 'money', cpfr.insert_time 'insert_time' FROM coin_purse_funds_record cpfr WHERE cpfr.type = 'to'  AND  cpfr.status = 0   ");
            buff.append("  GROUP BY cpfr.users_id )lqg ");
            buff.append("  ON lqg.users_id = a.id WHERE 1=1 ");


            if (!QwyUtil.isNullAndEmpty(channel)) {
                buff.append(" AND a.regist_channel = ? ");
                list.add(channel);
            }
            if (!QwyUtil.isNullAndEmpty(username)) {
                buff.append(" AND (");
                buff.append("  a.username = ? ");
                list.add(DESEncrypt.jiaMiUsername(username));
                buff.append(" OR a.id = '" + username + "' ");
                buff.append(" OR b.real_name = ? ");
                list.add(username);
                buff.append(" )");
            }
            if (!QwyUtil.isNullAndEmpty(isbindbank)) {
                buff.append(" AND b.is_bind_bank = ? ");
                list.add(isbindbank);
            }

            // 发布时间
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    buff.append(" AND a.insert_time >= ? ");
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buff.append(" AND a.insert_time <= ? ");
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
                } else {
                    buff.append(" AND a.insert_time >= ? ");
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buff.append(" AND a.insert_time <= ? ");
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            }

            // 发布时间
            if (!QwyUtil.isNullAndEmpty(acinsertTime)) {
                String[] time = QwyUtil.splitTime(acinsertTime);
                if (time.length > 1) {
                    buff.append(" AND ac.insert_time >= ? ");
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buff.append(" AND ac.insert_time <= ? ");
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
                } else {
                    buff.append(" AND ac.insert_time >= ? ");
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buff.append(" AND ac.insert_time <= ? ");
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            }


            if (!QwyUtil.isNullAndEmpty(level)) {
                buff.append(" AND b.level = ? ");
                list.add(level);
            }

            if (!QwyUtil.isNullAndEmpty(inMoney1) && !QwyUtil.isNullAndEmpty(inMoney2)) {
                buff.append(" AND  t.in_money >= ? ");
                list.add(Double.parseDouble(inMoney1) * 10000);
                buff.append(" AND  t.in_money <= ? ");
                list.add(Double.parseDouble(inMoney2) * 10000);
            } else if (!QwyUtil.isNullAndEmpty(inMoney1)) {
                buff.append(" AND  t.in_money >= ? ");
                list.add(Double.parseDouble(inMoney1) * 10000);
            } else if (!QwyUtil.isNullAndEmpty(inMoney2)) {
                buff.append(" AND  t.in_money <= ? ");
                list.add(Double.parseDouble(inMoney2) * 10000);
            }
            buff.append(" ORDER BY t.in_money DESC ");

            StringBuffer bufferCount = new StringBuffer();
            bufferCount.append(" SELECT COUNT(*)  ");
            bufferCount.append(" FROM (");
            bufferCount.append(buff);
            bufferCount.append(") t");
            pageUtil = dao.getBySqlAndSqlCount(pageUtil, buff.toString(), bufferCount.toString(), list.toArray());
            List<UserInfoList> platUsers = toIndexDateMoney(pageUtil.getList());
            pageUtil.setList(platUsers);
            return pageUtil;
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    private List<UserInfoList> toIndexDateMoney(List<Object[]> list) throws ParseException {
        List<UserInfoList> meowPay = new ArrayList<UserInfoList>();
        Double money = 0.0;
        if (!QwyUtil.isNullAndEmpty(list)) {
            for (Object[] object : list) {
                UserInfoList plat = new UserInfoList();
                plat.setId(object[0] == null ? "" : object[0] + "");
                plat.setUsername(object[1] == null ? "" : object[1] + "");
                plat.setRealName(object[2] == null ? "" : object[2] + "");
                plat.setProvince(object[3] == null ? "" : object[3] + "");
                plat.setCity(object[4] == null ? "" : object[4] + "");
                plat.setCategory(object[5] + "");
                plat.setRegistPlatform(object[6] == null ? "" : object[6] + "");
                plat.setIsBindBank(object[7] == null ? "" : object[7] + "");
                plat.setInMoney(object[8] == null ? "" : object[8] + "");
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date time = null;
                if (!QwyUtil.isNullAndEmpty(object[9])) {
                    time = sd.parse(object[9] + "");
                    plat.setInsertTime(time);
                }
                if (object[8] == null) {
                    plat.setLevel("0");
                } else {
                    money = Double.parseDouble(object[8] + "");
                    plat.setLevel(userLevel(money / 10000));
                }
                plat.setSex(object[11] == null ? "" : object[11] + "");
                plat.setAge(object[12] == null ? "" : object[12] + "");
                plat.setBirthday(object[13] == null ? "" : object[13].toString().trim().substring(object[13].toString().indexOf("-") + 1, 10) + "");
                meowPay.add(plat);
            }
        }
        return meowPay;
    }
}