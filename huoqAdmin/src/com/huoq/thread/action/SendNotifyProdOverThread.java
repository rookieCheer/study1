/**
 * hongshiwl.com Inc.
 * Copyright (c) 2015-2017 All Rights Reserved.
 */
package com.huoq.thread.action;

import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.InterestDetails;
import com.huoq.thread.bean.SendProfitThreadBean;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 产品到期短信提醒线程
 * @author guoyin.yi
 * @version $Id: SendNotifyProdOverThread.java, v 0.1  2017/11/10 Exp $
 */
@Service
public class SendNotifyProdOverThread implements Runnable{

    private Logger log = Logger.getLogger(SendNotifyProdOverThread.class);

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
            log.info("进入后台线程----对收益表InterestDetails投资产品到期短信通知;当前时间:"+QwyUtil.fmyyyyMMddHHmmss.format(new Date()));
            PageUtil<InterestDetails> pageUtil = new PageUtil<InterestDetails>();
            pageUtil.setPageSize(pageSize);
            String[] status = {"0","1"};//状态 0未支付,1已冻结,2已支付,3已删除
            for (;;) {
                if(currentPage>100){
                    log.info("-----------安全机制------达到100页自动退出,防止死循环,下次需手动启动线程-------");
                    break;
                }
                currentPage++;
                pageUtil.setCurrentPage(1);
                pageUtil = bean.getFinishInterestDetailsByPageUtil(pageUtil, status);
                List<InterestDetails> listInterestDetails = pageUtil.getList();
                if(QwyUtil.isNullAndEmpty(listInterestDetails) || (totalPage!=0 && currentPage>totalPage)){
                    leftSize = pageUtil.getCount();//结束时 剩余的条数;
                    log.info("投资产品到期短信通知结束,当前页数: "+currentPage);
                    break;
                }
                totalPage = totalPage==0?pageUtil.getPageCount():totalPage;
                totalSize = totalSize==0?pageUtil.getCount():totalSize;
                String logInfo = FixedOrderThread.pageUtilLog("InterestDetails表【产品利息表】", totalPage, totalSize, pageUtil.getCount(), currentPage);
                log.info(logInfo);
                for (InterestDetails interestDetails : listInterestDetails) {
                    //发送产品到期短信通知
                    bean.notifyUserProductFinish(interestDetails);
                }
            }
        } catch (Exception e) {
            log.error("操作异常: ",e);
            log.error("投资产品到期短信通知异常: ",e);
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
        log.info("发放【理财产品】收益InterestDetails投资产品到期短信通知耗时: "+(et-st));
    }
}
