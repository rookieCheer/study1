package com.huoq.admin.dailyStatement.bean;

import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.DailyStatement;
import com.huoq.thread.dao.ThreadDAO;
import com.huoq.util.MyLogger;
import javafx.beans.binding.When;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author 李瑞丽
 * @Date: Created in 17:53 2018/1/16
 */
@Service
public class UpdateDailyStatementBean {
    @Resource
    private ThreadDAO dao;
    private static MyLogger log = MyLogger.getLogger(UpdateDailyStatementBean.class);

    /**
     * 新增日报表数据
     */
    public void addDailyStatement(String insertTime) {
        StringBuffer sql = new StringBuffer();
        List<Date> list = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            //根据需要查询的时间查询日报表内的数据
            sql.append("SELECT * FROM daily_statement WHERE 1=1 ");
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    sql.append(" AND insert_time  BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') ");
                    list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
                    list.add(QwyUtil.fmMMddyyyy.parse(time[1]));
                } else {
                    sql.append(" AND insert_time  BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') ");
                    list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
                    list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
                }
            }
            //将查询的时间段转换为具体的时间
            List<Date> arrayList = new ArrayList<Date>();
            for (int i = 0; i < list.size(); i++) {
                Date start_time = list.get(0);
                Date end_time = list.get(1);
                if (start_time != null && end_time != null) {
                    if (start_time.equals(end_time)) {
                        //处理开始日期＝结束日期，重复的问题
                        arrayList.add(start_time);
                        break;
                    } else {
                        arrayList = getDatesBetweenTwoDate(start_time, end_time);
                    }
                }
            }
            //查询日报表中有没有这个时间段的数据
            List<Object[]> dailyStatementList = dao.LoadAllSql(sql.toString(), list.toArray());
            //如果查询出的结果为存遍历
            if (!QwyUtil.isNullAndEmpty(dailyStatementList)) {
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
                //将旧的时间拼接为字符串
                String olddate = "";
                Date date1 = null;
                for (int i = 0; i < dailyStatementList.size(); i++) {
                    String date = dailyStatementList.get(i)[1].toString();
                    date1 = sd.parse(date);
                    olddate += date1 + ",";
                }
                //遍历传入的数据
                for (int i = 0; i < arrayList.size(); i++) {
                    //遍历查询出的数据
                    Date date = arrayList.get(i);
                    if (i <= dailyStatementList.size() - 1) {
                        //查询的时间在日报表中存在则不做任何处理
                        if (olddate.contains(date.toString())) {

                        } else {
                            //更新平台资金概览数据
                            updateDailyStatement(date);
                        }
                    }
                    if (i > dailyStatementList.size() - 1) {
                        //查询的时间在日报表中存在则不做任何处理
                        if (olddate.contains(date.toString())) {

                        } else {
                            //更新平台资金概览数据
                            updateDailyStatement(date);
                        }
                    }
                }
            } else {
                //如果查询没有结果则直接更新数据(将时间段转换为具体的时间)
                for (Date date : arrayList) {
                    //更新平台资金概览数据
                    updateDailyStatement(date);
                }
            }
        } catch (Exception e) {
            log.error(e);
        }
        long et = System.currentTimeMillis();
    }


    /**
     * 将时间段转换为具体的时间
     *
     * @param beginDate
     * @param endDate
     * @return
     */
    public List<Date> getDatesBetweenTwoDate(Date beginDate, Date endDate) {
        List<Date> lDate = new ArrayList<Date>();
        lDate.add(beginDate);
        // 把开始时间加入集合
        Calendar cal = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        cal.setTime(beginDate);
        boolean bContinue = true;
        while (bContinue) {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            cal.add(Calendar.DAY_OF_MONTH, 1);
            // 测试此日期是否在指定日期之后
            if (endDate.after(cal.getTime())) {
                lDate.add(cal.getTime());
            } else {
                break;
            }
        }
        lDate.add(endDate);
        // 把结束时间加入集合
        return lDate;
    }

    /**
     * 分页查询日报表数据
     *
     * @param pageUtil   分页
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
            PageUtil bySqlAndSqlCount = dao.getBySqlAndSqlCount(pageUtil, sql.toString(), sqlCount.toString(), list.toArray());
            List<Object[]> objlist = bySqlAndSqlCount.getList();
            List<DailyStatement> dailyStatements = toDailyStatement(objlist);
            bySqlAndSqlCount.setList(dailyStatements);
            return bySqlAndSqlCount;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<DailyStatement> toDailyStatement(List<Object[]> objlist) {
        List<DailyStatement> list = new ArrayList<>();
        try {
            for (Object[] obj : objlist) {
                DailyStatement dailyStatement = new DailyStatement();
                dailyStatement.setId(!QwyUtil.isNullAndEmpty(obj[0]) ? Long.valueOf(obj[0] + "") : 0);
                SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
                Date date = sd.parse(!QwyUtil.isNullAndEmpty(obj[1]) ? (obj[1] + "") : null);
                dailyStatement.setInsertTime(date);
                dailyStatement.setTradingVolume(!QwyUtil.isNullAndEmpty(obj[2]) ? Double.valueOf(obj[2] + "") : 0.0);
                //在贷金额（含零钱罐）
                dailyStatement.setLoanAmountAll(!QwyUtil.isNullAndEmpty(obj[3]) ? Double.valueOf(obj[3] + "") : 0.0);
                //在贷金额（不含零钱罐）
                dailyStatement.setLoanAmount(!QwyUtil.isNullAndEmpty(obj[4]) ? Double.valueOf(obj[4] + "") : 0.0);
                //回款金额（不含零钱罐）
                dailyStatement.setReimbursementAmount(!QwyUtil.isNullAndEmpty(obj[5]) ? Double.valueOf(obj[5] + "") : 0.0);
                //回款金额（含零钱罐及余额）
                dailyStatement.setReimbursementAmountAll(!QwyUtil.isNullAndEmpty(obj[6]) ? Double.valueOf(obj[6] + "") : 0.0);
                //支付利息
                dailyStatement.setInterestpayment(!QwyUtil.isNullAndEmpty(obj[7]) ? Double.valueOf(obj[7] + "") : 0.0);
                // 今日提现金额
                dailyStatement.setTodayOutCashMoney(!QwyUtil.isNullAndEmpty(obj[8]) ? Double.valueOf(obj[8] + "") : 0.0);
                //回款用户投资率
                dailyStatement.setReturnInvestmentRate(!QwyUtil.isNullAndEmpty(obj[9]) ? Double.valueOf(obj[9] + "") : 0.0);
                // 资金流入额
                dailyStatement.setCapitalInflow(!QwyUtil.isNullAndEmpty(obj[10]) ? Double.valueOf(obj[10] + "") : 0.0);
                // 净流入金额
                dailyStatement.setNetInflow(!QwyUtil.isNullAndEmpty(obj[11]) ? Double.valueOf(obj[11] + "") : 0.0);
                // 资金存量
                dailyStatement.setCapitalStock(!QwyUtil.isNullAndEmpty(obj[12]) ? Double.valueOf(obj[12] + "") : 0.0);
                //激活用户数
                dailyStatement.setActivationCount(!QwyUtil.isNullAndEmpty(obj[13]) ? Integer.valueOf(obj[13] + "") : 0);
                //投资用户数
                dailyStatement.setInvestCount(!QwyUtil.isNullAndEmpty(obj[14]) ? Integer.valueOf(obj[14] + "") : 0);
                // 今日注册人数
                dailyStatement.setTodayregisterCount(!QwyUtil.isNullAndEmpty(obj[15]) ? Integer.valueOf(obj[15] + "") : 0);
                // 今日认证用户
                dailyStatement.setTodaycertificationCount(!QwyUtil.isNullAndEmpty(obj[16]) ? Integer.valueOf(obj[16] + "") : 0);
                // 今日首投用户
                dailyStatement.setTodayNewBuyNumber(!QwyUtil.isNullAndEmpty(obj[17]) ? Integer.valueOf(obj[17] + "") : 0);
                // 首投用户转化率
                dailyStatement.setFirstPercentConversion(!QwyUtil.isNullAndEmpty(obj[18]) ? Double.valueOf(obj[18] + "") : 0.0);
                dailyStatement.setFirstInvestmentTotalMoney(!QwyUtil.isNullAndEmpty(obj[19]) ? Double.valueOf(obj[19] + "") : 0.0);
                //首投客单金额（元）
                dailyStatement.setFirstInvestmentMoney(!QwyUtil.isNullAndEmpty(obj[20]) ? Double.valueOf(obj[20] + "") : 0.0);
                //复投金额（元）
                dailyStatement.setReInvestmentMoney(!QwyUtil.isNullAndEmpty(obj[21]) ? Double.valueOf(obj[21] + "") : 0.0);
                //零钱罐新增金额（元）
                dailyStatement.setAmountNewMoney(!QwyUtil.isNullAndEmpty(obj[22]) ? Double.valueOf(obj[22] + "") : 0.0);
                //复投用户数
                dailyStatement.setReInvestmentCount(!QwyUtil.isNullAndEmpty(obj[23]) ? Integer.valueOf(obj[23] + "") : 0);
                //新增复投用户数
                dailyStatement.setAddReInvestmentCount(!QwyUtil.isNullAndEmpty(obj[24]) ? Integer.valueOf(obj[24] + "") : 0);
                //新增复投用户投资总额
                dailyStatement.setAddReInvestmentMoney(!QwyUtil.isNullAndEmpty(obj[25]) ? Double.valueOf(obj[25] + "") : 0.0);
                //复投次数
                dailyStatement.setReInvestmentAmount(!QwyUtil.isNullAndEmpty(obj[26]) ? Integer.valueOf(obj[26] + "") : 0);
                //新增复投率（%）
                dailyStatement.setMultipleRate(!QwyUtil.isNullAndEmpty(obj[27]) ? Double.valueOf(obj[27] + "") : 0.0);
                //复投用户占比（%）
                dailyStatement.setOccupationRatio(!QwyUtil.isNullAndEmpty(obj[28]) ? Double.valueOf(obj[28] + "") : 0.0);
                //复投金额占比（%）
                dailyStatement.setReInvestmentRate(!QwyUtil.isNullAndEmpty(obj[29]) ? Double.valueOf(obj[29] + "") : 0.0);
                // 复投客单金额（元）
                dailyStatement.setSumMoney(!QwyUtil.isNullAndEmpty(obj[30]) ? Double.valueOf(obj[30] + "") : 0.0);
                //人均投资金额（元）
                if(!QwyUtil.isNullAndEmpty(obj[31])){
                    BigDecimal bd = new BigDecimal(obj[31] + "");
                    String capitaInvestmentMoney = bd.toPlainString();
                    dailyStatement.setCapitaInvestmentMoney(Double.valueOf(capitaInvestmentMoney));
                }else{
                    dailyStatement.setCapitaInvestmentMoney(0.0);
                }
                list.add(dailyStatement);
            }
            return list;
        } catch (Exception e) {
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
            String yesterday = QwyUtil.fmyyyyMMdd.format(QwyUtil.addDaysFromOldDate(new Date(), -1).getTime());
            if (!QwyUtil.isNullAndEmpty(yestady)) {
                time.add(yestady);
            }
            for (Date date : time) {
                String today = sd.format(date);
                //根据时间去补全所有数据
                DailyStatement dailyStatement = new DailyStatement();
                dailyStatement.setInsertTime(yestady);
                //交易额
                Double tradingVolume = updateTradingVolume(today);
                dailyStatement.setTradingVolume(Double.valueOf(tradingVolume.toString().replaceAll(",", "")));
                //在贷金额（含零钱罐）
                Double loanAmountAll = updateLoanAmountAll(today);
                dailyStatement.setLoanAmountAll(Double.valueOf(loanAmountAll.toString().replaceAll(",", "")));
                //在贷金额（不含零钱罐）
                Double loanAmount = updateLoanAmount(today);
                dailyStatement.setLoanAmount(Double.valueOf(loanAmount.toString().replaceAll(",", "")));
                //回款金额（不含零钱罐）
                Double reimbursementAmount = updateReimbursementAmount(today);
                dailyStatement.setReimbursementAmount(Double.valueOf(reimbursementAmount.toString().replaceAll(",", "")));
                //回款金额（含零钱罐及余额）
                Double reimbursementAmountAll = updateReimbursementAmountAll(today);
                dailyStatement.setReimbursementAmountAll(Double.valueOf(reimbursementAmountAll.toString().replaceAll(",", "")));
                //支付利息
                Double interestpayment = updateInterestpayment(today);
                dailyStatement.setInterestpayment(Double.valueOf(interestpayment.toString().replaceAll(",", "")));
                // 今日提现金额
                Double todayOutCashMoney = updateTodayOutCashMoney(today);
                dailyStatement.setTodayOutCashMoney(Double.valueOf(todayOutCashMoney.toString().replaceAll(",", "")));
                //回款用户投资率
                Double returnInvestmentRate = updateReturnInvestmentRate(today);
                dailyStatement.setReturnInvestmentRate(Double.valueOf(returnInvestmentRate.toString().replaceAll(",", "")));
                //资金流入额
                Double capitalInflow = updateCapitalInflow(today);
                dailyStatement.setCapitalInflow(Double.valueOf(capitalInflow.toString().replaceAll(",", "")));
                //净流入金额(资金流入额-今日提现金额)
                Double netInflow = updateNetInflow(today);
                dailyStatement.setNetInflow(Double.valueOf(netInflow.toString().replaceAll(",", "")));
                //平台今日资金存量总额
                Double todayCapitalStock = updateTodayCapitalStock(today);
                dailyStatement.setCapitalStock(Double.valueOf(todayCapitalStock.toString().replaceAll(",", "")));
                //激活用户数
                Integer activationCount = updateActivationCount(today);
                dailyStatement.setActivationCount(Integer.valueOf(activationCount.toString().replaceAll(",", "")));
                //投资用户数
                Integer investCount = updateInvestCount(today);
                dailyStatement.setInvestCount(Integer.valueOf(investCount.toString().replaceAll(",", "")));
                // 今日注册用户
                Integer updateTodayregisterCount = updateTodayregisterCount(today);
                dailyStatement.setTodayregisterCount(Integer.valueOf(updateTodayregisterCount.toString().replaceAll(",", "")));
                // 今日绑定(认证)用户
                Integer updateTodaycertificationCount = updateTodaycertificationCount(today);
                dailyStatement.setTodaycertificationCount(Integer.valueOf(updateTodaycertificationCount.toString().replaceAll(",", "")));
                // 今日首投人数
                Integer updateTodayfirstBuyNumber = updateTodayfirstBuyNumber(today);
                dailyStatement.setTodayNewBuyNumber(Integer.valueOf(updateTodayfirstBuyNumber.toString().replaceAll(",", "")));
                // 首投用户转化率
                Double firstPercentConversion = updateFirstPercentConversion(today);
                dailyStatement.setFirstPercentConversion(Double.valueOf(firstPercentConversion.toString().replaceAll(",", "")));
                //首投总金额
                Double firstInvestmentTotalMoney = updateFirstInvestmentTotalMoney(today);
                dailyStatement.setFirstInvestmentTotalMoney(Double.valueOf(firstInvestmentTotalMoney.toString().replaceAll(",", "")));
                //首投客单金额（元）
                Double firstInvestmentMoney = updateFirstInvestmentMoney(today);
                dailyStatement.setFirstInvestmentMoney(Double.valueOf(firstInvestmentMoney.toString().replaceAll(",", "")));
                //复投总金额
                Double reInvestmentMoney = updateReInvestmentMoney(today);
                dailyStatement.setReInvestmentMoney(Double.valueOf(reInvestmentMoney.toString().replaceAll(",", "")));
                //零钱罐新增金额（元）
                Double amountNewMoney = updateAmountNewMoney(today);
                dailyStatement.setAmountNewMoney(Double.valueOf(amountNewMoney.toString().replaceAll(",", "")));
                //复投用户数
                Integer reInvestmentCount = updateReInvestmentCount(today);
                dailyStatement.setReInvestmentCount(Integer.valueOf(reInvestmentCount.toString().replaceAll(",", "")));
                //新增复投用户数
                Integer addReInvestmentCount = updateAddReInvestmentCount(today);
                dailyStatement.setAddReInvestmentCount(Integer.valueOf(addReInvestmentCount.toString().replaceAll(",", "")));
                //新增复投总金额
                Double addReInvestmentMoney = updateAddReInvestmentMoney(today);
                dailyStatement.setAddReInvestmentMoney(Double.valueOf(addReInvestmentMoney.toString().replaceAll(",", "")));
                //复投次数
                Integer reInvestmentAmount = updateReInvestmentAmount(today);
                dailyStatement.setReInvestmentAmount(Integer.valueOf(reInvestmentAmount.toString().replaceAll(",", "")));
                //新增复投率（%）
                Double multipleRate = updateMultipleRate(today);
                dailyStatement.setMultipleRate(Double.valueOf(multipleRate.toString().replaceAll(",", "")));
                //复投用户占比（%）
                Double occupationRatio = updateOccupationRatio(today);
                dailyStatement.setOccupationRatio(Double.valueOf(occupationRatio.toString().replaceAll(",", "")));
                //复投金额占比（%）
                Double reInvestmentRate = updateReInvestmentRate(today);
                dailyStatement.setReInvestmentRate(Double.valueOf(reInvestmentRate.toString().replaceAll(",", "")));
                // 复投客单金额（元）
                Double sumMoney = updateSumMoney(today);
                dailyStatement.setSumMoney(Double.valueOf(sumMoney.toString().replaceAll(",", "")));
                //人均投资金额（元）
                Double capitaInvestmentMoney = updateCapitaInvestmentMoney(today);
                dailyStatement.setCapitaInvestmentMoney(Double.valueOf(capitaInvestmentMoney.toString().replaceAll(",", "")));
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
     * 更新交易额
     *
     * @param insertTime
     * @return
     */
    private Double updateTradingVolume(String insertTime) {
        try {
            List<Object> list = new ArrayList<Object>();
            list.add(insertTime);
            list.add(insertTime);
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT SUM(i.in_money)/100 money FROM investors i WHERE 1=1 ");
            sql.append(" AND i.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') AND i.investor_status IN('1','2','3') ");
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Double tradingVolume = 0.0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
                tradingVolume = Double.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
            }
            return tradingVolume;
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 更新在贷金额（含零钱罐）
     *
     * @param insertTime
     * @return
     */
    private Double updateLoanAmountAll(String insertTime) {
        try {
            List<Object> list = new ArrayList<Object>();
            list.add(insertTime);
            list.add(insertTime);
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT FORMAT(SUM(money)/100,2) FROM (SELECT i.in_money money FROM investors i WHERE 1=1 ");
            sql.append(" AND i.investor_status IN('1','2','3') ");
            sql.append(" AND i.insert_time < DATE_FORMAT(?, '%Y-%m-%d 23:59:59')");
            sql.append(" UNION ALL ");
            sql.append(" SELECT in_money money FROM coin_purse cp WHERE in_money > 0 ");
            sql.append(" AND cp.insert_time < DATE_FORMAT(?, '%Y-%m-%d 23:59:59') )t ");
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Double tradingVolume = 0.0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
                tradingVolume = Double.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
            }
            return tradingVolume;
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 更新在贷金额（不含零钱罐）
     *
     * @param insertTime
     * @return
     */
    private Double updateLoanAmount(String insertTime) {
        try {
            List<Object> list = new ArrayList<Object>();
            list.add(insertTime);
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT SUM(i.in_money)/100 money FROM investors i WHERE 1=1 ");
            sql.append(" AND i.investor_status IN('1','2','3') ");
            sql.append(" AND i.insert_time < DATE_FORMAT(?, '%Y-%m-%d 23:59:59')");
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Double tradingVolume = 0.0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
                tradingVolume = Double.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
            }
            return tradingVolume;
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }


    /**
     * 更新回款金额（不含零钱罐）
     *
     * @param insertTime
     * @return
     */
    private Double updateReimbursementAmount(String insertTime) {
        try {
            List<Object> list = new ArrayList<Object>();
            StringBuffer sql = new StringBuffer();
            list.add(insertTime);
            list.add(insertTime);
            sql.append("SELECT FORMAT(SUM(already_pay+pay_interest_cent_coupon+pay_hongbao)/100,2) FROM interest_details WHERE 1=1  ");
            sql.append(" AND update_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') ");
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Double tradingVolume = 0.0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
                tradingVolume = Double.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
            }
            return tradingVolume;
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 更新回款金额（含零钱罐及余额）
     *
     * @param insertTime
     * @return
     */
    private Double updateReimbursementAmountAll(String insertTime) {
        try {
            List<Object> list = new ArrayList<Object>();
            StringBuffer sql = new StringBuffer();
            list.add(insertTime);
            list.add(insertTime);
            list.add(insertTime);
            list.add(insertTime);
            sql.append("SELECT FORMAT(SUM(money)/100,2) FROM (SELECT FORMAT(SUM(already_pay+pay_interest_cent_coupon+pay_hongbao)/100,2) money FROM interest_details ");
            sql.append(" WHERE update_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') ");
            sql.append(" UNION ALL ");
            sql.append(" SELECT in_money money FROM coin_purse cp WHERE in_money > 0 AND update_time < DATE_FORMAT(?,'%Y-%m-%d 23:59:59') ");
            sql.append(" UNION ALL ");
            sql.append(" SELECT total_money FROM users_info WHERE update_time < DATE_FORMAT(?,'%Y-%m-%d 23:59:59'))t ");
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Double tradingVolume = 0.0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
                tradingVolume = Double.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
            }
            return tradingVolume;
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 更新支付利息
     *
     * @param insertTime
     * @return
     */
    private Double updateInterestpayment(String insertTime) {
        try {
            List<Object> list = new ArrayList<Object>();
            list.add(insertTime);
            list.add(insertTime);
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT SUM(pay_money/100) FROM interest_details ids WHERE ids.insert_time " +
                    "BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00')  AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59')  AND ids.status IN('0','1','2')");
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Double interestpayment = 0.0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
                interestpayment = Double.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
            }
            return interestpayment;
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 更新今日提现金额
     *
     * @param insertTime
     * @return
     */
    private Double updateTodayOutCashMoney(String insertTime) {
        try {
            List<Object> list = new ArrayList<Object>();
            list.add(insertTime);
            list.add(insertTime);
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT FORMAT(SUM(tr.money/100),2) FROM tx_record tr "
                    + "WHERE tr.is_check = '1' AND tr.status = '1'  AND tr.check_time "
                    + "BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00')  AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59') ");
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Double todayOutCashMoney = 0.0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
                todayOutCashMoney = Double.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
            }
            return todayOutCashMoney;
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 更新回款用户投资率
     *
     * @param insertTime
     * @return
     */
    private Double updateReturnInvestmentRate(String insertTime) {
        try {
            //先查询当天回款且投资的用户数量
            List<Object> list = new ArrayList<Object>();
            list.add(insertTime);
            list.add(insertTime);
            list.add(insertTime);
            list.add(insertTime);
            list.add(insertTime);
            list.add(insertTime);
            list.add(insertTime);
            list.add(insertTime);
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT COUNT(users_id)FROM( ");
            sql.append("SELECT users_id FROM ( ");
            sql.append("SELECT bc.users_id users_id FROM (SELECT users_id FROM interest_details ");
            sql.append("WHERE update_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59'))bc ");
            sql.append("LEFT JOIN investors i ");
            sql.append("ON i.users_id = bc.users_id ");
            sql.append("WHERE i.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') ");
            sql.append("UNION ALL ");
            sql.append("SELECT bc.users_id users_id FROM (SELECT users_id FROM interest_details ");
            sql.append("WHERE update_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59'))bc ");
            sql.append("LEFT JOIN coin_purse_funds_record cpfr ON cpfr.users_id = bc.users_id ");
            sql.append("WHERE cpfr.type = 'to' AND cpfr.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59'))t ");
            sql.append("GROUP BY users_id)t ");
            List todaybackCashNumberinList = dao.LoadAllSql(sql.toString(), list.toArray());
            Double todaybackCashNumberin = 0.0;
            if (!QwyUtil.isNullAndEmpty(todaybackCashNumberinList.get(0))) {
                todaybackCashNumberin = Double.valueOf((todaybackCashNumberinList.get(0) + "").replaceAll(",", ""));
            }
            //获取当日回款用户
            list.clear();
            sql.delete(0, sql.length());
            list.add(insertTime);
            list.add(insertTime);
            sql.append("SELECT COUNT(users_id)FROM( ");
            sql.append("SELECT users_id FROM interest_details   ");
            sql.append("WHERE update_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') ");
            sql.append("GROUP BY users_id)t ");
            List todaybackCashNumberList = dao.LoadAllSql(sql.toString(), list.toArray());
            Double todaybackCashNumber = 0.0;
            if (!QwyUtil.isNullAndEmpty(todaybackCashNumberList.get(0))) {
                todaybackCashNumber = Double.valueOf((todaybackCashNumberList.get(0) + "").replaceAll(",", ""));
            }
            Double outInRate = 0.0;
            if (todaybackCashNumberin != 0.0 && todaybackCashNumber != 0.0) {
                outInRate = (todaybackCashNumberin / todaybackCashNumber);
            }
            return outInRate;
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 更新资金流入额
     *
     * @param insertTime
     * @return
     */
    private Double updateCapitalInflow(String insertTime) {
        try {
            List<Object> list = new ArrayList<Object>();
            list.add(insertTime);
            list.add(insertTime);
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT SUM(cz.money)/100 money FROM cz_record cz WHERE cz.STATUS = '1' AND TYPE = '1' AND cz.insert_time "
                    + "BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59')  ");
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Double capitalInflow = 0.0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
                capitalInflow = Double.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
            }
            return capitalInflow;
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 更新净流入金额
     *
     * @param insertTime
     * @return
     */
    private Double updateNetInflow(String insertTime) {
        try {
            List<Object> list = new ArrayList<Object>();
            list.add(insertTime);
            list.add(insertTime);
            list.add(insertTime);
            list.add(insertTime);
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT FORMAT((a.money  -b.money1),2) FROM "
                    + "(SELECT IFNULL(SUM(cz.money)/100,0) money FROM cz_record cz WHERE cz.STATUS = '1' AND TYPE = '1' AND cz.insert_time "
                    + "BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00')  AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59') )a,"
                    + "(SELECT IFNULL(SUM(tr.money/100),0) money1 FROM tx_record tr  WHERE tr.is_check = '1' AND tr.status = '1' AND tr.check_time "
                    + "BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59'))b ");
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Double netInflow = 0.0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
                netInflow = Double.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
            }
            return netInflow;
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 平台今日资金存量总额
     *
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
     * 更新今日激活用户数
     *
     * @param insertTime
     * @return
     */
    private Integer updateActivationCount(String insertTime) {
        try {
            List<Object> list = new ArrayList<Object>();
            list.add(insertTime);
            list.add(insertTime);
            StringBuffer sql = new StringBuffer();
            sql.append(" SELECT COUNT(id) FROM activity a WHERE  a.insert_time BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59')");
            // sql.append("SELECT SUM(q.activityCount) FROM qdtj  q WHERE q.insert_time BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59') ");
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Integer activationCount = 0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
                activationCount = Integer.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
            }
            return activationCount;
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 更新投资用户数
     *
     * @param insertTime
     * @return
     */
    private Integer updateInvestCount(String insertTime) {
        try {
            List<Object> list = new ArrayList<Object>();
            list.add(insertTime);
            list.add(insertTime);
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT COUNT(DISTINCT ins.`users_id`)  AS people FROM investors ins  "
                    + "LEFT JOIN users u ON ins.users_id = u.id  "
                    + "LEFT JOIN users_info i ON i.users_id = u.id  "
                    + "WHERE ins.investor_status IN ('1', '2', '3')  "
                    + "AND ins.insert_time  "
                    + "BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59')  ");
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Integer todayregisterCount = 0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
                todayregisterCount = Integer.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
            }
            return todayregisterCount;
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
            Integer todayregisterCount = 0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
                todayregisterCount = Integer.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
            }
            return todayregisterCount;
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 更新当日认证人数
     *
     * @param insertTime
     * @return
     */
    private Integer updateTodaycertificationCount(String insertTime) {
        try {
            List<Object> list = new ArrayList<Object>();
            list.add(insertTime);
            list.add(insertTime);
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT COUNT(1) FROM account ac  WHERE ac.insert_time  BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59') ");
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Integer todaycertificationCount = 0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0)) && loadAllSql != null) {
                todaycertificationCount = Integer.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
            }
            return todaycertificationCount;
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 更新平台今日首投用户数
     *
     * @param insertTime
     * @return
     */
    private Integer updateTodayfirstBuyNumber(String insertTime) {
        try {
            List<Object> list = new ArrayList<Object>();
            list.add(insertTime);
            list.add(insertTime);
            list.add(insertTime);
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT COUNT(nb) FROM ( ");
            sql.append("SELECT COUNT(id) nb ,users_id,MIN(insert_time) insert_time,investor_status FROM investors WHERE insert_time <  DATE_FORMAT(?,'%Y-%m-%d 23:59:59') GROUP BY users_id)t ");
            sql.append("WHERE t.nb >= 1 AND t.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59')AND t.investor_status IN ('1','2','3') ");
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
     * 更新首投用户转化率
     *
     * @param insertTime
     * @return
     */
    private Double updateFirstPercentConversion(String insertTime) {
        try {
            //获取今日注册人数
            Integer integer = updateTodayregisterCount(insertTime);
            //获取今日注册且首投的人数
            List<Object> list = new ArrayList<Object>();
            list.add(insertTime);
            list.add(insertTime);
            list.add(insertTime);
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT COUNT(nb) FROM ( ");
            sql.append("SELECT COUNT(i.id) nb ,i.users_id,i.investor_status FROM users u ");
            sql.append("LEFT JOIN  investors i ON u.id = i.users_id ");
            sql.append("WHERE i.insert_time BETWEEN DATE_FORMAT('?','%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') ");
            sql.append("AND u.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') ");
            sql.append("GROUP BY users_id ");
            sql.append(")t WHERE t.nb >= 1 AND t.investor_status IN ('1','2','3') ");
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Integer todayNewBuyNumber = 0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
                todayNewBuyNumber = Integer.valueOf((loadAllSql.get(0) + ""));
            }
            Double firstPercentConversion = 0.0;
            if (integer != 0 && todayNewBuyNumber != 0.0) {
                firstPercentConversion = todayNewBuyNumber.doubleValue() / integer.doubleValue();
            }
            return firstPercentConversion;
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }

    /**
     * 更新首投总金额
     *
     * @param insertTime
     * @return
     */
    private Double updateFirstInvestmentTotalMoney(String insertTime) {
        try {
            List<Object> list = new ArrayList<Object>();
            list.add(insertTime);
            list.add(insertTime);
            list.add(insertTime);
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT SUM(in_money) FROM ( ");
            sql.append("SELECT COUNT(id) nb ,users_id,MIN(insert_time) insert_time,SUM(in_money)/100 in_money,investor_status FROM investors WHERE insert_time <  DATE_FORMAT(?,'%Y-%m-%d 23:59:59') GROUP BY users_id )t ");
            sql.append("WHERE t.nb >= 1 AND t.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59')AND t.investor_status IN ('1','2','3') ");
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
     * 更新首投客单金额
     *
     * @param insertTime
     * @return
     */
    private Double updateFirstInvestmentMoney(String insertTime) {
        try {
            //首投人数
            Integer integer = updateTodayfirstBuyNumber(insertTime);
            //首投金额
            Double aDouble = updateFirstInvestmentTotalMoney(insertTime);
            Double reInvestmentMoney = 0.0;
            if (integer != 0 && aDouble != 0.0) {
                reInvestmentMoney = aDouble / integer;
            }
            return reInvestmentMoney;
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }

    /**
     * 更新复投总金额
     *
     * @param insertTime
     * @return
     */
    private Double updateReInvestmentMoney(String insertTime) {
        try {
            List<Object> list = new ArrayList<Object>();
            list.add(insertTime);
            list.add(insertTime);
            list.add(insertTime);
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT SUM(i.in_money)/100 FROM ( ");
            sql.append("SELECT COUNT(id) nb ,users_id,MIN(insert_time) insert_time,in_money in_money,investor_status FROM investors ");
            sql.append("WHERE insert_time <  DATE_FORMAT(?,'%Y-%m-%d 23:59:59') GROUP BY users_id )t ");
            sql.append("JOIN  investors i ON i.users_id = t.users_id ");
            sql.append("WHERE  i.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59')AND t.investor_status IN ('1','2','3') ");
            sql.append("AND i.insert_time > t.insert_time ");
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
     * 更新零钱罐新增金额
     *
     * @param insertTime
     * @return
     */
    private Double updateAmountNewMoney(String insertTime) {
        try {
            List<Object> list = new ArrayList<Object>();
            list.add(insertTime);
            list.add(insertTime);
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT SUM(money)/100 FROM coin_purse_funds_record  cpfr  WHERE  cpfr.insert_time " +
                    "BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00')  AND DATE_FORMAT(?, '%Y-%m-%d 23:59:59') AND  cpfr.type='to' ");
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Double todayOutCashMoney = 0.0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
                todayOutCashMoney = Double.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
            }
            return todayOutCashMoney;
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 更新复投用户数
     *
     * @param insertTime
     * @return
     */
    private Integer updateReInvestmentCount(String insertTime) {
        try {
            List<Object> list = new ArrayList<Object>();
            list.add(insertTime);
            list.add(insertTime);
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT COUNT(1) FROM (SELECT i.users_id FROM ( ");
            sql.append("SELECT COUNT(id) nb ,users_id,MIN(insert_time) insert_time,investor_status FROM investors  ");
            sql.append("WHERE insert_time <  DATE_FORMAT('2018-01-16','%Y-%m-%d 23:59:59') GROUP BY users_id )t ");
            sql.append("JOIN  investors i ON i.users_id = t.users_id  ");
            sql.append("WHERE  i.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59')AND t.investor_status IN ('1','2','3') ");
            sql.append("AND i.insert_time > t.insert_time GROUP BY users_id)t ");
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
     * 更新新增复投用户
     *
     * @param insertTime
     * @return
     */
    private Integer updateAddReInvestmentCount(String insertTime) {
        try {
            List<Object> list = new ArrayList<Object>();
            list.add(insertTime);
            list.add(insertTime);
            list.add(insertTime);
            list.add(insertTime);
            list.add(insertTime);
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT COUNT(1) FROM (SELECT i.users_id FROM ( ");
            sql.append("SELECT COUNT(id) nb ,users_id,MIN(insert_time) insert_time,investor_status FROM investors ");
            sql.append("WHERE insert_time <  DATE_FORMAT(?,'%Y-%m-%d 23:59:59') GROUP BY users_id )t ");
            sql.append("JOIN  investors i ON i.users_id = t.users_id  ");
            sql.append("JOIN (SELECT COUNT(id) nb ,users_id,MIN(insert_time) insert_time,investor_status FROM investors  ");
            sql.append("WHERE insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') GROUP BY users_id )a ");
            sql.append("ON a.users_id = t.users_id ");
            sql.append("WHERE  i.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59')AND t.investor_status IN ('1','2','3') ");
            sql.append("AND i.insert_time > t.insert_time  ");
            sql.append("AND t.nb - a.nb < 2 ");
            sql.append("GROUP BY users_id)t ");
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
     *
     * @param insertTime
     * @return
     */
    private Double updateAddReInvestmentMoney(String insertTime) {
        try {
            List<Object> list = new ArrayList<Object>();
            list.add(insertTime);
            list.add(insertTime);
            list.add(insertTime);
            list.add(insertTime);
            list.add(insertTime);
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT i.in_money/100 FROM ( ");
            sql.append("SELECT COUNT(id) nb ,users_id,MIN(insert_time) insert_time,investor_status FROM investors ");
            sql.append("WHERE insert_time <  DATE_FORMAT(?,'%Y-%m-%d 23:59:59') GROUP BY users_id )t ");
            sql.append("JOIN  investors i ON i.users_id = t.users_id  ");
            sql.append("JOIN (SELECT COUNT(id) nb ,users_id,MIN(insert_time) insert_time,investor_status FROM investors ");
            sql.append("WHERE insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') GROUP BY users_id )a ");
            sql.append("ON a.users_id = t.users_id ");
            sql.append("WHERE  i.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59')AND t.investor_status IN ('1','2','3') ");
            sql.append("AND i.insert_time > t.insert_time ");
            sql.append("AND t.nb - a.nb < 2 ");
            sql.append("GROUP BY i.users_id ");
            sql.append("ORDER BY i.insert_time ASC ");
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Double addReInvestmentMoney = 0.0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql) && !QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
                addReInvestmentMoney = Double.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
            }
            return addReInvestmentMoney;
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }

    /**
     * 更新复投次数
     *
     * @param insertTime
     * @return
     */
    private Integer updateReInvestmentAmount(String insertTime) {
        try {
            List<Object> list = new ArrayList<Object>();
            list.add(insertTime);
            list.add(insertTime);
            list.add(insertTime);
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT COUNT(1) FROM (SELECT i.users_id FROM ( ");
            sql.append("SELECT users_id,MIN(insert_time) insert_time,investor_status FROM investors  ");
            sql.append("WHERE insert_time <  DATE_FORMAT(?,'%Y-%m-%d 23:59:59') GROUP BY users_id )t ");
            sql.append("JOIN  investors i ON i.users_id = t.users_id  ");
            sql.append("WHERE  i.insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59')AND t.investor_status IN ('1','2','3') ");
            sql.append("AND i.insert_time > t.insert_time )t ");
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Integer addReInvestmentMoney = 0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0))) {
                addReInvestmentMoney = Integer.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
            }
            return addReInvestmentMoney;
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }

    /**
     * 更新新增复投率（%）
     *
     * @param insertTime
     * @return
     */
    private Double updateMultipleRate(String insertTime) {
        try {
            //查询复投用户
            Integer reInvestmentCount = updateReInvestmentCount(insertTime);
            //查询新增复投用户
            Integer addReInvestmentCount = updateAddReInvestmentCount(insertTime);
            Double addReInvestmentMoney = 0.0;
            if (reInvestmentCount != 0 && addReInvestmentCount != 0) {
                addReInvestmentMoney = addReInvestmentCount.doubleValue() / reInvestmentCount.doubleValue();
            }
            return addReInvestmentMoney;
        } catch (Exception e) {
            log.error(e);
        }

        return null;
    }

    /**
     * 更新复投用户占比（%）
     *
     * @param insertTime
     * @return
     */
    private Double updateOccupationRatio(String insertTime) {
        try {
            //查询复投用户金额
            Double reInvestmentMoney = updateReInvestmentMoney(insertTime);
            //查询今日平台投资金额
            List<Object> list = new ArrayList<Object>();
            list.add(insertTime);
            list.add(insertTime);
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT SUM(in_money)/100 FROM investors WHERE  investor_status IN ('1','2','3') ");
            sql.append("AND insert_time BETWEEN DATE_FORMAT(?,'%Y-%m-%d 00:00:00') AND DATE_FORMAT(?,'%Y-%m-%d 23:59:59') ");
            List loadAllSql = dao.LoadAllSql(sql.toString(), list.toArray());
            Double investmentCount = 0.0;
            Double investmentMoney = 0.0;
            if (!QwyUtil.isNullAndEmpty(loadAllSql.get(0)) && !QwyUtil.isNullAndEmpty(loadAllSql)) {
                investmentCount = Double.valueOf((loadAllSql.get(0) + "").replaceAll(",", ""));
            }
            if (reInvestmentMoney != 0.0 && investmentCount != 0) {
                investmentMoney = reInvestmentMoney.doubleValue() / investmentCount.doubleValue();
            }
            return investmentMoney;
        } catch (Exception e) {
            log.error(e);
        }

        return null;
    }

    /**
     * 更新复投金额占比（%）
     *
     * @param insertTime
     * @return
     */
    private Double updateReInvestmentRate(String insertTime) {
        try {
            //查询复投金额
            Integer reInvestmentCount = updateReInvestmentCount(insertTime);
            //查询投资金额
            Integer investCount = updateInvestCount(insertTime);
            Double investmentMoney = 0.0;
            if (reInvestmentCount != 0 && investCount != 0) {
                investmentMoney = reInvestmentCount.doubleValue() / investCount.doubleValue();
            }
            return investmentMoney;
        } catch (Exception e) {
            log.error(e);
        }

        return null;
    }

    /**
     * 更新复投客单金额（元）
     *
     * @param insertTime
     * @return
     */
    private Double updateSumMoney(String insertTime) {
        try {
            //复投用户数
            Integer reInvestmentCount = updateReInvestmentCount(insertTime);
            //查询复投金额
            Double reInvestmentMoney = updateReInvestmentMoney(insertTime);
            Double addReInvestmentMoney = 0.0;
            if (reInvestmentCount != 0 && reInvestmentMoney != 0.0) {
                addReInvestmentMoney = reInvestmentMoney / reInvestmentCount;
            }
            return addReInvestmentMoney;
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }

    /**
     * 更新人均投资金额（元）
     *
     * @param insertTime
     * @return
     */
    private Double updateCapitaInvestmentMoney(String insertTime) {
        try {
            //投资户数
            Integer investCount = updateInvestCount(insertTime);
            //投资金额
            Double tradingVolume = updateTradingVolume(insertTime);
            Double addReInvestmentMoney = 0.0;
            if (investCount != 0 && tradingVolume != 0.0) {
                addReInvestmentMoney =  tradingVolume/ investCount;
            }
            return addReInvestmentMoney;
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }


    /**
     * 运营日报表合计
     */
    public DailyStatement tjDailyStatement(List<DailyStatement> dailyStatements) throws Exception {
        DailyStatement ds = new DailyStatement();

        //交易额
        if (QwyUtil.isNullAndEmpty(ds.getTradingVolume()))
            ds.setTradingVolume(0.0);

        //在贷金额（含零钱罐）
        if (QwyUtil.isNullAndEmpty(ds.getLoanAmountAll()))
            ds.setLoanAmountAll(0.0);

        //在贷金额（不含零钱罐
        if (QwyUtil.isNullAndEmpty(ds.getLoanAmount()))
            ds.setLoanAmount(0.0);

        //回款金额（不含零钱罐）
        if (QwyUtil.isNullAndEmpty(ds.getReimbursementAmount()))
            ds.setReimbursementAmount(0.0);

        //回款金额（含零钱罐及余额）
        if (QwyUtil.isNullAndEmpty(ds.getReimbursementAmountAll()))
            ds.setReimbursementAmountAll(0.0);

        //支付利息
        if (QwyUtil.isNullAndEmpty(ds.getInterestpayment()))
            ds.setInterestpayment(0.0);

        //今日提现金额
        if (QwyUtil.isNullAndEmpty(ds.getTodayOutCashMoney()))
            ds.setTodayOutCashMoney(0.0);

        //回款用户投资率
        if (QwyUtil.isNullAndEmpty(ds.getReturnInvestmentRate()))
            ds.setReturnInvestmentRate(0.0);

        //资金流入额
        if (QwyUtil.isNullAndEmpty(ds.getCapitalInflow()))
            ds.setCapitalInflow(0.0);

        //净流入金额
        if (QwyUtil.isNullAndEmpty(ds.getNetInflow()))
            ds.setNetInflow(0.0);
        //资金存量
        if (QwyUtil.isNullAndEmpty(ds.getCapitalStock()))
            ds.setCapitalStock(0.0);
        //激活用户数
        if (QwyUtil.isNullAndEmpty(ds.getActivationCount()))
            ds.setActivationCount(0);
        //投资用户数
        if (QwyUtil.isNullAndEmpty(ds.getInvestCount()))
            ds.setInvestCount(0);
        //今日注册人数
        if (QwyUtil.isNullAndEmpty(ds.getTodayregisterCount()))
            ds.setTodayregisterCount(0);
        //今日认证用户
        if (QwyUtil.isNullAndEmpty(ds.getTodaycertificationCount()))
            ds.setTodaycertificationCount(0);
        //今日首投用户
        if (QwyUtil.isNullAndEmpty(ds.getTodayNewBuyNumber()))
            ds.setTodayNewBuyNumber(0);
        //首投用户转化率
        if (QwyUtil.isNullAndEmpty(ds.getFirstPercentConversion()))
            ds.setFirstPercentConversion(0.0);
        //首投总金额
        if (QwyUtil.isNullAndEmpty(ds.getFirstInvestmentTotalMoney()))
            ds.setFirstInvestmentTotalMoney(0.0);
        // 首投客单金额（元）
        if (QwyUtil.isNullAndEmpty(ds.getFirstInvestmentMoney()))
            ds.setFirstInvestmentMoney(0.0);
        //复投金额（元）
        if (QwyUtil.isNullAndEmpty(ds.getReInvestmentMoney()))
            ds.setReInvestmentMoney(0.0);
        //零钱罐新增金额（元）
        if (QwyUtil.isNullAndEmpty(ds.getAmountNewMoney()))
            ds.setAmountNewMoney(0.0);
        //复投用户数
        if (QwyUtil.isNullAndEmpty(ds.getReInvestmentCount()))
            ds.setReInvestmentCount(0);
        //新增复投用户数
        if (QwyUtil.isNullAndEmpty(ds.getAddReInvestmentCount()))
            ds.setAddReInvestmentCount(0);
        //新增复投用户投资总额（元）
        if (QwyUtil.isNullAndEmpty(ds.getAddReInvestmentMoney()))
            ds.setAddReInvestmentMoney(0.0);
        //复投次数
        if (QwyUtil.isNullAndEmpty(ds.getReInvestmentAmount()))
            ds.setReInvestmentAmount(0);
        //新增复投率（%）
        if (QwyUtil.isNullAndEmpty(ds.getMultipleRate()))
            ds.setMultipleRate(0.0);
        //复投用户占比（%）
        if (QwyUtil.isNullAndEmpty(ds.getOccupationRatio()))
            ds.setOccupationRatio(0.0);
        //复投金额占比（%）
        if (QwyUtil.isNullAndEmpty(ds.getReInvestmentRate()))
            ds.setReInvestmentRate(0.0);
        //复投客单金额（元）
        if (QwyUtil.isNullAndEmpty(ds.getSumMoney()))
            ds.setSumMoney(0.0);
        //人均投资金额（元）
        if (QwyUtil.isNullAndEmpty(ds.getCapitaInvestmentMoney()))
            ds.setCapitaInvestmentMoney(0.0);
        if (!QwyUtil.isNullAndEmpty(dailyStatements)) {
            for (DailyStatement dailyStatement : dailyStatements) {
                if (!QwyUtil.isNullAndEmpty(dailyStatement))
                    //交易额
                    ds.setTradingVolume(QwyUtil.calcNumber(dailyStatement.getTradingVolume(), ds.getTradingVolume(), "+").doubleValue());
                //在贷金额（含零钱罐）
                ds.setLoanAmountAll(QwyUtil.calcNumber(dailyStatement.getLoanAmountAll(), ds.getLoanAmountAll(), "+").doubleValue());
                //在贷金额（不含零钱罐）
                ds.setLoanAmount(QwyUtil.calcNumber(dailyStatement.getLoanAmount(), ds.getLoanAmount(), "+").doubleValue());
                //回款金额（不含零钱罐）
                ds.setReimbursementAmount(QwyUtil.calcNumber(dailyStatement.getReimbursementAmount(), ds.getReimbursementAmount(), "+").doubleValue());
                //回款金额（含零钱罐及余额）
                ds.setReimbursementAmountAll(QwyUtil.calcNumber(dailyStatement.getReimbursementAmountAll(), ds.getReimbursementAmountAll(), "+").doubleValue());
                //支付利息
                ds.setInterestpayment(QwyUtil.calcNumber(dailyStatement.getInterestpayment(), ds.getInterestpayment(), "+").doubleValue());
                //今日提现金额
                ds.setTodayOutCashMoney(QwyUtil.calcNumber(dailyStatement.getTodayOutCashMoney(), ds.getTodayOutCashMoney(), "+").doubleValue());
                //资金流入额
                ds.setCapitalInflow(QwyUtil.calcNumber(dailyStatement.getCapitalInflow(), ds.getCapitalInflow(), "+").doubleValue());
                //净流入金额
                ds.setNetInflow(QwyUtil.calcNumber(dailyStatement.getNetInflow(), ds.getNetInflow(), "+").doubleValue());
                //资金存量
                ds.setCapitalStock(QwyUtil.calcNumber(dailyStatement.getCapitalStock(), ds.getCapitalStock(), "+").doubleValue());
                //激活用户数
                ds.setActivationCount(QwyUtil.calcNumber(dailyStatement.getActivationCount(), ds.getActivationCount(), "+").intValue());
                //投资用户数
                ds.setInvestCount(QwyUtil.calcNumber(dailyStatement.getInvestCount(), ds.getInvestCount(), "+").intValue());
                //今日注册人数
                ds.setTodayregisterCount(QwyUtil.calcNumber(dailyStatement.getTodayregisterCount(), ds.getTodayregisterCount(), "+").intValue());
                //今日认证用户
                ds.setTodaycertificationCount(QwyUtil.calcNumber(dailyStatement.getTodaycertificationCount(), ds.getTodaycertificationCount(), "+").intValue());
                //今日首投用户
                ds.setTodayNewBuyNumber(QwyUtil.calcNumber(dailyStatement.getTodayNewBuyNumber(), ds.getTodayNewBuyNumber(), "+").intValue());
                //首投总金额
                ds.setFirstInvestmentTotalMoney(QwyUtil.calcNumber(dailyStatement.getFirstInvestmentTotalMoney(), ds.getFirstInvestmentTotalMoney(), "+").doubleValue());
                //首投客单金额（元）
                ds.setFirstInvestmentMoney(QwyUtil.calcNumber(dailyStatement.getFirstInvestmentMoney(), ds.getFirstInvestmentMoney(), "+").doubleValue());
                //复投金额（元）
                ds.setReInvestmentMoney(QwyUtil.calcNumber(dailyStatement.getReInvestmentMoney(), ds.getReInvestmentMoney(), "+").doubleValue());
                //零钱罐新增金额（元）
                ds.setAmountNewMoney(QwyUtil.calcNumber(dailyStatement.getAmountNewMoney(), ds.getAmountNewMoney(), "+").doubleValue());
                //复投用户数
                ds.setReInvestmentCount(QwyUtil.calcNumber(dailyStatement.getReInvestmentCount(), ds.getReInvestmentCount(), "+").intValue());
                //新增复投用户数
                ds.setAddReInvestmentCount(QwyUtil.calcNumber(dailyStatement.getAddReInvestmentCount(), ds.getAddReInvestmentCount(), "+").intValue());
                //新增复投用户投资总额（元）
                ds.setAddReInvestmentMoney(QwyUtil.calcNumber(dailyStatement.getAddReInvestmentMoney(), ds.getAddReInvestmentMoney(), "+").doubleValue());
                //复投次数
                ds.setReInvestmentAmount(QwyUtil.calcNumber(dailyStatement.getReInvestmentAmount(), ds.getReInvestmentAmount(), "+").intValue());
                //复投客单金额（元）
                ds.setSumMoney(QwyUtil.calcNumber(dailyStatement.getSumMoney(), ds.getSumMoney(), "+").doubleValue());
                //人均投资金额（元）
                ds.setCapitaInvestmentMoney(QwyUtil.calcNumber(dailyStatement.getCapitaInvestmentMoney(), ds.getCapitaInvestmentMoney(), "+").doubleValue());
            }
        }
        return ds;
    }
}
