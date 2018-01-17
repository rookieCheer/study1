package com.huoq.thread.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Resource;

import com.huoq.common.util.MyRedis;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.huoq.account.bean.CoinPurseFundRecordBean;
import com.huoq.account.bean.CoinpPurseBean;
import com.huoq.account.bean.SendRatesBean;
import com.huoq.account.bean.ShiftToBean;
import com.huoq.account.dao.ShiftToDAO;
import com.huoq.common.ApplicationContexts;
import com.huoq.common.bean.MyWalletBean;
import com.huoq.common.bean.SystemConfigBean;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.login.bean.RegisterUserBean;
import com.huoq.orm.CoinPurse;
import com.huoq.orm.SendRates;
import com.huoq.orm.SystemConfig;

/**
 * 后台线程自动发放收益<br>
 *
 * @author qwy
 * @createTime 2015-4-28上午9:42:57
 */
@Service(value = "sendCoinPurseRatesThread")
public class SendCoinPurseRatesThread implements Runnable {
    private Logger log = Logger.getLogger(SendCoinPurseRatesThread.class);
    @Resource
    ShiftToBean shiftToBean;
    @Resource
    ShiftToDAO dao;
    @Resource
    SendRatesBean sendRatesBean;
    @Resource
    private SystemConfigBean systemConfigBean;
    @Resource
    RegisterUserBean registerUserBean;
    @Resource
    MyWalletBean walletBean;
    @Resource
    CoinpPurseBean coinPurseBean;
    @Resource
    CoinPurseFundRecordBean cpfrBean;

    private Integer pageSize = 50;

    private MyRedis myRedis = new MyRedis();
    private final static String SEND_COIN_PURSE_RATES_REDIS_LOCK = "SEND_COIN_PURSE_RATES_REDIS_LOCK";

    private Lock lock = new ReentrantLock();

