package com.huoq.admin.product.bean;

import com.huoq.admin.product.dao.InvestorsDAO;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * WAP 投资统计业务bean
 * Created by yks on 2016/10/25.
 */
@Service
public class WapInvestorRecordBean {
    @Resource
    InvestorsDAO dao;

    /**
     * 获取每日wap投资情况
     *
     * @param insertTime     注册时间
     * @param registPlatform 注册平台
     * @return
     */

    public PageUtil<WapInvestors> findWapInvestorsByDate(PageUtil pageUtil, String insertTime, String registPlatform) throws Exception {
        ArrayList<Object> list = new ArrayList<Object>();
        StringBuffer buffer = new StringBuffer();
        buffer.append(" SELECT r.date ,r.platform ,r.registCount ,r.bindCount , ");
        buffer.append(" r.investCount,r.totalInvestMoney ,r.reinvestCount ");
        buffer.append(" FROM (");
        buffer.append(wapInvestSql());
        buffer.append(" ) r ");
        buffer.append(" WHERE 1=1 ");
        if (!QwyUtil.isNullAndEmpty(registPlatform)) {
            if (registPlatform.equals("wx")) {  //微信平台
                buffer.append(" AND r.platform = 3 ");
            }
            if (registPlatform.equals("wap")) { // WAP平台
                buffer.append(" AND r.platform >= 3 ");
            }
        }
        // 时间
        if (!QwyUtil.isNullAndEmpty(insertTime)) {
            String[] time = QwyUtil.splitTime(insertTime);
            if (time.length > 1) {
                buffer.append(" AND r.date >= ? ");
                list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
                buffer.append(" AND r.date <= ? ");
                list.add(QwyUtil.fmMMddyyyy.parse(time[1]));
            } else {
                buffer.append(" AND r.date >= ? ");
                list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                buffer.append(" AND r.date <= ? ");
                list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
            }
        }
        buffer.append(" ORDER BY  r.date  DESC");
        StringBuffer bufferCount = new StringBuffer();
        bufferCount.append(" SELECT COUNT(t.date)  ");
        bufferCount.append(" FROM (");
        bufferCount.append(buffer);
        bufferCount.append(") t");
        pageUtil = dao.getBySqlAndSqlCount(pageUtil, buffer.toString(), bufferCount.toString(), list.toArray());
        pageUtil.setList(toWapInvestors(pageUtil.getList()));
        return pageUtil;
    }

    /**
     * 转换list
     *
     * @param list
     * @return
     * @throws Exception
     */
    private List<WapInvestors> toWapInvestors(List<Object[]> list) throws Exception {
        List<WapInvestors> wapInvestorsList = new ArrayList<WapInvestors>();
        if (!QwyUtil.isNullAndEmpty(list)) {
            for (Object[] obj : list) {
                WapInvestors wapInvestors = new WapInvestors();
                /**
                 * date;//统计日期 platform;//投资平台registCount;//注册人数bindCount;//绑卡人数investCount;//投资人数
                 totalInvestMoney;//投资金额reinvestCount;//复投人数reinvestRate;//复投率 = 复投/投资
                 */
                wapInvestors.setDate(QwyUtil.fmyyyyMMdd.format(obj[0]));
                if (!QwyUtil.isNullAndEmpty(obj[1])) {
                    if (obj[1].toString().equals("3")) {
                        wapInvestors.setPlatform("微信");
                    } else {
                        wapInvestors.setPlatform("WAP");
                    }
                }
                wapInvestors.setRegistCount(!QwyUtil.isNullAndEmpty(obj[2]) ? obj[2] + "" : "0");
                wapInvestors.setBindCount(!QwyUtil.isNullAndEmpty(obj[3]) ? obj[3] + "" : "0");
                wapInvestors.setInvestCount(!QwyUtil.isNullAndEmpty(obj[4]) ? obj[4] + "" : "0");
                wapInvestors.setTotalInvestMoney(!QwyUtil.isNullAndEmpty(obj[5])
                        ? QwyUtil.calcNumber(obj[5], 100, "/", 2) + "" : "0");
                wapInvestors.setReinvestCount(!QwyUtil.isNullAndEmpty(obj[6]) ? obj[6] + "" : "0");
                if (wapInvestors.getInvestCount().equals("0") || wapInvestors.getReinvestCount().equals("0")) {
                    wapInvestors.setReinvestRate("0");
                } else {
                    BigDecimal ftl = QwyUtil.calcNumber(wapInvestors.getReinvestCount(), wapInvestors.getInvestCount(), "/");
                    wapInvestors.setReinvestRate(QwyUtil.calcNumber(ftl, 0.01, "/", 2).toString());
                }
                wapInvestorsList.add(wapInvestors);
            }
        }
        return wapInvestorsList;
    }

