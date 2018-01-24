package com.huoq.admin.product.bean;

import com.huoq.admin.product.dao.ActivityDAO;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.*;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.log4j.Logger;
import org.aspectj.weaver.ast.Var;
import org.jfree.util.Log;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 入口统计
 *
 * @author 曾礼强
 * 2015年6月10日 10:55:05
 */
@Service
public class ActivityBean {

    private static Logger log = Logger.getLogger(ActivityBean.class);

    @Resource
    ActivityDAO dao;
    @Resource
    UsersConvertBean bean;

    /**
     * 获取Activity统计的数据
     *
     * @param insertTime 激活时间
     * @return
     */
    public List<ActivityStat> findTjActivitys(String insertTime) throws Exception {
        List<Object> arrayList = new ArrayList<Object>();
        StringBuffer sql = new StringBuffer();
        sql.append(" SELECT  aty.channel,COUNT(aty.channel)'激活人数',");
        sql.append(" (SELECT COUNT(*) FROM users us WHERE us.regist_channel = aty.channel AND us.regist_platform = '1') '注册人数' ,");
        sql.append(" (SELECT COUNT(*) FROM account ac LEFT JOIN users us ON ac.users_id = us.id WHERE ac.status = '0' AND us.regist_channel = aty.channel AND us.regist_platform = '1')'绑定银行卡人数' ");
        sql.append(" FROM Activity as aty   ");
        sql.append(" WHERE aty .channel is not NULL ");
        //发布时间
        if (!QwyUtil.isNullAndEmpty(insertTime)) {
            String[] time = QwyUtil.splitTime(insertTime);
            if (time.length > 1) {
                sql.append(" AND aty.insert_time >= ? ");
                arrayList.add(QwyUtil.fmMMddyyyy.parse(time[0]));
                sql.append(" AND aty.insert_time <= ? ");
                arrayList.add(QwyUtil.fmMMddyyyy.parse(time[1]));
            } else {
                sql.append(" AND aty.insert_time >= ? ");
                arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                sql.append(" AND aty.insert_time <= ? ");
                arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
            }
        }
        sql.append(" GROUP BY aty.channel ");
        List<Object[]> list = dao.LoadAllSql(sql.toString(), arrayList.toArray());
        return parseActivityStat(list);
    }

    /**
     * 获取渠道报表数据
     *
     * @param sourceFileName
     * @return
     * @throws Exception
     */
    public List<JasperPrint> getQdtjJasperPrintList(String sDate, String eDate, String sourceFileName, String channelType) throws Exception {
        List<JasperPrint> list = new ArrayList<JasperPrint>();
        //List<Qdtj> qdtjs=getQdtj(insertTime,channelCode);
        List<Qdtj> qdtjs = loadQdtjMain(sDate, eDate, channelType);
        if (!QwyUtil.isNullAndEmpty(qdtjs)) {
            Qdtj qdtj = tjQdtj(qdtjs);
            Map<String, String> map = QwyUtil.getValueMap(qdtj);
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(qdtjs);
            //JasperPrint 	 js=JasperFillManager.fillReport(context.getRealPath(path) +File.separator+getJxmlStr(), map, ds);"D:\\table"+File.separator+"releaseProduct.jasper"
            JasperPrint js = JasperFillManager.fillReport(sourceFileName, map, ds);
            list.add(js);
        }
        return list;
    }

