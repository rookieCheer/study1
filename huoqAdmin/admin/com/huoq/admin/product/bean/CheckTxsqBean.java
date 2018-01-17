package com.huoq.admin.product.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.huoq.account.bean.UserInfoBean;
import com.huoq.common.ApplicationContexts;
import com.huoq.common.bean.YiBaoPayBean;
import com.huoq.orm.Users;
import com.huoq.orm.UsersInfo;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.huoq.admin.product.dao.RechargeDAO;
import com.huoq.common.bean.MyWalletBean;
import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.FundRecord;
import com.huoq.orm.TxRecord;
import com.huoq.orm.TxRecordCompany;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;


/**后台管理--审核提现的Bean层;
 * @author qwy
 *
 * @createTime 2015-05-28 01:33:11
 */
@Service
public class CheckTxsqBean {
	private static Logger log = Logger.getLogger(CheckTxsqBean.class);
	@Resource
	private RechargeDAO dao;
	@Resource
	private YiBaoPayBean yiBaoPayBean;
	@Resource
	private MyWalletBean myWalletBean;

	@Resource
	private UserInfoBean userInfoBean;

	/**获得提现的记录;根据状态来查找;
	 * @param pageUtil 分页对象;
	 * @param status 状态; all:全部; 0:待审核; 1:提现失败; 2:提现成功;3:正在审核 
	 * @param isdate 是否需要与现在时间间隔1天的数据
	 * @return
	 * @throws Exception 
	 */
	public PageUtil<TxRecord> loadTxRecord(PageUtil<TxRecord> pageUtil,String status,boolean isdate) throws Exception {
		return loadTxRecord(pageUtil, status, isdate,null,null);
	}	
	
	/**获得提现的记录;根据状态来查找;
	 * @param pageUtil 分页对象;
	 * @param status 状态; all:全部; 0:待审核; 1:提现失败; 2:提现成功;3:正在审核 
	 * @param isdate 是否需要与现在时间间隔1天的数据
	 * @param name 用户名
	 * @param insertTime  提现时间
	 * @return
	 * @throws Exception 
	 */
	public PageUtil<TxRecord> loadTxRecord(PageUtil<TxRecord> pageUtil,String status,String name, String insertTime) throws Exception{
		return loadTxRecord(pageUtil, status,false, name, insertTime);
	}
	
	/**首页提现的记录;根据状态来查找;
	 * @param pageUtil 分页对象;
	 * @param status 状态; all:全部; 0:待审核; 1:提现失败; 2:提现成功;3:正在审核 
	 * @param isdate 是否需要与现在时间间隔1天的数据
	 * @param name 用户名
	 * @param insertTime  提现时间
	 * @return
	 * @throws Exception 
	 */
	public PageUtil<TxRecord> indexloadTxRecord(PageUtil<TxRecord> pageUtil,String status,String name, String insertTime) throws Exception{
		return loadIndexTxRecord(pageUtil, status,false, name, insertTime);
	}
	
