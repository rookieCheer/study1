package com.huoq.thread.bean;

import com.huoq.common.bean.PlatformBean;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.DataOverview;
import com.huoq.thread.dao.ThreadDAO;
import com.huoq.util.MyLogger;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

/**
 * 更新平台资金存量bean
 *
 * @author Administrator
 */
@Service
public class UpdateDataOverviewThreadBean {
    @Resource
    private ThreadDAO dao;
    @Resource
    private PlatformBean bean;

    private static MyLogger log = MyLogger.getLogger(UpdateDataOverviewThreadBean.class);

    private static ResourceBundle resbSms = ResourceBundle.getBundle("sms-notice");

    /**
     * 分页查询首页概览数据
     * @param pageUtil
     * @param insertTime
     * @return
     */
    public PageUtil<DataOverview> findDataOverview(PageUtil pageUtil, String insertTime)  {
        List<Object> list = new ArrayList<Object>();
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT id,insert_time,all_capital_stock,today_capital_stock,all_out_cash_money, ");
            sql.append("today_out_cash_money,recharge_money,today_recharge_money,today_buy_money,");
            sql.append("register_count,todayregister_count,certification_count,todaycertification_count,");
            sql.append("today_buy_number,today_new_buy_number,today_uauditing_out_cash_money ");
            sql.append("FROM data_overview ");
            sql.append("where 1=1 ");
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    sql.append(" AND insert_time  BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') ");
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
                } else {
                    sql.append(" AND insert_time  BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') ");
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            }
            sql.append(" ORDER BY insert_time DESC ");
            StringBuffer sqlCount = new StringBuffer();
            sqlCount.append("SELECT COUNT(*) ");
            sqlCount.append("FROM (");
            sqlCount.append(sql);
            sqlCount.append(") c");
            PageUtil bySqlAndSqlCount = dao.getBySqlAndSqlCount(pageUtil,sql.toString(), sqlCount.toString(), list.toArray());
            List<Object[]> objlist = bySqlAndSqlCount.getList();
            List<DataOverview> dataOverviews = toDataOverview(objlist);
            bySqlAndSqlCount.setList(dataOverviews);
            return bySqlAndSqlCount;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public List<DataOverview> toDataOverview(List<Object[]>  objects){
        List<DataOverview> list = new ArrayList<>();
        try {
            for(Object[] obj:objects){
                DataOverview dataOverview = new DataOverview();
                dataOverview.setId(!QwyUtil.isNullAndEmpty(obj[0])?Long.valueOf(obj[0]+""):0);
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
                Date date = sd.parse(!QwyUtil.isNullAndEmpty(obj[1])?(obj[1] + ""):null);
                dataOverview.setInsertTime(date);
                dataOverview.setAllCapitalStock(!QwyUtil.isNullAndEmpty(obj[2])?Double.valueOf(obj[2]+""):0.0);
                dataOverview.setTodayCapitalStock(!QwyUtil.isNullAndEmpty(obj[3])?Double.valueOf(obj[3]+""):0.0);
                dataOverview.setAllOutCashMoney(!QwyUtil.isNullAndEmpty(obj[4])?Double.valueOf(obj[4]+""):0.0);
                dataOverview.setTodayOutCashMoney(!QwyUtil.isNullAndEmpty(obj[5])?Double.valueOf(obj[5]+""):0.0);
                dataOverview.setRechargeMoney(!QwyUtil.isNullAndEmpty(obj[6])?Double.valueOf(obj[6]+""):0.0);
                dataOverview.setTodayrechargeMoney(!QwyUtil.isNullAndEmpty(obj[7])?Double.valueOf(obj[7]+""):0.0);
                dataOverview.setTodayBuyMoney(!QwyUtil.isNullAndEmpty(obj[8])?Double.valueOf(obj[8]+""):0.0);
                dataOverview.setRegisterCount(!QwyUtil.isNullAndEmpty(obj[9])?Integer.valueOf(obj[9]+""):0);
                dataOverview.setTodayregisterCount(!QwyUtil.isNullAndEmpty(obj[10])?Integer.valueOf(obj[10]+""):0);
                dataOverview.setCertificationCount(!QwyUtil.isNullAndEmpty(obj[11])?Integer.valueOf(obj[11]+""):0);
                dataOverview.setTodaycertificationCount(!QwyUtil.isNullAndEmpty(obj[12])?Integer.valueOf(obj[12]+""):0);
                dataOverview.setTodayBuyNumber(!QwyUtil.isNullAndEmpty(obj[13])?Integer.valueOf(obj[13]+""):0);
                dataOverview.setTodayNewBuyNumber(!QwyUtil.isNullAndEmpty(obj[14])?Integer.valueOf(obj[14]+""):0);
                dataOverview.setTodayUAuditingOutCashMoney(!QwyUtil.isNullAndEmpty(obj[15])?Double.valueOf(obj[15]+""):0);
                list.add(dataOverview);
            }
            return list;
        }catch ( Exception e){
            e.printStackTrace();
        }
        return null;

    }


