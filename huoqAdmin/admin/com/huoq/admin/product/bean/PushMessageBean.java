package com.huoq.admin.product.bean;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.jfree.util.Log;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.huoq.admin.product.dao.PushMessageDAO;
import com.huoq.common.bean.MQTTServer;
import com.huoq.common.util.QwyUtil;
import com.huoq.orm.PushMessage;

@Service
public class PushMessageBean{
	
	private static Logger log = Logger.getLogger(PushMessageBean.class);
	
	@Resource
	PushMessageDAO dao;
	@Resource
	MQTTServer mqttServer;
	
	/**
	 * 查找推送消息
	 * @param type  0 iOS 1 Android 
	 * @return
	 */
	public List<PushMessage> findPushMessage(String type,String status) throws Exception{
		List<Object> list=new ArrayList<Object>();
		StringBuffer hql=new StringBuffer();
		hql.append(" FROM PushMessage v ");
		hql.append(" WHERE 1 = 1 ");
		if(!QwyUtil.isNullAndEmpty(type)){
			hql.append(" AND v.type = ? ");
			list.add(type);
		}
		if(!QwyUtil.isNullAndEmpty(status)){
			hql.append(" AND v.status = ? ");
			list.add(status);
		}
		hql.append(" ORDER BY v.insertTime DESC");
		return dao.LoadAll(hql.toString(), list.toArray());
	}
	/**
	 * 保存pushMessage
	 * @param message
	 * @return
	 */
	public PushMessage savePushMessage(PushMessage message,String target){
		message.setInsertTime(new Date());
		message.setStatus("0");
		message.setType("0");
		message.setTarget(target);
		dao.save(message);
		return message;
		
	}
	
	/**
	 * 根据ID获取消息
	 */
	public PushMessage getPushMessageById(Long id){
		return (PushMessage) dao.findById(new PushMessage(), id);
	}
	
	/**
	 * 发送推送消息
	 */
	public boolean push(Long id){
		PushMessage message=getPushMessageById(id);
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("title", message.getTitle());
		jsonObject.put("content", message.getContent());
		try {
			String encodeStr = URLEncoder.encode(jsonObject.toString(),"UTF-8");//编码
			Log.info("推送内容编码:__"+encodeStr);
			Log.info("推送目标:__"+message.getTarget());
			mqttServer.sendMessage(encodeStr,message.getTarget());
			updateStatus(id,"1");
			return true;
		} catch (Exception e) {
			log.error("操作异常: ",e);
		}
		updateStatus(id,"2");
		return false;
	}
	
	/**
	 * 修改状态
	 */
	public boolean updateStatus(Long id,String status){
		PushMessage message=getPushMessageById(id);
		message.setStatus(status);
		dao.update(message);
		return true;
	}
}
