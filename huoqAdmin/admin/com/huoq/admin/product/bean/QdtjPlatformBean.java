package com.huoq.admin.product.bean;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.admin.product.dao.QdtjPlatformDao;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.QdtjPlatform;

/**
 * 各平台渠道总汇
 *
 * @author wxl
 *         2017年3月15日18:38:47
 */
@Service
public class QdtjPlatformBean {
	
	private static Logger log = Logger.getLogger(QdtjPlatformBean.class);
	
    @Resource
    QdtjPlatformDao dao;
    
    /**
     * 分页获取  各平台渠道统计 
     * @param pageUtil
     * @param insertTime
     * @param platform
     * @return
     * @throws Exception
     */
    public PageUtil<QdtjPlatform> getQdtjPlatformList(PageUtil<QdtjPlatform> pageUtil,String insertTime,String platform) throws Exception{
    	
    	StringBuffer hql = new StringBuffer();
    	List<Object> arrayList = new ArrayList<Object>();
    	hql.append(" FROM QdtjPlatform qd WHERE 1=1");
    	
    	if (!QwyUtil.isNullAndEmpty(insertTime)) {
    		String[] time = QwyUtil.splitTime(insertTime);
            if (!QwyUtil.isNullAndEmpty(time))
                if (time.length > 1) {
                	hql.append(" AND qd.queryDate >= ? ");
                	arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                	hql.append(" AND qd.queryDate <= ?");
                	arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
                       
                 }else{
                	 hql.append(" AND qd.queryDate >= ? ");
                     arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
                     hql.append(" AND qd.queryDate <= ? ");
                     arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
                 }
    	}
    	
    	if (!QwyUtil.isNullAndEmpty(platform)) {
    		hql.append(" AND qd.platform = ?");
			arrayList.add(platform);
		}
    	
    	hql.append(" ORDER BY qd.queryDate DESC");
    	
    	pageUtil = dao.getPage(pageUtil, hql.toString(), arrayList.toArray());
    	
    	if (!QwyUtil.isNullAndEmpty(pageUtil)) {
    		return pageUtil;
		}
    	return null;
    }
    
