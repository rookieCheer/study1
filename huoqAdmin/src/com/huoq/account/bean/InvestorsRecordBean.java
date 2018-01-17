package com.huoq.account.bean;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;











import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.common.dao.ObjectDAO;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.ActivityStat;
import com.huoq.orm.Investors;
import com.huoq.orm.PlatformInvestDetail;
import com.huoq.orm.QdtjPlatform;
import com.huoq.orm.Users;
import com.huoq.thread.bean.UpdateQdtjPlatformThreadBean;


@Service
public class InvestorsRecordBean {
	@Resource(name="objectDAO")
	private ObjectDAO dao;
	
	@Resource
	private UpdateQdtjPlatformThreadBean updateQdtjPlatformThreadBean;
	
	private static Logger log = Logger.getLogger(InvestorsRecordBean.class); 
	/**加载投资记录,根据分页 用户;
	 * @param pageUtil 分页对象;
	 * @return
	 */

	@SuppressWarnings("unchecked")
	public PageUtil<Investors> getInvestorsByPageUtil(
			PageUtil<Investors> pageUtil, String[] status,long uid) {
		try {
			String st = "";
			if(QwyUtil.isNullAndEmpty(status)){
				st = "'1','2','3'";
			}else{
				st = QwyUtil.packString(status);
			}
			StringBuffer buff = new StringBuffer();
			buff.append("FROM Investors ivs ");
			buff.append("WHERE ivs.investorStatus IN ("+st+") ");
			buff.append("AND ivs.usersId = "+uid );
			buff.append(" ORDER BY  ivs.investorStatus ASC, ivs.payTime DESC,ivs.id ASC");
			return (PageUtil<Investors>)dao.getPage(pageUtil, buff.toString(), null);
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return null;
	}
	
	/**
	 * 投资情况
	 * @param payTime 支付时间
	 * @param status 投资状态
	 * @throws Exception 
	 */
	public List<Object[]> findPlatformInvestDetail(String payTime, String status,String insertTime,String platform) throws Exception{
		List<Object> arrayList=new ArrayList<Object>();
		StringBuffer sql=new StringBuffer();
		
		sql.append("SELECT platform ,SUM(jhcs),SUM(zcrs),SUM(strs),SUM(stje),SUM(tzje),SUM(czje)-SUM(txje) FROM qdtj_platform ");
		sql.append(" WHERE 1=1");
		
		if(!QwyUtil.isNullAndEmpty(payTime)){
			String [] time=QwyUtil.splitTime(payTime);
			if(time.length>1){
				sql.append(" AND query_date >= ? ");
				arrayList.add(QwyUtil.fmMMddyyyy.parse(time[0]));
				sql.append(" AND query_date <= ? ");
				arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1]+" 23:59:59"));
			}else{
				sql.append(" AND query_date >= ? ");
				arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 00:00:00"));
				sql.append(" AND query_date <= ? ");
				arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 23:59:59"));
			}
		}
        if(!QwyUtil.isNullAndEmpty(platform)){
            sql.append(" AND platform = ? ");
            arrayList.add(platform);
        }
		sql.append(" GROUP BY platform");
		
//		sql.append(" SELECT us.regist_platform,SUM(ins.copies)  FROM USERS as us ");
//		sql.append(" LEFT JOIN investors as ins ON us.id =  ins.users_id ");
//		sql.append(" WHERE 1=1 ");
//		if(!QwyUtil.isNullAndEmpty(payTime)){
//			String [] time=QwyUtil.splitTime(payTime);
//			if(time.length>1){
//				sql.append(" AND ins.pay_time >= ? ");
//				arrayList.add(QwyUtil.fmMMddyyyy.parse(time[0]));
//				sql.append(" AND ins.pay_time <= ? ");
//				arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1]+" 23:59:59"));
//			}else{
//				sql.append(" AND ins.pay_time >= ? ");
//				arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 00:00:00"));
//				sql.append(" AND ins.pay_time <= ? ");
//				arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 23:59:59"));
//			}
//		}
//		if(!QwyUtil.isNullAndEmpty(insertTime)){
//			String [] insertT=QwyUtil.splitTime(insertTime);
//			if(insertT.length>1){
//				sql.append(" AND us.insert_time >= ? ");
//				arrayList.add(QwyUtil.fmMMddyyyy.parse(insertT[0]));
//				sql.append(" AND us.insert_time <= ? ");
//				arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(insertT[1]+" 23:59:59"));
//			}else{
//				sql.append(" AND us.insert_time >= ? ");
//				arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(insertT[0]+" 00:00:00"));
//				sql.append(" AND us.insert_time <= ? ");
//				arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(insertT[0]+" 23:59:59"));
//			}
//		}
//		if(!QwyUtil.isNullAndEmpty(status)){
//			sql.append(" AND ins.investor_status = ?  ");
//			arrayList.add(status);
//		}else{
//			sql.append(" AND ins.investor_status in ('1','2','3') ");
//		}
//		sql.append(" GROUP BY us.regist_platform ");
		List<Object []> list=dao.LoadAllSql(sql.toString(), arrayList.toArray());