    /**
     * 绑定银行的人数sql语句
     *
     * @return
     */
    private final String bindCountSQL() throws ParseException {
        StringBuffer buffer = new StringBuffer();
        buffer.append(" SELECT DATE_FORMAT( ui.insert_time, '%Y-%m-%d' ) as date ,  ");
        buffer.append(" COUNT(DISTINCT ui.users_id) as bindCount ");
        buffer.append(" FROM users_info ui ");
        buffer.append(" WHERE ui.is_bind_bank = '1' ");////是否绑定银行卡;0:未绑定; 1:已绑定
        buffer.append(" AND ui.users_id in (");
        buffer.append(" SELECT id FROM users us  ");
        buffer.append(" WHERE us.regist_platform >= 3 ");
        buffer.append(" )  ");
        buffer.append(" GROUP BY DATE_FORMAT( ui.insert_time, '%Y-%m-%d' ) ");
        buffer.append(" ORDER BY DATE_FORMAT( ui.insert_time, '%Y-%m-%d' ) DESC ");
        return buffer.toString();
    }


    /**
     * 以日期分组日期、投资平台、注册人数、注册并投资人数、投资金额sql语句
     *
     * @return
     * @throws ParseException
     */
    private final String registAndInvestSQL() throws ParseException {
        StringBuffer buffer = new StringBuffer();
        buffer.append("	SELECT us.regist_platform AS platform , DATE_FORMAT( us.insert_time, '%Y-%m-%d' ) as date ,  ");
        buffer.append(" COUNT(DISTINCT us.id) as registCount , ");
        buffer.append(
                " COUNT(DISTINCT CASE WHEN (ins.investor_status in ('1','2','3') AND ins.id is not NULL AND DATE_FORMAT( us.insert_time, '%Y-%m-%d' ) = DATE_FORMAT( ins.pay_time, '%Y-%m-%d' )) then us.id else null end )  as investCount, ");
        buffer.append(
                " SUM(CASE WHEN (ins.investor_status in ('1','2','3') AND ins.id is not NULL AND DATE_FORMAT( us.insert_time, '%Y-%m-%d' ) = DATE_FORMAT( ins.pay_time, '%Y-%m-%d' )) then ins.in_money else 0 end )  as totalInvestMoney ");
        buffer.append(" FROM  users as us ");
        buffer.append(" LEFT JOIN investors ins ON  us.id = ins.users_id  WHERE 1=1 ");
        buffer.append(" AND us.regist_platform >= 3 ");
        buffer.append(" GROUP BY DATE_FORMAT( us.insert_time, '%Y-%m-%d' ) ");
        buffer.append(" ORDER BY DATE_FORMAT( us.insert_time, '%Y-%m-%d' ) DESC  ");
        return buffer.toString();
    }

    /**
     * 复投人数
     *
     * @throws Exception
     */
    private final String ftCountSQL() throws Exception {
        StringBuffer buffer = new StringBuffer();
        buffer.append(" SELECT DATE_FORMAT(t.date, '%Y-%m-%d') AS date, COUNT(DISTINCT t.users_id) AS reinvestCount ");
        buffer.append(" FROM ( SELECT * FROM ( SELECT DATE_FORMAT(ins.insert_time, '%Y-%m-%d') AS date,");
        buffer.append(" COUNT(ins.users_id) AS investCount, ins.users_id AS users_id FROM investors ins, users us WHERE ");
        buffer.append(" ins.investor_status IN ('1', '2', '3') AND ins.users_id = us.id ");
        buffer.append(" AND us.regist_platform >= 3 GROUP BY ins.users_id ) x WHERE x.investCount >= 2 ");
        buffer.append(" ) t GROUP BY t.date ");
        return buffer.toString();
    }

    /**
     * wap投资的SQL语句
     *
     * @return
     */
    private final String wapInvestSql() throws Exception {
        StringBuffer buffer = new StringBuffer();
        buffer.append(" SELECT dd.insert_time as date ,q.platform as platform , q.registCount as registCount ,q.investCount as investCount ,q.totalInvestMoney as totalInvestMoney , ");
        buffer.append(" w.bindCount as bindCount,e.reinvestCount as reinvestCount  ");
        buffer.append(" FROM ");
        buffer.append(" dateday dd ");
        buffer.append(" LEFT JOIN ");
        buffer.append(" (" + registAndInvestSQL()); //投资平台、注册人数、注册并投资人数、投资金额
        buffer.append(" ) q ");
        buffer.append(" ON DATE_FORMAT(dd.insert_time, '%Y-%m-%d' ) = q.date ");
        buffer.append(" LEFT JOIN (");
        buffer.append(bindCountSQL());//银行卡绑定人数
        buffer.append(" ) w ");
        buffer.append(" ON DATE_FORMAT(dd.insert_time, '%Y-%m-%d' )= w.date ");
        buffer.append(" LEFT JOIN (");
        buffer.append(ftCountSQL());//复投人数
        buffer.append(" )e ON DATE_FORMAT(dd.insert_time, '%Y-%m-%d' )=e.date ");
        return buffer.toString();
    }

}