	/**获得提现的记录;根据状态来查找;
	 * @param pageUtil 分页对象;
	 * @param status 状态; all:全部; 0:待审核; 1:提现失败; 2:提现成功;3:正在审核 
	 * @param isdate 是否需要与现在时间间隔1天的数据
	 * @param name 用户名
	 * @param insertTime  提现时间
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public PageUtil<TxRecord> loadTxRecord(PageUtil<TxRecord> pageUtil,String status,boolean isdate, String name, String insertTime) throws Exception{
		ArrayList<Object> ob = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();
		hql.append(" FROM TxRecord tx ");
		hql.append(" WHERE 1 = 1 ");
		if(!"all".equals(status)){
			hql.append(" AND tx.status = ? ");
			ob.add(status);
		}
		if(isdate){
			hql.append(" AND date_add_interval(DATE_FORMAT(tx.insertTime,'%Y-%m-%d'), 1, DAY) <=NOW() ");
		}
		if(!QwyUtil.isNullAndEmpty(name)){
			hql.append(" AND tx.users.username = ? ");
			ob.add(DESEncrypt.jiaMiUsername(name));
		}
		//充值时间
		if(!QwyUtil.isNullAndEmpty(insertTime)){
			String [] time=QwyUtil.splitTime(insertTime);
			if(time.length>1){
				hql.append(" AND tx.insertTime >= ? ");
				ob.add(QwyUtil.fmMMddyyyy.parse(time[0]));
				hql.append(" AND tx.insertTime <= ? ");
				ob.add(QwyUtil.fmMMddyyyy.parse(time[1]));
			}else{
				hql.append(" AND tx.insertTime >= ? ");
				ob.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 00:00:00"));
				hql.append(" AND tx.insertTime <= ? ");
				ob.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 23:59:59"));
			}
		}
		hql.append(" ORDER BY tx.insertTime DESC ");
		return (PageUtil<TxRecord>)dao.getPage(pageUtil, hql.toString(), ob.toArray());
	}
	
	/**获得提现的记录;根据状态来查找;
	 * @param pageUtil 分页对象;
	 * @param status 状态; all:全部; 0:待审核; 1:提现失败; 2:提现成功;3:正在审核 
	 * @param isdate 是否需要与现在时间间隔1天的数据
	 * @param name 用户名
	 * @param insertTime  提现时间
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public PageUtil<TxRecord> loadIndexTxRecord(PageUtil pageUtil,String status,boolean isdate, String name, String insertTime) throws Exception{
		List<Object> list = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * FROM ( ");
		sql.append("SELECT * FROM ( ");
		sql.append("SELECT u.id , u.username username,tx.money money,us.real_name,u.province,u.city ,r.realname,tx.status status,tx.note note,tx.record_number recordNumber," +
				"tx.insert_time insertTime,tx.check_time checkTime,tx.draw_type drawType," +
				"tx.request_id requestId,tx.yb_order_id ybOrderId,tx.type type " );
		sql.append("FROM  tx_record tx ");
		sql.append("LEFT JOIN users_info us ON us.users_id  = tx.users_id ");
		sql.append("LEFT JOIN users u ON u.id = us.users_id ");
		sql.append("LEFT JOIN (SELECT u.id id ,ui.real_name  realname FROM users u LEFT JOIN invite i ON i.be_invited_id = u.id " );
		sql.append("LEFT JOIN users_info ui ON ui.users_id = i.invite_id) r ON u.id = r.id ) t ) t WHERE 1=1 ");

		if(!"all".equals(status)){
			sql.append(" AND t.status = ? ");
			list.add(status);
		}
		if(isdate){
			sql.append(" AND date_add_interval(DATE_FORMAT(t.checkTime,'%Y-%m-%d'), 1, DAY) <=NOW() ");
		}
		if(!QwyUtil.isNullAndEmpty(name)){
			sql.append(" AND t.username = ? ");
			list.add(DESEncrypt.jiaMiUsername(name));
		}
		//充值时间
		if(!QwyUtil.isNullAndEmpty(insertTime)){
			String [] time=QwyUtil.splitTime(insertTime);
			if(time.length>1){
				sql.append(" AND t.insertTime >= ? ");
				list.add(QwyUtil.fmMMddyyyy.parse(time[0]));
				sql.append(" AND t.insertTime <= ? ");
				list.add(QwyUtil.fmMMddyyyy.parse(time[1]));
			}else{
				sql.append(" AND t.insertTime >= ? ");
				list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 00:00:00"));
				sql.append(" AND t.insertTime <= ? ");
				list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 23:59:59"));
			}
		}
		sql.append(" ORDER BY t.insertTime DESC ");
		StringBuffer sqlCount = new StringBuffer();
		sqlCount.append("SELECT COUNT(*) ");
		sqlCount.append("FROM (");
		sqlCount.append(sql);
		sqlCount.append(") a");
		pageUtil = dao.getBySqlAndSqlCount(pageUtil, sql.toString(), sqlCount.toString(), list.toArray());
		List<TxRecord> indexCxRecord =toIndexTxRecord(pageUtil.getList());
		pageUtil.setList(indexCxRecord);
		return pageUtil;

	}

	private List<TxRecord> toIndexTxRecord(List<Object[]> list) throws ParseException {
		List<TxRecord> txRecords = new ArrayList<TxRecord>();
		if (!QwyUtil.isNullAndEmpty(list)) {
			for (Object[] object : list) {
				TxRecord txRecord= new TxRecord();
				txRecord.setUserName(!QwyUtil.isNullAndEmpty(object[1])? object[1]+"":"");
				if (object[2] ==null) {
					txRecord.setMoney(Double.valueOf(0.0+""));
				}else {
					txRecord.setMoney(!QwyUtil.isNullAndEmpty(object[2])? Double.valueOf(object[2]+""):0.0);
				}
				txRecord.setRealName(!QwyUtil.isNullAndEmpty(object[3])? object[3]+"":"");
				txRecord.setProvince(object[4] + "");
				txRecord.setCity(object[5] + "");
				txRecord.setCategory(object[6]+"");
				txRecord.setStatus(object[7]+"");
				txRecord.setNote(object[8]+"");
				txRecord.setRecordNumber(Long.valueOf(!QwyUtil.isNullAndEmpty(object[9])?object[9].toString():null));
				SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date time1=null;
				if(!QwyUtil.isNullAndEmpty(object[10])){
					time1 = sd.parse(object[10] + "");
					txRecord.setInsertTime(time1);
				}
				Date time2 =null;
				if(!QwyUtil.isNullAndEmpty(object[11])){
					time2 = sd.parse(object[11] + "");
					txRecord.setCheckTime(time2);
				}
				txRecord.setDrawType(object[12] + "");
				txRecord.setRequestId(!QwyUtil.isNullAndEmpty(object[13])?object[13].toString():null);
				txRecord.setYbOrderId(!QwyUtil.isNullAndEmpty(object[14])?object[14].toString():null);
				txRecord.setType(object[15]+"");
				txRecords.add(txRecord);
			}
		}
		return txRecords;
	}

	/**
	 * 查询6个小时前正在提现的记录
	 * @param pageUtil
	 * @param txStatus 提现状态 0:未操作,1:已操作
	 * @param isHour 是否查询6小时记录
	 * @return
	 * @throws Exception
	 */
	public PageUtil<TxRecord> loadRequestTxRecord(PageUtil<TxRecord> pageUtil,String txStatus,boolean isHour) throws Exception{
		ArrayList<Object> ob = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();
		hql.append(" FROM TxRecord tx ");
		
		hql.append(" WHERE tx.status = '0' ");
		if (isHour) {
			hql.append(" AND date_add_interval(tx.insertTime, 6, HOUR) <=NOW() ");
		}
		if(!QwyUtil.isNullAndEmpty(txStatus)){
			hql.append(" AND tx.txStatus=? ");
			ob.add(txStatus);
		}
		hql.append("GROUP BY tx.usersId HAVING count(*)=1 ORDER BY tx.insertTime ASC ");
		
		return (PageUtil<TxRecord>)dao.getPage(pageUtil, hql.toString(), ob.toArray());
	}
	
