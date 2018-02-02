package com.huoq.common.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.huoq.common.dao.OutCashDao;
import com.huoq.common.util.DESEncrypt;
import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.OutCash;

@Service
public class OutCashBean {
	@Resource
	private OutCashDao dao;
	public PageUtil<OutCash> outCashTable (PageUtil pageUtil, String insertTime,String phone) throws ParseException {
		List<Object> list = new ArrayList<Object>();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT tr.check_time ,tr.money/100,ui.real_name ,ui.sex,ui.phone ,r.realname ,u.province,u.city FROM tx_record tr LEFT JOIN users_info ui ON tr.users_id = ui.users_id LEFT JOIN " + 
				"(SELECT u.id id ,ui.real_name  realname FROM users u LEFT JOIN invite i ON i.be_invited_id = u.id " + 
				"LEFT JOIN users_info ui ON ui.users_id = i.invite_id ) r ON r.id = tr.users_id LEFT JOIN users u ON u.id = tr.users_id ");
		sql.append("WHERE 1=1 AND tr.is_check = '1' ");
		if (!QwyUtil.isNullAndEmpty(insertTime)) {
			String[] time = QwyUtil.splitTime(insertTime);
			if (time.length > 1) {
				sql.append(" AND tr.check_time >= ? ");
				list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
				sql.append(" AND tr.check_time <= ? ");
				list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[1] + " 23:59:59"));
			} else {
				sql.append(" AND tr.check_time >= ? ");
				list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 00:00:00"));
				sql.append(" AND tr.check_time <= ? ");
				list.add(QwyUtil.fmMMddyyyyHHmmss.parse(time[0] + " 23:59:59"));
			}
		}
		//根据用户名(手机号查询)
		if (!QwyUtil.isNullAndEmpty(phone)) {
			sql.append("AND ui.phone=?");
			list.add(DESEncrypt.jiaMiUsername(phone));
		} 
		sql.append("  ORDER BY tr.check_time DESC ");
		StringBuffer sqlCount = new StringBuffer();
		sqlCount.append("SELECT COUNT(*) ");
		sqlCount.append("FROM (");
		sqlCount.append(sql);
		sqlCount.append(") c");
		pageUtil = dao.getBySqlAndSqlCount(pageUtil, sql.toString(), sqlCount.toString(), list.toArray());
		List outCash = toOutCash(pageUtil.getList());
		pageUtil.setList(outCash);
		return pageUtil;
		
	}
	
	/**
	 * 将数据转换为OutCash形式
	 * 
	 * @param list
	 * @return
	 */
	private List<OutCash> toOutCash(List<Object[]> list) {
		List<OutCash> OutCashs = new ArrayList<OutCash>();
		if (!QwyUtil.isNullAndEmpty(list)) {
			for (Object[] object : list) {
				OutCash outCash = new OutCash();
				SimpleDateFormat sdf = new SimpleDateFormat( " yyyyMMdd " );
				String format = sdf.format(object[0]);
				outCash.setOutCashTime(format);
				outCash.setOutMoney(object[1]+"");
				outCash.setRealname(object[2]+"");
				outCash.setGender(object[3]+"");
				outCash.setPhone(object[4]+"");
				outCash.setCategory(object[5] + "");
				outCash.setProvince(object[6] + "");
				outCash.setCity(object[7] + "");
				OutCashs.add(outCash);
			}
		}
		return OutCashs;
	}
}
