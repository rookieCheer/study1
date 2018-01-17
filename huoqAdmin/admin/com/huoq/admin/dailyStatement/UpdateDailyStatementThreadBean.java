package com.huoq.admin.dailyStatement;

import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.DailyStatement;
import com.huoq.thread.dao.ThreadDAO;
import com.huoq.util.MyLogger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.NumberFormat;
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
}