	/**
	 * 请求提现
	 * @param txId
	 * @return
	 */
	public boolean requestTx(String txId){
		try {
			if (!QwyUtil.isNullAndEmpty(txId)) {
				TxRecord txRecord = (TxRecord) dao.findById(new TxRecord(),txId);
				if (!QwyUtil.isNullAndEmpty(txId)) {
					String myjson = yiBaoPayBean.withdraw(
							txRecord.getRequestId(), txRecord.getUsersId(),
							txRecord.getMoney().intValue(),
							txRecord.getDrawType(), txRecord.getUserIp());
					log.info(txRecord.getId() + "请求提现的记录" + myjson);
					JSONObject jb = JSONObject.fromObject(myjson);
					String status = QwyUtil.get(jb, "status");
					if("SUCCESS".equalsIgnoreCase(status)){
						log.info(txRecord.getId()+"请求提现的成功记录");
						//提交提现成功;次日到账;直接扣除冻结金额;修改于2015-06-03 15:23:28;
						String yblsh = jb.getString("ybdrawflowid");
						txRecord.setYbOrderId(yblsh);
						txRecord.setTxStatus("1");
						dao.saveOrUpdate(txRecord);
						return true;
					} else {
						log.info(txRecord.getId() + "请求提现失败");
						String yblsh = QwyUtil.get(jb, "message");
						txRecord.setYbOrderId(yblsh);
						txRecord.setStatus("2");
						txRecord.setTxStatus("1");
						txRecord.setCheckTime(new Date());
						dao.saveOrUpdate(txRecord);
						myWalletBean.addTotalMoneyLeftMoney(
								txRecord.getUsersId(), txRecord.getMoney(),
								"txfk", "", "提现失败,返款提现金额到用户帐号");
						return true;
					}
				} else {
					log.info(txRecord.getId() + "请求提现的失败");
					txRecord.setStatus("2");
					txRecord.setTxStatus("1");
					txRecord.setCheckTime(new Date());
					dao.saveOrUpdate(txRecord);
					myWalletBean.addTotalMoneyLeftMoney(txRecord.getUsersId(),
									txRecord.getMoney(), "txfk", "",
									"提现失败,返款提现金额到用户帐号");
					return true;
				}
			}
		} catch (Exception e) {
			log.error("操作异常: ",e);
			log.error(e.getMessage(), e);
		}
		return false;
	}

