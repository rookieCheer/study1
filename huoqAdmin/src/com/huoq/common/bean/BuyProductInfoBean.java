package com.huoq.common.bean;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.huoq.common.dao.BuyProductInfoDao;
import com.huoq.common.dao.TiedCardDao;
import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.BuyProductInfo;

@Service
public class BuyProductInfoBean {
    @Resource
    private BuyProductInfoDao dao;
    @Resource
    private TiedCardDao tcdDao;

    /**
     * 购买情况统计
     *
     * @param pageUtil
     * @throws ParseException
     */
    public PageUtil<BuyProductInfo> productInfo(PageUtil pageUtil, String insertTime, String phone, String isnew) throws ParseException {
        List<Object> list = new ArrayList<Object>();
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM ( ");
        sql.append("(SELECT * FROM ( ");
        sql.append("SELECT u.id , u.username username ,i.insert_time insterTime ,i.in_money ,p.title productname,DATE_FORMAT(p.finish_time,'%Y%m%d') ,");
        sql.append("DATEDIFF(p.finish_time,NOW()) ,us.real_name ,us.sex ,us.phone as phone,r.realname ,u.province ,u.city ");
        sql.append("FROM  investors i  JOIN product p ON i.product_id= p.id AND i.investor_status IN ('1','2','3') ");
        sql.append(" JOIN users_info us ON us.users_id  = i.users_id ");
        sql.append(" JOIN users u ON u.id = us.users_id ");
        sql.append(" left JOIN (SELECT u.id id ,ui.real_name  realname FROM users u JOIN invite i ON i.be_invited_id = u.id ");
        sql.append(" JOIN users_info ui ON ui.users_id = i.invite_id) r ON u.id = r.id ) t WHERE 1=1 AND t.productname IS NOT NULL ");
        // 充值时间
        if (!QwyUtil.isNullAndEmpty(insertTime)) {
            String[] time = QwyUtil.splitTime(insertTime);
            if (time.length > 1) {
                sql.append(" AND t.insterTime >= ? ");

                list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                sql.append(" AND t.insterTime <= ? ");

                list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
            } else {
                sql.append(" AND t.insterTime >= ? ");
                list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                sql.append(" AND t.insterTime <= ? ");
                list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
            }
        }
        //根据用户名(手机号查询)
        if (!QwyUtil.isNullAndEmpty(phone)) {
            sql.append("AND t.username=?");
            list.add(DESEncrypt.jiaMiUsername(phone));
        }
        sql.append(" )");
        sql.append("  UNION ALL ");
        sql.append("(SELECT * FROM ( ");
        sql.append(
                "SELECT u.id , u.username username ,cpf.insert_time insterTime ,cpf.money ,"
                        + "cpf.TYPE productname, NOW() ,"
                        + "DATEDIFF(NOW(),NOW()) , us.real_name ,"
                        + "us.sex ,us.phone as phone,r.realname ,u.province ,u.city ");
        sql.append("FROM users_info us JOIN users u ON u.id = us.users_id ");
        sql.append("LEFT JOIN (SELECT u.id id ,ui.real_name  realname FROM users u  JOIN invite i ON i.be_invited_id = u.id "
                + " JOIN users_info ui ON ui.users_id = i.invite_id ) r ON u.id = r.id  ");
        sql.append(" JOIN coin_purse_funds_record cpf  ON cpf.users_id = u.id WHERE cpf.TYPE = 'to' ) a WHERE 1=1 ");
        // 充值时间
        if (!QwyUtil.isNullAndEmpty(insertTime)) {
            String[] time = QwyUtil.splitTime(insertTime);
            if (time.length > 1) {
                sql.append(" AND a.insterTime >= ? ");

                list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                sql.append(" AND a.insterTime <= ? ");
                list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
            } else {
                sql.append(" AND a.insterTime >= ? ");
                list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                sql.append(" AND a.insterTime <= ? ");
                list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
            }
        }
        //根据用户名(手机号查询)
        if (!QwyUtil.isNullAndEmpty(phone)) {
            sql.append("AND a.username=?");
            list.add(DESEncrypt.jiaMiUsername(phone));
        }
        sql.append(" ) )a  ");
        //查询是新手(首投用户)
        if (!QwyUtil.isNullAndEmpty(isnew) && isnew.equals("1")) {
            sql.append("WHERE a.productname LIKE'%新手%' ");
        }
        sql.append(" ORDER BY a.insterTime DESC ");
        StringBuffer sqlCount = new StringBuffer();
        sqlCount.append("SELECT COUNT(*) ");
        sqlCount.append("FROM (");
        sqlCount.append(sql);
        sqlCount.append(") c");
        pageUtil = dao.getBySqlAndSqlCount(pageUtil, sql.toString(), sqlCount.toString(), list.toArray());
        List<BuyProductInfo> buyProductInfo = toBuyProductInfo(pageUtil.getList());
        pageUtil.setList(buyProductInfo);
        return pageUtil;
    }

    /**
     * 将数据转换为BuyProductInfo形式
     *
     * @param list
     * @return
     */
    private List<BuyProductInfo> toBuyProductInfo(List<Object[]> list) {
        List<BuyProductInfo> buyProductInfos = new ArrayList<BuyProductInfo>();
        if (!QwyUtil.isNullAndEmpty(list)) {
            for (Object[] object : list) {
                BuyProductInfo buyProductInfo = new BuyProductInfo();
                if (object[0] != null) {
                    BigInteger id = (BigInteger) object[0];
                    if (id != null) {
                        buyProductInfo.setId(id.intValue());
                    }
                }
                if (object[1] != null) {
                    buyProductInfo.setUsername(object[1] + "");
                }
                if (object[2] != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat(" yyyyMMdd ");
                    String format = sdf.format(object[2]);
                    buyProductInfo.setInsterTime(format);
                }
                if (object[3] == null) {
                    buyProductInfo.setInMoney(Double.valueOf(0.0 + ""));
                } else {
                    buyProductInfo.setInMoney(Double.valueOf(object[3] + ""));
                }
                if ("to".equals(object[4] + "")) {
                    buyProductInfo.setProductName("新华零钱罐");
                } else {
                    buyProductInfo.setProductName(object[4] + "");
                }
                if ("to".equals(object[4] + "")) {
                    buyProductInfo.setFinishTime("-");
                } else {
                    buyProductInfo.setFinishTime(object[5] + "");
                }
                if ("to".equals(object[4] + "")) {
                    buyProductInfo.setEndTime("-");
                } else {
                    buyProductInfo.setEndTime(object[6] + "");
                }
                if (object[7] != null) {
                    buyProductInfo.setRealName(object[7] + "");
                }
                if (object[8] != null) {
                    buyProductInfo.setGender(object[8] + "");
                }

                if (object[9] != null) {
                    buyProductInfo.setPhone(object[9] + "");
                }

                Object friend = object[10];
                if (friend == null) {
                    buyProductInfo.setFriend("客户");
                } else {
                    if (friend instanceof String) {
                        String friendStr = (String) friend;
                        friendStr = friendStr.trim();
                        if ("".equals(friendStr)) {
                            buyProductInfo.setFriend("客户");
                        } else {
                            buyProductInfo.setFriend(friendStr);
                        }

                    }
                }
                if (object[11] != null) {
                    buyProductInfo.setProvince(object[11] + "");
                }
                if (object[12] != null) {
                    buyProductInfo.setCity(object[12] + "");
                }
                buyProductInfos.add(buyProductInfo);
            }
        }
        return buyProductInfos;
    }
}
