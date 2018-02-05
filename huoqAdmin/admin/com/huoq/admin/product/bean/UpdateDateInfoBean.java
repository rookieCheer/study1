package com.huoq.admin.product.bean;

import com.huoq.admin.product.dao.UpdateDateInfoDao;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.Activity;
import com.huoq.orm.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class UpdateDateInfoBean {
    @Autowired
    private UpdateDateInfoDao indexDateInfoDao;

    /**
     * 查询时间表的数据
     *
     * @return
     */
    public List<Date> findInsertTime() {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM times");
        List<Object> list = indexDateInfoDao.LoadAllSql(sql.toString(), null);
        List<Date> time = (List<Date>) (List) list;
        return time;
    }

    /**
     * 查询出头条的所有ios用户所有用户
     *
     * @return
     */
    public List<Users> findIOSUsers() {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT u.id,u.username,u.regist_channel FROM users u ");
        sql.append(" JOIN ( ");
        sql.append("SELECT ttuc.username username FROM third_toutiao_userinfo ttu ");
        sql.append("JOIN third_toutiao_userinfo_callback ttuc ON ttu.idfa = ttuc.idfa WHERE ttu.os = 1 AND ttu.idfa = ttuc.idfa ");
        sql.append("AND ttuc.username != '' AND  ttuc.idfa != '00000000-0000-0000-0000-000000000000' ");
        sql.append("GROUP BY ttu.idfa )t ");
        sql.append("ON t.username = u.username WHERE t.username = u.username   ");
        List<Object[]> users = indexDateInfoDao.LoadAllSql(sql.toString(), null);
        List<Users> user = toUser(users);
        return user;
    }


    /**
     * 查询出头条的所有ios用户所有用户
     *
     * @return
     */
    public List<Long> findUsers() {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT t.id FROM `third_toutiao_userinfo_callback` ttuc LEFT JOIN");
        sql.append("(SELECT id,username FROM users WHERE regist_channel LIKE 'ttIOS%' ");
        sql.append("GROUP BY `username`)t ON t.username = ttuc.username WHERE t.username = ttuc.username AND  ttuc.idfa = '00000000-0000-0000-0000-000000000000' ");
        List<BigInteger> users = indexDateInfoDao.LoadAllSql(sql.toString(), null);
        List<Long> list = new ArrayList<>();
        for (BigInteger obj : users) {
            long id = obj.longValue();
            list.add(id);
        }
        return list;
    }

    /**
     * 查询出头条的所有ios用户所有用户
     *
     * @return
     */
    public List<Users> findHaveChannelIOSUsers() {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT u.id,u.username,t.channel FROM users u ");
        sql.append("JOIN ( ");
        sql.append("SELECT ttuc.username username ,ttu.channel channel FROM third_toutiao_userinfo ttu ");
        sql.append(" JOIN third_toutiao_userinfo_callback ttuc ON ttu.idfa = ttuc.idfa WHERE ttu.os = 1 AND ttu.idfa = ttuc.idfa ");
        sql.append("AND ttuc.username != '' AND  ttuc.idfa != '00000000-0000-0000-0000-000000000000' ");
        sql.append("AND ttu.channel IN ('ttIOS01','ttIOS02') ");
        sql.append("GROUP BY ttu.idfa )t ");
        sql.append("ON t.username = u.username WHERE t.username = u.username   ");
        List<Object[]> users = indexDateInfoDao.LoadAllSql(sql.toString(), null);
        List<Users> user = toUser(users);
        return user;
    }

    /**
     * 查询出头条的所有android用户所有用户
     *
     * @return
     */
    public List<Users> findandroidUsers() {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT u.id,u.username,u.regist_channel FROM users u ");
        sql.append("JOIN ( ");
        sql.append("SELECT ttuc.username username FROM third_toutiao_userinfo ttu ");
        sql.append("JOIN third_toutiao_userinfo_callback ttuc ON ttu.imei = ttuc.imei WHERE ttu.os = 0 AND ttu.imei = ttuc.imei ");
        sql.append("AND ttuc.username != ''  ");
        sql.append("GROUP BY ttu.imei )t ");
        sql.append("ON t.username = u.username WHERE t.username = u.username   ");
        List<Object[]> users = indexDateInfoDao.LoadAllSql(sql.toString(), null);
        List<Users> user = toUser(users);
        return user;
    }

    /**
     * 查询出头条的所有custom用户所有用户
     *
     * @return
     */
    public List<Users> findAndroidUsers() {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT u.id,u.username,u.regist_channel FROM  users u WHERE u.regist_channel ='custom' AND u.insert_time >'2017-11-27' AND u.insert_time <'2017-12-7' ");
        List<Object[]> users = indexDateInfoDao.LoadAllSql(sql.toString(), null);
        List<Users> user = toUser(users);
        return user;
    }

    /**
     * 查询出所有激活用户(为头条IOS用户的)
     *
     * @return
     */
    public List<Activity> findActivity() {
        List<Activity> list = new ArrayList<>();
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT ac.id FROM activity ac ");
        sql.append(" JOIN ( ");
        sql.append("SELECT ttuc.idfa idfa FROM third_toutiao_userinfo ttu ");
        sql.append(" JOIN third_toutiao_userinfo_callback ttuc ON  ttu.idfa = ttuc.idfa WHERE ttu.os = 1  AND ttu.idfa = ttuc.idfa ");
        sql.append("AND  ttuc.idfa != '00000000-0000-0000-0000-000000000000' ");
        sql.append("GROUP BY ttu.idfa )t ");
        sql.append("ON t.idfa = ac.imei WHERE t.idfa = ac.imei ");
        List<String> ac = indexDateInfoDao.LoadAllSql(sql.toString(), null);
        for (String id : ac) {
            Activity activity = new Activity();
            activity.setId(id);
            list.add(activity);
        }
        return list;
    }


    /**
     * 查询出所有激活用户(为头条IOS用户的并渠道为ttIOS01或者为ttIOS02)
     *
     * @return
     */
    public List<Activity> findHavaChannelActivity() {
        List<Activity> list = new ArrayList<>();
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT ac.id,t.channel FROM activity ac ");
        sql.append("JOIN ( ");
        sql.append("SELECT ttuc.idfa idfa ,ttu.channel channel FROM third_toutiao_userinfo ttu ");
        sql.append("JOIN third_toutiao_userinfo_callback ttuc ON  ttu.idfa = ttuc.idfa WHERE ttu.os = 1  AND ttu.idfa = ttuc.idfa ");
        sql.append("AND  ttuc.idfa != '00000000-0000-0000-0000-000000000000'  ");
        sql.append("AND ttu.channel IN ('ttIOS01','ttIOS02') ");
        sql.append("GROUP BY ttu.idfa )t ");
        sql.append("ON t.idfa = ac.imei WHERE t.idfa = ac.imei ");
        sql.append("OR t.idfa = ac.idfa ");
        List<Object[]> ac = indexDateInfoDao.LoadAllSql(sql.toString(), null);
        for (Object[] obj : ac) {
            Activity activity = new Activity();
            activity.setId(obj[0]+"");
            activity.setChannel(obj[1]+"");
            list.add(activity);
        }
        return list;
    }


    /**
     * 查询出所有激活用户(为头条android用户的)
     *
     * @return
     */
    public List<Activity> findAndroidActivity() {
        List<Activity> list = new ArrayList<>();
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT ac.id FROM activity ac ");
        sql.append("JOIN ( ");
        sql.append("SELECT ttuc.imei imei FROM third_toutiao_userinfo ttu ");
        sql.append("JOIN third_toutiao_userinfo_callback ttuc ON  ttu.imei = ttuc.imei WHERE ttu.os = 0  AND ttu.imei = ttuc.imei ");
        sql.append("GROUP BY ttu.imei )t ");
        sql.append("ON t.imei = ac.MD5_IMEI WHERE t.imei = ac.MD5_IMEI ");
        List<String> ac = indexDateInfoDao.LoadAllSql(sql.toString(), null);
        for (String id : ac) {
            Activity activity = new Activity();
            activity.setId(id);
            list.add(activity);
        }
        return list;
    }
    /**
     * 查询出激活用户为ttanoroid的用户
     *
     * @return
     */
    public List<Activity> findAndroid() {
        List<Activity> list = new ArrayList<>();
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT id FROM activity WHERE channel LIKE '%ttandriod%' ");
        sql.append("AND MD5_IMEI NOT IN (SELECT ttuc.imei MD5_IMEI FROM third_toutiao_userinfo ttu ");
        sql.append("JOIN third_toutiao_userinfo_callback ttuc ON  ttu.imei = ttuc.imei WHERE ttu.os = 0  AND ttu.imei = ttuc.imei ");
        sql.append("GROUP BY ttu.imei) ");
        List<String> ac = indexDateInfoDao.LoadAllSql(sql.toString(), null);
        for (String id : ac) {
            Activity activity = new Activity();
            activity.setId(id);
            list.add(activity);
        }
        return list;
    }

    /**
     * 根据id查询出激活用户
     *
     * @return
     */
    public Activity findActivityBuId(String id) throws Exception {
        Activity activity = (Activity) indexDateInfoDao.findById(new Activity(), id);
        return activity;
    }

    /**
     * 根据id查询出用户
     *
     * @return
     */
    public Users findUsersBuId(Long id) {
        Users user = (Users) indexDateInfoDao.findById(new Users(), id);
        return user;
    }

    /**
     * 更新Activity
     *
     * @return
     */
    public void updateActivityBuyId(Activity activity) {
        indexDateInfoDao.saveOrUpdate(activity);
    }

    /**
     * 更新users
     *
     * @return
     */
    public void updateUsersBuyId(Users user) {
        indexDateInfoDao.saveOrUpdate(user);
    }

    public List<Users> toUser(List<Object[]> list) {
        List<Users> userList = new ArrayList<>();
        if (!QwyUtil.isNullAndEmpty(list)) {
            for (Object[] obj : list) {
                Users users = new Users();
                users.setId(Long.valueOf(obj[0] + ""));
                users.setUsername(obj[1] + "");
                users.setRegistChannel(obj[2] + "");
                userList.add(users);
            }
        }
        return userList;
    }
}
