package com.huoq.common.bean;

import java.net.URLDecoder;
import java.util.Date;

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.huoq.common.ApplicationContexts;
import com.huoq.common.util.MQTTUtil;
import com.huoq.common.util.QwyUtil;


/**
 * MQTT服务器端
 * @author 覃文勇
 *2015年4月7日 14:17:04
 */
@Service(value="mqttServer")
public class MQTTServer {
	private static Logger logger = Logger.getLogger(MQTTServer.class);
	/**
	 * 服务端发送消息
	 * @param data JSON格式的消息;title,和connet为key值;
	 * @param mqttTopic 推送频道; 所有: baiyimao
	 */
	public void sendMessage(String data,String mqttTopic) {
		try {
			MqttClient client = MQTTUtil.connect("Server");
			 MqttTopic topic = client.getTopic(mqttTopic);
			 MqttMessage message=new MqttMessage();
			//设置消息传输的类型  
			 message.setQos(2); 
			//设置是否在服务器中保存消息体  
			 message.setRetained(false);
			//设置消息的内容
			 message.setPayload(data.getBytes());  
			MqttDeliveryToken token = topic.publish(message);
			token.waitForCompletion();
		} catch (Exception e) {
			logger.error("操作异常: ",e);
		}
	}
	
	public static void main(String[] args) {
		while (true) {
			ApplicationContext context = ApplicationContexts.getContexts();
			MQTTServer mqttServer=(MQTTServer) context.getBean("mqttServer");
			
			mqttServer.sendMessage("{\"title\":\""+QwyUtil.fmHHmmss.format(new Date())+"新华金典理财测试通知\",\"content\":\"新华金典理财测试通知新华金典理财测试通知新华金典理财测试通知新华金典理财测试通知新华金典理财测试通知\"}","baiyimao");
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				logger.error("操作异常: ",e);
			}
		}
	}
	
	
	
}