    /**
     * 获取 注册平台的集合
     * @return
     */
    public List getPlatformList(){
    	try {
    		
    		StringBuffer sql = new StringBuffer();
    		sql.append("SELECT regist_platform FROM users GROUP BY regist_platform ");
    		
    		List list = dao.LoadAllSql(sql.toString(), null);
    		if (!QwyUtil.isNullAndEmpty(list)) {
				return list;
			}
			
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
    	
    	return null;
    }
    
    /**
     * 各平台渠道统计  合计
     * @return
     */
    public List<Object[]> getHj(String platform,String insertTime){
    	try {
		
	    	StringBuffer sql = new StringBuffer();
	    	List<Object> arrayList = new ArrayList<Object>();
	    	sql.append(" SELECT  SUM(zcrs),SUM(bkrs),SUM(bkrs)/SUM(zcrs),SUM(strs),SUM(strs)/SUM(bkrs),SUM(stje),");
	    	sql.append(" SUM(stje)/SUM(strs),SUM(tzrs),SUM(tzje),SUM(tzje)/SUM(tzrs),SUM(czje),SUM(txje) FROM qdtj_platform WHERE 1=1");
	    	
	    	if (!QwyUtil.isNullAndEmpty(insertTime)) {
	    		String[] time = QwyUtil.splitTime(insertTime);
	            if (!QwyUtil.isNullAndEmpty(time))
	                if (time.length > 1) {
	                	sql.append(" AND query_date >= ? ");
	                	arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
	                	sql.append(" AND query_date <= ?");
	                	arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
	                       
	                 }else{
	                	 sql.append(" AND query_date >= ? ");
	                     arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
	                     sql.append(" AND query_date <= ? ");
	                     arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
	                 }
	    	}
	    	
	    	if (!QwyUtil.isNullAndEmpty(platform)) {
	    		sql.append(" AND platform = ?");
				arrayList.add(platform);
				
			}
	    	
	    	return dao.LoadAllSql(sql.toString(), arrayList.toArray());
    	
    	} catch (Exception e) {
    		log.error("操作异常: ",e);
		}
    	return null;
    	
    }
    
    /**
     * 合计
     */
    public QdtjPlatform tjQdtjPlatform(List<QdtjPlatform> platformList) throws Exception {
//        Qdtj cr = new Qdtj();
        QdtjPlatform qdjtp = new QdtjPlatform();

        //注册人数
        if (QwyUtil.isNullAndEmpty(qdjtp.getZcrs())){
        	qdjtp.setZcrs("0");
        }
             
        //绑卡人数
        if (QwyUtil.isNullAndEmpty(qdjtp.getBkrs())){
        	qdjtp.setBkrs("0");
        } 
        //首投人数
        if (QwyUtil.isNullAndEmpty(qdjtp.getStrs())){
        	qdjtp.setStrs("0");
        }  
        //首投金额
        if (QwyUtil.isNullAndEmpty(qdjtp.getStje())){
        	qdjtp.setStje("0");
        }
        //投资人数
        if (QwyUtil.isNullAndEmpty(qdjtp.getTzrs())){
        	qdjtp.setTzrs("0");
        }
       //投资金额
        if (QwyUtil.isNullAndEmpty(qdjtp.getTzje())){
        	qdjtp.setTzje("0");
        }
        
        if (!QwyUtil.isNullAndEmpty(platformList)) {
        	for(QdtjPlatform qdtjPlatform : platformList){
        		//注册人数
        		qdjtp.setZcrs(QwyUtil.calcNumber(qdtjPlatform.getZcrs(), qdjtp.getZcrs(), "+")+"");
        		//绑卡人数
        		qdjtp.setBkrs(QwyUtil.calcNumber(qdtjPlatform.getBkrs(), qdjtp.getBkrs(), "+")+"");
        		//首投人数
        		qdjtp.setStrs(QwyUtil.calcNumber(qdtjPlatform.getStrs(), qdjtp.getStrs(), "+")+"");
        		//首投金额
        		qdjtp.setStje(QwyUtil.calcNumber(qdtjPlatform.getStje(), qdjtp.getStje(), "+") +"");
        		//投资人数
        		qdjtp.setTzrs(QwyUtil.calcNumber(qdtjPlatform.getTzrs(), qdjtp.getTzrs(), "+") +"");
        		//投资金额
        		qdjtp.setTzje(QwyUtil.calcNumber(qdtjPlatform.getTzje(), qdjtp.getTzje(), "+") +"");
        		
        	}
			
		}
        
        //绑卡转化率 绑卡/注册
        if ("0".equals(qdjtp.getBkrs()) || "0".equals(qdjtp.getZcrs())) {
        	qdjtp.setBkzhl("0.0");
		}else{
			String zhl = QwyUtil.calcNumber(qdjtp.getBkrs(),qdjtp.getZcrs(), "/").toString();
			qdjtp.setBkzhl(QwyUtil.calcNumber(zhl, 0.01, "/",2) +"");
		}
             
        // 首投 /绑卡  首投转化率
        if ("0".equals(qdjtp.getStrs()) || "0".equals(qdjtp.getBkrs())) {
			qdjtp.setStzhl("0.0");
		}else {
			String stzhl = QwyUtil.calcNumber(qdjtp.getStrs(), qdjtp.getBkrs(), "/").toString();
			qdjtp.setStzhl(QwyUtil.calcNumber(stzhl, 0.01,"/",2) +"");
		}
        
        //人均首投金额
        if ("0".equals(qdjtp.getStrs()) || "0".equals(qdjtp.getStje())) {
        	qdjtp.setRjstje("0.0");
		}else {
			String rjstje = QwyUtil.calcNumber(qdjtp.getStje(), qdjtp.getStrs(), "/").toString();
			qdjtp.setRjstje(QwyUtil.calcNumber(rjstje, 1, "/",2)+"");
		}
        
        //人均投资金额
        if ("0".equals(qdjtp.getTzje()) || "0".equals(qdjtp.getTzrs())) {
        	qdjtp.setRjtzje("0.0");
		}else {
			String rjtzje = QwyUtil.calcNumber(qdjtp.getTzje(), qdjtp.getTzrs(), "/").toString();
        	qdjtp.setRjtzje(QwyUtil.calcNumber(rjtzje, 1, "/",2)+"");
		}
             
        return qdjtp;
    }
    
   
}
