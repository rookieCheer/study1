package com.huoq.admin.dailyStatement.bean;

import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.DailyStatement;
import com.huoq.thread.dao.ThreadDAO;
import com.huoq.util.MyLogger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 李瑞丽
 * @Date: Created in 17:53 2018/1/16
 */
@Service
public class UpdateDailyStatementThreadBean {
    @Resource
    private ThreadDAO dao;
    private static MyLogger log = MyLogger.getLogger(UpdateDailyStatementThreadBean.class);

    /**
     * 分页查询日报表数据
     * @param pageUtil  分页
     * @param insertTime
     * @return
     */
    public PageUtil<DailyStatement> findDailyStatement(PageUtil pageUtil, String insertTime) {
        List<Object> list = new ArrayList<Object>();
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT id,insert_time,tradingVolume,loanAmountAll,loanAmount,reimbursementAmount,reimbursementAmountAll, ");
            sql.append("interestpayment,todayOutCashMoney,returnInvestmentRate,capitalInflow, netInflow,capitalStock,activationCount, ");
            sql.append("investCount,todayregisterCount, todaycertificationCount,todayNewBuyNumber,firstPercentConversion,firstInvestmentTotalMoney,firstInvestmentMoney, ");
            sql.append("reInvestmentMoney,amountNewMoney,reInvestmentCount,addReInvestmentCount,addReInvestmentMoney,reInvestmentAmount,multipleRate, ");
            sql.append("occupationRatio,reInvestmentRate,sumMoney,capitaInvestmentMoney ");
            sql.append("FROM daily_statement  ");
            sql.append("where 1=1 ");
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    sql.append(" AND insert_time  BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') ");
                    list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
                    list.add(QwyUtil.fmMMddyyyy.parse(time[1]));
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
            List<DailyStatement> dailyStatements = toDailyStatement(objlist);
            bySqlAndSqlCount.setList(dailyStatements);
            return bySqlAndSqlCount;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private List<DailyStatement> toDailyStatement(List<Object[]> objlist) {
        List<DailyStatement> list = new ArrayList<>();
        try {
            for(Object[] obj:objlist){
                DailyStatement dailyStatement = new DailyStatement();
                dailyStatement.setId(!QwyUtil.isNullAndEmpty(obj[0])?Long.valueOf(obj[0]+""):0);
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
                Date date = sd.parse(!QwyUtil.isNullAndEmpty(obj[1])?(obj[1] + ""):null);
                dailyStatement.setInsertTime(date);
                dailyStatement.setTradingVolume(!QwyUtil.isNullAndEmpty(obj[2])?Double.valueOf(obj[2]+""):0.0);
                //在贷金额（含零钱罐）
                dailyStatement.setLoanAmountAll(!QwyUtil.isNullAndEmpty(obj[3])?Double.valueOf(obj[3]+""):0.0);
                //在贷金额（不含零钱罐）
                dailyStatement.setLoanAmount(!QwyUtil.isNullAndEmpty(obj[4])?Double.valueOf(obj[4]+""):0.0);
                //回款金额（不含零钱罐）
                dailyStatement.setReimbursementAmount(!QwyUtil.isNullAndEmpty(obj[5])?Double.valueOf(obj[5]+""):0.0);
                //回款金额（含零钱罐及余额）
                dailyStatement.setReimbursementAmountAll(!QwyUtil.isNullAndEmpty(obj[6])?Double.valueOf(obj[6]+""):0.0);
                //支付利息
                dailyStatement.setInterestpayment(!QwyUtil.isNullAndEmpty(obj[7])?Double.valueOf(obj[7]+""):0.0);
                // 今日提现金额
                dailyStatement.setTodayOutCashMoney(!QwyUtil.isNullAndEmpty(obj[8])?Double.valueOf(obj[8]+""):0.0);
                //回款用户投资率
                dailyStatement.setReturnInvestmentRate(!QwyUtil.isNullAndEmpty(obj[9])?Double.valueOf(obj[9]+""):0.0);
                // 资金流入额
                dailyStatement.setCapitalInflow(!QwyUtil.isNullAndEmpty(obj[10])?Double.valueOf(obj[10]+""):0.0);
                // 净流入金额
                dailyStatement.setNetInflow(!QwyUtil.isNullAndEmpty(obj[11])?Double.valueOf(obj[11]+""):0.0);
                // 资金存量
                dailyStatement.setCapitalStock(!QwyUtil.isNullAndEmpty(obj[12])?Double.valueOf(obj[12]+""):0.0);
                //激活用户数
                dailyStatement.setActivationCount(!QwyUtil.isNullAndEmpty(obj[13])?Integer.valueOf(obj[13]+""):0);
                //投资用户数
                dailyStatement.setInvestCount(!QwyUtil.isNullAndEmpty(obj[14])?Integer.valueOf(obj[14]+""):0);
                // 今日注册人数
                dailyStatement.setTodayregisterCount(!QwyUtil.isNullAndEmpty(obj[15])?Integer.valueOf(obj[15]+""):0);
                // 今日认证用户
                dailyStatement.setTodaycertificationCount(!QwyUtil.isNullAndEmpty(obj[16])?Integer.valueOf(obj[16]+""):0);
                // 今日首投用户
                dailyStatement.setTodayNewBuyNumber(!QwyUtil.isNullAndEmpty(obj[17])?Integer.valueOf(obj[17]+""):0);
                // 首投用户转化率
                dailyStatement.setFirstPercentConversion(!QwyUtil.isNullAndEmpty(obj[18])?Double.valueOf(obj[18]+""):0.0);
                dailyStatement.setFirstInvestmentTotalMoney(!QwyUtil.isNullAndEmpty(obj[19])?Double.valueOf(obj[19]+""):0.0);
                //首投客单金额（元）
                dailyStatement.setFirstInvestmentMoney(!QwyUtil.isNullAndEmpty(obj[20])?Double.valueOf(obj[20]+""):0.0);
                //复投金额（元）
                dailyStatement.setReInvestmentMoney(!QwyUtil.isNullAndEmpty(obj[21])?Double.valueOf(obj[21]+""):0.0);
                //零钱罐新增金额（元）
                dailyStatement.setAmountNewMoney(!QwyUtil.isNullAndEmpty(obj[22])?Double.valueOf(obj[22]+""):0.0);
                //复投用户数
                dailyStatement.setReInvestmentCount(!QwyUtil.isNullAndEmpty(obj[23])?Integer.valueOf(obj[23]+""):0);
                //新增复投用户数
                dailyStatement.setAddReInvestmentCount(!QwyUtil.isNullAndEmpty(obj[24])?Integer.valueOf(obj[24]+""):0);
                //新增复投用户投资总额
                dailyStatement.setAddReInvestmentMoney(!QwyUtil.isNullAndEmpty(obj[25])?Double.valueOf(obj[25]+""):0.0);
                //复投次数
                dailyStatement.setReInvestmentAmount(!QwyUtil.isNullAndEmpty(obj[26])?Integer.valueOf(obj[26]+""):0);
                //新增复投率（%）
                dailyStatement.setMultipleRate(!QwyUtil.isNullAndEmpty(obj[27])?Double.valueOf(obj[27]+""):0.0);
                //复投用户占比（%）
                dailyStatement.setOccupationRatio(!QwyUtil.isNullAndEmpty(obj[28])?Double.valueOf(obj[28]+""):0.0);
                //复投金额占比（%）
                dailyStatement.setReInvestmentRate(!QwyUtil.isNullAndEmpty(obj[29])?Double.valueOf(obj[29]+""):0.0);
                // 复投客单金额（元）
                dailyStatement.setSumMoney(!QwyUtil.isNullAndEmpty(obj[30])?Double.valueOf(obj[30]+""):0.0);
                //人均投资金额（元）
                dailyStatement.setCapitaInvestmentMoney(!QwyUtil.isNullAndEmpty(obj[31])?Double.valueOf(obj[31]+""):0.0);
                list.add(dailyStatement);
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
    public void updateDailyStatement(Date yestady) {
        List<Date> time = new ArrayList<>();
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        try {
            long a1 = System.currentTimeMillis();
            //查询昨日
            String yesterday = QwyUtil.fmyyyyMMdd.format(QwyUtil.addDaysFromOldDate(new Date(), -2).getTime());
            if(!QwyUtil.isNullAndEmpty(yestady)){
                time.add(yestady);
            }
            for (Date date : time) {
                String today = sd.format(date);
                //根据时间去补全所有数据
                DailyStatement dailyStatement = new DailyStatement();
                dailyStatement.setInsertTime(yestady);

                // 今日提现金额
                Double updateTodayOutCashMoney = uodateTodayOutCashMoney(today);
                dailyStatement.setTodayOutCashMoney(Double.valueOf(updateTodayOutCashMoney.toString().replaceAll(",", "")));
                //激活用户数
                Integer updateActivationCount = updateActivationCount(today);
                dailyStatement.setActivationCount(Integer.valueOf(updateActivationCount.toString().replaceAll(",","")));

                //平台今日资金存量总额
                Double todayCapitalStock = updateTodayCapitalStock(today);
                dailyStatement.setCapitalStock(Double.valueOf(todayCapitalStock.toString().replaceAll(",", "")));

                // 今日注册用户
                Integer updateTodayregisterCount = updateTodayregisterCount(today);
                dailyStatement.setTodayregisterCount(Integer.valueOf(updateTodayregisterCount.toString().replaceAll(",", "")));
                // 今日首投人数
                Integer updateTodayfirstBuyNumber = updateTodayfirstBuyNumber(today);
                dailyStatement.setTodayNewBuyNumber(Integer.valueOf(updateTodayfirstBuyNumber.toString().replaceAll(",", "")));
                //首投总金额
                Double firstInvestmentTotalMoney = updateFirstInvestmentTotalMoney(today);
                dailyStatement.setFirstInvestmentTotalMoney(Double.valueOf(firstInvestmentTotalMoney.toString().replaceAll(",", "")));

                //复投用户数
                Integer reInvestmentCount = updateReInvestmentCount(today);
                dailyStatement.setReInvestmentCount(Integer.valueOf(reInvestmentCount.toString().replaceAll(",", "")));
                //复投总金额
                Double reInvestmentMoney = updateReInvestmentMoney(today);
                dailyStatement.setReInvestmentMoney(Double.valueOf(reInvestmentMoney.toString().replaceAll(",", "")));

                //新增复投用户数
                Integer addReInvestmentCount = updateAddReInvestmentCount(today);
                dailyStatement.setAddReInvestmentCount(Integer.valueOf(addReInvestmentCount.toString().replaceAll(",", "")));
                //新增复投总金额
                Double addReInvestmentMoney =updateAddReInvestmentMoney(today);
                dailyStatement.setAddReInvestmentMoney(Double.valueOf(reInvestmentMoney.toString().replaceAll(",", "")));

                dao.saveOrUpdate(dailyStatement);
            }
            long a2 = System.currentTimeMillis();
            log.info("-------------updateAllCapitalStock【更新平台资金概览DailyStatement】耗时:" + (a2 - a1));
        } catch (Exception e) {
        e.printStackTrace();
        log.error(e);
    }
}




    /**
     *  更新今日提现金额
     * @param insertTime
     * @return
     */
    private Double uodateTodayOutCashMoney(String insertTime) {
        try {
            List<Object> list = new ArrayList<Object>();
            list.add(insertTime);
            list.add(insertTime);
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT FORMAT(SUM(tr.money/100),2) FROM tx_record tr "
                    + "WHERE tr.is_check = '1' AND tr.status = '1'  AND tr.check_time "
                    + "BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00')  AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59') ");
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
     * 更新今日激活用户数
     * @param insertTime
     * @return
     */
    private Integer updateActivationCount(String insertTime) {
        try {
            List<Object> list = new ArrayList<Object>();
            list.add(insertTime);
            list.add(insertTime);
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT SUM(q.activityCount) FROM qdtj  q WHERE q.insert_time BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59') ");
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
     * 平台今日资金存量总额
     * @param insertTime
     * @return
     */
    private Double updateTodayCapitalStock(String insertTime) {
        try {
            List<Object> list = new ArrayList<Object>();
            list.add(insertTime);
            list.add(insertTime);
            list.add(insertTime);
            list.add(insertTime);
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT FORMAT(SUM(money),2) FROM(  ");
            sql.append(
                    "SELECT SUM(cz.money)/100 money FROM cz_record cz WHERE cz.STATUS = '1'  AND TYPE = '1' AND cz.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') ");
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
     * 更新今日注册人数
     */
    private Integer updateTodayregisterCount(String insertTime) {
        try {
            List<Object> list = new ArrayList<Object>();
            list.add(insertTime);
            list.add(insertTime);
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT COUNT(1) FROM users u WHERE u.insert_time BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT (?, '%Y-%m-%d 23:59:59') ");
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
     * 更新平台今日首投用户数
     * @param insertTime
     * @return
     */
    private Integer updateTodayfirstBuyNumber(String insertTime) {
        try {
            List<Object> list = new ArrayList<Object>();
            list.add(insertTime);
            list.add(insertTime);
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT SUM(q.strs) FROM qdtj  q WHERE q.insert_time BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59') ");
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
     * 更新首投总金额
     * @param insertTime
     * @return
     */
    private Double updateFirstInvestmentTotalMoney(String insertTime) {
        try {
            List<Object> list = new ArrayList<Object>();
            list.add(insertTime);
            list.add(insertTime);
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT SUM(q.stje) FROM qdtj q WHERE q.insert_time BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59') ");
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Double firstInvestmentTotalMoney = 0.0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
                firstInvestmentTotalMoney = Double.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
            }
            return firstInvestmentTotalMoney;
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }


    /**
     * 更新复投用户数
     * @param insertTime
     * @return
     */
    private Integer updateReInvestmentCount(String insertTime) {
        try {
            List<Object> list = new ArrayList<Object>();
            list.add(insertTime);
            list.add(insertTime);
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT SUM(q.ftrs) FROM qdtj  q WHERE q.insert_time BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59') ");
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Integer reInvestmentCount = 0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
                reInvestmentCount = Integer.valueOf((loadAllSql.get(0) + ""));
            }
            return reInvestmentCount;
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }

    /**
     * 更新复投总金额
     * @param insertTime
     * @return
     */
    private Double updateReInvestmentMoney(String insertTime) {
        try {
            List<Object> list = new ArrayList<Object>();
            list.add(insertTime);
            list.add(insertTime);
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT SUM(q.ftje) FROM qdtj  q WHERE q.insert_time BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59') ");
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Double reInvestmentMoney = 0.0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
                reInvestmentMoney = Double.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
            }
            return reInvestmentMoney;
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }


    /**
     * 更新新增复投用户
     * @param insertTime
     * @return
     */
    private Integer updateAddReInvestmentCount(String insertTime) {
        try {
            List<Object> list = new ArrayList<Object>();
            list.add(insertTime);
            list.add(insertTime);
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT SUM(q.xzftyh) FROM qdtj  q WHERE q.insert_time BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59') ");
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Integer addReInvestmentCount = 0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
                addReInvestmentCount = Integer.valueOf((loadAllSql.get(0) + ""));
            }
            return addReInvestmentCount;
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }

    /**
     * 更新新增复投用户投资总额
     * @param insertTime
     * @return
     */
    private Double updateAddReInvestmentMoney(String insertTime) {
        try {
            List<Object> list = new ArrayList<Object>();
            list.add(insertTime);
            list.add(insertTime);
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT SUM(q.xhftyhtzze) FROM qdtj  q WHERE q.insert_time BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59') ");
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Double addReInvestmentMoney = 0.0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
                addReInvestmentMoney = Double.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
            }
            return addReInvestmentMoney;
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }



}
