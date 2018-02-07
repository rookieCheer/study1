package com.huoq.filter;

import com.huoq.common.bean.BaoFuBean;
import com.huoq.common.util.PropertiesUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.Modul;
import com.huoq.orm.RolesRight;
import com.huoq.orm.SystemConfig;
import com.huoq.thread.action.*;
import com.huoq.thread.dao.ThreadDAO;
import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MyServletContextListener implements ServletContextListener {

    private static Logger log = Logger.getLogger(MyServletContextListener.class);
    private static ResourceBundle resb = ResourceBundle.getBundle("app");
    
    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        log.info("MyServletContextListener_contextDestroyed");
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        try {
            
            
            log.info("初始化监听......");
            WebApplicationContext context = WebApplicationContextUtils
                    .getRequiredWebApplicationContext(arg0.getServletContext());
            ThreadDAO threadDAO = (ThreadDAO) context.getBean("threadDAO");
            SystemConfig systemConfig = threadDAO.getSystemConfig();
            arg0.getServletContext().setAttribute("systemConfig", systemConfig);
            TxRequestThread.pfxpath = arg0.getServletContext().getRealPath("/CER");
            BaoFuBean.APP_PROPERTIES_URL = arg0.getServletContext().getRealPath("/WEB-INF/classes/app.properties");
            BaoFuBean.CER_URL = arg0.getServletContext().getRealPath("/CER/");
            // 获取所有权限;
            List<Modul> listModul = threadDAO.getModul();
            arg0.getServletContext().setAttribute("listModul", listModul);

            // 获取所有用户的一级标题权限;
            List<RolesRight> firstRolesRight = threadDAO.getFirstRolesRight();
            arg0.getServletContext().setAttribute("firstRolesRight", firstRolesRight);

            // 获取用户的权限;
            List<RolesRight> listRolesRight = threadDAO.getRolesRight();
            arg0.getServletContext().setAttribute("listRolesRight", listRolesRight);

            Object isStartThread = PropertiesUtil.getProperties("isStartThread");
            if (!QwyUtil.isNullAndEmpty(isStartThread) && "1".equals(isStartThread.toString())) {
                log.info("启动后台线程;");
                // 创建线程池50个大小;
                ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(50);

                // 自动计算零钱罐一天以前转入的资金的收益 **********重要**************
                AutoSendRatesThread autoSendRatesThread = (AutoSendRatesThread) context.getBean("autoSendRatesThread");
                scheduler.scheduleAtFixedRate(autoSendRatesThread, 5, 10, TimeUnit.MINUTES);

                // 筛选无重复的提现记录，线程发起提现请求-------宝付支付
                // TxRequestThread txRequestThread=(TxRequestThread)context.getBean("txRequestThread");
                 //scheduler.scheduleAtFixedRate(txRequestThread, 1, 2,TimeUnit.MINUTES);

//              //----------------------------------以下线程定点执行的---------------------------------
                long oneDay = 24 * 60 * 60 * 1000;
                //自动虚标功能 **********重要**************
                CheckProductStatusThread checkProductStatusThread=(CheckProductStatusThread)context.getBean("checkProductStatusThread");
                scheduler.scheduleAtFixedRate(checkProductStatusThread, getFixedTime("00:03:00"),oneDay,TimeUnit.MILLISECONDS);


                //自动续标功能 **********重要**************
                PutOnSaleProductThread putOnSaleProductThread=(PutOnSaleProductThread)context.getBean("putOnSaleProductThread");
                scheduler.scheduleAtFixedRate(putOnSaleProductThread, 3, 2, TimeUnit.MINUTES);

                //固定执行的一些程序,一天执行一次;且有顺序的执行; **********重要**************
                FixedOrderThread fixedOrderThread = (FixedOrderThread) context.getBean("fixedOrderThread");
                scheduler.scheduleAtFixedRate(fixedOrderThread,  getFixedTime("00:15:00"), oneDay, TimeUnit.MILLISECONDS);
                
                //手机归属地补全
                NumberAssignmentThread numberAssignmentThread=(NumberAssignmentThread) context.getBean("numberAssignmentThread");
                scheduler.scheduleAtFixedRate(numberAssignmentThread, 2, 10, TimeUnit.MINUTES);

                //自动保存首页数据
                UpdateDataOverviewThread updateDataOverviewThread=(UpdateDataOverviewThread) context.getBean("updateDataOverviewThread");
                scheduler.scheduleAtFixedRate(updateDataOverviewThread, getFixedTime("00:01:00"),oneDay,TimeUnit.MILLISECONDS);

                //自动保存运营日报表数据
                UpdateDailyStatementThread updateDailyStatementThread=(UpdateDailyStatementThread) context.getBean("updateDailyStatementThread");
                scheduler.scheduleAtFixedRate(updateDailyStatementThread, getFixedTime("00:21:00"),oneDay,TimeUnit.MILLISECONDS);

                if (resb.getString("SMS.PUSH.SWITCH").equals("ON")){
                    SendNotifySendProfitThread sendNotifySendProfitThread = (SendNotifySendProfitThread) context.getBean("sendNotifySendProfitThread");
                    scheduler.scheduleAtFixedRate(sendNotifySendProfitThread,  getFixedTime("09:10:00"), oneDay, TimeUnit.MILLISECONDS);

                    //注册未认证发送短信提醒
                    SendRegistThread sendRegistThread = (SendRegistThread) context.getBean("sendRegistThread");
                    scheduler.scheduleAtFixedRate(sendRegistThread,1,20,TimeUnit.HOURS);

                    //认证用户2天为投资 短信通知
                    SendUsersInformThread sendUsersInformThread = (SendUsersInformThread) context.getBean("sendUsersInformThread");
                    scheduler.scheduleAtFixedRate(sendUsersInformThread,getFixedTime("09:10:00"), oneDay, TimeUnit.MILLISECONDS);


                    //理财券到期提醒推送消息
                    CuponSendPushThread cuponSendPushThread = (CuponSendPushThread) context.getBean("cuponSendPushThread");
                    scheduler.scheduleAtFixedRate(cuponSendPushThread,getFixedTime("17:18:00"), oneDay, TimeUnit.MILLISECONDS);
                }

                //后台每日统计今日头条激活用户数据 执行今日头条接口回调  v1.0.0未用到
//              AutoExcuteDeviceActiveCallbakThread autoExcuteDeviceActiveCallbakThread = (AutoExcuteDeviceActiveCallbakThread) context.getBean("autoExcuteDeviceActiveCallbakThread");
//              scheduler.scheduleAtFixedRate(autoExcuteDeviceActiveCallbakThread, getFixedTime("04:00:00"), oneDay, TimeUnit.MILLISECONDS);
                
                
                //后台自动发放新版本  新手活动首投最高用户奖励
//              SendMoneyToTopDayThread sendMoneyToTopDayThread = (SendMoneyToTopDayThread)context.getBean("sendMoneyToTopDayThread");
//              scheduler.scheduleAtFixedRate(sendMoneyToTopDayThread, getFixedDateTime("01:00:00"), oneDay, TimeUnit.MILLISECONDS);
                
                // 后台每日投资统计线程（平台）  用不到，后台统计未用到  back_stats_operate_day废弃表
//              AutoStatsOperateThread autoStatsOperateThread = (AutoStatsOperateThread) context.getBean("autoStatsOperateThread");
//              scheduler.scheduleAtFixedRate(autoStatsOperateThread, getFixedTime("02:00:00"), oneDay, TimeUnit.MILLISECONDS);
                
                // 后台每日投资统计线程（安卓渠道） channel_operate_day 已经废弃 没有在代码中使用，现在使用的是Qdtj
//              ChannelOperateThread channelOperateThread = (ChannelOperateThread) context.getBean("channelOperateThread");
//              scheduler.scheduleAtFixedRate(channelOperateThread, getFixedTime("02:30:00"), oneDay, TimeUnit.MILLISECONDS);
                
                // 后台每日投资统计线程（苹果渠道）   iphone_operate_day 废弃表
//              IphoneOperateThread iphoneOperateThread = (IphoneOperateThread) context.getBean("iphoneOperateThread");
//              scheduler.scheduleAtFixedRate(iphoneOperateThread, getFixedTime("03:00:00"), oneDay, TimeUnit.MILLISECONDS);


                // 后台自动发放返款奖励 理财券 
//              AutoSendCouponThread autoSendCouponThread = (AutoSendCouponThread) context.getBean("autoSendCouponThread");
//              scheduler.scheduleAtFixedRate(autoSendCouponThread, getFixedTime("03:40:00"), oneDay, TimeUnit.MILLISECONDS);
                
                //后台自动发送 返款奖励短信  活动时间：2017-06-05 00:00:00---2017-06-26 23:59:59 
//              SendsmsThread sendsmsThread = (SendsmsThread)context.getBean("sendsmsThread");
//              scheduler.scheduleAtFixedRate(sendsmsThread, getFixedTime("10:15:00"), oneDay, TimeUnit.MILLISECONDS);
//               
                
                //枫叶网短信自动发送-1月25日早上9点   2017-01-25 09:00:00
                //MessageSendThreadNine messageSendThreadNine= (MessageSendThreadNine) context.getBean("messageSendThreadNine");
                //scheduler.schedule(messageSendThreadNine, getFixedTime("2017-01-25 09:00:00"), TimeUnit.MILLISECONDS);
                //枫叶网短信自动发送-1月28日早上8点   2017-01-28 08:00:00
                //MessageSendThreadEight messageSendThreadEight= (MessageSendThreadEight) context.getBean("messageSendThreadEight");
                //scheduler.schedule(messageSendThreadEight, getFixedTime("2017-01-28 08:00:00"), TimeUnit.MILLISECONDS);
                //枫叶网短信自动发送-2月3日晚上21点   2017-02-03 21:00:00
                //MessageSendThreadTen messageSendThreadTen= (MessageSendThreadTen) context.getBean("messageSendThreadTen");
                //scheduler.schedule(messageSendThreadTen, getFixedTime("2017-02-03 21:00:00"), TimeUnit.MILLISECONDS);

                
            } else {
                log.info("不启动后台线程;");
            }
        } catch (Exception e) {
            log.error("操作异常: ",e);
        }
    }

    private static long getTimeMilli(String time) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
            DateFormat dayFormat = new SimpleDateFormat("yy-MM-dd");
            Date curDate = dateFormat.parse(dayFormat.format(new Date()) + " " + time);
            return curDate.getTime();
        } catch (ParseException e) {
            log.error("操作异常: ",e);
        }
        return 0;
    }
    
    /**获取固定时间的毫秒数;<br>
     * 一般这个方法用于线程延迟启动的毫秒数;
     * @param time 格式为: HH:mm:ss的时间; 14:57:17
     * @return毫秒
     */
    public static long getFixedDateTime(String time){
        long oneDay = 24 * 60 * 60 * 1000;
        long initDelay = getDayMilli(time) - System.currentTimeMillis();
        initDelay = initDelay > 0 ? initDelay : oneDay + initDelay;
        return initDelay;
        
    }
    
    /**获取固定时间的毫秒数;<br>
     * 一般这个方法用于线程延迟启动的毫秒数;
     * @param time 格式为: HH:mm:ss的时间; 14:57:17
     * @return毫秒
     */
    public static long getFixedTime(String time){
        long oneDay = 24 * 60 * 60 * 1000;
        long initDelay = getTimeMilli(time) - System.currentTimeMillis();
        initDelay = initDelay > 0 ? initDelay : oneDay + initDelay;
        return initDelay;
    }
    
    private static long getDayMilli(String time) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date curDate = dateFormat.parse(time);
            return curDate.getTime();
        } catch (ParseException e) {
            log.error("操作异常: ",e);
        }
        return 0;
    }
    
}
