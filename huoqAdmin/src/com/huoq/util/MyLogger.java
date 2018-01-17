package com.huoq.util;

import com.huoq.common.util.PropertiesUtil;
import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

/**
 * @Description 自定义Logger, 提供日志报警功能
 * @Author ZF
 * @Time 2017/7/17 10:27
 */
public class MyLogger {

    private static Logger logger;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    public static MyLogger getLogger(Class clazz) {
        init(clazz);
        return new MyLogger();
    }

    public static void init(Class clazz) {
        logger = LogManager.getLogger(clazz.getName());
    }

    public void error(Object message) {
        logger.error(message);
    }

    public void error(Object message, Throwable e) {
        logger.error(message, e);
        /**
         * @description 添加报警开关
         * @author ZF
         * @date 2017年8月24日17:20:25
         */
//        String result = SwitchUtil.getSwitch(SwitchEnum.WARNING.getCode());
//        if (Constants.SWITCH_ON.equalsIgnoreCase(result)) {
            setMsg(message, e);
//        }
    }

    public void info(Object object) {
        logger.info(object);
    }

    public void info(Object object, Throwable t) {
        logger.info(object, t);
    }

    public void debug(Object object) {
        logger.debug(object);
    }

    public void debug(Object object, Throwable t) {
        logger.debug(object, t);
    }

    /**
     * 设置异常信息
     * @param msg
     * @param t
     */
    public void setMsg(Object msg, Throwable t) {
    	
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        t.printStackTrace(new PrintStream(stream));

        JSONObject obj = new JSONObject();
        obj.element("authorName", "紧急异常");
        obj.element("title", "【案发时间："+sdf.format(new Date())+"】");
        obj.element("text", "【异常IP："+getServerIp()+"】【"+msg+"】"+stream.toString());
        post(obj);
    }

    /**
     * 设置方法执行时间和方法名称
     * @param time 方法执行时间
     * @param name 完整方法名
     */
    public void setMethodRunningTimeAndMethodName(long time, String name) {
        JSONObject obj = new JSONObject();
        obj.element("authorName", "超时");
        obj.element("title", "【案发时间："+sdf.format(new Date())+"】MethodMessage");
        obj.element("text", "【异常IP："+getServerIp()+"】"+"MethodName: " + name + "\nMethodRunningTime: " + time + "ms");
        post(obj);
    }

    /**
     * 发送异常日志信息
     * @param json 请求参数
     * @return
     */
    public static void post(JSONObject json) {

        HttpClient client;
        HttpPost post;
        try {
            client = HttpClients.createDefault();
            post = new HttpPost((String) PropertiesUtil.getProperties("jianliao.mylog.server"));
            post.setHeader("Content-Type", "application/json");
            post.addHeader("Authorization", "Basic YWRtaW46");
            StringEntity s = new StringEntity(json.toString(), "utf-8");
            s.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            post.setEntity(s);
            // 发送请求
            HttpResponse httpResponse = client.execute(post);
            if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                logger.info("请求服务器失败，请检查推送服务器");
            }
        } catch (Exception e) {
            logger.error("推送日志异常信息失败");
        }
    }

    /**
     * 获取服务器地址
     *
     * @return Ip地址
     */
    public String getServerIp() {
        // 获取操作系统类型
        String sysType = System.getProperties().getProperty("os.name");
        String ip;
        if (sysType.toLowerCase().startsWith("win")) {  // 如果是Windows系统，获取本地IP地址
            String localIP = null;
            try {
                localIP = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                logger.error(e.getMessage(), e);
            }
            if (localIP != null) {
                return localIP;
            }
        } else {
            ip = getIpByEthNum("eth0"); // 兼容Linux
            if (ip != null) {
                return ip;
            }
        }
        return "获取服务器IP错误";
    }

    /**
     * 根据网关获取IP地址
     *
     * @param ethNum 网络端口
     * @return Ip地址
     */
    private String getIpByEthNum(String ethNum) {
        try {
            Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                if (ethNum.equals(netInterface.getName())) {
                    Enumeration addresses = netInterface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        ip = (InetAddress) addresses.nextElement();
                        if (ip != null && ip instanceof Inet4Address) {
                            return ip.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException e) {
            logger.error(e.getMessage(), e);
        }
        return "获取服务器IP错误";
    }
    
    
    /**  
     * info:只做日志打印，针对CODE类信息
     * @author changhaipeng
     * @since JDK 1.8
     */
    public void infoCode(LogsEnum le) {
    	infoCode(le,false);
    }
    
    /**  
     * infoCode:不推送，附带数据消息的日志信息
     *  
     * @author changhaipeng
     * @param le
     * @param othMsg  
     * @since JDK 1.8  
     */
    public void infoCode(LogsEnum le,String othMsg) {
    	infoCode(le,false,othMsg);
    }
    
    /**  
     * infoCode:根据code打印描述消息，同时在最后拼接额外的业务数据消息
     * @author changhaipeng
     * @param le	      ：日志枚举类
     * @param isSend  ：是否推送到简聊
     * @param othMsg  ：额外的业务数据消息
     * @since JDK 1.8  
     */
    public void infoCode(LogsEnum le,boolean isSend,String othMsg) {
    	logger.info(le.getCode()+":"+LogsEnum.fromCode(le.getCode()).getDesc());
    	logger.info(othMsg);
	    if(isSend==true) {
	    	JSONObject obj = new JSONObject();
	    	obj.element("authorName", "【业务异常】");
	    	obj.element("title", "【案发时间："+sdf.format(new Date())+"】");
	    	obj.element("text","【异常IP："+getServerIp()+"】"+le.getCode()+":"+LogsEnum.fromCode(le.getCode()).getDesc()+";业务数据："+othMsg);
	    	post(obj);
	    }
    }
    
    /**  
     * sendMsg:发送简聊的信息，根据code发送相关信息，用于推送业务异常非程序异常的消息
     * @author changhaipeng
     * @param  le，isSend，msg
     * @since JDK 1.8  
     */
    public  void infoCode(LogsEnum le,boolean isSend) {
	    logger.info(le.getCode()+":"+LogsEnum.fromCode(le.getCode()).getDesc());
	    if(isSend==true) {
	    	JSONObject obj = new JSONObject();
	    	obj.element("authorName", "【业务异常】");
	    	obj.element("title", "【案发时间："+sdf.format(new Date())+"】");
	    	obj.element("text","【异常IP："+getServerIp()+"】"+le.getCode()+":"+LogsEnum.fromCode(le.getCode()).getDesc());
	    	post(obj);
	    }
    }
}
