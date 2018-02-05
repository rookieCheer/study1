package com.huoq.common.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.huoq.common.dao.TiedCardDao;
import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.TiedCard;

@Service
public class TiedCardBean {

    @Resource
    private TiedCardDao tcdDao;

    /**
     * 绑卡详情统计
     * 
     * @param pageUtil
     * @return
     * @throws ParseException
     */
    @SuppressWarnings("unchecked")
    public PageUtil<TiedCard> findTiedCard(PageUtil pageUtil, String insertTime, String phone, String realname) throws ParseException {
        List<Object> list = new ArrayList<Object>();
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT * FROM ( ");
        sql.append("SELECT u.insert_time zinsertTime, ac.insert_time insertTime , ac.bank_name , ac.bank_account ,");
        sql.append(" u.regist_platform  , u.id  , ui.real_name  , ");
        sql.append("u.phone phone , u.card_type  , ui.idcard  , u.province  , u.city  , ");
        sql.append("ui.sex  , ui.age   ,DATE_FORMAT(ui.birthday,'%Y%m%d'),u.regist_channel ,r.realname FROM account  ac ");
        sql.append("LEFT JOIN users u ON ac.users_id = u.id LEFT JOIN users_info ui ON ui.users_id = u.id  ");
        sql.append("LEFT JOIN (SELECT u.id id ,ui.real_name  realname FROM users u  ");
        sql.append("LEFT JOIN invite i ON i.be_invited_id = u.id LEFT JOIN users_info ui ON ui.users_id = i.invite_id) r ON u.id = r.id ) t ");
        sql.append(" WHERE 1=1 ");
        // 充值时间
        if (!QwyUtil.isNullAndEmpty(insertTime)) {
            String[] time = QwyUtil.splitTime(insertTime);
            if (time.length > 1) {
                sql.append(" AND t.insertTime >= ? ");
                list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
                sql.append(" AND t.insertTime <= ? ");
                list.add(QwyUtil.fmMMddyyyy.parse(time[1]));
            } else {
                sql.append(" AND t.insertTime >= ? ");
                list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                sql.append(" AND t.insertTime <= ? ");
                list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
            }
        }
        // 手机号
        if (!QwyUtil.isNullAndEmpty(phone)) {
            sql.append(" AND t.phone = ?");
            list.add(DESEncrypt.jiaMiUsername(phone));
        }
        sql.append("ORDER BY t.insertTime DESC");
        StringBuffer sqlCount = new StringBuffer();
        sqlCount.append("SELECT COUNT(*) ");
        sqlCount.append("FROM (");
        sqlCount.append(sql);
        sqlCount.append(") c");
        pageUtil = tcdDao.getBySqlAndSqlCount(pageUtil, sql.toString(), sqlCount.toString(), list.toArray());
        
        List<TiedCard> tiedCard = toTiedCard(pageUtil.getList());
        pageUtil.setList(tiedCard);
        return pageUtil;
    }

    private List<TiedCard> toTiedCard(List<Object[]> list) {
        List<TiedCard> buyProductInfos = new ArrayList<TiedCard>();
        if (!QwyUtil.isNullAndEmpty(list)) {
            for (Object[] object : list) {
                TiedCard tiedCard = new TiedCard();
                SimpleDateFormat sdf = new SimpleDateFormat(" yyyyMMdd ");
                if (object[0] != null) {

                    String ZinsertTime = sdf.format(object[0]);
                    tiedCard.setZinsertTime(ZinsertTime);
                }
                if (object[1] != null) {
                    String InsertTime = sdf.format(object[1]);
                    tiedCard.setInsertTime(InsertTime);
                }
                if (object[2] != null) {
                    tiedCard.setBankName(object[2] + "");
                }
                if (object[3] != null) {
                    tiedCard.setBankAccount(object[3] + "");
                }
                if (object[4] != null) {
                    tiedCard.setRegistPlatform(object[4] + "");
                }
                if (object[5] != null) {
                    tiedCard.setId(object[5] + "");
                }
                if (object[6] != null) {
                    tiedCard.setRealName(object[6] + "");
                }
                if (object[7] != null) {
                    tiedCard.setPhone(object[7] + "");
                }
                if (object[8] != null) {
                    tiedCard.setCardType(object[8] + "");
                }
                if (object[9] != null) {
                    tiedCard.setIdCard(object[9] + "");
                }
                if (object[10] != null) {
                    tiedCard.setProvince(object[10] + "");
                }
                if (object[11] != null) {
                    tiedCard.setCity(object[11] + "");
                }
                if (object[12] != null) {
                    tiedCard.setGender(object[12] + "");
                }
                if (object[13] != null) {
                    tiedCard.setAge(object[13] + "");
                }
                if (object[14] != null) {
                    tiedCard.setBirthday(object[14] + "");
                }
                if (object[15] != null) {
                    tiedCard.setChannel(object[15] + "");
                }
                if (object[16] != null) {
                    tiedCard.setCardFriend(object[16] + "");
                }
               buyProductInfos.add(tiedCard);
            }
        }
        return buyProductInfos;
    }
}
