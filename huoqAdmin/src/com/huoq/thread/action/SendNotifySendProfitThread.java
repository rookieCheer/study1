/**
 * hongshiwl.com Inc.
 * Copyright (c) 2015-2017 All Rights Reserved.
 */
package com.huoq.thread.action;

import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.Investors;
import com.huoq.thread.bean.SendProfitThreadBean;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 产品到期后，账户资金变动短信通知客户
 * @author guoyin.yi
 * @version $Id: SendNotifySendProfitThread.java, v 0.1  2017/11/20 Exp $
 */
@Service
public class SendNotifySendProfitThread implements Runnable {

    private Logger log = Logger.getLogger(SendNotifySendProfitThread.class);

    private Integer pageSize = 50;

    @Resource
    private SendProfitThreadBean bean;

    @Override
    public void run() {
        long st = System.currentTimeMillis();
        int currentPage = 0;
        int totalPage = 0;
        int totalSize = 0;
        int leftSize = 0;
        try {
            log.info("进入后台线程----发送短信通知客户产品到期后账户资金变动;当前时间:"+ QwyUtil.fmyyyyMMddHHmmss.format(new Date()));
            PageUtil<Investors> pageUtil = new PageUtil<>();
            pageUtil.setPageSize(pageSize);
            String[] status = {"0","1"};//状态 0未支付,1已冻结,2已支付,3已删除
            for (;;) {
                if(currentPage>100){
                    log.info("-----------安全机制------达到100页自动退出,防止死循环,下次需手动启动线程-------");
                    break;
                }
                currentPage++;
                pageUtil.setCurrentPage(currentPage);
                pageUtil = bean.getInvestorsByPageUtil(pageUtil);
                List<Investors> investorsList = pageUtil.getList();
                if(QwyUtil.isNullAndEmpty(investorsList) || (totalPage!=0 && currentPage>totalPage)){
                    leftSize = pageUtil.getCount();//结束时 剩余的条数;
                    log.info("发送短信通知客户产品到期后账户资金变动 结束,当前页数: "+currentPage);
                    break;
                }
                totalPage = totalPage==0?pageUtil.getPageCount():totalPage;
                totalSize = totalSize==0?pageUtil.getCount():totalSize;
                String logInfo = FixedOrderThread.pageUtilLog("InterestDetails表【产品利息表】", totalPage, totalSize, pageUtil.getCount(), currentPage);
                log.info(logInfo);
                for (Investors investors : investorsList) {
                    //发送资金到帐短信通知
                    bean.sendNotifySendProfit(investors);
                }
            }
        } catch (Exception e) {
            log.error("操作异常: ",e);
            log.error("发送短信通知客户产品到期后账户资金变动异常: ",e);
        }
        if(leftSize>=pageSize){
            try {
                Thread a = new Thread(this);
                a.start();
                a.join();
            } catch (InterruptedException e) {
                log.error("操作异常: ",e);
            }
        }
        long et = System.currentTimeMillis();
        log.info("发送短信通知客户产品到期后账户资金变动: "+(et-st));

    }
}