//		return parsePlatformInvestDetail(list);
		return list;
	}
	
	
	/**
	 * 投资情况
	 * @param payTime 支付时间
	 * @param status 投资状态
	 * @throws Exception 
	 */
	public String findAllInvestDetail(String payTime, String status,String insertTime) throws Exception{
		List<Object> arrayList=new ArrayList<Object>();
		StringBuffer sql=new StringBuffer();
		sql.append(" SELECT SUM(ins.copies)  FROM USERS as us ");
		sql.append(" LEFT JOIN investors as ins ON us.id =  ins.users_id ");
		sql.append(" WHERE us.regist_platform is not NULL ");
		if(!QwyUtil.isNullAndEmpty(payTime)){
			String [] time=QwyUtil.splitTime(payTime);
			if(time.length>1){
				sql.append(" AND ins.pay_time >= ? ");
				arrayList.add(QwyUtil.fmMMddyyyy.parse(time[0]));
				sql.append(" AND ins.pay_time <= ? ");
				arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1]+" 23:59:59"));
			}else{
				sql.append(" AND ins.pay_time >= ? ");
				arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 00:00:00"));
				sql.append(" AND ins.pay_time <= ? ");
				arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 23:59:59"));
			}
		}
		if(!QwyUtil.isNullAndEmpty(insertTime)){
			String [] insertT=QwyUtil.splitTime(insertTime);
			if(insertT.length>1){
				sql.append(" AND us.insert_time >= ? ");
				arrayList.add(QwyUtil.fmMMddyyyy.parse(insertT[0]));
				sql.append(" AND us.insert_time <= ? ");
				arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(insertT[1]+" 23:59:59"));
			}else{
				sql.append(" AND us.insert_time >= ? ");
				arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(insertT[0]+" 00:00:00"));
				sql.append(" AND us.insert_time <= ? ");
				arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(insertT[0]+" 23:59:59"));
			}
		}
		if(!QwyUtil.isNullAndEmpty(status)){
			sql.append(" AND ins.investor_status = ?  ");
			arrayList.add(status);
		}else{
			sql.append(" AND ins.investor_status in ('1','3') ");
		}
		Object object=dao.getSqlCount(sql.toString(),arrayList.toArray());
		if(!QwyUtil.isNullAndEmpty(object)){
			return object+"";
		}
		return "0";
	}
	
	/**
	 * 转换为平台投资情况
	 * @param list
	 * @return
	 */
	private List<PlatformInvestDetail> parsePlatformInvestDetail(List<Object []> list){
		List<PlatformInvestDetail> platformInvestDetails=new ArrayList<PlatformInvestDetail>();
		if(!QwyUtil.isNullAndEmpty(list)){
			for (Object [] object : list) {
				PlatformInvestDetail platformInvestDetail=new PlatformInvestDetail();
				platformInvestDetail.setRegistPlatform(object[0]+"");
				platformInvestDetail.setPlatformInvest(object[1]+"");
				platformInvestDetails.add(platformInvestDetail);
			}
		}
		return platformInvestDetails;
	}
	
	/**
	 * 合计
	 * @param payTime
	 * @return
	 */
	public List<Object[]> getHj(String payTime,String platform){
		
		try {
			List<Object> arrayList=new ArrayList<Object>();
			StringBuffer sql=new StringBuffer();
			
			sql.append("SELECT SUM(jhcs),SUM(zcrs),SUM(strs),SUM(stje),SUM(tzje),SUM(czje)-SUM(txje) FROM qdtj_platform");
			sql.append(" WHERE 1=1");
			
			if(!QwyUtil.isNullAndEmpty(payTime)){
				String [] time=QwyUtil.splitTime(payTime);
				if(time.length>1){
					sql.append(" AND query_date >= ? ");
					arrayList.add(QwyUtil.fmMMddyyyy.parse(time[0]));
					sql.append(" AND query_date <= ? ");
					arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1]+" 23:59:59"));
				}else{
					sql.append(" AND query_date >= ? ");
					arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 00:00:00"));
					sql.append(" AND query_date <= ? ");
					arrayList.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 23:59:59"));
				}
			}

			if(!QwyUtil.isNullAndEmpty(platform)){
				sql.append(" AND platform = ?  ");
				arrayList.add(platform);
			}


			List<Object []> list=dao.LoadAllSql(sql.toString(), arrayList.toArray());
			
			if (!QwyUtil.isNullAndEmpty(list)) {
				return list;
			}
		
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		return null;
	}
	
	/**
	 * 活动期内 投资总额
	 * @param usersId
	 * @param stTime
	 * @param etTime
	 * @param isInclude  是否包含周周利 新手标
	 * @param num	1：包含理财券  2 纯本金  	
	 * @return					字段非空判断  提高代码重用率
	 */
	public Object getSumInvCopies(Long usersId,String stTime,String etTime,boolean isInclude,String num,String qxType){
		StringBuffer sql = new StringBuffer();
		if ("1".equals(num)) {
			sql.append(" SELECT SUM(coupon+in_money)*0.01 FROM investors WHERE 1=1");
		}else if("2".equals(num)){
			sql.append(" SELECT SUM(in_money)*0.01 FROM investors WHERE 1=1");
		}
		//sql拼接
		String sqlStr = getSql(usersId, isInclude, qxType, stTime, etTime);
		sql.append(sqlStr);
//		sql.append(" AND investor_status IN ('1','2','3')");
//		
//		if (!QwyUtil.isNullAndEmpty(usersId)) {
//			sql.append(" AND users_id = "+usersId);
//		}
//		
//		if (!isInclude) {
//			sql.append(" AND product_id NOT IN (SELECT id FROM product WHERE qx_type IN ("+qxType+") )");
//		}
//		
//		if (!QwyUtil.isNullAndEmpty(stTime)) {
//			sql.append(" AND pay_time BETWEEN '"+stTime+"' and '"+etTime+"'");
//		}
		
		Object object = dao.getSqlCount(sql.toString(), null);
		
		return object;
	}
	
	
	/**
	 * 活动期内  查询用户投资总额
	 * @param usersId
	 * @param stTime
	 * @param etTime
	 * @param isInclude  是否包含周周利 新手标  包含则传false
	 * @param num	1：包含理财券  2 纯本金  3总收益
	 * 				字段非空判断  提高代码重用率
	 * @param qxType  product的qxtype 字段 
	 * 				传参格式 "'1','2'"
	 * @param atLeast 分组后金额判断  单位：元
	 * @return	 返回单位为：元
	 */
	public PageUtil getInvList(PageUtil pageUtil,Long usersId,String stTime,String etTime,boolean isInclude,String qxType,String num,Double atLeast){
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT * FROM (");
		if ("1".equals(num)) {
			sql.append(" SELECT SUM(coupon+in_money)*0.01 sumInv,users_id FROM investors WHERE 1=1");
		}else if("2".equals(num)){
			sql.append(" SELECT SUM(in_money)*0.01 sumInv,users_id FROM investors WHERE 1=1");
		}else if ("3".equals(num)) {
			sql.append(" SELECT SUM(expect_earnings)*0.01 sumInv,users_id FROM investors WHERE 1=1");
		}
		//拼接 投资查询 sql语句
		String sqlStr = getSql(usersId, isInclude, qxType, stTime, etTime);
		sql.append(sqlStr);
		
		sql.append(" GROUP BY users_id ORDER BY sumInv DESC )a");
		
		//分组后金额判断
		if (!QwyUtil.isNullAndEmpty(atLeast)) {
			sql.append(" WHERE sumInv >= "+atLeast);
		}
		
		StringBuffer sqlCount = new StringBuffer();
		sqlCount.append("SELECT COUNT(*) FROM (");
		sqlCount.append(sql);
		sqlCount.append(" )t");
		
		
		return dao.getBySqlAndSqlCount(pageUtil, sql.toString(), sqlCount.toString(), null);
	}
	
	/**
	 * 投资列表公用 拼接 sql 语句
	 * @param usersId	用户id
	 * @param isInclude 是否包含某个特定的标期产品
	 * @param qxType  product.qxtype 传参格式 "'1','2'"
	 * @param stTime  活动开始时间  string类型
	 * @param etTime  活动结束时间  string类型
	 * @return
	 */
	public String getSql(Long usersId,boolean isInclude,String qxType,String stTime,String etTime){
		StringBuffer sql = new StringBuffer();
		//状态
		sql.append(" AND investor_status IN ('1','2','3')");
		//用户ID
		if (!QwyUtil.isNullAndEmpty(usersId)) {
			sql.append(" AND users_id = "+usersId);
		}
		//是否包含某个类型的产品
		if (!isInclude) {
			sql.append(" AND product_id NOT IN (SELECT id FROM product WHERE qx_type IN ("+qxType+") )");
		}
		//时间范围内
		if (!QwyUtil.isNullAndEmpty(stTime)) {
			sql.append(" AND pay_time BETWEEN '"+stTime+"' and '"+etTime+"'");
		}
		return sql.toString();
	}
}
