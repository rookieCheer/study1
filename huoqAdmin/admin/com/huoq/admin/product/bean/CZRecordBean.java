package com.huoq.admin.product.bean;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;

import com.huoq.common.util.PageUtil;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.Users;
import org.jsoup.helper.StringUtil;
import org.springframework.stereotype.Service;

import com.huoq.common.dao.ObjectDAO;
import com.huoq.orm.CzRecord;

import static groovy.xml.Entity.nbsp;

@Service
public class CZRecordBean {
	@Resource
	private  ObjectDAO objectDAO;

	
	@SuppressWarnings("unchecked")
	public CzRecord queryCzRecordByOrderId(String orderId){
		if(StringUtil.isBlank(orderId)){
			return null;
		}
		ArrayList<Object> list = new ArrayList<Object>();
		StringBuffer sb = new StringBuffer();
		sb.append("FROM CzRecord  cz ");
		sb.append(" AND cz.queryId IS NULL ");
		sb.append(" AND cz.orderId IS NOT NULL ");
		sb.append(" AND cz.orderId !='' ");
		sb.append(" AND cz.orderId = ? ");	
		list.add(orderId);
		sb.append(" ORDER BY cz.insertTime ASC ");
	   return (CzRecord)objectDAO.findUniqueResult(sb.toString(), list.toArray());
	}
}