	/**
	 * 提现记录审核
	 * add by yks  2016-10-08
	 * @param txRecordId 待审提现记录id
	 * @param shStatus  0:待审核;1:提现成功;2提现失败;
	 * @throws Exception
	 */
	public void updateTxRecordStatus(String txRecordId,String shStatus) throws Exception{

//		ApplicationContext context = ApplicationContexts.getContexts();
//		PlatformTransactionManager tm = (PlatformTransactionManager) context.getBean("transactionManager");
//		TransactionStatus ts = tm.getTransaction(new DefaultTransactionDefinition());
//		try {
			TxRecord txRecord = (TxRecord) dao.findById(new TxRecord(), txRecordId);
			if (!QwyUtil.isNullAndEmpty(txRecord)) {
				txRecord.setStatus(shStatus);
				txRecord.setCheckTime(new Date());
				txRecord.setNote("审核不通过");
				dao.saveOrUpdate(txRecord);

				//更新账户余额
				Long usersId = txRecord.getUsersId();
				Users users = userInfoBean.getUserById(usersId);
				if (null != users) {
					UsersInfo usersInfo = users.getUsersInfo();
					// 对用户余额做处理
					double leftMoney = QwyUtil.calcNumber(usersInfo.getLeftMoney(), txRecord.getMoney(), "+").doubleValue();
					usersInfo.setLeftMoney(leftMoney);
					// 对用户总资产做处理
					double totalMoney = QwyUtil.calcNumber(usersInfo.getTotalMoney(), txRecord.getMoney(), "+").doubleValue();
					usersInfo.setTotalMoney(totalMoney);
					userInfoBean.saveOrUpdateUserInfo(usersInfo);
					FundRecord fund = myWalletBean.packFundRecord(usersInfo.getUsersId(), txRecord.getMoney(), "0", "back", "管理员后台充值", "审核提现不通过", leftMoney);
					dao.save(fund);
				}
			}
//			tm.commit(ts);
//		}catch (Exception e){
////			tm.rollback(ts);
//			log.error("审核提现异常：更新提现状态或账户余额错误。");
//		}
	}


	
	/**获得提现的记录;根据状态来查找;  企业用户
	 * @param pageUtil 分页对象;
	 * @param status 状态; all:全部; 0:待审核; 1:提现失败; 2:提现成功;3:正在审核 
	 * @param isdate 是否需要与现在时间间隔1天的数据
	 * @return
	 * @throws Exception 
	 */	
	public PageUtil<TxRecordCompany> loadTxRecordCompany(PageUtil<TxRecordCompany> pageUtil,String status,String name, String insertTime) throws Exception{
		return loadTxRecordCompany(pageUtil, status,false, name, insertTime);
	}

	
	/**获得提现的记录;根据状态来查找;   企业用户
	 * @param pageUtil 分页对象;
	 * @param status 状态; all:全部; 0:待审核; 1:提现失败; 2:提现成功;3:正在审核 
	 * @param isdate 是否需要与现在时间间隔1天的数据
	 * @param name 用户名
	 * @param insertTime  提现时间
	 * @return
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public PageUtil<TxRecordCompany> loadTxRecordCompany(PageUtil<TxRecordCompany> pageUtil,String status,boolean isdate, String name, String insertTime) throws Exception{
		ArrayList<Object> ob = new ArrayList<Object>();
		StringBuffer hql = new StringBuffer();
		hql.append(" FROM TxRecordCompany tx ");
		hql.append(" WHERE 1 = 1 ");
		if(!"all".equals(status)){
			hql.append(" AND tx.status = ? ");
			ob.add(status);
		}
		if(isdate){
			hql.append(" AND date_add_interval(DATE_FORMAT(tx.checkTime,'%Y-%m-%d'), 1, DAY) <=NOW() ");
		}
		if(!QwyUtil.isNullAndEmpty(name)){
			hql.append(" AND tx.usersCompanyId = ? ");
			ob.add(DESEncrypt.jiaMiUsername(name));
		}
		//充值时间
		if(!QwyUtil.isNullAndEmpty(insertTime)){
			String [] time=QwyUtil.splitTime(insertTime);
			if(time.length>1){
				hql.append(" AND tx.insertTime >= ? ");
				ob.add(QwyUtil.fmMMddyyyy.parse(time[0]));
				hql.append(" AND tx.insertTime <= ? ");
				ob.add(QwyUtil.fmMMddyyyy.parse(time[1]));
			}else{
				hql.append(" AND tx.insertTime >= ? ");
				ob.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 00:00:00"));
				hql.append(" AND tx.insertTime <= ? ");
				ob.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 23:59:59"));
			}
		}
		hql.append(" ORDER BY tx.insertTime DESC, tx.checkTime ASC ");
		return (PageUtil<TxRecordCompany>)dao.getPage(pageUtil, hql.toString(), ob.toArray());
	}

	/**
	 * 分页显示异常提现记录列表
	 *  当天某一用户提现2次以上即为异常提现
	 * @param pageUtil
	 * @param status
	 * @param name
	 * @param insertTime
	 * @return
	 * @throws Exception
	 */
	public PageUtil<TxRecord> loadYCTxRecord(PageUtil pageUtil,String status, String name, String insertTime) throws Exception{
		return loadYCTxRecord(pageUtil,status,name,insertTime,false);
	}

