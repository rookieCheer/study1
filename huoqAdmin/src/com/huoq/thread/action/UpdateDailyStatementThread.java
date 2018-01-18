package com.huoq.thread.action;

import com.huoq.admin.dailyStatement.bean.UpdateDailyStatementThreadBean;
import com.huoq.common.util.QwyUtil;
import com.huoq.util.MyLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author 李瑞丽
 * @Date: Created in 16:00 2018/1/17
 */
@Service
public class UpdateDailyStatementThread implements Runnable{
    private static MyLogger log = MyLogger.getLogger(UpdateDailyStatementThread.class);
    @Autowired
    private UpdateDailyStatementThreadBean bean;
    @Override
    public synchronized void run() {
        long st = System.currentTimeMillis();
        try {
            log.info("----------执行更新-----【平台资金概览】-------当前时间:"+ QwyUtil.fmyyyyMMddHHmmss.format(new Date()));
            //获取昨天时间
            String yesterday = QwyUtil.fmyyyyMMdd.format(QwyUtil.addDaysFromOldDate(new Date(), -1).getTime());
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
            Date newyesterday = sd.parse(yesterday);
            //更新平台资金概览数据
            bean.updateDailyStatement(newyesterday);
        } catch (Exception e) {
            log.error(e);
        }
        long et = System.currentTimeMillis();
        log.info("--------执行更新-----【平台资金概览】------耗时:"+(et-st));
    }
}

