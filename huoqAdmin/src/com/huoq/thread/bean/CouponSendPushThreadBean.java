/**
 * hongshiwl.com Inc.
 * Copyright (c) 2015-2017 All Rights Reserved.
 */
package com.huoq.thread.bean;

import com.huoq.admin.product.bean.NoticeBean;
import com.huoq.common.util.ObjectUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.*;
import com.huoq.thread.dao.ThreadDAO;
import com.huoq.thread.model.CouponPush;
import com.huoq.util.MyLogger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 理财券红包到期前发送推送消息提醒
 * @author guoyin.yi
 * @version $Id: CouponSendPushBean.java, v 0.1  2017/12/16 Exp $
 */
@Service
public class CouponSendPushThreadBean {
    MyLogger logger = MyLogger.getLogger(CouponSendPushThreadBean.class);
    @Resource
    private JiguangPushBean jiGuangPushBean;

    @Resource
    private NoticeBean noticeBean;

    @Resource
    private ThreadDAO dao;

    public List<CouponPush> queryOverTimeSeven(String overTime){
        List<CouponPush> result = new ArrayList<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dateTime = format.format(new Date()).trim();
        StringBuffer sql = new StringBuffer(" select users_id ,count(1) couponcount from coupon  ");
        sql.append(" where status = 0 ");
        sql.append(" and TIMESTAMPDIFF(day,date_format('"+dateTime+"','%Y-%m-%d'),date_format(over_time,'%Y-%m-%d')) ="+overTime+" ");
        sql.append("   and users_id=1886  group by users_id ");
        System.out.println("#######################MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM"+sql.toString());
        List<Map<String, Object>>  list = dao.LoadAllListMapSql(sql.toString(),null);
        try {
            for (Map<String, Object> map : list) {
                CouponPush couponPush = new CouponPush();
                couponPush = (CouponPush) ObjectUtil.mapToObject(map, couponPush);
                result.add(couponPush);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        System.out.println("#######################MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM"+list.size());
        return result;
    }


    public void queryCupon(List<CouponPush> list,String message){
        try {
            for (int i = 0; i < list.size(); i++) {
                Long usersId = list.get(i).getUsersId();
                String pushMsg = String.format(message,list.get(i).getCouponCount());
                logger.info("push 消息："+pushMsg);
                //保存消息到系统消息表
                Notice notice = new Notice();
                notice.setType("1");
                notice.setUsersAdminId(0L);
                notice.setTitle("您有理财券或红包即将过期");
                notice.setStatus("0");
                notice.setUsersId(usersId);
                notice.setContent(pushMsg);
                UsersAdmin ua = new UsersAdmin();
                ua.setId(0L);

                noticeBean.saveNotice(notice,ua);
                jiGuangPushBean.jiguangPush(list.get(i).getUsersId(),pushMsg);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


}