	@SuppressWarnings("unchecked")
	private PageUtil<TxRecord> loadYCTxRecord(PageUtil pageUtil,String status, String name, String insertTime,boolean isdate) throws Exception{
		ArrayList<Object> ob = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT tx.id, tx.record_number, tx.users_id, tx.money, tx.STATUS, tx.insert_time, tx.check_time, " +
						" tx.note, tx.type, tx.account_id, tx.request_id, tx.yb_order_id, tx.error_code, tx.draw_type, " +
						" tx.user_ip, tx.tx_status, t.txCount, t.userName, t.realName FROM tx_record tx LEFT JOIN ( " +
						" SELECT COUNT(tx2.insert_time) AS txCount, tx2.users_id AS usersId, DATE_FORMAT(tx2.insert_time, '%Y-%m-%d') " +
						" AS insertTime, us.username AS userName, ui.real_name AS realName FROM tx_record tx2 LEFT JOIN " +
						" users us ON tx2.users_id = us.id LEFT JOIN users_info ui ON tx2.users_id = ui.users_id WHERE 1 = 1" +
						" GROUP BY DATE_FORMAT(tx2.insert_time, '%Y-%m-%d'), tx2.users_id HAVING  COUNT(tx2.insert_time) > 1 ORDER BY " +
				        " tx2.insert_time DESC, tx2.check_time ASC ) t ON tx.users_id = t.usersId  WHERE 1 = 1 AND " +
						" DATE_FORMAT(tx.insert_time, '%Y-%m-%d') = DATE_FORMAT(t.insertTime, '%Y-%m-%d') "
				);
		if(!"all".equals(status)){
			sql.append(" AND tx.status = ? ");
			ob.add(status);
		}
		if(isdate){
			sql.append(" AND date_add_interval(DATE_FORMAT(tx.check_time,'%Y-%m-%d'), 1, DAY) <=NOW() ");
		}
		if(!QwyUtil.isNullAndEmpty(name)){
			sql.append(" AND t.userName = ? ");
			ob.add(DESEncrypt.jiaMiUsername(name));
		}
		//充值时间
		if(!QwyUtil.isNullAndEmpty(insertTime)){
			String [] time=QwyUtil.splitTime(insertTime);
			if(time.length>1){
				sql.append(" AND tx.insert_time >= ? ");
				ob.add(QwyUtil.fmMMddyyyy.parse(time[0]));
				sql.append(" AND tx.insert_time <= ? ");
				ob.add(QwyUtil.fmMMddyyyy.parse(time[1]));
			}else{
				sql.append(" AND tx.insert_time >= ? ");
				ob.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 00:00:00"));
				sql.append(" AND tx.insert_time <= ? ");
				ob.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0]+" 23:59:59"));
			}
		}
		sql.append(" ORDER BY tx.insert_time DESC, tx.check_time ASC ");
		StringBuffer sqlCount = new StringBuffer();
		sqlCount.append(" SELECT COUNT(x.id)  ");
		sqlCount.append(" FROM (");
		sqlCount.append(sql);
		sqlCount.append(") x");
		pageUtil = dao.getBySqlAndSqlCount(pageUtil,sql.toString(),sqlCount.toString(),ob.toArray());
		pageUtil.setList(toTxRecord(pageUtil.getList()));
		return pageUtil;
	}


	/**
	 * 转换list
	 *
	 * @param list
	 * @return
	 * @throws Exception
	 */
	private List<TxRecord> toTxRecord(List<Object[]> list) throws Exception {
		List<TxRecord> trList = new ArrayList<TxRecord>();
		if (!QwyUtil.isNullAndEmpty(list)) {
			for (Object[] obj : list) {
				TxRecord tr = new TxRecord();
				tr.setId(!QwyUtil.isNullAndEmpty(obj[0])?obj[0].toString():null);
				tr.setRecordNumber(!QwyUtil.isNullAndEmpty(obj[1])?Long.parseLong(obj[1].toString()):0L);
				tr.setUsersId(!QwyUtil.isNullAndEmpty(obj[2])?Long.parseLong(obj[2].toString()):0L);
				tr.setMoney(!QwyUtil.isNullAndEmpty(obj[3])?Double.parseDouble(obj[3].toString()): 0);
				tr.setStatus(!QwyUtil.isNullAndEmpty(obj[4])?obj[4].toString():null);
				tr.setInsertTime(QwyUtil.fmyyyyMMddHHmmss.parse(obj[5].toString()));
				tr.setCheckTime(!QwyUtil.isNullAndEmpty(obj[6])?QwyUtil.fmyyyyMMddHHmmss.parse(obj[6].toString()):null);
				tr.setNote(!QwyUtil.isNullAndEmpty(obj[7])?obj[7].toString():null);
				tr.setType(!QwyUtil.isNullAndEmpty(obj[8])?obj[8].toString():null);
				tr.setAccountId(!QwyUtil.isNullAndEmpty(obj[9])?obj[9].toString():null);
				tr.setRequestId(!QwyUtil.isNullAndEmpty(obj[10])?obj[10].toString():null);
				tr.setYbOrderId(!QwyUtil.isNullAndEmpty(obj[11])?obj[11].toString():null);
				tr.setErrorCode(!QwyUtil.isNullAndEmpty(obj[12])?obj[12].toString():null);
				tr.setDrawType(!QwyUtil.isNullAndEmpty(obj[13])?obj[13].toString():null);
				tr.setUserIp(!QwyUtil.isNullAndEmpty(obj[14])?obj[14].toString():null);
				tr.setTxStatus(!QwyUtil.isNullAndEmpty(obj[15])?obj[15].toString():null);
				tr.setTxCount(!QwyUtil.isNullAndEmpty(obj[16])?obj[16].toString():"0");
				tr.setUserName(!QwyUtil.isNullAndEmpty(obj[17])?obj[17].toString():" ");
				tr.setRealName(!QwyUtil.isNullAndEmpty(obj[18])?obj[18].toString():" ");
				trList.add(tr);
			}
		}
		return trList;
	}

	
	/**
	 * 根据用户ID获取提现记录
	 * 
	 * @param uid
	 *            用户ID
	 * @param currentPage
	 *            当前页
	 * @param pageSize
	 *            一页条数
	 * @param isInsertTime
	 *            是否需要当前日期
	 * @return
	 */
	public List<TxRecord> findRecordsByUid(Long uid, Integer currentPage, Integer pageSize, boolean isDSH, boolean isInsertTime) throws Exception {
		StringBuffer hql = new StringBuffer();
		hql.append(" FROM TxRecord tx ");
		hql.append(" WHERE tx.usersId = ? ");
		if (isDSH) {
			hql.append(" AND tx.status in ('1','3') ");
		}
		if (isInsertTime) {
			hql.append(" AND tx.insertTime >= '" + QwyUtil.fmyyyyMMdd.format(new Date()) + "' ");
		}
		hql.append(" ORDER BY tx.insertTime DESC ");
		log.info(QwyUtil.fmyyyyMMdd.format(new Date()));
		return dao.findAdvList(hql.toString(), new Object[] { uid }, currentPage, pageSize);
	}

	/**
	 * Description:提现记录根据订单号 ybOrderId查找订单信息
	 * @author  changhaipeng
	 * @ 2017年6月3日17:03:32
	 */
	public TxRecord findTxRecordByJyls(String merBillNo){
		@SuppressWarnings("unchecked")
		List<TxRecord> tempList = dao.getHibernateTemplate().find(" FROM TxRecord tx WHERE tx.ybOrderId = ? ", new Object[]{merBillNo});
		if(!QwyUtil.isNullAndEmpty(tempList)){
			return tempList.get(0);
		}
		return null ;
	}

}
