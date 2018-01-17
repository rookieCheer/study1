/**
 * 
 */
package com.huoq.thread.action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.huoq.common.bean.DeviceEnableBean;


/**
 * 异步发送设备激活回调线程
 * @author gaoshuang
 * @email  gaoshvng@163.com
 * @createTime 2017年6月1日 下午4:00:01
 */
@Service(value = "autoExcuteDeviceActiveCallbakThread")
public class AutoExcuteDeviceActiveCallbakThread extends Thread {

	private Logger LOGGER = Logger.getLogger(AutoExcuteDeviceActiveCallbakThread.class);
	
	public static final Long ONE_DAY_TIME = 24 * 60 * 60 * 1000L;
	
	@Resource(name="deviceEnableBean")
	DeviceEnableBean deviceEnableBean;
	
	@Override
	public void run() {
		LOGGER.info("开始验证处理今日头条激活设备，查询成功执行回调接口！！！");
		//获取需要处理的文件名称
		SimpleDateFormat sf =new SimpleDateFormat("yyyy-MM-dd");
		//获取前一天日期
		String name = sf.format(new Date().getTime() - 1*ONE_DAY_TIME);
//		name=MessageFormat.format("C:\\Users\\Administrator\\Desktop\\request1.{0}.log",name);
		name=MessageFormat.format("/mnt/logs/toutiao/request.{0}.log",name);
		
		File file = new File(name);
		
		if(file.exists()){
			BufferedReader reader = null;
	        try {
	            reader = new BufferedReader(new FileReader(file));
	            String tempString = null;
	            while ((tempString = reader.readLine()) != null) {
	            	String [] activity = StringUtils.split(tempString,"|");
	            	if(activity.length == 7){
	            		//保存数据完整
	            		if(deviceEnableBean.checkDeviceActived(7, activity[1])){
	            			//发送回调请求
	            			deviceEnableBean.sendToutiaoDeviceActivityCallbak(activity[6]);
	            		}
	            	}
	            }
	        } catch (IOException e) {
	            LOGGER.error("今日头条接口落地请求文件读取异常"+e.getMessage());
	        } finally {
	            if (reader != null) {
	                try {
	                    reader.close();
	                } catch (IOException e1) {
	                	LOGGER.error("今日头条接口落地请求文件缓冲关闭异常"+e1.getMessage());
	                }
	            }
	        }
	        LOGGER.info(name + "文件今日头条渠道设备激活回调接口执行结束！！！");
		}
	}
}