    /**
     * 获取单个渠道详情报表数据
     *
     * @param registChannel
     * @param insertTime
     * @param sourceFileName
     * @return
     * @throws Exception
     */
    public List<JasperPrint> getQdtjDetailJasperPrintList(String registChannel, String insertTime, String sourceFileName) throws Exception {
        List<JasperPrint> list = new ArrayList<JasperPrint>();
        List<ChannelOperateDay> details = new ArrayList<ChannelOperateDay>();
        List<ChannelOperateDay> ChannelOperateDays = getQdtjDetails(registChannel, insertTime);
        if (!QwyUtil.isNullAndEmpty(ChannelOperateDays)) {
            for (Object obj : ChannelOperateDays) {
                ChannelOperateDay channelOperateDay = (ChannelOperateDay) obj;
                channelOperateDay.setRegActivityRate(channelOperateDay.getRegActivityRate() * 100);
                channelOperateDay.setReInvestRate(channelOperateDay.getReInvestRate() * 100);
                details.add(channelOperateDay);
            }
            Map<String, String> map = QwyUtil.getValueMap(details);
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(details);
            //JasperPrint 	 js=JasperFillManager.fillReport(context.getRealPath(path) +File.separator+getJxmlStr(), map, ds);"D:\\table"+File.separator+"releaseProduct.jasper"
            JasperPrint js = JasperFillManager.fillReport(sourceFileName, map, ds);
            list.add(js);
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    public List<ChannelOperateDay> getQdtjDetails(String registChannel, String insertTime) throws Exception {
        List<Object> list = new ArrayList<Object>();
        StringBuffer buffer = new StringBuffer(" FROM ChannelOperateDay cd  where 1=1 ");
        if (!QwyUtil.isNullAndEmpty(registChannel)) {
            buffer.append("  and cd.registChannel = ");
            buffer.append(Integer.valueOf(registChannel));
        }
        if (!QwyUtil.isNullAndEmpty(insertTime)) {
            String[] time = QwyUtil.splitTime(insertTime);
            if (time.length > 1) {
                buffer.append(" AND cd.date >= ? ");
                list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
                buffer.append(" AND cd.date  <= ? ");
                list.add(QwyUtil.fmMMddyyyy.parse(time[1]));
            } else {
                buffer.append(" AND cd.date  >= ? ");
                list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                buffer.append(" AND cd.date  <= ? ");
                list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
            }
        }
        List<ChannelOperateDay> details = dao.LoadAll(buffer.toString(), list.toArray());

        return details;
    }

    /**
     * 将数据转换为ActivityStat
     *
     * @param list
     * @return
     */
    public List<ActivityStat> parseActivityStat(List<Object[]> list) throws Exception {
        List<ActivityStat> activityStats = new ArrayList<ActivityStat>();
        if (!QwyUtil.isNullAndEmpty(list)) {
            for (Object[] object : list) {
                ActivityStat activityStat = new ActivityStat();
                activityStat.setChannel(object[0] + "");
                activityStat.setChannelCount(object[1] + "");
                activityStat.setRegCount(!QwyUtil.isNullAndEmpty(object[2]) ? object[2] + "" : "0");
                activityStat.setBindCount(!QwyUtil.isNullAndEmpty(object[3]) ? object[3] + "" : "0");
                activityStats.add(activityStat);
            }
        }
        return activityStats;
    }


    /**
     * 以平台分组获取绑定人数
     *
     * @return
     * @throws Exception
     */
    public String bindjl(String channelCode, String insertTime, List<Object> arrayList) throws Exception {
        StringBuffer buffer = new StringBuffer();
        buffer.append(" SELECT COUNT(DISTINCT acc.users_id) ,us.regist_channel ");
        buffer.append(" FROM account acc ");
        buffer.append("	INNER JOIN users us on us.id = acc.users_id ");
        buffer.append(" AND us.regist_platform = '1' ");
        buffer.append(" WHERE acc.`status` = '0'  ");
        if (!QwyUtil.isNullAndEmpty(channelCode)) {
            buffer.append(" AND us.regist_channel = ? ");
            arrayList.add(channelCode);
        }
        if (!QwyUtil.isNullAndEmpty(insertTime)) {
            String[] time = QwyUtil.splitTime(insertTime);
            if (time.length > 1) {
                buffer.append(" AND acc.insert_time >= ? ");
                arrayList.add(QwyUtil.fmMMddyyyy.parse(time[0]));
                buffer.append(" AND acc.insert_time < ? ");
                arrayList.add(QwyUtil.fmMMddyyyy.parse(time[1]));
            } else {
                buffer.append(" AND acc.insert_time >= ? ");
                arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                buffer.append(" AND acc.insert_time <= ? ");
                arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
            }
        }
        buffer.append(" GROUP BY us.regist_channel  ");
        return buffer.toString();
    }

    /**
     * 以平台分组获取投资记录
     *
     * @return
     * @throws Exception
     */
    public String tzjl(String channelCode, String insertTime, List<Object> arrayList) throws Exception {
        StringBuffer buffer = new StringBuffer();
        buffer.append(" SELECT COUNT(DISTINCT ivs.users_id) ,SUM(ivs.in_money) ,us.regist_channel ");
        buffer.append(" FROM investors ivs  ");
        buffer.append("	INNER  JOIN users us on us.id = ivs.users_id ");
        buffer.append(" AND us.regist_platform = '1' ");
        buffer.append(" WHERE ivs.investor_status in ('1','2','3')  ");
        if (!QwyUtil.isNullAndEmpty(channelCode)) {
            buffer.append(" AND us.regist_channel = ? ");
            arrayList.add(channelCode);
        }
        if (!QwyUtil.isNullAndEmpty(insertTime)) {
            String[] time = QwyUtil.splitTime(insertTime);
            if (time.length > 1) {
                buffer.append(" AND ivs.pay_time >= ? ");
                arrayList.add(QwyUtil.fmMMddyyyy.parse(time[0]));
                buffer.append(" AND ivs.pay_time < ? ");
                arrayList.add(QwyUtil.fmMMddyyyy.parse(time[1]));
            } else {
                buffer.append(" AND ivs.pay_time >= ? ");
                arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                buffer.append(" AND ivs.pay_time <= ? ");
                arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
            }
        }
        buffer.append(" GROUP BY us.regist_channel  ");
        return buffer.toString();
    }

    /**
     * 以平台分组获取首投人数和金额
     *
     * @return
     * @throws Exception
     */
    public String stjl(String channelCode, String insertTime, List<Object> arrayList) throws Exception {
        StringBuffer buffer = new StringBuffer();
        buffer.append(" SELECT COUNT(DISTINCT d.users_id1),SUM(d.in_money1),	c.regist_channel");
        buffer.append(" FROM users c,( SELECT	a.id,	a.users_id AS users_id1, a.in_money AS in_money1,");
        buffer.append(" a.copies AS copies,	a.pay_time AS pay_time1	FROM investors a, ");
        buffer.append(" (SELECT	users_id, MIN(pay_time) AS pay_time FROM investors WHERE investor_status IN (1, 2, 3) GROUP BY users_id) b");
        buffer.append("	WHERE	a.users_id = b.users_id	AND a.pay_time = b.pay_time		AND a.investor_status IN (1, 2, 3) ");
        buffer.append(" ) d");
        buffer.append(" WHERE c.id = d.users_id1 AND c.regist_platform = 1 ");
        if (!QwyUtil.isNullAndEmpty(channelCode)) {
            buffer.append(" AND c.regist_channel = ? ");
            arrayList.add(channelCode);
        }
        if (!QwyUtil.isNullAndEmpty(insertTime)) {
            String[] time = QwyUtil.splitTime(insertTime);
            if (time.length > 1) {
                buffer.append(" AND  d.pay_time1 >= ? ");
                arrayList.add(QwyUtil.fmMMddyyyy.parse(time[0]));
                buffer.append(" AND  d.pay_time1 < ? ");
                arrayList.add(QwyUtil.fmMMddyyyy.parse(time[1]));
            } else {
                buffer.append(" AND  d.pay_time1 >= ? ");
                arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                buffer.append(" AND  d.pay_time1 <= ? ");
                arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
            }
        }
        buffer.append(" GROUP BY c.regist_channel");
        return buffer.toString();
    }
//	public String stjl(String insertTime,List<Object> arrayList) throws Exception{
//		StringBuffer buffer=new StringBuffer();
//		buffer.append(" SELECT	COUNT(DISTINCT t.users_id),	SUM(t.in_money),	us.regist_channel FROM users us  ");
//		buffer.append(" LEFT JOIN (  ");
//		buffer.append("	SELECT	MIN(ins.pay_time) AS date,ins.users_id,ins.in_money ");
//		buffer.append(" FROM	investors ins ");
//		buffer.append(" WHERE ins.investor_status in ('1','2','3')  ");
//		if(!QwyUtil.isNullAndEmpty(insertTime)){
//			String [] time=QwyUtil.splitTime(insertTime);
//			if(time.length>1){
//				buffer.append(" AND ins.pay_time >= ? ");
//				arrayList.add(QwyUtil.fmMMddyyyy.parse(time[0]));
//				buffer.append(" AND ins.pay_time <= ? ");
//				arrayList.add(QwyUtil.fmMMddyyyy.parse(time[1]));
//			}else{
//				buffer.append(" AND ins.pay_time >= ? ");
//				arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 00:00:00"));
//				buffer.append(" AND ins.pay_time <= ? ");
//				arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 23:59:59"));
//			}
//		}
//		buffer.append(" GROUP BY ins.users_id  ");
//		buffer.append(" )t  ");
//		buffer.append(" ON us.id = t.users_id  ");
//		buffer.append(" WHERE us.regist_platform = '1'  ");
//		buffer.append(" GROUP BY us.regist_channel  ");
//		return buffer.toString();
//	}
//	public String stjl(String insertTime,List<Object> arrayList) throws Exception{
//		StringBuffer buffer=new StringBuffer();
//		buffer.append(" SELECT COUNT(DISTINCT ivs.users_id) ,SUM(ivs.in_money) ,us.regist_channel ");
//		buffer.append(" FROM investors ivs ");
//		buffer.append("	LEFT JOIN users us on us.id = ivs.users_id ");
//		buffer.append(" AND us.regist_platform = '1' ");
//		buffer.append(" WHERE ivs.investor_status in ('1','2','3')  ");
//		buffer.append(" AND ivs.pay_time in ( SELECT MIN(ins.pay_time) as date FROM investors ins  WHERE ins.investor_status in ('1','2','3') GROUP BY users_id ) ");
//		if(!QwyUtil.isNullAndEmpty(insertTime)){
//			String [] time=QwyUtil.splitTime(insertTime);
//			if(time.length>1){
//				buffer.append(" AND ivs.pay_time >= ? ");
//				arrayList.add(QwyUtil.fmMMddyyyy.parse(time[0]));
//				buffer.append(" AND ivs.pay_time <= ? ");
//				arrayList.add(QwyUtil.fmMMddyyyy.parse(time[1]));
//			}else{
//				buffer.append(" AND ivs.pay_time >= ? ");
//				arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 00:00:00"));
//				buffer.append(" AND ivs.pay_time <= ? ");
//				arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 23:59:59"));
//			}
//		}
//		buffer.append(" GROUP BY us.regist_channel  ");
//		return buffer.toString();
//	}

    /**
     * 以平台分组获取复投人数和金额
     *
     * @return
     * @throws Exception
     */
    public String ftjl(String channelCode, String insertTime, List<Object> arrayList) throws Exception {
        StringBuffer buffer = new StringBuffer();
        buffer.append(" SELECT count(	DISTINCT CASE	WHEN a.pay_time > b.minPayTime THEN	a.users_id	ELSE NULL	END	) reInvestUserSum ,c.regist_channel");
        buffer.append(" FROM investors a INNER JOIN (	SELECT	users_id,	MIN(DATE_FORMAT(pay_time, '%Y-%m-%d')) minPayDate,");
        buffer.append(" MIN(pay_time) minPayTime FROM	investors a	WHERE	a.investor_status IN (1, 2, 3)");
        buffer.append(" GROUP BY	users_id) b ON a.users_id = b.users_id");
        buffer.append(" INNER JOIN users c ON b.users_id = c.id	WHERE	a.investor_status IN (1, 2, 3)");
        buffer.append(" and c.regist_platform = '1' ");
        if (!QwyUtil.isNullAndEmpty(channelCode)) {
            buffer.append(" AND c.regist_channel = ?  ");
            arrayList.add(channelCode);
        }
        if (!QwyUtil.isNullAndEmpty(insertTime)) {
            String[] time = QwyUtil.splitTime(insertTime);
            if (time.length > 1) {
                buffer.append(" AND a.pay_time >= ? ");
                arrayList.add(QwyUtil.fmMMddyyyy.parse(time[0]));
                buffer.append(" AND a.pay_time < ? ");
                arrayList.add(QwyUtil.fmMMddyyyy.parse(time[1]));
            } else {
                buffer.append(" AND a.pay_time >= ? ");
                arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                buffer.append(" AND a.pay_time <= ? ");
                arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
            }
        }
        buffer.append("GROUP BY	c.regist_channel");
//		buffer.append(" SELECT COUNT(DISTINCT ivs.users_id) ,us.regist_channel  ");
//		buffer.append(" FROM investors ivs ");
//		buffer.append("	INNER JOIN users us on us.id = ivs.users_id ");
//		buffer.append(" AND us.regist_platform = '1' ");
//		buffer.append(" WHERE ivs.investor_status in ('1','2','3')  ");
//		buffer.append(" AND  NOT EXISTS ( SELECT t.id FROM  ( SELECT MIN(ins.pay_time) as date,id FROM investors ins  WHERE ins.investor_status in ('1','2','3') GROUP BY users_id )t WHERE t.id=ivs.id) ");
//		if(!QwyUtil.isNullAndEmpty(insertTime)){
//			String [] time=QwyUtil.splitTime(insertTime);
//			if(time.length>1){
//				buffer.append(" AND ivs.pay_time >= ? ");
//				arrayList.add(QwyUtil.fmMMddyyyy.parse(time[0]));
//				buffer.append(" AND ivs.pay_time < ? ");
//				arrayList.add(QwyUtil.fmMMddyyyy.parse(time[1]));
//			}else{
//				buffer.append(" AND ivs.pay_time >= ? ");
//				arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 00:00:00"));
//				buffer.append(" AND ivs.pay_time <= ? ");
//				arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 23:59:59"));
//			}
//		}
//		buffer.append(" GROUP BY us.regist_channel  ");
        return buffer.toString();
    }
    /*public String ftjl(String insertTime,List<Object> arrayList) throws Exception{
		StringBuffer buffer=new StringBuffer();
		buffer.append(" SELECT COUNT(DISTINCT ivs.users_id) ,us.regist_channel  ");
		buffer.append(" FROM investors ivs ");
		buffer.append("	LEFT JOIN users us on us.id = ivs.users_id ");
		buffer.append(" AND us.regist_platform = '1' ");
		buffer.append(" WHERE ivs.investor_status in ('1','2','3')  ");
		buffer.append(" AND ivs.pay_time not in ( SELECT MIN(ins.pay_time) as date FROM investors ins  WHERE ins.investor_status in ('1','2','3') GROUP BY users_id ) ");
		if(!QwyUtil.isNullAndEmpty(insertTime)){
			String [] time=QwyUtil.splitTime(insertTime);
			if(time.length>1){
				buffer.append(" AND ivs.pay_time >= ? ");
				arrayList.add(QwyUtil.fmMMddyyyy.parse(time[0]));
				buffer.append(" AND ivs.pay_time <= ? ");
				arrayList.add(QwyUtil.fmMMddyyyy.parse(time[1]));
			}else{
				buffer.append(" AND ivs.pay_time >= ? ");
				arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 00:00:00"));
				buffer.append(" AND ivs.pay_time <= ? ");
				arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 23:59:59"));
			}
		}
		buffer.append(" GROUP BY us.regist_channel  ");
		return buffer.toString();
	}*/


    /**
     * 以平台分组获取充值次数和金额
     *
     * @return
     * @throws Exception
     */
    public String czjl(String channelCode, String insertTime, List<Object> arrayList) throws Exception {
        StringBuffer buffer = new StringBuffer();
        buffer.append(" SELECT COUNT(DISTINCT cr.id) ,SUM(cr.money) ,us.regist_channel ");
        buffer.append(" FROM cz_record cr ");
        buffer.append("	INNER JOIN users us on us.id = cr.users_id ");
        buffer.append(" AND us.regist_platform = '1' ");
        buffer.append(" WHERE cr.`STATUS` = '1'  ");
        if (!QwyUtil.isNullAndEmpty(channelCode)) {
            buffer.append(" AND us.regist_channel = ?  ");
            arrayList.add(channelCode);
        }
        if (!QwyUtil.isNullAndEmpty(insertTime)) {
            String[] time = QwyUtil.splitTime(insertTime);
            if (time.length > 1) {
                buffer.append(" AND cr.insert_time >= ? ");
                arrayList.add(QwyUtil.fmMMddyyyy.parse(time[0]));
                buffer.append(" AND cr.insert_time < ? ");
                arrayList.add(QwyUtil.fmMMddyyyy.parse(time[1]));
            } else {
                buffer.append(" AND cr.insert_time >= ? ");
                arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                buffer.append(" AND cr.insert_time <= ? ");
                arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
            }
        }
        buffer.append(" GROUP BY us.regist_channel  ");
        return buffer.toString();
    }

    public String insUsersCountSQL(String insertTime) throws Exception {
        ArrayList<Object> list = new ArrayList<Object>();
        StringBuffer buffer = new StringBuffer();
        buffer.append(" SELECT COUNT(DISTINCT users_id) FROM investors ivs  ");
        buffer.append(" WHERE ivs.investor_status in ('1','2','3') ");
        buffer.append(" AND EXISTS ( ");
        buffer.append(" SELECT id FROM  users  us ");
        buffer.append("  WHERE us.regist_platform = '1' ");
        buffer.append("  AND us.regist_channel != '' ");
        buffer.append("  AND us.regist_channel IS NOT NULL ");
        buffer.append("  AND us.id=ivs.users_id ");
        buffer.append("  ) ");
        if (!QwyUtil.isNullAndEmpty(insertTime)) {
            String[] time = QwyUtil.splitTime(insertTime);
            if (time.length > 1) {
                buffer.append(" AND DATE_FORMAT(ivs.pay_time, '%Y-%m-%d' )>= ? ");
                list.add(QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyy.parse(time[0])));
                buffer.append(" AND DATE_FORMAT(ivs.pay_time, '%Y-%m-%d' )<= ? ");
                list.add(QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59")));
            } else {
                buffer.append(" AND DATE_FORMAT(ivs.pay_time, '%Y-%m-%d' )>= ? ");
                list.add(QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00")));
                buffer.append(" AND DATE_FORMAT(ivs.pay_time, '%Y-%m-%d' )<= ? ");
                list.add(QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59")));
            }
        }
        Object object = dao.getSqlCount(buffer.toString(), list.toArray());
        if (!QwyUtil.isNullAndEmpty(object)) {
            return object + "";
        }
        return "0";
    }

    /**
     * 投资金额
     *
     * @param insertTime 投资时间
     * @throws Exception
     */
    public String insMoney(String insertTime) throws Exception {
        ArrayList<Object> list = new ArrayList<Object>();
        StringBuffer buffer = new StringBuffer();
//		buffer.append(" SELECT SUM(ivs.in_money) FROM investors ivs  ");
//		buffer.append(" AND EXISTS ( ");
//		buffer.append(" SELECT id FROM  users  us ");
//		buffer.append("  WHERE us.regist_platform = '1' ");
//		buffer.append("  AND us.regist_channel != '' ");
//		buffer.append("  AND us.regist_channel IS NOT NULL ");
//		buffer.append("  AND us.id=ivs.users_id ");
//		buffer.append("  ) ");
//		if(!QwyUtil.isNullAndEmpty(insertTime)){
//			String [] time=QwyUtil.splitTime(insertTime);
//			if(time.length>1){
//				buffer.append(" AND ivs.pay_time>= ? ");
//				list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
//				buffer.append(" AND ivs.pay_time<= ? ");
//				list.add(QwyUtil.fmMMddyyyy.parse(time[1]));
//			}else{
//				buffer.append(" AND ivs.pay_time>= ? ");
//				list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 00:00:00"));
//				buffer.append(" AND ivs.pay_time<= ? ");
//				list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 23:59:59"));
//			}
//		}
        buffer.append(insMoneySQL(insertTime, list));
        Object object = dao.getSqlCount(buffer.toString(), list.toArray());
        if (!QwyUtil.isNullAndEmpty(object)) {
            return object + "";
        }
        return "0";
    }

    public String insMoneySQL(String insertTime, ArrayList<Object> list) throws Exception {
        StringBuffer buffer = new StringBuffer();
        buffer.append(" SELECT SUM(ivs.in_money) FROM investors ivs  ");
        buffer.append(" WHERE ivs.investor_status in ('1','2','3') ");
        buffer.append(" AND EXISTS ( ");
        buffer.append(" SELECT id FROM  users  us ");
        buffer.append("  WHERE us.regist_platform = '1' ");
        buffer.append("  AND us.regist_channel != '' ");
        buffer.append("  AND us.regist_channel IS NOT NULL ");
        buffer.append("  AND us.id=ivs.users_id ");
        buffer.append("  ) ");
        if (!QwyUtil.isNullAndEmpty(insertTime)) {
            String[] time = QwyUtil.splitTime(insertTime);
            if (time.length > 1) {
                buffer.append(" AND DATE_FORMAT(ivs.pay_time, '%Y-%m-%d' )>= ? ");
                list.add(QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyy.parse(time[0])));
                buffer.append(" AND DATE_FORMAT(ivs.pay_time, '%Y-%m-%d' )< ? ");
                list.add(QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59")));
            } else {

                buffer.append(" AND DATE_ADD( DATE_FORMAT( ivs.pay_time  , '%Y-%m-%d'),INTERVAL 31 DAY)  > ?  ");
                list.add(QwyUtil.fmyyyyMMdd.format(new Date(time[0] + "")));
                buffer.append(" AND  ivs.pay_time <= ? ");
                list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
            }
        }
        return buffer.toString();
    }

    /**
     * 首投人数
     *
     * @param insertTime
     * @param list
     * @return
     * @throws Exception
     */
    public String firstInsUsersCountSQL(String insertTime, ArrayList<Object> list) throws Exception {
//		StringBuffer buffer=new StringBuffer();
//		buffer.append(" SELECT COUNT(DISTINCT t.users_id) FROM (   ");
//		buffer.append("  SELECT  MIN(ins.pay_time) AS date,users_id  FROM   investors ins  ");
//		buffer.append(" WHERE		ins.investor_status IN ('1', '2', '3')  ");
//		buffer.append(" AND EXISTS ( ");
//		buffer.append(" SELECT id FROM  users  us ");
//		buffer.append("  WHERE us.regist_platform = '1' ");
//		buffer.append("  AND us.regist_channel != '' ");
//		buffer.append("  AND us.regist_channel IS NOT NULL ");
//		buffer.append("  AND us.id=ins.users_id ");
//		buffer.append("  ) ");
//		buffer.append("  GROUP BY	users_id ");
//		buffer.append(" )t ");
//		if(!QwyUtil.isNullAndEmpty(insertTime)){
//			String [] time=QwyUtil.splitTime(insertTime);
//			if(time.length>1){
//				buffer.append(" WHERE DATE_FORMAT(t.date, '%Y-%m-%d' )>= ? ");
//				list.add(QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyy.parse(time[0])));
//				buffer.append(" AND DATE_FORMAT(t.date, '%Y-%m-%d' )<= ? ");
//				list.add(QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyy.parse(time[1])));
//			}else{
//				buffer.append(" WHERE DATE_FORMAT(t.date, '%Y-%m-%d' )>= ? ");
//				list.add(QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 00:00:00")));
//				buffer.append(" AND DATE_FORMAT(t.date, '%Y-%m-%d' )<= ? ");
//				list.add(QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 23:59:59")));
//			}
//		}
        StringBuffer buffer = new StringBuffer();
        buffer.append(" SELECT SUM(m.usercount) FROM (   ");
        buffer.append(" SELECT	COUNT(DISTINCT t.users_id) as usercount,	SUM(t.in_money),	us.regist_channel FROM users us  ");
        buffer.append(" INNER JOIN (  ");
        buffer.append(" SELECT	ivs.users_id as users_id ,	ivs.in_money AS in_money,	ivs.pay_time FROM investors ivs  ");
        buffer.append(" WHERE ivs.investor_status in ('1','2','3')  ");
        buffer.append(" AND EXISTS ( SELECT	w.date	FROM	( ");
        buffer.append("	SELECT	MIN(ins.pay_time) AS date,ins.users_id,ins.in_money ");
        buffer.append(" FROM	investors ins ");
        buffer.append(" WHERE ins.investor_status in ('1','2','3')  ");
        buffer.append(" GROUP BY ins.users_id  ");
        buffer.append(" ) w  ");
        buffer.append(" WHERE	w.date = ivs.pay_time ");
        buffer.append(" ) ");
        if (!QwyUtil.isNullAndEmpty(insertTime)) {
            String[] time = QwyUtil.splitTime(insertTime);
            if (time.length > 1) {
                buffer.append(" AND ivs.pay_time >= ? ");
                list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
                buffer.append(" AND ivs.pay_time < ? ");
                list.add(QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59")));
            } else {
                buffer.append(" AND ivs.pay_time >= ? ");
                list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                buffer.append(" AND ivs.pay_time <= ? ");
                list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
            }
        }

        buffer.append(" )t ");

        buffer.append(" ON us.id = t.users_id  ");
        buffer.append(" WHERE us.regist_platform = '1'  ");
        buffer.append("  AND us.regist_channel != '' ");
        buffer.append("  AND us.regist_channel IS NOT NULL ");
        buffer.append(" GROUP BY us.regist_channel  ");
        buffer.append(" )m ");
        return buffer.toString();

    }

    /**
     * 首投金额
     *
     * @param insertTime 投资时间
     * @throws Exception
     */
    public String firstInsMoney(String insertTime) throws Exception {
        ArrayList<Object> list = new ArrayList<Object>();
        StringBuffer buffer = new StringBuffer();
//		buffer.append(" SELECT SUM(ivs.in_money) FROM investors ivs ");
//		buffer.append(" WHERE ivs.investor_status IN ('1','2','3') ");
//		buffer.append(" AND ivs.pay_time in ( ");
//		buffer.append("  SELECT MIN(ins.pay_time) as date FROM investors ins  WHERE ins.investor_status in ('1','2','3') GROUP BY users_id ) ");
//		buffer.append(" AND EXISTS ( ");
//		buffer.append(" SELECT id FROM  users  us ");
//		buffer.append("  WHERE us.regist_platform = '1' ");
//		buffer.append("  AND us.regist_channel != '' ");
//		buffer.append("  AND us.regist_channel IS NOT NULL ");
//		buffer.append("  AND us.id=ivs.users_id ");
//		buffer.append("  ) ");
//		if(!QwyUtil.isNullAndEmpty(insertTime)){
//			String [] time=QwyUtil.splitTime(insertTime);
//			if(time.length>1){
//				buffer.append(" AND ivs.pay_time>= ? ");
//				list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
//				buffer.append(" AND ivs.pay_time<= ? ");
//				list.add(QwyUtil.fmMMddyyyy.parse(time[1]));
//			}else{
//				buffer.append(" AND ivs.pay_time>= ? ");
//				list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 00:00:00"));
//				buffer.append(" AND ivs.pay_time<= ? ");
//				list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 23:59:59"));
//			}
//		}
        //buffer.append(firstInsMoneySQL(insertTime, list));
        buffer.append(" SELECT  sum(t.stje) FROM ( ");
        buffer.append(" SELECT DATE_FORMAT( dd.insert_time, '%Y-%m-%d' ) as date,  ");
        buffer.append("  q.strs , ");
        buffer.append("  q.stje");
        buffer.append(" FROM dateday dd  ");
        //首投人数，首投金额
        buffer.append(" LEFT JOIN (  ");
        buffer.append(" SELECT DATE_FORMAT(shoutouDate, '%Y-%m-%d') as date,COUNT(*) AS strs,SUM(in_money) as stje FROM ( ");
        buffer.append(" SELECT DATE_FORMAT(investors1.pay_time,'%Y-%m-%d') AS shoutouDate,investors1.users_id ,investors1.in_money FROM investors investors1 ");
        buffer.append(" INNER JOIN ( ");
        buffer.append(" SELECT MIN(t.pay_time) as minDate,users_id FROM investors t WHERE t.investor_status IN ('1','2','3') GROUP BY t.users_id ");
        buffer.append(" ) investors2 ON investors1.pay_time=investors2.minDate  ");
        buffer.append(" INNER JOIN users u ON u.id=investors1.users_id   ");
        buffer.append(" AND u.regist_platform = '1'  AND u.regist_channel != ''  AND u.regist_channel IS NOT NULL ");
        buffer.append(" WHERE DATE_FORMAT(investors1.pay_time,'%Y-%m-%d')=DATE_FORMAT(investors2.minDate,'%Y-%m-%d') GROUP BY investors1.users_id ");
        buffer.append(" ) tab4 GROUP BY shoutouDate ");
        buffer.append(" ) q ON q.date = DATE_FORMAT(dd.insert_time, '%Y-%m-%d')  ");
        if (!QwyUtil.isNullAndEmpty(insertTime)) {
            String[] time = QwyUtil.splitTime(insertTime);
            if (time.length > 1) {
                buffer.append(" WHERE dd.insert_time>= ? ");
                list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
                buffer.append(" AND dd.insert_time< ? ");
                list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
            } else {
                buffer.append("  WHERE DATE_ADD( DATE_FORMAT(  dd.insert_time  , '%Y-%m-%d'),INTERVAL 31 DAY)  > ?  ");
                list.add(QwyUtil.fmyyyyMMdd.format(new Date(time[0] + "")));
                buffer.append(" AND   dd.insert_time  <= ? ");
                list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
            }
        }
        buffer.append("  GROUP BY DATE_FORMAT(dd.insert_time, '%Y-%m-%d' )");
        buffer.append("  ORDER BY DATE_FORMAT(dd.insert_time, '%Y-%m-%d' ) DESC");
        buffer.append(" )t ");
        Object object = dao.getSqlCount(buffer.toString(), list.toArray());
        if (!QwyUtil.isNullAndEmpty(object)) {
            return object + "";
        }
        return "0";
    }

    /**
     * 手机金额
     *
     * @param insertTime
     * @param list
     * @return
     * @throws Exception
     */
    public String firstInsMoneySQL(String insertTime, ArrayList<Object> list) throws Exception {
//		StringBuffer buffer=new StringBuffer();
//		buffer.append(" SELECT SUM(t.in_money) FROM (   ");
//		buffer.append(" SELECT	ivs.users_id as users_id ,	ivs.in_money AS in_money,	ivs.pay_time FROM investors ivs  ");
//		buffer.append(" WHERE ivs.investor_status in ('1','2','3')  ");
//		buffer.append(" AND EXISTS ( SELECT	w.date	FROM	( ");
//		buffer.append("	SELECT	MIN(ins.pay_time) AS date,ins.users_id,ins.in_money ");
//		buffer.append(" FROM	investors ins ");
//		buffer.append(" WHERE ins.investor_status in ('1','2','3')  ");
//		buffer.append(" GROUP BY ins.users_id  ");
//		buffer.append(" ) w  ");
//		buffer.append(" WHERE	w.date = ivs.pay_time ");
//		buffer.append(" ) )t ");
//		if(!QwyUtil.isNullAndEmpty(insertTime)){
//			String [] time=QwyUtil.splitTime(insertTime);
//			if(time.length>1){
//				buffer.append(" WHERE DATE_FORMAT(t.pay_time, '%Y-%m-%d' )>= ? ");
//				list.add(QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyy.parse(time[0])));
//				buffer.append(" AND DATE_FORMAT(t.pay_time, '%Y-%m-%d' )<= ? ");
//				list.add(QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyy.parse(time[1])));
//			}else{
//				buffer.append(" WHERE DATE_FORMAT(t.pay_time, '%Y-%m-%d' )>= ? ");
//				list.add(QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 00:00:00")));
//				buffer.append(" AND DATE_FORMAT(t.pay_time, '%Y-%m-%d' )<= ? ");
//				list.add(QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 23:59:59")));
//			}
//		}
        StringBuffer buffer = new StringBuffer();
        buffer.append(" SELECT SUM(m.in_money) FROM (   ");
        buffer.append(" SELECT	COUNT(DISTINCT t.users_id) as usercount,	SUM(t.in_money) as in_money,	us.regist_channel FROM users us  ");
        buffer.append(" INNER JOIN (  ");
        buffer.append(" SELECT	ivs.users_id as users_id ,	ivs.in_money AS in_money,	ivs.pay_time FROM investors ivs  ");
        buffer.append(" WHERE ivs.investor_status in ('1','2','3')  ");
        buffer.append(" AND EXISTS ( SELECT	w.date	FROM	( ");
        buffer.append("	SELECT	MIN(ins.pay_time) AS date,ins.users_id,ins.in_money ");
        buffer.append(" FROM	investors ins ");
        buffer.append(" WHERE ins.investor_status in ('1','2','3')  ");
        buffer.append(" GROUP BY ins.users_id  ");
        buffer.append(" ) w  ");
        buffer.append(" WHERE	w.date = ivs.pay_time ");
        buffer.append(" ) ");
        if (!QwyUtil.isNullAndEmpty(insertTime)) {
            String[] time = QwyUtil.splitTime(insertTime);
            if (time.length > 1) {
                buffer.append(" AND ivs.pay_time >= ? ");
                list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
                buffer.append(" AND ivs.pay_time < ? ");
                list.add(QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59")));
            } else {
                buffer.append(" AND ivs.pay_time >= ? ");
                list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                buffer.append(" AND ivs.pay_time <= ? ");
                list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
            }
        }

        buffer.append(" )t ");

        buffer.append(" ON us.id = t.users_id  ");
        buffer.append(" WHERE us.regist_platform = '1'  ");
        buffer.append("  AND us.regist_channel != '' ");
        buffer.append("  AND us.regist_channel IS NOT NULL ");
        buffer.append(" GROUP BY us.regist_channel  ");
        buffer.append(" )m ");
        return buffer.toString();

    }

    /**
     * 绑定人数
     *
     * @param insertTime 绑定时间
     * @throws Exception
     */
    public String bindUserCount(String insertTime) throws Exception {
        ArrayList<Object> list = new ArrayList<Object>();
        StringBuffer buffer = new StringBuffer();
        buffer.append("  SELECT  COUNT(DISTINCT acc.users_id) FROM account acc  ");
        buffer.append("  WHERE  acc.`status` = '0' ");
        if (!QwyUtil.isNullAndEmpty(insertTime)) {
            String[] time = QwyUtil.splitTime(insertTime);
            if (time.length > 1) {
                buffer.append(" AND DATE_FORMAT(acc.insert_time, '%Y-%m-%d' )>= ? ");
                list.add(QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyy.parse(time[0])));
                buffer.append(" AND DATE_FORMAT(acc.insert_time, '%Y-%m-%d' )< ? ");
                list.add(QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyy.parse(time[1])));
            } else {
                buffer.append(" AND DATE_FORMAT(acc.insert_time, '%Y-%m-%d' )>= ? ");
                list.add(QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00")));
                buffer.append(" AND DATE_FORMAT(acc.insert_time, '%Y-%m-%d' )<= ? ");
                list.add(QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59")));
            }
        }
        buffer.append(" AND EXISTS ( ");
        buffer.append(" SELECT id FROM  users  us ");
        buffer.append("  WHERE us.regist_platform = '1' ");
        buffer.append("  AND us.regist_channel != '' ");
        buffer.append("  AND us.regist_channel IS NOT NULL ");
        buffer.append("  AND us.id=acc.users_id ");
        buffer.append("  ) ");
        Object object = dao.getSqlCount(buffer.toString(), list.toArray());
        if (!QwyUtil.isNullAndEmpty(object)) {
            return object + "";
        }
        return "0";
    }

    public String regUserCountSQL(String insertTime, ArrayList<Object> list) throws Exception {
        StringBuffer buffer = new StringBuffer();
        buffer.append("  SELECT  COUNT(DISTINCT us.id) FROM  users us ");
        buffer.append("  WHERE us.regist_platform = '1' ");
        buffer.append("  AND us.regist_channel != '' ");
        buffer.append("  AND us.regist_channel IS NOT NULL ");
        if (!QwyUtil.isNullAndEmpty(insertTime)) {
            String[] time = QwyUtil.splitTime(insertTime);
            if (time.length > 1) {
                buffer.append(" AND DATE_FORMAT(us.insert_time, '%Y-%m-%d' )>= ? ");
                list.add(QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyy.parse(time[0])));
                buffer.append(" AND DATE_FORMAT(us.insert_time, '%Y-%m-%d' )<= ? ");
                list.add(QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59")));
            } else {
                buffer.append(" AND DATE_FORMAT(us.insert_time, '%Y-%m-%d' )>= ? ");
                list.add(QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00")));
                buffer.append(" AND DATE_FORMAT(us.insert_time, '%Y-%m-%d' )<= ? ");
                list.add(QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59")));
            }
        }
        return buffer.toString();
    }


    /**
     * 充值次数
     *
     * @param insertTime 充值时间
     * @throws Exception
     */
    public String czCount(String insertTime) throws Exception {
        ArrayList<Object> list = new ArrayList<Object>();
        StringBuffer buffer = new StringBuffer();
        buffer.append("  SELECT COUNT(DISTINCT cr.id) FROM cz_record cr  ");
        buffer.append("  WHERE cr.`STATUS` = '1' ");
        if (!QwyUtil.isNullAndEmpty(insertTime)) {
            String[] time = QwyUtil.splitTime(insertTime);
            if (time.length > 1) {
                buffer.append(" AND DATE_FORMAT(cr.insert_time, '%Y-%m-%d' )>= ? ");
                list.add(QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyy.parse(time[0])));
                buffer.append(" AND DATE_FORMAT(cr.insert_time, '%Y-%m-%d' )< ? ");
                list.add(QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyy.parse(time[1])));
            } else {
                buffer.append(" AND DATE_FORMAT(cr.insert_time, '%Y-%m-%d' )>= ? ");
                list.add(QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00")));
                buffer.append(" AND DATE_FORMAT(cr.insert_time, '%Y-%m-%d' )<= ? ");
                list.add(QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59")));
            }
        }
        buffer.append(" AND EXISTS ( ");
        buffer.append(" SELECT id FROM  users  us ");
        buffer.append("  WHERE us.regist_platform = '1' ");
        buffer.append("  AND us.regist_channel != '' ");
        buffer.append("  AND us.regist_channel IS NOT NULL ");
        buffer.append("  AND us.id=cr.users_id ");
        buffer.append("  ) ");
        Object object = dao.getSqlCount(buffer.toString(), list.toArray());
        if (!QwyUtil.isNullAndEmpty(object)) {
            return object + "";
        }
        return "0";
    }

    /**
     * 充值金额
     *
     * @param insertTime 充值时间
     * @throws Exception
     */
    public String czMoney(String insertTime) throws Exception {
        ArrayList<Object> list = new ArrayList<Object>();
        StringBuffer buffer = new StringBuffer();
        buffer.append("  SELECT SUM(cr.money) FROM cz_record cr  ");
        buffer.append("  WHERE cr.`STATUS` = '1' ");
        if (!QwyUtil.isNullAndEmpty(insertTime)) {
            String[] time = QwyUtil.splitTime(insertTime);
            if (time.length > 1) {
                buffer.append(" AND DATE_FORMAT(cr.insert_time, '%Y-%m-%d' )>= ? ");
                list.add(QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyy.parse(time[0])));
                buffer.append(" AND DATE_FORMAT(cr.insert_time, '%Y-%m-%d' )< ? ");
                list.add(QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyy.parse(time[1])));
            } else {
                buffer.append(" AND DATE_FORMAT(cr.insert_time, '%Y-%m-%d' )>= ? ");
                list.add(QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00")));
                buffer.append(" AND DATE_FORMAT(cr.insert_time, '%Y-%m-%d' )<= ? ");
                list.add(QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59")));
            }
        }
        buffer.append(" AND EXISTS ( ");
        buffer.append(" SELECT id FROM  users  us ");
        buffer.append("  WHERE us.regist_platform = '1' ");
        buffer.append("  AND us.regist_channel != '' ");
        buffer.append("  AND us.regist_channel IS NOT NULL ");
        buffer.append("  AND us.id=cr.users_id ");
        buffer.append("  ) ");
        Object object = dao.getSqlCount(buffer.toString(), list.toArray());
        if (!QwyUtil.isNullAndEmpty(object)) {
            return object + "";
        }
        return "0";
    }


    /**
     * 将数据转换为渠道成本
     *
     * @param list
     * @return
     */
    public List<Qdcb> parseQdcb(List<Object[]> list) throws Exception {
        List<Qdcb> qdcbs = new ArrayList<Qdcb>();
        if (!QwyUtil.isNullAndEmpty(list)) {
            for (int i = 0; i < list.size(); i++) {
                Object[] object = list.get(i);
                Qdcb qdcb = new Qdcb();
                qdcb.setDate(object[0] + "");
                qdcb.setRegUsersCount(!QwyUtil.isNullAndEmpty(object[1]) ? object[1] + "" : "0");
                qdcb.setInsUsersCount(!QwyUtil.isNullAndEmpty(object[2]) ? object[2] + "" : "0");
                qdcb.setInsMoney(!QwyUtil.isNullAndEmpty(object[3]) ? QwyUtil.calcNumber(object[3], 100, "/", 2) + "" : "0");
                qdcb.setStrs(!QwyUtil.isNullAndEmpty(object[4]) ? object[4] + "" : "0");
                qdcb.setStje(!QwyUtil.isNullAndEmpty(object[5]) ? QwyUtil.calcNumber(object[5], 100, "/", 2) + "" : "0");
                qdcbs.add(qdcb);
            }
        }
        return qdcbs;
    }


    /**
     * 合计
     *
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public Qdcb tjQdcb(String insertTime) {
        List<Object> list = new ArrayList<Object>();
        Qdcb cr = new Qdcb();
        try {
            StringBuffer buffer = new StringBuffer("FROM BackStatsOperateDay rr ");
            buffer.append(" WHERE 1=1 and rr.registPlatform =1 ");
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    buffer.append(" AND rr.date >= ? ");
                    list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
                    buffer.append(" AND rr.date  <= ? ");
                    list.add(QwyUtil.fmMMddyyyy.parse(time[1]));
                } else {
                    buffer.append(" AND rr.date  >= ? ");
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND rr.date  <= ? ");
                    list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            }
            List<BackStatsOperateDay> backQdtj = dao.LoadAll(buffer.toString(), list.toArray());
            if (!QwyUtil.isNullAndEmpty(backQdtj)) {
                //投资人数
                cr.setInsUsersCount(insUsersCountSQL(insertTime));
                for (BackStatsOperateDay q : backQdtj) {
                    //注册人数
                    if (QwyUtil.isNullAndEmpty(cr.getRegUsersCount()))
                        cr.setRegUsersCount("0");
                    //投资人数
                    //if(QwyUtil.isNullAndEmpty(cr.getInsUsersCount()))
                    //cr.setInsUsersCount("0");
                    //投资金额
                    if (QwyUtil.isNullAndEmpty(cr.getInsMoney()))
                        cr.setInsMoney("0");
                    //首投人数
                    if (QwyUtil.isNullAndEmpty(cr.getStrs()))
                        cr.setStrs("0");
                    //首投金额
                    if (QwyUtil.isNullAndEmpty(cr.getStje()))
                        cr.setStje("0");

                    cr.setRegUsersCount(QwyUtil.calcNumber(cr.getRegUsersCount(), q.getRegUserSum(), "+") + "");
                    //	cr.setInsUsersCount(QwyUtil.calcNumber(cr.getInsUsersCount(), q.getInvestUserSum(), "+")+"");
                    cr.setInsMoney(QwyUtil.calcNumber(cr.getInsMoney(), q.getInvestCentSum(), "+") + "");
                    cr.setStrs(QwyUtil.calcNumber(cr.getStrs(), q.getFirstInvestUserSum(), "+") + "");
                    cr.setStje(QwyUtil.calcNumber(cr.getStje(), q.getFirstInvestCentSum(), "+") + "");

                }

            }

        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return cr;
    }


    @SuppressWarnings("unchecked")
    public PageUtil<BackStatsOperateDay> findQdcb(PageUtil pageUtil, String insertTime) throws Exception {
        List<Object> list = new ArrayList<Object>();
        StringBuffer buffer = new StringBuffer();
        buffer.append(" FROM BackStatsOperateDay us");
        buffer.append(" WHERE 1=1 and us.registPlatform =1 ");

        //发布时间
        if (!QwyUtil.isNullAndEmpty(insertTime)) {
            String[] time = QwyUtil.splitTime(insertTime);
            if (time.length > 1) {
                buffer.append(" AND us.date >= ? ");
                list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
                buffer.append(" AND us.date <= ? ");
                list.add(QwyUtil.fmMMddyyyy.parse(time[1]));
            } else {
                buffer.append(" AND us.date >= ? ");
                list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                buffer.append(" AND us.date <= ? ");
                list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
            }
        }
        buffer.append(" ORDER BY us.date DESC ");
        return dao.getByHqlAndHqlCount(pageUtil, buffer.toString(), buffer.toString(), list.toArray());
    }

    /**
     * 获取渠道成本报表数据
     *
     * @param insertTime
     * @param sourceFileName
     * @return
     * @throws Exception
     */
    public List<JasperPrint> getQdcbJasperPrintList(PageUtil pageUtil, String insertTime, String sourceFileName) throws Exception {
        List<JasperPrint> list = new ArrayList<JasperPrint>();
        List<BackStatsOperateDay> qdcbs = findQdcb(pageUtil, insertTime).getList();
        if (!QwyUtil.isNullAndEmpty(qdcbs)) {
            Qdcb qdcb = tjQdcb(insertTime);
            Map<String, String> map = QwyUtil.getValueMap(qdcb);
            JRBeanCollectionDataSource ds = new JRBeanCollectionDataSource(qdcbs);
            JasperPrint js = JasperFillManager.fillReport(sourceFileName, map, ds);
            list.add(js);
        }
        return list;
    }


    /**
     * 查询渠道统计的绑定记录
     *
     * @param insertTime
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> findQdBindjl(String insertTime, String channelCode) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            StringBuffer buffer = new StringBuffer();
            List<Object> arrayList = new ArrayList<Object>();
            buffer.append(bindjl(channelCode, insertTime, arrayList));
            List<Object[]> list = dao.LoadAllSql(buffer.toString(), arrayList.toArray());
            if (!QwyUtil.isNullAndEmpty(list)) {
                for (Object[] obj : list) {
                    if (!QwyUtil.isNullAndEmpty(obj[0])) {
                        map.put(obj[1] + "bind", obj[0] + "");
                    } else {
                        map.put(obj[1] + "bind", "0");
                    }
                }
            }
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return map;
    }

    /**
     * 查询渠道统计的投资记录
     *
     * @param insertTime
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> findQdTzjl(String insertTime, String channelCode) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            StringBuffer buffer = new StringBuffer();
            List<Object> arrayList = new ArrayList<Object>();
            buffer.append(tzjl(channelCode, insertTime, arrayList));
            List<Object[]> list = dao.LoadAllSql(buffer.toString(), arrayList.toArray());
            if (!QwyUtil.isNullAndEmpty(list)) {
                for (Object[] obj : list) {
                    if (!QwyUtil.isNullAndEmpty(obj[0])) {
                        map.put(obj[2] + "tzCount", obj[0] + "");
                    } else {
                        map.put(obj[2] + "tzCount", "0");
                    }
                    if (!QwyUtil.isNullAndEmpty(obj[1])) {
                        map.put(obj[2] + "tzCopies", obj[1] + "");
                    } else {
                        map.put(obj[2] + "tzCopies", "0");
                    }
                }
            }
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return map;
    }


    /**
     * 查询渠道统计的首投记录
     *
     * @param insertTime
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> findQdStjl(String insertTime, String channelCode) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            StringBuffer buffer = new StringBuffer();
            List<Object> arrayList = new ArrayList<Object>();
            buffer.append(stjl(channelCode, insertTime, arrayList));
            List<Object[]> list = dao.LoadAllSql(buffer.toString(), arrayList.toArray());
            if (!QwyUtil.isNullAndEmpty(list)) {
                for (Object[] obj : list) {
                    if (!QwyUtil.isNullAndEmpty(obj[0])) {
                        map.put(obj[2] + "stCount", obj[0] + "");
                    } else {
                        map.put(obj[2] + "stCount", "0");
                    }
                    if (!QwyUtil.isNullAndEmpty(obj[1])) {
                        map.put(obj[2] + "stCopies", obj[1] + "");
                    } else {
                        map.put(obj[2] + "stCopies", "0");
                    }
                }
            }
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return map;
    }


    /**
     * 查询渠道统计的复投记录
     *
     * @param insertTime
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> findQdFtjl(String insertTime, String channelCode) {
        Map<String, String> map = new HashMap<String, String>();
        try {
            StringBuffer buffer = new StringBuffer();
            List<Object> arrayList = new ArrayList<Object>();
            buffer.append(ftjl(channelCode, insertTime, arrayList));
            List<Object[]> list = dao.LoadAllSql(buffer.toString(), arrayList.toArray());
            if (!QwyUtil.isNullAndEmpty(list)) {
                for (Object[] obj : list) {
                    if (!QwyUtil.isNullAndEmpty(obj[0])) {
                        map.put(obj[1] + "ftCount", obj[0] + "");
                    } else {
                        map.put(obj[1] + "ftCount", "0");
                    }
                }
            }
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return map;
    }

    /**
     * 查询渠道统计的充值记录
     *
     * @param insertTime
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> findQdCzjl(String insertTime, String channelCode) {
        Map<String, String> map = new HashMap<String, String>();
        List<Object[]> list = null;
        try {
            StringBuffer buffer = new StringBuffer();
            List<Object> arrayList = new ArrayList<Object>();
            buffer.append(czjl(channelCode, insertTime, arrayList));
            list = dao.LoadAllSql(buffer.toString(), arrayList.toArray());
            if (!QwyUtil.isNullAndEmpty(list)) {
                for (Object[] obj : list) {
                    if (!QwyUtil.isNullAndEmpty(obj[0])) {
                        map.put(obj[2] + "czCount", obj[0] + "");
                    } else {
                        map.put(obj[2] + "czCount", "0");
                    }
                    if (!QwyUtil.isNullAndEmpty(obj[1])) {
                        map.put(obj[2] + "czMoney", obj[1] + "");
                    } else {
                        map.put(obj[2] + "czMoney", "0");
                    }
                }
            }
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return map;
    }

    /**
     * 查询渠道统计的激活人数
     *
     * @param insertTime
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> findQdJhrs(String insertTime, String channelCode) {

        Map<String, String> map = new HashMap<String, String>();
        List<Object[]> list = null;
        try {
            StringBuffer buffer = new StringBuffer();
            List<Object> arrayList = new ArrayList<Object>();
            buffer.append(" SELECT	COUNT(DISTINCT ac.id),ac.channel FROM activity ac WHERE 1=1 ");
            if (!QwyUtil.isNullAndEmpty(channelCode)) {
                buffer.append(" AND ac.channel = ? ");
                arrayList.add(channelCode);
            }
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    buffer.append(" WHERE ac.insert_time >= ? ");
                    arrayList.add(QwyUtil.fmMMddyyyy.parse(time[0]));
                    buffer.append(" AND ac.insert_time < ? ");
                    arrayList.add(QwyUtil.fmMMddyyyy.parse(time[1]));
                } else {
                    buffer.append(" WHERE ac.insert_time >= ? ");
                    arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND ac.insert_time <= ? ");
                    arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            }
            //buffer.append(" GROUP BY ac.channel ");
            list = dao.LoadAllSql(buffer.toString(), arrayList.toArray());
            if (!QwyUtil.isNullAndEmpty(list)) {
                for (Object[] obj : list) {
                    map.put(obj[1] + "jhCount", obj[0] + "");
                }
            }
            buffer.append(" GROUP BY ac.channel ");
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return map;
    }


    /**
     * 查询渠道统计的注册人数
     *
     * @param insertTime
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> findQdZcrs(String insertTime, String channelCode) {
        Map<String, String> map = new HashMap<String, String>();
        List<Object[]> list = null;
        try {
            StringBuffer buffer = new StringBuffer();
            List<Object> arrayList = new ArrayList<Object>();
            buffer.append(" SELECT	COUNT(DISTINCT us.id),us.regist_channel	FROM users us WHERE  us.regist_platform = '1' ");
            if (!QwyUtil.isNullAndEmpty(channelCode)) {
                buffer.append(" AND us.regist_channel = ? ");
                arrayList.add(channelCode);
            }
            if (!QwyUtil.isNullAndEmpty(insertTime)) {
                String[] time = QwyUtil.splitTime(insertTime);
                if (time.length > 1) {
                    buffer.append(" AND us.insert_time >= ? ");
                    arrayList.add(QwyUtil.fmMMddyyyy.parse(time[0]));
                    buffer.append(" AND us.insert_time < ? ");
                    arrayList.add(QwyUtil.fmMMddyyyy.parse(time[1]));
                } else {
                    buffer.append(" AND us.insert_time >= ? ");
                    arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                    buffer.append(" AND us.insert_time <= ? ");
                    arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                }
            }
            buffer.append(" GROUP BY us.regist_channel ");
            list = dao.LoadAllSql(buffer.toString(), arrayList.toArray());
            if (!QwyUtil.isNullAndEmpty(list)) {
                for (Object[] obj : list) {
                    map.put(obj[1] + "zcCount", obj[0] + "");
                }
            }
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    public List<PlatChannel> getPlatChannel(String beginDate, String endDate) {
        List<Object> list = new ArrayList<Object>();
        try {
            StringBuffer buffer = new StringBuffer("FROM PlatChannel rr ");
            buffer.append("WHERE 1=1 ");
            if (!QwyUtil.isNullAndEmpty(beginDate) && !QwyUtil.isNullAndEmpty(endDate)) {
                buffer.append(" AND rr.insertTime >= ? ");
                list.add(QwyUtil.fmyyyyMMdd.parse(beginDate));
                buffer.append(" AND rr.insertTime <= ? ");
                list.add(QwyUtil.fmyyyyMMdd.parse(endDate));
                //	buffer.append(" and DATE_FORMAT(AND rr.insertTime,'%Y-%m-%d') BETWEEN DATE_FORMAT('"+beginDate+"','%Y-%m-%d')  AND DATE_FORMAT('"+endDate+"','%Y-%m-%d')");
            } else {
                buffer.append(" AND rr.insertTime >= ? ");
                list.add(QwyUtil.fmMMddyyyy.parse(QwyUtil.fmMMddyyyy.format(QwyUtil.addDaysFromOldDate(new Date(), -31).getTime())));
                buffer.append(" AND rr.insertTime < ? ");
                list.add(QwyUtil.fmMMddyyyy.parse(QwyUtil.fmMMddyyyy.format(new Date().getTime())));
            }
            buffer.append("ORDER BY  rr.insertTime ASC");
            return (List<PlatChannel>) dao.LoadAll(buffer.toString(), list.toArray());
        } catch (Exception e) {
            log.error("操作异常: ", e);
        }
        return null;
    }

    /**
     * 获取渠道统计
     *
     * @param insertTime
     * @throws Exception
     */
    public List<Qdtj> getQdtj(String insertTime, String channelCode) throws Exception {
        List<Qdtj> list = new ArrayList<Qdtj>();
        Map<String, String> map = new HashMap<String, String>();
        List<PlatChannel> channels = dao.LoadAll("PlatChannel");
        map.putAll(findQdBindjl(insertTime, channelCode));
        map.putAll(findQdTzjl(insertTime, channelCode));
        map.putAll(findQdStjl(insertTime, channelCode));
        map.putAll(findQdFtjl(insertTime, channelCode));
        map.putAll(findQdCzjl(insertTime, channelCode));
        map.putAll(findQdJhrs(insertTime, channelCode));
        map.putAll(findQdZcrs(insertTime, channelCode));
		/*if(!QwyUtil.isNullAndEmpty(channels)){
			for (int i = 0; i < channels.size(); i++) {
				PlatChannel platChannel=channels.get(i);
				map.putAll(findQdBindjl(insertTime,platChannel.getChannelCode()));
				map.putAll(findQdTzjl(insertTime,platChannel.getChannelCode()));
				map.putAll(findQdStjl(insertTime,platChannel.getChannelCode()));
				map.putAll(findQdFtjl(insertTime,platChannel.getChannelCode()));
				map.putAll(findQdCzjl(insertTime,platChannel.getChannelCode()));
				map.putAll(findQdJhrs(insertTime,platChannel.getChannelCode()));
				map.putAll(findQdZcrs(insertTime,platChannel.getChannelCode()));
			}
		}*/

        if (!QwyUtil.isNullAndEmpty(channels)) {
            for (int i = 0; i < channels.size(); i++) {
                PlatChannel platChannel = channels.get(i);
                Qdtj qdtj = new Qdtj();
                //序号
                qdtj.setIndex((i + 1) + "");
                if (!QwyUtil.isNullAndEmpty(platChannel.getChannelName())) {
                    qdtj.setDate(QwyUtil.fmyyyyMMdd.parse(platChannel.getDate()));
                } else {
                    qdtj.setDate(null);
                }
                if (!QwyUtil.isNullAndEmpty(platChannel.getChannelName())) {
                    qdtj.setChannelName(platChannel.getChannelName());
                } else {
                    qdtj.setChannelName("");
                }


                //激活人数
                String activityCount = map.get(platChannel.getChannel() + "jhCount");
                if (QwyUtil.isNullAndEmpty(activityCount)) {
                    activityCount = "0";
                }
                qdtj.setActivityCount(activityCount);
                //绑定人数
                String bindCount = map.get(platChannel.getChannel() + "bind");
                if (QwyUtil.isNullAndEmpty(bindCount)) {
                    bindCount = "0";
                }
                qdtj.setBindCount(bindCount);
                //渠道
                qdtj.setChannel(platChannel.getChannel() + "");
                //充值人数
                String czcount = map.get(platChannel.getChannel() + "czCount");
                if (QwyUtil.isNullAndEmpty(czcount)) {
                    czcount = "0";
                }
                qdtj.setCzcount(czcount);
                //充值金额
                String czje = map.get(platChannel.getChannel() + "czMoney");
                if (QwyUtil.isNullAndEmpty(czje)) {
                    czje = "0";
                }
                qdtj.setCzje(QwyUtil.calcNumber(czje, "100", "/", 2).toString());
                //复投人数
                String ftrs = map.get(platChannel.getChannel() + "ftCount");
                if (QwyUtil.isNullAndEmpty(ftrs)) {
                    ftrs = "0";
                }
                qdtj.setFtrs(ftrs);
                //注册人数
                String regCount = map.get(platChannel.getChannel() + "zcCount");
                if (QwyUtil.isNullAndEmpty(regCount)) {
                    regCount = "0";
                }
                qdtj.setRegCount(regCount);
                //首投金额
                String stje = map.get(platChannel.getChannel() + "stCopies");
                if (QwyUtil.isNullAndEmpty(stje)) {
                    stje = "0";
                }
                qdtj.setStje(QwyUtil.calcNumber(stje, "100", "/", 2).toString());
                //首投人数
                String strs = map.get(platChannel.getChannel() + "stCount");
                if (QwyUtil.isNullAndEmpty(strs)) {
                    strs = "0";
                }
                qdtj.setStrs(strs);
                //投资金额
                String tzje = map.get(platChannel.getChannel() + "tzCopies");
                if (QwyUtil.isNullAndEmpty(tzje)) {
                    tzje = "0";
                }
                qdtj.setTzje(QwyUtil.calcNumber(tzje, "100", "/", 2).toString());
                //投资人数
                String tzrs = map.get(platChannel.getChannel() + "tzCount");
                if (QwyUtil.isNullAndEmpty(tzrs)) {
                    tzrs = "0";
                }
                qdtj.setTzrs(tzrs);

                if (qdtj.getBindCount().equals("0") || qdtj.getActivityCount().equals("0")) {
                    qdtj.setQdzhl("0.00");
                } else {
                    String zhl = QwyUtil.calcNumber(qdtj.getBindCount(), qdtj.getActivityCount(), "/").toString();
                    qdtj.setQdzhl(QwyUtil.calcNumber(zhl, 0.01, "/", 2) + "");
                }
                if (qdtj.getFtrs().equals("0") || qdtj.getStrs().equals("0")) {
                    qdtj.setCftzl("0.00");
                } else {
                    String zhl = QwyUtil.calcNumber(qdtj.getFtrs(), qdtj.getStrs(), "/").toString();
                    qdtj.setCftzl(QwyUtil.calcNumber(zhl, 0.01, "/", 2) + "");
                }
                //渠道编码
                qdtj.setChannelCode(platChannel.getChannelCode());
                list.add(qdtj);
            }
        }
        return list;
    }

    /**
     * 注册人数
     *
     * @param insertTime 注册时间
     * @throws Exception
     */
    public String regUserCount(String insertTime) throws Exception {
        ArrayList<Object> list = new ArrayList<Object>();
        StringBuffer buffer = new StringBuffer();
//		buffer.append("  SELECT  COUNT(DISTINCT us.id) FROM  users us ");
//		buffer.append("  WHERE us.regist_platform = '1' ");
//		buffer.append("  AND us.regist_channel != '' ");
//		buffer.append("  AND us.regist_channel IS NOT NULL ");
//		if(!QwyUtil.isNullAndEmpty(insertTime)){
//			String [] time=QwyUtil.splitTime(insertTime);
//			if(time.length>1){
//				buffer.append(" AND us.insert_time>= ? ");
//				list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
//				buffer.append(" AND us.insert_time<= ? ");
//				list.add(QwyUtil.fmMMddyyyy.parse(time[1]));
//			}else{
//				buffer.append(" AND us.insert_time>= ? ");
//				list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 00:00:00"));
//				buffer.append(" AND us.insert_time<= ? ");
//				list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 23:59:59"));
//			}
//		}
        buffer.append(regUserCountSQL(insertTime, list));
        Object object = dao.getSqlCount(buffer.toString(), list.toArray());
        if (!QwyUtil.isNullAndEmpty(object)) {
            return object + "";
        }
        return "0";
    }

    /**
     * 投资人数
     *
     * @param insertTime 投资时间
     * @throws Exception
     */
    public String insUsersCount(String insertTime) throws Exception {
        ArrayList<Object> list = new ArrayList<Object>();
        StringBuffer buffer = new StringBuffer();
//		buffer.append(" SELECT COUNT(DISTINCT users_id) FROM investors ivs  ");
//		buffer.append(" AND EXISTS ( ");
//		buffer.append(" SELECT id FROM  users  us ");
//		buffer.append("  WHERE us.regist_platform = '1' ");
//		buffer.append("  AND us.regist_channel != '' ");
//		buffer.append("  AND us.regist_channel IS NOT NULL ");
//		buffer.append("  AND us.id=ivs.users_id ");
//		buffer.append("  ) ");
//		if(!QwyUtil.isNullAndEmpty(insertTime)){
//			String [] time=QwyUtil.splitTime(insertTime);
//			if(time.length>1){
//				buffer.append(" AND ivs.pay_time>= ? ");
//				list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
//				buffer.append(" AND ivs.pay_time<= ? ");
//				list.add(QwyUtil.fmMMddyyyy.parse(time[1]));
//			}else{
//				buffer.append(" AND ivs.pay_time>= ? ");
//				list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 00:00:00"));
//				buffer.append(" AND ivs.pay_time<= ? ");
//				list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 23:59:59"));
//			}
//		}
        buffer.append(insUsersCountSQL(insertTime, list));
        Object object = dao.getSqlCount(buffer.toString(), list.toArray());
        if (!QwyUtil.isNullAndEmpty(object)) {
            return object + "";
        }
        return "0";
    }

    public String insUsersCountSQL(String insertTime, ArrayList<Object> list) throws Exception {
        StringBuffer buffer = new StringBuffer();
        buffer.append(" SELECT COUNT(DISTINCT users_id) FROM investors ivs  ");
        buffer.append(" WHERE ivs.investor_status in ('1','2','3') ");
        buffer.append(" AND EXISTS ( ");
        buffer.append(" SELECT id FROM  users  us ");
        buffer.append("  WHERE us.regist_platform = '1' ");
        buffer.append("  AND us.regist_channel != '' ");
        buffer.append("  AND us.regist_channel IS NOT NULL ");
        buffer.append("  AND us.id=ivs.users_id ");
        buffer.append("  ) ");
        if (!QwyUtil.isNullAndEmpty(insertTime)) {
            String[] time = QwyUtil.splitTime(insertTime);
            if (time.length > 1) {
                buffer.append(" AND DATE_FORMAT(ivs.pay_time, '%Y-%m-%d' )>= ? ");
                list.add(QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyy.parse(time[0])));
                buffer.append(" AND DATE_FORMAT(ivs.pay_time, '%Y-%m-%d' )<= ? ");
                list.add(QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59")));
            } else {
                buffer.append(" AND DATE_FORMAT(ivs.pay_time, '%Y-%m-%d' )>= ? ");
                list.add(QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00")));
                buffer.append(" AND DATE_FORMAT(ivs.pay_time, '%Y-%m-%d' )<= ? ");
                list.add(QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59")));
            }
        }
        return buffer.toString();
    }

    /**
     * 合计
     */
    public Qdtj tjQdtj(List<Qdtj> qdtjs) throws Exception {
        Qdtj cr = new Qdtj();

        //激活次数
        if (QwyUtil.isNullAndEmpty(cr.getActivityCount()))
            cr.setActivityCount("0");

        //注册人数
        if (QwyUtil.isNullAndEmpty(cr.getRegCount()))
            cr.setRegCount("0");

        //绑定人数
        if (QwyUtil.isNullAndEmpty(cr.getBindCount()))
            cr.setBindCount("0");

        //充值金额
        if (QwyUtil.isNullAndEmpty(cr.getTzrs()))
            cr.setTzrs("0");

        //首投人数
        if (QwyUtil.isNullAndEmpty(cr.getStrs()))
            cr.setStrs("0");

        //首投金额
        if (QwyUtil.isNullAndEmpty(cr.getStje()))
            cr.setStje("0");

        //复投人数
        if (QwyUtil.isNullAndEmpty(cr.getFtrs()))
            cr.setFtrs("0");

        //复投金额
        if (QwyUtil.isNullAndEmpty(cr.getFtje()))
            cr.setFtje("0");

        //投资金额(元)()
        if (QwyUtil.isNullAndEmpty(cr.getTzje()))
            cr.setTzje("0");

        //重复投资率(%)
        if (QwyUtil.isNullAndEmpty(cr.getCftzl()))
            cr.setCftzl("0");
        //充值次数
        if (QwyUtil.isNullAndEmpty(cr.getCzcount()))
            cr.setCzcount("0");
        //充值金额(元)
        if (QwyUtil.isNullAndEmpty(cr.getCzje()))
            cr.setCzje("0");
        //人均复投金额(元)
        if (QwyUtil.isNullAndEmpty(cr.getRjftje()))
            cr.setRjftje("0");
        //人均首投金额(元)
        if (QwyUtil.isNullAndEmpty(cr.getRjstje()))
            cr.setRjstje("0");
        //人均投资金额(元)()
        if (QwyUtil.isNullAndEmpty(cr.getRjtzje()))
            cr.setRjtzje("0");
        //投资人数
        if (QwyUtil.isNullAndEmpty(cr.getTzrs()))
            cr.setTzrs("0");
        //投资金额
        if (QwyUtil.isNullAndEmpty(cr.getTzje()))
            cr.setTzje("0");
        //人均投资金额
        if (QwyUtil.isNullAndEmpty(cr.getRjtzje()))
            cr.setRjtzje("0");
        //新增复投用户数
        if (QwyUtil.isNullAndEmpty(cr.getXzftyh()))
            cr.setXzftyh("0");
        //新增复投用户投资总金额
        if (QwyUtil.isNullAndEmpty(cr.getXhftyhtzze()))
            cr.setXhftyhtzze("0");
        //零钱罐金额
        if (QwyUtil.isNullAndEmpty(cr.getLqgje()))
            cr.setLqgje("0");
        //渠道金额
        if (QwyUtil.isNullAndEmpty(cr.getChannelCost()))
            cr.setChannelCost("0");
        //激活成本
        if (QwyUtil.isNullAndEmpty(cr.getActivityCost()))
            cr.setActivityCost("0");
        //注册成本
        if (QwyUtil.isNullAndEmpty(cr.getRegisterCost()))
            cr.setRegisterCost("0");
        //首投成本
        if (QwyUtil.isNullAndEmpty(cr.getFristBuyCost()))
            cr.setFristBuyCost("0");
        //首投ROI
        if (QwyUtil.isNullAndEmpty(cr.getFristBuyROI()))
            cr.setFristBuyROI("0");
        //购买ROI
        if (QwyUtil.isNullAndEmpty(cr.getBuyROI()))
            cr.setBuyROI("0");
        if (!QwyUtil.isNullAndEmpty(qdtjs)) {
            for (Qdtj qdtj : qdtjs) {
                if (!QwyUtil.isNullAndEmpty(qdtj))
                    cr.setLqgje(QwyUtil.calcNumber(qdtj.getLqgje(), cr.getLqgje(), "+") + "");
                //新增复投用户数
                cr.setXzftyh(QwyUtil.calcNumber(qdtj.getXzftyh(), cr.getXzftyh(), "+") + "");
                //新增复投总金额
                cr.setXhftyhtzze(QwyUtil.calcNumber(qdtj.getXhftyhtzze(), cr.getXhftyhtzze(), "+") + "");
                //激活用户
                cr.setActivityCount(QwyUtil.calcNumber(qdtj.getActivityCount(), cr.getActivityCount(), "+") + "");
                //注册人数
                cr.setRegCount(QwyUtil.calcNumber(qdtj.getRegCount(), cr.getRegCount(), "+") + "");
                //绑定人数
                cr.setBindCount(QwyUtil.calcNumber(qdtj.getBindCount(), cr.getBindCount(), "+") + "");
                //投资人数
                cr.setTzrs(QwyUtil.calcNumber(qdtj.getTzrs(), cr.getTzrs(), "+") + "");
                //首投人数
                cr.setStrs(QwyUtil.calcNumber(qdtj.getStrs(), cr.getStrs(), "+") + "");
                //首投金额
                cr.setStje(QwyUtil.calcNumber(qdtj.getStje(), cr.getStje(), "+") + "");
                //复投人数
                cr.setFtrs(QwyUtil.calcNumber(qdtj.getFtrs(), cr.getFtrs(), "+") + "");
                //复投金额
                cr.setFtje(QwyUtil.calcNumber(qdtj.getFtje(), cr.getFtje(), "+") + "");
                //投资金额
                cr.setTzje(QwyUtil.calcNumber(qdtj.getTzje(), cr.getTzje(), "+") + "");
                //充值次数
                cr.setCzcount(QwyUtil.calcNumber(qdtj.getCzcount(), cr.getCzcount(), "+") + "");
                //充值金额
                cr.setCzje(QwyUtil.calcNumber(qdtj.getCzje(), cr.getCzje(), "+") + "");
                //人均首投金额
                cr.setRjstje(QwyUtil.calcNumber(qdtj.getRjstje(), cr.getRjstje(), "+") + "");
                //人均复投金额
                cr.setRjftje(QwyUtil.calcNumber(qdtj.getRjftje(), cr.getRjftje(), "+") + "");
                //人均投资金额
                cr.setRjtzje(QwyUtil.calcNumber(qdtj.getRjtzje(), cr.getRjtzje(), "+") + "");
                //渠道金额
                cr.setChannelCost(QwyUtil.calcNumber(qdtj.getChannelCost(), cr.getChannelCost(), "+") + "");
                //激活成本
                cr.setActivityCost(QwyUtil.calcNumber(qdtj.getActivityCost(), cr.getActivityCost(), "+") + "");
                //注册成本
                cr.setRegisterCost(QwyUtil.calcNumber(qdtj.getRegisterCost(), cr.getRegisterCost(), "+") + "");
                //首投成本
                cr.setFristBuyCost(QwyUtil.calcNumber(qdtj.getFristBuyCost(), cr.getFristBuyCost(), "+") + "");
                //首投ROI
                cr.setFristBuyROI(QwyUtil.calcNumber(qdtj.getFristBuyROI(), cr.getFristBuyROI(), "+") + "");
                //投资ROI
                cr.setBuyROI(QwyUtil.calcNumber(qdtj.getBuyROI(), cr.getBuyROI(), "+") + "");
            }

            //渠道转化率(%)
        }
        if (cr.getBindCount().equals("0") || cr.getActivityCount().equals("0")) {
            cr.setQdzhl("0.00");
        } else {
            String zhl = QwyUtil.calcNumber(cr.getRegCount(), cr.getActivityCount(), "/").toString();
            cr.setQdzhl(QwyUtil.calcNumber(zhl, 0.01, "/", 2) + "");
        }

        //重复投资率
        if (cr.getFtrs().equals("0") || cr.getStrs().equals("0")) {
            cr.setCftzl("0.00");
        } else {
            String zhl = QwyUtil.calcNumber(cr.getFtrs(), cr.getTzrs(), "/").toString();
            cr.setCftzl(QwyUtil.calcNumber(zhl, 0.01, "/", 2) + "");
        }
        return cr;
    }

    /**
     * 合计
     * @param qdtjs
     * @param insertTime  时间
     * @return
     * @throws Exception
     */
//	public Qdtj tjQdtj(String insertTime) throws Exception{
//		Qdtj or=new Qdtj();
//			//激活用户
//			or.setActivityCount(bean.activityCount(insertTime));
//			//注册人数
//			or.setRegCount(regUserCount(insertTime));
//			//绑定人数
//			or.setBindCount(bindUserCount(insertTime));
//			//投资人数
//			or.setTzrs(insUsersCount(insertTime));
//			//首投人数
//			or.setStrs(firstInsUsersCount(insertTime));
//			//首投金额
//			or.setStje(QwyUtil.calcNumber(firstInsMoney(insertTime), 100, "/", 2)+"");
//			//复投人数
//			or.setFtrs(againInsUsersCount(insertTime));
//			//投资金额
//			or.setTzje(QwyUtil.calcNumber(insMoney(insertTime), 100, "/", 2)+"");
//			//充值次数
//			or.setCzcount(czCount(insertTime));
//			//充值金额
//			or.setCzje(QwyUtil.calcNumber(czMoney(insertTime), 100, "/", 2)+"");
//			//渠道转换率
//			if(or.getBindCount().equals("0")||or.getActivityCount().equals("0")){
//				or.setQdzhl("0.00");
//			}else{
//				String zhl=QwyUtil.calcNumber(or.getBindCount(), or.getActivityCount(), "/").toString();
//				or.setQdzhl(QwyUtil.calcNumber(zhl, 0.01, "/", 2)+"");
//			}
//			//重复投资率
//			if(or.getFtrs().equals("0")||or.getStrs().equals("0")){
//				or.setCftzl("0.00");
//			}else{
//				String zhl=QwyUtil.calcNumber(or.getFtrs(), or.getStrs(), "/").toString();
//				or.setCftzl(QwyUtil.calcNumber(zhl, 0.01, "/", 2)+"");
//			}
//		return or;
//	}

    /**
     * 首投人数
     *
     * @param insertTime 投资时间
     * @throws Exception
     */
    public String firstInsUsersCount(String insertTime) throws Exception {
        ArrayList<Object> list = new ArrayList<Object>();
        StringBuffer buffer = new StringBuffer();
//		buffer.append(" SELECT COUNT(DISTINCT ivs.users_id) FROM investors ivs  ");
//		buffer.append(" WHERE ivs.investor_status IN ('1','2','3') ");
//		buffer.append(" AND ivs.pay_time in ( ");
//		buffer.append("  SELECT MIN(ins.pay_time) as date FROM investors ins  WHERE ins.investor_status in ('1','2','3') GROUP BY users_id ) ");
//		buffer.append(" AND EXISTS ( ");
//		buffer.append(" SELECT id FROM  users  us ");
//		buffer.append("  WHERE us.regist_platform = '1' ");
//		buffer.append("  AND us.regist_channel != '' ");
//		buffer.append("  AND us.regist_channel IS NOT NULL ");
//		buffer.append("  AND us.id=ivs.users_id ");
//		buffer.append("  ) ");
//		if(!QwyUtil.isNullAndEmpty(insertTime)){
//			String [] time=QwyUtil.splitTime(insertTime);
//			if(time.length>1){
//				buffer.append(" AND ivs.pay_time>= ? ");
//				list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
//				buffer.append(" AND ivs.pay_time<= ? ");
//				list.add(QwyUtil.fmMMddyyyy.parse(time[1]));
//			}else{
//				buffer.append(" AND ivs.pay_time>= ? ");
//				list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 00:00:00"));
//				buffer.append(" AND ivs.pay_time<= ? ");
//				list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 23:59:59"));
//			}
//		}
        //buffer.append(firstInsUsersCountSQL(insertTime, list));
        buffer.append(" SELECT  sum(t.strs) FROM ( ");
        buffer.append(" SELECT DATE_FORMAT( dd.insert_time, '%Y-%m-%d' ) as date,  ");
        buffer.append("  q.strs , ");
        buffer.append("  q.stje");
        buffer.append(" FROM dateday dd  ");
        //首投人数，首投金额
        buffer.append(" LEFT JOIN (  ");
        buffer.append(" SELECT DATE_FORMAT(shoutouDate, '%Y-%m-%d') as date,COUNT(*) AS strs,SUM(in_money) as stje FROM ( ");
        buffer.append(" SELECT DATE_FORMAT(investors1.pay_time,'%Y-%m-%d') AS shoutouDate,investors1.users_id ,investors1.in_money FROM investors investors1 ");
        buffer.append(" INNER JOIN ( ");
        buffer.append(" SELECT MIN(t.pay_time) as minDate,users_id FROM investors t WHERE t.investor_status IN ('1','2','3') GROUP BY t.users_id ");
        buffer.append(" ) investors2 ON investors1.pay_time=investors2.minDate AND investors1.users_id = investors2.users_id  ");
        buffer.append(" INNER JOIN users u ON u.id=investors1.users_id   ");
        buffer.append(" AND u.regist_platform = '1'  AND u.regist_channel != ''  AND u.regist_channel IS NOT NULL ");
        buffer.append(" WHERE DATE_FORMAT(investors1.pay_time,'%Y-%m-%d')=DATE_FORMAT(investors2.minDate,'%Y-%m-%d') GROUP BY investors1.users_id ");
        buffer.append(" ) tab4 GROUP BY shoutouDate ");
        buffer.append(" ) q ON q.date = DATE_FORMAT(dd.insert_time, '%Y-%m-%d')  ");
        if (!QwyUtil.isNullAndEmpty(insertTime)) {
            String[] time = QwyUtil.splitTime(insertTime);
            if (time.length > 1) {
                buffer.append(" WHERE dd.insert_time>= ? ");
                list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
                buffer.append(" AND dd.insert_time< ? ");
                list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
            } else {
                buffer.append(" WHERE dd.insert_time>= ? ");
                list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                buffer.append(" AND dd.insert_time<= ? ");
                list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
            }
        }
        buffer.append("  GROUP BY DATE_FORMAT(dd.insert_time, '%Y-%m-%d' )");
        buffer.append("  ORDER BY DATE_FORMAT(dd.insert_time, '%Y-%m-%d' ) DESC");
        buffer.append(" )t ");
        Object object = dao.getSqlCount(buffer.toString(), list.toArray());
        if (!QwyUtil.isNullAndEmpty(object)) {
            return object + "";
        }
        return "0";
    }

    /**
     * 复投人数
     *
     * @param insertTime 投资时间
     * @throws Exception
     */
    public String againInsUsersCount(String insertTime) throws Exception {
        ArrayList<Object> list = new ArrayList<Object>();
        StringBuffer buffer = new StringBuffer();
        buffer.append("  SELECT COUNT(DISTINCT ivs.users_id) FROM investors ivs ");
        buffer.append(" WHERE ivs.investor_status  in ('1','2','3') ");
//		buffer.append(" AND ivs.pay_time not in ( ");
//		buffer.append(" SELECT MIN(ins.pay_time) as date FROM investors ins  WHERE ins.investor_status in ('1','2','3') GROUP BY users_id ) ");
        buffer.append(" AND  NOT EXISTS ( SELECT t.id FROM  ( SELECT MIN(ins.pay_time) as date,id FROM investors ins  WHERE ins.investor_status in ('1','2','3') GROUP BY users_id )t WHERE t.id=ivs.id) ");
        if (!QwyUtil.isNullAndEmpty(insertTime)) {
            String[] time = QwyUtil.splitTime(insertTime);
            if (time.length > 1) {
                buffer.append(" AND DATE_FORMAT(ivs.pay_time, '%Y-%m-%d' )>= ? ");
                list.add(QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyy.parse(time[0])));
                buffer.append(" AND DATE_FORMAT(ivs.pay_time, '%Y-%m-%d' )< ? ");
                list.add(QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyy.parse(time[1])));
            } else {
                buffer.append(" AND DATE_FORMAT(ivs.pay_time, '%Y-%m-%d' )>= ? ");
                list.add(QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00")));
                buffer.append(" AND DATE_FORMAT(ivs.pay_time, '%Y-%m-%d' )<= ? ");
                list.add(QwyUtil.fmyyyyMMdd.format(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59")));
            }
        }
        buffer.append(" AND EXISTS ( ");
        buffer.append(" SELECT id FROM  users  us ");
        buffer.append("  WHERE us.regist_platform = '1' ");
        buffer.append("  AND us.regist_channel != '' ");
        buffer.append("  AND us.regist_channel IS NOT NULL ");
        buffer.append("  AND us.id=ivs.users_id ");
        buffer.append("  ) ");

        Object object = dao.getSqlCount(buffer.toString(), list.toArray());
        if (!QwyUtil.isNullAndEmpty(object)) {
            return object + "";
        }
        return "0";
    }

    @SuppressWarnings("unchecked")
    public PageUtil<ChannelOperateDay> getQdtjDetail(PageUtil<ChannelOperateDay> pageUtil, String registChannel, String channelCode, String insertTime) throws Exception {
        List<Object> list = new ArrayList<Object>();
        StringBuffer hql = new StringBuffer();
        hql.append(" FROM ChannelOperateDay c WHERE 1 = 1 ");

        if (!QwyUtil.isNullAndEmpty(registChannel)) {
            hql.append(" AND c.registChannel = ? ");
            list.add(Integer.valueOf(registChannel));
        }
        if (!QwyUtil.isNullAndEmpty(channelCode)) {
            hql.append(" AND c.channelCode = ? ");
            list.add(channelCode);
        }

        // 插入时间
        if (!QwyUtil.isNullAndEmpty(insertTime)) {
            String[] time = QwyUtil.splitTime(insertTime);
            if (time.length > 1) {
                hql.append(" AND c.date >= ? ");
                list.add(QwyUtil.fmMMddyyyy.parse(time[0] + " 00:00:00"));
                hql.append(" AND c.date <= ? ");
                list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
            } else {
                hql.append(" AND c.date >= ? ");
                list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                hql.append(" AND c.date <= ? ");
                list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
            }
        }

        hql.append(" ORDER BY c.date DESC ");
        return dao.getByHqlAndHqlCount(pageUtil, hql.toString(), hql.toString(), list.toArray());
    }

    /**
     * 查询渠道
     *
     * @param channel
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public PlatChannel loadPlatChannel(String channel, String channelCode) throws Exception {
        PlatChannel platChannel = null;
        StringBuffer buff = new StringBuffer();
        buff.append(" FROM PlatChannel where 1=1 ");
        if (!QwyUtil.isNullAndEmpty(channel) && QwyUtil.isOnlyNumber(channel)) {
            buff.append(" AND channel=?");
            if (!QwyUtil.isNullAndEmpty(channelCode)) {
                buff.append(" AND channelCode=?");
            }
            Object[] ob = new Object[]{Integer.valueOf(channel), channelCode};
            List<PlatChannel> list = dao.LoadAll(buff.toString(), ob);
            if (!QwyUtil.isNullAndEmpty(list)) {
                if (list.size() > 0) {
                    platChannel = list.get(0);
                }
            }
        }

        return platChannel;
    }


    /**
     * Android渠道统计汇总记录
     *
     * @param insertTime
     * @return
     * @throws Exception modified by yks on 2016-11-28
     */
    public List<Qdtj> getQdtj1(String insertTime) throws Exception {
        List<Qdtj> list = new ArrayList<>();
        List<Object> param = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT cod.registChannel, pc.channel_name, SUM(cod.activateUserSum),SUM(cod.regUserSum),SUM(cod.bindBankUserSum)," +
                " IF ( SUM(cod.activateUserSum) = 0, 0, SUM(cod.bindBankUserSum) / SUM(cod.activateUserSum)) AS qdzhl, SUM(investUserSum)," +
                " SUM(firstInvestUserSum),SUM(firstInvestCentSum), SUM(reInvestUserSum), SUM(investCentSum), IF ( " +
                " SUM(firstInvestUserSum) = 0,0,SUM(reInvestUserSum) / SUM(firstInvestUserSum)) AS cftzl, SUM(rechargeCount)," +
                " SUM(rechargeCentSum),pc.channel_code FROM channel_operate_day cod LEFT JOIN plat_channel pc ON cod.registChannel = pc.channel AND cod.channelCode = pc.channel_code " +
                " WHERE 1 = 1 GROUP BY cod.registChannel ");
        if (!QwyUtil.isNullAndEmpty(insertTime)) {
            String[] time = QwyUtil.splitTime(insertTime);
            if (time.length > 1) {
                sb.append(" AND cod.date >= ? ");
                param.add(QwyUtil.fmMMddyyyy.parse(time[0]));
                sb.append(" AND cod.date <= ? ");
                param.add(QwyUtil.fmMMddyyyy.parse(time[1]));
            } else {
                sb.append(" AND cod.date >= ? ");
                param.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                sb.append(" AND cod.date <= ? ");
                param.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
            }
        }
        sb.append(" ORDER BY cod.registChannel ");
        list = toQdtjLsit(dao.LoadAllSql(sb.toString(), param.toArray()));
        return list;
    }

    private List<Qdtj> toQdtjLsit(List<Object[]> list) throws Exception {
        List<Qdtj> results = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Qdtj qdtj = new Qdtj();
            qdtj.setIndex((i + 1) + "");
            qdtj.setChannel(!QwyUtil.isNullAndEmpty(list.get(i)[0]) ? list.get(i)[0].toString() : "");
            qdtj.setChannelName(!QwyUtil.isNullAndEmpty(list.get(i)[1]) ? list.get(i)[1].toString() : "");
            qdtj.setActivityCount(!QwyUtil.isNullAndEmpty(list.get(i)[2]) ? list.get(i)[2].toString() : "0");
            qdtj.setRegCount(!QwyUtil.isNullAndEmpty(list.get(i)[3]) ? list.get(i)[3].toString() : "0");
            qdtj.setBindCount(!QwyUtil.isNullAndEmpty(list.get(i)[4]) ? list.get(i)[4].toString() : "0");
            qdtj.setQdzhl(!QwyUtil.isNullAndEmpty(list.get(i)[5]) ? QwyUtil.calcNumber(list.get(i)[5], 0.01, "/", 2).doubleValue() + "" : "0");
            qdtj.setTzrs(!QwyUtil.isNullAndEmpty(list.get(i)[6]) ? list.get(i)[6].toString() : "0");
            qdtj.setStrs(!QwyUtil.isNullAndEmpty(list.get(i)[7]) ? list.get(i)[7].toString() : "0");
            qdtj.setStje(!QwyUtil.isNullAndEmpty(list.get(i)[8]) ? QwyUtil.calcNumber(list.get(i)[8], 0.01, "*", 2).doubleValue() + "" : "0");
            qdtj.setFtrs(!QwyUtil.isNullAndEmpty(list.get(i)[9]) ? list.get(i)[9].toString() : "0");
            qdtj.setTzje(!QwyUtil.isNullAndEmpty(list.get(i)[10]) ? QwyUtil.calcNumber(list.get(i)[10], 0.01, "*", 2).doubleValue() + "" : "0");
            qdtj.setCftzl(!QwyUtil.isNullAndEmpty(list.get(i)[11]) ? QwyUtil.calcNumber(list.get(i)[11], 0.01, "/", 2).doubleValue() + "" : "0");
            qdtj.setCzcount(!QwyUtil.isNullAndEmpty(list.get(i)[12]) ? list.get(i)[12].toString() : "0");
            qdtj.setCzje(!QwyUtil.isNullAndEmpty(list.get(i)[13]) ? QwyUtil.calcNumber(list.get(i)[13], 0.01, "*", 2).doubleValue() + "" : "0");
            qdtj.setChannelCode(!QwyUtil.isNullAndEmpty(list.get(i)[14]) ? list.get(i)[14].toString() : "");
            results.add(qdtj);
        }
        return results;
    }


    /**
     * Android渠道统计汇总表 新入口;
     *
     * @param sDate 日期
     * @param eDate
     * @return
     */
    public List<Qdtj> loadQdtjMain(String sDate, String eDate, String channelType) {
        //ChannelOperateDay
        StringBuffer sb = new StringBuffer();
        List list = new ArrayList();
        sb.append("SELECT qd.channel,qd.channelName,qd.channelCode,");
        sb.append("SUM(qd.activityCount), SUM(qd.regCount),SUM(qd.bindCount),SUM(qd.tzrs),SUM(qd.strs),SUM(qd.stje),");
        sb.append("SUM(qd.ftrs), SUM(qd.ftje),SUM(qd.tzje),SUM(qd.czcount),SUM(qd.czje),SUM(qd.xzftyh),SUM(qd.xhftyhtzze), ");
        sb.append("SUM(qd.rzstzhl),SUM(qd.rjstje),SUM(qd.xzftl),SUM(qd.rjftje),SUM(qd.rjtzje),SUM(qd.zcjhzhl),SUM(qd.lqgje),SUM(qd.lqgrs), ");
        sb.append("SUM(qd.channel_cost),SUM(qd.activity_cost),SUM(qd.register_cost),SUM(qd.frist_buy_cost),SUM(qd.frist_buy_ROI),SUM(qd.buy_ROI) ");
        sb.append(" FROM qdtj qd  WHERE 1 = 1 ");
        //查询时间范围;
        if (!QwyUtil.isNullAndEmpty(sDate)) {
            sb.append(" AND qd.date BETWEEN '");
            sb.append(sDate);
            sb.append("' AND '");
            sb.append(eDate);
            sb.append("'");
        }
        if (!QwyUtil.isNullAndEmpty(channelType)) {
            sb.append(" AND qd.channel_type = ? ");
            list.add(channelType);
        }else{
            sb.append(" AND qd.channel_type = 1 ");
        }
        sb.append("GROUP BY qd.channelCode ");
        sb.append(" ORDER BY qd.channel+0 ASC ");
        Log.info(sb.toString());
        List<Object[]> listObject = dao.LoadAllSql(sb.toString(), list.toArray());
        return (List<Qdtj>) parseToQdtj(listObject);
    }


    /**
     * 把统计好的数据;填充到对象;方便页面取值
     *
     * @param list
     * @return
     */
    public List<Qdtj> parseToQdtj(List<Object[]> list) {
        List<Qdtj> qdtjLists = new ArrayList<Qdtj>();
        if (!QwyUtil.isNullAndEmpty(list)) {
            for (int i = 0; i < list.size(); i++) {
                try {
                    Object[] object = list.get(i);
                    Qdtj qdtj = new Qdtj();
                    qdtj.setIndex(i + 1 + "");//序号
                    qdtj.setChannel(isNullReturnZero(object[0]));//渠道号
                    qdtj.setChannelName(isNullReturnZero(object[1]));//渠道名称
                    qdtj.setChannelCode(isNullReturnZero(object[2]));
                    qdtj.setActivityCount(isNullReturnZero(object[3]));//激活人数
                    qdtj.setRegCount(isNullReturnZero(object[4]));//注册人数
                    qdtj.setBindCount(isNullReturnZero(object[5]));//绑卡人数
                    qdtj.setTzrs(isNullReturnZero(object[6]));//投资人数
                    qdtj.setStrs(isNullReturnZero(object[7]));//首投人数
                    qdtj.setStje(isNullReturnZero(Double.parseDouble(isNullReturnZero((object[8]))) * 0.01));//首投金额
                    qdtj.setFtrs(isNullReturnZero(object[9]));//复投人数
                    qdtj.setFtje(isNullReturnZero(Double.parseDouble(isNullReturnZero((object[10]))) * 0.01));//复投金额
                    qdtj.setTzje(isNullReturnZero(Double.parseDouble(isNullReturnZero((object[11]))) * 0.01));//投资金额
                    qdtj.setCzcount(isNullReturnZero((object[12])));//充值次数
                    qdtj.setCzje(isNullReturnZero((Double.parseDouble(isNullReturnZero((object[13]))) * 0.01)));//充值金额
                    String cftzlStr = isNullReturnZero((object[6]));
                    String cftzlStrNew = "0.0".equals(cftzlStr) ? "0.0" : QwyUtil.calcNumber(isNullReturnZero((object[9])), cftzlStr, "/").toString();
                    qdtj.setCftzl(QwyUtil.calcNumber(cftzlStrNew, 100, "*").toString());//重复投资率
                    String qdzhlStr = isNullReturnZero((object[3]));
                    String qdzhlStrNew = "0.0".equals(qdzhlStr) ? "0.0" : QwyUtil.calcNumber(isNullReturnZero((object[4])), qdzhlStr, "/").toString();
                    qdtj.setQdzhl(QwyUtil.calcNumber(qdzhlStrNew, 100, "*").toString());//注册认证转换率
                    qdtj.setXzftyh(object[14] + "");//新增复投用户
                    qdtj.setXhftyhtzze(isNullReturnZero(Double.parseDouble(isNullReturnZero((object[15]))) * 0.01));//复投总金额
                    String rzstzhlStr = isNullReturnZero((object[5]));
                    String rzstzhlStrNew = "0.0".equals(rzstzhlStr) ? "0.0" : QwyUtil.calcNumber(isNullReturnZero((object[7])), rzstzhlStr, "/").toString();
                    qdtj.setRzstzhl(QwyUtil.calcNumber(rzstzhlStrNew, 100, "*").toString());//认证首投转换率()
                    qdtj.setRjstje("0.0".equals(object[17] + "") ? "0.0" : isNullReturnZeroDouble(Double.parseDouble(isNullReturnZeroDouble((object[17] + ""))) * 0.01));//人均首投金额
                    String xzzczhlStr = isNullReturnZero((object[9]));
                    String xzzczhlStrNew = "0.0".equals(xzzczhlStr) ? "0.0" : QwyUtil.calcNumber(isNullReturnZero((object[14])), xzzczhlStr, "/").toString();
                    qdtj.setXzftl(QwyUtil.calcNumber(xzzczhlStrNew, 100, "*").toString());//新增复投率
                    qdtj.setRjftje(isNullReturnZero(Double.parseDouble(isNullReturnZero((object[19]))) * 0.01));//人均复投金额
                    String tzje = qdtj.getTzje();//投资金额
                    String tzrs = qdtj.getTzrs();//投资人数
                    String rjtzje = "0.0".equals(tzrs) ? "0.0" : QwyUtil.calcNumber(tzje, tzrs, "/").toString();
                    qdtj.setRjtzje(rjtzje);//人均投资金额
                    String zcrzzhlStr = isNullReturnZero((object[4]));
                    String zcrzzhlStrNew = "0.0".equals(zcrzzhlStr) ? "0.0" : QwyUtil.calcNumber(isNullReturnZero((object[5])), zcrzzhlStr, "/").toString();
                    qdtj.setZcjhzhl(QwyUtil.calcNumber(zcrzzhlStrNew, 100, "*").toString());//注册认证转率
                    qdtj.setLqgje(isNullReturnZero(Double.parseDouble(isNullReturnZero((object[22]))) * 0.01));//零钱罐金额
                    qdtj.setLqgrs(isNullReturnZero(Double.parseDouble(isNullReturnZero((object[23])))));//零钱罐人数
                    qdtj.setChannelCost(isNullReturnZero(Double.parseDouble(isNullReturnZero((object[24])))));//渠道费用
                    qdtj.setActivityCost(isNullReturnZero(Double.parseDouble(isNullReturnZero((object[25])))));//激活成本
                    qdtj.setRegisterCost(isNullReturnZero(Double.parseDouble(isNullReturnZero((object[26])))));//注册成本
                    qdtj.setFristBuyCost(isNullReturnZero(Double.parseDouble(isNullReturnZero((object[27])))));//首投成本
                    qdtj.setFristBuyROI(isNullReturnZero(Double.parseDouble(isNullReturnZero((object[28])))));//首投ROI
                    qdtj.setBuyROI(isNullReturnZero(Double.parseDouble(isNullReturnZero((object[29])))));//投资ROI
                    qdtjLists.add(qdtj);
                } catch (Exception e) {
                    log.error("操作异常: ", e);
                }
            }
        }

        return qdtjLists;
    }


    /**
     * 参数空时，返回"0"
     *
     * @param str
     * @return
     */
    public String isNullReturnZero(Object str) {
        return QwyUtil.isNullAndEmpty(str) ? "0" : str.toString();
    }

    /**
     * 参数空时，返回"0.0"
     *
     * @param str
     * @return
     */
    public String isNullReturnZeroDouble(Object str) {
        return QwyUtil.isNullAndEmpty(str) ? "0.0" : str.toString();
    }

    /**
     * Android单个渠道统计详情表 新入口;
     *
     * @param channelCode
     * @param channel
     * @return
     */
    public PageUtil<Qdtj> loadQdtjDetailByChannel(PageUtil pageUtil, String channelCode, String channel, String date) throws Exception {
        StringBuffer sql = new StringBuffer();
        List<Object> param = new ArrayList<>();
        sql.append(" SELECT a.channel,a.channelName, a.channelCode,a.activityCount , a.regCount ,a.bindCount,a.qdzhl,");
        sql.append(" a.tzrs, a.strs,a.stje,a.ftrs,a.ftje,a.tzje,a.cftzl,a.czcount,a.czje,a.date, ");
        sql.append(" a.xzftyh,a.xhftyhtzze,a.rzstzhl,a.rjstje,a.xzftl,a.rjftje,a.rjtzje,a.zcjhzhl,a.lqgje,a.lqgrs, ");
        sql.append(" a.channel_cost,a.activity_cost,a.register_cost,a.frist_buy_cost,a.frist_buy_ROI,a.buy_ROI ");
        sql.append(" FROM qdtj a WHERE 1=1 ");
        if (!QwyUtil.isNullAndEmpty(channelCode)) {
            sql.append(" AND a.channelCode = ? ");
            param.add(channelCode);
        }
        if (!QwyUtil.isNullAndEmpty(channel)) {
            sql.append("AND a.channel = ? ");
            param.add(channel);
        }
        if (!QwyUtil.isNullAndEmpty(date)) {
            String[] time = QwyUtil.splitTime(date);
            if (time.length > 1) {
                sql.append(" AND a.date >= ? ");
                param.add(QwyUtil.fmMMddyyyy.parse(time[0]));
                sql.append(" AND a.date <= ? ");
                param.add(QwyUtil.fmMMddyyyy.parse(time[1]));
            } else {
                sql.append(" AND a.date >= ? ");
                param.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                sql.append(" AND a.date <= ? ");
                param.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
            }
        }
        sql.append(" GROUP BY a.date ");
        sql.append(" ORDER BY a.date DESC ");
        StringBuffer sqlCount = new StringBuffer();
        sqlCount.append(" SELECT COUNT(*)  ");
        sqlCount.append(" FROM (");
        sqlCount.append(sql);
        sqlCount.append(") t");
        pageUtil = dao.getBySqlAndSqlCount(pageUtil, sql.toString(), sqlCount.toString(), param.toArray());
        //List<Object[]> listObject = (List<Object[]>)dao.LoadAllSql(sql.toString(),param.toArray());
        List<Qdtj> list = toyksQdtjLsit(pageUtil.getList());
        pageUtil.setList(list);
        return pageUtil;
    }

    private List<Qdtj> toyksQdtjLsit(List<Object[]> list) throws Exception {
        List<Qdtj> results = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Qdtj qdtj = new Qdtj();
            qdtj.setIndex((i + 1) + "");
            qdtj.setChannel(!QwyUtil.isNullAndEmpty(list.get(i)[0]) ? list.get(i)[0].toString() : "");
            qdtj.setChannelName(!QwyUtil.isNullAndEmpty(list.get(i)[1]) ? list.get(i)[1].toString() : "");
            qdtj.setChannelCode(!QwyUtil.isNullAndEmpty(list.get(i)[2]) ? list.get(i)[2].toString() : "");
            qdtj.setActivityCount(!QwyUtil.isNullAndEmpty(list.get(i)[3]) ? list.get(i)[3].toString() : "0");
            qdtj.setRegCount(!QwyUtil.isNullAndEmpty(list.get(i)[4]) ? list.get(i)[4].toString() : "0");
            qdtj.setBindCount(!QwyUtil.isNullAndEmpty(list.get(i)[5]) ? list.get(i)[5].toString() : "0");
            qdtj.setQdzhl(!QwyUtil.isNullAndEmpty(list.get(i)[6]) ? list.get(i)[6].toString() : "0");
            qdtj.setTzrs(!QwyUtil.isNullAndEmpty(list.get(i)[7]) ? list.get(i)[7].toString() : "0");
            qdtj.setStrs(!QwyUtil.isNullAndEmpty(list.get(i)[8]) ? list.get(i)[8].toString() : "0");
            qdtj.setStje(isNullReturnZero(Double.parseDouble(isNullReturnZero((list.get(i)[9]))) * 0.01));
            qdtj.setFtrs(!QwyUtil.isNullAndEmpty(list.get(i)[10]) ? list.get(i)[10].toString() : "0");
            qdtj.setFtje(!QwyUtil.isNullAndEmpty(list.get(i)[11]) ? QwyUtil.calcNumber(list.get(i)[11], 0.01, "*", 2).doubleValue() + "" : "0");
            qdtj.setTzje(!QwyUtil.isNullAndEmpty(list.get(i)[12]) ? QwyUtil.calcNumber(list.get(i)[12], 0.01, "*", 2).doubleValue() + "" : "0");
            qdtj.setCftzl(!QwyUtil.isNullAndEmpty(list.get(i)[13]) ? list.get(i)[13].toString() : "0");
            qdtj.setCzcount(!QwyUtil.isNullAndEmpty(list.get(i)[14]) ? list.get(i)[14].toString() : "0");
            qdtj.setCzje(!QwyUtil.isNullAndEmpty(list.get(i)[15]) ? QwyUtil.calcNumber(list.get(i)[15], 0.01, "*", 2).doubleValue() + "" : "0");
            qdtj.setDateStr(!QwyUtil.isNullAndEmpty(list.get(i)[16]) ? QwyUtil.fmyyyyMMdd.format(list.get(i)[16]) : null);
            qdtj.setXzftyh(!QwyUtil.isNullAndEmpty(list.get(i)[17]) ? list.get(i)[17].toString() : "");
            qdtj.setXhftyhtzze(isNullReturnZero(Double.parseDouble(isNullReturnZero((list.get(i)[18]))) * 0.01));
            qdtj.setRzstzhl(!QwyUtil.isNullAndEmpty(list.get(i)[19]) ? list.get(i)[19].toString() : "");
            qdtj.setRjstje(isNullReturnZero(Double.parseDouble(isNullReturnZero((list.get(i)[20]))) * 0.01));
            qdtj.setXzftl(!QwyUtil.isNullAndEmpty(list.get(i)[21]) ? list.get(i)[21].toString() : "");
            qdtj.setRjftje(isNullReturnZero(Double.parseDouble(isNullReturnZero((list.get(i)[22]))) * 0.01));
            qdtj.setRjtzje(isNullReturnZero(Double.parseDouble(isNullReturnZero((list.get(i)[23]))) * 0.01));
            qdtj.setZcjhzhl(!QwyUtil.isNullAndEmpty(list.get(i)[24]) ? list.get(i)[24].toString() : "");
            qdtj.setLqgje(isNullReturnZero(Double.parseDouble(isNullReturnZero((list.get(i)[25]))) * 0.01));
            qdtj.setLqgrs(!QwyUtil.isNullAndEmpty(list.get(i)[26]) ? list.get(i)[26].toString() : "0");
            qdtj.setChannelCost(!QwyUtil.isNullAndEmpty(list.get(i)[27]) ? list.get(i)[27].toString() : "0");//渠道费用
            qdtj.setActivityCost(!QwyUtil.isNullAndEmpty(list.get(i)[28]) ? list.get(i)[28].toString() : "0");//激活成本
            qdtj.setRegisterCost(!QwyUtil.isNullAndEmpty(list.get(i)[29]) ? list.get(i)[29].toString() : "0");//注册成本
            qdtj.setFristBuyCost(!QwyUtil.isNullAndEmpty(list.get(i)[30]) ? list.get(i)[30].toString() : "0");//首投成本
            qdtj.setFristBuyROI(!QwyUtil.isNullAndEmpty(list.get(i)[31]) ? list.get(i)[31].toString() : "0");//首投ROI
            qdtj.setBuyROI(!QwyUtil.isNullAndEmpty(list.get(i)[32]) ? list.get(i)[32].toString() : "0");//投资ROI
            results.add(qdtj);
        }
        return results;
    }

    /**
     * 分页查询渠道信息
     *
     * @param pageUtil
     * @param Date
     * @param channelType
     * @return
     */

    public PageUtil<Qdtj> loadQdtj(PageUtil<Qdtj> pageUtil, String Date, String channelType,String channelName) {
        StringBuffer sb = new StringBuffer();
        List<Object> list = new ArrayList<Object>();
        if(!QwyUtil.isNullAndEmpty(Date)){
            sb.append("SELECT qd.channel,qd.channelName,qd.channel_cost,qd.date,qd.id ");
        }else {
            sb.append("SELECT qd.channel,qd.channelName,qd.channel_cost,'0',qd.id ");
        }
        sb.append("FROM qdtj qd  WHERE 1 = 1 ");

        //查询时间范围;
        if (!QwyUtil.isNullAndEmpty(Date)) {
            sb.append("AND qd.date BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND  DATE_FORMAT(?, '%Y-%m-%d 23:59:59') ");
            list.add(Date);
            list.add(Date);
        }
        if (!QwyUtil.isNullAndEmpty(channelType)) {
            sb.append(" AND qd.channel_type = ? ");
            list.add(channelType);
        }else{
            sb.append(" AND qd.channel_type = 1 ");
        }
        if (!QwyUtil.isNullAndEmpty(channelName)) {
            sb.append(" AND qd.channelName like '%"+channelName+"%' ");
        }
        sb.append("GROUP BY qd.channelCode ");
        sb.append(" ORDER BY qd.channel+0 ASC ");
        StringBuffer sqlCount = new StringBuffer();
        sqlCount.append("SELECT COUNT(*) FROM ( ");
        sqlCount.append(sb);
        sqlCount.append(" )t");
        return dao.getBySqlAndSqlCount(pageUtil, sb.toString(), sqlCount.toString(), list.toArray());
    }

    /**
     * 根据条件查询相应的渠道统计数据
     *
     * @param insertTmie
     * @param channelName
     * @return
     */
    public List<Qdtj> loadQdtj(Date insertTmie, String channelName) {
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        String format = sd.format(insertTmie);
        StringBuffer hql = new StringBuffer();
        List<Object> list = new ArrayList<>();
        list.add(channelName);
        list.add(format);
        list.add(format);
        hql.append("SELECT * FROM qdtj WHERE 1=1 ");
        hql.append("AND channelName = ? ");
        hql.append("AND date BETWEEN DATE_FORMAT(?, '%Y-%m-%d 00:00:00') AND  DATE_FORMAT(?, '%Y-%m-%d 23:59:59') ");
        List<Qdtj> qdtjList = dao.LoadAllSql(hql.toString(), list.toArray(), Qdtj.class);
        return qdtjList;
    }

    /**
     * 计算并修改相应数据
     *
     * @param qdtj
     */
    public void updateQdtj(Qdtj qdtj) {
        try {
            Double channelCost = Double.valueOf(qdtj.getChannelCost());//获取渠道成本,根据渠道成本计算其他相关数据
            if (!QwyUtil.isNullAndEmpty(channelCost)&& channelCost > 0) {
                //获取激活人数
                Integer activityCount = Integer.valueOf(qdtj.getActivityCount());
                //计算激活成本
                if(!QwyUtil.isNullAndEmpty(activityCount)&& activityCount > 0){
                    qdtj.setActivityCost((channelCost/activityCount)+"");
                }else{
                    qdtj.setActivityCost("0");
                }
                //获取注册人数
                Integer regCount = Integer.valueOf(qdtj.getRegCount());
                //计算注册成本
                if(!QwyUtil.isNullAndEmpty(regCount)&& regCount > 0){
                    qdtj.setRegisterCost((channelCost/regCount)+"");
                }else{
                    qdtj.setRegisterCost("0");
                }
                //获取首投人数
                Integer strs = Integer.valueOf(qdtj.getStrs());
                //计算首投成本
                if(!QwyUtil.isNullAndEmpty(strs)&& strs > 0){
                    qdtj.setFristBuyCost((channelCost/strs)+"");
                }else{
                    qdtj.setFristBuyCost("0");
                }
                //获取首投总金额
                Double stje = Double.valueOf(qdtj.getStje())/100;
                //计算首投ROI
                //计算首投成本
                if(!QwyUtil.isNullAndEmpty(stje) && stje > 0){
                    qdtj.setFristBuyROI((stje/channelCost)+"");
                }else{
                    qdtj.setFristBuyROI("0");
                }
                //获取投资金额
                Double tzje = Double.valueOf(qdtj.getTzje())/100;
                //计算投资ROI
                if(!QwyUtil.isNullAndEmpty(tzje)&& tzje > 0){
                    qdtj.setBuyROI((tzje/channelCost)+"");
                }else{
                    qdtj.setBuyROI("0");
                }
                dao.update(qdtj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据主键获取渠道信息
     *
     * @return
     */
    public Qdtj getQdtjById(Integer id) {
        String hql = "from ProjectBorrowerInfo b where b.id=?";

        return (Qdtj) dao.LoadAll(hql, new Object[]{id});
    }
}