    @Override
    public void run() {
        long st = System.currentTimeMillis();
        log.info("进入后台线程----对收益表SendRates表进行按要求发放;当前时间:" + QwyUtil.fmyyyyMMddHHmmss.format(new Date()));
        int currentPage = 0;
        int totalPage = 0;
        int totalSize = 0;
        int leftSize = 0;

        log.info("### ReentrantLock 开始加锁 ###");
        lock.lock();

        String keyValue = myRedis.get(SEND_COIN_PURSE_RATES_REDIS_LOCK);
        if (!QwyUtil.isNullAndEmpty(keyValue)) {
            log.info("### Redis锁已设置，准备退出零钱罐发息线程 ###");

            lock.unlock();
            log.info("### ReentrantLock 解锁完毕 ###");

            return;
        }

        myRedis.setex(SEND_COIN_PURSE_RATES_REDIS_LOCK, 60 * 60, SEND_COIN_PURSE_RATES_REDIS_LOCK);
        log.info("### 设置了Redis锁，防止出现分布式重复发息 时效为 " + 60 * 60 + " ####");

        try {
            ApplicationContext context = ApplicationContexts.getContexts();
            PlatformTransactionManager tm = (PlatformTransactionManager) context.getBean("transactionManager");
            TransactionStatus ts = null;

            int position = 0;

            for (; ; ) {
                if (currentPage > 300) {
                    log.info("-----------安全机制------达到100页自动退出,防止死循环,下次需手动启动线程-------");
                    break;
                }
                SystemConfig systemConfig = systemConfigBean.findSystemConfig();

                //synchronized (this) {

                // 查询的100个人的ID
                try {
                    SimpleDateFormat fmyyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");
                    Date time = fmyyyyMMdd.parse(fmyyyyMMdd.format(new Date()));
                    currentPage++;
                    PageUtil<SendRates> pageUtil = new PageUtil<SendRates>();
                    pageUtil.setCurrentPage(1);
                    pageUtil.setPageSize(pageSize);
                    pageUtil = sendRatesBean.findPageUtil(pageUtil, null, "0", time);
                    log.info(pageUtil.getPageCount());
                    if (QwyUtil.isNullAndEmpty(pageUtil.getList()) || (totalPage != 0 && currentPage > totalPage)) {
                        leftSize = pageUtil.getCount();//结束时 剩余的条数;
                        log.info("发放收益SendRates表【零钱罐】结束: " + currentPage);
                        break;
                    }
                    totalPage = totalPage == 0 ? pageUtil.getPageCount() : totalPage;
                    totalSize = totalSize == 0 ? pageUtil.getCount() : totalSize;
                    String logInfo = FixedOrderThread.pageUtilLog("SendRates表【零钱罐】", totalPage, totalSize, pageUtil.getCount(), currentPage);
                    log.info(logInfo);
                    try {
                        List<SendRates> list = pageUtil.getList();
                        //sendLqgRate(list, systemConfig);
                        long a1 = System.currentTimeMillis();
                        for (SendRates sendRates : list) {
                            ts = tm.getTransaction(new DefaultTransactionDefinition());
                            CoinPurse coinPurse = coinPurseBean.findCoinPurseByUsersId(sendRates.getUsersId());
                            Double bcsy = sendRates.getPayInterest();
                            boolean isOk = walletBean.addTotalMoney(coinPurse.getUsersId(), bcsy.doubleValue());
                            if (!isOk) {
                                sendRates.setStatus("-1");
                                sendRates.setUpdateTime(new Date());
                                dao.saveOrUpdate(sendRates);
                                tm.commit(ts);
                                continue;
                            }
                            // 总收益
                            coinPurse.setEarnings(systemConfig.getEarnings());
                            coinPurse.setInMoney(QwyUtil.calcNumber(coinPurse.getInMoney(), bcsy, "+").doubleValue());
                            coinPurse.setInvestDay(coinPurse.getInvestDay() + 1);
                            coinPurse.setUpdateTime(new Date());
                            coinPurse.setPayInterest(QwyUtil.calcNumber(coinPurse.getPayInterest(), bcsy, "+").doubleValue());
                            dao.saveOrUpdate(coinPurse);
                            log.info("\n======================== 保存零钱罐收益 - start：" + coinPurse.getUsersId() + ">><<<" + bcsy + ">><<<" + sendRates.getId() + ">><<<" + "shouyi" + ">><<<" + coinPurse.getInMoney());
                            // 改变总资产
                            String note = currentPage + "_" + position;
                            String cpfrId = cpfrBean.saveCoinPurseFundsRecord(coinPurse.getUsersId(), bcsy, sendRates.getId(), "shouyi", coinPurse.getInMoney(), note);
                            log.info("\n======================== 保存零钱罐收益 - end：");
                            if (QwyUtil.isNullAndEmpty(cpfrId)) {
                                tm.rollback(ts);
                                log.error("\n======================== 保存零钱罐收益数据回滚：");
                                continue;
                            }
                            log.info("\n======================== 保存零钱罐收益 - end2");
                            sendRates.setStatus("1");
                            sendRates.setUpdateTime(new Date());
                            dao.saveOrUpdate(sendRates);
                            tm.commit(ts);
                        }
                        long a2 = System.currentTimeMillis();
                        log.info(pageSize + "条数据处理耗时: " + (a2 - a1));
                    } catch (Exception e) {
                        if (!QwyUtil.isNullAndEmpty(tm))
                            tm.rollback(ts);
                        log.error("操作异常: ", e);
                        log.error("进入修改产品状态的后台线程异常: ", e);
                    }
                } catch (Exception e) {
                    log.error("进入修改产品状态的后台线程异常: ", e);
                }
                //} sync end
            }

        } finally {
            lock.unlock();
            log.info("### ReentrantLock 解锁完毕 ###");
        }

        if (leftSize >= pageSize) {
            try {
                Thread a = new Thread(this);
                a.start();
                a.join();
            } catch (InterruptedException e) {
                log.error("操作异常: ", e);
            }

        }
        long et = System.currentTimeMillis();
        log.info("发放【零钱罐】收益SendRates耗时: " + (et - st));
    }

	/*
     * public static void main(String[] args) {
	 * log.info(QwyUtil.addDaysFromOldDate(new Date(),
	 * -2).getTime().toLocaleString()); }
	 */

}
