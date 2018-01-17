/**
 * 
 */
package com.huoq.common.bean;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huoq.common.dao.DeviceEnableDAO;
import com.huoq.common.util.HttpRequest;
import com.huoq.common.util.HttpRespons;
import com.huoq.orm.Activity;

/**
 * 处理各个渠道发送过来的设备静态数据
 * 
 * @author gaoshuang
 * @email gaoshvng@163.com
 * @createTime 2017年5月24日 下午4:07:30
 */
@Service
public class DeviceEnableBean {

	private static Logger LOGGER = Logger.getLogger(DeviceEnableBean.class);
	private static Logger LOGGER1 = Logger.getLogger("jinritoutiao");
	@Resource
	DeviceEnableDAO deviceEnableDAO;
	/*
	 * 一天时间
	 */
	public static final Long ONE_DAY_TIME = 24 * 60 * 60 * 1000L;
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


	/**
	 * 录入渠道查询设备激活请求
	 * @param ac
	 * @return
	 */

	public Activity saveChannelDeviceActivityRepuest(Activity ac) {
		LOGGER1.info(ac);
		return ac;
	}
	/**
	 * 查询时段内待激活回调请求
	 * @param day
	 * @return
	 */
	public boolean checkDeviceActived(int day ,String imei) {
		Date endDate = new Date();
		Date startDate = null;
		
		try {
			startDate = dateFormat.parse(dateFormat.format(endDate.getTime() - day * ONE_DAY_TIME));
		} catch (ParseException e) {
			LOGGER.debug("格式化时间出错！！！", e);
		}
		StringBuffer hql = new StringBuffer(" FROM Activity a WHERE md5Imei=? or imei=? ");
		List<Object> list = new ArrayList<Object>();
		list.add(imei);
		list.add(imei);
		list.add(startDate);
		list.add(endDate);
		hql.append(" AND a.insertTime BETWEEN ? AND ?  ");
		int num = deviceEnableDAO.findCountByHql(hql.toString(), list.toArray());
		if(num>0){
			return true;
		}
		return false;
	}
	
	/**
	 * 查询终端设备当天是否激活
	 * 
	 * @param imei
	 * @param os
	 * @return
	 * @throws ParseException
	 */
	public Activity checkTerminalDeviceEnable(String imei, String idfa, Integer os) throws ParseException {
		Date startDate = new Date();
		startDate.setHours(0);
		startDate.setMinutes(0);
		startDate.setSeconds(0);

		Date endDate = new Date();
		endDate.setHours(24);
		endDate.setMinutes(0);
		endDate.setSeconds(0);

		StringBuffer hql = new StringBuffer(" FROM Activity a WHERE 1 = 1 ");
		List<Object> list = new ArrayList<Object>();
		
		if (os <= 1) {
			//安卓端 MIEI号已加密
			list.add(imei);
			hql.append(" AND a.md5Imei = ? ");
			hql.append(" AND a.type = ? OR a.type IS null OR a.type =''");
		} else {
			list.add(idfa);
			hql.append(" AND a.imei = ? ");
			hql.append(" AND a.type = ? ");
		}
		
		list.add(String.valueOf(os));
		list.add(startDate);
		list.add(endDate);
		hql.append(" AND a.insertTime BETWEEN ? AND ?");
		List result = deviceEnableDAO.LoadAll(hql.toString(), list.toArray());
		Activity ac = null;
		if (result.size() > 0) {
			ac = (Activity)result.get(0);
		}
		return ac;
	}
	
	public boolean sendToutiaoDeviceActivityCallbak(String callback_url){
		boolean result = true;
		if(!StringUtils.isNotBlank(callback_url)){
			LOGGER.info(MessageFormat.format("[ret:{0}, message:{1}]", "1", "今日头条返回callbak_url为空"));
			return false;
        }else{
        	try {       
                HttpRequest request = new HttpRequest();  
                request.setDefaultContentEncoding("utf-8");
                
                HttpRespons hr = request.sendGet(callback_url);  
                       
                JSONObject msg = JSON.parseObject(hr.getContent());
                
                if(0 != Long.parseLong(String.valueOf(msg.get("ret")))){
                	//响应失败
                	result = false;
                	LOGGER.error("今日头条响应失败！！！ret" + msg.get("ret") + " message:" + msg.get("msg"));
                }
            } catch (Exception e) {    
                LOGGER.error(callback_url+" HTTP 请求失败:", e);
                result = false;
            } 
        }
		return result;
	}
}