    /**
     * 更新平台资金概览
     */
    public void updateDataOverview(Date yestady) {
        List<Date> time = new ArrayList<>();
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        try {
            long a1 = System.currentTimeMillis();
            //查询昨日资金增量
            String yesterday = QwyUtil.fmyyyyMMdd.format(QwyUtil.addDaysFromOldDate(new Date(), -2).getTime());
            if(!QwyUtil.isNullAndEmpty(yestady)){
                time.add(yestady);
            }
 /*           if(QwyUtil.isNullAndEmpty(yestady)){
                //查询时间类
                StringBuffer sql = new StringBuffer();
                sql.append("SELECT * FROM weeks");
                List<Object> list = dao.LoadAllSql(sql.toString(), null);
                time = (List<Date>) (List) list;
            }*/
            for (Date date : time) {
               String today = sd.format(date);
                //根据时间去补全所有的首页数据
                DataOverview dataOverview = new DataOverview();
                dataOverview.setInsertTime(yestady);
                // 今日充值金额
                Double updateTodayrechargeMoney = updateTodayrechargeMoney(today);
                dataOverview.setTodayrechargeMoney(Double.valueOf(updateTodayrechargeMoney.toString().replaceAll(",", "")));
                // 今日存量增量
                Double todayCapitalStock = updateTodayCapitalStock(today);
                dataOverview.setTodayCapitalStock(Double.valueOf(todayCapitalStock.toString().replaceAll(",", "")));
                // 更新平台资金存量(用昨天的存量加昨天的总存量)
                Double AllCapitalStock = updateAllCapitalStock(yesterday);
                dataOverview.setAllCapitalStock(!QwyUtil.isNullAndEmpty(AllCapitalStock)?((Double.valueOf(AllCapitalStock.toString().replaceAll(",", "")))+dataOverview.getTodayCapitalStock()):0.0);
                // 累计提现金额
                Double uodateAllOutCashMoney = uodateAllOutCashMoney(today);
                dataOverview.setAllOutCashMoney(Double.valueOf(uodateAllOutCashMoney.toString().replaceAll(",", "")));
                // 今日提现金额
                Double uodateTodayOutCashMoney = uodateTodayOutCashMoney(today);
                dataOverview.setTodayOutCashMoney(Double.valueOf(uodateTodayOutCashMoney.toString().replaceAll(",", "")));
                // 今日购买金额
                Double updateTodayBuyMoney = updateTodayBuyMoney(today);
                dataOverview.setTodayBuyMoney(Double.valueOf(updateTodayBuyMoney.toString().replaceAll(",", "")));
                // 累计充值金额
                Double rechargeMoney = updateRechargeMoney(today);
                dataOverview.setRechargeMoney(Double.valueOf(rechargeMoney.toString().replaceAll(",", "")));
                // 更新累计注册用户
                Integer updateRegisterCount = updateRegisterCount(today);
                dataOverview.setRegisterCount(Integer.valueOf(updateRegisterCount.toString().replaceAll(",", "")));
                // 累计绑定用户
                Integer updateCertificationCount = updateCertificationCount(today);
                dataOverview.setCertificationCount(Integer.valueOf(updateCertificationCount.toString().replaceAll(",", "")));
                // 今日注册用户
                Integer updateTodayregisterCount = updateTodayregisterCount(today);
                dataOverview.setTodayregisterCount(Integer.valueOf(updateTodayregisterCount.toString().replaceAll(",", "")));
                // 今日绑定用户
                Integer updateTodaycertificationCount = updateTodaycertificationCount(today);
                dataOverview.setTodaycertificationCount(Integer.valueOf(updateTodaycertificationCount.toString().replaceAll(",", "")));
                // 今日购买人数
                Integer updateTodayBuyNumber = updateTodayBuyNumber(today);
                dataOverview.setTodayBuyNumber(Integer.valueOf(updateTodayBuyNumber.toString().replaceAll(",", "")));
                // 今日首投人数
                Integer updateTodayfirstBuyNumber = updateTodayfirstBuyNumber(today);
                dataOverview.setTodayNewBuyNumber(Integer.valueOf(updateTodayfirstBuyNumber.toString().replaceAll(",", "")));
                //提现未审核
                Double updateTodayUAuditingOutCashMoney = updateTodayUAuditingOutCashMoney(today);
                dataOverview.setTodayUAuditingOutCashMoney(Double.valueOf(updateTodayUAuditingOutCashMoney.toString().replaceAll(",", "")));
                dao.saveOrUpdate(dataOverview);
            }
            long a2 = System.currentTimeMillis();
            log.info("-------------updateAllCapitalStock【更新平台资金概览】耗时:" + (a2 - a1));
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e);
        }
    }


    /**
     * 今日未审核提现总额(元)
     *
     * @param insertTime
     */
    public Double updateTodayUAuditingOutCashMoney(String insertTime) {
        try {
            List<Object> list = new ArrayList<Object>();
            list.add(insertTime);
            list.add(insertTime);
            StringBuffer sql = new StringBuffer();
            sql.append(" SELECT SUM(tx.money)/100 FROM `tx_record` tx WHERE tx.insert_time BETWEEN  DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59')  ");
            sql.append(" AND tx.is_check = 0 ");
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Double todayUAuditingOutCashMoney = 0.0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
                todayUAuditingOutCashMoney = Double.valueOf((loadAllSql.get(0) + ""));
            }
            return todayUAuditingOutCashMoney;
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }

    /**
     * 更新平台今日首投用户数
     *
     * @param insertTime
     */
    public Integer updateTodayfirstBuyNumber(String insertTime) {
        try {
            List<Object> list = new ArrayList<Object>();
            list.add(insertTime);
            list.add(insertTime);
            StringBuffer sql = new StringBuffer();
            sql.append(
                    " SELECT COUNT(DISTINCT i.users_id) FROM investors i LEFT JOIN product p ON p.id = i.product_id WHERE i.investor_status = '1' AND i.insert_time  ");
            sql.append(
                    "BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59' ) AND p.product_type = 1");
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Integer todayNewBuyNumber = 0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
                todayNewBuyNumber = Integer.valueOf((loadAllSql.get(0) + ""));
            }
            return todayNewBuyNumber;
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }

    /**
     * 更新平台资金总存量
     *
     * @param insertTime
     */
    public Double updateAllCapitalStock(String insertTime) {
        try {
            List<Object> list = new ArrayList<Object>();
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT all_capital_stock FROM data_overview WHERE 1=1 ");
            if(!QwyUtil.isNullAndEmpty(insertTime)){
                list.add(insertTime);
                list.add(insertTime);
                sql.append("AND insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') ");
            }
            if(QwyUtil.isNullAndEmpty(insertTime)){
                Date date = new Date();
                list.add(date);
                list.add(date);
                sql.append("AND insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') ");
            }
            //String yesterday = QwyUtil.fmyyyyMMdd.format(QwyUtil.addDaysFromOldDate(new Date(), -1).getTime());
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Double allCapitalStock = 0.0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
                allCapitalStock = Double.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
            }
            return allCapitalStock;
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }

    /**
     * 今日平台增量
     */
    public Double updateTodayCapitalStock(String insertTime) {
        try {
            List<Object> list = new ArrayList<Object>();
            list.add(insertTime);
            list.add(insertTime);
            list.add(insertTime);
            list.add(insertTime);
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT FORMAT(SUM(money),2) FROM(  ");
            sql.append(
                    "SELECT SUM(cz.money)/100 money FROM cz_record cz WHERE cz.STATUS = '1' AND TYPE = '1' AND cz.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') ");
            sql.append("UNION ALL ");
            sql.append(
                    "SELECT SUM(-tr.money/100) money FROM tx_record tr WHERE tr.is_check = '1'  AND tr.status = '1' AND tr.check_time BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59'))t ");
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Double todayCapitalStock = 0.0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
                todayCapitalStock = Double.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
            }
            return todayCapitalStock;
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 今日充值金额
     */
    public Double updateTodayrechargeMoney(String insertTime) {
        try {
            List<Object> list = new ArrayList<Object>();
            list.add(insertTime);
            list.add(insertTime);
            StringBuffer sql = new StringBuffer();
            sql.append(
                    "SELECT FORMAT(SUM(cz.money)/100,0) money FROM cz_record cz WHERE cz.STATUS = '1' AND TYPE = '1' ");
            sql.append(
                    "AND cz.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59')");
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Double todayrechargeMoney = 0.0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
                todayrechargeMoney = Double.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
            }
            return todayrechargeMoney;
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 今日购买人数
     */
    public Integer updateTodayBuyNumber(String insertTime) {
        try {
            List<Object> list = new ArrayList<Object>();
            list.add(insertTime);
            list.add(insertTime);
            list.add(insertTime);
            list.add(insertTime);
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT SUM(1) FROM ( " + "SELECT SUM(t.number) t FROM ( "
                    + "SELECT COUNT(i.id) number,i.users_id userid FROM investors i WHERE i.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') AND i.investor_status IN('1','2','3') GROUP BY users_id "
                    + "UNION ALL "
                    + "SELECT COUNT(cpfr.id) number,cpfr.users_id userid   FROM coin_purse_funds_record  cpfr WHERE cpfr.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') AND cpfr.type='to' GROUP BY users_id)t GROUP BY t.userid)a ");
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Integer todayBuyMoney = 0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
                todayBuyMoney = Integer.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
            }
            return todayBuyMoney;
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 今日购买金额
     */
    public Double updateTodayBuyMoney(String insertTime) {
        try {
            List<Object> list = new ArrayList<Object>();
            list.add(insertTime);
            list.add(insertTime);
            list.add(insertTime);
            list.add(insertTime);
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT SUM(t.money) FROM ( "
                    + "SELECT SUM(i.in_money)/100 money FROM investors i WHERE i.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') AND i.investor_status IN('1','2','3') "
                    + "UNION ALL "
                    + "SELECT SUM(cpfr.money)/100 money  FROM coin_purse_funds_record  cpfr WHERE cpfr.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') AND cpfr.type='to')t ");
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Double todayBuyMoney = 0.0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
                todayBuyMoney = Double.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
            }
            return todayBuyMoney;
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 更新当日认证人数
     */
    public Integer updateTodaycertificationCount(String insertTime) {
        try {
            List<Object> list = new ArrayList<Object>();
            list.add(insertTime);
            list.add(insertTime);
            StringBuffer sql = new StringBuffer();
            sql.append(
                    "SELECT COUNT(1) FROM account ac WHERE ac.insert_time  BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59') ");
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Integer registerCount = 0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0)) && loadAllSql != null) {
                registerCount = Integer.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
            }
            return registerCount;
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 更新平台总认证人数
     */
    public Integer updateCertificationCount(String insertTime) {
        try {
            List<Object> list = new ArrayList<Object>();
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT COUNT(1) FROM account ac WHERE ac.STATUS = 1 ");
            list.add(insertTime);
            sql.append("AND ac.insert_time < DATE_FORMAT(?, '%Y-%m-%d 23:59:59')");
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Integer registerCount = 0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
                registerCount = Integer.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
            }
            return registerCount;
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 更新今日注册人数
     */
    public Integer updateTodayregisterCount(String insertTime) {
        try {
            List<Object> list = new ArrayList<Object>();
            list.add(insertTime);
            list.add(insertTime);
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT COUNT(1) FROM users u WHERE u.insert_time BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59') ");
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Integer registerCount = 0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
                registerCount = Integer.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
            }
            return registerCount;
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 更新累计提现金额
     */
    public Double uodateAllOutCashMoney(String insertTime) {
        try {
            StringBuffer sql = new StringBuffer();
            List<Object> list = new ArrayList<Object>();
            sql.append("SELECT FORMAT(SUM(tr.money/100),2) FROM tx_record tr WHERE tr.is_check = '1' AND tr.status = '1' AND tr.check_time ");
            list.add(insertTime);
            sql.append(" < DATE_FORMAT(?, '%Y-%m-%d 23:59:59')");
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Double allMoney = 0.0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
                allMoney = Double.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
            }
            return allMoney;
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 更新今日提现金额
     */
    public Double uodateTodayOutCashMoney(String insertTime) {
        try {
            List<Object> list = new ArrayList<Object>();
            list.add(insertTime);
            list.add(insertTime);
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT FORMAT(SUM(tr.money/100),2) FROM tx_record tr "
                    + "WHERE tr.is_check = '1' AND tr.status = '1' AND tr.check_time "
                    + "BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59') ");
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Double allMoney = 0.0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
                allMoney = Double.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
            }
            return allMoney;
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }


    /**
     * 更新平台充值总额;
     * <p>
     * 已募集产品的总额(分)
     */
    public Double updateRechargeMoney(String insertTime) {
        try {
            List<Object> list = new ArrayList<Object>();
            // 获取昨日充值金额
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT FORMAT(SUM(cz.money)/100,0) money FROM cz_record cz WHERE cz.STATUS = '1' AND TYPE = '1' ");
            list.add(insertTime);
            sql.append("AND cz.insert_time < DATE_FORMAT(?, '%Y-%m-%d 23:59:59') ");
            Object object = dao.LoadAllSql(sql.toString(), list.toArray()).get(0);
            Double allmoney = 0.0;
            if (!QwyUtil.isNullAndEmpty(object)) {
                allmoney = Double.valueOf(dao.LoadAllSql(sql.toString(), list.toArray()).get(0).toString().replaceAll(",", ""));
            }
            //查询充值金额差额数据
            List<Double> money = dao.LoadAllSql("select recharge_money from recharge_money ", null);
            Double inmoney = 0.0;
            if(!QwyUtil.isNullAndEmpty(money) && !QwyUtil.isNullAndEmpty(money.get(0))){
                inmoney = money.get(0);
            }
            //1583776
            return allmoney + inmoney;
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 更新平台的注册人数;<br>
     * 用户注册成功之后调用此方法;
     */
    public Integer updateRegisterCount(String insertTime) {
        try {
            List<Object> list = new ArrayList<Object>();
            StringBuffer sql = new StringBuffer("SELECT SUM(1) FROM users u ");
            list.add(insertTime);
            sql.append("WHERE u.insert_time < DATE_FORMAT(?, '%Y-%m-%d 23:59:59')");
            Object object = dao.LoadAllSql(sql.toString(), list.toArray()).get(0);
            Integer parseInt = 0;
            if (!QwyUtil.isNullAndEmpty(object)) {
                parseInt = Integer.valueOf(dao.LoadAllSql(sql.toString(), list.toArray()).get(0).toString());
            }
            return parseInt;
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 获取昨日今日平台增量
     */
    public Double findTodayCapitalStock(String insertTime) {
        try {
            List<Object> list = new ArrayList<Object>();
            list.add(insertTime);
            list.add(insertTime);
            list.add(insertTime);
            list.add(insertTime);
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT FORMAT(SUM(money),2) FROM(  ");
            sql.append(
                    "SELECT SUM(cz.money)/100 money FROM cz_record cz WHERE cz.STATUS = '1' AND TYPE = '1' AND cz.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') ");
            sql.append("UNION ");
            sql.append(
                    "SELECT SUM(-tr.money/100) money FROM tx_record tr WHERE tr.is_check = '1' AND tr.check_time BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59'))t ");
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Double todayCapitalStock = 0.0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
                todayCapitalStock = Double.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
            }
            return todayCapitalStock;
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    public List listBySql(Object[] params,String sql){
        return dao.LoadAllSql(sql,params);
    }

}
