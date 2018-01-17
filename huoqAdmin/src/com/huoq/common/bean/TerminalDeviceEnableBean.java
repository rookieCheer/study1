/**
 * 
 */
package com.huoq.common.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.common.dao.DeviceEnableDAO;

/**
 * 处理各个渠道发送过来的设备静态数据
 * 
 * @author gaoshuang
 * @email gaoshvng@163.com
 * @createTime 2017年5月24日 下午4:07:30
 */
@Service
public class TerminalDeviceEnableBean {

	private static Logger LOGGER = Logger.getLogger(TerminalDeviceEnableBean.class);

	@Resource
	DeviceEnableDAO deviceEnableDAO;
	/*
	 * 一天时间
	 */
	public static final Long ONE_DAY_TIME = 24 * 60 * 60 * 1000L;
	/**
	 * 线程安全
	 */
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * 查询指定时间段设备是否激活
	 * 
	 * @param imei
	 * @param os
	 * @param startTime
	 *            时间戳
	 * @param endTime
	 *            时间戳
	 * @return
	 * @throws ParseException
	 */
	public boolean checkTerminalDeviceEnable(String imei, String os, Long startTime, Long endTime)
			throws ParseException {
		Date startDate = dateFormat.parse(dateFormat.format(startTime));
		Date endDate   = dateFormat.parse(dateFormat.format(endTime));
		List<Object> list = new ArrayList<Object>();
		list.add(imei);
		list.add(os);
		list.add(startDate);
		list.add(endDate);
		String hql = " FROM Activity a WHERE a.md5Imei = ? AND a.type = ? AND a.insertTime BETWEEN ? AND ?";
		List result = deviceEnableDAO.LoadAll(hql, list.toArray());
		if (result.size() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 查询startTime 前 day天设备终端是否激活
	 * 
	 * @param imei
	 * @param os
	 * @param startTime 时间戳
	 * @param day
	 * @return
	 * @throws ParseException
	 */
	public boolean checkTerminalDeviceEnable(String imei, String idfa, Integer os, Long time, int day) {
		Date startDate = null;
		Date endDate = null;
		try {
			startDate = dateFormat.parse(dateFormat.format(time - day * ONE_DAY_TIME));
			endDate = dateFormat.parse(dateFormat.format(time));
		} catch (ParseException e) {
			LOGGER.debug("格式化时间失败", e);
		}
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
		if (result.size() > 0) {
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
	public boolean checkTerminalDeviceEnable(String imei, String os) throws ParseException {
		Date startDate = new Date();
		startDate.setHours(0);
		startDate.setMinutes(0);
		startDate.setSeconds(0);

		Date endDate = new Date();
		endDate.setHours(24);
		endDate.setMinutes(0);
		endDate.setSeconds(0);

		List<Object> list = new ArrayList<Object>();
		list.add(imei);
		list.add(os);
		list.add(dateFormat.parse(dateFormat.format(startDate)));
		list.add(dateFormat.parse(dateFormat.format(endDate)));
		String hql = " FROM Activity a WHERE a.md5Imei = ? AND a.type = ? AND a.insertTime BETWEEN ? AND ?";
		List result = deviceEnableDAO.LoadAll(hql, list.toArray());
		if (result.size() > 0) {
			return true;
		}
		return false;
	}

}
